// notion-pr-merge-log.mjs
// Create a Notion DB page when a PR is merged.
// Required env:
// - NOTION_TOKEN
// - NOTION_DATABASE_ID
// - GITHUB_REPOSITORY (owner/repo)
// - PR_PAYLOAD_JSON (full GitHub event JSON)
// Optional env:
// - NOTION_TITLE_PROP (default: "name")
//
// Notion DB expected properties (exact names):
// - 상태 (status)
// - 날짜 (date)
// - PR 링크 (url)
// - 라벨 (multi_select)
// - Merge Commit (rich_text)
// - Source ID (rich_text)

const NOTION_VERSION = '2022-06-28';

function must(name) {
    const v = process.env[name];
    if (!v) throw new Error(`Missing env ${name}`);
    return v;
}

function stripMarkdown(md = '') {
    // keep it simple: remove fenced blocks + inline backticks
    return md
        .replace(/```[\s\S]*?```/g, '')
        .replace(/`([^`]+)`/g, '$1')
        .trim();
}

function extractSummary(body = '') {
    const text = body || '';
    const m = text.match(/(^|\n)#{1,3}\s*(summary|요약)\s*\n([\s\S]*?)(\n#{1,3}\s|$)/i);
    if (m && m[3]) return m[3].trim();
    return text.trim();
}

function extractTodos(body = '') {
    const lines = (body || '').split(/\r?\n/);
    const todos = [];
    for (const line of lines) {
        const m = line.match(/^\s*[-*]\s*\[( |x|X)\]\s+(.*)$/);
        if (!m) continue;
        todos.push({ checked: m[1].toLowerCase() === 'x', text: m[2].trim() });
    }
    return todos;
}

function richText(s) {
    return [{ type: 'text', text: { content: s } }];
}

async function notion(path, { method = 'GET', body } = {}) {
    const token = must('NOTION_TOKEN');
    const res = await fetch(`https://api.notion.com/v1${path}`, {
        method,
        headers: {
            'Authorization': `Bearer ${token}`,
            'Notion-Version': NOTION_VERSION,
            'Content-Type': 'application/json',
        },
        body: body ? JSON.stringify(body) : undefined,
    });
    const text = await res.text();
    let json;
    try { json = text ? JSON.parse(text) : {}; } catch { json = { raw: text }; }
    if (!res.ok) {
        throw new Error(`Notion HTTP ${res.status} ${res.statusText}: ${text.slice(0, 500)}`);
    }
    return json;
}

async function findExistingPage(databaseId, sourceId) {
    const q = await notion(`/databases/${databaseId}/query`, {
        method: 'POST',
        body: {
            page_size: 1,
            filter: {
                property: 'Source ID',
                rich_text: { equals: sourceId },
            },
        },
    });
    const first = (q.results || [])[0];
    return first ? first.id : null;
}

function buildBlocks({ title, prUrl, repo, prNumber, baseRef, headRef, author, mergedBy, summary, stats, labels, mergeSha }) {
    const blocks = [];

    // 제목
    blocks.push({
        object: 'block',
        type: 'heading_2',
        heading_2: { rich_text: richText('제목') },
    });
    blocks.push({
        object: 'block',
        type: 'paragraph',
        paragraph: {
            rich_text: [
                { type: 'text', text: { content: `${title} (#${prNumber})` }, annotations: { bold: true } },
            ],
        },
    });
    blocks.push({
        object: 'block',
        type: 'paragraph',
        paragraph: {
            rich_text: [
                { type: 'text', text: { content: 'PR: ' } },
                { type: 'text', text: { content: prUrl, link: { url: prUrl } } },
            ],
        },
    });
    blocks.push({
        object: 'block',
        type: 'bulleted_list_item',
        bulleted_list_item: { rich_text: richText(`Repo: ${repo}`) },
    });
    blocks.push({
        object: 'block',
        type: 'bulleted_list_item',
        bulleted_list_item: { rich_text: richText(`Branch: ${baseRef} ← ${headRef}`) },
    });
    if (author) {
        blocks.push({ object: 'block', type: 'bulleted_list_item', bulleted_list_item: { rich_text: richText(`Author: ${author}`) } });
    }
    if (mergedBy) {
        blocks.push({ object: 'block', type: 'bulleted_list_item', bulleted_list_item: { rich_text: richText(`Merged by: ${mergedBy}`) } });
    }

    // 업무 파악
    blocks.push({ object: 'block', type: 'heading_2', heading_2: { rich_text: richText('업무 파악') } });
    const cleanSummary = stripMarkdown(summary).slice(0, 1800) || '(요약 없음)';
    blocks.push({ object: 'block', type: 'paragraph', paragraph: { rich_text: richText(cleanSummary) } });
    if (labels?.length) {
        blocks.push({ object: 'block', type: 'paragraph', paragraph: { rich_text: richText(`Labels: ${labels.join(', ')}`) } });
    }
    blocks.push({ object: 'block', type: 'paragraph', paragraph: { rich_text: richText(`Stats: files ${stats.filesChanged}, +${stats.additions}, -${stats.deletions}`) } });

    // 체크 리스트
    blocks.push({ object: 'block', type: 'heading_2', heading_2: { rich_text: richText('체크 리스트') } });

    const todos = stats.todos?.length ? stats.todos : [
        { checked: false, text: '테스트/검증 확인' },
        { checked: false, text: '배포 영향도 확인' },
        { checked: false, text: '롤백 플랜 확인' },
        { checked: false, text: '모니터링 포인트 확인' },
    ];
    for (const t of todos) {
        blocks.push({
            object: 'block',
            type: 'to_do',
            to_do: {
                checked: !!t.checked,
                rich_text: richText(t.text),
            },
        });
    }

    // 최종 정리
    blocks.push({ object: 'block', type: 'heading_2', heading_2: { rich_text: richText('최종 정리') } });
    blocks.push({ object: 'block', type: 'bulleted_list_item', bulleted_list_item: { rich_text: richText(`Merge commit: ${mergeSha}`) } });
    blocks.push({ object: 'block', type: 'bulleted_list_item', bulleted_list_item: { rich_text: richText(`PR link: ${prUrl}`) } });
    blocks.push({ object: 'block', type: 'paragraph', paragraph: { rich_text: richText('추가 메모:') } });

    return blocks;
}

