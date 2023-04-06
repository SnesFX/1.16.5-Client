/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Lists
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.block.entity;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.critereon.ConstructBeaconTrigger;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.Container;
import net.minecraft.world.LockCode;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.BeaconMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BeaconBeamBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.TickableBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;

public class BeaconBlockEntity
extends BlockEntity
implements MenuProvider,
TickableBlockEntity {
    public static final MobEffect[][] BEACON_EFFECTS = new MobEffect[][]{{MobEffects.MOVEMENT_SPEED, MobEffects.DIG_SPEED}, {MobEffects.DAMAGE_RESISTANCE, MobEffects.JUMP}, {MobEffects.DAMAGE_BOOST}, {MobEffects.REGENERATION}};
    private static final Set<MobEffect> VALID_EFFECTS = Arrays.stream(BEACON_EFFECTS).flatMap(Arrays::stream).collect(Collectors.toSet());
    private List<BeaconBeamSection> beamSections = Lists.newArrayList();
    private List<BeaconBeamSection> checkingBeamSections = Lists.newArrayList();
    private int levels;
    private int lastCheckY = -1;
    @Nullable
    private MobEffect primaryPower;
    @Nullable
    private MobEffect secondaryPower;
    @Nullable
    private Component name;
    private LockCode lockKey = LockCode.NO_LOCK;
    private final ContainerData dataAccess = new ContainerData(){

        @Override
        public int get(int n) {
            switch (n) {
                case 0: {
                    return BeaconBlockEntity.this.levels;
                }
                case 1: {
                    return MobEffect.getId(BeaconBlockEntity.this.primaryPower);
                }
                case 2: {
                    return MobEffect.getId(BeaconBlockEntity.this.secondaryPower);
                }
            }
            return 0;
        }

        @Override
        public void set(int n, int n2) {
            switch (n) {
                case 0: {
                    BeaconBlockEntity.this.levels = n2;
                    break;
                }
                case 1: {
                    if (!BeaconBlockEntity.this.level.isClientSide && !BeaconBlockEntity.this.beamSections.isEmpty()) {
                        BeaconBlockEntity.this.playSound(SoundEvents.BEACON_POWER_SELECT);
                    }
                    BeaconBlockEntity.this.primaryPower = BeaconBlockEntity.getValidEffectById(n2);
                    break;
                }
                case 2: {
                    BeaconBlockEntity.this.secondaryPower = BeaconBlockEntity.getValidEffectById(n2);
                }
            }
        }

        @Override
        public int getCount() {
            return 3;
        }
    };

    public BeaconBlockEntity() {
        super(BlockEntityType.BEACON);
    }

    @Override
    public void tick() {
        int n;
        BlockPos blockPos;
        int n2 = this.worldPosition.getX();
        int n3 = this.worldPosition.getY();
        int n4 = this.worldPosition.getZ();
        if (this.lastCheckY < n3) {
            blockPos = this.worldPosition;
            this.checkingBeamSections = Lists.newArrayList();
            this.lastCheckY = blockPos.getY() - 1;
        } else {
            blockPos = new BlockPos(n2, this.lastCheckY + 1, n4);
        }
        BeaconBeamSection beaconBeamSection = this.checkingBeamSections.isEmpty() ? null : this.checkingBeamSections.get(this.checkingBeamSections.size() - 1);
        int n5 = this.level.getHeight(Heightmap.Types.WORLD_SURFACE, n2, n4);
        for (n = 0; n < 10 && blockPos.getY() <= n5; ++n) {
            block18 : {
                BlockState blockState;
                Block block;
                block16 : {
                    float[] arrf;
                    block17 : {
                        blockState = this.level.getBlockState(blockPos);
                        block = blockState.getBlock();
                        if (!(block instanceof BeaconBeamBlock)) break block16;
                        arrf = ((BeaconBeamBlock)((Object)block)).getColor().getTextureDiffuseColors();
                        if (this.checkingBeamSections.size() > 1) break block17;
                        beaconBeamSection = new BeaconBeamSection(arrf);
                        this.checkingBeamSections.add(beaconBeamSection);
                        break block18;
                    }
                    if (beaconBeamSection == null) break block18;
                    if (Arrays.equals(arrf, beaconBeamSection.color)) {
                        beaconBeamSection.increaseHeight();
                    } else {
                        beaconBeamSection = new BeaconBeamSection(new float[]{(beaconBeamSection.color[0] + arrf[0]) / 2.0f, (beaconBeamSection.color[1] + arrf[1]) / 2.0f, (beaconBeamSection.color[2] + arrf[2]) / 2.0f});
                        this.checkingBeamSections.add(beaconBeamSection);
                    }
                    break block18;
                }
                if (beaconBeamSection != null && (blockState.getLightBlock(this.level, blockPos) < 15 || block == Blocks.BEDROCK)) {
                    beaconBeamSection.increaseHeight();
                } else {
                    this.checkingBeamSections.clear();
                    this.lastCheckY = n5;
                    break;
                }
            }
            blockPos = blockPos.above();
            ++this.lastCheckY;
        }
        n = this.levels;
        if (this.level.getGameTime() % 80L == 0L) {
            if (!this.beamSections.isEmpty()) {
                this.updateBase(n2, n3, n4);
            }
            if (this.levels > 0 && !this.beamSections.isEmpty()) {
                this.applyEffects();
                this.playSound(SoundEvents.BEACON_AMBIENT);
            }
        }
        if (this.lastCheckY >= n5) {
            this.lastCheckY = -1;
            boolean bl = n > 0;
            this.beamSections = this.checkingBeamSections;
            if (!this.level.isClientSide) {
                boolean bl2;
                boolean bl3 = bl2 = this.levels > 0;
                if (!bl && bl2) {
                    this.playSound(SoundEvents.BEACON_ACTIVATE);
                    for (ServerPlayer serverPlayer : this.level.getEntitiesOfClass(ServerPlayer.class, new AABB(n2, n3, n4, n2, n3 - 4, n4).inflate(10.0, 5.0, 10.0))) {
                        CriteriaTriggers.CONSTRUCT_BEACON.trigger(serverPlayer, this);
                    }
                } else if (bl && !bl2) {
                    this.playSound(SoundEvents.BEACON_DEACTIVATE);
                }
            }
        }
    }

    private void updateBase(int n, int n2, int n3) {
        int n4;
        this.levels = 0;
        int n5 = 1;
        while (n5 <= 4 && (n4 = n2 - n5) >= 0) {
            boolean bl = true;
            block1 : for (int i = n - n5; i <= n + n5 && bl; ++i) {
                for (int j = n3 - n5; j <= n3 + n5; ++j) {
                    if (this.level.getBlockState(new BlockPos(i, n4, j)).is(BlockTags.BEACON_BASE_BLOCKS)) continue;
                    bl = false;
                    continue block1;
                }
            }
            if (!bl) break;
            this.levels = n5++;
        }
    }

    @Override
    public void setRemoved() {
        this.playSound(SoundEvents.BEACON_DEACTIVATE);
        super.setRemoved();
    }

    private void applyEffects() {
        if (this.level.isClientSide || this.primaryPower == null) {
            return;
        }
        double d = this.levels * 10 + 10;
        int n = 0;
        if (this.levels >= 4 && this.primaryPower == this.secondaryPower) {
            n = 1;
        }
        int n2 = (9 + this.levels * 2) * 20;
        AABB aABB = new AABB(this.worldPosition).inflate(d).expandTowards(0.0, this.level.getMaxBuildHeight(), 0.0);
        List<Player> list = this.level.getEntitiesOfClass(Player.class, aABB);
        for (Player player : list) {
            player.addEffect(new MobEffectInstance(this.primaryPower, n2, n, true, true));
        }
        if (this.levels >= 4 && this.primaryPower != this.secondaryPower && this.secondaryPower != null) {
            for (Player player : list) {
                player.addEffect(new MobEffectInstance(this.secondaryPower, n2, 0, true, true));
            }
        }
    }

    public void playSound(SoundEvent soundEvent) {
        this.level.playSound(null, this.worldPosition, soundEvent, SoundSource.BLOCKS, 1.0f, 1.0f);
    }

    public List<BeaconBeamSection> getBeamSections() {
        return this.levels == 0 ? ImmutableList.of() : this.beamSections;
    }

    public int getLevels() {
        return this.levels;
    }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return new ClientboundBlockEntityDataPacket(this.worldPosition, 3, this.getUpdateTag());
    }

    @Override
    public CompoundTag getUpdateTag() {
        return this.save(new CompoundTag());
    }

    @Override
    public double getViewDistance() {
        return 256.0;
    }

    @Nullable
    private static MobEffect getValidEffectById(int n) {
        MobEffect mobEffect = MobEffect.byId(n);
        return VALID_EFFECTS.contains(mobEffect) ? mobEffect : null;
    }

    @Override
    public void load(BlockState blockState, CompoundTag compoundTag) {
        super.load(blockState, compoundTag);
        this.primaryPower = BeaconBlockEntity.getValidEffectById(compoundTag.getInt("Primary"));
        this.secondaryPower = BeaconBlockEntity.getValidEffectById(compoundTag.getInt("Secondary"));
        if (compoundTag.contains("CustomName", 8)) {
            this.name = Component.Serializer.fromJson(compoundTag.getString("CustomName"));
        }
        this.lockKey = LockCode.fromTag(compoundTag);
    }

    @Override
    public CompoundTag save(CompoundTag compoundTag) {
        super.save(compoundTag);
        compoundTag.putInt("Primary", MobEffect.getId(this.primaryPower));
        compoundTag.putInt("Secondary", MobEffect.getId(this.secondaryPower));
        compoundTag.putInt("Levels", this.levels);
        if (this.name != null) {
            compoundTag.putString("CustomName", Component.Serializer.toJson(this.name));
        }
        this.lockKey.addToTag(compoundTag);
        return compoundTag;
    }

    public void setCustomName(@Nullable Component component) {
        this.name = component;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int n, Inventory inventory, Player player) {
        if (BaseContainerBlockEntity.canUnlock(player, this.lockKey, this.getDisplayName())) {
            return new BeaconMenu(n, inventory, this.dataAccess, ContainerLevelAccess.create(this.level, this.getBlockPos()));
        }
        return null;
    }

    @Override
    public Component getDisplayName() {
        return this.name != null ? this.name : new TranslatableComponent("container.beacon");
    }

    public static class BeaconBeamSection {
        private final float[] color;
        private int height;

        public BeaconBeamSection(float[] arrf) {
            this.color = arrf;
            this.height = 1;
        }

        protected void increaseHeight() {
            ++this.height;
        }

        public float[] getColor() {
            return this.color;
        }

        public int getHeight() {
            return this.height;
        }
    }

}

