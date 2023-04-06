/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Ordering
 *  com.google.common.collect.Sets
 */
package net.minecraft.client.renderer.debug;

import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.phys.AABB;

public class NeighborsUpdateRenderer
implements DebugRenderer.SimpleDebugRenderer {
    private final Minecraft minecraft;
    private final Map<Long, Map<BlockPos, Integer>> lastUpdate = Maps.newTreeMap((Comparator)Ordering.natural().reverse());

    NeighborsUpdateRenderer(Minecraft minecraft) {
        this.minecraft = minecraft;
    }

    public void addUpdate(long l2, BlockPos blockPos) {
        Map map = this.lastUpdate.computeIfAbsent(l2, l -> Maps.newHashMap());
        int n = map.getOrDefault(blockPos, 0);
        map.put(blockPos, n + 1);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource multiBufferSource, double d, double d2, double d3) {
        Comparable<Long> comparable;
        Object object;
        long l = this.minecraft.level.getGameTime();
        int n = 200;
        double d4 = 0.0025;
        HashSet hashSet = Sets.newHashSet();
        HashMap hashMap = Maps.newHashMap();
        VertexConsumer vertexConsumer = multiBufferSource.getBuffer(RenderType.lines());
        Iterator<Map.Entry<Long, Map<BlockPos, Integer>>> iterator = this.lastUpdate.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Long, Map<BlockPos, Integer>> entry = iterator.next();
            comparable = entry.getKey();
            object = entry.getValue();
            long l2 = l - (Long)comparable;
            if (l2 > 200L) {
                iterator.remove();
                continue;
            }
            for (Map.Entry entry2 : object.entrySet()) {
                BlockPos blockPos = (BlockPos)entry2.getKey();
                Integer n2 = (Integer)entry2.getValue();
                if (!hashSet.add(blockPos)) continue;
                AABB aABB = new AABB(BlockPos.ZERO).inflate(0.002).deflate(0.0025 * (double)l2).move(blockPos.getX(), blockPos.getY(), blockPos.getZ()).move(-d, -d2, -d3);
                LevelRenderer.renderLineBox(poseStack, vertexConsumer, aABB.minX, aABB.minY, aABB.minZ, aABB.maxX, aABB.maxY, aABB.maxZ, 1.0f, 1.0f, 1.0f, 1.0f);
                hashMap.put(blockPos, n2);
            }
        }
        for (Map.Entry<Long, Map<BlockPos, Integer>> entry : hashMap.entrySet()) {
            comparable = (BlockPos)((Object)entry.getKey());
            object = (Integer)((Object)entry.getValue());
            DebugRenderer.renderFloatingText(String.valueOf(object), ((Vec3i)comparable).getX(), ((Vec3i)comparable).getY(), ((Vec3i)comparable).getZ(), -1);
        }
    }
}

