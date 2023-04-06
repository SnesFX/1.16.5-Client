/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.inventory;

import net.minecraft.core.Registry;
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
import net.minecraft.world.inventory.MerchantMenu;
import net.minecraft.world.inventory.ShulkerBoxMenu;
import net.minecraft.world.inventory.SmithingMenu;
import net.minecraft.world.inventory.SmokerMenu;
import net.minecraft.world.inventory.StonecutterMenu;

public class MenuType<T extends AbstractContainerMenu> {
    public static final MenuType<ChestMenu> GENERIC_9x1 = MenuType.register("generic_9x1", (arg_0, arg_1) -> ChestMenu.oneRow(arg_0, arg_1));
    public static final MenuType<ChestMenu> GENERIC_9x2 = MenuType.register("generic_9x2", (arg_0, arg_1) -> ChestMenu.twoRows(arg_0, arg_1));
    public static final MenuType<ChestMenu> GENERIC_9x3 = MenuType.register("generic_9x3", (arg_0, arg_1) -> ChestMenu.threeRows(arg_0, arg_1));
    public static final MenuType<ChestMenu> GENERIC_9x4 = MenuType.register("generic_9x4", (arg_0, arg_1) -> ChestMenu.fourRows(arg_0, arg_1));
    public static final MenuType<ChestMenu> GENERIC_9x5 = MenuType.register("generic_9x5", (arg_0, arg_1) -> ChestMenu.fiveRows(arg_0, arg_1));
    public static final MenuType<ChestMenu> GENERIC_9x6 = MenuType.register("generic_9x6", (arg_0, arg_1) -> ChestMenu.sixRows(arg_0, arg_1));
    public static final MenuType<DispenserMenu> GENERIC_3x3 = MenuType.register("generic_3x3", (arg_0, arg_1) -> DispenserMenu.new(arg_0, arg_1));
    public static final MenuType<AnvilMenu> ANVIL = MenuType.register("anvil", (arg_0, arg_1) -> AnvilMenu.new(arg_0, arg_1));
    public static final MenuType<BeaconMenu> BEACON = MenuType.register("beacon", (arg_0, arg_1) -> BeaconMenu.new(arg_0, arg_1));
    public static final MenuType<BlastFurnaceMenu> BLAST_FURNACE = MenuType.register("blast_furnace", (arg_0, arg_1) -> BlastFurnaceMenu.new(arg_0, arg_1));
    public static final MenuType<BrewingStandMenu> BREWING_STAND = MenuType.register("brewing_stand", (arg_0, arg_1) -> BrewingStandMenu.new(arg_0, arg_1));
    public static final MenuType<CraftingMenu> CRAFTING = MenuType.register("crafting", (arg_0, arg_1) -> CraftingMenu.new(arg_0, arg_1));
    public static final MenuType<EnchantmentMenu> ENCHANTMENT = MenuType.register("enchantment", (arg_0, arg_1) -> EnchantmentMenu.new(arg_0, arg_1));
    public static final MenuType<FurnaceMenu> FURNACE = MenuType.register("furnace", (arg_0, arg_1) -> FurnaceMenu.new(arg_0, arg_1));
    public static final MenuType<GrindstoneMenu> GRINDSTONE = MenuType.register("grindstone", (arg_0, arg_1) -> GrindstoneMenu.new(arg_0, arg_1));
    public static final MenuType<HopperMenu> HOPPER = MenuType.register("hopper", (arg_0, arg_1) -> HopperMenu.new(arg_0, arg_1));
    public static final MenuType<LecternMenu> LECTERN = MenuType.register("lectern", (n, inventory) -> new LecternMenu(n));
    public static final MenuType<LoomMenu> LOOM = MenuType.register("loom", (arg_0, arg_1) -> LoomMenu.new(arg_0, arg_1));
    public static final MenuType<MerchantMenu> MERCHANT = MenuType.register("merchant", (arg_0, arg_1) -> MerchantMenu.new(arg_0, arg_1));
    public static final MenuType<ShulkerBoxMenu> SHULKER_BOX = MenuType.register("shulker_box", (arg_0, arg_1) -> ShulkerBoxMenu.new(arg_0, arg_1));
    public static final MenuType<SmithingMenu> SMITHING = MenuType.register("smithing", (arg_0, arg_1) -> SmithingMenu.new(arg_0, arg_1));
    public static final MenuType<SmokerMenu> SMOKER = MenuType.register("smoker", (arg_0, arg_1) -> SmokerMenu.new(arg_0, arg_1));
    public static final MenuType<CartographyTableMenu> CARTOGRAPHY_TABLE = MenuType.register("cartography_table", (arg_0, arg_1) -> CartographyTableMenu.new(arg_0, arg_1));
    public static final MenuType<StonecutterMenu> STONECUTTER = MenuType.register("stonecutter", (arg_0, arg_1) -> StonecutterMenu.new(arg_0, arg_1));
    private final MenuSupplier<T> constructor;

    private static <T extends AbstractContainerMenu> MenuType<T> register(String string, MenuSupplier<T> menuSupplier) {
        return Registry.register(Registry.MENU, string, new MenuType<T>(menuSupplier));
    }

    private MenuType(MenuSupplier<T> menuSupplier) {
        this.constructor = menuSupplier;
    }

    public T create(int n, Inventory inventory) {
        return this.constructor.create(n, inventory);
    }

    static interface MenuSupplier<T extends AbstractContainerMenu> {
        public T create(int var1, Inventory var2);
    }

}

