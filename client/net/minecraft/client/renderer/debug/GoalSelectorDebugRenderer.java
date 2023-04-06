/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 */
package net.minecraft.client.renderer.debug;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.phys.Vec3;

public class GoalSelectorDebugRenderer
implements DebugRenderer.SimpleDebugRenderer {
    private final Minecraft minecraft;
    private final Map<Integer, List<DebugGoal>> goalSelectors = Maps.newHashMap();

    @Override
    public void clear() {
        this.goalSelectors.clear();
    }

    public void addGoalSelector(int n, List<DebugGoal> list) {
        this.goalSelectors.put(n, list);
    }

    public GoalSelectorDebugRenderer(Minecraft minecraft) {
        this.minecraft = minecraft;
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource multiBufferSource, double d, double d2, double d3) {
        Camera camera = this.minecraft.gameRenderer.getMainCamera();
        RenderSystem.pushMatrix();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableTexture();
        BlockPos blockPos = new BlockPos(camera.getPosition().x, 0.0, camera.getPosition().z);
        this.goalSelectors.forEach((n, list) -> {
            for (int i = 0; i < list.size(); ++i) {
                DebugGoal debugGoal = (DebugGoal)list.get(i);
                if (!blockPos.closerThan(debugGoal.pos, 160.0)) continue;
                double d = (double)debugGoal.pos.getX() + 0.5;
                double d2 = (double)debugGoal.pos.getY() + 2.0 + (double)i * 0.25;
                double d3 = (double)debugGoal.pos.getZ() + 0.5;
                int n2 = debugGoal.isRunning ? -16711936 : -3355444;
                DebugRenderer.renderFloatingText(debugGoal.name, d, d2, d3, n2);
            }
        });
        RenderSystem.enableDepthTest();
        RenderSystem.enableTexture();
        RenderSystem.popMatrix();
    }

    public static class DebugGoal {
        public final BlockPos pos;
        public final int priority;
        public final String name;
        public final boolean isRunning;

        public DebugGoal(BlockPos blockPos, int n, String string, boolean bl) {
            this.pos = blockPos;
            this.priority = n;
            this.name = string;
            this.isRunning = bl;
        }
    }

}

