/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  org.lwjgl.glfw.GLFWErrorCallbackI
 */
package net.minecraft.client;

import com.mojang.blaze3d.Blaze3D;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.ClipboardManager;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Window;
import java.io.File;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.CrashReport;
import net.minecraft.ReportedException;
import net.minecraft.Util;
import net.minecraft.client.CycleOption;
import net.minecraft.client.DebugQueryHandler;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Option;
import net.minecraft.client.Options;
import net.minecraft.client.ProgressOption;
import net.minecraft.client.Screenshot;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Overlay;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.SimpleOptionsSubScreen;
import net.minecraft.client.gui.screens.controls.ControlsScreen;
import net.minecraft.client.gui.screens.debug.GameModeSwitcherScreen;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.commands.arguments.blocks.BlockStateParser;
import net.minecraft.core.BlockPos;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.lwjgl.glfw.GLFWErrorCallbackI;

public class KeyboardHandler {
    private final Minecraft minecraft;
    private boolean sendRepeatsToGui;
    private final ClipboardManager clipboardManager = new ClipboardManager();
    private long debugCrashKeyTime = -1L;
    private long debugCrashKeyReportedTime = -1L;
    private long debugCrashKeyReportedCount = -1L;
    private boolean handledDebugKey;

    public KeyboardHandler(Minecraft minecraft) {
        this.minecraft = minecraft;
    }

    private void debugFeedbackTranslated(String string, Object ... arrobject) {
        this.minecraft.gui.getChat().addMessage(new TextComponent("").append(new TranslatableComponent("debug.prefix").withStyle(ChatFormatting.YELLOW, ChatFormatting.BOLD)).append(" ").append(new TranslatableComponent(string, arrobject)));
    }

    private void debugWarningTranslated(String string, Object ... arrobject) {
        this.minecraft.gui.getChat().addMessage(new TextComponent("").append(new TranslatableComponent("debug.prefix").withStyle(ChatFormatting.RED, ChatFormatting.BOLD)).append(" ").append(new TranslatableComponent(string, arrobject)));
    }

