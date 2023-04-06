/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonDeserializer
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonParseException
 *  com.mojang.datafixers.DSL
 *  com.mojang.datafixers.DSL$TypeReference
 *  com.mojang.datafixers.OpticFinder
 *  com.mojang.datafixers.Typed
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.OptionalDynamic
 *  org.apache.commons.lang3.StringUtils
 */
package net.minecraft.util.datafix.fixes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.OptionalDynamic;
import java.lang.reflect.Type;
import java.util.function.Function;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.datafix.fixes.NamedEntityFix;
import net.minecraft.util.datafix.fixes.References;
import org.apache.commons.lang3.StringUtils;

public class BlockEntitySignTextStrictJsonFix
extends NamedEntityFix {
    public static final Gson GSON = new GsonBuilder().registerTypeAdapter(Component.class, (Object)new JsonDeserializer<Component>(){

        public MutableComponent deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            if (jsonElement.isJsonPrimitive()) {
                return new TextComponent(jsonElement.getAsString());
            }
            if (jsonElement.isJsonArray()) {
                JsonArray jsonArray = jsonElement.getAsJsonArray();
                MutableComponent mutableComponent = null;
                for (JsonElement jsonElement2 : jsonArray) {
                    MutableComponent mutableComponent2 = this.deserialize(jsonElement2, jsonElement2.getClass(), jsonDeserializationContext);
                    if (mutableComponent == null) {
                        mutableComponent = mutableComponent2;
                        continue;
                    }
                    mutableComponent.append(mutableComponent2);
                }
                return mutableComponent;
            }
            throw new JsonParseException("Don't know how to turn " + (Object)jsonElement + " into a Component");
        }

        public /* synthetic */ Object deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            return this.deserialize(jsonElement, type, jsonDeserializationContext);
        }
    }).create();

    public BlockEntitySignTextStrictJsonFix(Schema schema, boolean bl) {
        super(schema, bl, "BlockEntitySignTextStrictJsonFix", References.BLOCK_ENTITY, "Sign");
    }

    private Dynamic<?> updateLine(Dynamic<?> dynamic, String string) {
        String string2 = dynamic.get(string).asString("");
        Component component = null;
        if ("null".equals(string2) || StringUtils.isEmpty((CharSequence)string2)) {
            component = TextComponent.EMPTY;
        } else if (string2.charAt(0) == '\"' && string2.charAt(string2.length() - 1) == '\"' || string2.charAt(0) == '{' && string2.charAt(string2.length() - 1) == '}') {
            try {
                component = GsonHelper.fromJson(GSON, string2, Component.class, true);
                if (component == null) {
                    component = TextComponent.EMPTY;
                }
            }
            catch (JsonParseException jsonParseException) {
                // empty catch block
            }
            if (component == null) {
                try {
                    component = Component.Serializer.fromJson(string2);
                }
                catch (JsonParseException jsonParseException) {
                    // empty catch block
                }
            }
            if (component == null) {
                try {
                    component = Component.Serializer.fromJsonLenient(string2);
                }
                catch (JsonParseException jsonParseException) {
                    // empty catch block
                }
            }
            if (component == null) {
                component = new TextComponent(string2);
            }
        } else {
            component = new TextComponent(string2);
        }
        return dynamic.set(string, dynamic.createString(Component.Serializer.toJson(component)));
    }

    @Override
    protected Typed<?> fix(Typed<?> typed) {
        return typed.update(DSL.remainderFinder(), dynamic -> {
            dynamic = this.updateLine((Dynamic<?>)dynamic, "Text1");
            dynamic = this.updateLine((Dynamic<?>)dynamic, "Text2");
            dynamic = this.updateLine((Dynamic<?>)dynamic, "Text3");
            dynamic = this.updateLine((Dynamic<?>)dynamic, "Text4");
            return dynamic;
        });
    }

}

