/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.DSL
 *  com.mojang.datafixers.DSL$TypeReference
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.datafixers.types.Type
 *  com.mojang.datafixers.types.templates.Const
 *  com.mojang.datafixers.types.templates.Const$PrimitiveType
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.codecs.PrimitiveCodec
 */
package net.minecraft.util.datafix.schemas;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.Const;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.PrimitiveCodec;
import java.util.function.Function;
import net.minecraft.resources.ResourceLocation;

public class NamespacedSchema
extends Schema {
    public static final PrimitiveCodec<String> NAMESPACED_STRING_CODEC = new PrimitiveCodec<String>(){

        public <T> DataResult<String> read(DynamicOps<T> dynamicOps, T t) {
            return dynamicOps.getStringValue(t).map(NamespacedSchema::ensureNamespaced);
        }

        public <T> T write(DynamicOps<T> dynamicOps, String string) {
            return (T)dynamicOps.createString(string);
        }

        public String toString() {
            return "NamespacedString";
        }

        public /* synthetic */ Object write(DynamicOps dynamicOps, Object object) {
            return this.write(dynamicOps, (String)object);
        }
    };
    private static final Type<String> NAMESPACED_STRING = new Const.PrimitiveType(NAMESPACED_STRING_CODEC);

    public NamespacedSchema(int n, Schema schema) {
        super(n, schema);
    }

    public static String ensureNamespaced(String string) {
        ResourceLocation resourceLocation = ResourceLocation.tryParse(string);
        if (resourceLocation != null) {
            return resourceLocation.toString();
        }
        return string;
    }

    public static Type<String> namespacedString() {
        return NAMESPACED_STRING;
    }

    public Type<?> getChoiceType(DSL.TypeReference typeReference, String string) {
        return super.getChoiceType(typeReference, NamespacedSchema.ensureNamespaced(string));
    }

}

