/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.client.resources.language;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.function.Consumer;
import java.util.stream.Stream;
import net.minecraft.client.resources.language.ClientLanguage;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.client.resources.language.LanguageInfo;
import net.minecraft.client.resources.metadata.language.LanguageMetadataSection;
import net.minecraft.client.resources.metadata.language.LanguageMetadataSectionSerializer;
import net.minecraft.locale.Language;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LanguageManager
implements ResourceManagerReloadListener {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final LanguageInfo DEFAULT_LANGUAGE = new LanguageInfo("en_us", "US", "English", false);
    private Map<String, LanguageInfo> languages = ImmutableMap.of((Object)"en_us", (Object)DEFAULT_LANGUAGE);
    private String currentCode;
    private LanguageInfo currentLanguage = DEFAULT_LANGUAGE;

    public LanguageManager(String string) {
        this.currentCode = string;
    }

    private static Map<String, LanguageInfo> extractLanguages(Stream<PackResources> stream) {
        HashMap hashMap = Maps.newHashMap();
        stream.forEach(packResources -> {
            try {
                LanguageMetadataSection languageMetadataSection = packResources.getMetadataSection(LanguageMetadataSection.SERIALIZER);
                if (languageMetadataSection != null) {
                    for (LanguageInfo languageInfo : languageMetadataSection.getLanguages()) {
                        hashMap.putIfAbsent(languageInfo.getCode(), languageInfo);
                    }
                }
            }
            catch (IOException | RuntimeException exception) {
                LOGGER.warn("Unable to parse language metadata section of resourcepack: {}", (Object)packResources.getName(), (Object)exception);
            }
        });
        return ImmutableMap.copyOf((Map)hashMap);
    }

    @Override
    public void onResourceManagerReload(ResourceManager resourceManager) {
        this.languages = LanguageManager.extractLanguages(resourceManager.listPacks());
        LanguageInfo languageInfo = this.languages.getOrDefault("en_us", DEFAULT_LANGUAGE);
        this.currentLanguage = this.languages.getOrDefault(this.currentCode, languageInfo);
        ArrayList arrayList = Lists.newArrayList((Object[])new LanguageInfo[]{languageInfo});
        if (this.currentLanguage != languageInfo) {
            arrayList.add(this.currentLanguage);
        }
        ClientLanguage clientLanguage = ClientLanguage.loadFrom(resourceManager, arrayList);
        I18n.setLanguage(clientLanguage);
        Language.inject(clientLanguage);
    }

    public void setSelected(LanguageInfo languageInfo) {
        this.currentCode = languageInfo.getCode();
        this.currentLanguage = languageInfo;
    }

    public LanguageInfo getSelected() {
        return this.currentLanguage;
    }

    public SortedSet<LanguageInfo> getLanguages() {
        return Sets.newTreeSet(this.languages.values());
    }

    public LanguageInfo getLanguage(String string) {
        return this.languages.get(string);
    }
}

