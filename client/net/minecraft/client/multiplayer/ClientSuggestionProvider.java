/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.authlib.GameProfile
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.suggestion.Suggestions
 *  com.mojang.brigadier.suggestion.SuggestionsBuilder
 */
package net.minecraft.client.multiplayer;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundCommandSuggestionPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.Scoreboard;

public class ClientSuggestionProvider
implements SharedSuggestionProvider {
    private final ClientPacketListener connection;
    private final Minecraft minecraft;
    private int pendingSuggestionsId = -1;
    private CompletableFuture<Suggestions> pendingSuggestionsFuture;

    public ClientSuggestionProvider(ClientPacketListener clientPacketListener, Minecraft minecraft) {
        this.connection = clientPacketListener;
        this.minecraft = minecraft;
    }

    @Override
    public Collection<String> getOnlinePlayerNames() {
        ArrayList arrayList = Lists.newArrayList();
        for (PlayerInfo playerInfo : this.connection.getOnlinePlayers()) {
            arrayList.add(playerInfo.getProfile().getName());
        }
        return arrayList;
    }

    @Override
    public Collection<String> getSelectedEntities() {
        if (this.minecraft.hitResult != null && this.minecraft.hitResult.getType() == HitResult.Type.ENTITY) {
            return Collections.singleton(((EntityHitResult)this.minecraft.hitResult).getEntity().getStringUUID());
        }
        return Collections.emptyList();
    }

    @Override
    public Collection<String> getAllTeams() {
        return this.connection.getLevel().getScoreboard().getTeamNames();
    }

    @Override
    public Collection<ResourceLocation> getAvailableSoundEvents() {
        return this.minecraft.getSoundManager().getAvailableSounds();
    }

    @Override
    public Stream<ResourceLocation> getRecipeNames() {
        return this.connection.getRecipeManager().getRecipeIds();
    }

    @Override
    public boolean hasPermission(int n) {
        LocalPlayer localPlayer = this.minecraft.player;
        return localPlayer != null ? localPlayer.hasPermissions(n) : n == 0;
    }

    @Override
    public CompletableFuture<Suggestions> customSuggestion(CommandContext<SharedSuggestionProvider> commandContext, SuggestionsBuilder suggestionsBuilder) {
        if (this.pendingSuggestionsFuture != null) {
            this.pendingSuggestionsFuture.cancel(false);
        }
        this.pendingSuggestionsFuture = new CompletableFuture();
        int n = ++this.pendingSuggestionsId;
        this.connection.send(new ServerboundCommandSuggestionPacket(n, commandContext.getInput()));
        return this.pendingSuggestionsFuture;
    }

    private static String prettyPrint(double d) {
        return String.format(Locale.ROOT, "%.2f", d);
    }

    private static String prettyPrint(int n) {
        return Integer.toString(n);
    }

    @Override
    public Collection<SharedSuggestionProvider.TextCoordinates> getRelevantCoordinates() {
        HitResult hitResult = this.minecraft.hitResult;
        if (hitResult == null || hitResult.getType() != HitResult.Type.BLOCK) {
            return SharedSuggestionProvider.super.getRelevantCoordinates();
        }
        BlockPos blockPos = ((BlockHitResult)hitResult).getBlockPos();
        return Collections.singleton(new SharedSuggestionProvider.TextCoordinates(ClientSuggestionProvider.prettyPrint(blockPos.getX()), ClientSuggestionProvider.prettyPrint(blockPos.getY()), ClientSuggestionProvider.prettyPrint(blockPos.getZ())));
    }

    @Override
    public Collection<SharedSuggestionProvider.TextCoordinates> getAbsoluteCoordinates() {
        HitResult hitResult = this.minecraft.hitResult;
        if (hitResult == null || hitResult.getType() != HitResult.Type.BLOCK) {
            return SharedSuggestionProvider.super.getAbsoluteCoordinates();
        }
        Vec3 vec3 = hitResult.getLocation();
        return Collections.singleton(new SharedSuggestionProvider.TextCoordinates(ClientSuggestionProvider.prettyPrint(vec3.x), ClientSuggestionProvider.prettyPrint(vec3.y), ClientSuggestionProvider.prettyPrint(vec3.z)));
    }

    @Override
    public Set<ResourceKey<Level>> levels() {
        return this.connection.levels();
    }

    @Override
    public RegistryAccess registryAccess() {
        return this.connection.registryAccess();
    }

    public void completeCustomSuggestions(int n, Suggestions suggestions) {
        if (n == this.pendingSuggestionsId) {
            this.pendingSuggestionsFuture.complete(suggestions);
            this.pendingSuggestionsFuture = null;
            this.pendingSuggestionsId = -1;
        }
    }
}

