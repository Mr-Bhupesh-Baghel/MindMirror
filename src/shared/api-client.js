(function () {
  const DEFAULT_BASE_URL = "http://localhost:8081";
  const CONFIG_KEY = "mindmirrorApiBaseUrl";
  const TOKEN_KEYS = ["mindmirrorAccessToken", "accessToken"];
  const AUTH_KEYS = ["mindmirrorAuth", "auth"];

  function baseUrl() {
    return localStorage.getItem(CONFIG_KEY) || DEFAULT_BASE_URL;
  }

  function setBaseUrl(value) {
    if (value) {
      localStorage.setItem(CONFIG_KEY, value.replace(/\/$/, ""));
    }
  }

  function getAccessToken() {
    for (const key of TOKEN_KEYS) {
      const token = localStorage.getItem(key);
      if (token) {
        return token;
      }
    }

    for (const key of AUTH_KEYS) {
      try {
        const auth = JSON.parse(localStorage.getItem(key) || "null");
        if (auth?.accessToken) {
          return auth.accessToken;
        }
      } catch {
        // Ignore malformed legacy auth entries.
      }
    }

    return "";
  }

  function setSession(auth) {
    if (!auth) {
      return;
    }

    localStorage.setItem("mindmirrorAuth", JSON.stringify(auth));
    if (auth.accessToken) {
      localStorage.setItem("mindmirrorAccessToken", auth.accessToken);
    }
    if (auth.refreshToken) {
      localStorage.setItem("mindmirrorRefreshToken", auth.refreshToken);
    }

    window.dispatchEvent(new CustomEvent("mindmirror:login", { detail: auth }));
  }

  function getSession() {
    for (const key of AUTH_KEYS) {
      try {
        const auth = JSON.parse(localStorage.getItem(key) || "null");
        if (auth?.accessToken || auth?.refreshToken) {
          return auth;
        }
      } catch {
        // Ignore malformed legacy auth entries.
      }
    }

    return {
      accessToken: localStorage.getItem("mindmirrorAccessToken") || "",
      refreshToken: localStorage.getItem("mindmirrorRefreshToken") || ""
    };
  }

  async function logout() {
    const refreshToken = getSession().refreshToken;
    if (refreshToken) {
      try {
        await request("/api/auth/logout", { method: "POST", body: { refreshToken } });
      } catch {
        // Local logout should still complete if the server is offline.
      }
    }

    clearSession();
  }

  function clearSession() {
    ["mindmirrorAuth", "auth", "mindmirrorAccessToken", "accessToken", "mindmirrorRefreshToken"].forEach(key => {
      localStorage.removeItem(key);
    });
    window.dispatchEvent(new CustomEvent("mindmirror:logout"));
  }

  async function request(path, options = {}) {
    const headers = new Headers(options.headers || {});
    const token = getAccessToken();

    if (token) {
      headers.set("Authorization", `Bearer ${token}`);
    }

    if (options.body !== undefined && !headers.has("Content-Type")) {
      headers.set("Content-Type", "application/json");
    }

    const response = await fetch(`${baseUrl()}${path}`, {
      ...options,
      headers,
      body: options.body === undefined ? undefined : JSON.stringify(options.body)
    });

    if (!response.ok) {
      const message = await response.text();
      const error = new Error(message || `Request failed with ${response.status}`);
      error.status = response.status;
      throw error;
    }

    if (response.status === 204) {
      return null;
    }

    const contentType = response.headers.get("Content-Type") || "";
    return contentType.includes("application/json") ? response.json() : response.text();
  }

  window.MindMirrorApi = {
    baseUrl,
    setBaseUrl,
    getAccessToken,
    getSession,
    setSession,
    clearSession,
    logout,
    request,
    get: path => request(path),
    post: (path, body) => request(path, { method: "POST", body }),
    put: (path, body) => request(path, { method: "PUT", body }),
    patch: (path, body) => request(path, { method: "PATCH", body }),
    delete: path => request(path, { method: "DELETE" })
  };
})();
