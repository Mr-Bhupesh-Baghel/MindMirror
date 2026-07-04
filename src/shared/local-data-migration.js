(function () {
  const QUEUE_KEY = "mindmirrorMigrationQueue";
  const MIGRATED_KEY = "mindmirrorMigratedOperations";
  const STATUS_KEY = "mindmirrorMigrationStatus";
  const LEGACY_KEYS = [
    "customTasks",
    "holidayTasks",
    "holidayTasksChecked",
    "waterGoal",
    "history",
    "pushupProgress",
    "maintenanceRecords",
    "affirmations",
    "feedbackList"
  ];
  const todayIso = new Date().toISOString().slice(0, 10);

  function storage() {
    return window.MindMirrorStorage;
  }

  function api() {
    return window.MindMirrorApi;
  }

  function getMigrated() {
    return storage().getJson(MIGRATED_KEY, []);
  }

  function setMigrated(ids) {
    storage().setJson(MIGRATED_KEY, Array.from(new Set(ids)));
  }

  function getQueue() {
    return storage().getJson(QUEUE_KEY, []);
  }

  function setQueue(queue) {
    storage().setJson(QUEUE_KEY, queue);
  }

  function setStatus(status) {
    const nextStatus = {
      updatedAt: new Date().toISOString(),
      ...status
    };
    storage().setJson(STATUS_KEY, nextStatus);
    window.dispatchEvent(new CustomEvent("mindmirror:migration-status", { detail: nextStatus }));
  }

  function getStatus() {
    return storage().getJson(STATUS_KEY, {});
  }

  function hasLocalData() {
    return LEGACY_KEYS.some(key => localStorage.getItem(key) !== null)
      || storage().keysStartingWith("daily-tasks-").length > 0
      || getQueue().length > 0;
  }

  function cleanupLocalData() {
    LEGACY_KEYS.forEach(key => storage().remove(key));
    storage().keysStartingWith("daily-tasks-").forEach(key => storage().remove(key));
    storage().remove(QUEUE_KEY);
    setStatus({
      ...getStatus(),
      cleanupComplete: true,
      message: "Migration complete. Local browser data was cleaned up."
    });
  }

  function operationId(method, path, body) {
    return `${method} ${path} ${JSON.stringify(body || {})}`;
  }

  function op(method, path, body) {
    return {
      id: operationId(method, path, body),
      method,
      path,
      body
    };
  }

  function uniqueOperations(operations) {
    const seen = new Set();
    return operations.filter(operation => {
      if (seen.has(operation.id)) {
        return false;
      }
      seen.add(operation.id);
      return true;
    });
  }

  function toIsoDate(value, fallback = todayIso) {
    if (!value) {
      return fallback;
    }

    if (/^\d{4}-\d{2}-\d{2}$/.test(value)) {
      return value;
    }

    const parsed = new Date(value);
    if (!Number.isNaN(parsed.getTime())) {
      return parsed.toISOString().slice(0, 10);
    }

    const parts = String(value).split(/[/-]/).map(Number);
    if (parts.length === 3 && parts.every(Number.isFinite)) {
      const [first, second, year] = parts;
      const monthFirst = first > 12;
      const day = monthFirst ? first : second;
      const month = monthFirst ? second : first;
      return `${year.toString().padStart(4, "0")}-${month.toString().padStart(2, "0")}-${day.toString().padStart(2, "0")}`;
    }

    return fallback;
  }

  function numberValue(value, fallback = 0) {
    const parsed = Number(value);
    return Number.isFinite(parsed) ? parsed : fallback;
  }

  function legacyTaskMaps(backendTasks) {
    const dailyTasks = backendTasks.filter(task => task.category === "daily");
    const customTasks = storage().getJson("customTasks", []);
    const holidayTasks = storage().getJson("holidayTasks", []);
    const customByIndex = new Map();
    const holidayByTitle = new Map();

    customTasks.forEach((title, index) => {
      const task = backendTasks.find(item => item.title === title && item.category === "custom");
      if (task) {
        customByIndex.set(`custom-${index}`, task);
      }
    });

    holidayTasks.forEach(title => {
      const task = backendTasks.find(item => item.title === title && item.category === "holiday");
      if (task) {
        holidayByTitle.set(title, task);
      }
    });

    return {
      defaultTask: dailyTasks[0],
      customByIndex,
      holidayByTitle
    };
  }

  function collectTaskOperations() {
    const operations = [];
    storage().getJson("customTasks", []).forEach((title, index) => {
      operations.push(op("POST", "/api/routine/tasks", {
        title,
        category: "custom",
        sortOrder: index + 1
      }));
    });

    storage().getJson("holidayTasks", []).forEach((title, index) => {
      operations.push(op("POST", "/api/routine/tasks", {
        title,
        category: "holiday",
        sortOrder: index + 1
      }));
    });

    return operations;
  }

  function collectSimpleOperations() {
    const operations = [];
    const waterGoal = numberValue(storage().getJson("waterGoal", 8), 8);
    const waterHistory = storage().getJson("history", {});

    Object.entries(waterHistory).forEach(([date, glasses]) => {
      operations.push(op("PUT", "/api/water", {
        entryDate: toIsoDate(date),
        glasses: Math.max(0, numberValue(glasses)),
        goalGlasses: Math.max(1, waterGoal)
      }));
    });

    const pushupProgress = storage().getJson("pushupProgress", {});
    Object.entries(pushupProgress).forEach(([day, record]) => {
      if (record?.status !== "done") {
        return;
      }
      const challengeDay = Math.max(1, numberValue(day, 1));
      operations.push(op("PUT", "/api/pushups/challenge", {
        entryDate: toIsoDate(record.date),
        challengeDay,
        targetCount: challengeDay,
        completedCount: Math.max(0, numberValue(record.count))
      }));
    });

    storage().getJson("maintenanceRecords", []).forEach((record, index) => {
      operations.push(op("PUT", "/api/pushups/maintenance", {
        entryDate: toIsoDate(record.date),
        pushupsCount: Math.max(1, numberValue(record.pushups, 1)),
        challengeDay: index + 1
      }));
    });

    storage().getJson("affirmations", []).forEach(text => {
      operations.push(op("POST", "/api/affirmations", { text }));
    });

    storage().getJson("feedbackList", []).forEach(feedback => {
      operations.push(op("POST", "/api/feedback", {
        name: feedback.name,
        email: feedback.email,
        rating: numberValue(feedback.rating, 5),
        message: feedback.message,
        feedbackDate: toIsoDate(feedback.date)
      }));
    });

    return operations;
  }

  async function uploadOperation(operation) {
    const client = api();
    if (operation.method === "POST") {
      return client.post(operation.path, operation.body);
    }
    if (operation.method === "PUT") {
      return client.put(operation.path, operation.body);
    }
    if (operation.method === "PATCH") {
      return client.patch(operation.path, operation.body);
    }
    if (operation.method === "DELETE") {
      return client.delete(operation.path);
    }
    return client.get(operation.path);
  }

  async function uploadOperations(operations) {
    const migrated = new Set(getMigrated());
    const failed = [];
    let uploaded = 0;
    let skipped = 0;

    for (const operation of uniqueOperations(operations)) {
      if (migrated.has(operation.id)) {
        skipped++;
        continue;
      }

      try {
        await uploadOperation(operation);
        migrated.add(operation.id);
        uploaded++;
      } catch (error) {
        failed.push({
          ...operation,
          attempts: (operation.attempts || 0) + 1,
          lastError: error.message,
          lastTriedAt: new Date().toISOString()
        });
      }
    }

    setMigrated(Array.from(migrated));
    return { uploaded, failed, skipped };
  }

  async function notifyMigrationStart() {
    try {
      await api().post("/api/migration/start", {});
    } catch {
      // Status reporting is best-effort; domain uploads still carry the data.
    }
  }

  async function notifySync(status) {
    try {
      return await api().post("/api/sync", {
        state: status.state,
        uploaded: status.uploaded || 0,
        failed: status.failed || 0,
        queued: status.queued || 0,
        conflicts: status.conflicts || 0,
        lastError: status.lastError || null
      });
    } catch {
      return null;
    }
  }

  async function fetchCloudStatus() {
    try {
      const cloud = await api().get("/api/sync/status");
      setStatus({
        ...getStatus(),
        cloud
      });
      return cloud;
    } catch {
      return null;
    }
  }

  async function collectRoutineCompletionOperations() {
    const backendTasks = await api().get("/api/routine/tasks");
    const maps = legacyTaskMaps(backendTasks);
    const operations = [];

    storage().keysStartingWith("daily-tasks-").forEach(key => {
      const completionDate = key.replace("daily-tasks-", "");
      const data = storage().getJson(key, {});
      const completions = [];

      Object.entries(data).forEach(([legacyId, completed]) => {
        let task = null;
        if (legacyId.startsWith("custom-")) {
          task = maps.customByIndex.get(legacyId);
        } else {
          task = maps.defaultTask;
        }

        if (task) {
          completions.push({ taskId: task.id, completed: Boolean(completed) });
        }
      });

      if (completions.length) {
        operations.push(op("PUT", "/api/routine/completions", {
          completionDate,
          completions
        }));
      }
    });

    const checkedHolidayTasks = storage().getJson("holidayTasksChecked", []);
    const holidayCompletions = checkedHolidayTasks
      .map(title => maps.holidayByTitle.get(title))
      .filter(Boolean)
      .map(task => ({ taskId: task.id, completed: true }));

    if (holidayCompletions.length) {
      operations.push(op("PUT", "/api/routine/completions", {
        completionDate: todayIso,
        completions: holidayCompletions
      }));
    }

    return operations;
  }

  async function runMigration() {
    if (!api()?.getAccessToken()) {
      setStatus({
        state: "needs-auth",
        message: "Sign in before migrating local data.",
        queued: getQueue().length
      });
      return getStatus();
    }

    await notifyMigrationStart();

    if (!navigator.onLine) {
      const queue = uniqueOperations([...getQueue(), ...collectTaskOperations(), ...collectSimpleOperations()]);
      setQueue(queue);
      const offlineStatus = {
        state: "offline",
        message: "Offline. Local data is queued and will retry when the browser is online.",
        queued: queue.length
      };
      await notifySync(offlineStatus);
      setStatus(offlineStatus);
      return getStatus();
    }

    setStatus({ state: "running", message: "Migrating local data...", queued: getQueue().length });

    const queuedResult = await uploadOperations(getQueue());
    setQueue(queuedResult.failed);

    const firstPass = await uploadOperations([
      ...collectTaskOperations(),
      ...collectSimpleOperations()
    ]);

    let routineResult = { uploaded: 0, failed: [], skipped: 0 };
    try {
      routineResult = await uploadOperations(await collectRoutineCompletionOperations());
    } catch (error) {
      routineResult.failed = [{
        id: "routine-completion-collection",
        method: "GET",
        path: "/api/routine/tasks",
        lastError: error.message,
        attempts: 1,
        lastTriedAt: new Date().toISOString()
      }];
    }

    const failed = uniqueOperations([...queuedResult.failed, ...firstPass.failed, ...routineResult.failed]);
    setQueue(failed);

    const uploaded = queuedResult.uploaded + firstPass.uploaded + routineResult.uploaded;
    const conflicts = queuedResult.skipped + firstPass.skipped + routineResult.skipped;
    const state = failed.length ? "partial" : "complete";
    const nextStatus = {
      state,
      uploaded,
      failed: failed.length,
      queued: failed.length,
      conflicts,
      lastSyncAt: new Date().toISOString(),
      message: failed.length
        ? "Migration partially completed. Failed uploads are queued for retry."
        : "Migration complete. Local data remains available as an offline fallback."
    };
    const cloud = await notifySync(nextStatus);
    setStatus({ ...nextStatus, cloud });

    return getStatus();
  }

  async function retryQueued() {
    if (!getQueue().length) {
      return getStatus();
    }
    return runMigration();
  }

  function initialize() {
    window.addEventListener("online", retryQueued);
    window.addEventListener("mindmirror:login", runMigration);

    if (api()?.getAccessToken() && navigator.onLine && getQueue().length) {
      retryQueued();
    } else if (api()?.getAccessToken() && navigator.onLine) {
      fetchCloudStatus();
    }
  }

  window.MindMirrorMigration = {
    run: runMigration,
    retryQueued,
    getStatus,
    getQueue,
    hasLocalData,
    cleanupLocalData,
    fetchCloudStatus,
    initialize
  };

  initialize();
})();
