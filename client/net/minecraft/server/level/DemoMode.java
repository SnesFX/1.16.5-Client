/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.server.level;

import java.util.UUID;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;

public class DemoMode
extends ServerPlayerGameMode {
    private boolean displayedIntro;
    private boolean demoHasEnded;
    private int demoEndedReminder;
    private int gameModeTicks;

    public DemoMode(ServerLevel serverLevel) {
        super(serverLevel);
    }

    @Override
    public void tick() {
        super.tick();
        ++this.gameModeTicks;
        long l = this.level.getGameTime();
        long l2 = l / 24000L + 1L;
        if (!this.displayedIntro && this.gameModeTicks > 20) {
            this.displayedIntro = true;
            this.player.connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.DEMO_EVENT, 0.0f));
        }
        boolean bl = this.demoHasEnded = l > 120500L;
        if (this.demoHasEnded) {
            ++this.demoEndedReminder;
        }
        if (l % 24000L == 500L) {
            if (l2 <= 6L) {
                if (l2 == 6L) {
                    this.player.connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.DEMO_EVENT, 104.0f));
                } else {
                    this.player.sendMessage(new TranslatableComponent("demo.day." + l2), Util.NIL_UUID);
                }
            }
        } else if (l2 == 1L) {
            if (l == 100L) {
                this.player.connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.DEMO_EVENT, 101.0f));
            } else if (l == 175L) {
                this.player.connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.DEMO_EVENT, 102.0f));
            } else if (l == 250L) {
                this.player.connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.DEMO_EVENT, 103.0f));
            }
        } else if (l2 == 5L && l % 24000L == 22000L) {
            this.player.sendMessage(new TranslatableComponent("demo.day.warning"), Util.NIL_UUID);
        }
    }

    private void outputDemoReminder() {
        if (this.demoEndedReminder > 100) {
            this.player.sendMessage(new TranslatableComponent("demo.reminder"), Util.NIL_UUID);
            this.demoEndedReminder = 0;
        }
    }

    @Override
    public void handleBlockBreakAction(BlockPos blockPos, ServerboundPlayerActionPacket.Action action, Direction direction, int n) {
        if (this.demoHasEnded) {
            this.outputDemoReminder();
            return;
        }
        super.handleBlockBreakAction(blockPos, action, direction, n);
    }

    @Override
    public InteractionResult useItem(ServerPlayer serverPlayer, Level level, ItemStack itemStack, InteractionHand interactionHand) {
        if (this.demoHasEnded) {
            this.outputDemoReminder();
            return InteractionResult.PASS;
        }
        return super.useItem(serverPlayer, level, itemStack, interactionHand);
    }

    @Override
    public InteractionResult useItemOn(ServerPlayer serverPlayer, Level level, ItemStack itemStack, InteractionHand interactionHand, BlockHitResult blockHitResult) {
        if (this.demoHasEnded) {
            this.outputDemoReminder();
            return InteractionResult.PASS;
        }
        return super.useItemOn(serverPlayer, level, itemStack, interactionHand, blockHitResult);
    }
}

