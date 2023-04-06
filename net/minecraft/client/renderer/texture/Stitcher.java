/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Sets
 */
package net.minecraft.client.renderer.texture;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Stream;
import net.minecraft.client.renderer.texture.StitcherException;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class Stitcher {
    private static final Comparator<Holder> HOLDER_COMPARATOR = Comparator.comparing(holder -> -holder.height).thenComparing(holder -> -holder.width).thenComparing(holder -> holder.spriteInfo.name());
    private final int mipLevel;
    private final Set<Holder> texturesToBeStitched = Sets.newHashSetWithExpectedSize((int)256);
    private final List<Region> storage = Lists.newArrayListWithCapacity((int)256);
    private int storageX;
    private int storageY;
    private final int maxWidth;
    private final int maxHeight;

    public Stitcher(int n, int n2, int n3) {
        this.mipLevel = n3;
        this.maxWidth = n;
        this.maxHeight = n2;
    }

    public int getWidth() {
        return this.storageX;
    }

    public int getHeight() {
        return this.storageY;
    }

    public void registerSprite(TextureAtlasSprite.Info info) {
        Holder holder = new Holder(info, this.mipLevel);
        this.texturesToBeStitched.add(holder);
    }

    public void stitch() {
        ArrayList arrayList = Lists.newArrayList(this.texturesToBeStitched);
        arrayList.sort(HOLDER_COMPARATOR);
        for (Holder holder2 : arrayList) {
            if (this.addToStorage(holder2)) continue;
            throw new StitcherException(holder2.spriteInfo, (Collection)arrayList.stream().map(holder -> holder.spriteInfo).collect(ImmutableList.toImmutableList()));
        }
        this.storageX = Mth.smallestEncompassingPowerOfTwo(this.storageX);
        this.storageY = Mth.smallestEncompassingPowerOfTwo(this.storageY);
    }

    public void gatherSprites(SpriteLoader spriteLoader) {
        for (Region region2 : this.storage) {
            region2.walk(region -> {
                Holder holder = region.getHolder();
                TextureAtlasSprite.Info info = holder.spriteInfo;
                spriteLoader.load(info, this.storageX, this.storageY, region.getX(), region.getY());
            });
        }
    }

    private static int smallestFittingMinTexel(int n, int n2) {
        return (n >> n2) + ((n & (1 << n2) - 1) == 0 ? 0 : 1) << n2;
    }

    private boolean addToStorage(Holder holder) {
        for (Region region : this.storage) {
            if (!region.add(holder)) continue;
            return true;
        }
        return this.expand(holder);
    }

    private boolean expand(Holder holder) {
        boolean bl;
        Region region;
        boolean bl2;
        boolean bl3;
        int n = Mth.smallestEncompassingPowerOfTwo(this.storageX);
        int n2 = Mth.smallestEncompassingPowerOfTwo(this.storageY);
        int n3 = Mth.smallestEncompassingPowerOfTwo(this.storageX + holder.width);
        int n4 = Mth.smallestEncompassingPowerOfTwo(this.storageY + holder.height);
        boolean bl4 = n3 <= this.maxWidth;
        boolean bl5 = bl3 = n4 <= this.maxHeight;
        if (!bl4 && !bl3) {
            return false;
        }
        boolean bl6 = bl4 && n != n3;
        boolean bl7 = bl2 = bl3 && n2 != n4;
        if (bl6 ^ bl2) {
            bl = bl6;
        } else {
            boolean bl8 = bl = bl4 && n <= n2;
        }
        if (bl) {
            if (this.storageY == 0) {
                this.storageY = holder.height;
            }
            region = new Region(this.storageX, 0, holder.width, this.storageY);
            this.storageX += holder.width;
        } else {
            region = new Region(0, this.storageY, this.storageX, holder.height);
            this.storageY += holder.height;
        }
        region.add(holder);
        this.storage.add(region);
        return true;
    }

    public static class Region {
        private final int originX;
        private final int originY;
        private final int width;
        private final int height;
        private List<Region> subSlots;
        private Holder holder;

        public Region(int n, int n2, int n3, int n4) {
            this.originX = n;
            this.originY = n2;
            this.width = n3;
            this.height = n4;
        }

        public Holder getHolder() {
            return this.holder;
        }

        public int getX() {
            return this.originX;
        }

        public int getY() {
            return this.originY;
        }

        public boolean add(Holder holder) {
            if (this.holder != null) {
                return false;
            }
            int n = holder.width;
            int n2 = holder.height;
            if (n > this.width || n2 > this.height) {
                return false;
            }
            if (n == this.width && n2 == this.height) {
                this.holder = holder;
                return true;
            }
            if (this.subSlots == null) {
                this.subSlots = Lists.newArrayListWithCapacity((int)1);
                this.subSlots.add(new Region(this.originX, this.originY, n, n2));
                int n3 = this.width - n;
                int n4 = this.height - n2;
                if (n4 > 0 && n3 > 0) {
                    int n5;
                    int n6 = Math.max(this.height, n3);
                    if (n6 >= (n5 = Math.max(this.width, n4))) {
                        this.subSlots.add(new Region(this.originX, this.originY + n2, n, n4));
                        this.subSlots.add(new Region(this.originX + n, this.originY, n3, this.height));
                    } else {
                        this.subSlots.add(new Region(this.originX + n, this.originY, n3, n2));
                        this.subSlots.add(new Region(this.originX, this.originY + n2, this.width, n4));
                    }
                } else if (n3 == 0) {
                    this.subSlots.add(new Region(this.originX, this.originY + n2, n, n4));
                } else if (n4 == 0) {
                    this.subSlots.add(new Region(this.originX + n, this.originY, n3, n2));
                }
            }
            for (Region region : this.subSlots) {
                if (!region.add(holder)) continue;
                return true;
            }
            return false;
        }

        public void walk(Consumer<Region> consumer) {
            if (this.holder != null) {
                consumer.accept(this);
            } else if (this.subSlots != null) {
                for (Region region : this.subSlots) {
                    region.walk(consumer);
                }
            }
        }

        public String toString() {
            return "Slot{originX=" + this.originX + ", originY=" + this.originY + ", width=" + this.width + ", height=" + this.height + ", texture=" + this.holder + ", subSlots=" + this.subSlots + '}';
        }
    }

    static class Holder {
        public final TextureAtlasSprite.Info spriteInfo;
        public final int width;
        public final int height;

        public Holder(TextureAtlasSprite.Info info, int n) {
            this.spriteInfo = info;
            this.width = Stitcher.smallestFittingMinTexel(info.width(), n);
            this.height = Stitcher.smallestFittingMinTexel(info.height(), n);
        }

        public String toString() {
            return "Holder{width=" + this.width + ", height=" + this.height + '}';
        }
    }

    public static interface SpriteLoader {
        public void load(TextureAtlasSprite.Info var1, int var2, int var3, int var4, int var5);
    }

}

