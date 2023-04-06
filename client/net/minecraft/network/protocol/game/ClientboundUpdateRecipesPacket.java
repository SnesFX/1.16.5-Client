/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 */
package net.minecraft.network.protocol.game;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class ClientboundUpdateRecipesPacket
implements Packet<ClientGamePacketListener> {
    private List<Recipe<?>> recipes;

    public ClientboundUpdateRecipesPacket() {
    }

    public ClientboundUpdateRecipesPacket(Collection<Recipe<?>> collection) {
        this.recipes = Lists.newArrayList(collection);
    }

    @Override
    public void handle(ClientGamePacketListener clientGamePacketListener) {
        clientGamePacketListener.handleUpdateRecipes(this);
    }

    @Override
    public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
        this.recipes = Lists.newArrayList();
        int n = friendlyByteBuf.readVarInt();
        for (int i = 0; i < n; ++i) {
            this.recipes.add(ClientboundUpdateRecipesPacket.fromNetwork(friendlyByteBuf));
        }
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
        friendlyByteBuf.writeVarInt(this.recipes.size());
        for (Recipe<?> recipe : this.recipes) {
            ClientboundUpdateRecipesPacket.toNetwork(recipe, friendlyByteBuf);
        }
    }

    public List<Recipe<?>> getRecipes() {
        return this.recipes;
    }

    public static Recipe<?> fromNetwork(FriendlyByteBuf friendlyByteBuf) {
        ResourceLocation resourceLocation = friendlyByteBuf.readResourceLocation();
        ResourceLocation resourceLocation2 = friendlyByteBuf.readResourceLocation();
        return Registry.RECIPE_SERIALIZER.getOptional(resourceLocation).orElseThrow(() -> new IllegalArgumentException("Unknown recipe serializer " + resourceLocation)).fromNetwork(resourceLocation2, friendlyByteBuf);
    }

    public static <T extends Recipe<?>> void toNetwork(T t, FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeResourceLocation(Registry.RECIPE_SERIALIZER.getKey(t.getSerializer()));
        friendlyByteBuf.writeResourceLocation(t.getId());
        t.getSerializer().toNetwork(friendlyByteBuf, t);
    }
}

