/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.brigadier.Command
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.arguments.IntegerArgumentType
 *  com.mojang.brigadier.builder.ArgumentBuilder
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.builder.RequiredArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.DynamicCommandExceptionType
 *  com.mojang.brigadier.exceptions.SimpleCommandExceptionType
 *  com.mojang.brigadier.suggestion.SuggestionProvider
 *  com.mojang.brigadier.suggestion.SuggestionsBuilder
 *  com.mojang.brigadier.tree.LiteralCommandNode
 */
package net.minecraft.server.commands;

import com.google.common.collect.Lists;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.arguments.SlotArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.commands.ReplaceItemCommand;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTables;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;

public class LootCommand {
    public static final SuggestionProvider<CommandSourceStack> SUGGEST_LOOT_TABLE = (commandContext, suggestionsBuilder) -> {
        LootTables lootTables = ((CommandSourceStack)commandContext.getSource()).getServer().getLootTables();
        return SharedSuggestionProvider.suggestResource(lootTables.getIds(), suggestionsBuilder);
    };
    private static final DynamicCommandExceptionType ERROR_NO_HELD_ITEMS = new DynamicCommandExceptionType(object -> new TranslatableComponent("commands.drop.no_held_items", object));
    private static final DynamicCommandExceptionType ERROR_NO_LOOT_TABLE = new DynamicCommandExceptionType(object -> new TranslatableComponent("commands.drop.no_loot_table", object));

