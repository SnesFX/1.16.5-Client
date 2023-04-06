/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.nbt;

import java.io.DataInput;
import java.io.IOException;
import net.minecraft.nbt.EndTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.Tag;

public interface TagType<T extends Tag> {
    public T load(DataInput var1, int var2, NbtAccounter var3) throws IOException;

    default public boolean isValue() {
        return false;
    }

    public String getName();

    public String getPrettyName();

    public static TagType<EndTag> createInvalid(final int n) {
        return new TagType<EndTag>(){

            @Override
            public EndTag load(DataInput dataInput, int n2, NbtAccounter nbtAccounter) throws IOException {
                throw new IllegalArgumentException("Invalid tag id: " + n);
            }

            @Override
            public String getName() {
                return "INVALID[" + n + "]";
            }

            @Override
            public String getPrettyName() {
                return "UNKNOWN_" + n;
            }

            @Override
            public /* synthetic */ Tag load(DataInput dataInput, int n2, NbtAccounter nbtAccounter) throws IOException {
                return this.load(dataInput, n2, nbtAccounter);
            }
        };
    }

}

