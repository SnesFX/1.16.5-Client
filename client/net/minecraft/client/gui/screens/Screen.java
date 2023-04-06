/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Sets
 *  it.unimi.dsi.fastutil.booleans.BooleanConsumer
 *  javax.annotation.Nullable
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.client.gui.screens;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Matrix4f;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.CrashReportDetail;
import net.minecraft.ReportedException;
import net.minecraft.Util;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.gui.components.TickableWidget;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.components.events.AbstractContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class Screen
extends AbstractContainerEventHandler
implements TickableWidget,
Widget {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Set<String> ALLOWED_PROTOCOLS = Sets.newHashSet((Object[])new String[]{"http", "https"});
    protected final Component title;
    protected final List<GuiEventListener> children = Lists.newArrayList();
    @Nullable
    protected Minecraft minecraft;
    protected ItemRenderer itemRenderer;
    public int width;
    public int height;
    protected final List<AbstractWidget> buttons = Lists.newArrayList();
    public boolean passEvents;
    protected Font font;
    private URI clickedLink;

    protected Screen(Component component) {
        this.title = component;
    }

    public Component getTitle() {
        return this.title;
    }

    public String getNarrationMessage() {
        return this.getTitle().getString();
    }

    @Override
    public void render(PoseStack poseStack, int n, int n2, float f) {
        for (int i = 0; i < this.buttons.size(); ++i) {
            this.buttons.get(i).render(poseStack, n, n2, f);
        }
    }

    @Override
    public boolean keyPressed(int n, int n2, int n3) {
        if (n == 256 && this.shouldCloseOnEsc()) {
            this.onClose();
            return true;
        }
        if (n == 258) {
            boolean bl;
            boolean bl2 = bl = !Screen.hasShiftDown();
            if (!this.changeFocus(bl)) {
                this.changeFocus(bl);
            }
            return false;
        }
        return super.keyPressed(n, n2, n3);
    }

    public boolean shouldCloseOnEsc() {
        return true;
    }

    public void onClose() {
        this.minecraft.setScreen(null);
    }

    protected <T extends AbstractWidget> T addButton(T t) {
        this.buttons.add(t);
        return this.addWidget(t);
    }

    protected <T extends GuiEventListener> T addWidget(T t) {
        this.children.add(t);
        return t;
    }

    protected void renderTooltip(PoseStack poseStack, ItemStack itemStack, int n, int n2) {
        this.renderComponentTooltip(poseStack, this.getTooltipFromItem(itemStack), n, n2);
    }

    public List<Component> getTooltipFromItem(ItemStack itemStack) {
        return itemStack.getTooltipLines(this.minecraft.player, this.minecraft.options.advancedItemTooltips ? TooltipFlag.Default.ADVANCED : TooltipFlag.Default.NORMAL);
    }

    public void renderTooltip(PoseStack poseStack, Component component, int n, int n2) {
        this.renderTooltip(poseStack, Arrays.asList(component.getVisualOrderText()), n, n2);
    }

    public void renderComponentTooltip(PoseStack poseStack, List<Component> list, int n, int n2) {
        this.renderTooltip(poseStack, Lists.transform(list, Component::getVisualOrderText), n, n2);
    }

    public void renderTooltip(PoseStack poseStack, List<? extends FormattedCharSequence> list, int n, int n2) {
        int n3;
        if (list.isEmpty()) {
            return;
        }
        int n4 = 0;
        for (FormattedCharSequence formattedCharSequence : list) {
            n3 = this.font.width(formattedCharSequence);
            if (n3 <= n4) continue;
            n4 = n3;
        }
        int n5 = n + 12;
        int n6 = n2 - 12;
        n3 = n4;
        int n7 = 8;
        if (list.size() > 1) {
            n7 += 2 + (list.size() - 1) * 10;
        }
        if (n5 + n4 > this.width) {
            n5 -= 28 + n4;
        }
        if (n6 + n7 + 6 > this.height) {
            n6 = this.height - n7 - 6;
        }
        poseStack.pushPose();
        int n8 = -267386864;
        int n9 = 1347420415;
        int n10 = 1344798847;
        int n11 = 400;
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferBuilder = tesselator.getBuilder();
        bufferBuilder.begin(7, DefaultVertexFormat.POSITION_COLOR);
        Matrix4f matrix4f = poseStack.last().pose();
        Screen.fillGradient(matrix4f, bufferBuilder, n5 - 3, n6 - 4, n5 + n3 + 3, n6 - 3, 400, -267386864, -267386864);
        Screen.fillGradient(matrix4f, bufferBuilder, n5 - 3, n6 + n7 + 3, n5 + n3 + 3, n6 + n7 + 4, 400, -267386864, -267386864);
        Screen.fillGradient(matrix4f, bufferBuilder, n5 - 3, n6 - 3, n5 + n3 + 3, n6 + n7 + 3, 400, -267386864, -267386864);
        Screen.fillGradient(matrix4f, bufferBuilder, n5 - 4, n6 - 3, n5 - 3, n6 + n7 + 3, 400, -267386864, -267386864);
        Screen.fillGradient(matrix4f, bufferBuilder, n5 + n3 + 3, n6 - 3, n5 + n3 + 4, n6 + n7 + 3, 400, -267386864, -267386864);
        Screen.fillGradient(matrix4f, bufferBuilder, n5 - 3, n6 - 3 + 1, n5 - 3 + 1, n6 + n7 + 3 - 1, 400, 1347420415, 1344798847);
        Screen.fillGradient(matrix4f, bufferBuilder, n5 + n3 + 2, n6 - 3 + 1, n5 + n3 + 3, n6 + n7 + 3 - 1, 400, 1347420415, 1344798847);
        Screen.fillGradient(matrix4f, bufferBuilder, n5 - 3, n6 - 3, n5 + n3 + 3, n6 - 3 + 1, 400, 1347420415, 1347420415);
        Screen.fillGradient(matrix4f, bufferBuilder, n5 - 3, n6 + n7 + 2, n5 + n3 + 3, n6 + n7 + 3, 400, 1344798847, 1344798847);
        RenderSystem.enableDepthTest();
        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.shadeModel(7425);
        bufferBuilder.end();
        BufferUploader.end(bufferBuilder);
        RenderSystem.shadeModel(7424);
        RenderSystem.disableBlend();
        RenderSystem.enableTexture();
        MultiBufferSource.BufferSource bufferSource = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
        poseStack.translate(0.0, 0.0, 400.0);
        for (int i = 0; i < list.size(); ++i) {
            FormattedCharSequence formattedCharSequence = list.get(i);
            if (formattedCharSequence != null) {
                this.font.drawInBatch(formattedCharSequence, (float)n5, (float)n6, -1, true, matrix4f, (MultiBufferSource)bufferSource, false, 0, 15728880);
            }
            if (i == 0) {
                n6 += 2;
            }
            n6 += 10;
        }
        bufferSource.endBatch();
        poseStack.popPose();
    }

    protected void renderComponentHoverEffect(PoseStack poseStack, @Nullable Style style, int n, int n2) {
        if (style == null || style.getHoverEvent() == null) {
            return;
        }
        HoverEvent hoverEvent = style.getHoverEvent();
        HoverEvent.ItemStackInfo itemStackInfo = hoverEvent.getValue(HoverEvent.Action.SHOW_ITEM);
        if (itemStackInfo != null) {
            this.renderTooltip(poseStack, itemStackInfo.getItemStack(), n, n2);
        } else {
            HoverEvent.EntityTooltipInfo entityTooltipInfo = hoverEvent.getValue(HoverEvent.Action.SHOW_ENTITY);
            if (entityTooltipInfo != null) {
                if (this.minecraft.options.advancedItemTooltips) {
                    this.renderComponentTooltip(poseStack, entityTooltipInfo.getTooltipLines(), n, n2);
                }
            } else {
                Component component = hoverEvent.getValue(HoverEvent.Action.SHOW_TEXT);
                if (component != null) {
                    this.renderTooltip(poseStack, this.minecraft.font.split(component, Math.max(this.width / 2, 200)), n, n2);
                }
            }
        }
    }

    protected void insertText(String string, boolean bl) {
    }

    public boolean handleComponentClicked(@Nullable Style style) {
        if (style == null) {
            return false;
        }
        ClickEvent clickEvent = style.getClickEvent();
        if (Screen.hasShiftDown()) {
            if (style.getInsertion() != null) {
                this.insertText(style.getInsertion(), false);
            }
        } else if (clickEvent != null) {
            block21 : {
                if (clickEvent.getAction() == ClickEvent.Action.OPEN_URL) {
                    if (!this.minecraft.options.chatLinks) {
                        return false;
                    }
                    try {
                        URI uRI = new URI(clickEvent.getValue());
                        String string = uRI.getScheme();
                        if (string == null) {
                            throw new URISyntaxException(clickEvent.getValue(), "Missing protocol");
                        }
                        if (!ALLOWED_PROTOCOLS.contains(string.toLowerCase(Locale.ROOT))) {
                            throw new URISyntaxException(clickEvent.getValue(), "Unsupported protocol: " + string.toLowerCase(Locale.ROOT));
                        }
                        if (this.minecraft.options.chatLinksPrompt) {
                            this.clickedLink = uRI;
                            this.minecraft.setScreen(new ConfirmLinkScreen(this::confirmLink, clickEvent.getValue(), false));
                            break block21;
                        }
                        this.openLink(uRI);
                    }
                    catch (URISyntaxException uRISyntaxException) {
                        LOGGER.error("Can't open url for {}", (Object)clickEvent, (Object)uRISyntaxException);
                    }
                } else if (clickEvent.getAction() == ClickEvent.Action.OPEN_FILE) {
                    URI uRI = new File(clickEvent.getValue()).toURI();
                    this.openLink(uRI);
                } else if (clickEvent.getAction() == ClickEvent.Action.SUGGEST_COMMAND) {
                    this.insertText(clickEvent.getValue(), true);
                } else if (clickEvent.getAction() == ClickEvent.Action.RUN_COMMAND) {
                    this.sendMessage(clickEvent.getValue(), false);
                } else if (clickEvent.getAction() == ClickEvent.Action.COPY_TO_CLIPBOARD) {
                    this.minecraft.keyboardHandler.setClipboard(clickEvent.getValue());
                } else {
                    LOGGER.error("Don't know how to handle {}", (Object)clickEvent);
                }
            }
            return true;
        }
        return false;
    }

    public void sendMessage(String string) {
        this.sendMessage(string, true);
    }

    public void sendMessage(String string, boolean bl) {
        if (bl) {
            this.minecraft.gui.getChat().addRecentChat(string);
        }
        this.minecraft.player.chat(string);
    }

    public void init(Minecraft minecraft, int n, int n2) {
        this.minecraft = minecraft;
        this.itemRenderer = minecraft.getItemRenderer();
        this.font = minecraft.font;
        this.width = n;
        this.height = n2;
        this.buttons.clear();
        this.children.clear();
        this.setFocused(null);
        this.init();
    }

    @Override
    public List<? extends GuiEventListener> children() {
        return this.children;
    }

    protected void init() {
    }

    @Override
    public void tick() {
    }

    public void removed() {
    }

    public void renderBackground(PoseStack poseStack) {
        this.renderBackground(poseStack, 0);
    }

    public void renderBackground(PoseStack poseStack, int n) {
        if (this.minecraft.level != null) {
            this.fillGradient(poseStack, 0, 0, this.width, this.height, -1072689136, -804253680);
        } else {
            this.renderDirtBackground(n);
        }
    }

    public void renderDirtBackground(int n) {
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferBuilder = tesselator.getBuilder();
        this.minecraft.getTextureManager().bind(BACKGROUND_LOCATION);
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        float f = 32.0f;
        bufferBuilder.begin(7, DefaultVertexFormat.POSITION_TEX_COLOR);
        bufferBuilder.vertex(0.0, this.height, 0.0).uv(0.0f, (float)this.height / 32.0f + (float)n).color(64, 64, 64, 255).endVertex();
        bufferBuilder.vertex(this.width, this.height, 0.0).uv((float)this.width / 32.0f, (float)this.height / 32.0f + (float)n).color(64, 64, 64, 255).endVertex();
        bufferBuilder.vertex(this.width, 0.0, 0.0).uv((float)this.width / 32.0f, n).color(64, 64, 64, 255).endVertex();
        bufferBuilder.vertex(0.0, 0.0, 0.0).uv(0.0f, n).color(64, 64, 64, 255).endVertex();
        tesselator.end();
    }

    public boolean isPauseScreen() {
        return true;
    }

    private void confirmLink(boolean bl) {
        if (bl) {
            this.openLink(this.clickedLink);
        }
        this.clickedLink = null;
        this.minecraft.setScreen(this);
    }

    private void openLink(URI uRI) {
        Util.getPlatform().openUri(uRI);
    }

    public static boolean hasControlDown() {
        if (Minecraft.ON_OSX) {
            return InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 343) || InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 347);
        }
        return InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 341) || InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 345);
    }

    public static boolean hasShiftDown() {
        return InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 340) || InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 344);
    }

    public static boolean hasAltDown() {
        return InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 342) || InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 346);
    }

    public static boolean isCut(int n) {
        return n == 88 && Screen.hasControlDown() && !Screen.hasShiftDown() && !Screen.hasAltDown();
    }

    public static boolean isPaste(int n) {
        return n == 86 && Screen.hasControlDown() && !Screen.hasShiftDown() && !Screen.hasAltDown();
    }

    public static boolean isCopy(int n) {
        return n == 67 && Screen.hasControlDown() && !Screen.hasShiftDown() && !Screen.hasAltDown();
    }

    public static boolean isSelectAll(int n) {
        return n == 65 && Screen.hasControlDown() && !Screen.hasShiftDown() && !Screen.hasAltDown();
    }

    public void resize(Minecraft minecraft, int n, int n2) {
        this.init(minecraft, n, n2);
    }

    public static void wrapScreenError(Runnable runnable, String string, String string2) {
        try {
            runnable.run();
        }
        catch (Throwable throwable) {
            CrashReport crashReport = CrashReport.forThrowable(throwable, string);
            CrashReportCategory crashReportCategory = crashReport.addCategory("Affected screen");
            crashReportCategory.setDetail("Screen name", () -> string2);
            throw new ReportedException(crashReport);
        }
    }

    protected boolean isValidCharacterForName(String string, char c, int n) {
        int n2 = string.indexOf(58);
        int n3 = string.indexOf(47);
        if (c == ':') {
            return (n3 == -1 || n <= n3) && n2 == -1;
        }
        if (c == '/') {
            return n > n2;
        }
        return c == '_' || c == '-' || c >= 'a' && c <= 'z' || c >= '0' && c <= '9' || c == '.';
    }

    @Override
    public boolean isMouseOver(double d, double d2) {
        return true;
    }

    public void onFilesDrop(List<Path> list) {
    }
}

