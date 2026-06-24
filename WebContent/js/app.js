// =============================================
// EV CHARGING NETWORK — MAIN JS
// =============================================

const BASE = '/ev_charging/api';  // Change context path if different

// ── API HELPER ──────────────────────────────
// async function api(path, method = 'GET', body = null) {
//     const opts = {
//         method,
//         headers: { 'Content-Type': 'application/json' }
//     };
//     if (body) opts.body = JSON.stringify(body);
//     const res = await fetch(BASE + path, opts);
//     const text = await res.text();
//     try { return { ok: res.ok, data: JSON.parse(text), status: res.status }; }
//     catch { return { ok: res.ok, data: text, status: res.status }; }
// }
async function api(path, method = 'GET', body = null) {
    const opts = {
        method,
        headers: { 'Content-Type': 'application/json' }
    };

    if (body) {
        opts.body = JSON.stringify(body);
    }

    const res = await fetch('/ev_charging/api' + path, opts);
    const text = await res.text();

    try {
        return { ok: res.ok, data: JSON.parse(text) };
    } catch {
        return { ok: res.ok, data: text };
    }
}

// ── TOAST NOTIFICATIONS ──────────────────────
function toast(msg, type = 'success') {
    const icon = type === 'success' ? '✅' : '❌';
    const el = document.createElement('div');
    el.className = `toast ${type}`;
    el.innerHTML = `<span>${icon}</span><span>${msg}</span>`;
    document.getElementById('toast-container').appendChild(el);
    setTimeout(() => el.remove(), 3500);
}

// ── NAVIGATION ──────────────────────────────
function navigate(pageId) {
    document.querySelectorAll('.page').forEach(p => p.classList.remove('active'));
    document.querySelectorAll('#sidebar nav a').forEach(a => a.classList.remove('active'));
    document.getElementById(pageId).classList.add('active');
    document.querySelector(`[data-page="${pageId}"]`).classList.add('active');

    const titles = {
        'page-dashboard':  'Dashboard',
        'page-stations':   'Charging Stations',
        'page-users':      'Users & Vehicles',
        'page-sessions':   'Charging Sessions',
        'page-payments':   'Payments',
        'page-tickets':    'Maintenance Tickets'
    };
    document.getElementById('page-title').textContent = titles[pageId] || '';

    // Load data for the page
    const loaders = {
        'page-dashboard':  loadDashboard,
        'page-stations':   loadStations,
        'page-users':      loadUsers,
        'page-sessions':   loadSessions,
        'page-payments':   loadPayments,
        'page-tickets':    loadTickets
    };
    if (loaders[pageId]) loaders[pageId]();
}

// ── DASHBOARD ───────────────────────────────
async function loadDashboard() {
    const [stations, sessions, payments, tickets] = await Promise.all([
        api('/stations'), api('/sessions'), api('/payments'), api('/tickets')
    ]);

    if (stations.ok) {
        document.getElementById('stat-stations').textContent = stations.data.length;
    }
    if (sessions.ok) {
        document.getElementById('stat-sessions').textContent = sessions.data.length;
        const active = sessions.data.filter(s => !s.endTime).length;
        document.getElementById('stat-active').textContent = active;
    }
    if (payments.ok) {
        const total = payments.data.reduce((sum, p) => sum + p.amount, 0);
        document.getElementById('stat-revenue').textContent = '₹' + total.toFixed(0);
    }
    if (tickets.ok) {
        const open = tickets.data.filter(t => !t.closedTime).length;
        document.getElementById('stat-tickets').textContent = open;
    }

    // Recent sessions table
    if (sessions.ok) {
        const tbody = document.getElementById('recent-sessions-body');
        const recent = sessions.data.slice(0, 6);
        tbody.innerHTML = recent.length === 0
            ? `<tr><td colspan="5" class="loading-row">No sessions yet</td></tr>`
            : recent.map(s => `
                <tr>
                    <td>#${s.sessionId}</td>
                    <td>${s.vehicleMake || '—'}</td>
                    <td><span class="badge badge-blue">${s.connectorType || '—'}</span></td>
                    <td>${s.totalKwh ? s.totalKwh.toFixed(2) + ' kWh' : '—'}</td>
                    <td>${s.endTime
                        ? '<span class="badge badge-green">Completed</span>'
                        : '<span class="badge badge-yellow">Active</span>'}</td>
                </tr>`).join('');
    }
}

