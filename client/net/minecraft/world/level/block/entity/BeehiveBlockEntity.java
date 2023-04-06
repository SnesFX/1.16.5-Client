/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.block.entity;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.TickableBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BeehiveBlockEntity
extends BlockEntity
implements TickableBlockEntity {
    private final List<BeeData> stored = Lists.newArrayList();
    @Nullable
    private BlockPos savedFlowerPos = null;

    public BeehiveBlockEntity() {
        super(BlockEntityType.BEEHIVE);
    }

    @Override
    public void setChanged() {
        if (this.isFireNearby()) {
            this.emptyAllLivingFromHive(null, this.level.getBlockState(this.getBlockPos()), BeeReleaseStatus.EMERGENCY);
        }
        super.setChanged();
    }

    public boolean isFireNearby() {
        if (this.level == null) {
            return false;
        }
        for (BlockPos blockPos : BlockPos.betweenClosed(this.worldPosition.offset(-1, -1, -1), this.worldPosition.offset(1, 1, 1))) {
            if (!(this.level.getBlockState(blockPos).getBlock() instanceof FireBlock)) continue;
            return true;
        }
        return false;
    }

    public boolean isEmpty() {
        return this.stored.isEmpty();
    }

    public boolean isFull() {
        return this.stored.size() == 3;
    }

    public void emptyAllLivingFromHive(@Nullable Player player, BlockState blockState, BeeReleaseStatus beeReleaseStatus) {
        List<Entity> list = this.releaseAllOccupants(blockState, beeReleaseStatus);
        if (player != null) {
            for (Entity entity : list) {
                if (!(entity instanceof Bee)) continue;
                Bee bee = (Bee)entity;
                if (!(player.position().distanceToSqr(entity.position()) <= 16.0)) continue;
                if (!this.isSedated()) {
                    bee.setTarget(player);
                    continue;
                }
                bee.setStayOutOfHiveCountdown(400);
            }
        }
    }

    private List<Entity> releaseAllOccupants(BlockState blockState, BeeReleaseStatus beeReleaseStatus) {
        ArrayList arrayList = Lists.newArrayList();
        this.stored.removeIf(beeData -> this.releaseOccupant(blockState, (BeeData)beeData, arrayList, beeReleaseStatus));
        return arrayList;
    }

    public void addOccupant(Entity entity, boolean bl) {
        this.addOccupantWithPresetTicks(entity, bl, 0);
    }

    public int getOccupantCount() {
        return this.stored.size();
    }

    public static int getHoneyLevel(BlockState blockState) {
        return blockState.getValue(BeehiveBlock.HONEY_LEVEL);
    }

    public boolean isSedated() {
        return CampfireBlock.isSmokeyPos(this.level, this.getBlockPos());
    }

    protected void sendDebugPackets() {
        DebugPackets.sendHiveInfo(this);
    }

    public void addOccupantWithPresetTicks(Entity entity, boolean bl, int n) {
        if (this.stored.size() >= 3) {
            return;
        }
        entity.stopRiding();
        entity.ejectPassengers();
        CompoundTag compoundTag = new CompoundTag();
        entity.save(compoundTag);
        this.stored.add(new BeeData(compoundTag, n, bl ? 2400 : 600));
        if (this.level != null) {
            Object object;
            if (entity instanceof Bee && ((Bee)(object = (Bee)entity)).hasSavedFlowerPos() && (!this.hasSavedFlowerPos() || this.level.random.nextBoolean())) {
                this.savedFlowerPos = ((Bee)object).getSavedFlowerPos();
            }
            object = this.getBlockPos();
            this.level.playSound(null, ((Vec3i)object).getX(), ((Vec3i)object).getY(), ((Vec3i)object).getZ(), SoundEvents.BEEHIVE_ENTER, SoundSource.BLOCKS, 1.0f, 1.0f);
        }
        entity.remove();
    }

    private boolean releaseOccupant(BlockState blockState, BeeData beeData, @Nullable List<Entity> list, BeeReleaseStatus beeReleaseStatus) {
        boolean bl;
        if ((this.level.isNight() || this.level.isRaining()) && beeReleaseStatus != BeeReleaseStatus.EMERGENCY) {
            return false;
        }
        BlockPos blockPos = this.getBlockPos();
        CompoundTag compoundTag = beeData.entityData;
        compoundTag.remove("Passengers");
        compoundTag.remove("Leash");
        compoundTag.remove("UUID");
        Direction direction = blockState.getValue(BeehiveBlock.FACING);
        BlockPos blockPos2 = blockPos.relative(direction);
        boolean bl2 = bl = !this.level.getBlockState(blockPos2).getCollisionShape(this.level, blockPos2).isEmpty();
        if (bl && beeReleaseStatus != BeeReleaseStatus.EMERGENCY) {
            return false;
        }
        Entity entity2 = EntityType.loadEntityRecursive(compoundTag, this.level, entity -> entity);
        if (entity2 != null) {
            if (!entity2.getType().is(EntityTypeTags.BEEHIVE_INHABITORS)) {
                return false;
            }
            if (entity2 instanceof Bee) {
                Bee bee = (Bee)entity2;
                if (this.hasSavedFlowerPos() && !bee.hasSavedFlowerPos() && this.level.random.nextFloat() < 0.9f) {
                    bee.setSavedFlowerPos(this.savedFlowerPos);
                }
                if (beeReleaseStatus == BeeReleaseStatus.HONEY_DELIVERED) {
                    int n;
                    bee.dropOffNectar();
                    if (blockState.getBlock().is(BlockTags.BEEHIVES) && (n = BeehiveBlockEntity.getHoneyLevel(blockState)) < 5) {
                        int n2;
                        int n3 = n2 = this.level.random.nextInt(100) == 0 ? 2 : 1;
                        if (n + n2 > 5) {
                            --n2;
                        }
                        this.level.setBlockAndUpdate(this.getBlockPos(), (BlockState)blockState.setValue(BeehiveBlock.HONEY_LEVEL, n + n2));
                    }
                }
                this.setBeeReleaseData(beeData.ticksInHive, bee);
                if (list != null) {
                    list.add(bee);
                }
                float f = entity2.getBbWidth();
                double d = bl ? 0.0 : 0.55 + (double)(f / 2.0f);
                double d2 = (double)blockPos.getX() + 0.5 + d * (double)direction.getStepX();
                double d3 = (double)blockPos.getY() + 0.5 - (double)(entity2.getBbHeight() / 2.0f);
                double d4 = (double)blockPos.getZ() + 0.5 + d * (double)direction.getStepZ();
                entity2.moveTo(d2, d3, d4, entity2.yRot, entity2.xRot);
            }
            this.level.playSound(null, blockPos, SoundEvents.BEEHIVE_EXIT, SoundSource.BLOCKS, 1.0f, 1.0f);
            return this.level.addFreshEntity(entity2);
        }
        return false;
    }

    private void setBeeReleaseData(int n, Bee bee) {
        int n2 = bee.getAge();
        if (n2 < 0) {
            bee.setAge(Math.min(0, n2 + n));
        } else if (n2 > 0) {
            bee.setAge(Math.max(0, n2 - n));
        }
        bee.setInLoveTime(Math.max(0, bee.getInLoveTime() - n));
        bee.resetTicksWithoutNectarSinceExitingHive();
    }

    private boolean hasSavedFlowerPos() {
        return this.savedFlowerPos != null;
    }

    private void tickOccupants() {
        Iterator<BeeData> iterator = this.stored.iterator();
        BlockState blockState = this.getBlockState();
        while (iterator.hasNext()) {
            BeeData beeData = iterator.next();
            if (beeData.ticksInHive > beeData.minOccupationTicks) {
                BeeReleaseStatus beeReleaseStatus;
                BeeReleaseStatus beeReleaseStatus2 = beeReleaseStatus = beeData.entityData.getBoolean("HasNectar") ? BeeReleaseStatus.HONEY_DELIVERED : BeeReleaseStatus.BEE_RELEASED;
                if (this.releaseOccupant(blockState, beeData, null, beeReleaseStatus)) {
                    iterator.remove();
                }
            }
            beeData.ticksInHive++;
        }
    }

    @Override
    public void tick() {
        if (this.level.isClientSide) {
            return;
        }
        this.tickOccupants();
        BlockPos blockPos = this.getBlockPos();
        if (this.stored.size() > 0 && this.level.getRandom().nextDouble() < 0.005) {
            double d = (double)blockPos.getX() + 0.5;
            double d2 = blockPos.getY();
            double d3 = (double)blockPos.getZ() + 0.5;
            this.level.playSound(null, d, d2, d3, SoundEvents.BEEHIVE_WORK, SoundSource.BLOCKS, 1.0f, 1.0f);
        }
        this.sendDebugPackets();
    }

    @Override
    public void load(BlockState blockState, CompoundTag compoundTag) {
        super.load(blockState, compoundTag);
        this.stored.clear();
        ListTag listTag = compoundTag.getList("Bees", 10);
        for (int i = 0; i < listTag.size(); ++i) {
            CompoundTag compoundTag2 = listTag.getCompound(i);
            BeeData beeData = new BeeData(compoundTag2.getCompound("EntityData"), compoundTag2.getInt("TicksInHive"), compoundTag2.getInt("MinOccupationTicks"));
            this.stored.add(beeData);
        }
        this.savedFlowerPos = null;
        if (compoundTag.contains("FlowerPos")) {
            this.savedFlowerPos = NbtUtils.readBlockPos(compoundTag.getCompound("FlowerPos"));
        }
    }

    @Override
    public CompoundTag save(CompoundTag compoundTag) {
        super.save(compoundTag);
        compoundTag.put("Bees", this.writeBees());
        if (this.hasSavedFlowerPos()) {
            compoundTag.put("FlowerPos", NbtUtils.writeBlockPos(this.savedFlowerPos));
        }
        return compoundTag;
    }

    public ListTag writeBees() {
        ListTag listTag = new ListTag();
        for (BeeData beeData : this.stored) {
            beeData.entityData.remove("UUID");
            CompoundTag compoundTag = new CompoundTag();
            compoundTag.put("EntityData", beeData.entityData);
            compoundTag.putInt("TicksInHive", beeData.ticksInHive);
            compoundTag.putInt("MinOccupationTicks", beeData.minOccupationTicks);
            listTag.add(compoundTag);
        }
        return listTag;
    }

    static class BeeData {
        private final CompoundTag entityData;
        private int ticksInHive;
        private final int minOccupationTicks;

        private BeeData(CompoundTag compoundTag, int n, int n2) {
            compoundTag.remove("UUID");
            this.entityData = compoundTag;
            this.ticksInHive = n;
            this.minOccupationTicks = n2;
        }
    }

    public static enum BeeReleaseStatus {
        HONEY_DELIVERED,
        BEE_RELEASED,
        EMERGENCY;
        
    }

}

