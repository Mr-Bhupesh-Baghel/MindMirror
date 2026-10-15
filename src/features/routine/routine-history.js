MindMirrorStorage.ready().then(() => {
  function getProgressEntries() {
    return MindMirrorStorage.keysStartingWith("daily-tasks-")
      .map(key => ({
        key,
        date: key.slice("daily-tasks-".length),
        data: MindMirrorStorage.getJson(key, {})
      }))
      .sort((first, second) => new Date(second.date) - new Date(first.date));
  }

  function createModal() {
    const modal = document.createElement("div");
    modal.id = "previousModal";
    modal.style.cssText = `
      display: none; position: fixed; inset: 0; z-index: 9999;
      overflow-y: auto; padding: 80px 20px; background: rgba(0, 0, 0, .5);
    `;
    modal.innerHTML = `
      <section style="margin: auto; padding: 20px; border-radius: 8px; width: min(100%, 500px); background: white;">
        <h2>Previous progress</h2>
        <table id="previousTable" style="width: 100%; border-collapse: collapse; margin-top: 10px;"></table>
        <button type="button" id="closePrevious" style="margin-top: 10px;">Close</button>
      </section>
    `;
    document.body.appendChild(modal);
    modal.querySelector("#closePrevious").addEventListener("click", () => {
      modal.style.display = "none";
    });
    modal.addEventListener("click", event => {
      const button = event.target.closest("[data-history-key]");
      if (!button) return;

      const { historyKey, date } = button.dataset;
      if (!confirm(`Delete progress for ${date}?`)) return;
      MindMirrorStorage.remove(historyKey);
      button.closest("tr").remove();
    });
    return modal;
  }

  function renderHistory() {
    const modal = document.getElementById("previousModal") || createModal();
    const table = modal.querySelector("#previousTable");
    const heading = document.createElement("tr");

    ["Date", "Completion", "Action"].forEach(text => {
      const cell = document.createElement("th");
      cell.textContent = text;
      cell.style.cssText = "padding: 8px; border-bottom: 1px solid #ccc; text-align: left;";
      heading.appendChild(cell);
    });
    table.replaceChildren(heading);

    getProgressEntries().forEach(({ key, date, data }) => {
      const completed = Object.values(data).filter(Boolean).length;
      const total = Object.keys(data).length;
      const percent = total ? Math.round((completed / total) * 100) : 0;
      const row = document.createElement("tr");
      const dateCell = document.createElement("td");
      const percentCell = document.createElement("td");
      const actionCell = document.createElement("td");
      const deleteButton = document.createElement("button");

      dateCell.textContent = date;
      percentCell.textContent = `${percent}%`;
      percentCell.style.color = percent >= 80 ? "#28a745" : percent >= 50 ? "#ffc107" : "#dc3545";
      deleteButton.type = "button";
      deleteButton.dataset.historyKey = key;
      deleteButton.dataset.date = date;
      deleteButton.textContent = "Delete";
      [dateCell, percentCell, actionCell].forEach(cell => {
        cell.style.cssText += "padding: 8px; border-bottom: 1px solid #eee;";
      });
      actionCell.appendChild(deleteButton);
      row.append(dateCell, percentCell, actionCell);
      table.appendChild(row);
    });

    modal.style.display = "block";
  }

  function exportProgress() {
    const rows = [["Date", "Completed (%)"]];
    getProgressEntries().forEach(({ date, data }) => {
      const completed = Object.values(data).filter(Boolean).length;
      const total = Object.keys(data).length;
      rows.push([date, total ? Math.round((completed / total) * 100) : 0]);
    });

    const worksheet = XLSX.utils.aoa_to_sheet(rows);
    const workbook = XLSX.utils.book_new();
    XLSX.utils.book_append_sheet(workbook, worksheet, "Performance");
    XLSX.writeFile(workbook, "Performance.xlsx");
  }

  document.getElementById("viewHistoryBtn").addEventListener("click", renderHistory);
  document.getElementById("exportProgressBtn").addEventListener("click", exportProgress);
});
