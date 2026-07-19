const DEFAULT_TASK = "Select the one task, breathe, focus, and keep doing it.";
const localDate = (date = new Date()) => {
  const offsetDate = new Date(date.getTime() - date.getTimezoneOffset() * 60_000);
  return offsetDate.toISOString().slice(0, 10);
};
let today = localDate();
let storageKey = `daily-tasks-${today}`;
let allTasks = [];
let cloudTasks = [];
let affirmations = MindMirrorStorage.getJson("affirmations", []);
const cloudEnabled = () => navigator.onLine && Boolean(window.MindMirrorApi?.getAccessToken());

function setProgress(done, total) {
  const percent = total ? Math.round(done / total * 100) : 0;
  const bar = document.getElementById("progress");
  bar.style.width = `${percent}%`; bar.textContent = `${percent}%`;
  document.getElementById("progressValue").textContent = `${percent}%`;
  bar.parentElement.setAttribute("aria-valuenow", percent);
}

function localTasks() {
  return [
    { id: "daily-0", title: DEFAULT_TASK, category: "daily" },
    ...MindMirrorStorage.getJson("customTasks", []).map((title, index) => ({ id: `custom-${index}`, title, category: "custom" })),
    ...MindMirrorStorage.getJson("holidayTasks", []).map((title, index) => ({ id: `holiday-${index}`, title, category: "holiday" }))
  ];
}

function renderTasks(tasks, completions = {}) {
  const container = document.getElementById("taskContainer");
  const holiday = document.getElementById("HtaskContainer");
  container.innerHTML = ""; holiday.innerHTML = ""; allTasks = tasks;
  const groups = { daily: ["Today's Tasks", container], custom: ["Custom Tasks", container], holiday: ["Holiday Tasks", holiday] };
  Object.entries(groups).forEach(([category, [headingText, target]]) => {
    const groupTasks = tasks.filter(task => task.category === category);
    if (!groupTasks.length) return;
    const box = document.createElement("div"); box.className = "task-group";
    const heading = document.createElement("h2"); heading.textContent = headingText; box.appendChild(heading);
    groupTasks.forEach(task => {
      const label = document.createElement("label"); label.className = "task-item";
      const span = document.createElement("span"); span.textContent = task.title;
      const checkbox = document.createElement("input"); checkbox.type = "checkbox"; checkbox.dataset.taskId = task.id; checkbox.checked = Boolean(completions[task.id]);
      checkbox.addEventListener("change", saveStatus);
      label.append(span, checkbox);
      if (category !== "daily") { const remove = document.createElement("button"); remove.textContent = "Delete"; remove.onclick = () => removeTask(task); label.append(remove); }
      box.appendChild(label);
    });
    target.appendChild(box);
  });
  updateProgress(false);
}

function updateProgress(save = true) {
  const boxes = [...document.querySelectorAll("#taskContainer input[type=checkbox], #HtaskContainer input[type=checkbox]")];
  setProgress(boxes.filter(box => box.checked).length, boxes.length);
  if (save) saveStatus();
}

async function loadTasks() {
  if (cloudEnabled()) {
    try {
      cloudTasks = await MindMirrorApi.get("/api/routine/tasks");
      if (!cloudTasks.length) { await MindMirrorApi.post("/api/routine/tasks", { title: DEFAULT_TASK, category: "daily", sortOrder: 1 }); cloudTasks = await MindMirrorApi.get("/api/routine/tasks"); }
      const completion = await MindMirrorApi.get(`/api/routine/completions?date=${today}`);
      renderTasks(cloudTasks, Object.fromEntries(completion.tasks.map(task => [task.taskId, task.completed])));
      return;
    } catch (error) { console.warn("Using offline routine data:", error); }
  }
  renderTasks(localTasks(), MindMirrorStorage.getJson(storageKey, {}));
}

async function saveStatus() {
  const boxes = [...document.querySelectorAll("#taskContainer input[type=checkbox], #HtaskContainer input[type=checkbox]")];
  const values = Object.fromEntries(boxes.map(box => [box.dataset.taskId, box.checked]));
  MindMirrorStorage.setJson(storageKey, values);
  setProgress(boxes.filter(box => box.checked).length, boxes.length);
  if (cloudEnabled() && cloudTasks.length) {
    try { await MindMirrorApi.put("/api/routine/completions", { completionDate: today, completions: cloudTasks.map(task => ({ taskId: task.id, completed: Boolean(values[task.id]) })) }); }
    catch (error) { console.warn("Routine progress saved locally:", error); }
  }
}

