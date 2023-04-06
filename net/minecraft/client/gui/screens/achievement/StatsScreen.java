/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Sets
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  javax.annotation.Nullable
 */
package net.minecraft.client.gui.screens.achievement;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.achievement.StatsUpdateListener;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundClientCommandPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stat;
import net.minecraft.stats.StatType;
import net.minecraft.stats.Stats;
import net.minecraft.stats.StatsCounter;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;

public class StatsScreen
extends Screen
implements StatsUpdateListener {
    private static final Component PENDING_TEXT = new TranslatableComponent("multiplayer.downloadingStats");
    protected final Screen lastScreen;
    private GeneralStatisticsList statsList;
    private ItemStatisticsList itemStatsList;
    private MobsStatisticsList mobsStatsList;
    private final StatsCounter stats;
    @Nullable
    private ObjectSelectionList<?> activeList;
    private boolean isLoading = true;

    public StatsScreen(Screen screen, StatsCounter statsCounter) {
        super(new TranslatableComponent("gui.stats"));
        this.lastScreen = screen;
        this.stats = statsCounter;
    }

    @Override
    protected void init() {
        this.isLoading = true;
        this.minecraft.getConnection().send(new ServerboundClientCommandPacket(ServerboundClientCommandPacket.Action.REQUEST_STATS));
    }

    public void initLists() {
        this.statsList = new GeneralStatisticsList(this.minecraft);
        this.itemStatsList = new ItemStatisticsList(this.minecraft);
        this.mobsStatsList = new MobsStatisticsList(this.minecraft);
    }

    public void initButtons() {
        this.addButton(new Button(this.width / 2 - 120, this.height - 52, 80, 20, new TranslatableComponent("stat.generalButton"), button -> this.setActiveList(this.statsList)));
        Button button2 = this.addButton(new Button(this.width / 2 - 40, this.height - 52, 80, 20, new TranslatableComponent("stat.itemsButton"), button -> this.setActiveList(this.itemStatsList)));
        Button button3 = this.addButton(new Button(this.width / 2 + 40, this.height - 52, 80, 20, new TranslatableComponent("stat.mobsButton"), button -> this.setActiveList(this.mobsStatsList)));
        this.addButton(new Button(this.width / 2 - 100, this.height - 28, 200, 20, CommonComponents.GUI_DONE, button -> this.minecraft.setScreen(this.lastScreen)));
        if (this.itemStatsList.children().isEmpty()) {
            button2.active = false;
        }
        if (this.mobsStatsList.children().isEmpty()) {
            button3.active = false;
        }
    }

    @Override
    public void render(PoseStack poseStack, int n, int n2, float f) {
        if (this.isLoading) {
            this.renderBackground(poseStack);
            StatsScreen.drawCenteredString(poseStack, this.font, PENDING_TEXT, this.width / 2, this.height / 2, 16777215);
            this.font.getClass();
            StatsScreen.drawCenteredString(poseStack, this.font, LOADING_SYMBOLS[(int)(Util.getMillis() / 150L % (long)LOADING_SYMBOLS.length)], this.width / 2, this.height / 2 + 9 * 2, 16777215);
        } else {
            this.getActiveList().render(poseStack, n, n2, f);
            StatsScreen.drawCenteredString(poseStack, this.font, this.title, this.width / 2, 20, 16777215);
            super.render(poseStack, n, n2, f);
        }
    }

    @Override
    public void onStatsUpdated() {
        if (this.isLoading) {
            this.initLists();
            this.initButtons();
            this.setActiveList(this.statsList);
            this.isLoading = false;
        }
    }

    @Override
    public boolean isPauseScreen() {
        return !this.isLoading;
    }

    @Nullable
    public ObjectSelectionList<?> getActiveList() {
        return this.activeList;
    }

    public void setActiveList(@Nullable ObjectSelectionList<?> objectSelectionList) {
        this.children.remove(this.statsList);
        this.children.remove(this.itemStatsList);
        this.children.remove(this.mobsStatsList);
        if (objectSelectionList != null) {
            this.children.add(0, objectSelectionList);
            this.activeList = objectSelectionList;
        }
    }

    private static String getTranslationKey(Stat<ResourceLocation> stat) {
        return "stat." + stat.getValue().toString().replace(':', '.');
    }

    private int getColumnX(int n) {
        return 115 + 40 * n;
    }

    private void blitSlot(PoseStack poseStack, int n, int n2, Item item) {
        this.blitSlotIcon(poseStack, n + 1, n2 + 1, 0, 0);
        RenderSystem.enableRescaleNormal();
        this.itemRenderer.renderGuiItem(item.getDefaultInstance(), n + 2, n2 + 2);
        RenderSystem.disableRescaleNormal();
    }

    private void blitSlotIcon(PoseStack poseStack, int n, int n2, int n3, int n4) {
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.minecraft.getTextureManager().bind(STATS_ICON_LOCATION);
        StatsScreen.blit(poseStack, n, n2, this.getBlitOffset(), n3, n4, 18, 18, 128, 128);
    }

    static /* synthetic */ ItemStatisticsList access$1200(StatsScreen statsScreen) {
        return statsScreen.itemStatsList;
    }

    class MobsStatisticsList
    extends ObjectSelectionList<MobRow> {
        public MobsStatisticsList(Minecraft minecraft) {
            StatsScreen.this.font.getClass();
            super(minecraft, StatsScreen.this.width, StatsScreen.this.height, 32, StatsScreen.this.height - 64, 9 * 4);
            for (EntityType entityType : Registry.ENTITY_TYPE) {
                if (StatsScreen.this.stats.getValue(Stats.ENTITY_KILLED.get(entityType)) <= 0 && StatsScreen.this.stats.getValue(Stats.ENTITY_KILLED_BY.get(entityType)) <= 0) continue;
                this.addEntry(new MobRow(entityType));
            }
        }

        @Override
        protected void renderBackground(PoseStack poseStack) {
            StatsScreen.this.renderBackground(poseStack);
        }

        class MobRow
        extends ObjectSelectionList.Entry<MobRow> {
            private final EntityType<?> type;
            private final Component mobName;
            private final Component kills;
            private final boolean hasKills;
            private final Component killedBy;
            private final boolean wasKilledBy;

            public MobRow(EntityType<?> entityType) {
                this.type = entityType;
                this.mobName = entityType.getDescription();
                int n = StatsScreen.this.stats.getValue(Stats.ENTITY_KILLED.get(entityType));
                if (n == 0) {
                    this.kills = new TranslatableComponent("stat_type.minecraft.killed.none", this.mobName);
                    this.hasKills = false;
                } else {
                    this.kills = new TranslatableComponent("stat_type.minecraft.killed", n, this.mobName);
                    this.hasKills = true;
                }
                int n2 = StatsScreen.this.stats.getValue(Stats.ENTITY_KILLED_BY.get(entityType));
                if (n2 == 0) {
                    this.killedBy = new TranslatableComponent("stat_type.minecraft.killed_by.none", this.mobName);
                    this.wasKilledBy = false;
                } else {
                    this.killedBy = new TranslatableComponent("stat_type.minecraft.killed_by", this.mobName, n2);
                    this.wasKilledBy = true;
                }
            }

            @Override
            public void render(PoseStack poseStack, int n, int n2, int n3, int n4, int n5, int n6, int n7, boolean bl, float f) {
                GuiComponent.drawString(poseStack, StatsScreen.this.font, this.mobName, n3 + 2, n2 + 1, 16777215);
                StatsScreen.this.font.getClass();
                GuiComponent.drawString(poseStack, StatsScreen.this.font, this.kills, n3 + 2 + 10, n2 + 1 + 9, this.hasKills ? 9474192 : 6316128);
                StatsScreen.this.font.getClass();
                GuiComponent.drawString(poseStack, StatsScreen.this.font, this.killedBy, n3 + 2 + 10, n2 + 1 + 9 * 2, this.wasKilledBy ? 9474192 : 6316128);
            }
        }

    }

    class ItemStatisticsList
    extends ObjectSelectionList<ItemRow> {
        protected final List<StatType<Block>> blockColumns;
        protected final List<StatType<Item>> itemColumns;
        private final int[] iconOffsets;
        protected int headerPressed;
        protected final List<Item> statItemList;
        protected final Comparator<Item> itemStatSorter;
        @Nullable
        protected StatType<?> sortColumn;
        protected int sortOrder;

        public ItemStatisticsList(Minecraft minecraft) {
            boolean bl;
            super(minecraft, StatsScreen.this.width, StatsScreen.this.height, 32, StatsScreen.this.height - 64, 20);
            this.iconOffsets = new int[]{3, 4, 1, 2, 5, 6};
            this.headerPressed = -1;
            this.itemStatSorter = new ItemComparator();
            this.blockColumns = Lists.newArrayList();
            this.blockColumns.add(Stats.BLOCK_MINED);
            this.itemColumns = Lists.newArrayList((Object[])new StatType[]{Stats.ITEM_BROKEN, Stats.ITEM_CRAFTED, Stats.ITEM_USED, Stats.ITEM_PICKED_UP, Stats.ITEM_DROPPED});
            this.setRenderHeader(true, 20);
            Set set = Sets.newIdentityHashSet();
            for (Item itemLike : Registry.ITEM) {
                bl = false;
                for (StatType<Item> statType : this.itemColumns) {
                    if (!statType.contains(itemLike) || StatsScreen.this.stats.getValue(statType.get(itemLike)) <= 0) continue;
                    bl = true;
                }
                if (!bl) continue;
                set.add(itemLike);
            }
            for (Block block : Registry.BLOCK) {
                bl = false;
                for (StatType<ItemLike> statType : this.blockColumns) {
                    if (!statType.contains(block) || StatsScreen.this.stats.getValue(statType.get(block)) <= 0) continue;
                    bl = true;
                }
                if (!bl) continue;
                set.add(block.asItem());
            }
            set.remove(Items.AIR);
            this.statItemList = Lists.newArrayList((Iterable)set);
            for (int i = 0; i < this.statItemList.size(); ++i) {
                this.addEntry(new ItemRow());
            }
        }

        @Override
        protected void renderHeader(PoseStack poseStack, int n, int n2, Tesselator tesselator) {
            int n3;
            int n4;
            if (!this.minecraft.mouseHandler.isLeftPressed()) {
                this.headerPressed = -1;
            }
            for (n4 = 0; n4 < this.iconOffsets.length; ++n4) {
                StatsScreen.this.blitSlotIcon(poseStack, n + StatsScreen.this.getColumnX(n4) - 18, n2 + 1, 0, this.headerPressed == n4 ? 0 : 18);
            }
            if (this.sortColumn != null) {
                n4 = StatsScreen.this.getColumnX(this.getColumnIndex(this.sortColumn)) - 36;
                n3 = this.sortOrder == 1 ? 2 : 1;
                StatsScreen.this.blitSlotIcon(poseStack, n + n4, n2 + 1, 18 * n3, 0);
            }
            for (n4 = 0; n4 < this.iconOffsets.length; ++n4) {
                n3 = this.headerPressed == n4 ? 1 : 0;
                StatsScreen.this.blitSlotIcon(poseStack, n + StatsScreen.this.getColumnX(n4) - 18 + n3, n2 + 1 + n3, 18 * this.iconOffsets[n4], 18);
            }
        }

        @Override
        public int getRowWidth() {
            return 375;
        }

        @Override
        protected int getScrollbarPosition() {
            return this.width / 2 + 140;
        }

        @Override
        protected void renderBackground(PoseStack poseStack) {
            StatsScreen.this.renderBackground(poseStack);
        }

        @Override
        protected void clickedHeader(int n, int n2) {
            this.headerPressed = -1;
            for (int i = 0; i < this.iconOffsets.length; ++i) {
                int n3 = n - StatsScreen.this.getColumnX(i);
                if (n3 < -36 || n3 > 0) continue;
                this.headerPressed = i;
                break;
            }
            if (this.headerPressed >= 0) {
                this.sortByColumn(this.getColumn(this.headerPressed));
                this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0f));
            }
        }

        private StatType<?> getColumn(int n) {
            return n < this.blockColumns.size() ? this.blockColumns.get(n) : this.itemColumns.get(n - this.blockColumns.size());
        }

        private int getColumnIndex(StatType<?> statType) {
            int n = this.blockColumns.indexOf(statType);
            if (n >= 0) {
                return n;
            }
            int n2 = this.itemColumns.indexOf(statType);
            if (n2 >= 0) {
                return n2 + this.blockColumns.size();
            }
            return -1;
        }

        @Override
        protected void renderDecorations(PoseStack poseStack, int n, int n2) {
            if (n2 < this.y0 || n2 > this.y1) {
                return;
            }
            ItemRow itemRow = (ItemRow)this.getEntryAtPosition(n, n2);
            int n3 = (this.width - this.getRowWidth()) / 2;
            if (itemRow != null) {
                if (n < n3 + 40 || n > n3 + 40 + 20) {
                    return;
                }
                Item item = this.statItemList.get(this.children().indexOf(itemRow));
                this.renderMousehoverTooltip(poseStack, this.getString(item), n, n2);
            } else {
                Component component = null;
                int n4 = n - n3;
                for (int i = 0; i < this.iconOffsets.length; ++i) {
                    int n5 = StatsScreen.this.getColumnX(i);
                    if (n4 < n5 - 18 || n4 > n5) continue;
                    component = this.getColumn(i).getDisplayName();
                    break;
                }
                this.renderMousehoverTooltip(poseStack, component, n, n2);
            }
        }

        protected void renderMousehoverTooltip(PoseStack poseStack, @Nullable Component component, int n, int n2) {
            if (component == null) {
                return;
            }
            int n3 = n + 12;
            int n4 = n2 - 12;
            int n5 = StatsScreen.this.font.width(component);
            this.fillGradient(poseStack, n3 - 3, n4 - 3, n3 + n5 + 3, n4 + 8 + 3, -1073741824, -1073741824);
            RenderSystem.pushMatrix();
            RenderSystem.translatef(0.0f, 0.0f, 400.0f);
            StatsScreen.this.font.drawShadow(poseStack, component, (float)n3, (float)n4, -1);
            RenderSystem.popMatrix();
        }

        protected Component getString(Item item) {
            return item.getDescription();
        }

        protected void sortByColumn(StatType<?> statType) {
            if (statType != this.sortColumn) {
                this.sortColumn = statType;
                this.sortOrder = -1;
            } else if (this.sortOrder == -1) {
                this.sortOrder = 1;
            } else {
                this.sortColumn = null;
                this.sortOrder = 0;
            }
            this.statItemList.sort(this.itemStatSorter);
        }

        class ItemRow
        extends ObjectSelectionList.Entry<ItemRow> {
            private ItemRow() {
            }

            @Override
            public void render(PoseStack poseStack, int n, int n2, int n3, int n4, int n5, int n6, int n7, boolean bl, float f) {
                int n8;
                Item item = StatsScreen.access$1200((StatsScreen)StatsScreen.this).statItemList.get(n);
                StatsScreen.this.blitSlot(poseStack, n3 + 40, n2, item);
                for (n8 = 0; n8 < StatsScreen.access$1200((StatsScreen)StatsScreen.this).blockColumns.size(); ++n8) {
                    Stat<Block> stat = item instanceof BlockItem ? StatsScreen.access$1200((StatsScreen)StatsScreen.this).blockColumns.get(n8).get(((BlockItem)item).getBlock()) : null;
                    this.renderStat(poseStack, stat, n3 + StatsScreen.this.getColumnX(n8), n2, n % 2 == 0);
                }
                for (n8 = 0; n8 < StatsScreen.access$1200((StatsScreen)StatsScreen.this).itemColumns.size(); ++n8) {
                    this.renderStat(poseStack, StatsScreen.access$1200((StatsScreen)StatsScreen.this).itemColumns.get(n8).get(item), n3 + StatsScreen.this.getColumnX(n8 + StatsScreen.access$1200((StatsScreen)StatsScreen.this).blockColumns.size()), n2, n % 2 == 0);
                }
            }

            protected void renderStat(PoseStack poseStack, @Nullable Stat<?> stat, int n, int n2, boolean bl) {
                String string = stat == null ? "-" : stat.format(StatsScreen.this.stats.getValue(stat));
                GuiComponent.drawString(poseStack, StatsScreen.this.font, string, n - StatsScreen.this.font.width(string), n2 + 5, bl ? 16777215 : 9474192);
            }
        }

        class ItemComparator
        implements Comparator<Item> {
            private ItemComparator() {
            }

            @Override
            public int compare(Item item, Item item2) {
                int n;
                int n2;
                if (ItemStatisticsList.this.sortColumn == null) {
                    n = 0;
                    n2 = 0;
                } else if (ItemStatisticsList.this.blockColumns.contains(ItemStatisticsList.this.sortColumn)) {
                    StatType<?> statType = ItemStatisticsList.this.sortColumn;
                    n = item instanceof BlockItem ? StatsScreen.this.stats.getValue(statType, ((BlockItem)item).getBlock()) : -1;
                    n2 = item2 instanceof BlockItem ? StatsScreen.this.stats.getValue(statType, ((BlockItem)item2).getBlock()) : -1;
                } else {
                    StatType<?> statType = ItemStatisticsList.this.sortColumn;
                    n = StatsScreen.this.stats.getValue(statType, item);
                    n2 = StatsScreen.this.stats.getValue(statType, item2);
                }
                if (n == n2) {
                    return ItemStatisticsList.this.sortOrder * Integer.compare(Item.getId(item), Item.getId(item2));
                }
                return ItemStatisticsList.this.sortOrder * Integer.compare(n, n2);
            }

            @Override
            public /* synthetic */ int compare(Object object, Object object2) {
                return this.compare((Item)object, (Item)object2);
            }
        }

    }

    class GeneralStatisticsList
    extends ObjectSelectionList<Entry> {
        public GeneralStatisticsList(Minecraft minecraft) {
            super(minecraft, StatsScreen.this.width, StatsScreen.this.height, 32, StatsScreen.this.height - 64, 10);
            ObjectArrayList objectArrayList = new ObjectArrayList(Stats.CUSTOM.iterator());
            objectArrayList.sort(Comparator.comparing(stat -> I18n.get(StatsScreen.getTranslationKey(stat), new Object[0])));
            for (Stat stat2 : objectArrayList) {
                this.addEntry(new Entry(stat2));
            }
        }

        @Override
        protected void renderBackground(PoseStack poseStack) {
            StatsScreen.this.renderBackground(poseStack);
        }

        class Entry
        extends ObjectSelectionList.Entry<Entry> {
            private final Stat<ResourceLocation> stat;
            private final Component statDisplay;

            private Entry(Stat<ResourceLocation> stat) {
                this.stat = stat;
                this.statDisplay = new TranslatableComponent(StatsScreen.getTranslationKey(stat));
            }

            @Override
            public void render(PoseStack poseStack, int n, int n2, int n3, int n4, int n5, int n6, int n7, boolean bl, float f) {
                GuiComponent.drawString(poseStack, StatsScreen.this.font, this.statDisplay, n3 + 2, n2 + 1, n % 2 == 0 ? 16777215 : 9474192);
                String string = this.stat.format(StatsScreen.this.stats.getValue(this.stat));
                GuiComponent.drawString(poseStack, StatsScreen.this.font, string, n3 + 2 + 213 - StatsScreen.this.font.width(string), n2 + 1, n % 2 == 0 ? 16777215 : 9474192);
            }
        }

    }

}

