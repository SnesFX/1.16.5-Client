/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  javax.annotation.Nullable
 */
package net.minecraft.client.renderer.debug;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.client.renderer.debug.PathfindingRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.network.protocol.game.DebugEntityNameGenerator;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;

public class BeeDebugRenderer
implements DebugRenderer.SimpleDebugRenderer {
    private final Minecraft minecraft;
    private final Map<BlockPos, HiveInfo> hives = Maps.newHashMap();
    private final Map<UUID, BeeInfo> beeInfosPerEntity = Maps.newHashMap();
    private UUID lastLookedAtUuid;

    public BeeDebugRenderer(Minecraft minecraft) {
        this.minecraft = minecraft;
    }

    @Override
    public void clear() {
        this.hives.clear();
        this.beeInfosPerEntity.clear();
        this.lastLookedAtUuid = null;
    }

    public void addOrUpdateHiveInfo(HiveInfo hiveInfo) {
        this.hives.put(hiveInfo.pos, hiveInfo);
    }

    public void addOrUpdateBeeInfo(BeeInfo beeInfo) {
        this.beeInfosPerEntity.put(beeInfo.uuid, beeInfo);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource multiBufferSource, double d, double d2, double d3) {
        RenderSystem.pushMatrix();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableTexture();
        this.clearRemovedHives();
        this.clearRemovedBees();
        this.doRender();
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
        RenderSystem.popMatrix();
        if (!this.minecraft.player.isSpectator()) {
            this.updateLastLookedAtUuid();
        }
    }

    private void clearRemovedBees() {
        this.beeInfosPerEntity.entrySet().removeIf(entry -> this.minecraft.level.getEntity(((BeeInfo)entry.getValue()).id) == null);
    }

    private void clearRemovedHives() {
        long l = this.minecraft.level.getGameTime() - 20L;
        this.hives.entrySet().removeIf(entry -> ((HiveInfo)entry.getValue()).lastSeen < l);
    }

    private void doRender() {
        BlockPos blockPos = this.getCamera().getBlockPosition();
        this.beeInfosPerEntity.values().forEach(beeInfo -> {
            if (this.isPlayerCloseEnoughToMob((BeeInfo)beeInfo)) {
                this.renderBeeInfo((BeeInfo)beeInfo);
            }
        });
        this.renderFlowerInfos();
        for (BlockPos blockPos3 : this.hives.keySet()) {
            if (!blockPos.closerThan(blockPos3, 30.0)) continue;
            BeeDebugRenderer.highlightHive(blockPos3);
        }
        Map<BlockPos, Set<UUID>> map = this.createHiveBlacklistMap();
        this.hives.values().forEach(hiveInfo -> {
            if (blockPos.closerThan(hiveInfo.pos, 30.0)) {
                Set set = (Set)map.get(hiveInfo.pos);
                this.renderHiveInfo((HiveInfo)hiveInfo, set == null ? Sets.newHashSet() : set);
            }
        });
        this.getGhostHives().forEach((blockPos2, list) -> {
            if (blockPos.closerThan((Vec3i)blockPos2, 30.0)) {
                this.renderGhostHive((BlockPos)blockPos2, (List<String>)list);
            }
        });
    }

    private Map<BlockPos, Set<UUID>> createHiveBlacklistMap() {
        HashMap hashMap = Maps.newHashMap();
        this.beeInfosPerEntity.values().forEach(beeInfo -> beeInfo.blacklistedHives.forEach(blockPos2 -> hashMap.computeIfAbsent(blockPos2, blockPos -> Sets.newHashSet()).add(beeInfo.getUuid())));
        return hashMap;
    }

    private void renderFlowerInfos() {
        HashMap hashMap = Maps.newHashMap();
        this.beeInfosPerEntity.values().stream().filter(BeeInfo::hasFlower).forEach(beeInfo -> hashMap.computeIfAbsent(beeInfo.flowerPos, blockPos -> Sets.newHashSet()).add(beeInfo.getUuid()));
        hashMap.entrySet().forEach(entry -> {
            BlockPos blockPos = (BlockPos)entry.getKey();
            Set set = (Set)entry.getValue();
            Set set2 = set.stream().map(DebugEntityNameGenerator::getEntityName).collect(Collectors.toSet());
            int n = 1;
            BeeDebugRenderer.renderTextOverPos(set2.toString(), blockPos, n++, -256);
            BeeDebugRenderer.renderTextOverPos("Flower", blockPos, n++, -1);
            float f = 0.05f;
            BeeDebugRenderer.renderTransparentFilledBox(blockPos, 0.05f, 0.8f, 0.8f, 0.0f, 0.3f);
        });
    }

    private static String getBeeUuidsAsString(Collection<UUID> collection) {
        if (collection.isEmpty()) {
            return "-";
        }
        if (collection.size() > 3) {
            return "" + collection.size() + " bees";
        }
        return collection.stream().map(DebugEntityNameGenerator::getEntityName).collect(Collectors.toSet()).toString();
    }

    private static void highlightHive(BlockPos blockPos) {
        float f = 0.05f;
        BeeDebugRenderer.renderTransparentFilledBox(blockPos, 0.05f, 0.2f, 0.2f, 1.0f, 0.3f);
    }

    private void renderGhostHive(BlockPos blockPos, List<String> list) {
        float f = 0.05f;
        BeeDebugRenderer.renderTransparentFilledBox(blockPos, 0.05f, 0.2f, 0.2f, 1.0f, 0.3f);
        BeeDebugRenderer.renderTextOverPos("" + list, blockPos, 0, -256);
        BeeDebugRenderer.renderTextOverPos("Ghost Hive", blockPos, 1, -65536);
    }

    private static void renderTransparentFilledBox(BlockPos blockPos, float f, float f2, float f3, float f4, float f5) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        DebugRenderer.renderFilledBox(blockPos, f, f2, f3, f4, f5);
    }

    private void renderHiveInfo(HiveInfo hiveInfo, Collection<UUID> collection) {
        int n = 0;
        if (!collection.isEmpty()) {
            BeeDebugRenderer.renderTextOverHive("Blacklisted by " + BeeDebugRenderer.getBeeUuidsAsString(collection), hiveInfo, n++, -65536);
        }
        BeeDebugRenderer.renderTextOverHive("Out: " + BeeDebugRenderer.getBeeUuidsAsString(this.getHiveMembers(hiveInfo.pos)), hiveInfo, n++, -3355444);
        if (hiveInfo.occupantCount == 0) {
            BeeDebugRenderer.renderTextOverHive("In: -", hiveInfo, n++, -256);
        } else if (hiveInfo.occupantCount == 1) {
            BeeDebugRenderer.renderTextOverHive("In: 1 bee", hiveInfo, n++, -256);
        } else {
            BeeDebugRenderer.renderTextOverHive("In: " + hiveInfo.occupantCount + " bees", hiveInfo, n++, -256);
        }
        BeeDebugRenderer.renderTextOverHive("Honey: " + hiveInfo.honeyLevel, hiveInfo, n++, -23296);
        BeeDebugRenderer.renderTextOverHive(hiveInfo.hiveType + (hiveInfo.sedated ? " (sedated)" : ""), hiveInfo, n++, -1);
    }

    private void renderPath(BeeInfo beeInfo) {
        if (beeInfo.path != null) {
            PathfindingRenderer.renderPath(beeInfo.path, 0.5f, false, false, this.getCamera().getPosition().x(), this.getCamera().getPosition().y(), this.getCamera().getPosition().z());
        }
    }

    private void renderBeeInfo(BeeInfo beeInfo) {
        boolean bl = this.isBeeSelected(beeInfo);
        int n = 0;
        BeeDebugRenderer.renderTextOverMob(beeInfo.pos, n++, beeInfo.toString(), -1, 0.03f);
        if (beeInfo.hivePos == null) {
            BeeDebugRenderer.renderTextOverMob(beeInfo.pos, n++, "No hive", -98404, 0.02f);
        } else {
            BeeDebugRenderer.renderTextOverMob(beeInfo.pos, n++, "Hive: " + this.getPosDescription(beeInfo, beeInfo.hivePos), -256, 0.02f);
        }
        if (beeInfo.flowerPos == null) {
            BeeDebugRenderer.renderTextOverMob(beeInfo.pos, n++, "No flower", -98404, 0.02f);
        } else {
            BeeDebugRenderer.renderTextOverMob(beeInfo.pos, n++, "Flower: " + this.getPosDescription(beeInfo, beeInfo.flowerPos), -256, 0.02f);
        }
        for (String string : beeInfo.goals) {
            BeeDebugRenderer.renderTextOverMob(beeInfo.pos, n++, string, -16711936, 0.02f);
        }
        if (bl) {
            this.renderPath(beeInfo);
        }
        if (beeInfo.travelTicks > 0) {
            int n2 = beeInfo.travelTicks < 600 ? -3355444 : -23296;
            BeeDebugRenderer.renderTextOverMob(beeInfo.pos, n++, "Travelling: " + beeInfo.travelTicks + " ticks", n2, 0.02f);
        }
    }

    private static void renderTextOverHive(String string, HiveInfo hiveInfo, int n, int n2) {
        BlockPos blockPos = hiveInfo.pos;
        BeeDebugRenderer.renderTextOverPos(string, blockPos, n, n2);
    }

    private static void renderTextOverPos(String string, BlockPos blockPos, int n, int n2) {
        double d = 1.3;
        double d2 = 0.2;
        double d3 = (double)blockPos.getX() + 0.5;
        double d4 = (double)blockPos.getY() + 1.3 + (double)n * 0.2;
        double d5 = (double)blockPos.getZ() + 0.5;
        DebugRenderer.renderFloatingText(string, d3, d4, d5, n2, 0.02f, true, 0.0f, true);
    }

    private static void renderTextOverMob(Position position, int n, String string, int n2, float f) {
        double d = 2.4;
        double d2 = 0.25;
        BlockPos blockPos = new BlockPos(position);
        double d3 = (double)blockPos.getX() + 0.5;
        double d4 = position.y() + 2.4 + (double)n * 0.25;
        double d5 = (double)blockPos.getZ() + 0.5;
        float f2 = 0.5f;
        DebugRenderer.renderFloatingText(string, d3, d4, d5, n2, f, false, 0.5f, true);
    }

    private Camera getCamera() {
        return this.minecraft.gameRenderer.getMainCamera();
    }

    private String getPosDescription(BeeInfo beeInfo, BlockPos blockPos) {
        float f = Mth.sqrt(blockPos.distSqr(beeInfo.pos.x(), beeInfo.pos.y(), beeInfo.pos.z(), true));
        double d = (double)Math.round(f * 10.0f) / 10.0;
        return blockPos.toShortString() + " (dist " + d + ")";
    }

    private boolean isBeeSelected(BeeInfo beeInfo) {
        return Objects.equals(this.lastLookedAtUuid, beeInfo.uuid);
    }

    private boolean isPlayerCloseEnoughToMob(BeeInfo beeInfo) {
        LocalPlayer localPlayer = this.minecraft.player;
        BlockPos blockPos = new BlockPos(localPlayer.getX(), beeInfo.pos.y(), localPlayer.getZ());
        BlockPos blockPos2 = new BlockPos(beeInfo.pos);
        return blockPos.closerThan(blockPos2, 30.0);
    }

    private Collection<UUID> getHiveMembers(BlockPos blockPos) {
        return this.beeInfosPerEntity.values().stream().filter(beeInfo -> beeInfo.hasHive(blockPos)).map(BeeInfo::getUuid).collect(Collectors.toSet());
    }

    private Map<BlockPos, List<String>> getGhostHives() {
        HashMap hashMap = Maps.newHashMap();
        for (BeeInfo beeInfo : this.beeInfosPerEntity.values()) {
            if (beeInfo.hivePos == null || this.hives.containsKey(beeInfo.hivePos)) continue;
            hashMap.computeIfAbsent(beeInfo.hivePos, blockPos -> Lists.newArrayList()).add(beeInfo.getName());
        }
        return hashMap;
    }

    private void updateLastLookedAtUuid() {
        DebugRenderer.getTargetedEntity(this.minecraft.getCameraEntity(), 8).ifPresent(entity -> {
            this.lastLookedAtUuid = entity.getUUID();
        });
    }

    public static class BeeInfo {
        public final UUID uuid;
        public final int id;
        public final Position pos;
        @Nullable
        public final Path path;
        @Nullable
        public final BlockPos hivePos;
        @Nullable
        public final BlockPos flowerPos;
        public final int travelTicks;
        public final List<String> goals = Lists.newArrayList();
        public final Set<BlockPos> blacklistedHives = Sets.newHashSet();

        public BeeInfo(UUID uUID, int n, Position position, Path path, BlockPos blockPos, BlockPos blockPos2, int n2) {
            this.uuid = uUID;
            this.id = n;
            this.pos = position;
            this.path = path;
            this.hivePos = blockPos;
            this.flowerPos = blockPos2;
            this.travelTicks = n2;
        }

        public boolean hasHive(BlockPos blockPos) {
            return this.hivePos != null && this.hivePos.equals(blockPos);
        }

        public UUID getUuid() {
            return this.uuid;
        }

        public String getName() {
            return DebugEntityNameGenerator.getEntityName(this.uuid);
        }

        public String toString() {
            return this.getName();
        }

        public boolean hasFlower() {
            return this.flowerPos != null;
        }
    }

    public static class HiveInfo {
        public final BlockPos pos;
        public final String hiveType;
        public final int occupantCount;
        public final int honeyLevel;
        public final boolean sedated;
        public final long lastSeen;

        public HiveInfo(BlockPos blockPos, String string, int n, int n2, boolean bl, long l) {
            this.pos = blockPos;
            this.hiveType = string;
            this.occupantCount = n;
            this.honeyLevel = n2;
            this.sedated = bl;
            this.lastSeen = l;
        }
    }

}

