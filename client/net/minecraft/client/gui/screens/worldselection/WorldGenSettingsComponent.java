/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonIOException
 *  com.google.gson.JsonParser
 *  com.google.gson.JsonSyntaxException
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.DataResult$PartialResult
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.JsonOps
 *  com.mojang.serialization.Lifecycle
 *  it.unimi.dsi.fastutil.booleans.BooleanConsumer
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.lwjgl.util.tinyfd.TinyFileDialogs
 */
package net.minecraft.client.gui.screens.worldselection;

import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.Lifecycle;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.client.gui.components.TickableWidget;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.gui.screens.worldselection.WorldPreset;
import net.minecraft.commands.Commands;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.RegistryReadOps;
import net.minecraft.resources.RegistryWriteOps;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerResources;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.repository.FolderRepositorySource;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.RepositorySource;
import net.minecraft.server.packs.repository.ServerPacksSource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.DataPackConfig;
import net.minecraft.world.level.levelgen.WorldGenSettings;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.util.tinyfd.TinyFileDialogs;

public class WorldGenSettingsComponent
implements TickableWidget,
Widget {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Component CUSTOM_WORLD_DESCRIPTION = new TranslatableComponent("generator.custom");
    private static final Component AMPLIFIED_HELP_TEXT = new TranslatableComponent("generator.amplified.info");
    private static final Component MAP_FEATURES_INFO = new TranslatableComponent("selectWorld.mapFeatures.info");
    private MultiLineLabel amplifiedWorldInfo = MultiLineLabel.EMPTY;
    private Font font;
    private int width;
    private EditBox seedEdit;
    private Button featuresButton;
    public Button bonusItemsButton;
    private Button typeButton;
    private Button customizeTypeButton;
    private Button importSettingsButton;
    private RegistryAccess.RegistryHolder registryHolder;
    private WorldGenSettings settings;
    private Optional<WorldPreset> preset;
    private OptionalLong seed;

    public WorldGenSettingsComponent(RegistryAccess.RegistryHolder registryHolder, WorldGenSettings worldGenSettings, Optional<WorldPreset> optional, OptionalLong optionalLong) {
        this.registryHolder = registryHolder;
        this.settings = worldGenSettings;
        this.preset = optional;
        this.seed = optionalLong;
    }

    public void init(final CreateWorldScreen createWorldScreen, Minecraft minecraft, Font font) {
        this.font = font;
        this.width = createWorldScreen.width;
        this.seedEdit = new EditBox(this.font, this.width / 2 - 100, 60, 200, 20, new TranslatableComponent("selectWorld.enterSeed"));
        this.seedEdit.setValue(WorldGenSettingsComponent.toString(this.seed));
        this.seedEdit.setResponder(string -> {
            this.seed = this.parseSeed();
        });
        createWorldScreen.addWidget(this.seedEdit);
        int n = this.width / 2 - 155;
        int n2 = this.width / 2 + 5;
        this.featuresButton = createWorldScreen.addButton(new Button(n, 100, 150, 20, new TranslatableComponent("selectWorld.mapFeatures"), button -> {
            this.settings = this.settings.withFeaturesToggled();
            button.queueNarration(250);
        }){

            @Override
            public Component getMessage() {
                return CommonComponents.optionStatus(super.getMessage(), WorldGenSettingsComponent.this.settings.generateFeatures());
            }

            @Override
            protected MutableComponent createNarrationMessage() {
                return super.createNarrationMessage().append(". ").append(new TranslatableComponent("selectWorld.mapFeatures.info"));
            }
        });
        this.featuresButton.visible = false;
        this.typeButton = createWorldScreen.addButton(new Button(n2, 100, 150, 20, new TranslatableComponent("selectWorld.mapType"), button -> {
            while (this.preset.isPresent()) {
                int n = WorldPreset.PRESETS.indexOf(this.preset.get()) + 1;
                if (n >= WorldPreset.PRESETS.size()) {
                    n = 0;
                }
                WorldPreset worldPreset = WorldPreset.PRESETS.get(n);
                this.preset = Optional.of(worldPreset);
                this.settings = worldPreset.create(this.registryHolder, this.settings.seed(), this.settings.generateFeatures(), this.settings.generateBonusChest());
                if (this.settings.isDebug() && !Screen.hasShiftDown()) continue;
            }
            createWorldScreen.updateDisplayOptions();
            button.queueNarration(250);
        }){

            @Override
            public Component getMessage() {
                return super.getMessage().copy().append(" ").append(WorldGenSettingsComponent.this.preset.map(WorldPreset::description).orElse(CUSTOM_WORLD_DESCRIPTION));
            }

            @Override
            protected MutableComponent createNarrationMessage() {
                if (Objects.equals(WorldGenSettingsComponent.this.preset, Optional.of(WorldPreset.AMPLIFIED))) {
                    return super.createNarrationMessage().append(". ").append(AMPLIFIED_HELP_TEXT);
                }
                return super.createNarrationMessage();
            }
        });
        this.typeButton.visible = false;
        this.typeButton.active = this.preset.isPresent();
        this.customizeTypeButton = createWorldScreen.addButton(new Button(n2, 120, 150, 20, new TranslatableComponent("selectWorld.customizeType"), button -> {
            WorldPreset.PresetEditor presetEditor = WorldPreset.EDITORS.get(this.preset);
            if (presetEditor != null) {
                minecraft.setScreen(presetEditor.createEditScreen(createWorldScreen, this.settings));
            }
        }));
        this.customizeTypeButton.visible = false;
        this.bonusItemsButton = createWorldScreen.addButton(new Button(n, 151, 150, 20, new TranslatableComponent("selectWorld.bonusItems"), button -> {
            this.settings = this.settings.withBonusChestToggled();
            button.queueNarration(250);
        }){

            @Override
            public Component getMessage() {
                return CommonComponents.optionStatus(super.getMessage(), WorldGenSettingsComponent.this.settings.generateBonusChest() && !createWorldScreen.hardCore);
            }
        });
        this.bonusItemsButton.visible = false;
        this.importSettingsButton = createWorldScreen.addButton(new Button(n, 185, 150, 20, new TranslatableComponent("selectWorld.import_worldgen_settings"), button -> {
            Object object;
            Object object2;
            DataResult dataResult;
            Object object3;
            Object object4;
            ServerResources serverResources;
            TranslatableComponent translatableComponent = new TranslatableComponent("selectWorld.import_worldgen_settings.select_file");
            String string = TinyFileDialogs.tinyfd_openFileDialog((CharSequence)translatableComponent.getString(), null, null, null, (boolean)false);
            if (string == null) {
                return;
            }
            RegistryAccess.RegistryHolder registryHolder = RegistryAccess.builtin();
            PackRepository packRepository = new PackRepository(new ServerPacksSource(), new FolderRepositorySource(createWorldScreen.getTempDataPackDir().toFile(), PackSource.WORLD));
            try {
                MinecraftServer.configurePackRepository(packRepository, createWorldScreen.dataPacks, false);
                object3 = ServerResources.loadResources(packRepository.openAllSelected(), Commands.CommandSelection.INTEGRATED, 2, Util.backgroundExecutor(), minecraft);
                minecraft.managedBlock(object3::isDone);
                serverResources = ((CompletableFuture)object3).get();
            }
            catch (InterruptedException | ExecutionException exception) {
                LOGGER.error("Error loading data packs when importing world settings", (Throwable)exception);
                TranslatableComponent translatableComponent2 = new TranslatableComponent("selectWorld.import_worldgen_settings.failure");
                TextComponent textComponent = new TextComponent(exception.getMessage());
                minecraft.getToasts().addToast(SystemToast.multiline(minecraft, SystemToast.SystemToastIds.WORLD_GEN_SETTINGS_TRANSFER, translatableComponent2, textComponent));
                packRepository.close();
                return;
            }
            object3 = RegistryReadOps.create(JsonOps.INSTANCE, serverResources.getResourceManager(), registryHolder);
            JsonParser jsonParser = new JsonParser();
            try {
                object2 = Files.newBufferedReader(Paths.get(string, new String[0]));
                object = null;
                try {
                    object4 = jsonParser.parse((Reader)object2);
                    dataResult = WorldGenSettings.CODEC.parse((DynamicOps)object3, object4);
                }
                catch (Throwable throwable) {
                    object = throwable;
                    throw throwable;
                }
                finally {
                    if (object2 != null) {
                        if (object != null) {
                            try {
                                ((BufferedReader)object2).close();
                            }
                            catch (Throwable throwable) {
                                ((Throwable)object).addSuppressed(throwable);
                            }
                        } else {
                            ((BufferedReader)object2).close();
                        }
                    }
                }
            }
            catch (JsonIOException | JsonSyntaxException | IOException throwable) {
                dataResult = DataResult.error((String)("Failed to parse file: " + throwable.getMessage()));
            }
            if (dataResult.error().isPresent()) {
                object2 = new TranslatableComponent("selectWorld.import_worldgen_settings.failure");
                object = ((DataResult.PartialResult)dataResult.error().get()).message();
                LOGGER.error("Error parsing world settings: {}", object);
                object4 = new TextComponent((String)object);
                minecraft.getToasts().addToast(SystemToast.multiline(minecraft, SystemToast.SystemToastIds.WORLD_GEN_SETTINGS_TRANSFER, (Component)object2, (Component)object4));
            }
            serverResources.close();
            object2 = dataResult.lifecycle();
            dataResult.resultOrPartial(((Logger)LOGGER)::error).ifPresent(arg_0 -> this.lambda$null$6(minecraft, createWorldScreen, registryHolder, (Lifecycle)object2, arg_0));
        }));
        this.importSettingsButton.visible = false;
        this.amplifiedWorldInfo = MultiLineLabel.create(font, (FormattedText)AMPLIFIED_HELP_TEXT, this.typeButton.getWidth());
    }

    private void importSettings(RegistryAccess.RegistryHolder registryHolder, WorldGenSettings worldGenSettings) {
        this.registryHolder = registryHolder;
        this.settings = worldGenSettings;
        this.preset = WorldPreset.of(worldGenSettings);
        this.seed = OptionalLong.of(worldGenSettings.seed());
        this.seedEdit.setValue(WorldGenSettingsComponent.toString(this.seed));
        this.typeButton.active = this.preset.isPresent();
    }

    @Override
    public void tick() {
        this.seedEdit.tick();
    }

    @Override
    public void render(PoseStack poseStack, int n, int n2, float f) {
        if (this.featuresButton.visible) {
            this.font.drawShadow(poseStack, MAP_FEATURES_INFO, (float)(this.width / 2 - 150), 122.0f, -6250336);
        }
        this.seedEdit.render(poseStack, n, n2, f);
        if (this.preset.equals(Optional.of(WorldPreset.AMPLIFIED))) {
            this.font.getClass();
            this.amplifiedWorldInfo.renderLeftAligned(poseStack, this.typeButton.x + 2, this.typeButton.y + 22, 9, 10526880);
        }
    }

    protected void updateSettings(WorldGenSettings worldGenSettings) {
        this.settings = worldGenSettings;
    }

    private static String toString(OptionalLong optionalLong) {
        if (optionalLong.isPresent()) {
            return Long.toString(optionalLong.getAsLong());
        }
        return "";
    }

    private static OptionalLong parseLong(String string) {
        try {
            return OptionalLong.of(Long.parseLong(string));
        }
        catch (NumberFormatException numberFormatException) {
            return OptionalLong.empty();
        }
    }

    public WorldGenSettings makeSettings(boolean bl) {
        OptionalLong optionalLong = this.parseSeed();
        return this.settings.withSeed(bl, optionalLong);
    }

    private OptionalLong parseSeed() {
        OptionalLong optionalLong;
        String string = this.seedEdit.getValue();
        OptionalLong optionalLong2 = StringUtils.isEmpty((CharSequence)string) ? OptionalLong.empty() : ((optionalLong = WorldGenSettingsComponent.parseLong(string)).isPresent() && optionalLong.getAsLong() != 0L ? optionalLong : OptionalLong.of(string.hashCode()));
        return optionalLong2;
    }

    public boolean isDebug() {
        return this.settings.isDebug();
    }

    public void setDisplayOptions(boolean bl) {
        this.typeButton.visible = bl;
        if (this.settings.isDebug()) {
            this.featuresButton.visible = false;
            this.bonusItemsButton.visible = false;
            this.customizeTypeButton.visible = false;
            this.importSettingsButton.visible = false;
        } else {
            this.featuresButton.visible = bl;
            this.bonusItemsButton.visible = bl;
            this.customizeTypeButton.visible = bl && WorldPreset.EDITORS.containsKey(this.preset);
            this.importSettingsButton.visible = bl;
        }
        this.seedEdit.setVisible(bl);
    }

    public RegistryAccess.RegistryHolder registryHolder() {
        return this.registryHolder;
    }

    void updateDataPacks(ServerResources serverResources) {
        RegistryAccess.RegistryHolder registryHolder = RegistryAccess.builtin();
        RegistryWriteOps registryWriteOps = RegistryWriteOps.create(JsonOps.INSTANCE, this.registryHolder);
        RegistryReadOps registryReadOps = RegistryReadOps.create(JsonOps.INSTANCE, serverResources.getResourceManager(), registryHolder);
        DataResult dataResult = WorldGenSettings.CODEC.encodeStart(registryWriteOps, (Object)this.settings).flatMap(jsonElement -> WorldGenSettings.CODEC.parse((DynamicOps)registryReadOps, jsonElement));
        dataResult.resultOrPartial(Util.prefix("Error parsing worldgen settings after loading data packs: ", ((Logger)LOGGER)::error)).ifPresent(worldGenSettings -> {
            this.settings = worldGenSettings;
            this.registryHolder = registryHolder;
        });
    }

    private /* synthetic */ void lambda$null$6(Minecraft minecraft, CreateWorldScreen createWorldScreen, RegistryAccess.RegistryHolder registryHolder, Lifecycle lifecycle, WorldGenSettings worldGenSettings) {
        BooleanConsumer booleanConsumer = bl -> {
            minecraft.setScreen(createWorldScreen);
            if (bl) {
                this.importSettings(registryHolder, worldGenSettings);
            }
        };
        if (lifecycle == Lifecycle.stable()) {
            this.importSettings(registryHolder, worldGenSettings);
        } else if (lifecycle == Lifecycle.experimental()) {
            minecraft.setScreen(new ConfirmScreen(booleanConsumer, new TranslatableComponent("selectWorld.import_worldgen_settings.experimental.title"), new TranslatableComponent("selectWorld.import_worldgen_settings.experimental.question")));
        } else {
            minecraft.setScreen(new ConfirmScreen(booleanConsumer, new TranslatableComponent("selectWorld.import_worldgen_settings.deprecated.title"), new TranslatableComponent("selectWorld.import_worldgen_settings.deprecated.question")));
        }
    }

}

