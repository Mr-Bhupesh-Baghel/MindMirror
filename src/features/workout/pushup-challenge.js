MindMirrorStorage.ready().then(() => {
  const totalDays = 100;
  const tracker = document.getElementById("tracker");
  const attemptList = document.getElementById("attempt-list");
  const summary = document.getElementById("summary");
  const progress = MindMirrorStorage.getJson("pushupProgress", {});

  const messages = [
    "Start strong!", "Keep going!", "Feeling it yet?", "Nice form!", "You're doing great!",
    "Push a bit harder!", "One step at a time!", "Consistency is key!", "Keep pushing!", "Discipline over motivation!",
    "10 days down!", "Leveling up!", "You’re stronger than you think!", "Crush it today!", "Midway milestone soon!",
    "Body is transforming!", "Respect your progress!", "Push harder!", "Don’t slow down!", "Halfway done!",
    "Now it gets fun!", "More control, more strength!", "You're a machine!", "This is your routine!", "Mind over muscle!",
    "You inspire yourself!", "Momentum is real!", "Stay sharp!", "Only a few days left!", "Push-up master loading…",
    "This habit = power!", "No breaks now!", "Fire mode on!", "Keep proving it!", "Discipline > excuses!",
    "Almost there!", "Strength, grit, growth!", "Big goals, small steps!", "Never back down!", "Beast mode unlocked!",
    "Closer than ever!", "Keep going strong!", "Legend mode starting!", "Break all limits!", "Hard work pays off!",
    "Final lap!", "Stay consistent!", "Push-up boss!", "Almost 100!", "100/100 — mission complete!"
  ];

  function renderSummary(records) {
    summary.replaceChildren();
    const totalPushups = records.reduce((total, record) => total + record.count, 0);
    const average = records.length ? (totalPushups / records.length).toFixed(1) : "0";
    const progressText = document.createElement("p");

    if (records.length >= totalDays) {
      const heading = document.createElement("h2");
      heading.textContent = "Challenge complete!";
      progressText.textContent = "You’ve completed all 100 days. Keep logging if you want to continue the habit.";
      summary.append(heading, progressText);
    } else {
      progressText.textContent = `Progress: ${records.length}/${totalDays} days completed.`;
      summary.appendChild(progressText);
    }

    const details = document.createElement("p");
    const lastDate = records.length ? records[records.length - 1].date : "—";
    details.textContent = `Total push-ups: ${totalPushups} · Average: ${average} · Last activity: ${lastDate}`;
    summary.appendChild(details);
  }

  function renderAttemptLog(records) {
    attemptList.replaceChildren();

    if (!records.length) {
      const emptyMessage = document.createElement("li");
      emptyMessage.textContent = "No attempts yet. Start today.";
      attemptList.appendChild(emptyMessage);
      return;
    }

    records.forEach(record => {
      const item = document.createElement("li");
      item.textContent = `Day ${record.day} — ${record.count} push-ups on ${record.date}`;
      attemptList.appendChild(item);
    });
  }

  function renderTracker() {
    tracker.replaceChildren();
    const completedRecords = Object.entries(progress)
      .filter(([, record]) => record.status === "done")
      .map(([day, record]) => ({ day, ...record }))
      .sort((first, second) => Number(first.day) - Number(second.day));
    const trackerLength = Math.max(totalDays, completedRecords.length + 5);

    for (let day = 1; day <= trackerLength; day++) {
      const record = progress[day] || { status: "pending", count: 0, date: "" };
      const card = document.createElement("div");
      const details = document.createElement("div");
      const dayLabel = document.createElement("strong");
      const target = document.createElement("div");
      const message = document.createElement("div");
      const result = document.createElement("div");
      const count = document.createElement("strong");
      const input = document.createElement("input");
      const button = document.createElement("button");

      card.className = `day ${record.status === "done" ? "done" : "pending"}`;
      dayLabel.textContent = `Day ${day}`;
      target.className = "target";
      target.textContent = `Target: ${day} push-ups`;
      message.className = "message";
      message.textContent = messages[day - 1] || "Keep the streak alive!";
      count.textContent = record.count || 0;
      result.className = "record";
      result.append("You did: ", count, record.date ? ` on ${record.date}` : "");
      input.type = "number";
      input.id = `input-${day}`;
      input.className = "count-input";
      input.placeholder = "Enter push-ups";
      input.min = "0";
      input.setAttribute("aria-label", `Push-ups completed on day ${day}`);
      button.type = "button";
      button.className = "done-btn";
      button.dataset.day = String(day);
      button.textContent = "Mark done";
      button.disabled = record.status === "done";

      details.append(dayLabel, target, message, result, input);
      card.append(details, button);
      tracker.appendChild(card);
    }

    renderAttemptLog(completedRecords);
    renderSummary(completedRecords);
  }

  tracker.addEventListener("click", event => {
    const button = event.target.closest("[data-day]");
    if (!button) return;

    const day = Number(button.dataset.day);
    const input = document.getElementById(`input-${day}`);
    progress[day] = {
      status: "done",
      count: Number.parseInt(input.value, 10) || 0,
      date: new Date().toLocaleDateString()
    };
    MindMirrorStorage.setJson("pushupProgress", progress);
    renderTracker();
  });

  document.getElementById("resetTrackerBtn").addEventListener("click", () => {
    if (!confirm("Are you sure you want to reset all data?")) return;
    Object.keys(progress).forEach(day => delete progress[day]);
    MindMirrorStorage.setJson("pushupProgress", progress);
    renderTracker();
  });

  renderTracker();
});
