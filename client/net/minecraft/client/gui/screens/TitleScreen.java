/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.util.concurrent.Runnables
 *  it.unimi.dsi.fastutil.booleans.BooleanConsumer
 *  javax.annotation.Nullable
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.client.gui.screens;

import com.google.common.util.concurrent.Runnables;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.io.IOException;
import java.lang.invoke.LambdaMetafactory;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.screens.AccessibilityOptionsScreen;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.LanguageSelectScreen;
import net.minecraft.client.gui.screens.OptionsScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.WinScreen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.gui.screens.multiplayer.SafetyScreen;
import net.minecraft.client.gui.screens.worldselection.SelectWorldScreen;
import net.minecraft.client.renderer.CubeMap;
import net.minecraft.client.renderer.PanoramaRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.SplashManager;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.client.resources.language.LanguageManager;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.realms.RealmsBridge;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.levelgen.WorldGenSettings;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.LevelSummary;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TitleScreen
extends Screen {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final CubeMap CUBE_MAP = new CubeMap(new ResourceLocation("textures/gui/title/background/panorama"));
    private static final ResourceLocation PANORAMA_OVERLAY = new ResourceLocation("textures/gui/title/background/panorama_overlay.png");
    private static final ResourceLocation ACCESSIBILITY_TEXTURE = new ResourceLocation("textures/gui/accessibility.png");
    private final boolean minceraftEasterEgg;
    @Nullable
    private String splash;
    private Button resetDemoButton;
    private static final ResourceLocation MINECRAFT_LOGO = new ResourceLocation("textures/gui/title/minecraft.png");
    private static final ResourceLocation MINECRAFT_EDITION = new ResourceLocation("textures/gui/title/edition.png");
    private boolean realmsNotificationsInitialized;
    private Screen realmsNotificationsScreen;
    private int copyrightWidth;
    private int copyrightX;
    private final PanoramaRenderer panorama = new PanoramaRenderer(CUBE_MAP);
    private final boolean fading;
    private long fadeInStart;

    public TitleScreen() {
        this(false);
    }

    public TitleScreen(boolean bl) {
        super(new TranslatableComponent("narrator.screen.title"));
        this.fading = bl;
        this.minceraftEasterEgg = (double)new Random().nextFloat() < 1.0E-4;
    }

    private boolean realmsNotificationsEnabled() {
        return this.minecraft.options.realmsNotifications && this.realmsNotificationsScreen != null;
    }

    @Override
    public void tick() {
        if (this.realmsNotificationsEnabled()) {
            this.realmsNotificationsScreen.tick();
        }
    }

    public static CompletableFuture<Void> preloadResources(TextureManager textureManager, Executor executor) {
        return CompletableFuture.allOf(textureManager.preload(MINECRAFT_LOGO, executor), textureManager.preload(MINECRAFT_EDITION, executor), textureManager.preload(PANORAMA_OVERLAY, executor), CUBE_MAP.preload(textureManager, executor));
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    @Override
    protected void init() {
        if (this.splash == null) {
            this.splash = this.minecraft.getSplashManager().getSplash();
        }
        this.copyrightWidth = this.font.width("Copyright Mojang AB. Do not distribute!");
        this.copyrightX = this.width - this.copyrightWidth - 2;
        int n = 24;
        int n2 = this.height / 4 + 48;
        if (this.minecraft.isDemo()) {
            this.createDemoMenuOptions(n2, 24);
        } else {
            this.createNormalMenuOptions(n2, 24);
        }
        this.addButton(new ImageButton(this.width / 2 - 124, n2 + 72 + 12, 20, 20, 0, 106, 20, Button.WIDGETS_LOCATION, 256, 256, button -> this.minecraft.setScreen(new LanguageSelectScreen(this, this.minecraft.options, this.minecraft.getLanguageManager())), new TranslatableComponent("narrator.button.language")));
        this.addButton(new Button(this.width / 2 - 100, n2 + 72 + 12, 98, 20, new TranslatableComponent("menu.options"), button -> this.minecraft.setScreen(new OptionsScreen(this, this.minecraft.options))));
        this.addButton(new Button(this.width / 2 + 2, n2 + 72 + 12, 98, 20, new TranslatableComponent("menu.quit"), button -> this.minecraft.stop()));
        this.addButton(new ImageButton(this.width / 2 + 104, n2 + 72 + 12, 20, 20, 0, 0, 20, ACCESSIBILITY_TEXTURE, 32, 64, button -> this.minecraft.setScreen(new AccessibilityOptionsScreen(this, this.minecraft.options)), new TranslatableComponent("narrator.button.accessibility")));
        this.minecraft.setConnectedToRealms(false);
        if (this.minecraft.options.realmsNotifications && !this.realmsNotificationsInitialized) {
            RealmsBridge realmsBridge = new RealmsBridge();
            this.realmsNotificationsScreen = realmsBridge.getNotificationScreen(this);
            this.realmsNotificationsInitialized = true;
        }
        if (this.realmsNotificationsEnabled()) {
            this.realmsNotificationsScreen.init(this.minecraft, this.width, this.height);
        }
    }

    private void createNormalMenuOptions(int n3, int n4) {
        this.addButton(new Button(this.width / 2 - 100, n3, 200, 20, new TranslatableComponent("menu.singleplayer"), button -> this.minecraft.setScreen(new SelectWorldScreen(this))));
        boolean bl = this.minecraft.allowsMultiplayer();
        Button.OnTooltip onTooltip = bl ? Button.NO_TOOLTIP : (button, poseStack, n, n2) -> {
            if (!button.active) {
                this.renderTooltip(poseStack, this.minecraft.font.split(new TranslatableComponent("title.multiplayer.disabled"), Math.max(this.width / 2 - 43, 170)), n, n2);
            }
        };
        this.addButton(new Button((int)(this.width / 2 - 100), (int)(n3 + n4 * 1), (int)200, (int)20, (Component)new TranslatableComponent((String)"menu.multiplayer"), (Button.OnPress)(Button.OnPress)LambdaMetafactory.metafactory(null, null, null, (Lnet/minecraft/client/gui/components/Button;)V, lambda$createNormalMenuOptions$6(net.minecraft.client.gui.components.Button ), (Lnet/minecraft/client/gui/components/Button;)V)((TitleScreen)this), (Button.OnTooltip)onTooltip)).active = bl;
        this.addButton(new Button((int)(this.width / 2 - 100), (int)(n3 + n4 * 2), (int)200, (int)20, (Component)new TranslatableComponent((String)"menu.online"), (Button.OnPress)(Button.OnPress)LambdaMetafactory.metafactory(null, null, null, (Lnet/minecraft/client/gui/components/Button;)V, lambda$createNormalMenuOptions$7(net.minecraft.client.gui.components.Button ), (Lnet/minecraft/client/gui/components/Button;)V)((TitleScreen)this), (Button.OnTooltip)onTooltip)).active = bl;
    }

    private void createDemoMenuOptions(int n, int n2) {
        boolean bl = this.checkDemoWorldPresence();
        this.addButton(new Button(this.width / 2 - 100, n, 200, 20, new TranslatableComponent("menu.playdemo"), button -> {
            if (bl) {
                this.minecraft.loadLevel("Demo_World");
            } else {
                RegistryAccess.RegistryHolder registryHolder = RegistryAccess.builtin();
                this.minecraft.createLevel("Demo_World", MinecraftServer.DEMO_SETTINGS, registryHolder, WorldGenSettings.demoSettings(registryHolder));
            }
        }));
        this.resetDemoButton = this.addButton(new Button(this.width / 2 - 100, n + n2 * 1, 200, 20, new TranslatableComponent("menu.resetdemo"), button -> {
            LevelStorageSource levelStorageSource = this.minecraft.getLevelSource();
            try {
                try (LevelStorageSource.LevelStorageAccess levelStorageAccess = levelStorageSource.createAccess("Demo_World");){
                    LevelSummary levelSummary = levelStorageAccess.getSummary();
                    if (levelSummary != null) {
                        this.minecraft.setScreen(new ConfirmScreen(this::confirmDemo, new TranslatableComponent("selectWorld.deleteQuestion"), new TranslatableComponent("selectWorld.deleteWarning", levelSummary.getLevelName()), new TranslatableComponent("selectWorld.deleteButton"), CommonComponents.GUI_CANCEL));
                    }
                }
            }
            catch (IOException iOException) {
                SystemToast.onWorldAccessFailure(this.minecraft, "Demo_World");
                LOGGER.warn("Failed to access demo world", (Throwable)iOException);
            }
        }));
        this.resetDemoButton.active = bl;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private boolean checkDemoWorldPresence() {
        try {
            try (LevelStorageSource.LevelStorageAccess levelStorageAccess = this.minecraft.getLevelSource().createAccess("Demo_World");){
                boolean bl = levelStorageAccess.getSummary() != null;
                return bl;
            }
        }
        catch (IOException iOException) {
            SystemToast.onWorldAccessFailure(this.minecraft, "Demo_World");
            LOGGER.warn("Failed to read demo world data", (Throwable)iOException);
            return false;
        }
    }

    private void realmsButtonClicked() {
        RealmsBridge realmsBridge = new RealmsBridge();
        realmsBridge.switchToRealms(this);
    }

    @Override
    public void render(PoseStack poseStack, int n3, int n4, float f) {
        if (this.fadeInStart == 0L && this.fading) {
            this.fadeInStart = Util.getMillis();
        }
        float f2 = this.fading ? (float)(Util.getMillis() - this.fadeInStart) / 1000.0f : 1.0f;
        TitleScreen.fill(poseStack, 0, 0, this.width, this.height, -1);
        this.panorama.render(f, Mth.clamp(f2, 0.0f, 1.0f));
        int n5 = 274;
        int n6 = this.width / 2 - 137;
        int n7 = 30;
        this.minecraft.getTextureManager().bind(PANORAMA_OVERLAY);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, this.fading ? (float)Mth.ceil(Mth.clamp(f2, 0.0f, 1.0f)) : 1.0f);
        TitleScreen.blit(poseStack, 0, 0, this.width, this.height, 0.0f, 0.0f, 16, 128, 16, 128);
        float f3 = this.fading ? Mth.clamp(f2 - 1.0f, 0.0f, 1.0f) : 1.0f;
        int n8 = Mth.ceil(f3 * 255.0f) << 24;
        if ((n8 & 0xFC000000) == 0) {
            return;
        }
        this.minecraft.getTextureManager().bind(MINECRAFT_LOGO);
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, f3);
        if (this.minceraftEasterEgg) {
            this.blitOutlineBlack(n6, 30, (n, n2) -> {
                this.blit(poseStack, n + 0, (int)n2, 0, 0, 99, 44);
                this.blit(poseStack, n + 99, (int)n2, 129, 0, 27, 44);
                this.blit(poseStack, n + 99 + 26, (int)n2, 126, 0, 3, 44);
                this.blit(poseStack, n + 99 + 26 + 3, (int)n2, 99, 0, 26, 44);
                this.blit(poseStack, n + 155, (int)n2, 0, 45, 155, 44);
            });
        } else {
            this.blitOutlineBlack(n6, 30, (n, n2) -> {
                this.blit(poseStack, n + 0, (int)n2, 0, 0, 155, 44);
                this.blit(poseStack, n + 155, (int)n2, 0, 45, 155, 44);
            });
        }
        this.minecraft.getTextureManager().bind(MINECRAFT_EDITION);
        TitleScreen.blit(poseStack, n6 + 88, 67, 0.0f, 0.0f, 98, 14, 128, 16);
        if (this.splash != null) {
            RenderSystem.pushMatrix();
            RenderSystem.translatef(this.width / 2 + 90, 70.0f, 0.0f);
            RenderSystem.rotatef(-20.0f, 0.0f, 0.0f, 1.0f);
            float f4 = 1.8f - Mth.abs(Mth.sin((float)(Util.getMillis() % 1000L) / 1000.0f * 6.2831855f) * 0.1f);
            f4 = f4 * 100.0f / (float)(this.font.width(this.splash) + 32);
            RenderSystem.scalef(f4, f4, f4);
            TitleScreen.drawCenteredString(poseStack, this.font, this.splash, 0, -8, 0xFFFF00 | n8);
            RenderSystem.popMatrix();
        }
        String string = "Minecraft " + SharedConstants.getCurrentVersion().getName();
        string = this.minecraft.isDemo() ? string + " Demo" : string + ("release".equalsIgnoreCase(this.minecraft.getVersionType()) ? "" : "/" + this.minecraft.getVersionType());
        if (this.minecraft.isProbablyModded()) {
            string = string + I18n.get("menu.modded", new Object[0]);
        }
        TitleScreen.drawString(poseStack, this.font, string, 2, this.height - 10, 0xFFFFFF | n8);
        TitleScreen.drawString(poseStack, this.font, "Copyright Mojang AB. Do not distribute!", this.copyrightX, this.height - 10, 0xFFFFFF | n8);
        if (n3 > this.copyrightX && n3 < this.copyrightX + this.copyrightWidth && n4 > this.height - 10 && n4 < this.height) {
            TitleScreen.fill(poseStack, this.copyrightX, this.height - 1, this.copyrightX + this.copyrightWidth, this.height, 0xFFFFFF | n8);
        }
        for (AbstractWidget abstractWidget : this.buttons) {
            abstractWidget.setAlpha(f3);
        }
        super.render(poseStack, n3, n4, f);
        if (this.realmsNotificationsEnabled() && f3 >= 1.0f) {
            this.realmsNotificationsScreen.render(poseStack, n3, n4, f);
        }
    }

    @Override
    public boolean mouseClicked(double d, double d2, int n) {
        if (super.mouseClicked(d, d2, n)) {
            return true;
        }
        if (this.realmsNotificationsEnabled() && this.realmsNotificationsScreen.mouseClicked(d, d2, n)) {
            return true;
        }
        if (d > (double)this.copyrightX && d < (double)(this.copyrightX + this.copyrightWidth) && d2 > (double)(this.height - 10) && d2 < (double)this.height) {
            this.minecraft.setScreen(new WinScreen(false, Runnables.doNothing()));
        }
        return false;
    }

    @Override
    public void removed() {
        if (this.realmsNotificationsScreen != null) {
            this.realmsNotificationsScreen.removed();
        }
    }

    private void confirmDemo(boolean bl) {
        if (bl) {
            try {
                try (LevelStorageSource.LevelStorageAccess levelStorageAccess = this.minecraft.getLevelSource().createAccess("Demo_World");){
                    levelStorageAccess.deleteLevel();
                }
            }
            catch (IOException iOException) {
                SystemToast.onWorldDeleteFailure(this.minecraft, "Demo_World");
                LOGGER.warn("Failed to delete demo world", (Throwable)iOException);
            }
        }
        this.minecraft.setScreen(this);
    }

    private /* synthetic */ void lambda$createNormalMenuOptions$7(Button button) {
        this.realmsButtonClicked();
    }

    private /* synthetic */ void lambda$createNormalMenuOptions$6(Button button) {
        Screen screen = this.minecraft.options.skipMultiplayerWarning ? new JoinMultiplayerScreen(this) : new SafetyScreen(this);
        this.minecraft.setScreen(screen);
    }
}