    public static void register(CommandDispatcher<CommandSourceStack> commandDispatcher) {
        commandDispatcher.register((LiteralArgumentBuilder)LootCommand.addTargets(Commands.literal("loot").requires(commandSourceStack -> commandSourceStack.hasPermission(2)), (argumentBuilder, dropConsumer) -> argumentBuilder.then(Commands.literal("fish").then(Commands.argument("loot_table", ResourceLocationArgument.id()).suggests(SUGGEST_LOOT_TABLE).then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("pos", BlockPosArgument.blockPos()).executes(commandContext -> LootCommand.dropFishingLoot((CommandContext<CommandSourceStack>)commandContext, ResourceLocationArgument.getId((CommandContext<CommandSourceStack>)commandContext, "loot_table"), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)commandContext, "pos"), ItemStack.EMPTY, dropConsumer))).then(Commands.argument("tool", ItemArgument.item()).executes(commandContext -> LootCommand.dropFishingLoot((CommandContext<CommandSourceStack>)commandContext, ResourceLocationArgument.getId((CommandContext<CommandSourceStack>)commandContext, "loot_table"), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)commandContext, "pos"), ItemArgument.getItem(commandContext, "tool").createItemStack(1, false), dropConsumer)))).then(Commands.literal("mainhand").executes(commandContext -> LootCommand.dropFishingLoot((CommandContext<CommandSourceStack>)commandContext, ResourceLocationArgument.getId((CommandContext<CommandSourceStack>)commandContext, "loot_table"), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)commandContext, "pos"), LootCommand.getSourceHandItem((CommandSourceStack)commandContext.getSource(), EquipmentSlot.MAINHAND), dropConsumer)))).then(Commands.literal("offhand").executes(commandContext -> LootCommand.dropFishingLoot((CommandContext<CommandSourceStack>)commandContext, ResourceLocationArgument.getId((CommandContext<CommandSourceStack>)commandContext, "loot_table"), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)commandContext, "pos"), LootCommand.getSourceHandItem((CommandSourceStack)commandContext.getSource(), EquipmentSlot.OFFHAND), dropConsumer)))))).then(Commands.literal("loot").then(Commands.argument("loot_table", ResourceLocationArgument.id()).suggests(SUGGEST_LOOT_TABLE).executes(commandContext -> LootCommand.dropChestLoot((CommandContext<CommandSourceStack>)commandContext, ResourceLocationArgument.getId((CommandContext<CommandSourceStack>)commandContext, "loot_table"), dropConsumer)))).then(Commands.literal("kill").then(Commands.argument("target", EntityArgument.entity()).executes(commandContext -> LootCommand.dropKillLoot((CommandContext<CommandSourceStack>)commandContext, EntityArgument.getEntity((CommandContext<CommandSourceStack>)commandContext, "target"), dropConsumer)))).then(Commands.literal("mine").then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("pos", BlockPosArgument.blockPos()).executes(commandContext -> LootCommand.dropBlockLoot((CommandContext<CommandSourceStack>)commandContext, BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)commandContext, "pos"), ItemStack.EMPTY, dropConsumer))).then(Commands.argument("tool", ItemArgument.item()).executes(commandContext -> LootCommand.dropBlockLoot((CommandContext<CommandSourceStack>)commandContext, BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)commandContext, "pos"), ItemArgument.getItem(commandContext, "tool").createItemStack(1, false), dropConsumer)))).then(Commands.literal("mainhand").executes(commandContext -> LootCommand.dropBlockLoot((CommandContext<CommandSourceStack>)commandContext, BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)commandContext, "pos"), LootCommand.getSourceHandItem((CommandSourceStack)commandContext.getSource(), EquipmentSlot.MAINHAND), dropConsumer)))).then(Commands.literal("offhand").executes(commandContext -> LootCommand.dropBlockLoot((CommandContext<CommandSourceStack>)commandContext, BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)commandContext, "pos"), LootCommand.getSourceHandItem((CommandSourceStack)commandContext.getSource(), EquipmentSlot.OFFHAND), dropConsumer)))))));
    }

    private static <T extends ArgumentBuilder<CommandSourceStack, T>> T addTargets(T t, TailProvider tailProvider) {
        return (T)t.then(((LiteralArgumentBuilder)Commands.literal("replace").then(Commands.literal("entity").then(Commands.argument("entities", EntityArgument.entities()).then(tailProvider.construct((ArgumentBuilder<CommandSourceStack, ?>)Commands.argument("slot", SlotArgument.slot()), (commandContext, list, callback) -> LootCommand.entityReplace(EntityArgument.getEntities((CommandContext<CommandSourceStack>)commandContext, "entities"), SlotArgument.getSlot((CommandContext<CommandSourceStack>)commandContext, "slot"), list.size(), list, callback)).then(tailProvider.construct((ArgumentBuilder<CommandSourceStack, ?>)Commands.argument("count", IntegerArgumentType.integer((int)0)), (commandContext, list, callback) -> LootCommand.entityReplace(EntityArgument.getEntities((CommandContext<CommandSourceStack>)commandContext, "entities"), SlotArgument.getSlot((CommandContext<CommandSourceStack>)commandContext, "slot"), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"count"), list, callback))))))).then(Commands.literal("block").then(Commands.argument("targetPos", BlockPosArgument.blockPos()).then(tailProvider.construct((ArgumentBuilder<CommandSourceStack, ?>)Commands.argument("slot", SlotArgument.slot()), (commandContext, list, callback) -> LootCommand.blockReplace((CommandSourceStack)commandContext.getSource(), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)commandContext, "targetPos"), SlotArgument.getSlot((CommandContext<CommandSourceStack>)commandContext, "slot"), list.size(), list, callback)).then(tailProvider.construct((ArgumentBuilder<CommandSourceStack, ?>)Commands.argument("count", IntegerArgumentType.integer((int)0)), (commandContext, list, callback) -> LootCommand.blockReplace((CommandSourceStack)commandContext.getSource(), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)commandContext, "targetPos"), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"slot"), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"count"), list, callback))))))).then(Commands.literal("insert").then(tailProvider.construct((ArgumentBuilder<CommandSourceStack, ?>)Commands.argument("targetPos", BlockPosArgument.blockPos()), (commandContext, list, callback) -> LootCommand.blockDistribute((CommandSourceStack)commandContext.getSource(), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)commandContext, "targetPos"), list, callback)))).then(Commands.literal("give").then(tailProvider.construct((ArgumentBuilder<CommandSourceStack, ?>)Commands.argument("players", EntityArgument.players()), (commandContext, list, callback) -> LootCommand.playerGive(EntityArgument.getPlayers((CommandContext<CommandSourceStack>)commandContext, "players"), list, callback)))).then(Commands.literal("spawn").then(tailProvider.construct((ArgumentBuilder<CommandSourceStack, ?>)Commands.argument("targetPos", Vec3Argument.vec3()), (commandContext, list, callback) -> LootCommand.dropInWorld((CommandSourceStack)commandContext.getSource(), Vec3Argument.getVec3((CommandContext<CommandSourceStack>)commandContext, "targetPos"), list, callback))));
    }

    private static Container getContainer(CommandSourceStack commandSourceStack, BlockPos blockPos) throws CommandSyntaxException {
        BlockEntity blockEntity = commandSourceStack.getLevel().getBlockEntity(blockPos);
        if (!(blockEntity instanceof Container)) {
            throw ReplaceItemCommand.ERROR_NOT_A_CONTAINER.create();
        }
        return (Container)((Object)blockEntity);
    }

    private static int blockDistribute(CommandSourceStack commandSourceStack, BlockPos blockPos, List<ItemStack> list, Callback callback) throws CommandSyntaxException {
        Container container = LootCommand.getContainer(commandSourceStack, blockPos);
        ArrayList arrayList = Lists.newArrayListWithCapacity((int)list.size());
        for (ItemStack itemStack : list) {
            if (!LootCommand.distributeToContainer(container, itemStack.copy())) continue;
            container.setChanged();
            arrayList.add(itemStack);
        }
        callback.accept(arrayList);
        return arrayList.size();
    }

    private static boolean distributeToContainer(Container container, ItemStack itemStack) {
        boolean bl = false;
        for (int i = 0; i < container.getContainerSize() && !itemStack.isEmpty(); ++i) {
            ItemStack itemStack2 = container.getItem(i);
            if (!container.canPlaceItem(i, itemStack)) continue;
            if (itemStack2.isEmpty()) {
                container.setItem(i, itemStack);
                bl = true;
                break;
            }
            if (!LootCommand.canMergeItems(itemStack2, itemStack)) continue;
            int n = itemStack.getMaxStackSize() - itemStack2.getCount();
            int n2 = Math.min(itemStack.getCount(), n);
            itemStack.shrink(n2);
            itemStack2.grow(n2);
            bl = true;
        }
        return bl;
    }

    private static int blockReplace(CommandSourceStack commandSourceStack, BlockPos blockPos, int n, int n2, List<ItemStack> list, Callback callback) throws CommandSyntaxException {
        Container container = LootCommand.getContainer(commandSourceStack, blockPos);
        int n3 = container.getContainerSize();
        if (n < 0 || n >= n3) {
            throw ReplaceItemCommand.ERROR_INAPPLICABLE_SLOT.create((Object)n);
        }
        ArrayList arrayList = Lists.newArrayListWithCapacity((int)list.size());
        for (int i = 0; i < n2; ++i) {
            ItemStack itemStack;
            int n4 = n + i;
            ItemStack itemStack2 = itemStack = i < list.size() ? list.get(i) : ItemStack.EMPTY;
            if (!container.canPlaceItem(n4, itemStack)) continue;
            container.setItem(n4, itemStack);
            arrayList.add(itemStack);
        }
        callback.accept(arrayList);
        return arrayList.size();
    }

    private static boolean canMergeItems(ItemStack itemStack, ItemStack itemStack2) {
        return itemStack.getItem() == itemStack2.getItem() && itemStack.getDamageValue() == itemStack2.getDamageValue() && itemStack.getCount() <= itemStack.getMaxStackSize() && Objects.equals(itemStack.getTag(), itemStack2.getTag());
    }

    private static int playerGive(Collection<ServerPlayer> collection, List<ItemStack> list, Callback callback) throws CommandSyntaxException {
        ArrayList arrayList = Lists.newArrayListWithCapacity((int)list.size());
        for (ItemStack itemStack : list) {
            for (ServerPlayer serverPlayer : collection) {
                if (!serverPlayer.inventory.add(itemStack.copy())) continue;
                arrayList.add(itemStack);
            }
        }
        callback.accept(arrayList);
        return arrayList.size();
    }

    private static void setSlots(Entity entity, List<ItemStack> list, int n, int n2, List<ItemStack> list2) {
        for (int i = 0; i < n2; ++i) {
            ItemStack itemStack;
            ItemStack itemStack2 = itemStack = i < list.size() ? list.get(i) : ItemStack.EMPTY;
            if (!entity.setSlot(n + i, itemStack.copy())) continue;
            list2.add(itemStack);
        }
    }

    private static int entityReplace(Collection<? extends Entity> collection, int n, int n2, List<ItemStack> list, Callback callback) throws CommandSyntaxException {
        ArrayList arrayList = Lists.newArrayListWithCapacity((int)list.size());
        for (Entity entity : collection) {
            if (entity instanceof ServerPlayer) {
                ServerPlayer serverPlayer = (ServerPlayer)entity;
                serverPlayer.inventoryMenu.broadcastChanges();
                LootCommand.setSlots(entity, list, n, n2, arrayList);
                serverPlayer.inventoryMenu.broadcastChanges();
                continue;
            }
            LootCommand.setSlots(entity, list, n, n2, arrayList);
        }
        callback.accept(arrayList);
        return arrayList.size();
    }

    private static int dropInWorld(CommandSourceStack commandSourceStack, Vec3 vec3, List<ItemStack> list, Callback callback) throws CommandSyntaxException {
        ServerLevel serverLevel = commandSourceStack.getLevel();
        list.forEach(itemStack -> {
            ItemEntity itemEntity = new ItemEntity(serverLevel, vec3.x, vec3.y, vec3.z, itemStack.copy());
            itemEntity.setDefaultPickUpDelay();
            serverLevel.addFreshEntity(itemEntity);
        });
        callback.accept(list);
        return list.size();
    }

    private static void callback(CommandSourceStack commandSourceStack, List<ItemStack> list) {
        if (list.size() == 1) {
            ItemStack itemStack = list.get(0);
            commandSourceStack.sendSuccess(new TranslatableComponent("commands.drop.success.single", itemStack.getCount(), itemStack.getDisplayName()), false);
        } else {
            commandSourceStack.sendSuccess(new TranslatableComponent("commands.drop.success.multiple", list.size()), false);
        }
    }

    private static void callback(CommandSourceStack commandSourceStack, List<ItemStack> list, ResourceLocation resourceLocation) {
        if (list.size() == 1) {
            ItemStack itemStack = list.get(0);
            commandSourceStack.sendSuccess(new TranslatableComponent("commands.drop.success.single_with_table", itemStack.getCount(), itemStack.getDisplayName(), resourceLocation), false);
        } else {
            commandSourceStack.sendSuccess(new TranslatableComponent("commands.drop.success.multiple_with_table", list.size(), resourceLocation), false);
        }
    }

    private static ItemStack getSourceHandItem(CommandSourceStack commandSourceStack, EquipmentSlot equipmentSlot) throws CommandSyntaxException {
        Entity entity = commandSourceStack.getEntityOrException();
        if (entity instanceof LivingEntity) {
            return ((LivingEntity)entity).getItemBySlot(equipmentSlot);
        }
        throw ERROR_NO_HELD_ITEMS.create((Object)entity.getDisplayName());
    }

    private static int dropBlockLoot(CommandContext<CommandSourceStack> commandContext, BlockPos blockPos, ItemStack itemStack, DropConsumer dropConsumer) throws CommandSyntaxException {
        CommandSourceStack commandSourceStack = (CommandSourceStack)commandContext.getSource();
        ServerLevel serverLevel = commandSourceStack.getLevel();
        BlockState blockState = serverLevel.getBlockState(blockPos);
        BlockEntity blockEntity = serverLevel.getBlockEntity(blockPos);
        LootContext.Builder builder = new LootContext.Builder(serverLevel).withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(blockPos)).withParameter(LootContextParams.BLOCK_STATE, blockState).withOptionalParameter(LootContextParams.BLOCK_ENTITY, blockEntity).withOptionalParameter(LootContextParams.THIS_ENTITY, commandSourceStack.getEntity()).withParameter(LootContextParams.TOOL, itemStack);
        List<ItemStack> list2 = blockState.getDrops(builder);
        return dropConsumer.accept(commandContext, list2, list -> LootCommand.callback(commandSourceStack, list, blockState.getBlock().getLootTable()));
    }

    private static int dropKillLoot(CommandContext<CommandSourceStack> commandContext, Entity entity, DropConsumer dropConsumer) throws CommandSyntaxException {
        if (!(entity instanceof LivingEntity)) {
            throw ERROR_NO_LOOT_TABLE.create((Object)entity.getDisplayName());
        }
        ResourceLocation resourceLocation = ((LivingEntity)entity).getLootTable();
        CommandSourceStack commandSourceStack = (CommandSourceStack)commandContext.getSource();
        LootContext.Builder builder = new LootContext.Builder(commandSourceStack.getLevel());
        Entity entity2 = commandSourceStack.getEntity();
        if (entity2 instanceof Player) {
            builder.withParameter(LootContextParams.LAST_DAMAGE_PLAYER, (Player)entity2);
        }
        builder.withParameter(LootContextParams.DAMAGE_SOURCE, DamageSource.MAGIC);
        builder.withOptionalParameter(LootContextParams.DIRECT_KILLER_ENTITY, entity2);
        builder.withOptionalParameter(LootContextParams.KILLER_ENTITY, entity2);
        builder.withParameter(LootContextParams.THIS_ENTITY, entity);
        builder.withParameter(LootContextParams.ORIGIN, commandSourceStack.getPosition());
        LootTable lootTable = commandSourceStack.getServer().getLootTables().get(resourceLocation);
        List<ItemStack> list2 = lootTable.getRandomItems(builder.create(LootContextParamSets.ENTITY));
        return dropConsumer.accept(commandContext, list2, list -> LootCommand.callback(commandSourceStack, list, resourceLocation));
    }

    private static int dropChestLoot(CommandContext<CommandSourceStack> commandContext, ResourceLocation resourceLocation, DropConsumer dropConsumer) throws CommandSyntaxException {
        CommandSourceStack commandSourceStack = (CommandSourceStack)commandContext.getSource();
        LootContext.Builder builder = new LootContext.Builder(commandSourceStack.getLevel()).withOptionalParameter(LootContextParams.THIS_ENTITY, commandSourceStack.getEntity()).withParameter(LootContextParams.ORIGIN, commandSourceStack.getPosition());
        return LootCommand.drop(commandContext, resourceLocation, builder.create(LootContextParamSets.CHEST), dropConsumer);
    }

    private static int dropFishingLoot(CommandContext<CommandSourceStack> commandContext, ResourceLocation resourceLocation, BlockPos blockPos, ItemStack itemStack, DropConsumer dropConsumer) throws CommandSyntaxException {
        CommandSourceStack commandSourceStack = (CommandSourceStack)commandContext.getSource();
        LootContext lootContext = new LootContext.Builder(commandSourceStack.getLevel()).withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(blockPos)).withParameter(LootContextParams.TOOL, itemStack).withOptionalParameter(LootContextParams.THIS_ENTITY, commandSourceStack.getEntity()).create(LootContextParamSets.FISHING);
        return LootCommand.drop(commandContext, resourceLocation, lootContext, dropConsumer);
    }

    private static int drop(CommandContext<CommandSourceStack> commandContext, ResourceLocation resourceLocation, LootContext lootContext, DropConsumer dropConsumer) throws CommandSyntaxException {
        CommandSourceStack commandSourceStack = (CommandSourceStack)commandContext.getSource();
        LootTable lootTable = commandSourceStack.getServer().getLootTables().get(resourceLocation);
        List<ItemStack> list2 = lootTable.getRandomItems(lootContext);
        return dropConsumer.accept(commandContext, list2, list -> LootCommand.callback(commandSourceStack, list));
    }

    @FunctionalInterface
    static interface TailProvider {
        public ArgumentBuilder<CommandSourceStack, ?> construct(ArgumentBuilder<CommandSourceStack, ?> var1, DropConsumer var2);
    }

    @FunctionalInterface
    static interface DropConsumer {
        public int accept(CommandContext<CommandSourceStack> var1, List<ItemStack> var2, Callback var3) throws CommandSyntaxException;
    }

    @FunctionalInterface
    static interface Callback {
        public void accept(List<ItemStack> var1) throws CommandSyntaxException;
    }

}

