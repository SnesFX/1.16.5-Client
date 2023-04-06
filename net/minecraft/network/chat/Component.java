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
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  com.google.gson.JsonPrimitive
 *  com.google.gson.JsonSerializationContext
 *  com.google.gson.JsonSerializer
 *  com.google.gson.TypeAdapter
 *  com.google.gson.TypeAdapterFactory
 *  com.google.gson.stream.JsonReader
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.StringReader
 *  javax.annotation.Nullable
 */
package net.minecraft.network.chat;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.stream.JsonReader;
import com.mojang.brigadier.Message;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.KeybindComponent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.NbtComponent;
import net.minecraft.network.chat.ScoreComponent;
import net.minecraft.network.chat.SelectorComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.LowerCaseEnumTypeAdapterFactory;

public interface Component
extends Message,
FormattedText {
    public Style getStyle();

    public String getContents();

    @Override
    default public String getString() {
        return FormattedText.super.getString();
    }

    default public String getString(int n) {
        StringBuilder stringBuilder = new StringBuilder();
        this.visit(string -> {
            int n2 = n - stringBuilder.length();
            if (n2 <= 0) {
                return STOP_ITERATION;
            }
            stringBuilder.append(string.length() <= n2 ? string : string.substring(0, n2));
            return Optional.empty();
        });
        return stringBuilder.toString();
    }

    public List<Component> getSiblings();

    public MutableComponent plainCopy();

    public MutableComponent copy();

    public FormattedCharSequence getVisualOrderText();

    @Override
    default public <T> Optional<T> visit(FormattedText.StyledContentConsumer<T> styledContentConsumer, Style style) {
        Style style2 = this.getStyle().applyTo(style);
        Optional<T> optional = this.visitSelf(styledContentConsumer, style2);
        if (optional.isPresent()) {
            return optional;
        }
        for (Component component : this.getSiblings()) {
            Optional<T> optional2 = component.visit(styledContentConsumer, style2);
            if (!optional2.isPresent()) continue;
            return optional2;
        }
        return Optional.empty();
    }

    @Override
    default public <T> Optional<T> visit(FormattedText.ContentConsumer<T> contentConsumer) {
        Optional<T> optional = this.visitSelf(contentConsumer);
        if (optional.isPresent()) {
            return optional;
        }
        for (Component component : this.getSiblings()) {
            Optional<T> optional2 = component.visit(contentConsumer);
            if (!optional2.isPresent()) continue;
            return optional2;
        }
        return Optional.empty();
    }

    default public <T> Optional<T> visitSelf(FormattedText.StyledContentConsumer<T> styledContentConsumer, Style style) {
        return styledContentConsumer.accept(style, this.getContents());
    }

    default public <T> Optional<T> visitSelf(FormattedText.ContentConsumer<T> contentConsumer) {
        return contentConsumer.accept(this.getContents());
    }

    public static Component nullToEmpty(@Nullable String string) {
        return string != null ? new TextComponent(string) : TextComponent.EMPTY;
    }

    public static class Serializer
    implements JsonDeserializer<MutableComponent>,
    JsonSerializer<Component> {
        private static final Gson GSON = Util.make(() -> {
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.disableHtmlEscaping();
            gsonBuilder.registerTypeHierarchyAdapter(Component.class, (Object)new Serializer());
            gsonBuilder.registerTypeHierarchyAdapter(Style.class, (Object)new Style.Serializer());
            gsonBuilder.registerTypeAdapterFactory((TypeAdapterFactory)new LowerCaseEnumTypeAdapterFactory());
            return gsonBuilder.create();
        });
        private static final Field JSON_READER_POS = Util.make(() -> {
            try {
                new JsonReader((Reader)new StringReader(""));
                Field field = JsonReader.class.getDeclaredField("pos");
                field.setAccessible(true);
                return field;
            }
            catch (NoSuchFieldException noSuchFieldException) {
                throw new IllegalStateException("Couldn't get field 'pos' for JsonReader", noSuchFieldException);
            }
        });
        private static final Field JSON_READER_LINESTART = Util.make(() -> {
            try {
                new JsonReader((Reader)new StringReader(""));
                Field field = JsonReader.class.getDeclaredField("lineStart");
                field.setAccessible(true);
                return field;
            }
            catch (NoSuchFieldException noSuchFieldException) {
                throw new IllegalStateException("Couldn't get field 'lineStart' for JsonReader", noSuchFieldException);
            }
        });

        /*
         * WARNING - void declaration
         * Enabled force condition propagation
         * Lifted jumps to return sites
         */
        public MutableComponent deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            void var5_19;
            if (jsonElement.isJsonPrimitive()) {
                return new TextComponent(jsonElement.getAsString());
            }
            if (jsonElement.isJsonObject()) {
                void var5_17;
                String string;
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                if (jsonObject.has("text")) {
                    TextComponent textComponent = new TextComponent(GsonHelper.getAsString(jsonObject, "text"));
                } else if (jsonObject.has("translate")) {
                    string = GsonHelper.getAsString(jsonObject, "translate");
                    if (jsonObject.has("with")) {
                        JsonArray jsonArray = GsonHelper.getAsJsonArray(jsonObject, "with");
                        Object[] arrobject = new Object[jsonArray.size()];
                        for (int i = 0; i < arrobject.length; ++i) {
                            TextComponent textComponent;
                            arrobject[i] = this.deserialize(jsonArray.get(i), type, jsonDeserializationContext);
                            if (!(arrobject[i] instanceof TextComponent) || !(textComponent = (TextComponent)arrobject[i]).getStyle().isEmpty() || !textComponent.getSiblings().isEmpty()) continue;
                            arrobject[i] = textComponent.getText();
                        }
                        TranslatableComponent translatableComponent = new TranslatableComponent(string, arrobject);
                    } else {
                        TranslatableComponent translatableComponent = new TranslatableComponent(string);
                    }
                } else if (jsonObject.has("score")) {
                    string = GsonHelper.getAsJsonObject(jsonObject, "score");
                    if (!string.has("name") || !string.has("objective")) throw new JsonParseException("A score component needs a least a name and an objective");
                    ScoreComponent scoreComponent = new ScoreComponent(GsonHelper.getAsString((JsonObject)string, "name"), GsonHelper.getAsString((JsonObject)string, "objective"));
                } else if (jsonObject.has("selector")) {
                    SelectorComponent selectorComponent = new SelectorComponent(GsonHelper.getAsString(jsonObject, "selector"));
                } else if (jsonObject.has("keybind")) {
                    KeybindComponent keybindComponent = new KeybindComponent(GsonHelper.getAsString(jsonObject, "keybind"));
                } else {
                    if (!jsonObject.has("nbt")) throw new JsonParseException("Don't know how to turn " + (Object)jsonElement + " into a Component");
                    string = GsonHelper.getAsString(jsonObject, "nbt");
                    boolean bl = GsonHelper.getAsBoolean(jsonObject, "interpret", false);
                    if (jsonObject.has("block")) {
                        NbtComponent.BlockNbtComponent blockNbtComponent = new NbtComponent.BlockNbtComponent(string, bl, GsonHelper.getAsString(jsonObject, "block"));
                    } else if (jsonObject.has("entity")) {
                        NbtComponent.EntityNbtComponent entityNbtComponent = new NbtComponent.EntityNbtComponent(string, bl, GsonHelper.getAsString(jsonObject, "entity"));
                    } else {
                        if (!jsonObject.has("storage")) throw new JsonParseException("Don't know how to turn " + (Object)jsonElement + " into a Component");
                        NbtComponent.StorageNbtComponent storageNbtComponent = new NbtComponent.StorageNbtComponent(string, bl, new ResourceLocation(GsonHelper.getAsString(jsonObject, "storage")));
                    }
                }
                if (jsonObject.has("extra")) {
                    string = GsonHelper.getAsJsonArray(jsonObject, "extra");
                    if (string.size() <= 0) throw new JsonParseException("Unexpected empty array of components");
                    for (int i = 0; i < string.size(); ++i) {
                        var5_17.append(this.deserialize(string.get(i), type, jsonDeserializationContext));
                    }
                }
                var5_17.setStyle((Style)jsonDeserializationContext.deserialize(jsonElement, Style.class));
                return var5_17;
            }
            if (!jsonElement.isJsonArray()) throw new JsonParseException("Don't know how to turn " + (Object)jsonElement + " into a Component");
            JsonArray jsonArray = jsonElement.getAsJsonArray();
            Object var5_18 = null;
            for (JsonElement jsonElement2 : jsonArray) {
                MutableComponent mutableComponent = this.deserialize(jsonElement2, jsonElement2.getClass(), jsonDeserializationContext);
                if (var5_19 == null) {
                    MutableComponent mutableComponent2 = mutableComponent;
                    continue;
                }
                var5_19.append(mutableComponent);
            }
            return var5_19;
        }

        private void serializeStyle(Style style, JsonObject jsonObject, JsonSerializationContext jsonSerializationContext) {
            JsonElement jsonElement = jsonSerializationContext.serialize((Object)style);
            if (jsonElement.isJsonObject()) {
                JsonObject jsonObject2 = (JsonObject)jsonElement;
                for (Map.Entry entry : jsonObject2.entrySet()) {
                    jsonObject.add((String)entry.getKey(), (JsonElement)entry.getValue());
                }
            }
        }

        /*
         * Enabled force condition propagation
         * Lifted jumps to return sites
         */
        public JsonElement serialize(Component component, Type type, JsonSerializationContext jsonSerializationContext) {
            Object object;
            Object object2;
            JsonObject jsonObject = new JsonObject();
            if (!component.getStyle().isEmpty()) {
                this.serializeStyle(component.getStyle(), jsonObject, jsonSerializationContext);
            }
            if (!component.getSiblings().isEmpty()) {
                object2 = new JsonArray();
                for (Component component2 : component.getSiblings()) {
                    object2.add(this.serialize(component2, component2.getClass(), jsonSerializationContext));
                }
                jsonObject.add("extra", (JsonElement)object2);
            }
            if (component instanceof TextComponent) {
                jsonObject.addProperty("text", ((TextComponent)component).getText());
                return jsonObject;
            } else if (component instanceof TranslatableComponent) {
                object2 = (TranslatableComponent)component;
                jsonObject.addProperty("translate", ((TranslatableComponent)object2).getKey());
                if (((TranslatableComponent)object2).getArgs() == null || ((TranslatableComponent)object2).getArgs().length <= 0) return jsonObject;
                object = new JsonArray();
                for (Object object3 : ((TranslatableComponent)object2).getArgs()) {
                    if (object3 instanceof Component) {
                        object.add(this.serialize((Component)object3, object3.getClass(), jsonSerializationContext));
                        continue;
                    }
                    object.add((JsonElement)new JsonPrimitive(String.valueOf(object3)));
                }
                jsonObject.add("with", (JsonElement)object);
                return jsonObject;
            } else if (component instanceof ScoreComponent) {
                object2 = (ScoreComponent)component;
                object = new JsonObject();
                object.addProperty("name", ((ScoreComponent)object2).getName());
                object.addProperty("objective", ((ScoreComponent)object2).getObjective());
                jsonObject.add("score", (JsonElement)object);
                return jsonObject;
            } else if (component instanceof SelectorComponent) {
                object2 = (SelectorComponent)component;
                jsonObject.addProperty("selector", ((SelectorComponent)object2).getPattern());
                return jsonObject;
            } else if (component instanceof KeybindComponent) {
                object2 = (KeybindComponent)component;
                jsonObject.addProperty("keybind", ((KeybindComponent)object2).getName());
                return jsonObject;
            } else {
                if (!(component instanceof NbtComponent)) throw new IllegalArgumentException("Don't know how to serialize " + component + " as a Component");
                object2 = (NbtComponent)component;
                jsonObject.addProperty("nbt", ((NbtComponent)object2).getNbtPath());
                jsonObject.addProperty("interpret", Boolean.valueOf(((NbtComponent)object2).isInterpreting()));
                if (component instanceof NbtComponent.BlockNbtComponent) {
                    object = (NbtComponent.BlockNbtComponent)component;
                    jsonObject.addProperty("block", ((NbtComponent.BlockNbtComponent)object).getPos());
                    return jsonObject;
                } else if (component instanceof NbtComponent.EntityNbtComponent) {
                    object = (NbtComponent.EntityNbtComponent)component;
                    jsonObject.addProperty("entity", ((NbtComponent.EntityNbtComponent)object).getSelector());
                    return jsonObject;
                } else {
                    if (!(component instanceof NbtComponent.StorageNbtComponent)) throw new IllegalArgumentException("Don't know how to serialize " + component + " as a Component");
                    object = (NbtComponent.StorageNbtComponent)component;
                    jsonObject.addProperty("storage", ((NbtComponent.StorageNbtComponent)object).getId().toString());
                }
            }
            return jsonObject;
        }

        public static String toJson(Component component) {
            return GSON.toJson((Object)component);
        }

        public static JsonElement toJsonTree(Component component) {
            return GSON.toJsonTree((Object)component);
        }

        @Nullable
        public static MutableComponent fromJson(String string) {
            return GsonHelper.fromJson(GSON, string, MutableComponent.class, false);
        }

        @Nullable
        public static MutableComponent fromJson(JsonElement jsonElement) {
            return (MutableComponent)GSON.fromJson(jsonElement, MutableComponent.class);
        }

        @Nullable
        public static MutableComponent fromJsonLenient(String string) {
            return GsonHelper.fromJson(GSON, string, MutableComponent.class, true);
        }

        public static MutableComponent fromJson(com.mojang.brigadier.StringReader stringReader) {
            try {
                JsonReader jsonReader = new JsonReader((Reader)new StringReader(stringReader.getRemaining()));
                jsonReader.setLenient(false);
                MutableComponent mutableComponent = (MutableComponent)GSON.getAdapter(MutableComponent.class).read(jsonReader);
                stringReader.setCursor(stringReader.getCursor() + Serializer.getPos(jsonReader));
                return mutableComponent;
            }
            catch (IOException | StackOverflowError throwable) {
                throw new JsonParseException(throwable);
            }
        }

        private static int getPos(JsonReader jsonReader) {
            try {
                return JSON_READER_POS.getInt((Object)jsonReader) - JSON_READER_LINESTART.getInt((Object)jsonReader) + 1;
            }
            catch (IllegalAccessException illegalAccessException) {
                throw new IllegalStateException("Couldn't read position of JsonReader", illegalAccessException);
            }
        }

        public /* synthetic */ JsonElement serialize(Object object, Type type, JsonSerializationContext jsonSerializationContext) {
            return this.serialize((Component)object, type, jsonSerializationContext);
        }

        public /* synthetic */ Object deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            return this.deserialize(jsonElement, type, jsonDeserializationContext);
        }
    }

}

