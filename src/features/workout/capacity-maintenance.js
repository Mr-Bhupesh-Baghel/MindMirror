(() => {
  const totalDays = 365;
  let completedDays = MindMirrorStorage.getJson("completedDays", 0);
  let maintenanceRecords = MindMirrorStorage.getJson("maintenanceRecords", []);

  function updateProgress() {
    const progress = document.getElementById("challengeProgress");
    const info = document.getElementById("challengeInfo");
    const percent = Math.round((completedDays / totalDays) * 100);
    progress.style.width = `${percent}%`;
    progress.textContent = completedDays ? `${percent}%` : "";
    info.textContent = `Progress: ${completedDays} / ${totalDays} days`;
    MindMirrorStorage.setJson("completedDays", completedDays);
  }

  function renderRecords() {
    const list = document.getElementById("recordList");
    list.replaceChildren();

    if (!maintenanceRecords.length) {
      const emptyMessage = document.createElement("div");
      emptyMessage.className = "record-item helper-text";
      emptyMessage.textContent = "No capacity records yet.";
      list.appendChild(emptyMessage);
      return;
    }

    maintenanceRecords.forEach(record => {
      const item = document.createElement("div");
      const date = document.createElement("strong");
      date.textContent = record.date;
      item.className = "record-item";
      item.append(date, document.createElement("br"), `${record.pushups} push-ups`);
      list.appendChild(item);
    });
  }

  function completeDay() {
    if (completedDays >= totalDays) {
      alert("Congratulations! You’ve completed your 365-day challenge!");
      return;
    }

    completedDays++;
    updateProgress();
  }

  function addRecord() {
    const input = document.getElementById("pushupCount");
    const count = Number.parseInt(input.value, 10);
    if (!Number.isInteger(count) || count <= 0) {
      alert("Enter a valid number of push-ups.");
      return;
    }

    maintenanceRecords.push({ date: new Date().toLocaleDateString(), pushups: count });
    MindMirrorStorage.setJson("maintenanceRecords", maintenanceRecords);
    input.value = "";
    renderRecords();
  }

  function deleteAllData() {
    if (!confirm("Are you sure you want to delete all data?")) return;
    MindMirrorStorage.remove("completedDays");
    MindMirrorStorage.remove("maintenanceRecords");
    completedDays = 0;
    maintenanceRecords = [];
    updateProgress();
    renderRecords();
    alert("All data cleared.");
  }

  document.querySelector('[data-action="complete-day"]').addEventListener("click", completeDay);
  document.querySelector('[data-action="add-record"]').addEventListener("click", addRecord);
  document.querySelector('[data-action="delete-data"]').addEventListener("click", deleteAllData);
  updateProgress();
  renderRecords();
})();