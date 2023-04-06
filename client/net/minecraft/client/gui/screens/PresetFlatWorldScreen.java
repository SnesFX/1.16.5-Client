/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Splitter
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  javax.annotation.Nullable
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.client.gui.screens;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.CreateFlatWorldScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.gui.screens.worldselection.WorldGenSettingsComponent;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.WritableRegistry;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.StructureSettings;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.JigsawConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.MineshaftConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OceanRuinConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.RuinedPortalConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.ShipwreckConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.StrongholdConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.StructureFeatureConfiguration;
import net.minecraft.world.level.levelgen.flat.FlatLayerInfo;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorSettings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PresetFlatWorldScreen
extends Screen {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final List<PresetInfo> PRESETS = Lists.newArrayList();
    private final CreateFlatWorldScreen parent;
    private Component shareText;
    private Component listText;
    private PresetsList list;
    private Button selectButton;
    private EditBox export;
    private FlatLevelGeneratorSettings settings;

    public PresetFlatWorldScreen(CreateFlatWorldScreen createFlatWorldScreen) {
        super(new TranslatableComponent("createWorld.customize.presets.title"));
        this.parent = createFlatWorldScreen;
    }

    @Nullable
    private static FlatLayerInfo getLayerInfoFromString(String string, int n) {
        int n2;
        Block block;
        String[] arrstring = string.split("\\*", 2);
        if (arrstring.length == 2) {
            try {
                n2 = Math.max(Integer.parseInt(arrstring[0]), 0);
            }
            catch (NumberFormatException numberFormatException) {
                LOGGER.error("Error while parsing flat world string => {}", (Object)numberFormatException.getMessage());
                return null;
            }
        } else {
            n2 = 1;
        }
        int n3 = Math.min(n + n2, 256);
        int n4 = n3 - n;
        String string2 = arrstring[arrstring.length - 1];
        try {
            block = Registry.BLOCK.getOptional(new ResourceLocation(string2)).orElse(null);
        }
        catch (Exception exception) {
            LOGGER.error("Error while parsing flat world string => {}", (Object)exception.getMessage());
            return null;
        }
        if (block == null) {
            LOGGER.error("Error while parsing flat world string => Unknown block, {}", (Object)string2);
            return null;
        }
        FlatLayerInfo flatLayerInfo = new FlatLayerInfo(n4, block);
        flatLayerInfo.setStart(n);
        return flatLayerInfo;
    }

    private static List<FlatLayerInfo> getLayersInfoFromString(String string) {
        ArrayList arrayList = Lists.newArrayList();
        String[] arrstring = string.split(",");
        int n = 0;
        for (String string2 : arrstring) {
            FlatLayerInfo flatLayerInfo = PresetFlatWorldScreen.getLayerInfoFromString(string2, n);
            if (flatLayerInfo == null) {
                return Collections.emptyList();
            }
            arrayList.add(flatLayerInfo);
            n += flatLayerInfo.getHeight();
        }
        return arrayList;
    }

    public static FlatLevelGeneratorSettings fromString(Registry<Biome> registry, String string, FlatLevelGeneratorSettings flatLevelGeneratorSettings) {
        Object object;
        Iterator iterator = Splitter.on((char)';').split((CharSequence)string).iterator();
        if (!iterator.hasNext()) {
            return FlatLevelGeneratorSettings.getDefault(registry);
        }
        List<FlatLayerInfo> list = PresetFlatWorldScreen.getLayersInfoFromString((String)iterator.next());
        if (list.isEmpty()) {
            return FlatLevelGeneratorSettings.getDefault(registry);
        }
        FlatLevelGeneratorSettings flatLevelGeneratorSettings2 = flatLevelGeneratorSettings.withLayers(list, flatLevelGeneratorSettings.structureSettings());
        ResourceKey<Biome> resourceKey = Biomes.PLAINS;
        if (iterator.hasNext()) {
            try {
                object = new ResourceLocation((String)iterator.next());
                resourceKey = ResourceKey.create(Registry.BIOME_REGISTRY, (ResourceLocation)object);
                registry.getOptional(resourceKey).orElseThrow(() -> PresetFlatWorldScreen.lambda$fromString$0((ResourceLocation)object));
            }
            catch (Exception exception) {
                LOGGER.error("Error while parsing flat world string => {}", (Object)exception.getMessage());
            }
        }
        object = resourceKey;
        flatLevelGeneratorSettings2.setBiome(() -> (Biome)registry.getOrThrow((ResourceKey<Biome>)object));
        return flatLevelGeneratorSettings2;
    }

    private static String save(Registry<Biome> registry, FlatLevelGeneratorSettings flatLevelGeneratorSettings) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < flatLevelGeneratorSettings.getLayersInfo().size(); ++i) {
            if (i > 0) {
                stringBuilder.append(",");
            }
            stringBuilder.append(flatLevelGeneratorSettings.getLayersInfo().get(i));
        }
        stringBuilder.append(";");
        stringBuilder.append(registry.getKey(flatLevelGeneratorSettings.getBiome()));
        return stringBuilder.toString();
    }

    @Override
    protected void init() {
        this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
        this.shareText = new TranslatableComponent("createWorld.customize.presets.share");
        this.listText = new TranslatableComponent("createWorld.customize.presets.list");
        this.export = new EditBox(this.font, 50, 40, this.width - 100, 20, this.shareText);
        this.export.setMaxLength(1230);
        WritableRegistry<Biome> writableRegistry = this.parent.parent.worldGenSettingsComponent.registryHolder().registryOrThrow(Registry.BIOME_REGISTRY);
        this.export.setValue(PresetFlatWorldScreen.save(writableRegistry, this.parent.settings()));
        this.settings = this.parent.settings();
        this.children.add(this.export);
        this.list = new PresetsList();
        this.children.add(this.list);
        this.selectButton = this.addButton(new Button(this.width / 2 - 155, this.height - 28, 150, 20, new TranslatableComponent("createWorld.customize.presets.select"), button -> {
            FlatLevelGeneratorSettings flatLevelGeneratorSettings = PresetFlatWorldScreen.fromString(writableRegistry, this.export.getValue(), this.settings);
            this.parent.setConfig(flatLevelGeneratorSettings);
            this.minecraft.setScreen(this.parent);
        }));
        this.addButton(new Button(this.width / 2 + 5, this.height - 28, 150, 20, CommonComponents.GUI_CANCEL, button -> this.minecraft.setScreen(this.parent)));
        this.updateButtonValidity(this.list.getSelected() != null);
    }

    @Override
    public boolean mouseScrolled(double d, double d2, double d3) {
        return this.list.mouseScrolled(d, d2, d3);
    }

    @Override
    public void resize(Minecraft minecraft, int n, int n2) {
        String string = this.export.getValue();
        this.init(minecraft, n, n2);
        this.export.setValue(string);
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(this.parent);
    }

    @Override
    public void removed() {
        this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
    }

    @Override
    public void render(PoseStack poseStack, int n, int n2, float f) {
        this.renderBackground(poseStack);
        this.list.render(poseStack, n, n2, f);
        RenderSystem.pushMatrix();
        RenderSystem.translatef(0.0f, 0.0f, 400.0f);
        PresetFlatWorldScreen.drawCenteredString(poseStack, this.font, this.title, this.width / 2, 8, 16777215);
        PresetFlatWorldScreen.drawString(poseStack, this.font, this.shareText, 50, 30, 10526880);
        PresetFlatWorldScreen.drawString(poseStack, this.font, this.listText, 50, 70, 10526880);
        RenderSystem.popMatrix();
        this.export.render(poseStack, n, n2, f);
        super.render(poseStack, n, n2, f);
    }

    @Override
    public void tick() {
        this.export.tick();
        super.tick();
    }

    public void updateButtonValidity(boolean bl) {
        this.selectButton.active = bl || this.export.getValue().length() > 1;
    }

    private static void preset(Component component, ItemLike itemLike, ResourceKey<Biome> resourceKey, List<StructureFeature<?>> list, boolean bl, boolean bl2, boolean bl3, FlatLayerInfo ... arrflatLayerInfo) {
        PRESETS.add(new PresetInfo(itemLike.asItem(), component, registry -> {
            Object object2;
            HashMap hashMap = Maps.newHashMap();
            for (Object object2 : list) {
                hashMap.put(object2, StructureSettings.DEFAULTS.get(object2));
            }
            StructureSettings structureSettings = new StructureSettings(bl ? Optional.of(StructureSettings.DEFAULT_STRONGHOLD) : Optional.empty(), hashMap);
            object2 = new FlatLevelGeneratorSettings(structureSettings, (Registry<Biome>)registry);
            if (bl2) {
                ((FlatLevelGeneratorSettings)object2).setDecoration();
            }
            if (bl3) {
                ((FlatLevelGeneratorSettings)object2).setAddLakes();
            }
            for (int i = arrflatLayerInfo.length - 1; i >= 0; --i) {
                ((FlatLevelGeneratorSettings)object2).getLayersInfo().add(arrflatLayerInfo[i]);
            }
            ((FlatLevelGeneratorSettings)object2).setBiome(() -> (Biome)registry.getOrThrow(resourceKey));
            ((FlatLevelGeneratorSettings)object2).updateLayers();
            return ((FlatLevelGeneratorSettings)object2).withStructureSettings(structureSettings);
        }));
    }

    private static /* synthetic */ IllegalArgumentException lambda$fromString$0(ResourceLocation resourceLocation) {
        return new IllegalArgumentException("Invalid Biome: " + resourceLocation);
    }

    static /* synthetic */ CreateFlatWorldScreen access$200(PresetFlatWorldScreen presetFlatWorldScreen) {
        return presetFlatWorldScreen.parent;
    }

    static {
        PresetFlatWorldScreen.preset(new TranslatableComponent("createWorld.customize.preset.classic_flat"), Blocks.GRASS_BLOCK, Biomes.PLAINS, Arrays.asList(StructureFeature.VILLAGE), false, false, false, new FlatLayerInfo(1, Blocks.GRASS_BLOCK), new FlatLayerInfo(2, Blocks.DIRT), new FlatLayerInfo(1, Blocks.BEDROCK));
        PresetFlatWorldScreen.preset(new TranslatableComponent("createWorld.customize.preset.tunnelers_dream"), Blocks.STONE, Biomes.MOUNTAINS, Arrays.asList(StructureFeature.MINESHAFT), true, true, false, new FlatLayerInfo(1, Blocks.GRASS_BLOCK), new FlatLayerInfo(5, Blocks.DIRT), new FlatLayerInfo(230, Blocks.STONE), new FlatLayerInfo(1, Blocks.BEDROCK));
        PresetFlatWorldScreen.preset(new TranslatableComponent("createWorld.customize.preset.water_world"), Items.WATER_BUCKET, Biomes.DEEP_OCEAN, Arrays.asList(StructureFeature.OCEAN_RUIN, StructureFeature.SHIPWRECK, StructureFeature.OCEAN_MONUMENT), false, false, false, new FlatLayerInfo(90, Blocks.WATER), new FlatLayerInfo(5, Blocks.SAND), new FlatLayerInfo(5, Blocks.DIRT), new FlatLayerInfo(5, Blocks.STONE), new FlatLayerInfo(1, Blocks.BEDROCK));
        PresetFlatWorldScreen.preset(new TranslatableComponent("createWorld.customize.preset.overworld"), Blocks.GRASS, Biomes.PLAINS, Arrays.asList(StructureFeature.VILLAGE, StructureFeature.MINESHAFT, StructureFeature.PILLAGER_OUTPOST, StructureFeature.RUINED_PORTAL), true, true, true, new FlatLayerInfo(1, Blocks.GRASS_BLOCK), new FlatLayerInfo(3, Blocks.DIRT), new FlatLayerInfo(59, Blocks.STONE), new FlatLayerInfo(1, Blocks.BEDROCK));
        PresetFlatWorldScreen.preset(new TranslatableComponent("createWorld.customize.preset.snowy_kingdom"), Blocks.SNOW, Biomes.SNOWY_TUNDRA, Arrays.asList(StructureFeature.VILLAGE, StructureFeature.IGLOO), false, false, false, new FlatLayerInfo(1, Blocks.SNOW), new FlatLayerInfo(1, Blocks.GRASS_BLOCK), new FlatLayerInfo(3, Blocks.DIRT), new FlatLayerInfo(59, Blocks.STONE), new FlatLayerInfo(1, Blocks.BEDROCK));
        PresetFlatWorldScreen.preset(new TranslatableComponent("createWorld.customize.preset.bottomless_pit"), Items.FEATHER, Biomes.PLAINS, Arrays.asList(StructureFeature.VILLAGE), false, false, false, new FlatLayerInfo(1, Blocks.GRASS_BLOCK), new FlatLayerInfo(3, Blocks.DIRT), new FlatLayerInfo(2, Blocks.COBBLESTONE));
        PresetFlatWorldScreen.preset(new TranslatableComponent("createWorld.customize.preset.desert"), Blocks.SAND, Biomes.DESERT, Arrays.asList(StructureFeature.VILLAGE, StructureFeature.DESERT_PYRAMID, StructureFeature.MINESHAFT), true, true, false, new FlatLayerInfo(8, Blocks.SAND), new FlatLayerInfo(52, Blocks.SANDSTONE), new FlatLayerInfo(3, Blocks.STONE), new FlatLayerInfo(1, Blocks.BEDROCK));
        PresetFlatWorldScreen.preset(new TranslatableComponent("createWorld.customize.preset.redstone_ready"), Items.REDSTONE, Biomes.DESERT, Collections.emptyList(), false, false, false, new FlatLayerInfo(52, Blocks.SANDSTONE), new FlatLayerInfo(3, Blocks.STONE), new FlatLayerInfo(1, Blocks.BEDROCK));
        PresetFlatWorldScreen.preset(new TranslatableComponent("createWorld.customize.preset.the_void"), Blocks.BARRIER, Biomes.THE_VOID, Collections.emptyList(), false, true, false, new FlatLayerInfo(1, Blocks.AIR));
    }

    static class PresetInfo {
        public final Item icon;
        public final Component name;
        public final Function<Registry<Biome>, FlatLevelGeneratorSettings> settings;

        public PresetInfo(Item item, Component component, Function<Registry<Biome>, FlatLevelGeneratorSettings> function) {
            this.icon = item;
            this.name = component;
            this.settings = function;
        }

        public Component getName() {
            return this.name;
        }
    }

    class PresetsList
    extends ObjectSelectionList<Entry> {
        public PresetsList() {
            super(PresetFlatWorldScreen.this.minecraft, PresetFlatWorldScreen.this.width, PresetFlatWorldScreen.this.height, 80, PresetFlatWorldScreen.this.height - 37, 24);
            for (int i = 0; i < PRESETS.size(); ++i) {
                this.addEntry(new Entry());
            }
        }

        @Override
        public void setSelected(@Nullable Entry entry) {
            super.setSelected(entry);
            if (entry != null) {
                NarratorChatListener.INSTANCE.sayNow(new TranslatableComponent("narrator.select", ((PresetInfo)PRESETS.get(this.children().indexOf(entry))).getName()).getString());
            }
            PresetFlatWorldScreen.this.updateButtonValidity(entry != null);
        }

        @Override
        protected boolean isFocused() {
            return PresetFlatWorldScreen.this.getFocused() == this;
        }

        @Override
        public boolean keyPressed(int n, int n2, int n3) {
            if (super.keyPressed(n, n2, n3)) {
                return true;
            }
            if ((n == 257 || n == 335) && this.getSelected() != null) {
                ((Entry)this.getSelected()).select();
            }
            return false;
        }

        public class Entry
        extends ObjectSelectionList.Entry<Entry> {
            @Override
            public void render(PoseStack poseStack, int n, int n2, int n3, int n4, int n5, int n6, int n7, boolean bl, float f) {
                PresetInfo presetInfo = (PresetInfo)PRESETS.get(n);
                this.blitSlot(poseStack, n3, n2, presetInfo.icon);
                PresetFlatWorldScreen.this.font.draw(poseStack, presetInfo.name, (float)(n3 + 18 + 5), (float)(n2 + 6), 16777215);
            }

            @Override
            public boolean mouseClicked(double d, double d2, int n) {
                if (n == 0) {
                    this.select();
                }
                return false;
            }

            private void select() {
                PresetsList.this.setSelected(this);
                PresetInfo presetInfo = (PresetInfo)PRESETS.get(PresetsList.this.children().indexOf(this));
                WritableRegistry<Biome> writableRegistry = PresetFlatWorldScreen.access$200((PresetFlatWorldScreen)PresetFlatWorldScreen.this).parent.worldGenSettingsComponent.registryHolder().registryOrThrow(Registry.BIOME_REGISTRY);
                PresetFlatWorldScreen.this.settings = presetInfo.settings.apply(writableRegistry);
                PresetFlatWorldScreen.this.export.setValue(PresetFlatWorldScreen.save(writableRegistry, PresetFlatWorldScreen.this.settings));
                PresetFlatWorldScreen.this.export.moveCursorToStart();
            }

            private void blitSlot(PoseStack poseStack, int n, int n2, Item item) {
                this.blitSlotBg(poseStack, n + 1, n2 + 1);
                RenderSystem.enableRescaleNormal();
                PresetFlatWorldScreen.this.itemRenderer.renderGuiItem(new ItemStack(item), n + 2, n2 + 2);
                RenderSystem.disableRescaleNormal();
            }

            private void blitSlotBg(PoseStack poseStack, int n, int n2) {
                RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
                PresetsList.this.minecraft.getTextureManager().bind(GuiComponent.STATS_ICON_LOCATION);
                GuiComponent.blit(poseStack, n, n2, PresetFlatWorldScreen.this.getBlitOffset(), 0.0f, 0.0f, 18, 18, 128, 128);
            }
        }

    }

}

