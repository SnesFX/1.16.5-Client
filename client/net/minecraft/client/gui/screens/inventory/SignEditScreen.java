/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.gui.screens.inventory;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Matrix4f;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.font.TextFieldHelper;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.SignRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundSignUpdatePacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class SignEditScreen
extends Screen {
    private final SignRenderer.SignModel signModel = new SignRenderer.SignModel();
    private final SignBlockEntity sign;
    private int frame;
    private int line;
    private TextFieldHelper signField;
    private final String[] messages = (String[])IntStream.range(0, 4).mapToObj(signBlockEntity::getMessage).map(Component::getString).toArray(n -> new String[n]);

    public SignEditScreen(SignBlockEntity signBlockEntity) {
        super(new TranslatableComponent("sign.edit"));
        this.sign = signBlockEntity;
    }

    @Override
    protected void init() {
        this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
        this.addButton(new Button(this.width / 2 - 100, this.height / 4 + 120, 200, 20, CommonComponents.GUI_DONE, button -> this.onDone()));
        this.sign.setEditable(false);
        this.signField = new TextFieldHelper(() -> this.messages[this.line], string -> {
            this.messages[this.line] = string;
            this.sign.setMessage(this.line, new TextComponent((String)string));
        }, TextFieldHelper.createClipboardGetter(this.minecraft), TextFieldHelper.createClipboardSetter(this.minecraft), string -> this.minecraft.font.width((String)string) <= 90);
    }

    @Override
    public void removed() {
        this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
        ClientPacketListener clientPacketListener = this.minecraft.getConnection();
        if (clientPacketListener != null) {
            clientPacketListener.send(new ServerboundSignUpdatePacket(this.sign.getBlockPos(), this.messages[0], this.messages[1], this.messages[2], this.messages[3]));
        }
        this.sign.setEditable(true);
    }

    @Override
    public void tick() {
        ++this.frame;
        if (!this.sign.getType().isValid(this.sign.getBlockState().getBlock())) {
            this.onDone();
        }
    }

    private void onDone() {
        this.sign.setChanged();
        this.minecraft.setScreen(null);
    }

    @Override
    public boolean charTyped(char c, int n) {
        this.signField.charTyped(c);
        return true;
    }

    @Override
    public void onClose() {
        this.onDone();
    }

    @Override
    public boolean keyPressed(int n, int n2, int n3) {
        if (n == 265) {
            this.line = this.line - 1 & 3;
            this.signField.setCursorToEnd();
            return true;
        }
        if (n == 264 || n == 257 || n == 335) {
            this.line = this.line + 1 & 3;
            this.signField.setCursorToEnd();
            return true;
        }
        if (this.signField.keyPressed(n)) {
            return true;
        }
        return super.keyPressed(n, n2, n3);
    }

    @Override
    public void render(PoseStack poseStack, int n, int n2, float f) {
        int n3;
        int n4;
        String string;
        int n5;
        Lighting.setupForFlatItems();
        this.renderBackground(poseStack);
        SignEditScreen.drawCenteredString(poseStack, this.font, this.title, this.width / 2, 40, 16777215);
        poseStack.pushPose();
        poseStack.translate(this.width / 2, 0.0, 50.0);
        float f2 = 93.75f;
        poseStack.scale(93.75f, -93.75f, 93.75f);
        poseStack.translate(0.0, -1.3125, 0.0);
        BlockState blockState = this.sign.getBlockState();
        boolean bl = blockState.getBlock() instanceof StandingSignBlock;
        if (!bl) {
            poseStack.translate(0.0, -0.3125, 0.0);
        }
        boolean bl2 = this.frame / 6 % 2 == 0;
        float f3 = 0.6666667f;
        poseStack.pushPose();
        poseStack.scale(0.6666667f, -0.6666667f, -0.6666667f);
        MultiBufferSource.BufferSource bufferSource = this.minecraft.renderBuffers().bufferSource();
        Material material = SignRenderer.getMaterial(blockState.getBlock());
        VertexConsumer vertexConsumer = material.buffer(bufferSource, this.signModel::renderType);
        this.signModel.sign.render(poseStack, vertexConsumer, 15728880, OverlayTexture.NO_OVERLAY);
        if (bl) {
            this.signModel.stick.render(poseStack, vertexConsumer, 15728880, OverlayTexture.NO_OVERLAY);
        }
        poseStack.popPose();
        float f4 = 0.010416667f;
        poseStack.translate(0.0, 0.3333333432674408, 0.046666666865348816);
        poseStack.scale(0.010416667f, -0.010416667f, 0.010416667f);
        int n6 = this.sign.getColor().getTextColor();
        int n7 = this.signField.getCursorPos();
        int n8 = this.signField.getSelectionPos();
        int n9 = this.line * 10 - this.messages.length * 5;
        Matrix4f matrix4f = poseStack.last().pose();
        for (n3 = 0; n3 < this.messages.length; ++n3) {
            string = this.messages[n3];
            if (string == null) continue;
            if (this.font.isBidirectional()) {
                string = this.font.bidirectionalShaping(string);
            }
            float f5 = -this.minecraft.font.width(string) / 2;
            this.minecraft.font.drawInBatch(string, f5, n3 * 10 - this.messages.length * 5, n6, false, matrix4f, bufferSource, false, 0, 15728880, false);
            if (n3 != this.line || n7 < 0 || !bl2) continue;
            n4 = this.minecraft.font.width(string.substring(0, Math.max(Math.min(n7, string.length()), 0)));
            n5 = n4 - this.minecraft.font.width(string) / 2;
            if (n7 < string.length()) continue;
            this.minecraft.font.drawInBatch("_", n5, n9, n6, false, matrix4f, bufferSource, false, 0, 15728880, false);
        }
        bufferSource.endBatch();
        for (n3 = 0; n3 < this.messages.length; ++n3) {
            string = this.messages[n3];
            if (string == null || n3 != this.line || n7 < 0) continue;
            int n10 = this.minecraft.font.width(string.substring(0, Math.max(Math.min(n7, string.length()), 0)));
            n4 = n10 - this.minecraft.font.width(string) / 2;
            if (bl2 && n7 < string.length()) {
                this.minecraft.font.getClass();
                SignEditScreen.fill(poseStack, n4, n9 - 1, n4 + 1, n9 + 9, 0xFF000000 | n6);
            }
            if (n8 == n7) continue;
            n5 = Math.min(n7, n8);
            int n11 = Math.max(n7, n8);
            int n12 = this.minecraft.font.width(string.substring(0, n5)) - this.minecraft.font.width(string) / 2;
            int n13 = this.minecraft.font.width(string.substring(0, n11)) - this.minecraft.font.width(string) / 2;
            int n14 = Math.min(n12, n13);
            int n15 = Math.max(n12, n13);
            Tesselator tesselator = Tesselator.getInstance();
            BufferBuilder bufferBuilder = tesselator.getBuilder();
            RenderSystem.disableTexture();
            RenderSystem.enableColorLogicOp();
            RenderSystem.logicOp(GlStateManager.LogicOp.OR_REVERSE);
            bufferBuilder.begin(7, DefaultVertexFormat.POSITION_COLOR);
            this.minecraft.font.getClass();
            bufferBuilder.vertex(matrix4f, n14, n9 + 9, 0.0f).color(0, 0, 255, 255).endVertex();
            this.minecraft.font.getClass();
            bufferBuilder.vertex(matrix4f, n15, n9 + 9, 0.0f).color(0, 0, 255, 255).endVertex();
            bufferBuilder.vertex(matrix4f, n15, n9, 0.0f).color(0, 0, 255, 255).endVertex();
            bufferBuilder.vertex(matrix4f, n14, n9, 0.0f).color(0, 0, 255, 255).endVertex();
            bufferBuilder.end();
            BufferUploader.end(bufferBuilder);
            RenderSystem.disableColorLogicOp();
            RenderSystem.enableTexture();
        }
        poseStack.popPose();
        Lighting.setupFor3DItems();
        super.render(poseStack, n, n2, f);
    }
}

