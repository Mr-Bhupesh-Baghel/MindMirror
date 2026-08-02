let exercises = [
  {
    text: "The library opens at nine o'clock every morning.",
    hint: "It begins with ‘The library’ and includes a time.",
    level: "Everyday English",
  },
  {
    text: "Could you send me the details by Friday?",
    hint: "A polite question ending with a day of the week.",
    level: "Everyday English",
  },
  {
    text: "I usually take a short walk after lunch.",
    hint: "Listen for the adverb at the start: ‘usually’.",
    level: "Everyday English",
  },
  {
    text: "The weather forecast says it might rain later.",
    hint: "There are two words about weather in this sentence.",
    level: "Everyday English",
  },
  {
    text: "Please remember to charge your phone tonight.",
    hint: "A friendly instruction beginning with ‘Please’.",
    level: "Everyday English",
  },
  {
    text: "She has been learning English for two years.",
    hint: "This uses ‘has been’ to describe an ongoing activity.",
    level: "Building fluency",
  },
  {
    text: "The meeting was moved to the following Tuesday.",
    hint: "Pay attention to the past tense verb after ‘meeting’.",
    level: "Building fluency",
  },
  {
    text: "Small improvements become meaningful over time.",
    hint: "A motivating sentence with an adjective at the beginning.",
    level: "Building fluency",
  },
];

