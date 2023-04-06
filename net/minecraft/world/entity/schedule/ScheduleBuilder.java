/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 */
package net.minecraft.world.entity.schedule;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.entity.schedule.Schedule;
import net.minecraft.world.entity.schedule.Timeline;

public class ScheduleBuilder {
    private final Schedule schedule;
    private final List<ActivityTransition> transitions = Lists.newArrayList();

    public ScheduleBuilder(Schedule schedule) {
        this.schedule = schedule;
    }

    public ScheduleBuilder changeActivityAt(int n, Activity activity) {
        this.transitions.add(new ActivityTransition(n, activity));
        return this;
    }

    public Schedule build() {
        this.transitions.stream().map(ActivityTransition::getActivity).collect(Collectors.toSet()).forEach(this.schedule::ensureTimelineExistsFor);
        this.transitions.forEach(activityTransition -> {
            Activity activity = activityTransition.getActivity();
            this.schedule.getAllTimelinesExceptFor(activity).forEach(timeline -> timeline.addKeyframe(activityTransition.getTime(), 0.0f));
            this.schedule.getTimelineFor(activity).addKeyframe(activityTransition.getTime(), 1.0f);
        });
        return this.schedule;
    }

    static class ActivityTransition {
        private final int time;
        private final Activity activity;

        public ActivityTransition(int n, Activity activity) {
            this.time = n;
            this.activity = activity;
        }

        public int getTime() {
            return this.time;
        }

        public Activity getActivity() {
            return this.activity;
        }
    }

}

