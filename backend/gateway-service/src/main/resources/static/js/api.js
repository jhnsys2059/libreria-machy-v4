const API_BASE = window.location.hostname === 'localhost' ? 'http://localhost:8080/api' : window.location.origin + '/api';

const api = {
  _token: null,
  _userId: null,
  _userNameFull: null,

  setToken(token) {
    this._token = token;
    if (!token) { this._userId = null; this._userNameFull = null; }
  },
  setUserId(id) { this._userId = id; },
  setUserNameFull(name) { this._userNameFull = name; },
  getUserId() { return this._userId; },
  getToken() { return this._token; },

  async _fetch(method, path, body) {
    const headers = { 'Content-Type': 'application/json' };
    if (this._token) {
      headers['Authorization'] = 'Bearer ' + this._token;
      if (this._userId) headers['X-User-Id'] = this._userId;
      if (this._userNameFull) headers['X-User-Nombre'] = this._userNameFull;
    }
    const opts = { method, headers };
    if (body) opts.body = JSON.stringify(body);
    const res = await fetch(API_BASE + path, opts);
    if (res.status === 204) return null;
    const json = await res.json();
    if (!json.success) throw new Error(json.message || 'Error de red');
    return json.data;
  },

  get(path) { return this._fetch('GET', path); },
  post(path, body) { return this._fetch('POST', path, body); },
  put(path, body) { return this._fetch('PUT', path, body); },
  patch(path, body) { return this._fetch('PATCH', path, body); },
  del(path) { return this._fetch('DELETE', path); },

  // ── Auth ──
  login(username, password) {
    return this.post('/auth/login', { username, password });
  },
  recover(usernameOrEmail) {
    return this.post('/auth/recover', { usernameOrEmail });
  },

  // ── Dashboard ──
  getDashboard() { return this.get('/dashboard'); },

  // ── Products ──
  getProducts(q, categoria) {
    let path = '/products';
    const params = [];
    if (q) params.push('q=' + encodeURIComponent(q));
    if (categoria) params.push('categoria=' + encodeURIComponent(categoria));
    if (params.length) path += '?' + params.join('&');
    return this.get(path);
  },
  getActiveProducts() { return this.get('/products/active'); },
  getProduct(id) { return this.get('/products/' + id); },
  getProductByCode(code) { return this.get('/products/by-code/' + encodeURIComponent(code)); },
  createProduct(data) { return this.post('/products', data); },
  updateProduct(id, data) { return this.put('/products/' + id, data); },
  toggleProduct(id) { return this.patch('/products/' + id + '/toggle'); },

  // ── Sales ──
  getSales() { return this.get('/sales'); },
  getSale(id) { return this.get('/sales/' + id); },
  createSale(data) { return this.post('/sales', data); },
  cancelSale(id, motivo) { return this.post('/sales/' + id + '/cancel', { motivo }); },

  // ── Users ──
  getUsers() { return this.get('/users'); },
  getUser(id) { return this.get('/users/' + id); },
  createUser(data) { return this.post('/users', data); },
  updateUser(id, data) { return this.put('/users/' + id, data); },
  toggleUser(id) { return this.patch('/users/' + id + '/toggle'); },
  getUserDashboard() { return this.get('/users/dashboard'); },

  // ── Attendance ──
  getAttendanceStatus() { return this.get('/attendance/status'); },
  checkIn() { return this.post('/attendance/check-in'); },
  checkOut() { return this.post('/attendance/check-out'); },
  adminAttendance(usuarioId, tipo) { return this.post('/attendance/admin', { usuarioId, tipo }); },

  // ── Categories ──
  getCategories() { return this.get('/categories'); },
  createCategory(nombre) { return this.post('/categories', { nombre }); },
  deleteCategory(id) { return this.patch('/categories/' + id + '/toggle'); },

  // ── Suppliers ──
  getSuppliers() { return this.get('/suppliers'); },
  createSupplier(data) { return this.post('/suppliers', data); },
  deleteSupplier(id) { return this.patch('/suppliers/' + id + '/toggle'); },

  // ── Config ──
  getConfig() { return this.get('/config'); },
  saveConfig(data) { return this.put('/config', data); },

  // ── Reports ──
  getSalesReport() { return this.get('/sales/reports'); },
  getInventoryReport() { return this.get('/products/inventory-summary'); },
  getAttendanceReport() { return this.get('/attendance/weekly-report'); },
  getAttendanceLog() { return this.get('/attendance/log'); },

  // ── Scan Session ──
  createScanSession() { return this.post('/scan/session'); },
  endScanSession(sessionId) { return this.del('/scan/session/' + sessionId); },

  // ── Backup ──
  backupExportAuth() { return this.get('/auth/backup/export'); },
  backupImportAuth(data) { return this.post('/auth/backup/import', data); },
  backupExportProduct() { return this.get('/products/backup/export'); },
  backupImportProduct(data) { return this.post('/products/backup/import', data); },
  backupExportSale() { return this.get('/sales/backup/export'); },
  backupImportSale(data) { return this.post('/sales/backup/import', data); },
};
