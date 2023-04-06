/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Sets
 */
package net.minecraft.client.tutorial;

import com.google.common.collect.Sets;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.client.gui.components.toasts.TutorialToast;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.tutorial.Tutorial;
import net.minecraft.client.tutorial.TutorialStepInstance;
import net.minecraft.client.tutorial.TutorialSteps;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.stats.Stat;
import net.minecraft.stats.StatType;
import net.minecraft.stats.Stats;
import net.minecraft.stats.StatsCounter;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public class FindTreeTutorialStepInstance
implements TutorialStepInstance {
    private static final Set<Block> TREE_BLOCKS = Sets.newHashSet((Object[])new Block[]{Blocks.OAK_LOG, Blocks.SPRUCE_LOG, Blocks.BIRCH_LOG, Blocks.JUNGLE_LOG, Blocks.ACACIA_LOG, Blocks.DARK_OAK_LOG, Blocks.WARPED_STEM, Blocks.CRIMSON_STEM, Blocks.OAK_WOOD, Blocks.SPRUCE_WOOD, Blocks.BIRCH_WOOD, Blocks.JUNGLE_WOOD, Blocks.ACACIA_WOOD, Blocks.DARK_OAK_WOOD, Blocks.WARPED_HYPHAE, Blocks.CRIMSON_HYPHAE, Blocks.OAK_LEAVES, Blocks.SPRUCE_LEAVES, Blocks.BIRCH_LEAVES, Blocks.JUNGLE_LEAVES, Blocks.ACACIA_LEAVES, Blocks.DARK_OAK_LEAVES, Blocks.NETHER_WART_BLOCK, Blocks.WARPED_WART_BLOCK});
    private static final Component TITLE = new TranslatableComponent("tutorial.find_tree.title");
    private static final Component DESCRIPTION = new TranslatableComponent("tutorial.find_tree.description");
    private final Tutorial tutorial;
    private TutorialToast toast;
    private int timeWaiting;

    public FindTreeTutorialStepInstance(Tutorial tutorial) {
        this.tutorial = tutorial;
    }

    @Override
    public void tick() {
        LocalPlayer localPlayer;
        ++this.timeWaiting;
        if (this.tutorial.getGameMode() != GameType.SURVIVAL) {
            this.tutorial.setStep(TutorialSteps.NONE);
            return;
        }
        if (this.timeWaiting == 1 && (localPlayer = this.tutorial.getMinecraft().player) != null) {
            for (Block block : TREE_BLOCKS) {
                if (!localPlayer.inventory.contains(new ItemStack(block))) continue;
                this.tutorial.setStep(TutorialSteps.CRAFT_PLANKS);
                return;
            }
            if (FindTreeTutorialStepInstance.hasPunchedTreesPreviously(localPlayer)) {
                this.tutorial.setStep(TutorialSteps.CRAFT_PLANKS);
                return;
            }
        }
        if (this.timeWaiting >= 6000 && this.toast == null) {
            this.toast = new TutorialToast(TutorialToast.Icons.TREE, TITLE, DESCRIPTION, false);
            this.tutorial.getMinecraft().getToasts().addToast(this.toast);
        }
    }

    @Override
    public void clear() {
        if (this.toast != null) {
            this.toast.hide();
            this.toast = null;
        }
    }

    @Override
    public void onLookAt(ClientLevel clientLevel, HitResult hitResult) {
        BlockState blockState;
        if (hitResult.getType() == HitResult.Type.BLOCK && TREE_BLOCKS.contains((blockState = clientLevel.getBlockState(((BlockHitResult)hitResult).getBlockPos())).getBlock())) {
            this.tutorial.setStep(TutorialSteps.PUNCH_TREE);
        }
    }

    @Override
    public void onGetItem(ItemStack itemStack) {
        for (Block block : TREE_BLOCKS) {
            if (itemStack.getItem() != block.asItem()) continue;
            this.tutorial.setStep(TutorialSteps.CRAFT_PLANKS);
            return;
        }
    }

    public static boolean hasPunchedTreesPreviously(LocalPlayer localPlayer) {
        for (Block block : TREE_BLOCKS) {
            if (localPlayer.getStats().getValue(Stats.BLOCK_MINED.get(block)) <= 0) continue;
            return true;
        }
        return false;
    }
}

