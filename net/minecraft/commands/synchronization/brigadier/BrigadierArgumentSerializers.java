/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.arguments.BoolArgumentType
 *  com.mojang.brigadier.arguments.DoubleArgumentType
 *  com.mojang.brigadier.arguments.FloatArgumentType
 *  com.mojang.brigadier.arguments.IntegerArgumentType
 *  com.mojang.brigadier.arguments.LongArgumentType
 *  com.mojang.brigadier.arguments.StringArgumentType
 */
package net.minecraft.commands.synchronization.brigadier;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import java.util.function.Supplier;
import net.minecraft.commands.synchronization.ArgumentTypes;
import net.minecraft.commands.synchronization.EmptyArgumentSerializer;
import net.minecraft.commands.synchronization.brigadier.DoubleArgumentSerializer;
import net.minecraft.commands.synchronization.brigadier.FloatArgumentSerializer;
import net.minecraft.commands.synchronization.brigadier.IntegerArgumentSerializer;
import net.minecraft.commands.synchronization.brigadier.LongArgumentSerializer;
import net.minecraft.commands.synchronization.brigadier.StringArgumentSerializer;

public class BrigadierArgumentSerializers {
    public static void bootstrap() {
        ArgumentTypes.register("brigadier:bool", BoolArgumentType.class, new EmptyArgumentSerializer<BoolArgumentType>(BoolArgumentType::bool));
        ArgumentTypes.register("brigadier:float", FloatArgumentType.class, new FloatArgumentSerializer());
        ArgumentTypes.register("brigadier:double", DoubleArgumentType.class, new DoubleArgumentSerializer());
        ArgumentTypes.register("brigadier:integer", IntegerArgumentType.class, new IntegerArgumentSerializer());
        ArgumentTypes.register("brigadier:long", LongArgumentType.class, new LongArgumentSerializer());
        ArgumentTypes.register("brigadier:string", StringArgumentType.class, new StringArgumentSerializer());
    }

    public static byte createNumberFlags(boolean bl, boolean bl2) {
        byte by = 0;
        if (bl) {
            by = (byte)(by | true ? 1 : 0);
        }
        if (bl2) {
            by = (byte)(by | 2);
        }
        return by;
    }

    public static boolean numberHasMin(byte by) {
        return (by & 1) != 0;
    }

    public static boolean numberHasMax(byte by) {
        return (by & 2) != 0;
    }
}

