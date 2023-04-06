/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Maps
 */
package net.minecraft.world.item;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class HoeItem
extends DiggerItem {
    private static final Set<Block> DIGGABLES = ImmutableSet.of((Object)Blocks.NETHER_WART_BLOCK, (Object)Blocks.WARPED_WART_BLOCK, (Object)Blocks.HAY_BLOCK, (Object)Blocks.DRIED_KELP_BLOCK, (Object)Blocks.TARGET, (Object)Blocks.SHROOMLIGHT, (Object[])new Block[]{Blocks.SPONGE, Blocks.WET_SPONGE, Blocks.JUNGLE_LEAVES, Blocks.OAK_LEAVES, Blocks.SPRUCE_LEAVES, Blocks.DARK_OAK_LEAVES, Blocks.ACACIA_LEAVES, Blocks.BIRCH_LEAVES});
    protected static final Map<Block, BlockState> TILLABLES = Maps.newHashMap((Map)ImmutableMap.of((Object)Blocks.GRASS_BLOCK, (Object)Blocks.FARMLAND.defaultBlockState(), (Object)Blocks.GRASS_PATH, (Object)Blocks.FARMLAND.defaultBlockState(), (Object)Blocks.DIRT, (Object)Blocks.FARMLAND.defaultBlockState(), (Object)Blocks.COARSE_DIRT, (Object)Blocks.DIRT.defaultBlockState()));

    protected HoeItem(Tier tier, int n, float f, Item.Properties properties) {
        super(n, f, tier, DIGGABLES, properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext useOnContext) {
        BlockState blockState;
        Level level = useOnContext.getLevel();
        BlockPos blockPos = useOnContext.getClickedPos();
        if (useOnContext.getClickedFace() != Direction.DOWN && level.getBlockState(blockPos.above()).isAir() && (blockState = TILLABLES.get(level.getBlockState(blockPos).getBlock())) != null) {
            Player player2 = useOnContext.getPlayer();
            level.playSound(player2, blockPos, SoundEvents.HOE_TILL, SoundSource.BLOCKS, 1.0f, 1.0f);
            if (!level.isClientSide) {
                level.setBlock(blockPos, blockState, 11);
                if (player2 != null) {
                    useOnContext.getItemInHand().hurtAndBreak(1, player2, player -> player.broadcastBreakEvent(useOnContext.getHand()));
                }
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return InteractionResult.PASS;
    }
}