    private boolean handleDebugKeys(int n) {
        if (this.debugCrashKeyTime > 0L && this.debugCrashKeyTime < Util.getMillis() - 100L) {
            return true;
        }
        switch (n) {
            case 65: {
                this.minecraft.levelRenderer.allChanged();
                this.debugFeedbackTranslated("debug.reload_chunks.message", new Object[0]);
                return true;
            }
            case 66: {
                boolean bl = !this.minecraft.getEntityRenderDispatcher().shouldRenderHitBoxes();
                this.minecraft.getEntityRenderDispatcher().setRenderHitBoxes(bl);
                this.debugFeedbackTranslated(bl ? "debug.show_hitboxes.on" : "debug.show_hitboxes.off", new Object[0]);
                return true;
            }
            case 68: {
                if (this.minecraft.gui != null) {
                    this.minecraft.gui.getChat().clearMessages(false);
                }
                return true;
            }
            case 70: {
                Option.RENDER_DISTANCE.set(this.minecraft.options, Mth.clamp((double)(this.minecraft.options.renderDistance + (Screen.hasShiftDown() ? -1 : 1)), Option.RENDER_DISTANCE.getMinValue(), Option.RENDER_DISTANCE.getMaxValue()));
                this.debugFeedbackTranslated("debug.cycle_renderdistance.message", this.minecraft.options.renderDistance);
                return true;
            }
            case 71: {
                boolean bl = this.minecraft.debugRenderer.switchRenderChunkborder();
                this.debugFeedbackTranslated(bl ? "debug.chunk_boundaries.on" : "debug.chunk_boundaries.off", new Object[0]);
                return true;
            }
            case 72: {
                this.minecraft.options.advancedItemTooltips = !this.minecraft.options.advancedItemTooltips;
                this.debugFeedbackTranslated(this.minecraft.options.advancedItemTooltips ? "debug.advanced_tooltips.on" : "debug.advanced_tooltips.off", new Object[0]);
                this.minecraft.options.save();
                return true;
            }
            case 73: {
                if (!this.minecraft.player.isReducedDebugInfo()) {
                    this.copyRecreateCommand(this.minecraft.player.hasPermissions(2), !Screen.hasShiftDown());
                }
                return true;
            }
            case 78: {
                if (!this.minecraft.player.hasPermissions(2)) {
                    this.debugFeedbackTranslated("debug.creative_spectator.error", new Object[0]);
                } else if (!this.minecraft.player.isSpectator()) {
                    this.minecraft.player.chat("/gamemode spectator");
                } else {
                    this.minecraft.player.chat("/gamemode " + this.minecraft.gameMode.getPreviousPlayerMode().getName());
                }
                return true;
            }
            case 293: {
                if (!this.minecraft.player.hasPermissions(2)) {
                    this.debugFeedbackTranslated("debug.gamemodes.error", new Object[0]);
                } else {
                    this.minecraft.setScreen(new GameModeSwitcherScreen());
                }
                return true;
            }
            case 80: {
                this.minecraft.options.pauseOnLostFocus = !this.minecraft.options.pauseOnLostFocus;
                this.minecraft.options.save();
                this.debugFeedbackTranslated(this.minecraft.options.pauseOnLostFocus ? "debug.pause_focus.on" : "debug.pause_focus.off", new Object[0]);
                return true;
            }
            case 81: {
                this.debugFeedbackTranslated("debug.help.message", new Object[0]);
                ChatComponent chatComponent = this.minecraft.gui.getChat();
                chatComponent.addMessage(new TranslatableComponent("debug.reload_chunks.help"));
                chatComponent.addMessage(new TranslatableComponent("debug.show_hitboxes.help"));
                chatComponent.addMessage(new TranslatableComponent("debug.copy_location.help"));
                chatComponent.addMessage(new TranslatableComponent("debug.clear_chat.help"));
                chatComponent.addMessage(new TranslatableComponent("debug.cycle_renderdistance.help"));
                chatComponent.addMessage(new TranslatableComponent("debug.chunk_boundaries.help"));
                chatComponent.addMessage(new TranslatableComponent("debug.advanced_tooltips.help"));
                chatComponent.addMessage(new TranslatableComponent("debug.inspect.help"));
                chatComponent.addMessage(new TranslatableComponent("debug.creative_spectator.help"));
                chatComponent.addMessage(new TranslatableComponent("debug.pause_focus.help"));
                chatComponent.addMessage(new TranslatableComponent("debug.help.help"));
                chatComponent.addMessage(new TranslatableComponent("debug.reload_resourcepacks.help"));
                chatComponent.addMessage(new TranslatableComponent("debug.pause.help"));
                chatComponent.addMessage(new TranslatableComponent("debug.gamemodes.help"));
                return true;
            }
            case 84: {
                this.debugFeedbackTranslated("debug.reload_resourcepacks.message", new Object[0]);
                this.minecraft.reloadResourcePacks();
                return true;
            }
            case 67: {
                if (this.minecraft.player.isReducedDebugInfo()) {
                    return false;
                }
                ClientPacketListener clientPacketListener = this.minecraft.player.connection;
                if (clientPacketListener == null) {
                    return false;
                }
                this.debugFeedbackTranslated("debug.copy_location.message", new Object[0]);
                this.setClipboard(String.format(Locale.ROOT, "/execute in %s run tp @s %.2f %.2f %.2f %.2f %.2f", this.minecraft.player.level.dimension().location(), this.minecraft.player.getX(), this.minecraft.player.getY(), this.minecraft.player.getZ(), Float.valueOf(this.minecraft.player.yRot), Float.valueOf(this.minecraft.player.xRot)));
                return true;
            }
        }
        return false;
    }

    private void copyRecreateCommand(boolean bl, boolean bl2) {
        HitResult hitResult = this.minecraft.hitResult;
        if (hitResult == null) {
            return;
        }
        switch (hitResult.getType()) {
            case BLOCK: {
                BlockPos blockPos = ((BlockHitResult)hitResult).getBlockPos();
                BlockState blockState = this.minecraft.player.level.getBlockState(blockPos);
                if (bl) {
                    if (bl2) {
                        this.minecraft.player.connection.getDebugQueryHandler().queryBlockEntityTag(blockPos, compoundTag -> {
                            this.copyCreateBlockCommand(blockState, blockPos, (CompoundTag)compoundTag);
                            this.debugFeedbackTranslated("debug.inspect.server.block", new Object[0]);
                        });
                        break;
                    }
                    BlockEntity blockEntity = this.minecraft.player.level.getBlockEntity(blockPos);
                    CompoundTag compoundTag2 = blockEntity != null ? blockEntity.save(new CompoundTag()) : null;
                    this.copyCreateBlockCommand(blockState, blockPos, compoundTag2);
                    this.debugFeedbackTranslated("debug.inspect.client.block", new Object[0]);
                    break;
                }
                this.copyCreateBlockCommand(blockState, blockPos, null);
                this.debugFeedbackTranslated("debug.inspect.client.block", new Object[0]);
                break;
            }
            case ENTITY: {
                Entity entity = ((EntityHitResult)hitResult).getEntity();
                ResourceLocation resourceLocation = Registry.ENTITY_TYPE.getKey(entity.getType());
                if (bl) {
                    if (bl2) {
                        this.minecraft.player.connection.getDebugQueryHandler().queryEntityTag(entity.getId(), compoundTag -> {
                            this.copyCreateEntityCommand(resourceLocation, entity.position(), (CompoundTag)compoundTag);
                            this.debugFeedbackTranslated("debug.inspect.server.entity", new Object[0]);
                        });
                        break;
                    }
                    CompoundTag compoundTag3 = entity.saveWithoutId(new CompoundTag());
                    this.copyCreateEntityCommand(resourceLocation, entity.position(), compoundTag3);
                    this.debugFeedbackTranslated("debug.inspect.client.entity", new Object[0]);
                    break;
                }
                this.copyCreateEntityCommand(resourceLocation, entity.position(), null);
                this.debugFeedbackTranslated("debug.inspect.client.entity", new Object[0]);
                break;
            }
        }
    }

