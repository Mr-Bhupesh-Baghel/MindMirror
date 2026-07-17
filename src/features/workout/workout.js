(() => {
  const page = document.body.dataset.page;
  const api = () => window.MindMirrorApi;
  const today = () => new Date().toISOString().slice(0, 10);
  const $ = selector => document.querySelector(selector);
  const escape = value => String(value).replace(/[&<>"]/g, char => ({"&":"&amp;","<":"&lt;",">":"&gt;",'"':"&quot;"}[char]));
  const loggedIn = () => Boolean(api()?.getAccessToken());
  function message(text = "", error = false) { const el = $("#notice"); if (el) { el.textContent = text; el.classList.toggle("error", error); } }
  function requireLogin() { if (!loggedIn()) { message("Sign in from the Account page to save and view your workout history.", true); return false; } return true; }
  async function call(action) { try { return await action(); } catch (error) { message(error.message || "Could not reach the server.", true); return null; } }

  async function challenge() {
    const form = $("#challenge-form"), history = $("#challenge-history");
    function renderStats(data = {}) { const completed = data.completedDays || 0; $("#challenge-progress").style.width = `${completed}%`; $("#challenge-count").textContent = `${completed} / 100 days`; $("#challenge-total").textContent = data.totalCompletedPushups || 0; $("#challenge-streak").textContent = data.currentStreak || 0; $("#challenge-rate").textContent = `${Math.round(data.completionRate || 0)}%`; }
    async function load() { if (!requireLogin()) return; const [summary, entries] = await Promise.all([call(() => api().get("/api/pushups/challenge")), call(() => api().get("/api/pushups/challenge/history"))]); if (!summary || !entries) return; renderStats(summary); history.innerHTML = entries.length ? entries.map(entry => `<li><span><b>Day ${entry.challengeDay}</b> — ${entry.completedCount}/${entry.targetCount} push-ups<br><small>${entry.entryDate} · ${entry.status === "DONE" ? "Completed" : "In progress"}</small></span></li>`).join("") : '<li class="empty">No challenge days logged yet.</li>'; }
    form.addEventListener("submit", async event => { event.preventDefault(); if (!requireLogin()) return; const data = Object.fromEntries(new FormData(form)); const result = await call(() => api().put("/api/pushups/challenge", { entryDate:data.entryDate, challengeDay:Number(data.challengeDay), targetCount:Number(data.targetCount), completedCount:Number(data.completedCount) })); if (result) { message("Challenge day saved."); form.reset(); $("#challenge-date").value = today(); await load(); } });
    $("#challenge-date").value = today(); load();
  }

  async function maintenance() {
    const form = $("#maintenance-form"), history = $("#maintenance-history");
    function renderStats(data = {}) { $("#maintenance-total").textContent = data.totalPushups || 0; $("#maintenance-days").textContent = data.totalDays || 0; $("#maintenance-streak").textContent = data.currentStreak || 0; $("#maintenance-average").textContent = data.averagePerDay || 0; }
    async function load() { if (!requireLogin()) return; const [summary, entries] = await Promise.all([call(() => api().get("/api/pushups/maintenance")), call(() => api().get("/api/pushups/maintenance/history"))]); if (!summary || !entries) return; renderStats(summary); history.innerHTML = entries.length ? entries.map(entry => `<li><span><b>${entry.pushupsCount} push-ups</b><br><small>${entry.entryDate}${entry.challengeDay ? ` · Maintain day ${entry.challengeDay}` : ""}</small></span></li>`).join("") : '<li class="empty">No maintenance sessions yet.</li>'; }
    form.addEventListener("submit", async event => { event.preventDefault(); if (!requireLogin()) return; const data = Object.fromEntries(new FormData(form)); const result = await call(() => api().put("/api/pushups/maintenance", { entryDate:data.entryDate, pushupsCount:Number(data.pushupsCount), challengeDay:data.challengeDay ? Number(data.challengeDay) : null })); if (result) { message("Maintenance record saved."); form.reset(); $("#maintenance-date").value = today(); await load(); } });
    $("#maintenance-date").value = today(); load();
  }

  async function prison() {
    const form = $("#generator-form"), output = $("#workout-output"), history = $("#workout-history"); let generated = null;
    function generateSets(start) { const reps = []; for (let high = start, low = 1; high >= low; high--, low++) { reps.push(high); if (low !== high) reps.push(low); } return reps; }
    async function load() { if (!requireLogin()) return; const entries = await call(() => api().get("/api/workouts")); if (!entries) return; history.innerHTML = entries.length ? entries.map(entry => `<li><span><b>${escape(entry.exerciseName)}</b> — ${entry.totalReps} reps<br><small>${entry.completedOn} · started at ${entry.startingNumber}</small></span><button class="danger" data-delete="${entry.id}">Delete</button></li>`).join("") : '<li class="empty">No saved workouts yet.</li>'; history.querySelectorAll("[data-delete]").forEach(button => button.addEventListener("click", async () => { if (!confirm("Delete this saved workout?")) return; try { await api().delete(`/api/workouts/${button.dataset.delete}`); message("Workout deleted."); load(); } catch (error) { message(error.message || "Could not delete workout.", true); } })); }
    form.addEventListener("submit", event => { event.preventDefault(); const data = Object.fromEntries(new FormData(form)); const start = Number(data.startingNumber); const reps = generateSets(start); generated = { exerciseName:data.exerciseName.trim(), startingNumber:start, totalReps:reps.reduce((a,b) => a+b,0) }; output.innerHTML = `<h2>${escape(generated.exerciseName)} workout</h2><p class="muted">${reps.length} sets · ${generated.totalReps} total reps</p><div class="sets">${reps.map((rep,index) => `<div class="set">Set ${index + 1}: ${rep}</div>`).join("")}</div><div class="actions"><button id="save-workout">Save workout</button></div>`; $("#save-workout").addEventListener("click", async () => { if (!requireLogin()) return; const result = await call(() => api().post("/api/workouts", { ...generated, completedOn:today() })); if (result) { message("Workout saved to your history."); load(); } }); });
    load();
  }
  if (page === "challenge") challenge(); if (page === "maintenance") maintenance(); if (page === "prison") prison();
})();
