/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Lists
 */
package net.minecraft.network.protocol.game;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.RecipeBookSettings;

public class ClientboundRecipePacket
implements Packet<ClientGamePacketListener> {
    private State state;
    private List<ResourceLocation> recipes;
    private List<ResourceLocation> toHighlight;
    private RecipeBookSettings bookSettings;

    public ClientboundRecipePacket() {
    }

    public ClientboundRecipePacket(State state, Collection<ResourceLocation> collection, Collection<ResourceLocation> collection2, RecipeBookSettings recipeBookSettings) {
        this.state = state;
        this.recipes = ImmutableList.copyOf(collection);
        this.toHighlight = ImmutableList.copyOf(collection2);
        this.bookSettings = recipeBookSettings;
    }

    @Override
    public void handle(ClientGamePacketListener clientGamePacketListener) {
        clientGamePacketListener.handleAddOrRemoveRecipes(this);
    }

    @Override
    public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
        int n;
        this.state = friendlyByteBuf.readEnum(State.class);
        this.bookSettings = RecipeBookSettings.read(friendlyByteBuf);
        int n2 = friendlyByteBuf.readVarInt();
        this.recipes = Lists.newArrayList();
        for (n = 0; n < n2; ++n) {
            this.recipes.add(friendlyByteBuf.readResourceLocation());
        }
        if (this.state == State.INIT) {
            n2 = friendlyByteBuf.readVarInt();
            this.toHighlight = Lists.newArrayList();
            for (n = 0; n < n2; ++n) {
                this.toHighlight.add(friendlyByteBuf.readResourceLocation());
            }
        }
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
        friendlyByteBuf.writeEnum(this.state);
        this.bookSettings.write(friendlyByteBuf);
        friendlyByteBuf.writeVarInt(this.recipes.size());
        for (ResourceLocation resourceLocation : this.recipes) {
            friendlyByteBuf.writeResourceLocation(resourceLocation);
        }
        if (this.state == State.INIT) {
            friendlyByteBuf.writeVarInt(this.toHighlight.size());
            for (ResourceLocation resourceLocation : this.toHighlight) {
                friendlyByteBuf.writeResourceLocation(resourceLocation);
            }
        }
    }

    public List<ResourceLocation> getRecipes() {
        return this.recipes;
    }

    public List<ResourceLocation> getHighlights() {
        return this.toHighlight;
    }

    public RecipeBookSettings getBookSettings() {
        return this.bookSettings;
    }

    public State getState() {
        return this.state;
    }

    public static enum State {
        INIT,
        ADD,
        REMOVE;
        
    }

}

