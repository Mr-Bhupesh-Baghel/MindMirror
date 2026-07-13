(function () {
  const api = window.MindMirrorApi;
  const migration = window.MindMirrorMigration;
  const $ = id => document.getElementById(id);
  const sections = ["profileSection", "statisticsSection", "actionsSection", "settingsSection", "dangerSection"].map($);

  function showMessage(message, isError = false) {
    const element = $("accountMessage");
    element.textContent = message;
    element.classList.toggle("is-error", isError);
    element.hidden = !message;
  }

  function setDarkMode(enabled) {
    document.body.classList.toggle("dark", enabled);
  }

  function renderSignedOut(message = "") {
    $("profileName").textContent = "Guest User";
    $("profileEmail").textContent = "Sign in to save your progress";
    $("avatarLetter").textContent = "M";
    $("loginStatus").textContent = "Signed out";
    $("loginStatus").className = "status offline";
    $("authSection").hidden = false;
    sections.forEach(section => { section.hidden = true; });
    setDarkMode(false);
    showMessage(message);
  }

  function renderProfile(user, summary) {
    $("profileName").textContent = user.displayName;
    $("profileEmail").textContent = user.email;
    $("avatarLetter").textContent = user.displayName.charAt(0).toUpperCase();
    $("loginStatus").textContent = "Signed in";
    $("loginStatus").className = "status online";
    $("displayName").value = user.displayName;
    $("email").value = user.email;
    $("routineCount").textContent = summary.routineDays;
    $("waterCount").textContent = summary.waterGlasses;
    $("pushupCount").textContent = summary.pushups;
    $("streakCount").textContent = summary.currentStreak;
    $("darkMode").checked = summary.darkMode;
    $("notifications").checked = summary.notifications;
    $("apiBaseUrl").value = api.baseUrl();
    setDarkMode(summary.darkMode);
    $("authSection").hidden = true;
    sections.forEach(section => { section.hidden = false; });
    $("syncStatus").textContent = migration.hasLocalData()
      ? "Local data is ready to sync to your account."
      : "No unsynced local data was found.";
  }

  async function loadAccount() {
    if (!api.getAccessToken()) {
      renderSignedOut();
      return;
    }
    try {
      const [user, summary] = await Promise.all([api.get("/api/users/me"), api.get("/api/users/me/summary")]);
      api.updateSessionUser(user);
      renderProfile(user, summary);
    } catch (error) {
      api.clearSession();
      renderSignedOut("Your session ended. Please sign in again.");
    }
  }

  async function authenticate(path, form) {
    const submit = form.querySelector("button[type=submit]");
    submit.disabled = true;
    try {
      api.setSession(await api.post(path, Object.fromEntries(new FormData(form).entries())));
      form.reset();
      showMessage(path.endsWith("register") ? "Account created and signed in." : "Signed in successfully.");
      await loadAccount();
    } catch (error) {
      showMessage(`Could not sign in: ${error.message}`, true);
    } finally {
      submit.disabled = false;
    }
  }

  $("loginForm").addEventListener("submit", event => { event.preventDefault(); authenticate("/api/auth/login", event.currentTarget); });
  $("registerForm").addEventListener("submit", event => { event.preventDefault(); authenticate("/api/auth/register", event.currentTarget); });

  $("profileForm").addEventListener("submit", async event => {
    event.preventDefault();
    const form = event.currentTarget;
    if ($("newPassword").value !== $("confirmPassword").value) {
      showMessage("The new-password fields do not match.", true);
      return;
    }
    const payload = Object.fromEntries(new FormData(form).entries());
    delete payload.confirmPassword;
    Object.keys(payload).forEach(key => { if (!payload[key]) delete payload[key]; });
    try {
      const user = await api.put("/api/users/me", payload);
      api.updateSessionUser(user);
      $("currentPassword").value = "";
      $("newPassword").value = "";
      $("confirmPassword").value = "";
      showMessage("Profile saved.");
      await loadAccount();
    } catch (error) {
      showMessage(`Could not save profile: ${error.message}`, true);
    }
  });

  async function savePreferences() {
    try {
      await api.put("/api/users/me", { darkMode: $("darkMode").checked, notifications: $("notifications").checked });
      setDarkMode($("darkMode").checked);
      showMessage("Preferences saved.");
    } catch (error) {
      showMessage(`Could not save preferences: ${error.message}`, true);
    }
  }
  $("darkMode").addEventListener("change", savePreferences);
  $("notifications").addEventListener("change", savePreferences);

  $("saveApiUrlBtn").addEventListener("click", () => {
    const value = $("apiBaseUrl").value.trim();
    if (!value) { showMessage("Enter a backend API URL.", true); return; }
    api.setBaseUrl(value);
    showMessage("API URL saved. Sign in again to connect to that backend.");
  });

  $("syncBtn").addEventListener("click", async () => {
    try {
      $("syncBtn").disabled = true;
      $("syncStatus").textContent = "Syncing local data...";
      const status = await migration.run();
      $("syncStatus").textContent = status.message || "Sync complete.";
      await loadAccount();
    } catch (error) {
      $("syncStatus").textContent = `Sync failed: ${error.message}`;
    } finally {
      $("syncBtn").disabled = false;
    }
  });

  $("exportBtn").addEventListener("click", () => {
    const data = Object.fromEntries(Object.keys(localStorage).map(key => [key, localStorage.getItem(key)]));
    const url = URL.createObjectURL(new Blob([JSON.stringify(data, null, 2)], { type: "application/json" }));
    const link = Object.assign(document.createElement("a"), { href: url, download: "mindmirror-browser-backup.json" });
    link.click();
    URL.revokeObjectURL(url);
  });

  $("importBtn").addEventListener("click", () => {
    const input = Object.assign(document.createElement("input"), { type: "file", accept: "application/json" });
    input.addEventListener("change", () => {
      const file = input.files[0];
      if (!file) return;
      const reader = new FileReader();
      reader.onload = async () => {
        try {
          const data = JSON.parse(reader.result);
          Object.entries(data).forEach(([key, value]) => localStorage.setItem(key, value));
          showMessage("Browser backup imported. Use Sync local data to upload it.");
        } catch { showMessage("That file is not a valid MindMirror browser backup.", true); }
      };
      reader.readAsText(file);
    });
    input.click();
  });

  $("logoutBtn").addEventListener("click", async () => { await api.logout(); renderSignedOut("Signed out. Your local browser data remains available."); });
  $("deleteAccountBtn").addEventListener("click", async () => {
    if (!window.confirm("Delete your account? This cannot be undone.")) return;
    try {
      await api.delete("/api/users/me");
      api.clearSession();
      renderSignedOut("Your account was deleted.");
    } catch (error) { showMessage(`Could not delete account: ${error.message}`, true); }
  });

  window.addEventListener("mindmirror:migration-status", event => { $("syncStatus").textContent = event.detail.message || "Sync status updated."; });
  loadAccount();
})();
