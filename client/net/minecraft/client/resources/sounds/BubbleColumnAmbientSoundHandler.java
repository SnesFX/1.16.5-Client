/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.resources.sounds;

import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.AmbientSoundHandler;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BubbleColumnBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.AABB;

public class BubbleColumnAmbientSoundHandler
implements AmbientSoundHandler {
    private final LocalPlayer player;
    private boolean wasInBubbleColumn;
    private boolean firstTick = true;

    public BubbleColumnAmbientSoundHandler(LocalPlayer localPlayer) {
        this.player = localPlayer;
    }

    @Override
    public void tick() {
        Level level = this.player.level;
        BlockState blockState2 = level.getBlockStatesIfLoaded(this.player.getBoundingBox().inflate(0.0, -0.4000000059604645, 0.0).deflate(0.001)).filter(blockState -> blockState.is(Blocks.BUBBLE_COLUMN)).findFirst().orElse(null);
        if (blockState2 != null) {
            if (!this.wasInBubbleColumn && !this.firstTick && blockState2.is(Blocks.BUBBLE_COLUMN) && !this.player.isSpectator()) {
                boolean bl = blockState2.getValue(BubbleColumnBlock.DRAG_DOWN);
                if (bl) {
                    this.player.playSound(SoundEvents.BUBBLE_COLUMN_WHIRLPOOL_INSIDE, 1.0f, 1.0f);
                } else {
                    this.player.playSound(SoundEvents.BUBBLE_COLUMN_UPWARDS_INSIDE, 1.0f, 1.0f);
                }
            }
            this.wasInBubbleColumn = true;
        } else {
            this.wasInBubbleColumn = false;
        }
        this.firstTick = false;
    }
}

