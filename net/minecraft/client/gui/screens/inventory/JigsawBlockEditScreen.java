/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.gui.screens.inventory;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundJigsawGeneratePacket;
import net.minecraft.network.protocol.game.ServerboundSetJigsawBlockPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.JigsawBlock;
import net.minecraft.world.level.block.entity.JigsawBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class JigsawBlockEditScreen
extends Screen {
    private static final Component JOINT_LABEL = new TranslatableComponent("jigsaw_block.joint_label");
    private static final Component POOL_LABEL = new TranslatableComponent("jigsaw_block.pool");
    private static final Component NAME_LABEL = new TranslatableComponent("jigsaw_block.name");
    private static final Component TARGET_LABEL = new TranslatableComponent("jigsaw_block.target");
    private static final Component FINAL_STATE_LABEL = new TranslatableComponent("jigsaw_block.final_state");
    private final JigsawBlockEntity jigsawEntity;
    private EditBox nameEdit;
    private EditBox targetEdit;
    private EditBox poolEdit;
    private EditBox finalStateEdit;
    private int levels;
    private boolean keepJigsaws = true;
    private Button jointButton;
    private Button doneButton;
    private JigsawBlockEntity.JointType joint;

    public JigsawBlockEditScreen(JigsawBlockEntity jigsawBlockEntity) {
        super(NarratorChatListener.NO_TITLE);
        this.jigsawEntity = jigsawBlockEntity;
    }

    @Override
    public void tick() {
        this.nameEdit.tick();
        this.targetEdit.tick();
        this.poolEdit.tick();
        this.finalStateEdit.tick();
    }

    private void onDone() {
        this.sendToServer();
        this.minecraft.setScreen(null);
    }

    private void onCancel() {
        this.minecraft.setScreen(null);
    }

    private void sendToServer() {
        this.minecraft.getConnection().send(new ServerboundSetJigsawBlockPacket(this.jigsawEntity.getBlockPos(), new ResourceLocation(this.nameEdit.getValue()), new ResourceLocation(this.targetEdit.getValue()), new ResourceLocation(this.poolEdit.getValue()), this.finalStateEdit.getValue(), this.joint));
    }

    private void sendGenerate() {
        this.minecraft.getConnection().send(new ServerboundJigsawGeneratePacket(this.jigsawEntity.getBlockPos(), this.levels, this.keepJigsaws));
    }

    @Override
    public void onClose() {
        this.onCancel();
    }

    @Override
    protected void init() {
        boolean bl;
        this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
        this.poolEdit = new EditBox(this.font, this.width / 2 - 152, 20, 300, 20, new TranslatableComponent("jigsaw_block.pool"));
        this.poolEdit.setMaxLength(128);
        this.poolEdit.setValue(this.jigsawEntity.getPool().toString());
        this.poolEdit.setResponder(string -> this.updateValidity());
        this.children.add(this.poolEdit);
        this.nameEdit = new EditBox(this.font, this.width / 2 - 152, 55, 300, 20, new TranslatableComponent("jigsaw_block.name"));
        this.nameEdit.setMaxLength(128);
        this.nameEdit.setValue(this.jigsawEntity.getName().toString());
        this.nameEdit.setResponder(string -> this.updateValidity());
        this.children.add(this.nameEdit);
        this.targetEdit = new EditBox(this.font, this.width / 2 - 152, 90, 300, 20, new TranslatableComponent("jigsaw_block.target"));
        this.targetEdit.setMaxLength(128);
        this.targetEdit.setValue(this.jigsawEntity.getTarget().toString());
        this.targetEdit.setResponder(string -> this.updateValidity());
        this.children.add(this.targetEdit);
        this.finalStateEdit = new EditBox(this.font, this.width / 2 - 152, 125, 300, 20, new TranslatableComponent("jigsaw_block.final_state"));
        this.finalStateEdit.setMaxLength(256);
        this.finalStateEdit.setValue(this.jigsawEntity.getFinalState());
        this.children.add(this.finalStateEdit);
        this.joint = this.jigsawEntity.getJoint();
        int n = this.font.width(JOINT_LABEL) + 10;
        this.jointButton = this.addButton(new Button(this.width / 2 - 152 + n, 150, 300 - n, 20, this.getJointText(), button -> {
            JigsawBlockEntity.JointType[] arrjointType = JigsawBlockEntity.JointType.values();
            int n = (this.joint.ordinal() + 1) % arrjointType.length;
            this.joint = arrjointType[n];
            button.setMessage(this.getJointText());
        }));
        this.jointButton.active = bl = JigsawBlock.getFrontFacing(this.jigsawEntity.getBlockState()).getAxis().isVertical();
        this.jointButton.visible = bl;
        this.addButton(new AbstractSliderButton(this.width / 2 - 154, 180, 100, 20, TextComponent.EMPTY, 0.0){
            {
                this.updateMessage();
            }

            @Override
            protected void updateMessage() {
                this.setMessage(new TranslatableComponent("jigsaw_block.levels", JigsawBlockEditScreen.this.levels));
            }

            @Override
            protected void applyValue() {
                JigsawBlockEditScreen.this.levels = Mth.floor(Mth.clampedLerp(0.0, 7.0, this.value));
            }
        });
        this.addButton(new Button(this.width / 2 - 50, 180, 100, 20, new TranslatableComponent("jigsaw_block.keep_jigsaws"), button -> {
            this.keepJigsaws = !this.keepJigsaws;
            button.queueNarration(250);
        }){

            @Override
            public Component getMessage() {
                return CommonComponents.optionStatus(super.getMessage(), JigsawBlockEditScreen.this.keepJigsaws);
            }
        });
        this.addButton(new Button(this.width / 2 + 54, 180, 100, 20, new TranslatableComponent("jigsaw_block.generate"), button -> {
            this.onDone();
            this.sendGenerate();
        }));
        this.doneButton = this.addButton(new Button(this.width / 2 - 4 - 150, 210, 150, 20, CommonComponents.GUI_DONE, button -> this.onDone()));
        this.addButton(new Button(this.width / 2 + 4, 210, 150, 20, CommonComponents.GUI_CANCEL, button -> this.onCancel()));
        this.setInitialFocus(this.poolEdit);
        this.updateValidity();
    }

    private void updateValidity() {
        this.doneButton.active = ResourceLocation.isValidResourceLocation(this.nameEdit.getValue()) && ResourceLocation.isValidResourceLocation(this.targetEdit.getValue()) && ResourceLocation.isValidResourceLocation(this.poolEdit.getValue());
    }

    @Override
    public void resize(Minecraft minecraft, int n, int n2) {
        String string = this.nameEdit.getValue();
        String string2 = this.targetEdit.getValue();
        String string3 = this.poolEdit.getValue();
        String string4 = this.finalStateEdit.getValue();
        int n3 = this.levels;
        JigsawBlockEntity.JointType jointType = this.joint;
        this.init(minecraft, n, n2);
        this.nameEdit.setValue(string);
        this.targetEdit.setValue(string2);
        this.poolEdit.setValue(string3);
        this.finalStateEdit.setValue(string4);
        this.levels = n3;
        this.joint = jointType;
        this.jointButton.setMessage(this.getJointText());
    }

    private Component getJointText() {
        return new TranslatableComponent("jigsaw_block.joint." + this.joint.getSerializedName());
    }

    @Override
    public void removed() {
        this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
    }

    @Override
    public boolean keyPressed(int n, int n2, int n3) {
        if (super.keyPressed(n, n2, n3)) {
            return true;
        }
        if (this.doneButton.active && (n == 257 || n == 335)) {
            this.onDone();
            return true;
        }
        return false;
    }

    @Override
    public void render(PoseStack poseStack, int n, int n2, float f) {
        this.renderBackground(poseStack);
        JigsawBlockEditScreen.drawString(poseStack, this.font, POOL_LABEL, this.width / 2 - 153, 10, 10526880);
        this.poolEdit.render(poseStack, n, n2, f);
        JigsawBlockEditScreen.drawString(poseStack, this.font, NAME_LABEL, this.width / 2 - 153, 45, 10526880);
        this.nameEdit.render(poseStack, n, n2, f);
        JigsawBlockEditScreen.drawString(poseStack, this.font, TARGET_LABEL, this.width / 2 - 153, 80, 10526880);
        this.targetEdit.render(poseStack, n, n2, f);
        JigsawBlockEditScreen.drawString(poseStack, this.font, FINAL_STATE_LABEL, this.width / 2 - 153, 115, 10526880);
        this.finalStateEdit.render(poseStack, n, n2, f);
        if (JigsawBlock.getFrontFacing(this.jigsawEntity.getBlockState()).getAxis().isVertical()) {
            JigsawBlockEditScreen.drawString(poseStack, this.font, JOINT_LABEL, this.width / 2 - 153, 156, 16777215);
        }
        super.render(poseStack, n, n2, f);
    }

}

