/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.client.renderer.debug;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;
import com.mojang.math.Transformation;
import java.util.Optional;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.debug.BeeDebugRenderer;
import net.minecraft.client.renderer.debug.BrainDebugRenderer;
import net.minecraft.client.renderer.debug.CaveDebugRenderer;
import net.minecraft.client.renderer.debug.ChunkBorderRenderer;
import net.minecraft.client.renderer.debug.ChunkDebugRenderer;
import net.minecraft.client.renderer.debug.CollisionBoxRenderer;
import net.minecraft.client.renderer.debug.GameTestDebugRenderer;
import net.minecraft.client.renderer.debug.GoalSelectorDebugRenderer;
import net.minecraft.client.renderer.debug.HeightMapRenderer;
import net.minecraft.client.renderer.debug.LightDebugRenderer;
import net.minecraft.client.renderer.debug.NeighborsUpdateRenderer;
import net.minecraft.client.renderer.debug.PathfindingRenderer;
import net.minecraft.client.renderer.debug.RaidDebugRenderer;
import net.minecraft.client.renderer.debug.SolidFaceRenderer;
import net.minecraft.client.renderer.debug.StructureRenderer;
import net.minecraft.client.renderer.debug.VillageSectionsDebugRenderer;
import net.minecraft.client.renderer.debug.WaterDebugRenderer;
import net.minecraft.client.renderer.debug.WorldGenAttemptRenderer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

public class DebugRenderer {
    public final PathfindingRenderer pathfindingRenderer = new PathfindingRenderer();
    public final SimpleDebugRenderer waterDebugRenderer;
    public final SimpleDebugRenderer chunkBorderRenderer;
    public final SimpleDebugRenderer heightMapRenderer;
    public final SimpleDebugRenderer collisionBoxRenderer;
    public final SimpleDebugRenderer neighborsUpdateRenderer;
    public final CaveDebugRenderer caveRenderer;
    public final StructureRenderer structureRenderer;
    public final SimpleDebugRenderer lightDebugRenderer;
    public final SimpleDebugRenderer worldGenAttemptRenderer;
    public final SimpleDebugRenderer solidFaceRenderer;
    public final SimpleDebugRenderer chunkRenderer;
    public final BrainDebugRenderer brainDebugRenderer;
    public final VillageSectionsDebugRenderer villageSectionsDebugRenderer;
    public final BeeDebugRenderer beeDebugRenderer;
    public final RaidDebugRenderer raidDebugRenderer;
    public final GoalSelectorDebugRenderer goalSelectorRenderer;
    public final GameTestDebugRenderer gameTestDebugRenderer;
    private boolean renderChunkborder;

    public DebugRenderer(Minecraft minecraft) {
        this.waterDebugRenderer = new WaterDebugRenderer(minecraft);
        this.chunkBorderRenderer = new ChunkBorderRenderer(minecraft);
        this.heightMapRenderer = new HeightMapRenderer(minecraft);
        this.collisionBoxRenderer = new CollisionBoxRenderer(minecraft);
        this.neighborsUpdateRenderer = new NeighborsUpdateRenderer(minecraft);
        this.caveRenderer = new CaveDebugRenderer();
        this.structureRenderer = new StructureRenderer(minecraft);
        this.lightDebugRenderer = new LightDebugRenderer(minecraft);
        this.worldGenAttemptRenderer = new WorldGenAttemptRenderer();
        this.solidFaceRenderer = new SolidFaceRenderer(minecraft);
        this.chunkRenderer = new ChunkDebugRenderer(minecraft);
        this.brainDebugRenderer = new BrainDebugRenderer(minecraft);
        this.villageSectionsDebugRenderer = new VillageSectionsDebugRenderer();
        this.beeDebugRenderer = new BeeDebugRenderer(minecraft);
        this.raidDebugRenderer = new RaidDebugRenderer(minecraft);
        this.goalSelectorRenderer = new GoalSelectorDebugRenderer(minecraft);
        this.gameTestDebugRenderer = new GameTestDebugRenderer();
    }

    public void clear() {
        this.pathfindingRenderer.clear();
        this.waterDebugRenderer.clear();
        this.chunkBorderRenderer.clear();
        this.heightMapRenderer.clear();
        this.collisionBoxRenderer.clear();
        this.neighborsUpdateRenderer.clear();
        this.caveRenderer.clear();
        this.structureRenderer.clear();
        this.lightDebugRenderer.clear();
        this.worldGenAttemptRenderer.clear();
        this.solidFaceRenderer.clear();
        this.chunkRenderer.clear();
        this.brainDebugRenderer.clear();
        this.villageSectionsDebugRenderer.clear();
        this.beeDebugRenderer.clear();
        this.raidDebugRenderer.clear();
        this.goalSelectorRenderer.clear();
        this.gameTestDebugRenderer.clear();
    }

    public boolean switchRenderChunkborder() {
        this.renderChunkborder = !this.renderChunkborder;
        return this.renderChunkborder;
    }

    public void render(PoseStack poseStack, MultiBufferSource.BufferSource bufferSource, double d, double d2, double d3) {
        if (this.renderChunkborder && !Minecraft.getInstance().showOnlyReducedInfo()) {
            this.chunkBorderRenderer.render(poseStack, bufferSource, d, d2, d3);
        }
        this.gameTestDebugRenderer.render(poseStack, bufferSource, d, d2, d3);
    }

