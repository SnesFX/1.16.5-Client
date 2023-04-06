/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonDeserializer
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonParseException
 *  com.google.gson.JsonPrimitive
 *  com.google.gson.JsonSerializationContext
 *  com.google.gson.JsonSerializer
 *  com.mojang.brigadier.ImmutableStringReader
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.SimpleCommandExceptionType
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.codecs.PrimitiveCodec
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.StringUtils
 */
package net.minecraft.resources;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.PrimitiveCodec;
import java.lang.reflect.Type;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.ResourceLocationException;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.GsonHelper;
import org.apache.commons.lang3.StringUtils;

public class ResourceLocation
implements Comparable<ResourceLocation> {
    public static final Codec<ResourceLocation> CODEC = Codec.STRING.comapFlatMap(ResourceLocation::read, ResourceLocation::toString).stable();
    private static final SimpleCommandExceptionType ERROR_INVALID = new SimpleCommandExceptionType((Message)new TranslatableComponent("argument.id.invalid"));
    protected final String namespace;
    protected final String path;

    protected ResourceLocation(String[] arrstring) {
        this.namespace = StringUtils.isEmpty((CharSequence)arrstring[0]) ? "minecraft" : arrstring[0];
        this.path = arrstring[1];
        if (!ResourceLocation.isValidNamespace(this.namespace)) {
            throw new ResourceLocationException("Non [a-z0-9_.-] character in namespace of location: " + this.namespace + ':' + this.path);
        }
        if (!ResourceLocation.isValidPath(this.path)) {
            throw new ResourceLocationException("Non [a-z0-9/._-] character in path of location: " + this.namespace + ':' + this.path);
        }
    }

    public ResourceLocation(String string) {
        this(ResourceLocation.decompose(string, ':'));
    }

    public ResourceLocation(String string, String string2) {
        this(new String[]{string, string2});
    }

    public static ResourceLocation of(String string, char c) {
        return new ResourceLocation(ResourceLocation.decompose(string, c));
    }

    @Nullable
    public static ResourceLocation tryParse(String string) {
        try {
            return new ResourceLocation(string);
        }
        catch (ResourceLocationException resourceLocationException) {
            return null;
        }
    }

    protected static String[] decompose(String string, char c) {
        String[] arrstring = new String[]{"minecraft", string};
        int n = string.indexOf(c);
        if (n >= 0) {
            arrstring[1] = string.substring(n + 1, string.length());
            if (n >= 1) {
                arrstring[0] = string.substring(0, n);
            }
        }
        return arrstring;
    }

    private static DataResult<ResourceLocation> read(String string) {
        try {
            return DataResult.success((Object)new ResourceLocation(string));
        }
        catch (ResourceLocationException resourceLocationException) {
            return DataResult.error((String)("Not a valid resource location: " + string + " " + resourceLocationException.getMessage()));
        }
    }

    public String getPath() {
        return this.path;
    }

    public String getNamespace() {
        return this.namespace;
    }

    public String toString() {
        return this.namespace + ':' + this.path;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object instanceof ResourceLocation) {
            ResourceLocation resourceLocation = (ResourceLocation)object;
            return this.namespace.equals(resourceLocation.namespace) && this.path.equals(resourceLocation.path);
        }
        return false;
    }

    public int hashCode() {
        return 31 * this.namespace.hashCode() + this.path.hashCode();
    }

    @Override
    public int compareTo(ResourceLocation resourceLocation) {
        int n = this.path.compareTo(resourceLocation.path);
        if (n == 0) {
            n = this.namespace.compareTo(resourceLocation.namespace);
        }
        return n;
    }

    public static ResourceLocation read(StringReader stringReader) throws CommandSyntaxException {
        int n = stringReader.getCursor();
        while (stringReader.canRead() && ResourceLocation.isAllowedInResourceLocation(stringReader.peek())) {
            stringReader.skip();
        }
        String string = stringReader.getString().substring(n, stringReader.getCursor());
        try {
            return new ResourceLocation(string);
        }
        catch (ResourceLocationException resourceLocationException) {
            stringReader.setCursor(n);
            throw ERROR_INVALID.createWithContext((ImmutableStringReader)stringReader);
        }
    }

    public static boolean isAllowedInResourceLocation(char c) {
        return c >= '0' && c <= '9' || c >= 'a' && c <= 'z' || c == '_' || c == ':' || c == '/' || c == '.' || c == '-';
    }

    private static boolean isValidPath(String string) {
        for (int i = 0; i < string.length(); ++i) {
            if (ResourceLocation.validPathChar(string.charAt(i))) continue;
            return false;
        }
        return true;
    }

    private static boolean isValidNamespace(String string) {
        for (int i = 0; i < string.length(); ++i) {
            if (ResourceLocation.validNamespaceChar(string.charAt(i))) continue;
            return false;
        }
        return true;
    }

    public static boolean validPathChar(char c) {
        return c == '_' || c == '-' || c >= 'a' && c <= 'z' || c >= '0' && c <= '9' || c == '/' || c == '.';
    }

    private static boolean validNamespaceChar(char c) {
        return c == '_' || c == '-' || c >= 'a' && c <= 'z' || c >= '0' && c <= '9' || c == '.';
    }

    public static boolean isValidResourceLocation(String string) {
        String[] arrstring = ResourceLocation.decompose(string, ':');
        return ResourceLocation.isValidNamespace(StringUtils.isEmpty((CharSequence)arrstring[0]) ? "minecraft" : arrstring[0]) && ResourceLocation.isValidPath(arrstring[1]);
    }

    @Override
    public /* synthetic */ int compareTo(Object object) {
        return this.compareTo((ResourceLocation)object);
    }

    public static class Serializer
    implements JsonDeserializer<ResourceLocation>,
    JsonSerializer<ResourceLocation> {
        public ResourceLocation deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            return new ResourceLocation(GsonHelper.convertToString(jsonElement, "location"));
        }

        public JsonElement serialize(ResourceLocation resourceLocation, Type type, JsonSerializationContext jsonSerializationContext) {
            return new JsonPrimitive(resourceLocation.toString());
        }

        public /* synthetic */ JsonElement serialize(Object object, Type type, JsonSerializationContext jsonSerializationContext) {
            return this.serialize((ResourceLocation)object, type, jsonSerializationContext);
        }

        public /* synthetic */ Object deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            return this.deserialize(jsonElement, type, jsonDeserializationContext);
        }
    }

}

