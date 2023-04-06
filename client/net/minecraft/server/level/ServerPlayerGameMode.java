/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.server.level;

import java.util.Objects;
import java.util.OptionalInt;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.critereon.ItemUsedOnBlockTrigger;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundBlockBreakAckPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemCooldowns;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CommandBlock;
import net.minecraft.world.level.block.JigsawBlock;
import net.minecraft.world.level.block.StructureBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerPlayerGameMode {
    private static final Logger LOGGER = LogManager.getLogger();
    public ServerLevel level;
    public ServerPlayer player;
    private GameType gameModeForPlayer = GameType.NOT_SET;
    private GameType previousGameModeForPlayer = GameType.NOT_SET;
    private boolean isDestroyingBlock;
    private int destroyProgressStart;
    private BlockPos destroyPos = BlockPos.ZERO;
    private int gameTicks;
    private boolean hasDelayedDestroy;
    private BlockPos delayedDestroyPos = BlockPos.ZERO;
    private int delayedTickStart;
    private int lastSentState = -1;

    public ServerPlayerGameMode(ServerLevel serverLevel) {
        this.level = serverLevel;
    }

    public void setGameModeForPlayer(GameType gameType) {
        this.setGameModeForPlayer(gameType, gameType != this.gameModeForPlayer ? this.gameModeForPlayer : this.previousGameModeForPlayer);
    }

    public void setGameModeForPlayer(GameType gameType, GameType gameType2) {
        this.previousGameModeForPlayer = gameType2;
        this.gameModeForPlayer = gameType;
        gameType.updatePlayerAbilities(this.player.abilities);
        this.player.onUpdateAbilities();
        this.player.server.getPlayerList().broadcastAll(new ClientboundPlayerInfoPacket(ClientboundPlayerInfoPacket.Action.UPDATE_GAME_MODE, this.player));
        this.level.updateSleepingPlayerList();
    }

    public GameType getGameModeForPlayer() {
        return this.gameModeForPlayer;
    }

    public GameType getPreviousGameModeForPlayer() {
        return this.previousGameModeForPlayer;
    }

    public boolean isSurvival() {
        return this.gameModeForPlayer.isSurvival();
    }

    public boolean isCreative() {
        return this.gameModeForPlayer.isCreative();
    }

    public void updateGameMode(GameType gameType) {
        if (this.gameModeForPlayer == GameType.NOT_SET) {
            this.gameModeForPlayer = gameType;
        }
        this.setGameModeForPlayer(this.gameModeForPlayer);
    }

    public void tick() {
        ++this.gameTicks;
        if (this.hasDelayedDestroy) {
            BlockState blockState = this.level.getBlockState(this.delayedDestroyPos);
            if (blockState.isAir()) {
                this.hasDelayedDestroy = false;
            } else {
                float f = this.incrementDestroyProgress(blockState, this.delayedDestroyPos, this.delayedTickStart);
                if (f >= 1.0f) {
                    this.hasDelayedDestroy = false;
                    this.destroyBlock(this.delayedDestroyPos);
                }
            }
        } else if (this.isDestroyingBlock) {
            BlockState blockState = this.level.getBlockState(this.destroyPos);
            if (blockState.isAir()) {
                this.level.destroyBlockProgress(this.player.getId(), this.destroyPos, -1);
                this.lastSentState = -1;
                this.isDestroyingBlock = false;
            } else {
                this.incrementDestroyProgress(blockState, this.destroyPos, this.destroyProgressStart);
            }
        }
    }

    private float incrementDestroyProgress(BlockState blockState, BlockPos blockPos, int n) {
        int n2 = this.gameTicks - n;
        float f = blockState.getDestroyProgress(this.player, this.player.level, blockPos) * (float)(n2 + 1);
        int n3 = (int)(f * 10.0f);
        if (n3 != this.lastSentState) {
            this.level.destroyBlockProgress(this.player.getId(), blockPos, n3);
            this.lastSentState = n3;
        }
        return f;
    }

    public void handleBlockBreakAction(BlockPos blockPos, ServerboundPlayerActionPacket.Action action, Direction direction, int n) {
        double d;
        double d2;
        double d3 = this.player.getX() - ((double)blockPos.getX() + 0.5);
        double d4 = d3 * d3 + (d2 = this.player.getY() - ((double)blockPos.getY() + 0.5) + 1.5) * d2 + (d = this.player.getZ() - ((double)blockPos.getZ() + 0.5)) * d;
        if (d4 > 36.0) {
            this.player.connection.send(new ClientboundBlockBreakAckPacket(blockPos, this.level.getBlockState(blockPos), action, false, "too far"));
            return;
        }
        if (blockPos.getY() >= n) {
            this.player.connection.send(new ClientboundBlockBreakAckPacket(blockPos, this.level.getBlockState(blockPos), action, false, "too high"));
            return;
        }
        if (action == ServerboundPlayerActionPacket.Action.START_DESTROY_BLOCK) {
            if (!this.level.mayInteract(this.player, blockPos)) {
                this.player.connection.send(new ClientboundBlockBreakAckPacket(blockPos, this.level.getBlockState(blockPos), action, false, "may not interact"));
                return;
            }
            if (this.isCreative()) {
                this.destroyAndAck(blockPos, action, "creative destroy");
                return;
            }
            if (this.player.blockActionRestricted(this.level, blockPos, this.gameModeForPlayer)) {
                this.player.connection.send(new ClientboundBlockBreakAckPacket(blockPos, this.level.getBlockState(blockPos), action, false, "block action restricted"));
                return;
            }
            this.destroyProgressStart = this.gameTicks;
            float f = 1.0f;
            BlockState blockState = this.level.getBlockState(blockPos);
            if (!blockState.isAir()) {
                blockState.attack(this.level, blockPos, this.player);
                f = blockState.getDestroyProgress(this.player, this.player.level, blockPos);
            }
            if (!blockState.isAir() && f >= 1.0f) {
                this.destroyAndAck(blockPos, action, "insta mine");
            } else {
                if (this.isDestroyingBlock) {
                    this.player.connection.send(new ClientboundBlockBreakAckPacket(this.destroyPos, this.level.getBlockState(this.destroyPos), ServerboundPlayerActionPacket.Action.START_DESTROY_BLOCK, false, "abort destroying since another started (client insta mine, server disagreed)"));
                }
                this.isDestroyingBlock = true;
                this.destroyPos = blockPos.immutable();
                int n2 = (int)(f * 10.0f);
                this.level.destroyBlockProgress(this.player.getId(), blockPos, n2);
                this.player.connection.send(new ClientboundBlockBreakAckPacket(blockPos, this.level.getBlockState(blockPos), action, true, "actual start of destroying"));
                this.lastSentState = n2;
            }
        } else if (action == ServerboundPlayerActionPacket.Action.STOP_DESTROY_BLOCK) {
            if (blockPos.equals(this.destroyPos)) {
                int n3 = this.gameTicks - this.destroyProgressStart;
                BlockState blockState = this.level.getBlockState(blockPos);
                if (!blockState.isAir()) {
                    float f = blockState.getDestroyProgress(this.player, this.player.level, blockPos) * (float)(n3 + 1);
                    if (f >= 0.7f) {
                        this.isDestroyingBlock = false;
                        this.level.destroyBlockProgress(this.player.getId(), blockPos, -1);
                        this.destroyAndAck(blockPos, action, "destroyed");
                        return;
                    }
                    if (!this.hasDelayedDestroy) {
                        this.isDestroyingBlock = false;
                        this.hasDelayedDestroy = true;
                        this.delayedDestroyPos = blockPos;
                        this.delayedTickStart = this.destroyProgressStart;
                    }
                }
            }
            this.player.connection.send(new ClientboundBlockBreakAckPacket(blockPos, this.level.getBlockState(blockPos), action, true, "stopped destroying"));
        } else if (action == ServerboundPlayerActionPacket.Action.ABORT_DESTROY_BLOCK) {
            this.isDestroyingBlock = false;
            if (!Objects.equals(this.destroyPos, blockPos)) {
                LOGGER.warn("Mismatch in destroy block pos: " + this.destroyPos + " " + blockPos);
                this.level.destroyBlockProgress(this.player.getId(), this.destroyPos, -1);
                this.player.connection.send(new ClientboundBlockBreakAckPacket(this.destroyPos, this.level.getBlockState(this.destroyPos), action, true, "aborted mismatched destroying"));
            }
            this.level.destroyBlockProgress(this.player.getId(), blockPos, -1);
            this.player.connection.send(new ClientboundBlockBreakAckPacket(blockPos, this.level.getBlockState(blockPos), action, true, "aborted destroying"));
        }
    }

    public void destroyAndAck(BlockPos blockPos, ServerboundPlayerActionPacket.Action action, String string) {
        if (this.destroyBlock(blockPos)) {
            this.player.connection.send(new ClientboundBlockBreakAckPacket(blockPos, this.level.getBlockState(blockPos), action, true, string));
        } else {
            this.player.connection.send(new ClientboundBlockBreakAckPacket(blockPos, this.level.getBlockState(blockPos), action, false, string));
        }
    }

    public boolean destroyBlock(BlockPos blockPos) {
        BlockState blockState = this.level.getBlockState(blockPos);
        if (!this.player.getMainHandItem().getItem().canAttackBlock(blockState, this.level, blockPos, this.player)) {
            return false;
        }
        BlockEntity blockEntity = this.level.getBlockEntity(blockPos);
        Block block = blockState.getBlock();
        if ((block instanceof CommandBlock || block instanceof StructureBlock || block instanceof JigsawBlock) && !this.player.canUseGameMasterBlocks()) {
            this.level.sendBlockUpdated(blockPos, blockState, blockState, 3);
            return false;
        }
        if (this.player.blockActionRestricted(this.level, blockPos, this.gameModeForPlayer)) {
            return false;
        }
        block.playerWillDestroy(this.level, blockPos, blockState, this.player);
        boolean bl = this.level.removeBlock(blockPos, false);
        if (bl) {
            block.destroy(this.level, blockPos, blockState);
        }
        if (this.isCreative()) {
            return true;
        }
        ItemStack itemStack = this.player.getMainHandItem();
        ItemStack itemStack2 = itemStack.copy();
        boolean bl2 = this.player.hasCorrectToolForDrops(blockState);
        itemStack.mineBlock(this.level, blockState, blockPos, this.player);
        if (bl && bl2) {
            block.playerDestroy(this.level, this.player, blockPos, blockState, blockEntity, itemStack2);
        }
        return true;
    }

    public InteractionResult useItem(ServerPlayer serverPlayer, Level level, ItemStack itemStack, InteractionHand interactionHand) {
        if (this.gameModeForPlayer == GameType.SPECTATOR) {
            return InteractionResult.PASS;
        }
        if (serverPlayer.getCooldowns().isOnCooldown(itemStack.getItem())) {
            return InteractionResult.PASS;
        }
        int n = itemStack.getCount();
        int n2 = itemStack.getDamageValue();
        InteractionResultHolder<ItemStack> interactionResultHolder = itemStack.use(level, serverPlayer, interactionHand);
        ItemStack itemStack2 = interactionResultHolder.getObject();
        if (itemStack2 == itemStack && itemStack2.getCount() == n && itemStack2.getUseDuration() <= 0 && itemStack2.getDamageValue() == n2) {
            return interactionResultHolder.getResult();
        }
        if (interactionResultHolder.getResult() == InteractionResult.FAIL && itemStack2.getUseDuration() > 0 && !serverPlayer.isUsingItem()) {
            return interactionResultHolder.getResult();
        }
        serverPlayer.setItemInHand(interactionHand, itemStack2);
        if (this.isCreative()) {
            itemStack2.setCount(n);
            if (itemStack2.isDamageableItem() && itemStack2.getDamageValue() != n2) {
                itemStack2.setDamageValue(n2);
            }
        }
        if (itemStack2.isEmpty()) {
            serverPlayer.setItemInHand(interactionHand, ItemStack.EMPTY);
        }
        if (!serverPlayer.isUsingItem()) {
            serverPlayer.refreshContainer(serverPlayer.inventoryMenu);
        }
        return interactionResultHolder.getResult();
    }

    public InteractionResult useItemOn(ServerPlayer serverPlayer, Level level, ItemStack itemStack, InteractionHand interactionHand, BlockHitResult blockHitResult) {
        InteractionResult interactionResult;
        Object object;
        BlockPos blockPos = blockHitResult.getBlockPos();
        BlockState blockState = level.getBlockState(blockPos);
        if (this.gameModeForPlayer == GameType.SPECTATOR) {
            MenuProvider menuProvider = blockState.getMenuProvider(level, blockPos);
            if (menuProvider != null) {
                serverPlayer.openMenu(menuProvider);
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.PASS;
        }
        boolean bl = !serverPlayer.getMainHandItem().isEmpty() || !serverPlayer.getOffhandItem().isEmpty();
        boolean bl2 = serverPlayer.isSecondaryUseActive() && bl;
        ItemStack itemStack2 = itemStack.copy();
        if (!bl2 && (object = blockState.use(level, serverPlayer, interactionHand, blockHitResult)).consumesAction()) {
            CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger(serverPlayer, blockPos, itemStack2);
            return object;
        }
        if (itemStack.isEmpty() || serverPlayer.getCooldowns().isOnCooldown(itemStack.getItem())) {
            return InteractionResult.PASS;
        }
        object = new UseOnContext(serverPlayer, interactionHand, blockHitResult);
        if (this.isCreative()) {
            int n = itemStack.getCount();
            interactionResult = itemStack.useOn((UseOnContext)object);
            itemStack.setCount(n);
        } else {
            interactionResult = itemStack.useOn((UseOnContext)object);
        }
        if (interactionResult.consumesAction()) {
            CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger(serverPlayer, blockPos, itemStack2);
        }
        return interactionResult;
    }

    public void setLevel(ServerLevel serverLevel) {
        this.level = serverLevel;
    }
}

