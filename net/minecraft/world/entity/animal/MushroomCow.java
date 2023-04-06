/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.tuple.Pair
 */
package net.minecraft.world.entity.animal;

import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.AgableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Shearable;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SuspiciousStewItem;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.tuple.Pair;

public class MushroomCow
extends Cow
implements Shearable {
    private static final EntityDataAccessor<String> DATA_TYPE = SynchedEntityData.defineId(MushroomCow.class, EntityDataSerializers.STRING);
    private MobEffect effect;
    private int effectDuration;
    private UUID lastLightningBoltUUID;

    public MushroomCow(EntityType<? extends MushroomCow> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public float getWalkTargetValue(BlockPos blockPos, LevelReader levelReader) {
        if (levelReader.getBlockState(blockPos.below()).is(Blocks.MYCELIUM)) {
            return 10.0f;
        }
        return levelReader.getBrightness(blockPos) - 0.5f;
    }

    public static boolean checkMushroomSpawnRules(EntityType<MushroomCow> entityType, LevelAccessor levelAccessor, MobSpawnType mobSpawnType, BlockPos blockPos, Random random) {
        return levelAccessor.getBlockState(blockPos.below()).is(Blocks.MYCELIUM) && levelAccessor.getRawBrightness(blockPos, 0) > 8;
    }

    @Override
    public void thunderHit(ServerLevel serverLevel, LightningBolt lightningBolt) {
        UUID uUID = lightningBolt.getUUID();
        if (!uUID.equals(this.lastLightningBoltUUID)) {
            this.setMushroomType(this.getMushroomType() == MushroomType.RED ? MushroomType.BROWN : MushroomType.RED);
            this.lastLightningBoltUUID = uUID;
            this.playSound(SoundEvents.MOOSHROOM_CONVERT, 2.0f, 1.0f);
        }
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_TYPE, MushroomType.RED.type);
    }

    @Override
    public InteractionResult mobInteract(Player player2, InteractionHand interactionHand) {
        ItemStack itemStack = player2.getItemInHand(interactionHand);
        if (itemStack.getItem() == Items.BOWL && !this.isBaby()) {
            ItemStack itemStack2;
            boolean bl = false;
            if (this.effect != null) {
                bl = true;
                itemStack2 = new ItemStack(Items.SUSPICIOUS_STEW);
                SuspiciousStewItem.saveMobEffect(itemStack2, this.effect, this.effectDuration);
                this.effect = null;
                this.effectDuration = 0;
            } else {
                itemStack2 = new ItemStack(Items.MUSHROOM_STEW);
            }
            ItemStack itemStack3 = ItemUtils.createFilledResult(itemStack, player2, itemStack2, false);
            player2.setItemInHand(interactionHand, itemStack3);
            SoundEvent soundEvent = bl ? SoundEvents.MOOSHROOM_MILK_SUSPICIOUSLY : SoundEvents.MOOSHROOM_MILK;
            this.playSound(soundEvent, 1.0f, 1.0f);
            return InteractionResult.sidedSuccess(this.level.isClientSide);
        }
        if (itemStack.getItem() == Items.SHEARS && this.readyForShearing()) {
            this.shear(SoundSource.PLAYERS);
            if (!this.level.isClientSide) {
                itemStack.hurtAndBreak(1, player2, player -> player.broadcastBreakEvent(interactionHand));
            }
            return InteractionResult.sidedSuccess(this.level.isClientSide);
        }
        if (this.getMushroomType() == MushroomType.BROWN && itemStack.getItem().is(ItemTags.SMALL_FLOWERS)) {
            if (this.effect != null) {
                for (int i = 0; i < 2; ++i) {
                    this.level.addParticle(ParticleTypes.SMOKE, this.getX() + this.random.nextDouble() / 2.0, this.getY(0.5), this.getZ() + this.random.nextDouble() / 2.0, 0.0, this.random.nextDouble() / 5.0, 0.0);
                }
            } else {
                Optional<Pair<MobEffect, Integer>> optional = this.getEffectFromItemStack(itemStack);
                if (!optional.isPresent()) {
                    return InteractionResult.PASS;
                }
                Pair<MobEffect, Integer> pair = optional.get();
                if (!player2.abilities.instabuild) {
                    itemStack.shrink(1);
                }
                for (int i = 0; i < 4; ++i) {
                    this.level.addParticle(ParticleTypes.EFFECT, this.getX() + this.random.nextDouble() / 2.0, this.getY(0.5), this.getZ() + this.random.nextDouble() / 2.0, 0.0, this.random.nextDouble() / 5.0, 0.0);
                }
                this.effect = (MobEffect)pair.getLeft();
                this.effectDuration = (Integer)pair.getRight();
                this.playSound(SoundEvents.MOOSHROOM_EAT, 2.0f, 1.0f);
            }
            return InteractionResult.sidedSuccess(this.level.isClientSide);
        }
        return super.mobInteract(player2, interactionHand);
    }

    @Override
    public void shear(SoundSource soundSource) {
        this.level.playSound(null, this, SoundEvents.MOOSHROOM_SHEAR, soundSource, 1.0f, 1.0f);
        if (!this.level.isClientSide()) {
            ((ServerLevel)this.level).sendParticles(ParticleTypes.EXPLOSION, this.getX(), this.getY(0.5), this.getZ(), 1, 0.0, 0.0, 0.0, 0.0);
            this.remove();
            Cow cow = EntityType.COW.create(this.level);
            cow.moveTo(this.getX(), this.getY(), this.getZ(), this.yRot, this.xRot);
            cow.setHealth(this.getHealth());
            cow.yBodyRot = this.yBodyRot;
            if (this.hasCustomName()) {
                cow.setCustomName(this.getCustomName());
                cow.setCustomNameVisible(this.isCustomNameVisible());
            }
            if (this.isPersistenceRequired()) {
                cow.setPersistenceRequired();
            }
            cow.setInvulnerable(this.isInvulnerable());
            this.level.addFreshEntity(cow);
            for (int i = 0; i < 5; ++i) {
                this.level.addFreshEntity(new ItemEntity(this.level, this.getX(), this.getY(1.0), this.getZ(), new ItemStack(this.getMushroomType().blockState.getBlock())));
            }
        }
    }

    @Override
    public boolean readyForShearing() {
        return this.isAlive() && !this.isBaby();
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        compoundTag.putString("Type", this.getMushroomType().type);
        if (this.effect != null) {
            compoundTag.putByte("EffectId", (byte)MobEffect.getId(this.effect));
            compoundTag.putInt("EffectDuration", this.effectDuration);
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        this.setMushroomType(MushroomType.byType(compoundTag.getString("Type")));
        if (compoundTag.contains("EffectId", 1)) {
            this.effect = MobEffect.byId(compoundTag.getByte("EffectId"));
        }
        if (compoundTag.contains("EffectDuration", 3)) {
            this.effectDuration = compoundTag.getInt("EffectDuration");
        }
    }

    private Optional<Pair<MobEffect, Integer>> getEffectFromItemStack(ItemStack itemStack) {
        Block block;
        Item item = itemStack.getItem();
        if (item instanceof BlockItem && (block = ((BlockItem)item).getBlock()) instanceof FlowerBlock) {
            FlowerBlock flowerBlock = (FlowerBlock)block;
            return Optional.of(Pair.of((Object)flowerBlock.getSuspiciousStewEffect(), (Object)flowerBlock.getEffectDuration()));
        }
        return Optional.empty();
    }

    private void setMushroomType(MushroomType mushroomType) {
        this.entityData.set(DATA_TYPE, mushroomType.type);
    }

    public MushroomType getMushroomType() {
        return MushroomType.byType(this.entityData.get(MushroomCow.DATA_TYPE));
    }

    @Override
    public MushroomCow getBreedOffspring(ServerLevel serverLevel, AgableMob agableMob) {
        MushroomCow mushroomCow = EntityType.MOOSHROOM.create(serverLevel);
        mushroomCow.setMushroomType(this.getOffspringType((MushroomCow)agableMob));
        return mushroomCow;
    }

    private MushroomType getOffspringType(MushroomCow mushroomCow) {
        MushroomType mushroomType;
        MushroomType mushroomType2 = this.getMushroomType();
        MushroomType mushroomType3 = mushroomType2 == (mushroomType = mushroomCow.getMushroomType()) && this.random.nextInt(1024) == 0 ? (mushroomType2 == MushroomType.BROWN ? MushroomType.RED : MushroomType.BROWN) : (this.random.nextBoolean() ? mushroomType2 : mushroomType);
        return mushroomType3;
    }

    @Override
    public /* synthetic */ Cow getBreedOffspring(ServerLevel serverLevel, AgableMob agableMob) {
        return this.getBreedOffspring(serverLevel, agableMob);
    }

    @Override
    public /* synthetic */ AgableMob getBreedOffspring(ServerLevel serverLevel, AgableMob agableMob) {
        return this.getBreedOffspring(serverLevel, agableMob);
    }

    public static enum MushroomType {
        RED("red", Blocks.RED_MUSHROOM.defaultBlockState()),
        BROWN("brown", Blocks.BROWN_MUSHROOM.defaultBlockState());
        
        private final String type;
        private final BlockState blockState;

        private MushroomType(String string2, BlockState blockState) {
            this.type = string2;
            this.blockState = blockState;
        }

        public BlockState getBlockState() {
            return this.blockState;
        }

        private static MushroomType byType(String string) {
            for (MushroomType mushroomType : MushroomType.values()) {
                if (!mushroomType.type.equals(string)) continue;
                return mushroomType;
            }
            return RED;
        }
    }

}

