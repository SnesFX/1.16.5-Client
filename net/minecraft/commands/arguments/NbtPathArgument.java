/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.brigadier.ImmutableStringReader
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.DynamicCommandExceptionType
 *  com.mojang.brigadier.exceptions.SimpleCommandExceptionType
 *  it.unimi.dsi.fastutil.objects.Object2IntMap
 *  it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
 *  org.apache.commons.lang3.mutable.MutableBoolean
 */
package net.minecraft.commands.arguments;

import com.google.common.collect.Lists;
import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.nbt.CollectionTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.chat.TranslatableComponent;
import org.apache.commons.lang3.mutable.MutableBoolean;

public class NbtPathArgument
implements ArgumentType<NbtPath> {
    private static final Collection<String> EXAMPLES = Arrays.asList("foo", "foo.bar", "foo[0]", "[0]", "[]", "{foo=bar}");
    public static final SimpleCommandExceptionType ERROR_INVALID_NODE = new SimpleCommandExceptionType((Message)new TranslatableComponent("arguments.nbtpath.node.invalid"));
    public static final DynamicCommandExceptionType ERROR_NOTHING_FOUND = new DynamicCommandExceptionType(object -> new TranslatableComponent("arguments.nbtpath.nothing_found", object));

    public static NbtPathArgument nbtPath() {
        return new NbtPathArgument();
    }

    public static NbtPath getPath(CommandContext<CommandSourceStack> commandContext, String string) {
        return (NbtPath)commandContext.getArgument(string, NbtPath.class);
    }

    public NbtPath parse(StringReader stringReader) throws CommandSyntaxException {
        ArrayList arrayList = Lists.newArrayList();
        int n = stringReader.getCursor();
        Object2IntOpenHashMap object2IntOpenHashMap = new Object2IntOpenHashMap();
        boolean bl = true;
        while (stringReader.canRead() && stringReader.peek() != ' ') {
            char c;
            Node node = NbtPathArgument.parseNode(stringReader, bl);
            arrayList.add(node);
            object2IntOpenHashMap.put((Object)node, stringReader.getCursor() - n);
            bl = false;
            if (!stringReader.canRead() || (c = stringReader.peek()) == ' ' || c == '[' || c == '{') continue;
            stringReader.expect('.');
        }
        return new NbtPath(stringReader.getString().substring(n, stringReader.getCursor()), arrayList.toArray(new Node[0]), (Object2IntMap<Node>)object2IntOpenHashMap);
    }

    private static Node parseNode(StringReader stringReader, boolean bl) throws CommandSyntaxException {
        switch (stringReader.peek()) {
            case '{': {
                if (!bl) {
                    throw ERROR_INVALID_NODE.createWithContext((ImmutableStringReader)stringReader);
                }
                CompoundTag compoundTag = new TagParser(stringReader).readStruct();
                return new MatchRootObjectNode(compoundTag);
            }
            case '[': {
                stringReader.skip();
                char c = stringReader.peek();
                if (c == '{') {
                    CompoundTag compoundTag = new TagParser(stringReader).readStruct();
                    stringReader.expect(']');
                    return new MatchElementNode(compoundTag);
                }
                if (c == ']') {
                    stringReader.skip();
                    return AllElementsNode.INSTANCE;
                }
                int n = stringReader.readInt();
                stringReader.expect(']');
                return new IndexedElementNode(n);
            }
            case '\"': {
                String string = stringReader.readString();
                return NbtPathArgument.readObjectNode(stringReader, string);
            }
        }
        String string = NbtPathArgument.readUnquotedName(stringReader);
        return NbtPathArgument.readObjectNode(stringReader, string);
    }

    private static Node readObjectNode(StringReader stringReader, String string) throws CommandSyntaxException {
        if (stringReader.canRead() && stringReader.peek() == '{') {
            CompoundTag compoundTag = new TagParser(stringReader).readStruct();
            return new MatchObjectNode(string, compoundTag);
        }
        return new CompoundChildNode(string);
    }

    private static String readUnquotedName(StringReader stringReader) throws CommandSyntaxException {
        int n = stringReader.getCursor();
        while (stringReader.canRead() && NbtPathArgument.isAllowedInUnquotedName(stringReader.peek())) {
            stringReader.skip();
        }
        if (stringReader.getCursor() == n) {
            throw ERROR_INVALID_NODE.createWithContext((ImmutableStringReader)stringReader);
        }
        return stringReader.getString().substring(n, stringReader.getCursor());
    }

    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    private static boolean isAllowedInUnquotedName(char c) {
        return c != ' ' && c != '\"' && c != '[' && c != ']' && c != '.' && c != '{' && c != '}';
    }

    private static Predicate<Tag> createTagPredicate(CompoundTag compoundTag) {
        return tag -> NbtUtils.compareNbt(compoundTag, tag, true);
    }

    public /* synthetic */ Object parse(StringReader stringReader) throws CommandSyntaxException {
        return this.parse(stringReader);
    }

    static class MatchRootObjectNode
    implements Node {
        private final Predicate<Tag> predicate;

        public MatchRootObjectNode(CompoundTag compoundTag) {
            this.predicate = NbtPathArgument.createTagPredicate(compoundTag);
        }

        @Override
        public void getTag(Tag tag, List<Tag> list) {
            if (tag instanceof CompoundTag && this.predicate.test(tag)) {
                list.add(tag);
            }
        }

        @Override
        public void getOrCreateTag(Tag tag, Supplier<Tag> supplier, List<Tag> list) {
            this.getTag(tag, list);
        }

        @Override
        public Tag createPreferredParentTag() {
            return new CompoundTag();
        }

        @Override
        public int setTag(Tag tag, Supplier<Tag> supplier) {
            return 0;
        }

        @Override
        public int removeTag(Tag tag) {
            return 0;
        }
    }

    static class MatchObjectNode
    implements Node {
        private final String name;
        private final CompoundTag pattern;
        private final Predicate<Tag> predicate;

        public MatchObjectNode(String string, CompoundTag compoundTag) {
            this.name = string;
            this.pattern = compoundTag;
            this.predicate = NbtPathArgument.createTagPredicate(compoundTag);
        }

        @Override
        public void getTag(Tag tag, List<Tag> list) {
            Tag tag2;
            if (tag instanceof CompoundTag && this.predicate.test(tag2 = ((CompoundTag)tag).get(this.name))) {
                list.add(tag2);
            }
        }

        @Override
        public void getOrCreateTag(Tag tag, Supplier<Tag> supplier, List<Tag> list) {
            if (tag instanceof CompoundTag) {
                CompoundTag compoundTag = (CompoundTag)tag;
                Tag tag2 = compoundTag.get(this.name);
                if (tag2 == null) {
                    tag2 = this.pattern.copy();
                    compoundTag.put(this.name, tag2);
                    list.add(tag2);
                } else if (this.predicate.test(tag2)) {
                    list.add(tag2);
                }
            }
        }

        @Override
        public Tag createPreferredParentTag() {
            return new CompoundTag();
        }

        @Override
        public int setTag(Tag tag, Supplier<Tag> supplier) {
            Tag tag2;
            CompoundTag compoundTag;
            Tag tag3;
            if (tag instanceof CompoundTag && this.predicate.test(tag3 = (compoundTag = (CompoundTag)tag).get(this.name)) && !(tag2 = supplier.get()).equals(tag3)) {
                compoundTag.put(this.name, tag2);
                return 1;
            }
            return 0;
        }

        @Override
        public int removeTag(Tag tag) {
            Tag tag2;
            CompoundTag compoundTag;
            if (tag instanceof CompoundTag && this.predicate.test(tag2 = (compoundTag = (CompoundTag)tag).get(this.name))) {
                compoundTag.remove(this.name);
                return 1;
            }
            return 0;
        }
    }

    static class AllElementsNode
    implements Node {
        public static final AllElementsNode INSTANCE = new AllElementsNode();

        private AllElementsNode() {
        }

        @Override
        public void getTag(Tag tag, List<Tag> list) {
            if (tag instanceof CollectionTag) {
                list.addAll((CollectionTag)tag);
            }
        }

        @Override
        public void getOrCreateTag(Tag tag, Supplier<Tag> supplier, List<Tag> list) {
            if (tag instanceof CollectionTag) {
                CollectionTag collectionTag = (CollectionTag)tag;
                if (collectionTag.isEmpty()) {
                    Tag tag2 = supplier.get();
                    if (collectionTag.addTag(0, tag2)) {
                        list.add(tag2);
                    }
                } else {
                    list.addAll(collectionTag);
                }
            }
        }

        @Override
        public Tag createPreferredParentTag() {
            return new ListTag();
        }

        @Override
        public int setTag(Tag tag, Supplier<Tag> supplier) {
            if (tag instanceof CollectionTag) {
                CollectionTag collectionTag = (CollectionTag)tag;
                int n = collectionTag.size();
                if (n == 0) {
                    collectionTag.addTag(0, supplier.get());
                    return 1;
                }
                Tag tag2 = supplier.get();
                int n2 = n - (int)collectionTag.stream().filter(tag2::equals).count();
                if (n2 == 0) {
                    return 0;
                }
                collectionTag.clear();
                if (!collectionTag.addTag(0, tag2)) {
                    return 0;
                }
                for (int i = 1; i < n; ++i) {
                    collectionTag.addTag(i, supplier.get());
                }
                return n2;
            }
            return 0;
        }

        @Override
        public int removeTag(Tag tag) {
            CollectionTag collectionTag;
            int n;
            if (tag instanceof CollectionTag && (n = (collectionTag = (CollectionTag)tag).size()) > 0) {
                collectionTag.clear();
                return n;
            }
            return 0;
        }
    }

    static class MatchElementNode
    implements Node {
        private final CompoundTag pattern;
        private final Predicate<Tag> predicate;

        public MatchElementNode(CompoundTag compoundTag) {
            this.pattern = compoundTag;
            this.predicate = NbtPathArgument.createTagPredicate(compoundTag);
        }

        @Override
        public void getTag(Tag tag, List<Tag> list) {
            if (tag instanceof ListTag) {
                ListTag listTag = (ListTag)tag;
                listTag.stream().filter(this.predicate).forEach(list::add);
            }
        }

        @Override
        public void getOrCreateTag(Tag tag2, Supplier<Tag> supplier, List<Tag> list) {
            MutableBoolean mutableBoolean = new MutableBoolean();
            if (tag2 instanceof ListTag) {
                ListTag listTag = (ListTag)tag2;
                listTag.stream().filter(this.predicate).forEach(tag -> {
                    list.add((Tag)tag);
                    mutableBoolean.setTrue();
                });
                if (mutableBoolean.isFalse()) {
                    CompoundTag compoundTag = this.pattern.copy();
                    listTag.add(compoundTag);
                    list.add(compoundTag);
                }
            }
        }

        @Override
        public Tag createPreferredParentTag() {
            return new ListTag();
        }

        @Override
        public int setTag(Tag tag, Supplier<Tag> supplier) {
            int n = 0;
            if (tag instanceof ListTag) {
                ListTag listTag = (ListTag)tag;
                int n2 = listTag.size();
                if (n2 == 0) {
                    listTag.add(supplier.get());
                    ++n;
                } else {
                    for (int i = 0; i < n2; ++i) {
                        Tag tag2;
                        Tag tag3 = listTag.get(i);
                        if (!this.predicate.test(tag3) || (tag2 = supplier.get()).equals(tag3) || !listTag.setTag(i, tag2)) continue;
                        ++n;
                    }
                }
            }
            return n;
        }

        @Override
        public int removeTag(Tag tag) {
            int n = 0;
            if (tag instanceof ListTag) {
                ListTag listTag = (ListTag)tag;
                for (int i = listTag.size() - 1; i >= 0; --i) {
                    if (!this.predicate.test(listTag.get(i))) continue;
                    listTag.remove(i);
                    ++n;
                }
            }
            return n;
        }
    }

    static class IndexedElementNode
    implements Node {
        private final int index;

        public IndexedElementNode(int n) {
            this.index = n;
        }

        @Override
        public void getTag(Tag tag, List<Tag> list) {
            if (tag instanceof CollectionTag) {
                int n;
                CollectionTag collectionTag = (CollectionTag)tag;
                int n2 = collectionTag.size();
                int n3 = n = this.index < 0 ? n2 + this.index : this.index;
                if (0 <= n && n < n2) {
                    list.add((Tag)collectionTag.get(n));
                }
            }
        }

        @Override
        public void getOrCreateTag(Tag tag, Supplier<Tag> supplier, List<Tag> list) {
            this.getTag(tag, list);
        }

        @Override
        public Tag createPreferredParentTag() {
            return new ListTag();
        }

        @Override
        public int setTag(Tag tag, Supplier<Tag> supplier) {
            if (tag instanceof CollectionTag) {
                int n;
                CollectionTag collectionTag = (CollectionTag)tag;
                int n2 = collectionTag.size();
                int n3 = n = this.index < 0 ? n2 + this.index : this.index;
                if (0 <= n && n < n2) {
                    Tag tag2 = (Tag)collectionTag.get(n);
                    Tag tag3 = supplier.get();
                    if (!tag3.equals(tag2) && collectionTag.setTag(n, tag3)) {
                        return 1;
                    }
                }
            }
            return 0;
        }

        @Override
        public int removeTag(Tag tag) {
            if (tag instanceof CollectionTag) {
                int n;
                CollectionTag collectionTag = (CollectionTag)tag;
                int n2 = collectionTag.size();
                int n3 = n = this.index < 0 ? n2 + this.index : this.index;
                if (0 <= n && n < n2) {
                    collectionTag.remove(n);
                    return 1;
                }
            }
            return 0;
        }
    }

    static class CompoundChildNode
    implements Node {
        private final String name;

        public CompoundChildNode(String string) {
            this.name = string;
        }

        @Override
        public void getTag(Tag tag, List<Tag> list) {
            Tag tag2;
            if (tag instanceof CompoundTag && (tag2 = ((CompoundTag)tag).get(this.name)) != null) {
                list.add(tag2);
            }
        }

        @Override
        public void getOrCreateTag(Tag tag, Supplier<Tag> supplier, List<Tag> list) {
            if (tag instanceof CompoundTag) {
                Tag tag2;
                CompoundTag compoundTag = (CompoundTag)tag;
                if (compoundTag.contains(this.name)) {
                    tag2 = compoundTag.get(this.name);
                } else {
                    tag2 = supplier.get();
                    compoundTag.put(this.name, tag2);
                }
                list.add(tag2);
            }
        }

        @Override
        public Tag createPreferredParentTag() {
            return new CompoundTag();
        }

        @Override
        public int setTag(Tag tag, Supplier<Tag> supplier) {
            if (tag instanceof CompoundTag) {
                Tag tag2;
                CompoundTag compoundTag = (CompoundTag)tag;
                Tag tag3 = supplier.get();
                if (!tag3.equals(tag2 = compoundTag.put(this.name, tag3))) {
                    return 1;
                }
            }
            return 0;
        }

        @Override
        public int removeTag(Tag tag) {
            CompoundTag compoundTag;
            if (tag instanceof CompoundTag && (compoundTag = (CompoundTag)tag).contains(this.name)) {
                compoundTag.remove(this.name);
                return 1;
            }
            return 0;
        }
    }

    static interface Node {
        public void getTag(Tag var1, List<Tag> var2);

        public void getOrCreateTag(Tag var1, Supplier<Tag> var2, List<Tag> var3);

        public Tag createPreferredParentTag();

        public int setTag(Tag var1, Supplier<Tag> var2);

        public int removeTag(Tag var1);

        default public List<Tag> get(List<Tag> list) {
            return this.collect(list, (arg_0, arg_1) -> this.getTag(arg_0, arg_1));
        }

        default public List<Tag> getOrCreate(List<Tag> list2, Supplier<Tag> supplier) {
            return this.collect(list2, (tag, list) -> this.getOrCreateTag((Tag)tag, supplier, (List<Tag>)list));
        }

        default public List<Tag> collect(List<Tag> list, BiConsumer<Tag, List<Tag>> biConsumer) {
            ArrayList arrayList = Lists.newArrayList();
            for (Tag tag : list) {
                biConsumer.accept(tag, arrayList);
            }
            return arrayList;
        }
    }

    public static class NbtPath {
        private final String original;
        private final Object2IntMap<Node> nodeToOriginalPosition;
        private final Node[] nodes;

        public NbtPath(String string, Node[] arrnode, Object2IntMap<Node> object2IntMap) {
            this.original = string;
            this.nodes = arrnode;
            this.nodeToOriginalPosition = object2IntMap;
        }

        public List<Tag> get(Tag tag) throws CommandSyntaxException {
            List<Tag> list = Collections.singletonList(tag);
            for (Node node : this.nodes) {
                if (!(list = node.get(list)).isEmpty()) continue;
                throw this.createNotFoundException(node);
            }
            return list;
        }

        public int countMatching(Tag tag) {
            List<Tag> list = Collections.singletonList(tag);
            for (Node node : this.nodes) {
                if (!(list = node.get(list)).isEmpty()) continue;
                return 0;
            }
            return list.size();
        }

        private List<Tag> getOrCreateParents(Tag tag) throws CommandSyntaxException {
            List<Tag> list = Collections.singletonList(tag);
            for (int i = 0; i < this.nodes.length - 1; ++i) {
                Node node = this.nodes[i];
                int n = i + 1;
                if (!(list = node.getOrCreate(list, this.nodes[n]::createPreferredParentTag)).isEmpty()) continue;
                throw this.createNotFoundException(node);
            }
            return list;
        }

        public List<Tag> getOrCreate(Tag tag, Supplier<Tag> supplier) throws CommandSyntaxException {
            List<Tag> list = this.getOrCreateParents(tag);
            Node node = this.nodes[this.nodes.length - 1];
            return node.getOrCreate(list, supplier);
        }

        private static int apply(List<Tag> list, Function<Tag, Integer> function) {
            return list.stream().map(function).reduce(0, (n, n2) -> n + n2);
        }

        public int set(Tag tag2, Supplier<Tag> supplier) throws CommandSyntaxException {
            List<Tag> list = this.getOrCreateParents(tag2);
            Node node = this.nodes[this.nodes.length - 1];
            return NbtPath.apply(list, tag -> node.setTag((Tag)tag, supplier));
        }

        public int remove(Tag tag) {
            List<Tag> list = Collections.singletonList(tag);
            for (int i = 0; i < this.nodes.length - 1; ++i) {
                list = this.nodes[i].get(list);
            }
            Node node = this.nodes[this.nodes.length - 1];
            return NbtPath.apply(list, node::removeTag);
        }

        private CommandSyntaxException createNotFoundException(Node node) {
            int n = this.nodeToOriginalPosition.getInt((Object)node);
            return ERROR_NOTHING_FOUND.create((Object)this.original.substring(0, n));
        }

        public String toString() {
            return this.original;
        }
    }

}

