/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.authlib.GameProfile
 *  javax.annotation.Nullable
 */
package net.minecraft.client.player;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.client.CameraType;
import net.minecraft.client.ClientRecipeBook;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.BookEditScreen;
import net.minecraft.client.gui.screens.inventory.CommandBlockEditScreen;
import net.minecraft.client.gui.screens.inventory.JigsawBlockEditScreen;
import net.minecraft.client.gui.screens.inventory.MinecartCommandBlockEditScreen;
import net.minecraft.client.gui.screens.inventory.SignEditScreen;
import net.minecraft.client.gui.screens.inventory.StructureBlockEditScreen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.Input;
import net.minecraft.client.resources.sounds.AmbientSoundHandler;
import net.minecraft.client.resources.sounds.BiomeAmbientSoundsHandler;
import net.minecraft.client.resources.sounds.BubbleColumnAmbientSoundHandler;
import net.minecraft.client.resources.sounds.ElytraOnPlayerSoundInstance;
import net.minecraft.client.resources.sounds.RidingMinecartSoundInstance;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.resources.sounds.UnderwaterAmbientSoundHandler;
import net.minecraft.client.resources.sounds.UnderwaterAmbientSoundInstances;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.client.tutorial.Tutorial;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundChatPacket;
import net.minecraft.network.protocol.game.ServerboundClientCommandPacket;
import net.minecraft.network.protocol.game.ServerboundContainerClosePacket;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.network.protocol.game.ServerboundMoveVehiclePacket;
import net.minecraft.network.protocol.game.ServerboundPlayerAbilitiesPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerInputPacket;
import net.minecraft.network.protocol.game.ServerboundRecipeBookSeenRecipePacket;
import net.minecraft.network.protocol.game.ServerboundSwingPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.StatsCounter;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.PlayerRideableJumping;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ElytraItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.BaseCommandBlock;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.block.entity.CommandBlockEntity;
import net.minecraft.world.level.block.entity.JigsawBlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.StructureBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class LocalPlayer
extends AbstractClientPlayer {
    public final ClientPacketListener connection;
    private final StatsCounter stats;
    private final ClientRecipeBook recipeBook;
    private final List<AmbientSoundHandler> ambientSoundHandlers = Lists.newArrayList();
    private int permissionLevel = 0;
    private double xLast;
    private double yLast1;
    private double zLast;
    private float yRotLast;
    private float xRotLast;
    private boolean lastOnGround;
    private boolean crouching;
    private boolean wasShiftKeyDown;
    private boolean wasSprinting;
    private int positionReminder;
    private boolean flashOnSetHealth;
    private String serverBrand;
    public Input input;
    protected final Minecraft minecraft;
    protected int sprintTriggerTime;
    public int sprintTime;
    public float yBob;
    public float xBob;
    public float yBobO;
    public float xBobO;
    private int jumpRidingTicks;
    private float jumpRidingScale;
    public float portalTime;
    public float oPortalTime;
    private boolean startedUsingItem;
    private InteractionHand usingItemHand;
    private boolean handsBusy;
    private boolean autoJumpEnabled = true;
    private int autoJumpTime;
    private boolean wasFallFlying;
    private int waterVisionTime;
    private boolean showDeathScreen = true;

    public LocalPlayer(Minecraft minecraft, ClientLevel clientLevel, ClientPacketListener clientPacketListener, StatsCounter statsCounter, ClientRecipeBook clientRecipeBook, boolean bl, boolean bl2) {
        super(clientLevel, clientPacketListener.getLocalGameProfile());
        this.minecraft = minecraft;
        this.connection = clientPacketListener;
        this.stats = statsCounter;
        this.recipeBook = clientRecipeBook;
        this.wasShiftKeyDown = bl;
        this.wasSprinting = bl2;
        this.ambientSoundHandlers.add(new UnderwaterAmbientSoundHandler(this, minecraft.getSoundManager()));
        this.ambientSoundHandlers.add(new BubbleColumnAmbientSoundHandler(this));
        this.ambientSoundHandlers.add(new BiomeAmbientSoundsHandler(this, minecraft.getSoundManager(), clientLevel.getBiomeManager()));
    }

    @Override
    public boolean hurt(DamageSource damageSource, float f) {
        return false;
    }

    @Override
    public void heal(float f) {
    }

    @Override
    public boolean startRiding(Entity entity, boolean bl) {
        if (!super.startRiding(entity, bl)) {
            return false;
        }
        if (entity instanceof AbstractMinecart) {
            this.minecraft.getSoundManager().play(new RidingMinecartSoundInstance(this, (AbstractMinecart)entity));
        }
        if (entity instanceof Boat) {
            this.yRotO = entity.yRot;
            this.yRot = entity.yRot;
            this.setYHeadRot(entity.yRot);
        }
        return true;
    }

    @Override
    public void removeVehicle() {
        super.removeVehicle();
        this.handsBusy = false;
    }

    @Override
    public float getViewXRot(float f) {
        return this.xRot;
    }

    @Override
    public float getViewYRot(float f) {
        if (this.isPassenger()) {
            return super.getViewYRot(f);
        }
        return this.yRot;
    }

    @Override
    public void tick() {
        if (!this.level.hasChunkAt(new BlockPos(this.getX(), 0.0, this.getZ()))) {
            return;
        }
        super.tick();
        if (this.isPassenger()) {
            this.connection.send(new ServerboundMovePlayerPacket.Rot(this.yRot, this.xRot, this.onGround));
            this.connection.send(new ServerboundPlayerInputPacket(this.xxa, this.zza, this.input.jumping, this.input.shiftKeyDown));
            Entity entity = this.getRootVehicle();
            if (entity != this && entity.isControlledByLocalInstance()) {
                this.connection.send(new ServerboundMoveVehiclePacket(entity));
            }
        } else {
            this.sendPosition();
        }
        for (AmbientSoundHandler ambientSoundHandler : this.ambientSoundHandlers) {
            ambientSoundHandler.tick();
        }
    }

    public float getCurrentMood() {
        for (AmbientSoundHandler ambientSoundHandler : this.ambientSoundHandlers) {
            if (!(ambientSoundHandler instanceof BiomeAmbientSoundsHandler)) continue;
            return ((BiomeAmbientSoundsHandler)ambientSoundHandler).getMoodiness();
        }
        return 0.0f;
    }

    private void sendPosition() {
        boolean bl;
        boolean bl2 = this.isSprinting();
        if (bl2 != this.wasSprinting) {
            ServerboundPlayerCommandPacket.Action action = bl2 ? ServerboundPlayerCommandPacket.Action.START_SPRINTING : ServerboundPlayerCommandPacket.Action.STOP_SPRINTING;
            this.connection.send(new ServerboundPlayerCommandPacket(this, action));
            this.wasSprinting = bl2;
        }
        if ((bl = this.isShiftKeyDown()) != this.wasShiftKeyDown) {
            ServerboundPlayerCommandPacket.Action action = bl ? ServerboundPlayerCommandPacket.Action.PRESS_SHIFT_KEY : ServerboundPlayerCommandPacket.Action.RELEASE_SHIFT_KEY;
            this.connection.send(new ServerboundPlayerCommandPacket(this, action));
            this.wasShiftKeyDown = bl;
        }
        if (this.isControlledCamera()) {
            boolean bl3;
            double d = this.getX() - this.xLast;
            double d2 = this.getY() - this.yLast1;
            double d3 = this.getZ() - this.zLast;
            double d4 = this.yRot - this.yRotLast;
            double d5 = this.xRot - this.xRotLast;
            ++this.positionReminder;
            boolean bl4 = d * d + d2 * d2 + d3 * d3 > 9.0E-4 || this.positionReminder >= 20;
            boolean bl5 = bl3 = d4 != 0.0 || d5 != 0.0;
            if (this.isPassenger()) {
                Vec3 vec3 = this.getDeltaMovement();
                this.connection.send(new ServerboundMovePlayerPacket.PosRot(vec3.x, -999.0, vec3.z, this.yRot, this.xRot, this.onGround));
                bl4 = false;
            } else if (bl4 && bl3) {
                this.connection.send(new ServerboundMovePlayerPacket.PosRot(this.getX(), this.getY(), this.getZ(), this.yRot, this.xRot, this.onGround));
            } else if (bl4) {
                this.connection.send(new ServerboundMovePlayerPacket.Pos(this.getX(), this.getY(), this.getZ(), this.onGround));
            } else if (bl3) {
                this.connection.send(new ServerboundMovePlayerPacket.Rot(this.yRot, this.xRot, this.onGround));
            } else if (this.lastOnGround != this.onGround) {
                this.connection.send(new ServerboundMovePlayerPacket(this.onGround));
            }
            if (bl4) {
                this.xLast = this.getX();
                this.yLast1 = this.getY();
                this.zLast = this.getZ();
                this.positionReminder = 0;
            }
            if (bl3) {
                this.yRotLast = this.yRot;
                this.xRotLast = this.xRot;
            }
            this.lastOnGround = this.onGround;
            this.autoJumpEnabled = this.minecraft.options.autoJump;
        }
    }

    @Override
    public boolean drop(boolean bl) {
        ServerboundPlayerActionPacket.Action action = bl ? ServerboundPlayerActionPacket.Action.DROP_ALL_ITEMS : ServerboundPlayerActionPacket.Action.DROP_ITEM;
        this.connection.send(new ServerboundPlayerActionPacket(action, BlockPos.ZERO, Direction.DOWN));
        return this.inventory.removeItem(this.inventory.selected, bl && !this.inventory.getSelected().isEmpty() ? this.inventory.getSelected().getCount() : 1) != ItemStack.EMPTY;
    }

    public void chat(String string) {
        this.connection.send(new ServerboundChatPacket(string));
    }

    @Override
    public void swing(InteractionHand interactionHand) {
        super.swing(interactionHand);
        this.connection.send(new ServerboundSwingPacket(interactionHand));
    }

    @Override
    public void respawn() {
        this.connection.send(new ServerboundClientCommandPacket(ServerboundClientCommandPacket.Action.PERFORM_RESPAWN));
    }

    @Override
    protected void actuallyHurt(DamageSource damageSource, float f) {
        if (this.isInvulnerableTo(damageSource)) {
            return;
        }
        this.setHealth(this.getHealth() - f);
    }

    @Override
    public void closeContainer() {
        this.connection.send(new ServerboundContainerClosePacket(this.containerMenu.containerId));
        this.clientSideCloseContainer();
    }

    public void clientSideCloseContainer() {
        this.inventory.setCarried(ItemStack.EMPTY);
        super.closeContainer();
        this.minecraft.setScreen(null);
    }

    public void hurtTo(float f) {
        if (this.flashOnSetHealth) {
            float f2 = this.getHealth() - f;
            if (f2 <= 0.0f) {
                this.setHealth(f);
                if (f2 < 0.0f) {
                    this.invulnerableTime = 10;
                }
            } else {
                this.lastHurt = f2;
                this.setHealth(this.getHealth());
                this.invulnerableTime = 20;
                this.actuallyHurt(DamageSource.GENERIC, f2);
                this.hurtTime = this.hurtDuration = 10;
            }
        } else {
            this.setHealth(f);
            this.flashOnSetHealth = true;
        }
    }

    @Override
    public void onUpdateAbilities() {
        this.connection.send(new ServerboundPlayerAbilitiesPacket(this.abilities));
    }

    @Override
    public boolean isLocalPlayer() {
        return true;
    }

    @Override
    public boolean isSuppressingSlidingDownLadder() {
        return !this.abilities.flying && super.isSuppressingSlidingDownLadder();
    }

    @Override
    public boolean canSpawnSprintParticle() {
        return !this.abilities.flying && super.canSpawnSprintParticle();
    }

    @Override
    public boolean canSpawnSoulSpeedParticle() {
        return !this.abilities.flying && super.canSpawnSoulSpeedParticle();
    }

    protected void sendRidingJump() {
        this.connection.send(new ServerboundPlayerCommandPacket(this, ServerboundPlayerCommandPacket.Action.START_RIDING_JUMP, Mth.floor(this.getJumpRidingScale() * 100.0f)));
    }

    public void sendOpenInventory() {
        this.connection.send(new ServerboundPlayerCommandPacket(this, ServerboundPlayerCommandPacket.Action.OPEN_INVENTORY));
    }

    public void setServerBrand(String string) {
        this.serverBrand = string;
    }

    public String getServerBrand() {
        return this.serverBrand;
    }

    public StatsCounter getStats() {
        return this.stats;
    }

    public ClientRecipeBook getRecipeBook() {
        return this.recipeBook;
    }

    public void removeRecipeHighlight(Recipe<?> recipe) {
        if (this.recipeBook.willHighlight(recipe)) {
            this.recipeBook.removeHighlight(recipe);
            this.connection.send(new ServerboundRecipeBookSeenRecipePacket(recipe));
        }
    }

    @Override
    protected int getPermissionLevel() {
        return this.permissionLevel;
    }

    public void setPermissionLevel(int n) {
        this.permissionLevel = n;
    }

    @Override
    public void displayClientMessage(Component component, boolean bl) {
        if (bl) {
            this.minecraft.gui.setOverlayMessage(component, false);
        } else {
            this.minecraft.gui.getChat().addMessage(component);
        }
    }

    private void moveTowardsClosestSpace(double d, double d2) {
        Direction[] arrdirection;
        BlockPos blockPos = new BlockPos(d, this.getY(), d2);
        if (!this.suffocatesAt(blockPos)) {
            return;
        }
        double d3 = d - (double)blockPos.getX();
        double d4 = d2 - (double)blockPos.getZ();
        Direction direction = null;
        double d5 = Double.MAX_VALUE;
        for (Direction direction2 : arrdirection = new Direction[]{Direction.WEST, Direction.EAST, Direction.NORTH, Direction.SOUTH}) {
            double d6;
            double d7 = direction2.getAxis().choose(d3, 0.0, d4);
            double d8 = d6 = direction2.getAxisDirection() == Direction.AxisDirection.POSITIVE ? 1.0 - d7 : d7;
            if (!(d6 < d5) || this.suffocatesAt(blockPos.relative(direction2))) continue;
            d5 = d6;
            direction = direction2;
        }
        if (direction != null) {
            Vec3 vec3 = this.getDeltaMovement();
            if (direction.getAxis() == Direction.Axis.X) {
                this.setDeltaMovement(0.1 * (double)direction.getStepX(), vec3.y, vec3.z);
            } else {
                this.setDeltaMovement(vec3.x, vec3.y, 0.1 * (double)direction.getStepZ());
            }
        }
    }

    private boolean suffocatesAt(BlockPos blockPos2) {
        AABB aABB = this.getBoundingBox();
        AABB aABB2 = new AABB(blockPos2.getX(), aABB.minY, blockPos2.getZ(), (double)blockPos2.getX() + 1.0, aABB.maxY, (double)blockPos2.getZ() + 1.0).deflate(1.0E-7);
        return !this.level.noBlockCollision(this, aABB2, (blockState, blockPos) -> blockState.isSuffocating(this.level, (BlockPos)blockPos));
    }

    @Override
    public void setSprinting(boolean bl) {
        super.setSprinting(bl);
        this.sprintTime = 0;
    }

    public void setExperienceValues(float f, int n, int n2) {
        this.experienceProgress = f;
        this.totalExperience = n;
        this.experienceLevel = n2;
    }

    @Override
    public void sendMessage(Component component, UUID uUID) {
        this.minecraft.gui.getChat().addMessage(component);
    }

    @Override
    public void handleEntityEvent(byte by) {
        if (by >= 24 && by <= 28) {
            this.setPermissionLevel(by - 24);
        } else {
            super.handleEntityEvent(by);
        }
    }

    public void setShowDeathScreen(boolean bl) {
        this.showDeathScreen = bl;
    }

    public boolean shouldShowDeathScreen() {
        return this.showDeathScreen;
    }

    @Override
    public void playSound(SoundEvent soundEvent, float f, float f2) {
        this.level.playLocalSound(this.getX(), this.getY(), this.getZ(), soundEvent, this.getSoundSource(), f, f2, false);
    }

    @Override
    public void playNotifySound(SoundEvent soundEvent, SoundSource soundSource, float f, float f2) {
        this.level.playLocalSound(this.getX(), this.getY(), this.getZ(), soundEvent, soundSource, f, f2, false);
    }

    @Override
    public boolean isEffectiveAi() {
        return true;
    }

    @Override
    public void startUsingItem(InteractionHand interactionHand) {
        ItemStack itemStack = this.getItemInHand(interactionHand);
        if (itemStack.isEmpty() || this.isUsingItem()) {
            return;
        }
        super.startUsingItem(interactionHand);
        this.startedUsingItem = true;
        this.usingItemHand = interactionHand;
    }

    @Override
    public boolean isUsingItem() {
        return this.startedUsingItem;
    }

    @Override
    public void stopUsingItem() {
        super.stopUsingItem();
        this.startedUsingItem = false;
    }

    @Override
    public InteractionHand getUsedItemHand() {
        return this.usingItemHand;
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> entityDataAccessor) {
        super.onSyncedDataUpdated(entityDataAccessor);
        if (DATA_LIVING_ENTITY_FLAGS.equals(entityDataAccessor)) {
            InteractionHand interactionHand;
            boolean bl = ((Byte)this.entityData.get(DATA_LIVING_ENTITY_FLAGS) & 1) > 0;
            InteractionHand interactionHand2 = interactionHand = ((Byte)this.entityData.get(DATA_LIVING_ENTITY_FLAGS) & 2) > 0 ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
            if (bl && !this.startedUsingItem) {
                this.startUsingItem(interactionHand);
            } else if (!bl && this.startedUsingItem) {
                this.stopUsingItem();
            }
        }
        if (DATA_SHARED_FLAGS_ID.equals(entityDataAccessor) && this.isFallFlying() && !this.wasFallFlying) {
            this.minecraft.getSoundManager().play(new ElytraOnPlayerSoundInstance(this));
        }
    }

    public boolean isRidingJumpable() {
        Entity entity = this.getVehicle();
        return this.isPassenger() && entity instanceof PlayerRideableJumping && ((PlayerRideableJumping)((Object)entity)).canJump();
    }

    public float getJumpRidingScale() {
        return this.jumpRidingScale;
    }

    @Override
    public void openTextEdit(SignBlockEntity signBlockEntity) {
        this.minecraft.setScreen(new SignEditScreen(signBlockEntity));
    }

    @Override
    public void openMinecartCommandBlock(BaseCommandBlock baseCommandBlock) {
        this.minecraft.setScreen(new MinecartCommandBlockEditScreen(baseCommandBlock));
    }

    @Override
    public void openCommandBlock(CommandBlockEntity commandBlockEntity) {
        this.minecraft.setScreen(new CommandBlockEditScreen(commandBlockEntity));
    }

    @Override
    public void openStructureBlock(StructureBlockEntity structureBlockEntity) {
        this.minecraft.setScreen(new StructureBlockEditScreen(structureBlockEntity));
    }

    @Override
    public void openJigsawBlock(JigsawBlockEntity jigsawBlockEntity) {
        this.minecraft.setScreen(new JigsawBlockEditScreen(jigsawBlockEntity));
    }

    @Override
    public void openItemGui(ItemStack itemStack, InteractionHand interactionHand) {
        Item item = itemStack.getItem();
        if (item == Items.WRITABLE_BOOK) {
            this.minecraft.setScreen(new BookEditScreen(this, itemStack, interactionHand));
        }
    }

    @Override
    public void crit(Entity entity) {
        this.minecraft.particleEngine.createTrackingEmitter(entity, ParticleTypes.CRIT);
    }

    @Override
    public void magicCrit(Entity entity) {
        this.minecraft.particleEngine.createTrackingEmitter(entity, ParticleTypes.ENCHANTED_HIT);
    }

    @Override
    public boolean isShiftKeyDown() {
        return this.input != null && this.input.shiftKeyDown;
    }

    @Override
    public boolean isCrouching() {
        return this.crouching;
    }

    public boolean isMovingSlowly() {
        return this.isCrouching() || this.isVisuallyCrawling();
    }

    @Override
    public void serverAiStep() {
        super.serverAiStep();
        if (this.isControlledCamera()) {
            this.xxa = this.input.leftImpulse;
            this.zza = this.input.forwardImpulse;
            this.jumping = this.input.jumping;
            this.yBobO = this.yBob;
            this.xBobO = this.xBob;
            this.xBob = (float)((double)this.xBob + (double)(this.xRot - this.xBob) * 0.5);
            this.yBob = (float)((double)this.yBob + (double)(this.yRot - this.yBob) * 0.5);
        }
    }

    protected boolean isControlledCamera() {
        return this.minecraft.getCameraEntity() == this;
    }

    @Override
    public void aiStep() {
        boolean bl;
        boolean bl2;
        ItemStack itemStack;
        int n;
        ++this.sprintTime;
        if (this.sprintTriggerTime > 0) {
            --this.sprintTriggerTime;
        }
        this.handleNetherPortalClient();
        boolean bl3 = this.input.jumping;
        boolean bl4 = this.input.shiftKeyDown;
        boolean bl5 = this.hasEnoughImpulseToStartSprinting();
        this.crouching = !this.abilities.flying && !this.isSwimming() && this.canEnterPose(Pose.CROUCHING) && (this.isShiftKeyDown() || !this.isSleeping() && !this.canEnterPose(Pose.STANDING));
        this.input.tick(this.isMovingSlowly());
        this.minecraft.getTutorial().onInput(this.input);
        if (this.isUsingItem() && !this.isPassenger()) {
            this.input.leftImpulse *= 0.2f;
            this.input.forwardImpulse *= 0.2f;
            this.sprintTriggerTime = 0;
        }
        boolean bl6 = false;
        if (this.autoJumpTime > 0) {
            --this.autoJumpTime;
            bl6 = true;
            this.input.jumping = true;
        }
        if (!this.noPhysics) {
            this.moveTowardsClosestSpace(this.getX() - (double)this.getBbWidth() * 0.35, this.getZ() + (double)this.getBbWidth() * 0.35);
            this.moveTowardsClosestSpace(this.getX() - (double)this.getBbWidth() * 0.35, this.getZ() - (double)this.getBbWidth() * 0.35);
            this.moveTowardsClosestSpace(this.getX() + (double)this.getBbWidth() * 0.35, this.getZ() - (double)this.getBbWidth() * 0.35);
            this.moveTowardsClosestSpace(this.getX() + (double)this.getBbWidth() * 0.35, this.getZ() + (double)this.getBbWidth() * 0.35);
        }
        if (bl4) {
            this.sprintTriggerTime = 0;
        }
        boolean bl7 = bl2 = (float)this.getFoodData().getFoodLevel() > 6.0f || this.abilities.mayfly;
        if (!(!this.onGround && !this.isUnderWater() || bl4 || bl5 || !this.hasEnoughImpulseToStartSprinting() || this.isSprinting() || !bl2 || this.isUsingItem() || this.hasEffect(MobEffects.BLINDNESS))) {
            if (this.sprintTriggerTime > 0 || this.minecraft.options.keySprint.isDown()) {
                this.setSprinting(true);
            } else {
                this.sprintTriggerTime = 7;
            }
        }
        if (!this.isSprinting() && (!this.isInWater() || this.isUnderWater()) && this.hasEnoughImpulseToStartSprinting() && bl2 && !this.isUsingItem() && !this.hasEffect(MobEffects.BLINDNESS) && this.minecraft.options.keySprint.isDown()) {
            this.setSprinting(true);
        }
        if (this.isSprinting()) {
            bl = !this.input.hasForwardImpulse() || !bl2;
            int n2 = n = bl || this.horizontalCollision || this.isInWater() && !this.isUnderWater() ? 1 : 0;
            if (this.isSwimming()) {
                if (!this.onGround && !this.input.shiftKeyDown && bl || !this.isInWater()) {
                    this.setSprinting(false);
                }
            } else if (n != 0) {
                this.setSprinting(false);
            }
        }
        bl = false;
        if (this.abilities.mayfly) {
            if (this.minecraft.gameMode.isAlwaysFlying()) {
                if (!this.abilities.flying) {
                    this.abilities.flying = true;
                    bl = true;
                    this.onUpdateAbilities();
                }
            } else if (!bl3 && this.input.jumping && !bl6) {
                if (this.jumpTriggerTime == 0) {
                    this.jumpTriggerTime = 7;
                } else if (!this.isSwimming()) {
                    this.abilities.flying = !this.abilities.flying;
                    bl = true;
                    this.onUpdateAbilities();
                    this.jumpTriggerTime = 0;
                }
            }
        }
        if (this.input.jumping && !bl && !bl3 && !this.abilities.flying && !this.isPassenger() && !this.onClimbable() && (itemStack = this.getItemBySlot(EquipmentSlot.CHEST)).getItem() == Items.ELYTRA && ElytraItem.isFlyEnabled(itemStack) && this.tryToStartFallFlying()) {
            this.connection.send(new ServerboundPlayerCommandPacket(this, ServerboundPlayerCommandPacket.Action.START_FALL_FLYING));
        }
        this.wasFallFlying = this.isFallFlying();
        if (this.isInWater() && this.input.shiftKeyDown && this.isAffectedByFluids()) {
            this.goDownInWater();
        }
        if (this.isEyeInFluid(FluidTags.WATER)) {
            n = this.isSpectator() ? 10 : 1;
            this.waterVisionTime = Mth.clamp(this.waterVisionTime + n, 0, 600);
        } else if (this.waterVisionTime > 0) {
            this.isEyeInFluid(FluidTags.WATER);
            this.waterVisionTime = Mth.clamp(this.waterVisionTime - 10, 0, 600);
        }
        if (this.abilities.flying && this.isControlledCamera()) {
            n = 0;
            if (this.input.shiftKeyDown) {
                --n;
            }
            if (this.input.jumping) {
                ++n;
            }
            if (n != 0) {
                this.setDeltaMovement(this.getDeltaMovement().add(0.0, (float)n * this.abilities.getFlyingSpeed() * 3.0f, 0.0));
            }
        }
        if (this.isRidingJumpable()) {
            PlayerRideableJumping playerRideableJumping = (PlayerRideableJumping)((Object)this.getVehicle());
            if (this.jumpRidingTicks < 0) {
                ++this.jumpRidingTicks;
                if (this.jumpRidingTicks == 0) {
                    this.jumpRidingScale = 0.0f;
                }
            }
            if (bl3 && !this.input.jumping) {
                this.jumpRidingTicks = -10;
                playerRideableJumping.onPlayerJump(Mth.floor(this.getJumpRidingScale() * 100.0f));
                this.sendRidingJump();
            } else if (!bl3 && this.input.jumping) {
                this.jumpRidingTicks = 0;
                this.jumpRidingScale = 0.0f;
            } else if (bl3) {
                ++this.jumpRidingTicks;
                this.jumpRidingScale = this.jumpRidingTicks < 10 ? (float)this.jumpRidingTicks * 0.1f : 0.8f + 2.0f / (float)(this.jumpRidingTicks - 9) * 0.1f;
            }
        } else {
            this.jumpRidingScale = 0.0f;
        }
        super.aiStep();
        if (this.onGround && this.abilities.flying && !this.minecraft.gameMode.isAlwaysFlying()) {
            this.abilities.flying = false;
            this.onUpdateAbilities();
        }
    }

    private void handleNetherPortalClient() {
        this.oPortalTime = this.portalTime;
        if (this.isInsidePortal) {
            if (this.minecraft.screen != null && !this.minecraft.screen.isPauseScreen()) {
                if (this.minecraft.screen instanceof AbstractContainerScreen) {
                    this.closeContainer();
                }
                this.minecraft.setScreen(null);
            }
            if (this.portalTime == 0.0f) {
                this.minecraft.getSoundManager().play(SimpleSoundInstance.forLocalAmbience(SoundEvents.PORTAL_TRIGGER, this.random.nextFloat() * 0.4f + 0.8f, 0.25f));
            }
            this.portalTime += 0.0125f;
            if (this.portalTime >= 1.0f) {
                this.portalTime = 1.0f;
            }
            this.isInsidePortal = false;
        } else if (this.hasEffect(MobEffects.CONFUSION) && this.getEffect(MobEffects.CONFUSION).getDuration() > 60) {
            this.portalTime += 0.006666667f;
            if (this.portalTime > 1.0f) {
                this.portalTime = 1.0f;
            }
        } else {
            if (this.portalTime > 0.0f) {
                this.portalTime -= 0.05f;
            }
            if (this.portalTime < 0.0f) {
                this.portalTime = 0.0f;
            }
        }
        this.processPortalCooldown();
    }

    @Override
    public void rideTick() {
        super.rideTick();
        this.handsBusy = false;
        if (this.getVehicle() instanceof Boat) {
            Boat boat = (Boat)this.getVehicle();
            boat.setInput(this.input.left, this.input.right, this.input.up, this.input.down);
            this.handsBusy |= this.input.left || this.input.right || this.input.up || this.input.down;
        }
    }

    public boolean isHandsBusy() {
        return this.handsBusy;
    }

    @Nullable
    @Override
    public MobEffectInstance removeEffectNoUpdate(@Nullable MobEffect mobEffect) {
        if (mobEffect == MobEffects.CONFUSION) {
            this.oPortalTime = 0.0f;
            this.portalTime = 0.0f;
        }
        return super.removeEffectNoUpdate(mobEffect);
    }

    @Override
    public void move(MoverType moverType, Vec3 vec3) {
        double d = this.getX();
        double d2 = this.getZ();
        super.move(moverType, vec3);
        this.updateAutoJump((float)(this.getX() - d), (float)(this.getZ() - d2));
    }

    public boolean isAutoJumpEnabled() {
        return this.autoJumpEnabled;
    }

    protected void updateAutoJump(float f, float f2) {
        float f3;
        if (!this.canAutoJump()) {
            return;
        }
        Vec3 vec3 = this.position();
        Vec3 vec32 = vec3.add(f, 0.0, f2);
        Vec3 vec33 = new Vec3(f, 0.0, f2);
        float f4 = this.getSpeed();
        float f5 = (float)vec33.lengthSqr();
        if (f5 <= 0.001f) {
            Vec2 vec2 = this.input.getMoveVector();
            float f6 = f4 * vec2.x;
            float f7 = f4 * vec2.y;
            f3 = Mth.sin(this.yRot * 0.017453292f);
            float f8 = Mth.cos(this.yRot * 0.017453292f);
            vec33 = new Vec3(f6 * f8 - f7 * f3, vec33.y, f7 * f8 + f6 * f3);
            f5 = (float)vec33.lengthSqr();
            if (f5 <= 0.001f) {
                return;
            }
        }
        float f9 = Mth.fastInvSqrt(f5);
        Vec3 vec34 = vec33.scale(f9);
        Vec3 vec35 = this.getForward();
        f3 = (float)(vec35.x * vec34.x + vec35.z * vec34.z);
        if (f3 < -0.15f) {
            return;
        }
        CollisionContext collisionContext = CollisionContext.of(this);
        BlockPos blockPos = new BlockPos(this.getX(), this.getBoundingBox().maxY, this.getZ());
        BlockState blockState = this.level.getBlockState(blockPos);
        if (!blockState.getCollisionShape(this.level, blockPos, collisionContext).isEmpty()) {
            return;
        }
        BlockState blockState2 = this.level.getBlockState(blockPos = blockPos.above());
        if (!blockState2.getCollisionShape(this.level, blockPos, collisionContext).isEmpty()) {
            return;
        }
        float f10 = 7.0f;
        float f11 = 1.2f;
        if (this.hasEffect(MobEffects.JUMP)) {
            f11 += (float)(this.getEffect(MobEffects.JUMP).getAmplifier() + 1) * 0.75f;
        }
        float f12 = Math.max(f4 * 7.0f, 1.0f / f9);
        Vec3 vec36 = vec3;
        Vec3 vec37 = vec32.add(vec34.scale(f12));
        float f13 = this.getBbWidth();
        float f14 = this.getBbHeight();
        AABB aABB = new AABB(vec36, vec37.add(0.0, f14, 0.0)).inflate(f13, 0.0, f13);
        vec36 = vec36.add(0.0, 0.5099999904632568, 0.0);
        vec37 = vec37.add(0.0, 0.5099999904632568, 0.0);
        Vec3 vec38 = vec34.cross(new Vec3(0.0, 1.0, 0.0));
        Vec3 vec39 = vec38.scale(f13 * 0.5f);
        Vec3 vec310 = vec36.subtract(vec39);
        Vec3 vec311 = vec37.subtract(vec39);
        Vec3 vec312 = vec36.add(vec39);
        Vec3 vec313 = vec37.add(vec39);
        Iterator iterator = this.level.getCollisions(this, aABB, entity -> true).flatMap(voxelShape -> voxelShape.toAabbs().stream()).iterator();
        float f15 = Float.MIN_VALUE;
        while (iterator.hasNext()) {
            AABB aABB2 = (AABB)iterator.next();
            if (!aABB2.intersects(vec310, vec311) && !aABB2.intersects(vec312, vec313)) continue;
            f15 = (float)aABB2.maxY;
            Vec3 vec314 = aABB2.getCenter();
            BlockPos blockPos2 = new BlockPos(vec314);
            int n = 1;
            while ((float)n < f11) {
                BlockState blockState3;
                BlockPos blockPos3 = blockPos2.above(n);
                BlockState blockState4 = this.level.getBlockState(blockPos3);
                VoxelShape voxelShape2 = blockState4.getCollisionShape(this.level, blockPos3, collisionContext);
                if (!voxelShape2.isEmpty() && (double)(f15 = (float)voxelShape2.max(Direction.Axis.Y) + (float)blockPos3.getY()) - this.getY() > (double)f11) {
                    return;
                }
                if (n > 1 && !(blockState3 = this.level.getBlockState(blockPos = blockPos.above())).getCollisionShape(this.level, blockPos, collisionContext).isEmpty()) {
                    return;
                }
                ++n;
            }
            break block0;
        }
        if (f15 == Float.MIN_VALUE) {
            return;
        }
        float f16 = (float)((double)f15 - this.getY());
        if (f16 <= 0.5f || f16 > f11) {
            return;
        }
        this.autoJumpTime = 1;
    }

    private boolean canAutoJump() {
        return this.isAutoJumpEnabled() && this.autoJumpTime <= 0 && this.onGround && !this.isStayingOnGroundSurface() && !this.isPassenger() && this.isMoving() && (double)this.getBlockJumpFactor() >= 1.0;
    }

    private boolean isMoving() {
        Vec2 vec2 = this.input.getMoveVector();
        return vec2.x != 0.0f || vec2.y != 0.0f;
    }

    private boolean hasEnoughImpulseToStartSprinting() {
        double d = 0.8;
        return this.isUnderWater() ? this.input.hasForwardImpulse() : (double)this.input.forwardImpulse >= 0.8;
    }

    public float getWaterVision() {
        if (!this.isEyeInFluid(FluidTags.WATER)) {
            return 0.0f;
        }
        float f = 600.0f;
        float f2 = 100.0f;
        if ((float)this.waterVisionTime >= 600.0f) {
            return 1.0f;
        }
        float f3 = Mth.clamp((float)this.waterVisionTime / 100.0f, 0.0f, 1.0f);
        float f4 = (float)this.waterVisionTime < 100.0f ? 0.0f : Mth.clamp(((float)this.waterVisionTime - 100.0f) / 500.0f, 0.0f, 1.0f);
        return f3 * 0.6f + f4 * 0.39999998f;
    }

    @Override
    public boolean isUnderWater() {
        return this.wasUnderwater;
    }

    @Override
    protected boolean updateIsUnderwater() {
        boolean bl = this.wasUnderwater;
        boolean bl2 = super.updateIsUnderwater();
        if (this.isSpectator()) {
            return this.wasUnderwater;
        }
        if (!bl && bl2) {
            this.level.playLocalSound(this.getX(), this.getY(), this.getZ(), SoundEvents.AMBIENT_UNDERWATER_ENTER, SoundSource.AMBIENT, 1.0f, 1.0f, false);
            this.minecraft.getSoundManager().play(new UnderwaterAmbientSoundInstances.UnderwaterAmbientSoundInstance(this));
        }
        if (bl && !bl2) {
            this.level.playLocalSound(this.getX(), this.getY(), this.getZ(), SoundEvents.AMBIENT_UNDERWATER_EXIT, SoundSource.AMBIENT, 1.0f, 1.0f, false);
        }
        return this.wasUnderwater;
    }

    @Override
    public Vec3 getRopeHoldPosition(float f) {
        if (this.minecraft.options.getCameraType().isFirstPerson()) {
            float f2 = Mth.lerp(f * 0.5f, this.yRot, this.yRotO) * 0.017453292f;
            float f3 = Mth.lerp(f * 0.5f, this.xRot, this.xRotO) * 0.017453292f;
            double d = this.getMainArm() == HumanoidArm.RIGHT ? -1.0 : 1.0;
            Vec3 vec3 = new Vec3(0.39 * d, -0.6, 0.3);
            return vec3.xRot(-f3).yRot(-f2).add(this.getEyePosition(f));
        }
        return super.getRopeHoldPosition(f);
    }
}

