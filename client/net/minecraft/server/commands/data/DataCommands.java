/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Iterables
 *  com.mojang.brigadier.Command
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.arguments.DoubleArgumentType
 *  com.mojang.brigadier.arguments.IntegerArgumentType
 *  com.mojang.brigadier.builder.ArgumentBuilder
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.builder.RequiredArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.DynamicCommandExceptionType
 *  com.mojang.brigadier.exceptions.SimpleCommandExceptionType
 *  com.mojang.brigadier.tree.LiteralCommandNode
 */
package net.minecraft.server.commands.data;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Stream;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.CompoundTagArgument;
import net.minecraft.commands.arguments.NbtPathArgument;
import net.minecraft.commands.arguments.NbtTagArgument;
import net.minecraft.nbt.CollectionTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NumericTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.commands.data.BlockDataAccessor;
import net.minecraft.server.commands.data.DataAccessor;
import net.minecraft.server.commands.data.EntityDataAccessor;
import net.minecraft.server.commands.data.StorageDataAccessor;
import net.minecraft.util.Mth;

public class DataCommands {
    private static final SimpleCommandExceptionType ERROR_MERGE_UNCHANGED = new SimpleCommandExceptionType((Message)new TranslatableComponent("commands.data.merge.failed"));
    private static final DynamicCommandExceptionType ERROR_GET_NOT_NUMBER = new DynamicCommandExceptionType(object -> new TranslatableComponent("commands.data.get.invalid", object));
    private static final DynamicCommandExceptionType ERROR_GET_NON_EXISTENT = new DynamicCommandExceptionType(object -> new TranslatableComponent("commands.data.get.unknown", object));
    private static final SimpleCommandExceptionType ERROR_MULTIPLE_TAGS = new SimpleCommandExceptionType((Message)new TranslatableComponent("commands.data.get.multiple"));
    private static final DynamicCommandExceptionType ERROR_EXPECTED_LIST = new DynamicCommandExceptionType(object -> new TranslatableComponent("commands.data.modify.expected_list", object));
    private static final DynamicCommandExceptionType ERROR_EXPECTED_OBJECT = new DynamicCommandExceptionType(object -> new TranslatableComponent("commands.data.modify.expected_object", object));
    private static final DynamicCommandExceptionType ERROR_INVALID_INDEX = new DynamicCommandExceptionType(object -> new TranslatableComponent("commands.data.modify.invalid_index", object));
    public static final List<Function<String, DataProvider>> ALL_PROVIDERS = ImmutableList.of(EntityDataAccessor.PROVIDER, BlockDataAccessor.PROVIDER, StorageDataAccessor.PROVIDER);
    public static final List<DataProvider> TARGET_PROVIDERS = (List)ALL_PROVIDERS.stream().map(function -> (DataProvider)function.apply("target")).collect(ImmutableList.toImmutableList());
    public static final List<DataProvider> SOURCE_PROVIDERS = (List)ALL_PROVIDERS.stream().map(function -> (DataProvider)function.apply("source")).collect(ImmutableList.toImmutableList());

