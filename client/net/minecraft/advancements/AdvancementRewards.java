/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonNull
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  javax.annotation.Nullable
 */
package net.minecraft.advancements;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandFunction;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerFunctionManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTables;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;

public class AdvancementRewards {
    public static final AdvancementRewards EMPTY = new AdvancementRewards(0, new ResourceLocation[0], new ResourceLocation[0], CommandFunction.CacheableFunction.NONE);
    private final int experience;
    private final ResourceLocation[] loot;
    private final ResourceLocation[] recipes;
    private final CommandFunction.CacheableFunction function;

    public AdvancementRewards(int n, ResourceLocation[] arrresourceLocation, ResourceLocation[] arrresourceLocation2, CommandFunction.CacheableFunction cacheableFunction) {
        this.experience = n;
        this.loot = arrresourceLocation;
        this.recipes = arrresourceLocation2;
        this.function = cacheableFunction;
    }

    public void grant(ServerPlayer serverPlayer) {
        serverPlayer.giveExperiencePoints(this.experience);
        LootContext lootContext = new LootContext.Builder(serverPlayer.getLevel()).withParameter(LootContextParams.THIS_ENTITY, serverPlayer).withParameter(LootContextParams.ORIGIN, serverPlayer.position()).withRandom(serverPlayer.getRandom()).create(LootContextParamSets.ADVANCEMENT_REWARD);
        boolean bl = false;
        for (ResourceLocation resourceLocation : this.loot) {
            for (ItemStack itemStack : serverPlayer.server.getLootTables().get(resourceLocation).getRandomItems(lootContext)) {
                if (serverPlayer.addItem(itemStack)) {
                    serverPlayer.level.playSound(null, serverPlayer.getX(), serverPlayer.getY(), serverPlayer.getZ(), SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.2f, ((serverPlayer.getRandom().nextFloat() - serverPlayer.getRandom().nextFloat()) * 0.7f + 1.0f) * 2.0f);
                    bl = true;
                    continue;
                }
                ItemEntity itemEntity = serverPlayer.drop(itemStack, false);
                if (itemEntity == null) continue;
                itemEntity.setNoPickUpDelay();
                itemEntity.setOwner(serverPlayer.getUUID());
            }
        }
        if (bl) {
            serverPlayer.inventoryMenu.broadcastChanges();
        }
        if (this.recipes.length > 0) {
            serverPlayer.awardRecipesByKey(this.recipes);
        }
        MinecraftServer minecraftServer = serverPlayer.server;
        this.function.get(minecraftServer.getFunctions()).ifPresent(commandFunction -> minecraftServer.getFunctions().execute((CommandFunction)commandFunction, serverPlayer.createCommandSourceStack().withSuppressedOutput().withPermission(2)));
    }

    public String toString() {
        return "AdvancementRewards{experience=" + this.experience + ", loot=" + Arrays.toString(this.loot) + ", recipes=" + Arrays.toString(this.recipes) + ", function=" + this.function + '}';
    }

    public JsonElement serializeToJson() {
        JsonArray jsonArray;
        if (this == EMPTY) {
            return JsonNull.INSTANCE;
        }
        JsonObject jsonObject = new JsonObject();
        if (this.experience != 0) {
            jsonObject.addProperty("experience", (Number)this.experience);
        }
        if (this.loot.length > 0) {
            jsonArray = new JsonArray();
            for (ResourceLocation resourceLocation : this.loot) {
                jsonArray.add(resourceLocation.toString());
            }
            jsonObject.add("loot", (JsonElement)jsonArray);
        }
        if (this.recipes.length > 0) {
            jsonArray = new JsonArray();
            for (ResourceLocation resourceLocation : this.recipes) {
                jsonArray.add(resourceLocation.toString());
            }
            jsonObject.add("recipes", (JsonElement)jsonArray);
        }
        if (this.function.getId() != null) {
            jsonObject.addProperty("function", this.function.getId().toString());
        }
        return jsonObject;
    }

    public static AdvancementRewards deserialize(JsonObject jsonObject) throws JsonParseException {
        int n = GsonHelper.getAsInt(jsonObject, "experience", 0);
        JsonArray jsonArray = GsonHelper.getAsJsonArray(jsonObject, "loot", new JsonArray());
        ResourceLocation[] arrresourceLocation = new ResourceLocation[jsonArray.size()];
        for (int i = 0; i < arrresourceLocation.length; ++i) {
            arrresourceLocation[i] = new ResourceLocation(GsonHelper.convertToString(jsonArray.get(i), "loot[" + i + "]"));
        }
        JsonArray jsonArray2 = GsonHelper.getAsJsonArray(jsonObject, "recipes", new JsonArray());
        ResourceLocation[] arrresourceLocation2 = new ResourceLocation[jsonArray2.size()];
        for (int i = 0; i < arrresourceLocation2.length; ++i) {
            arrresourceLocation2[i] = new ResourceLocation(GsonHelper.convertToString(jsonArray2.get(i), "recipes[" + i + "]"));
        }
        CommandFunction.CacheableFunction cacheableFunction = jsonObject.has("function") ? new CommandFunction.CacheableFunction(new ResourceLocation(GsonHelper.getAsString(jsonObject, "function"))) : CommandFunction.CacheableFunction.NONE;
        return new AdvancementRewards(n, arrresourceLocation, arrresourceLocation2, cacheableFunction);
    }

    public static class Builder {
        private int experience;
        private final List<ResourceLocation> loot = Lists.newArrayList();
        private final List<ResourceLocation> recipes = Lists.newArrayList();
        @Nullable
        private ResourceLocation function;

        public static Builder experience(int n) {
            return new Builder().addExperience(n);
        }

        public Builder addExperience(int n) {
            this.experience += n;
            return this;
        }

        public static Builder recipe(ResourceLocation resourceLocation) {
            return new Builder().addRecipe(resourceLocation);
        }

        public Builder addRecipe(ResourceLocation resourceLocation) {
            this.recipes.add(resourceLocation);
            return this;
        }

        public AdvancementRewards build() {
            return new AdvancementRewards(this.experience, this.loot.toArray(new ResourceLocation[0]), this.recipes.toArray(new ResourceLocation[0]), this.function == null ? CommandFunction.CacheableFunction.NONE : new CommandFunction.CacheableFunction(this.function));
        }
    }

}