    private void copyCreateBlockCommand(BlockState blockState, BlockPos blockPos, @Nullable CompoundTag compoundTag) {
        if (compoundTag != null) {
            compoundTag.remove("x");
            compoundTag.remove("y");
            compoundTag.remove("z");
            compoundTag.remove("id");
        }
        StringBuilder stringBuilder = new StringBuilder(BlockStateParser.serialize(blockState));
        if (compoundTag != null) {
            stringBuilder.append(compoundTag);
        }
        String string = String.format(Locale.ROOT, "/setblock %d %d %d %s", blockPos.getX(), blockPos.getY(), blockPos.getZ(), stringBuilder);
        this.setClipboard(string);
    }

    private void copyCreateEntityCommand(ResourceLocation resourceLocation, Vec3 vec3, @Nullable CompoundTag compoundTag) {
        String string;
        if (compoundTag != null) {
            compoundTag.remove("UUID");
            compoundTag.remove("Pos");
            compoundTag.remove("Dimension");
            String string2 = compoundTag.getPrettyDisplay().getString();
            string = String.format(Locale.ROOT, "/summon %s %.2f %.2f %.2f %s", resourceLocation.toString(), vec3.x, vec3.y, vec3.z, string2);
        } else {
            string = String.format(Locale.ROOT, "/summon %s %.2f %.2f %.2f", resourceLocation.toString(), vec3.x, vec3.y, vec3.z);
        }
        this.setClipboard(string);
    }