    public static void register(CommandDispatcher<CommandSourceStack> commandDispatcher) {
        LiteralArgumentBuilder literalArgumentBuilder = (LiteralArgumentBuilder)Commands.literal("data").requires(commandSourceStack -> commandSourceStack.hasPermission(2));
        for (DataProvider dataProvider : TARGET_PROVIDERS) {
            ((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)literalArgumentBuilder.then(dataProvider.wrap((ArgumentBuilder<CommandSourceStack, ?>)Commands.literal("merge"), argumentBuilder -> argumentBuilder.then(Commands.argument("nbt", CompoundTagArgument.compoundTag()).executes(commandContext -> DataCommands.mergeData((CommandSourceStack)commandContext.getSource(), dataProvider.access((CommandContext<CommandSourceStack>)commandContext), CompoundTagArgument.getCompoundTag(commandContext, "nbt"))))))).then(dataProvider.wrap((ArgumentBuilder<CommandSourceStack, ?>)Commands.literal("get"), argumentBuilder -> argumentBuilder.executes(commandContext -> DataCommands.getData((CommandSourceStack)commandContext.getSource(), dataProvider.access((CommandContext<CommandSourceStack>)commandContext))).then(((RequiredArgumentBuilder)Commands.argument("path", NbtPathArgument.nbtPath()).executes(commandContext -> DataCommands.getData((CommandSourceStack)commandContext.getSource(), dataProvider.access((CommandContext<CommandSourceStack>)commandContext), NbtPathArgument.getPath((CommandContext<CommandSourceStack>)commandContext, "path")))).then(Commands.argument("scale", DoubleArgumentType.doubleArg()).executes(commandContext -> DataCommands.getNumeric((CommandSourceStack)commandContext.getSource(), dataProvider.access((CommandContext<CommandSourceStack>)commandContext), NbtPathArgument.getPath((CommandContext<CommandSourceStack>)commandContext, "path"), DoubleArgumentType.getDouble((CommandContext)commandContext, (String)"scale")))))))).then(dataProvider.wrap((ArgumentBuilder<CommandSourceStack, ?>)Commands.literal("remove"), argumentBuilder -> argumentBuilder.then(Commands.argument("path", NbtPathArgument.nbtPath()).executes(commandContext -> DataCommands.removeData((CommandSourceStack)commandContext.getSource(), dataProvider.access((CommandContext<CommandSourceStack>)commandContext), NbtPathArgument.getPath((CommandContext<CommandSourceStack>)commandContext, "path"))))))).then(DataCommands.decorateModification((argumentBuilder, dataManipulatorDecorator) -> argumentBuilder.then(Commands.literal("insert").then(Commands.argument("index", IntegerArgumentType.integer()).then(dataManipulatorDecorator.create((commandContext, compoundTag, nbtPath, list) -> {
                int n = IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"index");
                return DataCommands.insertAtIndex(n, compoundTag, nbtPath, list);
            })))).then(Commands.literal("prepend").then(dataManipulatorDecorator.create((commandContext, compoundTag, nbtPath, list) -> DataCommands.insertAtIndex(0, compoundTag, nbtPath, list)))).then(Commands.literal("append").then(dataManipulatorDecorator.create((commandContext, compoundTag, nbtPath, list) -> DataCommands.insertAtIndex(-1, compoundTag, nbtPath, list)))).then(Commands.literal("set").then(dataManipulatorDecorator.create((commandContext, compoundTag, nbtPath, list) -> nbtPath.set(compoundTag, ((Tag)Iterables.getLast((Iterable)list))::copy)))).then(Commands.literal("merge").then(dataManipulatorDecorator.create((commandContext, compoundTag, nbtPath, list) -> {
                List<Tag> list2 = nbtPath.getOrCreate(compoundTag, CompoundTag::new);
                int n = 0;
                for (Tag tag : list2) {
                    if (!(tag instanceof CompoundTag)) {
                        throw ERROR_EXPECTED_OBJECT.create((Object)tag);
                    }
                    CompoundTag compoundTag2 = (CompoundTag)tag;
                    CompoundTag compoundTag3 = compoundTag2.copy();
                    for (Tag tag2 : list) {
                        if (!(tag2 instanceof CompoundTag)) {
                            throw ERROR_EXPECTED_OBJECT.create((Object)tag2);
                        }
                        compoundTag2.merge((CompoundTag)tag2);
                    }
                    n += compoundTag3.equals(compoundTag2) ? 0 : 1;
                }
                return n;
            })))));
        }
        commandDispatcher.register(literalArgumentBuilder);
    }

    private static int insertAtIndex(int n, CompoundTag compoundTag, NbtPathArgument.NbtPath nbtPath, List<Tag> list) throws CommandSyntaxException {
        List<Tag> list2 = nbtPath.getOrCreate(compoundTag, ListTag::new);
        int n2 = 0;
        for (Tag tag : list2) {
            if (!(tag instanceof CollectionTag)) {
                throw ERROR_EXPECTED_LIST.create((Object)tag);
            }
            boolean bl = false;
            CollectionTag collectionTag = (CollectionTag)tag;
            int n3 = n < 0 ? collectionTag.size() + n + 1 : n;
            for (Tag tag2 : list) {
                try {
                    if (!collectionTag.addTag(n3, tag2.copy())) continue;
                    ++n3;
                    bl = true;
                }
                catch (IndexOutOfBoundsException indexOutOfBoundsException) {
                    throw ERROR_INVALID_INDEX.create((Object)n3);
                }
            }
            n2 += bl ? 1 : 0;
        }
        return n2;
    }

    private static ArgumentBuilder<CommandSourceStack, ?> decorateModification(BiConsumer<ArgumentBuilder<CommandSourceStack, ?>, DataManipulatorDecorator> biConsumer) {
        LiteralArgumentBuilder<CommandSourceStack> literalArgumentBuilder = Commands.literal("modify");
        for (DataProvider dataProvider : TARGET_PROVIDERS) {
            dataProvider.wrap((ArgumentBuilder<CommandSourceStack, ?>)literalArgumentBuilder, argumentBuilder -> {
                RequiredArgumentBuilder<CommandSourceStack, NbtPathArgument.NbtPath> requiredArgumentBuilder = Commands.argument("targetPath", NbtPathArgument.nbtPath());
                for (DataProvider dataProvider2 : SOURCE_PROVIDERS) {
                    biConsumer.accept((ArgumentBuilder<CommandSourceStack, ?>)requiredArgumentBuilder, dataManipulator -> dataProvider2.wrap((ArgumentBuilder<CommandSourceStack, ?>)Commands.literal("from"), argumentBuilder -> argumentBuilder.executes(commandContext -> {
                        List<Tag> list = Collections.singletonList(dataProvider2.access((CommandContext<CommandSourceStack>)commandContext).getData());
                        return DataCommands.manipulateData((CommandContext<CommandSourceStack>)commandContext, dataProvider, dataManipulator, list);
                    }).then(Commands.argument("sourcePath", NbtPathArgument.nbtPath()).executes(commandContext -> {
                        DataAccessor dataAccessor = dataProvider2.access((CommandContext<CommandSourceStack>)commandContext);
                        NbtPathArgument.NbtPath nbtPath = NbtPathArgument.getPath((CommandContext<CommandSourceStack>)commandContext, "sourcePath");
                        List<Tag> list = nbtPath.get(dataAccessor.getData());
                        return DataCommands.manipulateData((CommandContext<CommandSourceStack>)commandContext, dataProvider, dataManipulator, list);
                    }))));
                }
                biConsumer.accept((ArgumentBuilder<CommandSourceStack, ?>)requiredArgumentBuilder, dataManipulator -> (LiteralArgumentBuilder)Commands.literal("value").then(Commands.argument("value", NbtTagArgument.nbtTag()).executes(commandContext -> {
                    List<Tag> list = Collections.singletonList(NbtTagArgument.getNbtTag(commandContext, "value"));
                    return DataCommands.manipulateData((CommandContext<CommandSourceStack>)commandContext, dataProvider, dataManipulator, list);
                })));
                return argumentBuilder.then(requiredArgumentBuilder);
            });
        }
        return literalArgumentBuilder;
    }

    private static int manipulateData(CommandContext<CommandSourceStack> commandContext, DataProvider dataProvider, DataManipulator dataManipulator, List<Tag> list) throws CommandSyntaxException {
        DataAccessor dataAccessor = dataProvider.access(commandContext);
        NbtPathArgument.NbtPath nbtPath = NbtPathArgument.getPath(commandContext, "targetPath");
        CompoundTag compoundTag = dataAccessor.getData();
        int n = dataManipulator.modify(commandContext, compoundTag, nbtPath, list);
        if (n == 0) {
            throw ERROR_MERGE_UNCHANGED.create();
        }
        dataAccessor.setData(compoundTag);
        ((CommandSourceStack)commandContext.getSource()).sendSuccess(dataAccessor.getModifiedSuccess(), true);
        return n;
    }

    private static int removeData(CommandSourceStack commandSourceStack, DataAccessor dataAccessor, NbtPathArgument.NbtPath nbtPath) throws CommandSyntaxException {
        CompoundTag compoundTag = dataAccessor.getData();
        int n = nbtPath.remove(compoundTag);
        if (n == 0) {
            throw ERROR_MERGE_UNCHANGED.create();
        }
        dataAccessor.setData(compoundTag);
        commandSourceStack.sendSuccess(dataAccessor.getModifiedSuccess(), true);
        return n;
    }

    private static Tag getSingleTag(NbtPathArgument.NbtPath nbtPath, DataAccessor dataAccessor) throws CommandSyntaxException {
        List<Tag> list = nbtPath.get(dataAccessor.getData());
        Iterator iterator = list.iterator();
        Tag tag = (Tag)iterator.next();
        if (iterator.hasNext()) {
            throw ERROR_MULTIPLE_TAGS.create();
        }
        return tag;
    }

    private static int getData(CommandSourceStack commandSourceStack, DataAccessor dataAccessor, NbtPathArgument.NbtPath nbtPath) throws CommandSyntaxException {
        int n;
        Tag tag = DataCommands.getSingleTag(nbtPath, dataAccessor);
        if (tag instanceof NumericTag) {
            n = Mth.floor(((NumericTag)tag).getAsDouble());
        } else if (tag instanceof CollectionTag) {
            n = ((CollectionTag)tag).size();
        } else if (tag instanceof CompoundTag) {
            n = ((CompoundTag)tag).size();
        } else if (tag instanceof StringTag) {
            n = tag.getAsString().length();
        } else {
            throw ERROR_GET_NON_EXISTENT.create((Object)nbtPath.toString());
        }
        commandSourceStack.sendSuccess(dataAccessor.getPrintSuccess(tag), false);
        return n;
    }

    private static int getNumeric(CommandSourceStack commandSourceStack, DataAccessor dataAccessor, NbtPathArgument.NbtPath nbtPath, double d) throws CommandSyntaxException {
        Tag tag = DataCommands.getSingleTag(nbtPath, dataAccessor);
        if (!(tag instanceof NumericTag)) {
            throw ERROR_GET_NOT_NUMBER.create((Object)nbtPath.toString());
        }
        int n = Mth.floor(((NumericTag)tag).getAsDouble() * d);
        commandSourceStack.sendSuccess(dataAccessor.getPrintSuccess(nbtPath, d, n), false);
        return n;
    }

    private static int getData(CommandSourceStack commandSourceStack, DataAccessor dataAccessor) throws CommandSyntaxException {
        commandSourceStack.sendSuccess(dataAccessor.getPrintSuccess(dataAccessor.getData()), false);
        return 1;
    }

    private static int mergeData(CommandSourceStack commandSourceStack, DataAccessor dataAccessor, CompoundTag compoundTag) throws CommandSyntaxException {
        CompoundTag compoundTag2;
        CompoundTag compoundTag3 = dataAccessor.getData();
        if (compoundTag3.equals(compoundTag2 = compoundTag3.copy().merge(compoundTag))) {
            throw ERROR_MERGE_UNCHANGED.create();
        }
        dataAccessor.setData(compoundTag2);
        commandSourceStack.sendSuccess(dataAccessor.getModifiedSuccess(), true);
        return 1;
    }

    public static interface DataProvider {
        public DataAccessor access(CommandContext<CommandSourceStack> var1) throws CommandSyntaxException;

        public ArgumentBuilder<CommandSourceStack, ?> wrap(ArgumentBuilder<CommandSourceStack, ?> var1, Function<ArgumentBuilder<CommandSourceStack, ?>, ArgumentBuilder<CommandSourceStack, ?>> var2);
    }

    static interface DataManipulatorDecorator {
        public ArgumentBuilder<CommandSourceStack, ?> create(DataManipulator var1);
    }

    static interface DataManipulator {
        public int modify(CommandContext<CommandSourceStack> var1, CompoundTag var2, NbtPathArgument.NbtPath var3, List<Tag> var4) throws CommandSyntaxException;
    }

}

