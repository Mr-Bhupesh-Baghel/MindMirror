(function () {
  const $ = selector => document.querySelector(selector);

  function setText(selector, value) {
    $(selector).textContent = value;
  }

  function setGardenStage(streak = 0) {
    const garden = $("[data-garden-stage]");
    if (!garden) return;
    garden.dataset.gardenStage = streak >= 21 ? "forest" : streak >= 7 ? "tree" : streak >= 1 ? "sprout" : "seed";
  }

  function renderGuest() {
    setText("[data-streak]", "--");
    setText("[data-routine-total]", "--");
    setText("[data-water-total]", "--");
    setText("[data-pushup-total]", "--");
    setText("[data-sync-status]", "Progress stays on this device until you sign in.");
    setGardenStage();
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
      setText("[data-welcome-copy]", "Your garden is connected to your account. One small act of care today keeps the growth going.");
      setText("[data-secondary-action]", "View your garden ->");
      setText("[data-streak]", summary.currentStreak);
      setGardenStage(summary.currentStreak);
      setText("[data-routine-total]", summary.routineDays);
      setText("[data-water-total]", summary.waterGlasses);
      setText("[data-pushup-total]", summary.pushups);
      setText("[data-sync-status]", "Your garden is safely connected to MindMirror.");
    } catch (error) {
      renderGuest();
      setText("[data-sync-status]", "Unable to reach your account right now. Your local progress is still available.");
    }
  }

  window.addEventListener("mindmirror:login", renderSignedIn);
  window.addEventListener("mindmirror:logout", renderGuest);
  renderSignedIn();
})();
