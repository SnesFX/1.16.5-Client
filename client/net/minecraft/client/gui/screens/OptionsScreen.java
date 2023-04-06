/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  it.unimi.dsi.fastutil.booleans.BooleanConsumer
 */
package net.minecraft.client.gui.screens;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import net.minecraft.client.BooleanOption;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Option;
import net.minecraft.client.Options;
import net.minecraft.client.ProgressOption;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.LockIconButton;
import net.minecraft.client.gui.components.OptionButton;
import net.minecraft.client.gui.screens.AccessibilityOptionsScreen;
import net.minecraft.client.gui.screens.ChatOptionsScreen;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.LanguageSelectScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.SkinCustomizationScreen;
import net.minecraft.client.gui.screens.SoundOptionsScreen;
import net.minecraft.client.gui.screens.VideoSettingsScreen;
import net.minecraft.client.gui.screens.controls.ControlsScreen;
import net.minecraft.client.gui.screens.packs.PackSelectionScreen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.resources.language.LanguageManager;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundChangeDifficultyPacket;
import net.minecraft.network.protocol.game.ServerboundLockDifficultyPacket;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackCompatibility;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.world.Difficulty;

public class OptionsScreen
extends Screen {
    private static final Option[] OPTION_SCREEN_OPTIONS = new Option[]{Option.FOV};
    private final Screen lastScreen;
    private final Options options;
    private Button difficultyButton;
    private LockIconButton lockButton;
    private Difficulty currentDifficulty;

    public OptionsScreen(Screen screen, Options options) {
        super(new TranslatableComponent("options.title"));
        this.lastScreen = screen;
        this.options = options;
    }

    @Override
    protected void init() {
        int n = 0;
        for (Option option : OPTION_SCREEN_OPTIONS) {
            int n2 = this.width / 2 - 155 + n % 2 * 160;
            int n3 = this.height / 6 - 12 + 24 * (n >> 1);
            this.addButton(option.createButton(this.minecraft.options, n2, n3, 150));
            ++n;
        }
        if (this.minecraft.level != null) {
            this.currentDifficulty = this.minecraft.level.getDifficulty();
            this.difficultyButton = this.addButton(new Button(this.width / 2 - 155 + n % 2 * 160, this.height / 6 - 12 + 24 * (n >> 1), 150, 20, this.getDifficultyText(this.currentDifficulty), button -> {
                this.currentDifficulty = Difficulty.byId(this.currentDifficulty.getId() + 1);
                this.minecraft.getConnection().send(new ServerboundChangeDifficultyPacket(this.currentDifficulty));
                this.difficultyButton.setMessage(this.getDifficultyText(this.currentDifficulty));
            }));
            if (this.minecraft.hasSingleplayerServer() && !this.minecraft.level.getLevelData().isHardcore()) {
                this.difficultyButton.setWidth(this.difficultyButton.getWidth() - 20);
                this.lockButton = this.addButton(new LockIconButton(this.difficultyButton.x + this.difficultyButton.getWidth(), this.difficultyButton.y, button -> this.minecraft.setScreen(new ConfirmScreen(this::lockCallback, new TranslatableComponent("difficulty.lock.title"), new TranslatableComponent("difficulty.lock.question", new TranslatableComponent("options.difficulty." + this.minecraft.level.getLevelData().getDifficulty().getKey()))))));
                this.lockButton.setLocked(this.minecraft.level.getLevelData().isDifficultyLocked());
                this.lockButton.active = !this.lockButton.isLocked();
                this.difficultyButton.active = !this.lockButton.isLocked();
            } else {
                this.difficultyButton.active = false;
            }
        } else {
            this.addButton(new OptionButton(this.width / 2 - 155 + n % 2 * 160, this.height / 6 - 12 + 24 * (n >> 1), 150, 20, Option.REALMS_NOTIFICATIONS, Option.REALMS_NOTIFICATIONS.getMessage(this.options), button -> {
                Option.REALMS_NOTIFICATIONS.toggle(this.options);
                this.options.save();
                button.setMessage(Option.REALMS_NOTIFICATIONS.getMessage(this.options));
            }));
        }
        this.addButton(new Button(this.width / 2 - 155, this.height / 6 + 48 - 6, 150, 20, new TranslatableComponent("options.skinCustomisation"), button -> this.minecraft.setScreen(new SkinCustomizationScreen(this, this.options))));
        this.addButton(new Button(this.width / 2 + 5, this.height / 6 + 48 - 6, 150, 20, new TranslatableComponent("options.sounds"), button -> this.minecraft.setScreen(new SoundOptionsScreen(this, this.options))));
        this.addButton(new Button(this.width / 2 - 155, this.height / 6 + 72 - 6, 150, 20, new TranslatableComponent("options.video"), button -> this.minecraft.setScreen(new VideoSettingsScreen(this, this.options))));
        this.addButton(new Button(this.width / 2 + 5, this.height / 6 + 72 - 6, 150, 20, new TranslatableComponent("options.controls"), button -> this.minecraft.setScreen(new ControlsScreen(this, this.options))));
        this.addButton(new Button(this.width / 2 - 155, this.height / 6 + 96 - 6, 150, 20, new TranslatableComponent("options.language"), button -> this.minecraft.setScreen(new LanguageSelectScreen(this, this.options, this.minecraft.getLanguageManager()))));
        this.addButton(new Button(this.width / 2 + 5, this.height / 6 + 96 - 6, 150, 20, new TranslatableComponent("options.chat.title"), button -> this.minecraft.setScreen(new ChatOptionsScreen(this, this.options))));
        this.addButton(new Button(this.width / 2 - 155, this.height / 6 + 120 - 6, 150, 20, new TranslatableComponent("options.resourcepack"), button -> this.minecraft.setScreen(new PackSelectionScreen(this, this.minecraft.getResourcePackRepository(), this::updatePackList, this.minecraft.getResourcePackDirectory(), new TranslatableComponent("resourcePack.title")))));
        this.addButton(new Button(this.width / 2 + 5, this.height / 6 + 120 - 6, 150, 20, new TranslatableComponent("options.accessibility.title"), button -> this.minecraft.setScreen(new AccessibilityOptionsScreen(this, this.options))));
        this.addButton(new Button(this.width / 2 - 100, this.height / 6 + 168, 200, 20, CommonComponents.GUI_DONE, button -> this.minecraft.setScreen(this.lastScreen)));
    }

    private void updatePackList(PackRepository packRepository) {
        ImmutableList immutableList = ImmutableList.copyOf(this.options.resourcePacks);
        this.options.resourcePacks.clear();
        this.options.incompatibleResourcePacks.clear();
        for (Pack pack : packRepository.getSelectedPacks()) {
            if (pack.isFixedPosition()) continue;
            this.options.resourcePacks.add(pack.getId());
            if (pack.getCompatibility().isCompatible()) continue;
            this.options.incompatibleResourcePacks.add(pack.getId());
        }
        this.options.save();
        ImmutableList immutableList2 = ImmutableList.copyOf(this.options.resourcePacks);
        if (!immutableList2.equals((Object)immutableList)) {
            this.minecraft.reloadResourcePacks();
        }
    }

    private Component getDifficultyText(Difficulty difficulty) {
        return new TranslatableComponent("options.difficulty").append(": ").append(difficulty.getDisplayName());
    }

    private void lockCallback(boolean bl) {
        this.minecraft.setScreen(this);
        if (bl && this.minecraft.level != null) {
            this.minecraft.getConnection().send(new ServerboundLockDifficultyPacket(true));
            this.lockButton.setLocked(true);
            this.lockButton.active = false;
            this.difficultyButton.active = false;
        }
    }

    @Override
    public void removed() {
        this.options.save();
    }

    @Override
    public void render(PoseStack poseStack, int n, int n2, float f) {
        this.renderBackground(poseStack);
        OptionsScreen.drawCenteredString(poseStack, this.font, this.title, this.width / 2, 15, 16777215);
        super.render(poseStack, n, n2, f);
    }
}

