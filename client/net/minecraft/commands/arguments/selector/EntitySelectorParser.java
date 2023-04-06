/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.primitives.Doubles
 *  com.mojang.brigadier.ImmutableStringReader
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.DynamicCommandExceptionType
 *  com.mojang.brigadier.exceptions.SimpleCommandExceptionType
 *  com.mojang.brigadier.suggestion.Suggestions
 *  com.mojang.brigadier.suggestion.SuggestionsBuilder
 *  javax.annotation.Nullable
 */
package net.minecraft.commands.arguments.selector;

import com.google.common.primitives.Doubles;
import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;
import javax.annotation.Nullable;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.advancements.critereon.WrappedMinMaxBounds;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.commands.arguments.selector.options.EntitySelectorOptions;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class EntitySelectorParser {
    public static final SimpleCommandExceptionType ERROR_INVALID_NAME_OR_UUID = new SimpleCommandExceptionType((Message)new TranslatableComponent("argument.entity.invalid"));
    public static final DynamicCommandExceptionType ERROR_UNKNOWN_SELECTOR_TYPE = new DynamicCommandExceptionType(object -> new TranslatableComponent("argument.entity.selector.unknown", object));
    public static final SimpleCommandExceptionType ERROR_SELECTORS_NOT_ALLOWED = new SimpleCommandExceptionType((Message)new TranslatableComponent("argument.entity.selector.not_allowed"));
    public static final SimpleCommandExceptionType ERROR_MISSING_SELECTOR_TYPE = new SimpleCommandExceptionType((Message)new TranslatableComponent("argument.entity.selector.missing"));
    public static final SimpleCommandExceptionType ERROR_EXPECTED_END_OF_OPTIONS = new SimpleCommandExceptionType((Message)new TranslatableComponent("argument.entity.options.unterminated"));
    public static final DynamicCommandExceptionType ERROR_EXPECTED_OPTION_VALUE = new DynamicCommandExceptionType(object -> new TranslatableComponent("argument.entity.options.valueless", object));
    public static final BiConsumer<Vec3, List<? extends Entity>> ORDER_ARBITRARY = (vec3, list) -> {};
    public static final BiConsumer<Vec3, List<? extends Entity>> ORDER_NEAREST = (vec3, list) -> list.sort((entity, entity2) -> Doubles.compare((double)entity.distanceToSqr((Vec3)vec3), (double)entity2.distanceToSqr((Vec3)vec3)));
    public static final BiConsumer<Vec3, List<? extends Entity>> ORDER_FURTHEST = (vec3, list) -> list.sort((entity, entity2) -> Doubles.compare((double)entity2.distanceToSqr((Vec3)vec3), (double)entity.distanceToSqr((Vec3)vec3)));
    public static final BiConsumer<Vec3, List<? extends Entity>> ORDER_RANDOM = (vec3, list) -> Collections.shuffle(list);
    public static final BiFunction<SuggestionsBuilder, Consumer<SuggestionsBuilder>, CompletableFuture<Suggestions>> SUGGEST_NOTHING = (suggestionsBuilder, consumer) -> suggestionsBuilder.buildFuture();
    private final StringReader reader;
    private final boolean allowSelectors;
    private int maxResults;
    private boolean includesEntities;
    private boolean worldLimited;
    private MinMaxBounds.Floats distance = MinMaxBounds.Floats.ANY;
    private MinMaxBounds.Ints level = MinMaxBounds.Ints.ANY;
    @Nullable
    private Double x;
    @Nullable
    private Double y;
    @Nullable
    private Double z;
    @Nullable
    private Double deltaX;
    @Nullable
    private Double deltaY;
    @Nullable
    private Double deltaZ;
    private WrappedMinMaxBounds rotX = WrappedMinMaxBounds.ANY;
    private WrappedMinMaxBounds rotY = WrappedMinMaxBounds.ANY;
    private Predicate<Entity> predicate = entity -> true;
    private BiConsumer<Vec3, List<? extends Entity>> order = ORDER_ARBITRARY;
    private boolean currentEntity;
    @Nullable
    private String playerName;
    private int startPosition;
    @Nullable
    private UUID entityUUID;
    private BiFunction<SuggestionsBuilder, Consumer<SuggestionsBuilder>, CompletableFuture<Suggestions>> suggestions = SUGGEST_NOTHING;
    private boolean hasNameEquals;
    private boolean hasNameNotEquals;
    private boolean isLimited;
    private boolean isSorted;
    private boolean hasGamemodeEquals;
    private boolean hasGamemodeNotEquals;
    private boolean hasTeamEquals;
    private boolean hasTeamNotEquals;
    @Nullable
    private EntityType<?> type;
    private boolean typeInverse;
    private boolean hasScores;
    private boolean hasAdvancements;
    private boolean usesSelectors;

    public EntitySelectorParser(StringReader stringReader) {
        this(stringReader, true);
    }

    public EntitySelectorParser(StringReader stringReader, boolean bl) {
        this.reader = stringReader;
        this.allowSelectors = bl;
    }

    public EntitySelector getSelector() {
        AABB aABB;
        if (this.deltaX != null || this.deltaY != null || this.deltaZ != null) {
            aABB = this.createAabb(this.deltaX == null ? 0.0 : this.deltaX, this.deltaY == null ? 0.0 : this.deltaY, this.deltaZ == null ? 0.0 : this.deltaZ);
        } else if (this.distance.getMax() != null) {
            float f = ((Float)this.distance.getMax()).floatValue();
            aABB = new AABB(-f, -f, -f, f + 1.0f, f + 1.0f, f + 1.0f);
        } else {
            aABB = null;
        }
        Function<Vec3, Vec3> function = this.x == null && this.y == null && this.z == null ? vec3 -> vec3 : vec3 -> new Vec3(this.x == null ? vec3.x : this.x, this.y == null ? vec3.y : this.y, this.z == null ? vec3.z : this.z);
        return new EntitySelector(this.maxResults, this.includesEntities, this.worldLimited, this.predicate, this.distance, function, aABB, this.order, this.currentEntity, this.playerName, this.entityUUID, this.type, this.usesSelectors);
    }

    private AABB createAabb(double d, double d2, double d3) {
        boolean bl = d < 0.0;
        boolean bl2 = d2 < 0.0;
        boolean bl3 = d3 < 0.0;
        double d4 = bl ? d : 0.0;
        double d5 = bl2 ? d2 : 0.0;
        double d6 = bl3 ? d3 : 0.0;
        double d7 = (bl ? 0.0 : d) + 1.0;
        double d8 = (bl2 ? 0.0 : d2) + 1.0;
        double d9 = (bl3 ? 0.0 : d3) + 1.0;
        return new AABB(d4, d5, d6, d7, d8, d9);
    }

    private void finalizePredicates() {
        if (this.rotX != WrappedMinMaxBounds.ANY) {
            this.predicate = this.predicate.and(this.createRotationPredicate(this.rotX, entity -> entity.xRot));
        }
        if (this.rotY != WrappedMinMaxBounds.ANY) {
            this.predicate = this.predicate.and(this.createRotationPredicate(this.rotY, entity -> entity.yRot));
        }
        if (!this.level.isAny()) {
            this.predicate = this.predicate.and(entity -> {
                if (!(entity instanceof ServerPlayer)) {
                    return false;
                }
                return this.level.matches(((ServerPlayer)entity).experienceLevel);
            });
        }
    }

    private Predicate<Entity> createRotationPredicate(WrappedMinMaxBounds wrappedMinMaxBounds, ToDoubleFunction<Entity> toDoubleFunction) {
        double d = Mth.wrapDegrees(wrappedMinMaxBounds.getMin() == null ? 0.0f : wrappedMinMaxBounds.getMin().floatValue());
        double d2 = Mth.wrapDegrees(wrappedMinMaxBounds.getMax() == null ? 359.0f : wrappedMinMaxBounds.getMax().floatValue());
        return entity -> {
            double d3 = Mth.wrapDegrees(toDoubleFunction.applyAsDouble((Entity)entity));
            if (d > d2) {
                return d3 >= d || d3 <= d2;
            }
            return d3 >= d && d3 <= d2;
        };
    }

    protected void parseSelector() throws CommandSyntaxException {
        this.usesSelectors = true;
        this.suggestions = (arg_0, arg_1) -> this.suggestSelector(arg_0, arg_1);
        if (!this.reader.canRead()) {
            throw ERROR_MISSING_SELECTOR_TYPE.createWithContext((ImmutableStringReader)this.reader);
        }
        int n = this.reader.getCursor();
        char c = this.reader.read();
        if (c == 'p') {
            this.maxResults = 1;
            this.includesEntities = false;
            this.order = ORDER_NEAREST;
            this.limitToType(EntityType.PLAYER);
        } else if (c == 'a') {
            this.maxResults = Integer.MAX_VALUE;
            this.includesEntities = false;
            this.order = ORDER_ARBITRARY;
            this.limitToType(EntityType.PLAYER);
        } else if (c == 'r') {
            this.maxResults = 1;
            this.includesEntities = false;
            this.order = ORDER_RANDOM;
            this.limitToType(EntityType.PLAYER);
        } else if (c == 's') {
            this.maxResults = 1;
            this.includesEntities = true;
            this.currentEntity = true;
        } else if (c == 'e') {
            this.maxResults = Integer.MAX_VALUE;
            this.includesEntities = true;
            this.order = ORDER_ARBITRARY;
            this.predicate = Entity::isAlive;
        } else {
            this.reader.setCursor(n);
            throw ERROR_UNKNOWN_SELECTOR_TYPE.createWithContext((ImmutableStringReader)this.reader, (Object)('@' + String.valueOf(c)));
        }
        this.suggestions = (arg_0, arg_1) -> this.suggestOpenOptions(arg_0, arg_1);
        if (this.reader.canRead() && this.reader.peek() == '[') {
            this.reader.skip();
            this.suggestions = (arg_0, arg_1) -> this.suggestOptionsKeyOrClose(arg_0, arg_1);
            this.parseOptions();
        }
    }

    protected void parseNameOrUUID() throws CommandSyntaxException {
        if (this.reader.canRead()) {
            this.suggestions = (arg_0, arg_1) -> this.suggestName(arg_0, arg_1);
        }
        int n = this.reader.getCursor();
        String string = this.reader.readString();
        try {
            this.entityUUID = UUID.fromString(string);
            this.includesEntities = true;
        }
        catch (IllegalArgumentException illegalArgumentException) {
            if (string.isEmpty() || string.length() > 16) {
                this.reader.setCursor(n);
                throw ERROR_INVALID_NAME_OR_UUID.createWithContext((ImmutableStringReader)this.reader);
            }
            this.includesEntities = false;
            this.playerName = string;
        }
        this.maxResults = 1;
    }

    protected void parseOptions() throws CommandSyntaxException {
        this.suggestions = (arg_0, arg_1) -> this.suggestOptionsKey(arg_0, arg_1);
        this.reader.skipWhitespace();
        while (this.reader.canRead() && this.reader.peek() != ']') {
            this.reader.skipWhitespace();
            int n = this.reader.getCursor();
            String string = this.reader.readString();
            EntitySelectorOptions.Modifier modifier = EntitySelectorOptions.get(this, string, n);
            this.reader.skipWhitespace();
            if (!this.reader.canRead() || this.reader.peek() != '=') {
                this.reader.setCursor(n);
                throw ERROR_EXPECTED_OPTION_VALUE.createWithContext((ImmutableStringReader)this.reader, (Object)string);
            }
            this.reader.skip();
            this.reader.skipWhitespace();
            this.suggestions = SUGGEST_NOTHING;
            modifier.handle(this);
            this.reader.skipWhitespace();
            this.suggestions = (arg_0, arg_1) -> this.suggestOptionsNextOrClose(arg_0, arg_1);
            if (!this.reader.canRead()) continue;
            if (this.reader.peek() == ',') {
                this.reader.skip();
                this.suggestions = (arg_0, arg_1) -> this.suggestOptionsKey(arg_0, arg_1);
                continue;
            }
            if (this.reader.peek() == ']') break;
            throw ERROR_EXPECTED_END_OF_OPTIONS.createWithContext((ImmutableStringReader)this.reader);
        }
        if (!this.reader.canRead()) {
            throw ERROR_EXPECTED_END_OF_OPTIONS.createWithContext((ImmutableStringReader)this.reader);
        }
        this.reader.skip();
        this.suggestions = SUGGEST_NOTHING;
    }

    public boolean shouldInvertValue() {
        this.reader.skipWhitespace();
        if (this.reader.canRead() && this.reader.peek() == '!') {
            this.reader.skip();
            this.reader.skipWhitespace();
            return true;
        }
        return false;
    }

    public boolean isTag() {
        this.reader.skipWhitespace();
        if (this.reader.canRead() && this.reader.peek() == '#') {
            this.reader.skip();
            this.reader.skipWhitespace();
            return true;
        }
        return false;
    }

    public StringReader getReader() {
        return this.reader;
    }

    public void addPredicate(Predicate<Entity> predicate) {
        this.predicate = this.predicate.and(predicate);
    }

    public void setWorldLimited() {
        this.worldLimited = true;
    }

    public MinMaxBounds.Floats getDistance() {
        return this.distance;
    }

    public void setDistance(MinMaxBounds.Floats floats) {
        this.distance = floats;
    }

    public MinMaxBounds.Ints getLevel() {
        return this.level;
    }

    public void setLevel(MinMaxBounds.Ints ints) {
        this.level = ints;
    }

    public WrappedMinMaxBounds getRotX() {
        return this.rotX;
    }

    public void setRotX(WrappedMinMaxBounds wrappedMinMaxBounds) {
        this.rotX = wrappedMinMaxBounds;
    }

    public WrappedMinMaxBounds getRotY() {
        return this.rotY;
    }

    public void setRotY(WrappedMinMaxBounds wrappedMinMaxBounds) {
        this.rotY = wrappedMinMaxBounds;
    }

    @Nullable
    public Double getX() {
        return this.x;
    }

    @Nullable
    public Double getY() {
        return this.y;
    }

    @Nullable
    public Double getZ() {
        return this.z;
    }

    public void setX(double d) {
        this.x = d;
    }

    public void setY(double d) {
        this.y = d;
    }

    public void setZ(double d) {
        this.z = d;
    }

    public void setDeltaX(double d) {
        this.deltaX = d;
    }

    public void setDeltaY(double d) {
        this.deltaY = d;
    }

    public void setDeltaZ(double d) {
        this.deltaZ = d;
    }

    @Nullable
    public Double getDeltaX() {
        return this.deltaX;
    }

    @Nullable
    public Double getDeltaY() {
        return this.deltaY;
    }

    @Nullable
    public Double getDeltaZ() {
        return this.deltaZ;
    }

    public void setMaxResults(int n) {
        this.maxResults = n;
    }

    public void setIncludesEntities(boolean bl) {
        this.includesEntities = bl;
    }

    public void setOrder(BiConsumer<Vec3, List<? extends Entity>> biConsumer) {
        this.order = biConsumer;
    }

    public EntitySelector parse() throws CommandSyntaxException {
        this.startPosition = this.reader.getCursor();
        this.suggestions = (arg_0, arg_1) -> this.suggestNameOrSelector(arg_0, arg_1);
        if (this.reader.canRead() && this.reader.peek() == '@') {
            if (!this.allowSelectors) {
                throw ERROR_SELECTORS_NOT_ALLOWED.createWithContext((ImmutableStringReader)this.reader);
            }
            this.reader.skip();
            this.parseSelector();
        } else {
            this.parseNameOrUUID();
        }
        this.finalizePredicates();
        return this.getSelector();
    }

    private static void fillSelectorSuggestions(SuggestionsBuilder suggestionsBuilder) {
        suggestionsBuilder.suggest("@p", (Message)new TranslatableComponent("argument.entity.selector.nearestPlayer"));
        suggestionsBuilder.suggest("@a", (Message)new TranslatableComponent("argument.entity.selector.allPlayers"));
        suggestionsBuilder.suggest("@r", (Message)new TranslatableComponent("argument.entity.selector.randomPlayer"));
        suggestionsBuilder.suggest("@s", (Message)new TranslatableComponent("argument.entity.selector.self"));
        suggestionsBuilder.suggest("@e", (Message)new TranslatableComponent("argument.entity.selector.allEntities"));
    }

    private CompletableFuture<Suggestions> suggestNameOrSelector(SuggestionsBuilder suggestionsBuilder, Consumer<SuggestionsBuilder> consumer) {
        consumer.accept(suggestionsBuilder);
        if (this.allowSelectors) {
            EntitySelectorParser.fillSelectorSuggestions(suggestionsBuilder);
        }
        return suggestionsBuilder.buildFuture();
    }

    private CompletableFuture<Suggestions> suggestName(SuggestionsBuilder suggestionsBuilder, Consumer<SuggestionsBuilder> consumer) {
        SuggestionsBuilder suggestionsBuilder2 = suggestionsBuilder.createOffset(this.startPosition);
        consumer.accept(suggestionsBuilder2);
        return suggestionsBuilder.add(suggestionsBuilder2).buildFuture();
    }

    private CompletableFuture<Suggestions> suggestSelector(SuggestionsBuilder suggestionsBuilder, Consumer<SuggestionsBuilder> consumer) {
        SuggestionsBuilder suggestionsBuilder2 = suggestionsBuilder.createOffset(suggestionsBuilder.getStart() - 1);
        EntitySelectorParser.fillSelectorSuggestions(suggestionsBuilder2);
        suggestionsBuilder.add(suggestionsBuilder2);
        return suggestionsBuilder.buildFuture();
    }

    private CompletableFuture<Suggestions> suggestOpenOptions(SuggestionsBuilder suggestionsBuilder, Consumer<SuggestionsBuilder> consumer) {
        suggestionsBuilder.suggest(String.valueOf('['));
        return suggestionsBuilder.buildFuture();
    }

    private CompletableFuture<Suggestions> suggestOptionsKeyOrClose(SuggestionsBuilder suggestionsBuilder, Consumer<SuggestionsBuilder> consumer) {
        suggestionsBuilder.suggest(String.valueOf(']'));
        EntitySelectorOptions.suggestNames(this, suggestionsBuilder);
        return suggestionsBuilder.buildFuture();
    }

    private CompletableFuture<Suggestions> suggestOptionsKey(SuggestionsBuilder suggestionsBuilder, Consumer<SuggestionsBuilder> consumer) {
        EntitySelectorOptions.suggestNames(this, suggestionsBuilder);
        return suggestionsBuilder.buildFuture();
    }

    private CompletableFuture<Suggestions> suggestOptionsNextOrClose(SuggestionsBuilder suggestionsBuilder, Consumer<SuggestionsBuilder> consumer) {
        suggestionsBuilder.suggest(String.valueOf(','));
        suggestionsBuilder.suggest(String.valueOf(']'));
        return suggestionsBuilder.buildFuture();
    }

    public boolean isCurrentEntity() {
        return this.currentEntity;
    }

    public void setSuggestions(BiFunction<SuggestionsBuilder, Consumer<SuggestionsBuilder>, CompletableFuture<Suggestions>> biFunction) {
        this.suggestions = biFunction;
    }

    public CompletableFuture<Suggestions> fillSuggestions(SuggestionsBuilder suggestionsBuilder, Consumer<SuggestionsBuilder> consumer) {
        return this.suggestions.apply(suggestionsBuilder.createOffset(this.reader.getCursor()), consumer);
    }

    public boolean hasNameEquals() {
        return this.hasNameEquals;
    }

    public void setHasNameEquals(boolean bl) {
        this.hasNameEquals = bl;
    }

    public boolean hasNameNotEquals() {
        return this.hasNameNotEquals;
    }

    public void setHasNameNotEquals(boolean bl) {
        this.hasNameNotEquals = bl;
    }

    public boolean isLimited() {
        return this.isLimited;
    }

    public void setLimited(boolean bl) {
        this.isLimited = bl;
    }

    public boolean isSorted() {
        return this.isSorted;
    }

    public void setSorted(boolean bl) {
        this.isSorted = bl;
    }

    public boolean hasGamemodeEquals() {
        return this.hasGamemodeEquals;
    }

    public void setHasGamemodeEquals(boolean bl) {
        this.hasGamemodeEquals = bl;
    }

    public boolean hasGamemodeNotEquals() {
        return this.hasGamemodeNotEquals;
    }

    public void setHasGamemodeNotEquals(boolean bl) {
        this.hasGamemodeNotEquals = bl;
    }

    public boolean hasTeamEquals() {
        return this.hasTeamEquals;
    }

    public void setHasTeamEquals(boolean bl) {
        this.hasTeamEquals = bl;
    }

    public void setHasTeamNotEquals(boolean bl) {
        this.hasTeamNotEquals = bl;
    }

    public void limitToType(EntityType<?> entityType) {
        this.type = entityType;
    }

    public void setTypeLimitedInversely() {
        this.typeInverse = true;
    }

    public boolean isTypeLimited() {
        return this.type != null;
    }

    public boolean isTypeLimitedInversely() {
        return this.typeInverse;
    }

    public boolean hasScores() {
        return this.hasScores;
    }

    public void setHasScores(boolean bl) {
        this.hasScores = bl;
    }

    public boolean hasAdvancements() {
        return this.hasAdvancements;
    }

    public void setHasAdvancements(boolean bl) {
        this.hasAdvancements = bl;
    }
}