// ── STATIONS ────────────────────────────────
async function loadStations() {
    const tbody = document.getElementById('stations-body');
    tbody.innerHTML = `<tr class="loading-row"><td colspan="6"><div class="spinner"></div> Loading...</td></tr>`;
    const r = await api('/stations');
    if (!r.ok) { toast('Failed to load stations', 'error'); return; }
    tbody.innerHTML = r.data.length === 0
        ? `<tr><td colspan="6" class="loading-row">No stations found</td></tr>`
        : r.data.map(s => `
            <tr>
                <td><strong>#${s.stationId}</strong></td>
                <td>${s.operatorName}</td>
                <td><span class="badge badge-blue">${s.discomName || 'DISCOM #' + s.discomId}</span></td>
                <td style="font-size:12px;color:var(--text-muted)">${s.latitude.toFixed(4)}, ${s.longitude.toFixed(4)}</td>
                <td>T-${s.transformerId}</td>
                <td>
                    <button class="btn btn-red btn-sm" onclick="deleteStation(${s.stationId})">🗑 Delete</button>
                </td>
            </tr>`).join('');
}

async function addStation() {
    const data = {
        operatorName:  document.getElementById('stn-operator').value.trim(),
        latitude:      parseFloat(document.getElementById('stn-lat').value),
        longitude:     parseFloat(document.getElementById('stn-lng').value),
        discomId:      parseInt(document.getElementById('stn-discom').value),
        transformerId: parseInt(document.getElementById('stn-transformer').value)
    };

    // validation
    if (!data.operatorName || isNaN(data.latitude) || isNaN(data.longitude)) {
        toast('Fill all fields correctly', 'error');
        return;
    }

    try {
        const r = await api('/stations', 'POST', data);

        if (r.ok) {
            toast('Station added successfully ✅');
            loadStations();     // reload table
            clearForm('station-form');
        } else {
            toast(r.data || 'Error adding station ❌', 'error');
        }
    } catch (err) {
        console.error(err);
        toast('Server error ❌', 'error');
    }
}

async function deleteStation(id) {
    if (!confirm('Delete station #' + id + '?')) return;
    const r = await api('/stations/' + id, 'DELETE');
    if (r.ok) { toast('Station deleted'); loadStations(); }
    else toast('Cannot delete — may have linked data', 'error');
}

// ── USERS ────────────────────────────────────
async function loadUsers() {
    const [uRes, vRes] = await Promise.all([api('/users'), api('/vehicles')]);
    const tbody = document.getElementById('users-body');
    if (!uRes.ok) { toast('Failed to load users', 'error'); return; }

    const vehicleMap = {};
    if (vRes.ok) {
        vRes.data.forEach(v => {
            if (!vehicleMap[v.userId]) vehicleMap[v.userId] = [];
            vehicleMap[v.userId].push(v.make);
        });
    }

    tbody.innerHTML = uRes.data.length === 0
        ? `<tr><td colspan="4" class="loading-row">No users found</td></tr>`
        : uRes.data.map(u => `
            <tr>
                <td><strong>#${u.userId}</strong></td>
                <td>
                    <span class="badge ${u.kycStatus === 'Verified' ? 'badge-green' : u.kycStatus === 'Rejected' ? 'badge-red' : 'badge-yellow'}">
                        ${u.kycStatus}
                    </span>
                </td>
                <td style="font-size:12px">${(vehicleMap[u.userId] || []).join(', ') || '—'}</td>
                <td>
                    <select class="form-group select" onchange="updateKyc(${u.userId}, this.value)"
                        style="background:var(--bg3);border:1px solid var(--border);color:var(--text);padding:4px 8px;border-radius:6px;font-size:12px">
                        <option value="Pending"  ${u.kycStatus==='Pending'  ? 'selected':''}>Pending</option>
                        <option value="Verified" ${u.kycStatus==='Verified' ? 'selected':''}>Verified</option>
                        <option value="Rejected" ${u.kycStatus==='Rejected' ? 'selected':''}>Rejected</option>
                    </select>
                </td>
            </tr>`).join('');
}

