/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.hash.HashCode
 *  com.google.common.hash.Hashing
 *  com.mojang.datafixers.util.Function4
 *  it.unimi.dsi.fastutil.booleans.BooleanConsumer
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.commons.lang3.Validate
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.client.gui.screens.worldselection;

import com.google.common.collect.ImmutableList;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Function4;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.screens.AlertScreen;
import net.minecraft.client.gui.screens.BackupConfirmScreen;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.ErrorScreen;
import net.minecraft.client.gui.screens.GenericDirtMessageScreen;
import net.minecraft.client.gui.screens.ProgressScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.gui.screens.worldselection.EditWorldScreen;
import net.minecraft.client.gui.screens.worldselection.SelectWorldScreen;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.level.DataPackConfig;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.levelgen.WorldGenSettings;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.level.storage.LevelStorageException;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.LevelSummary;
import net.minecraft.world.level.storage.WorldData;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WorldSelectionList
extends ObjectSelectionList<WorldListEntry> {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat();
    private static final ResourceLocation ICON_MISSING = new ResourceLocation("textures/misc/unknown_server.png");
    private static final ResourceLocation ICON_OVERLAY_LOCATION = new ResourceLocation("textures/gui/world_selection.png");
    private static final Component FROM_NEWER_TOOLTIP_1 = new TranslatableComponent("selectWorld.tooltip.fromNewerVersion1").withStyle(ChatFormatting.RED);
    private static final Component FROM_NEWER_TOOLTIP_2 = new TranslatableComponent("selectWorld.tooltip.fromNewerVersion2").withStyle(ChatFormatting.RED);
    private static final Component SNAPSHOT_TOOLTIP_1 = new TranslatableComponent("selectWorld.tooltip.snapshot1").withStyle(ChatFormatting.GOLD);
    private static final Component SNAPSHOT_TOOLTIP_2 = new TranslatableComponent("selectWorld.tooltip.snapshot2").withStyle(ChatFormatting.GOLD);
    private static final Component WORLD_LOCKED_TOOLTIP = new TranslatableComponent("selectWorld.locked").withStyle(ChatFormatting.RED);
    private final SelectWorldScreen screen;
    @Nullable
    private List<LevelSummary> cachedList;

    public WorldSelectionList(SelectWorldScreen selectWorldScreen, Minecraft minecraft, int n, int n2, int n3, int n4, int n5, Supplier<String> supplier, @Nullable WorldSelectionList worldSelectionList) {
        super(minecraft, n, n2, n3, n4, n5);
        this.screen = selectWorldScreen;
        if (worldSelectionList != null) {
            this.cachedList = worldSelectionList.cachedList;
        }
        this.refreshList(supplier, false);
    }

    public void refreshList(Supplier<String> supplier, boolean bl) {
        this.clearEntries();
        LevelStorageSource levelStorageSource = this.minecraft.getLevelSource();
        if (this.cachedList == null || bl) {
            try {
                this.cachedList = levelStorageSource.getLevelList();
            }
            catch (LevelStorageException levelStorageException) {
                LOGGER.error("Couldn't load level list", (Throwable)levelStorageException);
                this.minecraft.setScreen(new ErrorScreen(new TranslatableComponent("selectWorld.unable_to_load"), new TextComponent(levelStorageException.getMessage())));
                return;
            }
            Collections.sort(this.cachedList);
        }
        if (this.cachedList.isEmpty()) {
            this.minecraft.setScreen(CreateWorldScreen.create(null));
            return;
        }
        String string = supplier.get().toLowerCase(Locale.ROOT);
        for (LevelSummary levelSummary : this.cachedList) {
            if (!levelSummary.getLevelName().toLowerCase(Locale.ROOT).contains(string) && !levelSummary.getLevelId().toLowerCase(Locale.ROOT).contains(string)) continue;
            this.addEntry(new WorldListEntry(this, levelSummary));
        }
    }

    @Override
    protected int getScrollbarPosition() {
        return super.getScrollbarPosition() + 20;
    }

    @Override
    public int getRowWidth() {
        return super.getRowWidth() + 50;
    }

    @Override
    protected boolean isFocused() {
        return this.screen.getFocused() == this;
    }

    @Override
    public void setSelected(@Nullable WorldListEntry worldListEntry) {
        super.setSelected(worldListEntry);
        if (worldListEntry != null) {
            LevelSummary levelSummary = worldListEntry.summary;
            NarratorChatListener.INSTANCE.sayNow(new TranslatableComponent("narrator.select", new TranslatableComponent("narrator.select.world", levelSummary.getLevelName(), new Date(levelSummary.getLastPlayed()), levelSummary.isHardcore() ? new TranslatableComponent("gameMode.hardcore") : new TranslatableComponent("gameMode." + levelSummary.getGameMode().getName()), levelSummary.hasCheats() ? new TranslatableComponent("selectWorld.cheats") : TextComponent.EMPTY, levelSummary.getWorldVersionName())).getString());
        }
        this.screen.updateButtonStatus(worldListEntry != null && !worldListEntry.summary.isLocked());
    }

    @Override
    protected void moveSelection(AbstractSelectionList.SelectionDirection selectionDirection) {
        this.moveSelection(selectionDirection, worldListEntry -> !worldListEntry.summary.isLocked());
    }

    public Optional<WorldListEntry> getSelectedOpt() {
        return Optional.ofNullable(this.getSelected());
    }

    public SelectWorldScreen getScreen() {
        return this.screen;
    }

    public final class WorldListEntry
    extends ObjectSelectionList.Entry<WorldListEntry>
    implements AutoCloseable {
        private final Minecraft minecraft;
        private final SelectWorldScreen screen;
        private final LevelSummary summary;
        private final ResourceLocation iconLocation;
        private File iconFile;
        @Nullable
        private final DynamicTexture icon;
        private long lastClickTime;

        public WorldListEntry(WorldSelectionList worldSelectionList2, LevelSummary levelSummary) {
            this.screen = worldSelectionList2.getScreen();
            this.summary = levelSummary;
            this.minecraft = Minecraft.getInstance();
            String string = levelSummary.getLevelId();
            this.iconLocation = new ResourceLocation("minecraft", "worlds/" + Util.sanitizeName(string, ResourceLocation::validPathChar) + "/" + (Object)Hashing.sha1().hashUnencodedChars((CharSequence)string) + "/icon");
            this.iconFile = levelSummary.getIcon();
            if (!this.iconFile.isFile()) {
                this.iconFile = null;
            }
            this.icon = this.loadServerIcon();
        }

        @Override
        public void render(PoseStack poseStack, int n, int n2, int n3, int n4, int n5, int n6, int n7, boolean bl, float f) {
            String string = this.summary.getLevelName();
            String string2 = this.summary.getLevelId() + " (" + DATE_FORMAT.format(new Date(this.summary.getLastPlayed())) + ")";
            if (StringUtils.isEmpty((CharSequence)string)) {
                string = I18n.get("selectWorld.world", new Object[0]) + " " + (n + 1);
            }
            Component component = this.summary.getInfo();
            this.minecraft.font.draw(poseStack, string, (float)(n3 + 32 + 3), (float)(n2 + 1), 16777215);
            this.minecraft.font.getClass();
            this.minecraft.font.draw(poseStack, string2, (float)(n3 + 32 + 3), (float)(n2 + 9 + 3), 8421504);
            this.minecraft.font.getClass();
            this.minecraft.font.getClass();
            this.minecraft.font.draw(poseStack, component, (float)(n3 + 32 + 3), (float)(n2 + 9 + 9 + 3), 8421504);
            RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            this.minecraft.getTextureManager().bind(this.icon != null ? this.iconLocation : ICON_MISSING);
            RenderSystem.enableBlend();
            GuiComponent.blit(poseStack, n3, n2, 0.0f, 0.0f, 32, 32, 32, 32);
            RenderSystem.disableBlend();
            if (this.minecraft.options.touchscreen || bl) {
                int n8;
                this.minecraft.getTextureManager().bind(ICON_OVERLAY_LOCATION);
                GuiComponent.fill(poseStack, n3, n2, n3 + 32, n2 + 32, -1601138544);
                RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
                int n9 = n6 - n3;
                boolean bl2 = n9 < 32;
                int n10 = n8 = bl2 ? 32 : 0;
                if (this.summary.isLocked()) {
                    GuiComponent.blit(poseStack, n3, n2, 96.0f, n8, 32, 32, 256, 256);
                    if (bl2) {
                        this.screen.setToolTip(this.minecraft.font.split(WORLD_LOCKED_TOOLTIP, 175));
                    }
                } else if (this.summary.markVersionInList()) {
                    GuiComponent.blit(poseStack, n3, n2, 32.0f, n8, 32, 32, 256, 256);
                    if (this.summary.askToOpenWorld()) {
                        GuiComponent.blit(poseStack, n3, n2, 96.0f, n8, 32, 32, 256, 256);
                        if (bl2) {
                            this.screen.setToolTip((List<FormattedCharSequence>)ImmutableList.of((Object)FROM_NEWER_TOOLTIP_1.getVisualOrderText(), (Object)FROM_NEWER_TOOLTIP_2.getVisualOrderText()));
                        }
                    } else if (!SharedConstants.getCurrentVersion().isStable()) {
                        GuiComponent.blit(poseStack, n3, n2, 64.0f, n8, 32, 32, 256, 256);
                        if (bl2) {
                            this.screen.setToolTip((List<FormattedCharSequence>)ImmutableList.of((Object)SNAPSHOT_TOOLTIP_1.getVisualOrderText(), (Object)SNAPSHOT_TOOLTIP_2.getVisualOrderText()));
                        }
                    }
                } else {
                    GuiComponent.blit(poseStack, n3, n2, 0.0f, n8, 32, 32, 256, 256);
                }
            }
        }

        @Override
        public boolean mouseClicked(double d, double d2, int n) {
            if (this.summary.isLocked()) {
                return true;
            }
            WorldSelectionList.this.setSelected(this);
            this.screen.updateButtonStatus(WorldSelectionList.this.getSelectedOpt().isPresent());
            if (d - (double)WorldSelectionList.this.getRowLeft() <= 32.0) {
                this.joinWorld();
                return true;
            }
            if (Util.getMillis() - this.lastClickTime < 250L) {
                this.joinWorld();
                return true;
            }
            this.lastClickTime = Util.getMillis();
            return false;
        }

        public void joinWorld() {
            if (this.summary.isLocked()) {
                return;
            }
            if (this.summary.shouldBackup()) {
                TranslatableComponent translatableComponent = new TranslatableComponent("selectWorld.backupQuestion");
                TranslatableComponent translatableComponent2 = new TranslatableComponent("selectWorld.backupWarning", this.summary.getWorldVersionName(), SharedConstants.getCurrentVersion().getName());
                this.minecraft.setScreen(new BackupConfirmScreen(this.screen, (bl, bl2) -> {
                    if (bl) {
                        String string = this.summary.getLevelId();
                        try {
                            try (LevelStorageSource.LevelStorageAccess levelStorageAccess = this.minecraft.getLevelSource().createAccess(string);){
                                EditWorldScreen.makeBackupAndShowToast(levelStorageAccess);
                            }
                        }
                        catch (IOException iOException) {
                            SystemToast.onWorldAccessFailure(this.minecraft, string);
                            LOGGER.error("Failed to backup level {}", (Object)string, (Object)iOException);
                        }
                    }
                    this.loadWorld();
                }, translatableComponent, translatableComponent2, false));
            } else if (this.summary.askToOpenWorld()) {
                this.minecraft.setScreen(new ConfirmScreen(bl -> {
                    if (bl) {
                        try {
                            this.loadWorld();
                        }
                        catch (Exception exception) {
                            LOGGER.error("Failure to open 'future world'", (Throwable)exception);
                            this.minecraft.setScreen(new AlertScreen(() -> this.minecraft.setScreen(this.screen), new TranslatableComponent("selectWorld.futureworld.error.title"), new TranslatableComponent("selectWorld.futureworld.error.text")));
                        }
                    } else {
                        this.minecraft.setScreen(this.screen);
                    }
                }, new TranslatableComponent("selectWorld.versionQuestion"), new TranslatableComponent("selectWorld.versionWarning", this.summary.getWorldVersionName(), new TranslatableComponent("selectWorld.versionJoinButton"), CommonComponents.GUI_CANCEL)));
            } else {
                this.loadWorld();
            }
        }

        public void deleteWorld() {
            this.minecraft.setScreen(new ConfirmScreen(bl -> {
                if (bl) {
                    this.minecraft.setScreen(new ProgressScreen());
                    LevelStorageSource levelStorageSource = this.minecraft.getLevelSource();
                    String string = this.summary.getLevelId();
                    try {
                        try (LevelStorageSource.LevelStorageAccess levelStorageAccess = levelStorageSource.createAccess(string);){
                            levelStorageAccess.deleteLevel();
                        }
                    }
                    catch (IOException iOException) {
                        SystemToast.onWorldDeleteFailure(this.minecraft, string);
                        LOGGER.error("Failed to delete world {}", (Object)string, (Object)iOException);
                    }
                    WorldSelectionList.this.refreshList(() -> this.screen.searchBox.getValue(), true);
                }
                this.minecraft.setScreen(this.screen);
            }, new TranslatableComponent("selectWorld.deleteQuestion"), new TranslatableComponent("selectWorld.deleteWarning", this.summary.getLevelName()), new TranslatableComponent("selectWorld.deleteButton"), CommonComponents.GUI_CANCEL));
        }

        public void editWorld() {
            String string = this.summary.getLevelId();
            try {
                LevelStorageSource.LevelStorageAccess levelStorageAccess = this.minecraft.getLevelSource().createAccess(string);
                this.minecraft.setScreen(new EditWorldScreen(bl -> {
                    try {
                        levelStorageAccess.close();
                    }
                    catch (IOException iOException) {
                        LOGGER.error("Failed to unlock level {}", (Object)string, (Object)iOException);
                    }
                    if (bl) {
                        WorldSelectionList.this.refreshList(() -> this.screen.searchBox.getValue(), true);
                    }
                    this.minecraft.setScreen(this.screen);
                }, levelStorageAccess));
            }
            catch (IOException iOException) {
                SystemToast.onWorldAccessFailure(this.minecraft, string);
                LOGGER.error("Failed to access level {}", (Object)string, (Object)iOException);
                WorldSelectionList.this.refreshList(() -> this.screen.searchBox.getValue(), true);
            }
        }

        public void recreateWorld() {
            this.queueLoadScreen();
            RegistryAccess.RegistryHolder registryHolder = RegistryAccess.builtin();
            try {
                try (LevelStorageSource.LevelStorageAccess levelStorageAccess = this.minecraft.getLevelSource().createAccess(this.summary.getLevelId());
                     Minecraft.ServerStem serverStem = this.minecraft.makeServerStem(registryHolder, Minecraft::loadDataPacks, (Function4<LevelStorageSource.LevelStorageAccess, RegistryAccess.RegistryHolder, ResourceManager, DataPackConfig, WorldData>)((Function4)(arg_0, arg_1, arg_2, arg_3) -> Minecraft.loadWorldData(arg_0, arg_1, arg_2, arg_3)), false, levelStorageAccess);){
                    LevelSettings levelSettings = serverStem.worldData().getLevelSettings();
                    DataPackConfig dataPackConfig = levelSettings.getDataPackConfig();
                    WorldGenSettings worldGenSettings = serverStem.worldData().worldGenSettings();
                    Path path = CreateWorldScreen.createTempDataPackDirFromExistingWorld(levelStorageAccess.getLevelPath(LevelResource.DATAPACK_DIR), this.minecraft);
                    if (worldGenSettings.isOldCustomizedWorld()) {
                        this.minecraft.setScreen(new ConfirmScreen(bl -> this.minecraft.setScreen(bl ? new CreateWorldScreen(this.screen, levelSettings, worldGenSettings, path, dataPackConfig, registryHolder) : this.screen), new TranslatableComponent("selectWorld.recreate.customized.title"), new TranslatableComponent("selectWorld.recreate.customized.text"), CommonComponents.GUI_PROCEED, CommonComponents.GUI_CANCEL));
                    } else {
                        this.minecraft.setScreen(new CreateWorldScreen(this.screen, levelSettings, worldGenSettings, path, dataPackConfig, registryHolder));
                    }
                }
            }
            catch (Exception exception) {
                LOGGER.error("Unable to recreate world", (Throwable)exception);
                this.minecraft.setScreen(new AlertScreen(() -> this.minecraft.setScreen(this.screen), new TranslatableComponent("selectWorld.recreate.error.title"), new TranslatableComponent("selectWorld.recreate.error.text")));
            }
        }

        private void loadWorld() {
            this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0f));
            if (this.minecraft.getLevelSource().levelExists(this.summary.getLevelId())) {
                this.queueLoadScreen();
                this.minecraft.loadLevel(this.summary.getLevelId());
            }
        }

        private void queueLoadScreen() {
            this.minecraft.forceSetScreen(new GenericDirtMessageScreen(new TranslatableComponent("selectWorld.data_read")));
        }

        /*
         * Enabled aggressive block sorting
         * Enabled unnecessary exception pruning
         * Enabled aggressive exception aggregation
         */
        @Nullable
        private DynamicTexture loadServerIcon() {
            boolean bl;
            boolean bl2 = bl = this.iconFile != null && this.iconFile.isFile();
            if (!bl) {
                this.minecraft.getTextureManager().release(this.iconLocation);
                return null;
            }
            try {
                try (FileInputStream fileInputStream = new FileInputStream(this.iconFile);){
                    NativeImage nativeImage = NativeImage.read(fileInputStream);
                    Validate.validState((boolean)(nativeImage.getWidth() == 64), (String)"Must be 64 pixels wide", (Object[])new Object[0]);
                    Validate.validState((boolean)(nativeImage.getHeight() == 64), (String)"Must be 64 pixels high", (Object[])new Object[0]);
                    DynamicTexture dynamicTexture2 = new DynamicTexture(nativeImage);
                    this.minecraft.getTextureManager().register(this.iconLocation, (AbstractTexture)dynamicTexture2);
                    DynamicTexture dynamicTexture = dynamicTexture2;
                    return dynamicTexture;
                }
            }
            catch (Throwable throwable6) {
                LOGGER.error("Invalid icon for world {}", (Object)this.summary.getLevelId(), (Object)throwable6);
                this.iconFile = null;
                return null;
            }
        }

        @Override
        public void close() {
            if (this.icon != null) {
                this.icon.close();
            }
        }
    }

}

