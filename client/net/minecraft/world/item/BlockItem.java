/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.world.item;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.critereon.PlacedBlockTrigger;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.shapes.CollisionContext;

public class BlockItem
extends Item {
    @Deprecated
    private final Block block;

    public BlockItem(Block block, Item.Properties properties) {
        super(properties);
        this.block = block;
    }

    @Override
    public InteractionResult useOn(UseOnContext useOnContext) {
        InteractionResult interactionResult = this.place(new BlockPlaceContext(useOnContext));
        if (!interactionResult.consumesAction() && this.isEdible()) {
            return this.use(useOnContext.getLevel(), useOnContext.getPlayer(), useOnContext.getHand()).getResult();
        }
        return interactionResult;
    }

    public InteractionResult place(BlockPlaceContext blockPlaceContext) {
        if (!blockPlaceContext.canPlace()) {
            return InteractionResult.FAIL;
        }
        BlockPlaceContext blockPlaceContext2 = this.updatePlacementContext(blockPlaceContext);
        if (blockPlaceContext2 == null) {
            return InteractionResult.FAIL;
        }
        BlockState blockState = this.getPlacementState(blockPlaceContext2);
        if (blockState == null) {
            return InteractionResult.FAIL;
        }
        if (!this.placeBlock(blockPlaceContext2, blockState)) {
            return InteractionResult.FAIL;
        }
        BlockPos blockPos = blockPlaceContext2.getClickedPos();
        Level level = blockPlaceContext2.getLevel();
        Player player = blockPlaceContext2.getPlayer();
        ItemStack itemStack = blockPlaceContext2.getItemInHand();
        BlockState blockState2 = level.getBlockState(blockPos);
        Block block = blockState2.getBlock();
        if (block == blockState.getBlock()) {
            blockState2 = this.updateBlockStateFromTag(blockPos, level, itemStack, blockState2);
            this.updateCustomBlockEntityTag(blockPos, level, player, itemStack, blockState2);
            block.setPlacedBy(level, blockPos, blockState2, player, itemStack);
            if (player instanceof ServerPlayer) {
                CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayer)player, blockPos, itemStack);
            }
        }
        SoundType soundType = blockState2.getSoundType();
        level.playSound(player, blockPos, this.getPlaceSound(blockState2), SoundSource.BLOCKS, (soundType.getVolume() + 1.0f) / 2.0f, soundType.getPitch() * 0.8f);
        if (player == null || !player.abilities.instabuild) {
            itemStack.shrink(1);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    protected SoundEvent getPlaceSound(BlockState blockState) {
        return blockState.getSoundType().getPlaceSound();
    }

    @Nullable
    public BlockPlaceContext updatePlacementContext(BlockPlaceContext blockPlaceContext) {
        return blockPlaceContext;
    }

    protected boolean updateCustomBlockEntityTag(BlockPos blockPos, Level level, @Nullable Player player, ItemStack itemStack, BlockState blockState) {
        return BlockItem.updateCustomBlockEntityTag(level, player, blockPos, itemStack);
    }

    @Nullable
    protected BlockState getPlacementState(BlockPlaceContext blockPlaceContext) {
        BlockState blockState = this.getBlock().getStateForPlacement(blockPlaceContext);
        return blockState != null && this.canPlace(blockPlaceContext, blockState) ? blockState : null;
    }

    private BlockState updateBlockStateFromTag(BlockPos blockPos, Level level, ItemStack itemStack, BlockState blockState) {
        BlockState blockState2 = blockState;
        CompoundTag compoundTag = itemStack.getTag();
        if (compoundTag != null) {
            CompoundTag compoundTag2 = compoundTag.getCompound("BlockStateTag");
            StateDefinition<Block, BlockState> stateDefinition = blockState2.getBlock().getStateDefinition();
            for (String string : compoundTag2.getAllKeys()) {
                Property<?> property = stateDefinition.getProperty(string);
                if (property == null) continue;
                String string2 = compoundTag2.get(string).getAsString();
                blockState2 = BlockItem.updateState(blockState2, property, string2);
            }
        }
        if (blockState2 != blockState) {
            level.setBlock(blockPos, blockState2, 2);
        }
        return blockState2;
    }

    private static <T extends Comparable<T>> BlockState updateState(BlockState blockState, Property<T> property, String string) {
        return property.getValue(string).map(comparable -> (BlockState)blockState.setValue(property, comparable)).orElse(blockState);
    }

    protected boolean canPlace(BlockPlaceContext blockPlaceContext, BlockState blockState) {
        Player player = blockPlaceContext.getPlayer();
        CollisionContext collisionContext = player == null ? CollisionContext.empty() : CollisionContext.of(player);
        return (!this.mustSurvive() || blockState.canSurvive(blockPlaceContext.getLevel(), blockPlaceContext.getClickedPos())) && blockPlaceContext.getLevel().isUnobstructed(blockState, blockPlaceContext.getClickedPos(), collisionContext);
    }

    protected boolean mustSurvive() {
        return true;
    }

    protected boolean placeBlock(BlockPlaceContext blockPlaceContext, BlockState blockState) {
        return blockPlaceContext.getLevel().setBlock(blockPlaceContext.getClickedPos(), blockState, 11);
    }

    public static boolean updateCustomBlockEntityTag(Level level, @Nullable Player player, BlockPos blockPos, ItemStack itemStack) {
        BlockEntity blockEntity;
        MinecraftServer minecraftServer = level.getServer();
        if (minecraftServer == null) {
            return false;
        }
        CompoundTag compoundTag = itemStack.getTagElement("BlockEntityTag");
        if (compoundTag != null && (blockEntity = level.getBlockEntity(blockPos)) != null) {
            if (!(level.isClientSide || !blockEntity.onlyOpCanSetNbt() || player != null && player.canUseGameMasterBlocks())) {
                return false;
            }
            CompoundTag compoundTag2 = blockEntity.save(new CompoundTag());
            CompoundTag compoundTag3 = compoundTag2.copy();
            compoundTag2.merge(compoundTag);
            compoundTag2.putInt("x", blockPos.getX());
            compoundTag2.putInt("y", blockPos.getY());
            compoundTag2.putInt("z", blockPos.getZ());
            if (!compoundTag2.equals(compoundTag3)) {
                blockEntity.load(level.getBlockState(blockPos), compoundTag2);
                blockEntity.setChanged();
                return true;
            }
        }
        return false;
    }

    @Override
    public String getDescriptionId() {
        return this.getBlock().getDescriptionId();
    }

    @Override
    public void fillItemCategory(CreativeModeTab creativeModeTab, NonNullList<ItemStack> nonNullList) {
        if (this.allowdedIn(creativeModeTab)) {
            this.getBlock().fillItemCategory(creativeModeTab, nonNullList);
        }
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> list, TooltipFlag tooltipFlag) {
        super.appendHoverText(itemStack, level, list, tooltipFlag);
        this.getBlock().appendHoverText(itemStack, level, list, tooltipFlag);
    }

    public Block getBlock() {
        return this.block;
    }

    public void registerBlocks(Map<Block, Item> map, Item item) {
        map.put(this.getBlock(), item);
    }
}

