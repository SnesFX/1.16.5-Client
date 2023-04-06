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
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.blocks.BlockPredicateArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Clearable;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerTickList;
import net.minecraft.world.level.TickList;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public class CloneCommands {
    private static final SimpleCommandExceptionType ERROR_OVERLAP = new SimpleCommandExceptionType((Message)new TranslatableComponent("commands.clone.overlap"));
    private static final Dynamic2CommandExceptionType ERROR_AREA_TOO_LARGE = new Dynamic2CommandExceptionType((object, object2) -> new TranslatableComponent("commands.clone.toobig", object, object2));
    private static final SimpleCommandExceptionType ERROR_FAILED = new SimpleCommandExceptionType((Message)new TranslatableComponent("commands.clone.failed"));
    public static final Predicate<BlockInWorld> FILTER_AIR = blockInWorld -> !blockInWorld.getState().isAir();

    public static void register(CommandDispatcher<CommandSourceStack> commandDispatcher) {
        commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("clone").requires(commandSourceStack -> commandSourceStack.hasPermission(2))).then(Commands.argument("begin", BlockPosArgument.blockPos()).then(Commands.argument("end", BlockPosArgument.blockPos()).then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("destination", BlockPosArgument.blockPos()).executes(commandContext -> CloneCommands.clone((CommandSourceStack)commandContext.getSource(), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)commandContext, "begin"), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)commandContext, "end"), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)commandContext, "destination"), blockInWorld -> true, Mode.NORMAL))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("replace").executes(commandContext -> CloneCommands.clone((CommandSourceStack)commandContext.getSource(), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)commandContext, "begin"), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)commandContext, "end"), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)commandContext, "destination"), blockInWorld -> true, Mode.NORMAL))).then(Commands.literal("force").executes(commandContext -> CloneCommands.clone((CommandSourceStack)commandContext.getSource(), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)commandContext, "begin"), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)commandContext, "end"), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)commandContext, "destination"), blockInWorld -> true, Mode.FORCE)))).then(Commands.literal("move").executes(commandContext -> CloneCommands.clone((CommandSourceStack)commandContext.getSource(), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)commandContext, "begin"), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)commandContext, "end"), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)commandContext, "destination"), blockInWorld -> true, Mode.MOVE)))).then(Commands.literal("normal").executes(commandContext -> CloneCommands.clone((CommandSourceStack)commandContext.getSource(), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)commandContext, "begin"), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)commandContext, "end"), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)commandContext, "destination"), blockInWorld -> true, Mode.NORMAL))))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("masked").executes(commandContext -> CloneCommands.clone((CommandSourceStack)commandContext.getSource(), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)commandContext, "begin"), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)commandContext, "end"), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)commandContext, "destination"), FILTER_AIR, Mode.NORMAL))).then(Commands.literal("force").executes(commandContext -> CloneCommands.clone((CommandSourceStack)commandContext.getSource(), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)commandContext, "begin"), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)commandContext, "end"), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)commandContext, "destination"), FILTER_AIR, Mode.FORCE)))).then(Commands.literal("move").executes(commandContext -> CloneCommands.clone((CommandSourceStack)commandContext.getSource(), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)commandContext, "begin"), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)commandContext, "end"), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)commandContext, "destination"), FILTER_AIR, Mode.MOVE)))).then(Commands.literal("normal").executes(commandContext -> CloneCommands.clone((CommandSourceStack)commandContext.getSource(), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)commandContext, "begin"), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)commandContext, "end"), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)commandContext, "destination"), FILTER_AIR, Mode.NORMAL))))).then(Commands.literal("filtered").then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("filter", BlockPredicateArgument.blockPredicate()).executes(commandContext -> CloneCommands.clone((CommandSourceStack)commandContext.getSource(), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)commandContext, "begin"), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)commandContext, "end"), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)commandContext, "destination"), BlockPredicateArgument.getBlockPredicate((CommandContext<CommandSourceStack>)commandContext, "filter"), Mode.NORMAL))).then(Commands.literal("force").executes(commandContext -> CloneCommands.clone((CommandSourceStack)commandContext.getSource(), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)commandContext, "begin"), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)commandContext, "end"), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)commandContext, "destination"), BlockPredicateArgument.getBlockPredicate((CommandContext<CommandSourceStack>)commandContext, "filter"), Mode.FORCE)))).then(Commands.literal("move").executes(commandContext -> CloneCommands.clone((CommandSourceStack)commandContext.getSource(), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)commandContext, "begin"), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)commandContext, "end"), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)commandContext, "destination"), BlockPredicateArgument.getBlockPredicate((CommandContext<CommandSourceStack>)commandContext, "filter"), Mode.MOVE)))).then(Commands.literal("normal").executes(commandContext -> CloneCommands.clone((CommandSourceStack)commandContext.getSource(), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)commandContext, "begin"), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)commandContext, "end"), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)commandContext, "destination"), BlockPredicateArgument.getBlockPredicate((CommandContext<CommandSourceStack>)commandContext, "filter"), Mode.NORMAL)))))))));
    }

    private static int clone(CommandSourceStack commandSourceStack, BlockPos blockPos, BlockPos blockPos2, BlockPos blockPos3, Predicate<BlockInWorld> predicate, Mode mode) throws CommandSyntaxException {
        Object object3;
        Object object2;
        BoundingBox boundingBox = new BoundingBox(blockPos, blockPos2);
        BlockPos blockPos4 = blockPos3.offset(boundingBox.getLength());
        BoundingBox boundingBox2 = new BoundingBox(blockPos3, blockPos4);
        if (!mode.canOverlap() && boundingBox2.intersects(boundingBox)) {
            throw ERROR_OVERLAP.create();
        }
        int n = boundingBox.getXSpan() * boundingBox.getYSpan() * boundingBox.getZSpan();
        if (n > 32768) {
            throw ERROR_AREA_TOO_LARGE.create((Object)32768, (Object)n);
        }
        ServerLevel serverLevel = commandSourceStack.getLevel();
        if (!serverLevel.hasChunksAt(blockPos, blockPos2) || !serverLevel.hasChunksAt(blockPos3, blockPos4)) {
            throw BlockPosArgument.ERROR_NOT_LOADED.create();
        }
        ArrayList arrayList = Lists.newArrayList();
        ArrayList arrayList2 = Lists.newArrayList();
        ArrayList arrayList3 = Lists.newArrayList();
        LinkedList linkedList = Lists.newLinkedList();
        BlockPos blockPos5 = new BlockPos(boundingBox2.x0 - boundingBox.x0, boundingBox2.y0 - boundingBox.y0, boundingBox2.z0 - boundingBox.z0);
        for (int i = boundingBox.z0; i <= boundingBox.z1; ++i) {
            for (int j = boundingBox.y0; j <= boundingBox.y1; ++j) {
                for (int k = boundingBox.x0; k <= boundingBox.x1; ++k) {
                    Iterator iterator = new BlockPos(k, j, i);
                    object3 = ((BlockPos)((Object)iterator)).offset(blockPos5);
                    object2 = new BlockInWorld(serverLevel, (BlockPos)((Object)iterator), false);
                    BlockState blockState = ((BlockInWorld)object2).getState();
                    if (!predicate.test((BlockInWorld)object2)) continue;
                    BlockEntity blockEntity = serverLevel.getBlockEntity((BlockPos)((Object)iterator));
                    if (blockEntity != null) {
                        CompoundTag compoundTag = blockEntity.save(new CompoundTag());
                        arrayList2.add(new CloneBlockInfo((BlockPos)object3, blockState, compoundTag));
                        linkedList.addLast(iterator);
                        continue;
                    }
                    if (blockState.isSolidRender(serverLevel, (BlockPos)((Object)iterator)) || blockState.isCollisionShapeFullBlock(serverLevel, (BlockPos)((Object)iterator))) {
                        arrayList.add(new CloneBlockInfo((BlockPos)object3, blockState, null));
                        linkedList.addLast(iterator);
                        continue;
                    }
                    arrayList3.add(new CloneBlockInfo((BlockPos)object3, blockState, null));
                    linkedList.addFirst(iterator);
                }
            }
        }
        if (mode == Mode.MOVE) {
            for (BlockPos blockPos6 : linkedList) {
                BlockEntity blockEntity = serverLevel.getBlockEntity(blockPos6);
                Clearable.tryClear(blockEntity);
                serverLevel.setBlock(blockPos6, Blocks.BARRIER.defaultBlockState(), 2);
            }
            for (BlockPos blockPos7 : linkedList) {
                serverLevel.setBlock(blockPos7, Blocks.AIR.defaultBlockState(), 3);
            }
        }
        ArrayList arrayList4 = Lists.newArrayList();
        arrayList4.addAll(arrayList);
        arrayList4.addAll(arrayList2);
        arrayList4.addAll(arrayList3);
        List list = Lists.reverse((List)arrayList4);
        for (Iterator iterator : list) {
            object3 = serverLevel.getBlockEntity(((CloneBlockInfo)iterator).pos);
            Clearable.tryClear(object3);
            serverLevel.setBlock(((CloneBlockInfo)iterator).pos, Blocks.BARRIER.defaultBlockState(), 2);
        }
        int n2 = 0;
        for (Object object3 : arrayList4) {
            if (!serverLevel.setBlock(((CloneBlockInfo)object3).pos, ((CloneBlockInfo)object3).state, 2)) continue;
            ++n2;
        }
        for (Object object3 : arrayList2) {
            object2 = serverLevel.getBlockEntity(((CloneBlockInfo)object3).pos);
            if (((CloneBlockInfo)object3).tag != null && object2 != null) {
                ((CloneBlockInfo)object3).tag.putInt("x", ((CloneBlockInfo)object3).pos.getX());
                ((CloneBlockInfo)object3).tag.putInt("y", ((CloneBlockInfo)object3).pos.getY());
                ((CloneBlockInfo)object3).tag.putInt("z", ((CloneBlockInfo)object3).pos.getZ());
                ((BlockEntity)object2).load(((CloneBlockInfo)object3).state, ((CloneBlockInfo)object3).tag);
                ((BlockEntity)object2).setChanged();
            }
            serverLevel.setBlock(((CloneBlockInfo)object3).pos, ((CloneBlockInfo)object3).state, 2);
        }
        for (Object object3 : list) {
            serverLevel.blockUpdated(((CloneBlockInfo)object3).pos, ((CloneBlockInfo)object3).state.getBlock());
        }
        ((ServerTickList)serverLevel.getBlockTicks()).copy(boundingBox, blockPos5);
        if (n2 == 0) {
            throw ERROR_FAILED.create();
        }
        commandSourceStack.sendSuccess(new TranslatableComponent("commands.clone.success", n2), true);
        return n2;
    }

    static class CloneBlockInfo {
        public final BlockPos pos;
        public final BlockState state;
        @Nullable
        public final CompoundTag tag;

        public CloneBlockInfo(BlockPos blockPos, BlockState blockState, @Nullable CompoundTag compoundTag) {
            this.pos = blockPos;
            this.state = blockState;
            this.tag = compoundTag;
        }
    }

    static enum Mode {
        FORCE(true),
        MOVE(true),
        NORMAL(false);
        
        private final boolean canOverlap;

        private Mode(boolean bl) {
            this.canOverlap = bl;
        }

        public boolean canOverlap() {
            return this.canOverlap;
        }
    }

}

