/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.saveddata.maps;

import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public class MapDecoration {
    private final Type type;
    private byte x;
    private byte y;
    private byte rot;
    private final Component name;

    public MapDecoration(Type type, byte by, byte by2, byte by3, @Nullable Component component) {
        this.type = type;
        this.x = by;
        this.y = by2;
        this.rot = by3;
        this.name = component;
    }

    public byte getImage() {
        return this.type.getIcon();
    }

    public Type getType() {
        return this.type;
    }

    public byte getX() {
        return this.x;
    }

    public byte getY() {
        return this.y;
    }

    public byte getRot() {
        return this.rot;
    }

    public boolean renderOnFrame() {
        return this.type.isRenderedOnFrame();
    }

    @Nullable
    public Component getName() {
        return this.name;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof MapDecoration)) {
            return false;
        }
        MapDecoration mapDecoration = (MapDecoration)object;
        if (this.type != mapDecoration.type) {
            return false;
        }
        if (this.rot != mapDecoration.rot) {
            return false;
        }
        if (this.x != mapDecoration.x) {
            return false;
        }
        if (this.y != mapDecoration.y) {
            return false;
        }
        return Objects.equals(this.name, mapDecoration.name);
    }

    public int hashCode() {
        int n = this.type.getIcon();
        n = 31 * n + this.x;
        n = 31 * n + this.y;
        n = 31 * n + this.rot;
        n = 31 * n + Objects.hashCode(this.name);
        return n;
    }

    public static enum Type {
        PLAYER(false),
        FRAME(true),
        RED_MARKER(false),
        BLUE_MARKER(false),
        TARGET_X(true),
        TARGET_POINT(true),
        PLAYER_OFF_MAP(false),
        PLAYER_OFF_LIMITS(false),
        MANSION(true, 5393476),
        MONUMENT(true, 3830373),
        BANNER_WHITE(true),
        BANNER_ORANGE(true),
        BANNER_MAGENTA(true),
        BANNER_LIGHT_BLUE(true),
        BANNER_YELLOW(true),
        BANNER_LIME(true),
        BANNER_PINK(true),
        BANNER_GRAY(true),
        BANNER_LIGHT_GRAY(true),
        BANNER_CYAN(true),
        BANNER_PURPLE(true),
        BANNER_BLUE(true),
        BANNER_BROWN(true),
        BANNER_GREEN(true),
        BANNER_RED(true),
        BANNER_BLACK(true),
        RED_X(true);
        
        private final byte icon = (byte)this.ordinal();
        private final boolean renderedOnFrame;
        private final int mapColor;

        private Type(boolean bl) {
            this(bl, -1);
        }

        private Type(boolean bl, int n2) {
            this.renderedOnFrame = bl;
            this.mapColor = n2;
        }

        public byte getIcon() {
            return this.icon;
        }

        public boolean isRenderedOnFrame() {
            return this.renderedOnFrame;
        }

        public boolean hasMapColor() {
            return this.mapColor >= 0;
        }

        public int getMapColor() {
            return this.mapColor;
        }

        public static Type byIcon(byte by) {
            return Type.values()[Mth.clamp(by, 0, Type.values().length - 1)];
        }
    }

}

