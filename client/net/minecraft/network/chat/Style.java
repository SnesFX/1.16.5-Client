/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonDeserializer
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  com.google.gson.JsonSerializationContext
 *  com.google.gson.JsonSerializer
 *  com.google.gson.JsonSyntaxException
 *  javax.annotation.Nullable
 */
package net.minecraft.network.chat;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonSyntaxException;
import java.lang.reflect.Type;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.ResourceLocationException;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

public class Style {
    public static final Style EMPTY = new Style(null, null, null, null, null, null, null, null, null, null);
    public static final ResourceLocation DEFAULT_FONT = new ResourceLocation("minecraft", "default");
    @Nullable
    private final TextColor color;
    @Nullable
    private final Boolean bold;
    @Nullable
    private final Boolean italic;
    @Nullable
    private final Boolean underlined;
    @Nullable
    private final Boolean strikethrough;
    @Nullable
    private final Boolean obfuscated;
    @Nullable
    private final ClickEvent clickEvent;
    @Nullable
    private final HoverEvent hoverEvent;
    @Nullable
    private final String insertion;
    @Nullable
    private final ResourceLocation font;

    private Style(@Nullable TextColor textColor, @Nullable Boolean bl, @Nullable Boolean bl2, @Nullable Boolean bl3, @Nullable Boolean bl4, @Nullable Boolean bl5, @Nullable ClickEvent clickEvent, @Nullable HoverEvent hoverEvent, @Nullable String string, @Nullable ResourceLocation resourceLocation) {
        this.color = textColor;
        this.bold = bl;
        this.italic = bl2;
        this.underlined = bl3;
        this.strikethrough = bl4;
        this.obfuscated = bl5;
        this.clickEvent = clickEvent;
        this.hoverEvent = hoverEvent;
        this.insertion = string;
        this.font = resourceLocation;
    }

    @Nullable
    public TextColor getColor() {
        return this.color;
    }

    public boolean isBold() {
        return this.bold == Boolean.TRUE;
    }

    public boolean isItalic() {
        return this.italic == Boolean.TRUE;
    }

    public boolean isStrikethrough() {
        return this.strikethrough == Boolean.TRUE;
    }

    public boolean isUnderlined() {
        return this.underlined == Boolean.TRUE;
    }

    public boolean isObfuscated() {
        return this.obfuscated == Boolean.TRUE;
    }

    public boolean isEmpty() {
        return this == EMPTY;
    }

    @Nullable
    public ClickEvent getClickEvent() {
        return this.clickEvent;
    }

    @Nullable
    public HoverEvent getHoverEvent() {
        return this.hoverEvent;
    }

    @Nullable
    public String getInsertion() {
        return this.insertion;
    }

    public ResourceLocation getFont() {
        return this.font != null ? this.font : DEFAULT_FONT;
    }

    public Style withColor(@Nullable TextColor textColor) {
        return new Style(textColor, this.bold, this.italic, this.underlined, this.strikethrough, this.obfuscated, this.clickEvent, this.hoverEvent, this.insertion, this.font);
    }

    public Style withColor(@Nullable ChatFormatting chatFormatting) {
        return this.withColor(chatFormatting != null ? TextColor.fromLegacyFormat(chatFormatting) : null);
    }

    public Style withBold(@Nullable Boolean bl) {
        return new Style(this.color, bl, this.italic, this.underlined, this.strikethrough, this.obfuscated, this.clickEvent, this.hoverEvent, this.insertion, this.font);
    }

    public Style withItalic(@Nullable Boolean bl) {
        return new Style(this.color, this.bold, bl, this.underlined, this.strikethrough, this.obfuscated, this.clickEvent, this.hoverEvent, this.insertion, this.font);
    }

    public Style withUnderlined(@Nullable Boolean bl) {
        return new Style(this.color, this.bold, this.italic, bl, this.strikethrough, this.obfuscated, this.clickEvent, this.hoverEvent, this.insertion, this.font);
    }

    public Style withClickEvent(@Nullable ClickEvent clickEvent) {
        return new Style(this.color, this.bold, this.italic, this.underlined, this.strikethrough, this.obfuscated, clickEvent, this.hoverEvent, this.insertion, this.font);
    }

    public Style withHoverEvent(@Nullable HoverEvent hoverEvent) {
        return new Style(this.color, this.bold, this.italic, this.underlined, this.strikethrough, this.obfuscated, this.clickEvent, hoverEvent, this.insertion, this.font);
    }

    public Style withInsertion(@Nullable String string) {
        return new Style(this.color, this.bold, this.italic, this.underlined, this.strikethrough, this.obfuscated, this.clickEvent, this.hoverEvent, string, this.font);
    }

    public Style withFont(@Nullable ResourceLocation resourceLocation) {
        return new Style(this.color, this.bold, this.italic, this.underlined, this.strikethrough, this.obfuscated, this.clickEvent, this.hoverEvent, this.insertion, resourceLocation);
    }

