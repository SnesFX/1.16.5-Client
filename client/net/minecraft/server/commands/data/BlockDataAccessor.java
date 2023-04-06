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
import java.util.function.Function;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.NbtPathArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.commands.data.DataAccessor;
import net.minecraft.server.commands.data.DataCommands;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class BlockDataAccessor
implements DataAccessor {
    private static final SimpleCommandExceptionType ERROR_NOT_A_BLOCK_ENTITY = new SimpleCommandExceptionType((Message)new TranslatableComponent("commands.data.block.invalid"));
    public static final Function<String, DataCommands.DataProvider> PROVIDER = string -> new DataCommands.DataProvider((String)string){
        final /* synthetic */ String val$argPrefix;
        {
            this.val$argPrefix = string;
        }

        @Override
        public DataAccessor access(CommandContext<CommandSourceStack> commandContext) throws CommandSyntaxException {
            BlockPos blockPos = BlockPosArgument.getLoadedBlockPos(commandContext, this.val$argPrefix + "Pos");
            BlockEntity blockEntity = ((CommandSourceStack)commandContext.getSource()).getLevel().getBlockEntity(blockPos);
            if (blockEntity == null) {
                throw ERROR_NOT_A_BLOCK_ENTITY.create();
            }
            return new BlockDataAccessor(blockEntity, blockPos);
        }

        @Override
        public ArgumentBuilder<CommandSourceStack, ?> wrap(ArgumentBuilder<CommandSourceStack, ?> argumentBuilder, Function<ArgumentBuilder<CommandSourceStack, ?>, ArgumentBuilder<CommandSourceStack, ?>> function) {
            return argumentBuilder.then(Commands.literal("block").then(function.apply((ArgumentBuilder<CommandSourceStack, ?>)Commands.argument(this.val$argPrefix + "Pos", BlockPosArgument.blockPos()))));
        }
    };
    private final BlockEntity entity;
    private final BlockPos pos;

    public BlockDataAccessor(BlockEntity blockEntity, BlockPos blockPos) {
        this.entity = blockEntity;
        this.pos = blockPos;
    }

    @Override
    public void setData(CompoundTag compoundTag) {
        compoundTag.putInt("x", this.pos.getX());
        compoundTag.putInt("y", this.pos.getY());
        compoundTag.putInt("z", this.pos.getZ());
        BlockState blockState = this.entity.getLevel().getBlockState(this.pos);
        this.entity.load(blockState, compoundTag);
        this.entity.setChanged();
        this.entity.getLevel().sendBlockUpdated(this.pos, blockState, blockState, 3);
    }

    @Override
    public CompoundTag getData() {
        return this.entity.save(new CompoundTag());
    }

    @Override
    public Component getModifiedSuccess() {
        return new TranslatableComponent("commands.data.block.modified", this.pos.getX(), this.pos.getY(), this.pos.getZ());
    }

    @Override
    public Component getPrintSuccess(Tag tag) {
        return new TranslatableComponent("commands.data.block.query", this.pos.getX(), this.pos.getY(), this.pos.getZ(), tag.getPrettyDisplay());
    }

    @Override
    public Component getPrintSuccess(NbtPathArgument.NbtPath nbtPath, double d, int n) {
        return new TranslatableComponent("commands.data.block.get", nbtPath, this.pos.getX(), this.pos.getY(), this.pos.getZ(), String.format(Locale.ROOT, "%.2f", d), n);
    }

}

