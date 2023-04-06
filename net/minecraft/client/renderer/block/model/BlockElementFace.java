/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonDeserializer
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  javax.annotation.Nullable
 */
package net.minecraft.client.renderer.block.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.block.model.BlockFaceUV;
import net.minecraft.core.Direction;
import net.minecraft.util.GsonHelper;

public class BlockElementFace {
    public final Direction cullForDirection;
    public final int tintIndex;
    public final String texture;
    public final BlockFaceUV uv;

    public BlockElementFace(@Nullable Direction direction, int n, String string, BlockFaceUV blockFaceUV) {
        this.cullForDirection = direction;
        this.tintIndex = n;
        this.texture = string;
        this.uv = blockFaceUV;
    }

    public static class Deserializer
    implements JsonDeserializer<BlockElementFace> {
        protected Deserializer() {
        }

        public BlockElementFace deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            Direction direction = this.getCullFacing(jsonObject);
            int n = this.getTintIndex(jsonObject);
            String string = this.getTexture(jsonObject);
            BlockFaceUV blockFaceUV = (BlockFaceUV)jsonDeserializationContext.deserialize((JsonElement)jsonObject, BlockFaceUV.class);
            return new BlockElementFace(direction, n, string, blockFaceUV);
        }

        protected int getTintIndex(JsonObject jsonObject) {
            return GsonHelper.getAsInt(jsonObject, "tintindex", -1);
        }

        private String getTexture(JsonObject jsonObject) {
            return GsonHelper.getAsString(jsonObject, "texture");
        }

        @Nullable
        private Direction getCullFacing(JsonObject jsonObject) {
            String string = GsonHelper.getAsString(jsonObject, "cullface", "");
            return Direction.byName(string);
        }

        public /* synthetic */ Object deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            return this.deserialize(jsonElement, type, jsonDeserializationContext);
        }
    }

}