    public static Optional<Entity> getTargetedEntity(@Nullable Entity entity2, int n) {
        int n2;
        AABB aABB;
        Vec3 vec3;
        Predicate<Entity> predicate;
        Vec3 vec32;
        if (entity2 == null) {
            return Optional.empty();
        }
        Vec3 vec33 = entity2.getEyePosition(1.0f);
        EntityHitResult entityHitResult = ProjectileUtil.getEntityHitResult(entity2, vec33, vec32 = vec33.add(vec3 = entity2.getViewVector(1.0f).scale(n)), aABB = entity2.getBoundingBox().expandTowards(vec3).inflate(1.0), predicate = entity -> !entity.isSpectator() && entity.isPickable(), n2 = n * n);
        if (entityHitResult == null) {
            return Optional.empty();
        }
        if (vec33.distanceToSqr(entityHitResult.getLocation()) > (double)n2) {
            return Optional.empty();
        }
        return Optional.of(entityHitResult.getEntity());
    }

    public static void renderFilledBox(BlockPos blockPos, BlockPos blockPos2, float f, float f2, float f3, float f4) {
        Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
        if (!camera.isInitialized()) {
            return;
        }
        Vec3 vec3 = camera.getPosition().reverse();
        AABB aABB = new AABB(blockPos, blockPos2).move(vec3);
        DebugRenderer.renderFilledBox(aABB, f, f2, f3, f4);
    }

    public static void renderFilledBox(BlockPos blockPos, float f, float f2, float f3, float f4, float f5) {
        Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
        if (!camera.isInitialized()) {
            return;
        }
        Vec3 vec3 = camera.getPosition().reverse();
        AABB aABB = new AABB(blockPos).move(vec3).inflate(f);
        DebugRenderer.renderFilledBox(aABB, f2, f3, f4, f5);
    }

    public static void renderFilledBox(AABB aABB, float f, float f2, float f3, float f4) {
        DebugRenderer.renderFilledBox(aABB.minX, aABB.minY, aABB.minZ, aABB.maxX, aABB.maxY, aABB.maxZ, f, f2, f3, f4);
    }

    public static void renderFilledBox(double d, double d2, double d3, double d4, double d5, double d6, float f, float f2, float f3, float f4) {
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferBuilder = tesselator.getBuilder();
        bufferBuilder.begin(5, DefaultVertexFormat.POSITION_COLOR);
        LevelRenderer.addChainedFilledBoxVertices(bufferBuilder, d, d2, d3, d4, d5, d6, f, f2, f3, f4);
        tesselator.end();
    }

    public static void renderFloatingText(String string, int n, int n2, int n3, int n4) {
        DebugRenderer.renderFloatingText(string, (double)n + 0.5, (double)n2 + 0.5, (double)n3 + 0.5, n4);
    }

    public static void renderFloatingText(String string, double d, double d2, double d3, int n) {
        DebugRenderer.renderFloatingText(string, d, d2, d3, n, 0.02f);
    }

    public static void renderFloatingText(String string, double d, double d2, double d3, int n, float f) {
        DebugRenderer.renderFloatingText(string, d, d2, d3, n, f, true, 0.0f, false);
    }

    public static void renderFloatingText(String string, double d, double d2, double d3, int n, float f, boolean bl, float f2, boolean bl2) {
        Minecraft minecraft = Minecraft.getInstance();
        Camera camera = minecraft.gameRenderer.getMainCamera();
        if (!camera.isInitialized() || minecraft.getEntityRenderDispatcher().options == null) {
            return;
        }
        Font font = minecraft.font;
        double d4 = camera.getPosition().x;
        double d5 = camera.getPosition().y;
        double d6 = camera.getPosition().z;
        RenderSystem.pushMatrix();
        RenderSystem.translatef((float)(d - d4), (float)(d2 - d5) + 0.07f, (float)(d3 - d6));
        RenderSystem.normal3f(0.0f, 1.0f, 0.0f);
        RenderSystem.multMatrix(new Matrix4f(camera.rotation()));
        RenderSystem.scalef(f, -f, f);
        RenderSystem.enableTexture();
        if (bl2) {
            RenderSystem.disableDepthTest();
        } else {
            RenderSystem.enableDepthTest();
        }
        RenderSystem.depthMask(true);
        RenderSystem.scalef(-1.0f, 1.0f, 1.0f);
        float f3 = bl ? (float)(-font.width(string)) / 2.0f : 0.0f;
        RenderSystem.enableAlphaTest();
        MultiBufferSource.BufferSource bufferSource = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
        font.drawInBatch(string, f3 -= f2 / f, 0.0f, n, false, Transformation.identity().getMatrix(), (MultiBufferSource)bufferSource, bl2, 0, 15728880);
        bufferSource.endBatch();
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.enableDepthTest();
        RenderSystem.popMatrix();
    }

    public static interface SimpleDebugRenderer {
        public void render(PoseStack var1, MultiBufferSource var2, double var3, double var5, double var7);

        default public void clear() {
        }
    }

}

