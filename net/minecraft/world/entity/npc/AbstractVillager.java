/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Sets
 *  javax.annotation.Nullable
 */
package net.minecraft.world.entity.npc;

import com.google.common.collect.Sets;
import java.util.HashSet;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.critereon.TradeTrigger;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.npc.Npc;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.Merchant;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public abstract class AbstractVillager
extends AgableMob
implements Npc,
Merchant {
    private static final EntityDataAccessor<Integer> DATA_UNHAPPY_COUNTER = SynchedEntityData.defineId(AbstractVillager.class, EntityDataSerializers.INT);
    @Nullable
    private Player tradingPlayer;
    @Nullable
    protected MerchantOffers offers;
    private final SimpleContainer inventory = new SimpleContainer(8);

    public AbstractVillager(EntityType<? extends AbstractVillager> entityType, Level level) {
        super(entityType, level);
        this.setPathfindingMalus(BlockPathTypes.DANGER_FIRE, 16.0f);
        this.setPathfindingMalus(BlockPathTypes.DAMAGE_FIRE, -1.0f);
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor serverLevelAccessor, DifficultyInstance difficultyInstance, MobSpawnType mobSpawnType, @Nullable SpawnGroupData spawnGroupData, @Nullable CompoundTag compoundTag) {
        if (spawnGroupData == null) {
            spawnGroupData = new AgableMob.AgableMobGroupData(false);
        }
        return super.finalizeSpawn(serverLevelAccessor, difficultyInstance, mobSpawnType, spawnGroupData, compoundTag);
    }

    public int getUnhappyCounter() {
        return this.entityData.get(DATA_UNHAPPY_COUNTER);
    }

    public void setUnhappyCounter(int n) {
        this.entityData.set(DATA_UNHAPPY_COUNTER, n);
    }

    @Override
    public int getVillagerXp() {
        return 0;
    }

    @Override
    protected float getStandingEyeHeight(Pose pose, EntityDimensions entityDimensions) {
        if (this.isBaby()) {
            return 0.81f;
        }
        return 1.62f;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_UNHAPPY_COUNTER, 0);
    }

    @Override
    public void setTradingPlayer(@Nullable Player player) {
        this.tradingPlayer = player;
    }

    @Nullable
    @Override
    public Player getTradingPlayer() {
        return this.tradingPlayer;
    }

    public boolean isTrading() {
        return this.tradingPlayer != null;
    }

    @Override
    public MerchantOffers getOffers() {
        if (this.offers == null) {
            this.offers = new MerchantOffers();
            this.updateTrades();
        }
        return this.offers;
    }

    @Override
    public void overrideOffers(@Nullable MerchantOffers merchantOffers) {
    }

    @Override
    public void overrideXp(int n) {
    }

    @Override
    public void notifyTrade(MerchantOffer merchantOffer) {
        merchantOffer.increaseUses();
        this.ambientSoundTime = -this.getAmbientSoundInterval();
        this.rewardTradeXp(merchantOffer);
        if (this.tradingPlayer instanceof ServerPlayer) {
            CriteriaTriggers.TRADE.trigger((ServerPlayer)this.tradingPlayer, this, merchantOffer.getResult());
        }
    }

    protected abstract void rewardTradeXp(MerchantOffer var1);

    @Override
    public boolean showProgressBar() {
        return true;
    }

    @Override
    public void notifyTradeUpdated(ItemStack itemStack) {
        if (!this.level.isClientSide && this.ambientSoundTime > -this.getAmbientSoundInterval() + 20) {
            this.ambientSoundTime = -this.getAmbientSoundInterval();
            this.playSound(this.getTradeUpdatedSound(!itemStack.isEmpty()), this.getSoundVolume(), this.getVoicePitch());
        }
    }

    @Override
    public SoundEvent getNotifyTradeSound() {
        return SoundEvents.VILLAGER_YES;
    }

    protected SoundEvent getTradeUpdatedSound(boolean bl) {
        return bl ? SoundEvents.VILLAGER_YES : SoundEvents.VILLAGER_NO;
    }

    public void playCelebrateSound() {
        this.playSound(SoundEvents.VILLAGER_CELEBRATE, this.getSoundVolume(), this.getVoicePitch());
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        MerchantOffers merchantOffers = this.getOffers();
        if (!merchantOffers.isEmpty()) {
            compoundTag.put("Offers", merchantOffers.createTag());
        }
        compoundTag.put("Inventory", this.inventory.createTag());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        if (compoundTag.contains("Offers", 10)) {
            this.offers = new MerchantOffers(compoundTag.getCompound("Offers"));
        }
        this.inventory.fromTag(compoundTag.getList("Inventory", 10));
    }

    @Nullable
    @Override
    public Entity changeDimension(ServerLevel serverLevel) {
        this.stopTrading();
        return super.changeDimension(serverLevel);
    }

    protected void stopTrading() {
        this.setTradingPlayer(null);
    }

    @Override
    public void die(DamageSource damageSource) {
        super.die(damageSource);
        this.stopTrading();
    }

    protected void addParticlesAroundSelf(ParticleOptions particleOptions) {
        for (int i = 0; i < 5; ++i) {
            double d = this.random.nextGaussian() * 0.02;
            double d2 = this.random.nextGaussian() * 0.02;
            double d3 = this.random.nextGaussian() * 0.02;
            this.level.addParticle(particleOptions, this.getRandomX(1.0), this.getRandomY() + 1.0, this.getRandomZ(1.0), d, d2, d3);
        }
    }

    @Override
    public boolean canBeLeashed(Player player) {
        return false;
    }

    public SimpleContainer getInventory() {
        return this.inventory;
    }

    @Override
    public boolean setSlot(int n, ItemStack itemStack) {
        if (super.setSlot(n, itemStack)) {
            return true;
        }
        int n2 = n - 300;
        if (n2 >= 0 && n2 < this.inventory.getContainerSize()) {
            this.inventory.setItem(n2, itemStack);
            return true;
        }
        return false;
    }

    @Override
    public Level getLevel() {
        return this.level;
    }

    protected abstract void updateTrades();

    protected void addOffersFromItemListings(MerchantOffers merchantOffers, VillagerTrades.ItemListing[] arritemListing, int n) {
        HashSet hashSet = Sets.newHashSet();
        if (arritemListing.length > n) {
            while (hashSet.size() < n) {
                hashSet.add(this.random.nextInt(arritemListing.length));
            }
        } else {
            for (int i = 0; i < arritemListing.length; ++i) {
                hashSet.add(i);
            }
        }
        for (Integer n2 : hashSet) {
            VillagerTrades.ItemListing itemListing = arritemListing[n2];
            MerchantOffer merchantOffer = itemListing.getOffer(this, this.random);
            if (merchantOffer == null) continue;
            merchantOffers.add(merchantOffer);
        }
    }

    @Override
    public Vec3 getRopeHoldPosition(float f) {
        float f2 = Mth.lerp(f, this.yBodyRotO, this.yBodyRot) * 0.017453292f;
        Vec3 vec3 = new Vec3(0.0, this.getBoundingBox().getYsize() - 1.0, 0.2);
        return this.getPosition(f).add(vec3.yRot(-f2));
    }
}

