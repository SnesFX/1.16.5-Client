/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.util.profiling;

import net.minecraft.util.profiling.ProfileResults;
import net.minecraft.util.profiling.ProfilerFiller;

public interface ProfileCollector
extends ProfilerFiller {
    public ProfileResults getResults();
}

