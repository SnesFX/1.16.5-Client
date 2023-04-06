/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.ImmutableStringReader
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.SimpleCommandExceptionType
 */
package net.minecraft.commands.arguments;

import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Arrays;
import java.util.Collection;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.coordinates.WorldCoordinate;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec2;

public class AngleArgument
implements ArgumentType<SingleAngle> {
    private static final Collection<String> EXAMPLES = Arrays.asList("0", "~", "~-5");
    public static final SimpleCommandExceptionType ERROR_NOT_COMPLETE = new SimpleCommandExceptionType((Message)new TranslatableComponent("argument.angle.incomplete"));

    public static AngleArgument angle() {
        return new AngleArgument();
    }

    public static float getAngle(CommandContext<CommandSourceStack> commandContext, String string) {
        return ((SingleAngle)commandContext.getArgument(string, SingleAngle.class)).getAngle((CommandSourceStack)commandContext.getSource());
    }

    public SingleAngle parse(StringReader stringReader) throws CommandSyntaxException {
        if (!stringReader.canRead()) {
            throw ERROR_NOT_COMPLETE.createWithContext((ImmutableStringReader)stringReader);
        }
        boolean bl = WorldCoordinate.isRelative(stringReader);
        float f = stringReader.canRead() && stringReader.peek() != ' ' ? stringReader.readFloat() : 0.0f;
        return new SingleAngle(f, bl);
    }

    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    public /* synthetic */ Object parse(StringReader stringReader) throws CommandSyntaxException {
        return this.parse(stringReader);
    }

    public static final class SingleAngle {
        private final float angle;
        private final boolean isRelative;

        private SingleAngle(float f, boolean bl) {
            this.angle = f;
            this.isRelative = bl;
        }

        public float getAngle(CommandSourceStack commandSourceStack) {
            return Mth.wrapDegrees(this.isRelative ? this.angle + commandSourceStack.getRotation().y : this.angle);
        }
    }

}

