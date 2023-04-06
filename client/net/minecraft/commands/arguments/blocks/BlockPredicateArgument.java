/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.DynamicCommandExceptionType
 *  com.mojang.brigadier.suggestion.Suggestions
 *  com.mojang.brigadier.suggestion.SuggestionsBuilder
 *  javax.annotation.Nullable
 */
package net.minecraft.commands.arguments.blocks;

import com.mojang.brigadier.Message;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.blocks.BlockStateParser;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagCollection;
import net.minecraft.tags.TagContainer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.block.state.properties.Property;

public class BlockPredicateArgument
implements ArgumentType<Result> {
    private static final Collection<String> EXAMPLES = Arrays.asList("stone", "minecraft:stone", "stone[foo=bar]", "#stone", "#stone[foo=bar]{baz=nbt}");
    private static final DynamicCommandExceptionType ERROR_UNKNOWN_TAG = new DynamicCommandExceptionType(object -> new TranslatableComponent("arguments.block.tag.unknown", object));

    public static BlockPredicateArgument blockPredicate() {
        return new BlockPredicateArgument();
    }

    public Result parse(StringReader stringReader) throws CommandSyntaxException {
        BlockStateParser blockStateParser = new BlockStateParser(stringReader, true).parse(true);
        if (blockStateParser.getState() != null) {
            BlockPredicate blockPredicate = new BlockPredicate(blockStateParser.getState(), blockStateParser.getProperties().keySet(), blockStateParser.getNbt());
            return tagContainer -> blockPredicate;
        }
        ResourceLocation resourceLocation = blockStateParser.getTag();
        return tagContainer -> {
            Tag<Block> tag = tagContainer.getBlocks().getTag(resourceLocation);
            if (tag == null) {
                throw ERROR_UNKNOWN_TAG.create((Object)resourceLocation.toString());
            }
            return new TagPredicate(tag, blockStateParser.getVagueProperties(), blockStateParser.getNbt());
        };
    }

    public static Predicate<BlockInWorld> getBlockPredicate(CommandContext<CommandSourceStack> commandContext, String string) throws CommandSyntaxException {
        return ((Result)commandContext.getArgument(string, Result.class)).create(((CommandSourceStack)commandContext.getSource()).getServer().getTags());
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandContext, SuggestionsBuilder suggestionsBuilder) {
        StringReader stringReader = new StringReader(suggestionsBuilder.getInput());
        stringReader.setCursor(suggestionsBuilder.getStart());
        BlockStateParser blockStateParser = new BlockStateParser(stringReader, true);
        try {
            blockStateParser.parse(true);
        }
        catch (CommandSyntaxException commandSyntaxException) {
            // empty catch block
        }
        return blockStateParser.fillSuggestions(suggestionsBuilder, BlockTags.getAllTags());
    }

    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    public /* synthetic */ Object parse(StringReader stringReader) throws CommandSyntaxException {
        return this.parse(stringReader);
    }

    static class TagPredicate
    implements Predicate<BlockInWorld> {
        private final Tag<Block> tag;
        @Nullable
        private final CompoundTag nbt;
        private final Map<String, String> vagueProperties;

        private TagPredicate(Tag<Block> tag, Map<String, String> map, @Nullable CompoundTag compoundTag) {
            this.tag = tag;
            this.vagueProperties = map;
            this.nbt = compoundTag;
        }

        @Override
        public boolean test(BlockInWorld blockInWorld) {
            BlockState blockState = blockInWorld.getState();
            if (!blockState.is(this.tag)) {
                return false;
            }
            for (Map.Entry<String, String> entry : this.vagueProperties.entrySet()) {
                Property<?> property = blockState.getBlock().getStateDefinition().getProperty(entry.getKey());
                if (property == null) {
                    return false;
                }
                Comparable comparable = property.getValue(entry.getValue()).orElse(null);
                if (comparable == null) {
                    return false;
                }
                if (blockState.getValue(property) == comparable) continue;
                return false;
            }
            if (this.nbt != null) {
                BlockEntity blockEntity = blockInWorld.getEntity();
                return blockEntity != null && NbtUtils.compareNbt(this.nbt, blockEntity.save(new CompoundTag()), true);
            }
            return true;
        }

        @Override
        public /* synthetic */ boolean test(Object object) {
            return this.test((BlockInWorld)object);
        }
    }

    static class BlockPredicate
    implements Predicate<BlockInWorld> {
        private final BlockState state;
        private final Set<Property<?>> properties;
        @Nullable
        private final CompoundTag nbt;

        public BlockPredicate(BlockState blockState, Set<Property<?>> set, @Nullable CompoundTag compoundTag) {
            this.state = blockState;
            this.properties = set;
            this.nbt = compoundTag;
        }

        @Override
        public boolean test(BlockInWorld blockInWorld) {
            BlockState blockState = blockInWorld.getState();
            if (!blockState.is(this.state.getBlock())) {
                return false;
            }
            for (Property<?> property : this.properties) {
                if (blockState.getValue(property) == this.state.getValue(property)) continue;
                return false;
            }
            if (this.nbt != null) {
                BlockEntity blockEntity = blockInWorld.getEntity();
                return blockEntity != null && NbtUtils.compareNbt(this.nbt, blockEntity.save(new CompoundTag()), true);
            }
            return true;
        }

        @Override
        public /* synthetic */ boolean test(Object object) {
            return this.test((BlockInWorld)object);
        }
    }

    public static interface Result {
        public Predicate<BlockInWorld> create(TagContainer var1) throws CommandSyntaxException;
    }

}

