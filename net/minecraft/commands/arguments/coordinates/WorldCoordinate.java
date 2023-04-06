/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.ImmutableStringReader
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.SimpleCommandExceptionType
 */
package net.minecraft.commands.arguments.coordinates;

import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.network.chat.TranslatableComponent;

public class WorldCoordinate {
    public static final SimpleCommandExceptionType ERROR_EXPECTED_DOUBLE = new SimpleCommandExceptionType((Message)new TranslatableComponent("argument.pos.missing.double"));
    public static final SimpleCommandExceptionType ERROR_EXPECTED_INT = new SimpleCommandExceptionType((Message)new TranslatableComponent("argument.pos.missing.int"));
    private final boolean relative;
    private final double value;

    public WorldCoordinate(boolean bl, double d) {
        this.relative = bl;
        this.value = d;
    }

    public double get(double d) {
        if (this.relative) {
            return this.value + d;
        }
        return this.value;
    }

    public static WorldCoordinate parseDouble(StringReader stringReader, boolean bl) throws CommandSyntaxException {
        if (stringReader.canRead() && stringReader.peek() == '^') {
            throw Vec3Argument.ERROR_MIXED_TYPE.createWithContext((ImmutableStringReader)stringReader);
        }
        if (!stringReader.canRead()) {
            throw ERROR_EXPECTED_DOUBLE.createWithContext((ImmutableStringReader)stringReader);
        }
        boolean bl2 = WorldCoordinate.isRelative(stringReader);
        int n = stringReader.getCursor();
        double d = stringReader.canRead() && stringReader.peek() != ' ' ? stringReader.readDouble() : 0.0;
        String string = stringReader.getString().substring(n, stringReader.getCursor());
        if (bl2 && string.isEmpty()) {
            return new WorldCoordinate(true, 0.0);
        }
        if (!string.contains(".") && !bl2 && bl) {
            d += 0.5;
        }
        return new WorldCoordinate(bl2, d);
    }

    public static WorldCoordinate parseInt(StringReader stringReader) throws CommandSyntaxException {
        if (stringReader.canRead() && stringReader.peek() == '^') {
            throw Vec3Argument.ERROR_MIXED_TYPE.createWithContext((ImmutableStringReader)stringReader);
        }
        if (!stringReader.canRead()) {
            throw ERROR_EXPECTED_INT.createWithContext((ImmutableStringReader)stringReader);
        }
        boolean bl = WorldCoordinate.isRelative(stringReader);
        double d = stringReader.canRead() && stringReader.peek() != ' ' ? (bl ? stringReader.readDouble() : (double)stringReader.readInt()) : 0.0;
        return new WorldCoordinate(bl, d);
    }

    public static boolean isRelative(StringReader stringReader) {
        boolean bl;
        if (stringReader.peek() == '~') {
            bl = true;
            stringReader.skip();
        } else {
            bl = false;
        }
        return bl;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof WorldCoordinate)) {
            return false;
        }
        WorldCoordinate worldCoordinate = (WorldCoordinate)object;
        if (this.relative != worldCoordinate.relative) {
            return false;
        }
        return Double.compare(worldCoordinate.value, this.value) == 0;
    }

    public int hashCode() {
        int n = this.relative ? 1 : 0;
        long l = Double.doubleToLongBits(this.value);
        n = 31 * n + (int)(l ^ l >>> 32);
        return n;
    }

    public boolean isRelative() {
        return this.relative;
    }
}

