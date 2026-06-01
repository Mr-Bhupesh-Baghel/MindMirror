(function () {
  const storage = {
    getJson(key, fallback) {
      try {
        const value = localStorage.getItem(key);
        return value === null ? fallback : JSON.parse(value);
      } catch {
        return fallback;
      }
    },

    setJson(key, value) {
      localStorage.setItem(key, JSON.stringify(value));
    },

    remove(key) {
      localStorage.removeItem(key);
    },

    keysStartingWith(prefix) {
      return Object.keys(localStorage).filter(key => key.startsWith(prefix));
    }
  };

  window.MindMirrorStorage = storage;
})();
