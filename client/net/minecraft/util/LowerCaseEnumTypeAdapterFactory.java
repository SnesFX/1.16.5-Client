/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.google.gson.Gson
 *  com.google.gson.TypeAdapter
 *  com.google.gson.TypeAdapterFactory
 *  com.google.gson.reflect.TypeToken
 *  com.google.gson.stream.JsonReader
 *  com.google.gson.stream.JsonToken
 *  com.google.gson.stream.JsonWriter
 *  javax.annotation.Nullable
 */
package net.minecraft.util;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import javax.annotation.Nullable;

public class LowerCaseEnumTypeAdapterFactory
implements TypeAdapterFactory {
    @Nullable
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
        Class class_ = typeToken.getRawType();
        if (!class_.isEnum()) {
            return null;
        }
        final HashMap hashMap = Maps.newHashMap();
        for (Object t : class_.getEnumConstants()) {
            hashMap.put(this.toLowercase(t), t);
        }
        return new TypeAdapter<T>(){

            public void write(JsonWriter jsonWriter, T t) throws IOException {
                if (t == null) {
                    jsonWriter.nullValue();
                } else {
                    jsonWriter.value(LowerCaseEnumTypeAdapterFactory.this.toLowercase(t));
                }
            }

            @Nullable
            public T read(JsonReader jsonReader) throws IOException {
                if (jsonReader.peek() == JsonToken.NULL) {
                    jsonReader.nextNull();
                    return null;
                }
                return (T)hashMap.get(jsonReader.nextString());
            }
        };
    }

    private String toLowercase(Object object) {
        if (object instanceof Enum) {
            return ((Enum)object).name().toLowerCase(Locale.ROOT);
        }
        return object.toString().toLowerCase(Locale.ROOT);
    }

}

