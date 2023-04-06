/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 */
package net.minecraft.world.entity.schedule;

import com.google.common.collect.Maps;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.core.Registry;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.entity.schedule.ScheduleBuilder;
import net.minecraft.world.entity.schedule.Timeline;

public class Schedule {
    public static final Schedule EMPTY = Schedule.register("empty").changeActivityAt(0, Activity.IDLE).build();
    public static final Schedule SIMPLE = Schedule.register("simple").changeActivityAt(5000, Activity.WORK).changeActivityAt(11000, Activity.REST).build();
    public static final Schedule VILLAGER_BABY = Schedule.register("villager_baby").changeActivityAt(10, Activity.IDLE).changeActivityAt(3000, Activity.PLAY).changeActivityAt(6000, Activity.IDLE).changeActivityAt(10000, Activity.PLAY).changeActivityAt(12000, Activity.REST).build();
    public static final Schedule VILLAGER_DEFAULT = Schedule.register("villager_default").changeActivityAt(10, Activity.IDLE).changeActivityAt(2000, Activity.WORK).changeActivityAt(9000, Activity.MEET).changeActivityAt(11000, Activity.IDLE).changeActivityAt(12000, Activity.REST).build();
    private final Map<Activity, Timeline> timelines = Maps.newHashMap();

    protected static ScheduleBuilder register(String string) {
        Schedule schedule = Registry.register(Registry.SCHEDULE, string, new Schedule());
        return new ScheduleBuilder(schedule);
    }

    protected void ensureTimelineExistsFor(Activity activity) {
        if (!this.timelines.containsKey(activity)) {
            this.timelines.put(activity, new Timeline());
        }
    }

    protected Timeline getTimelineFor(Activity activity) {
        return this.timelines.get(activity);
    }

    protected List<Timeline> getAllTimelinesExceptFor(Activity activity) {
        return this.timelines.entrySet().stream().filter(entry -> entry.getKey() != activity).map(Map.Entry::getValue).collect(Collectors.toList());
    }

    public Activity getActivityAt(int n) {
        return this.timelines.entrySet().stream().max(Comparator.comparingDouble(entry -> ((Timeline)entry.getValue()).getValueAt(n))).map(Map.Entry::getKey).orElse(Activity.IDLE);
    }
}

