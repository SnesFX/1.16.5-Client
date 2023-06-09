/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.tuple.Pair
 */
package net.minecraft.world.level.block.entity;

import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import org.apache.commons.lang3.tuple.Pair;

public enum BannerPattern {
    BASE("base", "b", false),
    SQUARE_BOTTOM_LEFT("square_bottom_left", "bl"),
    SQUARE_BOTTOM_RIGHT("square_bottom_right", "br"),
    SQUARE_TOP_LEFT("square_top_left", "tl"),
    SQUARE_TOP_RIGHT("square_top_right", "tr"),
    STRIPE_BOTTOM("stripe_bottom", "bs"),
    STRIPE_TOP("stripe_top", "ts"),
    STRIPE_LEFT("stripe_left", "ls"),
    STRIPE_RIGHT("stripe_right", "rs"),
    STRIPE_CENTER("stripe_center", "cs"),
    STRIPE_MIDDLE("stripe_middle", "ms"),
    STRIPE_DOWNRIGHT("stripe_downright", "drs"),
    STRIPE_DOWNLEFT("stripe_downleft", "dls"),
    STRIPE_SMALL("small_stripes", "ss"),
    CROSS("cross", "cr"),
    STRAIGHT_CROSS("straight_cross", "sc"),
    TRIANGLE_BOTTOM("triangle_bottom", "bt"),
    TRIANGLE_TOP("triangle_top", "tt"),
    TRIANGLES_BOTTOM("triangles_bottom", "bts"),
    TRIANGLES_TOP("triangles_top", "tts"),
    DIAGONAL_LEFT("diagonal_left", "ld"),
    DIAGONAL_RIGHT("diagonal_up_right", "rd"),
    DIAGONAL_LEFT_MIRROR("diagonal_up_left", "lud"),
    DIAGONAL_RIGHT_MIRROR("diagonal_right", "rud"),
    CIRCLE_MIDDLE("circle", "mc"),
    RHOMBUS_MIDDLE("rhombus", "mr"),
    HALF_VERTICAL("half_vertical", "vh"),
    HALF_HORIZONTAL("half_horizontal", "hh"),
    HALF_VERTICAL_MIRROR("half_vertical_right", "vhr"),
    HALF_HORIZONTAL_MIRROR("half_horizontal_bottom", "hhb"),
    BORDER("border", "bo"),
    CURLY_BORDER("curly_border", "cbo"),
    GRADIENT("gradient", "gra"),
    GRADIENT_UP("gradient_up", "gru"),
    BRICKS("bricks", "bri"),
    GLOBE("globe", "glb", true),
    CREEPER("creeper", "cre", true),
    SKULL("skull", "sku", true),
    FLOWER("flower", "flo", true),
    MOJANG("mojang", "moj", true),
    PIGLIN("piglin", "pig", true);
    
    private static final BannerPattern[] VALUES;
    public static final int COUNT;
    public static final int PATTERN_ITEM_COUNT;
    public static final int AVAILABLE_PATTERNS;
    private final boolean hasPatternItem;
    private final String filename;
    private final String hashname;

    private BannerPattern(String string2, String string3) {
        this(string2, string3, false);
    }

    private BannerPattern(String string2, String string3, boolean bl) {
        this.filename = string2;
        this.hashname = string3;
        this.hasPatternItem = bl;
    }

    public ResourceLocation location(boolean bl) {
        String string = bl ? "banner" : "shield";
        return new ResourceLocation("entity/" + string + "/" + this.getFilename());
    }

    public String getFilename() {
        return this.filename;
    }

    public String getHashname() {
        return this.hashname;
    }

    @Nullable
    public static BannerPattern byHash(String string) {
        for (BannerPattern bannerPattern : BannerPattern.values()) {
            if (!bannerPattern.hashname.equals(string)) continue;
            return bannerPattern;
        }
        return null;
    }

    static {
        VALUES = BannerPattern.values();
        COUNT = VALUES.length;
        PATTERN_ITEM_COUNT = (int)Arrays.stream(VALUES).filter(bannerPattern -> bannerPattern.hasPatternItem).count();
        AVAILABLE_PATTERNS = COUNT - PATTERN_ITEM_COUNT - 1;
    }

    public static class Builder {
        private final List<Pair<BannerPattern, DyeColor>> patterns = Lists.newArrayList();

        public Builder addPattern(BannerPattern bannerPattern, DyeColor dyeColor) {
            this.patterns.add((Pair<BannerPattern, DyeColor>)Pair.of((Object)((Object)bannerPattern), (Object)dyeColor));
            return this;
        }

        public ListTag toListTag() {
            ListTag listTag = new ListTag();
            for (Pair<BannerPattern, DyeColor> pair : this.patterns) {
                CompoundTag compoundTag = new CompoundTag();
                compoundTag.putString("Pattern", ((BannerPattern)((Object)pair.getLeft())).hashname);
                compoundTag.putInt("Color", ((DyeColor)pair.getRight()).getId());
                listTag.add(compoundTag);
            }
            return listTag;
        }
    }

}

