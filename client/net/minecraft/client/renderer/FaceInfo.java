/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.renderer;

import net.minecraft.Util;
import net.minecraft.core.Direction;

public enum FaceInfo {
    DOWN(new VertexInfo(Constants.MIN_X, Constants.MIN_Y, Constants.MAX_Z), new VertexInfo(Constants.MIN_X, Constants.MIN_Y, Constants.MIN_Z), new VertexInfo(Constants.MAX_X, Constants.MIN_Y, Constants.MIN_Z), new VertexInfo(Constants.MAX_X, Constants.MIN_Y, Constants.MAX_Z)),
    UP(new VertexInfo(Constants.MIN_X, Constants.MAX_Y, Constants.MIN_Z), new VertexInfo(Constants.MIN_X, Constants.MAX_Y, Constants.MAX_Z), new VertexInfo(Constants.MAX_X, Constants.MAX_Y, Constants.MAX_Z), new VertexInfo(Constants.MAX_X, Constants.MAX_Y, Constants.MIN_Z)),
    NORTH(new VertexInfo(Constants.MAX_X, Constants.MAX_Y, Constants.MIN_Z), new VertexInfo(Constants.MAX_X, Constants.MIN_Y, Constants.MIN_Z), new VertexInfo(Constants.MIN_X, Constants.MIN_Y, Constants.MIN_Z), new VertexInfo(Constants.MIN_X, Constants.MAX_Y, Constants.MIN_Z)),
    SOUTH(new VertexInfo(Constants.MIN_X, Constants.MAX_Y, Constants.MAX_Z), new VertexInfo(Constants.MIN_X, Constants.MIN_Y, Constants.MAX_Z), new VertexInfo(Constants.MAX_X, Constants.MIN_Y, Constants.MAX_Z), new VertexInfo(Constants.MAX_X, Constants.MAX_Y, Constants.MAX_Z)),
    WEST(new VertexInfo(Constants.MIN_X, Constants.MAX_Y, Constants.MIN_Z), new VertexInfo(Constants.MIN_X, Constants.MIN_Y, Constants.MIN_Z), new VertexInfo(Constants.MIN_X, Constants.MIN_Y, Constants.MAX_Z), new VertexInfo(Constants.MIN_X, Constants.MAX_Y, Constants.MAX_Z)),
    EAST(new VertexInfo(Constants.MAX_X, Constants.MAX_Y, Constants.MAX_Z), new VertexInfo(Constants.MAX_X, Constants.MIN_Y, Constants.MAX_Z), new VertexInfo(Constants.MAX_X, Constants.MIN_Y, Constants.MIN_Z), new VertexInfo(Constants.MAX_X, Constants.MAX_Y, Constants.MIN_Z));
    
    private static final FaceInfo[] BY_FACING;
    private final VertexInfo[] infos;

    public static FaceInfo fromFacing(Direction direction) {
        return BY_FACING[direction.get3DDataValue()];
    }

    private FaceInfo(VertexInfo ... arrvertexInfo) {
        this.infos = arrvertexInfo;
    }

    public VertexInfo getVertexInfo(int n) {
        return this.infos[n];
    }

    static {
        BY_FACING = Util.make(new FaceInfo[6], arrfaceInfo -> {
            arrfaceInfo[Constants.MIN_Y] = DOWN;
            arrfaceInfo[Constants.MAX_Y] = UP;
            arrfaceInfo[Constants.MIN_Z] = NORTH;
            arrfaceInfo[Constants.MAX_Z] = SOUTH;
            arrfaceInfo[Constants.MIN_X] = WEST;
            arrfaceInfo[Constants.MAX_X] = EAST;
        });
    }

    public static class VertexInfo {
        public final int xFace;
        public final int yFace;
        public final int zFace;

        private VertexInfo(int n, int n2, int n3) {
            this.xFace = n;
            this.yFace = n2;
            this.zFace = n3;
        }
    }

    public static final class Constants {
        public static final int MAX_Z = Direction.SOUTH.get3DDataValue();
        public static final int MAX_Y = Direction.UP.get3DDataValue();
        public static final int MAX_X = Direction.EAST.get3DDataValue();
        public static final int MIN_Z = Direction.NORTH.get3DDataValue();
        public static final int MIN_Y = Direction.DOWN.get3DDataValue();
        public static final int MIN_X = Direction.WEST.get3DDataValue();
    }

}

