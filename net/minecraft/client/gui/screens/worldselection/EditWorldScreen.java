/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonIOException
 *  com.google.gson.stream.JsonWriter
 *  com.mojang.datafixers.DataFixer
 *  com.mojang.datafixers.util.Either
 *  com.mojang.datafixers.util.Function4
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.DataResult$PartialResult
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.JsonOps
 *  it.unimi.dsi.fastutil.booleans.BooleanConsumer
 *  org.apache.commons.io.FileUtils
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.client.gui.screens.worldselection;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.stream.JsonWriter;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Function4;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.Util;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.client.gui.screens.BackupConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.worldselection.OptimizeWorldScreen;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.RegistryWriteOps;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.Mth;
import net.minecraft.world.level.DataPackConfig;
import net.minecraft.world.level.levelgen.WorldGenSettings;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.LevelSummary;
import net.minecraft.world.level.storage.WorldData;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EditWorldScreen
extends Screen {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson WORLD_GEN_SETTINGS_GSON = new GsonBuilder().setPrettyPrinting().serializeNulls().disableHtmlEscaping().create();
    private static final Component NAME_LABEL = new TranslatableComponent("selectWorld.enterName");
    private Button renameButton;
    private final BooleanConsumer callback;
    private EditBox nameEdit;
    private final LevelStorageSource.LevelStorageAccess levelAccess;

    public EditWorldScreen(BooleanConsumer booleanConsumer, LevelStorageSource.LevelStorageAccess levelStorageAccess) {
        super(new TranslatableComponent("selectWorld.edit.title"));
        this.callback = booleanConsumer;
        this.levelAccess = levelStorageAccess;
    }

    @Override
    public void tick() {
        this.nameEdit.tick();
    }

    @Override
    protected void init() {
        this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
        Button button2 = this.addButton(new Button(this.width / 2 - 100, this.height / 4 + 0 + 5, 200, 20, new TranslatableComponent("selectWorld.edit.resetIcon"), button -> {
            FileUtils.deleteQuietly((File)this.levelAccess.getIconFile());
            button.active = false;
        }));
        this.addButton(new Button(this.width / 2 - 100, this.height / 4 + 24 + 5, 200, 20, new TranslatableComponent("selectWorld.edit.openFolder"), button -> Util.getPlatform().openFile(this.levelAccess.getLevelPath(LevelResource.ROOT).toFile())));
        this.addButton(new Button(this.width / 2 - 100, this.height / 4 + 48 + 5, 200, 20, new TranslatableComponent("selectWorld.edit.backup"), button -> {
            boolean bl = EditWorldScreen.makeBackupAndShowToast(this.levelAccess);
            this.callback.accept(!bl);
        }));
        this.addButton(new Button(this.width / 2 - 100, this.height / 4 + 72 + 5, 200, 20, new TranslatableComponent("selectWorld.edit.backupFolder"), button -> {
            LevelStorageSource levelStorageSource = this.minecraft.getLevelSource();
            Path path = levelStorageSource.getBackupPath();
            try {
                Files.createDirectories(Files.exists(path, new LinkOption[0]) ? path.toRealPath(new LinkOption[0]) : path, new FileAttribute[0]);
            }
            catch (IOException iOException) {
                throw new RuntimeException(iOException);
            }
            Util.getPlatform().openFile(path.toFile());
        }));
        this.addButton(new Button(this.width / 2 - 100, this.height / 4 + 96 + 5, 200, 20, new TranslatableComponent("selectWorld.edit.optimize"), button -> this.minecraft.setScreen(new BackupConfirmScreen(this, (bl, bl2) -> {
            if (bl) {
                EditWorldScreen.makeBackupAndShowToast(this.levelAccess);
            }
            this.minecraft.setScreen(OptimizeWorldScreen.create(this.minecraft, this.callback, this.minecraft.getFixerUpper(), this.levelAccess, bl2));
        }, new TranslatableComponent("optimizeWorld.confirm.title"), new TranslatableComponent("optimizeWorld.confirm.description"), true))));
        this.addButton(new Button(this.width / 2 - 100, this.height / 4 + 120 + 5, 200, 20, new TranslatableComponent("selectWorld.edit.export_worldgen_settings"), button -> {
            Object object;
            DataResult dataResult;
            Object object2;
            RegistryAccess.RegistryHolder registryHolder = RegistryAccess.builtin();
            try {
                object = this.minecraft.makeServerStem(registryHolder, Minecraft::loadDataPacks, (Function4<LevelStorageSource.LevelStorageAccess, RegistryAccess.RegistryHolder, ResourceManager, DataPackConfig, WorldData>)((Function4)(arg_0, arg_1, arg_2, arg_3) -> Minecraft.loadWorldData(arg_0, arg_1, arg_2, arg_3)), false, this.levelAccess);
                object2 = null;
                try {
                    RegistryWriteOps registryWriteOps = RegistryWriteOps.create(JsonOps.INSTANCE, registryHolder);
                    DataResult dataResult2 = WorldGenSettings.CODEC.encodeStart(registryWriteOps, (Object)((Minecraft.ServerStem)object).worldData().worldGenSettings());
                    dataResult = dataResult2.flatMap(jsonElement -> {
                        Path path = this.levelAccess.getLevelPath(LevelResource.ROOT).resolve("worldgen_settings_export.json");
                        try {
                            try (JsonWriter jsonWriter = WORLD_GEN_SETTINGS_GSON.newJsonWriter((Writer)Files.newBufferedWriter(path, StandardCharsets.UTF_8, new OpenOption[0]));){
                                WORLD_GEN_SETTINGS_GSON.toJson(jsonElement, jsonWriter);
                            }
                        }
                        catch (JsonIOException | IOException throwable) {
                            return DataResult.error((String)("Error writing file: " + throwable.getMessage()));
                        }
                        return DataResult.success((Object)path.toString());
                    });
                }
                catch (Throwable throwable) {
                    object2 = throwable;
                    throw throwable;
                }
                finally {
                    if (object != null) {
                        if (object2 != null) {
                            try {
                                ((Minecraft.ServerStem)object).close();
                            }
                            catch (Throwable throwable) {
                                ((Throwable)object2).addSuppressed(throwable);
                            }
                        } else {
                            ((Minecraft.ServerStem)object).close();
                        }
                    }
                }
            }
            catch (InterruptedException | ExecutionException exception) {
                dataResult = DataResult.error((String)"Could not parse level data!");
            }
            object = new TextComponent((String)dataResult.get().map(Function.identity(), DataResult.PartialResult::message));
            object2 = new TranslatableComponent(dataResult.result().isPresent() ? "selectWorld.edit.export_worldgen_settings.success" : "selectWorld.edit.export_worldgen_settings.failure");
            dataResult.error().ifPresent(partialResult -> LOGGER.error("Error exporting world settings: {}", partialResult));
            this.minecraft.getToasts().addToast(SystemToast.multiline(this.minecraft, SystemToast.SystemToastIds.WORLD_GEN_SETTINGS_TRANSFER, (Component)object2, (Component)object));
        }));
        this.renameButton = this.addButton(new Button(this.width / 2 - 100, this.height / 4 + 144 + 5, 98, 20, new TranslatableComponent("selectWorld.edit.save"), button -> this.onRename()));
        this.addButton(new Button(this.width / 2 + 2, this.height / 4 + 144 + 5, 98, 20, CommonComponents.GUI_CANCEL, button -> this.callback.accept(false)));
        button2.active = this.levelAccess.getIconFile().isFile();
        LevelSummary levelSummary = this.levelAccess.getSummary();
        String string2 = levelSummary == null ? "" : levelSummary.getLevelName();
        this.nameEdit = new EditBox(this.font, this.width / 2 - 100, 38, 200, 20, new TranslatableComponent("selectWorld.enterName"));
        this.nameEdit.setValue(string2);
        this.nameEdit.setResponder(string -> {
            this.renameButton.active = !string.trim().isEmpty();
        });
        this.children.add(this.nameEdit);
        this.setInitialFocus(this.nameEdit);
    }

    @Override
    public void resize(Minecraft minecraft, int n, int n2) {
        String string = this.nameEdit.getValue();
        this.init(minecraft, n, n2);
        this.nameEdit.setValue(string);
    }

    @Override
    public void onClose() {
        this.callback.accept(false);
    }

    @Override
    public void removed() {
        this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
    }

    private void onRename() {
        try {
            this.levelAccess.renameLevel(this.nameEdit.getValue().trim());
            this.callback.accept(true);
        }
        catch (IOException iOException) {
            LOGGER.error("Failed to access world '{}'", (Object)this.levelAccess.getLevelId(), (Object)iOException);
            SystemToast.onWorldAccessFailure(this.minecraft, this.levelAccess.getLevelId());
            this.callback.accept(true);
        }
    }

    public static void makeBackupAndShowToast(LevelStorageSource levelStorageSource, String string) {
        boolean bl = false;
        try {
            try (LevelStorageSource.LevelStorageAccess levelStorageAccess = levelStorageSource.createAccess(string);){
                bl = true;
                EditWorldScreen.makeBackupAndShowToast(levelStorageAccess);
            }
        }
        catch (IOException iOException) {
            if (!bl) {
                SystemToast.onWorldAccessFailure(Minecraft.getInstance(), string);
            }
            LOGGER.warn("Failed to create backup of level {}", (Object)string, (Object)iOException);
        }
    }

    public static boolean makeBackupAndShowToast(LevelStorageSource.LevelStorageAccess levelStorageAccess) {
        long l = 0L;
        IOException iOException = null;
        try {
            l = levelStorageAccess.makeWorldBackup();
        }
        catch (IOException iOException2) {
            iOException = iOException2;
        }
        if (iOException != null) {
            TranslatableComponent translatableComponent = new TranslatableComponent("selectWorld.edit.backupFailed");
            TextComponent textComponent = new TextComponent(iOException.getMessage());
            Minecraft.getInstance().getToasts().addToast(new SystemToast(SystemToast.SystemToastIds.WORLD_BACKUP, translatableComponent, textComponent));
            return false;
        }
        TranslatableComponent translatableComponent = new TranslatableComponent("selectWorld.edit.backupCreated", levelStorageAccess.getLevelId());
        TranslatableComponent translatableComponent2 = new TranslatableComponent("selectWorld.edit.backupSize", Mth.ceil((double)l / 1048576.0));
        Minecraft.getInstance().getToasts().addToast(new SystemToast(SystemToast.SystemToastIds.WORLD_BACKUP, translatableComponent, translatableComponent2));
        return true;
    }

    @Override
    public void render(PoseStack poseStack, int n, int n2, float f) {
        this.renderBackground(poseStack);
        EditWorldScreen.drawCenteredString(poseStack, this.font, this.title, this.width / 2, 15, 16777215);
        EditWorldScreen.drawString(poseStack, this.font, NAME_LABEL, this.width / 2 - 100, 24, 10526880);
        this.nameEdit.render(poseStack, n, n2, f);
        super.render(poseStack, n, n2, f);
    }
}

