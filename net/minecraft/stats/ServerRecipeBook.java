/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.stats;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import net.minecraft.ResourceLocationException;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundRecipePacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.stats.RecipeBook;
import net.minecraft.stats.RecipeBookSettings;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerRecipeBook
extends RecipeBook {
    private static final Logger LOGGER = LogManager.getLogger();

    public int addRecipes(Collection<Recipe<?>> collection, ServerPlayer serverPlayer) {
        ArrayList arrayList = Lists.newArrayList();
        int n = 0;
        for (Recipe<?> recipe : collection) {
            ResourceLocation resourceLocation = recipe.getId();
            if (this.known.contains(resourceLocation) || recipe.isSpecial()) continue;
            this.add(resourceLocation);
            this.addHighlight(resourceLocation);
            arrayList.add(resourceLocation);
            CriteriaTriggers.RECIPE_UNLOCKED.trigger(serverPlayer, recipe);
            ++n;
        }
        this.sendRecipes(ClientboundRecipePacket.State.ADD, serverPlayer, arrayList);
        return n;
    }

    public int removeRecipes(Collection<Recipe<?>> collection, ServerPlayer serverPlayer) {
        ArrayList arrayList = Lists.newArrayList();
        int n = 0;
        for (Recipe<?> recipe : collection) {
            ResourceLocation resourceLocation = recipe.getId();
            if (!this.known.contains(resourceLocation)) continue;
            this.remove(resourceLocation);
            arrayList.add(resourceLocation);
            ++n;
        }
        this.sendRecipes(ClientboundRecipePacket.State.REMOVE, serverPlayer, arrayList);
        return n;
    }

    private void sendRecipes(ClientboundRecipePacket.State state, ServerPlayer serverPlayer, List<ResourceLocation> list) {
        serverPlayer.connection.send(new ClientboundRecipePacket(state, list, Collections.emptyList(), this.getBookSettings()));
    }

    public CompoundTag toNbt() {
        CompoundTag compoundTag = new CompoundTag();
        this.getBookSettings().write(compoundTag);
        ListTag listTag = new ListTag();
        for (Object object : this.known) {
            listTag.add(StringTag.valueOf(((ResourceLocation)object).toString()));
        }
        compoundTag.put("recipes", listTag);
        ListTag listTag2 = new ListTag();
        for (ResourceLocation resourceLocation : this.highlight) {
            listTag2.add(StringTag.valueOf(resourceLocation.toString()));
        }
        compoundTag.put("toBeDisplayed", listTag2);
        return compoundTag;
    }

    public void fromNbt(CompoundTag compoundTag, RecipeManager recipeManager) {
        this.setBookSettings(RecipeBookSettings.read(compoundTag));
        ListTag listTag = compoundTag.getList("recipes", 8);
        this.loadRecipes(listTag, this::add, recipeManager);
        ListTag listTag2 = compoundTag.getList("toBeDisplayed", 8);
        this.loadRecipes(listTag2, this::addHighlight, recipeManager);
    }

    private void loadRecipes(ListTag listTag, Consumer<Recipe<?>> consumer, RecipeManager recipeManager) {
        for (int i = 0; i < listTag.size(); ++i) {
            String string = listTag.getString(i);
            try {
                ResourceLocation resourceLocation = new ResourceLocation(string);
                Optional<Recipe<?>> optional = recipeManager.byKey(resourceLocation);
                if (!optional.isPresent()) {
                    LOGGER.error("Tried to load unrecognized recipe: {} removed now.", (Object)resourceLocation);
                    continue;
                }
                consumer.accept(optional.get());
                continue;
            }
            catch (ResourceLocationException resourceLocationException) {
                LOGGER.error("Tried to load improperly formatted recipe: {} removed now.", (Object)string);
            }
        }
    }

    public void sendInitialRecipeBook(ServerPlayer serverPlayer) {
        serverPlayer.connection.send(new ClientboundRecipePacket(ClientboundRecipePacket.State.INIT, this.known, this.highlight, this.getBookSettings()));
    }
}

