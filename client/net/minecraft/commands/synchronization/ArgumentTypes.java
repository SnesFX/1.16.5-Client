/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.mojang.brigadier.Command
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.tree.ArgumentCommandNode
 *  com.mojang.brigadier.tree.CommandNode
 *  com.mojang.brigadier.tree.LiteralCommandNode
 *  com.mojang.brigadier.tree.RootCommandNode
 *  javax.annotation.Nullable
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.commands.synchronization;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.commands.arguments.AngleArgument;
import net.minecraft.commands.arguments.ColorArgument;
import net.minecraft.commands.arguments.ComponentArgument;
import net.minecraft.commands.arguments.CompoundTagArgument;
import net.minecraft.commands.arguments.DimensionArgument;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.EntitySummonArgument;
import net.minecraft.commands.arguments.GameProfileArgument;
import net.minecraft.commands.arguments.ItemEnchantmentArgument;
import net.minecraft.commands.arguments.MessageArgument;
import net.minecraft.commands.arguments.MobEffectArgument;
import net.minecraft.commands.arguments.NbtPathArgument;
import net.minecraft.commands.arguments.NbtTagArgument;
import net.minecraft.commands.arguments.ObjectiveArgument;
import net.minecraft.commands.arguments.ObjectiveCriteriaArgument;
import net.minecraft.commands.arguments.OperationArgument;
import net.minecraft.commands.arguments.ParticleArgument;
import net.minecraft.commands.arguments.RangeArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.arguments.ScoreHolderArgument;
import net.minecraft.commands.arguments.ScoreboardSlotArgument;
import net.minecraft.commands.arguments.SlotArgument;
import net.minecraft.commands.arguments.TeamArgument;
import net.minecraft.commands.arguments.TimeArgument;
import net.minecraft.commands.arguments.UuidArgument;
import net.minecraft.commands.arguments.blocks.BlockPredicateArgument;
import net.minecraft.commands.arguments.blocks.BlockStateArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.commands.arguments.coordinates.ColumnPosArgument;
import net.minecraft.commands.arguments.coordinates.RotationArgument;
import net.minecraft.commands.arguments.coordinates.SwizzleArgument;
import net.minecraft.commands.arguments.coordinates.Vec2Argument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.commands.arguments.item.FunctionArgument;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.commands.arguments.item.ItemPredicateArgument;
import net.minecraft.commands.synchronization.ArgumentSerializer;
import net.minecraft.commands.synchronization.EmptyArgumentSerializer;
import net.minecraft.commands.synchronization.brigadier.BrigadierArgumentSerializers;
import net.minecraft.gametest.framework.TestClassNameArgument;
import net.minecraft.gametest.framework.TestFunctionArgument;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ArgumentTypes {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Map<Class<?>, Entry<?>> BY_CLASS = Maps.newHashMap();
    private static final Map<ResourceLocation, Entry<?>> BY_NAME = Maps.newHashMap();

    public static <T extends ArgumentType<?>> void register(String string, Class<T> class_, ArgumentSerializer<T> argumentSerializer) {
        ResourceLocation resourceLocation = new ResourceLocation(string);
        if (BY_CLASS.containsKey(class_)) {
            throw new IllegalArgumentException("Class " + class_.getName() + " already has a serializer!");
        }
        if (BY_NAME.containsKey(resourceLocation)) {
            throw new IllegalArgumentException("'" + resourceLocation + "' is already a registered serializer!");
        }
        Entry entry = new Entry(class_, argumentSerializer, resourceLocation);
        BY_CLASS.put(class_, entry);
        BY_NAME.put(resourceLocation, entry);
    }

    public static void bootStrap() {
        BrigadierArgumentSerializers.bootstrap();
        ArgumentTypes.register("entity", EntityArgument.class, new EntityArgument.Serializer());
        ArgumentTypes.register("game_profile", GameProfileArgument.class, new EmptyArgumentSerializer<GameProfileArgument>(GameProfileArgument::gameProfile));
        ArgumentTypes.register("block_pos", BlockPosArgument.class, new EmptyArgumentSerializer<BlockPosArgument>(BlockPosArgument::blockPos));
        ArgumentTypes.register("column_pos", ColumnPosArgument.class, new EmptyArgumentSerializer<ColumnPosArgument>(ColumnPosArgument::columnPos));
        ArgumentTypes.register("vec3", Vec3Argument.class, new EmptyArgumentSerializer<Vec3Argument>(Vec3Argument::vec3));
        ArgumentTypes.register("vec2", Vec2Argument.class, new EmptyArgumentSerializer<Vec2Argument>(Vec2Argument::vec2));
        ArgumentTypes.register("block_state", BlockStateArgument.class, new EmptyArgumentSerializer<BlockStateArgument>(BlockStateArgument::block));
        ArgumentTypes.register("block_predicate", BlockPredicateArgument.class, new EmptyArgumentSerializer<BlockPredicateArgument>(BlockPredicateArgument::blockPredicate));
        ArgumentTypes.register("item_stack", ItemArgument.class, new EmptyArgumentSerializer<ItemArgument>(ItemArgument::item));
        ArgumentTypes.register("item_predicate", ItemPredicateArgument.class, new EmptyArgumentSerializer<ItemPredicateArgument>(ItemPredicateArgument::itemPredicate));
        ArgumentTypes.register("color", ColorArgument.class, new EmptyArgumentSerializer<ColorArgument>(ColorArgument::color));
        ArgumentTypes.register("component", ComponentArgument.class, new EmptyArgumentSerializer<ComponentArgument>(ComponentArgument::textComponent));
        ArgumentTypes.register("message", MessageArgument.class, new EmptyArgumentSerializer<MessageArgument>(MessageArgument::message));
        ArgumentTypes.register("nbt_compound_tag", CompoundTagArgument.class, new EmptyArgumentSerializer<CompoundTagArgument>(CompoundTagArgument::compoundTag));
        ArgumentTypes.register("nbt_tag", NbtTagArgument.class, new EmptyArgumentSerializer<NbtTagArgument>(NbtTagArgument::nbtTag));
        ArgumentTypes.register("nbt_path", NbtPathArgument.class, new EmptyArgumentSerializer<NbtPathArgument>(NbtPathArgument::nbtPath));
        ArgumentTypes.register("objective", ObjectiveArgument.class, new EmptyArgumentSerializer<ObjectiveArgument>(ObjectiveArgument::objective));
        ArgumentTypes.register("objective_criteria", ObjectiveCriteriaArgument.class, new EmptyArgumentSerializer<ObjectiveCriteriaArgument>(ObjectiveCriteriaArgument::criteria));
        ArgumentTypes.register("operation", OperationArgument.class, new EmptyArgumentSerializer<OperationArgument>(OperationArgument::operation));
        ArgumentTypes.register("particle", ParticleArgument.class, new EmptyArgumentSerializer<ParticleArgument>(ParticleArgument::particle));
        ArgumentTypes.register("angle", AngleArgument.class, new EmptyArgumentSerializer<AngleArgument>(AngleArgument::angle));
        ArgumentTypes.register("rotation", RotationArgument.class, new EmptyArgumentSerializer<RotationArgument>(RotationArgument::rotation));
        ArgumentTypes.register("scoreboard_slot", ScoreboardSlotArgument.class, new EmptyArgumentSerializer<ScoreboardSlotArgument>(ScoreboardSlotArgument::displaySlot));
        ArgumentTypes.register("score_holder", ScoreHolderArgument.class, new ScoreHolderArgument.Serializer());
        ArgumentTypes.register("swizzle", SwizzleArgument.class, new EmptyArgumentSerializer<SwizzleArgument>(SwizzleArgument::swizzle));
        ArgumentTypes.register("team", TeamArgument.class, new EmptyArgumentSerializer<TeamArgument>(TeamArgument::team));
        ArgumentTypes.register("item_slot", SlotArgument.class, new EmptyArgumentSerializer<SlotArgument>(SlotArgument::slot));
        ArgumentTypes.register("resource_location", ResourceLocationArgument.class, new EmptyArgumentSerializer<ResourceLocationArgument>(ResourceLocationArgument::id));
        ArgumentTypes.register("mob_effect", MobEffectArgument.class, new EmptyArgumentSerializer<MobEffectArgument>(MobEffectArgument::effect));
        ArgumentTypes.register("function", FunctionArgument.class, new EmptyArgumentSerializer<FunctionArgument>(FunctionArgument::functions));
        ArgumentTypes.register("entity_anchor", EntityAnchorArgument.class, new EmptyArgumentSerializer<EntityAnchorArgument>(EntityAnchorArgument::anchor));
        ArgumentTypes.register("int_range", RangeArgument.Ints.class, new EmptyArgumentSerializer<RangeArgument.Ints>(RangeArgument::intRange));
        ArgumentTypes.register("float_range", RangeArgument.Floats.class, new EmptyArgumentSerializer<RangeArgument.Floats>(RangeArgument::floatRange));
        ArgumentTypes.register("item_enchantment", ItemEnchantmentArgument.class, new EmptyArgumentSerializer<ItemEnchantmentArgument>(ItemEnchantmentArgument::enchantment));
        ArgumentTypes.register("entity_summon", EntitySummonArgument.class, new EmptyArgumentSerializer<EntitySummonArgument>(EntitySummonArgument::id));
        ArgumentTypes.register("dimension", DimensionArgument.class, new EmptyArgumentSerializer<DimensionArgument>(DimensionArgument::dimension));
        ArgumentTypes.register("time", TimeArgument.class, new EmptyArgumentSerializer<TimeArgument>(TimeArgument::time));
        ArgumentTypes.register("uuid", UuidArgument.class, new EmptyArgumentSerializer<UuidArgument>(UuidArgument::uuid));
        if (SharedConstants.IS_RUNNING_IN_IDE) {
            ArgumentTypes.register("test_argument", TestFunctionArgument.class, new EmptyArgumentSerializer<TestFunctionArgument>(TestFunctionArgument::testFunctionArgument));
            ArgumentTypes.register("test_class", TestClassNameArgument.class, new EmptyArgumentSerializer<TestClassNameArgument>(TestClassNameArgument::testClassName));
        }
    }

    @Nullable
    private static Entry<?> get(ResourceLocation resourceLocation) {
        return BY_NAME.get(resourceLocation);
    }

    @Nullable
    private static Entry<?> get(ArgumentType<?> argumentType) {
        return BY_CLASS.get(argumentType.getClass());
    }

    public static <T extends ArgumentType<?>> void serialize(FriendlyByteBuf friendlyByteBuf, T t) {
        Entry<?> entry = ArgumentTypes.get(t);
        if (entry == null) {
            LOGGER.error("Could not serialize {} ({}) - will not be sent to client!", t, t.getClass());
            friendlyByteBuf.writeResourceLocation(new ResourceLocation(""));
            return;
        }
        friendlyByteBuf.writeResourceLocation(entry.name);
        entry.serializer.serializeToNetwork(t, friendlyByteBuf);
    }

    @Nullable
    public static ArgumentType<?> deserialize(FriendlyByteBuf friendlyByteBuf) {
        ResourceLocation resourceLocation = friendlyByteBuf.readResourceLocation();
        Entry<?> entry = ArgumentTypes.get(resourceLocation);
        if (entry == null) {
            LOGGER.error("Could not deserialize {}", (Object)resourceLocation);
            return null;
        }
        return entry.serializer.deserializeFromNetwork(friendlyByteBuf);
    }

    private static <T extends ArgumentType<?>> void serializeToJson(JsonObject jsonObject, T t) {
        Entry<?> entry = ArgumentTypes.get(t);
        if (entry == null) {
            LOGGER.error("Could not serialize argument {} ({})!", t, t.getClass());
            jsonObject.addProperty("type", "unknown");
        } else {
            jsonObject.addProperty("type", "argument");
            jsonObject.addProperty("parser", entry.name.toString());
            JsonObject jsonObject2 = new JsonObject();
            entry.serializer.serializeToJson(t, jsonObject2);
            if (jsonObject2.size() > 0) {
                jsonObject.add("properties", (JsonElement)jsonObject2);
            }
        }
    }

    public static <S> JsonObject serializeNodeToJson(CommandDispatcher<S> commandDispatcher, CommandNode<S> commandNode) {
        CommandNode commandNode2;
        JsonObject jsonObject = new JsonObject();
        if (commandNode instanceof RootCommandNode) {
            jsonObject.addProperty("type", "root");
        } else if (commandNode instanceof LiteralCommandNode) {
            jsonObject.addProperty("type", "literal");
        } else if (commandNode instanceof ArgumentCommandNode) {
            ArgumentTypes.serializeToJson(jsonObject, ((ArgumentCommandNode)commandNode).getType());
        } else {
            LOGGER.error("Could not serialize node {} ({})!", commandNode, commandNode.getClass());
            jsonObject.addProperty("type", "unknown");
        }
        JsonObject jsonObject2 = new JsonObject();
        Object object = commandNode.getChildren().iterator();
        while (object.hasNext()) {
            commandNode2 = (CommandNode)object.next();
            jsonObject2.add(commandNode2.getName(), (JsonElement)ArgumentTypes.serializeNodeToJson(commandDispatcher, commandNode2));
        }
        if (jsonObject2.size() > 0) {
            jsonObject.add("children", (JsonElement)jsonObject2);
        }
        if (commandNode.getCommand() != null) {
            jsonObject.addProperty("executable", Boolean.valueOf(true));
        }
        if (commandNode.getRedirect() != null && !(object = commandDispatcher.getPath(commandNode.getRedirect())).isEmpty()) {
            commandNode2 = new JsonArray();
            Iterator iterator = object.iterator();
            while (iterator.hasNext()) {
                String string = (String)iterator.next();
                commandNode2.add(string);
            }
            jsonObject.add("redirect", (JsonElement)commandNode2);
        }
        return jsonObject;
    }

    public static boolean isTypeRegistered(ArgumentType<?> argumentType) {
        return ArgumentTypes.get(argumentType) != null;
    }

    public static <T> Set<ArgumentType<?>> findUsedArgumentTypes(CommandNode<T> commandNode) {
        Set set = Sets.newIdentityHashSet();
        HashSet hashSet = Sets.newHashSet();
        ArgumentTypes.findUsedArgumentTypes(commandNode, hashSet, set);
        return hashSet;
    }

    private static <T> void findUsedArgumentTypes(CommandNode<T> commandNode2, Set<ArgumentType<?>> set, Set<CommandNode<T>> set2) {
        if (!set2.add(commandNode2)) {
            return;
        }
        if (commandNode2 instanceof ArgumentCommandNode) {
            set.add(((ArgumentCommandNode)commandNode2).getType());
        }
        commandNode2.getChildren().forEach(commandNode -> ArgumentTypes.findUsedArgumentTypes(commandNode, set, set2));
        CommandNode commandNode3 = commandNode2.getRedirect();
        if (commandNode3 != null) {
            ArgumentTypes.findUsedArgumentTypes(commandNode3, set, set2);
        }
    }

    static class Entry<T extends ArgumentType<?>> {
        public final Class<T> clazz;
        public final ArgumentSerializer<T> serializer;
        public final ResourceLocation name;

        private Entry(Class<T> class_, ArgumentSerializer<T> argumentSerializer, ResourceLocation resourceLocation) {
            this.clazz = class_;
            this.serializer = argumentSerializer;
            this.name = resourceLocation;
        }
    }

}

