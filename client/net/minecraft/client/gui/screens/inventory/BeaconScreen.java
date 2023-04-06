/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.gui.screens.inventory;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.MobEffectTextureManager;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundContainerClosePacket;
import net.minecraft.network.protocol.game.ServerboundSetBeaconPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.BeaconMenu;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;

public class BeaconScreen
extends AbstractContainerScreen<BeaconMenu> {
    private static final ResourceLocation BEACON_LOCATION = new ResourceLocation("textures/gui/container/beacon.png");
    private static final Component PRIMARY_EFFECT_LABEL = new TranslatableComponent("block.minecraft.beacon.primary");
    private static final Component SECONDARY_EFFECT_LABEL = new TranslatableComponent("block.minecraft.beacon.secondary");
    private BeaconConfirmButton confirmButton;
    private boolean initPowerButtons;
    private MobEffect primary;
    private MobEffect secondary;

    public BeaconScreen(final BeaconMenu beaconMenu, Inventory inventory, Component component) {
        super(beaconMenu, inventory, component);
        this.imageWidth = 230;
        this.imageHeight = 219;
        beaconMenu.addSlotListener(new ContainerListener(){

            @Override
            public void refreshContainer(AbstractContainerMenu abstractContainerMenu, NonNullList<ItemStack> nonNullList) {
            }

            @Override
            public void slotChanged(AbstractContainerMenu abstractContainerMenu, int n, ItemStack itemStack) {
            }

            @Override
            public void setContainerData(AbstractContainerMenu abstractContainerMenu, int n, int n2) {
                BeaconScreen.this.primary = beaconMenu.getPrimaryEffect();
                BeaconScreen.this.secondary = beaconMenu.getSecondaryEffect();
                BeaconScreen.this.initPowerButtons = true;
            }
        });
    }

    @Override
    protected void init() {
        super.init();
        this.confirmButton = this.addButton(new BeaconConfirmButton(this.leftPos + 164, this.topPos + 107));
        this.addButton(new BeaconCancelButton(this.leftPos + 190, this.topPos + 107));
        this.initPowerButtons = true;
        this.confirmButton.active = false;
    }

    @Override
    public void tick() {
        super.tick();
        int n = ((BeaconMenu)this.menu).getLevels();
        if (this.initPowerButtons && n >= 0) {
            int n2;
            int n3;
            int n4;
            int n5;
            MobEffect mobEffect;
            BeaconPowerButton beaconPowerButton;
            this.initPowerButtons = false;
            for (n2 = 0; n2 <= 2; ++n2) {
                n4 = BeaconBlockEntity.BEACON_EFFECTS[n2].length;
                n5 = n4 * 22 + (n4 - 1) * 2;
                for (n3 = 0; n3 < n4; ++n3) {
                    mobEffect = BeaconBlockEntity.BEACON_EFFECTS[n2][n3];
                    beaconPowerButton = new BeaconPowerButton(this.leftPos + 76 + n3 * 24 - n5 / 2, this.topPos + 22 + n2 * 25, mobEffect, true);
                    this.addButton(beaconPowerButton);
                    if (n2 >= n) {
                        beaconPowerButton.active = false;
                        continue;
                    }
                    if (mobEffect != this.primary) continue;
                    beaconPowerButton.setSelected(true);
                }
            }
            n2 = 3;
            n4 = BeaconBlockEntity.BEACON_EFFECTS[3].length + 1;
            n5 = n4 * 22 + (n4 - 1) * 2;
            for (n3 = 0; n3 < n4 - 1; ++n3) {
                mobEffect = BeaconBlockEntity.BEACON_EFFECTS[3][n3];
                beaconPowerButton = new BeaconPowerButton(this.leftPos + 167 + n3 * 24 - n5 / 2, this.topPos + 47, mobEffect, false);
                this.addButton(beaconPowerButton);
                if (3 >= n) {
                    beaconPowerButton.active = false;
                    continue;
                }
                if (mobEffect != this.secondary) continue;
                beaconPowerButton.setSelected(true);
            }
            if (this.primary != null) {
                BeaconPowerButton beaconPowerButton2 = new BeaconPowerButton(this.leftPos + 167 + (n4 - 1) * 24 - n5 / 2, this.topPos + 47, this.primary, false);
                this.addButton(beaconPowerButton2);
                if (3 >= n) {
                    beaconPowerButton2.active = false;
                } else if (this.primary == this.secondary) {
                    beaconPowerButton2.setSelected(true);
                }
            }
        }
        this.confirmButton.active = ((BeaconMenu)this.menu).hasPayment() && this.primary != null;
    }

    @Override
    protected void renderLabels(PoseStack poseStack, int n, int n2) {
        BeaconScreen.drawCenteredString(poseStack, this.font, PRIMARY_EFFECT_LABEL, 62, 10, 14737632);
        BeaconScreen.drawCenteredString(poseStack, this.font, SECONDARY_EFFECT_LABEL, 169, 10, 14737632);
        for (AbstractWidget abstractWidget : this.buttons) {
            if (!abstractWidget.isHovered()) continue;
            abstractWidget.renderToolTip(poseStack, n - this.leftPos, n2 - this.topPos);
            break;
        }
    }

    @Override
    protected void renderBg(PoseStack poseStack, float f, int n, int n2) {
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.minecraft.getTextureManager().bind(BEACON_LOCATION);
        int n3 = (this.width - this.imageWidth) / 2;
        int n4 = (this.height - this.imageHeight) / 2;
        this.blit(poseStack, n3, n4, 0, 0, this.imageWidth, this.imageHeight);
        this.itemRenderer.blitOffset = 100.0f;
        this.itemRenderer.renderAndDecorateItem(new ItemStack(Items.NETHERITE_INGOT), n3 + 20, n4 + 109);
        this.itemRenderer.renderAndDecorateItem(new ItemStack(Items.EMERALD), n3 + 41, n4 + 109);
        this.itemRenderer.renderAndDecorateItem(new ItemStack(Items.DIAMOND), n3 + 41 + 22, n4 + 109);
        this.itemRenderer.renderAndDecorateItem(new ItemStack(Items.GOLD_INGOT), n3 + 42 + 44, n4 + 109);
        this.itemRenderer.renderAndDecorateItem(new ItemStack(Items.IRON_INGOT), n3 + 42 + 66, n4 + 109);
        this.itemRenderer.blitOffset = 0.0f;
    }

    @Override
    public void render(PoseStack poseStack, int n, int n2, float f) {
        this.renderBackground(poseStack);
        super.render(poseStack, n, n2, f);
        this.renderTooltip(poseStack, n, n2);
    }

    static /* synthetic */ Minecraft access$700(BeaconScreen beaconScreen) {
        return beaconScreen.minecraft;
    }

    static /* synthetic */ Minecraft access$800(BeaconScreen beaconScreen) {
        return beaconScreen.minecraft;
    }

    static /* synthetic */ Minecraft access$1000(BeaconScreen beaconScreen) {
        return beaconScreen.minecraft;
    }

    static /* synthetic */ Minecraft access$1100(BeaconScreen beaconScreen) {
        return beaconScreen.minecraft;
    }

    class BeaconCancelButton
    extends BeaconSpriteScreenButton {
        public BeaconCancelButton(int n, int n2) {
            super(n, n2, 112, 220);
        }

        @Override
        public void onPress() {
            BeaconScreen.access$1100((BeaconScreen)BeaconScreen.this).player.connection.send(new ServerboundContainerClosePacket(BeaconScreen.access$1000((BeaconScreen)BeaconScreen.this).player.containerMenu.containerId));
            BeaconScreen.this.minecraft.setScreen(null);
        }

        @Override
        public void renderToolTip(PoseStack poseStack, int n, int n2) {
            BeaconScreen.this.renderTooltip(poseStack, CommonComponents.GUI_CANCEL, n, n2);
        }
    }

    class BeaconConfirmButton
    extends BeaconSpriteScreenButton {
        public BeaconConfirmButton(int n, int n2) {
            super(n, n2, 90, 220);
        }

        @Override
        public void onPress() {
            BeaconScreen.this.minecraft.getConnection().send(new ServerboundSetBeaconPacket(MobEffect.getId(BeaconScreen.this.primary), MobEffect.getId(BeaconScreen.this.secondary)));
            BeaconScreen.access$800((BeaconScreen)BeaconScreen.this).player.connection.send(new ServerboundContainerClosePacket(BeaconScreen.access$700((BeaconScreen)BeaconScreen.this).player.containerMenu.containerId));
            BeaconScreen.this.minecraft.setScreen(null);
        }

        @Override
        public void renderToolTip(PoseStack poseStack, int n, int n2) {
            BeaconScreen.this.renderTooltip(poseStack, CommonComponents.GUI_DONE, n, n2);
        }
    }

    static abstract class BeaconSpriteScreenButton
    extends BeaconScreenButton {
        private final int iconX;
        private final int iconY;

        protected BeaconSpriteScreenButton(int n, int n2, int n3, int n4) {
            super(n, n2);
            this.iconX = n3;
            this.iconY = n4;
        }

        @Override
        protected void renderIcon(PoseStack poseStack) {
            this.blit(poseStack, this.x + 2, this.y + 2, this.iconX, this.iconY, 18, 18);
        }
    }

    class BeaconPowerButton
    extends BeaconScreenButton {
        private final MobEffect effect;
        private final TextureAtlasSprite sprite;
        private final boolean isPrimary;
        private final Component tooltip;

        public BeaconPowerButton(int n, int n2, MobEffect mobEffect, boolean bl) {
            super(n, n2);
            this.effect = mobEffect;
            this.sprite = Minecraft.getInstance().getMobEffectTextures().get(mobEffect);
            this.isPrimary = bl;
            this.tooltip = this.createTooltip(mobEffect, bl);
        }

        private Component createTooltip(MobEffect mobEffect, boolean bl) {
            TranslatableComponent translatableComponent = new TranslatableComponent(mobEffect.getDescriptionId());
            if (!bl && mobEffect != MobEffects.REGENERATION) {
                translatableComponent.append(" II");
            }
            return translatableComponent;
        }

        @Override
        public void onPress() {
            if (this.isSelected()) {
                return;
            }
            if (this.isPrimary) {
                BeaconScreen.this.primary = this.effect;
            } else {
                BeaconScreen.this.secondary = this.effect;
            }
            BeaconScreen.this.buttons.clear();
            BeaconScreen.this.children.clear();
            BeaconScreen.this.init();
            BeaconScreen.this.tick();
        }

        @Override
        public void renderToolTip(PoseStack poseStack, int n, int n2) {
            BeaconScreen.this.renderTooltip(poseStack, this.tooltip, n, n2);
        }

        @Override
        protected void renderIcon(PoseStack poseStack) {
            Minecraft.getInstance().getTextureManager().bind(this.sprite.atlas().location());
            BeaconPowerButton.blit(poseStack, this.x + 2, this.y + 2, this.getBlitOffset(), 18, 18, this.sprite);
        }
    }

    static abstract class BeaconScreenButton
    extends AbstractButton {
        private boolean selected;

        protected BeaconScreenButton(int n, int n2) {
            super(n, n2, 22, 22, TextComponent.EMPTY);
        }

        @Override
        public void renderButton(PoseStack poseStack, int n, int n2, float f) {
            Minecraft.getInstance().getTextureManager().bind(BEACON_LOCATION);
            RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            int n3 = 219;
            int n4 = 0;
            if (!this.active) {
                n4 += this.width * 2;
            } else if (this.selected) {
                n4 += this.width * 1;
            } else if (this.isHovered()) {
                n4 += this.width * 3;
            }
            this.blit(poseStack, this.x, this.y, n4, 219, this.width, this.height);
            this.renderIcon(poseStack);
        }

        protected abstract void renderIcon(PoseStack var1);

        public boolean isSelected() {
            return this.selected;
        }

        public void setSelected(boolean bl) {
            this.selected = bl;
        }
    }

}

