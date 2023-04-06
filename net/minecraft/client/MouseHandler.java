/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  org.lwjgl.glfw.GLFWDropCallback
 */
package net.minecraft.client;

import com.mojang.blaze3d.Blaze3D;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Window;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.components.spectator.SpectatorGui;
import net.minecraft.client.gui.screens.Overlay;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.tutorial.Tutorial;
import net.minecraft.util.Mth;
import net.minecraft.util.SmoothDouble;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Inventory;
import org.lwjgl.glfw.GLFWDropCallback;

public class MouseHandler {
    private final Minecraft minecraft;
    private boolean isLeftPressed;
    private boolean isMiddlePressed;
    private boolean isRightPressed;
    private double xpos;
    private double ypos;
    private int fakeRightMouse;
    private int activeButton = -1;
    private boolean ignoreFirstMove = true;
    private int clickDepth;
    private double mousePressedTime;
    private final SmoothDouble smoothTurnX = new SmoothDouble();
    private final SmoothDouble smoothTurnY = new SmoothDouble();
    private double accumulatedDX;
    private double accumulatedDY;
    private double accumulatedScroll;
    private double lastMouseEventTime = Double.MIN_VALUE;
    private boolean mouseGrabbed;

    public MouseHandler(Minecraft minecraft) {
        this.minecraft = minecraft;
    }

    private void onPress(long l, int n, int n2, int n3) {
        boolean bl;
        if (l != this.minecraft.getWindow().getWindow()) {
            return;
        }
        boolean bl2 = bl = n2 == 1;
        if (Minecraft.ON_OSX && n == 0) {
            if (bl) {
                if ((n3 & 2) == 2) {
                    n = 1;
                    ++this.fakeRightMouse;
                }
            } else if (this.fakeRightMouse > 0) {
                n = 1;
                --this.fakeRightMouse;
            }
        }
        int n4 = n;
        if (bl) {
            if (this.minecraft.options.touchscreen && this.clickDepth++ > 0) {
                return;
            }
            this.activeButton = n4;
            this.mousePressedTime = Blaze3D.getTime();
        } else if (this.activeButton != -1) {
            if (this.minecraft.options.touchscreen && --this.clickDepth > 0) {
                return;
            }
            this.activeButton = -1;
        }
        boolean[] arrbl = new boolean[]{false};
        if (this.minecraft.overlay == null) {
            if (this.minecraft.screen == null) {
                if (!this.mouseGrabbed && bl) {
                    this.grabMouse();
                }
            } else {
                double d = this.xpos * (double)this.minecraft.getWindow().getGuiScaledWidth() / (double)this.minecraft.getWindow().getScreenWidth();
                double d2 = this.ypos * (double)this.minecraft.getWindow().getGuiScaledHeight() / (double)this.minecraft.getWindow().getScreenHeight();
                if (bl) {
                    Screen.wrapScreenError(() -> {
                        arrbl[0] = this.minecraft.screen.mouseClicked(d, d2, n4);
                    }, "mouseClicked event handler", this.minecraft.screen.getClass().getCanonicalName());
                } else {
                    Screen.wrapScreenError(() -> {
                        arrbl[0] = this.minecraft.screen.mouseReleased(d, d2, n4);
                    }, "mouseReleased event handler", this.minecraft.screen.getClass().getCanonicalName());
                }
            }
        }
        if (!arrbl[0] && (this.minecraft.screen == null || this.minecraft.screen.passEvents) && this.minecraft.overlay == null) {
            if (n4 == 0) {
                this.isLeftPressed = bl;
            } else if (n4 == 2) {
                this.isMiddlePressed = bl;
            } else if (n4 == 1) {
                this.isRightPressed = bl;
            }
            KeyMapping.set(InputConstants.Type.MOUSE.getOrCreate(n4), bl);
            if (bl) {
                if (this.minecraft.player.isSpectator() && n4 == 2) {
                    this.minecraft.gui.getSpectatorGui().onMouseMiddleClick();
                } else {
                    KeyMapping.click(InputConstants.Type.MOUSE.getOrCreate(n4));
                }
            }
        }
    }

