MindMirrorStorage.ready().then(() => {
// =============================
// Skills Dashboard
// =============================

let skills = MindMirrorStorage.getJson("skills", []);
let editIndex = -1;

// -----------------------------
// Save Data
// -----------------------------
function saveData() {
    return MindMirrorStorage.setJson("skills", skills);
}

// -----------------------------
// Dashboard
// -----------------------------
function updateDashboard() {

    const total = skills.length;

    let average = 0;

    if (total > 0) {

        let sum = 0;

        skills.forEach(skill => {
            sum += Number(skill.percent);
        });

        average = Math.round(sum / total);
    }

    document.getElementById("totalSkills").textContent = total;
    document.getElementById("averageSkill").textContent = average + "%";
}

// -----------------------------
// Add Skill
// -----------------------------
function addSkill() {

    const name = document.getElementById("skillName").value.trim();

    const percent = parseInt(
        document.getElementById("skillPercent").value
    );

    const works = document
        .getElementById("skillWorks")
        .value
        .split("\n")
        .map(work => work.trim())
        .filter(work => work !== "");

    if (name === "") {
        alert("Enter Skill Name");
        return;
    }

    if (isNaN(percent) || percent < 0 || percent > 100) {
        alert("Percentage must be between 0 and 100");
        return;
    }

    skills.push({
        name: name,
        percent: percent,
        works: works
    });

    saveData();

    clearForm();

    renderSkills();
}

// -----------------------------
// Clear Form
// -----------------------------
function clearForm() {

    document.getElementById("skillName").value = "";

    document.getElementById("skillPercent").value = "";

    document.getElementById("skillWorks").value = "";
}

// -----------------------------
// Render Skills
// -----------------------------
function renderSkills() {

    const container = document.getElementById("skillsContainer");

    const search = document
        .getElementById("searchSkill")
        .value
        .toLowerCase();

    container.innerHTML = "";

    const filteredSkills = skills.filter(skill =>
        skill.name.toLowerCase().includes(search)
    );

    if (filteredSkills.length === 0) {

        container.innerHTML =
        `
        <div class="empty">
            No Skills Found
        </div>
        `;

        updateDashboard();

        return;
    }

    filteredSkills.forEach(skill => {

        const index = skills.indexOf(skill);

        let color = "low";

        if (skill.percent >= 70) {
            color = "high";
        }
        else if (skill.percent >= 40) {
            color = "medium";
        }

        let workHTML = "";

        if (skill.works.length > 0) {

            skill.works.forEach(work => {

                workHTML +=
                `
                <li>${work}</li>
                `;
            });

        } else {

            workHTML =
            `
            <li>No Work Added</li>
            `;
        }

        container.innerHTML +=
        `
        <div class="skill-card">

            <div class="skill-header">

                <h2>${skill.name}</h2>

                <span>${skill.percent}%</span>

            </div>

            <div class="progress">

                <div
                    class="progress-bar ${color}"
                    style="width:${skill.percent}%"
                >

                    ${skill.percent}%

                </div>

            </div>

            <h3 class="work-title">
                Work List
            </h3>

            <ul class="work-list">

                ${workHTML}

            </ul>

            <div class="actions">

                <button
                    class="edit-btn"
                    onclick="editSkill(${index})"
                >

                    Edit

                </button>

                <button
                    class="delete-btn"
                    onclick="deleteSkill(${index})"
                >

                    Delete

                </button>

            </div>

        </div>
        `;
    });

    updateDashboard();
}
// -----------------------------
// Delete Skill
// -----------------------------
function deleteSkill(index) {

    const confirmDelete = confirm(
        "Are you sure you want to delete this skill?"
    );

    if (!confirmDelete) {
        return;
    }

    skills.splice(index, 1);

    saveData();

    renderSkills();
}

// -----------------------------
// Edit Skill
// -----------------------------
function editSkill(index) {

    editIndex = index;

    document.getElementById("editSkillName").value =
        skills[index].name;

    document.getElementById("editSkillPercent").value =
        skills[index].percent;

    document.getElementById("editSkillWorks").value =
        skills[index].works.join("\n");

    document.getElementById("editModal").style.display = "flex";
}

// -----------------------------
// Update Skill
// -----------------------------
function updateSkill() {

    if (editIndex === -1) {
        return;
    }

    const name =
        document.getElementById("editSkillName")
        .value
        .trim();

    const percent =
        parseInt(
            document.getElementById("editSkillPercent").value
        );

    const works =
        document.getElementById("editSkillWorks")
        .value
        .split("\n")
        .map(work => work.trim())
        .filter(work => work !== "");

    if (name === "") {

        alert("Skill name cannot be empty.");

        return;
    }

    if (
        isNaN(percent) ||
        percent < 0 ||
        percent > 100
    ) {

        alert("Percentage must be between 0 and 100.");

        return;
    }

    skills[editIndex].name = name;

    skills[editIndex].percent = percent;

    skills[editIndex].works = works;

    saveData();

    closeModal();

    renderSkills();
}

// -----------------------------
// Close Modal
// -----------------------------
function closeModal() {

    editIndex = -1;

    document.getElementById("editModal").style.display = "none";
}

// -----------------------------
// Close Modal if Click Outside
// -----------------------------
window.onclick = function(event) {

    const modal =
        document.getElementById("editModal");

    if (event.target === modal) {

        closeModal();

    }

};
// =============================
// Extra Utility Functions
// =============================

// Sort Skills (Highest Percentage First)
function sortSkills() {

    skills.sort((a, b) => b.percent - a.percent);

    saveData();

    renderSkills();
}

// Sort Skills (A-Z)
function sortByName() {

    skills.sort((a, b) => a.name.localeCompare(b.name));

    saveData();

    renderSkills();
}

// Remove All Skills
function clearAllSkills() {

    const confirmClear = confirm(
        "Delete ALL skills permanently?"
    );

    if (!confirmClear) {
        return;
    }

    skills = [];

    saveData();

    renderSkills();
}

// Export JSON
function exportSkills() {

    const data = JSON.stringify(skills, null, 4);

    const blob = new Blob(
        [data],
        {
            type: "application/json"
        }
    );

    const url = URL.createObjectURL(blob);

    const a = document.createElement("a");

    a.href = url;

    a.download = "skills.json";

    a.click();

    URL.revokeObjectURL(url);
}

// Import JSON
function importSkills(event) {

    const file = event.target.files[0];

    if (!file) {
        return;
    }

    const reader = new FileReader();

    reader.onload = function(e) {

        try {

            skills = JSON.parse(e.target.result);

            Promise.resolve(saveData()).then(result => {
                if (result.ok) {
                    renderSkills();
                    alert("Skills Imported Successfully");
                } else {
                    alert("Skills were not saved. Try again when account services are online.");
                }
            });

        } catch {

            alert("Invalid JSON File");

        }

    };

    reader.readAsText(file);

}

// =============================
// Keyboard Shortcut
// Ctrl + Enter = Add Skill
// =============================

document.addEventListener("keydown", function(event) {

    if (
        event.ctrlKey &&
        event.key === "Enter"
    ) {

        addSkill();

    }

});

// =============================
// Initial Load
// =============================

renderSkills();

updateDashboard();
});

// =============================
// End of script.js
// =============================
