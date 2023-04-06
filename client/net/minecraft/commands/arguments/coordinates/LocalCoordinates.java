/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.ImmutableStringReader
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.SimpleCommandExceptionType
 */
package net.minecraft.commands.arguments.coordinates;

import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Objects;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.commands.arguments.coordinates.Coordinates;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.commands.arguments.coordinates.WorldCoordinate;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public class LocalCoordinates
implements Coordinates {
    private final double left;
    private final double up;
    private final double forwards;

    public LocalCoordinates(double d, double d2, double d3) {
        this.left = d;
        this.up = d2;
        this.forwards = d3;
    }

    @Override
    public Vec3 getPosition(CommandSourceStack commandSourceStack) {
        Vec2 vec2 = commandSourceStack.getRotation();
        Vec3 vec3 = commandSourceStack.getAnchor().apply(commandSourceStack);
        float f = Mth.cos((vec2.y + 90.0f) * 0.017453292f);
        float f2 = Mth.sin((vec2.y + 90.0f) * 0.017453292f);
        float f3 = Mth.cos(-vec2.x * 0.017453292f);
        float f4 = Mth.sin(-vec2.x * 0.017453292f);
        float f5 = Mth.cos((-vec2.x + 90.0f) * 0.017453292f);
        float f6 = Mth.sin((-vec2.x + 90.0f) * 0.017453292f);
        Vec3 vec32 = new Vec3(f * f3, f4, f2 * f3);
        Vec3 vec33 = new Vec3(f * f5, f6, f2 * f5);
        Vec3 vec34 = vec32.cross(vec33).scale(-1.0);
        double d = vec32.x * this.forwards + vec33.x * this.up + vec34.x * this.left;
        double d2 = vec32.y * this.forwards + vec33.y * this.up + vec34.y * this.left;
        double d3 = vec32.z * this.forwards + vec33.z * this.up + vec34.z * this.left;
        return new Vec3(vec3.x + d, vec3.y + d2, vec3.z + d3);
    }

    @Override
    public Vec2 getRotation(CommandSourceStack commandSourceStack) {
        return Vec2.ZERO;
    }

    @Override
    public boolean isXRelative() {
        return true;
    }

    @Override
    public boolean isYRelative() {
        return true;
    }

    @Override
    public boolean isZRelative() {
        return true;
    }

    public static LocalCoordinates parse(StringReader stringReader) throws CommandSyntaxException {
        int n = stringReader.getCursor();
        double d = LocalCoordinates.readDouble(stringReader, n);
        if (!stringReader.canRead() || stringReader.peek() != ' ') {
            stringReader.setCursor(n);
            throw Vec3Argument.ERROR_NOT_COMPLETE.createWithContext((ImmutableStringReader)stringReader);
        }
        stringReader.skip();
        double d2 = LocalCoordinates.readDouble(stringReader, n);
        if (!stringReader.canRead() || stringReader.peek() != ' ') {
            stringReader.setCursor(n);
            throw Vec3Argument.ERROR_NOT_COMPLETE.createWithContext((ImmutableStringReader)stringReader);
        }
        stringReader.skip();
        double d3 = LocalCoordinates.readDouble(stringReader, n);
        return new LocalCoordinates(d, d2, d3);
    }

    private static double readDouble(StringReader stringReader, int n) throws CommandSyntaxException {
        if (!stringReader.canRead()) {
            throw WorldCoordinate.ERROR_EXPECTED_DOUBLE.createWithContext((ImmutableStringReader)stringReader);
        }
        if (stringReader.peek() != '^') {
            stringReader.setCursor(n);
            throw Vec3Argument.ERROR_MIXED_TYPE.createWithContext((ImmutableStringReader)stringReader);
        }
        stringReader.skip();
        return stringReader.canRead() && stringReader.peek() != ' ' ? stringReader.readDouble() : 0.0;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof LocalCoordinates)) {
            return false;
        }
        LocalCoordinates localCoordinates = (LocalCoordinates)object;
        return this.left == localCoordinates.left && this.up == localCoordinates.up && this.forwards == localCoordinates.forwards;
    }

    public int hashCode() {
        return Objects.hash(this.left, this.up, this.forwards);
    }
}

