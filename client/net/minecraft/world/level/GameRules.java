/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Maps
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.arguments.BoolArgumentType
 *  com.mojang.brigadier.arguments.IntegerArgumentType
 *  com.mojang.brigadier.builder.RequiredArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.DynamicLike
 *  com.mojang.serialization.OptionalDynamic
 *  javax.annotation.Nullable
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.world.level;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicLike;
import com.mojang.serialization.OptionalDynamic;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.entity.Entity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GameRules {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Map<Key<?>, Type<?>> GAME_RULE_TYPES = Maps.newTreeMap(Comparator.comparing(key -> Key.access$600(key)));
    public static final Key<BooleanValue> RULE_DOFIRETICK = GameRules.register("doFireTick", Category.UPDATES, BooleanValue.access$000(true));
    public static final Key<BooleanValue> RULE_MOBGRIEFING = GameRules.register("mobGriefing", Category.MOBS, BooleanValue.access$000(true));
    public static final Key<BooleanValue> RULE_KEEPINVENTORY = GameRules.register("keepInventory", Category.PLAYER, BooleanValue.access$000(false));
    public static final Key<BooleanValue> RULE_DOMOBSPAWNING = GameRules.register("doMobSpawning", Category.SPAWNING, BooleanValue.access$000(true));
    public static final Key<BooleanValue> RULE_DOMOBLOOT = GameRules.register("doMobLoot", Category.DROPS, BooleanValue.access$000(true));
    public static final Key<BooleanValue> RULE_DOBLOCKDROPS = GameRules.register("doTileDrops", Category.DROPS, BooleanValue.access$000(true));
    public static final Key<BooleanValue> RULE_DOENTITYDROPS = GameRules.register("doEntityDrops", Category.DROPS, BooleanValue.access$000(true));
    public static final Key<BooleanValue> RULE_COMMANDBLOCKOUTPUT = GameRules.register("commandBlockOutput", Category.CHAT, BooleanValue.access$000(true));
    public static final Key<BooleanValue> RULE_NATURAL_REGENERATION = GameRules.register("naturalRegeneration", Category.PLAYER, BooleanValue.access$000(true));
    public static final Key<BooleanValue> RULE_DAYLIGHT = GameRules.register("doDaylightCycle", Category.UPDATES, BooleanValue.access$000(true));
    public static final Key<BooleanValue> RULE_LOGADMINCOMMANDS = GameRules.register("logAdminCommands", Category.CHAT, BooleanValue.access$000(true));
    public static final Key<BooleanValue> RULE_SHOWDEATHMESSAGES = GameRules.register("showDeathMessages", Category.CHAT, BooleanValue.access$000(true));
    public static final Key<IntegerValue> RULE_RANDOMTICKING = GameRules.register("randomTickSpeed", Category.UPDATES, IntegerValue.access$100(3));
    public static final Key<BooleanValue> RULE_SENDCOMMANDFEEDBACK = GameRules.register("sendCommandFeedback", Category.CHAT, BooleanValue.access$000(true));
    public static final Key<BooleanValue> RULE_REDUCEDDEBUGINFO = GameRules.register("reducedDebugInfo", Category.MISC, BooleanValue.access$200(false, (minecraftServer, booleanValue) -> {
        byte by = booleanValue.get() ? (byte)22 : (byte)23;
        for (ServerPlayer serverPlayer : minecraftServer.getPlayerList().getPlayers()) {
            serverPlayer.connection.send(new ClientboundEntityEventPacket(serverPlayer, by));
        }
    }));
    public static final Key<BooleanValue> RULE_SPECTATORSGENERATECHUNKS = GameRules.register("spectatorsGenerateChunks", Category.PLAYER, BooleanValue.access$000(true));
    public static final Key<IntegerValue> RULE_SPAWN_RADIUS = GameRules.register("spawnRadius", Category.PLAYER, IntegerValue.access$100(10));
    public static final Key<BooleanValue> RULE_DISABLE_ELYTRA_MOVEMENT_CHECK = GameRules.register("disableElytraMovementCheck", Category.PLAYER, BooleanValue.access$000(false));
    public static final Key<IntegerValue> RULE_MAX_ENTITY_CRAMMING = GameRules.register("maxEntityCramming", Category.MOBS, IntegerValue.access$100(24));
    public static final Key<BooleanValue> RULE_WEATHER_CYCLE = GameRules.register("doWeatherCycle", Category.UPDATES, BooleanValue.access$000(true));
    public static final Key<BooleanValue> RULE_LIMITED_CRAFTING = GameRules.register("doLimitedCrafting", Category.PLAYER, BooleanValue.access$000(false));
    public static final Key<IntegerValue> RULE_MAX_COMMAND_CHAIN_LENGTH = GameRules.register("maxCommandChainLength", Category.MISC, IntegerValue.access$100(65536));
    public static final Key<BooleanValue> RULE_ANNOUNCE_ADVANCEMENTS = GameRules.register("announceAdvancements", Category.CHAT, BooleanValue.access$000(true));
    public static final Key<BooleanValue> RULE_DISABLE_RAIDS = GameRules.register("disableRaids", Category.MOBS, BooleanValue.access$000(false));
    public static final Key<BooleanValue> RULE_DOINSOMNIA = GameRules.register("doInsomnia", Category.SPAWNING, BooleanValue.access$000(true));
    public static final Key<BooleanValue> RULE_DO_IMMEDIATE_RESPAWN = GameRules.register("doImmediateRespawn", Category.PLAYER, BooleanValue.access$200(false, (minecraftServer, booleanValue) -> {
        for (ServerPlayer serverPlayer : minecraftServer.getPlayerList().getPlayers()) {
            serverPlayer.connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.IMMEDIATE_RESPAWN, booleanValue.get() ? 1.0f : 0.0f));
        }
    }));
    public static final Key<BooleanValue> RULE_DROWNING_DAMAGE = GameRules.register("drowningDamage", Category.PLAYER, BooleanValue.access$000(true));
    public static final Key<BooleanValue> RULE_FALL_DAMAGE = GameRules.register("fallDamage", Category.PLAYER, BooleanValue.access$000(true));
    public static final Key<BooleanValue> RULE_FIRE_DAMAGE = GameRules.register("fireDamage", Category.PLAYER, BooleanValue.access$000(true));
    public static final Key<BooleanValue> RULE_DO_PATROL_SPAWNING = GameRules.register("doPatrolSpawning", Category.SPAWNING, BooleanValue.access$000(true));
    public static final Key<BooleanValue> RULE_DO_TRADER_SPAWNING = GameRules.register("doTraderSpawning", Category.SPAWNING, BooleanValue.access$000(true));
    public static final Key<BooleanValue> RULE_FORGIVE_DEAD_PLAYERS = GameRules.register("forgiveDeadPlayers", Category.MOBS, BooleanValue.access$000(true));
    public static final Key<BooleanValue> RULE_UNIVERSAL_ANGER = GameRules.register("universalAnger", Category.MOBS, BooleanValue.access$000(false));
    private final Map<Key<?>, Value<?>> rules;

    private static <T extends Value<T>> Key<T> register(String string, Category category, Type<T> type) {
        Key key = new Key(string, category);
        Type<T> type2 = GAME_RULE_TYPES.put(key, type);
        if (type2 != null) {
            throw new IllegalStateException("Duplicate game rule registration for " + string);
        }
        return key;
    }

    public GameRules(DynamicLike<?> dynamicLike) {
        this();
        this.loadFromTag(dynamicLike);
    }

    public GameRules() {
        this.rules = (Map)GAME_RULE_TYPES.entrySet().stream().collect(ImmutableMap.toImmutableMap(Map.Entry::getKey, entry -> ((Type)entry.getValue()).createRule()));
    }

    private GameRules(Map<Key<?>, Value<?>> map) {
        this.rules = map;
    }

    public <T extends Value<T>> T getRule(Key<T> key) {
        return (T)this.rules.get(key);
    }

    public CompoundTag createTag() {
        CompoundTag compoundTag = new CompoundTag();
        this.rules.forEach((key, value) -> compoundTag.putString(key.id, value.serialize()));
        return compoundTag;
    }

    private void loadFromTag(DynamicLike<?> dynamicLike) {
        this.rules.forEach((key, value) -> dynamicLike.get(key.id).asString().result().ifPresent(value::deserialize));
    }

    public GameRules copy() {
        return new GameRules((Map)this.rules.entrySet().stream().collect(ImmutableMap.toImmutableMap(Map.Entry::getKey, entry -> ((Value)entry.getValue()).copy())));
    }

    public static void visitGameRuleTypes(GameRuleTypeVisitor gameRuleTypeVisitor) {
        GAME_RULE_TYPES.forEach((key, type) -> GameRules.callVisitorCap(gameRuleTypeVisitor, key, type));
    }

    private static <T extends Value<T>> void callVisitorCap(GameRuleTypeVisitor gameRuleTypeVisitor, Key<?> key, Type<?> type) {
        Key<?> key2 = key;
        Type<?> type2 = type;
        gameRuleTypeVisitor.visit(key2, type2);
        type2.callVisitor(gameRuleTypeVisitor, key2);
    }

    public void assignFrom(GameRules gameRules, @Nullable MinecraftServer minecraftServer) {
        gameRules.rules.keySet().forEach(key -> this.assignCap((Key<T>)key, gameRules, minecraftServer));
    }

    private <T extends Value<T>> void assignCap(Key<T> key, GameRules gameRules, @Nullable MinecraftServer minecraftServer) {
        T t = gameRules.getRule(key);
        ((Value)this.getRule(key)).setFrom(t, minecraftServer);
    }

    public boolean getBoolean(Key<BooleanValue> key) {
        return this.getRule(key).get();
    }

    public int getInt(Key<IntegerValue> key) {
        return this.getRule(key).get();
    }

    public static class BooleanValue
    extends Value<BooleanValue> {
        private boolean value;

        private static Type<BooleanValue> create(boolean bl, BiConsumer<MinecraftServer, BooleanValue> biConsumer) {
            return new Type<BooleanValue>(BoolArgumentType::bool, type -> new BooleanValue((Type<BooleanValue>)type, bl), biConsumer, GameRuleTypeVisitor::visitBoolean);
        }

        private static Type<BooleanValue> create(boolean bl) {
            return BooleanValue.create(bl, (minecraftServer, booleanValue) -> {});
        }

        public BooleanValue(Type<BooleanValue> type, boolean bl) {
            super(type);
            this.value = bl;
        }

        @Override
        protected void updateFromArgument(CommandContext<CommandSourceStack> commandContext, String string) {
            this.value = BoolArgumentType.getBool(commandContext, (String)string);
        }

        public boolean get() {
            return this.value;
        }

        public void set(boolean bl, @Nullable MinecraftServer minecraftServer) {
            this.value = bl;
            this.onChanged(minecraftServer);
        }

        @Override
        public String serialize() {
            return Boolean.toString(this.value);
        }

        @Override
        protected void deserialize(String string) {
            this.value = Boolean.parseBoolean(string);
        }

        @Override
        public int getCommandResult() {
            return this.value ? 1 : 0;
        }

        @Override
        protected BooleanValue getSelf() {
            return this;
        }

        @Override
        protected BooleanValue copy() {
            return new BooleanValue(this.type, this.value);
        }

        @Override
        public void setFrom(BooleanValue booleanValue, @Nullable MinecraftServer minecraftServer) {
            this.value = booleanValue.value;
            this.onChanged(minecraftServer);
        }

        @Override
        protected /* synthetic */ Value copy() {
            return this.copy();
        }

        @Override
        protected /* synthetic */ Value getSelf() {
            return this.getSelf();
        }

        static /* synthetic */ Type access$000(boolean bl) {
            return BooleanValue.create(bl);
        }

        static /* synthetic */ Type access$200(boolean bl, BiConsumer biConsumer) {
            return BooleanValue.create(bl, biConsumer);
        }
    }

    public static class IntegerValue
    extends Value<IntegerValue> {
        private int value;

        private static Type<IntegerValue> create(int n, BiConsumer<MinecraftServer, IntegerValue> biConsumer) {
            return new Type<IntegerValue>(IntegerArgumentType::integer, type -> new IntegerValue((Type<IntegerValue>)type, n), biConsumer, GameRuleTypeVisitor::visitInteger);
        }

        private static Type<IntegerValue> create(int n) {
            return IntegerValue.create(n, (minecraftServer, integerValue) -> {});
        }

        public IntegerValue(Type<IntegerValue> type, int n) {
            super(type);
            this.value = n;
        }

        @Override
        protected void updateFromArgument(CommandContext<CommandSourceStack> commandContext, String string) {
            this.value = IntegerArgumentType.getInteger(commandContext, (String)string);
        }

        public int get() {
            return this.value;
        }

        @Override
        public String serialize() {
            return Integer.toString(this.value);
        }

        @Override
        protected void deserialize(String string) {
            this.value = IntegerValue.safeParse(string);
        }

        public boolean tryDeserialize(String string) {
            try {
                this.value = Integer.parseInt(string);
                return true;
            }
            catch (NumberFormatException numberFormatException) {
                return false;
            }
        }

        private static int safeParse(String string) {
            if (!string.isEmpty()) {
                try {
                    return Integer.parseInt(string);
                }
                catch (NumberFormatException numberFormatException) {
                    LOGGER.warn("Failed to parse integer {}", (Object)string);
                }
            }
            return 0;
        }

        @Override
        public int getCommandResult() {
            return this.value;
        }

        @Override
        protected IntegerValue getSelf() {
            return this;
        }

        @Override
        protected IntegerValue copy() {
            return new IntegerValue(this.type, this.value);
        }

        @Override
        public void setFrom(IntegerValue integerValue, @Nullable MinecraftServer minecraftServer) {
            this.value = integerValue.value;
            this.onChanged(minecraftServer);
        }

        @Override
        protected /* synthetic */ Value copy() {
            return this.copy();
        }

        @Override
        protected /* synthetic */ Value getSelf() {
            return this.getSelf();
        }

        static /* synthetic */ Type access$100(int n) {
            return IntegerValue.create(n);
        }
    }

    public static abstract class Value<T extends Value<T>> {
        protected final Type<T> type;

        public Value(Type<T> type) {
            this.type = type;
        }

        protected abstract void updateFromArgument(CommandContext<CommandSourceStack> var1, String var2);

        public void setFromArgument(CommandContext<CommandSourceStack> commandContext, String string) {
            this.updateFromArgument(commandContext, string);
            this.onChanged(((CommandSourceStack)commandContext.getSource()).getServer());
        }

        protected void onChanged(@Nullable MinecraftServer minecraftServer) {
            if (minecraftServer != null) {
                this.type.callback.accept(minecraftServer, this.getSelf());
            }
        }

        protected abstract void deserialize(String var1);

        public abstract String serialize();

        public String toString() {
            return this.serialize();
        }

        public abstract int getCommandResult();

        protected abstract T getSelf();

        protected abstract T copy();

        public abstract void setFrom(T var1, @Nullable MinecraftServer var2);
    }

    public static class Type<T extends Value<T>> {
        private final Supplier<ArgumentType<?>> argument;
        private final Function<Type<T>, T> constructor;
        private final BiConsumer<MinecraftServer, T> callback;
        private final VisitorCaller<T> visitorCaller;

        private Type(Supplier<ArgumentType<?>> supplier, Function<Type<T>, T> function, BiConsumer<MinecraftServer, T> biConsumer, VisitorCaller<T> visitorCaller) {
            this.argument = supplier;
            this.constructor = function;
            this.callback = biConsumer;
            this.visitorCaller = visitorCaller;
        }

        public RequiredArgumentBuilder<CommandSourceStack, ?> createArgument(String string) {
            return Commands.argument(string, this.argument.get());
        }

        public T createRule() {
            return (T)((Value)this.constructor.apply(this));
        }

        public void callVisitor(GameRuleTypeVisitor gameRuleTypeVisitor, Key<T> key) {
            this.visitorCaller.call(gameRuleTypeVisitor, key, this);
        }
    }

    public static final class Key<T extends Value<T>> {
        private final String id;
        private final Category category;

        public Key(String string, Category category) {
            this.id = string;
            this.category = category;
        }

        public String toString() {
            return this.id;
        }

        public boolean equals(Object object) {
            if (this == object) {
                return true;
            }
            return object instanceof Key && ((Key)object).id.equals(this.id);
        }

        public int hashCode() {
            return this.id.hashCode();
        }

        public String getId() {
            return this.id;
        }

        public String getDescriptionId() {
            return "gamerule." + this.id;
        }

        public Category getCategory() {
            return this.category;
        }
    }

    public static interface GameRuleTypeVisitor {
        default public <T extends Value<T>> void visit(Key<T> key, Type<T> type) {
        }

        default public void visitBoolean(Key<BooleanValue> key, Type<BooleanValue> type) {
        }

        default public void visitInteger(Key<IntegerValue> key, Type<IntegerValue> type) {
        }
    }

    static interface VisitorCaller<T extends Value<T>> {
        public void call(GameRuleTypeVisitor var1, Key<T> var2, Type<T> var3);
    }

    public static enum Category {
        PLAYER("gamerule.category.player"),
        MOBS("gamerule.category.mobs"),
        SPAWNING("gamerule.category.spawning"),
        DROPS("gamerule.category.drops"),
        UPDATES("gamerule.category.updates"),
        CHAT("gamerule.category.chat"),
        MISC("gamerule.category.misc");
        
        private final String descriptionId;

        private Category(String string2) {
            this.descriptionId = string2;
        }

        public String getDescriptionId() {
            return this.descriptionId;
        }
    }

}

