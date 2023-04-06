/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.level.block.piston;

import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;

public class PistonMath {
    public static AABB getMovementArea(AABB aABB, Direction direction, double d) {
        double d2 = d * (double)direction.getAxisDirection().getStep();
        double d3 = Math.min(d2, 0.0);
        double d4 = Math.max(d2, 0.0);
        switch (direction) {
            case WEST: {
                return new AABB(aABB.minX + d3, aABB.minY, aABB.minZ, aABB.minX + d4, aABB.maxY, aABB.maxZ);
            }
            case EAST: {
                return new AABB(aABB.maxX + d3, aABB.minY, aABB.minZ, aABB.maxX + d4, aABB.maxY, aABB.maxZ);
            }
            case DOWN: {
                return new AABB(aABB.minX, aABB.minY + d3, aABB.minZ, aABB.maxX, aABB.minY + d4, aABB.maxZ);
            }
            default: {
                return new AABB(aABB.minX, aABB.maxY + d3, aABB.minZ, aABB.maxX, aABB.maxY + d4, aABB.maxZ);
            }
            case NORTH: {
                return new AABB(aABB.minX, aABB.minY, aABB.minZ + d3, aABB.maxX, aABB.maxY, aABB.minZ + d4);
            }
            case SOUTH: 
        }
        return new AABB(aABB.minX, aABB.minY, aABB.maxZ + d3, aABB.maxX, aABB.maxY, aABB.maxZ + d4);
    }

}

