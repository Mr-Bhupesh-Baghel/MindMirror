(function () {
  function api() {
    return window.MindMirrorApi;
  }

  function migration() {
    return window.MindMirrorMigration;
  }

  function formData(form) {
    return Object.fromEntries(new FormData(form).entries());
  }

  function sessionUser() {
    return api().getSession()?.user || null;
  }

  function formatTime(value) {
    if (!value) {
      return "Never";
    }
    return new Date(value).toLocaleString();
  }

  function createDashboard(root) {
    const authStatus = root.querySelector("[data-auth-status]");
    const networkStatus = root.querySelector("[data-network-status]");
    const syncStatus = root.querySelector("[data-sync-status]");
    const lastSync = root.querySelector("[data-last-sync]");
    const progress = root.querySelector("[data-sync-progress]");
    const cleanupButton = root.querySelector("[data-cleanup-local]");
    const logoutButton = root.querySelector("[data-logout]");
    const syncButton = root.querySelector("[data-sync-now]");
    const loginForm = root.querySelector("[data-login-form]");
    const registerForm = root.querySelector("[data-register-form]");

    function setBusy(isBusy) {
      progress.hidden = !isBusy;
      syncButton.disabled = isBusy;
    }

    function render(status = migration().getStatus()) {
      const user = sessionUser();
      const cloud = status.cloud || {};
      root.classList.toggle("is-authenticated", Boolean(api().getAccessToken()));
      authStatus.textContent = user ? `Signed in as ${user.displayName || user.email}` : "Sign in to back up local progress.";
      networkStatus.textContent = navigator.onLine ? "Online" : "Offline mode";
      syncStatus.textContent = status.message || (migration().hasLocalData()
        ? "Local browser data detected. Run migration after login."
        : "No local browser data is waiting.");
      lastSync.textContent = `Last sync: ${formatTime(status.lastSyncAt || cloud.lastSyncAt)}`;
      cleanupButton.hidden = status.state !== "complete";
      setBusy(status.state === "running");
    }

    async function authenticate(path, body) {
      setBusy(true);
      syncStatus.textContent = "Signing in...";
      try {
        api().setSession(await api().post(path, body));
        render({ message: "Signed in. Migration will start automatically." });
      } catch (error) {
        render({ message: `Authentication failed: ${error.message}` });
      } finally {
        setBusy(false);
      }
    }

    loginForm.addEventListener("submit", event => {
      event.preventDefault();
      authenticate("/api/auth/login", formData(loginForm));
    });

    registerForm.addEventListener("submit", event => {
      event.preventDefault();
      authenticate("/api/auth/register", formData(registerForm));
    });

    syncButton.addEventListener("click", async () => {
      setBusy(true);
      render({ state: "running", message: "Syncing local data..." });
      try {
        render(await migration().run());
      } catch (error) {
        render({ state: "failed", message: `Sync failed: ${error.message}` });
      } finally {
        setBusy(false);
      }
    });

    cleanupButton.addEventListener("click", () => {
      migration().cleanupLocalData();
      render();
    });

    logoutButton.addEventListener("click", async () => {
      await api().logout();
      render({ message: "Signed out. Offline local data remains available." });
    });

    window.addEventListener("online", () => render({ ...migration().getStatus(), message: "Back online. Background sync can run." }));
    window.addEventListener("offline", () => render({ ...migration().getStatus(), message: "Offline mode. Changes will stay local." }));
    window.addEventListener("mindmirror:migration-status", event => render(event.detail));
    window.addEventListener("mindmirror:login", () => render({ message: "Signed in. Checking local data..." }));
    window.addEventListener("mindmirror:logout", () => render());

    render();
  }

  window.MindMirrorAuthDashboard = {
    create: createDashboard
  };
})();
