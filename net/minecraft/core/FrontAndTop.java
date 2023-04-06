/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
 */
package net.minecraft.core;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;

public enum FrontAndTop implements StringRepresentable
{
    DOWN_EAST("down_east", Direction.DOWN, Direction.EAST),
    DOWN_NORTH("down_north", Direction.DOWN, Direction.NORTH),
    DOWN_SOUTH("down_south", Direction.DOWN, Direction.SOUTH),
    DOWN_WEST("down_west", Direction.DOWN, Direction.WEST),
    UP_EAST("up_east", Direction.UP, Direction.EAST),
    UP_NORTH("up_north", Direction.UP, Direction.NORTH),
    UP_SOUTH("up_south", Direction.UP, Direction.SOUTH),
    UP_WEST("up_west", Direction.UP, Direction.WEST),
    WEST_UP("west_up", Direction.WEST, Direction.UP),
    EAST_UP("east_up", Direction.EAST, Direction.UP),
    NORTH_UP("north_up", Direction.NORTH, Direction.UP),
    SOUTH_UP("south_up", Direction.SOUTH, Direction.UP);
    
    private static final Int2ObjectMap<FrontAndTop> LOOKUP_TOP_FRONT;
    private final String name;
    private final Direction top;
    private final Direction front;

    private static int lookupKey(Direction direction, Direction direction2) {
        return direction.ordinal() << 3 | direction2.ordinal();
    }

    private FrontAndTop(String string2, Direction direction, Direction direction2) {
        this.name = string2;
        this.front = direction;
        this.top = direction2;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }

    public static FrontAndTop fromFrontAndTop(Direction direction, Direction direction2) {
        int n = FrontAndTop.lookupKey(direction2, direction);
        return (FrontAndTop)LOOKUP_TOP_FRONT.get(n);
    }

    public Direction front() {
        return this.front;
    }

    public Direction top() {
        return this.top;
    }

    static {
        LOOKUP_TOP_FRONT = new Int2ObjectOpenHashMap(FrontAndTop.values().length);
        for (FrontAndTop frontAndTop : FrontAndTop.values()) {
            LOOKUP_TOP_FRONT.put(FrontAndTop.lookupKey(frontAndTop.top, frontAndTop.front), (Object)frontAndTop);
        }
    }
}

