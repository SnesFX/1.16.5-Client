/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.block.entity;

import java.util.Optional;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.Clearable;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CampfireCookingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.TickableBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;

public class CampfireBlockEntity
extends BlockEntity
implements Clearable,
TickableBlockEntity {
    private final NonNullList<ItemStack> items = NonNullList.withSize(4, ItemStack.EMPTY);
    private final int[] cookingProgress = new int[4];
    private final int[] cookingTime = new int[4];

    public CampfireBlockEntity() {
        super(BlockEntityType.CAMPFIRE);
    }

    @Override
    public void tick() {
        boolean bl = this.getBlockState().getValue(CampfireBlock.LIT);
        boolean bl2 = this.level.isClientSide;
        if (bl2) {
            if (bl) {
                this.makeParticles();
            }
            return;
        }
        if (bl) {
            this.cook();
        } else {
            for (int i = 0; i < this.items.size(); ++i) {
                if (this.cookingProgress[i] <= 0) continue;
                this.cookingProgress[i] = Mth.clamp(this.cookingProgress[i] - 2, 0, this.cookingTime[i]);
            }
        }
    }

    private void cook() {
        for (int i = 0; i < this.items.size(); ++i) {
            ItemStack itemStack = this.items.get(i);
            if (itemStack.isEmpty()) continue;
            int[] arrn = this.cookingProgress;
            int n = i;
            arrn[n] = arrn[n] + 1;
            if (this.cookingProgress[i] < this.cookingTime[i]) continue;
            SimpleContainer simpleContainer = new SimpleContainer(itemStack);
            ItemStack itemStack2 = this.level.getRecipeManager().getRecipeFor(RecipeType.CAMPFIRE_COOKING, simpleContainer, this.level).map(campfireCookingRecipe -> campfireCookingRecipe.assemble(simpleContainer)).orElse(itemStack);
            BlockPos blockPos = this.getBlockPos();
            Containers.dropItemStack(this.level, blockPos.getX(), blockPos.getY(), blockPos.getZ(), itemStack2);
            this.items.set(i, ItemStack.EMPTY);
            this.markUpdated();
        }
    }

    private void makeParticles() {
        int n;
        Level level = this.getLevel();
        if (level == null) {
            return;
        }
        BlockPos blockPos = this.getBlockPos();
        Random random = level.random;
        if (random.nextFloat() < 0.11f) {
            for (n = 0; n < random.nextInt(2) + 2; ++n) {
                CampfireBlock.makeParticles(level, blockPos, this.getBlockState().getValue(CampfireBlock.SIGNAL_FIRE), false);
            }
        }
        n = this.getBlockState().getValue(CampfireBlock.FACING).get2DDataValue();
        for (int i = 0; i < this.items.size(); ++i) {
            if (this.items.get(i).isEmpty() || !(random.nextFloat() < 0.2f)) continue;
            Direction direction = Direction.from2DDataValue(Math.floorMod(i + n, 4));
            float f = 0.3125f;
            double d = (double)blockPos.getX() + 0.5 - (double)((float)direction.getStepX() * 0.3125f) + (double)((float)direction.getClockWise().getStepX() * 0.3125f);
            double d2 = (double)blockPos.getY() + 0.5;
            double d3 = (double)blockPos.getZ() + 0.5 - (double)((float)direction.getStepZ() * 0.3125f) + (double)((float)direction.getClockWise().getStepZ() * 0.3125f);
            for (int j = 0; j < 4; ++j) {
                level.addParticle(ParticleTypes.SMOKE, d, d2, d3, 0.0, 5.0E-4, 0.0);
            }
        }
    }

    public NonNullList<ItemStack> getItems() {
        return this.items;
    }

    @Override
    public void load(BlockState blockState, CompoundTag compoundTag) {
        int[] arrn;
        super.load(blockState, compoundTag);
        this.items.clear();
        ContainerHelper.loadAllItems(compoundTag, this.items);
        if (compoundTag.contains("CookingTimes", 11)) {
            arrn = compoundTag.getIntArray("CookingTimes");
            System.arraycopy(arrn, 0, this.cookingProgress, 0, Math.min(this.cookingTime.length, arrn.length));
        }
        if (compoundTag.contains("CookingTotalTimes", 11)) {
            arrn = compoundTag.getIntArray("CookingTotalTimes");
            System.arraycopy(arrn, 0, this.cookingTime, 0, Math.min(this.cookingTime.length, arrn.length));
        }
    }

    @Override
    public CompoundTag save(CompoundTag compoundTag) {
        this.saveMetadataAndItems(compoundTag);
        compoundTag.putIntArray("CookingTimes", this.cookingProgress);
        compoundTag.putIntArray("CookingTotalTimes", this.cookingTime);
        return compoundTag;
    }

    private CompoundTag saveMetadataAndItems(CompoundTag compoundTag) {
        super.save(compoundTag);
        ContainerHelper.saveAllItems(compoundTag, this.items, true);
        return compoundTag;
    }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return new ClientboundBlockEntityDataPacket(this.worldPosition, 13, this.getUpdateTag());
    }

    @Override
    public CompoundTag getUpdateTag() {
        return this.saveMetadataAndItems(new CompoundTag());
    }

    public Optional<CampfireCookingRecipe> getCookableRecipe(ItemStack itemStack) {
        if (this.items.stream().noneMatch(ItemStack::isEmpty)) {
            return Optional.empty();
        }
        return this.level.getRecipeManager().getRecipeFor(RecipeType.CAMPFIRE_COOKING, new SimpleContainer(itemStack), this.level);
    }

    public boolean placeFood(ItemStack itemStack, int n) {
        for (int i = 0; i < this.items.size(); ++i) {
            ItemStack itemStack2 = this.items.get(i);
            if (!itemStack2.isEmpty()) continue;
            this.cookingTime[i] = n;
            this.cookingProgress[i] = 0;
            this.items.set(i, itemStack.split(1));
            this.markUpdated();
            return true;
        }
        return false;
    }

    private void markUpdated() {
        this.setChanged();
        this.getLevel().sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
    }

    @Override
    public void clearContent() {
        this.items.clear();
    }

    public void dowse() {
        if (this.level != null) {
            if (!this.level.isClientSide) {
                Containers.dropContents(this.level, this.getBlockPos(), this.getItems());
            }
            this.markUpdated();
        }
    }
}

