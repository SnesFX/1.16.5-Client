/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.entity.projectile;

import java.util.Random;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class EyeOfEnder
extends Entity
implements ItemSupplier {
    private static final EntityDataAccessor<ItemStack> DATA_ITEM_STACK = SynchedEntityData.defineId(EyeOfEnder.class, EntityDataSerializers.ITEM_STACK);
    private double tx;
    private double ty;
    private double tz;
    private int life;
    private boolean surviveAfterDeath;

    public EyeOfEnder(EntityType<? extends EyeOfEnder> entityType, Level level) {
        super(entityType, level);
    }

    public EyeOfEnder(Level level, double d, double d2, double d3) {
        this(EntityType.EYE_OF_ENDER, level);
        this.life = 0;
        this.setPos(d, d2, d3);
    }

    public void setItem(ItemStack itemStack2) {
        if (itemStack2.getItem() != Items.ENDER_EYE || itemStack2.hasTag()) {
            this.getEntityData().set(DATA_ITEM_STACK, Util.make(itemStack2.copy(), itemStack -> itemStack.setCount(1)));
        }
    }

    private ItemStack getItemRaw() {
        return this.getEntityData().get(DATA_ITEM_STACK);
    }

    @Override
    public ItemStack getItem() {
        ItemStack itemStack = this.getItemRaw();
        return itemStack.isEmpty() ? new ItemStack(Items.ENDER_EYE) : itemStack;
    }

    @Override
    protected void defineSynchedData() {
        this.getEntityData().define(DATA_ITEM_STACK, ItemStack.EMPTY);
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double d) {
        double d2 = this.getBoundingBox().getSize() * 4.0;
        if (Double.isNaN(d2)) {
            d2 = 4.0;
        }
        return d < (d2 *= 64.0) * d2;
    }

    public void signalTo(BlockPos blockPos) {
        double d;
        double d2 = blockPos.getX();
        int n = blockPos.getY();
        double d3 = blockPos.getZ();
        double d4 = d2 - this.getX();
        float f = Mth.sqrt(d4 * d4 + (d = d3 - this.getZ()) * d);
        if (f > 12.0f) {
            this.tx = this.getX() + d4 / (double)f * 12.0;
            this.tz = this.getZ() + d / (double)f * 12.0;
            this.ty = this.getY() + 8.0;
        } else {
            this.tx = d2;
            this.ty = n;
            this.tz = d3;
        }
        this.life = 0;
        this.surviveAfterDeath = this.random.nextInt(5) > 0;
    }

    @Override
    public void lerpMotion(double d, double d2, double d3) {
        this.setDeltaMovement(d, d2, d3);
        if (this.xRotO == 0.0f && this.yRotO == 0.0f) {
            float f = Mth.sqrt(d * d + d3 * d3);
            this.yRot = (float)(Mth.atan2(d, d3) * 57.2957763671875);
            this.xRot = (float)(Mth.atan2(d2, f) * 57.2957763671875);
            this.yRotO = this.yRot;
            this.xRotO = this.xRot;
        }
    }

    @Override
    public void tick() {
        super.tick();
        Vec3 vec3 = this.getDeltaMovement();
        double d = this.getX() + vec3.x;
        double d2 = this.getY() + vec3.y;
        double d3 = this.getZ() + vec3.z;
        float f = Mth.sqrt(EyeOfEnder.getHorizontalDistanceSqr(vec3));
        this.xRot = Projectile.lerpRotation(this.xRotO, (float)(Mth.atan2(vec3.y, f) * 57.2957763671875));
        this.yRot = Projectile.lerpRotation(this.yRotO, (float)(Mth.atan2(vec3.x, vec3.z) * 57.2957763671875));
        if (!this.level.isClientSide) {
            double d4 = this.tx - d;
            double d5 = this.tz - d3;
            float f2 = (float)Math.sqrt(d4 * d4 + d5 * d5);
            float f3 = (float)Mth.atan2(d5, d4);
            double d6 = Mth.lerp(0.0025, (double)f, (double)f2);
            double d7 = vec3.y;
            if (f2 < 1.0f) {
                d6 *= 0.8;
                d7 *= 0.8;
            }
            int n = this.getY() < this.ty ? 1 : -1;
            vec3 = new Vec3(Math.cos(f3) * d6, d7 + ((double)n - d7) * 0.014999999664723873, Math.sin(f3) * d6);
            this.setDeltaMovement(vec3);
        }
        float f4 = 0.25f;
        if (this.isInWater()) {
            for (int i = 0; i < 4; ++i) {
                this.level.addParticle(ParticleTypes.BUBBLE, d - vec3.x * 0.25, d2 - vec3.y * 0.25, d3 - vec3.z * 0.25, vec3.x, vec3.y, vec3.z);
            }
        } else {
            this.level.addParticle(ParticleTypes.PORTAL, d - vec3.x * 0.25 + this.random.nextDouble() * 0.6 - 0.3, d2 - vec3.y * 0.25 - 0.5, d3 - vec3.z * 0.25 + this.random.nextDouble() * 0.6 - 0.3, vec3.x, vec3.y, vec3.z);
        }
        if (!this.level.isClientSide) {
            this.setPos(d, d2, d3);
            ++this.life;
            if (this.life > 80 && !this.level.isClientSide) {
                this.playSound(SoundEvents.ENDER_EYE_DEATH, 1.0f, 1.0f);
                this.remove();
                if (this.surviveAfterDeath) {
                    this.level.addFreshEntity(new ItemEntity(this.level, this.getX(), this.getY(), this.getZ(), this.getItem()));
                } else {
                    this.level.levelEvent(2003, this.blockPosition(), 0);
                }
            }
        } else {
            this.setPosRaw(d, d2, d3);
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compoundTag) {
        ItemStack itemStack = this.getItemRaw();
        if (!itemStack.isEmpty()) {
            compoundTag.put("Item", itemStack.save(new CompoundTag()));
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compoundTag) {
        ItemStack itemStack = ItemStack.of(compoundTag.getCompound("Item"));
        this.setItem(itemStack);
    }

    @Override
    public float getBrightness() {
        return 1.0f;
    }

    @Override
    public boolean isAttackable() {
        return false;
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this);
    }
}

