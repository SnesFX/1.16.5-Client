/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.LinkedHashMultiset
 *  com.google.common.collect.Multiset
 *  com.google.common.collect.Multisets
 *  javax.annotation.Nullable
 */
package net.minecraft.world.item;

import com.google.common.collect.Iterables;
import com.google.common.collect.LinkedHashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ComplexItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.level.storage.LevelData;

public class MapItem
extends ComplexItem {
    public MapItem(Item.Properties properties) {
        super(properties);
    }

    public static ItemStack create(Level level, int n, int n2, byte by, boolean bl, boolean bl2) {
        ItemStack itemStack = new ItemStack(Items.FILLED_MAP);
        MapItem.createAndStoreSavedData(itemStack, level, n, n2, by, bl, bl2, level.dimension());
        return itemStack;
    }

    @Nullable
    public static MapItemSavedData getSavedData(ItemStack itemStack, Level level) {
        return level.getMapData(MapItem.makeKey(MapItem.getMapId(itemStack)));
    }

    @Nullable
    public static MapItemSavedData getOrCreateSavedData(ItemStack itemStack, Level level) {
        MapItemSavedData mapItemSavedData = MapItem.getSavedData(itemStack, level);
        if (mapItemSavedData == null && level instanceof ServerLevel) {
            mapItemSavedData = MapItem.createAndStoreSavedData(itemStack, level, level.getLevelData().getXSpawn(), level.getLevelData().getZSpawn(), 3, false, false, level.dimension());
        }
        return mapItemSavedData;
    }

    public static int getMapId(ItemStack itemStack) {
        CompoundTag compoundTag = itemStack.getTag();
        return compoundTag != null && compoundTag.contains("map", 99) ? compoundTag.getInt("map") : 0;
    }

    private static MapItemSavedData createAndStoreSavedData(ItemStack itemStack, Level level, int n, int n2, int n3, boolean bl, boolean bl2, ResourceKey<Level> resourceKey) {
        int n4 = level.getFreeMapId();
        MapItemSavedData mapItemSavedData = new MapItemSavedData(MapItem.makeKey(n4));
        mapItemSavedData.setProperties(n, n2, n3, bl, bl2, resourceKey);
        level.setMapData(mapItemSavedData);
        itemStack.getOrCreateTag().putInt("map", n4);
        return mapItemSavedData;
    }

    public static String makeKey(int n) {
        return "map_" + n;
    }

    public void update(Level level, Entity entity, MapItemSavedData mapItemSavedData) {
        if (level.dimension() != mapItemSavedData.dimension || !(entity instanceof Player)) {
            return;
        }
        int n = 1 << mapItemSavedData.scale;
        int n2 = mapItemSavedData.x;
        int n3 = mapItemSavedData.z;
        int n4 = Mth.floor(entity.getX() - (double)n2) / n + 64;
        int n5 = Mth.floor(entity.getZ() - (double)n3) / n + 64;
        int n6 = 128 / n;
        if (level.dimensionType().hasCeiling()) {
            n6 /= 2;
        }
        MapItemSavedData.HoldingPlayer holdingPlayer = mapItemSavedData.getHoldingPlayer((Player)entity);
        ++holdingPlayer.step;
        boolean bl = false;
        for (int i = n4 - n6 + 1; i < n4 + n6; ++i) {
            if ((i & 0xF) != (holdingPlayer.step & 0xF) && !bl) continue;
            bl = false;
            double d = 0.0;
            for (int j = n5 - n6 - 1; j < n5 + n6; ++j) {
                Object object;
                int n7;
                Object object2;
                int n8;
                if (i < 0 || j < -1 || i >= 128 || j >= 128) continue;
                int n9 = i - n4;
                int n10 = j - n5;
                boolean bl2 = n9 * n9 + n10 * n10 > (n6 - 2) * (n6 - 2);
                int n11 = (n2 / n + i - 64) * n;
                int n12 = (n3 / n + j - 64) * n;
                LinkedHashMultiset linkedHashMultiset = LinkedHashMultiset.create();
                LevelChunk levelChunk = level.getChunkAt(new BlockPos(n11, 0, n12));
                if (levelChunk.isEmpty()) continue;
                ChunkPos chunkPos = levelChunk.getPos();
                int n13 = n11 & 0xF;
                int n14 = n12 & 0xF;
                int n15 = 0;
                double d2 = 0.0;
                if (level.dimensionType().hasCeiling()) {
                    int n16 = n11 + n12 * 231871;
                    if (((n16 = n16 * n16 * 31287121 + n16 * 11) >> 20 & 1) == 0) {
                        linkedHashMultiset.add((Object)Blocks.DIRT.defaultBlockState().getMapColor(level, BlockPos.ZERO), 10);
                    } else {
                        linkedHashMultiset.add((Object)Blocks.STONE.defaultBlockState().getMapColor(level, BlockPos.ZERO), 100);
                    }
                    d2 = 100.0;
                } else {
                    BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
                    BlockPos.MutableBlockPos mutableBlockPos2 = new BlockPos.MutableBlockPos();
                    for (n8 = 0; n8 < n; ++n8) {
                        for (object = 0; object < n; ++object) {
                            n7 = levelChunk.getHeight(Heightmap.Types.WORLD_SURFACE, n8 + n13, object + n14) + 1;
                            if (n7 > 1) {
                                do {
                                    mutableBlockPos.set(chunkPos.getMinBlockX() + n8 + n13, --n7, chunkPos.getMinBlockZ() + object + n14);
                                } while ((object2 = levelChunk.getBlockState(mutableBlockPos)).getMapColor(level, mutableBlockPos) == MaterialColor.NONE && n7 > 0);
                                if (n7 > 0 && !object2.getFluidState().isEmpty()) {
                                    BlockState blockState;
                                    int n17 = n7 - 1;
                                    mutableBlockPos2.set(mutableBlockPos);
                                    do {
                                        mutableBlockPos2.setY(n17--);
                                        blockState = levelChunk.getBlockState(mutableBlockPos2);
                                        ++n15;
                                    } while (n17 > 0 && !blockState.getFluidState().isEmpty());
                                    object2 = this.getCorrectStateForFluidBlock(level, (BlockState)object2, mutableBlockPos);
                                }
                            } else {
                                object2 = Blocks.BEDROCK.defaultBlockState();
                            }
                            mapItemSavedData.checkBanners(level, chunkPos.getMinBlockX() + n8 + n13, chunkPos.getMinBlockZ() + object + n14);
                            d2 += (double)n7 / (double)(n * n);
                            linkedHashMultiset.add((Object)object2.getMapColor(level, mutableBlockPos));
                        }
                    }
                }
                n15 /= n * n;
                double d3 = (d2 - d) * 4.0 / (double)(n + 4) + ((double)(i + j & 1) - 0.5) * 0.4;
                n8 = 1;
                if (d3 > 0.6) {
                    n8 = 2;
                }
                if (d3 < -0.6) {
                    n8 = 0;
                }
                if ((object = (Object)((MaterialColor)Iterables.getFirst((Iterable)Multisets.copyHighestCountFirst((Multiset)linkedHashMultiset), (Object)MaterialColor.NONE))) == MaterialColor.WATER) {
                    d3 = (double)n15 * 0.1 + (double)(i + j & 1) * 0.2;
                    n8 = 1;
                    if (d3 < 0.5) {
                        n8 = 2;
                    }
                    if (d3 > 0.9) {
                        n8 = 0;
                    }
                }
                d = d2;
                if (j < 0 || n9 * n9 + n10 * n10 >= n6 * n6 || bl2 && (i + j & 1) == 0 || (n7 = mapItemSavedData.colors[i + j * 128]) == (object2 = (Object)((byte)(object.id * 4 + n8)))) continue;
                mapItemSavedData.colors[i + j * 128] = (byte)object2;
                mapItemSavedData.setDirty(i, j);
                bl = true;
            }
        }
    }

    private BlockState getCorrectStateForFluidBlock(Level level, BlockState blockState, BlockPos blockPos) {
        FluidState fluidState = blockState.getFluidState();
        if (!fluidState.isEmpty() && !blockState.isFaceSturdy(level, blockPos, Direction.UP)) {
            return fluidState.createLegacyBlock();
        }
        return blockState;
    }

    private static boolean isLand(Biome[] arrbiome, int n, int n2, int n3) {
        return arrbiome[n2 * n + n3 * n * 128 * n].getDepth() >= 0.0f;
    }

    public static void renderBiomePreviewMap(ServerLevel serverLevel, ItemStack itemStack) {
        int n;
        int n2;
        MapItemSavedData mapItemSavedData = MapItem.getOrCreateSavedData(itemStack, serverLevel);
        if (mapItemSavedData == null) {
            return;
        }
        if (serverLevel.dimension() != mapItemSavedData.dimension) {
            return;
        }
        int n3 = 1 << mapItemSavedData.scale;
        int n4 = mapItemSavedData.x;
        int n5 = mapItemSavedData.z;
        Biome[] arrbiome = new Biome[128 * n3 * 128 * n3];
        for (n2 = 0; n2 < 128 * n3; ++n2) {
            for (n = 0; n < 128 * n3; ++n) {
                arrbiome[n2 * 128 * n3 + n] = serverLevel.getBiome(new BlockPos((n4 / n3 - 64) * n3 + n, 0, (n5 / n3 - 64) * n3 + n2));
            }
        }
        for (n2 = 0; n2 < 128; ++n2) {
            for (n = 0; n < 128; ++n) {
                if (n2 <= 0 || n <= 0 || n2 >= 127 || n >= 127) continue;
                Biome biome = arrbiome[n2 * n3 + n * n3 * 128 * n3];
                int n6 = 8;
                if (MapItem.isLand(arrbiome, n3, n2 - 1, n - 1)) {
                    --n6;
                }
                if (MapItem.isLand(arrbiome, n3, n2 - 1, n + 1)) {
                    --n6;
                }
                if (MapItem.isLand(arrbiome, n3, n2 - 1, n)) {
                    --n6;
                }
                if (MapItem.isLand(arrbiome, n3, n2 + 1, n - 1)) {
                    --n6;
                }
                if (MapItem.isLand(arrbiome, n3, n2 + 1, n + 1)) {
                    --n6;
                }
                if (MapItem.isLand(arrbiome, n3, n2 + 1, n)) {
                    --n6;
                }
                if (MapItem.isLand(arrbiome, n3, n2, n - 1)) {
                    --n6;
                }
                if (MapItem.isLand(arrbiome, n3, n2, n + 1)) {
                    --n6;
                }
                int n7 = 3;
                MaterialColor materialColor = MaterialColor.NONE;
                if (biome.getDepth() < 0.0f) {
                    materialColor = MaterialColor.COLOR_ORANGE;
                    if (n6 > 7 && n % 2 == 0) {
                        n7 = (n2 + (int)(Mth.sin((float)n + 0.0f) * 7.0f)) / 8 % 5;
                        if (n7 == 3) {
                            n7 = 1;
                        } else if (n7 == 4) {
                            n7 = 0;
                        }
                    } else if (n6 > 7) {
                        materialColor = MaterialColor.NONE;
                    } else if (n6 > 5) {
                        n7 = 1;
                    } else if (n6 > 3) {
                        n7 = 0;
                    } else if (n6 > 1) {
                        n7 = 0;
                    }
                } else if (n6 > 0) {
                    materialColor = MaterialColor.COLOR_BROWN;
                    n7 = n6 > 3 ? 1 : 3;
                }
                if (materialColor == MaterialColor.NONE) continue;
                mapItemSavedData.colors[n2 + n * 128] = (byte)(materialColor.id * 4 + n7);
                mapItemSavedData.setDirty(n2, n);
            }
        }
    }

    @Override
    public void inventoryTick(ItemStack itemStack, Level level, Entity entity, int n, boolean bl) {
        if (level.isClientSide) {
            return;
        }
        MapItemSavedData mapItemSavedData = MapItem.getOrCreateSavedData(itemStack, level);
        if (mapItemSavedData == null) {
            return;
        }
        if (entity instanceof Player) {
            Player player = (Player)entity;
            mapItemSavedData.tickCarriedBy(player, itemStack);
        }
        if (!mapItemSavedData.locked && (bl || entity instanceof Player && ((Player)entity).getOffhandItem() == itemStack)) {
            this.update(level, entity, mapItemSavedData);
        }
    }

    @Nullable
    @Override
    public Packet<?> getUpdatePacket(ItemStack itemStack, Level level, Player player) {
        return MapItem.getOrCreateSavedData(itemStack, level).getUpdatePacket(itemStack, level, player);
    }

    @Override
    public void onCraftedBy(ItemStack itemStack, Level level, Player player) {
        CompoundTag compoundTag = itemStack.getTag();
        if (compoundTag != null && compoundTag.contains("map_scale_direction", 99)) {
            MapItem.scaleMap(itemStack, level, compoundTag.getInt("map_scale_direction"));
            compoundTag.remove("map_scale_direction");
        } else if (compoundTag != null && compoundTag.contains("map_to_lock", 1) && compoundTag.getBoolean("map_to_lock")) {
            MapItem.lockMap(level, itemStack);
            compoundTag.remove("map_to_lock");
        }
    }

    protected static void scaleMap(ItemStack itemStack, Level level, int n) {
        MapItemSavedData mapItemSavedData = MapItem.getOrCreateSavedData(itemStack, level);
        if (mapItemSavedData != null) {
            MapItem.createAndStoreSavedData(itemStack, level, mapItemSavedData.x, mapItemSavedData.z, Mth.clamp(mapItemSavedData.scale + n, 0, 4), mapItemSavedData.trackingPosition, mapItemSavedData.unlimitedTracking, mapItemSavedData.dimension);
        }
    }

    public static void lockMap(Level level, ItemStack itemStack) {
        MapItemSavedData mapItemSavedData = MapItem.getOrCreateSavedData(itemStack, level);
        if (mapItemSavedData != null) {
            MapItemSavedData mapItemSavedData2 = MapItem.createAndStoreSavedData(itemStack, level, 0, 0, mapItemSavedData.scale, mapItemSavedData.trackingPosition, mapItemSavedData.unlimitedTracking, mapItemSavedData.dimension);
            mapItemSavedData2.lockData(mapItemSavedData);
        }
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> list, TooltipFlag tooltipFlag) {
        MapItemSavedData mapItemSavedData;
        MapItemSavedData mapItemSavedData2 = mapItemSavedData = level == null ? null : MapItem.getOrCreateSavedData(itemStack, level);
        if (mapItemSavedData != null && mapItemSavedData.locked) {
            list.add(new TranslatableComponent("filled_map.locked", MapItem.getMapId(itemStack)).withStyle(ChatFormatting.GRAY));
        }
        if (tooltipFlag.isAdvanced()) {
            if (mapItemSavedData != null) {
                list.add(new TranslatableComponent("filled_map.id", MapItem.getMapId(itemStack)).withStyle(ChatFormatting.GRAY));
                list.add(new TranslatableComponent("filled_map.scale", 1 << mapItemSavedData.scale).withStyle(ChatFormatting.GRAY));
                list.add(new TranslatableComponent("filled_map.level", mapItemSavedData.scale, 4).withStyle(ChatFormatting.GRAY));
            } else {
                list.add(new TranslatableComponent("filled_map.unknown").withStyle(ChatFormatting.GRAY));
            }
        }
    }

    public static int getColor(ItemStack itemStack) {
        CompoundTag compoundTag = itemStack.getTagElement("display");
        if (compoundTag != null && compoundTag.contains("MapColor", 99)) {
            int n = compoundTag.getInt("MapColor");
            return 0xFF000000 | n & 0xFFFFFF;
        }
        return -12173266;
    }

    @Override
    public InteractionResult useOn(UseOnContext useOnContext) {
        BlockState blockState = useOnContext.getLevel().getBlockState(useOnContext.getClickedPos());
        if (blockState.is(BlockTags.BANNERS)) {
            if (!useOnContext.getLevel().isClientSide) {
                MapItemSavedData mapItemSavedData = MapItem.getOrCreateSavedData(useOnContext.getItemInHand(), useOnContext.getLevel());
                mapItemSavedData.toggleBanner(useOnContext.getLevel(), useOnContext.getClickedPos());
            }
            return InteractionResult.sidedSuccess(useOnContext.getLevel().isClientSide);
        }
        return super.useOn(useOnContext);
    }
}