async function addUser() {
    const r = await api('/users', 'POST', { kycStatus: 'pending' });
    if (r.ok) { toast('User registered!'); loadUsers(); }
    else toast('Error registering user', 'error');
}

async function updateKyc(userId, status) {
    const r = await api(`/users/${userId}/kyc?status=${status}`, 'PUT');
    if (r.ok) toast(`KYC updated to ${status}`);
    else toast('KYC update failed', 'error');
}

async function addVehicle() {
    const data = {
        userId:          parseInt(document.getElementById('v-user').value),
        make:            document.getElementById('v-make').value.trim(),
        batteryCapacity: parseFloat(document.getElementById('v-battery').value)
    };
    if (!data.make || isNaN(data.userId) || isNaN(data.batteryCapacity)) {
        toast('Fill all vehicle fields', 'error'); return;
    }
    const r = await api('/vehicles', 'POST', data);
    if (r.ok) { toast('Vehicle registered!'); loadUsers(); clearForm('vehicle-form'); }
    else toast(r.data.error || 'Error', 'error');
}

// ── SESSIONS ─────────────────────────────────
async function loadSessions() {
    const tbody = document.getElementById('sessions-body');
    tbody.innerHTML = `<tr class="loading-row"><td colspan="7"><div class="spinner"></div> Loading...</td></tr>`;
    const r = await api('/sessions');
    if (!r.ok) { toast('Failed to load sessions', 'error'); return; }
    tbody.innerHTML = r.data.length === 0
        ? `<tr><td colspan="7" class="loading-row">No sessions</td></tr>`
        : r.data.map(s => `
            <tr>
                <td><strong>#${s.sessionId}</strong></td>
                <td>C-${s.connectorId} <span class="badge badge-blue" style="font-size:10px">${s.connectorType||''}</span></td>
                <td>${s.vehicleMake || 'Vehicle #' + s.vehicleId}</td>
                <td style="font-size:12px">${s.startTime ? s.startTime.substring(0,16) : '—'}</td>
                <td style="font-size:12px">${s.endTime   ? s.endTime.substring(0,16)   : '—'}</td>
                <td>${s.totalKwh ? s.totalKwh.toFixed(2) + ' kWh' : '—'}</td>
                <td>${s.endTime
                    ? '<span class="badge badge-green">Done</span>'
                    : `<button class="btn btn-red btn-sm" onclick="endSession(${s.sessionId})">⏹ End</button>`}
                </td>
            </tr>`).join('');
}

async function startSession() {
    const data = {
        connectorId: parseInt(document.getElementById('sess-connector').value),
        vehicleId:   parseInt(document.getElementById('sess-vehicle').value)
    };
    if (isNaN(data.connectorId) || isNaN(data.vehicleId)) {
        toast('Enter valid Connector ID and Vehicle ID', 'error'); return;
    }
    const r = await api('/sessions', 'POST', data);
    if (r.ok) { toast('Session started!'); loadSessions(); clearForm('session-form'); }
    else toast(r.data.error || 'Error', 'error');
}

async function endSession(id) {
    const kwh = parseFloat(prompt('Enter total kWh delivered:'));
    if (isNaN(kwh) || kwh < 0) { toast('Invalid kWh value', 'error'); return; }
    const r = await api('/sessions/end', 'POST', { sessionId: id, totalKwh: kwh });
    if (r.ok) { toast('Session ended!'); loadSessions(); }
    else toast('Error ending session', 'error');
}