    public Style applyFormat(ChatFormatting chatFormatting) {
        TextColor textColor = this.color;
        Boolean bl = this.bold;
        Boolean bl2 = this.italic;
        Boolean bl3 = this.strikethrough;
        Boolean bl4 = this.underlined;
        Boolean bl5 = this.obfuscated;
        switch (chatFormatting) {
            case OBFUSCATED: {
                bl5 = true;
                break;
            }
            case BOLD: {
                bl = true;
                break;
            }
            case STRIKETHROUGH: {
                bl3 = true;
                break;
            }
            case UNDERLINE: {
                bl4 = true;
                break;
            }
            case ITALIC: {
                bl2 = true;
                break;
            }
            case RESET: {
                return EMPTY;
            }
            default: {
                textColor = TextColor.fromLegacyFormat(chatFormatting);
            }
        }
        return new Style(textColor, bl, bl2, bl4, bl3, bl5, this.clickEvent, this.hoverEvent, this.insertion, this.font);
    }

    public Style applyLegacyFormat(ChatFormatting chatFormatting) {
        TextColor textColor = this.color;
        Boolean bl = this.bold;
        Boolean bl2 = this.italic;
        Boolean bl3 = this.strikethrough;
        Boolean bl4 = this.underlined;
        Boolean bl5 = this.obfuscated;
        switch (chatFormatting) {
            case OBFUSCATED: {
                bl5 = true;
                break;
            }
            case BOLD: {
                bl = true;
                break;
            }
            case STRIKETHROUGH: {
                bl3 = true;
                break;
            }
            case UNDERLINE: {
                bl4 = true;
                break;
            }
            case ITALIC: {
                bl2 = true;
                break;
            }
            case RESET: {
                return EMPTY;
            }
            default: {
                bl5 = false;
                bl = false;
                bl3 = false;
                bl4 = false;
                bl2 = false;
                textColor = TextColor.fromLegacyFormat(chatFormatting);
            }
        }
        return new Style(textColor, bl, bl2, bl4, bl3, bl5, this.clickEvent, this.hoverEvent, this.insertion, this.font);
    }

    public Style applyFormats(ChatFormatting ... arrchatFormatting) {
        TextColor textColor = this.color;
        Boolean bl = this.bold;
        Boolean bl2 = this.italic;
        Boolean bl3 = this.strikethrough;
        Boolean bl4 = this.underlined;
        Boolean bl5 = this.obfuscated;
        block8 : for (ChatFormatting chatFormatting : arrchatFormatting) {
            switch (chatFormatting) {
                case OBFUSCATED: {
                    bl5 = true;
                    continue block8;
                }
                case BOLD: {
                    bl = true;
                    continue block8;
                }
                case STRIKETHROUGH: {
                    bl3 = true;
                    continue block8;
                }
                case UNDERLINE: {
                    bl4 = true;
                    continue block8;
                }
                case ITALIC: {
                    bl2 = true;
                    continue block8;
                }
                case RESET: {
                    return EMPTY;
                }
                default: {
                    textColor = TextColor.fromLegacyFormat(chatFormatting);
                }
            }
        }
        return new Style(textColor, bl, bl2, bl4, bl3, bl5, this.clickEvent, this.hoverEvent, this.insertion, this.font);
    }

    public Style applyTo(Style style) {
        if (this == EMPTY) {
            return style;
        }
        if (style == EMPTY) {
            return this;
        }
        return new Style(this.color != null ? this.color : style.color, this.bold != null ? this.bold : style.bold, this.italic != null ? this.italic : style.italic, this.underlined != null ? this.underlined : style.underlined, this.strikethrough != null ? this.strikethrough : style.strikethrough, this.obfuscated != null ? this.obfuscated : style.obfuscated, this.clickEvent != null ? this.clickEvent : style.clickEvent, this.hoverEvent != null ? this.hoverEvent : style.hoverEvent, this.insertion != null ? this.insertion : style.insertion, this.font != null ? this.font : style.font);
    }

    public String toString() {
        return "Style{ color=" + this.color + ", bold=" + this.bold + ", italic=" + this.italic + ", underlined=" + this.underlined + ", strikethrough=" + this.strikethrough + ", obfuscated=" + this.obfuscated + ", clickEvent=" + this.getClickEvent() + ", hoverEvent=" + this.getHoverEvent() + ", insertion=" + this.getInsertion() + ", font=" + this.getFont() + '}';
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object instanceof Style) {
            Style style = (Style)object;
            return this.isBold() == style.isBold() && Objects.equals(this.getColor(), style.getColor()) && this.isItalic() == style.isItalic() && this.isObfuscated() == style.isObfuscated() && this.isStrikethrough() == style.isStrikethrough() && this.isUnderlined() == style.isUnderlined() && Objects.equals(this.getClickEvent(), style.getClickEvent()) && Objects.equals(this.getHoverEvent(), style.getHoverEvent()) && Objects.equals(this.getInsertion(), style.getInsertion()) && Objects.equals(this.getFont(), style.getFont());
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(this.color, this.bold, this.italic, this.underlined, this.strikethrough, this.obfuscated, this.clickEvent, this.hoverEvent, this.insertion);
    }

