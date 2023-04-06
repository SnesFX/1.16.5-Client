/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  javax.annotation.concurrent.Immutable
 */
package net.minecraft.world;

import javax.annotation.concurrent.Immutable;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;

@Immutable
public class DifficultyInstance {
    private final Difficulty base;
    private final float effectiveDifficulty;

    public DifficultyInstance(Difficulty difficulty, long l, long l2, float f) {
        this.base = difficulty;
        this.effectiveDifficulty = this.calculateDifficulty(difficulty, l, l2, f);
    }

    public Difficulty getDifficulty() {
        return this.base;
    }

    public float getEffectiveDifficulty() {
        return this.effectiveDifficulty;
    }

    public boolean isHarderThan(float f) {
        return this.effectiveDifficulty > f;
    }

    public float getSpecialMultiplier() {
        if (this.effectiveDifficulty < 2.0f) {
            return 0.0f;
        }
        if (this.effectiveDifficulty > 4.0f) {
            return 1.0f;
        }
        return (this.effectiveDifficulty - 2.0f) / 2.0f;
    }

    private float calculateDifficulty(Difficulty difficulty, long l, long l2, float f) {
        if (difficulty == Difficulty.PEACEFUL) {
            return 0.0f;
        }
        boolean bl = difficulty == Difficulty.HARD;
        float f2 = 0.75f;
        float f3 = Mth.clamp(((float)l + -72000.0f) / 1440000.0f, 0.0f, 1.0f) * 0.25f;
        f2 += f3;
        float f4 = 0.0f;
        f4 += Mth.clamp((float)l2 / 3600000.0f, 0.0f, 1.0f) * (bl ? 1.0f : 0.75f);
        f4 += Mth.clamp(f * 0.25f, 0.0f, f3);
        if (difficulty == Difficulty.EASY) {
            f4 *= 0.5f;
        }
        return (float)difficulty.getId() * (f2 += f4);
    }
}

