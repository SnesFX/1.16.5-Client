/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.google.common.collect.Lists
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParser
 *  com.google.gson.JsonSyntaxException
 *  javax.annotation.Nullable
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.client.renderer;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.platform.GlUtil;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GpuWarnlistManager
extends SimplePreparableReloadListener<Preparations> {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final ResourceLocation GPU_WARNLIST_LOCATION = new ResourceLocation("gpu_warnlist.json");
    private ImmutableMap<String, String> warnings = ImmutableMap.of();
    private boolean showWarning;
    private boolean warningDismissed;
    private boolean skipFabulous;

    public boolean hasWarnings() {
        return !this.warnings.isEmpty();
    }

    public boolean willShowWarning() {
        return this.hasWarnings() && !this.warningDismissed;
    }

    public void showWarning() {
        this.showWarning = true;
    }

    public void dismissWarning() {
        this.warningDismissed = true;
    }

    public void dismissWarningAndSkipFabulous() {
        this.warningDismissed = true;
        this.skipFabulous = true;
    }

    public boolean isShowingWarning() {
        return this.showWarning && !this.warningDismissed;
    }

    public boolean isSkippingFabulous() {
        return this.skipFabulous;
    }

    public void resetWarnings() {
        this.showWarning = false;
        this.warningDismissed = false;
        this.skipFabulous = false;
    }

    @Nullable
    public String getRendererWarnings() {
        return (String)this.warnings.get((Object)"renderer");
    }

    @Nullable
    public String getVersionWarnings() {
        return (String)this.warnings.get((Object)"version");
    }

    @Nullable
    public String getVendorWarnings() {
        return (String)this.warnings.get((Object)"vendor");
    }

    @Nullable
    public String getAllWarnings() {
        StringBuilder stringBuilder = new StringBuilder();
        this.warnings.forEach((string, string2) -> stringBuilder.append((String)string).append(": ").append((String)string2));
        return stringBuilder.length() == 0 ? null : stringBuilder.toString();
    }

    @Override
    protected Preparations prepare(ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        ArrayList arrayList = Lists.newArrayList();
        ArrayList arrayList2 = Lists.newArrayList();
        ArrayList arrayList3 = Lists.newArrayList();
        profilerFiller.startTick();
        JsonObject jsonObject = GpuWarnlistManager.parseJson(resourceManager, profilerFiller);
        if (jsonObject != null) {
            profilerFiller.push("compile_regex");
            GpuWarnlistManager.compilePatterns(jsonObject.getAsJsonArray("renderer"), arrayList);
            GpuWarnlistManager.compilePatterns(jsonObject.getAsJsonArray("version"), arrayList2);
            GpuWarnlistManager.compilePatterns(jsonObject.getAsJsonArray("vendor"), arrayList3);
            profilerFiller.pop();
        }
        profilerFiller.endTick();
        return new Preparations(arrayList, arrayList2, arrayList3);
    }

    @Override
    protected void apply(Preparations preparations, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        this.warnings = preparations.apply();
    }

    private static void compilePatterns(JsonArray jsonArray, List<Pattern> list) {
        jsonArray.forEach(jsonElement -> list.add(Pattern.compile(jsonElement.getAsString(), 2)));
    }

    @Nullable
    private static JsonObject parseJson(ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        profilerFiller.push("parse_json");
        JsonObject jsonObject = null;
        try {
            try (Resource resource = resourceManager.getResource(GPU_WARNLIST_LOCATION);
                 BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8));){
                jsonObject = new JsonParser().parse((Reader)bufferedReader).getAsJsonObject();
            }
        }
        catch (JsonSyntaxException | IOException throwable) {
            LOGGER.warn("Failed to load GPU warnlist");
        }
        profilerFiller.pop();
        return jsonObject;
    }

    @Override
    protected /* synthetic */ Object prepare(ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        return this.prepare(resourceManager, profilerFiller);
    }

    public static final class Preparations {
        private final List<Pattern> rendererPatterns;
        private final List<Pattern> versionPatterns;
        private final List<Pattern> vendorPatterns;

        private Preparations(List<Pattern> list, List<Pattern> list2, List<Pattern> list3) {
            this.rendererPatterns = list;
            this.versionPatterns = list2;
            this.vendorPatterns = list3;
        }

        private static String matchAny(List<Pattern> list, String string) {
            ArrayList arrayList = Lists.newArrayList();
            for (Pattern pattern : list) {
                Matcher matcher = pattern.matcher(string);
                while (matcher.find()) {
                    arrayList.add(matcher.group());
                }
            }
            return String.join((CharSequence)", ", arrayList);
        }

        private ImmutableMap<String, String> apply() {
            String string;
            String string2;
            ImmutableMap.Builder builder = new ImmutableMap.Builder();
            String string3 = Preparations.matchAny(this.rendererPatterns, GlUtil.getRenderer());
            if (!string3.isEmpty()) {
                builder.put((Object)"renderer", (Object)string3);
            }
            if (!(string = Preparations.matchAny(this.versionPatterns, GlUtil.getOpenGLVersion())).isEmpty()) {
                builder.put((Object)"version", (Object)string);
            }
            if (!(string2 = Preparations.matchAny(this.vendorPatterns, GlUtil.getVendor())).isEmpty()) {
                builder.put((Object)"vendor", (Object)string2);
            }
            return builder.build();
        }
    }

}

