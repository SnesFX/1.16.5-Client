/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 */
package net.minecraft.client.gui.screens.debug;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class GameModeSwitcherScreen
extends Screen {
    private static final ResourceLocation GAMEMODE_SWITCHER_LOCATION = new ResourceLocation("textures/gui/container/gamemode_switcher.png");
    private static final int ALL_SLOTS_WIDTH = GameModeIcon.values().length * 30 - 5;
    private static final Component SELECT_KEY = new TranslatableComponent("debug.gamemodes.select_next", new TranslatableComponent("debug.gamemodes.press_f4").withStyle(ChatFormatting.AQUA));
    private final Optional<GameModeIcon> previousHovered;
    private Optional<GameModeIcon> currentlyHovered = Optional.empty();
    private int firstMouseX;
    private int firstMouseY;
    private boolean setFirstMousePos;
    private final List<GameModeSlot> slots = Lists.newArrayList();

    public GameModeSwitcherScreen() {
        super(NarratorChatListener.NO_TITLE);
        this.previousHovered = GameModeIcon.getFromGameType(this.getDefaultSelected());
    }

    private GameType getDefaultSelected() {
        GameType gameType = Minecraft.getInstance().gameMode.getPlayerMode();
        GameType gameType2 = Minecraft.getInstance().gameMode.getPreviousPlayerMode();
        if (gameType2 == GameType.NOT_SET) {
            gameType2 = gameType == GameType.CREATIVE ? GameType.SURVIVAL : GameType.CREATIVE;
        }
        return gameType2;
    }

    @Override
    protected void init() {
        super.init();
        this.currentlyHovered = this.previousHovered.isPresent() ? this.previousHovered : GameModeIcon.getFromGameType(this.minecraft.gameMode.getPlayerMode());
        for (int i = 0; i < GameModeIcon.VALUES.length; ++i) {
            GameModeIcon gameModeIcon = GameModeIcon.VALUES[i];
            this.slots.add(new GameModeSlot(gameModeIcon, this.width / 2 - ALL_SLOTS_WIDTH / 2 + i * 30, this.height / 2 - 30));
        }
    }

    @Override
    public void render(PoseStack poseStack, int n, int n2, float f) {
        if (this.checkToClose()) {
            return;
        }
        poseStack.pushPose();
        RenderSystem.enableBlend();
        this.minecraft.getTextureManager().bind(GAMEMODE_SWITCHER_LOCATION);
        int n3 = this.width / 2 - 62;
        int n4 = this.height / 2 - 30 - 27;
        GameModeSwitcherScreen.blit(poseStack, n3, n4, 0.0f, 0.0f, 125, 75, 128, 128);
        poseStack.popPose();
        super.render(poseStack, n, n2, f);
        this.currentlyHovered.ifPresent(gameModeIcon -> GameModeSwitcherScreen.drawCenteredString(poseStack, this.font, gameModeIcon.getName(), this.width / 2, this.height / 2 - 30 - 20, -1));
        GameModeSwitcherScreen.drawCenteredString(poseStack, this.font, SELECT_KEY, this.width / 2, this.height / 2 + 5, 16777215);
        if (!this.setFirstMousePos) {
            this.firstMouseX = n;
            this.firstMouseY = n2;
            this.setFirstMousePos = true;
        }
        boolean bl = this.firstMouseX == n && this.firstMouseY == n2;
        for (GameModeSlot gameModeSlot : this.slots) {
            gameModeSlot.render(poseStack, n, n2, f);
            this.currentlyHovered.ifPresent(gameModeIcon -> gameModeSlot.setSelected(gameModeIcon == gameModeSlot.icon));
            if (bl || !gameModeSlot.isHovered()) continue;
            this.currentlyHovered = Optional.of(gameModeSlot.icon);
        }
    }

    private void switchToHoveredGameMode() {
        GameModeSwitcherScreen.switchToHoveredGameMode(this.minecraft, this.currentlyHovered);
    }

    private static void switchToHoveredGameMode(Minecraft minecraft, Optional<GameModeIcon> optional) {
        if (minecraft.gameMode == null || minecraft.player == null || !optional.isPresent()) {
            return;
        }
        Optional optional2 = GameModeIcon.getFromGameType(minecraft.gameMode.getPlayerMode());
        GameModeIcon gameModeIcon = optional.get();
        if (optional2.isPresent() && minecraft.player.hasPermissions(2) && gameModeIcon != optional2.get()) {
            minecraft.player.chat(gameModeIcon.getCommand());
        }
    }

    private boolean checkToClose() {
        if (!InputConstants.isKeyDown(this.minecraft.getWindow().getWindow(), 292)) {
            this.switchToHoveredGameMode();
            this.minecraft.setScreen(null);
            return true;
        }
        return false;
    }

    @Override
    public boolean keyPressed(int n, int n2, int n3) {
        if (n == 293 && this.currentlyHovered.isPresent()) {
            this.setFirstMousePos = false;
            this.currentlyHovered = this.currentlyHovered.get().getNext();
            return true;
        }
        return super.keyPressed(n, n2, n3);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    public class GameModeSlot
    extends AbstractWidget {
        private final GameModeIcon icon;
        private boolean isSelected;

        public GameModeSlot(GameModeIcon gameModeIcon, int n, int n2) {
            super(n, n2, 25, 25, gameModeIcon.getName());
            this.icon = gameModeIcon;
        }

        @Override
        public void renderButton(PoseStack poseStack, int n, int n2, float f) {
            Minecraft minecraft = Minecraft.getInstance();
            this.drawSlot(poseStack, minecraft.getTextureManager());
            this.icon.drawIcon(GameModeSwitcherScreen.this.itemRenderer, this.x + 5, this.y + 5);
            if (this.isSelected) {
                this.drawSelection(poseStack, minecraft.getTextureManager());
            }
        }

        @Override
        public boolean isHovered() {
            return super.isHovered() || this.isSelected;
        }

        public void setSelected(boolean bl) {
            this.isSelected = bl;
            this.narrate();
        }

        private void drawSlot(PoseStack poseStack, TextureManager textureManager) {
            textureManager.bind(GAMEMODE_SWITCHER_LOCATION);
            poseStack.pushPose();
            poseStack.translate(this.x, this.y, 0.0);
            GameModeSlot.blit(poseStack, 0, 0, 0.0f, 75.0f, 25, 25, 128, 128);
            poseStack.popPose();
        }

        private void drawSelection(PoseStack poseStack, TextureManager textureManager) {
            textureManager.bind(GAMEMODE_SWITCHER_LOCATION);
            poseStack.pushPose();
            poseStack.translate(this.x, this.y, 0.0);
            GameModeSlot.blit(poseStack, 0, 0, 25.0f, 75.0f, 25, 25, 128, 128);
            poseStack.popPose();
        }
    }

    static enum GameModeIcon {
        CREATIVE(new TranslatableComponent("gameMode.creative"), "/gamemode creative", new ItemStack(Blocks.GRASS_BLOCK)),
        SURVIVAL(new TranslatableComponent("gameMode.survival"), "/gamemode survival", new ItemStack(Items.IRON_SWORD)),
        ADVENTURE(new TranslatableComponent("gameMode.adventure"), "/gamemode adventure", new ItemStack(Items.MAP)),
        SPECTATOR(new TranslatableComponent("gameMode.spectator"), "/gamemode spectator", new ItemStack(Items.ENDER_EYE));
        
        protected static final GameModeIcon[] VALUES;
        final Component name;
        final String command;
        final ItemStack renderStack;

        private GameModeIcon(Component component, String string2, ItemStack itemStack) {
            this.name = component;
            this.command = string2;
            this.renderStack = itemStack;
        }

        private void drawIcon(ItemRenderer itemRenderer, int n, int n2) {
            itemRenderer.renderAndDecorateItem(this.renderStack, n, n2);
        }

        private Component getName() {
            return this.name;
        }

        private String getCommand() {
            return this.command;
        }

        private Optional<GameModeIcon> getNext() {
            switch (this) {
                case CREATIVE: {
                    return Optional.of(SURVIVAL);
                }
                case SURVIVAL: {
                    return Optional.of(ADVENTURE);
                }
                case ADVENTURE: {
                    return Optional.of(SPECTATOR);
                }
            }
            return Optional.of(CREATIVE);
        }

        private static Optional<GameModeIcon> getFromGameType(GameType gameType) {
            switch (gameType) {
                case SPECTATOR: {
                    return Optional.of(SPECTATOR);
                }
                case SURVIVAL: {
                    return Optional.of(SURVIVAL);
                }
                case CREATIVE: {
                    return Optional.of(CREATIVE);
                }
                case ADVENTURE: {
                    return Optional.of(ADVENTURE);
                }
            }
            return Optional.empty();
        }

        static {
            VALUES = GameModeIcon.values();
        }
    }

}

