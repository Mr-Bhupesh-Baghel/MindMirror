package com.mindmirror.backend.tree;

public enum Habit {
    WATER(10), EXERCISE(20), LEARNING(15), SLEEP(20), MEDITATION(10), MOOD(10);
    private final int points;
    Habit(int points) { this.points = points; }
    public int points() { return points; }
}