    public void keyPress(long l, int n, int n2, int n3, int n4) {
        boolean bl;
        Object object;
        if (l != this.minecraft.getWindow().getWindow()) {
            return;
        }
        if (this.debugCrashKeyTime > 0L) {
            if (!InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 67) || !InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 292)) {
                this.debugCrashKeyTime = -1L;
            }
        } else if (InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 67) && InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 292)) {
            this.handledDebugKey = true;
            this.debugCrashKeyTime = Util.getMillis();
            this.debugCrashKeyReportedTime = Util.getMillis();
            this.debugCrashKeyReportedCount = 0L;
        }
        Screen screen = this.minecraft.screen;
        if (!(n3 != 1 || this.minecraft.screen instanceof ControlsScreen && ((ControlsScreen)screen).lastKeySelection > Util.getMillis() - 20L)) {
            if (this.minecraft.options.keyFullscreen.matches(n, n2)) {
                this.minecraft.getWindow().toggleFullScreen();
                this.minecraft.options.fullscreen = this.minecraft.getWindow().isFullscreen();
                this.minecraft.options.save();
                return;
            }
            if (this.minecraft.options.keyScreenshot.matches(n, n2)) {
                if (Screen.hasControlDown()) {
                    // empty if block
                }
                Screenshot.grab(this.minecraft.gameDirectory, this.minecraft.getWindow().getWidth(), this.minecraft.getWindow().getHeight(), this.minecraft.getMainRenderTarget(), component -> this.minecraft.execute(() -> this.minecraft.gui.getChat().addMessage((Component)component)));
                return;
            }
        }
        boolean bl2 = bl = screen == null || !(screen.getFocused() instanceof EditBox) || !((EditBox)screen.getFocused()).canConsumeInput();
        if (n3 != 0 && n == 66 && Screen.hasControlDown() && bl) {
            Option.NARRATOR.toggle(this.minecraft.options, 1);
            if (screen instanceof SimpleOptionsSubScreen) {
                ((SimpleOptionsSubScreen)screen).updateNarratorButton();
            }
        }
        if (screen != null) {
            object = new boolean[]{false};
            Screen.wrapScreenError(() -> this.lambda$keyPress$4(n3, (boolean[])object, screen, n, n2, n4), "keyPressed event handler", screen.getClass().getCanonicalName());
            if (object[0]) {
                return;
            }
        }
        if (this.minecraft.screen == null || this.minecraft.screen.passEvents) {
            object = InputConstants.getKey(n, n2);
            if (n3 == 0) {
                KeyMapping.set((InputConstants.Key)object, false);
                if (n == 292) {
                    if (this.handledDebugKey) {
                        this.handledDebugKey = false;
                    } else {
                        this.minecraft.options.renderDebug = !this.minecraft.options.renderDebug;
                        this.minecraft.options.renderDebugCharts = this.minecraft.options.renderDebug && Screen.hasShiftDown();
                        this.minecraft.options.renderFpsChart = this.minecraft.options.renderDebug && Screen.hasAltDown();
                    }
                }
            } else {
                if (n == 293 && this.minecraft.gameRenderer != null) {
                    this.minecraft.gameRenderer.togglePostEffect();
                }
                boolean bl3 = false;
                if (this.minecraft.screen == null) {
                    if (n == 256) {
                        boolean bl4 = InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 292);
                        this.minecraft.pauseGame(bl4);
                    }
                    bl3 = InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 292) && this.handleDebugKeys(n);
                    this.handledDebugKey |= bl3;
                    if (n == 290) {
                        boolean bl5 = this.minecraft.options.hideGui = !this.minecraft.options.hideGui;
                    }
                }
                if (bl3) {
                    KeyMapping.set((InputConstants.Key)object, false);
                } else {
                    KeyMapping.set((InputConstants.Key)object, true);
                    KeyMapping.click((InputConstants.Key)object);
                }
                if (this.minecraft.options.renderDebugCharts && n >= 48 && n <= 57) {
                    this.minecraft.debugFpsMeterKeyPress(n - 48);
                }
            }
        }
    }

    private void charTyped(long l, int n, int n2) {
        if (l != this.minecraft.getWindow().getWindow()) {
            return;
        }
        Screen screen = this.minecraft.screen;
        if (screen == null || this.minecraft.getOverlay() != null) {
            return;
        }
        if (Character.charCount(n) == 1) {
            Screen.wrapScreenError(() -> screen.charTyped((char)n, n2), "charTyped event handler", screen.getClass().getCanonicalName());
        } else {
            for (char c : Character.toChars(n)) {
                Screen.wrapScreenError(() -> screen.charTyped(c, n2), "charTyped event handler", screen.getClass().getCanonicalName());
            }
        }
    }

    public void setSendRepeatsToGui(boolean bl) {
        this.sendRepeatsToGui = bl;
    }

    public void setup(long l2) {
        InputConstants.setupKeyboardCallbacks(l2, (l, n, n2, n3, n4) -> this.minecraft.execute(() -> this.keyPress(l, n, n2, n3, n4)), (l, n, n2) -> this.minecraft.execute(() -> this.charTyped(l, n, n2)));
    }

    public String getClipboard() {
        return this.clipboardManager.getClipboard(this.minecraft.getWindow().getWindow(), (n, l) -> {
            if (n != 65545) {
                this.minecraft.getWindow().defaultErrorCallback(n, l);
            }
        });
    }

    public void setClipboard(String string) {
        this.clipboardManager.setClipboard(this.minecraft.getWindow().getWindow(), string);
    }

    public void tick() {
        if (this.debugCrashKeyTime > 0L) {
            long l = Util.getMillis();
            long l2 = 10000L - (l - this.debugCrashKeyTime);
            long l3 = l - this.debugCrashKeyReportedTime;
            if (l2 < 0L) {
                if (Screen.hasControlDown()) {
                    Blaze3D.youJustLostTheGame();
                }
                throw new ReportedException(new CrashReport("Manually triggered debug crash", new Throwable()));
            }
            if (l3 >= 1000L) {
                if (this.debugCrashKeyReportedCount == 0L) {
                    this.debugFeedbackTranslated("debug.crash.message", new Object[0]);
                } else {
                    this.debugWarningTranslated("debug.crash.warning", Mth.ceil((float)l2 / 1000.0f));
                }
                this.debugCrashKeyReportedTime = l;
                ++this.debugCrashKeyReportedCount;
            }
        }
    }

    private /* synthetic */ void lambda$keyPress$4(int n, boolean[] arrbl, ContainerEventHandler containerEventHandler, int n2, int n3, int n4) {
        if (n == 1 || n == 2 && this.sendRepeatsToGui) {
            arrbl[0] = containerEventHandler.keyPressed(n2, n3, n4);
        } else if (n == 0) {
            arrbl[0] = containerEventHandler.keyReleased(n2, n3, n4);
        }
    }

}

