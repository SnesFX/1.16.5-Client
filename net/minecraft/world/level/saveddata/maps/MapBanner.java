/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.saveddata.maps;

import java.util.Objects;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BannerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.saveddata.maps.MapDecoration;

public class MapBanner {
    private final BlockPos pos;
    private final DyeColor color;
    @Nullable
    private final Component name;

    public MapBanner(BlockPos blockPos, DyeColor dyeColor, @Nullable Component component) {
        this.pos = blockPos;
        this.color = dyeColor;
        this.name = component;
    }

    public static MapBanner load(CompoundTag compoundTag) {
        BlockPos blockPos = NbtUtils.readBlockPos(compoundTag.getCompound("Pos"));
        DyeColor dyeColor = DyeColor.byName(compoundTag.getString("Color"), DyeColor.WHITE);
        MutableComponent mutableComponent = compoundTag.contains("Name") ? Component.Serializer.fromJson(compoundTag.getString("Name")) : null;
        return new MapBanner(blockPos, dyeColor, mutableComponent);
    }

    @Nullable
    public static MapBanner fromWorld(BlockGetter blockGetter, BlockPos blockPos) {
        BlockEntity blockEntity = blockGetter.getBlockEntity(blockPos);
        if (blockEntity instanceof BannerBlockEntity) {
            BannerBlockEntity bannerBlockEntity = (BannerBlockEntity)blockEntity;
            DyeColor dyeColor = bannerBlockEntity.getBaseColor(() -> blockGetter.getBlockState(blockPos));
            Component component = bannerBlockEntity.hasCustomName() ? bannerBlockEntity.getCustomName() : null;
            return new MapBanner(blockPos, dyeColor, component);
        }
        return null;
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public MapDecoration.Type getDecoration() {
        switch (this.color) {
            case WHITE: {
                return MapDecoration.Type.BANNER_WHITE;
            }
            case ORANGE: {
                return MapDecoration.Type.BANNER_ORANGE;
            }
            case MAGENTA: {
                return MapDecoration.Type.BANNER_MAGENTA;
            }
            case LIGHT_BLUE: {
                return MapDecoration.Type.BANNER_LIGHT_BLUE;
            }
            case YELLOW: {
                return MapDecoration.Type.BANNER_YELLOW;
            }
            case LIME: {
                return MapDecoration.Type.BANNER_LIME;
            }
            case PINK: {
                return MapDecoration.Type.BANNER_PINK;
            }
            case GRAY: {
                return MapDecoration.Type.BANNER_GRAY;
            }
            case LIGHT_GRAY: {
                return MapDecoration.Type.BANNER_LIGHT_GRAY;
            }
            case CYAN: {
                return MapDecoration.Type.BANNER_CYAN;
            }
            case PURPLE: {
                return MapDecoration.Type.BANNER_PURPLE;
            }
            case BLUE: {
                return MapDecoration.Type.BANNER_BLUE;
            }
            case BROWN: {
                return MapDecoration.Type.BANNER_BROWN;
            }
            case GREEN: {
                return MapDecoration.Type.BANNER_GREEN;
            }
            case RED: {
                return MapDecoration.Type.BANNER_RED;
            }
        }
        return MapDecoration.Type.BANNER_BLACK;
    }

    @Nullable
    public Component getName() {
        return this.name;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || this.getClass() != object.getClass()) {
            return false;
        }
        MapBanner mapBanner = (MapBanner)object;
        return Objects.equals(this.pos, mapBanner.pos) && this.color == mapBanner.color && Objects.equals(this.name, mapBanner.name);
    }

    public int hashCode() {
        return Objects.hash(this.pos, this.color, this.name);
    }

    public CompoundTag save() {
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.put("Pos", NbtUtils.writeBlockPos(this.pos));
        compoundTag.putString("Color", this.color.getName());
        if (this.name != null) {
            compoundTag.putString("Name", Component.Serializer.toJson(this.name));
        }
        return compoundTag;
    }

    public String getId() {
        return "banner-" + this.pos.getX() + "," + this.pos.getY() + "," + this.pos.getZ();
    }

}

