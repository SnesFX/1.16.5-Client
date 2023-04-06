/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.brigadier.Command
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.RedirectModifier
 *  com.mojang.brigadier.ResultConsumer
 *  com.mojang.brigadier.SingleRedirectModifier
 *  com.mojang.brigadier.arguments.DoubleArgumentType
 *  com.mojang.brigadier.builder.ArgumentBuilder
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.builder.RequiredArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType
 *  com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType$Function
 *  com.mojang.brigadier.exceptions.DynamicCommandExceptionType
 *  com.mojang.brigadier.exceptions.SimpleCommandExceptionType
 *  com.mojang.brigadier.suggestion.SuggestionProvider
 *  com.mojang.brigadier.suggestion.SuggestionsBuilder
 *  com.mojang.brigadier.tree.CommandNode
 *  com.mojang.brigadier.tree.LiteralCommandNode
 *  com.mojang.brigadier.tree.RootCommandNode
 */
package net.minecraft.server.commands;

import com.google.common.collect.Lists;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.RedirectModifier;
import com.mojang.brigadier.ResultConsumer;
import com.mojang.brigadier.SingleRedirectModifier;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.OptionalInt;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiPredicate;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.DimensionArgument;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.NbtPathArgument;
import net.minecraft.commands.arguments.ObjectiveArgument;
import net.minecraft.commands.arguments.RangeArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.arguments.ScoreHolderArgument;
import net.minecraft.commands.arguments.blocks.BlockPredicateArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.commands.arguments.coordinates.RotationArgument;
import net.minecraft.commands.arguments.coordinates.SwizzleArgument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.ShortTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerScoreboard;
import net.minecraft.server.bossevents.CustomBossEvent;
import net.minecraft.server.commands.BossBarCommands;
import net.minecraft.server.commands.data.DataAccessor;
import net.minecraft.server.commands.data.DataCommands;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.PredicateManager;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Score;
import net.minecraft.world.scores.Scoreboard;

public class ExecuteCommand {
    private static final Dynamic2CommandExceptionType ERROR_AREA_TOO_LARGE = new Dynamic2CommandExceptionType((object, object2) -> new TranslatableComponent("commands.execute.blocks.toobig", object, object2));
    private static final SimpleCommandExceptionType ERROR_CONDITIONAL_FAILED = new SimpleCommandExceptionType((Message)new TranslatableComponent("commands.execute.conditional.fail"));
    private static final DynamicCommandExceptionType ERROR_CONDITIONAL_FAILED_COUNT = new DynamicCommandExceptionType(object -> new TranslatableComponent("commands.execute.conditional.fail_count", object));
    private static final BinaryOperator<ResultConsumer<CommandSourceStack>> CALLBACK_CHAINER = (resultConsumer, resultConsumer2) -> (commandContext, bl, n) -> {
        resultConsumer.onCommandComplete(commandContext, bl, n);
        resultConsumer2.onCommandComplete(commandContext, bl, n);
    };
    private static final SuggestionProvider<CommandSourceStack> SUGGEST_PREDICATE = (commandContext, suggestionsBuilder) -> {
        PredicateManager predicateManager = ((CommandSourceStack)commandContext.getSource()).getServer().getPredicateManager();
        return SharedSuggestionProvider.suggestResource(predicateManager.getKeys(), suggestionsBuilder);
    };

