/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Strings
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Lists
 *  it.unimi.dsi.fastutil.bytes.ByteOpenHashSet
 *  it.unimi.dsi.fastutil.bytes.ByteSet
 */
package net.minecraft.nbt;

import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.bytes.ByteOpenHashSet;
import it.unimi.dsi.fastutil.bytes.ByteSet;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import net.minecraft.nbt.CollectionTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.ShortTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagType;
import net.minecraft.nbt.TagTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;

public class ListTag
extends CollectionTag<Tag> {
    public static final TagType<ListTag> TYPE = new TagType<ListTag>(){

        @Override
        public ListTag load(DataInput dataInput, int n, NbtAccounter nbtAccounter) throws IOException {
            nbtAccounter.accountBits(296L);
            if (n > 512) {
                throw new RuntimeException("Tried to read NBT tag with too high complexity, depth > 512");
            }
            byte by = dataInput.readByte();
            int n2 = dataInput.readInt();
            if (by == 0 && n2 > 0) {
                throw new RuntimeException("Missing type on ListTag");
            }
            nbtAccounter.accountBits(32L * (long)n2);
            TagType<?> tagType = TagTypes.getType(by);
            ArrayList arrayList = Lists.newArrayListWithCapacity((int)n2);
            for (int i = 0; i < n2; ++i) {
                arrayList.add(tagType.load(dataInput, n + 1, nbtAccounter));
            }
            return new ListTag(arrayList, by);
        }

        @Override
        public String getName() {
            return "LIST";
        }

        @Override
        public String getPrettyName() {
            return "TAG_List";
        }

        @Override
        public /* synthetic */ Tag load(DataInput dataInput, int n, NbtAccounter nbtAccounter) throws IOException {
            return this.load(dataInput, n, nbtAccounter);
        }
    };
    private static final ByteSet INLINE_ELEMENT_TYPES = new ByteOpenHashSet(Arrays.asList((byte)1, (byte)2, (byte)3, (byte)4, (byte)5, (byte)6));
    private final List<Tag> list;
    private byte type;

    private ListTag(List<Tag> list, byte by) {
        this.list = list;
        this.type = by;
    }

    public ListTag() {
        this(Lists.newArrayList(), 0);
    }

    @Override
    public void write(DataOutput dataOutput) throws IOException {
        this.type = this.list.isEmpty() ? (byte)0 : this.list.get(0).getId();
        dataOutput.writeByte(this.type);
        dataOutput.writeInt(this.list.size());
        for (Tag tag : this.list) {
            tag.write(dataOutput);
        }
    }

    @Override
    public byte getId() {
        return 9;
    }

    public TagType<ListTag> getType() {
        return TYPE;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("[");
        for (int i = 0; i < this.list.size(); ++i) {
            if (i != 0) {
                stringBuilder.append(',');
            }
            stringBuilder.append(this.list.get(i));
        }
        return stringBuilder.append(']').toString();
    }

    private void updateTypeAfterRemove() {
        if (this.list.isEmpty()) {
            this.type = 0;
        }
    }

    @Override
    public Tag remove(int n) {
        Tag tag = this.list.remove(n);
        this.updateTypeAfterRemove();
        return tag;
    }

    @Override
    public boolean isEmpty() {
        return this.list.isEmpty();
    }

    public CompoundTag getCompound(int n) {
        Tag tag;
        if (n >= 0 && n < this.list.size() && (tag = this.list.get(n)).getId() == 10) {
            return (CompoundTag)tag;
        }
        return new CompoundTag();
    }

    public ListTag getList(int n) {
        Tag tag;
        if (n >= 0 && n < this.list.size() && (tag = this.list.get(n)).getId() == 9) {
            return (ListTag)tag;
        }
        return new ListTag();
    }

    public short getShort(int n) {
        Tag tag;
        if (n >= 0 && n < this.list.size() && (tag = this.list.get(n)).getId() == 2) {
            return ((ShortTag)tag).getAsShort();
        }
        return 0;
    }

    public int getInt(int n) {
        Tag tag;
        if (n >= 0 && n < this.list.size() && (tag = this.list.get(n)).getId() == 3) {
            return ((IntTag)tag).getAsInt();
        }
        return 0;
    }

    public int[] getIntArray(int n) {
        Tag tag;
        if (n >= 0 && n < this.list.size() && (tag = this.list.get(n)).getId() == 11) {
            return ((IntArrayTag)tag).getAsIntArray();
        }
        return new int[0];
    }

    public double getDouble(int n) {
        Tag tag;
        if (n >= 0 && n < this.list.size() && (tag = this.list.get(n)).getId() == 6) {
            return ((DoubleTag)tag).getAsDouble();
        }
        return 0.0;
    }

    public float getFloat(int n) {
        Tag tag;
        if (n >= 0 && n < this.list.size() && (tag = this.list.get(n)).getId() == 5) {
            return ((FloatTag)tag).getAsFloat();
        }
        return 0.0f;
    }

    public String getString(int n) {
        if (n < 0 || n >= this.list.size()) {
            return "";
        }
        Tag tag = this.list.get(n);
        if (tag.getId() == 8) {
            return tag.getAsString();
        }
        return tag.toString();
    }

    @Override
    public int size() {
        return this.list.size();
    }

    @Override
    public Tag get(int n) {
        return this.list.get(n);
    }

    @Override
    public Tag set(int n, Tag tag) {
        Tag tag2 = this.get(n);
        if (!this.setTag(n, tag)) {
            throw new UnsupportedOperationException(String.format("Trying to add tag of type %d to list of %d", tag.getId(), this.type));
        }
        return tag2;
    }

    @Override
    public void add(int n, Tag tag) {
        if (!this.addTag(n, tag)) {
            throw new UnsupportedOperationException(String.format("Trying to add tag of type %d to list of %d", tag.getId(), this.type));
        }
    }

    @Override
    public boolean setTag(int n, Tag tag) {
        if (this.updateType(tag)) {
            this.list.set(n, tag);
            return true;
        }
        return false;
    }

    @Override
    public boolean addTag(int n, Tag tag) {
        if (this.updateType(tag)) {
            this.list.add(n, tag);
            return true;
        }
        return false;
    }

    private boolean updateType(Tag tag) {
        if (tag.getId() == 0) {
            return false;
        }
        if (this.type == 0) {
            this.type = tag.getId();
            return true;
        }
        return this.type == tag.getId();
    }

    @Override
    public ListTag copy() {
        List<Tag> list = TagTypes.getType(this.type).isValue() ? this.list : Iterables.transform(this.list, Tag::copy);
        ArrayList arrayList = Lists.newArrayList(list);
        return new ListTag(arrayList, this.type);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        return object instanceof ListTag && Objects.equals(this.list, ((ListTag)object).list);
    }

    @Override
    public int hashCode() {
        return this.list.hashCode();
    }

    @Override
    public Component getPrettyDisplay(String string, int n) {
        if (this.isEmpty()) {
            return new TextComponent("[]");
        }
        if (INLINE_ELEMENT_TYPES.contains(this.type) && this.size() <= 8) {
            String string2 = ", ";
            TextComponent textComponent = new TextComponent("[");
            for (int i = 0; i < this.list.size(); ++i) {
                if (i != 0) {
                    textComponent.append(", ");
                }
                textComponent.append(this.list.get(i).getPrettyDisplay());
            }
            textComponent.append("]");
            return textComponent;
        }
        TextComponent textComponent = new TextComponent("[");
        if (!string.isEmpty()) {
            textComponent.append("\n");
        }
        String string3 = String.valueOf(',');
        for (int i = 0; i < this.list.size(); ++i) {
            TextComponent textComponent2 = new TextComponent(Strings.repeat((String)string, (int)(n + 1)));
            textComponent2.append(this.list.get(i).getPrettyDisplay(string, n + 1));
            if (i != this.list.size() - 1) {
                textComponent2.append(string3).append(string.isEmpty() ? " " : "\n");
            }
            textComponent.append(textComponent2);
        }
        if (!string.isEmpty()) {
            textComponent.append("\n").append(Strings.repeat((String)string, (int)n));
        }
        textComponent.append("]");
        return textComponent;
    }

    @Override
    public byte getElementType() {
        return this.type;
    }

    @Override
    public void clear() {
        this.list.clear();
        this.type = 0;
    }

    @Override
    public /* synthetic */ Tag copy() {
        return this.copy();
    }

    @Override
    public /* synthetic */ Object remove(int n) {
        return this.remove(n);
    }

    @Override
    public /* synthetic */ void add(int n, Object object) {
        this.add(n, (Tag)object);
    }

    @Override
    public /* synthetic */ Object set(int n, Object object) {
        return this.set(n, (Tag)object);
    }

    @Override
    public /* synthetic */ Object get(int n) {
        return this.get(n);
    }

}

