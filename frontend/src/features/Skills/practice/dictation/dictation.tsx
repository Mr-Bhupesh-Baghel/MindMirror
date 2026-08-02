import { useEffect } from "react";
import "./styles.css";
import { initDictation } from "./app";

export default function Dictation({ onBack }: { onBack?: () => void }) {
  useEffect(() => {
    const cleanup = initDictation();

    return () => {
      cleanup?.();
    };
  }, []);

  return (
    <main className="dictation-page">
      {onBack && <button className="dictation-back" type="button" onClick={onBack}>← Back to dashboard</button>}

      {/* =========================
          HEADER
      ========================== */}

      <header className="practice-header">
        <div>
          <p className="practice-tag">
            🎧 English Listening Practice
          </p>

          <h1 className="practice-title">
            English Dictation
          </h1>

          <p className="practice-description">
            Listen carefully, type exactly what you hear,
            then check your accuracy.
          </p>
        </div>

        <div className="practice-stats">
          <div className="stat-card">
            <span>🔥</span>

            <div>
              <strong id="streakCount">
                0 day streak
              </strong>

              <small>Current streak</small>
            </div>
          </div>
        </div>
      </header>

      {/* =========================
          CUSTOM PRACTICE
      ========================== */}

      <section id="customDictation" className="custom-practice-card">

        <div className="section-heading">

          <div>
            <h2>
              Practice Your Own Text
            </h2>

            <p>
              Paste any English paragraph.
              It will automatically be split into
              individual dictation sentences.
            </p>
          </div>

        </div>

        <textarea
          id="sourceText"
          className="source-textarea"
          rows={5}
          spellCheck
          placeholder="Paste English text here..."
        />

        <div className="source-actions">

          <span>
            Your text becomes hidden after practice starts.
          </span>

          <button
            id="startCustomButton"
            className="primary-button"
            type="button"
          >
            ▶ Start Practice
          </button>

        </div>

      </section>

      <button
        id="changeTextButton"
        className="change-text-button"
        type="button"
        hidden
      >
        ← Change Practice Text
      </button>

      {/* =========================
          PROGRESS
      ========================== */}

      <section className="lesson-progress">

        <div className="lesson-info">

          <div className="lesson-left">

            <h3>
              Sentence
              <b id="exerciseNumber">1</b>
              {" / "}
              <b id="exerciseTotal">8</b>
            </h3>

            <span
              className="difficulty-pill"
              id="levelLabel"
            >
              Everyday English
            </span>

          </div>

          <div className="lesson-right">

            <span>
              Daily Goal
            </span>

            <strong>
              <b id="goalCount">0</b>/5
            </strong>

          </div>

        </div>

        <div className="progress-track">

          <span id="progressBar"></span>

        </div>

      </section>

      {/* =========================
          AUDIO PLAYER
      ========================== */}

      <section className="listen-card">

        <div className="listen-info">

          <div className="sound-circle">

            <i></i>
            <i></i>
            <i></i>
            <i></i>

          </div>

          <div>

            <h2>
              Listen Carefully
            </h2>

            <p>
              Replay the sentence as many
              times as you need before typing.
            </p>

          </div>

        </div>

        <button
          id="playButton"
          className="play-button"
          type="button"
        >
          ▶ Play Sentence
        </button>

      </section>

            {/* =========================
          PLAYBACK SPEED
      ========================== */}

      <section className="speed-card">

        <div className="section-title">
          <h3>Playback Speed</h3>
          <small>Choose a comfortable listening speed.</small>
        </div>

        <div className="speed-options">

          <button
            className="speed active"
            data-rate="0.8"
            type="button"
          >
            🐢 Slow
          </button>

          <button
            className="speed"
            data-rate="1"
            type="button"
          >
            ▶ Normal
          </button>

          <button
            className="speed"
            data-rate="1.2"
            type="button"
          >
            ⚡ Fast
          </button>

        </div>

      </section>

      {/* =========================
          ANSWER
      ========================== */}

      <section className="answer-card">

        <div className="section-title">

          <h2>Type What You Hear</h2>

          <p>
            Focus on spelling, punctuation,
            capitalization and every word.
          </p>

        </div>

        <label
          className="answer-label"
          htmlFor="answer"
        >
          Your Answer
        </label>

        <textarea
          id="answer"
          className="answer-box"
          rows={7}
          autoComplete="off"
          autoCapitalize="sentences"
          spellCheck
          placeholder="Start typing after listening..."
        />

        <div className="answer-toolbar">

          <span
            id="wordCount"
            className="word-counter"
          >
            0 words
          </span>

          <button
            id="hintButton"
            className="text-button"
            type="button"
          >
            💡 Show Hint
          </button>

        </div>

      </section>

      {/* =========================
          HINT
      ========================== */}

      <section className="hint-section">

        <p
          id="hint"
          className="hint"
          hidden
        ></p>

      </section>

      {/* =========================
          FEEDBACK
      ========================== */}

      <section className="feedback-section">

        <div
          id="feedback"
          className="feedback"
          role="status"
          hidden
        ></div>

      </section>

      {/* =========================
          ACTION BUTTONS
      ========================== */}

      <section className="actions-card">

        <button
          id="revealButton"
          className="secondary-button"
          type="button"
        >
          👁 Show Answer
        </button>

        <button
          id="checkButton"
          className="primary-button"
          type="button"
        >
          ✓ Check Answer
        </button>

        <button
          id="nextButton"
          className="success-button"
          type="button"
          hidden
        >
          ➜ Next Sentence
        </button>

      </section>

            {/* =========================
          SESSION SUMMARY
      ========================== */}

      <section className="statistics-grid">

        <article className="stat-box">

          <small>Average Accuracy</small>

          <strong id="scoreValue">
            0%
          </strong>

        </article>

        <article className="stat-box">

          <small>Completed</small>

          <strong id="completedCount">
            0
          </strong>

        </article>

        <article className="stat-box">

          <small>Best Score</small>

          <strong id="bestScore">
            —
          </strong>

        </article>

      </section>

      {/* =========================
          PRACTICE TIP
      ========================== */}

      <section className="practice-tip">

        <div className="tip-icon">
          💡
        </div>

        <div>

          <h3>
            Practice Tip
          </h3>

          <p>
            Don't pause after every word.
            Listen to the complete sentence,
            remember the meaning,
            then write naturally.
          </p>

        </div>

      </section>

      {/* =========================
          DAILY CHECKLIST
      ========================== */}

      <section className="practice-checklist">

        <h3>
          Today's Focus
        </h3>

        <ul>

          <li>
            👂 Listen carefully
          </li>

          <li>
            ✍ Type without looking
          </li>

          <li>
            ✔ Check mistakes
          </li>

          <li>
            🔁 Repeat difficult sentences
          </li>

        </ul>

      </section>

      {/* =========================
          SUPPORT MESSAGE
      ========================== */}

      <section className="support-section">

        <p
          id="supportNote"
          className="support-note"
        ></p>

      </section>

    </main>
  );
}
