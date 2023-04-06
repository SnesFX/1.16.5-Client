/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Charsets
 *  com.google.common.base.Splitter
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  com.google.common.io.Files
 *  com.google.gson.Gson
 *  com.google.gson.reflect.TypeToken
 *  com.mojang.datafixers.DataFixer
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.ArrayUtils
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.client;

import com.google.common.base.Charsets;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.VideoMode;
import com.mojang.blaze3d.platform.Window;
import com.mojang.datafixers.DataFixer;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.client.AmbientOcclusionStatus;
import net.minecraft.client.AttackIndicatorStatus;
import net.minecraft.client.BooleanOption;
import net.minecraft.client.CameraType;
import net.minecraft.client.CloudStatus;
import net.minecraft.client.GraphicsStatus;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.NarratorStatus;
import net.minecraft.client.Option;
import net.minecraft.client.ParticleStatus;
import net.minecraft.client.ProgressOption;
import net.minecraft.client.ToggleKeyMapping;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.client.tutorial.TutorialSteps;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundClientInformationPacket;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackCompatibility;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.ChatVisiblity;
import net.minecraft.world.entity.player.PlayerModelPart;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Options {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = new Gson();
    private static final TypeToken<List<String>> RESOURCE_PACK_TYPE = new TypeToken<List<String>>(){};
    private static final Splitter OPTION_SPLITTER = Splitter.on((char)':').limit(2);
    public double sensitivity = 0.5;
    public int renderDistance = -1;
    public float entityDistanceScaling = 1.0f;
    public int framerateLimit = 120;
    public CloudStatus renderClouds = CloudStatus.FANCY;
    public GraphicsStatus graphicsMode = GraphicsStatus.FANCY;
    public AmbientOcclusionStatus ambientOcclusion = AmbientOcclusionStatus.MAX;
    public List<String> resourcePacks = Lists.newArrayList();
    public List<String> incompatibleResourcePacks = Lists.newArrayList();
    public ChatVisiblity chatVisibility = ChatVisiblity.FULL;
    public double chatOpacity = 1.0;
    public double chatLineSpacing = 0.0;
    public double textBackgroundOpacity = 0.5;
    @Nullable
    public String fullscreenVideoModeString;
    public boolean hideServerAddress;
    public boolean advancedItemTooltips;
    public boolean pauseOnLostFocus = true;
    private final Set<PlayerModelPart> modelParts = Sets.newHashSet((Object[])PlayerModelPart.values());
    public HumanoidArm mainHand = HumanoidArm.RIGHT;
    public int overrideWidth;
    public int overrideHeight;
    public boolean heldItemTooltips = true;
    public double chatScale = 1.0;
    public double chatWidth = 1.0;
    public double chatHeightUnfocused = 0.44366195797920227;
    public double chatHeightFocused = 1.0;
    public double chatDelay = 0.0;
    public int mipmapLevels = 4;
    private final Map<SoundSource, Float> sourceVolumes = Maps.newEnumMap(SoundSource.class);
    public boolean useNativeTransport = true;
    public AttackIndicatorStatus attackIndicator = AttackIndicatorStatus.CROSSHAIR;
    public TutorialSteps tutorialStep = TutorialSteps.MOVEMENT;
    public boolean joinedFirstServer = false;
    public int biomeBlendRadius = 2;
    public double mouseWheelSensitivity = 1.0;
    public boolean rawMouseInput = true;
    public int glDebugVerbosity = 1;
    public boolean autoJump = true;
    public boolean autoSuggestions = true;
    public boolean chatColors = true;
    public boolean chatLinks = true;
    public boolean chatLinksPrompt = true;
    public boolean enableVsync = true;
    public boolean entityShadows = true;
    public boolean forceUnicodeFont;
    public boolean invertYMouse;
    public boolean discreteMouseScroll;
    public boolean realmsNotifications = true;
    public boolean reducedDebugInfo;
    public boolean snooperEnabled = true;
    public boolean showSubtitles;
    public boolean backgroundForChatOnly = true;
    public boolean touchscreen;
    public boolean fullscreen;
    public boolean bobView = true;
    public boolean toggleCrouch;
    public boolean toggleSprint;
    public boolean skipMultiplayerWarning;
    public boolean hideMatchedNames = true;
    public final KeyMapping keyUp = new KeyMapping("key.forward", 87, "key.categories.movement");
    public final KeyMapping keyLeft = new KeyMapping("key.left", 65, "key.categories.movement");
    public final KeyMapping keyDown = new KeyMapping("key.back", 83, "key.categories.movement");
    public final KeyMapping keyRight = new KeyMapping("key.right", 68, "key.categories.movement");
    public final KeyMapping keyJump = new KeyMapping("key.jump", 32, "key.categories.movement");
    public final KeyMapping keyShift = new ToggleKeyMapping("key.sneak", 340, "key.categories.movement", () -> this.toggleCrouch);
    public final KeyMapping keySprint = new ToggleKeyMapping("key.sprint", 341, "key.categories.movement", () -> this.toggleSprint);
    public final KeyMapping keyInventory = new KeyMapping("key.inventory", 69, "key.categories.inventory");
    public final KeyMapping keySwapOffhand = new KeyMapping("key.swapOffhand", 70, "key.categories.inventory");
    public final KeyMapping keyDrop = new KeyMapping("key.drop", 81, "key.categories.inventory");
    public final KeyMapping keyUse = new KeyMapping("key.use", InputConstants.Type.MOUSE, 1, "key.categories.gameplay");
    public final KeyMapping keyAttack = new KeyMapping("key.attack", InputConstants.Type.MOUSE, 0, "key.categories.gameplay");
    public final KeyMapping keyPickItem = new KeyMapping("key.pickItem", InputConstants.Type.MOUSE, 2, "key.categories.gameplay");
    public final KeyMapping keyChat = new KeyMapping("key.chat", 84, "key.categories.multiplayer");
    public final KeyMapping keyPlayerList = new KeyMapping("key.playerlist", 258, "key.categories.multiplayer");
    public final KeyMapping keyCommand = new KeyMapping("key.command", 47, "key.categories.multiplayer");
    public final KeyMapping keySocialInteractions = new KeyMapping("key.socialInteractions", 80, "key.categories.multiplayer");
    public final KeyMapping keyScreenshot = new KeyMapping("key.screenshot", 291, "key.categories.misc");
    public final KeyMapping keyTogglePerspective = new KeyMapping("key.togglePerspective", 294, "key.categories.misc");
    public final KeyMapping keySmoothCamera = new KeyMapping("key.smoothCamera", InputConstants.UNKNOWN.getValue(), "key.categories.misc");
    public final KeyMapping keyFullscreen = new KeyMapping("key.fullscreen", 300, "key.categories.misc");
    public final KeyMapping keySpectatorOutlines = new KeyMapping("key.spectatorOutlines", InputConstants.UNKNOWN.getValue(), "key.categories.misc");
    public final KeyMapping keyAdvancements = new KeyMapping("key.advancements", 76, "key.categories.misc");
    public final KeyMapping[] keyHotbarSlots = new KeyMapping[]{new KeyMapping("key.hotbar.1", 49, "key.categories.inventory"), new KeyMapping("key.hotbar.2", 50, "key.categories.inventory"), new KeyMapping("key.hotbar.3", 51, "key.categories.inventory"), new KeyMapping("key.hotbar.4", 52, "key.categories.inventory"), new KeyMapping("key.hotbar.5", 53, "key.categories.inventory"), new KeyMapping("key.hotbar.6", 54, "key.categories.inventory"), new KeyMapping("key.hotbar.7", 55, "key.categories.inventory"), new KeyMapping("key.hotbar.8", 56, "key.categories.inventory"), new KeyMapping("key.hotbar.9", 57, "key.categories.inventory")};
    public final KeyMapping keySaveHotbarActivator = new KeyMapping("key.saveToolbarActivator", 67, "key.categories.creative");
    public final KeyMapping keyLoadHotbarActivator = new KeyMapping("key.loadToolbarActivator", 88, "key.categories.creative");
    public final KeyMapping[] keyMappings = (KeyMapping[])ArrayUtils.addAll((Object[])new KeyMapping[]{this.keyAttack, this.keyUse, this.keyUp, this.keyLeft, this.keyDown, this.keyRight, this.keyJump, this.keyShift, this.keySprint, this.keyDrop, this.keyInventory, this.keyChat, this.keyPlayerList, this.keyPickItem, this.keyCommand, this.keySocialInteractions, this.keyScreenshot, this.keyTogglePerspective, this.keySmoothCamera, this.keyFullscreen, this.keySpectatorOutlines, this.keySwapOffhand, this.keySaveHotbarActivator, this.keyLoadHotbarActivator, this.keyAdvancements}, (Object[])this.keyHotbarSlots);
    protected Minecraft minecraft;
    private final File optionsFile;
    public Difficulty difficulty = Difficulty.NORMAL;
    public boolean hideGui;
    private CameraType cameraType = CameraType.FIRST_PERSON;
    public boolean renderDebug;
    public boolean renderDebugCharts;
    public boolean renderFpsChart;
    public String lastMpIp = "";
    public boolean smoothCamera;
    public double fov = 70.0;
    public float screenEffectScale = 1.0f;
    public float fovEffectScale = 1.0f;
    public double gamma;
    public int guiScale;
    public ParticleStatus particles = ParticleStatus.ALL;
    public NarratorStatus narratorStatus = NarratorStatus.OFF;
    public String languageCode = "en_us";
    public boolean syncWrites;

    public Options(Minecraft minecraft, File file) {
        this.minecraft = minecraft;
        this.optionsFile = new File(file, "options.txt");
        if (minecraft.is64Bit() && Runtime.getRuntime().maxMemory() >= 1000000000L) {
            Option.RENDER_DISTANCE.setMaxValue(32.0f);
        } else {
            Option.RENDER_DISTANCE.setMaxValue(16.0f);
        }
        this.renderDistance = minecraft.is64Bit() ? 12 : 8;
        this.syncWrites = Util.getPlatform() == Util.OS.WINDOWS;
        this.load();
    }

    public float getBackgroundOpacity(float f) {
        return this.backgroundForChatOnly ? f : (float)this.textBackgroundOpacity;
    }

    public int getBackgroundColor(float f) {
        return (int)(this.getBackgroundOpacity(f) * 255.0f) << 24 & 0xFF000000;
    }

    public int getBackgroundColor(int n) {
        return this.backgroundForChatOnly ? n : (int)(this.textBackgroundOpacity * 255.0) << 24 & 0xFF000000;
    }

    public void setKey(KeyMapping keyMapping, InputConstants.Key key) {
        keyMapping.setKey(key);
        this.save();
    }

    public void load() {
        try {
            if (!this.optionsFile.exists()) {
                return;
            }
            this.sourceVolumes.clear();
            CompoundTag compoundTag = new CompoundTag();
            Object object = Files.newReader((File)this.optionsFile, (Charset)Charsets.UTF_8);
            Object object2 = null;
            try {
                ((BufferedReader)object).lines().forEach(string -> {
                    try {
                        Iterator iterator = OPTION_SPLITTER.split((CharSequence)string).iterator();
                        compoundTag.putString((String)iterator.next(), (String)iterator.next());
                    }
                    catch (Exception exception) {
                        LOGGER.warn("Skipping bad option: {}", string);
                    }
                });
            }
            catch (Throwable throwable) {
                object2 = throwable;
                throw throwable;
            }
            finally {
                if (object != null) {
                    if (object2 != null) {
                        try {
                            ((BufferedReader)object).close();
                        }
                        catch (Throwable throwable) {
                            ((Throwable)object2).addSuppressed(throwable);
                        }
                    } else {
                        ((BufferedReader)object).close();
                    }
                }
            }
            object = this.dataFix(compoundTag);
            if (!((CompoundTag)object).contains("graphicsMode") && ((CompoundTag)object).contains("fancyGraphics")) {
                this.graphicsMode = "true".equals(((CompoundTag)object).getString("fancyGraphics")) ? GraphicsStatus.FANCY : GraphicsStatus.FAST;
            }
            for (String string2 : ((CompoundTag)object).getAllKeys()) {
                String string3 = ((CompoundTag)object).getString(string2);
                try {
                    if ("autoJump".equals(string2)) {
                        Option.AUTO_JUMP.set(this, string3);
                    }
                    if ("autoSuggestions".equals(string2)) {
                        Option.AUTO_SUGGESTIONS.set(this, string3);
                    }
                    if ("chatColors".equals(string2)) {
                        Option.CHAT_COLOR.set(this, string3);
                    }
                    if ("chatLinks".equals(string2)) {
                        Option.CHAT_LINKS.set(this, string3);
                    }
                    if ("chatLinksPrompt".equals(string2)) {
                        Option.CHAT_LINKS_PROMPT.set(this, string3);
                    }
                    if ("enableVsync".equals(string2)) {
                        Option.ENABLE_VSYNC.set(this, string3);
                    }
                    if ("entityShadows".equals(string2)) {
                        Option.ENTITY_SHADOWS.set(this, string3);
                    }
                    if ("forceUnicodeFont".equals(string2)) {
                        Option.FORCE_UNICODE_FONT.set(this, string3);
                    }
                    if ("discrete_mouse_scroll".equals(string2)) {
                        Option.DISCRETE_MOUSE_SCROLL.set(this, string3);
                    }
                    if ("invertYMouse".equals(string2)) {
                        Option.INVERT_MOUSE.set(this, string3);
                    }
                    if ("realmsNotifications".equals(string2)) {
                        Option.REALMS_NOTIFICATIONS.set(this, string3);
                    }
                    if ("reducedDebugInfo".equals(string2)) {
                        Option.REDUCED_DEBUG_INFO.set(this, string3);
                    }
                    if ("showSubtitles".equals(string2)) {
                        Option.SHOW_SUBTITLES.set(this, string3);
                    }
                    if ("snooperEnabled".equals(string2)) {
                        Option.SNOOPER_ENABLED.set(this, string3);
                    }
                    if ("touchscreen".equals(string2)) {
                        Option.TOUCHSCREEN.set(this, string3);
                    }
                    if ("fullscreen".equals(string2)) {
                        Option.USE_FULLSCREEN.set(this, string3);
                    }
                    if ("bobView".equals(string2)) {
                        Option.VIEW_BOBBING.set(this, string3);
                    }
                    if ("toggleCrouch".equals(string2)) {
                        this.toggleCrouch = "true".equals(string3);
                    }
                    if ("toggleSprint".equals(string2)) {
                        this.toggleSprint = "true".equals(string3);
                    }
                    if ("mouseSensitivity".equals(string2)) {
                        this.sensitivity = Options.readFloat(string3);
                    }
                    if ("fov".equals(string2)) {
                        this.fov = Options.readFloat(string3) * 40.0f + 70.0f;
                    }
                    if ("screenEffectScale".equals(string2)) {
                        this.screenEffectScale = Options.readFloat(string3);
                    }
                    if ("fovEffectScale".equals(string2)) {
                        this.fovEffectScale = Options.readFloat(string3);
                    }
                    if ("gamma".equals(string2)) {
                        this.gamma = Options.readFloat(string3);
                    }
                    if ("renderDistance".equals(string2)) {
                        this.renderDistance = Integer.parseInt(string3);
                    }
                    if ("entityDistanceScaling".equals(string2)) {
                        this.entityDistanceScaling = Float.parseFloat(string3);
                    }
                    if ("guiScale".equals(string2)) {
                        this.guiScale = Integer.parseInt(string3);
                    }
                    if ("particles".equals(string2)) {
                        this.particles = ParticleStatus.byId(Integer.parseInt(string3));
                    }
                    if ("maxFps".equals(string2)) {
                        this.framerateLimit = Integer.parseInt(string3);
                        if (this.minecraft.getWindow() != null) {
                            this.minecraft.getWindow().setFramerateLimit(this.framerateLimit);
                        }
                    }
                    if ("difficulty".equals(string2)) {
                        this.difficulty = Difficulty.byId(Integer.parseInt(string3));
                    }
                    if ("graphicsMode".equals(string2)) {
                        this.graphicsMode = GraphicsStatus.byId(Integer.parseInt(string3));
                    }
                    if ("tutorialStep".equals(string2)) {
                        this.tutorialStep = TutorialSteps.getByName(string3);
                    }
                    if ("ao".equals(string2)) {
                        this.ambientOcclusion = "true".equals(string3) ? AmbientOcclusionStatus.MAX : ("false".equals(string3) ? AmbientOcclusionStatus.OFF : AmbientOcclusionStatus.byId(Integer.parseInt(string3)));
                    }
                    if ("renderClouds".equals(string2)) {
                        if ("true".equals(string3)) {
                            this.renderClouds = CloudStatus.FANCY;
                        } else if ("false".equals(string3)) {
                            this.renderClouds = CloudStatus.OFF;
                        } else if ("fast".equals(string3)) {
                            this.renderClouds = CloudStatus.FAST;
                        }
                    }
                    if ("attackIndicator".equals(string2)) {
                        this.attackIndicator = AttackIndicatorStatus.byId(Integer.parseInt(string3));
                    }
                    if ("resourcePacks".equals(string2)) {
                        this.resourcePacks = GsonHelper.fromJson(GSON, string3, RESOURCE_PACK_TYPE);
                        if (this.resourcePacks == null) {
                            this.resourcePacks = Lists.newArrayList();
                        }
                    }
                    if ("incompatibleResourcePacks".equals(string2)) {
                        this.incompatibleResourcePacks = GsonHelper.fromJson(GSON, string3, RESOURCE_PACK_TYPE);
                        if (this.incompatibleResourcePacks == null) {
                            this.incompatibleResourcePacks = Lists.newArrayList();
                        }
                    }
                    if ("lastServer".equals(string2)) {
                        this.lastMpIp = string3;
                    }
                    if ("lang".equals(string2)) {
                        this.languageCode = string3;
                    }
                    if ("chatVisibility".equals(string2)) {
                        this.chatVisibility = ChatVisiblity.byId(Integer.parseInt(string3));
                    }
                    if ("chatOpacity".equals(string2)) {
                        this.chatOpacity = Options.readFloat(string3);
                    }
                    if ("chatLineSpacing".equals(string2)) {
                        this.chatLineSpacing = Options.readFloat(string3);
                    }
                    if ("textBackgroundOpacity".equals(string2)) {
                        this.textBackgroundOpacity = Options.readFloat(string3);
                    }
                    if ("backgroundForChatOnly".equals(string2)) {
                        this.backgroundForChatOnly = "true".equals(string3);
                    }
                    if ("fullscreenResolution".equals(string2)) {
                        this.fullscreenVideoModeString = string3;
                    }
                    if ("hideServerAddress".equals(string2)) {
                        this.hideServerAddress = "true".equals(string3);
                    }
                    if ("advancedItemTooltips".equals(string2)) {
                        this.advancedItemTooltips = "true".equals(string3);
                    }
                    if ("pauseOnLostFocus".equals(string2)) {
                        this.pauseOnLostFocus = "true".equals(string3);
                    }
                    if ("overrideHeight".equals(string2)) {
                        this.overrideHeight = Integer.parseInt(string3);
                    }
                    if ("overrideWidth".equals(string2)) {
                        this.overrideWidth = Integer.parseInt(string3);
                    }
                    if ("heldItemTooltips".equals(string2)) {
                        this.heldItemTooltips = "true".equals(string3);
                    }
                    if ("chatHeightFocused".equals(string2)) {
                        this.chatHeightFocused = Options.readFloat(string3);
                    }
                    if ("chatDelay".equals(string2)) {
                        this.chatDelay = Options.readFloat(string3);
                    }
                    if ("chatHeightUnfocused".equals(string2)) {
                        this.chatHeightUnfocused = Options.readFloat(string3);
                    }
                    if ("chatScale".equals(string2)) {
                        this.chatScale = Options.readFloat(string3);
                    }
                    if ("chatWidth".equals(string2)) {
                        this.chatWidth = Options.readFloat(string3);
                    }
                    if ("mipmapLevels".equals(string2)) {
                        this.mipmapLevels = Integer.parseInt(string3);
                    }
                    if ("useNativeTransport".equals(string2)) {
                        this.useNativeTransport = "true".equals(string3);
                    }
                    if ("mainHand".equals(string2)) {
                        HumanoidArm humanoidArm = this.mainHand = "left".equals(string3) ? HumanoidArm.LEFT : HumanoidArm.RIGHT;
                    }
                    if ("narrator".equals(string2)) {
                        this.narratorStatus = NarratorStatus.byId(Integer.parseInt(string3));
                    }
                    if ("biomeBlendRadius".equals(string2)) {
                        this.biomeBlendRadius = Integer.parseInt(string3);
                    }
                    if ("mouseWheelSensitivity".equals(string2)) {
                        this.mouseWheelSensitivity = Options.readFloat(string3);
                    }
                    if ("rawMouseInput".equals(string2)) {
                        this.rawMouseInput = "true".equals(string3);
                    }
                    if ("glDebugVerbosity".equals(string2)) {
                        this.glDebugVerbosity = Integer.parseInt(string3);
                    }
                    if ("skipMultiplayerWarning".equals(string2)) {
                        this.skipMultiplayerWarning = "true".equals(string3);
                    }
                    if ("hideMatchedNames".equals(string2)) {
                        this.hideMatchedNames = "true".equals(string3);
                    }
                    if ("joinedFirstServer".equals(string2)) {
                        this.joinedFirstServer = "true".equals(string3);
                    }
                    if ("syncChunkWrites".equals(string2)) {
                        this.syncWrites = "true".equals(string3);
                    }
                    for (KeyMapping keyMapping : this.keyMappings) {
                        if (!string2.equals("key_" + keyMapping.getName())) continue;
                        keyMapping.setKey(InputConstants.getKey(string3));
                    }
                    for (SoundSource soundSource : SoundSource.values()) {
                        if (!string2.equals("soundCategory_" + soundSource.getName())) continue;
                        this.sourceVolumes.put(soundSource, Float.valueOf(Options.readFloat(string3)));
                    }
                    for (PlayerModelPart playerModelPart : PlayerModelPart.values()) {
                        if (!string2.equals("modelPart_" + playerModelPart.getId())) continue;
                        this.setModelPart(playerModelPart, "true".equals(string3));
                    }
                }
                catch (Exception exception) {
                    LOGGER.warn("Skipping bad option: {}:{}", (Object)string2, (Object)string3);
                }
            }
            KeyMapping.resetMapping();
        }
        catch (Exception exception) {
            LOGGER.error("Failed to load options", (Throwable)exception);
        }
    }

    private CompoundTag dataFix(CompoundTag compoundTag) {
        int n = 0;
        try {
            n = Integer.parseInt(compoundTag.getString("version"));
        }
        catch (RuntimeException runtimeException) {
            // empty catch block
        }
        return NbtUtils.update(this.minecraft.getFixerUpper(), DataFixTypes.OPTIONS, compoundTag, n);
    }

    private static float readFloat(String string) {
        if ("true".equals(string)) {
            return 1.0f;
        }
        if ("false".equals(string)) {
            return 0.0f;
        }
        return Float.parseFloat(string);
    }

    public void save() {
        try {
            try (PrintWriter printWriter = new PrintWriter(new OutputStreamWriter((OutputStream)new FileOutputStream(this.optionsFile), StandardCharsets.UTF_8));){
                printWriter.println("version:" + SharedConstants.getCurrentVersion().getWorldVersion());
                printWriter.println("autoJump:" + Option.AUTO_JUMP.get(this));
                printWriter.println("autoSuggestions:" + Option.AUTO_SUGGESTIONS.get(this));
                printWriter.println("chatColors:" + Option.CHAT_COLOR.get(this));
                printWriter.println("chatLinks:" + Option.CHAT_LINKS.get(this));
                printWriter.println("chatLinksPrompt:" + Option.CHAT_LINKS_PROMPT.get(this));
                printWriter.println("enableVsync:" + Option.ENABLE_VSYNC.get(this));
                printWriter.println("entityShadows:" + Option.ENTITY_SHADOWS.get(this));
                printWriter.println("forceUnicodeFont:" + Option.FORCE_UNICODE_FONT.get(this));
                printWriter.println("discrete_mouse_scroll:" + Option.DISCRETE_MOUSE_SCROLL.get(this));
                printWriter.println("invertYMouse:" + Option.INVERT_MOUSE.get(this));
                printWriter.println("realmsNotifications:" + Option.REALMS_NOTIFICATIONS.get(this));
                printWriter.println("reducedDebugInfo:" + Option.REDUCED_DEBUG_INFO.get(this));
                printWriter.println("snooperEnabled:" + Option.SNOOPER_ENABLED.get(this));
                printWriter.println("showSubtitles:" + Option.SHOW_SUBTITLES.get(this));
                printWriter.println("touchscreen:" + Option.TOUCHSCREEN.get(this));
                printWriter.println("fullscreen:" + Option.USE_FULLSCREEN.get(this));
                printWriter.println("bobView:" + Option.VIEW_BOBBING.get(this));
                printWriter.println("toggleCrouch:" + this.toggleCrouch);
                printWriter.println("toggleSprint:" + this.toggleSprint);
                printWriter.println("mouseSensitivity:" + this.sensitivity);
                printWriter.println("fov:" + (this.fov - 70.0) / 40.0);
                printWriter.println("screenEffectScale:" + this.screenEffectScale);
                printWriter.println("fovEffectScale:" + this.fovEffectScale);
                printWriter.println("gamma:" + this.gamma);
                printWriter.println("renderDistance:" + this.renderDistance);
                printWriter.println("entityDistanceScaling:" + this.entityDistanceScaling);
                printWriter.println("guiScale:" + this.guiScale);
                printWriter.println("particles:" + this.particles.getId());
                printWriter.println("maxFps:" + this.framerateLimit);
                printWriter.println("difficulty:" + this.difficulty.getId());
                printWriter.println("graphicsMode:" + this.graphicsMode.getId());
                printWriter.println("ao:" + this.ambientOcclusion.getId());
                printWriter.println("biomeBlendRadius:" + this.biomeBlendRadius);
                switch (this.renderClouds) {
                    case FANCY: {
                        printWriter.println("renderClouds:true");
                        break;
                    }
                    case FAST: {
                        printWriter.println("renderClouds:fast");
                        break;
                    }
                    case OFF: {
                        printWriter.println("renderClouds:false");
                    }
                }
                printWriter.println("resourcePacks:" + GSON.toJson(this.resourcePacks));
                printWriter.println("incompatibleResourcePacks:" + GSON.toJson(this.incompatibleResourcePacks));
                printWriter.println("lastServer:" + this.lastMpIp);
                printWriter.println("lang:" + this.languageCode);
                printWriter.println("chatVisibility:" + this.chatVisibility.getId());
                printWriter.println("chatOpacity:" + this.chatOpacity);
                printWriter.println("chatLineSpacing:" + this.chatLineSpacing);
                printWriter.println("textBackgroundOpacity:" + this.textBackgroundOpacity);
                printWriter.println("backgroundForChatOnly:" + this.backgroundForChatOnly);
                if (this.minecraft.getWindow().getPreferredFullscreenVideoMode().isPresent()) {
                    printWriter.println("fullscreenResolution:" + this.minecraft.getWindow().getPreferredFullscreenVideoMode().get().write());
                }
                printWriter.println("hideServerAddress:" + this.hideServerAddress);
                printWriter.println("advancedItemTooltips:" + this.advancedItemTooltips);
                printWriter.println("pauseOnLostFocus:" + this.pauseOnLostFocus);
                printWriter.println("overrideWidth:" + this.overrideWidth);
                printWriter.println("overrideHeight:" + this.overrideHeight);
                printWriter.println("heldItemTooltips:" + this.heldItemTooltips);
                printWriter.println("chatHeightFocused:" + this.chatHeightFocused);
                printWriter.println("chatDelay: " + this.chatDelay);
                printWriter.println("chatHeightUnfocused:" + this.chatHeightUnfocused);
                printWriter.println("chatScale:" + this.chatScale);
                printWriter.println("chatWidth:" + this.chatWidth);
                printWriter.println("mipmapLevels:" + this.mipmapLevels);
                printWriter.println("useNativeTransport:" + this.useNativeTransport);
                printWriter.println("mainHand:" + (this.mainHand == HumanoidArm.LEFT ? "left" : "right"));
                printWriter.println("attackIndicator:" + this.attackIndicator.getId());
                printWriter.println("narrator:" + this.narratorStatus.getId());
                printWriter.println("tutorialStep:" + this.tutorialStep.getName());
                printWriter.println("mouseWheelSensitivity:" + this.mouseWheelSensitivity);
                printWriter.println("rawMouseInput:" + Option.RAW_MOUSE_INPUT.get(this));
                printWriter.println("glDebugVerbosity:" + this.glDebugVerbosity);
                printWriter.println("skipMultiplayerWarning:" + this.skipMultiplayerWarning);
                printWriter.println("hideMatchedNames:" + this.hideMatchedNames);
                printWriter.println("joinedFirstServer:" + this.joinedFirstServer);
                printWriter.println("syncChunkWrites:" + this.syncWrites);
                for (KeyMapping keyMapping : this.keyMappings) {
                    printWriter.println("key_" + keyMapping.getName() + ":" + keyMapping.saveString());
                }
                for (SoundSource soundSource : SoundSource.values()) {
                    printWriter.println("soundCategory_" + soundSource.getName() + ":" + this.getSoundSourceVolume(soundSource));
                }
                for (PlayerModelPart playerModelPart : PlayerModelPart.values()) {
                    printWriter.println("modelPart_" + playerModelPart.getId() + ":" + this.modelParts.contains((Object)playerModelPart));
                }
            }
        }
        catch (Exception exception) {
            LOGGER.error("Failed to save options", (Throwable)exception);
        }
        this.broadcastOptions();
    }

    public float getSoundSourceVolume(SoundSource soundSource) {
        if (this.sourceVolumes.containsKey((Object)soundSource)) {
            return this.sourceVolumes.get((Object)soundSource).floatValue();
        }
        return 1.0f;
    }

    public void setSoundCategoryVolume(SoundSource soundSource, float f) {
        this.sourceVolumes.put(soundSource, Float.valueOf(f));
        this.minecraft.getSoundManager().updateSourceVolume(soundSource, f);
    }

    public void broadcastOptions() {
        if (this.minecraft.player != null) {
            int n = 0;
            for (PlayerModelPart playerModelPart : this.modelParts) {
                n |= playerModelPart.getMask();
            }
            this.minecraft.player.connection.send(new ServerboundClientInformationPacket(this.languageCode, this.renderDistance, this.chatVisibility, this.chatColors, n, this.mainHand));
        }
    }

    public Set<PlayerModelPart> getModelParts() {
        return ImmutableSet.copyOf(this.modelParts);
    }

    public void setModelPart(PlayerModelPart playerModelPart, boolean bl) {
        if (bl) {
            this.modelParts.add(playerModelPart);
        } else {
            this.modelParts.remove((Object)playerModelPart);
        }
        this.broadcastOptions();
    }

    public void toggleModelPart(PlayerModelPart playerModelPart) {
        if (this.getModelParts().contains((Object)playerModelPart)) {
            this.modelParts.remove((Object)playerModelPart);
        } else {
            this.modelParts.add(playerModelPart);
        }
        this.broadcastOptions();
    }

    public CloudStatus getCloudsType() {
        if (this.renderDistance >= 4) {
            return this.renderClouds;
        }
        return CloudStatus.OFF;
    }

    public boolean useNativeTransport() {
        return this.useNativeTransport;
    }

    public void loadSelectedResourcePacks(PackRepository packRepository) {
        LinkedHashSet linkedHashSet = Sets.newLinkedHashSet();
        Iterator<String> iterator = this.resourcePacks.iterator();
        while (iterator.hasNext()) {
            String string = iterator.next();
            Pack pack = packRepository.getPack(string);
            if (pack == null && !string.startsWith("file/")) {
                pack = packRepository.getPack("file/" + string);
            }
            if (pack == null) {
                LOGGER.warn("Removed resource pack {} from options because it doesn't seem to exist anymore", (Object)string);
                iterator.remove();
                continue;
            }
            if (!pack.getCompatibility().isCompatible() && !this.incompatibleResourcePacks.contains(string)) {
                LOGGER.warn("Removed resource pack {} from options because it is no longer compatible", (Object)string);
                iterator.remove();
                continue;
            }
            if (pack.getCompatibility().isCompatible() && this.incompatibleResourcePacks.contains(string)) {
                LOGGER.info("Removed resource pack {} from incompatibility list because it's now compatible", (Object)string);
                this.incompatibleResourcePacks.remove(string);
                continue;
            }
            linkedHashSet.add(pack.getId());
        }
        packRepository.setSelected(linkedHashSet);
    }

    public CameraType getCameraType() {
        return this.cameraType;
    }

    public void setCameraType(CameraType cameraType) {
        this.cameraType = cameraType;
    }

}

