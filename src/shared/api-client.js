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
    setSession,
    request,
    get: path => request(path),
    post: (path, body) => request(path, { method: "POST", body }),
    put: (path, body) => request(path, { method: "PUT", body }),
    patch: (path, body) => request(path, { method: "PATCH", body }),
    delete: path => request(path, { method: "DELETE" })
  };
})();
