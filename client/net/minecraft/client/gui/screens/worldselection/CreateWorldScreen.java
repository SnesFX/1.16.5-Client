/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.mojang.datafixers.util.Pair
 *  it.unimi.dsi.fastutil.booleans.BooleanConsumer
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.mutable.MutableObject
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.client.gui.screens.worldselection;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.concurrent.CompletionStage;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.FileUtil;
import net.minecraft.Util;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.GenericDirtMessageScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.packs.PackSelectionScreen;
import net.minecraft.client.gui.screens.worldselection.EditGameRulesScreen;
import net.minecraft.client.gui.screens.worldselection.WorldGenSettingsComponent;
import net.minecraft.client.gui.screens.worldselection.WorldPreset;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.commands.Commands;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.WritableRegistry;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerResources;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.repository.FolderRepositorySource;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.RepositorySource;
import net.minecraft.server.packs.repository.ServerPacksSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.DataPackConfig;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.WorldGenSettings;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.apache.commons.lang3.mutable.MutableObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CreateWorldScreen
extends Screen {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Component GAME_MODEL_LABEL = new TranslatableComponent("selectWorld.gameMode");
    private static final Component SEED_LABEL = new TranslatableComponent("selectWorld.enterSeed");
    private static final Component SEED_INFO = new TranslatableComponent("selectWorld.seedInfo");
    private static final Component NAME_LABEL = new TranslatableComponent("selectWorld.enterName");
    private static final Component OUTPUT_DIR_INFO = new TranslatableComponent("selectWorld.resultFolder");
    private static final Component COMMANDS_INFO = new TranslatableComponent("selectWorld.allowCommands.info");
    private final Screen lastScreen;
    private EditBox nameEdit;
    private String resultFolder;
    private SelectedGameMode gameMode = SelectedGameMode.SURVIVAL;
    @Nullable
    private SelectedGameMode oldGameMode;
    private Difficulty selectedDifficulty = Difficulty.NORMAL;
    private Difficulty effectiveDifficulty = Difficulty.NORMAL;
    private boolean commands;
    private boolean commandsChanged;
    public boolean hardCore;
    protected DataPackConfig dataPacks;
    @Nullable
    private Path tempDataPackDir;
    @Nullable
    private PackRepository tempDataPackRepository;
    private boolean displayOptions;
    private Button createButton;
    private Button modeButton;
    private Button difficultyButton;
    private Button moreOptionsButton;
    private Button gameRulesButton;
    private Button dataPacksButton;
    private Button commandsButton;
    private Component gameModeHelp1;
    private Component gameModeHelp2;
    private String initName;
    private GameRules gameRules = new GameRules();
    public final WorldGenSettingsComponent worldGenSettingsComponent;

    public CreateWorldScreen(@Nullable Screen screen, LevelSettings levelSettings, WorldGenSettings worldGenSettings, @Nullable Path path, DataPackConfig dataPackConfig, RegistryAccess.RegistryHolder registryHolder) {
        this(screen, dataPackConfig, new WorldGenSettingsComponent(registryHolder, worldGenSettings, WorldPreset.of(worldGenSettings), OptionalLong.of(worldGenSettings.seed())));
        this.initName = levelSettings.levelName();
        this.commands = levelSettings.allowCommands();
        this.commandsChanged = true;
        this.effectiveDifficulty = this.selectedDifficulty = levelSettings.difficulty();
        this.gameRules.assignFrom(levelSettings.gameRules(), null);
        if (levelSettings.hardcore()) {
            this.gameMode = SelectedGameMode.HARDCORE;
        } else if (levelSettings.gameType().isSurvival()) {
            this.gameMode = SelectedGameMode.SURVIVAL;
        } else if (levelSettings.gameType().isCreative()) {
            this.gameMode = SelectedGameMode.CREATIVE;
        }
        this.tempDataPackDir = path;
    }

    public static CreateWorldScreen create(@Nullable Screen screen) {
        RegistryAccess.RegistryHolder registryHolder = RegistryAccess.builtin();
        return new CreateWorldScreen(screen, DataPackConfig.DEFAULT, new WorldGenSettingsComponent(registryHolder, WorldGenSettings.makeDefault(registryHolder.registryOrThrow(Registry.DIMENSION_TYPE_REGISTRY), registryHolder.registryOrThrow(Registry.BIOME_REGISTRY), registryHolder.registryOrThrow(Registry.NOISE_GENERATOR_SETTINGS_REGISTRY)), Optional.of(WorldPreset.NORMAL), OptionalLong.empty()));
    }

    private CreateWorldScreen(@Nullable Screen screen, DataPackConfig dataPackConfig, WorldGenSettingsComponent worldGenSettingsComponent) {
        super(new TranslatableComponent("selectWorld.create"));
        this.lastScreen = screen;
        this.initName = I18n.get("selectWorld.newWorld", new Object[0]);
        this.dataPacks = dataPackConfig;
        this.worldGenSettingsComponent = worldGenSettingsComponent;
    }

    @Override
    public void tick() {
        this.nameEdit.tick();
        this.worldGenSettingsComponent.tick();
    }

    @Override
    protected void init() {
        this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
        this.nameEdit = new EditBox(this.font, this.width / 2 - 100, 60, 200, 20, new TranslatableComponent("selectWorld.enterName")){

            @Override
            protected MutableComponent createNarrationMessage() {
                return super.createNarrationMessage().append(". ").append(new TranslatableComponent("selectWorld.resultFolder")).append(" ").append(CreateWorldScreen.this.resultFolder);
            }
        };
        this.nameEdit.setValue(this.initName);
        this.nameEdit.setResponder(string -> {
            this.initName = string;
            this.createButton.active = !this.nameEdit.getValue().isEmpty();
            this.updateResultFolder();
        });
        this.children.add(this.nameEdit);
        int n = this.width / 2 - 155;
        int n2 = this.width / 2 + 5;
        this.modeButton = this.addButton(new Button(n, 100, 150, 20, TextComponent.EMPTY, button -> {
            switch (this.gameMode) {
                case SURVIVAL: {
                    this.setGameMode(SelectedGameMode.HARDCORE);
                    break;
                }
                case HARDCORE: {
                    this.setGameMode(SelectedGameMode.CREATIVE);
                    break;
                }
                case CREATIVE: {
                    this.setGameMode(SelectedGameMode.SURVIVAL);
                }
            }
            button.queueNarration(250);
        }){

            @Override
            public Component getMessage() {
                return new TranslatableComponent("options.generic_value", GAME_MODEL_LABEL, new TranslatableComponent("selectWorld.gameMode." + CreateWorldScreen.this.gameMode.name));
            }

            @Override
            protected MutableComponent createNarrationMessage() {
                return super.createNarrationMessage().append(". ").append(CreateWorldScreen.this.gameModeHelp1).append(" ").append(CreateWorldScreen.this.gameModeHelp2);
            }
        });
        this.difficultyButton = this.addButton(new Button(n2, 100, 150, 20, new TranslatableComponent("options.difficulty"), button -> {
            this.effectiveDifficulty = this.selectedDifficulty = this.selectedDifficulty.nextById();
            button.queueNarration(250);
        }){

            @Override
            public Component getMessage() {
                return new TranslatableComponent("options.difficulty").append(": ").append(CreateWorldScreen.this.effectiveDifficulty.getDisplayName());
            }
        });
        this.commandsButton = this.addButton(new Button(n, 151, 150, 20, new TranslatableComponent("selectWorld.allowCommands"), button -> {
            this.commandsChanged = true;
            this.commands = !this.commands;
            button.queueNarration(250);
        }){

            @Override
            public Component getMessage() {
                return CommonComponents.optionStatus(super.getMessage(), CreateWorldScreen.this.commands && !CreateWorldScreen.this.hardCore);
            }

            @Override
            protected MutableComponent createNarrationMessage() {
                return super.createNarrationMessage().append(". ").append(new TranslatableComponent("selectWorld.allowCommands.info"));
            }
        });
        this.dataPacksButton = this.addButton(new Button(n2, 151, 150, 20, new TranslatableComponent("selectWorld.dataPacks"), button -> this.openDataPackSelectionScreen()));
        this.gameRulesButton = this.addButton(new Button(n, 185, 150, 20, new TranslatableComponent("selectWorld.gameRules"), button -> this.minecraft.setScreen(new EditGameRulesScreen(this.gameRules.copy(), optional -> {
            this.minecraft.setScreen(this);
            optional.ifPresent(gameRules -> {
                this.gameRules = gameRules;
            });
        }))));
        this.worldGenSettingsComponent.init(this, this.minecraft, this.font);
        this.moreOptionsButton = this.addButton(new Button(n2, 185, 150, 20, new TranslatableComponent("selectWorld.moreWorldOptions"), button -> this.toggleDisplayOptions()));
        this.createButton = this.addButton(new Button(n, this.height - 28, 150, 20, new TranslatableComponent("selectWorld.create"), button -> this.onCreate()));
        this.createButton.active = !this.initName.isEmpty();
        this.addButton(new Button(n2, this.height - 28, 150, 20, CommonComponents.GUI_CANCEL, button -> this.popScreen()));
        this.updateDisplayOptions();
        this.setInitialFocus(this.nameEdit);
        this.setGameMode(this.gameMode);
        this.updateResultFolder();
    }

    private void updateGameModeHelp() {
        this.gameModeHelp1 = new TranslatableComponent("selectWorld.gameMode." + this.gameMode.name + ".line1");
        this.gameModeHelp2 = new TranslatableComponent("selectWorld.gameMode." + this.gameMode.name + ".line2");
    }

    private void updateResultFolder() {
        this.resultFolder = this.nameEdit.getValue().trim();
        if (this.resultFolder.isEmpty()) {
            this.resultFolder = "World";
        }
        try {
            this.resultFolder = FileUtil.findAvailableName(this.minecraft.getLevelSource().getBaseDir(), this.resultFolder, "");
        }
        catch (Exception exception) {
            this.resultFolder = "World";
            try {
                this.resultFolder = FileUtil.findAvailableName(this.minecraft.getLevelSource().getBaseDir(), this.resultFolder, "");
            }
            catch (Exception exception2) {
                throw new RuntimeException("Could not create save folder", exception2);
            }
        }
    }

    @Override
    public void removed() {
        this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
    }

    private void onCreate() {
        LevelSettings levelSettings;
        this.minecraft.forceSetScreen(new GenericDirtMessageScreen(new TranslatableComponent("createWorld.preparing")));
        if (!this.copyTempDataPackDirToNewWorld()) {
            return;
        }
        this.cleanupTempResources();
        WorldGenSettings worldGenSettings = this.worldGenSettingsComponent.makeSettings(this.hardCore);
        if (worldGenSettings.isDebug()) {
            GameRules gameRules = new GameRules();
            gameRules.getRule(GameRules.RULE_DAYLIGHT).set(false, null);
            levelSettings = new LevelSettings(this.nameEdit.getValue().trim(), GameType.SPECTATOR, false, Difficulty.PEACEFUL, true, gameRules, DataPackConfig.DEFAULT);
        } else {
            levelSettings = new LevelSettings(this.nameEdit.getValue().trim(), this.gameMode.gameType, this.hardCore, this.effectiveDifficulty, this.commands && !this.hardCore, this.gameRules, this.dataPacks);
        }
        this.minecraft.createLevel(this.resultFolder, levelSettings, this.worldGenSettingsComponent.registryHolder(), worldGenSettings);
    }

    private void toggleDisplayOptions() {
        this.setDisplayOptions(!this.displayOptions);
    }

    private void setGameMode(SelectedGameMode selectedGameMode) {
        if (!this.commandsChanged) {
            boolean bl = this.commands = selectedGameMode == SelectedGameMode.CREATIVE;
        }
        if (selectedGameMode == SelectedGameMode.HARDCORE) {
            this.hardCore = true;
            this.commandsButton.active = false;
            this.worldGenSettingsComponent.bonusItemsButton.active = false;
            this.effectiveDifficulty = Difficulty.HARD;
            this.difficultyButton.active = false;
        } else {
            this.hardCore = false;
            this.commandsButton.active = true;
            this.worldGenSettingsComponent.bonusItemsButton.active = true;
            this.effectiveDifficulty = this.selectedDifficulty;
            this.difficultyButton.active = true;
        }
        this.gameMode = selectedGameMode;
        this.updateGameModeHelp();
    }

    public void updateDisplayOptions() {
        this.setDisplayOptions(this.displayOptions);
    }

    private void setDisplayOptions(boolean bl) {
        this.displayOptions = bl;
        this.modeButton.visible = !this.displayOptions;
        boolean bl2 = this.difficultyButton.visible = !this.displayOptions;
        if (this.worldGenSettingsComponent.isDebug()) {
            this.dataPacksButton.visible = false;
            this.modeButton.active = false;
            if (this.oldGameMode == null) {
                this.oldGameMode = this.gameMode;
            }
            this.setGameMode(SelectedGameMode.DEBUG);
            this.commandsButton.visible = false;
        } else {
            this.modeButton.active = true;
            if (this.oldGameMode != null) {
                this.setGameMode(this.oldGameMode);
            }
            this.commandsButton.visible = !this.displayOptions;
            this.dataPacksButton.visible = !this.displayOptions;
        }
        this.worldGenSettingsComponent.setDisplayOptions(this.displayOptions);
        this.nameEdit.setVisible(!this.displayOptions);
        if (this.displayOptions) {
            this.moreOptionsButton.setMessage(CommonComponents.GUI_DONE);
        } else {
            this.moreOptionsButton.setMessage(new TranslatableComponent("selectWorld.moreWorldOptions"));
        }
        this.gameRulesButton.visible = !this.displayOptions;
    }

    @Override
    public boolean keyPressed(int n, int n2, int n3) {
        if (super.keyPressed(n, n2, n3)) {
            return true;
        }
        if (n == 257 || n == 335) {
            this.onCreate();
            return true;
        }
        return false;
    }

    @Override
    public void onClose() {
        if (this.displayOptions) {
            this.setDisplayOptions(false);
        } else {
            this.popScreen();
        }
    }

    public void popScreen() {
        this.minecraft.setScreen(this.lastScreen);
        this.cleanupTempResources();
    }

    private void cleanupTempResources() {
        if (this.tempDataPackRepository != null) {
            this.tempDataPackRepository.close();
        }
        this.removeTempDataPackDir();
    }

    @Override
    public void render(PoseStack poseStack, int n, int n2, float f) {
        this.renderBackground(poseStack);
        CreateWorldScreen.drawCenteredString(poseStack, this.font, this.title, this.width / 2, 20, -1);
        if (this.displayOptions) {
            CreateWorldScreen.drawString(poseStack, this.font, SEED_LABEL, this.width / 2 - 100, 47, -6250336);
            CreateWorldScreen.drawString(poseStack, this.font, SEED_INFO, this.width / 2 - 100, 85, -6250336);
            this.worldGenSettingsComponent.render(poseStack, n, n2, f);
        } else {
            CreateWorldScreen.drawString(poseStack, this.font, NAME_LABEL, this.width / 2 - 100, 47, -6250336);
            CreateWorldScreen.drawString(poseStack, this.font, new TextComponent("").append(OUTPUT_DIR_INFO).append(" ").append(this.resultFolder), this.width / 2 - 100, 85, -6250336);
            this.nameEdit.render(poseStack, n, n2, f);
            CreateWorldScreen.drawString(poseStack, this.font, this.gameModeHelp1, this.width / 2 - 150, 122, -6250336);
            CreateWorldScreen.drawString(poseStack, this.font, this.gameModeHelp2, this.width / 2 - 150, 134, -6250336);
            if (this.commandsButton.visible) {
                CreateWorldScreen.drawString(poseStack, this.font, COMMANDS_INFO, this.width / 2 - 150, 172, -6250336);
            }
        }
        super.render(poseStack, n, n2, f);
    }

    @Override
    protected <T extends GuiEventListener> T addWidget(T t) {
        return super.addWidget(t);
    }

    @Override
    protected <T extends AbstractWidget> T addButton(T t) {
        return super.addButton(t);
    }

    @Nullable
    protected Path getTempDataPackDir() {
        if (this.tempDataPackDir == null) {
            try {
                this.tempDataPackDir = Files.createTempDirectory("mcworld-", new FileAttribute[0]);
            }
            catch (IOException iOException) {
                LOGGER.warn("Failed to create temporary dir", (Throwable)iOException);
                SystemToast.onPackCopyFailure(this.minecraft, this.resultFolder);
                this.popScreen();
            }
        }
        return this.tempDataPackDir;
    }

    private void openDataPackSelectionScreen() {
        Pair<File, PackRepository> pair = this.getDataPackSelectionSettings();
        if (pair != null) {
            this.minecraft.setScreen(new PackSelectionScreen(this, (PackRepository)pair.getSecond(), this::tryApplyNewDataPacks, (File)pair.getFirst(), new TranslatableComponent("dataPack.title")));
        }
    }

    private void tryApplyNewDataPacks(PackRepository packRepository) {
        ImmutableList immutableList = ImmutableList.copyOf(packRepository.getSelectedIds());
        List list = (List)packRepository.getAvailableIds().stream().filter(arg_0 -> CreateWorldScreen.lambda$tryApplyNewDataPacks$11((List)immutableList, arg_0)).collect(ImmutableList.toImmutableList());
        DataPackConfig dataPackConfig = new DataPackConfig((List<String>)immutableList, list);
        if (immutableList.equals(this.dataPacks.getEnabled())) {
            this.dataPacks = dataPackConfig;
            return;
        }
        this.minecraft.tell(() -> this.minecraft.setScreen(new GenericDirtMessageScreen(new TranslatableComponent("dataPack.validation.working"))));
        ServerResources.loadResources(packRepository.openAllSelected(), Commands.CommandSelection.INTEGRATED, 2, Util.backgroundExecutor(), this.minecraft).handle((serverResources, throwable) -> {
            if (throwable != null) {
                LOGGER.warn("Failed to validate datapack", throwable);
                this.minecraft.tell(() -> this.minecraft.setScreen(new ConfirmScreen(bl -> {
                    if (bl) {
                        this.openDataPackSelectionScreen();
                    } else {
                        this.dataPacks = DataPackConfig.DEFAULT;
                        this.minecraft.setScreen(this);
                    }
                }, new TranslatableComponent("dataPack.validation.failed"), TextComponent.EMPTY, new TranslatableComponent("dataPack.validation.back"), new TranslatableComponent("dataPack.validation.reset"))));
            } else {
                this.minecraft.tell(() -> {
                    this.dataPacks = dataPackConfig;
                    this.worldGenSettingsComponent.updateDataPacks((ServerResources)serverResources);
                    serverResources.close();
                    this.minecraft.setScreen(this);
                });
            }
            return null;
        });
    }

    private void removeTempDataPackDir() {
        if (this.tempDataPackDir != null) {
            try {
                try (Stream<Path> stream = Files.walk(this.tempDataPackDir, new FileVisitOption[0]);){
                    stream.sorted(Comparator.reverseOrder()).forEach(path -> {
                        try {
                            Files.delete(path);
                        }
                        catch (IOException iOException) {
                            LOGGER.warn("Failed to remove temporary file {}", path, (Object)iOException);
                        }
                    });
                }
            }
            catch (IOException iOException) {
                LOGGER.warn("Failed to list temporary dir {}", (Object)this.tempDataPackDir);
            }
            this.tempDataPackDir = null;
        }
    }

    private static void copyBetweenDirs(Path path, Path path2, Path path3) {
        try {
            Util.copyBetweenDirs(path, path2, path3);
        }
        catch (IOException iOException) {
            LOGGER.warn("Failed to copy datapack file from {} to {}", (Object)path3, (Object)path2);
            throw new OperationFailedException(iOException);
        }
    }

    private boolean copyTempDataPackDirToNewWorld() {
        if (this.tempDataPackDir != null) {
            try {
                try (LevelStorageSource.LevelStorageAccess levelStorageAccess = this.minecraft.getLevelSource().createAccess(this.resultFolder);
                     Stream<Path> stream = Files.walk(this.tempDataPackDir, new FileVisitOption[0]);){
                    Path path3 = levelStorageAccess.getLevelPath(LevelResource.DATAPACK_DIR);
                    Files.createDirectories(path3, new FileAttribute[0]);
                    stream.filter(path -> !path.equals(this.tempDataPackDir)).forEach(path2 -> CreateWorldScreen.copyBetweenDirs(this.tempDataPackDir, path3, path2));
                }
            }
            catch (IOException | OperationFailedException exception) {
                LOGGER.warn("Failed to copy datapacks to world {}", (Object)this.resultFolder, (Object)exception);
                SystemToast.onPackCopyFailure(this.minecraft, this.resultFolder);
                this.popScreen();
                return false;
            }
        }
        return true;
    }

    @Nullable
    public static Path createTempDataPackDirFromExistingWorld(Path path, Minecraft minecraft) {
        MutableObject mutableObject = new MutableObject();
        try {
            try (Stream<Path> stream = Files.walk(path, new FileVisitOption[0]);){
                stream.filter(path2 -> !path2.equals(path)).forEach(path2 -> {
                    Path path3 = (Path)mutableObject.getValue();
                    if (path3 == null) {
                        try {
                            path3 = Files.createTempDirectory("mcworld-", new FileAttribute[0]);
                        }
                        catch (IOException iOException) {
                            LOGGER.warn("Failed to create temporary dir");
                            throw new OperationFailedException(iOException);
                        }
                        mutableObject.setValue((Object)path3);
                    }
                    CreateWorldScreen.copyBetweenDirs(path, path3, path2);
                });
            }
        }
        catch (IOException | OperationFailedException exception) {
            LOGGER.warn("Failed to copy datapacks from world {}", (Object)path, (Object)exception);
            SystemToast.onPackCopyFailure(minecraft, path.toString());
            return null;
        }
        return (Path)mutableObject.getValue();
    }

    @Nullable
    private Pair<File, PackRepository> getDataPackSelectionSettings() {
        Path path = this.getTempDataPackDir();
        if (path != null) {
            File file = path.toFile();
            if (this.tempDataPackRepository == null) {
                this.tempDataPackRepository = new PackRepository(new ServerPacksSource(), new FolderRepositorySource(file, PackSource.DEFAULT));
                this.tempDataPackRepository.reload();
            }
            this.tempDataPackRepository.setSelected(this.dataPacks.getEnabled());
            return Pair.of((Object)file, (Object)this.tempDataPackRepository);
        }
        return null;
    }

    private static /* synthetic */ boolean lambda$tryApplyNewDataPacks$11(List list, String string) {
        return !list.contains(string);
    }

    static class OperationFailedException
    extends RuntimeException {
        public OperationFailedException(Throwable throwable) {
            super(throwable);
        }
    }

    static enum SelectedGameMode {
        SURVIVAL("survival", GameType.SURVIVAL),
        HARDCORE("hardcore", GameType.SURVIVAL),
        CREATIVE("creative", GameType.CREATIVE),
        DEBUG("spectator", GameType.SPECTATOR);
        
        private final String name;
        private final GameType gameType;

        private SelectedGameMode(String string2, GameType gameType) {
            this.name = string2;
            this.gameType = gameType;
        }
    }

}

