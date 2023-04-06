/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.core;

import com.google.common.collect.Maps;
import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;
import com.mojang.math.Transformation;
import com.mojang.math.Vector3f;
import java.util.EnumMap;
import java.util.function.Supplier;
import net.minecraft.Util;
import net.minecraft.core.Direction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BlockMath {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final EnumMap<Direction, Transformation> vanillaUvTransformLocalToGlobal = Util.make(Maps.newEnumMap(Direction.class), enumMap -> {
        enumMap.put(Direction.SOUTH, Transformation.identity());
        enumMap.put(Direction.EAST, new Transformation(null, new Quaternion(new Vector3f(0.0f, 1.0f, 0.0f), 90.0f, true), null, null));
        enumMap.put(Direction.WEST, new Transformation(null, new Quaternion(new Vector3f(0.0f, 1.0f, 0.0f), -90.0f, true), null, null));
        enumMap.put(Direction.NORTH, new Transformation(null, new Quaternion(new Vector3f(0.0f, 1.0f, 0.0f), 180.0f, true), null, null));
        enumMap.put(Direction.UP, new Transformation(null, new Quaternion(new Vector3f(1.0f, 0.0f, 0.0f), -90.0f, true), null, null));
        enumMap.put(Direction.DOWN, new Transformation(null, new Quaternion(new Vector3f(1.0f, 0.0f, 0.0f), 90.0f, true), null, null));
    });
    public static final EnumMap<Direction, Transformation> vanillaUvTransformGlobalToLocal = Util.make(Maps.newEnumMap(Direction.class), enumMap -> {
        for (Direction direction : Direction.values()) {
            enumMap.put(direction, vanillaUvTransformLocalToGlobal.get(direction).inverse());
        }
    });

    public static Transformation blockCenterToCorner(Transformation transformation) {
        Matrix4f matrix4f = Matrix4f.createTranslateMatrix(0.5f, 0.5f, 0.5f);
        matrix4f.multiply(transformation.getMatrix());
        matrix4f.multiply(Matrix4f.createTranslateMatrix(-0.5f, -0.5f, -0.5f));
        return new Transformation(matrix4f);
    }

    public static Transformation getUVLockTransform(Transformation transformation, Direction direction, Supplier<String> supplier) {
        Direction direction2 = Direction.rotate(transformation.getMatrix(), direction);
        Transformation transformation2 = transformation.inverse();
        if (transformation2 == null) {
            LOGGER.warn(supplier.get());
            return new Transformation(null, null, new Vector3f(0.0f, 0.0f, 0.0f), null);
        }
        Transformation transformation3 = vanillaUvTransformGlobalToLocal.get(direction).compose(transformation2).compose(vanillaUvTransformLocalToGlobal.get(direction2));
        return BlockMath.blockCenterToCorner(transformation3);
    }
}

