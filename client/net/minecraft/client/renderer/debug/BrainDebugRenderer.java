/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  javax.annotation.Nullable
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.client.renderer.debug;

import com.google.common.collect.Iterables;
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
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.client.renderer.debug.PathfindingRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.network.protocol.game.DebugEntityNameGenerator;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.pathfinder.Path;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BrainDebugRenderer
implements DebugRenderer.SimpleDebugRenderer {
    private static final Logger LOGGER = LogManager.getLogger();
    private final Minecraft minecraft;
    private final Map<BlockPos, PoiInfo> pois = Maps.newHashMap();
    private final Map<UUID, BrainDump> brainDumpsPerEntity = Maps.newHashMap();
    @Nullable
    private UUID lastLookedAtUuid;

    public BrainDebugRenderer(Minecraft minecraft) {
        this.minecraft = minecraft;
    }

    @Override
    public void clear() {
        this.pois.clear();
        this.brainDumpsPerEntity.clear();
        this.lastLookedAtUuid = null;
    }

    public void addPoi(PoiInfo poiInfo) {
        this.pois.put(poiInfo.pos, poiInfo);
    }

    public void removePoi(BlockPos blockPos) {
        this.pois.remove(blockPos);
    }

    public void setFreeTicketCount(BlockPos blockPos, int n) {
        PoiInfo poiInfo = this.pois.get(blockPos);
        if (poiInfo == null) {
            LOGGER.warn("Strange, setFreeTicketCount was called for an unknown POI: " + blockPos);
            return;
        }
        poiInfo.freeTicketCount = n;
    }

    public void addOrUpdateBrainDump(BrainDump brainDump) {
        this.brainDumpsPerEntity.put(brainDump.uuid, brainDump);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource multiBufferSource, double d, double d2, double d3) {
        RenderSystem.pushMatrix();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableTexture();
        this.clearRemovedEntities();
        this.doRender(d, d2, d3);
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
        RenderSystem.popMatrix();
        if (!this.minecraft.player.isSpectator()) {
            this.updateLastLookedAtUuid();
        }
    }

    private void clearRemovedEntities() {
        this.brainDumpsPerEntity.entrySet().removeIf(entry -> {
            Entity entity = this.minecraft.level.getEntity(((BrainDump)entry.getValue()).id);
            return entity == null || entity.removed;
        });
    }

    private void doRender(double d, double d2, double d3) {
        BlockPos blockPos = new BlockPos(d, d2, d3);
        this.brainDumpsPerEntity.values().forEach(brainDump -> {
            if (this.isPlayerCloseEnoughToMob((BrainDump)brainDump)) {
                this.renderBrainInfo((BrainDump)brainDump, d, d2, d3);
            }
        });
        for (BlockPos blockPos3 : this.pois.keySet()) {
            if (!blockPos.closerThan(blockPos3, 30.0)) continue;
            BrainDebugRenderer.highlightPoi(blockPos3);
        }
        this.pois.values().forEach(poiInfo -> {
            if (blockPos.closerThan(poiInfo.pos, 30.0)) {
                this.renderPoiInfo((PoiInfo)poiInfo);
            }
        });
        this.getGhostPois().forEach((blockPos2, list) -> {
            if (blockPos.closerThan((Vec3i)blockPos2, 30.0)) {
                this.renderGhostPoi((BlockPos)blockPos2, (List<String>)list);
            }
        });
    }

    private static void highlightPoi(BlockPos blockPos) {
        float f = 0.05f;
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        DebugRenderer.renderFilledBox(blockPos, 0.05f, 0.2f, 0.2f, 1.0f, 0.3f);
    }

    private void renderGhostPoi(BlockPos blockPos, List<String> list) {
        float f = 0.05f;
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        DebugRenderer.renderFilledBox(blockPos, 0.05f, 0.2f, 0.2f, 1.0f, 0.3f);
        BrainDebugRenderer.renderTextOverPos("" + list, blockPos, 0, -256);
        BrainDebugRenderer.renderTextOverPos("Ghost POI", blockPos, 1, -65536);
    }

    private void renderPoiInfo(PoiInfo poiInfo) {
        int n = 0;
        Set<String> set = this.getTicketHolderNames(poiInfo);
        if (set.size() < 4) {
            BrainDebugRenderer.renderTextOverPoi("Owners: " + set, poiInfo, n, -256);
        } else {
            BrainDebugRenderer.renderTextOverPoi("" + set.size() + " ticket holders", poiInfo, n, -256);
        }
        ++n;
        Set<String> set2 = this.getPotentialTicketHolderNames(poiInfo);
        if (set2.size() < 4) {
            BrainDebugRenderer.renderTextOverPoi("Candidates: " + set2, poiInfo, n, -23296);
        } else {
            BrainDebugRenderer.renderTextOverPoi("" + set2.size() + " potential owners", poiInfo, n, -23296);
        }
        BrainDebugRenderer.renderTextOverPoi("Free tickets: " + poiInfo.freeTicketCount, poiInfo, ++n, -256);
        BrainDebugRenderer.renderTextOverPoi(poiInfo.type, poiInfo, ++n, -1);
    }

    private void renderPath(BrainDump brainDump, double d, double d2, double d3) {
        if (brainDump.path != null) {
            PathfindingRenderer.renderPath(brainDump.path, 0.5f, false, false, d, d2, d3);
        }
    }

    private void renderBrainInfo(BrainDump brainDump, double d, double d2, double d3) {
        boolean bl = this.isMobSelected(brainDump);
        int n = 0;
        BrainDebugRenderer.renderTextOverMob(brainDump.pos, n, brainDump.name, -1, 0.03f);
        ++n;
        if (bl) {
            BrainDebugRenderer.renderTextOverMob(brainDump.pos, n, brainDump.profession + " " + brainDump.xp + " xp", -1, 0.02f);
            ++n;
        }
        if (bl) {
            int n2 = brainDump.health < brainDump.maxHealth ? -23296 : -1;
            BrainDebugRenderer.renderTextOverMob(brainDump.pos, n, "health: " + String.format("%.1f", Float.valueOf(brainDump.health)) + " / " + String.format("%.1f", Float.valueOf(brainDump.maxHealth)), n2, 0.02f);
            ++n;
        }
        if (bl && !brainDump.inventory.equals("")) {
            BrainDebugRenderer.renderTextOverMob(brainDump.pos, n, brainDump.inventory, -98404, 0.02f);
            ++n;
        }
        if (bl) {
            for (String string : brainDump.behaviors) {
                BrainDebugRenderer.renderTextOverMob(brainDump.pos, n, string, -16711681, 0.02f);
                ++n;
            }
        }
        if (bl) {
            for (String string : brainDump.activities) {
                BrainDebugRenderer.renderTextOverMob(brainDump.pos, n, string, -16711936, 0.02f);
                ++n;
            }
        }
        if (brainDump.wantsGolem) {
            BrainDebugRenderer.renderTextOverMob(brainDump.pos, n, "Wants Golem", -23296, 0.02f);
            ++n;
        }
        if (bl) {
            for (String string : brainDump.gossips) {
                if (string.startsWith(brainDump.name)) {
                    BrainDebugRenderer.renderTextOverMob(brainDump.pos, n, string, -1, 0.02f);
                } else {
                    BrainDebugRenderer.renderTextOverMob(brainDump.pos, n, string, -23296, 0.02f);
                }
                ++n;
            }
        }
        if (bl) {
            for (String string : Lists.reverse(brainDump.memories)) {
                BrainDebugRenderer.renderTextOverMob(brainDump.pos, n, string, -3355444, 0.02f);
                ++n;
            }
        }
        if (bl) {
            this.renderPath(brainDump, d, d2, d3);
        }
    }

    private static void renderTextOverPoi(String string, PoiInfo poiInfo, int n, int n2) {
        BlockPos blockPos = poiInfo.pos;
        BrainDebugRenderer.renderTextOverPos(string, blockPos, n, n2);
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

    private Set<String> getTicketHolderNames(PoiInfo poiInfo) {
        return this.getTicketHolders(poiInfo.pos).stream().map(DebugEntityNameGenerator::getEntityName).collect(Collectors.toSet());
    }

    private Set<String> getPotentialTicketHolderNames(PoiInfo poiInfo) {
        return this.getPotentialTicketHolders(poiInfo.pos).stream().map(DebugEntityNameGenerator::getEntityName).collect(Collectors.toSet());
    }

    private boolean isMobSelected(BrainDump brainDump) {
        return Objects.equals(this.lastLookedAtUuid, brainDump.uuid);
    }

    private boolean isPlayerCloseEnoughToMob(BrainDump brainDump) {
        LocalPlayer localPlayer = this.minecraft.player;
        BlockPos blockPos = new BlockPos(localPlayer.getX(), brainDump.pos.y(), localPlayer.getZ());
        BlockPos blockPos2 = new BlockPos(brainDump.pos);
        return blockPos.closerThan(blockPos2, 30.0);
    }

    private Collection<UUID> getTicketHolders(BlockPos blockPos) {
        return this.brainDumpsPerEntity.values().stream().filter(brainDump -> brainDump.hasPoi(blockPos)).map(BrainDump::getUuid).collect(Collectors.toSet());
    }

    private Collection<UUID> getPotentialTicketHolders(BlockPos blockPos) {
        return this.brainDumpsPerEntity.values().stream().filter(brainDump -> brainDump.hasPotentialPoi(blockPos)).map(BrainDump::getUuid).collect(Collectors.toSet());
    }

    private Map<BlockPos, List<String>> getGhostPois() {
        HashMap hashMap = Maps.newHashMap();
        for (BrainDump brainDump : this.brainDumpsPerEntity.values()) {
            for (BlockPos blockPos2 : Iterables.concat(brainDump.pois, brainDump.potentialPois)) {
                if (this.pois.containsKey(blockPos2)) continue;
                hashMap.computeIfAbsent(blockPos2, blockPos -> Lists.newArrayList()).add(brainDump.name);
            }
        }
        return hashMap;
    }

    private void updateLastLookedAtUuid() {
        DebugRenderer.getTargetedEntity(this.minecraft.getCameraEntity(), 8).ifPresent(entity -> {
            this.lastLookedAtUuid = entity.getUUID();
        });
    }

    public static class BrainDump {
        public final UUID uuid;
        public final int id;
        public final String name;
        public final String profession;
        public final int xp;
        public final float health;
        public final float maxHealth;
        public final Position pos;
        public final String inventory;
        public final Path path;
        public final boolean wantsGolem;
        public final List<String> activities = Lists.newArrayList();
        public final List<String> behaviors = Lists.newArrayList();
        public final List<String> memories = Lists.newArrayList();
        public final List<String> gossips = Lists.newArrayList();
        public final Set<BlockPos> pois = Sets.newHashSet();
        public final Set<BlockPos> potentialPois = Sets.newHashSet();

        public BrainDump(UUID uUID, int n, String string, String string2, int n2, float f, float f2, Position position, String string3, @Nullable Path path, boolean bl) {
            this.uuid = uUID;
            this.id = n;
            this.name = string;
            this.profession = string2;
            this.xp = n2;
            this.health = f;
            this.maxHealth = f2;
            this.pos = position;
            this.inventory = string3;
            this.path = path;
            this.wantsGolem = bl;
        }

        private boolean hasPoi(BlockPos blockPos) {
            return this.pois.stream().anyMatch(blockPos::equals);
        }

        private boolean hasPotentialPoi(BlockPos blockPos) {
            return this.potentialPois.contains(blockPos);
        }

        public UUID getUuid() {
            return this.uuid;
        }
    }

    public static class PoiInfo {
        public final BlockPos pos;
        public String type;
        public int freeTicketCount;

        public PoiInfo(BlockPos blockPos, String string, int n) {
            this.pos = blockPos;
            this.type = string;
            this.freeTicketCount = n;
        }
    }

}

