/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Sets
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.world.entity.ai.goal;

import com.google.common.collect.Sets;
import java.util.AbstractCollection;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GoalSelector {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final WrappedGoal NO_GOAL = new WrappedGoal(Integer.MAX_VALUE, new Goal(){

        @Override
        public boolean canUse() {
            return false;
        }
    }){

        @Override
        public boolean isRunning() {
            return false;
        }
    };
    private final Map<Goal.Flag, WrappedGoal> lockedFlags = new EnumMap<Goal.Flag, WrappedGoal>(Goal.Flag.class);
    private final Set<WrappedGoal> availableGoals = Sets.newLinkedHashSet();
    private final Supplier<ProfilerFiller> profiler;
    private final EnumSet<Goal.Flag> disabledFlags = EnumSet.noneOf(Goal.Flag.class);
    private int newGoalRate = 3;

    public GoalSelector(Supplier<ProfilerFiller> supplier) {
        this.profiler = supplier;
    }

    public void addGoal(int n, Goal goal) {
        this.availableGoals.add(new WrappedGoal(n, goal));
    }

    public void removeGoal(Goal goal) {
        this.availableGoals.stream().filter(wrappedGoal -> wrappedGoal.getGoal() == goal).filter(WrappedGoal::isRunning).forEach(WrappedGoal::stop);
        this.availableGoals.removeIf(wrappedGoal -> wrappedGoal.getGoal() == goal);
    }

    public void tick() {
        ProfilerFiller profilerFiller = this.profiler.get();
        profilerFiller.push("goalCleanup");
        this.getRunningGoals().filter(wrappedGoal -> {
            if (!wrappedGoal.isRunning()) return true;
            if (wrappedGoal.getFlags().stream().anyMatch(this.disabledFlags::contains)) return true;
            if (wrappedGoal.canContinueToUse()) return false;
            return true;
        }).forEach(Goal::stop);
        this.lockedFlags.forEach((flag, wrappedGoal) -> {
            if (!wrappedGoal.isRunning()) {
                this.lockedFlags.remove(flag);
            }
        });
        profilerFiller.pop();
        profilerFiller.push("goalUpdate");
        this.availableGoals.stream().filter(wrappedGoal -> !wrappedGoal.isRunning()).filter(wrappedGoal -> wrappedGoal.getFlags().stream().noneMatch(this.disabledFlags::contains)).filter(wrappedGoal -> wrappedGoal.getFlags().stream().allMatch(flag -> this.lockedFlags.getOrDefault(flag, NO_GOAL).canBeReplacedBy((WrappedGoal)wrappedGoal))).filter(WrappedGoal::canUse).forEach(wrappedGoal -> {
            wrappedGoal.getFlags().forEach(flag -> {
                WrappedGoal wrappedGoal2 = this.lockedFlags.getOrDefault(flag, NO_GOAL);
                wrappedGoal2.stop();
                this.lockedFlags.put((Goal.Flag)((Object)((Object)flag)), (WrappedGoal)wrappedGoal);
            });
            wrappedGoal.start();
        });
        profilerFiller.pop();
        profilerFiller.push("goalTick");
        this.getRunningGoals().forEach(WrappedGoal::tick);
        profilerFiller.pop();
    }

    public Stream<WrappedGoal> getRunningGoals() {
        return this.availableGoals.stream().filter(WrappedGoal::isRunning);
    }

    public void disableControlFlag(Goal.Flag flag) {
        this.disabledFlags.add(flag);
    }

    public void enableControlFlag(Goal.Flag flag) {
        this.disabledFlags.remove((Object)flag);
    }

    public void setControlFlag(Goal.Flag flag, boolean bl) {
        if (bl) {
            this.enableControlFlag(flag);
        } else {
            this.disableControlFlag(flag);
        }
    }

}

