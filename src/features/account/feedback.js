MindMirrorStorage.ready().then(() => {
  const form = document.getElementById("feedbackForm");
  const status = document.getElementById("feedbackStatus");
  const submitButton = document.getElementById("submitFeedback");
  const message = document.getElementById("message");
  const messageCount = document.getElementById("messageCount");

  function showStatus(text, type = "") {
    status.textContent = text;
    status.className = `notice ${type}`.trim();
    status.hidden = false;
  }

  function updateMessageCount() {
    messageCount.textContent = `${message.value.length} / 5000`;
  }

  function prefillSignedInUser() {
    const user = window.MindMirrorApi?.getSession?.()?.user;
    if (!user) return;
    document.getElementById("name").value = user.displayName || "";
    document.getElementById("email").value = user.email || "";
  }

  async function saveFeedback(payload) {
    const queued = window.MindMirrorStorage.getJson("feedbackList", []);
    queued.push({ ...payload, date: payload.feedbackDate });
    const result = await window.MindMirrorStorage.setJson("feedbackList", queued);
    if (!result.ok) throw result.error || new Error(window.MindMirrorStorage.offlineMessage);
  }

  function localDate() {
    const now = new Date();
    return new Date(now.getTime() - now.getTimezoneOffset() * 60000).toISOString().slice(0, 10);
  }

  async function submitFeedback(event) {
    event.preventDefault();
    if (!form.reportValidity()) return;

    const data = Object.fromEntries(new FormData(form).entries());
    const payload = {
      name: data.name.trim(),
      email: data.email.trim(),
      rating: Number(data.rating),
      message: data.message.trim(),
      feedbackDate: localDate()
    };

    submitButton.disabled = true;
    submitButton.textContent = "Saving...";
    try {
      await saveFeedback(payload);
      showStatus(window.MindMirrorStorage.isAccountMode()
        ? "Thanks — your feedback has been saved to your account."
        : "Thanks — your feedback has been saved on this device.", "success");
      form.reset();
      prefillSignedInUser();
      updateMessageCount();
    } catch (error) {
      const detail = error.message ? ` ${error.message}` : "";
      showStatus(`Your feedback was not saved.${detail}`, "error");
    } finally {
      submitButton.disabled = false;
      submitButton.textContent = "Save feedback";
    }
  }

  message.addEventListener("input", updateMessageCount);
  form.addEventListener("submit", submitFeedback);
  prefillSignedInUser();
});
