const DEFAULT_GLASSES = 8;
const MIN_GLASSES = 1;
const MAX_GLASSES = 20;
const GOAL_KEY = "waterGoal";
let waterIntake = 0;
let targetGlasses = DEFAULT_GLASSES;
const localDate = (date = new Date()) => {
  const offsetDate = new Date(date.getTime() - date.getTimezoneOffset() * 60_000);
  return offsetDate.toISOString().slice(0, 10);
};
const today = localDate();

const cloudEnabled = () => navigator.onLine && Boolean(window.MindMirrorApi?.getAccessToken());
const localHistory = () => MindMirrorStorage.getJson("history", {});
const sanitizeGoal = goal => Number.isFinite(goal) ? Math.min(MAX_GLASSES, Math.max(MIN_GLASSES, Math.round(goal))) : DEFAULT_GLASSES;

function setupGoalSelector() {
  const goalSelect = document.getElementById("goalSelect");
  for (let goal = MIN_GLASSES; goal <= MAX_GLASSES; goal++) {
    const option = document.createElement("option");
    option.value = String(goal);
    option.textContent = `${goal} glasses`;
    goalSelect.appendChild(option);
  }
  goalSelect.addEventListener("change", () => setTarget(Number(goalSelect.value)));
  document.querySelectorAll("[data-goal]").forEach(button => button.addEventListener("click", () => setTarget(Number(button.dataset.goal))));
}

async function loadToday() {
  if (cloudEnabled()) {
    try {
      const entry = await MindMirrorApi.get(`/api/water?date=${today}`);
      waterIntake = entry.glasses || 0;
      targetGlasses = sanitizeGoal(entry.goalGlasses || DEFAULT_GLASSES);
      updateUI();
      return;
    } catch (error) { console.warn("Using offline water data:", error); }
  }
  const history = localHistory();
  targetGlasses = sanitizeGoal(MindMirrorStorage.getJson(GOAL_KEY, DEFAULT_GLASSES));
  waterIntake = history[today] || 0;
  updateUI();
}

function updateUI() {
  const percent = Math.min((waterIntake / targetGlasses) * 100, 100);
  document.getElementById("progressText").textContent = `${Math.round(percent)}%`;
  document.getElementById("intakeText").textContent = `${waterIntake} / ${targetGlasses} Glasses`;
  document.getElementById("goalSelect").value = String(targetGlasses);
  document.getElementById("progressCircle").style.setProperty("--percent", `${percent}%`);
  document.getElementById("progressCircle").setAttribute("aria-label", `${Math.round(percent)}% of today's water goal complete`);
  document.querySelectorAll("[data-goal]").forEach(button => button.classList.toggle("is-selected", Number(button.dataset.goal) === targetGlasses));
}

async function saveProgress() {
  const history = localHistory();
  history[today] = waterIntake;
  MindMirrorStorage.setJson("history", history);
  MindMirrorStorage.setJson(GOAL_KEY, targetGlasses);
  if (cloudEnabled()) {
    try {
      await MindMirrorApi.put("/api/water", { entryDate: today, glasses: waterIntake, goalGlasses: targetGlasses });
    } catch (error) { console.warn("Water entry saved locally for offline fallback:", error); }
  }
}

async function setTarget(goal) { targetGlasses = sanitizeGoal(goal); await saveProgress(); updateUI(); }
async function addWater() { if (waterIntake < targetGlasses) { waterIntake++; await saveProgress(); updateUI(); } }
async function removeWater() { if (waterIntake > 0) { waterIntake--; await saveProgress(); updateUI(); } }

async function historyEntries() {
  if (cloudEnabled()) {
    try { return await MindMirrorApi.get("/api/water/history"); } catch (error) { console.warn("Using offline water history:", error); }
  }
  return Object.entries(localHistory()).map(([entryDate, glasses]) => ({ entryDate, glasses }));
}

async function renderHistory() {
  const historyList = document.getElementById("historyList");
  historyList.innerHTML = "";
  const entries = await historyEntries();
  entries.sort((a, b) => String(b.entryDate).localeCompare(String(a.entryDate)));
  if (!entries.length) { historyList.innerHTML = "<li>No data found.</li>"; return; }
  entries.forEach(entry => {
    const li = document.createElement("li");
    li.textContent = `${entry.entryDate}: ${entry.glasses} glasses`;
    historyList.appendChild(li);
  });
}

async function viewProgress() {
  const historyBox = document.getElementById("historyBox");
  if (getComputedStyle(historyBox).display !== "none") { historyBox.style.display = "none"; return; }
  await renderHistory();
  historyBox.style.display = "block";
}

function deleteAll() {
  if (!confirm("Delete local offline water history?")) return;
  MindMirrorStorage.remove("history");
  waterIntake = 0;
  saveProgress();
  updateUI();
}

window.addEventListener("load", () => {
  document.getElementById("todayLabel").textContent = new Intl.DateTimeFormat(undefined, { weekday: "short", month: "short", day: "numeric" }).format(new Date());
  setupGoalSelector();
  loadToday();
});