    public static void register(CommandDispatcher<CommandSourceStack> commandDispatcher) {
        LiteralCommandNode literalCommandNode = commandDispatcher.register((LiteralArgumentBuilder)Commands.literal("execute").requires(commandSourceStack -> commandSourceStack.hasPermission(2)));
        commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("execute").requires(commandSourceStack -> commandSourceStack.hasPermission(2))).then(Commands.literal("run").redirect((CommandNode)commandDispatcher.getRoot()))).then(ExecuteCommand.addConditionals((CommandNode<CommandSourceStack>)literalCommandNode, Commands.literal("if"), true))).then(ExecuteCommand.addConditionals((CommandNode<CommandSourceStack>)literalCommandNode, Commands.literal("unless"), false))).then(Commands.literal("as").then(Commands.argument("targets", EntityArgument.entities()).fork((CommandNode)literalCommandNode, commandContext -> {
            ArrayList arrayList = Lists.newArrayList();
            for (Entity entity : EntityArgument.getOptionalEntities((CommandContext<CommandSourceStack>)commandContext, "targets")) {
                arrayList.add(((CommandSourceStack)commandContext.getSource()).withEntity(entity));
            }
            return arrayList;
        })))).then(Commands.literal("at").then(Commands.argument("targets", EntityArgument.entities()).fork((CommandNode)literalCommandNode, commandContext -> {
            ArrayList arrayList = Lists.newArrayList();
            for (Entity entity : EntityArgument.getOptionalEntities((CommandContext<CommandSourceStack>)commandContext, "targets")) {
                arrayList.add(((CommandSourceStack)commandContext.getSource()).withLevel((ServerLevel)entity.level).withPosition(entity.position()).withRotation(entity.getRotationVector()));
            }
            return arrayList;
        })))).then(((LiteralArgumentBuilder)Commands.literal("store").then(ExecuteCommand.wrapStores((LiteralCommandNode<CommandSourceStack>)literalCommandNode, Commands.literal("result"), true))).then(ExecuteCommand.wrapStores((LiteralCommandNode<CommandSourceStack>)literalCommandNode, Commands.literal("success"), false)))).then(((LiteralArgumentBuilder)Commands.literal("positioned").then(Commands.argument("pos", Vec3Argument.vec3()).redirect((CommandNode)literalCommandNode, commandContext -> ((CommandSourceStack)commandContext.getSource()).withPosition(Vec3Argument.getVec3((CommandContext<CommandSourceStack>)commandContext, "pos")).withAnchor(EntityAnchorArgument.Anchor.FEET)))).then(Commands.literal("as").then(Commands.argument("targets", EntityArgument.entities()).fork((CommandNode)literalCommandNode, commandContext -> {
            ArrayList arrayList = Lists.newArrayList();
            for (Entity entity : EntityArgument.getOptionalEntities((CommandContext<CommandSourceStack>)commandContext, "targets")) {
                arrayList.add(((CommandSourceStack)commandContext.getSource()).withPosition(entity.position()));
            }
            return arrayList;
        }))))).then(((LiteralArgumentBuilder)Commands.literal("rotated").then(Commands.argument("rot", RotationArgument.rotation()).redirect((CommandNode)literalCommandNode, commandContext -> ((CommandSourceStack)commandContext.getSource()).withRotation(RotationArgument.getRotation((CommandContext<CommandSourceStack>)commandContext, "rot").getRotation((CommandSourceStack)commandContext.getSource()))))).then(Commands.literal("as").then(Commands.argument("targets", EntityArgument.entities()).fork((CommandNode)literalCommandNode, commandContext -> {
            ArrayList arrayList = Lists.newArrayList();
            for (Entity entity : EntityArgument.getOptionalEntities((CommandContext<CommandSourceStack>)commandContext, "targets")) {
                arrayList.add(((CommandSourceStack)commandContext.getSource()).withRotation(entity.getRotationVector()));
            }
            return arrayList;
        }))))).then(((LiteralArgumentBuilder)Commands.literal("facing").then(Commands.literal("entity").then(Commands.argument("targets", EntityArgument.entities()).then(Commands.argument("anchor", EntityAnchorArgument.anchor()).fork((CommandNode)literalCommandNode, commandContext -> {
            ArrayList arrayList = Lists.newArrayList();
            EntityAnchorArgument.Anchor anchor = EntityAnchorArgument.getAnchor((CommandContext<CommandSourceStack>)commandContext, "anchor");
            for (Entity entity : EntityArgument.getOptionalEntities((CommandContext<CommandSourceStack>)commandContext, "targets")) {
                arrayList.add(((CommandSourceStack)commandContext.getSource()).facing(entity, anchor));
            }
            return arrayList;
        }))))).then(Commands.argument("pos", Vec3Argument.vec3()).redirect((CommandNode)literalCommandNode, commandContext -> ((CommandSourceStack)commandContext.getSource()).facing(Vec3Argument.getVec3((CommandContext<CommandSourceStack>)commandContext, "pos")))))).then(Commands.literal("align").then(Commands.argument("axes", SwizzleArgument.swizzle()).redirect((CommandNode)literalCommandNode, commandContext -> ((CommandSourceStack)commandContext.getSource()).withPosition(((CommandSourceStack)commandContext.getSource()).getPosition().align(SwizzleArgument.getSwizzle((CommandContext<CommandSourceStack>)commandContext, "axes"))))))).then(Commands.literal("anchored").then(Commands.argument("anchor", EntityAnchorArgument.anchor()).redirect((CommandNode)literalCommandNode, commandContext -> ((CommandSourceStack)commandContext.getSource()).withAnchor(EntityAnchorArgument.getAnchor((CommandContext<CommandSourceStack>)commandContext, "anchor")))))).then(Commands.literal("in").then(Commands.argument("dimension", DimensionArgument.dimension()).redirect((CommandNode)literalCommandNode, commandContext -> ((CommandSourceStack)commandContext.getSource()).withLevel(DimensionArgument.getDimension((CommandContext<CommandSourceStack>)commandContext, "dimension"))))));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> wrapStores(LiteralCommandNode<CommandSourceStack> literalCommandNode, LiteralArgumentBuilder<CommandSourceStack> literalArgumentBuilder, boolean bl) {
        literalArgumentBuilder.then(Commands.literal("score").then(Commands.argument("targets", ScoreHolderArgument.scoreHolders()).suggests(ScoreHolderArgument.SUGGEST_SCORE_HOLDERS).then(Commands.argument("objective", ObjectiveArgument.objective()).redirect(literalCommandNode, commandContext -> ExecuteCommand.storeValue((CommandSourceStack)commandContext.getSource(), ScoreHolderArgument.getNamesWithDefaultWildcard((CommandContext<CommandSourceStack>)commandContext, "targets"), ObjectiveArgument.getObjective((CommandContext<CommandSourceStack>)commandContext, "objective"), bl)))));
        literalArgumentBuilder.then(Commands.literal("bossbar").then(((RequiredArgumentBuilder)Commands.argument("id", ResourceLocationArgument.id()).suggests(BossBarCommands.SUGGEST_BOSS_BAR).then(Commands.literal("value").redirect(literalCommandNode, commandContext -> ExecuteCommand.storeValue((CommandSourceStack)commandContext.getSource(), BossBarCommands.getBossBar((CommandContext<CommandSourceStack>)commandContext), true, bl)))).then(Commands.literal("max").redirect(literalCommandNode, commandContext -> ExecuteCommand.storeValue((CommandSourceStack)commandContext.getSource(), BossBarCommands.getBossBar((CommandContext<CommandSourceStack>)commandContext), false, bl)))));
        for (DataCommands.DataProvider dataProvider : DataCommands.TARGET_PROVIDERS) {
            dataProvider.wrap((ArgumentBuilder<CommandSourceStack, ?>)literalArgumentBuilder, argumentBuilder -> argumentBuilder.then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("path", NbtPathArgument.nbtPath()).then(Commands.literal("int").then(Commands.argument("scale", DoubleArgumentType.doubleArg()).redirect((CommandNode)literalCommandNode, commandContext -> ExecuteCommand.storeData((CommandSourceStack)commandContext.getSource(), dataProvider.access((CommandContext<CommandSourceStack>)commandContext), NbtPathArgument.getPath((CommandContext<CommandSourceStack>)commandContext, "path"), n -> IntTag.valueOf((int)((double)n * DoubleArgumentType.getDouble((CommandContext)commandContext, (String)"scale"))), bl))))).then(Commands.literal("float").then(Commands.argument("scale", DoubleArgumentType.doubleArg()).redirect((CommandNode)literalCommandNode, commandContext -> ExecuteCommand.storeData((CommandSourceStack)commandContext.getSource(), dataProvider.access((CommandContext<CommandSourceStack>)commandContext), NbtPathArgument.getPath((CommandContext<CommandSourceStack>)commandContext, "path"), n -> FloatTag.valueOf((float)((double)n * DoubleArgumentType.getDouble((CommandContext)commandContext, (String)"scale"))), bl))))).then(Commands.literal("short").then(Commands.argument("scale", DoubleArgumentType.doubleArg()).redirect((CommandNode)literalCommandNode, commandContext -> ExecuteCommand.storeData((CommandSourceStack)commandContext.getSource(), dataProvider.access((CommandContext<CommandSourceStack>)commandContext), NbtPathArgument.getPath((CommandContext<CommandSourceStack>)commandContext, "path"), n -> ShortTag.valueOf((short)((double)n * DoubleArgumentType.getDouble((CommandContext)commandContext, (String)"scale"))), bl))))).then(Commands.literal("long").then(Commands.argument("scale", DoubleArgumentType.doubleArg()).redirect((CommandNode)literalCommandNode, commandContext -> ExecuteCommand.storeData((CommandSourceStack)commandContext.getSource(), dataProvider.access((CommandContext<CommandSourceStack>)commandContext), NbtPathArgument.getPath((CommandContext<CommandSourceStack>)commandContext, "path"), n -> LongTag.valueOf((long)((double)n * DoubleArgumentType.getDouble((CommandContext)commandContext, (String)"scale"))), bl))))).then(Commands.literal("double").then(Commands.argument("scale", DoubleArgumentType.doubleArg()).redirect((CommandNode)literalCommandNode, commandContext -> ExecuteCommand.storeData((CommandSourceStack)commandContext.getSource(), dataProvider.access((CommandContext<CommandSourceStack>)commandContext), NbtPathArgument.getPath((CommandContext<CommandSourceStack>)commandContext, "path"), n -> DoubleTag.valueOf((double)n * DoubleArgumentType.getDouble((CommandContext)commandContext, (String)"scale")), bl))))).then(Commands.literal("byte").then(Commands.argument("scale", DoubleArgumentType.doubleArg()).redirect((CommandNode)literalCommandNode, commandContext -> ExecuteCommand.storeData((CommandSourceStack)commandContext.getSource(), dataProvider.access((CommandContext<CommandSourceStack>)commandContext), NbtPathArgument.getPath((CommandContext<CommandSourceStack>)commandContext, "path"), n -> ByteTag.valueOf((byte)((double)n * DoubleArgumentType.getDouble((CommandContext)commandContext, (String)"scale"))), bl))))));
        }
        return literalArgumentBuilder;
    }

    private static CommandSourceStack storeValue(CommandSourceStack commandSourceStack, Collection<String> collection, Objective objective, boolean bl) {
        ServerScoreboard serverScoreboard = commandSourceStack.getServer().getScoreboard();
        return commandSourceStack.withCallback((ResultConsumer<CommandSourceStack>)((ResultConsumer)(commandContext, bl2, n) -> {
            for (String string : collection) {
                Score score = serverScoreboard.getOrCreatePlayerScore(string, objective);
                int n2 = bl ? n : (bl2 ? 1 : 0);
                score.setScore(n2);
            }
        }), CALLBACK_CHAINER);
    }

    private static CommandSourceStack storeValue(CommandSourceStack commandSourceStack, CustomBossEvent customBossEvent, boolean bl, boolean bl2) {
        return commandSourceStack.withCallback((ResultConsumer<CommandSourceStack>)((ResultConsumer)(commandContext, bl3, n) -> {
            int n2;
            int n3 = bl2 ? n : (n2 = bl3 ? 1 : 0);
            if (bl) {
                customBossEvent.setValue(n2);
            } else {
                customBossEvent.setMax(n2);
            }
        }), CALLBACK_CHAINER);
    }

    private static CommandSourceStack storeData(CommandSourceStack commandSourceStack, DataAccessor dataAccessor, NbtPathArgument.NbtPath nbtPath, IntFunction<Tag> intFunction, boolean bl) {
        return commandSourceStack.withCallback((ResultConsumer<CommandSourceStack>)((ResultConsumer)(commandContext, bl2, n) -> {
            try {
                CompoundTag compoundTag = dataAccessor.getData();
                int n2 = bl ? n : (bl2 ? 1 : 0);
                nbtPath.set(compoundTag, () -> (Tag)intFunction.apply(n2));
                dataAccessor.setData(compoundTag);
            }
            catch (CommandSyntaxException commandSyntaxException) {
                // empty catch block
            }
        }), CALLBACK_CHAINER);
    }

    private static ArgumentBuilder<CommandSourceStack, ?> addConditionals(CommandNode<CommandSourceStack> commandNode, LiteralArgumentBuilder<CommandSourceStack> literalArgumentBuilder, boolean bl) {
        ((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)literalArgumentBuilder.then(Commands.literal("block").then(Commands.argument("pos", BlockPosArgument.blockPos()).then(ExecuteCommand.addConditional(commandNode, Commands.argument("block", BlockPredicateArgument.blockPredicate()), bl, commandContext -> BlockPredicateArgument.getBlockPredicate((CommandContext<CommandSourceStack>)commandContext, "block").test(new BlockInWorld(((CommandSourceStack)commandContext.getSource()).getLevel(), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)commandContext, "pos"), true))))))).then(Commands.literal("score").then(Commands.argument("target", ScoreHolderArgument.scoreHolder()).suggests(ScoreHolderArgument.SUGGEST_SCORE_HOLDERS).then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("targetObjective", ObjectiveArgument.objective()).then(Commands.literal("=").then(Commands.argument("source", ScoreHolderArgument.scoreHolder()).suggests(ScoreHolderArgument.SUGGEST_SCORE_HOLDERS).then(ExecuteCommand.addConditional(commandNode, Commands.argument("sourceObjective", ObjectiveArgument.objective()), bl, commandContext -> ExecuteCommand.checkScore((CommandContext<CommandSourceStack>)commandContext, Integer::equals)))))).then(Commands.literal("<").then(Commands.argument("source", ScoreHolderArgument.scoreHolder()).suggests(ScoreHolderArgument.SUGGEST_SCORE_HOLDERS).then(ExecuteCommand.addConditional(commandNode, Commands.argument("sourceObjective", ObjectiveArgument.objective()), bl, commandContext -> ExecuteCommand.checkScore((CommandContext<CommandSourceStack>)commandContext, (n, n2) -> n < n2)))))).then(Commands.literal("<=").then(Commands.argument("source", ScoreHolderArgument.scoreHolder()).suggests(ScoreHolderArgument.SUGGEST_SCORE_HOLDERS).then(ExecuteCommand.addConditional(commandNode, Commands.argument("sourceObjective", ObjectiveArgument.objective()), bl, commandContext -> ExecuteCommand.checkScore((CommandContext<CommandSourceStack>)commandContext, (n, n2) -> n <= n2)))))).then(Commands.literal(">").then(Commands.argument("source", ScoreHolderArgument.scoreHolder()).suggests(ScoreHolderArgument.SUGGEST_SCORE_HOLDERS).then(ExecuteCommand.addConditional(commandNode, Commands.argument("sourceObjective", ObjectiveArgument.objective()), bl, commandContext -> ExecuteCommand.checkScore((CommandContext<CommandSourceStack>)commandContext, (n, n2) -> n > n2)))))).then(Commands.literal(">=").then(Commands.argument("source", ScoreHolderArgument.scoreHolder()).suggests(ScoreHolderArgument.SUGGEST_SCORE_HOLDERS).then(ExecuteCommand.addConditional(commandNode, Commands.argument("sourceObjective", ObjectiveArgument.objective()), bl, commandContext -> ExecuteCommand.checkScore((CommandContext<CommandSourceStack>)commandContext, (n, n2) -> n >= n2)))))).then(Commands.literal("matches").then(ExecuteCommand.addConditional(commandNode, Commands.argument("range", RangeArgument.intRange()), bl, commandContext -> ExecuteCommand.checkScore((CommandContext<CommandSourceStack>)commandContext, RangeArgument.Ints.getRange((CommandContext<CommandSourceStack>)commandContext, "range"))))))))).then(Commands.literal("blocks").then(Commands.argument("start", BlockPosArgument.blockPos()).then(Commands.argument("end", BlockPosArgument.blockPos()).then(((RequiredArgumentBuilder)Commands.argument("destination", BlockPosArgument.blockPos()).then(ExecuteCommand.addIfBlocksConditional(commandNode, Commands.literal("all"), bl, false))).then(ExecuteCommand.addIfBlocksConditional(commandNode, Commands.literal("masked"), bl, true))))))).then(Commands.literal("entity").then(((RequiredArgumentBuilder)Commands.argument("entities", EntityArgument.entities()).fork(commandNode, commandContext -> ExecuteCommand.expect((CommandContext<CommandSourceStack>)commandContext, bl, !EntityArgument.getOptionalEntities((CommandContext<CommandSourceStack>)commandContext, "entities").isEmpty()))).executes(ExecuteCommand.createNumericConditionalHandler(bl, commandContext -> EntityArgument.getOptionalEntities((CommandContext<CommandSourceStack>)commandContext, "entities").size()))))).then(Commands.literal("predicate").then(ExecuteCommand.addConditional(commandNode, Commands.argument("predicate", ResourceLocationArgument.id()).suggests(SUGGEST_PREDICATE), bl, commandContext -> ExecuteCommand.checkCustomPredicate((CommandSourceStack)commandContext.getSource(), ResourceLocationArgument.getPredicate((CommandContext<CommandSourceStack>)commandContext, "predicate")))));
        for (DataCommands.DataProvider dataProvider : DataCommands.SOURCE_PROVIDERS) {
            literalArgumentBuilder.then(dataProvider.wrap((ArgumentBuilder<CommandSourceStack, ?>)Commands.literal("data"), argumentBuilder -> argumentBuilder.then(((RequiredArgumentBuilder)Commands.argument("path", NbtPathArgument.nbtPath()).fork(commandNode, commandContext -> ExecuteCommand.expect((CommandContext<CommandSourceStack>)commandContext, bl, ExecuteCommand.checkMatchingData(dataProvider.access((CommandContext<CommandSourceStack>)commandContext), NbtPathArgument.getPath((CommandContext<CommandSourceStack>)commandContext, "path")) > 0))).executes(ExecuteCommand.createNumericConditionalHandler(bl, commandContext -> ExecuteCommand.checkMatchingData(dataProvider.access((CommandContext<CommandSourceStack>)commandContext), NbtPathArgument.getPath((CommandContext<CommandSourceStack>)commandContext, "path")))))));
        }
        return literalArgumentBuilder;
    }

    private static Command<CommandSourceStack> createNumericConditionalHandler(boolean bl, CommandNumericPredicate commandNumericPredicate) {
        if (bl) {
            return commandContext -> {
                int n = commandNumericPredicate.test((CommandContext<CommandSourceStack>)commandContext);
                if (n > 0) {
                    ((CommandSourceStack)commandContext.getSource()).sendSuccess(new TranslatableComponent("commands.execute.conditional.pass_count", n), false);
                    return n;
                }
                throw ERROR_CONDITIONAL_FAILED.create();
            };
        }
        return commandContext -> {
            int n = commandNumericPredicate.test((CommandContext<CommandSourceStack>)commandContext);
            if (n == 0) {
                ((CommandSourceStack)commandContext.getSource()).sendSuccess(new TranslatableComponent("commands.execute.conditional.pass"), false);
                return 1;
            }
            throw ERROR_CONDITIONAL_FAILED_COUNT.create((Object)n);
        };
    }

    private static int checkMatchingData(DataAccessor dataAccessor, NbtPathArgument.NbtPath nbtPath) throws CommandSyntaxException {
        return nbtPath.countMatching(dataAccessor.getData());
    }

    private static boolean checkScore(CommandContext<CommandSourceStack> commandContext, BiPredicate<Integer, Integer> biPredicate) throws CommandSyntaxException {
        String string = ScoreHolderArgument.getName(commandContext, "target");
        Objective objective = ObjectiveArgument.getObjective(commandContext, "targetObjective");
        String string2 = ScoreHolderArgument.getName(commandContext, "source");
        Objective objective2 = ObjectiveArgument.getObjective(commandContext, "sourceObjective");
        ServerScoreboard serverScoreboard = ((CommandSourceStack)commandContext.getSource()).getServer().getScoreboard();
        if (!serverScoreboard.hasPlayerScore(string, objective) || !serverScoreboard.hasPlayerScore(string2, objective2)) {
            return false;
        }
        Score score = serverScoreboard.getOrCreatePlayerScore(string, objective);
        Score score2 = serverScoreboard.getOrCreatePlayerScore(string2, objective2);
        return biPredicate.test(score.getScore(), score2.getScore());
    }

    private static boolean checkScore(CommandContext<CommandSourceStack> commandContext, MinMaxBounds.Ints ints) throws CommandSyntaxException {
        String string = ScoreHolderArgument.getName(commandContext, "target");
        Objective objective = ObjectiveArgument.getObjective(commandContext, "targetObjective");
        ServerScoreboard serverScoreboard = ((CommandSourceStack)commandContext.getSource()).getServer().getScoreboard();
        if (!serverScoreboard.hasPlayerScore(string, objective)) {
            return false;
        }
        return ints.matches(serverScoreboard.getOrCreatePlayerScore(string, objective).getScore());
    }

    private static boolean checkCustomPredicate(CommandSourceStack commandSourceStack, LootItemCondition lootItemCondition) {
        ServerLevel serverLevel = commandSourceStack.getLevel();
        LootContext.Builder builder = new LootContext.Builder(serverLevel).withParameter(LootContextParams.ORIGIN, commandSourceStack.getPosition()).withOptionalParameter(LootContextParams.THIS_ENTITY, commandSourceStack.getEntity());
        return lootItemCondition.test(builder.create(LootContextParamSets.COMMAND));
    }

    private static Collection<CommandSourceStack> expect(CommandContext<CommandSourceStack> commandContext, boolean bl, boolean bl2) {
        if (bl2 == bl) {
            return Collections.singleton(commandContext.getSource());
        }
        return Collections.emptyList();
    }

    private static ArgumentBuilder<CommandSourceStack, ?> addConditional(CommandNode<CommandSourceStack> commandNode, ArgumentBuilder<CommandSourceStack, ?> argumentBuilder, boolean bl, CommandPredicate commandPredicate) {
        return argumentBuilder.fork(commandNode, commandContext -> ExecuteCommand.expect((CommandContext<CommandSourceStack>)commandContext, bl, commandPredicate.test((CommandContext<CommandSourceStack>)commandContext))).executes(commandContext -> {
            if (bl == commandPredicate.test((CommandContext<CommandSourceStack>)commandContext)) {
                ((CommandSourceStack)commandContext.getSource()).sendSuccess(new TranslatableComponent("commands.execute.conditional.pass"), false);
                return 1;
            }
            throw ERROR_CONDITIONAL_FAILED.create();
        });
    }

    private static ArgumentBuilder<CommandSourceStack, ?> addIfBlocksConditional(CommandNode<CommandSourceStack> commandNode, ArgumentBuilder<CommandSourceStack, ?> argumentBuilder, boolean bl, boolean bl2) {
        return argumentBuilder.fork(commandNode, commandContext -> ExecuteCommand.expect((CommandContext<CommandSourceStack>)commandContext, bl, ExecuteCommand.checkRegions((CommandContext<CommandSourceStack>)commandContext, bl2).isPresent())).executes(bl ? commandContext -> ExecuteCommand.checkIfRegions((CommandContext<CommandSourceStack>)commandContext, bl2) : commandContext -> ExecuteCommand.checkUnlessRegions((CommandContext<CommandSourceStack>)commandContext, bl2));
    }

    private static int checkIfRegions(CommandContext<CommandSourceStack> commandContext, boolean bl) throws CommandSyntaxException {
        OptionalInt optionalInt = ExecuteCommand.checkRegions(commandContext, bl);
        if (optionalInt.isPresent()) {
            ((CommandSourceStack)commandContext.getSource()).sendSuccess(new TranslatableComponent("commands.execute.conditional.pass_count", optionalInt.getAsInt()), false);
            return optionalInt.getAsInt();
        }
        throw ERROR_CONDITIONAL_FAILED.create();
    }

    private static int checkUnlessRegions(CommandContext<CommandSourceStack> commandContext, boolean bl) throws CommandSyntaxException {
        OptionalInt optionalInt = ExecuteCommand.checkRegions(commandContext, bl);
        if (optionalInt.isPresent()) {
            throw ERROR_CONDITIONAL_FAILED_COUNT.create((Object)optionalInt.getAsInt());
        }
        ((CommandSourceStack)commandContext.getSource()).sendSuccess(new TranslatableComponent("commands.execute.conditional.pass"), false);
        return 1;
    }

    private static OptionalInt checkRegions(CommandContext<CommandSourceStack> commandContext, boolean bl) throws CommandSyntaxException {
        return ExecuteCommand.checkRegions(((CommandSourceStack)commandContext.getSource()).getLevel(), BlockPosArgument.getLoadedBlockPos(commandContext, "start"), BlockPosArgument.getLoadedBlockPos(commandContext, "end"), BlockPosArgument.getLoadedBlockPos(commandContext, "destination"), bl);
    }

    private static OptionalInt checkRegions(ServerLevel serverLevel, BlockPos blockPos, BlockPos blockPos2, BlockPos blockPos3, boolean bl) throws CommandSyntaxException {
        BoundingBox boundingBox = new BoundingBox(blockPos, blockPos2);
        BoundingBox boundingBox2 = new BoundingBox(blockPos3, blockPos3.offset(boundingBox.getLength()));
        BlockPos blockPos4 = new BlockPos(boundingBox2.x0 - boundingBox.x0, boundingBox2.y0 - boundingBox.y0, boundingBox2.z0 - boundingBox.z0);
        int n = boundingBox.getXSpan() * boundingBox.getYSpan() * boundingBox.getZSpan();
        if (n > 32768) {
            throw ERROR_AREA_TOO_LARGE.create((Object)32768, (Object)n);
        }
        int n2 = 0;
        for (int i = boundingBox.z0; i <= boundingBox.z1; ++i) {
            for (int j = boundingBox.y0; j <= boundingBox.y1; ++j) {
                for (int k = boundingBox.x0; k <= boundingBox.x1; ++k) {
                    BlockPos blockPos5 = new BlockPos(k, j, i);
                    BlockPos blockPos6 = blockPos5.offset(blockPos4);
                    BlockState blockState = serverLevel.getBlockState(blockPos5);
                    if (bl && blockState.is(Blocks.AIR)) continue;
                    if (blockState != serverLevel.getBlockState(blockPos6)) {
                        return OptionalInt.empty();
                    }
                    BlockEntity blockEntity = serverLevel.getBlockEntity(blockPos5);
                    BlockEntity blockEntity2 = serverLevel.getBlockEntity(blockPos6);
                    if (blockEntity != null) {
                        if (blockEntity2 == null) {
                            return OptionalInt.empty();
                        }
                        CompoundTag compoundTag = blockEntity.save(new CompoundTag());
                        compoundTag.remove("x");
                        compoundTag.remove("y");
                        compoundTag.remove("z");
                        CompoundTag compoundTag2 = blockEntity2.save(new CompoundTag());
                        compoundTag2.remove("x");
                        compoundTag2.remove("y");
                        compoundTag2.remove("z");
                        if (!compoundTag.equals(compoundTag2)) {
                            return OptionalInt.empty();
                        }
                    }
                    ++n2;
                }
            }
        }
        return OptionalInt.of(n2);
    }

    @FunctionalInterface
    static interface CommandNumericPredicate {
        public int test(CommandContext<CommandSourceStack> var1) throws CommandSyntaxException;
    }

    @FunctionalInterface
    static interface CommandPredicate {
        public boolean test(CommandContext<CommandSourceStack> var1) throws CommandSyntaxException;
    }

}