    public static class Serializer
    implements JsonDeserializer<Style>,
    JsonSerializer<Style> {
        @Nullable
        public Style deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            if (jsonElement.isJsonObject()) {
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                if (jsonObject == null) {
                    return null;
                }
                Boolean bl = Serializer.getOptionalFlag(jsonObject, "bold");
                Boolean bl2 = Serializer.getOptionalFlag(jsonObject, "italic");
                Boolean bl3 = Serializer.getOptionalFlag(jsonObject, "underlined");
                Boolean bl4 = Serializer.getOptionalFlag(jsonObject, "strikethrough");
                Boolean bl5 = Serializer.getOptionalFlag(jsonObject, "obfuscated");
                TextColor textColor = Serializer.getTextColor(jsonObject);
                String string = Serializer.getInsertion(jsonObject);
                ClickEvent clickEvent = Serializer.getClickEvent(jsonObject);
                HoverEvent hoverEvent = Serializer.getHoverEvent(jsonObject);
                ResourceLocation resourceLocation = Serializer.getFont(jsonObject);
                return new Style(textColor, bl, bl2, bl3, bl4, bl5, clickEvent, hoverEvent, string, resourceLocation);
            }
            return null;
        }

        @Nullable
        private static ResourceLocation getFont(JsonObject jsonObject) {
            if (jsonObject.has("font")) {
                String string = GsonHelper.getAsString(jsonObject, "font");
                try {
                    return new ResourceLocation(string);
                }
                catch (ResourceLocationException resourceLocationException) {
                    throw new JsonSyntaxException("Invalid font name: " + string);
                }
            }
            return null;
        }

        @Nullable
        private static HoverEvent getHoverEvent(JsonObject jsonObject) {
            HoverEvent hoverEvent;
            JsonObject jsonObject2;
            if (jsonObject.has("hoverEvent") && (hoverEvent = HoverEvent.deserialize(jsonObject2 = GsonHelper.getAsJsonObject(jsonObject, "hoverEvent"))) != null && hoverEvent.getAction().isAllowedFromServer()) {
                return hoverEvent;
            }
            return null;
        }

        @Nullable
        private static ClickEvent getClickEvent(JsonObject jsonObject) {
            if (jsonObject.has("clickEvent")) {
                JsonObject jsonObject2 = GsonHelper.getAsJsonObject(jsonObject, "clickEvent");
                String string = GsonHelper.getAsString(jsonObject2, "action", null);
                ClickEvent.Action action = string == null ? null : ClickEvent.Action.getByName(string);
                String string2 = GsonHelper.getAsString(jsonObject2, "value", null);
                if (action != null && string2 != null && action.isAllowedFromServer()) {
                    return new ClickEvent(action, string2);
                }
            }
            return null;
        }

        @Nullable
        private static String getInsertion(JsonObject jsonObject) {
            return GsonHelper.getAsString(jsonObject, "insertion", null);
        }

        @Nullable
        private static TextColor getTextColor(JsonObject jsonObject) {
            if (jsonObject.has("color")) {
                String string = GsonHelper.getAsString(jsonObject, "color");
                return TextColor.parseColor(string);
            }
            return null;
        }

        @Nullable
        private static Boolean getOptionalFlag(JsonObject jsonObject, String string) {
            if (jsonObject.has(string)) {
                return jsonObject.get(string).getAsBoolean();
            }
            return null;
        }

        @Nullable
        public JsonElement serialize(Style style, Type type, JsonSerializationContext jsonSerializationContext) {
            if (style.isEmpty()) {
                return null;
            }
            JsonObject jsonObject = new JsonObject();
            if (style.bold != null) {
                jsonObject.addProperty("bold", style.bold);
            }
            if (style.italic != null) {
                jsonObject.addProperty("italic", style.italic);
            }
            if (style.underlined != null) {
                jsonObject.addProperty("underlined", style.underlined);
            }
            if (style.strikethrough != null) {
                jsonObject.addProperty("strikethrough", style.strikethrough);
            }
            if (style.obfuscated != null) {
                jsonObject.addProperty("obfuscated", style.obfuscated);
            }
            if (style.color != null) {
                jsonObject.addProperty("color", style.color.serialize());
            }
            if (style.insertion != null) {
                jsonObject.add("insertion", jsonSerializationContext.serialize((Object)style.insertion));
            }
            if (style.clickEvent != null) {
                JsonObject jsonObject2 = new JsonObject();
                jsonObject2.addProperty("action", style.clickEvent.getAction().getName());
                jsonObject2.addProperty("value", style.clickEvent.getValue());
                jsonObject.add("clickEvent", (JsonElement)jsonObject2);
            }
            if (style.hoverEvent != null) {
                jsonObject.add("hoverEvent", (JsonElement)style.hoverEvent.serialize());
            }
            if (style.font != null) {
                jsonObject.addProperty("font", style.font.toString());
            }
            return jsonObject;
        }

        @Nullable
        public /* synthetic */ JsonElement serialize(Object object, Type type, JsonSerializationContext jsonSerializationContext) {
            return this.serialize((Style)object, type, jsonSerializationContext);
        }

        @Nullable
        public /* synthetic */ Object deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            return this.deserialize(jsonElement, type, jsonDeserializationContext);
        }
    }

}

