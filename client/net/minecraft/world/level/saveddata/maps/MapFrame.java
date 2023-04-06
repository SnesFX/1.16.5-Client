/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.level.saveddata.maps;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;

public class MapFrame {
    private final BlockPos pos;
    private final int rotation;
    private final int entityId;

    public MapFrame(BlockPos blockPos, int n, int n2) {
        this.pos = blockPos;
        this.rotation = n;
        this.entityId = n2;
    }

    public static MapFrame load(CompoundTag compoundTag) {
        BlockPos blockPos = NbtUtils.readBlockPos(compoundTag.getCompound("Pos"));
        int n = compoundTag.getInt("Rotation");
        int n2 = compoundTag.getInt("EntityId");
        return new MapFrame(blockPos, n, n2);
    }

    public CompoundTag save() {
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.put("Pos", NbtUtils.writeBlockPos(this.pos));
        compoundTag.putInt("Rotation", this.rotation);
        compoundTag.putInt("EntityId", this.entityId);
        return compoundTag;
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public int getRotation() {
        return this.rotation;
    }

    public int getEntityId() {
        return this.entityId;
    }

    public String getId() {
        return MapFrame.frameId(this.pos);
    }

    public static String frameId(BlockPos blockPos) {
        return "frame-" + blockPos.getX() + "," + blockPos.getY() + "," + blockPos.getZ();
    }
}