export function initDictation() {

  const playButton = document.querySelector("#playButton");
  const answer = document.querySelector("#answer");
  const checkButton = document.querySelector("#checkButton");
  const nextButton = document.querySelector("#nextButton");
  const revealButton = document.querySelector("#revealButton");
  const hintButton = document.querySelector("#hintButton");
  const hint = document.querySelector("#hint");
  const feedback = document.querySelector("#feedback");
  const wordCount = document.querySelector("#wordCount");
  const exerciseNumber = document.querySelector("#exerciseNumber");
  const exerciseTotal = document.querySelector("#exerciseTotal");
  const levelLabel = document.querySelector("#levelLabel");
  const progressBar = document.querySelector("#progressBar");
  const scoreValue = document.querySelector("#scoreValue");
  const completedCount = document.querySelector("#completedCount");
  const bestScore = document.querySelector("#bestScore");
  const goalCount = document.querySelector("#goalCount");
  const streakCount = document.querySelector("#streakCount");
  const supportNote = document.querySelector("#supportNote");
  const sourceText = document.querySelector("#sourceText");
  const startCustomButton = document.querySelector("#startCustomButton");
  const customDictation = document.querySelector("#customDictation");
  const changeTextButton = document.querySelector("#changeTextButton");

  if (!playButton) return;

  let index = 0;
  let rate = 0.8;
  let scores = [];
  let completed = new Set();
  let revealed = false;

  function normalise(value) {
    return value
      .toLowerCase()
      .replace(/[^a-z0-9\s']/g, "")
      .replace(/\s+/g, " ")
      .trim();
  }

  function words(value) {
    return normalise(value).split(" ").filter(Boolean);
  }

  function accuracy(expected, actual) {
    const a = words(expected);
    const b = words(actual);

    if (!b.length) return 0;

    const row = Array.from(
      { length: a.length + 1 },
      (_, i) => [i]
    );

    for (let j = 1; j <= b.length; j++) {
      row[0][j] = j;
    }

    for (let i = 1; i <= a.length; i++) {
      for (let j = 1; j <= b.length; j++) {
        row[i][j] =
          a[i - 1] === b[j - 1]
            ? row[i - 1][j - 1]
            : 1 +
              Math.min(
                row[i - 1][j],
                row[i][j - 1],
                row[i - 1][j - 1]
              );
      }
    }

    return Math.max(
      0,
      Math.round(
        (1 -
          row[a.length][b.length] /
            Math.max(a.length, b.length)) *
          100
      )
    );
  }

  function updateStats() {
    const avg = scores.length
      ? Math.round(
          scores.reduce((a, b) => a + b, 0) /
            scores.length
        )
      : 0;

    scoreValue.textContent = `${avg}%`;
    completedCount.textContent = completed.size;
    bestScore.textContent = scores.length
      ? `${Math.max(...scores)}%`
      : "—";
    goalCount.textContent = Math.min(completed.size, 5);
    streakCount.textContent = completed.size
      ? `${completed.size} day streak`
      : "0 day streak";
  }

  function render() {
    const item = exercises[index];

    exerciseNumber.textContent = index + 1;
    exerciseTotal.textContent = exercises.length;
    levelLabel.textContent = item.level;

    progressBar.style.width = `${
      ((index + 1) / exercises.length) * 100
    }%`;

    answer.value = "";
    hint.hidden = true;
    hint.textContent = item.hint;
    feedback.hidden = true;
    feedback.className = "feedback";

    nextButton.hidden = true;
    checkButton.hidden = false;

    revealButton.textContent = "Show answer";
    revealed = false;

    wordCount.textContent = "0 words";

    answer.focus();
  }

  function speak() {
    if (!("speechSynthesis" in window)) {
      supportNote.textContent =
        "Audio playback is not available in this browser.";
      return;
    }

    window.speechSynthesis.cancel();

    const utterance = new SpeechSynthesisUtterance(
      exercises[index].text
    );

    utterance.lang = "en-US";
    utterance.rate = rate;

    utterance.onstart = () => {
      playButton.innerHTML =
        '<span aria-hidden="true">◼</span> Playing…';
    };

    utterance.onend = () => {
      playButton.innerHTML =
        '<span aria-hidden="true">▶</span> Play sentence';
    };

    window.speechSynthesis.speak(utterance);
  }

  function showFeedback(message, type) {
    feedback.innerHTML = message;
    feedback.className = `feedback ${type}`;
    feedback.hidden = false;
  }
    playButton.addEventListener("click", speak);

  answer.addEventListener("input", () => {
    wordCount.textContent = `${words(answer.value).length} words`;
  });

  document.querySelectorAll(".speed").forEach((button) => {
    button.addEventListener("click", () => {
      document
        .querySelector(".speed.active")
        ?.classList.remove("active");

      button.classList.add("active");

      rate = Number(button.dataset.rate);
    });
  });

  hintButton.addEventListener("click", () => {
    hint.hidden = !hint.hidden;
  });

  revealButton.addEventListener("click", () => {
    revealed = !revealed;

    if (revealed) {
      answer.value = exercises[index].text;

      wordCount.textContent = `${words(answer.value).length} words`;

      revealButton.textContent = "Hide answer";

      showFeedback(
        `<strong>Correct sentence:</strong> ${exercises[index].text}`,
        "retry"
      );
    } else {
      answer.value = "";

      wordCount.textContent = "0 words";

      revealButton.textContent = "Show answer";

      feedback.hidden = true;
    }
  });

  checkButton.addEventListener("click", () => {
    if (!answer.value.trim()) {
      showFeedback(
        "Type your answer first, then check it.",
        "retry"
      );

      answer.focus();

      return;
    }

    const score = accuracy(
      exercises[index].text,
      answer.value
    );

    if (!completed.has(index)) {
      completed.add(index);

      scores.push(score);

      updateStats();
    }

    const message =
      score === 100
        ? "<strong>Perfect!</strong> Every word is in the right place."
        : score >= 85
        ? `<strong>Great listening — ${score}% accurate.</strong> Check the punctuation and any small words.`
        : `<strong>${score}% accurate.</strong> Listen once more, then compare your answer with the sentence.`;

    showFeedback(
      message,
      score >= 85 ? "good" : "retry"
    );

    checkButton.hidden = true;
    nextButton.hidden = false;
  });

  nextButton.addEventListener("click", () => {
    index = (index + 1) % exercises.length;

    render();
  });

  startCustomButton.addEventListener("click", () => {
    const text = sourceText.value.trim();

    if (!text) {
      supportNote.textContent =
        "Paste your dictation text first.";

      sourceText.focus();

      return;
    }

    const sentences =
      text
        .match(/[^.!?]+[.!?]+|[^.!?]+$/g)
        ?.map((sentence) => sentence.trim())
        .filter(Boolean) || [];

    if (!sentences.length) return;

    exercises = sentences.map((text) => ({
      text,
      hint: `This sentence has ${words(text).length} words.`,
      level: "Your dictation",
    }));

    index = 0;
    scores = [];
    completed = new Set();

    updateStats();

    sourceText.value = "";

    if (customDictation) customDictation.hidden = true;
    changeTextButton.hidden = false;

    supportNote.textContent =
      "Your source text is now hidden. Listen, type, and check your answer.";

    render();

    speak();
  });

  changeTextButton.addEventListener("click", () => {
    window.speechSynthesis?.cancel();

    if (customDictation) customDictation.hidden = false;
    changeTextButton.hidden = true;

    supportNote.textContent = "";

    sourceText.focus();
  });
    // Initialize application
  updateStats();
  render();

  // Optional cleanup function (useful in React)
  return () => {
    window.speechSynthesis?.cancel();
  };
}
