/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  javax.annotation.Nullable
 */
package net.minecraft.world.entity;

import com.google.common.collect.Maps;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundSetEntityLinkPacket;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.Tag;
import net.minecraft.util.Mth;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.BodyRotationControl;
import net.minecraft.world.entity.ai.control.JumpControl;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.sensing.Sensing;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.entity.decoration.LeashFenceKnotEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemCooldowns;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.AbstractSkullBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public abstract class Mob
extends LivingEntity {
    private static final EntityDataAccessor<Byte> DATA_MOB_FLAGS_ID = SynchedEntityData.defineId(Mob.class, EntityDataSerializers.BYTE);
    public int ambientSoundTime;
    protected int xpReward;
    protected LookControl lookControl;
    protected MoveControl moveControl;
    protected JumpControl jumpControl;
    private final BodyRotationControl bodyRotationControl;
    protected PathNavigation navigation;
    protected final GoalSelector goalSelector;
    protected final GoalSelector targetSelector;
    private LivingEntity target;
    private final Sensing sensing;
    private final NonNullList<ItemStack> handItems = NonNullList.withSize(2, ItemStack.EMPTY);
    protected final float[] handDropChances = new float[2];
    private final NonNullList<ItemStack> armorItems = NonNullList.withSize(4, ItemStack.EMPTY);
    protected final float[] armorDropChances = new float[4];
    private boolean canPickUpLoot;
    private boolean persistenceRequired;
    private final Map<BlockPathTypes, Float> pathfindingMalus = Maps.newEnumMap(BlockPathTypes.class);
    private ResourceLocation lootTable;
    private long lootTableSeed;
    @Nullable
    private Entity leashHolder;
    private int delayedLeashHolderId;
    @Nullable
    private CompoundTag leashInfoTag;
    private BlockPos restrictCenter = BlockPos.ZERO;
    private float restrictRadius = -1.0f;

    protected Mob(EntityType<? extends Mob> entityType, Level level) {
        super(entityType, level);
        this.goalSelector = new GoalSelector(level.getProfilerSupplier());
        this.targetSelector = new GoalSelector(level.getProfilerSupplier());
        this.lookControl = new LookControl(this);
        this.moveControl = new MoveControl(this);
        this.jumpControl = new JumpControl(this);
        this.bodyRotationControl = this.createBodyControl();
        this.navigation = this.createNavigation(level);
        this.sensing = new Sensing(this);
        Arrays.fill(this.armorDropChances, 0.085f);
        Arrays.fill(this.handDropChances, 0.085f);
        if (level != null && !level.isClientSide) {
            this.registerGoals();
        }
    }

    protected void registerGoals() {
    }

    public static AttributeSupplier.Builder createMobAttributes() {
        return LivingEntity.createLivingAttributes().add(Attributes.FOLLOW_RANGE, 16.0).add(Attributes.ATTACK_KNOCKBACK);
    }

    protected PathNavigation createNavigation(Level level) {
        return new GroundPathNavigation(this, level);
    }

    protected boolean shouldPassengersInheritMalus() {
        return false;
    }

    public float getPathfindingMalus(BlockPathTypes blockPathTypes) {
        Mob mob = this.getVehicle() instanceof Mob && ((Mob)this.getVehicle()).shouldPassengersInheritMalus() ? (Mob)this.getVehicle() : this;
        Float f = mob.pathfindingMalus.get((Object)blockPathTypes);
        return f == null ? blockPathTypes.getMalus() : f.floatValue();
    }

    public void setPathfindingMalus(BlockPathTypes blockPathTypes, float f) {
        this.pathfindingMalus.put(blockPathTypes, Float.valueOf(f));
    }

    public boolean canCutCorner(BlockPathTypes blockPathTypes) {
        return blockPathTypes != BlockPathTypes.DANGER_FIRE && blockPathTypes != BlockPathTypes.DANGER_CACTUS && blockPathTypes != BlockPathTypes.DANGER_OTHER && blockPathTypes != BlockPathTypes.WALKABLE_DOOR;
    }

    protected BodyRotationControl createBodyControl() {
        return new BodyRotationControl(this);
    }

    public LookControl getLookControl() {
        return this.lookControl;
    }

    public MoveControl getMoveControl() {
        if (this.isPassenger() && this.getVehicle() instanceof Mob) {
            Mob mob = (Mob)this.getVehicle();
            return mob.getMoveControl();
        }
        return this.moveControl;
    }

    public JumpControl getJumpControl() {
        return this.jumpControl;
    }

    public PathNavigation getNavigation() {
        if (this.isPassenger() && this.getVehicle() instanceof Mob) {
            Mob mob = (Mob)this.getVehicle();
            return mob.getNavigation();
        }
        return this.navigation;
    }

    public Sensing getSensing() {
        return this.sensing;
    }

    @Nullable
    public LivingEntity getTarget() {
        return this.target;
    }

    public void setTarget(@Nullable LivingEntity livingEntity) {
        this.target = livingEntity;
    }

    @Override
    public boolean canAttackType(EntityType<?> entityType) {
        return entityType != EntityType.GHAST;
    }

    public boolean canFireProjectileWeapon(ProjectileWeaponItem projectileWeaponItem) {
        return false;
    }

    public void ate() {
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_MOB_FLAGS_ID, (byte)0);
    }

    public int getAmbientSoundInterval() {
        return 80;
    }

    public void playAmbientSound() {
        SoundEvent soundEvent = this.getAmbientSound();
        if (soundEvent != null) {
            this.playSound(soundEvent, this.getSoundVolume(), this.getVoicePitch());
        }
    }

    @Override
    public void baseTick() {
        super.baseTick();
        this.level.getProfiler().push("mobBaseTick");
        if (this.isAlive() && this.random.nextInt(1000) < this.ambientSoundTime++) {
            this.resetAmbientSoundTime();
            this.playAmbientSound();
        }
        this.level.getProfiler().pop();
    }

    @Override
    protected void playHurtSound(DamageSource damageSource) {
        this.resetAmbientSoundTime();
        super.playHurtSound(damageSource);
    }

    private void resetAmbientSoundTime() {
        this.ambientSoundTime = -this.getAmbientSoundInterval();
    }

    @Override
    protected int getExperienceReward(Player player) {
        if (this.xpReward > 0) {
            int n;
            int n2 = this.xpReward;
            for (n = 0; n < this.armorItems.size(); ++n) {
                if (this.armorItems.get(n).isEmpty() || !(this.armorDropChances[n] <= 1.0f)) continue;
                n2 += 1 + this.random.nextInt(3);
            }
            for (n = 0; n < this.handItems.size(); ++n) {
                if (this.handItems.get(n).isEmpty() || !(this.handDropChances[n] <= 1.0f)) continue;
                n2 += 1 + this.random.nextInt(3);
            }
            return n2;
        }
        return this.xpReward;
    }

    public void spawnAnim() {
        if (this.level.isClientSide) {
            for (int i = 0; i < 20; ++i) {
                double d = this.random.nextGaussian() * 0.02;
                double d2 = this.random.nextGaussian() * 0.02;
                double d3 = this.random.nextGaussian() * 0.02;
                double d4 = 10.0;
                this.level.addParticle(ParticleTypes.POOF, this.getX(1.0) - d * 10.0, this.getRandomY() - d2 * 10.0, this.getRandomZ(1.0) - d3 * 10.0, d, d2, d3);
            }
        } else {
            this.level.broadcastEntityEvent(this, (byte)20);
        }
    }

    @Override
    public void handleEntityEvent(byte by) {
        if (by == 20) {
            this.spawnAnim();
        } else {
            super.handleEntityEvent(by);
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level.isClientSide) {
            this.tickLeash();
            if (this.tickCount % 5 == 0) {
                this.updateControlFlags();
            }
        }
    }

    protected void updateControlFlags() {
        boolean bl = !(this.getControllingPassenger() instanceof Mob);
        boolean bl2 = !(this.getVehicle() instanceof Boat);
        this.goalSelector.setControlFlag(Goal.Flag.MOVE, bl);
        this.goalSelector.setControlFlag(Goal.Flag.JUMP, bl && bl2);
        this.goalSelector.setControlFlag(Goal.Flag.LOOK, bl);
    }

    @Override
    protected float tickHeadTurn(float f, float f2) {
        this.bodyRotationControl.clientTick();
        return f2;
    }

    @Nullable
    protected SoundEvent getAmbientSound() {
        return null;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        compoundTag.putBoolean("CanPickUpLoot", this.canPickUpLoot());
        compoundTag.putBoolean("PersistenceRequired", this.persistenceRequired);
        ListTag listTag = new ListTag();
        for (ItemStack object3 : this.armorItems) {
            CompoundTag compoundTag2 = new CompoundTag();
            if (!object3.isEmpty()) {
                object3.save(compoundTag2);
            }
            listTag.add(compoundTag2);
        }
        compoundTag.put("ArmorItems", listTag);
        ListTag listTag2 = new ListTag();
        for (ItemStack itemStack : this.handItems) {
            CompoundTag compoundTag3 = new CompoundTag();
            if (!itemStack.isEmpty()) {
                itemStack.save(compoundTag3);
            }
            listTag2.add(compoundTag3);
        }
        compoundTag.put("HandItems", listTag2);
        ListTag listTag3 = new ListTag();
        for (float f : this.armorDropChances) {
            listTag3.add(FloatTag.valueOf(f));
        }
        compoundTag.put("ArmorDropChances", listTag3);
        ListTag listTag4 = new ListTag();
        for (float f : this.handDropChances) {
            listTag4.add(FloatTag.valueOf(f));
        }
        compoundTag.put("HandDropChances", listTag4);
        if (this.leashHolder != null) {
            CompoundTag compoundTag4 = new CompoundTag();
            if (this.leashHolder instanceof LivingEntity) {
                UUID uUID = this.leashHolder.getUUID();
                compoundTag4.putUUID("UUID", uUID);
            } else if (this.leashHolder instanceof HangingEntity) {
                BlockPos blockPos = ((HangingEntity)this.leashHolder).getPos();
                compoundTag4.putInt("X", blockPos.getX());
                compoundTag4.putInt("Y", blockPos.getY());
                compoundTag4.putInt("Z", blockPos.getZ());
            }
            compoundTag.put("Leash", compoundTag4);
        } else if (this.leashInfoTag != null) {
            compoundTag.put("Leash", this.leashInfoTag.copy());
        }
        compoundTag.putBoolean("LeftHanded", this.isLeftHanded());
        if (this.lootTable != null) {
            compoundTag.putString("DeathLootTable", this.lootTable.toString());
            if (this.lootTableSeed != 0L) {
                compoundTag.putLong("DeathLootTableSeed", this.lootTableSeed);
            }
        }
        if (this.isNoAi()) {
            compoundTag.putBoolean("NoAI", this.isNoAi());
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compoundTag) {
        int n;
        ListTag listTag;
        super.readAdditionalSaveData(compoundTag);
        if (compoundTag.contains("CanPickUpLoot", 1)) {
            this.setCanPickUpLoot(compoundTag.getBoolean("CanPickUpLoot"));
        }
        this.persistenceRequired = compoundTag.getBoolean("PersistenceRequired");
        if (compoundTag.contains("ArmorItems", 9)) {
            listTag = compoundTag.getList("ArmorItems", 10);
            for (n = 0; n < this.armorItems.size(); ++n) {
                this.armorItems.set(n, ItemStack.of(listTag.getCompound(n)));
            }
        }
        if (compoundTag.contains("HandItems", 9)) {
            listTag = compoundTag.getList("HandItems", 10);
            for (n = 0; n < this.handItems.size(); ++n) {
                this.handItems.set(n, ItemStack.of(listTag.getCompound(n)));
            }
        }
        if (compoundTag.contains("ArmorDropChances", 9)) {
            listTag = compoundTag.getList("ArmorDropChances", 5);
            for (n = 0; n < listTag.size(); ++n) {
                this.armorDropChances[n] = listTag.getFloat(n);
            }
        }
        if (compoundTag.contains("HandDropChances", 9)) {
            listTag = compoundTag.getList("HandDropChances", 5);
            for (n = 0; n < listTag.size(); ++n) {
                this.handDropChances[n] = listTag.getFloat(n);
            }
        }
        if (compoundTag.contains("Leash", 10)) {
            this.leashInfoTag = compoundTag.getCompound("Leash");
        }
        this.setLeftHanded(compoundTag.getBoolean("LeftHanded"));
        if (compoundTag.contains("DeathLootTable", 8)) {
            this.lootTable = new ResourceLocation(compoundTag.getString("DeathLootTable"));
            this.lootTableSeed = compoundTag.getLong("DeathLootTableSeed");
        }
        this.setNoAi(compoundTag.getBoolean("NoAI"));
    }

    @Override
    protected void dropFromLootTable(DamageSource damageSource, boolean bl) {
        super.dropFromLootTable(damageSource, bl);
        this.lootTable = null;
    }

    @Override
    protected LootContext.Builder createLootContext(boolean bl, DamageSource damageSource) {
        return super.createLootContext(bl, damageSource).withOptionalRandomSeed(this.lootTableSeed, this.random);
    }

    @Override
    public final ResourceLocation getLootTable() {
        return this.lootTable == null ? this.getDefaultLootTable() : this.lootTable;
    }

    protected ResourceLocation getDefaultLootTable() {
        return super.getLootTable();
    }

    public void setZza(float f) {
        this.zza = f;
    }

    public void setYya(float f) {
        this.yya = f;
    }

    public void setXxa(float f) {
        this.xxa = f;
    }

    @Override
    public void setSpeed(float f) {
        super.setSpeed(f);
        this.setZza(f);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        this.level.getProfiler().push("looting");
        if (!this.level.isClientSide && this.canPickUpLoot() && this.isAlive() && !this.dead && this.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
            List<ItemEntity> list = this.level.getEntitiesOfClass(ItemEntity.class, this.getBoundingBox().inflate(1.0, 0.0, 1.0));
            for (ItemEntity itemEntity : list) {
                if (itemEntity.removed || itemEntity.getItem().isEmpty() || itemEntity.hasPickUpDelay() || !this.wantsToPickUp(itemEntity.getItem())) continue;
                this.pickUpItem(itemEntity);
            }
        }
        this.level.getProfiler().pop();
    }

    protected void pickUpItem(ItemEntity itemEntity) {
        ItemStack itemStack = itemEntity.getItem();
        if (this.equipItemIfPossible(itemStack)) {
            this.onItemPickup(itemEntity);
            this.take(itemEntity, itemStack.getCount());
            itemEntity.remove();
        }
    }

    public boolean equipItemIfPossible(ItemStack itemStack) {
        EquipmentSlot equipmentSlot = Mob.getEquipmentSlotForItem(itemStack);
        ItemStack itemStack2 = this.getItemBySlot(equipmentSlot);
        boolean bl = this.canReplaceCurrentItem(itemStack, itemStack2);
        if (bl && this.canHoldItem(itemStack)) {
            double d = this.getEquipmentDropChance(equipmentSlot);
            if (!itemStack2.isEmpty() && (double)Math.max(this.random.nextFloat() - 0.1f, 0.0f) < d) {
                this.spawnAtLocation(itemStack2);
            }
            this.setItemSlotAndDropWhenKilled(equipmentSlot, itemStack);
            this.playEquipSound(itemStack);
            return true;
        }
        return false;
    }

    protected void setItemSlotAndDropWhenKilled(EquipmentSlot equipmentSlot, ItemStack itemStack) {
        this.setItemSlot(equipmentSlot, itemStack);
        this.setGuaranteedDrop(equipmentSlot);
        this.persistenceRequired = true;
    }

    public void setGuaranteedDrop(EquipmentSlot equipmentSlot) {
        switch (equipmentSlot.getType()) {
            case HAND: {
                this.handDropChances[equipmentSlot.getIndex()] = 2.0f;
                break;
            }
            case ARMOR: {
                this.armorDropChances[equipmentSlot.getIndex()] = 2.0f;
            }
        }
    }

    protected boolean canReplaceCurrentItem(ItemStack itemStack, ItemStack itemStack2) {
        if (itemStack2.isEmpty()) {
            return true;
        }
        if (itemStack.getItem() instanceof SwordItem) {
            if (!(itemStack2.getItem() instanceof SwordItem)) {
                return true;
            }
            SwordItem swordItem = (SwordItem)itemStack.getItem();
            SwordItem swordItem2 = (SwordItem)itemStack2.getItem();
            if (swordItem.getDamage() != swordItem2.getDamage()) {
                return swordItem.getDamage() > swordItem2.getDamage();
            }
            return this.canReplaceEqualItem(itemStack, itemStack2);
        }
        if (itemStack.getItem() instanceof BowItem && itemStack2.getItem() instanceof BowItem) {
            return this.canReplaceEqualItem(itemStack, itemStack2);
        }
        if (itemStack.getItem() instanceof CrossbowItem && itemStack2.getItem() instanceof CrossbowItem) {
            return this.canReplaceEqualItem(itemStack, itemStack2);
        }
        if (itemStack.getItem() instanceof ArmorItem) {
            if (EnchantmentHelper.hasBindingCurse(itemStack2)) {
                return false;
            }
            if (!(itemStack2.getItem() instanceof ArmorItem)) {
                return true;
            }
            ArmorItem armorItem = (ArmorItem)itemStack.getItem();
            ArmorItem armorItem2 = (ArmorItem)itemStack2.getItem();
            if (armorItem.getDefense() != armorItem2.getDefense()) {
                return armorItem.getDefense() > armorItem2.getDefense();
            }
            if (armorItem.getToughness() != armorItem2.getToughness()) {
                return armorItem.getToughness() > armorItem2.getToughness();
            }
            return this.canReplaceEqualItem(itemStack, itemStack2);
        }
        if (itemStack.getItem() instanceof DiggerItem) {
            if (itemStack2.getItem() instanceof BlockItem) {
                return true;
            }
            if (itemStack2.getItem() instanceof DiggerItem) {
                DiggerItem diggerItem = (DiggerItem)itemStack.getItem();
                DiggerItem diggerItem2 = (DiggerItem)itemStack2.getItem();
                if (diggerItem.getAttackDamage() != diggerItem2.getAttackDamage()) {
                    return diggerItem.getAttackDamage() > diggerItem2.getAttackDamage();
                }
                return this.canReplaceEqualItem(itemStack, itemStack2);
            }
        }
        return false;
    }

    public boolean canReplaceEqualItem(ItemStack itemStack, ItemStack itemStack2) {
        if (itemStack.getDamageValue() < itemStack2.getDamageValue() || itemStack.hasTag() && !itemStack2.hasTag()) {
            return true;
        }
        if (itemStack.hasTag() && itemStack2.hasTag()) {
            return itemStack.getTag().getAllKeys().stream().anyMatch(string -> !string.equals("Damage")) && !itemStack2.getTag().getAllKeys().stream().anyMatch(string -> !string.equals("Damage"));
        }
        return false;
    }

    public boolean canHoldItem(ItemStack itemStack) {
        return true;
    }

    public boolean wantsToPickUp(ItemStack itemStack) {
        return this.canHoldItem(itemStack);
    }

    public boolean removeWhenFarAway(double d) {
        return true;
    }

    public boolean requiresCustomPersistence() {
        return this.isPassenger();
    }

    protected boolean shouldDespawnInPeaceful() {
        return false;
    }

    @Override
    public void checkDespawn() {
        if (this.level.getDifficulty() == Difficulty.PEACEFUL && this.shouldDespawnInPeaceful()) {
            this.remove();
            return;
        }
        if (this.isPersistenceRequired() || this.requiresCustomPersistence()) {
            this.noActionTime = 0;
            return;
        }
        Player player = this.level.getNearestPlayer(this, -1.0);
        if (player != null) {
            int n;
            int n2;
            double d = player.distanceToSqr(this);
            if (d > (double)(n = (n2 = this.getType().getCategory().getDespawnDistance()) * n2) && this.removeWhenFarAway(d)) {
                this.remove();
            }
            int n3 = this.getType().getCategory().getNoDespawnDistance();
            int n4 = n3 * n3;
            if (this.noActionTime > 600 && this.random.nextInt(800) == 0 && d > (double)n4 && this.removeWhenFarAway(d)) {
                this.remove();
            } else if (d < (double)n4) {
                this.noActionTime = 0;
            }
        }
    }

    @Override
    protected final void serverAiStep() {
        ++this.noActionTime;
        this.level.getProfiler().push("sensing");
        this.sensing.tick();
        this.level.getProfiler().pop();
        this.level.getProfiler().push("targetSelector");
        this.targetSelector.tick();
        this.level.getProfiler().pop();
        this.level.getProfiler().push("goalSelector");
        this.goalSelector.tick();
        this.level.getProfiler().pop();
        this.level.getProfiler().push("navigation");
        this.navigation.tick();
        this.level.getProfiler().pop();
        this.level.getProfiler().push("mob tick");
        this.customServerAiStep();
        this.level.getProfiler().pop();
        this.level.getProfiler().push("controls");
        this.level.getProfiler().push("move");
        this.moveControl.tick();
        this.level.getProfiler().popPush("look");
        this.lookControl.tick();
        this.level.getProfiler().popPush("jump");
        this.jumpControl.tick();
        this.level.getProfiler().pop();
        this.level.getProfiler().pop();
        this.sendDebugPackets();
    }

    protected void sendDebugPackets() {
        DebugPackets.sendGoalSelector(this.level, this, this.goalSelector);
    }

    protected void customServerAiStep() {
    }

    public int getMaxHeadXRot() {
        return 40;
    }

    public int getMaxHeadYRot() {
        return 75;
    }

    public int getHeadRotSpeed() {
        return 10;
    }

    public void lookAt(Entity entity, float f, float f2) {
        double d;
        double d2 = entity.getX() - this.getX();
        double d3 = entity.getZ() - this.getZ();
        if (entity instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity)entity;
            d = livingEntity.getEyeY() - this.getEyeY();
        } else {
            d = (entity.getBoundingBox().minY + entity.getBoundingBox().maxY) / 2.0 - this.getEyeY();
        }
        double d4 = Mth.sqrt(d2 * d2 + d3 * d3);
        float f3 = (float)(Mth.atan2(d3, d2) * 57.2957763671875) - 90.0f;
        float f4 = (float)(-(Mth.atan2(d, d4) * 57.2957763671875));
        this.xRot = this.rotlerp(this.xRot, f4, f2);
        this.yRot = this.rotlerp(this.yRot, f3, f);
    }

    private float rotlerp(float f, float f2, float f3) {
        float f4 = Mth.wrapDegrees(f2 - f);
        if (f4 > f3) {
            f4 = f3;
        }
        if (f4 < -f3) {
            f4 = -f3;
        }
        return f + f4;
    }

    public static boolean checkMobSpawnRules(EntityType<? extends Mob> entityType, LevelAccessor levelAccessor, MobSpawnType mobSpawnType, BlockPos blockPos, Random random) {
        BlockPos blockPos2 = blockPos.below();
        return mobSpawnType == MobSpawnType.SPAWNER || levelAccessor.getBlockState(blockPos2).isValidSpawn(levelAccessor, blockPos2, entityType);
    }

    public boolean checkSpawnRules(LevelAccessor levelAccessor, MobSpawnType mobSpawnType) {
        return true;
    }

    public boolean checkSpawnObstruction(LevelReader levelReader) {
        return !levelReader.containsAnyLiquid(this.getBoundingBox()) && levelReader.isUnobstructed(this);
    }

    public int getMaxSpawnClusterSize() {
        return 4;
    }

    public boolean isMaxGroupSizeReached(int n) {
        return false;
    }

    @Override
    public int getMaxFallDistance() {
        if (this.getTarget() == null) {
            return 3;
        }
        int n = (int)(this.getHealth() - this.getMaxHealth() * 0.33f);
        if ((n -= (3 - this.level.getDifficulty().getId()) * 4) < 0) {
            n = 0;
        }
        return n + 3;
    }

    @Override
    public Iterable<ItemStack> getHandSlots() {
        return this.handItems;
    }

    @Override
    public Iterable<ItemStack> getArmorSlots() {
        return this.armorItems;
    }

    @Override
    public ItemStack getItemBySlot(EquipmentSlot equipmentSlot) {
        switch (equipmentSlot.getType()) {
            case HAND: {
                return this.handItems.get(equipmentSlot.getIndex());
            }
            case ARMOR: {
                return this.armorItems.get(equipmentSlot.getIndex());
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public void setItemSlot(EquipmentSlot equipmentSlot, ItemStack itemStack) {
        switch (equipmentSlot.getType()) {
            case HAND: {
                this.handItems.set(equipmentSlot.getIndex(), itemStack);
                break;
            }
            case ARMOR: {
                this.armorItems.set(equipmentSlot.getIndex(), itemStack);
            }
        }
    }

    @Override
    protected void dropCustomDeathLoot(DamageSource damageSource, int n, boolean bl) {
        super.dropCustomDeathLoot(damageSource, n, bl);
        for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
            boolean bl2;
            ItemStack itemStack = this.getItemBySlot(equipmentSlot);
            float f = this.getEquipmentDropChance(equipmentSlot);
            boolean bl3 = bl2 = f > 1.0f;
            if (itemStack.isEmpty() || EnchantmentHelper.hasVanishingCurse(itemStack) || !bl && !bl2 || !(Math.max(this.random.nextFloat() - (float)n * 0.01f, 0.0f) < f)) continue;
            if (!bl2 && itemStack.isDamageableItem()) {
                itemStack.setDamageValue(itemStack.getMaxDamage() - this.random.nextInt(1 + this.random.nextInt(Math.max(itemStack.getMaxDamage() - 3, 1))));
            }
            this.spawnAtLocation(itemStack);
            this.setItemSlot(equipmentSlot, ItemStack.EMPTY);
        }
    }

    protected float getEquipmentDropChance(EquipmentSlot equipmentSlot) {
        float f;
        switch (equipmentSlot.getType()) {
            case HAND: {
                f = this.handDropChances[equipmentSlot.getIndex()];
                break;
            }
            case ARMOR: {
                f = this.armorDropChances[equipmentSlot.getIndex()];
                break;
            }
            default: {
                f = 0.0f;
            }
        }
        return f;
    }

    protected void populateDefaultEquipmentSlots(DifficultyInstance difficultyInstance) {
        if (this.random.nextFloat() < 0.15f * difficultyInstance.getSpecialMultiplier()) {
            float f;
            int n = this.random.nextInt(2);
            float f2 = f = this.level.getDifficulty() == Difficulty.HARD ? 0.1f : 0.25f;
            if (this.random.nextFloat() < 0.095f) {
                ++n;
            }
            if (this.random.nextFloat() < 0.095f) {
                ++n;
            }
            if (this.random.nextFloat() < 0.095f) {
                ++n;
            }
            boolean bl = true;
            for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
                Item item;
                if (equipmentSlot.getType() != EquipmentSlot.Type.ARMOR) continue;
                ItemStack itemStack = this.getItemBySlot(equipmentSlot);
                if (!bl && this.random.nextFloat() < f) break;
                bl = false;
                if (!itemStack.isEmpty() || (item = Mob.getEquipmentForSlot(equipmentSlot, n)) == null) continue;
                this.setItemSlot(equipmentSlot, new ItemStack(item));
            }
        }
    }

    public static EquipmentSlot getEquipmentSlotForItem(ItemStack itemStack) {
        Item item = itemStack.getItem();
        if (item == Blocks.CARVED_PUMPKIN.asItem() || item instanceof BlockItem && ((BlockItem)item).getBlock() instanceof AbstractSkullBlock) {
            return EquipmentSlot.HEAD;
        }
        if (item instanceof ArmorItem) {
            return ((ArmorItem)item).getSlot();
        }
        if (item == Items.ELYTRA) {
            return EquipmentSlot.CHEST;
        }
        if (item == Items.SHIELD) {
            return EquipmentSlot.OFFHAND;
        }
        return EquipmentSlot.MAINHAND;
    }

    @Nullable
    public static Item getEquipmentForSlot(EquipmentSlot equipmentSlot, int n) {
        switch (equipmentSlot) {
            case HEAD: {
                if (n == 0) {
                    return Items.LEATHER_HELMET;
                }
                if (n == 1) {
                    return Items.GOLDEN_HELMET;
                }
                if (n == 2) {
                    return Items.CHAINMAIL_HELMET;
                }
                if (n == 3) {
                    return Items.IRON_HELMET;
                }
                if (n == 4) {
                    return Items.DIAMOND_HELMET;
                }
            }
            case CHEST: {
                if (n == 0) {
                    return Items.LEATHER_CHESTPLATE;
                }
                if (n == 1) {
                    return Items.GOLDEN_CHESTPLATE;
                }
                if (n == 2) {
                    return Items.CHAINMAIL_CHESTPLATE;
                }
                if (n == 3) {
                    return Items.IRON_CHESTPLATE;
                }
                if (n == 4) {
                    return Items.DIAMOND_CHESTPLATE;
                }
            }
            case LEGS: {
                if (n == 0) {
                    return Items.LEATHER_LEGGINGS;
                }
                if (n == 1) {
                    return Items.GOLDEN_LEGGINGS;
                }
                if (n == 2) {
                    return Items.CHAINMAIL_LEGGINGS;
                }
                if (n == 3) {
                    return Items.IRON_LEGGINGS;
                }
                if (n == 4) {
                    return Items.DIAMOND_LEGGINGS;
                }
            }
            case FEET: {
                if (n == 0) {
                    return Items.LEATHER_BOOTS;
                }
                if (n == 1) {
                    return Items.GOLDEN_BOOTS;
                }
                if (n == 2) {
                    return Items.CHAINMAIL_BOOTS;
                }
                if (n == 3) {
                    return Items.IRON_BOOTS;
                }
                if (n != 4) break;
                return Items.DIAMOND_BOOTS;
            }
        }
        return null;
    }

    protected void populateDefaultEquipmentEnchantments(DifficultyInstance difficultyInstance) {
        float f = difficultyInstance.getSpecialMultiplier();
        this.enchantSpawnedWeapon(f);
        for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
            if (equipmentSlot.getType() != EquipmentSlot.Type.ARMOR) continue;
            this.enchantSpawnedArmor(f, equipmentSlot);
        }
    }

    protected void enchantSpawnedWeapon(float f) {
        if (!this.getMainHandItem().isEmpty() && this.random.nextFloat() < 0.25f * f) {
            this.setItemSlot(EquipmentSlot.MAINHAND, EnchantmentHelper.enchantItem(this.random, this.getMainHandItem(), (int)(5.0f + f * (float)this.random.nextInt(18)), false));
        }
    }

    protected void enchantSpawnedArmor(float f, EquipmentSlot equipmentSlot) {
        ItemStack itemStack = this.getItemBySlot(equipmentSlot);
        if (!itemStack.isEmpty() && this.random.nextFloat() < 0.5f * f) {
            this.setItemSlot(equipmentSlot, EnchantmentHelper.enchantItem(this.random, itemStack, (int)(5.0f + f * (float)this.random.nextInt(18)), false));
        }
    }

    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor serverLevelAccessor, DifficultyInstance difficultyInstance, MobSpawnType mobSpawnType, @Nullable SpawnGroupData spawnGroupData, @Nullable CompoundTag compoundTag) {
        this.getAttribute(Attributes.FOLLOW_RANGE).addPermanentModifier(new AttributeModifier("Random spawn bonus", this.random.nextGaussian() * 0.05, AttributeModifier.Operation.MULTIPLY_BASE));
        if (this.random.nextFloat() < 0.05f) {
            this.setLeftHanded(true);
        } else {
            this.setLeftHanded(false);
        }
        return spawnGroupData;
    }

    public boolean canBeControlledByRider() {
        return false;
    }

    public void setPersistenceRequired() {
        this.persistenceRequired = true;
    }

    public void setDropChance(EquipmentSlot equipmentSlot, float f) {
        switch (equipmentSlot.getType()) {
            case HAND: {
                this.handDropChances[equipmentSlot.getIndex()] = f;
                break;
            }
            case ARMOR: {
                this.armorDropChances[equipmentSlot.getIndex()] = f;
            }
        }
    }

    public boolean canPickUpLoot() {
        return this.canPickUpLoot;
    }

    public void setCanPickUpLoot(boolean bl) {
        this.canPickUpLoot = bl;
    }

    @Override
    public boolean canTakeItem(ItemStack itemStack) {
        EquipmentSlot equipmentSlot = Mob.getEquipmentSlotForItem(itemStack);
        return this.getItemBySlot(equipmentSlot).isEmpty() && this.canPickUpLoot();
    }

    public boolean isPersistenceRequired() {
        return this.persistenceRequired;
    }

    @Override
    public final InteractionResult interact(Player player, InteractionHand interactionHand) {
        if (!this.isAlive()) {
            return InteractionResult.PASS;
        }
        if (this.getLeashHolder() == player) {
            this.dropLeash(true, !player.abilities.instabuild);
            return InteractionResult.sidedSuccess(this.level.isClientSide);
        }
        InteractionResult interactionResult = this.checkAndHandleImportantInteractions(player, interactionHand);
        if (interactionResult.consumesAction()) {
            return interactionResult;
        }
        interactionResult = this.mobInteract(player, interactionHand);
        if (interactionResult.consumesAction()) {
            return interactionResult;
        }
        return super.interact(player, interactionHand);
    }

    private InteractionResult checkAndHandleImportantInteractions(Player player, InteractionHand interactionHand) {
        Object object;
        ItemStack itemStack = player.getItemInHand(interactionHand);
        if (itemStack.getItem() == Items.LEAD && this.canBeLeashed(player)) {
            this.setLeashedTo(player, true);
            itemStack.shrink(1);
            return InteractionResult.sidedSuccess(this.level.isClientSide);
        }
        if (itemStack.getItem() == Items.NAME_TAG && ((InteractionResult)((Object)(object = itemStack.interactLivingEntity(player, this, interactionHand)))).consumesAction()) {
            return object;
        }
        if (itemStack.getItem() instanceof SpawnEggItem) {
            if (this.level instanceof ServerLevel) {
                object = (SpawnEggItem)itemStack.getItem();
                Optional<Mob> optional = ((SpawnEggItem)object).spawnOffspringFromSpawnEgg(player, this, this.getType(), (ServerLevel)this.level, this.position(), itemStack);
                optional.ifPresent(mob -> this.onOffspringSpawnedFromEgg(player, (Mob)mob));
                return optional.isPresent() ? InteractionResult.SUCCESS : InteractionResult.PASS;
            }
            return InteractionResult.CONSUME;
        }
        return InteractionResult.PASS;
    }

    protected void onOffspringSpawnedFromEgg(Player player, Mob mob) {
    }

    protected InteractionResult mobInteract(Player player, InteractionHand interactionHand) {
        return InteractionResult.PASS;
    }

    public boolean isWithinRestriction() {
        return this.isWithinRestriction(this.blockPosition());
    }

    public boolean isWithinRestriction(BlockPos blockPos) {
        if (this.restrictRadius == -1.0f) {
            return true;
        }
        return this.restrictCenter.distSqr(blockPos) < (double)(this.restrictRadius * this.restrictRadius);
    }

    public void restrictTo(BlockPos blockPos, int n) {
        this.restrictCenter = blockPos;
        this.restrictRadius = n;
    }

    public BlockPos getRestrictCenter() {
        return this.restrictCenter;
    }

    public float getRestrictRadius() {
        return this.restrictRadius;
    }

    public boolean hasRestriction() {
        return this.restrictRadius != -1.0f;
    }

    @Nullable
    public <T extends Mob> T convertTo(EntityType<T> entityType, boolean bl) {
        if (this.removed) {
            return null;
        }
        Mob mob = (Mob)entityType.create(this.level);
        mob.copyPosition(this);
        mob.setBaby(this.isBaby());
        mob.setNoAi(this.isNoAi());
        if (this.hasCustomName()) {
            mob.setCustomName(this.getCustomName());
            mob.setCustomNameVisible(this.isCustomNameVisible());
        }
        if (this.isPersistenceRequired()) {
            mob.setPersistenceRequired();
        }
        mob.setInvulnerable(this.isInvulnerable());
        if (bl) {
            mob.setCanPickUpLoot(this.canPickUpLoot());
            for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
                ItemStack itemStack = this.getItemBySlot(equipmentSlot);
                if (itemStack.isEmpty()) continue;
                mob.setItemSlot(equipmentSlot, itemStack.copy());
                mob.setDropChance(equipmentSlot, this.getEquipmentDropChance(equipmentSlot));
                itemStack.setCount(0);
            }
        }
        this.level.addFreshEntity(mob);
        if (this.isPassenger()) {
            Entity entity = this.getVehicle();
            this.stopRiding();
            mob.startRiding(entity, true);
        }
        this.remove();
        return (T)mob;
    }

    protected void tickLeash() {
        if (this.leashInfoTag != null) {
            this.restoreLeashFromSave();
        }
        if (this.leashHolder == null) {
            return;
        }
        if (!this.isAlive() || !this.leashHolder.isAlive()) {
            this.dropLeash(true, true);
        }
    }

    public void dropLeash(boolean bl, boolean bl2) {
        if (this.leashHolder != null) {
            this.forcedLoading = false;
            if (!(this.leashHolder instanceof Player)) {
                this.leashHolder.forcedLoading = false;
            }
            this.leashHolder = null;
            this.leashInfoTag = null;
            if (!this.level.isClientSide && bl2) {
                this.spawnAtLocation(Items.LEAD);
            }
            if (!this.level.isClientSide && bl && this.level instanceof ServerLevel) {
                ((ServerLevel)this.level).getChunkSource().broadcast(this, new ClientboundSetEntityLinkPacket(this, null));
            }
        }
    }

    public boolean canBeLeashed(Player player) {
        return !this.isLeashed() && !(this instanceof Enemy);
    }

    public boolean isLeashed() {
        return this.leashHolder != null;
    }

    @Nullable
    public Entity getLeashHolder() {
        if (this.leashHolder == null && this.delayedLeashHolderId != 0 && this.level.isClientSide) {
            this.leashHolder = this.level.getEntity(this.delayedLeashHolderId);
        }
        return this.leashHolder;
    }

    public void setLeashedTo(Entity entity, boolean bl) {
        this.leashHolder = entity;
        this.leashInfoTag = null;
        this.forcedLoading = true;
        if (!(this.leashHolder instanceof Player)) {
            this.leashHolder.forcedLoading = true;
        }
        if (!this.level.isClientSide && bl && this.level instanceof ServerLevel) {
            ((ServerLevel)this.level).getChunkSource().broadcast(this, new ClientboundSetEntityLinkPacket(this, this.leashHolder));
        }
        if (this.isPassenger()) {
            this.stopRiding();
        }
    }

    public void setDelayedLeashHolderId(int n) {
        this.delayedLeashHolderId = n;
        this.dropLeash(false, false);
    }

    @Override
    public boolean startRiding(Entity entity, boolean bl) {
        boolean bl2 = super.startRiding(entity, bl);
        if (bl2 && this.isLeashed()) {
            this.dropLeash(true, true);
        }
        return bl2;
    }

    private void restoreLeashFromSave() {
        if (this.leashInfoTag != null && this.level instanceof ServerLevel) {
            if (this.leashInfoTag.hasUUID("UUID")) {
                UUID uUID = this.leashInfoTag.getUUID("UUID");
                Entity entity = ((ServerLevel)this.level).getEntity(uUID);
                if (entity != null) {
                    this.setLeashedTo(entity, true);
                    return;
                }
            } else if (this.leashInfoTag.contains("X", 99) && this.leashInfoTag.contains("Y", 99) && this.leashInfoTag.contains("Z", 99)) {
                BlockPos blockPos = new BlockPos(this.leashInfoTag.getInt("X"), this.leashInfoTag.getInt("Y"), this.leashInfoTag.getInt("Z"));
                this.setLeashedTo(LeashFenceKnotEntity.getOrCreateKnot(this.level, blockPos), true);
                return;
            }
            if (this.tickCount > 100) {
                this.spawnAtLocation(Items.LEAD);
                this.leashInfoTag = null;
            }
        }
    }

    @Override
    public boolean setSlot(int n, ItemStack itemStack) {
        EquipmentSlot equipmentSlot;
        if (n == 98) {
            equipmentSlot = EquipmentSlot.MAINHAND;
        } else if (n == 99) {
            equipmentSlot = EquipmentSlot.OFFHAND;
        } else if (n == 100 + EquipmentSlot.HEAD.getIndex()) {
            equipmentSlot = EquipmentSlot.HEAD;
        } else if (n == 100 + EquipmentSlot.CHEST.getIndex()) {
            equipmentSlot = EquipmentSlot.CHEST;
        } else if (n == 100 + EquipmentSlot.LEGS.getIndex()) {
            equipmentSlot = EquipmentSlot.LEGS;
        } else if (n == 100 + EquipmentSlot.FEET.getIndex()) {
            equipmentSlot = EquipmentSlot.FEET;
        } else {
            return false;
        }
        if (itemStack.isEmpty() || Mob.isValidSlotForItem(equipmentSlot, itemStack) || equipmentSlot == EquipmentSlot.HEAD) {
            this.setItemSlot(equipmentSlot, itemStack);
            return true;
        }
        return false;
    }

    @Override
    public boolean isControlledByLocalInstance() {
        return this.canBeControlledByRider() && super.isControlledByLocalInstance();
    }

    public static boolean isValidSlotForItem(EquipmentSlot equipmentSlot, ItemStack itemStack) {
        EquipmentSlot equipmentSlot2 = Mob.getEquipmentSlotForItem(itemStack);
        return equipmentSlot2 == equipmentSlot || equipmentSlot2 == EquipmentSlot.MAINHAND && equipmentSlot == EquipmentSlot.OFFHAND || equipmentSlot2 == EquipmentSlot.OFFHAND && equipmentSlot == EquipmentSlot.MAINHAND;
    }

    @Override
    public boolean isEffectiveAi() {
        return super.isEffectiveAi() && !this.isNoAi();
    }

    public void setNoAi(boolean bl) {
        byte by = this.entityData.get(DATA_MOB_FLAGS_ID);
        this.entityData.set(DATA_MOB_FLAGS_ID, bl ? (byte)(by | 1) : (byte)(by & 0xFFFFFFFE));
    }

    public void setLeftHanded(boolean bl) {
        byte by = this.entityData.get(DATA_MOB_FLAGS_ID);
        this.entityData.set(DATA_MOB_FLAGS_ID, bl ? (byte)(by | 2) : (byte)(by & 0xFFFFFFFD));
    }

    public void setAggressive(boolean bl) {
        byte by = this.entityData.get(DATA_MOB_FLAGS_ID);
        this.entityData.set(DATA_MOB_FLAGS_ID, bl ? (byte)(by | 4) : (byte)(by & 0xFFFFFFFB));
    }

    public boolean isNoAi() {
        return (this.entityData.get(DATA_MOB_FLAGS_ID) & 1) != 0;
    }

    public boolean isLeftHanded() {
        return (this.entityData.get(DATA_MOB_FLAGS_ID) & 2) != 0;
    }

    public boolean isAggressive() {
        return (this.entityData.get(DATA_MOB_FLAGS_ID) & 4) != 0;
    }

    public void setBaby(boolean bl) {
    }

    @Override
    public HumanoidArm getMainArm() {
        return this.isLeftHanded() ? HumanoidArm.LEFT : HumanoidArm.RIGHT;
    }

    @Override
    public boolean canAttack(LivingEntity livingEntity) {
        if (livingEntity.getType() == EntityType.PLAYER && ((Player)livingEntity).abilities.invulnerable) {
            return false;
        }
        return super.canAttack(livingEntity);
    }

    @Override
    public boolean doHurtTarget(Entity entity) {
        boolean bl;
        int n;
        float f = (float)this.getAttributeValue(Attributes.ATTACK_DAMAGE);
        float f2 = (float)this.getAttributeValue(Attributes.ATTACK_KNOCKBACK);
        if (entity instanceof LivingEntity) {
            f += EnchantmentHelper.getDamageBonus(this.getMainHandItem(), ((LivingEntity)entity).getMobType());
            f2 += (float)EnchantmentHelper.getKnockbackBonus(this);
        }
        if ((n = EnchantmentHelper.getFireAspect(this)) > 0) {
            entity.setSecondsOnFire(n * 4);
        }
        if (bl = entity.hurt(DamageSource.mobAttack(this), f)) {
            if (f2 > 0.0f && entity instanceof LivingEntity) {
                ((LivingEntity)entity).knockback(f2 * 0.5f, Mth.sin(this.yRot * 0.017453292f), -Mth.cos(this.yRot * 0.017453292f));
                this.setDeltaMovement(this.getDeltaMovement().multiply(0.6, 1.0, 0.6));
            }
            if (entity instanceof Player) {
                Player player;
                this.maybeDisableShield(player, this.getMainHandItem(), (player = (Player)entity).isUsingItem() ? player.getUseItem() : ItemStack.EMPTY);
            }
            this.doEnchantDamageEffects(this, entity);
            this.setLastHurtMob(entity);
        }
        return bl;
    }

    private void maybeDisableShield(Player player, ItemStack itemStack, ItemStack itemStack2) {
        if (!itemStack.isEmpty() && !itemStack2.isEmpty() && itemStack.getItem() instanceof AxeItem && itemStack2.getItem() == Items.SHIELD) {
            float f = 0.25f + (float)EnchantmentHelper.getBlockEfficiency(this) * 0.05f;
            if (this.random.nextFloat() < f) {
                player.getCooldowns().addCooldown(Items.SHIELD, 100);
                this.level.broadcastEntityEvent(player, (byte)30);
            }
        }
    }

    protected boolean isSunBurnTick() {
        if (this.level.isDay() && !this.level.isClientSide) {
            BlockPos blockPos;
            float f = this.getBrightness();
            BlockPos blockPos2 = blockPos = this.getVehicle() instanceof Boat ? new BlockPos(this.getX(), Math.round(this.getY()), this.getZ()).above() : new BlockPos(this.getX(), Math.round(this.getY()), this.getZ());
            if (f > 0.5f && this.random.nextFloat() * 30.0f < (f - 0.4f) * 2.0f && this.level.canSeeSky(blockPos)) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void jumpInLiquid(Tag<Fluid> tag) {
        if (this.getNavigation().canFloat()) {
            super.jumpInLiquid(tag);
        } else {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0, 0.3, 0.0));
        }
    }

    @Override
    protected void removeAfterChangingDimensions() {
        super.removeAfterChangingDimensions();
        this.dropLeash(true, false);
    }

}

