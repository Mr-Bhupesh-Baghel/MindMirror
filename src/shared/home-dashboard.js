(function () {
  const $ = selector => document.querySelector(selector);

  function setText(selector, value) {
    $(selector).textContent = value;
  }

  function renderGuest() {
    setText("[data-streak]", "--");
    setText("[data-routine-total]", "--");
    setText("[data-water-total]", "--");
    setText("[data-pushup-total]", "--");
    setText("[data-sync-status]", "Progress stays on this device until you sign in.");
  }

  async function renderSignedIn() {
    const api = window.MindMirrorApi;
    if (!api.getAccessToken()) {
      renderGuest();
      return;
    }

    try {
      const [user, summary] = await Promise.all([
        api.get("/api/users/me"),
        api.get("/api/users/me/summary")
      ]);
      setText("[data-account-link]", user.displayName || "My account");
      setText("[data-welcome-title]", `Welcome back, ${user.displayName || "there"}.`);
      setText("[data-welcome-copy]", "Your progress is safely connected to your account. Pick one small action to keep your streak moving.");
      setText("[data-secondary-action]", "Manage your account ->");
      setText("[data-streak]", summary.currentStreak);
      setText("[data-routine-total]", summary.routineDays);
      setText("[data-water-total]", summary.waterGlasses);
      setText("[data-pushup-total]", summary.pushups);
      setText("[data-sync-status]", "Connected to your MindMirror account.");
    } catch (error) {
      renderGuest();
      setText("[data-sync-status]", "Unable to reach your account right now. Your local progress is still available.");
    }
  }

  window.addEventListener("mindmirror:login", renderSignedIn);
  window.addEventListener("mindmirror:logout", renderGuest);
  renderSignedIn();
})();