    private void onScroll(long l, double d, double d2) {
        if (l == Minecraft.getInstance().getWindow().getWindow()) {
            double d3 = (this.minecraft.options.discreteMouseScroll ? Math.signum(d2) : d2) * this.minecraft.options.mouseWheelSensitivity;
            if (this.minecraft.overlay == null) {
                if (this.minecraft.screen != null) {
                    double d4 = this.xpos * (double)this.minecraft.getWindow().getGuiScaledWidth() / (double)this.minecraft.getWindow().getScreenWidth();
                    double d5 = this.ypos * (double)this.minecraft.getWindow().getGuiScaledHeight() / (double)this.minecraft.getWindow().getScreenHeight();
                    this.minecraft.screen.mouseScrolled(d4, d5, d3);
                } else if (this.minecraft.player != null) {
                    if (this.accumulatedScroll != 0.0 && Math.signum(d3) != Math.signum(this.accumulatedScroll)) {
                        this.accumulatedScroll = 0.0;
                    }
                    this.accumulatedScroll += d3;
                    float f = (int)this.accumulatedScroll;
                    if (f == 0.0f) {
                        return;
                    }
                    this.accumulatedScroll -= (double)f;
                    if (this.minecraft.player.isSpectator()) {
                        if (this.minecraft.gui.getSpectatorGui().isMenuActive()) {
                            this.minecraft.gui.getSpectatorGui().onMouseScrolled(-f);
                        } else {
                            float f2 = Mth.clamp(this.minecraft.player.abilities.getFlyingSpeed() + f * 0.005f, 0.0f, 0.2f);
                            this.minecraft.player.abilities.setFlyingSpeed(f2);
                        }
                    } else {
                        this.minecraft.player.inventory.swapPaint(f);
                    }
                }
            }
        }
    }

    private void onDrop(long l, List<Path> list) {
        if (this.minecraft.screen != null) {
            this.minecraft.screen.onFilesDrop(list);
        }
    }

    public void setup(long l3) {
        InputConstants.setupMouseCallbacks(l3, (l, d, d2) -> this.minecraft.execute(() -> this.onMove(l, d, d2)), (l, n, n2, n3) -> this.minecraft.execute(() -> this.onPress(l, n, n2, n3)), (l, d, d2) -> this.minecraft.execute(() -> this.onScroll(l, d, d2)), (l, n, l2) -> {
            Path[] arrpath = new Path[n];
            for (int i = 0; i < n; ++i) {
                arrpath[i] = Paths.get(GLFWDropCallback.getName((long)l2, (int)i), new String[0]);
            }
            this.minecraft.execute(() -> this.onDrop(l, Arrays.asList(arrpath)));
        });
    }

    private void onMove(long l, double d, double d2) {
        Screen screen;
        if (l != Minecraft.getInstance().getWindow().getWindow()) {
            return;
        }
        if (this.ignoreFirstMove) {
            this.xpos = d;
            this.ypos = d2;
            this.ignoreFirstMove = false;
        }
        if ((screen = this.minecraft.screen) != null && this.minecraft.overlay == null) {
            double d3 = d * (double)this.minecraft.getWindow().getGuiScaledWidth() / (double)this.minecraft.getWindow().getScreenWidth();
            double d4 = d2 * (double)this.minecraft.getWindow().getGuiScaledHeight() / (double)this.minecraft.getWindow().getScreenHeight();
            Screen.wrapScreenError(() -> screen.mouseMoved(d3, d4), "mouseMoved event handler", screen.getClass().getCanonicalName());
            if (this.activeButton != -1 && this.mousePressedTime > 0.0) {
                double d5 = (d - this.xpos) * (double)this.minecraft.getWindow().getGuiScaledWidth() / (double)this.minecraft.getWindow().getScreenWidth();
                double d6 = (d2 - this.ypos) * (double)this.minecraft.getWindow().getGuiScaledHeight() / (double)this.minecraft.getWindow().getScreenHeight();
                Screen.wrapScreenError(() -> screen.mouseDragged(d3, d4, this.activeButton, d5, d6), "mouseDragged event handler", screen.getClass().getCanonicalName());
            }
        }
        this.minecraft.getProfiler().push("mouse");
        if (this.isMouseGrabbed() && this.minecraft.isWindowActive()) {
            this.accumulatedDX += d - this.xpos;
            this.accumulatedDY += d2 - this.ypos;
        }
        this.turnPlayer();
        this.xpos = d;
        this.ypos = d2;
        this.minecraft.getProfiler().pop();
    }

