/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  com.mojang.authlib.GameProfile
 *  com.mojang.datafixers.DataFixer
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.DynamicOps
 *  io.netty.buffer.ByteBuf
 *  io.netty.buffer.Unpooled
 *  javax.annotation.Nullable
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.server.players;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.DataFixer;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.io.File;
import java.net.SocketAddress;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.Connection;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundChangeDifficultyPacket;
import net.minecraft.network.protocol.game.ClientboundChatPacket;
import net.minecraft.network.protocol.game.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.network.protocol.game.ClientboundLoginPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerAbilitiesPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoPacket;
import net.minecraft.network.protocol.game.ClientboundRespawnPacket;
import net.minecraft.network.protocol.game.ClientboundSetBorderPacket;
import net.minecraft.network.protocol.game.ClientboundSetCarriedItemPacket;
import net.minecraft.network.protocol.game.ClientboundSetChunkCacheRadiusPacket;
import net.minecraft.network.protocol.game.ClientboundSetDefaultSpawnPositionPacket;
import net.minecraft.network.protocol.game.ClientboundSetExperiencePacket;
import net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacket;
import net.minecraft.network.protocol.game.ClientboundSetTimePacket;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateMobEffectPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateRecipesPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateTagsPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.ServerAdvancementManager;
import net.minecraft.server.ServerScoreboard;
import net.minecraft.server.bossevents.CustomBossEvents;
import net.minecraft.server.level.DemoMode;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.server.players.GameProfileCache;
import net.minecraft.server.players.IpBanList;
import net.minecraft.server.players.IpBanListEntry;
import net.minecraft.server.players.ServerOpList;
import net.minecraft.server.players.ServerOpListEntry;
import net.minecraft.server.players.UserBanList;
import net.minecraft.server.players.UserBanListEntry;
import net.minecraft.server.players.UserWhiteList;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.ServerRecipeBook;
import net.minecraft.stats.ServerStatsCounter;
import net.minecraft.stats.Stats;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagContainer;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.border.BorderChangeListener;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.level.storage.PlayerDataStorage;
import net.minecraft.world.level.storage.WorldData;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Team;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class PlayerList {
    public static final File USERBANLIST_FILE = new File("banned-players.json");
    public static final File IPBANLIST_FILE = new File("banned-ips.json");
    public static final File OPLIST_FILE = new File("ops.json");
    public static final File WHITELIST_FILE = new File("whitelist.json");
    private static final Logger LOGGER = LogManager.getLogger();
    private static final SimpleDateFormat BAN_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
    private final MinecraftServer server;
    private final List<ServerPlayer> players = Lists.newArrayList();
    private final Map<UUID, ServerPlayer> playersByUUID = Maps.newHashMap();
    private final UserBanList bans = new UserBanList(USERBANLIST_FILE);
    private final IpBanList ipBans = new IpBanList(IPBANLIST_FILE);
    private final ServerOpList ops = new ServerOpList(OPLIST_FILE);
    private final UserWhiteList whitelist = new UserWhiteList(WHITELIST_FILE);
    private final Map<UUID, ServerStatsCounter> stats = Maps.newHashMap();
    private final Map<UUID, PlayerAdvancements> advancements = Maps.newHashMap();
    private final PlayerDataStorage playerIo;
    private boolean doWhiteList;
    private final RegistryAccess.RegistryHolder registryHolder;
    protected final int maxPlayers;
    private int viewDistance;
    private GameType overrideGameMode;
    private boolean allowCheatsForAllPlayers;
    private int sendAllPlayerInfoIn;

    public PlayerList(MinecraftServer minecraftServer, RegistryAccess.RegistryHolder registryHolder, PlayerDataStorage playerDataStorage, int n) {
        this.server = minecraftServer;
        this.registryHolder = registryHolder;
        this.maxPlayers = n;
        this.playerIo = playerDataStorage;
    }

    public void placeNewPlayer(Connection connection, ServerPlayer serverPlayer) {
        ServerLevel serverLevel;
        Object object;
        GameProfile gameProfile = serverPlayer.getGameProfile();
        GameProfileCache gameProfileCache = this.server.getProfileCache();
        GameProfile gameProfile2 = gameProfileCache.get(gameProfile.getId());
        String string = gameProfile2 == null ? gameProfile.getName() : gameProfile2.getName();
        gameProfileCache.add(gameProfile);
        CompoundTag compoundTag = this.load(serverPlayer);
        ResourceKey<Level> resourceKey = compoundTag != null ? DimensionType.parseLegacy(new Dynamic((DynamicOps)NbtOps.INSTANCE, (Object)compoundTag.get("Dimension"))).resultOrPartial(((Logger)LOGGER)::error).orElse(Level.OVERWORLD) : Level.OVERWORLD;
        ServerLevel serverLevel2 = this.server.getLevel(resourceKey);
        if (serverLevel2 == null) {
            LOGGER.warn("Unknown respawn dimension {}, defaulting to overworld", resourceKey);
            serverLevel = this.server.overworld();
        } else {
            serverLevel = serverLevel2;
        }
        serverPlayer.setLevel(serverLevel);
        serverPlayer.gameMode.setLevel((ServerLevel)serverPlayer.level);
        String string2 = "local";
        if (connection.getRemoteAddress() != null) {
            string2 = connection.getRemoteAddress().toString();
        }
        LOGGER.info("{}[{}] logged in with entity id {} at ({}, {}, {})", (Object)serverPlayer.getName().getString(), (Object)string2, (Object)serverPlayer.getId(), (Object)serverPlayer.getX(), (Object)serverPlayer.getY(), (Object)serverPlayer.getZ());
        LevelData levelData = serverLevel.getLevelData();
        this.updatePlayerGameMode(serverPlayer, null, serverLevel);
        ServerGamePacketListenerImpl serverGamePacketListenerImpl = new ServerGamePacketListenerImpl(this.server, connection, serverPlayer);
        GameRules gameRules = serverLevel.getGameRules();
        boolean bl = gameRules.getBoolean(GameRules.RULE_DO_IMMEDIATE_RESPAWN);
        boolean bl2 = gameRules.getBoolean(GameRules.RULE_REDUCEDDEBUGINFO);
        serverGamePacketListenerImpl.send(new ClientboundLoginPacket(serverPlayer.getId(), serverPlayer.gameMode.getGameModeForPlayer(), serverPlayer.gameMode.getPreviousGameModeForPlayer(), BiomeManager.obfuscateSeed(serverLevel.getSeed()), levelData.isHardcore(), this.server.levelKeys(), this.registryHolder, serverLevel.dimensionType(), serverLevel.dimension(), this.getMaxPlayers(), this.viewDistance, bl2, !bl, serverLevel.isDebug(), serverLevel.isFlat()));
        serverGamePacketListenerImpl.send(new ClientboundCustomPayloadPacket(ClientboundCustomPayloadPacket.BRAND, new FriendlyByteBuf(Unpooled.buffer()).writeUtf(this.getServer().getServerModName())));
        serverGamePacketListenerImpl.send(new ClientboundChangeDifficultyPacket(levelData.getDifficulty(), levelData.isDifficultyLocked()));
        serverGamePacketListenerImpl.send(new ClientboundPlayerAbilitiesPacket(serverPlayer.abilities));
        serverGamePacketListenerImpl.send(new ClientboundSetCarriedItemPacket(serverPlayer.inventory.selected));
        serverGamePacketListenerImpl.send(new ClientboundUpdateRecipesPacket(this.server.getRecipeManager().getRecipes()));
        serverGamePacketListenerImpl.send(new ClientboundUpdateTagsPacket(this.server.getTags()));
        this.sendPlayerPermissionLevel(serverPlayer);
        serverPlayer.getStats().markAllDirty();
        serverPlayer.getRecipeBook().sendInitialRecipeBook(serverPlayer);
        this.updateEntireScoreboard(serverLevel.getScoreboard(), serverPlayer);
        this.server.invalidateStatus();
        TranslatableComponent translatableComponent = serverPlayer.getGameProfile().getName().equalsIgnoreCase(string) ? new TranslatableComponent("multiplayer.player.joined", serverPlayer.getDisplayName()) : new TranslatableComponent("multiplayer.player.joined.renamed", serverPlayer.getDisplayName(), string);
        this.broadcastMessage(translatableComponent.withStyle(ChatFormatting.YELLOW), ChatType.SYSTEM, Util.NIL_UUID);
        serverGamePacketListenerImpl.teleport(serverPlayer.getX(), serverPlayer.getY(), serverPlayer.getZ(), serverPlayer.yRot, serverPlayer.xRot);
        this.players.add(serverPlayer);
        this.playersByUUID.put(serverPlayer.getUUID(), serverPlayer);
        this.broadcastAll(new ClientboundPlayerInfoPacket(ClientboundPlayerInfoPacket.Action.ADD_PLAYER, serverPlayer));
        for (int i = 0; i < this.players.size(); ++i) {
            serverPlayer.connection.send(new ClientboundPlayerInfoPacket(ClientboundPlayerInfoPacket.Action.ADD_PLAYER, this.players.get(i)));
        }
        serverLevel.addNewPlayer(serverPlayer);
        this.server.getCustomBossEvents().onPlayerConnect(serverPlayer);
        this.sendLevelInfo(serverPlayer, serverLevel);
        if (!this.server.getResourcePack().isEmpty()) {
            serverPlayer.sendTexturePack(this.server.getResourcePack(), this.server.getResourcePackHash());
        }
        Object object2 = serverPlayer.getActiveEffects().iterator();
        while (object2.hasNext()) {
            object = object2.next();
            serverGamePacketListenerImpl.send(new ClientboundUpdateMobEffectPacket(serverPlayer.getId(), (MobEffectInstance)object));
        }
        if (compoundTag != null && compoundTag.contains("RootVehicle", 10) && (object = EntityType.loadEntityRecursive(((CompoundTag)(object2 = compoundTag.getCompound("RootVehicle"))).getCompound("Entity"), serverLevel, entity -> {
            if (!serverLevel.addWithUUID((Entity)entity)) {
                return null;
            }
            return entity;
        })) != null) {
            UUID uUID = ((CompoundTag)object2).hasUUID("Attach") ? ((CompoundTag)object2).getUUID("Attach") : null;
            if (((Entity)object).getUUID().equals(uUID)) {
                serverPlayer.startRiding((Entity)object, true);
            } else {
                for (Entity entity2 : ((Entity)object).getIndirectPassengers()) {
                    if (!entity2.getUUID().equals(uUID)) continue;
                    serverPlayer.startRiding(entity2, true);
                    break;
                }
            }
            if (!serverPlayer.isPassenger()) {
                LOGGER.warn("Couldn't reattach entity to player");
                serverLevel.despawn((Entity)object);
                for (Entity entity2 : ((Entity)object).getIndirectPassengers()) {
                    serverLevel.despawn(entity2);
                }
            }
        }
        serverPlayer.initMenu();
    }

    protected void updateEntireScoreboard(ServerScoreboard serverScoreboard, ServerPlayer serverPlayer) {
        HashSet hashSet = Sets.newHashSet();
        for (PlayerTeam object : serverScoreboard.getPlayerTeams()) {
            serverPlayer.connection.send(new ClientboundSetPlayerTeamPacket(object, 0));
        }
        for (int i = 0; i < 19; ++i) {
            Objective objective = serverScoreboard.getDisplayObjective(i);
            if (objective == null || hashSet.contains(objective)) continue;
            List<Packet<?>> list = serverScoreboard.getStartTrackingPackets(objective);
            for (Packet<?> packet : list) {
                serverPlayer.connection.send(packet);
            }
            hashSet.add(objective);
        }
    }

    public void setLevel(ServerLevel serverLevel) {
        serverLevel.getWorldBorder().addListener(new BorderChangeListener(){

            @Override
            public void onBorderSizeSet(WorldBorder worldBorder, double d) {
                PlayerList.this.broadcastAll(new ClientboundSetBorderPacket(worldBorder, ClientboundSetBorderPacket.Type.SET_SIZE));
            }

            @Override
            public void onBorderSizeLerping(WorldBorder worldBorder, double d, double d2, long l) {
                PlayerList.this.broadcastAll(new ClientboundSetBorderPacket(worldBorder, ClientboundSetBorderPacket.Type.LERP_SIZE));
            }

            @Override
            public void onBorderCenterSet(WorldBorder worldBorder, double d, double d2) {
                PlayerList.this.broadcastAll(new ClientboundSetBorderPacket(worldBorder, ClientboundSetBorderPacket.Type.SET_CENTER));
            }

            @Override
            public void onBorderSetWarningTime(WorldBorder worldBorder, int n) {
                PlayerList.this.broadcastAll(new ClientboundSetBorderPacket(worldBorder, ClientboundSetBorderPacket.Type.SET_WARNING_TIME));
            }

            @Override
            public void onBorderSetWarningBlocks(WorldBorder worldBorder, int n) {
                PlayerList.this.broadcastAll(new ClientboundSetBorderPacket(worldBorder, ClientboundSetBorderPacket.Type.SET_WARNING_BLOCKS));
            }

            @Override
            public void onBorderSetDamagePerBlock(WorldBorder worldBorder, double d) {
            }

            @Override
            public void onBorderSetDamageSafeZOne(WorldBorder worldBorder, double d) {
            }
        });
    }

    @Nullable
    public CompoundTag load(ServerPlayer serverPlayer) {
        CompoundTag compoundTag;
        CompoundTag compoundTag2 = this.server.getWorldData().getLoadedPlayerTag();
        if (serverPlayer.getName().getString().equals(this.server.getSingleplayerName()) && compoundTag2 != null) {
            compoundTag = compoundTag2;
            serverPlayer.load(compoundTag);
            LOGGER.debug("loading single player");
        } else {
            compoundTag = this.playerIo.load(serverPlayer);
        }
        return compoundTag;
    }

    protected void save(ServerPlayer serverPlayer) {
        PlayerAdvancements playerAdvancements;
        this.playerIo.save(serverPlayer);
        ServerStatsCounter serverStatsCounter = this.stats.get(serverPlayer.getUUID());
        if (serverStatsCounter != null) {
            serverStatsCounter.save();
        }
        if ((playerAdvancements = this.advancements.get(serverPlayer.getUUID())) != null) {
            playerAdvancements.save();
        }
    }

    public void remove(ServerPlayer serverPlayer) {
        Object object;
        ServerLevel serverLevel = serverPlayer.getLevel();
        serverPlayer.awardStat(Stats.LEAVE_GAME);
        this.save(serverPlayer);
        if (serverPlayer.isPassenger() && ((Entity)(object = serverPlayer.getRootVehicle())).hasOnePlayerPassenger()) {
            LOGGER.debug("Removing player mount");
            serverPlayer.stopRiding();
            serverLevel.despawn((Entity)object);
            ((Entity)object).removed = true;
            for (Entity entity : ((Entity)object).getIndirectPassengers()) {
                serverLevel.despawn(entity);
                entity.removed = true;
            }
            serverLevel.getChunk(serverPlayer.xChunk, serverPlayer.zChunk).markUnsaved();
        }
        serverPlayer.unRide();
        serverLevel.removePlayerImmediately(serverPlayer);
        serverPlayer.getAdvancements().stopListening();
        this.players.remove(serverPlayer);
        this.server.getCustomBossEvents().onPlayerDisconnect(serverPlayer);
        object = serverPlayer.getUUID();
        ServerPlayer serverPlayer2 = this.playersByUUID.get(object);
        if (serverPlayer2 == serverPlayer) {
            this.playersByUUID.remove(object);
            this.stats.remove(object);
            this.advancements.remove(object);
        }
        this.broadcastAll(new ClientboundPlayerInfoPacket(ClientboundPlayerInfoPacket.Action.REMOVE_PLAYER, serverPlayer));
    }

    @Nullable
    public Component canPlayerLogin(SocketAddress socketAddress, GameProfile gameProfile) {
        if (this.bans.isBanned(gameProfile)) {
            UserBanListEntry userBanListEntry = (UserBanListEntry)this.bans.get(gameProfile);
            TranslatableComponent translatableComponent = new TranslatableComponent("multiplayer.disconnect.banned.reason", userBanListEntry.getReason());
            if (userBanListEntry.getExpires() != null) {
                translatableComponent.append(new TranslatableComponent("multiplayer.disconnect.banned.expiration", BAN_DATE_FORMAT.format(userBanListEntry.getExpires())));
            }
            return translatableComponent;
        }
        if (!this.isWhiteListed(gameProfile)) {
            return new TranslatableComponent("multiplayer.disconnect.not_whitelisted");
        }
        if (this.ipBans.isBanned(socketAddress)) {
            IpBanListEntry ipBanListEntry = this.ipBans.get(socketAddress);
            TranslatableComponent translatableComponent = new TranslatableComponent("multiplayer.disconnect.banned_ip.reason", ipBanListEntry.getReason());
            if (ipBanListEntry.getExpires() != null) {
                translatableComponent.append(new TranslatableComponent("multiplayer.disconnect.banned_ip.expiration", BAN_DATE_FORMAT.format(ipBanListEntry.getExpires())));
            }
            return translatableComponent;
        }
        if (this.players.size() >= this.maxPlayers && !this.canBypassPlayerLimit(gameProfile)) {
            return new TranslatableComponent("multiplayer.disconnect.server_full");
        }
        return null;
    }

    public ServerPlayer getPlayerForLogin(GameProfile gameProfile) {
        Object object3;
        Object object2;
        UUID uUID = Player.createPlayerUUID(gameProfile);
        ArrayList arrayList = Lists.newArrayList();
        for (int i = 0; i < this.players.size(); ++i) {
            object2 = this.players.get(i);
            if (!((Entity)object2).getUUID().equals(uUID)) continue;
            arrayList.add(object2);
        }
        ServerPlayer serverPlayer = this.playersByUUID.get(gameProfile.getId());
        if (serverPlayer != null && !arrayList.contains(serverPlayer)) {
            arrayList.add(serverPlayer);
        }
        for (Object object3 : arrayList) {
            ((ServerPlayer)object3).connection.disconnect(new TranslatableComponent("multiplayer.disconnect.duplicate_login"));
        }
        object3 = this.server.overworld();
        object2 = this.server.isDemo() ? new DemoMode((ServerLevel)object3) : new ServerPlayerGameMode((ServerLevel)object3);
        return new ServerPlayer(this.server, (ServerLevel)object3, gameProfile, (ServerPlayerGameMode)object2);
    }

    public ServerPlayer respawn(ServerPlayer serverPlayer, boolean bl) {
        this.players.remove(serverPlayer);
        serverPlayer.getLevel().removePlayerImmediately(serverPlayer);
        BlockPos blockPos = serverPlayer.getRespawnPosition();
        float f = serverPlayer.getRespawnAngle();
        boolean bl2 = serverPlayer.isRespawnForced();
        ServerLevel serverLevel = this.server.getLevel(serverPlayer.getRespawnDimension());
        Optional<Object> optional = serverLevel != null && blockPos != null ? Player.findRespawnPositionAndUseSpawnBlock(serverLevel, blockPos, f, bl2, bl) : Optional.empty();
        ServerLevel serverLevel2 = serverLevel != null && optional.isPresent() ? serverLevel : this.server.overworld();
        ServerPlayerGameMode serverPlayerGameMode = this.server.isDemo() ? new DemoMode(serverLevel2) : new ServerPlayerGameMode(serverLevel2);
        ServerPlayer serverPlayer2 = new ServerPlayer(this.server, serverLevel2, serverPlayer.getGameProfile(), serverPlayerGameMode);
        serverPlayer2.connection = serverPlayer.connection;
        serverPlayer2.restoreFrom(serverPlayer, bl);
        serverPlayer2.setId(serverPlayer.getId());
        serverPlayer2.setMainArm(serverPlayer.getMainArm());
        for (String object2 : serverPlayer.getTags()) {
            serverPlayer2.addTag(object2);
        }
        this.updatePlayerGameMode(serverPlayer2, serverPlayer, serverLevel2);
        boolean bl3 = false;
        if (optional.isPresent()) {
            float f2;
            BlockState blockState = serverLevel2.getBlockState(blockPos);
            boolean bl4 = blockState.is(Blocks.RESPAWN_ANCHOR);
            Vec3 vec3 = (Vec3)optional.get();
            if (blockState.is(BlockTags.BEDS) || bl4) {
                Vec3 vec32 = Vec3.atBottomCenterOf(blockPos).subtract(vec3).normalize();
                f2 = (float)Mth.wrapDegrees(Mth.atan2(vec32.z, vec32.x) * 57.2957763671875 - 90.0);
            } else {
                f2 = f;
            }
            serverPlayer2.moveTo(vec3.x, vec3.y, vec3.z, f2, 0.0f);
            serverPlayer2.setRespawnPosition(serverLevel2.dimension(), blockPos, f, bl2, false);
            bl3 = !bl && bl4;
        } else if (blockPos != null) {
            serverPlayer2.connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.NO_RESPAWN_BLOCK_AVAILABLE, 0.0f));
        }
        while (!serverLevel2.noCollision(serverPlayer2) && serverPlayer2.getY() < 256.0) {
            serverPlayer2.setPos(serverPlayer2.getX(), serverPlayer2.getY() + 1.0, serverPlayer2.getZ());
        }
        LevelData levelData = serverPlayer2.level.getLevelData();
        serverPlayer2.connection.send(new ClientboundRespawnPacket(serverPlayer2.level.dimensionType(), serverPlayer2.level.dimension(), BiomeManager.obfuscateSeed(serverPlayer2.getLevel().getSeed()), serverPlayer2.gameMode.getGameModeForPlayer(), serverPlayer2.gameMode.getPreviousGameModeForPlayer(), serverPlayer2.getLevel().isDebug(), serverPlayer2.getLevel().isFlat(), bl));
        serverPlayer2.connection.teleport(serverPlayer2.getX(), serverPlayer2.getY(), serverPlayer2.getZ(), serverPlayer2.yRot, serverPlayer2.xRot);
        serverPlayer2.connection.send(new ClientboundSetDefaultSpawnPositionPacket(serverLevel2.getSharedSpawnPos(), serverLevel2.getSharedSpawnAngle()));
        serverPlayer2.connection.send(new ClientboundChangeDifficultyPacket(levelData.getDifficulty(), levelData.isDifficultyLocked()));
        serverPlayer2.connection.send(new ClientboundSetExperiencePacket(serverPlayer2.experienceProgress, serverPlayer2.totalExperience, serverPlayer2.experienceLevel));
        this.sendLevelInfo(serverPlayer2, serverLevel2);
        this.sendPlayerPermissionLevel(serverPlayer2);
        serverLevel2.addRespawnedPlayer(serverPlayer2);
        this.players.add(serverPlayer2);
        this.playersByUUID.put(serverPlayer2.getUUID(), serverPlayer2);
        serverPlayer2.initMenu();
        serverPlayer2.setHealth(serverPlayer2.getHealth());
        if (bl3) {
            serverPlayer2.connection.send(new ClientboundSoundPacket(SoundEvents.RESPAWN_ANCHOR_DEPLETE, SoundSource.BLOCKS, blockPos.getX(), blockPos.getY(), blockPos.getZ(), 1.0f, 1.0f));
        }
        return serverPlayer2;
    }

    public void sendPlayerPermissionLevel(ServerPlayer serverPlayer) {
        GameProfile gameProfile = serverPlayer.getGameProfile();
        int n = this.server.getProfilePermissions(gameProfile);
        this.sendPlayerPermissionLevel(serverPlayer, n);
    }

    public void tick() {
        if (++this.sendAllPlayerInfoIn > 600) {
            this.broadcastAll(new ClientboundPlayerInfoPacket(ClientboundPlayerInfoPacket.Action.UPDATE_LATENCY, this.players));
            this.sendAllPlayerInfoIn = 0;
        }
    }

    public void broadcastAll(Packet<?> packet) {
        for (int i = 0; i < this.players.size(); ++i) {
            this.players.get((int)i).connection.send(packet);
        }
    }

    public void broadcastAll(Packet<?> packet, ResourceKey<Level> resourceKey) {
        for (int i = 0; i < this.players.size(); ++i) {
            ServerPlayer serverPlayer = this.players.get(i);
            if (serverPlayer.level.dimension() != resourceKey) continue;
            serverPlayer.connection.send(packet);
        }
    }

    public void broadcastToTeam(Player player, Component component) {
        Team team = player.getTeam();
        if (team == null) {
            return;
        }
        Collection<String> collection = team.getPlayers();
        for (String string : collection) {
            ServerPlayer serverPlayer = this.getPlayerByName(string);
            if (serverPlayer == null || serverPlayer == player) continue;
            serverPlayer.sendMessage(component, player.getUUID());
        }
    }

    public void broadcastToAllExceptTeam(Player player, Component component) {
        Team team = player.getTeam();
        if (team == null) {
            this.broadcastMessage(component, ChatType.SYSTEM, player.getUUID());
            return;
        }
        for (int i = 0; i < this.players.size(); ++i) {
            ServerPlayer serverPlayer = this.players.get(i);
            if (serverPlayer.getTeam() == team) continue;
            serverPlayer.sendMessage(component, player.getUUID());
        }
    }

    public String[] getPlayerNamesArray() {
        String[] arrstring = new String[this.players.size()];
        for (int i = 0; i < this.players.size(); ++i) {
            arrstring[i] = this.players.get(i).getGameProfile().getName();
        }
        return arrstring;
    }

    public UserBanList getBans() {
        return this.bans;
    }

    public IpBanList getIpBans() {
        return this.ipBans;
    }

    public void op(GameProfile gameProfile) {
        this.ops.add(new ServerOpListEntry(gameProfile, this.server.getOperatorUserPermissionLevel(), this.ops.canBypassPlayerLimit(gameProfile)));
        ServerPlayer serverPlayer = this.getPlayer(gameProfile.getId());
        if (serverPlayer != null) {
            this.sendPlayerPermissionLevel(serverPlayer);
        }
    }

    public void deop(GameProfile gameProfile) {
        this.ops.remove(gameProfile);
        ServerPlayer serverPlayer = this.getPlayer(gameProfile.getId());
        if (serverPlayer != null) {
            this.sendPlayerPermissionLevel(serverPlayer);
        }
    }

    private void sendPlayerPermissionLevel(ServerPlayer serverPlayer, int n) {
        if (serverPlayer.connection != null) {
            byte by = n <= 0 ? (byte)24 : (n >= 4 ? (byte)28 : (byte)((byte)(24 + n)));
            serverPlayer.connection.send(new ClientboundEntityEventPacket(serverPlayer, by));
        }
        this.server.getCommands().sendCommands(serverPlayer);
    }

    public boolean isWhiteListed(GameProfile gameProfile) {
        return !this.doWhiteList || this.ops.contains(gameProfile) || this.whitelist.contains(gameProfile);
    }

    public boolean isOp(GameProfile gameProfile) {
        return this.ops.contains(gameProfile) || this.server.isSingleplayerOwner(gameProfile) && this.server.getWorldData().getAllowCommands() || this.allowCheatsForAllPlayers;
    }

    @Nullable
    public ServerPlayer getPlayerByName(String string) {
        for (ServerPlayer serverPlayer : this.players) {
            if (!serverPlayer.getGameProfile().getName().equalsIgnoreCase(string)) continue;
            return serverPlayer;
        }
        return null;
    }

    public void broadcast(@Nullable Player player, double d, double d2, double d3, double d4, ResourceKey<Level> resourceKey, Packet<?> packet) {
        for (int i = 0; i < this.players.size(); ++i) {
            double d5;
            double d6;
            double d7;
            ServerPlayer serverPlayer = this.players.get(i);
            if (serverPlayer == player || serverPlayer.level.dimension() != resourceKey || !((d6 = d - serverPlayer.getX()) * d6 + (d5 = d2 - serverPlayer.getY()) * d5 + (d7 = d3 - serverPlayer.getZ()) * d7 < d4 * d4)) continue;
            serverPlayer.connection.send(packet);
        }
    }

    public void saveAll() {
        for (int i = 0; i < this.players.size(); ++i) {
            this.save(this.players.get(i));
        }
    }

    public UserWhiteList getWhiteList() {
        return this.whitelist;
    }

    public String[] getWhiteListNames() {
        return this.whitelist.getUserList();
    }

    public ServerOpList getOps() {
        return this.ops;
    }

    public String[] getOpNames() {
        return this.ops.getUserList();
    }

    public void reloadWhiteList() {
    }

    public void sendLevelInfo(ServerPlayer serverPlayer, ServerLevel serverLevel) {
        WorldBorder worldBorder = this.server.overworld().getWorldBorder();
        serverPlayer.connection.send(new ClientboundSetBorderPacket(worldBorder, ClientboundSetBorderPacket.Type.INITIALIZE));
        serverPlayer.connection.send(new ClientboundSetTimePacket(serverLevel.getGameTime(), serverLevel.getDayTime(), serverLevel.getGameRules().getBoolean(GameRules.RULE_DAYLIGHT)));
        serverPlayer.connection.send(new ClientboundSetDefaultSpawnPositionPacket(serverLevel.getSharedSpawnPos(), serverLevel.getSharedSpawnAngle()));
        if (serverLevel.isRaining()) {
            serverPlayer.connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.START_RAINING, 0.0f));
            serverPlayer.connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.RAIN_LEVEL_CHANGE, serverLevel.getRainLevel(1.0f)));
            serverPlayer.connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.THUNDER_LEVEL_CHANGE, serverLevel.getThunderLevel(1.0f)));
        }
    }

    public void sendAllPlayerInfo(ServerPlayer serverPlayer) {
        serverPlayer.refreshContainer(serverPlayer.inventoryMenu);
        serverPlayer.resetSentInfo();
        serverPlayer.connection.send(new ClientboundSetCarriedItemPacket(serverPlayer.inventory.selected));
    }

    public int getPlayerCount() {
        return this.players.size();
    }

    public int getMaxPlayers() {
        return this.maxPlayers;
    }

    public boolean isUsingWhitelist() {
        return this.doWhiteList;
    }

    public void setUsingWhiteList(boolean bl) {
        this.doWhiteList = bl;
    }

    public List<ServerPlayer> getPlayersWithAddress(String string) {
        ArrayList arrayList = Lists.newArrayList();
        for (ServerPlayer serverPlayer : this.players) {
            if (!serverPlayer.getIpAddress().equals(string)) continue;
            arrayList.add(serverPlayer);
        }
        return arrayList;
    }

    public int getViewDistance() {
        return this.viewDistance;
    }

    public MinecraftServer getServer() {
        return this.server;
    }

    public CompoundTag getSingleplayerData() {
        return null;
    }

    public void setOverrideGameMode(GameType gameType) {
        this.overrideGameMode = gameType;
    }

    private void updatePlayerGameMode(ServerPlayer serverPlayer, @Nullable ServerPlayer serverPlayer2, ServerLevel serverLevel) {
        if (serverPlayer2 != null) {
            serverPlayer.gameMode.setGameModeForPlayer(serverPlayer2.gameMode.getGameModeForPlayer(), serverPlayer2.gameMode.getPreviousGameModeForPlayer());
        } else if (this.overrideGameMode != null) {
            serverPlayer.gameMode.setGameModeForPlayer(this.overrideGameMode, GameType.NOT_SET);
        }
        serverPlayer.gameMode.updateGameMode(serverLevel.getServer().getWorldData().getGameType());
    }

    public void setAllowCheatsForAllPlayers(boolean bl) {
        this.allowCheatsForAllPlayers = bl;
    }

    public void removeAll() {
        for (int i = 0; i < this.players.size(); ++i) {
            this.players.get((int)i).connection.disconnect(new TranslatableComponent("multiplayer.disconnect.server_shutdown"));
        }
    }

    public void broadcastMessage(Component component, ChatType chatType, UUID uUID) {
        this.server.sendMessage(component, uUID);
        this.broadcastAll(new ClientboundChatPacket(component, chatType, uUID));
    }

    public ServerStatsCounter getPlayerStats(Player player) {
        ServerStatsCounter serverStatsCounter;
        UUID uUID = player.getUUID();
        ServerStatsCounter serverStatsCounter2 = serverStatsCounter = uUID == null ? null : this.stats.get(uUID);
        if (serverStatsCounter == null) {
            File file;
            File file2 = this.server.getWorldPath(LevelResource.PLAYER_STATS_DIR).toFile();
            File file3 = new File(file2, uUID + ".json");
            if (!file3.exists() && (file = new File(file2, player.getName().getString() + ".json")).exists() && file.isFile()) {
                file.renameTo(file3);
            }
            serverStatsCounter = new ServerStatsCounter(this.server, file3);
            this.stats.put(uUID, serverStatsCounter);
        }
        return serverStatsCounter;
    }

    public PlayerAdvancements getPlayerAdvancements(ServerPlayer serverPlayer) {
        UUID uUID = serverPlayer.getUUID();
        PlayerAdvancements playerAdvancements = this.advancements.get(uUID);
        if (playerAdvancements == null) {
            File file = this.server.getWorldPath(LevelResource.PLAYER_ADVANCEMENTS_DIR).toFile();
            File file2 = new File(file, uUID + ".json");
            playerAdvancements = new PlayerAdvancements(this.server.getFixerUpper(), this, this.server.getAdvancements(), file2, serverPlayer);
            this.advancements.put(uUID, playerAdvancements);
        }
        playerAdvancements.setPlayer(serverPlayer);
        return playerAdvancements;
    }

    public void setViewDistance(int n) {
        this.viewDistance = n;
        this.broadcastAll(new ClientboundSetChunkCacheRadiusPacket(n));
        for (ServerLevel serverLevel : this.server.getAllLevels()) {
            if (serverLevel == null) continue;
            serverLevel.getChunkSource().setViewDistance(n);
        }
    }

    public List<ServerPlayer> getPlayers() {
        return this.players;
    }

    @Nullable
    public ServerPlayer getPlayer(UUID uUID) {
        return this.playersByUUID.get(uUID);
    }

    public boolean canBypassPlayerLimit(GameProfile gameProfile) {
        return false;
    }

    public void reloadResources() {
        for (PlayerAdvancements object : this.advancements.values()) {
            object.reload(this.server.getAdvancements());
        }
        this.broadcastAll(new ClientboundUpdateTagsPacket(this.server.getTags()));
        ClientboundUpdateRecipesPacket clientboundUpdateRecipesPacket = new ClientboundUpdateRecipesPacket(this.server.getRecipeManager().getRecipes());
        for (ServerPlayer serverPlayer : this.players) {
            serverPlayer.connection.send(clientboundUpdateRecipesPacket);
            serverPlayer.getRecipeBook().sendInitialRecipeBook(serverPlayer);
        }
    }

    public boolean isAllowCheatsForAllPlayers() {
        return this.allowCheatsForAllPlayers;
    }

}

