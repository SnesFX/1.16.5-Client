/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.builder.ArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.SimpleCommandExceptionType
 */
package net.minecraft.server.commands.data;

import com.mojang.brigadier.Message;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Locale;
import java.util.UUID;
import java.util.function.Function;
import net.minecraft.advancements.critereon.NbtPredicate;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.NbtPathArgument;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.commands.data.DataAccessor;
import net.minecraft.server.commands.data.DataCommands;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public class EntityDataAccessor
implements DataAccessor {
    private static final SimpleCommandExceptionType ERROR_NO_PLAYERS = new SimpleCommandExceptionType((Message)new TranslatableComponent("commands.data.entity.invalid"));
    public static final Function<String, DataCommands.DataProvider> PROVIDER = string -> new DataCommands.DataProvider((String)string){
        final /* synthetic */ String val$arg;
        {
            this.val$arg = string;
        }

        @Override
        public DataAccessor access(CommandContext<CommandSourceStack> commandContext) throws CommandSyntaxException {
            return new EntityDataAccessor(EntityArgument.getEntity(commandContext, this.val$arg));
        }

        @Override
        public ArgumentBuilder<CommandSourceStack, ?> wrap(ArgumentBuilder<CommandSourceStack, ?> argumentBuilder, Function<ArgumentBuilder<CommandSourceStack, ?>, ArgumentBuilder<CommandSourceStack, ?>> function) {
            return argumentBuilder.then(Commands.literal("entity").then(function.apply((ArgumentBuilder<CommandSourceStack, ?>)Commands.argument(this.val$arg, EntityArgument.entity()))));
        }
    };
    private final Entity entity;

    public EntityDataAccessor(Entity entity) {
        this.entity = entity;
    }

    @Override
    public void setData(CompoundTag compoundTag) throws CommandSyntaxException {
        if (this.entity instanceof Player) {
            throw ERROR_NO_PLAYERS.create();
        }
        UUID uUID = this.entity.getUUID();
        this.entity.load(compoundTag);
        this.entity.setUUID(uUID);
    }

    @Override
    public CompoundTag getData() {
        return NbtPredicate.getEntityTagToCompare(this.entity);
    }

    @Override
    public Component getModifiedSuccess() {
        return new TranslatableComponent("commands.data.entity.modified", this.entity.getDisplayName());
    }

    @Override
    public Component getPrintSuccess(Tag tag) {
        return new TranslatableComponent("commands.data.entity.query", this.entity.getDisplayName(), tag.getPrettyDisplay());
    }

    @Override
    public Component getPrintSuccess(NbtPathArgument.NbtPath nbtPath, double d, int n) {
        return new TranslatableComponent("commands.data.entity.get", nbtPath, this.entity.getDisplayName(), String.format(Locale.ROOT, "%.2f", d), n);
    }

}

