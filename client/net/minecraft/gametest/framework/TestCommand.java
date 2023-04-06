/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.Command
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.arguments.BoolArgumentType
 *  com.mojang.brigadier.arguments.IntegerArgumentType
 *  com.mojang.brigadier.arguments.StringArgumentType
 *  com.mojang.brigadier.builder.ArgumentBuilder
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.builder.RequiredArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.tree.LiteralCommandNode
 *  javax.annotation.Nullable
 *  org.apache.commons.io.IOUtils
 */
package net.minecraft.gametest.framework;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.blocks.BlockInput;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.data.structures.NbtToSnbt;
import net.minecraft.gametest.framework.GameTestInfo;
import net.minecraft.gametest.framework.GameTestListener;
import net.minecraft.gametest.framework.GameTestRegistry;
import net.minecraft.gametest.framework.GameTestRunner;
import net.minecraft.gametest.framework.GameTestTicker;
import net.minecraft.gametest.framework.MultipleTestTracker;
import net.minecraft.gametest.framework.StructureUtils;
import net.minecraft.gametest.framework.TestClassNameArgument;
import net.minecraft.gametest.framework.TestFunction;
import net.minecraft.gametest.framework.TestFunctionArgument;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.StructureBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.io.IOUtils;

public class TestCommand {
    public static void register(CommandDispatcher<CommandSourceStack> commandDispatcher) {
        commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("test").then(Commands.literal("runthis").executes(commandContext -> TestCommand.runNearbyTest((CommandSourceStack)commandContext.getSource())))).then(Commands.literal("runthese").executes(commandContext -> TestCommand.runAllNearbyTests((CommandSourceStack)commandContext.getSource())))).then(((LiteralArgumentBuilder)Commands.literal("runfailed").executes(commandContext -> TestCommand.runLastFailedTests((CommandSourceStack)commandContext.getSource(), false, 0, 8))).then(((RequiredArgumentBuilder)Commands.argument("onlyRequiredTests", BoolArgumentType.bool()).executes(commandContext -> TestCommand.runLastFailedTests((CommandSourceStack)commandContext.getSource(), BoolArgumentType.getBool((CommandContext)commandContext, (String)"onlyRequiredTests"), 0, 8))).then(((RequiredArgumentBuilder)Commands.argument("rotationSteps", IntegerArgumentType.integer()).executes(commandContext -> TestCommand.runLastFailedTests((CommandSourceStack)commandContext.getSource(), BoolArgumentType.getBool((CommandContext)commandContext, (String)"onlyRequiredTests"), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"rotationSteps"), 8))).then(Commands.argument("testsPerRow", IntegerArgumentType.integer()).executes(commandContext -> TestCommand.runLastFailedTests((CommandSourceStack)commandContext.getSource(), BoolArgumentType.getBool((CommandContext)commandContext, (String)"onlyRequiredTests"), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"rotationSteps"), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"testsPerRow")))))))).then(Commands.literal("run").then(((RequiredArgumentBuilder)Commands.argument("testName", TestFunctionArgument.testFunctionArgument()).executes(commandContext -> TestCommand.runTest((CommandSourceStack)commandContext.getSource(), TestFunctionArgument.getTestFunction((CommandContext<CommandSourceStack>)commandContext, "testName"), 0))).then(Commands.argument("rotationSteps", IntegerArgumentType.integer()).executes(commandContext -> TestCommand.runTest((CommandSourceStack)commandContext.getSource(), TestFunctionArgument.getTestFunction((CommandContext<CommandSourceStack>)commandContext, "testName"), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"rotationSteps"))))))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("runall").executes(commandContext -> TestCommand.runAllTests((CommandSourceStack)commandContext.getSource(), 0, 8))).then(((RequiredArgumentBuilder)Commands.argument("testClassName", TestClassNameArgument.testClassName()).executes(commandContext -> TestCommand.runAllTestsInClass((CommandSourceStack)commandContext.getSource(), TestClassNameArgument.getTestClassName((CommandContext<CommandSourceStack>)commandContext, "testClassName"), 0, 8))).then(((RequiredArgumentBuilder)Commands.argument("rotationSteps", IntegerArgumentType.integer()).executes(commandContext -> TestCommand.runAllTestsInClass((CommandSourceStack)commandContext.getSource(), TestClassNameArgument.getTestClassName((CommandContext<CommandSourceStack>)commandContext, "testClassName"), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"rotationSteps"), 8))).then(Commands.argument("testsPerRow", IntegerArgumentType.integer()).executes(commandContext -> TestCommand.runAllTestsInClass((CommandSourceStack)commandContext.getSource(), TestClassNameArgument.getTestClassName((CommandContext<CommandSourceStack>)commandContext, "testClassName"), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"rotationSteps"), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"testsPerRow"))))))).then(((RequiredArgumentBuilder)Commands.argument("rotationSteps", IntegerArgumentType.integer()).executes(commandContext -> TestCommand.runAllTests((CommandSourceStack)commandContext.getSource(), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"rotationSteps"), 8))).then(Commands.argument("testsPerRow", IntegerArgumentType.integer()).executes(commandContext -> TestCommand.runAllTests((CommandSourceStack)commandContext.getSource(), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"rotationSteps"), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"testsPerRow"))))))).then(Commands.literal("export").then(Commands.argument("testName", StringArgumentType.word()).executes(commandContext -> TestCommand.exportTestStructure((CommandSourceStack)commandContext.getSource(), StringArgumentType.getString((CommandContext)commandContext, (String)"testName")))))).then(Commands.literal("exportthis").executes(commandContext -> TestCommand.exportNearestTestStructure((CommandSourceStack)commandContext.getSource())))).then(Commands.literal("import").then(Commands.argument("testName", StringArgumentType.word()).executes(commandContext -> TestCommand.importTestStructure((CommandSourceStack)commandContext.getSource(), StringArgumentType.getString((CommandContext)commandContext, (String)"testName")))))).then(((LiteralArgumentBuilder)Commands.literal("pos").executes(commandContext -> TestCommand.showPos((CommandSourceStack)commandContext.getSource(), "pos"))).then(Commands.argument("var", StringArgumentType.word()).executes(commandContext -> TestCommand.showPos((CommandSourceStack)commandContext.getSource(), StringArgumentType.getString((CommandContext)commandContext, (String)"var")))))).then(Commands.literal("create").then(((RequiredArgumentBuilder)Commands.argument("testName", StringArgumentType.word()).executes(commandContext -> TestCommand.createNewStructure((CommandSourceStack)commandContext.getSource(), StringArgumentType.getString((CommandContext)commandContext, (String)"testName"), 5, 5, 5))).then(((RequiredArgumentBuilder)Commands.argument("width", IntegerArgumentType.integer()).executes(commandContext -> TestCommand.createNewStructure((CommandSourceStack)commandContext.getSource(), StringArgumentType.getString((CommandContext)commandContext, (String)"testName"), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"width"), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"width"), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"width")))).then(Commands.argument("height", IntegerArgumentType.integer()).then(Commands.argument("depth", IntegerArgumentType.integer()).executes(commandContext -> TestCommand.createNewStructure((CommandSourceStack)commandContext.getSource(), StringArgumentType.getString((CommandContext)commandContext, (String)"testName"), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"width"), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"height"), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"depth"))))))))).then(((LiteralArgumentBuilder)Commands.literal("clearall").executes(commandContext -> TestCommand.clearAllTests((CommandSourceStack)commandContext.getSource(), 200))).then(Commands.argument("radius", IntegerArgumentType.integer()).executes(commandContext -> TestCommand.clearAllTests((CommandSourceStack)commandContext.getSource(), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"radius"))))));
    }

    private static int createNewStructure(CommandSourceStack commandSourceStack, String string, int n, int n2, int n3) {
        if (n > 48 || n2 > 48 || n3 > 48) {
            throw new IllegalArgumentException("The structure must be less than 48 blocks big in each axis");
        }
        ServerLevel serverLevel = commandSourceStack.getLevel();
        BlockPos blockPos = new BlockPos(commandSourceStack.getPosition());
        BlockPos blockPos2 = new BlockPos(blockPos.getX(), commandSourceStack.getLevel().getHeightmapPos(Heightmap.Types.WORLD_SURFACE, blockPos).getY(), blockPos.getZ() + 3);
        StructureUtils.createNewEmptyStructureBlock(string.toLowerCase(), blockPos2, new BlockPos(n, n2, n3), Rotation.NONE, serverLevel);
        for (int i = 0; i < n; ++i) {
            for (int j = 0; j < n3; ++j) {
                BlockPos blockPos3 = new BlockPos(blockPos2.getX() + i, blockPos2.getY() + 1, blockPos2.getZ() + j);
                Block block = Blocks.POLISHED_ANDESITE;
                BlockInput blockInput = new BlockInput(block.defaultBlockState(), Collections.EMPTY_SET, null);
                blockInput.place(serverLevel, blockPos3, 2);
            }
        }
        StructureUtils.addCommandBlockAndButtonToStartTest(blockPos2, new BlockPos(1, 0, -1), Rotation.NONE, serverLevel);
        return 0;
    }

    private static int showPos(CommandSourceStack commandSourceStack, String string) throws CommandSyntaxException {
        ServerLevel serverLevel;
        BlockHitResult blockHitResult = (BlockHitResult)commandSourceStack.getPlayerOrException().pick(10.0, 1.0f, false);
        BlockPos blockPos = blockHitResult.getBlockPos();
        Optional<BlockPos> optional = StructureUtils.findStructureBlockContainingPos(blockPos, 15, serverLevel = commandSourceStack.getLevel());
        if (!optional.isPresent()) {
            optional = StructureUtils.findStructureBlockContainingPos(blockPos, 200, serverLevel);
        }
        if (!optional.isPresent()) {
            commandSourceStack.sendFailure(new TextComponent("Can't find a structure block that contains the targeted pos " + blockPos));
            return 0;
        }
        StructureBlockEntity structureBlockEntity = (StructureBlockEntity)serverLevel.getBlockEntity(optional.get());
        BlockPos blockPos2 = blockPos.subtract(optional.get());
        String string2 = blockPos2.getX() + ", " + blockPos2.getY() + ", " + blockPos2.getZ();
        String string3 = structureBlockEntity.getStructurePath();
        MutableComponent mutableComponent = new TextComponent(string2).setStyle(Style.EMPTY.withBold(true).withColor(ChatFormatting.GREEN).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent("Click to copy to clipboard"))).withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, "final BlockPos " + string + " = new BlockPos(" + string2 + ");")));
        commandSourceStack.sendSuccess(new TextComponent("Position relative to " + string3 + ": ").append(mutableComponent), false);
        DebugPackets.sendGameTestAddMarker(serverLevel, new BlockPos(blockPos), string2, -2147418368, 10000);
        return 1;
    }

    private static int runNearbyTest(CommandSourceStack commandSourceStack) {
        ServerLevel serverLevel;
        BlockPos blockPos = new BlockPos(commandSourceStack.getPosition());
        BlockPos blockPos2 = StructureUtils.findNearestStructureBlock(blockPos, 15, serverLevel = commandSourceStack.getLevel());
        if (blockPos2 == null) {
            TestCommand.say(serverLevel, "Couldn't find any structure block within 15 radius", ChatFormatting.RED);
            return 0;
        }
        GameTestRunner.clearMarkers(serverLevel);
        TestCommand.runTest(serverLevel, blockPos2, null);
        return 1;
    }

    private static int runAllNearbyTests(CommandSourceStack commandSourceStack) {
        ServerLevel serverLevel;
        BlockPos blockPos2 = new BlockPos(commandSourceStack.getPosition());
        Collection<BlockPos> collection = StructureUtils.findStructureBlocks(blockPos2, 200, serverLevel = commandSourceStack.getLevel());
        if (collection.isEmpty()) {
            TestCommand.say(serverLevel, "Couldn't find any structure blocks within 200 block radius", ChatFormatting.RED);
            return 1;
        }
        GameTestRunner.clearMarkers(serverLevel);
        TestCommand.say(commandSourceStack, "Running " + collection.size() + " tests...");
        MultipleTestTracker multipleTestTracker = new MultipleTestTracker();
        collection.forEach(blockPos -> TestCommand.runTest(serverLevel, blockPos, multipleTestTracker));
        return 1;
    }

    private static void runTest(ServerLevel serverLevel, BlockPos blockPos, @Nullable MultipleTestTracker multipleTestTracker) {
        StructureBlockEntity structureBlockEntity = (StructureBlockEntity)serverLevel.getBlockEntity(blockPos);
        String string = structureBlockEntity.getStructurePath();
        TestFunction testFunction = GameTestRegistry.getTestFunction(string);
        GameTestInfo gameTestInfo = new GameTestInfo(testFunction, structureBlockEntity.getRotation(), serverLevel);
        if (multipleTestTracker != null) {
            multipleTestTracker.addTestToTrack(gameTestInfo);
            gameTestInfo.addListener(new TestSummaryDisplayer(serverLevel, multipleTestTracker));
        }
        TestCommand.runTestPreparation(testFunction, serverLevel);
        AABB aABB = StructureUtils.getStructureBounds(structureBlockEntity);
        BlockPos blockPos2 = new BlockPos(aABB.minX, aABB.minY, aABB.minZ);
        GameTestRunner.runTest(gameTestInfo, blockPos2, GameTestTicker.singleton);
    }

    private static void showTestSummaryIfAllDone(ServerLevel serverLevel, MultipleTestTracker multipleTestTracker) {
        if (multipleTestTracker.isDone()) {
            TestCommand.say(serverLevel, "GameTest done! " + multipleTestTracker.getTotalCount() + " tests were run", ChatFormatting.WHITE);
            if (multipleTestTracker.hasFailedRequired()) {
                TestCommand.say(serverLevel, "" + multipleTestTracker.getFailedRequiredCount() + " required tests failed :(", ChatFormatting.RED);
            } else {
                TestCommand.say(serverLevel, "All required tests passed :)", ChatFormatting.GREEN);
            }
            if (multipleTestTracker.hasFailedOptional()) {
                TestCommand.say(serverLevel, "" + multipleTestTracker.getFailedOptionalCount() + " optional tests failed", ChatFormatting.GRAY);
            }
        }
    }

    private static int clearAllTests(CommandSourceStack commandSourceStack, int n) {
        ServerLevel serverLevel = commandSourceStack.getLevel();
        GameTestRunner.clearMarkers(serverLevel);
        BlockPos blockPos = new BlockPos(commandSourceStack.getPosition().x, (double)commandSourceStack.getLevel().getHeightmapPos(Heightmap.Types.WORLD_SURFACE, new BlockPos(commandSourceStack.getPosition())).getY(), commandSourceStack.getPosition().z);
        GameTestRunner.clearAllTests(serverLevel, blockPos, GameTestTicker.singleton, Mth.clamp(n, 0, 1024));
        return 1;
    }

    private static int runTest(CommandSourceStack commandSourceStack, TestFunction testFunction, int n) {
        ServerLevel serverLevel = commandSourceStack.getLevel();
        BlockPos blockPos = new BlockPos(commandSourceStack.getPosition());
        int n2 = commandSourceStack.getLevel().getHeightmapPos(Heightmap.Types.WORLD_SURFACE, blockPos).getY();
        BlockPos blockPos2 = new BlockPos(blockPos.getX(), n2, blockPos.getZ() + 3);
        GameTestRunner.clearMarkers(serverLevel);
        TestCommand.runTestPreparation(testFunction, serverLevel);
        Rotation rotation = StructureUtils.getRotationForRotationSteps(n);
        GameTestInfo gameTestInfo = new GameTestInfo(testFunction, rotation, serverLevel);
        GameTestRunner.runTest(gameTestInfo, blockPos2, GameTestTicker.singleton);
        return 1;
    }

    private static void runTestPreparation(TestFunction testFunction, ServerLevel serverLevel) {
        Consumer<ServerLevel> consumer = GameTestRegistry.getBeforeBatchFunction(testFunction.getBatchName());
        if (consumer != null) {
            consumer.accept(serverLevel);
        }
    }

    private static int runAllTests(CommandSourceStack commandSourceStack, int n, int n2) {
        GameTestRunner.clearMarkers(commandSourceStack.getLevel());
        Collection<TestFunction> collection = GameTestRegistry.getAllTestFunctions();
        TestCommand.say(commandSourceStack, "Running all " + collection.size() + " tests...");
        GameTestRegistry.forgetFailedTests();
        TestCommand.runTests(commandSourceStack, collection, n, n2);
        return 1;
    }

    private static int runAllTestsInClass(CommandSourceStack commandSourceStack, String string, int n, int n2) {
        Collection<TestFunction> collection = GameTestRegistry.getTestFunctionsForClassName(string);
        GameTestRunner.clearMarkers(commandSourceStack.getLevel());
        TestCommand.say(commandSourceStack, "Running " + collection.size() + " tests from " + string + "...");
        GameTestRegistry.forgetFailedTests();
        TestCommand.runTests(commandSourceStack, collection, n, n2);
        return 1;
    }

    private static int runLastFailedTests(CommandSourceStack commandSourceStack, boolean bl, int n, int n2) {
        Collection collection = bl ? (Collection)GameTestRegistry.getLastFailedTests().stream().filter(TestFunction::isRequired).collect(Collectors.toList()) : GameTestRegistry.getLastFailedTests();
        if (collection.isEmpty()) {
            TestCommand.say(commandSourceStack, "No failed tests to rerun");
            return 0;
        }
        GameTestRunner.clearMarkers(commandSourceStack.getLevel());
        TestCommand.say(commandSourceStack, "Rerunning " + collection.size() + " failed tests (" + (bl ? "only required tests" : "including optional tests") + ")");
        TestCommand.runTests(commandSourceStack, collection, n, n2);
        return 1;
    }

    private static void runTests(CommandSourceStack commandSourceStack, Collection<TestFunction> collection, int n, int n2) {
        BlockPos blockPos = new BlockPos(commandSourceStack.getPosition());
        BlockPos blockPos2 = new BlockPos(blockPos.getX(), commandSourceStack.getLevel().getHeightmapPos(Heightmap.Types.WORLD_SURFACE, blockPos).getY(), blockPos.getZ() + 3);
        ServerLevel serverLevel = commandSourceStack.getLevel();
        Rotation rotation = StructureUtils.getRotationForRotationSteps(n);
        Collection<GameTestInfo> collection2 = GameTestRunner.runTests(collection, blockPos2, rotation, serverLevel, GameTestTicker.singleton, n2);
        MultipleTestTracker multipleTestTracker = new MultipleTestTracker(collection2);
        multipleTestTracker.addListener(new TestSummaryDisplayer(serverLevel, multipleTestTracker));
        multipleTestTracker.addFailureListener(gameTestInfo -> GameTestRegistry.rememberFailedTest(gameTestInfo.getTestFunction()));
    }

    private static void say(CommandSourceStack commandSourceStack, String string) {
        commandSourceStack.sendSuccess(new TextComponent(string), false);
    }

    private static int exportNearestTestStructure(CommandSourceStack commandSourceStack) {
        ServerLevel serverLevel;
        BlockPos blockPos = new BlockPos(commandSourceStack.getPosition());
        BlockPos blockPos2 = StructureUtils.findNearestStructureBlock(blockPos, 15, serverLevel = commandSourceStack.getLevel());
        if (blockPos2 == null) {
            TestCommand.say(serverLevel, "Couldn't find any structure block within 15 radius", ChatFormatting.RED);
            return 0;
        }
        StructureBlockEntity structureBlockEntity = (StructureBlockEntity)serverLevel.getBlockEntity(blockPos2);
        String string = structureBlockEntity.getStructurePath();
        return TestCommand.exportTestStructure(commandSourceStack, string);
    }

    private static int exportTestStructure(CommandSourceStack commandSourceStack, String string) {
        Path path = Paths.get(StructureUtils.testStructuresDir, new String[0]);
        ResourceLocation resourceLocation = new ResourceLocation("minecraft", string);
        Path path2 = commandSourceStack.getLevel().getStructureManager().createPathToStructure(resourceLocation, ".nbt");
        Path path3 = NbtToSnbt.convertStructure(path2, string, path);
        if (path3 == null) {
            TestCommand.say(commandSourceStack, "Failed to export " + path2);
            return 1;
        }
        try {
            Files.createDirectories(path3.getParent(), new FileAttribute[0]);
        }
        catch (IOException iOException) {
            TestCommand.say(commandSourceStack, "Could not create folder " + path3.getParent());
            iOException.printStackTrace();
            return 1;
        }
        TestCommand.say(commandSourceStack, "Exported " + string + " to " + path3.toAbsolutePath());
        return 0;
    }

    private static int importTestStructure(CommandSourceStack commandSourceStack, String string) {
        Path path = Paths.get(StructureUtils.testStructuresDir, string + ".snbt");
        ResourceLocation resourceLocation = new ResourceLocation("minecraft", string);
        Path path2 = commandSourceStack.getLevel().getStructureManager().createPathToStructure(resourceLocation, ".nbt");
        try {
            BufferedReader bufferedReader = Files.newBufferedReader(path);
            String string2 = IOUtils.toString((Reader)bufferedReader);
            Files.createDirectories(path2.getParent(), new FileAttribute[0]);
            try (OutputStream outputStream = Files.newOutputStream(path2, new OpenOption[0]);){
                NbtIo.writeCompressed(TagParser.parseTag(string2), outputStream);
            }
            TestCommand.say(commandSourceStack, "Imported to " + path2.toAbsolutePath());
            return 0;
        }
        catch (CommandSyntaxException | IOException throwable) {
            System.err.println("Failed to load structure " + string);
            throwable.printStackTrace();
            return 1;
        }
    }

    private static void say(ServerLevel serverLevel, String string, ChatFormatting chatFormatting) {
        serverLevel.getPlayers(serverPlayer -> true).forEach(serverPlayer -> serverPlayer.sendMessage(new TextComponent((Object)((Object)chatFormatting) + string), Util.NIL_UUID));
    }

    static class TestSummaryDisplayer
    implements GameTestListener {
        private final ServerLevel level;
        private final MultipleTestTracker tracker;

        public TestSummaryDisplayer(ServerLevel serverLevel, MultipleTestTracker multipleTestTracker) {
            this.level = serverLevel;
            this.tracker = multipleTestTracker;
        }

        @Override
        public void testStructureLoaded(GameTestInfo gameTestInfo) {
        }

        @Override
        public void testFailed(GameTestInfo gameTestInfo) {
            TestCommand.showTestSummaryIfAllDone(this.level, this.tracker);
        }
    }

}

