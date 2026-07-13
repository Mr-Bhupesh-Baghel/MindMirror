// ===============================
// MindMirror Account
// ===============================

const STORAGE_KEY = "mindmirror_user";

// Elements
const nameInput = document.getElementById("name");
const emailInput = document.getElementById("email");

const loginBtn = document.getElementById("loginBtn");
const logoutBtn = document.getElementById("logoutBtn");
const editProfileBtn = document.getElementById("editProfileBtn");

const profileName = document.getElementById("profileName");
const profileEmail = document.getElementById("profileEmail");
const avatarLetter = document.getElementById("avatarLetter");
const loginStatus = document.getElementById("loginStatus");

const showName = document.getElementById("showName");
const showEmail = document.getElementById("showEmail");

const loginSection = document.getElementById("loginSection");
const profileSection = document.getElementById("profileSection");

const darkMode = document.getElementById("darkMode");
const notifications = document.getElementById("notifications");

const routineCount = document.getElementById("routineCount");
const waterCount = document.getElementById("waterCount");
const pushupCount = document.getElementById("pushupCount");
const streakCount = document.getElementById("streakCount");

const exportBtn = document.getElementById("exportBtn");
const importBtn = document.getElementById("importBtn");
const syncBtn = document.getElementById("syncBtn");
const settingsBtn = document.getElementById("settingsBtn");

// ===============================
// Helpers
// ===============================

function getUser() {
    return JSON.parse(localStorage.getItem(STORAGE_KEY));
}

function saveUser(user) {
    localStorage.setItem(STORAGE_KEY, JSON.stringify(user));
}

// ===============================
// Profile
// ===============================

function loadProfile() {

    const user = getUser();

    if (!user) {

        profileName.textContent = "Guest User";
        profileEmail.textContent = "Not Logged In";
        avatarLetter.textContent = "👤";

        showName.textContent = "-";
        showEmail.textContent = "-";

        loginStatus.textContent = "Offline";
        loginStatus.className = "status offline";

        loginSection.style.display = "block";
        profileSection.style.display = "none";

        return;
    }

    profileName.textContent = user.name;
    profileEmail.textContent = user.email;

    showName.textContent = user.name;
    showEmail.textContent = user.email;

    avatarLetter.textContent = user.name.charAt(0).toUpperCase();

    loginStatus.textContent = "Online";
    loginStatus.className = "status online";

    loginSection.style.display = "none";
    profileSection.style.display = "block";
}

// ===============================
// Login
// ===============================

loginBtn.addEventListener("click", () => {

    const name = nameInput.value.trim();
    const email = emailInput.value.trim();

    if (name === "" || email === "") {
        alert("Please fill all fields.");
        return;
    }

    const user = {
        name,
        email
    };

    saveUser(user);

    nameInput.value = "";
    emailInput.value = "";

    loadProfile();

    alert("Login Successful!");
});

// ===============================
// Logout
// ===============================

logoutBtn.addEventListener("click", () => {

    if (!confirm("Logout?")) return;

    localStorage.removeItem(STORAGE_KEY);

    loadProfile();

});

// ===============================
// Edit Profile
// ===============================

editProfileBtn.addEventListener("click", () => {

    const user = getUser();

    if (!user) return;

    const newName = prompt("Enter Name", user.name);

    if (newName === null) return;

    const newEmail = prompt("Enter Email", user.email);

    if (newEmail === null) return;

    user.name = newName;
    user.email = newEmail;

    saveUser(user);

    loadProfile();

});

// ===============================
// Dark Mode
// ===============================

darkMode.checked = localStorage.getItem("darkMode") === "true";

if (darkMode.checked) {
    document.body.classList.add("dark");
}

darkMode.addEventListener("change", () => {

    document.body.classList.toggle("dark");

    localStorage.setItem(
        "darkMode",
        darkMode.checked
    );

});

// ===============================
// Notifications
// ===============================

notifications.checked =
    localStorage.getItem("notifications") === "true";

notifications.addEventListener("change", () => {

    localStorage.setItem(
        "notifications",
        notifications.checked
    );

});

// ===============================
// Statistics
// ===============================

function loadStatistics() {

    routineCount.textContent =
        JSON.parse(localStorage.getItem("routineData"))?.length || 0;

    waterCount.textContent =
        JSON.parse(localStorage.getItem("waterData"))?.total || 0;

    pushupCount.textContent =
        JSON.parse(localStorage.getItem("pushupData"))?.total || 0;

    streakCount.textContent =
        JSON.parse(localStorage.getItem("streak")) || 0;

}

loadStatistics();

// ===============================
// Export
// ===============================

exportBtn.addEventListener("click", () => {

    const data = JSON.stringify(localStorage, null, 2);

    const blob = new Blob([data], {
        type: "application/json"
    });

    const url = URL.createObjectURL(blob);

    const a = document.createElement("a");

    a.href = url;
    a.download = "MindMirror_Backup.json";

    a.click();

    URL.revokeObjectURL(url);

});

// ===============================
// Import
// ===============================

importBtn.addEventListener("click", () => {

    const input = document.createElement("input");

    input.type = "file";
    input.accept = ".json";

    input.onchange = (e) => {

        const file = e.target.files[0];

        if (!file) return;

        const reader = new FileReader();

        reader.onload = () => {

            const data = JSON.parse(reader.result);

            for (const key in data) {
                localStorage.setItem(key, data[key]);
            }

            loadProfile();
            loadStatistics();

            alert("Import Successful");

        };

        reader.readAsText(file);

    };

    input.click();

});

// ===============================
// Cloud Sync
// ===============================

syncBtn.addEventListener("click", () => {

    alert("Cloud Sync Coming Soon ☁");

});

// ===============================
// Settings
// ===============================

settingsBtn.addEventListener("click", () => {

    alert("Settings Page Coming Soon");

});

// ===============================
// Initialize
// ===============================

loadProfile();
loadStatistics();