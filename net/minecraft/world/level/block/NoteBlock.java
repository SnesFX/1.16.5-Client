/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;

public class NoteBlock
extends Block {
    public static final EnumProperty<NoteBlockInstrument> INSTRUMENT = BlockStateProperties.NOTEBLOCK_INSTRUMENT;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final IntegerProperty NOTE = BlockStateProperties.NOTE;

    public NoteBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(INSTRUMENT, NoteBlockInstrument.HARP)).setValue(NOTE, 0)).setValue(POWERED, false));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
        return (BlockState)this.defaultBlockState().setValue(INSTRUMENT, NoteBlockInstrument.byState(blockPlaceContext.getLevel().getBlockState(blockPlaceContext.getClickedPos().below())));
    }

    @Override
    public BlockState updateShape(BlockState blockState, Direction direction, BlockState blockState2, LevelAccessor levelAccessor, BlockPos blockPos, BlockPos blockPos2) {
        if (direction == Direction.DOWN) {
            return (BlockState)blockState.setValue(INSTRUMENT, NoteBlockInstrument.byState(blockState2));
        }
        return super.updateShape(blockState, direction, blockState2, levelAccessor, blockPos, blockPos2);
    }

    @Override
    public void neighborChanged(BlockState blockState, Level level, BlockPos blockPos, Block block, BlockPos blockPos2, boolean bl) {
        boolean bl2 = level.hasNeighborSignal(blockPos);
        if (bl2 != blockState.getValue(POWERED)) {
            if (bl2) {
                this.playNote(level, blockPos);
            }
            level.setBlock(blockPos, (BlockState)blockState.setValue(POWERED, bl2), 3);
        }
    }

    private void playNote(Level level, BlockPos blockPos) {
        if (level.getBlockState(blockPos.above()).isAir()) {
            level.blockEvent(blockPos, this, 0, 0);
        }
    }

    @Override
    public InteractionResult use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        blockState = (BlockState)blockState.cycle(NOTE);
        level.setBlock(blockPos, blockState, 3);
        this.playNote(level, blockPos);
        player.awardStat(Stats.TUNE_NOTEBLOCK);
        return InteractionResult.CONSUME;
    }

    @Override
    public void attack(BlockState blockState, Level level, BlockPos blockPos, Player player) {
        if (level.isClientSide) {
            return;
        }
        this.playNote(level, blockPos);
        player.awardStat(Stats.PLAY_NOTEBLOCK);
    }

    @Override
    public boolean triggerEvent(BlockState blockState, Level level, BlockPos blockPos, int n, int n2) {
        int n3 = blockState.getValue(NOTE);
        float f = (float)Math.pow(2.0, (double)(n3 - 12) / 12.0);
        level.playSound(null, blockPos, blockState.getValue(INSTRUMENT).getSoundEvent(), SoundSource.RECORDS, 3.0f, f);
        level.addParticle(ParticleTypes.NOTE, (double)blockPos.getX() + 0.5, (double)blockPos.getY() + 1.2, (double)blockPos.getZ() + 0.5, (double)n3 / 24.0, 0.0, 0.0);
        return true;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(INSTRUMENT, POWERED, NOTE);
    }
}

