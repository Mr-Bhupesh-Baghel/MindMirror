(function () {
  const SESSION_KEY = "mindmirror.account.session";
  const OFFLINE_MESSAGE = "MindMirror account services are currently offline. Please try again later or continue as a guest.";
  const apiBase = (window.MINDMIRROR_API_BASE || (
    window.location.protocol === "file:" || window.location.hostname === "localhost"
      ? "http://localhost:8084/api"
      : "/api"
  )).replace(/\/$/, "");

  function readSession() {
    try {
      const raw = sessionStorage.getItem(SESSION_KEY);
      return raw ? JSON.parse(raw) : null;
    } catch {
      return null;
    }
  }

  function writeSession(session) {
    if (session) {
      sessionStorage.setItem(SESSION_KEY, JSON.stringify(session));
    } else {
      sessionStorage.removeItem(SESSION_KEY);
    }
  }

  async function request(path, options = {}) {
    const headers = new Headers(options.headers || {});
    if (options.body !== undefined && !headers.has("Content-Type")) {
      headers.set("Content-Type", "application/json");
    }

    const session = readSession();
    if (session?.token) headers.set("Authorization", `Bearer ${session.token}`);

    let response;
    try {
      response = await fetch(`${apiBase}${path}`, { ...options, headers });
    } catch {
      throw new Error(OFFLINE_MESSAGE);
    }

    const raw = await response.text();
    let body = null;
    try {
      body = raw ? JSON.parse(raw) : null;
    } catch {
      body = raw;
    }

    if (!response.ok) {
      const message = body?.message || body?.error || (response.status >= 500 ? OFFLINE_MESSAGE : "The account request could not be completed.");
      const error = new Error(message);
      error.status = response.status;
      throw error;
    }
    return body;
  }

  const api = {
    baseUrl: apiBase,
    request,
    getSession: readSession,
    setSession: writeSession,
    clearSession() {
      writeSession(null);
    },
    async register(payload) {
      const result = await request("/auth/register", { method: "POST", body: JSON.stringify(payload) });
      writeSession(result);
      return result;
    },
    async login(payload) {
      const result = await request("/auth/login", { method: "POST", body: JSON.stringify(payload) });
      writeSession(result);
      return result;
    },
    async getProfile() {
      return request("/user/me");
    },
    async updateProfile(payload) {
      return request("/user/me", { method: "PUT", body: JSON.stringify(payload) });
    },
    async getData() {
      return request("/data");
    },
    async saveData(data) {
      return request("/data", { method: "PUT", body: JSON.stringify(data) });
    },
    async migrateData(guestData) {
      return request("/data/migrate", { method: "POST", body: JSON.stringify({ guestData }) });
    }
  };

  window.MindMirrorApi = api;

  let accountMode = Boolean(readSession()?.token);
  let accountAvailable = !accountMode;
  let accountData = {};
  let dataLoaded = !accountMode;
  let pendingSave = false;
  let changeVersion = 0;
  const pendingChanges = new Map();
  let saveQueue = Promise.resolve();
  let notice;

  function createNotice() {
    if (notice || !document.body) return notice;
    if (!document.getElementById("mindmirror-runtime-style")) {
      const style = document.createElement("style");
      style.id = "mindmirror-runtime-style";
      style.textContent = `
        #mindmirror-runtime-notice { position: fixed; right: 18px; bottom: 18px; z-index: 10000; display: flex; align-items: center; gap: 10px; max-width: min(620px, calc(100% - 36px)); padding: 12px 14px; border: 1px solid #e3b9b9; border-radius: 12px; background: #fff3f3; color: #7b3030; box-shadow: 0 16px 42px rgba(23, 33, 43, .12); font: 700 .86rem/1.4 system-ui, sans-serif; }
        #mindmirror-runtime-notice button { flex: 0 0 auto; padding: 7px 9px; border: 1px solid #c88787; border-radius: 7px; background: #fff; color: #7b3030; font: inherit; }
        @media (max-width: 620px) { #mindmirror-runtime-notice { right: 12px; bottom: 12px; align-items: stretch; flex-direction: column; } }
      `;
      document.head.appendChild(style);
    }
    notice = document.createElement("aside");
    notice.id = "mindmirror-runtime-notice";
    notice.setAttribute("role", "status");
    notice.setAttribute("aria-live", "polite");
    notice.hidden = true;
    notice.innerHTML = `
      <span data-runtime-message></span>
      <button type="button" data-runtime-retry>Try Again</button>
      <button type="button" data-runtime-guest>Continue as Guest</button>`;
    notice.querySelector("[data-runtime-retry]").addEventListener("click", retryAccountConnection);
    notice.querySelector("[data-runtime-guest]").addEventListener("click", continueAsGuest);
    document.body.appendChild(notice);
    return notice;
  }

  function showNotice(message, showActions = true) {
    const element = createNotice();
    if (!element) return;
    element.querySelector("[data-runtime-message]").textContent = message;
    element.querySelector("[data-runtime-retry]").hidden = !showActions;
    element.querySelector("[data-runtime-guest]").hidden = !showActions;
    element.hidden = false;
  }

  function hideNotice() {
    if (notice) notice.hidden = true;
  }

  function showSaveError() {
    showNotice(OFFLINE_MESSAGE, true);
    window.dispatchEvent(new CustomEvent("mindmirror:save-error"));
  }

  async function loadAccountData() {
    try {
      const result = await api.getData();
      accountData = { ...(result?.data || result || {}) };
      pendingChanges.forEach((value, key) => {
        if (value === undefined) delete accountData[key];
        else accountData[key] = value;
      });
      accountAvailable = true;
      dataLoaded = true;
      hideNotice();
      return { available: true };
    } catch (error) {
      accountAvailable = false;
      dataLoaded = false;
      showNotice(OFFLINE_MESSAGE, true);
      return { available: false, error };
    }
  }

  function queueAccountSave() {
    pendingSave = true;
    saveQueue = saveQueue
      .catch(() => undefined)
      .then(async () => {
        if (!accountAvailable) throw new Error(OFFLINE_MESSAGE);
        const saveVersion = changeVersion;
        const result = await api.saveData(accountData);
        if (saveVersion === changeVersion) {
          pendingChanges.clear();
          pendingSave = false;
          hideNotice();
          window.dispatchEvent(new CustomEvent("mindmirror:data-saved"));
        }
        return { ok: true, result };
      })
      .catch(error => {
        showSaveError();
        return { ok: false, error };
      });
    return saveQueue;
  }

  async function retryAccountConnection() {
    if (dataLoaded && pendingSave) {
      accountAvailable = true;
      return queueAccountSave();
    }
    const result = await loadAccountData();
    if (result.available) {
      window.dispatchEvent(new CustomEvent("mindmirror:account-ready"));
      if (pendingChanges.size) return queueAccountSave();
    }
    return result;
  }

  function continueAsGuest() {
    api.clearSession();
    window.location.reload();
  }

  const readyPromise = accountMode ? loadAccountData() : Promise.resolve({ available: true });

  const storage = {
    mode() {
      return accountMode ? "account" : "guest";
    },

    isAccountMode() {
      return accountMode;
    },

    isAccountAvailable() {
      return accountMode && accountAvailable;
    },

    ready() {
      return readyPromise;
    },

    getJson(key, fallback) {
      try {
        if (accountMode) return Object.prototype.hasOwnProperty.call(accountData, key) ? accountData[key] : fallback;
        const value = localStorage.getItem(key);
        return value === null ? fallback : JSON.parse(value);
      } catch {
        return fallback;
      }
    },

    setJson(key, value) {
      if (!accountMode) {
        try {
          localStorage.setItem(key, JSON.stringify(value));
          return Promise.resolve({ ok: true });
        } catch (error) {
          return Promise.resolve({ ok: false, error });
        }
      }
      changeVersion += 1;
      accountData[key] = value;
      pendingChanges.set(key, value);
      return queueAccountSave();
    },

    remove(key) {
      if (!accountMode) {
        localStorage.removeItem(key);
        return Promise.resolve({ ok: true });
      }
      changeVersion += 1;
      delete accountData[key];
      pendingChanges.set(key, undefined);
      return queueAccountSave();
    },

    keysStartingWith(prefix) {
      if (accountMode) return Object.keys(accountData).filter(key => key.startsWith(prefix));
      return Object.keys(localStorage).filter(key => key.startsWith(prefix));
    },

    guestSnapshot() {
      const snapshot = {};
      Object.keys(localStorage).forEach(key => {
        try {
          snapshot[key] = JSON.parse(localStorage.getItem(key));
        } catch {
          snapshot[key] = localStorage.getItem(key);
        }
      });
      return snapshot;
    },

    clearGuestData() {
      Object.keys(localStorage).forEach(key => localStorage.removeItem(key));
    },

    async migrateGuestData() {
      const result = await api.migrateData(storage.guestSnapshot());
      const refreshed = await api.getData();
      accountData = { ...(refreshed?.data || refreshed || {}) };
      return result;
    },

    showNotice,
    offlineMessage: OFFLINE_MESSAGE
  };

  window.MindMirrorStorage = storage;
  window.addEventListener("DOMContentLoaded", () => {
    if (accountMode && !accountAvailable) showNotice(OFFLINE_MESSAGE, true);
  });
})();
