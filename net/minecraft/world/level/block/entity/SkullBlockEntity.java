/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Iterables
 *  com.mojang.authlib.GameProfile
 *  com.mojang.authlib.minecraft.MinecraftSessionService
 *  com.mojang.authlib.properties.Property
 *  com.mojang.authlib.properties.PropertyMap
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.block.entity;

import com.google.common.collect.Iterables;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import java.util.Collection;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.players.GameProfileCache;
import net.minecraft.util.StringUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.TickableBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class SkullBlockEntity
extends BlockEntity
implements TickableBlockEntity {
    @Nullable
    private static GameProfileCache profileCache;
    @Nullable
    private static MinecraftSessionService sessionService;
    @Nullable
    private GameProfile owner;
    private int mouthTickCount;
    private boolean isMovingMouth;

    public SkullBlockEntity() {
        super(BlockEntityType.SKULL);
    }

    public static void setProfileCache(GameProfileCache gameProfileCache) {
        profileCache = gameProfileCache;
    }

    public static void setSessionService(MinecraftSessionService minecraftSessionService) {
        sessionService = minecraftSessionService;
    }

    @Override
    public CompoundTag save(CompoundTag compoundTag) {
        super.save(compoundTag);
        if (this.owner != null) {
            CompoundTag compoundTag2 = new CompoundTag();
            NbtUtils.writeGameProfile(compoundTag2, this.owner);
            compoundTag.put("SkullOwner", compoundTag2);
        }
        return compoundTag;
    }

    @Override
    public void load(BlockState blockState, CompoundTag compoundTag) {
        String string;
        super.load(blockState, compoundTag);
        if (compoundTag.contains("SkullOwner", 10)) {
            this.setOwner(NbtUtils.readGameProfile(compoundTag.getCompound("SkullOwner")));
        } else if (compoundTag.contains("ExtraType", 8) && !StringUtil.isNullOrEmpty(string = compoundTag.getString("ExtraType"))) {
            this.setOwner(new GameProfile(null, string));
        }
    }

    @Override
    public void tick() {
        BlockState blockState = this.getBlockState();
        if (blockState.is(Blocks.DRAGON_HEAD) || blockState.is(Blocks.DRAGON_WALL_HEAD)) {
            if (this.level.hasNeighborSignal(this.worldPosition)) {
                this.isMovingMouth = true;
                ++this.mouthTickCount;
            } else {
                this.isMovingMouth = false;
            }
        }
    }

    public float getMouthAnimation(float f) {
        if (this.isMovingMouth) {
            return (float)this.mouthTickCount + f;
        }
        return this.mouthTickCount;
    }

    @Nullable
    public GameProfile getOwnerProfile() {
        return this.owner;
    }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return new ClientboundBlockEntityDataPacket(this.worldPosition, 4, this.getUpdateTag());
    }

    @Override
    public CompoundTag getUpdateTag() {
        return this.save(new CompoundTag());
    }

    public void setOwner(@Nullable GameProfile gameProfile) {
        this.owner = gameProfile;
        this.updateOwnerProfile();
    }

    private void updateOwnerProfile() {
        this.owner = SkullBlockEntity.updateGameprofile(this.owner);
        this.setChanged();
    }

    @Nullable
    public static GameProfile updateGameprofile(@Nullable GameProfile gameProfile) {
        if (gameProfile == null || StringUtil.isNullOrEmpty(gameProfile.getName())) {
            return gameProfile;
        }
        if (gameProfile.isComplete() && gameProfile.getProperties().containsKey((Object)"textures")) {
            return gameProfile;
        }
        if (profileCache == null || sessionService == null) {
            return gameProfile;
        }
        GameProfile gameProfile2 = profileCache.get(gameProfile.getName());
        if (gameProfile2 == null) {
            return gameProfile;
        }
        Property property = (Property)Iterables.getFirst((Iterable)gameProfile2.getProperties().get((Object)"textures"), null);
        if (property == null) {
            gameProfile2 = sessionService.fillProfileProperties(gameProfile2, true);
        }
        return gameProfile2;
    }
}

