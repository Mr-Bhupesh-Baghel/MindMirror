(() => {
  const input = document.getElementById("affirmationInput");
  const list = document.getElementById("affirmationList");
  const affirmationsKey = "affirmations";
  let affirmations = MindMirrorStorage.getJson(affirmationsKey, []);

  function renderAffirmations() {
    list.replaceChildren();

    affirmations.forEach((text, index) => {
      const item = document.createElement("li");
      const label = document.createElement("span");
      const deleteButton = document.createElement("button");

      label.textContent = text;
      deleteButton.className = "delete-btn";
      deleteButton.textContent = "X";
      deleteButton.addEventListener("click", () => {
        affirmations.splice(index, 1);
        MindMirrorStorage.setJson(affirmationsKey, affirmations);
        renderAffirmations();
      });

      item.append(label, deleteButton);
      list.appendChild(item);
    });
  }

  document.getElementById("addBtn").addEventListener("click", () => {
    const text = input.value.trim();
    if (!text) {
      alert("Please write something!");
      return;
    }

    affirmations.push(text);
    MindMirrorStorage.setJson(affirmationsKey, affirmations);
    input.value = "";
    renderAffirmations();
  });

  document.getElementById("clearAllBtn").addEventListener("click", () => {
    if (!confirm("Delete all affirmations?")) return;
    MindMirrorStorage.remove(affirmationsKey);
    affirmations = [];
    renderAffirmations();
  });

  renderAffirmations();
})();