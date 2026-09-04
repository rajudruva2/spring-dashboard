let currentPage = 0;
const pageSize = 10;

const $ = (id) => document.getElementById(id);

async function loadSummary() {
    const response = await fetch('/api/deployments/summary');
    if (!response.ok) throw new Error('Unable to load summary');
    const data = await response.json();

    $('totalServices').textContent = data.totalServices;
    $('activeServices').textContent = data.active;
    $('failedServices').textContent = data.failed;
    $('uatServices').textContent = data.uat;
    $('prodServices').textContent = data.prod;
}

async function loadDeployments() {
    const search = $('searchInput').value.trim();
    const environment = $('environmentSelect').value;
    const status = $('statusSelect').value;

    const params = new URLSearchParams({
        page: currentPage,
        size: pageSize
    });

    if (search) params.set('search', search);
    if (environment) params.set('environment', environment);
    if (status) params.set('status', status);

    const response = await fetch(`/api/deployments?${params.toString()}`);

    if (!response.ok) {
        throw new Error('Unable to load deployment data');
    }

    const result = await response.json();
    renderTable(result);
}

function renderTable(result) {
    const body = $('deploymentBody');

    if (result.data.length === 0) {
        body.innerHTML = '<tr><td colspan="7" class="loading">No records found</td></tr>';
    } else {
        body.innerHTML = result.data.map(item => `
            <tr>
                <td><strong>${escapeHtml(item.name)}</strong></td>
                <td>${escapeHtml(item.environment)}</td>
                <td>${escapeHtml(item.version)}</td>
                <td>
                    <span class="status ${item.status.toLowerCase()}">
                        ${escapeHtml(item.status)}
                    </span>
                </td>
                <td>${escapeHtml(item.buildNumber)}</td>
                <td>${formatDate(item.lastDeployment)}</td>
                <td>${escapeHtml(item.deployedBy)}</td>
            </tr>
        `).join('');
    }

    const first = result.totalRecords === 0 ? 0 : result.page * result.pageSize + 1;
    const last = Math.min((result.page + 1) * result.pageSize, result.totalRecords);

    $('pageInfo').textContent =
        `Showing ${first}-${last} of ${result.totalRecords} records`;

    $('prevBtn').disabled = result.page <= 0;
    $('nextBtn').disabled = result.page >= result.totalPages - 1;

    $('pageNumbers').textContent =
        result.totalPages > 0
            ? `Page ${result.page + 1} of ${result.totalPages}`
            : 'Page 0 of 0';
}

function formatDate(value) {
    const date = new Date(value);
    return date.toLocaleString([], {
        year: 'numeric',
        month: 'short',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
    });
}

function escapeHtml(value) {
    return String(value)
        .replaceAll('&', '&amp;')
        .replaceAll('<', '&lt;')
        .replaceAll('>', '&gt;')
        .replaceAll('"', '&quot;')
        .replaceAll("'", '&#039;');
}

async function refresh() {
    $('error').classList.add('hidden');

    try {
        await Promise.all([loadSummary(), loadDeployments()]);
    } catch (error) {
        $('error').textContent = error.message;
        $('error').classList.remove('hidden');
    }
}

$('prevBtn').addEventListener('click', () => {
    if (currentPage > 0) {
        currentPage--;
        loadDeployments().catch(showError);
    }
});

$('nextBtn').addEventListener('click', () => {
    currentPage++;
    loadDeployments().catch(showError);
});

$('refreshBtn').addEventListener('click', refresh);

$('clearBtn').addEventListener('click', () => {
    $('searchInput').value = '';
    $('environmentSelect').value = '';
    $('statusSelect').value = '';
    currentPage = 0;
    refresh();
});

$('environmentSelect').addEventListener('change', () => {
    currentPage = 0;
    loadDeployments().catch(showError);
});

$('statusSelect').addEventListener('change', () => {
    currentPage = 0;
    loadDeployments().catch(showError);
});

let searchTimer;
$('searchInput').addEventListener('input', () => {
    clearTimeout(searchTimer);
    searchTimer = setTimeout(() => {
        currentPage = 0;
        loadDeployments().catch(showError);
    }, 300);
});

function showError(error) {
    $('error').textContent = error.message;
    $('error').classList.remove('hidden');
}

refresh();
