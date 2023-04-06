/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 */
package net.minecraft.client.renderer.debug;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.Vec3;

public class StructureRenderer
implements DebugRenderer.SimpleDebugRenderer {
    private final Minecraft minecraft;
    private final Map<DimensionType, Map<String, BoundingBox>> postMainBoxes = Maps.newIdentityHashMap();
    private final Map<DimensionType, Map<String, BoundingBox>> postPiecesBoxes = Maps.newIdentityHashMap();
    private final Map<DimensionType, Map<String, Boolean>> startPiecesMap = Maps.newIdentityHashMap();

    public StructureRenderer(Minecraft minecraft) {
        this.minecraft = minecraft;
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource multiBufferSource, double d, double d2, double d3) {
        Camera camera = this.minecraft.gameRenderer.getMainCamera();
        ClientLevel clientLevel = this.minecraft.level;
        DimensionType dimensionType = clientLevel.dimensionType();
        BlockPos blockPos = new BlockPos(camera.getPosition().x, 0.0, camera.getPosition().z);
        VertexConsumer vertexConsumer = multiBufferSource.getBuffer(RenderType.lines());
        if (this.postMainBoxes.containsKey(dimensionType)) {
            for (BoundingBox object : this.postMainBoxes.get(dimensionType).values()) {
                if (!blockPos.closerThan(object.getCenter(), 500.0)) continue;
                LevelRenderer.renderLineBox(poseStack, vertexConsumer, (double)object.x0 - d, (double)object.y0 - d2, (double)object.z0 - d3, (double)(object.x1 + 1) - d, (double)(object.y1 + 1) - d2, (double)(object.z1 + 1) - d3, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f);
            }
        }
        if (this.postPiecesBoxes.containsKey(dimensionType)) {
            for (Map.Entry entry : this.postPiecesBoxes.get(dimensionType).entrySet()) {
                String string = (String)entry.getKey();
                BoundingBox boundingBox = (BoundingBox)entry.getValue();
                Boolean bl = this.startPiecesMap.get(dimensionType).get(string);
                if (!blockPos.closerThan(boundingBox.getCenter(), 500.0)) continue;
                if (bl.booleanValue()) {
                    LevelRenderer.renderLineBox(poseStack, vertexConsumer, (double)boundingBox.x0 - d, (double)boundingBox.y0 - d2, (double)boundingBox.z0 - d3, (double)(boundingBox.x1 + 1) - d, (double)(boundingBox.y1 + 1) - d2, (double)(boundingBox.z1 + 1) - d3, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f);
                    continue;
                }
                LevelRenderer.renderLineBox(poseStack, vertexConsumer, (double)boundingBox.x0 - d, (double)boundingBox.y0 - d2, (double)boundingBox.z0 - d3, (double)(boundingBox.x1 + 1) - d, (double)(boundingBox.y1 + 1) - d2, (double)(boundingBox.z1 + 1) - d3, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f);
            }
        }
    }

    public void addBoundingBox(BoundingBox boundingBox, List<BoundingBox> list, List<Boolean> list2, DimensionType dimensionType) {
        if (!this.postMainBoxes.containsKey(dimensionType)) {
            this.postMainBoxes.put(dimensionType, Maps.newHashMap());
        }
        if (!this.postPiecesBoxes.containsKey(dimensionType)) {
            this.postPiecesBoxes.put(dimensionType, Maps.newHashMap());
            this.startPiecesMap.put(dimensionType, Maps.newHashMap());
        }
        this.postMainBoxes.get(dimensionType).put(boundingBox.toString(), boundingBox);
        for (int i = 0; i < list.size(); ++i) {
            BoundingBox boundingBox2 = list.get(i);
            Boolean bl = list2.get(i);
            this.postPiecesBoxes.get(dimensionType).put(boundingBox2.toString(), boundingBox2);
            this.startPiecesMap.get(dimensionType).put(boundingBox2.toString(), bl);
        }
    }

    @Override
    public void clear() {
        this.postMainBoxes.clear();
        this.postPiecesBoxes.clear();
        this.startPiecesMap.clear();
    }
}

