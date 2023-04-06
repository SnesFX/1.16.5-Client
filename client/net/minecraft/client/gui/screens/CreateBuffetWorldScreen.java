/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.client.gui.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.WritableRegistry;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;

public class CreateBuffetWorldScreen
extends Screen {
    private static final Component BIOME_SELECT_INFO = new TranslatableComponent("createWorld.customize.buffet.biome");
    private final Screen parent;
    private final Consumer<Biome> applySettings;
    private final WritableRegistry<Biome> biomes;
    private BiomeList list;
    private Biome biome;
    private Button doneButton;

    public CreateBuffetWorldScreen(Screen screen, RegistryAccess registryAccess, Consumer<Biome> consumer, Biome biome) {
        super(new TranslatableComponent("createWorld.customize.buffet.title"));
        this.parent = screen;
        this.applySettings = consumer;
        this.biome = biome;
        this.biomes = registryAccess.registryOrThrow(Registry.BIOME_REGISTRY);
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(this.parent);
    }

    @Override
    protected void init() {
        this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
        this.list = new BiomeList();
        this.children.add(this.list);
        this.doneButton = this.addButton(new Button(this.width / 2 - 155, this.height - 28, 150, 20, CommonComponents.GUI_DONE, button -> {
            this.applySettings.accept(this.biome);
            this.minecraft.setScreen(this.parent);
        }));
        this.addButton(new Button(this.width / 2 + 5, this.height - 28, 150, 20, CommonComponents.GUI_CANCEL, button -> this.minecraft.setScreen(this.parent)));
        this.list.setSelected(this.list.children().stream().filter(entry -> Objects.equals(entry.biome, this.biome)).findFirst().orElse(null));
    }

    private void updateButtonValidity() {
        this.doneButton.active = this.list.getSelected() != null;
    }

    @Override
    public void render(PoseStack poseStack, int n, int n2, float f) {
        this.renderDirtBackground(0);
        this.list.render(poseStack, n, n2, f);
        CreateBuffetWorldScreen.drawCenteredString(poseStack, this.font, this.title, this.width / 2, 8, 16777215);
        CreateBuffetWorldScreen.drawCenteredString(poseStack, this.font, BIOME_SELECT_INFO, this.width / 2, 28, 10526880);
        super.render(poseStack, n, n2, f);
    }

    class BiomeList
    extends ObjectSelectionList<Entry> {
        private BiomeList() {
            super(CreateBuffetWorldScreen.this.minecraft, CreateBuffetWorldScreen.this.width, CreateBuffetWorldScreen.this.height, 40, CreateBuffetWorldScreen.this.height - 37, 16);
            CreateBuffetWorldScreen.this.biomes.entrySet().stream().sorted(Comparator.comparing(entry -> ((ResourceKey)entry.getKey()).location().toString())).forEach(entry -> this.addEntry(new Entry((Biome)entry.getValue())));
        }

        @Override
        protected boolean isFocused() {
            return CreateBuffetWorldScreen.this.getFocused() == this;
        }

        @Override
        public void setSelected(@Nullable Entry entry) {
            super.setSelected(entry);
            if (entry != null) {
                CreateBuffetWorldScreen.this.biome = entry.biome;
                NarratorChatListener.INSTANCE.sayNow(new TranslatableComponent("narrator.select", CreateBuffetWorldScreen.this.biomes.getKey(entry.biome)).getString());
            }
            CreateBuffetWorldScreen.this.updateButtonValidity();
        }

        class Entry
        extends ObjectSelectionList.Entry<Entry> {
            private final Biome biome;
            private final Component name;

            public Entry(Biome biome) {
                this.biome = biome;
                ResourceLocation resourceLocation = CreateBuffetWorldScreen.this.biomes.getKey(biome);
                String string = "biome." + resourceLocation.getNamespace() + "." + resourceLocation.getPath();
                this.name = Language.getInstance().has(string) ? new TranslatableComponent(string) : new TextComponent(resourceLocation.toString());
            }

            @Override
            public void render(PoseStack poseStack, int n, int n2, int n3, int n4, int n5, int n6, int n7, boolean bl, float f) {
                GuiComponent.drawString(poseStack, CreateBuffetWorldScreen.this.font, this.name, n3 + 5, n2 + 2, 16777215);
            }

            @Override
            public boolean mouseClicked(double d, double d2, int n) {
                if (n == 0) {
                    BiomeList.this.setSelected(this);
                    return true;
                }
                return false;
            }
        }

    }

}

