/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  javax.annotation.Nullable
 */
package net.minecraft.client.renderer.blockentity;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.Camera;
import net.minecraft.client.gui.Font;
import net.minecraft.client.model.ShulkerModel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BannerRenderer;
import net.minecraft.client.renderer.blockentity.BeaconRenderer;
import net.minecraft.client.renderer.blockentity.BedRenderer;
import net.minecraft.client.renderer.blockentity.BellRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.CampfireRenderer;
import net.minecraft.client.renderer.blockentity.ChestRenderer;
import net.minecraft.client.renderer.blockentity.ConduitRenderer;
import net.minecraft.client.renderer.blockentity.EnchantTableRenderer;
import net.minecraft.client.renderer.blockentity.LecternRenderer;
import net.minecraft.client.renderer.blockentity.PistonHeadRenderer;
import net.minecraft.client.renderer.blockentity.ShulkerBoxRenderer;
import net.minecraft.client.renderer.blockentity.SignRenderer;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.minecraft.client.renderer.blockentity.SpawnerRenderer;
import net.minecraft.client.renderer.blockentity.StructureBlockRenderer;
import net.minecraft.client.renderer.blockentity.TheEndGatewayRenderer;
import net.minecraft.client.renderer.blockentity.TheEndPortalRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BannerBlockEntity;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.level.block.entity.BedBlockEntity;
import net.minecraft.world.level.block.entity.BellBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.CampfireBlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.ConduitBlockEntity;
import net.minecraft.world.level.block.entity.EnchantmentTableBlockEntity;
import net.minecraft.world.level.block.entity.EnderChestBlockEntity;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.entity.StructureBlockEntity;
import net.minecraft.world.level.block.entity.TheEndGatewayBlockEntity;
import net.minecraft.world.level.block.entity.TheEndPortalBlockEntity;
import net.minecraft.world.level.block.entity.TrappedChestBlockEntity;
import net.minecraft.world.level.block.piston.PistonMovingBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class BlockEntityRenderDispatcher {
    private final Map<BlockEntityType<?>, BlockEntityRenderer<?>> renderers = Maps.newHashMap();
    public static final BlockEntityRenderDispatcher instance = new BlockEntityRenderDispatcher();
    private final BufferBuilder singleRenderBuffer = new BufferBuilder(256);
    private Font font;
    public TextureManager textureManager;
    public Level level;
    public Camera camera;
    public HitResult cameraHitResult;

    private BlockEntityRenderDispatcher() {
        this.register(BlockEntityType.SIGN, new SignRenderer(this));
        this.register(BlockEntityType.MOB_SPAWNER, new SpawnerRenderer(this));
        this.register(BlockEntityType.PISTON, new PistonHeadRenderer(this));
        this.register(BlockEntityType.CHEST, new ChestRenderer(this));
        this.register(BlockEntityType.ENDER_CHEST, new ChestRenderer(this));
        this.register(BlockEntityType.TRAPPED_CHEST, new ChestRenderer(this));
        this.register(BlockEntityType.ENCHANTING_TABLE, new EnchantTableRenderer(this));
        this.register(BlockEntityType.LECTERN, new LecternRenderer(this));
        this.register(BlockEntityType.END_PORTAL, new TheEndPortalRenderer(this));
        this.register(BlockEntityType.END_GATEWAY, new TheEndGatewayRenderer(this));
        this.register(BlockEntityType.BEACON, new BeaconRenderer(this));
        this.register(BlockEntityType.SKULL, new SkullBlockRenderer(this));
        this.register(BlockEntityType.BANNER, new BannerRenderer(this));
        this.register(BlockEntityType.STRUCTURE_BLOCK, new StructureBlockRenderer(this));
        this.register(BlockEntityType.SHULKER_BOX, new ShulkerBoxRenderer(new ShulkerModel(), this));
        this.register(BlockEntityType.BED, new BedRenderer(this));
        this.register(BlockEntityType.CONDUIT, new ConduitRenderer(this));
        this.register(BlockEntityType.BELL, new BellRenderer(this));
        this.register(BlockEntityType.CAMPFIRE, new CampfireRenderer(this));
    }

    private <E extends BlockEntity> void register(BlockEntityType<E> blockEntityType, BlockEntityRenderer<E> blockEntityRenderer) {
        this.renderers.put(blockEntityType, blockEntityRenderer);
    }

    @Nullable
    public <E extends BlockEntity> BlockEntityRenderer<E> getRenderer(E e) {
        return this.renderers.get(((BlockEntity)e).getType());
    }

    public void prepare(Level level, TextureManager textureManager, Font font, Camera camera, HitResult hitResult) {
        if (this.level != level) {
            this.setLevel(level);
        }
        this.textureManager = textureManager;
        this.camera = camera;
        this.font = font;
        this.cameraHitResult = hitResult;
    }

    public <E extends BlockEntity> void render(E e, float f, PoseStack poseStack, MultiBufferSource multiBufferSource) {
        if (!Vec3.atCenterOf(((BlockEntity)e).getBlockPos()).closerThan(this.camera.getPosition(), ((BlockEntity)e).getViewDistance())) {
            return;
        }
        BlockEntityRenderer<E> blockEntityRenderer = this.getRenderer(e);
        if (blockEntityRenderer == null) {
            return;
        }
        if (!((BlockEntity)e).hasLevel() || !((BlockEntity)e).getType().isValid(((BlockEntity)e).getBlockState().getBlock())) {
            return;
        }
        BlockEntityRenderDispatcher.tryRender(e, () -> BlockEntityRenderDispatcher.setupAndRender(blockEntityRenderer, e, f, poseStack, multiBufferSource));
    }

    private static <T extends BlockEntity> void setupAndRender(BlockEntityRenderer<T> blockEntityRenderer, T t, float f, PoseStack poseStack, MultiBufferSource multiBufferSource) {
        Level level = ((BlockEntity)t).getLevel();
        int n = level != null ? LevelRenderer.getLightColor(level, ((BlockEntity)t).getBlockPos()) : 15728880;
        blockEntityRenderer.render(t, f, poseStack, multiBufferSource, n, OverlayTexture.NO_OVERLAY);
    }

    public <E extends BlockEntity> boolean renderItem(E e, PoseStack poseStack, MultiBufferSource multiBufferSource, int n, int n2) {
        BlockEntityRenderer<E> blockEntityRenderer = this.getRenderer(e);
        if (blockEntityRenderer == null) {
            return true;
        }
        BlockEntityRenderDispatcher.tryRender(e, () -> blockEntityRenderer.render(e, 0.0f, poseStack, multiBufferSource, n, n2));
        return false;
    }

    private static void tryRender(BlockEntity blockEntity, Runnable runnable) {
        try {
            runnable.run();
        }
        catch (Throwable throwable) {
            CrashReport crashReport = CrashReport.forThrowable(throwable, "Rendering Block Entity");
            CrashReportCategory crashReportCategory = crashReport.addCategory("Block Entity Details");
            blockEntity.fillCrashReportCategory(crashReportCategory);
            throw new ReportedException(crashReport);
        }
    }

    public void setLevel(@Nullable Level level) {
        this.level = level;
        if (level == null) {
            this.camera = null;
        }
    }

    public Font getFont() {
        return this.font;
    }
}

