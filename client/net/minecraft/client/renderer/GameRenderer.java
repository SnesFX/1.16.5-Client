/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonSyntaxException
 *  javax.annotation.Nullable
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.client.renderer;

import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.CrashReportDetail;
import net.minecraft.ReportedException;
import net.minecraft.Util;
import net.minecraft.client.Camera;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.Options;
import net.minecraft.client.Screenshot;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.MapRenderer;
import net.minecraft.client.gui.components.BossHealthOverlay;
import net.minecraft.client.gui.screens.Overlay;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.ScreenEffectRenderer;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.tags.TagContainer;
import net.minecraft.util.Mth;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GameRenderer
implements ResourceManagerReloadListener,
AutoCloseable {
    private static final ResourceLocation NAUSEA_LOCATION = new ResourceLocation("textures/misc/nausea.png");
    private static final Logger LOGGER = LogManager.getLogger();
    private final Minecraft minecraft;
    private final ResourceManager resourceManager;
    private final Random random = new Random();
    private float renderDistance;
    public final ItemInHandRenderer itemInHandRenderer;
    private final MapRenderer mapRenderer;
    private final RenderBuffers renderBuffers;
    private int tick;
    private float fov;
    private float oldFov;
    private float darkenWorldAmount;
    private float darkenWorldAmountO;
    private boolean renderHand = true;
    private boolean renderBlockOutline = true;
    private long lastScreenshotAttempt;
    private long lastActiveTime = Util.getMillis();
    private final LightTexture lightTexture;
    private final OverlayTexture overlayTexture = new OverlayTexture();
    private boolean panoramicMode;
    private float zoom = 1.0f;
    private float zoomX;
    private float zoomY;
    @Nullable
    private ItemStack itemActivationItem;
    private int itemActivationTicks;
    private float itemActivationOffX;
    private float itemActivationOffY;
    @Nullable
    private PostChain postEffect;
    private static final ResourceLocation[] EFFECTS = new ResourceLocation[]{new ResourceLocation("shaders/post/notch.json"), new ResourceLocation("shaders/post/fxaa.json"), new ResourceLocation("shaders/post/art.json"), new ResourceLocation("shaders/post/bumpy.json"), new ResourceLocation("shaders/post/blobs2.json"), new ResourceLocation("shaders/post/pencil.json"), new ResourceLocation("shaders/post/color_convolve.json"), new ResourceLocation("shaders/post/deconverge.json"), new ResourceLocation("shaders/post/flip.json"), new ResourceLocation("shaders/post/invert.json"), new ResourceLocation("shaders/post/ntsc.json"), new ResourceLocation("shaders/post/outline.json"), new ResourceLocation("shaders/post/phosphor.json"), new ResourceLocation("shaders/post/scan_pincushion.json"), new ResourceLocation("shaders/post/sobel.json"), new ResourceLocation("shaders/post/bits.json"), new ResourceLocation("shaders/post/desaturate.json"), new ResourceLocation("shaders/post/green.json"), new ResourceLocation("shaders/post/blur.json"), new ResourceLocation("shaders/post/wobble.json"), new ResourceLocation("shaders/post/blobs.json"), new ResourceLocation("shaders/post/antialias.json"), new ResourceLocation("shaders/post/creeper.json"), new ResourceLocation("shaders/post/spider.json")};
    public static final int EFFECT_NONE = EFFECTS.length;
    private int effectIndex = EFFECT_NONE;
    private boolean effectActive;
    private final Camera mainCamera = new Camera();

    public GameRenderer(Minecraft minecraft, ResourceManager resourceManager, RenderBuffers renderBuffers) {
        this.minecraft = minecraft;
        this.resourceManager = resourceManager;
        this.itemInHandRenderer = minecraft.getItemInHandRenderer();
        this.mapRenderer = new MapRenderer(minecraft.getTextureManager());
        this.lightTexture = new LightTexture(this, minecraft);
        this.renderBuffers = renderBuffers;
        this.postEffect = null;
    }

    @Override
    public void close() {
        this.lightTexture.close();
        this.mapRenderer.close();
        this.overlayTexture.close();
        this.shutdownEffect();
    }

    public void shutdownEffect() {
        if (this.postEffect != null) {
            this.postEffect.close();
        }
        this.postEffect = null;
        this.effectIndex = EFFECT_NONE;
    }

    public void togglePostEffect() {
        this.effectActive = !this.effectActive;
    }

    public void checkEntityPostEffect(@Nullable Entity entity) {
        if (this.postEffect != null) {
            this.postEffect.close();
        }
        this.postEffect = null;
        if (entity instanceof Creeper) {
            this.loadEffect(new ResourceLocation("shaders/post/creeper.json"));
        } else if (entity instanceof Spider) {
            this.loadEffect(new ResourceLocation("shaders/post/spider.json"));
        } else if (entity instanceof EnderMan) {
            this.loadEffect(new ResourceLocation("shaders/post/invert.json"));
        }
    }

    private void loadEffect(ResourceLocation resourceLocation) {
        if (this.postEffect != null) {
            this.postEffect.close();
        }
        try {
            this.postEffect = new PostChain(this.minecraft.getTextureManager(), this.resourceManager, this.minecraft.getMainRenderTarget(), resourceLocation);
            this.postEffect.resize(this.minecraft.getWindow().getWidth(), this.minecraft.getWindow().getHeight());
            this.effectActive = true;
        }
        catch (IOException iOException) {
            LOGGER.warn("Failed to load shader: {}", (Object)resourceLocation, (Object)iOException);
            this.effectIndex = EFFECT_NONE;
            this.effectActive = false;
        }
        catch (JsonSyntaxException jsonSyntaxException) {
            LOGGER.warn("Failed to parse shader: {}", (Object)resourceLocation, (Object)jsonSyntaxException);
            this.effectIndex = EFFECT_NONE;
            this.effectActive = false;
        }
    }

    @Override
    public void onResourceManagerReload(ResourceManager resourceManager) {
        if (this.postEffect != null) {
            this.postEffect.close();
        }
        this.postEffect = null;
        if (this.effectIndex == EFFECT_NONE) {
            this.checkEntityPostEffect(this.minecraft.getCameraEntity());
        } else {
            this.loadEffect(EFFECTS[this.effectIndex]);
        }
    }

    public void tick() {
        this.tickFov();
        this.lightTexture.tick();
        if (this.minecraft.getCameraEntity() == null) {
            this.minecraft.setCameraEntity(this.minecraft.player);
        }
        this.mainCamera.tick();
        ++this.tick;
        this.itemInHandRenderer.tick();
        this.minecraft.levelRenderer.tickRain(this.mainCamera);
        this.darkenWorldAmountO = this.darkenWorldAmount;
        if (this.minecraft.gui.getBossOverlay().shouldDarkenScreen()) {
            this.darkenWorldAmount += 0.05f;
            if (this.darkenWorldAmount > 1.0f) {
                this.darkenWorldAmount = 1.0f;
            }
        } else if (this.darkenWorldAmount > 0.0f) {
            this.darkenWorldAmount -= 0.0125f;
        }
        if (this.itemActivationTicks > 0) {
            --this.itemActivationTicks;
            if (this.itemActivationTicks == 0) {
                this.itemActivationItem = null;
            }
        }
    }

    @Nullable
    public PostChain currentEffect() {
        return this.postEffect;
    }

    public void resize(int n, int n2) {
        if (this.postEffect != null) {
            this.postEffect.resize(n, n2);
        }
        this.minecraft.levelRenderer.resize(n, n2);
    }

    public void pick(float f) {
        Entity entity2 = this.minecraft.getCameraEntity();
        if (entity2 == null) {
            return;
        }
        if (this.minecraft.level == null) {
            return;
        }
        this.minecraft.getProfiler().push("pick");
        this.minecraft.crosshairPickEntity = null;
        double d = this.minecraft.gameMode.getPickRange();
        this.minecraft.hitResult = entity2.pick(d, f, false);
        Vec3 vec3 = entity2.getEyePosition(f);
        boolean bl = false;
        int n = 3;
        double d2 = d;
        if (this.minecraft.gameMode.hasFarPickRange()) {
            d = d2 = 6.0;
        } else {
            if (d2 > 3.0) {
                bl = true;
            }
            d = d2;
        }
        d2 *= d2;
        if (this.minecraft.hitResult != null) {
            d2 = this.minecraft.hitResult.getLocation().distanceToSqr(vec3);
        }
        Vec3 vec32 = entity2.getViewVector(1.0f);
        Vec3 vec33 = vec3.add(vec32.x * d, vec32.y * d, vec32.z * d);
        float f2 = 1.0f;
        AABB aABB = entity2.getBoundingBox().expandTowards(vec32.scale(d)).inflate(1.0, 1.0, 1.0);
        EntityHitResult entityHitResult = ProjectileUtil.getEntityHitResult(entity2, vec3, vec33, aABB, entity -> !entity.isSpectator() && entity.isPickable(), d2);
        if (entityHitResult != null) {
            Entity entity3 = entityHitResult.getEntity();
            Vec3 vec34 = entityHitResult.getLocation();
            double d3 = vec3.distanceToSqr(vec34);
            if (bl && d3 > 9.0) {
                this.minecraft.hitResult = BlockHitResult.miss(vec34, Direction.getNearest(vec32.x, vec32.y, vec32.z), new BlockPos(vec34));
            } else if (d3 < d2 || this.minecraft.hitResult == null) {
                this.minecraft.hitResult = entityHitResult;
                if (entity3 instanceof LivingEntity || entity3 instanceof ItemFrame) {
                    this.minecraft.crosshairPickEntity = entity3;
                }
            }
        }
        this.minecraft.getProfiler().pop();
    }

    private void tickFov() {
        float f = 1.0f;
        if (this.minecraft.getCameraEntity() instanceof AbstractClientPlayer) {
            AbstractClientPlayer abstractClientPlayer = (AbstractClientPlayer)this.minecraft.getCameraEntity();
            f = abstractClientPlayer.getFieldOfViewModifier();
        }
        this.oldFov = this.fov;
        this.fov += (f - this.fov) * 0.5f;
        if (this.fov > 1.5f) {
            this.fov = 1.5f;
        }
        if (this.fov < 0.1f) {
            this.fov = 0.1f;
        }
    }

    private double getFov(Camera camera, float f, boolean bl) {
        FluidState fluidState;
        if (this.panoramicMode) {
            return 90.0;
        }
        double d = 70.0;
        if (bl) {
            d = this.minecraft.options.fov;
            d *= (double)Mth.lerp(f, this.oldFov, this.fov);
        }
        if (camera.getEntity() instanceof LivingEntity && ((LivingEntity)camera.getEntity()).isDeadOrDying()) {
            float f2 = Math.min((float)((LivingEntity)camera.getEntity()).deathTime + f, 20.0f);
            d /= (double)((1.0f - 500.0f / (f2 + 500.0f)) * 2.0f + 1.0f);
        }
        if (!(fluidState = camera.getFluidInCamera()).isEmpty()) {
            d = d * 60.0 / 70.0;
        }
        return d;
    }

    private void bobHurt(PoseStack poseStack, float f) {
        if (this.minecraft.getCameraEntity() instanceof LivingEntity) {
            float f2;
            LivingEntity livingEntity = (LivingEntity)this.minecraft.getCameraEntity();
            float f3 = (float)livingEntity.hurtTime - f;
            if (livingEntity.isDeadOrDying()) {
                f2 = Math.min((float)livingEntity.deathTime + f, 20.0f);
                poseStack.mulPose(Vector3f.ZP.rotationDegrees(40.0f - 8000.0f / (f2 + 200.0f)));
            }
            if (f3 < 0.0f) {
                return;
            }
            f3 /= (float)livingEntity.hurtDuration;
            f3 = Mth.sin(f3 * f3 * f3 * f3 * 3.1415927f);
            f2 = livingEntity.hurtDir;
            poseStack.mulPose(Vector3f.YP.rotationDegrees(-f2));
            poseStack.mulPose(Vector3f.ZP.rotationDegrees(-f3 * 14.0f));
            poseStack.mulPose(Vector3f.YP.rotationDegrees(f2));
        }
    }

    private void bobView(PoseStack poseStack, float f) {
        if (!(this.minecraft.getCameraEntity() instanceof Player)) {
            return;
        }
        Player player = (Player)this.minecraft.getCameraEntity();
        float f2 = player.walkDist - player.walkDistO;
        float f3 = -(player.walkDist + f2 * f);
        float f4 = Mth.lerp(f, player.oBob, player.bob);
        poseStack.translate(Mth.sin(f3 * 3.1415927f) * f4 * 0.5f, -Math.abs(Mth.cos(f3 * 3.1415927f) * f4), 0.0);
        poseStack.mulPose(Vector3f.ZP.rotationDegrees(Mth.sin(f3 * 3.1415927f) * f4 * 3.0f));
        poseStack.mulPose(Vector3f.XP.rotationDegrees(Math.abs(Mth.cos(f3 * 3.1415927f - 0.2f) * f4) * 5.0f));
    }

    private void renderItemInHand(PoseStack poseStack, Camera camera, float f) {
        boolean bl;
        if (this.panoramicMode) {
            return;
        }
        this.resetProjectionMatrix(this.getProjectionMatrix(camera, f, false));
        PoseStack.Pose pose = poseStack.last();
        pose.pose().setIdentity();
        pose.normal().setIdentity();
        poseStack.pushPose();
        this.bobHurt(poseStack, f);
        if (this.minecraft.options.bobView) {
            this.bobView(poseStack, f);
        }
        boolean bl2 = bl = this.minecraft.getCameraEntity() instanceof LivingEntity && ((LivingEntity)this.minecraft.getCameraEntity()).isSleeping();
        if (this.minecraft.options.getCameraType().isFirstPerson() && !bl && !this.minecraft.options.hideGui && this.minecraft.gameMode.getPlayerMode() != GameType.SPECTATOR) {
            this.lightTexture.turnOnLightLayer();
            this.itemInHandRenderer.renderHandsWithItems(f, poseStack, this.renderBuffers.bufferSource(), this.minecraft.player, this.minecraft.getEntityRenderDispatcher().getPackedLightCoords(this.minecraft.player, f));
            this.lightTexture.turnOffLightLayer();
        }
        poseStack.popPose();
        if (this.minecraft.options.getCameraType().isFirstPerson() && !bl) {
            ScreenEffectRenderer.renderScreenEffect(this.minecraft, poseStack);
            this.bobHurt(poseStack, f);
        }
        if (this.minecraft.options.bobView) {
            this.bobView(poseStack, f);
        }
    }

    public void resetProjectionMatrix(Matrix4f matrix4f) {
        RenderSystem.matrixMode(5889);
        RenderSystem.loadIdentity();
        RenderSystem.multMatrix(matrix4f);
        RenderSystem.matrixMode(5888);
    }

    public Matrix4f getProjectionMatrix(Camera camera, float f, boolean bl) {
        PoseStack poseStack = new PoseStack();
        poseStack.last().pose().setIdentity();
        if (this.zoom != 1.0f) {
            poseStack.translate(this.zoomX, -this.zoomY, 0.0);
            poseStack.scale(this.zoom, this.zoom, 1.0f);
        }
        poseStack.last().pose().multiply(Matrix4f.perspective(this.getFov(camera, f, bl), (float)this.minecraft.getWindow().getWidth() / (float)this.minecraft.getWindow().getHeight(), 0.05f, this.renderDistance * 4.0f));
        return poseStack.last().pose();
    }

    public static float getNightVisionScale(LivingEntity livingEntity, float f) {
        int n = livingEntity.getEffect(MobEffects.NIGHT_VISION).getDuration();
        if (n > 200) {
            return 1.0f;
        }
        return 0.7f + Mth.sin(((float)n - f) * 3.1415927f * 0.2f) * 0.3f;
    }

    public void render(float f, long l, boolean bl) {
        if (this.minecraft.isWindowActive() || !this.minecraft.options.pauseOnLostFocus || this.minecraft.options.touchscreen && this.minecraft.mouseHandler.isRightPressed()) {
            this.lastActiveTime = Util.getMillis();
        } else if (Util.getMillis() - this.lastActiveTime > 500L) {
            this.minecraft.pauseGame(false);
        }
        if (this.minecraft.noRender) {
            return;
        }
        int n = (int)(this.minecraft.mouseHandler.xpos() * (double)this.minecraft.getWindow().getGuiScaledWidth() / (double)this.minecraft.getWindow().getScreenWidth());
        int n2 = (int)(this.minecraft.mouseHandler.ypos() * (double)this.minecraft.getWindow().getGuiScaledHeight() / (double)this.minecraft.getWindow().getScreenHeight());
        RenderSystem.viewport(0, 0, this.minecraft.getWindow().getWidth(), this.minecraft.getWindow().getHeight());
        if (bl && this.minecraft.level != null) {
            this.minecraft.getProfiler().push("level");
            this.renderLevel(f, l, new PoseStack());
            if (this.minecraft.hasSingleplayerServer() && this.lastScreenshotAttempt < Util.getMillis() - 1000L) {
                this.lastScreenshotAttempt = Util.getMillis();
                if (!this.minecraft.getSingleplayerServer().hasWorldScreenshot()) {
                    this.takeAutoScreenshot();
                }
            }
            this.minecraft.levelRenderer.doEntityOutline();
            if (this.postEffect != null && this.effectActive) {
                RenderSystem.disableBlend();
                RenderSystem.disableDepthTest();
                RenderSystem.disableAlphaTest();
                RenderSystem.enableTexture();
                RenderSystem.matrixMode(5890);
                RenderSystem.pushMatrix();
                RenderSystem.loadIdentity();
                this.postEffect.process(f);
                RenderSystem.popMatrix();
            }
            this.minecraft.getMainRenderTarget().bindWrite(true);
        }
        Window window = this.minecraft.getWindow();
        RenderSystem.clear(256, Minecraft.ON_OSX);
        RenderSystem.matrixMode(5889);
        RenderSystem.loadIdentity();
        RenderSystem.ortho(0.0, (double)window.getWidth() / window.getGuiScale(), (double)window.getHeight() / window.getGuiScale(), 0.0, 1000.0, 3000.0);
        RenderSystem.matrixMode(5888);
        RenderSystem.loadIdentity();
        RenderSystem.translatef(0.0f, 0.0f, -2000.0f);
        Lighting.setupFor3DItems();
        PoseStack poseStack = new PoseStack();
        if (bl && this.minecraft.level != null) {
            float f2;
            this.minecraft.getProfiler().popPush("gui");
            if (this.minecraft.player != null && (f2 = Mth.lerp(f, this.minecraft.player.oPortalTime, this.minecraft.player.portalTime)) > 0.0f && this.minecraft.player.hasEffect(MobEffects.CONFUSION) && this.minecraft.options.screenEffectScale < 1.0f) {
                this.renderConfusionOverlay(f2 * (1.0f - this.minecraft.options.screenEffectScale));
            }
            if (!this.minecraft.options.hideGui || this.minecraft.screen != null) {
                RenderSystem.defaultAlphaFunc();
                this.renderItemActivationAnimation(this.minecraft.getWindow().getGuiScaledWidth(), this.minecraft.getWindow().getGuiScaledHeight(), f);
                this.minecraft.gui.render(poseStack, f);
                RenderSystem.clear(256, Minecraft.ON_OSX);
            }
            this.minecraft.getProfiler().pop();
        }
        if (this.minecraft.overlay != null) {
            try {
                this.minecraft.overlay.render(poseStack, n, n2, this.minecraft.getDeltaFrameTime());
            }
            catch (Throwable throwable) {
                CrashReport crashReport = CrashReport.forThrowable(throwable, "Rendering overlay");
                CrashReportCategory crashReportCategory = crashReport.addCategory("Overlay render details");
                crashReportCategory.setDetail("Overlay name", () -> this.minecraft.overlay.getClass().getCanonicalName());
                throw new ReportedException(crashReport);
            }
        }
        if (this.minecraft.screen != null) {
            try {
                this.minecraft.screen.render(poseStack, n, n2, this.minecraft.getDeltaFrameTime());
            }
            catch (Throwable throwable) {
                CrashReport crashReport = CrashReport.forThrowable(throwable, "Rendering screen");
                CrashReportCategory crashReportCategory = crashReport.addCategory("Screen render details");
                crashReportCategory.setDetail("Screen name", () -> this.minecraft.screen.getClass().getCanonicalName());
                crashReportCategory.setDetail("Mouse location", () -> String.format(Locale.ROOT, "Scaled: (%d, %d). Absolute: (%f, %f)", n, n2, this.minecraft.mouseHandler.xpos(), this.minecraft.mouseHandler.ypos()));
                crashReportCategory.setDetail("Screen size", () -> String.format(Locale.ROOT, "Scaled: (%d, %d). Absolute: (%d, %d). Scale factor of %f", this.minecraft.getWindow().getGuiScaledWidth(), this.minecraft.getWindow().getGuiScaledHeight(), this.minecraft.getWindow().getWidth(), this.minecraft.getWindow().getHeight(), this.minecraft.getWindow().getGuiScale()));
                throw new ReportedException(crashReport);
            }
        }
    }

    private void takeAutoScreenshot() {
        if (this.minecraft.levelRenderer.countRenderedChunks() > 10 && this.minecraft.levelRenderer.hasRenderedAllChunks() && !this.minecraft.getSingleplayerServer().hasWorldScreenshot()) {
            NativeImage nativeImage = Screenshot.takeScreenshot(this.minecraft.getWindow().getWidth(), this.minecraft.getWindow().getHeight(), this.minecraft.getMainRenderTarget());
            Util.ioPool().execute(() -> {
                int n = nativeImage.getWidth();
                int n2 = nativeImage.getHeight();
                int n3 = 0;
                int n4 = 0;
                if (n > n2) {
                    n3 = (n - n2) / 2;
                    n = n2;
                } else {
                    n4 = (n2 - n) / 2;
                    n2 = n;
                }
                try {
                    try (NativeImage nativeImage2 = new NativeImage(64, 64, false);){
                        nativeImage.resizeSubRectTo(n3, n4, n, n2, nativeImage2);
                        nativeImage2.writeToFile(this.minecraft.getSingleplayerServer().getWorldScreenshotFile());
                    }
                }
                catch (IOException iOException) {
                    LOGGER.warn("Couldn't save auto screenshot", (Throwable)iOException);
                }
                finally {
                    nativeImage.close();
                }
            });
        }
    }

    private boolean shouldRenderBlockOutline() {
        boolean bl;
        if (!this.renderBlockOutline) {
            return false;
        }
        Entity entity = this.minecraft.getCameraEntity();
        boolean bl2 = bl = entity instanceof Player && !this.minecraft.options.hideGui;
        if (bl && !((Player)entity).abilities.mayBuild) {
            ItemStack itemStack = ((LivingEntity)entity).getMainHandItem();
            HitResult hitResult = this.minecraft.hitResult;
            if (hitResult != null && hitResult.getType() == HitResult.Type.BLOCK) {
                BlockPos blockPos = ((BlockHitResult)hitResult).getBlockPos();
                BlockState blockState = this.minecraft.level.getBlockState(blockPos);
                if (this.minecraft.gameMode.getPlayerMode() == GameType.SPECTATOR) {
                    bl = blockState.getMenuProvider(this.minecraft.level, blockPos) != null;
                } else {
                    BlockInWorld blockInWorld = new BlockInWorld(this.minecraft.level, blockPos, false);
                    bl = !itemStack.isEmpty() && (itemStack.hasAdventureModeBreakTagForBlock(this.minecraft.level.getTagManager(), blockInWorld) || itemStack.hasAdventureModePlaceTagForBlock(this.minecraft.level.getTagManager(), blockInWorld));
                }
            }
        }
        return bl;
    }

    public void renderLevel(float f, long l, PoseStack poseStack) {
        float f2;
        this.lightTexture.updateLightTexture(f);
        if (this.minecraft.getCameraEntity() == null) {
            this.minecraft.setCameraEntity(this.minecraft.player);
        }
        this.pick(f);
        this.minecraft.getProfiler().push("center");
        boolean bl = this.shouldRenderBlockOutline();
        this.minecraft.getProfiler().popPush("camera");
        Camera camera = this.mainCamera;
        this.renderDistance = this.minecraft.options.renderDistance * 16;
        PoseStack poseStack2 = new PoseStack();
        poseStack2.last().pose().multiply(this.getProjectionMatrix(camera, f, true));
        this.bobHurt(poseStack2, f);
        if (this.minecraft.options.bobView) {
            this.bobView(poseStack2, f);
        }
        if ((f2 = Mth.lerp(f, this.minecraft.player.oPortalTime, this.minecraft.player.portalTime) * (this.minecraft.options.screenEffectScale * this.minecraft.options.screenEffectScale)) > 0.0f) {
            int n = this.minecraft.player.hasEffect(MobEffects.CONFUSION) ? 7 : 20;
            float f3 = 5.0f / (f2 * f2 + 5.0f) - f2 * 0.04f;
            f3 *= f3;
            Vector3f vector3f = new Vector3f(0.0f, Mth.SQRT_OF_TWO / 2.0f, Mth.SQRT_OF_TWO / 2.0f);
            poseStack2.mulPose(vector3f.rotationDegrees(((float)this.tick + f) * (float)n));
            poseStack2.scale(1.0f / f3, 1.0f, 1.0f);
            float f4 = -((float)this.tick + f) * (float)n;
            poseStack2.mulPose(vector3f.rotationDegrees(f4));
        }
        Matrix4f matrix4f = poseStack2.last().pose();
        this.resetProjectionMatrix(matrix4f);
        camera.setup(this.minecraft.level, this.minecraft.getCameraEntity() == null ? this.minecraft.player : this.minecraft.getCameraEntity(), !this.minecraft.options.getCameraType().isFirstPerson(), this.minecraft.options.getCameraType().isMirrored(), f);
        poseStack.mulPose(Vector3f.XP.rotationDegrees(camera.getXRot()));
        poseStack.mulPose(Vector3f.YP.rotationDegrees(camera.getYRot() + 180.0f));
        this.minecraft.levelRenderer.renderLevel(poseStack, f, l, bl, camera, this, this.lightTexture, matrix4f);
        this.minecraft.getProfiler().popPush("hand");
        if (this.renderHand) {
            RenderSystem.clear(256, Minecraft.ON_OSX);
            this.renderItemInHand(poseStack, camera, f);
        }
        this.minecraft.getProfiler().pop();
    }

    public void resetData() {
        this.itemActivationItem = null;
        this.mapRenderer.resetData();
        this.mainCamera.reset();
    }

    public MapRenderer getMapRenderer() {
        return this.mapRenderer;
    }

    public void displayItemActivation(ItemStack itemStack) {
        this.itemActivationItem = itemStack;
        this.itemActivationTicks = 40;
        this.itemActivationOffX = this.random.nextFloat() * 2.0f - 1.0f;
        this.itemActivationOffY = this.random.nextFloat() * 2.0f - 1.0f;
    }

    private void renderItemActivationAnimation(int n, int n2, float f) {
        if (this.itemActivationItem == null || this.itemActivationTicks <= 0) {
            return;
        }
        int n3 = 40 - this.itemActivationTicks;
        float f2 = ((float)n3 + f) / 40.0f;
        float f3 = f2 * f2;
        float f4 = f2 * f3;
        float f5 = 10.25f * f4 * f3 - 24.95f * f3 * f3 + 25.5f * f4 - 13.8f * f3 + 4.0f * f2;
        float f6 = f5 * 3.1415927f;
        float f7 = this.itemActivationOffX * (float)(n / 4);
        float f8 = this.itemActivationOffY * (float)(n2 / 4);
        RenderSystem.enableAlphaTest();
        RenderSystem.pushMatrix();
        RenderSystem.pushLightingAttributes();
        RenderSystem.enableDepthTest();
        RenderSystem.disableCull();
        PoseStack poseStack = new PoseStack();
        poseStack.pushPose();
        poseStack.translate((float)(n / 2) + f7 * Mth.abs(Mth.sin(f6 * 2.0f)), (float)(n2 / 2) + f8 * Mth.abs(Mth.sin(f6 * 2.0f)), -50.0);
        float f9 = 50.0f + 175.0f * Mth.sin(f6);
        poseStack.scale(f9, -f9, f9);
        poseStack.mulPose(Vector3f.YP.rotationDegrees(900.0f * Mth.abs(Mth.sin(f6))));
        poseStack.mulPose(Vector3f.XP.rotationDegrees(6.0f * Mth.cos(f2 * 8.0f)));
        poseStack.mulPose(Vector3f.ZP.rotationDegrees(6.0f * Mth.cos(f2 * 8.0f)));
        MultiBufferSource.BufferSource bufferSource = this.renderBuffers.bufferSource();
        this.minecraft.getItemRenderer().renderStatic(this.itemActivationItem, ItemTransforms.TransformType.FIXED, 15728880, OverlayTexture.NO_OVERLAY, poseStack, bufferSource);
        poseStack.popPose();
        bufferSource.endBatch();
        RenderSystem.popAttributes();
        RenderSystem.popMatrix();
        RenderSystem.enableCull();
        RenderSystem.disableDepthTest();
    }

    private void renderConfusionOverlay(float f) {
        int n = this.minecraft.getWindow().getGuiScaledWidth();
        int n2 = this.minecraft.getWindow().getGuiScaledHeight();
        double d = Mth.lerp((double)f, 2.0, 1.0);
        float f2 = 0.2f * f;
        float f3 = 0.4f * f;
        float f4 = 0.2f * f;
        double d2 = (double)n * d;
        double d3 = (double)n2 * d;
        double d4 = ((double)n - d2) / 2.0;
        double d5 = ((double)n2 - d3) / 2.0;
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
        RenderSystem.color4f(f2, f3, f4, 1.0f);
        this.minecraft.getTextureManager().bind(NAUSEA_LOCATION);
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferBuilder = tesselator.getBuilder();
        bufferBuilder.begin(7, DefaultVertexFormat.POSITION_TEX);
        bufferBuilder.vertex(d4, d5 + d3, -90.0).uv(0.0f, 1.0f).endVertex();
        bufferBuilder.vertex(d4 + d2, d5 + d3, -90.0).uv(1.0f, 1.0f).endVertex();
        bufferBuilder.vertex(d4 + d2, d5, -90.0).uv(1.0f, 0.0f).endVertex();
        bufferBuilder.vertex(d4, d5, -90.0).uv(0.0f, 0.0f).endVertex();
        tesselator.end();
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
    }

    public float getDarkenWorldAmount(float f) {
        return Mth.lerp(f, this.darkenWorldAmountO, this.darkenWorldAmount);
    }

    public float getRenderDistance() {
        return this.renderDistance;
    }

    public Camera getMainCamera() {
        return this.mainCamera;
    }

    public LightTexture lightTexture() {
        return this.lightTexture;
    }

    public OverlayTexture overlayTexture() {
        return this.overlayTexture;
    }
}

