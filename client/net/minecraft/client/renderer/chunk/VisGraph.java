/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.IntArrayFIFOQueue
 */
package net.minecraft.client.renderer.chunk;

import it.unimi.dsi.fastutil.ints.IntArrayFIFOQueue;
import java.util.BitSet;
import java.util.EnumSet;
import java.util.Set;
import net.minecraft.Util;
import net.minecraft.client.renderer.chunk.VisibilitySet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

public class VisGraph {
    private static final int DX = (int)Math.pow(16.0, 0.0);
    private static final int DZ = (int)Math.pow(16.0, 1.0);
    private static final int DY = (int)Math.pow(16.0, 2.0);
    private static final Direction[] DIRECTIONS = Direction.values();
    private final BitSet bitSet = new BitSet(4096);
    private static final int[] INDEX_OF_EDGES = Util.make(new int[1352], arrn -> {
        boolean bl = false;
        int n = 15;
        int n2 = 0;
        for (int i = 0; i < 16; ++i) {
            for (int j = 0; j < 16; ++j) {
                for (int k = 0; k < 16; ++k) {
                    if (i != 0 && i != 15 && j != 0 && j != 15 && k != 0 && k != 15) continue;
                    arrn[n2++] = VisGraph.getIndex(i, j, k);
                }
            }
        }
    });
    private int empty = 4096;

    public void setOpaque(BlockPos blockPos) {
        this.bitSet.set(VisGraph.getIndex(blockPos), true);
        --this.empty;
    }

    private static int getIndex(BlockPos blockPos) {
        return VisGraph.getIndex(blockPos.getX() & 0xF, blockPos.getY() & 0xF, blockPos.getZ() & 0xF);
    }

    private static int getIndex(int n, int n2, int n3) {
        return n << 0 | n2 << 8 | n3 << 4;
    }

    public VisibilitySet resolve() {
        VisibilitySet visibilitySet = new VisibilitySet();
        if (4096 - this.empty < 256) {
            visibilitySet.setAll(true);
        } else if (this.empty == 0) {
            visibilitySet.setAll(false);
        } else {
            for (int n : INDEX_OF_EDGES) {
                if (this.bitSet.get(n)) continue;
                visibilitySet.add(this.floodFill(n));
            }
        }
        return visibilitySet;
    }

    private Set<Direction> floodFill(int n) {
        EnumSet<Direction> enumSet = EnumSet.noneOf(Direction.class);
        IntArrayFIFOQueue intArrayFIFOQueue = new IntArrayFIFOQueue();
        intArrayFIFOQueue.enqueue(n);
        this.bitSet.set(n, true);
        while (!intArrayFIFOQueue.isEmpty()) {
            int n2 = intArrayFIFOQueue.dequeueInt();
            this.addEdges(n2, enumSet);
            for (Direction direction : DIRECTIONS) {
                int n3 = this.getNeighborIndexAtFace(n2, direction);
                if (n3 < 0 || this.bitSet.get(n3)) continue;
                this.bitSet.set(n3, true);
                intArrayFIFOQueue.enqueue(n3);
            }
        }
        return enumSet;
    }

    private void addEdges(int n, Set<Direction> set) {
        int n2 = n >> 0 & 0xF;
        if (n2 == 0) {
            set.add(Direction.WEST);
        } else if (n2 == 15) {
            set.add(Direction.EAST);
        }
        int n3 = n >> 8 & 0xF;
        if (n3 == 0) {
            set.add(Direction.DOWN);
        } else if (n3 == 15) {
            set.add(Direction.UP);
        }
        int n4 = n >> 4 & 0xF;
        if (n4 == 0) {
            set.add(Direction.NORTH);
        } else if (n4 == 15) {
            set.add(Direction.SOUTH);
        }
    }

    private int getNeighborIndexAtFace(int n, Direction direction) {
        switch (direction) {
            case DOWN: {
                if ((n >> 8 & 0xF) == 0) {
                    return -1;
                }
                return n - DY;
            }
            case UP: {
                if ((n >> 8 & 0xF) == 15) {
                    return -1;
                }
                return n + DY;
            }
            case NORTH: {
                if ((n >> 4 & 0xF) == 0) {
                    return -1;
                }
                return n - DZ;
            }
            case SOUTH: {
                if ((n >> 4 & 0xF) == 15) {
                    return -1;
                }
                return n + DZ;
            }
            case WEST: {
                if ((n >> 0 & 0xF) == 0) {
                    return -1;
                }
                return n - DX;
            }
            case EAST: {
                if ((n >> 0 & 0xF) == 15) {
                    return -1;
                }
                return n + DX;
            }
        }
        return -1;
    }

}

