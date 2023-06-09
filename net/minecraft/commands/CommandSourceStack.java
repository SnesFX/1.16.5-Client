/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.authlib.GameProfile
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.ResultConsumer
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.SimpleCommandExceptionType
 *  com.mojang.brigadier.suggestion.Suggestions
 *  com.mojang.brigadier.suggestion.SuggestionsBuilder
 *  javax.annotation.Nullable
 */
package net.minecraft.commands;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.ResultConsumer;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.BinaryOperator;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerScoreboard;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public class CommandSourceStack
implements SharedSuggestionProvider {
    public static final SimpleCommandExceptionType ERROR_NOT_PLAYER = new SimpleCommandExceptionType((Message)new TranslatableComponent("permissions.requires.player"));
    public static final SimpleCommandExceptionType ERROR_NOT_ENTITY = new SimpleCommandExceptionType((Message)new TranslatableComponent("permissions.requires.entity"));
    private final CommandSource source;
    private final Vec3 worldPosition;
    private final ServerLevel level;
    private final int permissionLevel;
    private final String textName;
    private final Component displayName;
    private final MinecraftServer server;
    private final boolean silent;
    @Nullable
    private final Entity entity;
    private final ResultConsumer<CommandSourceStack> consumer;
    private final EntityAnchorArgument.Anchor anchor;
    private final Vec2 rotation;

    public CommandSourceStack(CommandSource commandSource, Vec3 vec3, Vec2 vec2, ServerLevel serverLevel, int n2, String string, Component component, MinecraftServer minecraftServer, @Nullable Entity entity) {
        this(commandSource, vec3, vec2, serverLevel, n2, string, component, minecraftServer, entity, false, (ResultConsumer<CommandSourceStack>)((ResultConsumer)(commandContext, bl, n) -> {}), EntityAnchorArgument.Anchor.FEET);
    }

    protected CommandSourceStack(CommandSource commandSource, Vec3 vec3, Vec2 vec2, ServerLevel serverLevel, int n, String string, Component component, MinecraftServer minecraftServer, @Nullable Entity entity, boolean bl, ResultConsumer<CommandSourceStack> resultConsumer, EntityAnchorArgument.Anchor anchor) {
        this.source = commandSource;
        this.worldPosition = vec3;
        this.level = serverLevel;
        this.silent = bl;
        this.entity = entity;
        this.permissionLevel = n;
        this.textName = string;
        this.displayName = component;
        this.server = minecraftServer;
        this.consumer = resultConsumer;
        this.anchor = anchor;
        this.rotation = vec2;
    }

    public CommandSourceStack withEntity(Entity entity) {
        if (this.entity == entity) {
            return this;
        }
        return new CommandSourceStack(this.source, this.worldPosition, this.rotation, this.level, this.permissionLevel, entity.getName().getString(), entity.getDisplayName(), this.server, entity, this.silent, this.consumer, this.anchor);
    }

    public CommandSourceStack withPosition(Vec3 vec3) {
        if (this.worldPosition.equals(vec3)) {
            return this;
        }
        return new CommandSourceStack(this.source, vec3, this.rotation, this.level, this.permissionLevel, this.textName, this.displayName, this.server, this.entity, this.silent, this.consumer, this.anchor);
    }

    public CommandSourceStack withRotation(Vec2 vec2) {
        if (this.rotation.equals(vec2)) {
            return this;
        }
        return new CommandSourceStack(this.source, this.worldPosition, vec2, this.level, this.permissionLevel, this.textName, this.displayName, this.server, this.entity, this.silent, this.consumer, this.anchor);
    }

    public CommandSourceStack withCallback(ResultConsumer<CommandSourceStack> resultConsumer) {
        if (this.consumer.equals(resultConsumer)) {
            return this;
        }
        return new CommandSourceStack(this.source, this.worldPosition, this.rotation, this.level, this.permissionLevel, this.textName, this.displayName, this.server, this.entity, this.silent, resultConsumer, this.anchor);
    }

    public CommandSourceStack withCallback(ResultConsumer<CommandSourceStack> resultConsumer, BinaryOperator<ResultConsumer<CommandSourceStack>> binaryOperator) {
        ResultConsumer resultConsumer2 = (ResultConsumer)binaryOperator.apply(this.consumer, resultConsumer);
        return this.withCallback((ResultConsumer<CommandSourceStack>)resultConsumer2);
    }

    public CommandSourceStack withSuppressedOutput() {
        if (this.silent) {
            return this;
        }
        return new CommandSourceStack(this.source, this.worldPosition, this.rotation, this.level, this.permissionLevel, this.textName, this.displayName, this.server, this.entity, true, this.consumer, this.anchor);
    }

    public CommandSourceStack withPermission(int n) {
        if (n == this.permissionLevel) {
            return this;
        }
        return new CommandSourceStack(this.source, this.worldPosition, this.rotation, this.level, n, this.textName, this.displayName, this.server, this.entity, this.silent, this.consumer, this.anchor);
    }

    public CommandSourceStack withMaximumPermission(int n) {
        if (n <= this.permissionLevel) {
            return this;
        }
        return new CommandSourceStack(this.source, this.worldPosition, this.rotation, this.level, n, this.textName, this.displayName, this.server, this.entity, this.silent, this.consumer, this.anchor);
    }

    public CommandSourceStack withAnchor(EntityAnchorArgument.Anchor anchor) {
        if (anchor == this.anchor) {
            return this;
        }
        return new CommandSourceStack(this.source, this.worldPosition, this.rotation, this.level, this.permissionLevel, this.textName, this.displayName, this.server, this.entity, this.silent, this.consumer, anchor);
    }

    public CommandSourceStack withLevel(ServerLevel serverLevel) {
        if (serverLevel == this.level) {
            return this;
        }
        double d = DimensionType.getTeleportationScale(this.level.dimensionType(), serverLevel.dimensionType());
        Vec3 vec3 = new Vec3(this.worldPosition.x * d, this.worldPosition.y, this.worldPosition.z * d);
        return new CommandSourceStack(this.source, vec3, this.rotation, serverLevel, this.permissionLevel, this.textName, this.displayName, this.server, this.entity, this.silent, this.consumer, this.anchor);
    }

    public CommandSourceStack facing(Entity entity, EntityAnchorArgument.Anchor anchor) throws CommandSyntaxException {
        return this.facing(anchor.apply(entity));
    }

    public CommandSourceStack facing(Vec3 vec3) throws CommandSyntaxException {
        Vec3 vec32 = this.anchor.apply(this);
        double d = vec3.x - vec32.x;
        double d2 = vec3.y - vec32.y;
        double d3 = vec3.z - vec32.z;
        double d4 = Mth.sqrt(d * d + d3 * d3);
        float f = Mth.wrapDegrees((float)(-(Mth.atan2(d2, d4) * 57.2957763671875)));
        float f2 = Mth.wrapDegrees((float)(Mth.atan2(d3, d) * 57.2957763671875) - 90.0f);
        return this.withRotation(new Vec2(f, f2));
    }

    public Component getDisplayName() {
        return this.displayName;
    }

    public String getTextName() {
        return this.textName;
    }

    @Override
    public boolean hasPermission(int n) {
        return this.permissionLevel >= n;
    }

    public Vec3 getPosition() {
        return this.worldPosition;
    }

    public ServerLevel getLevel() {
        return this.level;
    }

    @Nullable
    public Entity getEntity() {
        return this.entity;
    }

    public Entity getEntityOrException() throws CommandSyntaxException {
        if (this.entity == null) {
            throw ERROR_NOT_ENTITY.create();
        }
        return this.entity;
    }

    public ServerPlayer getPlayerOrException() throws CommandSyntaxException {
        if (!(this.entity instanceof ServerPlayer)) {
            throw ERROR_NOT_PLAYER.create();
        }
        return (ServerPlayer)this.entity;
    }

    public Vec2 getRotation() {
        return this.rotation;
    }

    public MinecraftServer getServer() {
        return this.server;
    }

    public EntityAnchorArgument.Anchor getAnchor() {
        return this.anchor;
    }

    public void sendSuccess(Component component, boolean bl) {
        if (this.source.acceptsSuccess() && !this.silent) {
            this.source.sendMessage(component, Util.NIL_UUID);
        }
        if (bl && this.source.shouldInformAdmins() && !this.silent) {
            this.broadcastToAdmins(component);
        }
    }

    private void broadcastToAdmins(Component component) {
        MutableComponent mutableComponent = new TranslatableComponent("chat.type.admin", this.getDisplayName(), component).withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC);
        if (this.server.getGameRules().getBoolean(GameRules.RULE_SENDCOMMANDFEEDBACK)) {
            for (ServerPlayer serverPlayer : this.server.getPlayerList().getPlayers()) {
                if (serverPlayer == this.source || !this.server.getPlayerList().isOp(serverPlayer.getGameProfile())) continue;
                serverPlayer.sendMessage(mutableComponent, Util.NIL_UUID);
            }
        }
        if (this.source != this.server && this.server.getGameRules().getBoolean(GameRules.RULE_LOGADMINCOMMANDS)) {
            this.server.sendMessage(mutableComponent, Util.NIL_UUID);
        }
    }

    public void sendFailure(Component component) {
        if (this.source.acceptsFailure() && !this.silent) {
            this.source.sendMessage(new TextComponent("").append(component).withStyle(ChatFormatting.RED), Util.NIL_UUID);
        }
    }

    public void onCommandComplete(CommandContext<CommandSourceStack> commandContext, boolean bl, int n) {
        if (this.consumer != null) {
            this.consumer.onCommandComplete(commandContext, bl, n);
        }
    }

    @Override
    public Collection<String> getOnlinePlayerNames() {
        return Lists.newArrayList((Object[])this.server.getPlayerNames());
    }

    @Override
    public Collection<String> getAllTeams() {
        return this.server.getScoreboard().getTeamNames();
    }

    @Override
    public Collection<ResourceLocation> getAvailableSoundEvents() {
        return Registry.SOUND_EVENT.keySet();
    }

    @Override
    public Stream<ResourceLocation> getRecipeNames() {
        return this.server.getRecipeManager().getRecipeIds();
    }

    @Override
    public CompletableFuture<Suggestions> customSuggestion(CommandContext<SharedSuggestionProvider> commandContext, SuggestionsBuilder suggestionsBuilder) {
        return null;
    }

    @Override
    public Set<ResourceKey<Level>> levels() {
        return this.server.levelKeys();
    }

    @Override
    public RegistryAccess registryAccess() {
        return this.server.registryAccess();
    }
}

