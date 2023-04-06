/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.brigadier.Command
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.builder.ArgumentBuilder
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.builder.RequiredArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType
 *  com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType$Function
 *  com.mojang.brigadier.exceptions.SimpleCommandExceptionType
 *  com.mojang.brigadier.tree.LiteralCommandNode
 *  javax.annotation.Nullable
 */
package net.minecraft.server.commands;

import com.google.common.collect.Lists;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.blocks.BlockInput;
import net.minecraft.commands.arguments.blocks.BlockPredicateArgument;
import net.minecraft.commands.arguments.blocks.BlockStateArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.commands.SetBlockCommand;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Clearable;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public class FillCommand {
    private static final Dynamic2CommandExceptionType ERROR_AREA_TOO_LARGE = new Dynamic2CommandExceptionType((object, object2) -> new TranslatableComponent("commands.fill.toobig", object, object2));
    private static final BlockInput HOLLOW_CORE = new BlockInput(Blocks.AIR.defaultBlockState(), Collections.emptySet(), null);
    private static final SimpleCommandExceptionType ERROR_FAILED = new SimpleCommandExceptionType((Message)new TranslatableComponent("commands.fill.failed"));

    public static void register(CommandDispatcher<CommandSourceStack> commandDispatcher) {
        commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("fill").requires(commandSourceStack -> commandSourceStack.hasPermission(2))).then(Commands.argument("from", BlockPosArgument.blockPos()).then(Commands.argument("to", BlockPosArgument.blockPos()).then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("block", BlockStateArgument.block()).executes(commandContext -> FillCommand.fillBlocks((CommandSourceStack)commandContext.getSource(), new BoundingBox(BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)commandContext, "from"), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)commandContext, "to")), BlockStateArgument.getBlock((CommandContext<CommandSourceStack>)commandContext, "block"), Mode.REPLACE, null))).then(((LiteralArgumentBuilder)Commands.literal("replace").executes(commandContext -> FillCommand.fillBlocks((CommandSourceStack)commandContext.getSource(), new BoundingBox(BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)commandContext, "from"), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)commandContext, "to")), BlockStateArgument.getBlock((CommandContext<CommandSourceStack>)commandContext, "block"), Mode.REPLACE, null))).then(Commands.argument("filter", BlockPredicateArgument.blockPredicate()).executes(commandContext -> FillCommand.fillBlocks((CommandSourceStack)commandContext.getSource(), new BoundingBox(BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)commandContext, "from"), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)commandContext, "to")), BlockStateArgument.getBlock((CommandContext<CommandSourceStack>)commandContext, "block"), Mode.REPLACE, BlockPredicateArgument.getBlockPredicate((CommandContext<CommandSourceStack>)commandContext, "filter")))))).then(Commands.literal("keep").executes(commandContext -> FillCommand.fillBlocks((CommandSourceStack)commandContext.getSource(), new BoundingBox(BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)commandContext, "from"), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)commandContext, "to")), BlockStateArgument.getBlock((CommandContext<CommandSourceStack>)commandContext, "block"), Mode.REPLACE, blockInWorld -> blockInWorld.getLevel().isEmptyBlock(blockInWorld.getPos()))))).then(Commands.literal("outline").executes(commandContext -> FillCommand.fillBlocks((CommandSourceStack)commandContext.getSource(), new BoundingBox(BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)commandContext, "from"), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)commandContext, "to")), BlockStateArgument.getBlock((CommandContext<CommandSourceStack>)commandContext, "block"), Mode.OUTLINE, null)))).then(Commands.literal("hollow").executes(commandContext -> FillCommand.fillBlocks((CommandSourceStack)commandContext.getSource(), new BoundingBox(BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)commandContext, "from"), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)commandContext, "to")), BlockStateArgument.getBlock((CommandContext<CommandSourceStack>)commandContext, "block"), Mode.HOLLOW, null)))).then(Commands.literal("destroy").executes(commandContext -> FillCommand.fillBlocks((CommandSourceStack)commandContext.getSource(), new BoundingBox(BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)commandContext, "from"), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)commandContext, "to")), BlockStateArgument.getBlock((CommandContext<CommandSourceStack>)commandContext, "block"), Mode.DESTROY, null)))))));
    }

    private static int fillBlocks(CommandSourceStack commandSourceStack, BoundingBox boundingBox, BlockInput blockInput, Mode mode, @Nullable Predicate<BlockInWorld> predicate) throws CommandSyntaxException {
        Object object;
        int n = boundingBox.getXSpan() * boundingBox.getYSpan() * boundingBox.getZSpan();
        if (n > 32768) {
            throw ERROR_AREA_TOO_LARGE.create((Object)32768, (Object)n);
        }
        ArrayList arrayList = Lists.newArrayList();
        ServerLevel serverLevel = commandSourceStack.getLevel();
        int n2 = 0;
        for (BlockPos blockPos : BlockPos.betweenClosed(boundingBox.x0, boundingBox.y0, boundingBox.z0, boundingBox.x1, boundingBox.y1, boundingBox.z1)) {
            if (predicate != null && !predicate.test(new BlockInWorld(serverLevel, blockPos, true)) || (object = mode.filter.filter(boundingBox, blockPos, blockInput, serverLevel)) == null) continue;
            BlockEntity blockEntity = serverLevel.getBlockEntity(blockPos);
            Clearable.tryClear(blockEntity);
            if (!((BlockInput)object).place(serverLevel, blockPos, 2)) continue;
            arrayList.add(blockPos.immutable());
            ++n2;
        }
        for (BlockPos blockPos : arrayList) {
            object = serverLevel.getBlockState(blockPos).getBlock();
            serverLevel.blockUpdated(blockPos, (Block)object);
        }
        if (n2 == 0) {
            throw ERROR_FAILED.create();
        }
        commandSourceStack.sendSuccess(new TranslatableComponent("commands.fill.success", n2), true);
        return n2;
    }

    static enum Mode {
        REPLACE((boundingBox, blockPos, blockInput, serverLevel) -> blockInput),
        OUTLINE((boundingBox, blockPos, blockInput, serverLevel) -> {
            if (blockPos.getX() == boundingBox.x0 || blockPos.getX() == boundingBox.x1 || blockPos.getY() == boundingBox.y0 || blockPos.getY() == boundingBox.y1 || blockPos.getZ() == boundingBox.z0 || blockPos.getZ() == boundingBox.z1) {
                return blockInput;
            }
            return null;
        }),
        HOLLOW((boundingBox, blockPos, blockInput, serverLevel) -> {
            if (blockPos.getX() == boundingBox.x0 || blockPos.getX() == boundingBox.x1 || blockPos.getY() == boundingBox.y0 || blockPos.getY() == boundingBox.y1 || blockPos.getZ() == boundingBox.z0 || blockPos.getZ() == boundingBox.z1) {
                return blockInput;
            }
            return HOLLOW_CORE;
        }),
        DESTROY((boundingBox, blockPos, blockInput, serverLevel) -> {
            serverLevel.destroyBlock(blockPos, true);
            return blockInput;
        });
        
        public final SetBlockCommand.Filter filter;

        private Mode(SetBlockCommand.Filter filter) {
            this.filter = filter;
        }
    }

}

