/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.gui.screens.inventory;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.inventory.ItemCombinerScreen;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundRenameItemPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class AnvilScreen
extends ItemCombinerScreen<AnvilMenu> {
    private static final ResourceLocation ANVIL_LOCATION = new ResourceLocation("textures/gui/container/anvil.png");
    private static final Component TOO_EXPENSIVE_TEXT = new TranslatableComponent("container.repair.expensive");
    private EditBox name;

    public AnvilScreen(AnvilMenu anvilMenu, Inventory inventory, Component component) {
        super(anvilMenu, inventory, component, ANVIL_LOCATION);
        this.titleLabelX = 60;
    }

    @Override
    public void tick() {
        super.tick();
        this.name.tick();
    }

    @Override
    protected void subInit() {
        this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
        int n = (this.width - this.imageWidth) / 2;
        int n2 = (this.height - this.imageHeight) / 2;
        this.name = new EditBox(this.font, n + 62, n2 + 24, 103, 12, new TranslatableComponent("container.repair"));
        this.name.setCanLoseFocus(false);
        this.name.setTextColor(-1);
        this.name.setTextColorUneditable(-1);
        this.name.setBordered(false);
        this.name.setMaxLength(35);
        this.name.setResponder(this::onNameChanged);
        this.children.add(this.name);
        this.setInitialFocus(this.name);
    }

    @Override
    public void resize(Minecraft minecraft, int n, int n2) {
        String string = this.name.getValue();
        this.init(minecraft, n, n2);
        this.name.setValue(string);
    }

    @Override
    public void removed() {
        super.removed();
        this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
    }

    @Override
    public boolean keyPressed(int n, int n2, int n3) {
        if (n == 256) {
            this.minecraft.player.closeContainer();
        }
        if (this.name.keyPressed(n, n2, n3) || this.name.canConsumeInput()) {
            return true;
        }
        return super.keyPressed(n, n2, n3);
    }

    private void onNameChanged(String string) {
        if (string.isEmpty()) {
            return;
        }
        String string2 = string;
        Slot slot = ((AnvilMenu)this.menu).getSlot(0);
        if (slot != null && slot.hasItem() && !slot.getItem().hasCustomHoverName() && string2.equals(slot.getItem().getHoverName().getString())) {
            string2 = "";
        }
        ((AnvilMenu)this.menu).setItemName(string2);
        this.minecraft.player.connection.send(new ServerboundRenameItemPacket(string2));
    }

    @Override
    protected void renderLabels(PoseStack poseStack, int n, int n2) {
        RenderSystem.disableBlend();
        super.renderLabels(poseStack, n, n2);
        int n3 = ((AnvilMenu)this.menu).getCost();
        if (n3 > 0) {
            Component component;
            int n4 = 8453920;
            if (n3 >= 40 && !this.minecraft.player.abilities.instabuild) {
                component = TOO_EXPENSIVE_TEXT;
                n4 = 16736352;
            } else if (!((AnvilMenu)this.menu).getSlot(2).hasItem()) {
                component = null;
            } else {
                component = new TranslatableComponent("container.repair.cost", n3);
                if (!((AnvilMenu)this.menu).getSlot(2).mayPickup(this.inventory.player)) {
                    n4 = 16736352;
                }
            }
            if (component != null) {
                int n5 = this.imageWidth - 8 - this.font.width(component) - 2;
                int n6 = 69;
                AnvilScreen.fill(poseStack, n5 - 2, 67, this.imageWidth - 8, 79, 1325400064);
                this.font.drawShadow(poseStack, component, (float)n5, 69.0f, n4);
            }
        }
    }

    @Override
    public void renderFg(PoseStack poseStack, int n, int n2, float f) {
        this.name.render(poseStack, n, n2, f);
    }

    @Override
    public void slotChanged(AbstractContainerMenu abstractContainerMenu, int n, ItemStack itemStack) {
        if (n == 0) {
            this.name.setValue(itemStack.isEmpty() ? "" : itemStack.getHoverName().getString());
            this.name.setEditable(!itemStack.isEmpty());
            this.setFocused(this.name);
        }
    }
}