    public void turnPlayer() {
        double d;
        double d2;
        double d3 = Blaze3D.getTime();
        double d4 = d3 - this.lastMouseEventTime;
        this.lastMouseEventTime = d3;
        if (!this.isMouseGrabbed() || !this.minecraft.isWindowActive()) {
            this.accumulatedDX = 0.0;
            this.accumulatedDY = 0.0;
            return;
        }
        double d5 = this.minecraft.options.sensitivity * 0.6000000238418579 + 0.20000000298023224;
        double d6 = d5 * d5 * d5 * 8.0;
        if (this.minecraft.options.smoothCamera) {
            double d7 = this.smoothTurnX.getNewDeltaValue(this.accumulatedDX * d6, d4 * d6);
            double d8 = this.smoothTurnY.getNewDeltaValue(this.accumulatedDY * d6, d4 * d6);
            d2 = d7;
            d = d8;
        } else {
            this.smoothTurnX.reset();
            this.smoothTurnY.reset();
            d2 = this.accumulatedDX * d6;
            d = this.accumulatedDY * d6;
        }
        this.accumulatedDX = 0.0;
        this.accumulatedDY = 0.0;
        int n = 1;
        if (this.minecraft.options.invertYMouse) {
            n = -1;
        }
        this.minecraft.getTutorial().onMouse(d2, d);
        if (this.minecraft.player != null) {
            this.minecraft.player.turn(d2, d * (double)n);
        }
    }

    public boolean isLeftPressed() {
        return this.isLeftPressed;
    }

    public boolean isRightPressed() {
        return this.isRightPressed;
    }

    public double xpos() {
        return this.xpos;
    }

    public double ypos() {
        return this.ypos;
    }

    public void setIgnoreFirstMove() {
        this.ignoreFirstMove = true;
    }

    public boolean isMouseGrabbed() {
        return this.mouseGrabbed;
    }

    public void grabMouse() {
        if (!this.minecraft.isWindowActive()) {
            return;
        }
        if (this.mouseGrabbed) {
            return;
        }
        if (!Minecraft.ON_OSX) {
            KeyMapping.setAll();
        }
        this.mouseGrabbed = true;
        this.xpos = this.minecraft.getWindow().getScreenWidth() / 2;
        this.ypos = this.minecraft.getWindow().getScreenHeight() / 2;
        InputConstants.grabOrReleaseMouse(this.minecraft.getWindow().getWindow(), 212995, this.xpos, this.ypos);
        this.minecraft.setScreen(null);
        this.minecraft.missTime = 10000;
        this.ignoreFirstMove = true;
    }

    public void releaseMouse() {
        if (!this.mouseGrabbed) {
            return;
        }
        this.mouseGrabbed = false;
        this.xpos = this.minecraft.getWindow().getScreenWidth() / 2;
        this.ypos = this.minecraft.getWindow().getScreenHeight() / 2;
        InputConstants.grabOrReleaseMouse(this.minecraft.getWindow().getWindow(), 212993, this.xpos, this.ypos);
    }

    public void cursorEntered() {
        this.ignoreFirstMove = true;
    }
}

