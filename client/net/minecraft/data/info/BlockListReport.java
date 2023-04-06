/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 */
package net.minecraft.data.info;

import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Iterator;
import net.minecraft.Util;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;

public class BlockListReport
implements DataProvider {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private final DataGenerator generator;

    public BlockListReport(DataGenerator dataGenerator) {
        this.generator = dataGenerator;
    }

    @Override
    public void run(HashCache hashCache) throws IOException {
        JsonObject jsonObject = new JsonObject();
        for (Block block : Registry.BLOCK) {
            JsonObject jsonObject2;
            JsonArray jsonArray;
            ResourceLocation resourceLocation = Registry.BLOCK.getKey(block);
            JsonObject jsonObject3 = new JsonObject();
            StateDefinition<Block, BlockState> stateDefinition = block.getStateDefinition();
            if (!stateDefinition.getProperties().isEmpty()) {
                jsonObject2 = new JsonObject();
                for (Property property : stateDefinition.getProperties()) {
                    jsonArray = new JsonArray();
                    for (Object object : property.getPossibleValues()) {
                        jsonArray.add(Util.getPropertyName(property, object));
                    }
                    jsonObject2.add(property.getName(), (JsonElement)jsonArray);
                }
                jsonObject3.add("properties", (JsonElement)jsonObject2);
            }
            jsonObject2 = new JsonArray();
            for (BlockState blockState : stateDefinition.getPossibleStates()) {
                Object object;
                jsonArray = new JsonObject();
                JsonObject jsonObject4 = new JsonObject();
                object = stateDefinition.getProperties().iterator();
                while (object.hasNext()) {
                    Property property = (Property)object.next();
                    jsonObject4.addProperty(property.getName(), Util.getPropertyName(property, blockState.getValue(property)));
                }
                if (jsonObject4.size() > 0) {
                    jsonArray.add("properties", (JsonElement)jsonObject4);
                }
                jsonArray.addProperty("id", (Number)Block.getId(blockState));
                if (blockState == block.defaultBlockState()) {
                    jsonArray.addProperty("default", Boolean.valueOf(true));
                }
                jsonObject2.add((JsonElement)jsonArray);
            }
            jsonObject3.add("states", (JsonElement)jsonObject2);
            jsonObject.add(resourceLocation.toString(), (JsonElement)jsonObject3);
        }
        Path path = this.generator.getOutputFolder().resolve("reports/blocks.json");
        DataProvider.save(GSON, hashCache, (JsonElement)jsonObject, path);
    }

    @Override
    public String getName() {
        return "Block List";
    }
}

