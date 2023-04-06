/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Splitter
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  it.unimi.dsi.fastutil.objects.Object2LongMap
 *  it.unimi.dsi.fastutil.objects.Object2LongMaps
 *  org.apache.commons.io.IOUtils
 *  org.apache.commons.lang3.ObjectUtils
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.util.profiling;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongMaps;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.util.profiling.ProfileResults;
import net.minecraft.util.profiling.ProfilerPathEntry;
import net.minecraft.util.profiling.ResultField;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FilledProfileResults
implements ProfileResults {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final ProfilerPathEntry EMPTY = new ProfilerPathEntry(){

        @Override
        public long getDuration() {
            return 0L;
        }

        @Override
        public long getCount() {
            return 0L;
        }

        @Override
        public Object2LongMap<String> getCounters() {
            return Object2LongMaps.emptyMap();
        }
    };
    private static final Splitter SPLITTER = Splitter.on((char)'\u001e');
    private static final Comparator<Map.Entry<String, CounterCollector>> COUNTER_ENTRY_COMPARATOR = Map.Entry.comparingByValue(Comparator.comparingLong(counterCollector -> CounterCollector.access$000(counterCollector))).reversed();
    private final Map<String, ? extends ProfilerPathEntry> entries;
    private final long startTimeNano;
    private final int startTimeTicks;
    private final long endTimeNano;
    private final int endTimeTicks;
    private final int tickDuration;

    public FilledProfileResults(Map<String, ? extends ProfilerPathEntry> map, long l, int n, long l2, int n2) {
        this.entries = map;
        this.startTimeNano = l;
        this.startTimeTicks = n;
        this.endTimeNano = l2;
        this.endTimeTicks = n2;
        this.tickDuration = n2 - n;
    }

    private ProfilerPathEntry getEntry(String string) {
        ProfilerPathEntry profilerPathEntry = this.entries.get(string);
        return profilerPathEntry != null ? profilerPathEntry : EMPTY;
    }

    @Override
    public List<ResultField> getTimes(String string) {
        String string2 = string;
        ProfilerPathEntry profilerPathEntry = this.getEntry("root");
        long l = profilerPathEntry.getDuration();
        ProfilerPathEntry profilerPathEntry2 = this.getEntry(string);
        long l2 = profilerPathEntry2.getDuration();
        long l3 = profilerPathEntry2.getCount();
        ArrayList arrayList = Lists.newArrayList();
        if (!string.isEmpty()) {
            string = string + '\u001e';
        }
        long l4 = 0L;
        for (String object : this.entries.keySet()) {
            if (!FilledProfileResults.isDirectChild(string, object)) continue;
            l4 += this.getEntry(object).getDuration();
        }
        float f = l4;
        if (l4 < l2) {
            l4 = l2;
        }
        if (l < l4) {
            l = l4;
        }
        for (String string3 : this.entries.keySet()) {
            if (!FilledProfileResults.isDirectChild(string, string3)) continue;
            ProfilerPathEntry profilerPathEntry3 = this.getEntry(string3);
            long l5 = profilerPathEntry3.getDuration();
            double d = (double)l5 * 100.0 / (double)l4;
            double d2 = (double)l5 * 100.0 / (double)l;
            String string4 = string3.substring(string.length());
            arrayList.add(new ResultField(string4, d, d2, profilerPathEntry3.getCount()));
        }
        if ((float)l4 > f) {
            arrayList.add(new ResultField("unspecified", (double)((float)l4 - f) * 100.0 / (double)l4, (double)((float)l4 - f) * 100.0 / (double)l, l3));
        }
        Collections.sort(arrayList);
        arrayList.add(0, new ResultField(string2, 100.0, (double)l4 * 100.0 / (double)l, l3));
        return arrayList;
    }

    private static boolean isDirectChild(String string, String string2) {
        return string2.length() > string.length() && string2.startsWith(string) && string2.indexOf(30, string.length() + 1) < 0;
    }

    private Map<String, CounterCollector> getCounterValues() {
        TreeMap treeMap = Maps.newTreeMap();
        this.entries.forEach((string, profilerPathEntry) -> {
            Object2LongMap<String> object2LongMap = profilerPathEntry.getCounters();
            if (!object2LongMap.isEmpty()) {
                List list = SPLITTER.splitToList((CharSequence)string);
                object2LongMap.forEach((string2, l) -> treeMap.computeIfAbsent(string2, string -> new CounterCollector()).addValue(list.iterator(), (long)l));
            }
        });
        return treeMap;
    }

    @Override
    public long getStartTimeNano() {
        return this.startTimeNano;
    }

    @Override
    public int getStartTimeTicks() {
        return this.startTimeTicks;
    }

    @Override
    public long getEndTimeNano() {
        return this.endTimeNano;
    }

    @Override
    public int getEndTimeTicks() {
        return this.endTimeTicks;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean saveResults(File file) {
        boolean bl;
        file.getParentFile().mkdirs();
        OutputStreamWriter outputStreamWriter = null;
        try {
            outputStreamWriter = new OutputStreamWriter((OutputStream)new FileOutputStream(file), StandardCharsets.UTF_8);
            outputStreamWriter.write(this.getProfilerResults(this.getNanoDuration(), this.getTickDuration()));
            bl = true;
        }
        catch (Throwable throwable) {
            boolean bl2;
            try {
                LOGGER.error("Could not save profiler results to {}", (Object)file, (Object)throwable);
                bl2 = false;
            }
            catch (Throwable throwable2) {
                IOUtils.closeQuietly(outputStreamWriter);
                throw throwable2;
            }
            IOUtils.closeQuietly((Writer)outputStreamWriter);
            return bl2;
        }
        IOUtils.closeQuietly((Writer)outputStreamWriter);
        return bl;
    }

    protected String getProfilerResults(long l, int n) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("---- Minecraft Profiler Results ----\n");
        stringBuilder.append("// ");
        stringBuilder.append(FilledProfileResults.getComment());
        stringBuilder.append("\n\n");
        stringBuilder.append("Version: ").append(SharedConstants.getCurrentVersion().getId()).append('\n');
        stringBuilder.append("Time span: ").append(l / 1000000L).append(" ms\n");
        stringBuilder.append("Tick span: ").append(n).append(" ticks\n");
        stringBuilder.append("// This is approximately ").append(String.format(Locale.ROOT, "%.2f", Float.valueOf((float)n / ((float)l / 1.0E9f)))).append(" ticks per second. It should be ").append(20).append(" ticks per second\n\n");
        stringBuilder.append("--- BEGIN PROFILE DUMP ---\n\n");
        this.appendProfilerResults(0, "root", stringBuilder);
        stringBuilder.append("--- END PROFILE DUMP ---\n\n");
        Map<String, CounterCollector> map = this.getCounterValues();
        if (!map.isEmpty()) {
            stringBuilder.append("--- BEGIN COUNTER DUMP ---\n\n");
            this.appendCounters(map, stringBuilder, n);
            stringBuilder.append("--- END COUNTER DUMP ---\n\n");
        }
        return stringBuilder.toString();
    }

    private static StringBuilder indentLine(StringBuilder stringBuilder, int n) {
        stringBuilder.append(String.format("[%02d] ", n));
        for (int i = 0; i < n; ++i) {
            stringBuilder.append("|   ");
        }
        return stringBuilder;
    }

    private void appendProfilerResults(int n, String string2, StringBuilder stringBuilder) {
        List<ResultField> list = this.getTimes(string2);
        Object2LongMap<String> object2LongMap = ((ProfilerPathEntry)ObjectUtils.firstNonNull((Object[])new ProfilerPathEntry[]{this.entries.get(string2), EMPTY})).getCounters();
        object2LongMap.forEach((string, l) -> FilledProfileResults.indentLine(stringBuilder, n).append('#').append((String)string).append(' ').append(l).append('/').append(l / (long)this.tickDuration).append('\n'));
        if (list.size() < 3) {
            return;
        }
        for (int i = 1; i < list.size(); ++i) {
            ResultField resultField = list.get(i);
            FilledProfileResults.indentLine(stringBuilder, n).append(resultField.name).append('(').append(resultField.count).append('/').append(String.format(Locale.ROOT, "%.0f", Float.valueOf((float)resultField.count / (float)this.tickDuration))).append(')').append(" - ").append(String.format(Locale.ROOT, "%.2f", resultField.percentage)).append("%/").append(String.format(Locale.ROOT, "%.2f", resultField.globalPercentage)).append("%\n");
            if ("unspecified".equals(resultField.name)) continue;
            try {
                this.appendProfilerResults(n + 1, string2 + '\u001e' + resultField.name, stringBuilder);
                continue;
            }
            catch (Exception exception) {
                stringBuilder.append("[[ EXCEPTION ").append(exception).append(" ]]");
            }
        }
    }

    private void appendCounterResults(int n, String string, CounterCollector counterCollector, int n2, StringBuilder stringBuilder) {
        FilledProfileResults.indentLine(stringBuilder, n).append(string).append(" total:").append(counterCollector.selfValue).append('/').append(counterCollector.totalValue).append(" average: ").append(counterCollector.selfValue / (long)n2).append('/').append(counterCollector.totalValue / (long)n2).append('\n');
        counterCollector.children.entrySet().stream().sorted(COUNTER_ENTRY_COMPARATOR).forEach(entry -> this.appendCounterResults(n + 1, (String)entry.getKey(), (CounterCollector)entry.getValue(), n2, stringBuilder));
    }

    private void appendCounters(Map<String, CounterCollector> map, StringBuilder stringBuilder, int n) {
        map.forEach((string, counterCollector) -> {
            stringBuilder.append("-- Counter: ").append((String)string).append(" --\n");
            this.appendCounterResults(0, "root", (CounterCollector)counterCollector.children.get("root"), n, stringBuilder);
            stringBuilder.append("\n\n");
        });
    }

    private static String getComment() {
        String[] arrstring = new String[]{"Shiny numbers!", "Am I not running fast enough? :(", "I'm working as hard as I can!", "Will I ever be good enough for you? :(", "Speedy. Zoooooom!", "Hello world", "40% better than a crash report.", "Now with extra numbers", "Now with less numbers", "Now with the same numbers", "You should add flames to things, it makes them go faster!", "Do you feel the need for... optimization?", "*cracks redstone whip*", "Maybe if you treated it better then it'll have more motivation to work faster! Poor server."};
        try {
            return arrstring[(int)(Util.getNanos() % (long)arrstring.length)];
        }
        catch (Throwable throwable) {
            return "Witty comment unavailable :(";
        }
    }

    @Override
    public int getTickDuration() {
        return this.tickDuration;
    }

    static class CounterCollector {
        private long selfValue;
        private long totalValue;
        private final Map<String, CounterCollector> children = Maps.newHashMap();

        private CounterCollector() {
        }

        public void addValue(Iterator<String> iterator, long l) {
            this.totalValue += l;
            if (!iterator.hasNext()) {
                this.selfValue += l;
            } else {
                this.children.computeIfAbsent(iterator.next(), string -> new CounterCollector()).addValue(iterator, l);
            }
        }
    }

}

