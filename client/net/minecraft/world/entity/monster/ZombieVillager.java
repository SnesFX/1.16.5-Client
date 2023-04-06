/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.DynamicOps
 *  javax.annotation.Nullable
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.world.entity.monster;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.critereon.CuredZombieVillagerTrigger;
import net.minecraft.core.BlockPos;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ReputationEventHandler;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.village.ReputationEventType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.npc.VillagerDataHolder;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerType;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.logging.log4j.Logger;

public class ZombieVillager
extends Zombie
implements VillagerDataHolder {
    private static final EntityDataAccessor<Boolean> DATA_CONVERTING_ID = SynchedEntityData.defineId(ZombieVillager.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<VillagerData> DATA_VILLAGER_DATA = SynchedEntityData.defineId(ZombieVillager.class, EntityDataSerializers.VILLAGER_DATA);
    private int villagerConversionTime;
    private UUID conversionStarter;
    private Tag gossips;
    private CompoundTag tradeOffers;
    private int villagerXp;

    public ZombieVillager(EntityType<? extends ZombieVillager> entityType, Level level) {
        super(entityType, level);
        this.setVillagerData(this.getVillagerData().setProfession(Registry.VILLAGER_PROFESSION.getRandom(this.random)));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_CONVERTING_ID, false);
        this.entityData.define(DATA_VILLAGER_DATA, new VillagerData(VillagerType.PLAINS, VillagerProfession.NONE, 1));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        VillagerData.CODEC.encodeStart((DynamicOps)NbtOps.INSTANCE, (Object)this.getVillagerData()).resultOrPartial(((Logger)LOGGER)::error).ifPresent(tag -> compoundTag.put("VillagerData", (Tag)tag));
        if (this.tradeOffers != null) {
            compoundTag.put("Offers", this.tradeOffers);
        }
        if (this.gossips != null) {
            compoundTag.put("Gossips", this.gossips);
        }
        compoundTag.putInt("ConversionTime", this.isConverting() ? this.villagerConversionTime : -1);
        if (this.conversionStarter != null) {
            compoundTag.putUUID("ConversionPlayer", this.conversionStarter);
        }
        compoundTag.putInt("Xp", this.villagerXp);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        if (compoundTag.contains("VillagerData", 10)) {
            DataResult dataResult = VillagerData.CODEC.parse(new Dynamic((DynamicOps)NbtOps.INSTANCE, (Object)compoundTag.get("VillagerData")));
            dataResult.resultOrPartial(((Logger)LOGGER)::error).ifPresent(this::setVillagerData);
        }
        if (compoundTag.contains("Offers", 10)) {
            this.tradeOffers = compoundTag.getCompound("Offers");
        }
        if (compoundTag.contains("Gossips", 10)) {
            this.gossips = compoundTag.getList("Gossips", 10);
        }
        if (compoundTag.contains("ConversionTime", 99) && compoundTag.getInt("ConversionTime") > -1) {
            this.startConverting(compoundTag.hasUUID("ConversionPlayer") ? compoundTag.getUUID("ConversionPlayer") : null, compoundTag.getInt("ConversionTime"));
        }
        if (compoundTag.contains("Xp", 3)) {
            this.villagerXp = compoundTag.getInt("Xp");
        }
    }

    @Override
    public void tick() {
        if (!this.level.isClientSide && this.isAlive() && this.isConverting()) {
            int n = this.getConversionProgress();
            this.villagerConversionTime -= n;
            if (this.villagerConversionTime <= 0) {
                this.finishConversion((ServerLevel)this.level);
            }
        }
        super.tick();
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand interactionHand) {
        ItemStack itemStack = player.getItemInHand(interactionHand);
        if (itemStack.getItem() == Items.GOLDEN_APPLE) {
            if (this.hasEffect(MobEffects.WEAKNESS)) {
                if (!player.abilities.instabuild) {
                    itemStack.shrink(1);
                }
                if (!this.level.isClientSide) {
                    this.startConverting(player.getUUID(), this.random.nextInt(2401) + 3600);
                }
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.CONSUME;
        }
        return super.mobInteract(player, interactionHand);
    }

    @Override
    protected boolean convertsInWater() {
        return false;
    }

    @Override
    public boolean removeWhenFarAway(double d) {
        return !this.isConverting() && this.villagerXp == 0;
    }

    public boolean isConverting() {
        return this.getEntityData().get(DATA_CONVERTING_ID);
    }

    private void startConverting(@Nullable UUID uUID, int n) {
        this.conversionStarter = uUID;
        this.villagerConversionTime = n;
        this.getEntityData().set(DATA_CONVERTING_ID, true);
        this.removeEffect(MobEffects.WEAKNESS);
        this.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, n, Math.min(this.level.getDifficulty().getId() - 1, 0)));
        this.level.broadcastEntityEvent(this, (byte)16);
    }

    @Override
    public void handleEntityEvent(byte by) {
        if (by == 16) {
            if (!this.isSilent()) {
                this.level.playLocalSound(this.getX(), this.getEyeY(), this.getZ(), SoundEvents.ZOMBIE_VILLAGER_CURE, this.getSoundSource(), 1.0f + this.random.nextFloat(), this.random.nextFloat() * 0.7f + 0.3f, false);
            }
            return;
        }
        super.handleEntityEvent(by);
    }

    private void finishConversion(ServerLevel serverLevel) {
        Villager villager = this.convertTo(EntityType.VILLAGER, false);
        Object object = EquipmentSlot.values();
        int n = ((EquipmentSlot[])object).length;
        for (int i = 0; i < n; ++i) {
            EquipmentSlot equipmentSlot = object[i];
            ItemStack itemStack = this.getItemBySlot(equipmentSlot);
            if (itemStack.isEmpty()) continue;
            if (EnchantmentHelper.hasBindingCurse(itemStack)) {
                villager.setSlot(equipmentSlot.getIndex() + 300, itemStack);
                continue;
            }
            double d = this.getEquipmentDropChance(equipmentSlot);
            if (!(d > 1.0)) continue;
            this.spawnAtLocation(itemStack);
        }
        villager.setVillagerData(this.getVillagerData());
        if (this.gossips != null) {
            villager.setGossips(this.gossips);
        }
        if (this.tradeOffers != null) {
            villager.setOffers(new MerchantOffers(this.tradeOffers));
        }
        villager.setVillagerXp(this.villagerXp);
        villager.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(villager.blockPosition()), MobSpawnType.CONVERSION, null, null);
        if (this.conversionStarter != null && (object = serverLevel.getPlayerByUUID(this.conversionStarter)) instanceof ServerPlayer) {
            CriteriaTriggers.CURED_ZOMBIE_VILLAGER.trigger((ServerPlayer)object, this, villager);
            serverLevel.onReputationEvent(ReputationEventType.ZOMBIE_VILLAGER_CURED, (Entity)object, villager);
        }
        villager.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 200, 0));
        if (!this.isSilent()) {
            serverLevel.levelEvent(null, 1027, this.blockPosition(), 0);
        }
    }

    private int getConversionProgress() {
        int n = 1;
        if (this.random.nextFloat() < 0.01f) {
            int n2 = 0;
            BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
            for (int i = (int)this.getX() - 4; i < (int)this.getX() + 4 && n2 < 14; ++i) {
                for (int j = (int)this.getY() - 4; j < (int)this.getY() + 4 && n2 < 14; ++j) {
                    for (int k = (int)this.getZ() - 4; k < (int)this.getZ() + 4 && n2 < 14; ++k) {
                        Block block = this.level.getBlockState(mutableBlockPos.set(i, j, k)).getBlock();
                        if (block != Blocks.IRON_BARS && !(block instanceof BedBlock)) continue;
                        if (this.random.nextFloat() < 0.3f) {
                            ++n;
                        }
                        ++n2;
                    }
                }
            }
        }
        return n;
    }

    @Override
    protected float getVoicePitch() {
        if (this.isBaby()) {
            return (this.random.nextFloat() - this.random.nextFloat()) * 0.2f + 2.0f;
        }
        return (this.random.nextFloat() - this.random.nextFloat()) * 0.2f + 1.0f;
    }

    @Override
    public SoundEvent getAmbientSound() {
        return SoundEvents.ZOMBIE_VILLAGER_AMBIENT;
    }

    @Override
    public SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.ZOMBIE_VILLAGER_HURT;
    }

    @Override
    public SoundEvent getDeathSound() {
        return SoundEvents.ZOMBIE_VILLAGER_DEATH;
    }

    @Override
    public SoundEvent getStepSound() {
        return SoundEvents.ZOMBIE_VILLAGER_STEP;
    }

    @Override
    protected ItemStack getSkull() {
        return ItemStack.EMPTY;
    }

    public void setTradeOffers(CompoundTag compoundTag) {
        this.tradeOffers = compoundTag;
    }

    public void setGossips(Tag tag) {
        this.gossips = tag;
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor serverLevelAccessor, DifficultyInstance difficultyInstance, MobSpawnType mobSpawnType, @Nullable SpawnGroupData spawnGroupData, @Nullable CompoundTag compoundTag) {
        this.setVillagerData(this.getVillagerData().setType(VillagerType.byBiome(serverLevelAccessor.getBiomeName(this.blockPosition()))));
        return super.finalizeSpawn(serverLevelAccessor, difficultyInstance, mobSpawnType, spawnGroupData, compoundTag);
    }

    public void setVillagerData(VillagerData villagerData) {
        VillagerData villagerData2 = this.getVillagerData();
        if (villagerData2.getProfession() != villagerData.getProfession()) {
            this.tradeOffers = null;
        }
        this.entityData.set(DATA_VILLAGER_DATA, villagerData);
    }

    @Override
    public VillagerData getVillagerData() {
        return this.entityData.get(DATA_VILLAGER_DATA);
    }

    public void setVillagerXp(int n) {
        this.villagerXp = n;
    }
}

