# MindMirror - "Track Your Habits, Protect Your Mind"
# MindMirror – Mind Tree System

## Local setup

The backend uses PostgreSQL. Set these environment variables before starting it;
the schema is created automatically by Flyway:

```powershell
$env:DB_URL = 'jdbc:postgresql://localhost:5432/mindmirror'
$env:DB_USERNAME = 'postgres'
$env:DB_PASSWORD = '<your PostgreSQL password>'
```

Start the backend with `./mvnw.cmd spring-boot:run` from `backend`, and the
frontend with `npm run dev` from `frontend`.

## 🌳 Core Vision

The **Mind Tree** is the user's digital representation of their physical and mental well-being.

Every healthy action makes the tree stronger.

The dashboard always displays one living tree that grows throughout months and years.

Users don't simply complete habits—they **grow a life**.

---

# Tree Anatomy

| Tree Part   | Represents            | Influenced By    |
| ----------- | --------------------- | ---------------- |
| 🌱 Roots    | Physical foundation   | Water, Nutrition |
| 🌳 Trunk    | Strength & Discipline | Exercise         |
| 🌿 Branches | Knowledge             | Learning         |
| 🍃 Leaves   | Recovery              | Sleep            |
| 🌸 Flowers  | Inner Peace           | Meditation       |
| 🦋 Wildlife | Happiness             | Mood             |
| ✨ Fireflies | Consistency           | Streaks          |
| 🍎 Fruits   | Achievements          | Milestones       |

---

# Daily Habit Effects

### 💧 Water

Every completed glass:

* roots become stronger
* soil becomes healthier
* roots spread deeper

Missing water:

* no growth
* roots simply stop expanding

Never show dying roots.

---

### 💪 Exercise

Exercise increases

* trunk thickness
* bark quality
* tree height

Long streaks create stronger bark.

---

### 📚 Learning

Learning grows

* new branches
* extra twigs
* larger canopy

More learning

↓

More opportunities

↓

Bigger tree.

---

### 😴 Sleep

Good sleep adds

* greener leaves
* fuller canopy
* healthier appearance

Poor sleep

* leaves stop growing

No dead leaves.

---

### 🧘 Meditation

Meditation creates

* flowers
* soft glowing aura
* peaceful wind animation

---

### 😊 Mood

Positive mood attracts

* birds
* butterflies
* squirrels
* rabbits

Very high mood

↓

More wildlife appears.

---

### 🔥 Streaks

Consistency creates

Night mode effects

* glowing fireflies
* stars
* magical particles

Longer streak

↓

More glow

↓

More magic.

---

### 🏆 Milestones

Major achievements unlock

* fruits
* golden leaves
* special nests
* rare flowers
* rainbow
* waterfalls
* floating lights

These never disappear.

---

# Growth Stages

## Level 1

🌱 Seed

Tiny seed in soil.

---

## Level 2

🌿 Sprout

Small green shoot.

---

## Level 3

🌳 Young Tree

Few leaves.

Small branches.

---

## Level 4

🌲 Healthy Tree

Strong trunk.

Many branches.

---

## Level 5

🌸 Flowering Tree

Flowers bloom.

Butterflies appear.

---

## Level 6

🍎 Fruit Tree

Fruits grow.

Birds visit.

Golden sunlight.

---

## Level 7

🌳 Ancient Mind Tree

Huge canopy

Golden leaves

Fireflies

Bird nests

Butterflies

Soft wind

Glowing aura

Living ecosystem

---

# Growth Rules

Daily habits increase Growth Points (GP).

Example:

| Habit         |  GP |
| ------------- | --: |
| Water Goal    | +10 |
| Exercise      | +20 |
| Learning      | +15 |
| Sleep         | +20 |
| Meditation    | +10 |
| Positive Mood | +10 |

Maximum per day

85 GP

---

Tree Level

```
0–500 GP
Seed

500–1500
Sprout

1500–4000
Young Tree

4000–9000
Healthy Tree

9000–18000
Flowering

18000–35000
Fruit Tree

35000+
Ancient Tree
```

---

# Streak System

Missing one day

✅ pauses growth

Never removes decorations.

Missing several days

* no new growth
* existing tree stays beautiful

Returning to habits

↓

Growth resumes immediately.

No punishment.

Only encouragement.

---

# Rewards

| Achievement | Reward                   |
| ----------- | ------------------------ |
| 7 days      | 🐦 Bird                  |
| 14 days     | 🪺 Nest                  |
| 30 days     | 🦋 Butterfly             |
| 60 days     | 🌸 Flower Garden         |
| 100 days    | ✨ Fireflies              |
| 180 days    | 🍎 Fruits                |
| 365 days    | ⭐ Golden Leaves          |
| 1000 days   | 👑 Ancient Guardian Tree |

---

# Seasonal Themes

The tree reflects real-world seasons while preserving progress:

* 🌸 Spring: Blossoms and fresh leaves
* ☀️ Summer: Lush green canopy
* 🍂 Autumn: Warm orange and golden tones
* ❄️ Winter: Snow-covered branches with gentle lights

Seasonal changes are purely visual and never reduce growth.

---

# Dashboard Experience

The Mind Tree remains the centerpiece of the dashboard.

As users complete habits:

* Growth animations play within **300–500 ms**.
* A branch extends, a flower blooms, or a bird lands naturally.
* The tree gradually becomes a vibrant ecosystem that reflects long-term consistency rather than perfect daily performance.


## Author

Bhupesh Baghel
