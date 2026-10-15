MindMirrorStorage.ready().then(() => {
// 🔹 Default section define kar rahe hain jisme ek task diya gaya hai  
const sections = {};

let today = new Date().toISOString().split('T')[0];
let storageKey = `daily-tasks-${today}`;
let allTasks = [];

// ✅ Load tasks
function loadTasks() {
  const container = document.getElementById("taskContainer");
  container.innerHTML = '';
  allTasks = [];

  // Default tasks
  for (let [section, tasks] of Object.entries(sections)) {
    const box = document.createElement("div");
    box.className = "task-group";
    const heading = document.createElement("h2");
    heading.textContent = section;
    box.appendChild(heading);

    tasks.forEach((task, index) => {
      const id = `${section}-${index}`;
      allTasks.push(id);

      const label = document.createElement("label");
      label.className = "task-item";

      const span = document.createElement("span");
      span.textContent = task;

      const checkbox = document.createElement("input");
      checkbox.type = "checkbox";
      checkbox.id = id;
      checkbox.onchange = updateProgress;

      label.appendChild(span);
      label.appendChild(checkbox);
      box.appendChild(label);
    });

    container.appendChild(box);
  }

  // Custom tasks
  const custom = MindMirrorStorage.getJson("customTasks", []);

  if (custom.length) {
    const customBox = document.createElement("div");
    customBox.className = "task-group";
    const heading = document.createElement("h2");
    heading.textContent = "📝 Custom Tasks";
    customBox.appendChild(heading);

    custom.forEach((task, index) => {
      const id = `custom-${index}`;
      allTasks.push(id);

      const label = document.createElement("label");
      label.className = "task-item";

      const span = document.createElement("span");
      span.textContent = task;

      const checkbox = document.createElement("input");
      checkbox.type = "checkbox";
      checkbox.id = id;
      checkbox.onchange = updateProgress;

      const deleteBtn = document.createElement("button");
      deleteBtn.textContent = "❌";
      deleteBtn.onclick = () => removeCustomTask(index);

      const upBtn = document.createElement("button");
      upBtn.textContent = "🔼";
      upBtn.onclick = () => moveCustomTask(index, -1);
      upBtn.disabled = index === 0;

      const downBtn = document.createElement("button");
      downBtn.textContent = "🔽";
      downBtn.onclick = () => moveCustomTask(index, 1);
      downBtn.disabled = index === custom.length - 1;

      label.appendChild(span);
      label.appendChild(checkbox);
      label.appendChild(deleteBtn);
      label.appendChild(upBtn);
      label.appendChild(downBtn);

      customBox.appendChild(label);
    });

    container.appendChild(customBox);
  }
}

// ✅ Update progress
function updateProgress() {
  let done = 0;

  allTasks.forEach(id => {
    const box = document.getElementById(id);
    if (box?.checked) done++;
  });

  const percent = allTasks.length === 0 ? 0 : Math.round((done / allTasks.length) * 100);

  const bar = document.getElementById("progress");
  bar.style.width = percent + "%";
  bar.textContent = percent + "%";

  saveStatus();
}

// ✅ Save status
function saveStatus() {
  const status = {};

  allTasks.forEach(id => {
    const box = document.getElementById(id);
    status[id] = box?.checked || false;
  });

  MindMirrorStorage.setJson(storageKey, status);
}

// ✅ Load status
function loadStatus() {
  const data = MindMirrorStorage.getJson(storageKey, {});

  for (let id in data) {
    const box = document.getElementById(id);
    if (box) box.checked = data[id];
  }

  updateProgress();
}

// ✅ Add custom task
function addCustomTask() {
  const input = document.getElementById("customTaskInput");
  const task = input.value.trim();

  if (!task) return;

  const list = MindMirrorStorage.getJson("customTasks", []);

  if (list.includes(task)) {
    alert("⚠️ This task already exists.");
    return;
  }

  list.push(task);
  MindMirrorStorage.setJson("customTasks", list);

  input.value = '';

  loadTasks();
  loadStatus();
}

// ✅ Remove custom task
function removeCustomTask(index) {
  const list = MindMirrorStorage.getJson("customTasks", []);
  list.splice(index, 1);
  MindMirrorStorage.setJson("customTasks", list);

  loadTasks();
  loadStatus();
}

// ✅ Move custom task
function moveCustomTask(index, direction) {
  const list = MindMirrorStorage.getJson("customTasks", []);
  const newIndex = index + direction;

  if (newIndex < 0 || newIndex >= list.length) return;

  [list[index], list[newIndex]] = [list[newIndex], list[index]];
  MindMirrorStorage.setJson("customTasks", list);

  loadTasks();
  loadStatus();
}
// ✅ All progress and daily task data have been deleted function
async function deleteSpecificData() {
  const keys = MindMirrorStorage.keysStartingWith("daily-tasks-");
  const results = await Promise.all([...keys, "progress"].map(key => MindMirrorStorage.remove(key)));
  if (results.every(result => result.ok)) {
    alert("✅ All progress and daily task data have been deleted.");
  } else {
    MindMirrorStorage.showNotice(MindMirrorStorage.offlineMessage, true);
  }
}

// ✅ Auto reset
function autoReset() {
  const currentDate = new Date().toISOString().split('T')[0];
  const lastDate = MindMirrorStorage.getJson("lastOpenedDate", null);

  if (lastDate !== currentDate) {
    MindMirrorStorage.setJson("lastOpenedDate", currentDate);
    storageKey = `daily-tasks-${currentDate}`;
    loadTasks();
    loadStatus();
  }
}

// ✅ Submit and go to next day
function submitAndNextDay() {
  saveStatus();

  const todayDate = new Date();
  const tomorrowDate = new Date(todayDate);
  tomorrowDate.setDate(todayDate.getDate() + 1);

  const tomorrow = tomorrowDate.toISOString().split('T')[0];
  MindMirrorStorage.setJson("lastOpenedDate", tomorrow);
  storageKey = `daily-tasks-${tomorrow}`;

  loadTasks();
  loadStatus();
}

// ✅ Init
document.getElementById("addCustomTaskBtn").addEventListener("click", addCustomTask);
document.getElementById("completeDayBtn").addEventListener("click", submitAndNextDay);
document.getElementById("deleteProgressBtn").addEventListener("click", deleteSpecificData);
autoReset();
loadTasks();
loadStatus();
setInterval(autoReset, 60 * 1000);
});
