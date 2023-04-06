/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.level.levelgen.structure;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;

public abstract class ScatteredFeaturePiece
extends StructurePiece {
    protected final int width;
    protected final int height;
    protected final int depth;
    protected int heightPosition = -1;

    protected ScatteredFeaturePiece(StructurePieceType structurePieceType, Random random, int n, int n2, int n3, int n4, int n5, int n6) {
        super(structurePieceType, 0);
        this.width = n4;
        this.height = n5;
        this.depth = n6;
        this.setOrientation(Direction.Plane.HORIZONTAL.getRandomDirection(random));
        this.boundingBox = this.getOrientation().getAxis() == Direction.Axis.Z ? new BoundingBox(n, n2, n3, n + n4 - 1, n2 + n5 - 1, n3 + n6 - 1) : new BoundingBox(n, n2, n3, n + n6 - 1, n2 + n5 - 1, n3 + n4 - 1);
    }

    protected ScatteredFeaturePiece(StructurePieceType structurePieceType, CompoundTag compoundTag) {
        super(structurePieceType, compoundTag);
        this.width = compoundTag.getInt("Width");
        this.height = compoundTag.getInt("Height");
        this.depth = compoundTag.getInt("Depth");
        this.heightPosition = compoundTag.getInt("HPos");
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compoundTag) {
        compoundTag.putInt("Width", this.width);
        compoundTag.putInt("Height", this.height);
        compoundTag.putInt("Depth", this.depth);
        compoundTag.putInt("HPos", this.heightPosition);
    }

    protected boolean updateAverageGroundHeight(LevelAccessor levelAccessor, BoundingBox boundingBox, int n) {
        if (this.heightPosition >= 0) {
            return true;
        }
        int n2 = 0;
        int n3 = 0;
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
        for (int i = this.boundingBox.z0; i <= this.boundingBox.z1; ++i) {
            for (int j = this.boundingBox.x0; j <= this.boundingBox.x1; ++j) {
                mutableBlockPos.set(j, 64, i);
                if (!boundingBox.isInside(mutableBlockPos)) continue;
                n2 += levelAccessor.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, mutableBlockPos).getY();
                ++n3;
            }
        }
        if (n3 == 0) {
            return false;
        }
        this.heightPosition = n2 / n3;
        this.boundingBox.move(0, this.heightPosition - this.boundingBox.y0 + n, 0);
        return true;
    }
}

