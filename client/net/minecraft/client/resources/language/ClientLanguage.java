/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Maps
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.client.resources.language;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.client.resources.language.FormattedBidiReorder;
import net.minecraft.client.resources.language.LanguageInfo;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.FormattedCharSequence;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ClientLanguage
extends Language {
    private static final Logger LOGGER = LogManager.getLogger();
    private final Map<String, String> storage;
    private final boolean defaultRightToLeft;

    private ClientLanguage(Map<String, String> map, boolean bl) {
        this.storage = map;
        this.defaultRightToLeft = bl;
    }

    public static ClientLanguage loadFrom(ResourceManager resourceManager, List<LanguageInfo> list) {
        HashMap hashMap = Maps.newHashMap();
        boolean bl = false;
        for (LanguageInfo languageInfo : list) {
            bl |= languageInfo.isBidirectional();
            String string = String.format("lang/%s.json", languageInfo.getCode());
            for (String string2 : resourceManager.getNamespaces()) {
                try {
                    ResourceLocation resourceLocation = new ResourceLocation(string2, string);
                    ClientLanguage.appendFrom(resourceManager.getResources(resourceLocation), hashMap);
                }
                catch (FileNotFoundException fileNotFoundException) {
                }
                catch (Exception exception) {
                    LOGGER.warn("Skipped language file: {}:{} ({})", (Object)string2, (Object)string, (Object)exception.toString());
                }
            }
        }
        return new ClientLanguage((Map<String, String>)ImmutableMap.copyOf((Map)hashMap), bl);
    }

    private static void appendFrom(List<Resource> list, Map<String, String> map) {
        for (Resource resource : list) {
            try {
                InputStream inputStream = resource.getInputStream();
                Throwable throwable = null;
                try {
                    Language.loadFromJson(inputStream, (arg_0, arg_1) -> map.put(arg_0, arg_1));
                }
                catch (Throwable throwable2) {
                    throwable = throwable2;
                    throw throwable2;
                }
                finally {
                    if (inputStream == null) continue;
                    if (throwable != null) {
                        try {
                            inputStream.close();
                        }
                        catch (Throwable throwable3) {
                            throwable.addSuppressed(throwable3);
                        }
                        continue;
                    }
                    inputStream.close();
                }
            }
            catch (IOException iOException) {
                LOGGER.warn("Failed to load translations from {}", (Object)resource, (Object)iOException);
            }
        }
    }

    @Override
    public String getOrDefault(String string) {
        return this.storage.getOrDefault(string, string);
    }

    @Override
    public boolean has(String string) {
        return this.storage.containsKey(string);
    }

    @Override
    public boolean isDefaultRightToLeft() {
        return this.defaultRightToLeft;
    }

    @Override
    public FormattedCharSequence getVisualOrder(FormattedText formattedText) {
        return FormattedBidiReorder.reorder(formattedText, this.defaultRightToLeft);
    }
}