// ── PAYMENTS ─────────────────────────────────
async function loadPayments() {
    const tbody = document.getElementById('payments-body');
    tbody.innerHTML = `<tr class="loading-row"><td colspan="4"><div class="spinner"></div> Loading...</td></tr>`;
    const r = await api('/payments');
    if (!r.ok) { toast('Failed to load payments', 'error'); return; }
    tbody.innerHTML = r.data.length === 0
        ? `<tr><td colspan="4" class="loading-row">No payments</td></tr>`
        : r.data.map(p => `
            <tr>
                <td><strong>#${p.paymentId}</strong></td>
                <td>Session #${p.sessionId}</td>
                <td><span class="badge badge-blue">${p.paymentMethod}</span></td>
                <td style="color:var(--green);font-weight:600">₹${p.amount.toFixed(2)}</td>
            </tr>`).join('');
}

async function addPayment() {
    const data = {
        sessionId:     parseInt(document.getElementById('pay-session').value),
        paymentMethod: document.getElementById('pay-method').value,
        amount:        parseFloat(document.getElementById('pay-amount').value)
    };
    if (isNaN(data.sessionId) || isNaN(data.amount)) {
        toast('Fill all payment fields', 'error'); return;
    }
    const r = await api('/payments', 'POST', data);
    if (r.ok) { toast('Payment recorded!'); loadPayments(); clearForm('payment-form'); }
    else toast(r.data.error || 'Session may already have a payment', 'error');
}

// ── TICKETS ──────────────────────────────────
async function loadTickets() {
    const tbody = document.getElementById('tickets-body');
    tbody.innerHTML = `<tr class="loading-row"><td colspan="6"><div class="spinner"></div> Loading...</td></tr>`;
    const r = await api('/tickets');
    if (!r.ok) { toast('Failed to load tickets', 'error'); return; }
    tbody.innerHTML = r.data.length === 0
        ? `<tr><td colspan="6" class="loading-row">No tickets</td></tr>`
        : r.data.map(t => `
            <tr>
                <td><strong>#${t.ticketId}</strong></td>
                <td>${t.operatorName || 'Station #' + t.stationId}</td>
                <td style="max-width:220px;font-size:12px">${t.issueDesc}</td>
                <td style="font-size:12px">${t.openedTime ? t.openedTime.substring(0,16) : '—'}</td>
                <td>${t.closedTime
                    ? '<span class="badge badge-green">Closed</span>'
                    : '<span class="badge badge-red">Open</span>'}
                </td>
                <td>${!t.closedTime
                    ? `<button class="btn btn-green btn-sm" onclick="closeTicket(${t.ticketId})">✔ Close</button>`
                    : '—'}
                </td>
            </tr>`).join('');
}

async function openTicket() {
    const data = {
        stationId: parseInt(document.getElementById('tkt-station').value),
        issueDesc: document.getElementById('tkt-issue').value.trim()
    };
    if (!data.issueDesc || isNaN(data.stationId)) {
        toast('Fill all ticket fields', 'error'); return;
    }
    const r = await api('/tickets', 'POST', data);
    if (r.ok) { toast('Ticket opened!'); loadTickets(); clearForm('ticket-form'); }
    else toast(r.data.error || 'Error', 'error');
}

async function closeTicket(id) {
    const r = await api('/tickets/close', 'POST', { ticketId: id });
    if (r.ok) { toast('Ticket closed!'); loadTickets(); }
    else toast('Error closing ticket', 'error');
}

// ── HELPERS ──────────────────────────────────
function clearForm(formId) {
    const form = document.getElementById(formId);
    if (form) form.querySelectorAll('input, select').forEach(el => {
        if (el.tagName === 'SELECT') el.selectedIndex = 0;
        else el.value = '';
    });
}

// ── INIT ─────────────────────────────────────
document.addEventListener('DOMContentLoaded', () => {
    document.querySelectorAll('#sidebar nav a').forEach(a => {
        a.addEventListener('click', e => {
            e.preventDefault();
            navigate(a.dataset.page);
        });
    });
    navigate('page-dashboard');
});