async function main() {
    const databaseId = must('NOTION_DATABASE_ID');
    const titleProp = process.env.NOTION_TITLE_PROP || 'name';

    const repo = process.env.GITHUB_REPOSITORY || 'unknown/unknown';
    const payload = JSON.parse(must('PR_PAYLOAD_JSON'));
    const pr = payload.pull_request;

    if (!pr?.merged) {
        console.log('Not merged; skipping');
        return;
    }

    const prNumber = pr.number;
    const title = pr.title || `PR #${prNumber}`;
    const prUrl = pr.html_url;
    const labels = (pr.labels || []).map(l => l.name).filter(Boolean);
    const mergeSha = pr.merge_commit_sha || '';
    const mergedAt = pr.merged_at;
    const author = pr.user?.login;
    const mergedBy = pr.merged_by?.login;
    const baseRef = pr.base?.ref;
    const headRef = pr.head?.ref;

    const sourceId = `github:${repo}#PR${prNumber}`;

    // Find existing
    const existing = await findExistingPage(databaseId, sourceId);
    if (existing) {
        // Update basic props only; do not rewrite body.
        await notion(`/pages/${existing}`, {
            method: 'PATCH',
            body: {
                properties: {
                    [titleProp]: { title: richText(`${title} (#${prNumber})`) },
                    '상태': { status: { name: '완료' } },
                    '날짜': { date: { start: mergedAt } },
                    'PR 링크': { url: prUrl },
                    '라벨': { multi_select: labels.map(n => ({ name: n })) },
                    'Merge Commit': { rich_text: richText(mergeSha) },
                    'Source ID': { rich_text: richText(sourceId) },
                },
            },
        });
        console.log(`Updated existing Notion page ${existing}`);
        return;
    }

    const summary = extractSummary(pr.body || '');
    const todos = extractTodos(pr.body || '');
    const stats = {
        filesChanged: pr.changed_files ?? 0,
        additions: pr.additions ?? 0,
        deletions: pr.deletions ?? 0,
        todos,
    };

    const blocks = buildBlocks({
        title,
        prUrl,
        repo,
        prNumber,
        baseRef,
        headRef,
        author,
        mergedBy,
        summary,
        stats,
        labels,
        mergeSha,
    });

    const created = await notion('/pages', {
        method: 'POST',
        body: {
            parent: { database_id: databaseId },
            properties: {
                [titleProp]: { title: richText(`${title} (#${prNumber})`) },
                '상태': { status: { name: '완료' } },
                '날짜': { date: { start: mergedAt } },
                'PR 링크': { url: prUrl },
                '라벨': { multi_select: labels.map(n => ({ name: n })) },
                'Merge Commit': { rich_text: richText(mergeSha) },
                'Source ID': { rich_text: richText(sourceId) },
            },
            children: blocks,
        },
    });

    console.log(`Created Notion page: ${created.url}`);
}

main().catch((e) => {
    console.error(e);
    process.exit(1);
});