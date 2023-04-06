/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.client.gui.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.List;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.PresetFlatWorldScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.flat.FlatLayerInfo;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorSettings;

public class CreateFlatWorldScreen
extends Screen {
    protected final CreateWorldScreen parent;
    private final Consumer<FlatLevelGeneratorSettings> applySettings;
    private FlatLevelGeneratorSettings generator;
    private Component columnType;
    private Component columnHeight;
    private DetailsList list;
    private Button deleteLayerButton;

    public CreateFlatWorldScreen(CreateWorldScreen createWorldScreen, Consumer<FlatLevelGeneratorSettings> consumer, FlatLevelGeneratorSettings flatLevelGeneratorSettings) {
        super(new TranslatableComponent("createWorld.customize.flat.title"));
        this.parent = createWorldScreen;
        this.applySettings = consumer;
        this.generator = flatLevelGeneratorSettings;
    }

    public FlatLevelGeneratorSettings settings() {
        return this.generator;
    }

    public void setConfig(FlatLevelGeneratorSettings flatLevelGeneratorSettings) {
        this.generator = flatLevelGeneratorSettings;
    }

    @Override
    protected void init() {
        this.columnType = new TranslatableComponent("createWorld.customize.flat.tile");
        this.columnHeight = new TranslatableComponent("createWorld.customize.flat.height");
        this.list = new DetailsList();
        this.children.add(this.list);
        this.deleteLayerButton = this.addButton(new Button(this.width / 2 - 155, this.height - 52, 150, 20, new TranslatableComponent("createWorld.customize.flat.removeLayer"), button -> {
            if (!this.hasValidSelection()) {
                return;
            }
            List<FlatLayerInfo> list = this.generator.getLayersInfo();
            int n = this.list.children().indexOf(this.list.getSelected());
            int n2 = list.size() - n - 1;
            list.remove(n2);
            this.list.setSelected(list.isEmpty() ? null : (DetailsList.Entry)this.list.children().get(Math.min(n, list.size() - 1)));
            this.generator.updateLayers();
            this.list.resetRows();
            this.updateButtonValidity();
        }));
        this.addButton(new Button(this.width / 2 + 5, this.height - 52, 150, 20, new TranslatableComponent("createWorld.customize.presets"), button -> {
            this.minecraft.setScreen(new PresetFlatWorldScreen(this));
            this.generator.updateLayers();
            this.updateButtonValidity();
        }));
        this.addButton(new Button(this.width / 2 - 155, this.height - 28, 150, 20, CommonComponents.GUI_DONE, button -> {
            this.applySettings.accept(this.generator);
            this.minecraft.setScreen(this.parent);
            this.generator.updateLayers();
        }));
        this.addButton(new Button(this.width / 2 + 5, this.height - 28, 150, 20, CommonComponents.GUI_CANCEL, button -> {
            this.minecraft.setScreen(this.parent);
            this.generator.updateLayers();
        }));
        this.generator.updateLayers();
        this.updateButtonValidity();
    }

    private void updateButtonValidity() {
        this.deleteLayerButton.active = this.hasValidSelection();
    }

    private boolean hasValidSelection() {
        return this.list.getSelected() != null;
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(this.parent);
    }

    @Override
    public void render(PoseStack poseStack, int n, int n2, float f) {
        this.renderBackground(poseStack);
        this.list.render(poseStack, n, n2, f);
        CreateFlatWorldScreen.drawCenteredString(poseStack, this.font, this.title, this.width / 2, 8, 16777215);
        int n3 = this.width / 2 - 92 - 16;
        CreateFlatWorldScreen.drawString(poseStack, this.font, this.columnType, n3, 32, 16777215);
        CreateFlatWorldScreen.drawString(poseStack, this.font, this.columnHeight, n3 + 2 + 213 - this.font.width(this.columnHeight), 32, 16777215);
        super.render(poseStack, n, n2, f);
    }

    class DetailsList
    extends ObjectSelectionList<Entry> {
        public DetailsList() {
            super(CreateFlatWorldScreen.this.minecraft, CreateFlatWorldScreen.this.width, CreateFlatWorldScreen.this.height, 43, CreateFlatWorldScreen.this.height - 60, 24);
            for (int i = 0; i < CreateFlatWorldScreen.this.generator.getLayersInfo().size(); ++i) {
                this.addEntry(new Entry());
            }
        }

        @Override
        public void setSelected(@Nullable Entry entry) {
            FlatLayerInfo flatLayerInfo;
            Item item;
            super.setSelected(entry);
            if (entry != null && (item = (flatLayerInfo = CreateFlatWorldScreen.this.generator.getLayersInfo().get(CreateFlatWorldScreen.this.generator.getLayersInfo().size() - this.children().indexOf(entry) - 1)).getBlockState().getBlock().asItem()) != Items.AIR) {
                NarratorChatListener.INSTANCE.sayNow(new TranslatableComponent("narrator.select", item.getName(new ItemStack(item))).getString());
            }
            CreateFlatWorldScreen.this.updateButtonValidity();
        }

        @Override
        protected boolean isFocused() {
            return CreateFlatWorldScreen.this.getFocused() == this;
        }

        @Override
        protected int getScrollbarPosition() {
            return this.width - 70;
        }

        public void resetRows() {
            int n = this.children().indexOf(this.getSelected());
            this.clearEntries();
            for (int i = 0; i < CreateFlatWorldScreen.this.generator.getLayersInfo().size(); ++i) {
                this.addEntry(new Entry());
            }
            List list = this.children();
            if (n >= 0 && n < list.size()) {
                this.setSelected((Entry)list.get(n));
            }
        }

        class Entry
        extends ObjectSelectionList.Entry<Entry> {
            private Entry() {
            }

            @Override
            public void render(PoseStack poseStack, int n, int n2, int n3, int n4, int n5, int n6, int n7, boolean bl, float f) {
                FlatLayerInfo flatLayerInfo = CreateFlatWorldScreen.this.generator.getLayersInfo().get(CreateFlatWorldScreen.this.generator.getLayersInfo().size() - n - 1);
                BlockState blockState = flatLayerInfo.getBlockState();
                Item item = blockState.getBlock().asItem();
                if (item == Items.AIR) {
                    if (blockState.is(Blocks.WATER)) {
                        item = Items.WATER_BUCKET;
                    } else if (blockState.is(Blocks.LAVA)) {
                        item = Items.LAVA_BUCKET;
                    }
                }
                ItemStack itemStack = new ItemStack(item);
                this.blitSlot(poseStack, n3, n2, itemStack);
                CreateFlatWorldScreen.this.font.draw(poseStack, item.getName(itemStack), (float)(n3 + 18 + 5), (float)(n2 + 3), 16777215);
                String string = n == 0 ? I18n.get("createWorld.customize.flat.layer.top", flatLayerInfo.getHeight()) : (n == CreateFlatWorldScreen.this.generator.getLayersInfo().size() - 1 ? I18n.get("createWorld.customize.flat.layer.bottom", flatLayerInfo.getHeight()) : I18n.get("createWorld.customize.flat.layer", flatLayerInfo.getHeight()));
                CreateFlatWorldScreen.this.font.draw(poseStack, string, (float)(n3 + 2 + 213 - CreateFlatWorldScreen.this.font.width(string)), (float)(n2 + 3), 16777215);
            }

            @Override
            public boolean mouseClicked(double d, double d2, int n) {
                if (n == 0) {
                    DetailsList.this.setSelected(this);
                    return true;
                }
                return false;
            }

            private void blitSlot(PoseStack poseStack, int n, int n2, ItemStack itemStack) {
                this.blitSlotBg(poseStack, n + 1, n2 + 1);
                RenderSystem.enableRescaleNormal();
                if (!itemStack.isEmpty()) {
                    CreateFlatWorldScreen.this.itemRenderer.renderGuiItem(itemStack, n + 2, n2 + 2);
                }
                RenderSystem.disableRescaleNormal();
            }

            private void blitSlotBg(PoseStack poseStack, int n, int n2) {
                RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
                DetailsList.this.minecraft.getTextureManager().bind(GuiComponent.STATS_ICON_LOCATION);
                GuiComponent.blit(poseStack, n, n2, CreateFlatWorldScreen.this.getBlitOffset(), 0.0f, 0.0f, 18, 18, 128, 128);
            }
        }

    }

}

