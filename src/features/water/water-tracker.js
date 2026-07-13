 const DEFAULT_GLASSES = 8;
    const MIN_GLASSES = 1;
    const MAX_GLASSES = 20;
    const GOAL_KEY = "waterGoal";
    let waterIntake = 0;
    let targetGlasses = DEFAULT_GLASSES;
    let today = new Date().toLocaleDateString();

    function setupGoalSelector() {
      const goalSelect = document.getElementById("goalSelect");

      for (let goal = MIN_GLASSES; goal <= MAX_GLASSES; goal++) {
        const option = document.createElement("option");
        option.value = String(goal);
        option.textContent = `${goal} glasses`;
        goalSelect.appendChild(option);
      }

      goalSelect.addEventListener("change", () => setTarget(Number(goalSelect.value)));

      document.querySelectorAll("[data-goal]").forEach(button => {
        button.addEventListener("click", () => setTarget(Number(button.dataset.goal)));
      });
    }

    function loadToday() {
      const history = MindMirrorStorage.getJson("history", {});
      targetGlasses = sanitizeGoal(MindMirrorStorage.getJson(GOAL_KEY, DEFAULT_GLASSES));
      waterIntake = history[today] ? history[today] : 0;
      updateUI();
    }

    function sanitizeGoal(goal) {
      if (!Number.isFinite(goal)) {
        return DEFAULT_GLASSES;
      }

      return Math.min(MAX_GLASSES, Math.max(MIN_GLASSES, Math.round(goal)));
    }

    function setTarget(goal) {
      targetGlasses = sanitizeGoal(goal);
      MindMirrorStorage.setJson(GOAL_KEY, targetGlasses);
      updateUI();
    }

    function updateUI() {
      const percent = Math.min((waterIntake / targetGlasses) * 100, 100);
      const progressPercent = `${percent}%`;

      document.getElementById("progressText").textContent = `${Math.round(percent)}%`;
      document.getElementById("intakeText").textContent = `${waterIntake} / ${targetGlasses} Glasses`;
      document.getElementById("todayStat").textContent = waterIntake;
      document.getElementById("targetStat").textContent = targetGlasses;
      document.getElementById("goalSelect").value = String(targetGlasses);
      document.getElementById("progressCircle").style.setProperty("--percent", progressPercent);
    }

    function addWater() {
      if (waterIntake < targetGlasses) {
        waterIntake++;
        saveProgress();
        updateUI();
      }
    }

    function removeWater() {
      if (waterIntake > 0) {
        waterIntake--;
        saveProgress();
        updateUI();
      }
    }

    function saveProgress() {
      const history = MindMirrorStorage.getJson("history", {});
      history[today] = waterIntake;
      MindMirrorStorage.setJson("history", history);
    }

    function renderHistory() {
      const historyList = document.getElementById("historyList");
      historyList.innerHTML = "";

      const history = MindMirrorStorage.getJson("history", {});
      const dates = Object.keys(history).sort((a, b) => new Date(b) - new Date(a));

      if (dates.length === 0) {
        const item = document.createElement("li");
        item.textContent = "No data found.";
        historyList.appendChild(item);
      } else {
        dates.forEach(date => {
          const li = document.createElement("li");
          const progressText = document.createElement("span");
          const dateText = document.createElement("b");
          const deleteButton = document.createElement("button");

          dateText.textContent = date;
          progressText.appendChild(dateText);
          progressText.append(`: ${history[date]} glasses`);

          deleteButton.className = "delete-btn";
          deleteButton.textContent = "Delete";
          deleteButton.addEventListener("click", () => deleteDate(date));

          li.appendChild(progressText);
          li.appendChild(deleteButton);
          historyList.appendChild(li);
        });
      }
    }

    function viewProgress() {
      const historyBox = document.getElementById("historyBox");
      const isVisible = getComputedStyle(historyBox).display !== "none";

      if (isVisible) {
        historyBox.style.display = "none";
        return;
      }

      renderHistory();
      historyBox.style.display = "block";
    }

    function deleteDate(date) {
      const history = MindMirrorStorage.getJson("history", {});
      delete history[date];
      MindMirrorStorage.setJson("history", history);

      if (date === today) {
        waterIntake = 0;
        updateUI();
      }

      renderHistory();
    }

    function deleteAll() {
      if (confirm("Delete all water intake history?")) {
        MindMirrorStorage.remove("history");
        waterIntake = 0;
        updateUI();
        document.getElementById("historyList").innerHTML = "";
        alert("All progress deleted!");
      }
    }
   window.addEventListener("load", () => {
      setupGoalSelector();
      loadToday();
    });
