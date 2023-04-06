/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  javax.annotation.Nullable
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.client.gui.screens;

import com.google.common.collect.Maps;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AnvilScreen;
import net.minecraft.client.gui.screens.inventory.BeaconScreen;
import net.minecraft.client.gui.screens.inventory.BlastFurnaceScreen;
import net.minecraft.client.gui.screens.inventory.BrewingStandScreen;
import net.minecraft.client.gui.screens.inventory.CartographyTableScreen;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.client.gui.screens.inventory.CraftingScreen;
import net.minecraft.client.gui.screens.inventory.DispenserScreen;
import net.minecraft.client.gui.screens.inventory.EnchantmentScreen;
import net.minecraft.client.gui.screens.inventory.FurnaceScreen;
import net.minecraft.client.gui.screens.inventory.GrindstoneScreen;
import net.minecraft.client.gui.screens.inventory.HopperScreen;
import net.minecraft.client.gui.screens.inventory.LecternScreen;
import net.minecraft.client.gui.screens.inventory.LoomScreen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.gui.screens.inventory.MerchantScreen;
import net.minecraft.client.gui.screens.inventory.ShulkerBoxScreen;
import net.minecraft.client.gui.screens.inventory.SmithingScreen;
import net.minecraft.client.gui.screens.inventory.SmokerScreen;
import net.minecraft.client.gui.screens.inventory.StonecutterScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.BeaconMenu;
import net.minecraft.world.inventory.BlastFurnaceMenu;
import net.minecraft.world.inventory.BrewingStandMenu;
import net.minecraft.world.inventory.CartographyTableMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.DispenserMenu;
import net.minecraft.world.inventory.EnchantmentMenu;
import net.minecraft.world.inventory.FurnaceMenu;
import net.minecraft.world.inventory.GrindstoneMenu;
import net.minecraft.world.inventory.HopperMenu;
import net.minecraft.world.inventory.LecternMenu;
import net.minecraft.world.inventory.LoomMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.MerchantMenu;
import net.minecraft.world.inventory.ShulkerBoxMenu;
import net.minecraft.world.inventory.SmithingMenu;
import net.minecraft.world.inventory.SmokerMenu;
import net.minecraft.world.inventory.StonecutterMenu;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MenuScreens {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Map<MenuType<?>, ScreenConstructor<?, ?>> SCREENS = Maps.newHashMap();

    public static <T extends AbstractContainerMenu> void create(@Nullable MenuType<T> menuType, Minecraft minecraft, int n, Component component) {
        if (menuType == null) {
            LOGGER.warn("Trying to open invalid screen with name: {}", (Object)component.getString());
            return;
        }
        ScreenConstructor<T, ?> screenConstructor = MenuScreens.getConstructor(menuType);
        if (screenConstructor == null) {
            LOGGER.warn("Failed to create screen for menu type: {}", (Object)Registry.MENU.getKey(menuType));
            return;
        }
        screenConstructor.fromPacket(component, menuType, minecraft, n);
    }

    @Nullable
    private static <T extends AbstractContainerMenu> ScreenConstructor<T, ?> getConstructor(MenuType<T> menuType) {
        return SCREENS.get(menuType);
    }

    private static <M extends AbstractContainerMenu, U extends Screen> void register(MenuType<? extends M> menuType, ScreenConstructor<M, U> screenConstructor) {
        ScreenConstructor<M, U> screenConstructor2 = SCREENS.put(menuType, screenConstructor);
        if (screenConstructor2 != null) {
            throw new IllegalStateException("Duplicate registration for " + Registry.MENU.getKey(menuType));
        }
    }

    public static boolean selfTest() {
        boolean bl = false;
        for (MenuType menuType : Registry.MENU) {
            if (SCREENS.containsKey(menuType)) continue;
            LOGGER.debug("Menu {} has no matching screen", (Object)Registry.MENU.getKey(menuType));
            bl = true;
        }
        return bl;
    }

    static {
        MenuScreens.register(MenuType.GENERIC_9x1, (arg_0, arg_1, arg_2) -> ContainerScreen.new(arg_0, arg_1, arg_2));
        MenuScreens.register(MenuType.GENERIC_9x2, (arg_0, arg_1, arg_2) -> ContainerScreen.new(arg_0, arg_1, arg_2));
        MenuScreens.register(MenuType.GENERIC_9x3, (arg_0, arg_1, arg_2) -> ContainerScreen.new(arg_0, arg_1, arg_2));
        MenuScreens.register(MenuType.GENERIC_9x4, (arg_0, arg_1, arg_2) -> ContainerScreen.new(arg_0, arg_1, arg_2));
        MenuScreens.register(MenuType.GENERIC_9x5, (arg_0, arg_1, arg_2) -> ContainerScreen.new(arg_0, arg_1, arg_2));
        MenuScreens.register(MenuType.GENERIC_9x6, (arg_0, arg_1, arg_2) -> ContainerScreen.new(arg_0, arg_1, arg_2));
        MenuScreens.register(MenuType.GENERIC_3x3, (arg_0, arg_1, arg_2) -> DispenserScreen.new(arg_0, arg_1, arg_2));
        MenuScreens.register(MenuType.ANVIL, (arg_0, arg_1, arg_2) -> AnvilScreen.new(arg_0, arg_1, arg_2));
        MenuScreens.register(MenuType.BEACON, (arg_0, arg_1, arg_2) -> BeaconScreen.new(arg_0, arg_1, arg_2));
        MenuScreens.register(MenuType.BLAST_FURNACE, (arg_0, arg_1, arg_2) -> BlastFurnaceScreen.new(arg_0, arg_1, arg_2));
        MenuScreens.register(MenuType.BREWING_STAND, (arg_0, arg_1, arg_2) -> BrewingStandScreen.new(arg_0, arg_1, arg_2));
        MenuScreens.register(MenuType.CRAFTING, (arg_0, arg_1, arg_2) -> CraftingScreen.new(arg_0, arg_1, arg_2));
        MenuScreens.register(MenuType.ENCHANTMENT, (arg_0, arg_1, arg_2) -> EnchantmentScreen.new(arg_0, arg_1, arg_2));
        MenuScreens.register(MenuType.FURNACE, (arg_0, arg_1, arg_2) -> FurnaceScreen.new(arg_0, arg_1, arg_2));
        MenuScreens.register(MenuType.GRINDSTONE, (arg_0, arg_1, arg_2) -> GrindstoneScreen.new(arg_0, arg_1, arg_2));
        MenuScreens.register(MenuType.HOPPER, (arg_0, arg_1, arg_2) -> HopperScreen.new(arg_0, arg_1, arg_2));
        MenuScreens.register(MenuType.LECTERN, (arg_0, arg_1, arg_2) -> LecternScreen.new(arg_0, arg_1, arg_2));
        MenuScreens.register(MenuType.LOOM, (arg_0, arg_1, arg_2) -> LoomScreen.new(arg_0, arg_1, arg_2));
        MenuScreens.register(MenuType.MERCHANT, (arg_0, arg_1, arg_2) -> MerchantScreen.new(arg_0, arg_1, arg_2));
        MenuScreens.register(MenuType.SHULKER_BOX, (arg_0, arg_1, arg_2) -> ShulkerBoxScreen.new(arg_0, arg_1, arg_2));
        MenuScreens.register(MenuType.SMITHING, (arg_0, arg_1, arg_2) -> SmithingScreen.new(arg_0, arg_1, arg_2));
        MenuScreens.register(MenuType.SMOKER, (arg_0, arg_1, arg_2) -> SmokerScreen.new(arg_0, arg_1, arg_2));
        MenuScreens.register(MenuType.CARTOGRAPHY_TABLE, (arg_0, arg_1, arg_2) -> CartographyTableScreen.new(arg_0, arg_1, arg_2));
        MenuScreens.register(MenuType.STONECUTTER, (arg_0, arg_1, arg_2) -> StonecutterScreen.new(arg_0, arg_1, arg_2));
    }

    static interface ScreenConstructor<T extends AbstractContainerMenu, U extends Screen> {
        default public void fromPacket(Component component, MenuType<T> menuType, Minecraft minecraft, int n) {
            U u = this.create(menuType.create(n, minecraft.player.inventory), minecraft.player.inventory, component);
            minecraft.player.containerMenu = ((MenuAccess)u).getMenu();
            minecraft.setScreen((Screen)u);
        }

        public U create(T var1, Inventory var2, Component var3);
    }

}

