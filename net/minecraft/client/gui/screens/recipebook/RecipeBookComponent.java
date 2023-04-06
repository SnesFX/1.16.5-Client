/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet
 *  it.unimi.dsi.fastutil.objects.ObjectSet
 *  javax.annotation.Nullable
 */
package net.minecraft.client.gui.screens.recipebook;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.ClientRecipeBook;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.RecipeBookCategories;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.StateSwitchingButton;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.recipebook.GhostRecipe;
import net.minecraft.client.gui.screens.recipebook.RecipeBookPage;
import net.minecraft.client.gui.screens.recipebook.RecipeBookTabButton;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.client.gui.screens.recipebook.RecipeShownListener;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.language.LanguageInfo;
import net.minecraft.client.resources.language.LanguageManager;
import net.minecraft.client.searchtree.MutableSearchTree;
import net.minecraft.client.searchtree.SearchRegistry;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundRecipeBookChangeSettingsPacket;
import net.minecraft.recipebook.PlaceRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.RecipeBook;
import net.minecraft.stats.RecipeBookSettings;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.inventory.RecipeBookType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;

public class RecipeBookComponent
extends GuiComponent
implements Widget,
GuiEventListener,
RecipeShownListener,
PlaceRecipe<Ingredient> {
    protected static final ResourceLocation RECIPE_BOOK_LOCATION = new ResourceLocation("textures/gui/recipe_book.png");
    private static final Component SEARCH_HINT = new TranslatableComponent("gui.recipebook.search_hint").withStyle(ChatFormatting.ITALIC).withStyle(ChatFormatting.GRAY);
    private static final Component ONLY_CRAFTABLES_TOOLTIP = new TranslatableComponent("gui.recipebook.toggleRecipes.craftable");
    private static final Component ALL_RECIPES_TOOLTIP = new TranslatableComponent("gui.recipebook.toggleRecipes.all");
    private int xOffset;
    private int width;
    private int height;
    protected final GhostRecipe ghostRecipe = new GhostRecipe();
    private final List<RecipeBookTabButton> tabButtons = Lists.newArrayList();
    private RecipeBookTabButton selectedTab;
    protected StateSwitchingButton filterButton;
    protected RecipeBookMenu<?> menu;
    protected Minecraft minecraft;
    private EditBox searchBox;
    private String lastSearch = "";
    private ClientRecipeBook book;
    private final RecipeBookPage recipeBookPage = new RecipeBookPage();
    private final StackedContents stackedContents = new StackedContents();
    private int timesInventoryChanged;
    private boolean ignoreTextInput;

    public void init(int n, int n2, Minecraft minecraft, boolean bl, RecipeBookMenu<?> recipeBookMenu) {
        this.minecraft = minecraft;
        this.width = n;
        this.height = n2;
        this.menu = recipeBookMenu;
        minecraft.player.containerMenu = recipeBookMenu;
        this.book = minecraft.player.getRecipeBook();
        this.timesInventoryChanged = minecraft.player.inventory.getTimesChanged();
        if (this.isVisible()) {
            this.initVisuals(bl);
        }
        minecraft.keyboardHandler.setSendRepeatsToGui(true);
    }

    public void initVisuals(boolean bl) {
        this.xOffset = bl ? 0 : 86;
        int n = (this.width - 147) / 2 - this.xOffset;
        int n2 = (this.height - 166) / 2;
        this.stackedContents.clear();
        this.minecraft.player.inventory.fillStackedContents(this.stackedContents);
        this.menu.fillCraftSlotsStackedContents(this.stackedContents);
        String string = this.searchBox != null ? this.searchBox.getValue() : "";
        this.minecraft.font.getClass();
        this.searchBox = new EditBox(this.minecraft.font, n + 25, n2 + 14, 80, 9 + 5, new TranslatableComponent("itemGroup.search"));
        this.searchBox.setMaxLength(50);
        this.searchBox.setBordered(false);
        this.searchBox.setVisible(true);
        this.searchBox.setTextColor(16777215);
        this.searchBox.setValue(string);
        this.recipeBookPage.init(this.minecraft, n, n2);
        this.recipeBookPage.addListener(this);
        this.filterButton = new StateSwitchingButton(n + 110, n2 + 12, 26, 16, this.book.isFiltering(this.menu));
        this.initFilterButtonTextures();
        this.tabButtons.clear();
        for (RecipeBookCategories recipeBookCategories : RecipeBookCategories.getCategories(this.menu.getRecipeBookType())) {
            this.tabButtons.add(new RecipeBookTabButton(recipeBookCategories));
        }
        if (this.selectedTab != null) {
            this.selectedTab = this.tabButtons.stream().filter(recipeBookTabButton -> recipeBookTabButton.getCategory().equals((Object)this.selectedTab.getCategory())).findFirst().orElse(null);
        }
        if (this.selectedTab == null) {
            this.selectedTab = this.tabButtons.get(0);
        }
        this.selectedTab.setStateTriggered(true);
        this.updateCollections(false);
        this.updateTabs();
    }

    @Override
    public boolean changeFocus(boolean bl) {
        return false;
    }

    protected void initFilterButtonTextures() {
        this.filterButton.initTextureValues(152, 41, 28, 18, RECIPE_BOOK_LOCATION);
    }

    public void removed() {
        this.searchBox = null;
        this.selectedTab = null;
        this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
    }

    public int updateScreenPosition(boolean bl, int n, int n2) {
        int n3 = this.isVisible() && !bl ? 177 + (n - n2 - 200) / 2 : (n - n2) / 2;
        return n3;
    }

    public void toggleVisibility() {
        this.setVisible(!this.isVisible());
    }

    public boolean isVisible() {
        return this.book.isOpen(this.menu.getRecipeBookType());
    }

    protected void setVisible(boolean bl) {
        this.book.setOpen(this.menu.getRecipeBookType(), bl);
        if (!bl) {
            this.recipeBookPage.setInvisible();
        }
        this.sendUpdateSettings();
    }

    public void slotClicked(@Nullable Slot slot) {
        if (slot != null && slot.index < this.menu.getSize()) {
            this.ghostRecipe.clear();
            if (this.isVisible()) {
                this.updateStackedContents();
            }
        }
    }

    private void updateCollections(boolean bl) {
        List<RecipeCollection> list = this.book.getCollection(this.selectedTab.getCategory());
        list.forEach(recipeCollection -> recipeCollection.canCraft(this.stackedContents, this.menu.getGridWidth(), this.menu.getGridHeight(), this.book));
        ArrayList arrayList = Lists.newArrayList(list);
        arrayList.removeIf(recipeCollection -> !recipeCollection.hasKnownRecipes());
        arrayList.removeIf(recipeCollection -> !recipeCollection.hasFitting());
        String string = this.searchBox.getValue();
        if (!string.isEmpty()) {
            ObjectLinkedOpenHashSet objectLinkedOpenHashSet = new ObjectLinkedOpenHashSet(this.minecraft.getSearchTree(SearchRegistry.RECIPE_COLLECTIONS).search(string.toLowerCase(Locale.ROOT)));
            arrayList.removeIf(arg_0 -> RecipeBookComponent.lambda$updateCollections$4((ObjectSet)objectLinkedOpenHashSet, arg_0));
        }
        if (this.book.isFiltering(this.menu)) {
            arrayList.removeIf(recipeCollection -> !recipeCollection.hasCraftable());
        }
        this.recipeBookPage.updateCollections(arrayList, bl);
    }

    private void updateTabs() {
        int n = (this.width - 147) / 2 - this.xOffset - 30;
        int n2 = (this.height - 166) / 2 + 3;
        int n3 = 27;
        int n4 = 0;
        for (RecipeBookTabButton recipeBookTabButton : this.tabButtons) {
            RecipeBookCategories recipeBookCategories = recipeBookTabButton.getCategory();
            if (recipeBookCategories == RecipeBookCategories.CRAFTING_SEARCH || recipeBookCategories == RecipeBookCategories.FURNACE_SEARCH) {
                recipeBookTabButton.visible = true;
                recipeBookTabButton.setPosition(n, n2 + 27 * n4++);
                continue;
            }
            if (!recipeBookTabButton.updateVisibility(this.book)) continue;
            recipeBookTabButton.setPosition(n, n2 + 27 * n4++);
            recipeBookTabButton.startAnimation(this.minecraft);
        }
    }

    public void tick() {
        if (!this.isVisible()) {
            return;
        }
        if (this.timesInventoryChanged != this.minecraft.player.inventory.getTimesChanged()) {
            this.updateStackedContents();
            this.timesInventoryChanged = this.minecraft.player.inventory.getTimesChanged();
        }
        this.searchBox.tick();
    }

    private void updateStackedContents() {
        this.stackedContents.clear();
        this.minecraft.player.inventory.fillStackedContents(this.stackedContents);
        this.menu.fillCraftSlotsStackedContents(this.stackedContents);
        this.updateCollections(false);
    }

    @Override
    public void render(PoseStack poseStack, int n, int n2, float f) {
        if (!this.isVisible()) {
            return;
        }
        RenderSystem.pushMatrix();
        RenderSystem.translatef(0.0f, 0.0f, 100.0f);
        this.minecraft.getTextureManager().bind(RECIPE_BOOK_LOCATION);
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        int n3 = (this.width - 147) / 2 - this.xOffset;
        int n4 = (this.height - 166) / 2;
        this.blit(poseStack, n3, n4, 1, 1, 147, 166);
        if (!this.searchBox.isFocused() && this.searchBox.getValue().isEmpty()) {
            RecipeBookComponent.drawString(poseStack, this.minecraft.font, SEARCH_HINT, n3 + 25, n4 + 14, -1);
        } else {
            this.searchBox.render(poseStack, n, n2, f);
        }
        for (RecipeBookTabButton recipeBookTabButton : this.tabButtons) {
            recipeBookTabButton.render(poseStack, n, n2, f);
        }
        this.filterButton.render(poseStack, n, n2, f);
        this.recipeBookPage.render(poseStack, n3, n4, n, n2, f);
        RenderSystem.popMatrix();
    }

    public void renderTooltip(PoseStack poseStack, int n, int n2, int n3, int n4) {
        if (!this.isVisible()) {
            return;
        }
        this.recipeBookPage.renderTooltip(poseStack, n3, n4);
        if (this.filterButton.isHovered()) {
            Component component = this.getFilterButtonTooltip();
            if (this.minecraft.screen != null) {
                this.minecraft.screen.renderTooltip(poseStack, component, n3, n4);
            }
        }
        this.renderGhostRecipeTooltip(poseStack, n, n2, n3, n4);
    }

    private Component getFilterButtonTooltip() {
        return this.filterButton.isStateTriggered() ? this.getRecipeFilterName() : ALL_RECIPES_TOOLTIP;
    }

    protected Component getRecipeFilterName() {
        return ONLY_CRAFTABLES_TOOLTIP;
    }

    private void renderGhostRecipeTooltip(PoseStack poseStack, int n, int n2, int n3, int n4) {
        ItemStack itemStack = null;
        for (int i = 0; i < this.ghostRecipe.size(); ++i) {
            GhostRecipe.GhostIngredient ghostIngredient = this.ghostRecipe.get(i);
            int n5 = ghostIngredient.getX() + n;
            int n6 = ghostIngredient.getY() + n2;
            if (n3 < n5 || n4 < n6 || n3 >= n5 + 16 || n4 >= n6 + 16) continue;
            itemStack = ghostIngredient.getItem();
        }
        if (itemStack != null && this.minecraft.screen != null) {
            this.minecraft.screen.renderComponentTooltip(poseStack, this.minecraft.screen.getTooltipFromItem(itemStack), n3, n4);
        }
    }

    public void renderGhostRecipe(PoseStack poseStack, int n, int n2, boolean bl, float f) {
        this.ghostRecipe.render(poseStack, this.minecraft, n, n2, bl, f);
    }

    @Override
    public boolean mouseClicked(double d, double d2, int n) {
        if (!this.isVisible() || this.minecraft.player.isSpectator()) {
            return false;
        }
        if (this.recipeBookPage.mouseClicked(d, d2, n, (this.width - 147) / 2 - this.xOffset, (this.height - 166) / 2, 147, 166)) {
            Recipe<?> recipe = this.recipeBookPage.getLastClickedRecipe();
            RecipeCollection recipeCollection = this.recipeBookPage.getLastClickedRecipeCollection();
            if (recipe != null && recipeCollection != null) {
                if (!recipeCollection.isCraftable(recipe) && this.ghostRecipe.getRecipe() == recipe) {
                    return false;
                }
                this.ghostRecipe.clear();
                this.minecraft.gameMode.handlePlaceRecipe(this.minecraft.player.containerMenu.containerId, recipe, Screen.hasShiftDown());
                if (!this.isOffsetNextToMainGUI()) {
                    this.setVisible(false);
                }
            }
            return true;
        }
        if (this.searchBox.mouseClicked(d, d2, n)) {
            return true;
        }
        if (this.filterButton.mouseClicked(d, d2, n)) {
            boolean bl = this.toggleFiltering();
            this.filterButton.setStateTriggered(bl);
            this.sendUpdateSettings();
            this.updateCollections(false);
            return true;
        }
        for (RecipeBookTabButton recipeBookTabButton : this.tabButtons) {
            if (!recipeBookTabButton.mouseClicked(d, d2, n)) continue;
            if (this.selectedTab != recipeBookTabButton) {
                this.selectedTab.setStateTriggered(false);
                this.selectedTab = recipeBookTabButton;
                this.selectedTab.setStateTriggered(true);
                this.updateCollections(true);
            }
            return true;
        }
        return false;
    }

    private boolean toggleFiltering() {
        RecipeBookType recipeBookType = this.menu.getRecipeBookType();
        boolean bl = !this.book.isFiltering(recipeBookType);
        this.book.setFiltering(recipeBookType, bl);
        return bl;
    }

    public boolean hasClickedOutside(double d, double d2, int n, int n2, int n3, int n4, int n5) {
        if (!this.isVisible()) {
            return true;
        }
        boolean bl = d < (double)n || d2 < (double)n2 || d >= (double)(n + n3) || d2 >= (double)(n2 + n4);
        boolean bl2 = (double)(n - 147) < d && d < (double)n && (double)n2 < d2 && d2 < (double)(n2 + n4);
        return bl && !bl2 && !this.selectedTab.isHovered();
    }

    @Override
    public boolean keyPressed(int n, int n2, int n3) {
        this.ignoreTextInput = false;
        if (!this.isVisible() || this.minecraft.player.isSpectator()) {
            return false;
        }
        if (n == 256 && !this.isOffsetNextToMainGUI()) {
            this.setVisible(false);
            return true;
        }
        if (this.searchBox.keyPressed(n, n2, n3)) {
            this.checkSearchStringUpdate();
            return true;
        }
        if (this.searchBox.isFocused() && this.searchBox.isVisible() && n != 256) {
            return true;
        }
        if (this.minecraft.options.keyChat.matches(n, n2) && !this.searchBox.isFocused()) {
            this.ignoreTextInput = true;
            this.searchBox.setFocus(true);
            return true;
        }
        return false;
    }

    @Override
    public boolean keyReleased(int n, int n2, int n3) {
        this.ignoreTextInput = false;
        return GuiEventListener.super.keyReleased(n, n2, n3);
    }

    @Override
    public boolean charTyped(char c, int n) {
        if (this.ignoreTextInput) {
            return false;
        }
        if (!this.isVisible() || this.minecraft.player.isSpectator()) {
            return false;
        }
        if (this.searchBox.charTyped(c, n)) {
            this.checkSearchStringUpdate();
            return true;
        }
        return GuiEventListener.super.charTyped(c, n);
    }

    @Override
    public boolean isMouseOver(double d, double d2) {
        return false;
    }

    private void checkSearchStringUpdate() {
        String string = this.searchBox.getValue().toLowerCase(Locale.ROOT);
        this.pirateSpeechForThePeople(string);
        if (!string.equals(this.lastSearch)) {
            this.updateCollections(false);
            this.lastSearch = string;
        }
    }

    private void pirateSpeechForThePeople(String string) {
        if ("excitedze".equals(string)) {
            LanguageManager languageManager = this.minecraft.getLanguageManager();
            LanguageInfo languageInfo = languageManager.getLanguage("en_pt");
            if (languageManager.getSelected().compareTo(languageInfo) == 0) {
                return;
            }
            languageManager.setSelected(languageInfo);
            this.minecraft.options.languageCode = languageInfo.getCode();
            this.minecraft.reloadResourcePacks();
            this.minecraft.options.save();
        }
    }

    private boolean isOffsetNextToMainGUI() {
        return this.xOffset == 86;
    }

    public void recipesUpdated() {
        this.updateTabs();
        if (this.isVisible()) {
            this.updateCollections(false);
        }
    }

    @Override
    public void recipesShown(List<Recipe<?>> list) {
        for (Recipe<?> recipe : list) {
            this.minecraft.player.removeRecipeHighlight(recipe);
        }
    }

    public void setupGhostRecipe(Recipe<?> recipe, List<Slot> list) {
        ItemStack itemStack = recipe.getResultItem();
        this.ghostRecipe.setRecipe(recipe);
        this.ghostRecipe.addIngredient(Ingredient.of(itemStack), list.get((int)0).x, list.get((int)0).y);
        this.placeRecipe(this.menu.getGridWidth(), this.menu.getGridHeight(), this.menu.getResultSlotIndex(), recipe, recipe.getIngredients().iterator(), 0);
    }

    @Override
    public void addItemToSlot(Iterator<Ingredient> iterator, int n, int n2, int n3, int n4) {
        Ingredient ingredient = iterator.next();
        if (!ingredient.isEmpty()) {
            Slot slot = (Slot)this.menu.slots.get(n);
            this.ghostRecipe.addIngredient(ingredient, slot.x, slot.y);
        }
    }

    protected void sendUpdateSettings() {
        if (this.minecraft.getConnection() != null) {
            RecipeBookType recipeBookType = this.menu.getRecipeBookType();
            boolean bl = this.book.getBookSettings().isOpen(recipeBookType);
            boolean bl2 = this.book.getBookSettings().isFiltering(recipeBookType);
            this.minecraft.getConnection().send(new ServerboundRecipeBookChangeSettingsPacket(recipeBookType, bl, bl2));
        }
    }

    private static /* synthetic */ boolean lambda$updateCollections$4(ObjectSet objectSet, RecipeCollection recipeCollection) {
        return !objectSet.contains((Object)recipeCollection);
    }
}

