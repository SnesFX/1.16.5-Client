/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.HashMultimap
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Multimap
 *  com.google.common.collect.Sets
 *  javax.annotation.Nullable
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.apache.logging.log4j.Marker
 *  org.apache.logging.log4j.MarkerManager
 */
package net.minecraft.client.sounds;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.audio.Channel;
import com.mojang.blaze3d.audio.Library;
import com.mojang.blaze3d.audio.Listener;
import com.mojang.blaze3d.audio.SoundBuffer;
import com.mojang.math.Vector3f;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.client.Camera;
import net.minecraft.client.Options;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.resources.sounds.TickableSoundInstance;
import net.minecraft.client.sounds.AudioStream;
import net.minecraft.client.sounds.ChannelAccess;
import net.minecraft.client.sounds.SoundBufferLibrary;
import net.minecraft.client.sounds.SoundEngineExecutor;
import net.minecraft.client.sounds.SoundEventListener;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.client.sounds.WeighedSoundEvents;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public class SoundEngine {
    private static final Marker MARKER = MarkerManager.getMarker((String)"SOUNDS");
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Set<ResourceLocation> ONLY_WARN_ONCE = Sets.newHashSet();
    private final SoundManager soundManager;
    private final Options options;
    private boolean loaded;
    private final Library library = new Library();
    private final Listener listener = this.library.getListener();
    private final SoundBufferLibrary soundBuffers;
    private final SoundEngineExecutor executor = new SoundEngineExecutor();
    private final ChannelAccess channelAccess = new ChannelAccess(this.library, this.executor);
    private int tickCount;
    private final Map<SoundInstance, ChannelAccess.ChannelHandle> instanceToChannel = Maps.newHashMap();
    private final Multimap<SoundSource, SoundInstance> instanceBySource = HashMultimap.create();
    private final List<TickableSoundInstance> tickingSounds = Lists.newArrayList();
    private final Map<SoundInstance, Integer> queuedSounds = Maps.newHashMap();
    private final Map<SoundInstance, Integer> soundDeleteTime = Maps.newHashMap();
    private final List<SoundEventListener> listeners = Lists.newArrayList();
    private final List<TickableSoundInstance> queuedTickableSounds = Lists.newArrayList();
    private final List<Sound> preloadQueue = Lists.newArrayList();

    public SoundEngine(SoundManager soundManager, Options options, ResourceManager resourceManager) {
        this.soundManager = soundManager;
        this.options = options;
        this.soundBuffers = new SoundBufferLibrary(resourceManager);
    }

    public void reload() {
        ONLY_WARN_ONCE.clear();
        for (SoundEvent soundEvent : Registry.SOUND_EVENT) {
            ResourceLocation resourceLocation = soundEvent.getLocation();
            if (this.soundManager.getSoundEvent(resourceLocation) != null) continue;
            LOGGER.warn("Missing sound for event: {}", (Object)Registry.SOUND_EVENT.getKey(soundEvent));
            ONLY_WARN_ONCE.add(resourceLocation);
        }
        this.destroy();
        this.loadLibrary();
    }

    private synchronized void loadLibrary() {
        if (this.loaded) {
            return;
        }
        try {
            this.library.init();
            this.listener.reset();
            this.listener.setGain(this.options.getSoundSourceVolume(SoundSource.MASTER));
            this.soundBuffers.preload(this.preloadQueue).thenRun(this.preloadQueue::clear);
            this.loaded = true;
            LOGGER.info(MARKER, "Sound engine started");
        }
        catch (RuntimeException runtimeException) {
            LOGGER.error(MARKER, "Error starting SoundSystem. Turning off sounds & music", (Throwable)runtimeException);
        }
    }

    private float getVolume(@Nullable SoundSource soundSource) {
        if (soundSource == null || soundSource == SoundSource.MASTER) {
            return 1.0f;
        }
        return this.options.getSoundSourceVolume(soundSource);
    }

    public void updateCategoryVolume(SoundSource soundSource, float f) {
        if (!this.loaded) {
            return;
        }
        if (soundSource == SoundSource.MASTER) {
            this.listener.setGain(f);
            return;
        }
        this.instanceToChannel.forEach((soundInstance, channelHandle) -> {
            float f = this.calculateVolume((SoundInstance)soundInstance);
            channelHandle.execute(channel -> {
                if (f <= 0.0f) {
                    channel.stop();
                } else {
                    channel.setVolume(f);
                }
            });
        });
    }

    public void destroy() {
        if (this.loaded) {
            this.stopAll();
            this.soundBuffers.clear();
            this.library.cleanup();
            this.loaded = false;
        }
    }

    public void stop(SoundInstance soundInstance) {
        ChannelAccess.ChannelHandle channelHandle;
        if (this.loaded && (channelHandle = this.instanceToChannel.get(soundInstance)) != null) {
            channelHandle.execute(Channel::stop);
        }
    }

    public void stopAll() {
        if (this.loaded) {
            this.executor.flush();
            this.instanceToChannel.values().forEach(channelHandle -> channelHandle.execute(Channel::stop));
            this.instanceToChannel.clear();
            this.channelAccess.clear();
            this.queuedSounds.clear();
            this.tickingSounds.clear();
            this.instanceBySource.clear();
            this.soundDeleteTime.clear();
            this.queuedTickableSounds.clear();
        }
    }

    public void addEventListener(SoundEventListener soundEventListener) {
        this.listeners.add(soundEventListener);
    }

    public void removeEventListener(SoundEventListener soundEventListener) {
        this.listeners.remove(soundEventListener);
    }

    public void tick(boolean bl) {
        if (!bl) {
            this.tickNonPaused();
        }
        this.channelAccess.scheduleTick();
    }

    private void tickNonPaused() {
        ++this.tickCount;
        this.queuedTickableSounds.stream().filter(SoundInstance::canPlaySound).forEach(this::play);
        this.queuedTickableSounds.clear();
        for (TickableSoundInstance object2 : this.tickingSounds) {
            if (!object2.canPlaySound()) {
                this.stop(object2);
            }
            object2.tick();
            if (object2.isStopped()) {
                this.stop(object2);
                continue;
            }
            float f = this.calculateVolume(object2);
            float f2 = this.calculatePitch(object2);
            Vec3 vec3 = new Vec3(object2.getX(), object2.getY(), object2.getZ());
            ChannelAccess.ChannelHandle channelHandle = this.instanceToChannel.get(object2);
            if (channelHandle == null) continue;
            channelHandle.execute(channel -> {
                channel.setVolume(f);
                channel.setPitch(f2);
                channel.setSelfPosition(vec3);
            });
        }
        Iterator<Object> iterator = this.instanceToChannel.entrySet().iterator();
        while (iterator.hasNext()) {
            int n;
            Map.Entry entry = (Map.Entry)iterator.next();
            ChannelAccess.ChannelHandle channelHandle = (ChannelAccess.ChannelHandle)entry.getValue();
            SoundInstance soundInstance = (SoundInstance)entry.getKey();
            float f = this.options.getSoundSourceVolume(soundInstance.getSource());
            if (f <= 0.0f) {
                channelHandle.execute(Channel::stop);
                iterator.remove();
                continue;
            }
            if (!channelHandle.isStopped() || (n = this.soundDeleteTime.get(soundInstance).intValue()) > this.tickCount) continue;
            if (SoundEngine.shouldLoopManually(soundInstance)) {
                this.queuedSounds.put(soundInstance, this.tickCount + soundInstance.getDelay());
            }
            iterator.remove();
            LOGGER.debug(MARKER, "Removed channel {} because it's not playing anymore", (Object)channelHandle);
            this.soundDeleteTime.remove(soundInstance);
            try {
                this.instanceBySource.remove((Object)soundInstance.getSource(), (Object)soundInstance);
            }
            catch (RuntimeException runtimeException) {
                // empty catch block
            }
            if (!(soundInstance instanceof TickableSoundInstance)) continue;
            this.tickingSounds.remove(soundInstance);
        }
        Iterator<Map.Entry<SoundInstance, Integer>> iterator2 = this.queuedSounds.entrySet().iterator();
        while (iterator2.hasNext()) {
            Map.Entry<SoundInstance, Integer> entry = iterator2.next();
            if (this.tickCount < entry.getValue()) continue;
            SoundInstance soundInstance = entry.getKey();
            if (soundInstance instanceof TickableSoundInstance) {
                ((TickableSoundInstance)soundInstance).tick();
            }
            this.play(soundInstance);
            iterator2.remove();
        }
    }

    private static boolean requiresManualLooping(SoundInstance soundInstance) {
        return soundInstance.getDelay() > 0;
    }

    private static boolean shouldLoopManually(SoundInstance soundInstance) {
        return soundInstance.isLooping() && SoundEngine.requiresManualLooping(soundInstance);
    }

    private static boolean shouldLoopAutomatically(SoundInstance soundInstance) {
        return soundInstance.isLooping() && !SoundEngine.requiresManualLooping(soundInstance);
    }

    public boolean isActive(SoundInstance soundInstance) {
        if (!this.loaded) {
            return false;
        }
        if (this.soundDeleteTime.containsKey(soundInstance) && this.soundDeleteTime.get(soundInstance) <= this.tickCount) {
            return true;
        }
        return this.instanceToChannel.containsKey(soundInstance);
    }

    public void play(SoundInstance soundInstance) {
        boolean bl;
        if (!this.loaded) {
            return;
        }
        if (!soundInstance.canPlaySound()) {
            return;
        }
        WeighedSoundEvents weighedSoundEvents = soundInstance.resolve(this.soundManager);
        ResourceLocation resourceLocation = soundInstance.getLocation();
        if (weighedSoundEvents == null) {
            if (ONLY_WARN_ONCE.add(resourceLocation)) {
                LOGGER.warn(MARKER, "Unable to play unknown soundEvent: {}", (Object)resourceLocation);
            }
            return;
        }
        Sound sound = soundInstance.getSound();
        if (sound == SoundManager.EMPTY_SOUND) {
            if (ONLY_WARN_ONCE.add(resourceLocation)) {
                LOGGER.warn(MARKER, "Unable to play empty soundEvent: {}", (Object)resourceLocation);
            }
            return;
        }
        float f = soundInstance.getVolume();
        float f2 = Math.max(f, 1.0f) * (float)sound.getAttenuationDistance();
        SoundSource soundSource = soundInstance.getSource();
        float f3 = this.calculateVolume(soundInstance);
        float f4 = this.calculatePitch(soundInstance);
        SoundInstance.Attenuation attenuation = soundInstance.getAttenuation();
        boolean bl2 = soundInstance.isRelative();
        if (f3 == 0.0f && !soundInstance.canStartSilent()) {
            LOGGER.debug(MARKER, "Skipped playing sound {}, volume was zero.", (Object)sound.getLocation());
            return;
        }
        Vec3 vec3 = new Vec3(soundInstance.getX(), soundInstance.getY(), soundInstance.getZ());
        if (!this.listeners.isEmpty()) {
            boolean bl3 = bl = bl2 || attenuation == SoundInstance.Attenuation.NONE || this.listener.getListenerPosition().distanceToSqr(vec3) < (double)(f2 * f2);
            if (bl) {
                for (SoundEventListener completableFuture2 : this.listeners) {
                    completableFuture2.onPlaySound(soundInstance, weighedSoundEvents);
                }
            } else {
                LOGGER.debug(MARKER, "Did not notify listeners of soundEvent: {}, it is too far away to hear", (Object)resourceLocation);
            }
        }
        if (this.listener.getGain() <= 0.0f) {
            LOGGER.debug(MARKER, "Skipped playing soundEvent: {}, master volume was zero", (Object)resourceLocation);
            return;
        }
        bl = SoundEngine.shouldLoopAutomatically(soundInstance);
        boolean bl4 = sound.shouldStream();
        CompletableFuture<ChannelAccess.ChannelHandle> completableFuture = this.channelAccess.createHandle(sound.shouldStream() ? Library.Pool.STREAMING : Library.Pool.STATIC);
        ChannelAccess.ChannelHandle channelHandle = completableFuture.join();
        if (channelHandle == null) {
            LOGGER.warn("Failed to create new sound handle");
            return;
        }
        LOGGER.debug(MARKER, "Playing sound {} for event {}", (Object)sound.getLocation(), (Object)resourceLocation);
        this.soundDeleteTime.put(soundInstance, this.tickCount + 20);
        this.instanceToChannel.put(soundInstance, channelHandle);
        this.instanceBySource.put((Object)soundSource, (Object)soundInstance);
        channelHandle.execute(channel -> {
            channel.setPitch(f4);
            channel.setVolume(f3);
            if (attenuation == SoundInstance.Attenuation.LINEAR) {
                channel.linearAttenuation(f2);
            } else {
                channel.disableAttenuation();
            }
            channel.setLooping(bl && !bl4);
            channel.setSelfPosition(vec3);
            channel.setRelative(bl2);
        });
        if (!bl4) {
            this.soundBuffers.getCompleteBuffer(sound.getPath()).thenAccept(soundBuffer -> channelHandle.execute(channel -> {
                channel.attachStaticBuffer((SoundBuffer)soundBuffer);
                channel.play();
            }));
        } else {
            this.soundBuffers.getStream(sound.getPath(), bl).thenAccept(audioStream -> channelHandle.execute(channel -> {
                channel.attachBufferStream((AudioStream)audioStream);
                channel.play();
            }));
        }
        if (soundInstance instanceof TickableSoundInstance) {
            this.tickingSounds.add((TickableSoundInstance)soundInstance);
        }
    }

    public void queueTickingSound(TickableSoundInstance tickableSoundInstance) {
        this.queuedTickableSounds.add(tickableSoundInstance);
    }

    public void requestPreload(Sound sound) {
        this.preloadQueue.add(sound);
    }

    private float calculatePitch(SoundInstance soundInstance) {
        return Mth.clamp(soundInstance.getPitch(), 0.5f, 2.0f);
    }

    private float calculateVolume(SoundInstance soundInstance) {
        return Mth.clamp(soundInstance.getVolume() * this.getVolume(soundInstance.getSource()), 0.0f, 1.0f);
    }

    public void pause() {
        if (this.loaded) {
            this.channelAccess.executeOnChannels(stream -> stream.forEach(Channel::pause));
        }
    }

    public void resume() {
        if (this.loaded) {
            this.channelAccess.executeOnChannels(stream -> stream.forEach(Channel::unpause));
        }
    }

    public void playDelayed(SoundInstance soundInstance, int n) {
        this.queuedSounds.put(soundInstance, this.tickCount + n);
    }

    public void updateSource(Camera camera) {
        if (!this.loaded || !camera.isInitialized()) {
            return;
        }
        Vec3 vec3 = camera.getPosition();
        Vector3f vector3f = camera.getLookVector();
        Vector3f vector3f2 = camera.getUpVector();
        this.executor.execute(() -> {
            this.listener.setListenerPosition(vec3);
            this.listener.setListenerOrientation(vector3f, vector3f2);
        });
    }

    public void stop(@Nullable ResourceLocation resourceLocation, @Nullable SoundSource soundSource) {
        if (soundSource != null) {
            for (SoundInstance soundInstance : this.instanceBySource.get((Object)soundSource)) {
                if (resourceLocation != null && !soundInstance.getLocation().equals(resourceLocation)) continue;
                this.stop(soundInstance);
            }
        } else if (resourceLocation == null) {
            this.stopAll();
        } else {
            for (SoundInstance soundInstance : this.instanceToChannel.keySet()) {
                if (!soundInstance.getLocation().equals(resourceLocation)) continue;
                this.stop(soundInstance);
            }
        }
    }

    public String getDebugString() {
        return this.library.getDebugString();
    }
}

