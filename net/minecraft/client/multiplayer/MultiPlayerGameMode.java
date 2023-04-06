/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Pair
 *  it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.client.multiplayer;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.minecraft.client.ClientRecipeBook;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.client.tutorial.Tutorial;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundContainerButtonClickPacket;
import net.minecraft.network.protocol.game.ServerboundContainerClickPacket;
import net.minecraft.network.protocol.game.ServerboundInteractPacket;
import net.minecraft.network.protocol.game.ServerboundPickItemPacket;
import net.minecraft.network.protocol.game.ServerboundPlaceRecipePacket;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket;
import net.minecraft.network.protocol.game.ServerboundSetCreativeModeSlotPacket;
import net.minecraft.network.protocol.game.ServerboundUseItemOnPacket;
import net.minecraft.network.protocol.game.ServerboundUseItemPacket;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.StatsCounter;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemCooldowns;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CommandBlock;
import net.minecraft.world.level.block.JigsawBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.StructureBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MultiPlayerGameMode {
    private static final Logger LOGGER = LogManager.getLogger();
    private final Minecraft minecraft;
    private final ClientPacketListener connection;
    private BlockPos destroyBlockPos = new BlockPos(-1, -1, -1);
    private ItemStack destroyingItem = ItemStack.EMPTY;
    private float destroyProgress;
    private float destroyTicks;
    private int destroyDelay;
    private boolean isDestroying;
    private GameType localPlayerMode = GameType.SURVIVAL;
    private GameType previousLocalPlayerMode = GameType.NOT_SET;
    private final Object2ObjectLinkedOpenHashMap<Pair<BlockPos, ServerboundPlayerActionPacket.Action>, Vec3> unAckedActions = new Object2ObjectLinkedOpenHashMap();
    private int carriedIndex;

    public MultiPlayerGameMode(Minecraft minecraft, ClientPacketListener clientPacketListener) {
        this.minecraft = minecraft;
        this.connection = clientPacketListener;
    }

    public void adjustPlayer(Player player) {
        this.localPlayerMode.updatePlayerAbilities(player.abilities);
    }

    public void setPreviousLocalMode(GameType gameType) {
        this.previousLocalPlayerMode = gameType;
    }

    public void setLocalMode(GameType gameType) {
        if (gameType != this.localPlayerMode) {
            this.previousLocalPlayerMode = this.localPlayerMode;
        }
        this.localPlayerMode = gameType;
        this.localPlayerMode.updatePlayerAbilities(this.minecraft.player.abilities);
    }

    public boolean canHurtPlayer() {
        return this.localPlayerMode.isSurvival();
    }

    public boolean destroyBlock(BlockPos blockPos) {
        if (this.minecraft.player.blockActionRestricted(this.minecraft.level, blockPos, this.localPlayerMode)) {
            return false;
        }
        ClientLevel clientLevel = this.minecraft.level;
        BlockState blockState = clientLevel.getBlockState(blockPos);
        if (!this.minecraft.player.getMainHandItem().getItem().canAttackBlock(blockState, clientLevel, blockPos, this.minecraft.player)) {
            return false;
        }
        Block block = blockState.getBlock();
        if ((block instanceof CommandBlock || block instanceof StructureBlock || block instanceof JigsawBlock) && !this.minecraft.player.canUseGameMasterBlocks()) {
            return false;
        }
        if (blockState.isAir()) {
            return false;
        }
        block.playerWillDestroy(clientLevel, blockPos, blockState, this.minecraft.player);
        FluidState fluidState = clientLevel.getFluidState(blockPos);
        boolean bl = clientLevel.setBlock(blockPos, fluidState.createLegacyBlock(), 11);
        if (bl) {
            block.destroy(clientLevel, blockPos, blockState);
        }
        return bl;
    }

    public boolean startDestroyBlock(BlockPos blockPos, Direction direction) {
        if (this.minecraft.player.blockActionRestricted(this.minecraft.level, blockPos, this.localPlayerMode)) {
            return false;
        }
        if (!this.minecraft.level.getWorldBorder().isWithinBounds(blockPos)) {
            return false;
        }
        if (this.localPlayerMode.isCreative()) {
            BlockState blockState = this.minecraft.level.getBlockState(blockPos);
            this.minecraft.getTutorial().onDestroyBlock(this.minecraft.level, blockPos, blockState, 1.0f);
            this.sendBlockAction(ServerboundPlayerActionPacket.Action.START_DESTROY_BLOCK, blockPos, direction);
            this.destroyBlock(blockPos);
            this.destroyDelay = 5;
        } else if (!this.isDestroying || !this.sameDestroyTarget(blockPos)) {
            boolean bl;
            if (this.isDestroying) {
                this.sendBlockAction(ServerboundPlayerActionPacket.Action.ABORT_DESTROY_BLOCK, this.destroyBlockPos, direction);
            }
            BlockState blockState = this.minecraft.level.getBlockState(blockPos);
            this.minecraft.getTutorial().onDestroyBlock(this.minecraft.level, blockPos, blockState, 0.0f);
            this.sendBlockAction(ServerboundPlayerActionPacket.Action.START_DESTROY_BLOCK, blockPos, direction);
            boolean bl2 = bl = !blockState.isAir();
            if (bl && this.destroyProgress == 0.0f) {
                blockState.attack(this.minecraft.level, blockPos, this.minecraft.player);
            }
            if (bl && blockState.getDestroyProgress(this.minecraft.player, this.minecraft.player.level, blockPos) >= 1.0f) {
                this.destroyBlock(blockPos);
            } else {
                this.isDestroying = true;
                this.destroyBlockPos = blockPos;
                this.destroyingItem = this.minecraft.player.getMainHandItem();
                this.destroyProgress = 0.0f;
                this.destroyTicks = 0.0f;
                this.minecraft.level.destroyBlockProgress(this.minecraft.player.getId(), this.destroyBlockPos, (int)(this.destroyProgress * 10.0f) - 1);
            }
        }
        return true;
    }

    public void stopDestroyBlock() {
        if (this.isDestroying) {
            BlockState blockState = this.minecraft.level.getBlockState(this.destroyBlockPos);
            this.minecraft.getTutorial().onDestroyBlock(this.minecraft.level, this.destroyBlockPos, blockState, -1.0f);
            this.sendBlockAction(ServerboundPlayerActionPacket.Action.ABORT_DESTROY_BLOCK, this.destroyBlockPos, Direction.DOWN);
            this.isDestroying = false;
            this.destroyProgress = 0.0f;
            this.minecraft.level.destroyBlockProgress(this.minecraft.player.getId(), this.destroyBlockPos, -1);
            this.minecraft.player.resetAttackStrengthTicker();
        }
    }

    public boolean continueDestroyBlock(BlockPos blockPos, Direction direction) {
        this.ensureHasSentCarriedItem();
        if (this.destroyDelay > 0) {
            --this.destroyDelay;
            return true;
        }
        if (this.localPlayerMode.isCreative() && this.minecraft.level.getWorldBorder().isWithinBounds(blockPos)) {
            this.destroyDelay = 5;
            BlockState blockState = this.minecraft.level.getBlockState(blockPos);
            this.minecraft.getTutorial().onDestroyBlock(this.minecraft.level, blockPos, blockState, 1.0f);
            this.sendBlockAction(ServerboundPlayerActionPacket.Action.START_DESTROY_BLOCK, blockPos, direction);
            this.destroyBlock(blockPos);
            return true;
        }
        if (this.sameDestroyTarget(blockPos)) {
            BlockState blockState = this.minecraft.level.getBlockState(blockPos);
            if (blockState.isAir()) {
                this.isDestroying = false;
                return false;
            }
            this.destroyProgress += blockState.getDestroyProgress(this.minecraft.player, this.minecraft.player.level, blockPos);
            if (this.destroyTicks % 4.0f == 0.0f) {
                SoundType soundType = blockState.getSoundType();
                this.minecraft.getSoundManager().play(new SimpleSoundInstance(soundType.getHitSound(), SoundSource.BLOCKS, (soundType.getVolume() + 1.0f) / 8.0f, soundType.getPitch() * 0.5f, blockPos));
            }
            this.destroyTicks += 1.0f;
            this.minecraft.getTutorial().onDestroyBlock(this.minecraft.level, blockPos, blockState, Mth.clamp(this.destroyProgress, 0.0f, 1.0f));
            if (this.destroyProgress >= 1.0f) {
                this.isDestroying = false;
                this.sendBlockAction(ServerboundPlayerActionPacket.Action.STOP_DESTROY_BLOCK, blockPos, direction);
                this.destroyBlock(blockPos);
                this.destroyProgress = 0.0f;
                this.destroyTicks = 0.0f;
                this.destroyDelay = 5;
            }
        } else {
            return this.startDestroyBlock(blockPos, direction);
        }
        this.minecraft.level.destroyBlockProgress(this.minecraft.player.getId(), this.destroyBlockPos, (int)(this.destroyProgress * 10.0f) - 1);
        return true;
    }

    public float getPickRange() {
        if (this.localPlayerMode.isCreative()) {
            return 5.0f;
        }
        return 4.5f;
    }

    public void tick() {
        this.ensureHasSentCarriedItem();
        if (this.connection.getConnection().isConnected()) {
            this.connection.getConnection().tick();
        } else {
            this.connection.getConnection().handleDisconnection();
        }
    }

    private boolean sameDestroyTarget(BlockPos blockPos) {
        boolean bl;
        ItemStack itemStack = this.minecraft.player.getMainHandItem();
        boolean bl2 = bl = this.destroyingItem.isEmpty() && itemStack.isEmpty();
        if (!this.destroyingItem.isEmpty() && !itemStack.isEmpty()) {
            bl = itemStack.getItem() == this.destroyingItem.getItem() && ItemStack.tagMatches(itemStack, this.destroyingItem) && (itemStack.isDamageableItem() || itemStack.getDamageValue() == this.destroyingItem.getDamageValue());
        }
        return blockPos.equals(this.destroyBlockPos) && bl;
    }

    private void ensureHasSentCarriedItem() {
        int n = this.minecraft.player.inventory.selected;
        if (n != this.carriedIndex) {
            this.carriedIndex = n;
            this.connection.send(new ServerboundSetCarriedItemPacket(this.carriedIndex));
        }
    }

    public InteractionResult useItemOn(LocalPlayer localPlayer, ClientLevel clientLevel, InteractionHand interactionHand, BlockHitResult blockHitResult) {
        InteractionResult interactionResult;
        boolean bl;
        this.ensureHasSentCarriedItem();
        BlockPos blockPos = blockHitResult.getBlockPos();
        if (!this.minecraft.level.getWorldBorder().isWithinBounds(blockPos)) {
            return InteractionResult.FAIL;
        }
        ItemStack itemStack = localPlayer.getItemInHand(interactionHand);
        if (this.localPlayerMode == GameType.SPECTATOR) {
            this.connection.send(new ServerboundUseItemOnPacket(interactionHand, blockHitResult));
            return InteractionResult.SUCCESS;
        }
        boolean bl2 = !localPlayer.getMainHandItem().isEmpty() || !localPlayer.getOffhandItem().isEmpty();
        boolean bl3 = bl = localPlayer.isSecondaryUseActive() && bl2;
        if (!bl && (interactionResult = clientLevel.getBlockState(blockPos).use(clientLevel, localPlayer, interactionHand, blockHitResult)).consumesAction()) {
            this.connection.send(new ServerboundUseItemOnPacket(interactionHand, blockHitResult));
            return interactionResult;
        }
        this.connection.send(new ServerboundUseItemOnPacket(interactionHand, blockHitResult));
        if (itemStack.isEmpty() || localPlayer.getCooldowns().isOnCooldown(itemStack.getItem())) {
            return InteractionResult.PASS;
        }
        UseOnContext useOnContext = new UseOnContext(localPlayer, interactionHand, blockHitResult);
        if (this.localPlayerMode.isCreative()) {
            int n = itemStack.getCount();
            interactionResult = itemStack.useOn(useOnContext);
            itemStack.setCount(n);
        } else {
            interactionResult = itemStack.useOn(useOnContext);
        }
        return interactionResult;
    }

    public InteractionResult useItem(Player player, Level level, InteractionHand interactionHand) {
        if (this.localPlayerMode == GameType.SPECTATOR) {
            return InteractionResult.PASS;
        }
        this.ensureHasSentCarriedItem();
        this.connection.send(new ServerboundUseItemPacket(interactionHand));
        ItemStack itemStack = player.getItemInHand(interactionHand);
        if (player.getCooldowns().isOnCooldown(itemStack.getItem())) {
            return InteractionResult.PASS;
        }
        int n = itemStack.getCount();
        InteractionResultHolder<ItemStack> interactionResultHolder = itemStack.use(level, player, interactionHand);
        ItemStack itemStack2 = interactionResultHolder.getObject();
        if (itemStack2 != itemStack) {
            player.setItemInHand(interactionHand, itemStack2);
        }
        return interactionResultHolder.getResult();
    }

    public LocalPlayer createPlayer(ClientLevel clientLevel, StatsCounter statsCounter, ClientRecipeBook clientRecipeBook) {
        return this.createPlayer(clientLevel, statsCounter, clientRecipeBook, false, false);
    }

    public LocalPlayer createPlayer(ClientLevel clientLevel, StatsCounter statsCounter, ClientRecipeBook clientRecipeBook, boolean bl, boolean bl2) {
        return new LocalPlayer(this.minecraft, clientLevel, this.connection, statsCounter, clientRecipeBook, bl, bl2);
    }

    public void attack(Player player, Entity entity) {
        this.ensureHasSentCarriedItem();
        this.connection.send(new ServerboundInteractPacket(entity, player.isShiftKeyDown()));
        if (this.localPlayerMode != GameType.SPECTATOR) {
            player.attack(entity);
            player.resetAttackStrengthTicker();
        }
    }

    public InteractionResult interact(Player player, Entity entity, InteractionHand interactionHand) {
        this.ensureHasSentCarriedItem();
        this.connection.send(new ServerboundInteractPacket(entity, interactionHand, player.isShiftKeyDown()));
        if (this.localPlayerMode == GameType.SPECTATOR) {
            return InteractionResult.PASS;
        }
        return player.interactOn(entity, interactionHand);
    }

    public InteractionResult interactAt(Player player, Entity entity, EntityHitResult entityHitResult, InteractionHand interactionHand) {
        this.ensureHasSentCarriedItem();
        Vec3 vec3 = entityHitResult.getLocation().subtract(entity.getX(), entity.getY(), entity.getZ());
        this.connection.send(new ServerboundInteractPacket(entity, interactionHand, vec3, player.isShiftKeyDown()));
        if (this.localPlayerMode == GameType.SPECTATOR) {
            return InteractionResult.PASS;
        }
        return entity.interactAt(player, vec3, interactionHand);
    }

    public ItemStack handleInventoryMouseClick(int n, int n2, int n3, ClickType clickType, Player player) {
        short s = player.containerMenu.backup(player.inventory);
        ItemStack itemStack = player.containerMenu.clicked(n2, n3, clickType, player);
        this.connection.send(new ServerboundContainerClickPacket(n, n2, n3, clickType, itemStack, s));
        return itemStack;
    }

    public void handlePlaceRecipe(int n, Recipe<?> recipe, boolean bl) {
        this.connection.send(new ServerboundPlaceRecipePacket(n, recipe, bl));
    }

    public void handleInventoryButtonClick(int n, int n2) {
        this.connection.send(new ServerboundContainerButtonClickPacket(n, n2));
    }

    public void handleCreativeModeItemAdd(ItemStack itemStack, int n) {
        if (this.localPlayerMode.isCreative()) {
            this.connection.send(new ServerboundSetCreativeModeSlotPacket(n, itemStack));
        }
    }

    public void handleCreativeModeItemDrop(ItemStack itemStack) {
        if (this.localPlayerMode.isCreative() && !itemStack.isEmpty()) {
            this.connection.send(new ServerboundSetCreativeModeSlotPacket(-1, itemStack));
        }
    }

    public void releaseUsingItem(Player player) {
        this.ensureHasSentCarriedItem();
        this.connection.send(new ServerboundPlayerActionPacket(ServerboundPlayerActionPacket.Action.RELEASE_USE_ITEM, BlockPos.ZERO, Direction.DOWN));
        player.releaseUsingItem();
    }

    public boolean hasExperience() {
        return this.localPlayerMode.isSurvival();
    }

    public boolean hasMissTime() {
        return !this.localPlayerMode.isCreative();
    }

    public boolean hasInfiniteItems() {
        return this.localPlayerMode.isCreative();
    }

    public boolean hasFarPickRange() {
        return this.localPlayerMode.isCreative();
    }

    public boolean isServerControlledInventory() {
        return this.minecraft.player.isPassenger() && this.minecraft.player.getVehicle() instanceof AbstractHorse;
    }

    public boolean isAlwaysFlying() {
        return this.localPlayerMode == GameType.SPECTATOR;
    }

    public GameType getPreviousPlayerMode() {
        return this.previousLocalPlayerMode;
    }

    public GameType getPlayerMode() {
        return this.localPlayerMode;
    }

    public boolean isDestroying() {
        return this.isDestroying;
    }

    public void handlePickItem(int n) {
        this.connection.send(new ServerboundPickItemPacket(n));
    }

    private void sendBlockAction(ServerboundPlayerActionPacket.Action action, BlockPos blockPos, Direction direction) {
        LocalPlayer localPlayer = this.minecraft.player;
        this.unAckedActions.put((Object)Pair.of((Object)blockPos, (Object)((Object)action)), (Object)localPlayer.position());
        this.connection.send(new ServerboundPlayerActionPacket(action, blockPos, direction));
    }

    public void handleBlockBreakAck(ClientLevel clientLevel, BlockPos blockPos, BlockState blockState, ServerboundPlayerActionPacket.Action action, boolean bl) {
        LocalPlayer localPlayer;
        Vec3 vec3 = (Vec3)this.unAckedActions.remove((Object)Pair.of((Object)blockPos, (Object)((Object)action)));
        BlockState blockState2 = clientLevel.getBlockState(blockPos);
        if ((vec3 == null || !bl || action != ServerboundPlayerActionPacket.Action.START_DESTROY_BLOCK && blockState2 != blockState) && blockState2 != blockState) {
            clientLevel.setKnownState(blockPos, blockState);
            localPlayer = this.minecraft.player;
            if (vec3 != null && clientLevel == localPlayer.level && localPlayer.isColliding(blockPos, blockState)) {
                localPlayer.absMoveTo(vec3.x, vec3.y, vec3.z);
            }
        }
        while (this.unAckedActions.size() >= 50) {
            localPlayer = (Pair)this.unAckedActions.firstKey();
            this.unAckedActions.removeFirst();
            LOGGER.error("Too many unacked block actions, dropping " + localPlayer);
        }
    }
}

