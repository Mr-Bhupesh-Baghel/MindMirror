(() => {
  const storage = window.MindMirrorStorage;
  const api = window.MindMirrorApi;
  const accountStatus = document.getElementById("accountStatus");
  const authForms = document.getElementById("authForms");
  const profileForm = document.getElementById("profileForm");
  const guestMessage = document.getElementById("guestMessage");
  const migrationPanel = document.getElementById("guestMigration");
  const offlinePanel = document.getElementById("offlinePanel");
  const loginForm = document.getElementById("loginForm");
  const registerForm = document.getElementById("registerForm");

  function showStatus(message, type = "") {
    accountStatus.textContent = message;
    accountStatus.className = `notice ${type}`.trim();
    accountStatus.hidden = false;
  }

  function clearStatus() {
    accountStatus.hidden = true;
  }

  function setBusy(button, busy, busyText) {
    if (!button) return;
    if (busy) {
      button.dataset.originalText = button.textContent;
      button.disabled = true;
      button.textContent = busyText;
    } else {
      button.disabled = false;
      button.textContent = button.dataset.originalText || button.textContent;
    }
  }

  function showTab(tab) {
    const loginSelected = tab === "login";
    loginForm.hidden = !loginSelected;
    registerForm.hidden = loginSelected;
    document.getElementById("loginTab").classList.toggle("active", loginSelected);
    document.getElementById("registerTab").classList.toggle("active", !loginSelected);
    document.getElementById("loginTab").setAttribute("aria-selected", String(loginSelected));
    document.getElementById("registerTab").setAttribute("aria-selected", String(!loginSelected));
  }

  function updateProfileCard(user) {
    const displayName = user?.displayName || "Guest user";
    document.getElementById("avatarLetter").textContent = displayName.charAt(0).toUpperCase() || "G";
    document.getElementById("profile-title").textContent = displayName;
    document.getElementById("profileEmail").textContent = user?.email || "Your progress is stored on this device.";
    document.getElementById("profileName").value = user?.displayName || "";
  }

  function hasGuestData() {
    return Object.keys(storage.guestSnapshot()).length > 0;
  }

  function renderGuest() {
    updateProfileCard(null);
    document.getElementById("loginStatus").textContent = "Guest mode";
    document.getElementById("loginStatus").className = "status offline";
    document.getElementById("logoutButton").hidden = true;
    authForms.hidden = false;
    profileForm.hidden = true;
    guestMessage.hidden = false;
    migrationPanel.hidden = true;
    offlinePanel.hidden = true;
    document.getElementById("account-title").textContent = "Account access";
    document.getElementById("accountIntro").textContent = "Create an account or sign in to sync your progress with MySQL-backed account storage.";
  }

  async function renderAccount(userFromSession) {
    authForms.hidden = true;
    guestMessage.hidden = true;
    profileForm.hidden = false;
    document.getElementById("logoutButton").hidden = false;
    document.getElementById("loginStatus").textContent = "Account mode";
    document.getElementById("loginStatus").className = "status online";
    document.getElementById("account-title").textContent = "Your account";
    document.getElementById("accountIntro").textContent = "Your account data is loaded and saved through the API Gateway.";

    const guestDataExists = hasGuestData();
    migrationPanel.hidden = !guestDataExists;
    if (guestDataExists) {
      document.getElementById("migrationSummary").textContent = "Progress was found in this browser. Migrate it into this account, keep it locally, or decide later.";
    }

    updateProfileCard(userFromSession);
    if (!storage.isAccountAvailable()) {
      offlinePanel.hidden = false;
      document.getElementById("loginStatus").textContent = "Account offline";
      document.getElementById("loginStatus").className = "status offline";
      showStatus(storage.offlineMessage, "error");
      return;
    }

    try {
      const profile = await api.getProfile();
      updateProfileCard(profile);
    } catch (error) {
      offlinePanel.hidden = false;
      showStatus(storage.offlineMessage, "error");
    }
  }

  async function submitAuth(event, mode) {
    event.preventDefault();
    const form = event.currentTarget;
    if (!form.reportValidity()) return;

    const data = Object.fromEntries(new FormData(form).entries());
    if (mode === "register" && data.password !== data.confirmPassword) {
      showStatus("Passwords do not match.", "error");
      return;
    }

    const button = form.querySelector("button[type=submit]");
    setBusy(button, true, mode === "login" ? "Logging in..." : "Creating account...");
    clearStatus();
    try {
      await api[mode](mode === "login"
        ? { email: data.email, password: data.password }
        : { displayName: data.displayName, email: data.email, password: data.password });
      window.location.reload();
    } catch (error) {
      const message = error.message === storage.offlineMessage ? storage.offlineMessage : error.message;
      showStatus(message, "error");
      setBusy(button, false);
    }
  }

  async function saveProfile(event) {
    event.preventDefault();
    if (!profileForm.reportValidity()) return;
    const button = document.getElementById("profileSaveButton");
    setBusy(button, true, "Saving...");
    try {
      const data = Object.fromEntries(new FormData(profileForm).entries());
      const profile = await api.updateProfile({ displayName: data.displayName });
      const session = api.getSession();
      api.setSession({ ...session, user: { ...session.user, ...profile } });
      updateProfileCard(profile);
      showStatus("Profile saved on the server.", "success");
    } catch (error) {
      showStatus(error.message === storage.offlineMessage ? storage.offlineMessage : error.message, "error");
    } finally {
      setBusy(button, false);
    }
  }

  async function migrateGuestData() {
    const button = document.getElementById("migrateButton");
    setBusy(button, true, "Migrating...");
    try {
      const result = await storage.migrateGuestData();
      const conflicts = result.conflicts?.length ? ` Existing account values were kept for: ${result.conflicts.join(", ")}.` : "";
      const resultText = document.getElementById("migrationResult");
      resultText.textContent = `Guest data was saved to your account.${conflicts} The local copy is still here until you delete it.`;
      resultText.hidden = false;
      document.getElementById("deleteGuestButton").hidden = false;
      showStatus("Guest data migration completed on the server.", "success");
    } catch (error) {
      showStatus(error.message === storage.offlineMessage ? storage.offlineMessage : error.message, "error");
    } finally {
      setBusy(button, false);
    }
  }

  function keepGuestCopy() {
    migrationPanel.hidden = true;
    showStatus("The local guest copy was kept. Your account data remains separate.", "success");
  }

  function deleteGuestCopy() {
    if (!confirm("The account copy was saved successfully. Delete the local guest copy from this browser?")) return;
    storage.clearGuestData();
    migrationPanel.hidden = true;
    showStatus("The local guest copy was deleted after migration.", "success");
  }

  function logOut() {
    if (!confirm("Log out of this MindMirror account?")) return;
    api.clearSession();
    window.location.reload();
  }

  document.getElementById("loginTab").addEventListener("click", () => showTab("login"));
  document.getElementById("registerTab").addEventListener("click", () => showTab("register"));
  loginForm.addEventListener("submit", event => submitAuth(event, "login"));
  registerForm.addEventListener("submit", event => submitAuth(event, "register"));
  profileForm.addEventListener("submit", saveProfile);
  document.getElementById("logoutButton").addEventListener("click", logOut);
  document.getElementById("migrateButton").addEventListener("click", migrateGuestData);
  document.getElementById("keepGuestButton").addEventListener("click", keepGuestCopy);
  document.getElementById("deleteGuestButton").addEventListener("click", deleteGuestCopy);
  document.getElementById("retryAccountButton").addEventListener("click", () => window.location.reload());
  document.getElementById("continueGuestButton").addEventListener("click", () => {
    api.clearSession();
    window.location.reload();
  });

  storage.ready().then(async state => {
    const session = api.getSession();
    if (!session?.token) {
      renderGuest();
      return;
    }
    await renderAccount(session.user);
    if (!state.available) offlinePanel.hidden = false;
  });
})();
