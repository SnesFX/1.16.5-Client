/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.reflect.TypeToken
 *  javax.annotation.Nullable
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.client.sounds;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.client.Camera;
import net.minecraft.client.Options;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.client.resources.sounds.SoundEventRegistration;
import net.minecraft.client.resources.sounds.SoundEventRegistrationSerializer;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.resources.sounds.TickableSoundInstance;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.client.sounds.SoundEventListener;
import net.minecraft.client.sounds.WeighedSoundEvents;
import net.minecraft.client.sounds.Weighted;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SoundManager
extends SimplePreparableReloadListener<Preparations> {
    public static final Sound EMPTY_SOUND = new Sound("meta:missing_sound", 1.0f, 1.0f, 1, Sound.Type.FILE, false, false, 16);
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = new GsonBuilder().registerTypeHierarchyAdapter(Component.class, (Object)new Component.Serializer()).registerTypeAdapter(SoundEventRegistration.class, (Object)new SoundEventRegistrationSerializer()).create();
    private static final TypeToken<Map<String, SoundEventRegistration>> SOUND_EVENT_REGISTRATION_TYPE = new TypeToken<Map<String, SoundEventRegistration>>(){};
    private final Map<ResourceLocation, WeighedSoundEvents> registry = Maps.newHashMap();
    private final SoundEngine soundEngine;

    public SoundManager(ResourceManager resourceManager, Options options) {
        this.soundEngine = new SoundEngine(this, options, resourceManager);
    }

    @Override
    protected Preparations prepare(ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        Preparations preparations = new Preparations();
        profilerFiller.startTick();
        for (String string : resourceManager.getNamespaces()) {
            profilerFiller.push(string);
            try {
                List<Resource> list = resourceManager.getResources(new ResourceLocation(string, "sounds.json"));
                for (Resource resource : list) {
                    profilerFiller.push(resource.getSourceName());
                    try {
                        try (InputStream inputStream = resource.getInputStream();
                             InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);){
                            profilerFiller.push("parse");
                            Map<String, SoundEventRegistration> map = GsonHelper.fromJson(GSON, (Reader)inputStreamReader, SOUND_EVENT_REGISTRATION_TYPE);
                            profilerFiller.popPush("register");
                            for (Map.Entry<String, SoundEventRegistration> entry : map.entrySet()) {
                                preparations.handleRegistration(new ResourceLocation(string, entry.getKey()), entry.getValue(), resourceManager);
                            }
                            profilerFiller.pop();
                        }
                    }
                    catch (RuntimeException runtimeException) {
                        LOGGER.warn("Invalid sounds.json in resourcepack: '{}'", (Object)resource.getSourceName(), (Object)runtimeException);
                    }
                    profilerFiller.pop();
                }
            }
            catch (IOException iOException) {
                // empty catch block
            }
            profilerFiller.pop();
        }
        profilerFiller.endTick();
        return preparations;
    }

    @Override
    protected void apply(Preparations preparations, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        preparations.apply(this.registry, this.soundEngine);
        for (ResourceLocation resourceLocation : this.registry.keySet()) {
            String string;
            WeighedSoundEvents weighedSoundEvents = this.registry.get(resourceLocation);
            if (!(weighedSoundEvents.getSubtitle() instanceof TranslatableComponent) || I18n.exists(string = ((TranslatableComponent)weighedSoundEvents.getSubtitle()).getKey())) continue;
            LOGGER.debug("Missing subtitle {} for event: {}", (Object)string, (Object)resourceLocation);
        }
        if (LOGGER.isDebugEnabled()) {
            for (ResourceLocation resourceLocation : this.registry.keySet()) {
                if (Registry.SOUND_EVENT.containsKey(resourceLocation)) continue;
                LOGGER.debug("Not having sound event for: {}", (Object)resourceLocation);
            }
        }
        this.soundEngine.reload();
    }

    private static boolean validateSoundResource(Sound sound, ResourceLocation resourceLocation, ResourceManager resourceManager) {
        ResourceLocation resourceLocation2 = sound.getPath();
        if (!resourceManager.hasResource(resourceLocation2)) {
            LOGGER.warn("File {} does not exist, cannot add it to event {}", (Object)resourceLocation2, (Object)resourceLocation);
            return false;
        }
        return true;
    }

    @Nullable
    public WeighedSoundEvents getSoundEvent(ResourceLocation resourceLocation) {
        return this.registry.get(resourceLocation);
    }

    public Collection<ResourceLocation> getAvailableSounds() {
        return this.registry.keySet();
    }

    public void queueTickingSound(TickableSoundInstance tickableSoundInstance) {
        this.soundEngine.queueTickingSound(tickableSoundInstance);
    }

    public void play(SoundInstance soundInstance) {
        this.soundEngine.play(soundInstance);
    }

    public void playDelayed(SoundInstance soundInstance, int n) {
        this.soundEngine.playDelayed(soundInstance, n);
    }

    public void updateSource(Camera camera) {
        this.soundEngine.updateSource(camera);
    }

    public void pause() {
        this.soundEngine.pause();
    }

    public void stop() {
        this.soundEngine.stopAll();
    }

    public void destroy() {
        this.soundEngine.destroy();
    }

    public void tick(boolean bl) {
        this.soundEngine.tick(bl);
    }

    public void resume() {
        this.soundEngine.resume();
    }

    public void updateSourceVolume(SoundSource soundSource, float f) {
        if (soundSource == SoundSource.MASTER && f <= 0.0f) {
            this.stop();
        }
        this.soundEngine.updateCategoryVolume(soundSource, f);
    }

    public void stop(SoundInstance soundInstance) {
        this.soundEngine.stop(soundInstance);
    }

    public boolean isActive(SoundInstance soundInstance) {
        return this.soundEngine.isActive(soundInstance);
    }

    public void addListener(SoundEventListener soundEventListener) {
        this.soundEngine.addEventListener(soundEventListener);
    }

    public void removeListener(SoundEventListener soundEventListener) {
        this.soundEngine.removeEventListener(soundEventListener);
    }

    public void stop(@Nullable ResourceLocation resourceLocation, @Nullable SoundSource soundSource) {
        this.soundEngine.stop(resourceLocation, soundSource);
    }

    public String getDebugString() {
        return this.soundEngine.getDebugString();
    }

    @Override
    protected /* synthetic */ Object prepare(ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        return this.prepare(resourceManager, profilerFiller);
    }

    public static class Preparations {
        private final Map<ResourceLocation, WeighedSoundEvents> registry = Maps.newHashMap();

        protected Preparations() {
        }

        private void handleRegistration(ResourceLocation resourceLocation, SoundEventRegistration soundEventRegistration, ResourceManager resourceManager) {
            boolean bl;
            WeighedSoundEvents weighedSoundEvents = this.registry.get(resourceLocation);
            boolean bl2 = bl = weighedSoundEvents == null;
            if (bl || soundEventRegistration.isReplace()) {
                if (!bl) {
                    LOGGER.debug("Replaced sound event location {}", (Object)resourceLocation);
                }
                weighedSoundEvents = new WeighedSoundEvents(resourceLocation, soundEventRegistration.getSubtitle());
                this.registry.put(resourceLocation, weighedSoundEvents);
            }
            block4 : for (final Sound sound : soundEventRegistration.getSounds()) {
                Weighted<Sound> weighted;
                final ResourceLocation resourceLocation2 = sound.getLocation();
                switch (sound.getType()) {
                    case FILE: {
                        if (!SoundManager.validateSoundResource(sound, resourceLocation, resourceManager)) continue block4;
                        weighted = sound;
                        break;
                    }
                    case SOUND_EVENT: {
                        weighted = new Weighted<Sound>(){

                            @Override
                            public int getWeight() {
                                WeighedSoundEvents weighedSoundEvents = (WeighedSoundEvents)registry.get(resourceLocation2);
                                return weighedSoundEvents == null ? 0 : weighedSoundEvents.getWeight();
                            }

                            @Override
                            public Sound getSound() {
                                Sound sound2;
                                WeighedSoundEvents weighedSoundEvents = (WeighedSoundEvents)registry.get(resourceLocation2);
                                if (weighedSoundEvents == null) {
                                    return EMPTY_SOUND;
                                }
                                return new Sound(sound2.getLocation().toString(), sound2.getVolume() * sound.getVolume(), sound2.getPitch() * sound.getPitch(), sound.getWeight(), Sound.Type.FILE, (sound2 = weighedSoundEvents.getSound()).shouldStream() || sound.shouldStream(), sound2.shouldPreload(), sound2.getAttenuationDistance());
                            }

                            @Override
                            public void preloadIfRequired(SoundEngine soundEngine) {
                                WeighedSoundEvents weighedSoundEvents = (WeighedSoundEvents)registry.get(resourceLocation2);
                                if (weighedSoundEvents == null) {
                                    return;
                                }
                                weighedSoundEvents.preloadIfRequired(soundEngine);
                            }

                            @Override
                            public /* synthetic */ Object getSound() {
                                return this.getSound();
                            }
                        };
                        break;
                    }
                    default: {
                        throw new IllegalStateException("Unknown SoundEventRegistration type: " + (Object)((Object)sound.getType()));
                    }
                }
                weighedSoundEvents.addSound(weighted);
            }
        }

        public void apply(Map<ResourceLocation, WeighedSoundEvents> map, SoundEngine soundEngine) {
            map.clear();
            for (Map.Entry<ResourceLocation, WeighedSoundEvents> entry : this.registry.entrySet()) {
                map.put(entry.getKey(), entry.getValue());
                entry.getValue().preloadIfRequired(soundEngine);
            }
        }

    }

}