async function addTask(category, inputId) {
  const input = document.getElementById(inputId); const title = input.value.trim(); if (!title) return;
  if (cloudEnabled()) { try { await MindMirrorApi.post("/api/routine/tasks", { title, category, sortOrder: cloudTasks.length + 1 }); input.value = ""; return loadTasks(); } catch (error) { console.warn("Task saved locally:", error); } }
  const key = category === "holiday" ? "holidayTasks" : "customTasks"; const list = MindMirrorStorage.getJson(key, []); list.push(title); MindMirrorStorage.setJson(key, list); input.value = ""; loadTasks();
}
function addCustomTask() { return addTask("custom", "customTaskInput"); }
function addHolidayTask() { return addTask("holiday", "HolidayTaskInput"); }
async function removeTask(task) {
  if (cloudEnabled() && typeof task.id === "number") { try { await MindMirrorApi.delete(`/api/routine/tasks/${task.id}`); return loadTasks(); } catch (error) { console.warn("Task removal deferred to local fallback:", error); } }
  const key = task.category === "holiday" ? "holidayTasks" : "customTasks"; MindMirrorStorage.setJson(key, MindMirrorStorage.getJson(key, []).filter(title => title !== task.title)); loadTasks();
}
async function viewPrevious() {
  let entries = [];
  if (cloudEnabled()) try { entries = await MindMirrorApi.get("/api/routine/history"); } catch (error) { console.warn("Using offline routine history:", error); }
  if (!entries.length) entries = MindMirrorStorage.keysStartingWith("daily-tasks-").map(key => { const values = MindMirrorStorage.getJson(key, {}); return { completionDate: key.slice(12), completedTasks: Object.values(values).filter(Boolean).length, totalTasks: Object.keys(values).length }; });
  alert(entries.map(entry => `${entry.completionDate}: ${entry.completedTasks}/${entry.totalTasks}`).join("\n") || "No previous progress.");
}
function deleteSpecificData() { MindMirrorStorage.keysStartingWith("daily-tasks-").forEach(MindMirrorStorage.remove); alert("Local offline routine data deleted."); }
function submitAndNextDay() { saveStatus(); today = localDate(new Date(Date.now() + 86400000)); storageKey = `daily-tasks-${today}`; loadTasks(); }
async function exportToExcel() { if (cloudEnabled()) { try { const rows = await MindMirrorApi.get("/api/routine/history"); const data = [["Date", "Completed (%)"], ...rows.map(row => [row.completionDate, Math.round(row.completionRate * 100)])]; const sheet = XLSX.utils.aoa_to_sheet(data); const book = XLSX.utils.book_new(); XLSX.utils.book_append_sheet(book, sheet, "Performance"); XLSX.writeFile(book, "Performance.xlsx"); return; } catch (error) { console.warn("Exporting offline routine history:", error); } } const data = [["Date", "Completed (%)"]]; MindMirrorStorage.keysStartingWith("daily-tasks-").forEach(key => { const values = Object.values(MindMirrorStorage.getJson(key, {})); data.push([key.slice(12), values.length ? Math.round(values.filter(Boolean).length / values.length * 100) : 0]); }); const sheet = XLSX.utils.aoa_to_sheet(data); const book = XLSX.utils.book_new(); XLSX.utils.book_append_sheet(book, sheet, "Performance"); XLSX.writeFile(book, "Performance.xlsx"); }

async function loadAffirmations() { if (cloudEnabled()) try { affirmations = await MindMirrorApi.get("/api/affirmations"); } catch (error) { console.warn("Using offline affirmations:", error); } renderAffirmations(); }
function renderAffirmations() { const list = document.getElementById("affirmationList"); list.innerHTML = ""; affirmations.forEach((affirmation, index) => { const item = typeof affirmation === "string" ? { text: affirmation } : affirmation; const li = document.createElement("li"); li.textContent = item.text; const button = document.createElement("button"); button.textContent = "X"; button.onclick = () => deleteAffirmation(index); li.append(button); list.append(li); }); }
async function deleteAffirmation(index) { const item = affirmations[index]; if (cloudEnabled() && item.id) try { await MindMirrorApi.delete(`/api/affirmations/${item.id}`); } catch (error) { console.warn("Affirmation removal kept local:", error); } affirmations.splice(index, 1); MindMirrorStorage.setJson("affirmations", affirmations.map(item => typeof item === "string" ? item : item.text)); renderAffirmations(); }
document.getElementById("addBtn").onclick = async () => { const input = document.getElementById("affirmationInput"); const text = input.value.trim(); if (!text) return; let item = text; if (cloudEnabled()) try { item = await MindMirrorApi.post("/api/affirmations", { text }); } catch (error) { console.warn("Affirmation saved locally:", error); } affirmations.push(item); MindMirrorStorage.setJson("affirmations", affirmations.map(value => typeof value === "string" ? value : value.text)); input.value = ""; renderAffirmations(); };
document.getElementById("clearAllBtn").onclick = () => Promise.all(affirmations.filter(item => item.id && cloudEnabled()).map(item => MindMirrorApi.delete(`/api/affirmations/${item.id}`))).catch(() => {}).finally(() => { affirmations = []; MindMirrorStorage.remove("affirmations"); renderAffirmations(); });
loadTasks(); loadAffirmations();
