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
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.coordinates.Coordinates;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.commands.arguments.coordinates.WorldCoordinate;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public class WorldCoordinates
implements Coordinates {
    private final WorldCoordinate x;
    private final WorldCoordinate y;
    private final WorldCoordinate z;

    public WorldCoordinates(WorldCoordinate worldCoordinate, WorldCoordinate worldCoordinate2, WorldCoordinate worldCoordinate3) {
        this.x = worldCoordinate;
        this.y = worldCoordinate2;
        this.z = worldCoordinate3;
    }

    @Override
    public Vec3 getPosition(CommandSourceStack commandSourceStack) {
        Vec3 vec3 = commandSourceStack.getPosition();
        return new Vec3(this.x.get(vec3.x), this.y.get(vec3.y), this.z.get(vec3.z));
    }

    @Override
    public Vec2 getRotation(CommandSourceStack commandSourceStack) {
        Vec2 vec2 = commandSourceStack.getRotation();
        return new Vec2((float)this.x.get(vec2.x), (float)this.y.get(vec2.y));
    }

    @Override
    public boolean isXRelative() {
        return this.x.isRelative();
    }

    @Override
    public boolean isYRelative() {
        return this.y.isRelative();
    }

    @Override
    public boolean isZRelative() {
        return this.z.isRelative();
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof WorldCoordinates)) {
            return false;
        }
        WorldCoordinates worldCoordinates = (WorldCoordinates)object;
        if (!this.x.equals(worldCoordinates.x)) {
            return false;
        }
        if (!this.y.equals(worldCoordinates.y)) {
            return false;
        }
        return this.z.equals(worldCoordinates.z);
    }

    public static WorldCoordinates parseInt(StringReader stringReader) throws CommandSyntaxException {
        int n = stringReader.getCursor();
        WorldCoordinate worldCoordinate = WorldCoordinate.parseInt(stringReader);
        if (!stringReader.canRead() || stringReader.peek() != ' ') {
            stringReader.setCursor(n);
            throw Vec3Argument.ERROR_NOT_COMPLETE.createWithContext((ImmutableStringReader)stringReader);
        }
        stringReader.skip();
        WorldCoordinate worldCoordinate2 = WorldCoordinate.parseInt(stringReader);
        if (!stringReader.canRead() || stringReader.peek() != ' ') {
            stringReader.setCursor(n);
            throw Vec3Argument.ERROR_NOT_COMPLETE.createWithContext((ImmutableStringReader)stringReader);
        }
        stringReader.skip();
        WorldCoordinate worldCoordinate3 = WorldCoordinate.parseInt(stringReader);
        return new WorldCoordinates(worldCoordinate, worldCoordinate2, worldCoordinate3);
    }

    public static WorldCoordinates parseDouble(StringReader stringReader, boolean bl) throws CommandSyntaxException {
        int n = stringReader.getCursor();
        WorldCoordinate worldCoordinate = WorldCoordinate.parseDouble(stringReader, bl);
        if (!stringReader.canRead() || stringReader.peek() != ' ') {
            stringReader.setCursor(n);
            throw Vec3Argument.ERROR_NOT_COMPLETE.createWithContext((ImmutableStringReader)stringReader);
        }
        stringReader.skip();
        WorldCoordinate worldCoordinate2 = WorldCoordinate.parseDouble(stringReader, false);
        if (!stringReader.canRead() || stringReader.peek() != ' ') {
            stringReader.setCursor(n);
            throw Vec3Argument.ERROR_NOT_COMPLETE.createWithContext((ImmutableStringReader)stringReader);
        }
        stringReader.skip();
        WorldCoordinate worldCoordinate3 = WorldCoordinate.parseDouble(stringReader, bl);
        return new WorldCoordinates(worldCoordinate, worldCoordinate2, worldCoordinate3);
    }

    public static WorldCoordinates current() {
        return new WorldCoordinates(new WorldCoordinate(true, 0.0), new WorldCoordinate(true, 0.0), new WorldCoordinate(true, 0.0));
    }

    public int hashCode() {
        int n = this.x.hashCode();
        n = 31 * n + this.y.hashCode();
        n = 31 * n + this.z.hashCode();
        return n;
    }
}

