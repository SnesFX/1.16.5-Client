/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.DynamicOps
 *  javax.annotation.Nullable
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.world.level.saveddata.maps;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundMapItemDataPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.maps.MapBanner;
import net.minecraft.world.level.saveddata.maps.MapDecoration;
import net.minecraft.world.level.saveddata.maps.MapFrame;
import net.minecraft.world.level.storage.LevelData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MapItemSavedData
extends SavedData {
    private static final Logger LOGGER = LogManager.getLogger();
    public int x;
    public int z;
    public ResourceKey<Level> dimension;
    public boolean trackingPosition;
    public boolean unlimitedTracking;
    public byte scale;
    public byte[] colors = new byte[16384];
    public boolean locked;
    public final List<HoldingPlayer> carriedBy = Lists.newArrayList();
    private final Map<Player, HoldingPlayer> carriedByPlayers = Maps.newHashMap();
    private final Map<String, MapBanner> bannerMarkers = Maps.newHashMap();
    public final Map<String, MapDecoration> decorations = Maps.newLinkedHashMap();
    private final Map<String, MapFrame> frameMarkers = Maps.newHashMap();

    public MapItemSavedData(String string) {
        super(string);
    }

    public void setProperties(int n, int n2, int n3, boolean bl, boolean bl2, ResourceKey<Level> resourceKey) {
        this.scale = (byte)n3;
        this.setOrigin(n, n2, this.scale);
        this.dimension = resourceKey;
        this.trackingPosition = bl;
        this.unlimitedTracking = bl2;
        this.setDirty();
    }

    public void setOrigin(double d, double d2, int n) {
        int n2 = 128 * (1 << n);
        int n3 = Mth.floor((d + 64.0) / (double)n2);
        int n4 = Mth.floor((d2 + 64.0) / (double)n2);
        this.x = n3 * n2 + n2 / 2 - 64;
        this.z = n4 * n2 + n2 / 2 - 64;
    }

    @Override
    public void load(CompoundTag compoundTag) {
        this.dimension = (ResourceKey)DimensionType.parseLegacy(new Dynamic((DynamicOps)NbtOps.INSTANCE, (Object)compoundTag.get("dimension"))).resultOrPartial(((Logger)LOGGER)::error).orElseThrow(() -> new IllegalArgumentException("Invalid map dimension: " + compoundTag.get("dimension")));
        this.x = compoundTag.getInt("xCenter");
        this.z = compoundTag.getInt("zCenter");
        this.scale = (byte)Mth.clamp(compoundTag.getByte("scale"), 0, 4);
        this.trackingPosition = !compoundTag.contains("trackingPosition", 1) || compoundTag.getBoolean("trackingPosition");
        this.unlimitedTracking = compoundTag.getBoolean("unlimitedTracking");
        this.locked = compoundTag.getBoolean("locked");
        this.colors = compoundTag.getByteArray("colors");
        if (this.colors.length != 16384) {
            this.colors = new byte[16384];
        }
        ListTag listTag = compoundTag.getList("banners", 10);
        for (int i = 0; i < listTag.size(); ++i) {
            MapBanner mapBanner = MapBanner.load(listTag.getCompound(i));
            this.bannerMarkers.put(mapBanner.getId(), mapBanner);
            this.addDecoration(mapBanner.getDecoration(), null, mapBanner.getId(), mapBanner.getPos().getX(), mapBanner.getPos().getZ(), 180.0, mapBanner.getName());
        }
        ListTag listTag2 = compoundTag.getList("frames", 10);
        for (int i = 0; i < listTag2.size(); ++i) {
            MapFrame mapFrame = MapFrame.load(listTag2.getCompound(i));
            this.frameMarkers.put(mapFrame.getId(), mapFrame);
            this.addDecoration(MapDecoration.Type.FRAME, null, "frame-" + mapFrame.getEntityId(), mapFrame.getPos().getX(), mapFrame.getPos().getZ(), mapFrame.getRotation(), null);
        }
    }

    @Override
    public CompoundTag save(CompoundTag compoundTag) {
        ResourceLocation.CODEC.encodeStart((DynamicOps)NbtOps.INSTANCE, (Object)this.dimension.location()).resultOrPartial(((Logger)LOGGER)::error).ifPresent(tag -> compoundTag.put("dimension", (Tag)tag));
        compoundTag.putInt("xCenter", this.x);
        compoundTag.putInt("zCenter", this.z);
        compoundTag.putByte("scale", this.scale);
        compoundTag.putByteArray("colors", this.colors);
        compoundTag.putBoolean("trackingPosition", this.trackingPosition);
        compoundTag.putBoolean("unlimitedTracking", this.unlimitedTracking);
        compoundTag.putBoolean("locked", this.locked);
        ListTag listTag = new ListTag();
        for (MapBanner object : this.bannerMarkers.values()) {
            listTag.add(object.save());
        }
        compoundTag.put("banners", listTag);
        ListTag listTag2 = new ListTag();
        for (MapFrame mapFrame : this.frameMarkers.values()) {
            listTag2.add(mapFrame.save());
        }
        compoundTag.put("frames", listTag2);
        return compoundTag;
    }

    public void lockData(MapItemSavedData mapItemSavedData) {
        this.locked = true;
        this.x = mapItemSavedData.x;
        this.z = mapItemSavedData.z;
        this.bannerMarkers.putAll(mapItemSavedData.bannerMarkers);
        this.decorations.putAll(mapItemSavedData.decorations);
        System.arraycopy(mapItemSavedData.colors, 0, this.colors, 0, mapItemSavedData.colors.length);
        this.setDirty();
    }

    public void tickCarriedBy(Player player, ItemStack itemStack) {
        Object object;
        Object object2;
        Object object3;
        CompoundTag compoundTag;
        if (!this.carriedByPlayers.containsKey(player)) {
            HoldingPlayer holdingPlayer = new HoldingPlayer(player);
            this.carriedByPlayers.put(player, holdingPlayer);
            this.carriedBy.add(holdingPlayer);
        }
        if (!player.inventory.contains(itemStack)) {
            this.decorations.remove(player.getName().getString());
        }
        for (int i = 0; i < this.carriedBy.size(); ++i) {
            object3 = this.carriedBy.get(i);
            object = ((HoldingPlayer)object3).player.getName().getString();
            if (object3.player.removed || !object3.player.inventory.contains(itemStack) && !itemStack.isFramed()) {
                this.carriedByPlayers.remove(((HoldingPlayer)object3).player);
                this.carriedBy.remove(object3);
                this.decorations.remove(object);
                continue;
            }
            if (itemStack.isFramed() || object3.player.level.dimension() != this.dimension || !this.trackingPosition) continue;
            this.addDecoration(MapDecoration.Type.PLAYER, object3.player.level, (String)object, ((HoldingPlayer)object3).player.getX(), ((HoldingPlayer)object3).player.getZ(), object3.player.yRot, null);
        }
        if (itemStack.isFramed() && this.trackingPosition) {
            ItemFrame itemFrame = itemStack.getFrame();
            object3 = itemFrame.getPos();
            object = this.frameMarkers.get(MapFrame.frameId((BlockPos)object3));
            if (object != null && itemFrame.getId() != ((MapFrame)object).getEntityId() && this.frameMarkers.containsKey(((MapFrame)object).getId())) {
                this.decorations.remove("frame-" + ((MapFrame)object).getEntityId());
            }
            object2 = new MapFrame((BlockPos)object3, itemFrame.getDirection().get2DDataValue() * 90, itemFrame.getId());
            this.addDecoration(MapDecoration.Type.FRAME, player.level, "frame-" + itemFrame.getId(), ((Vec3i)object3).getX(), ((Vec3i)object3).getZ(), itemFrame.getDirection().get2DDataValue() * 90, null);
            this.frameMarkers.put(((MapFrame)object2).getId(), (MapFrame)object2);
        }
        if ((compoundTag = itemStack.getTag()) != null && compoundTag.contains("Decorations", 9)) {
            object3 = compoundTag.getList("Decorations", 10);
            for (int i = 0; i < ((ListTag)object3).size(); ++i) {
                object2 = ((ListTag)object3).getCompound(i);
                if (this.decorations.containsKey(((CompoundTag)object2).getString("id"))) continue;
                this.addDecoration(MapDecoration.Type.byIcon(((CompoundTag)object2).getByte("type")), player.level, ((CompoundTag)object2).getString("id"), ((CompoundTag)object2).getDouble("x"), ((CompoundTag)object2).getDouble("z"), ((CompoundTag)object2).getDouble("rot"), null);
            }
        }
    }

    public static void addTargetDecoration(ItemStack itemStack, BlockPos blockPos, String string, MapDecoration.Type type) {
        ListTag listTag;
        if (itemStack.hasTag() && itemStack.getTag().contains("Decorations", 9)) {
            listTag = itemStack.getTag().getList("Decorations", 10);
        } else {
            listTag = new ListTag();
            itemStack.addTagElement("Decorations", listTag);
        }
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.putByte("type", type.getIcon());
        compoundTag.putString("id", string);
        compoundTag.putDouble("x", blockPos.getX());
        compoundTag.putDouble("z", blockPos.getZ());
        compoundTag.putDouble("rot", 180.0);
        listTag.add(compoundTag);
        if (type.hasMapColor()) {
            CompoundTag compoundTag2 = itemStack.getOrCreateTagElement("display");
            compoundTag2.putInt("MapColor", type.getMapColor());
        }
    }

    private void addDecoration(MapDecoration.Type type, @Nullable LevelAccessor levelAccessor, String string, double d, double d2, double d3, @Nullable Component component) {
        byte by;
        int n = 1 << this.scale;
        float f = (float)(d - (double)this.x) / (float)n;
        float f2 = (float)(d2 - (double)this.z) / (float)n;
        byte by2 = (byte)((double)(f * 2.0f) + 0.5);
        byte by3 = (byte)((double)(f2 * 2.0f) + 0.5);
        int n2 = 63;
        if (f >= -63.0f && f2 >= -63.0f && f <= 63.0f && f2 <= 63.0f) {
            by = (byte)((d3 += d3 < 0.0 ? -8.0 : 8.0) * 16.0 / 360.0);
            if (this.dimension == Level.NETHER && levelAccessor != null) {
                int n3 = (int)(levelAccessor.getLevelData().getDayTime() / 10L);
                by = (byte)(n3 * n3 * 34187121 + n3 * 121 >> 15 & 0xF);
            }
        } else if (type == MapDecoration.Type.PLAYER) {
            int n4 = 320;
            if (Math.abs(f) < 320.0f && Math.abs(f2) < 320.0f) {
                type = MapDecoration.Type.PLAYER_OFF_MAP;
            } else if (this.unlimitedTracking) {
                type = MapDecoration.Type.PLAYER_OFF_LIMITS;
            } else {
                this.decorations.remove(string);
                return;
            }
            by = 0;
            if (f <= -63.0f) {
                by2 = -128;
            }
            if (f2 <= -63.0f) {
                by3 = -128;
            }
            if (f >= 63.0f) {
                by2 = 127;
            }
            if (f2 >= 63.0f) {
                by3 = 127;
            }
        } else {
            this.decorations.remove(string);
            return;
        }
        this.decorations.put(string, new MapDecoration(type, by2, by3, by, component));
    }

    @Nullable
    public Packet<?> getUpdatePacket(ItemStack itemStack, BlockGetter blockGetter, Player player) {
        HoldingPlayer holdingPlayer = this.carriedByPlayers.get(player);
        if (holdingPlayer == null) {
            return null;
        }
        return holdingPlayer.nextUpdatePacket(itemStack);
    }

    public void setDirty(int n, int n2) {
        this.setDirty();
        for (HoldingPlayer holdingPlayer : this.carriedBy) {
            holdingPlayer.markDirty(n, n2);
        }
    }

    public HoldingPlayer getHoldingPlayer(Player player) {
        HoldingPlayer holdingPlayer = this.carriedByPlayers.get(player);
        if (holdingPlayer == null) {
            holdingPlayer = new HoldingPlayer(player);
            this.carriedByPlayers.put(player, holdingPlayer);
            this.carriedBy.add(holdingPlayer);
        }
        return holdingPlayer;
    }

    public void toggleBanner(LevelAccessor levelAccessor, BlockPos blockPos) {
        double d = (double)blockPos.getX() + 0.5;
        double d2 = (double)blockPos.getZ() + 0.5;
        int n = 1 << this.scale;
        double d3 = (d - (double)this.x) / (double)n;
        double d4 = (d2 - (double)this.z) / (double)n;
        int n2 = 63;
        boolean bl = false;
        if (d3 >= -63.0 && d4 >= -63.0 && d3 <= 63.0 && d4 <= 63.0) {
            MapBanner mapBanner = MapBanner.fromWorld(levelAccessor, blockPos);
            if (mapBanner == null) {
                return;
            }
            boolean bl2 = true;
            if (this.bannerMarkers.containsKey(mapBanner.getId()) && this.bannerMarkers.get(mapBanner.getId()).equals(mapBanner)) {
                this.bannerMarkers.remove(mapBanner.getId());
                this.decorations.remove(mapBanner.getId());
                bl2 = false;
                bl = true;
            }
            if (bl2) {
                this.bannerMarkers.put(mapBanner.getId(), mapBanner);
                this.addDecoration(mapBanner.getDecoration(), levelAccessor, mapBanner.getId(), d, d2, 180.0, mapBanner.getName());
                bl = true;
            }
            if (bl) {
                this.setDirty();
            }
        }
    }

    public void checkBanners(BlockGetter blockGetter, int n, int n2) {
        Iterator<MapBanner> iterator = this.bannerMarkers.values().iterator();
        while (iterator.hasNext()) {
            MapBanner mapBanner;
            MapBanner mapBanner2 = iterator.next();
            if (mapBanner2.getPos().getX() != n || mapBanner2.getPos().getZ() != n2 || mapBanner2.equals(mapBanner = MapBanner.fromWorld(blockGetter, mapBanner2.getPos()))) continue;
            iterator.remove();
            this.decorations.remove(mapBanner2.getId());
        }
    }

    public void removedFromFrame(BlockPos blockPos, int n) {
        this.decorations.remove("frame-" + n);
        this.frameMarkers.remove(MapFrame.frameId(blockPos));
    }

    public class HoldingPlayer {
        public final Player player;
        private boolean dirtyData = true;
        private int minDirtyX;
        private int minDirtyY;
        private int maxDirtyX = 127;
        private int maxDirtyY = 127;
        private int tick;
        public int step;

        public HoldingPlayer(Player player) {
            this.player = player;
        }

        @Nullable
        public Packet<?> nextUpdatePacket(ItemStack itemStack) {
            if (this.dirtyData) {
                this.dirtyData = false;
                return new ClientboundMapItemDataPacket(MapItem.getMapId(itemStack), MapItemSavedData.this.scale, MapItemSavedData.this.trackingPosition, MapItemSavedData.this.locked, MapItemSavedData.this.decorations.values(), MapItemSavedData.this.colors, this.minDirtyX, this.minDirtyY, this.maxDirtyX + 1 - this.minDirtyX, this.maxDirtyY + 1 - this.minDirtyY);
            }
            if (this.tick++ % 5 == 0) {
                return new ClientboundMapItemDataPacket(MapItem.getMapId(itemStack), MapItemSavedData.this.scale, MapItemSavedData.this.trackingPosition, MapItemSavedData.this.locked, MapItemSavedData.this.decorations.values(), MapItemSavedData.this.colors, 0, 0, 0, 0);
            }
            return null;
        }

        public void markDirty(int n, int n2) {
            if (this.dirtyData) {
                this.minDirtyX = Math.min(this.minDirtyX, n);
                this.minDirtyY = Math.min(this.minDirtyY, n2);
                this.maxDirtyX = Math.max(this.maxDirtyX, n);
                this.maxDirtyY = Math.max(this.maxDirtyY, n2);
            } else {
                this.dirtyData = true;
                this.minDirtyX = n;
                this.minDirtyY = n2;
                this.maxDirtyX = n;
                this.maxDirtyY = n2;
            }
        }
    }

}

