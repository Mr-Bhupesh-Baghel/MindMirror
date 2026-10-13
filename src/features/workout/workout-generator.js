(() => {
  const exerciseInput = document.getElementById("exercise");
  const numberInput = document.getElementById("number");
  const result = document.getElementById("result");
  const historyContainer = document.getElementById("history");
  const historyKey = "prisonWorkoutHistory";

  function generateWorkout() {
    const exercise = exerciseInput.value.trim() || "Exercise";
    const startNumber = Number.parseInt(numberInput.value, 10);
    result.replaceChildren();

    if (!Number.isInteger(startNumber) || startNumber < 2) {
      const error = document.createElement("div");
      error.className = "total";
      error.textContent = "Enter a number greater than 1.";
      result.appendChild(error);
      return;
    }

    const workout = [];
    let low = 1;
    let high = startNumber;
    let setNumber = 1;
    let totalReps = 0;

    while (high >= low) {
      workout.push({ set: setNumber++, reps: high });
      totalReps += high;
      if (high === low) break;

      workout.push({ set: setNumber++, reps: low });
      totalReps += low;
      high--;
      low++;
    }

    workout.forEach(item => {
      const set = document.createElement("div");
      const label = document.createElement("strong");
      label.textContent = `Set ${item.set}`;
      set.className = "set";
      set.append(label, document.createElement("br"), `${item.reps} ${exercise}`);
      result.appendChild(set);
    });

    const total = document.createElement("div");
    total.className = "total";
    total.textContent = `Total reps: ${totalReps}`;
    result.appendChild(total);

    MindMirrorStorage.setJson("prisonWorkout", {
      exercise,
      startNumber,
      totalReps,
      workout,
      date: new Date().toLocaleDateString()
    });
  }

  function saveWorkout() {
    const workouts = MindMirrorStorage.getJson(historyKey, []);
    const total = result.querySelector(".total");

    workouts.push({
      exercise: exerciseInput.value.trim() || "Exercise",
      startNumber: Number.parseInt(numberInput.value, 10),
      totalReps: total?.textContent || "",
      date: new Date().toLocaleString()
    });
    MindMirrorStorage.setJson(historyKey, workouts);
    alert("Workout saved!");
  }

  function renderHistory() {
    historyContainer.replaceChildren();
    const workouts = MindMirrorStorage.getJson(historyKey, []);

    if (!workouts.length) {
      const emptyMessage = document.createElement("p");
      emptyMessage.textContent = "No workout history yet.";
      historyContainer.appendChild(emptyMessage);
      return;
    }

    const heading = document.createElement("h2");
    heading.textContent = "Workout history";
    historyContainer.appendChild(heading);

    [...workouts].reverse().forEach(workout => {
      const card = document.createElement("div");
      const exercise = document.createElement("strong");
      const date = document.createElement("span");
      exercise.textContent = workout.exercise;
      date.className = "helper-text";
      date.textContent = workout.date;
      card.className = "history-card";
      card.append(
        exercise,
        document.createElement("br"),
        `Starting number: ${workout.startNumber}`,
        document.createElement("br"),
        workout.totalReps,
        document.createElement("br"),
        date
      );
      historyContainer.appendChild(card);
    });
  }

  function clearHistory() {
    if (!confirm("Delete all workout history?")) return;
    MindMirrorStorage.remove(historyKey);
    historyContainer.replaceChildren();
    alert("History deleted.");
  }

  document.getElementById("generateWorkoutBtn").addEventListener("click", generateWorkout);
  document.getElementById("saveBtn").addEventListener("click", saveWorkout);
  document.getElementById("viewHistoryBtn").addEventListener("click", renderHistory);
  document.getElementById("clearHistoryBtn").addEventListener("click", clearHistory);
  generateWorkout();
})();