'use strict';
/* ---------- helpers ---------- */
const $ = (s, r = document) => r.querySelector(s);
const esc = s => String(s ?? '').replace(/[&<>"']/g, c => ({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[c]));
const store = {get:(k, d) => { try { return localStorage.getItem(k) ?? d } catch { return d } }, set:(k, v) => { try { localStorage.setItem(k, v) } catch {} }};
let BASE = store.get('api', '/inventory-management-service/api/v1');
const money = n => n == null ? '—' : Number(n).toLocaleString('en-US', {minimumFractionDigits:2, maximumFractionDigits:2});
const fdate = d => d ? new Date(d).toLocaleString('en-GB', {dateStyle:'medium', timeStyle:'short'}) : '—';

const TYPES = ['INBOUND', 'OUTBOUND', 'ADJUSTMENT', 'DISPOSAL'];
const TL = {INBOUND:'Inbound', OUTBOUND:'Outbound', ADJUSTMENT:'Adjustment', DISPOSAL:'Disposal'};
const NAV = [
  ['dashboard', 'Dashboard', 'M3 3h7v9H3zM14 3h7v5h-7zM14 12h7v9h-7zM3 16h7v5H3z'],
  ['products', 'Products', 'M21 8l-9-5-9 5v8l9 5 9-5zM3 8l9 5 9-5M12 13v8'],
  ['categories', 'Categories', 'M3 12V3h9l9 9-9 9zM7.5 7.5h.01'],
  ['operations', 'Stock operations', 'M7 4L3 8l4 4M3 8h14M17 20l4-4-4-4M21 16H7'],
  ['transactions', 'Transactions', 'M8 6h13M8 12h13M8 18h13M3 6h.01M3 12h.01M3 18h.01'],
  ['low', 'Low stock', 'M12 3l10 18H2zM12 10v5M12 18h.01']
];
/* field kinds mirror the backend validation: money = positive decimal, int1 = min 1, int0 = min 0 */
const OPS = {
  INBOUND:{url:'inbound', f:[['unitPrice','Unit price','money'],['quantity','Quantity','int1'],['supplierInfo','Supplier','text'],['notes','Notes','text',0]]},
  OUTBOUND:{url:'outbound', f:[['unitPrice','Unit price','money'],['quantity','Quantity','int1'],['orderReference','Order reference','text'],['notes','Notes','text',0]]},
  ADJUSTMENT:{url:'adjustment', f:[['actualQuantity','Counted quantity','int0'],['unitPrice','Unit price','money'],['adjustmentReason','Adjustment reason','text']]},
  DISPOSAL:{url:'disposal', f:[['unitCost','Unit cost','money'],['quantity','Quantity','int1'],['disposalReason','Disposal reason','text']]}
};
const OPHINT = {
  INBOUND:'Adds the quantity to the current stock.',
  OUTBOUND:'Removes the quantity from stock. It cannot exceed the current stock.',
  ADJUSTMENT:'Sets stock to the counted quantity. It must differ from the current stock.',
  DISPOSAL:'Removes damaged or expired items. It cannot exceed the current stock.'
};
const S = {pp:{page:0, kw:'', cat:''}, cp:{page:0}, tx:{page:0, type:'INBOUND'}, low:{page:0}, op:'INBOUND', opProduct:''};

/* ---------- api ---------- */
async function api(path, {method = 'GET', body} = {}) {
  let r;
  try { r = await fetch(BASE + path, {method, headers: body ? {'Content-Type':'application/json'} : {}, body: body && JSON.stringify(body)}) }
  catch { throw new Error('Cannot reach the server. Check the API URL.') }
  if (r.status === 204) return null;
  const j = await r.json().catch(() => null);
  if (!r.ok) {
    if (j?.message) throw new Error(j.message);
    if (r.status === 401 || r.status === 403) throw new Error('Access denied (' + r.status + '). Check SecurityConfig.');
    throw new Error('Request failed (' + r.status + ').');
  }
  return j?.data ?? null;
}
/* Spring Page may serialize flat (number, totalPages...) or nested under "page" */
const pg = p => { const n = p.number ?? p.page?.number ?? 0, tp = p.totalPages ?? p.page?.totalPages ?? 0, te = p.totalElements ?? p.page?.totalElements ?? 0; return {n, tp, te, first:n === 0, last:n + 1 >= tp} };
const allCats = () => api('/categories/all?size=500').then(p => p.content);
const allProds = () => api('/products/all?size=500').then(p => p.content);
/* the product list endpoints do not return stock levels, so load them from the stock endpoint when missing */
async function withStock(list) {
  await Promise.all(list.filter(p => p.currentQuantity == null || p.minStockLevel == null).map(async p => {
    try { const s = await api(`/inventory/product/${p.id}/stock`); p.currentQuantity = s.currentQuantity; p.minStockLevel = s.minStockLevel } catch {}
  }));
  return list;
}

/* ---------- ui kit ---------- */
function toast(msg, type = '') {
  const t = document.createElement('div'); t.className = 'toast ' + type; t.textContent = msg;
  $('#toasts').append(t); setTimeout(() => t.remove(), 4500);
}
function modal(title, html, onsubmit, ok = 'Save', danger = false) {
  const m = $('#modal'); m.hidden = false;
  m.innerHTML = `<form class="sheet"><h2>${title}</h2>${html}<div class="row end"><button type="button" class="btn ghost" data-close>Cancel</button><button class="btn ${danger ? 'danger' : ''}">${ok}</button></div></form>`;
  m.onclick = e => { if (e.target === m || e.target.hasAttribute('data-close')) m.hidden = true };
  $('form', m).onsubmit = async e => {
    e.preventDefault(); const b = $('.btn:not(.ghost)', m); b.disabled = true;
    try { await onsubmit(Object.fromEntries(new FormData(e.target))); m.hidden = true }
    catch (x) { toast(x.message, 'err'); b.disabled = false }
  };
  $('input,select', m)?.focus();
}
addEventListener('keydown', e => { if (e.key === 'Escape') $('#modal').hidden = true });
const ATTR = {money:'step="0.01" min="0.01"', int1:'step="1" min="1"', int0:'step="1" min="0"', text:''};
const fld = (name, label, o = {}) => {
  const {kind = 'text', value = '', req = true, opts, cls = '', extra = ''} = o;
  const inner = opts
    ? `<select name="${name}" ${req ? 'required' : ''}>${opts}</select>`
    : `<input name="${name}" type="${kind === 'text' ? 'text' : 'number'}" value="${esc(value)}" ${req ? 'required' : ''} ${ATTR[kind]} ${extra}>`;
  return `<label class="${cls}">${label}${req ? '' : ' <em>(optional)</em>'}${inner}</label>`;
};
const options = (list, sel, label = x => x.name, ph) =>
  (ph ? `<option value="">${ph}</option>` : '') + list.map(x => `<option value="${x.id}" ${x.id === sel ? 'selected' : ''}>${esc(label(x))}</option>`).join('');
function gauge(q, min) {
  if (q == null || min == null) return '<span class="qty">—</span>';
  const scale = Math.max(q, min * 2, 1), cls = q === 0 ? 'out' : q <= min ? 'low' : '';
  return `<span class="gauge ${cls}" title="Minimum level: ${min}"><i style="width:${q / scale * 100}%"></i><u style="left:${min / scale * 100}%"></u></span><span class="qty"><b>${q}</b> / min ${min}</span>`;
}
const pager = p => { const g = pg(p); return `<div class="pager"><span>${g.te} results · page ${g.n + 1} of ${Math.max(g.tp, 1)}</span><span class="row"><button class="btn ghost sm" data-pg="-1" ${g.first ? 'disabled' : ''}>Previous</button><button class="btn ghost sm" data-pg="1" ${g.last ? 'disabled' : ''}>Next</button></span></div>` };
const bindPager = (v, st) => v.querySelectorAll('[data-pg]').forEach(b => b.onclick = () => { st.page += +b.dataset.pg; route() });
const empty = t => `<div class="empty">${t}</div>`;
const typeBadge = t => `<span class="badge ${t}">${TL[t] || t}</span>`;
const confirmDel = (what, note, run) => modal('Delete this item?', `<p><b>${esc(what)}</b> will be permanently deleted.</p><p class="hint">${note}</p>`, async () => { await run(); toast('Deleted'); route() }, 'Delete', true);

/* ---------- pages ---------- */
async function dashboard(v) {
  const [prods, cats, low, ...tx] = await Promise.all([
    api('/products/all?size=1'), api('/categories/all?size=1'), api('/inventory/low-stock?size=5'),
    ...TYPES.map(t => api(`/inventory/transactions?operationType=${t}&size=5`))
  ]);
  const counts = tx.map(p => pg(p).te), total = counts.reduce((a, b) => a + b, 0), max = Math.max(...counts, 1);
  const recent = tx.flatMap(p => p.content).sort((a, b) => new Date(b.transactionDate) - new Date(a.transactionDate)).slice(0, 6);
  v.innerHTML = `
  <div class="grid kpis">
    <div class="card kpi"><small>Products</small><b>${pg(prods).te}</b></div>
    <div class="card kpi"><small>Categories</small><b>${pg(cats).te}</b></div>
    <div class="card kpi"><small>Transactions</small><b>${total}</b></div>
    <div class="card kpi ${pg(low).te ? 'warn' : ''}"><small>Low-stock products</small><b>${pg(low).te}</b></div>
  </div>
  <div class="grid two">
    <div class="card"><h2>Transactions by type</h2><div class="hbars">
      ${TYPES.map((t, i) => `<div class="hb"><span>${TL[t]}</span><div><i style="width:${counts[i] / max * 100}%"></i></div><b>${counts[i]}</b></div>`).join('')}
    </div></div>
    <div class="card"><h2>Needs attention</h2><div class="list">
      ${low.content.length ? low.content.map(s => `<div><span>${esc(s.productName)}<small>${esc(s.sku)}</small></span><span class="qty"><b>${s.currentQuantity}</b> / min ${s.minStockLevel}</span></div>`).join('') : empty('All products are above their minimum level.')}
    </div></div>
  </div>
  <div class="card tbl" style="margin-top:16px"><div style="padding:18px 20px 0"><h2>Recent transactions</h2></div>
    ${recent.length ? `<table><tbody>${recent.map(t => `<tr><td><b>${esc(t.productName)}</b></td><td>${typeBadge(t.transactionType)}</td><td class="num">${t.quantity} pcs</td><td class="qty">${fdate(t.transactionDate)}</td></tr>`).join('')}</tbody></table>` : empty('No transactions yet.')}
  </div>`;
}

async function products(v) {
  const s = S.pp, cats = await allCats();
  const path = s.kw ? `/products/search?keyword=${encodeURIComponent(s.kw)}&` : s.cat ? `/products/category/${s.cat}?` : '/products/all?';
  const p = await api(`${path}page=${s.page}&size=10`);
  await withStock(p.content);
  v.innerHTML = `
  <div class="bar">
    <input id="q" type="search" placeholder="Search by name or SKU, then press Enter…" value="${esc(s.kw)}">
    <select id="cf">${options(cats, s.cat, c => c.name, 'All categories')}</select>
    <button class="btn" id="add">New product</button>
  </div>
  <div class="card tbl">${p.content.length ? `<table><thead><tr><th>SKU</th><th>Name</th><th>Category</th><th class="num">Price</th><th>Stock</th><th></th></tr></thead><tbody>
    ${p.content.map(x => `<tr><td class="sku">${esc(x.sku)}</td><td><b>${esc(x.name)}</b></td><td>${esc(x.categoryName)}</td><td class="num">${money(x.price)}</td><td>${gauge(x.currentQuantity, x.minStockLevel)}</td>
    <td class="act"><button class="btn ghost sm" data-edit="${x.id}">Edit</button><button class="btn ghost sm" data-del="${x.id}">Delete</button></td></tr>`).join('')}
  </tbody></table>${pager(p)}` : empty('No products found.')}</div>`;
  $('#q').onchange = e => { s.kw = e.target.value.trim(); s.cat = ''; s.page = 0; route() };
  $('#cf').onchange = e => { s.cat = e.target.value; s.kw = ''; s.page = 0; route() };
  bindPager(v, s);
  const form = (x = {}) => `<div class="formgrid">${fld('sku', 'SKU', {value:x.sku})}${fld('name', 'Name', {value:x.name})}
    ${fld('categoryId', 'Category', {opts:options(cats, x.categoryId, c => c.name, 'Select a category…'), cls:'full'})}
    ${fld('price', 'Price', {kind:'money', value:x.price})}${fld('minStockLevel', 'Minimum stock level', {kind:'int0', value:x.minStockLevel ?? 0})}
    ${x.id ? `<input type="hidden" name="initialQuantity" value="${x.currentQuantity ?? 0}"><p class="hint full">Current stock is ${x.currentQuantity ?? '—'}. Change it from Stock operations.</p>`
           : fld('initialQuantity', 'Initial quantity', {kind:'int0', value:0, cls:'full'})}</div>`;
  const body = d => ({...d, price:+d.price, minStockLevel:+d.minStockLevel, initialQuantity:+d.initialQuantity});
  $('#add').onclick = () => modal('New product', form(), async d => { await api('/products/new', {method:'POST', body:body(d)}); toast('Product created'); route() });
  v.querySelectorAll('[data-edit]').forEach(b => b.onclick = () => { const x = p.content.find(i => i.id === b.dataset.edit);
    modal('Edit product', form(x), async d => { await api('/products/' + x.id, {method:'PUT', body:body(d)}); toast('Product updated'); route() }) });
  v.querySelectorAll('[data-del]').forEach(b => b.onclick = () => { const x = p.content.find(i => i.id === b.dataset.del);
    confirmDel(x.name, 'Only products with zero stock can be deleted.', () => api('/products/' + x.id, {method:'DELETE'})) });
}

async function categories(v) {
  const s = S.cp, [p, cats] = await Promise.all([api(`/categories/all?page=${s.page}&size=10`), allCats()]);
  v.innerHTML = `<div class="bar"><span style="flex:1"></span><button class="btn" id="add">New category</button></div>
  <div class="card tbl">${p.content.length ? `<table><thead><tr><th>Name</th><th>Description</th><th>Parent category</th><th></th></tr></thead><tbody>
    ${p.content.map(c => `<tr><td><b>${esc(c.name)}</b></td><td>${esc(c.description || '—')}</td><td>${esc(c.parentName || '—')}</td>
    <td class="act"><button class="btn ghost sm" data-edit="${c.id}">Edit</button><button class="btn ghost sm" data-del="${c.id}">Delete</button></td></tr>`).join('')}
  </tbody></table>${pager(p)}` : empty('No categories yet. Create the first one.')}</div>`;
  bindPager(v, s);
  const form = (c = {}) => fld('name', 'Name', {value:c.name}) + fld('description', 'Description', {value:c.description, req:false}) +
    fld('parentId', 'Parent category', {req:false, opts:options(cats.filter(x => x.id !== c.id), c.parentId, x => x.name, 'None')});
  const body = d => ({...d, parentId:d.parentId || null});
  $('#add').onclick = () => modal('New category', form(), async d => { await api('/categories/new', {method:'POST', body:body(d)}); toast('Category created'); route() });
  v.querySelectorAll('[data-edit]').forEach(b => b.onclick = () => { const c = p.content.find(i => i.id === b.dataset.edit);
    modal('Edit category', form(c), async d => { await api('/categories/' + c.id, {method:'PUT', body:body(d)}); toast('Category updated'); route() }) });
  v.querySelectorAll('[data-del]').forEach(b => b.onclick = () => { const c = p.content.find(i => i.id === b.dataset.del);
    confirmDel(c.name, 'Categories that have subcategories or products cannot be deleted.', () => api('/categories/' + c.id, {method:'DELETE'})) });
}

async function operations(v) {
  const prods = await allProds(), cfg = OPS[S.op];
  if (!prods.some(p => p.id === S.opProduct)) S.opProduct = '';
  v.innerHTML = `<div class="seg">${TYPES.map(t => `<button data-t="${t}" class="${t === S.op ? 'on' : ''}">${TL[t]}</button>`).join('')}</div>
  <form class="card" id="opf" style="max-width:620px">
    <p class="hint">${OPHINT[S.op]}</p>
    <div class="formgrid">
      ${fld('productId', 'Product', {cls:'full', opts:options(prods, S.opProduct, p => `${p.sku} — ${p.name}`, 'Select a product…')})}
      <p class="hint full" id="hint"></p>
      ${cfg.f.map(([n, l, k, req = 1]) => fld(n, l, {kind:k, req:!!req, cls:k === 'text' ? 'full' : ''})).join('')}
    </div>
    <div class="row end"><button class="btn">Record ${TL[S.op].toLowerCase()}</button></div>
  </form>`;
  const hint = async () => {
    const id = $('[name=productId]').value, h = $('#hint'), q = $('[name=quantity]'); h.textContent = '';
    if (!id) return;
    try {
      const st = await api(`/inventory/product/${id}/stock`);
      h.textContent = `Current stock: ${st.currentQuantity} · minimum level: ${st.minStockLevel}`;
      if (q && (S.op === 'OUTBOUND' || S.op === 'DISPOSAL')) q.max = st.currentQuantity;
    } catch (e) { h.textContent = e.message }
  };
  $('[name=productId]').onchange = e => { S.opProduct = e.target.value; hint() }; hint();
  v.querySelectorAll('[data-t]').forEach(b => b.onclick = () => { S.op = b.dataset.t; route() });
  $('#opf').onsubmit = async e => {
    e.preventDefault(); const btn = $('.btn', e.target); btn.disabled = true;
    const d = Object.fromEntries(new FormData(e.target)), body = {productId:d.productId};
    for (const [n, , k] of cfg.f) if (d[n] !== '') body[n] = k === 'text' ? d[n] : +d[n];
    try {
      const r = await api('/inventory/operations/' + cfg.url, {method:'POST', body});
      toast(`${r.productName}: stock is now ${r.currentQuantity}`); route();
    } catch (x) { toast(x.message, 'err'); btn.disabled = false }
  };
}

async function transactions(v) {
  const s = S.tx, p = await api(`/inventory/transactions?operationType=${s.type}&page=${s.page}&size=10`);
  v.innerHTML = `<div class="seg">${TYPES.map(t => `<button data-t="${t}" class="${t === s.type ? 'on' : ''}">${TL[t]}</button>`).join('')}</div>
  <div class="card tbl">${p.content.length ? `<table><thead><tr><th>Date</th><th>Product</th><th>Category</th><th>Type</th><th class="num">Quantity</th><th class="num">Unit price</th><th class="num">Total</th></tr></thead><tbody>
    ${p.content.map(t => `<tr><td class="qty">${fdate(t.transactionDate)}</td><td><b>${esc(t.productName)}</b></td><td>${esc(t.categoryName)}</td><td>${typeBadge(t.transactionType)}</td><td class="num">${t.quantity}</td><td class="num">${money(t.unitPrice)}</td><td class="num"><b>${money(t.totalSum)}</b></td></tr>`).join('')}
  </tbody></table>${pager(p)}` : empty(`No ${TL[s.type].toLowerCase()} transactions yet.`)}</div>`;
  v.querySelectorAll('[data-t]').forEach(b => b.onclick = () => { s.type = b.dataset.t; s.page = 0; route() });
  bindPager(v, s);
}

async function low(v) {
  const s = S.low, p = await api(`/inventory/low-stock?page=${s.page}&size=10`);
  v.innerHTML = `<div class="card tbl">${p.content.length ? `<table><thead><tr><th>SKU</th><th>Product</th><th>Stock level</th><th></th></tr></thead><tbody>
    ${p.content.map(x => `<tr><td class="sku">${esc(x.sku)}</td><td><b>${esc(x.productName)}</b></td><td>${gauge(x.currentQuantity, x.minStockLevel)}</td><td class="act"><button class="btn sm" data-re="${x.productId}">Restock</button></td></tr>`).join('')}
  </tbody></table>${pager(p)}` : empty('No products are at or below their minimum level.')}</div>`;
  bindPager(v, s);
  v.querySelectorAll('[data-re]').forEach(b => b.onclick = () => { S.op = 'INBOUND'; S.opProduct = b.dataset.re; location.hash = '#/operations' });
}

/* ---------- router & boot ---------- */
const ROUTES = {dashboard, products, categories, operations, transactions, low};
async function route() {
  const r = ROUTES[location.hash.slice(2)] ? location.hash.slice(2) : 'dashboard';
  $('#title').textContent = NAV.find(n => n[0] === r)[1];
  document.querySelectorAll('#nav a').forEach(a => a.classList.toggle('on', a.dataset.r === r));
  const v = $('#view'); v.innerHTML = '<div class="load">Loading…</div>';
  try { await ROUTES[r](v) }
  catch (e) { v.innerHTML = `<div class="card err"><b>Could not load data</b><p>${esc(e.message)}</p><button class="btn" onclick="route()">Try again</button></div>` }
}
$('#nav').innerHTML = NAV.map(([r, l, d]) => `<a href="#/${r}" data-r="${r}"><svg viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><path d="${d}"/></svg>${l}</a>`).join('');
const setTheme = t => { document.documentElement.dataset.theme = t; store.set('theme', t); $('#theme').textContent = t === 'dark' ? 'Light theme' : 'Dark theme' };
setTheme(store.get('theme', matchMedia('(prefers-color-scheme:dark)').matches ? 'dark' : 'light'));
$('#theme').onclick = () => setTheme(document.documentElement.dataset.theme === 'dark' ? 'light' : 'dark');
$('#api').onclick = () => modal('API URL', fld('base', 'Backend base URL', {value:BASE}) + '<p class="hint">The default works when this page is served by the Spring Boot app. If you open it from another origin, enter the full URL, e.g. http://localhost:8080/inventory-management-service/api/v1, and add that origin to app.cors.allowed-origins.</p>',
  async d => { BASE = d.base.replace(/\/$/, ''); store.set('api', BASE); route() });
addEventListener('hashchange', route);
route();
