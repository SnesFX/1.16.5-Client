/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.world.scores;

import java.util.Comparator;
import javax.annotation.Nullable;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;

public class Score {
    public static final Comparator<Score> SCORE_COMPARATOR = (score, score2) -> {
        if (score.getScore() > score2.getScore()) {
            return 1;
        }
        if (score.getScore() < score2.getScore()) {
            return -1;
        }
        return score2.getOwner().compareToIgnoreCase(score.getOwner());
    };
    private final Scoreboard scoreboard;
    @Nullable
    private final Objective objective;
    private final String owner;
    private int count;
    private boolean locked;
    private boolean forceUpdate;

    public Score(Scoreboard scoreboard, Objective objective, String string) {
        this.scoreboard = scoreboard;
        this.objective = objective;
        this.owner = string;
        this.locked = true;
        this.forceUpdate = true;
    }

    public void add(int n) {
        if (this.objective.getCriteria().isReadOnly()) {
            throw new IllegalStateException("Cannot modify read-only score");
        }
        this.setScore(this.getScore() + n);
    }

    public void increment() {
        this.add(1);
    }

    public int getScore() {
        return this.count;
    }

    public void reset() {
        this.setScore(0);
    }

    public void setScore(int n) {
        int n2 = this.count;
        this.count = n;
        if (n2 != n || this.forceUpdate) {
            this.forceUpdate = false;
            this.getScoreboard().onScoreChanged(this);
        }
    }

    @Nullable
    public Objective getObjective() {
        return this.objective;
    }

    public String getOwner() {
        return this.owner;
    }

    public Scoreboard getScoreboard() {
        return this.scoreboard;
    }

    public boolean isLocked() {
        return this.locked;
    }

    public void setLocked(boolean bl) {
        this.locked = bl;
    }
}

