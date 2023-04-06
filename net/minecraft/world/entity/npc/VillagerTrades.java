/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
 *  javax.annotation.Nullable
 */
package net.minecraft.world.entity.npc;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.npc.VillagerDataHolder;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerType;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.DyeableArmorItem;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.item.SuspiciousStewItem;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.saveddata.maps.MapDecoration;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;

public class VillagerTrades {
    public static final Map<VillagerProfession, Int2ObjectMap<ItemListing[]>> TRADES = Util.make(Maps.newHashMap(), hashMap -> {
        hashMap.put(VillagerProfession.FARMER, VillagerTrades.toIntMap((ImmutableMap<Integer, ItemListing[]>)ImmutableMap.of((Object)1, (Object)new ItemListing[]{new EmeraldForItems(Items.WHEAT, 20, 16, 2), new EmeraldForItems(Items.POTATO, 26, 16, 2), new EmeraldForItems(Items.CARROT, 22, 16, 2), new EmeraldForItems(Items.BEETROOT, 15, 16, 2), new ItemsForEmeralds(Items.BREAD, 1, 6, 16, 1)}, (Object)2, (Object)new ItemListing[]{new EmeraldForItems(Blocks.PUMPKIN, 6, 12, 10), new ItemsForEmeralds(Items.PUMPKIN_PIE, 1, 4, 5), new ItemsForEmeralds(Items.APPLE, 1, 4, 16, 5)}, (Object)3, (Object)new ItemListing[]{new ItemsForEmeralds(Items.COOKIE, 3, 18, 10), new EmeraldForItems(Blocks.MELON, 4, 12, 20)}, (Object)4, (Object)new ItemListing[]{new ItemsForEmeralds(Blocks.CAKE, 1, 1, 12, 15), new SuspisciousStewForEmerald(MobEffects.NIGHT_VISION, 100, 15), new SuspisciousStewForEmerald(MobEffects.JUMP, 160, 15), new SuspisciousStewForEmerald(MobEffects.WEAKNESS, 140, 15), new SuspisciousStewForEmerald(MobEffects.BLINDNESS, 120, 15), new SuspisciousStewForEmerald(MobEffects.POISON, 280, 15), new SuspisciousStewForEmerald(MobEffects.SATURATION, 7, 15)}, (Object)5, (Object)new ItemListing[]{new ItemsForEmeralds(Items.GOLDEN_CARROT, 3, 3, 30), new ItemsForEmeralds(Items.GLISTERING_MELON_SLICE, 4, 3, 30)})));
        hashMap.put(VillagerProfession.FISHERMAN, VillagerTrades.toIntMap((ImmutableMap<Integer, ItemListing[]>)ImmutableMap.of((Object)1, (Object)new ItemListing[]{new EmeraldForItems(Items.STRING, 20, 16, 2), new EmeraldForItems(Items.COAL, 10, 16, 2), new ItemsAndEmeraldsToItems(Items.COD, 6, Items.COOKED_COD, 6, 16, 1), new ItemsForEmeralds(Items.COD_BUCKET, 3, 1, 16, 1)}, (Object)2, (Object)new ItemListing[]{new EmeraldForItems(Items.COD, 15, 16, 10), new ItemsAndEmeraldsToItems(Items.SALMON, 6, Items.COOKED_SALMON, 6, 16, 5), new ItemsForEmeralds(Items.CAMPFIRE, 2, 1, 5)}, (Object)3, (Object)new ItemListing[]{new EmeraldForItems(Items.SALMON, 13, 16, 20), new EnchantedItemForEmeralds(Items.FISHING_ROD, 3, 3, 10, 0.2f)}, (Object)4, (Object)new ItemListing[]{new EmeraldForItems(Items.TROPICAL_FISH, 6, 12, 30)}, (Object)5, (Object)new ItemListing[]{new EmeraldForItems(Items.PUFFERFISH, 4, 12, 30), new EmeraldsForVillagerTypeItem(1, 12, 30, (Map<VillagerType, Item>)ImmutableMap.builder().put((Object)VillagerType.PLAINS, (Object)Items.OAK_BOAT).put((Object)VillagerType.TAIGA, (Object)Items.SPRUCE_BOAT).put((Object)VillagerType.SNOW, (Object)Items.SPRUCE_BOAT).put((Object)VillagerType.DESERT, (Object)Items.JUNGLE_BOAT).put((Object)VillagerType.JUNGLE, (Object)Items.JUNGLE_BOAT).put((Object)VillagerType.SAVANNA, (Object)Items.ACACIA_BOAT).put((Object)VillagerType.SWAMP, (Object)Items.DARK_OAK_BOAT).build())})));
        hashMap.put(VillagerProfession.SHEPHERD, VillagerTrades.toIntMap((ImmutableMap<Integer, ItemListing[]>)ImmutableMap.of((Object)1, (Object)new ItemListing[]{new EmeraldForItems(Blocks.WHITE_WOOL, 18, 16, 2), new EmeraldForItems(Blocks.BROWN_WOOL, 18, 16, 2), new EmeraldForItems(Blocks.BLACK_WOOL, 18, 16, 2), new EmeraldForItems(Blocks.GRAY_WOOL, 18, 16, 2), new ItemsForEmeralds(Items.SHEARS, 2, 1, 1)}, (Object)2, (Object)new ItemListing[]{new EmeraldForItems(Items.WHITE_DYE, 12, 16, 10), new EmeraldForItems(Items.GRAY_DYE, 12, 16, 10), new EmeraldForItems(Items.BLACK_DYE, 12, 16, 10), new EmeraldForItems(Items.LIGHT_BLUE_DYE, 12, 16, 10), new EmeraldForItems(Items.LIME_DYE, 12, 16, 10), new ItemsForEmeralds(Blocks.WHITE_WOOL, 1, 1, 16, 5), new ItemsForEmeralds(Blocks.ORANGE_WOOL, 1, 1, 16, 5), new ItemsForEmeralds(Blocks.MAGENTA_WOOL, 1, 1, 16, 5), new ItemsForEmeralds(Blocks.LIGHT_BLUE_WOOL, 1, 1, 16, 5), new ItemsForEmeralds(Blocks.YELLOW_WOOL, 1, 1, 16, 5), new ItemsForEmeralds(Blocks.LIME_WOOL, 1, 1, 16, 5), new ItemsForEmeralds(Blocks.PINK_WOOL, 1, 1, 16, 5), new ItemsForEmeralds(Blocks.GRAY_WOOL, 1, 1, 16, 5), new ItemsForEmeralds(Blocks.LIGHT_GRAY_WOOL, 1, 1, 16, 5), new ItemsForEmeralds(Blocks.CYAN_WOOL, 1, 1, 16, 5), new ItemsForEmeralds(Blocks.PURPLE_WOOL, 1, 1, 16, 5), new ItemsForEmeralds(Blocks.BLUE_WOOL, 1, 1, 16, 5), new ItemsForEmeralds(Blocks.BROWN_WOOL, 1, 1, 16, 5), new ItemsForEmeralds(Blocks.GREEN_WOOL, 1, 1, 16, 5), new ItemsForEmeralds(Blocks.RED_WOOL, 1, 1, 16, 5), new ItemsForEmeralds(Blocks.BLACK_WOOL, 1, 1, 16, 5), new ItemsForEmeralds(Blocks.WHITE_CARPET, 1, 4, 16, 5), new ItemsForEmeralds(Blocks.ORANGE_CARPET, 1, 4, 16, 5), new ItemsForEmeralds(Blocks.MAGENTA_CARPET, 1, 4, 16, 5), new ItemsForEmeralds(Blocks.LIGHT_BLUE_CARPET, 1, 4, 16, 5), new ItemsForEmeralds(Blocks.YELLOW_CARPET, 1, 4, 16, 5), new ItemsForEmeralds(Blocks.LIME_CARPET, 1, 4, 16, 5), new ItemsForEmeralds(Blocks.PINK_CARPET, 1, 4, 16, 5), new ItemsForEmeralds(Blocks.GRAY_CARPET, 1, 4, 16, 5), new ItemsForEmeralds(Blocks.LIGHT_GRAY_CARPET, 1, 4, 16, 5), new ItemsForEmeralds(Blocks.CYAN_CARPET, 1, 4, 16, 5), new ItemsForEmeralds(Blocks.PURPLE_CARPET, 1, 4, 16, 5), new ItemsForEmeralds(Blocks.BLUE_CARPET, 1, 4, 16, 5), new ItemsForEmeralds(Blocks.BROWN_CARPET, 1, 4, 16, 5), new ItemsForEmeralds(Blocks.GREEN_CARPET, 1, 4, 16, 5), new ItemsForEmeralds(Blocks.RED_CARPET, 1, 4, 16, 5), new ItemsForEmeralds(Blocks.BLACK_CARPET, 1, 4, 16, 5)}, (Object)3, (Object)new ItemListing[]{new EmeraldForItems(Items.YELLOW_DYE, 12, 16, 20), new EmeraldForItems(Items.LIGHT_GRAY_DYE, 12, 16, 20), new EmeraldForItems(Items.ORANGE_DYE, 12, 16, 20), new EmeraldForItems(Items.RED_DYE, 12, 16, 20), new EmeraldForItems(Items.PINK_DYE, 12, 16, 20), new ItemsForEmeralds(Blocks.WHITE_BED, 3, 1, 12, 10), new ItemsForEmeralds(Blocks.YELLOW_BED, 3, 1, 12, 10), new ItemsForEmeralds(Blocks.RED_BED, 3, 1, 12, 10), new ItemsForEmeralds(Blocks.BLACK_BED, 3, 1, 12, 10), new ItemsForEmeralds(Blocks.BLUE_BED, 3, 1, 12, 10), new ItemsForEmeralds(Blocks.BROWN_BED, 3, 1, 12, 10), new ItemsForEmeralds(Blocks.CYAN_BED, 3, 1, 12, 10), new ItemsForEmeralds(Blocks.GRAY_BED, 3, 1, 12, 10), new ItemsForEmeralds(Blocks.GREEN_BED, 3, 1, 12, 10), new ItemsForEmeralds(Blocks.LIGHT_BLUE_BED, 3, 1, 12, 10), new ItemsForEmeralds(Blocks.LIGHT_GRAY_BED, 3, 1, 12, 10), new ItemsForEmeralds(Blocks.LIME_BED, 3, 1, 12, 10), new ItemsForEmeralds(Blocks.MAGENTA_BED, 3, 1, 12, 10), new ItemsForEmeralds(Blocks.ORANGE_BED, 3, 1, 12, 10), new ItemsForEmeralds(Blocks.PINK_BED, 3, 1, 12, 10), new ItemsForEmeralds(Blocks.PURPLE_BED, 3, 1, 12, 10)}, (Object)4, (Object)new ItemListing[]{new EmeraldForItems(Items.BROWN_DYE, 12, 16, 30), new EmeraldForItems(Items.PURPLE_DYE, 12, 16, 30), new EmeraldForItems(Items.BLUE_DYE, 12, 16, 30), new EmeraldForItems(Items.GREEN_DYE, 12, 16, 30), new EmeraldForItems(Items.MAGENTA_DYE, 12, 16, 30), new EmeraldForItems(Items.CYAN_DYE, 12, 16, 30), new ItemsForEmeralds(Items.WHITE_BANNER, 3, 1, 12, 15), new ItemsForEmeralds(Items.BLUE_BANNER, 3, 1, 12, 15), new ItemsForEmeralds(Items.LIGHT_BLUE_BANNER, 3, 1, 12, 15), new ItemsForEmeralds(Items.RED_BANNER, 3, 1, 12, 15), new ItemsForEmeralds(Items.PINK_BANNER, 3, 1, 12, 15), new ItemsForEmeralds(Items.GREEN_BANNER, 3, 1, 12, 15), new ItemsForEmeralds(Items.LIME_BANNER, 3, 1, 12, 15), new ItemsForEmeralds(Items.GRAY_BANNER, 3, 1, 12, 15), new ItemsForEmeralds(Items.BLACK_BANNER, 3, 1, 12, 15), new ItemsForEmeralds(Items.PURPLE_BANNER, 3, 1, 12, 15), new ItemsForEmeralds(Items.MAGENTA_BANNER, 3, 1, 12, 15), new ItemsForEmeralds(Items.CYAN_BANNER, 3, 1, 12, 15), new ItemsForEmeralds(Items.BROWN_BANNER, 3, 1, 12, 15), new ItemsForEmeralds(Items.YELLOW_BANNER, 3, 1, 12, 15), new ItemsForEmeralds(Items.ORANGE_BANNER, 3, 1, 12, 15), new ItemsForEmeralds(Items.LIGHT_GRAY_BANNER, 3, 1, 12, 15)}, (Object)5, (Object)new ItemListing[]{new ItemsForEmeralds(Items.PAINTING, 2, 3, 30)})));
        hashMap.put(VillagerProfession.FLETCHER, VillagerTrades.toIntMap((ImmutableMap<Integer, ItemListing[]>)ImmutableMap.of((Object)1, (Object)new ItemListing[]{new EmeraldForItems(Items.STICK, 32, 16, 2), new ItemsForEmeralds(Items.ARROW, 1, 16, 1), new ItemsAndEmeraldsToItems(Blocks.GRAVEL, 10, Items.FLINT, 10, 12, 1)}, (Object)2, (Object)new ItemListing[]{new EmeraldForItems(Items.FLINT, 26, 12, 10), new ItemsForEmeralds(Items.BOW, 2, 1, 5)}, (Object)3, (Object)new ItemListing[]{new EmeraldForItems(Items.STRING, 14, 16, 20), new ItemsForEmeralds(Items.CROSSBOW, 3, 1, 10)}, (Object)4, (Object)new ItemListing[]{new EmeraldForItems(Items.FEATHER, 24, 16, 30), new EnchantedItemForEmeralds(Items.BOW, 2, 3, 15)}, (Object)5, (Object)new ItemListing[]{new EmeraldForItems(Items.TRIPWIRE_HOOK, 8, 12, 30), new EnchantedItemForEmeralds(Items.CROSSBOW, 3, 3, 15), new TippedArrowForItemsAndEmeralds(Items.ARROW, 5, Items.TIPPED_ARROW, 5, 2, 12, 30)})));
        hashMap.put(VillagerProfession.LIBRARIAN, VillagerTrades.toIntMap((ImmutableMap<Integer, ItemListing[]>)ImmutableMap.builder().put((Object)1, (Object)new ItemListing[]{new EmeraldForItems(Items.PAPER, 24, 16, 2), new EnchantBookForEmeralds(1), new ItemsForEmeralds(Blocks.BOOKSHELF, 9, 1, 12, 1)}).put((Object)2, (Object)new ItemListing[]{new EmeraldForItems(Items.BOOK, 4, 12, 10), new EnchantBookForEmeralds(5), new ItemsForEmeralds(Items.LANTERN, 1, 1, 5)}).put((Object)3, (Object)new ItemListing[]{new EmeraldForItems(Items.INK_SAC, 5, 12, 20), new EnchantBookForEmeralds(10), new ItemsForEmeralds(Items.GLASS, 1, 4, 10)}).put((Object)4, (Object)new ItemListing[]{new EmeraldForItems(Items.WRITABLE_BOOK, 2, 12, 30), new EnchantBookForEmeralds(15), new ItemsForEmeralds(Items.CLOCK, 5, 1, 15), new ItemsForEmeralds(Items.COMPASS, 4, 1, 15)}).put((Object)5, (Object)new ItemListing[]{new ItemsForEmeralds(Items.NAME_TAG, 20, 1, 30)}).build()));
        hashMap.put(VillagerProfession.CARTOGRAPHER, VillagerTrades.toIntMap((ImmutableMap<Integer, ItemListing[]>)ImmutableMap.of((Object)1, (Object)new ItemListing[]{new EmeraldForItems(Items.PAPER, 24, 16, 2), new ItemsForEmeralds(Items.MAP, 7, 1, 1)}, (Object)2, (Object)new ItemListing[]{new EmeraldForItems(Items.GLASS_PANE, 11, 16, 10), new TreasureMapForEmeralds(13, StructureFeature.OCEAN_MONUMENT, MapDecoration.Type.MONUMENT, 12, 5)}, (Object)3, (Object)new ItemListing[]{new EmeraldForItems(Items.COMPASS, 1, 12, 20), new TreasureMapForEmeralds(14, StructureFeature.WOODLAND_MANSION, MapDecoration.Type.MANSION, 12, 10)}, (Object)4, (Object)new ItemListing[]{new ItemsForEmeralds(Items.ITEM_FRAME, 7, 1, 15), new ItemsForEmeralds(Items.WHITE_BANNER, 3, 1, 15), new ItemsForEmeralds(Items.BLUE_BANNER, 3, 1, 15), new ItemsForEmeralds(Items.LIGHT_BLUE_BANNER, 3, 1, 15), new ItemsForEmeralds(Items.RED_BANNER, 3, 1, 15), new ItemsForEmeralds(Items.PINK_BANNER, 3, 1, 15), new ItemsForEmeralds(Items.GREEN_BANNER, 3, 1, 15), new ItemsForEmeralds(Items.LIME_BANNER, 3, 1, 15), new ItemsForEmeralds(Items.GRAY_BANNER, 3, 1, 15), new ItemsForEmeralds(Items.BLACK_BANNER, 3, 1, 15), new ItemsForEmeralds(Items.PURPLE_BANNER, 3, 1, 15), new ItemsForEmeralds(Items.MAGENTA_BANNER, 3, 1, 15), new ItemsForEmeralds(Items.CYAN_BANNER, 3, 1, 15), new ItemsForEmeralds(Items.BROWN_BANNER, 3, 1, 15), new ItemsForEmeralds(Items.YELLOW_BANNER, 3, 1, 15), new ItemsForEmeralds(Items.ORANGE_BANNER, 3, 1, 15), new ItemsForEmeralds(Items.LIGHT_GRAY_BANNER, 3, 1, 15)}, (Object)5, (Object)new ItemListing[]{new ItemsForEmeralds(Items.GLOBE_BANNER_PATTER, 8, 1, 30)})));
        hashMap.put(VillagerProfession.CLERIC, VillagerTrades.toIntMap((ImmutableMap<Integer, ItemListing[]>)ImmutableMap.of((Object)1, (Object)new ItemListing[]{new EmeraldForItems(Items.ROTTEN_FLESH, 32, 16, 2), new ItemsForEmeralds(Items.REDSTONE, 1, 2, 1)}, (Object)2, (Object)new ItemListing[]{new EmeraldForItems(Items.GOLD_INGOT, 3, 12, 10), new ItemsForEmeralds(Items.LAPIS_LAZULI, 1, 1, 5)}, (Object)3, (Object)new ItemListing[]{new EmeraldForItems(Items.RABBIT_FOOT, 2, 12, 20), new ItemsForEmeralds(Blocks.GLOWSTONE, 4, 1, 12, 10)}, (Object)4, (Object)new ItemListing[]{new EmeraldForItems(Items.SCUTE, 4, 12, 30), new EmeraldForItems(Items.GLASS_BOTTLE, 9, 12, 30), new ItemsForEmeralds(Items.ENDER_PEARL, 5, 1, 15)}, (Object)5, (Object)new ItemListing[]{new EmeraldForItems(Items.NETHER_WART, 22, 12, 30), new ItemsForEmeralds(Items.EXPERIENCE_BOTTLE, 3, 1, 30)})));
        hashMap.put(VillagerProfession.ARMORER, VillagerTrades.toIntMap((ImmutableMap<Integer, ItemListing[]>)ImmutableMap.of((Object)1, (Object)new ItemListing[]{new EmeraldForItems(Items.COAL, 15, 16, 2), new ItemsForEmeralds(new ItemStack(Items.IRON_LEGGINGS), 7, 1, 12, 1, 0.2f), new ItemsForEmeralds(new ItemStack(Items.IRON_BOOTS), 4, 1, 12, 1, 0.2f), new ItemsForEmeralds(new ItemStack(Items.IRON_HELMET), 5, 1, 12, 1, 0.2f), new ItemsForEmeralds(new ItemStack(Items.IRON_CHESTPLATE), 9, 1, 12, 1, 0.2f)}, (Object)2, (Object)new ItemListing[]{new EmeraldForItems(Items.IRON_INGOT, 4, 12, 10), new ItemsForEmeralds(new ItemStack(Items.BELL), 36, 1, 12, 5, 0.2f), new ItemsForEmeralds(new ItemStack(Items.CHAINMAIL_BOOTS), 1, 1, 12, 5, 0.2f), new ItemsForEmeralds(new ItemStack(Items.CHAINMAIL_LEGGINGS), 3, 1, 12, 5, 0.2f)}, (Object)3, (Object)new ItemListing[]{new EmeraldForItems(Items.LAVA_BUCKET, 1, 12, 20), new EmeraldForItems(Items.DIAMOND, 1, 12, 20), new ItemsForEmeralds(new ItemStack(Items.CHAINMAIL_HELMET), 1, 1, 12, 10, 0.2f), new ItemsForEmeralds(new ItemStack(Items.CHAINMAIL_CHESTPLATE), 4, 1, 12, 10, 0.2f), new ItemsForEmeralds(new ItemStack(Items.SHIELD), 5, 1, 12, 10, 0.2f)}, (Object)4, (Object)new ItemListing[]{new EnchantedItemForEmeralds(Items.DIAMOND_LEGGINGS, 14, 3, 15, 0.2f), new EnchantedItemForEmeralds(Items.DIAMOND_BOOTS, 8, 3, 15, 0.2f)}, (Object)5, (Object)new ItemListing[]{new EnchantedItemForEmeralds(Items.DIAMOND_HELMET, 8, 3, 30, 0.2f), new EnchantedItemForEmeralds(Items.DIAMOND_CHESTPLATE, 16, 3, 30, 0.2f)})));
        hashMap.put(VillagerProfession.WEAPONSMITH, VillagerTrades.toIntMap((ImmutableMap<Integer, ItemListing[]>)ImmutableMap.of((Object)1, (Object)new ItemListing[]{new EmeraldForItems(Items.COAL, 15, 16, 2), new ItemsForEmeralds(new ItemStack(Items.IRON_AXE), 3, 1, 12, 1, 0.2f), new EnchantedItemForEmeralds(Items.IRON_SWORD, 2, 3, 1)}, (Object)2, (Object)new ItemListing[]{new EmeraldForItems(Items.IRON_INGOT, 4, 12, 10), new ItemsForEmeralds(new ItemStack(Items.BELL), 36, 1, 12, 5, 0.2f)}, (Object)3, (Object)new ItemListing[]{new EmeraldForItems(Items.FLINT, 24, 12, 20)}, (Object)4, (Object)new ItemListing[]{new EmeraldForItems(Items.DIAMOND, 1, 12, 30), new EnchantedItemForEmeralds(Items.DIAMOND_AXE, 12, 3, 15, 0.2f)}, (Object)5, (Object)new ItemListing[]{new EnchantedItemForEmeralds(Items.DIAMOND_SWORD, 8, 3, 30, 0.2f)})));
        hashMap.put(VillagerProfession.TOOLSMITH, VillagerTrades.toIntMap((ImmutableMap<Integer, ItemListing[]>)ImmutableMap.of((Object)1, (Object)new ItemListing[]{new EmeraldForItems(Items.COAL, 15, 16, 2), new ItemsForEmeralds(new ItemStack(Items.STONE_AXE), 1, 1, 12, 1, 0.2f), new ItemsForEmeralds(new ItemStack(Items.STONE_SHOVEL), 1, 1, 12, 1, 0.2f), new ItemsForEmeralds(new ItemStack(Items.STONE_PICKAXE), 1, 1, 12, 1, 0.2f), new ItemsForEmeralds(new ItemStack(Items.STONE_HOE), 1, 1, 12, 1, 0.2f)}, (Object)2, (Object)new ItemListing[]{new EmeraldForItems(Items.IRON_INGOT, 4, 12, 10), new ItemsForEmeralds(new ItemStack(Items.BELL), 36, 1, 12, 5, 0.2f)}, (Object)3, (Object)new ItemListing[]{new EmeraldForItems(Items.FLINT, 30, 12, 20), new EnchantedItemForEmeralds(Items.IRON_AXE, 1, 3, 10, 0.2f), new EnchantedItemForEmeralds(Items.IRON_SHOVEL, 2, 3, 10, 0.2f), new EnchantedItemForEmeralds(Items.IRON_PICKAXE, 3, 3, 10, 0.2f), new ItemsForEmeralds(new ItemStack(Items.DIAMOND_HOE), 4, 1, 3, 10, 0.2f)}, (Object)4, (Object)new ItemListing[]{new EmeraldForItems(Items.DIAMOND, 1, 12, 30), new EnchantedItemForEmeralds(Items.DIAMOND_AXE, 12, 3, 15, 0.2f), new EnchantedItemForEmeralds(Items.DIAMOND_SHOVEL, 5, 3, 15, 0.2f)}, (Object)5, (Object)new ItemListing[]{new EnchantedItemForEmeralds(Items.DIAMOND_PICKAXE, 13, 3, 30, 0.2f)})));
        hashMap.put(VillagerProfession.BUTCHER, VillagerTrades.toIntMap((ImmutableMap<Integer, ItemListing[]>)ImmutableMap.of((Object)1, (Object)new ItemListing[]{new EmeraldForItems(Items.CHICKEN, 14, 16, 2), new EmeraldForItems(Items.PORKCHOP, 7, 16, 2), new EmeraldForItems(Items.RABBIT, 4, 16, 2), new ItemsForEmeralds(Items.RABBIT_STEW, 1, 1, 1)}, (Object)2, (Object)new ItemListing[]{new EmeraldForItems(Items.COAL, 15, 16, 2), new ItemsForEmeralds(Items.COOKED_PORKCHOP, 1, 5, 16, 5), new ItemsForEmeralds(Items.COOKED_CHICKEN, 1, 8, 16, 5)}, (Object)3, (Object)new ItemListing[]{new EmeraldForItems(Items.MUTTON, 7, 16, 20), new EmeraldForItems(Items.BEEF, 10, 16, 20)}, (Object)4, (Object)new ItemListing[]{new EmeraldForItems(Items.DRIED_KELP_BLOCK, 10, 12, 30)}, (Object)5, (Object)new ItemListing[]{new EmeraldForItems(Items.SWEET_BERRIES, 10, 12, 30)})));
        hashMap.put(VillagerProfession.LEATHERWORKER, VillagerTrades.toIntMap((ImmutableMap<Integer, ItemListing[]>)ImmutableMap.of((Object)1, (Object)new ItemListing[]{new EmeraldForItems(Items.LEATHER, 6, 16, 2), new DyedArmorForEmeralds(Items.LEATHER_LEGGINGS, 3), new DyedArmorForEmeralds(Items.LEATHER_CHESTPLATE, 7)}, (Object)2, (Object)new ItemListing[]{new EmeraldForItems(Items.FLINT, 26, 12, 10), new DyedArmorForEmeralds(Items.LEATHER_HELMET, 5, 12, 5), new DyedArmorForEmeralds(Items.LEATHER_BOOTS, 4, 12, 5)}, (Object)3, (Object)new ItemListing[]{new EmeraldForItems(Items.RABBIT_HIDE, 9, 12, 20), new DyedArmorForEmeralds(Items.LEATHER_CHESTPLATE, 7)}, (Object)4, (Object)new ItemListing[]{new EmeraldForItems(Items.SCUTE, 4, 12, 30), new DyedArmorForEmeralds(Items.LEATHER_HORSE_ARMOR, 6, 12, 15)}, (Object)5, (Object)new ItemListing[]{new ItemsForEmeralds(new ItemStack(Items.SADDLE), 6, 1, 12, 30, 0.2f), new DyedArmorForEmeralds(Items.LEATHER_HELMET, 5, 12, 30)})));
        hashMap.put(VillagerProfession.MASON, VillagerTrades.toIntMap((ImmutableMap<Integer, ItemListing[]>)ImmutableMap.of((Object)1, (Object)new ItemListing[]{new EmeraldForItems(Items.CLAY_BALL, 10, 16, 2), new ItemsForEmeralds(Items.BRICK, 1, 10, 16, 1)}, (Object)2, (Object)new ItemListing[]{new EmeraldForItems(Blocks.STONE, 20, 16, 10), new ItemsForEmeralds(Blocks.CHISELED_STONE_BRICKS, 1, 4, 16, 5)}, (Object)3, (Object)new ItemListing[]{new EmeraldForItems(Blocks.GRANITE, 16, 16, 20), new EmeraldForItems(Blocks.ANDESITE, 16, 16, 20), new EmeraldForItems(Blocks.DIORITE, 16, 16, 20), new ItemsForEmeralds(Blocks.POLISHED_ANDESITE, 1, 4, 16, 10), new ItemsForEmeralds(Blocks.POLISHED_DIORITE, 1, 4, 16, 10), new ItemsForEmeralds(Blocks.POLISHED_GRANITE, 1, 4, 16, 10)}, (Object)4, (Object)new ItemListing[]{new EmeraldForItems(Items.QUARTZ, 12, 12, 30), new ItemsForEmeralds(Blocks.ORANGE_TERRACOTTA, 1, 1, 12, 15), new ItemsForEmeralds(Blocks.WHITE_TERRACOTTA, 1, 1, 12, 15), new ItemsForEmeralds(Blocks.BLUE_TERRACOTTA, 1, 1, 12, 15), new ItemsForEmeralds(Blocks.LIGHT_BLUE_TERRACOTTA, 1, 1, 12, 15), new ItemsForEmeralds(Blocks.GRAY_TERRACOTTA, 1, 1, 12, 15), new ItemsForEmeralds(Blocks.LIGHT_GRAY_TERRACOTTA, 1, 1, 12, 15), new ItemsForEmeralds(Blocks.BLACK_TERRACOTTA, 1, 1, 12, 15), new ItemsForEmeralds(Blocks.RED_TERRACOTTA, 1, 1, 12, 15), new ItemsForEmeralds(Blocks.PINK_TERRACOTTA, 1, 1, 12, 15), new ItemsForEmeralds(Blocks.MAGENTA_TERRACOTTA, 1, 1, 12, 15), new ItemsForEmeralds(Blocks.LIME_TERRACOTTA, 1, 1, 12, 15), new ItemsForEmeralds(Blocks.GREEN_TERRACOTTA, 1, 1, 12, 15), new ItemsForEmeralds(Blocks.CYAN_TERRACOTTA, 1, 1, 12, 15), new ItemsForEmeralds(Blocks.PURPLE_TERRACOTTA, 1, 1, 12, 15), new ItemsForEmeralds(Blocks.YELLOW_TERRACOTTA, 1, 1, 12, 15), new ItemsForEmeralds(Blocks.BROWN_TERRACOTTA, 1, 1, 12, 15), new ItemsForEmeralds(Blocks.ORANGE_GLAZED_TERRACOTTA, 1, 1, 12, 15), new ItemsForEmeralds(Blocks.WHITE_GLAZED_TERRACOTTA, 1, 1, 12, 15), new ItemsForEmeralds(Blocks.BLUE_GLAZED_TERRACOTTA, 1, 1, 12, 15), new ItemsForEmeralds(Blocks.LIGHT_BLUE_GLAZED_TERRACOTTA, 1, 1, 12, 15), new ItemsForEmeralds(Blocks.GRAY_GLAZED_TERRACOTTA, 1, 1, 12, 15), new ItemsForEmeralds(Blocks.LIGHT_GRAY_GLAZED_TERRACOTTA, 1, 1, 12, 15), new ItemsForEmeralds(Blocks.BLACK_GLAZED_TERRACOTTA, 1, 1, 12, 15), new ItemsForEmeralds(Blocks.RED_GLAZED_TERRACOTTA, 1, 1, 12, 15), new ItemsForEmeralds(Blocks.PINK_GLAZED_TERRACOTTA, 1, 1, 12, 15), new ItemsForEmeralds(Blocks.MAGENTA_GLAZED_TERRACOTTA, 1, 1, 12, 15), new ItemsForEmeralds(Blocks.LIME_GLAZED_TERRACOTTA, 1, 1, 12, 15), new ItemsForEmeralds(Blocks.GREEN_GLAZED_TERRACOTTA, 1, 1, 12, 15), new ItemsForEmeralds(Blocks.CYAN_GLAZED_TERRACOTTA, 1, 1, 12, 15), new ItemsForEmeralds(Blocks.PURPLE_GLAZED_TERRACOTTA, 1, 1, 12, 15), new ItemsForEmeralds(Blocks.YELLOW_GLAZED_TERRACOTTA, 1, 1, 12, 15), new ItemsForEmeralds(Blocks.BROWN_GLAZED_TERRACOTTA, 1, 1, 12, 15)}, (Object)5, (Object)new ItemListing[]{new ItemsForEmeralds(Blocks.QUARTZ_PILLAR, 1, 1, 12, 30), new ItemsForEmeralds(Blocks.QUARTZ_BLOCK, 1, 1, 12, 30)})));
    });
    public static final Int2ObjectMap<ItemListing[]> WANDERING_TRADER_TRADES = VillagerTrades.toIntMap((ImmutableMap<Integer, ItemListing[]>)ImmutableMap.of((Object)1, (Object)new ItemListing[]{new ItemsForEmeralds(Items.SEA_PICKLE, 2, 1, 5, 1), new ItemsForEmeralds(Items.SLIME_BALL, 4, 1, 5, 1), new ItemsForEmeralds(Items.GLOWSTONE, 2, 1, 5, 1), new ItemsForEmeralds(Items.NAUTILUS_SHELL, 5, 1, 5, 1), new ItemsForEmeralds(Items.FERN, 1, 1, 12, 1), new ItemsForEmeralds(Items.SUGAR_CANE, 1, 1, 8, 1), new ItemsForEmeralds(Items.PUMPKIN, 1, 1, 4, 1), new ItemsForEmeralds(Items.KELP, 3, 1, 12, 1), new ItemsForEmeralds(Items.CACTUS, 3, 1, 8, 1), new ItemsForEmeralds(Items.DANDELION, 1, 1, 12, 1), new ItemsForEmeralds(Items.POPPY, 1, 1, 12, 1), new ItemsForEmeralds(Items.BLUE_ORCHID, 1, 1, 8, 1), new ItemsForEmeralds(Items.ALLIUM, 1, 1, 12, 1), new ItemsForEmeralds(Items.AZURE_BLUET, 1, 1, 12, 1), new ItemsForEmeralds(Items.RED_TULIP, 1, 1, 12, 1), new ItemsForEmeralds(Items.ORANGE_TULIP, 1, 1, 12, 1), new ItemsForEmeralds(Items.WHITE_TULIP, 1, 1, 12, 1), new ItemsForEmeralds(Items.PINK_TULIP, 1, 1, 12, 1), new ItemsForEmeralds(Items.OXEYE_DAISY, 1, 1, 12, 1), new ItemsForEmeralds(Items.CORNFLOWER, 1, 1, 12, 1), new ItemsForEmeralds(Items.LILY_OF_THE_VALLEY, 1, 1, 7, 1), new ItemsForEmeralds(Items.WHEAT_SEEDS, 1, 1, 12, 1), new ItemsForEmeralds(Items.BEETROOT_SEEDS, 1, 1, 12, 1), new ItemsForEmeralds(Items.PUMPKIN_SEEDS, 1, 1, 12, 1), new ItemsForEmeralds(Items.MELON_SEEDS, 1, 1, 12, 1), new ItemsForEmeralds(Items.ACACIA_SAPLING, 5, 1, 8, 1), new ItemsForEmeralds(Items.BIRCH_SAPLING, 5, 1, 8, 1), new ItemsForEmeralds(Items.DARK_OAK_SAPLING, 5, 1, 8, 1), new ItemsForEmeralds(Items.JUNGLE_SAPLING, 5, 1, 8, 1), new ItemsForEmeralds(Items.OAK_SAPLING, 5, 1, 8, 1), new ItemsForEmeralds(Items.SPRUCE_SAPLING, 5, 1, 8, 1), new ItemsForEmeralds(Items.RED_DYE, 1, 3, 12, 1), new ItemsForEmeralds(Items.WHITE_DYE, 1, 3, 12, 1), new ItemsForEmeralds(Items.BLUE_DYE, 1, 3, 12, 1), new ItemsForEmeralds(Items.PINK_DYE, 1, 3, 12, 1), new ItemsForEmeralds(Items.BLACK_DYE, 1, 3, 12, 1), new ItemsForEmeralds(Items.GREEN_DYE, 1, 3, 12, 1), new ItemsForEmeralds(Items.LIGHT_GRAY_DYE, 1, 3, 12, 1), new ItemsForEmeralds(Items.MAGENTA_DYE, 1, 3, 12, 1), new ItemsForEmeralds(Items.YELLOW_DYE, 1, 3, 12, 1), new ItemsForEmeralds(Items.GRAY_DYE, 1, 3, 12, 1), new ItemsForEmeralds(Items.PURPLE_DYE, 1, 3, 12, 1), new ItemsForEmeralds(Items.LIGHT_BLUE_DYE, 1, 3, 12, 1), new ItemsForEmeralds(Items.LIME_DYE, 1, 3, 12, 1), new ItemsForEmeralds(Items.ORANGE_DYE, 1, 3, 12, 1), new ItemsForEmeralds(Items.BROWN_DYE, 1, 3, 12, 1), new ItemsForEmeralds(Items.CYAN_DYE, 1, 3, 12, 1), new ItemsForEmeralds(Items.BRAIN_CORAL_BLOCK, 3, 1, 8, 1), new ItemsForEmeralds(Items.BUBBLE_CORAL_BLOCK, 3, 1, 8, 1), new ItemsForEmeralds(Items.FIRE_CORAL_BLOCK, 3, 1, 8, 1), new ItemsForEmeralds(Items.HORN_CORAL_BLOCK, 3, 1, 8, 1), new ItemsForEmeralds(Items.TUBE_CORAL_BLOCK, 3, 1, 8, 1), new ItemsForEmeralds(Items.VINE, 1, 1, 12, 1), new ItemsForEmeralds(Items.BROWN_MUSHROOM, 1, 1, 12, 1), new ItemsForEmeralds(Items.RED_MUSHROOM, 1, 1, 12, 1), new ItemsForEmeralds(Items.LILY_PAD, 1, 2, 5, 1), new ItemsForEmeralds(Items.SAND, 1, 8, 8, 1), new ItemsForEmeralds(Items.RED_SAND, 1, 4, 6, 1)}, (Object)2, (Object)new ItemListing[]{new ItemsForEmeralds(Items.TROPICAL_FISH_BUCKET, 5, 1, 4, 1), new ItemsForEmeralds(Items.PUFFERFISH_BUCKET, 5, 1, 4, 1), new ItemsForEmeralds(Items.PACKED_ICE, 3, 1, 6, 1), new ItemsForEmeralds(Items.BLUE_ICE, 6, 1, 6, 1), new ItemsForEmeralds(Items.GUNPOWDER, 1, 1, 8, 1), new ItemsForEmeralds(Items.PODZOL, 3, 3, 6, 1)}));

    private static Int2ObjectMap<ItemListing[]> toIntMap(ImmutableMap<Integer, ItemListing[]> immutableMap) {
        return new Int2ObjectOpenHashMap(immutableMap);
    }

    static class ItemsAndEmeraldsToItems
    implements ItemListing {
        private final ItemStack fromItem;
        private final int fromCount;
        private final int emeraldCost;
        private final ItemStack toItem;
        private final int toCount;
        private final int maxUses;
        private final int villagerXp;
        private final float priceMultiplier;

        public ItemsAndEmeraldsToItems(ItemLike itemLike, int n, Item item, int n2, int n3, int n4) {
            this(itemLike, n, 1, item, n2, n3, n4);
        }

        public ItemsAndEmeraldsToItems(ItemLike itemLike, int n, int n2, Item item, int n3, int n4, int n5) {
            this.fromItem = new ItemStack(itemLike);
            this.fromCount = n;
            this.emeraldCost = n2;
            this.toItem = new ItemStack(item);
            this.toCount = n3;
            this.maxUses = n4;
            this.villagerXp = n5;
            this.priceMultiplier = 0.05f;
        }

        @Nullable
        @Override
        public MerchantOffer getOffer(Entity entity, Random random) {
            return new MerchantOffer(new ItemStack(Items.EMERALD, this.emeraldCost), new ItemStack(this.fromItem.getItem(), this.fromCount), new ItemStack(this.toItem.getItem(), this.toCount), this.maxUses, this.villagerXp, this.priceMultiplier);
        }
    }

    static class TreasureMapForEmeralds
    implements ItemListing {
        private final int emeraldCost;
        private final StructureFeature<?> destination;
        private final MapDecoration.Type destinationType;
        private final int maxUses;
        private final int villagerXp;

        public TreasureMapForEmeralds(int n, StructureFeature<?> structureFeature, MapDecoration.Type type, int n2, int n3) {
            this.emeraldCost = n;
            this.destination = structureFeature;
            this.destinationType = type;
            this.maxUses = n2;
            this.villagerXp = n3;
        }

        @Nullable
        @Override
        public MerchantOffer getOffer(Entity entity, Random random) {
            if (!(entity.level instanceof ServerLevel)) {
                return null;
            }
            ServerLevel serverLevel = (ServerLevel)entity.level;
            BlockPos blockPos = serverLevel.findNearestMapFeature(this.destination, entity.blockPosition(), 100, true);
            if (blockPos != null) {
                ItemStack itemStack = MapItem.create(serverLevel, blockPos.getX(), blockPos.getZ(), (byte)2, true, true);
                MapItem.renderBiomePreviewMap(serverLevel, itemStack);
                MapItemSavedData.addTargetDecoration(itemStack, blockPos, "+", this.destinationType);
                itemStack.setHoverName(new TranslatableComponent("filled_map." + this.destination.getFeatureName().toLowerCase(Locale.ROOT)));
                return new MerchantOffer(new ItemStack(Items.EMERALD, this.emeraldCost), new ItemStack(Items.COMPASS), itemStack, this.maxUses, this.villagerXp, 0.2f);
            }
            return null;
        }
    }

    static class EnchantBookForEmeralds
    implements ItemListing {
        private final int villagerXp;

        public EnchantBookForEmeralds(int n) {
            this.villagerXp = n;
        }

        @Override
        public MerchantOffer getOffer(Entity entity, Random random) {
            List list = Registry.ENCHANTMENT.stream().filter(Enchantment::isTradeable).collect(Collectors.toList());
            Enchantment enchantment = (Enchantment)list.get(random.nextInt(list.size()));
            int n = Mth.nextInt(random, enchantment.getMinLevel(), enchantment.getMaxLevel());
            ItemStack itemStack = EnchantedBookItem.createForEnchantment(new EnchantmentInstance(enchantment, n));
            int n2 = 2 + random.nextInt(5 + n * 10) + 3 * n;
            if (enchantment.isTreasureOnly()) {
                n2 *= 2;
            }
            if (n2 > 64) {
                n2 = 64;
            }
            return new MerchantOffer(new ItemStack(Items.EMERALD, n2), new ItemStack(Items.BOOK), itemStack, 12, this.villagerXp, 0.2f);
        }
    }

    static class DyedArmorForEmeralds
    implements ItemListing {
        private final Item item;
        private final int value;
        private final int maxUses;
        private final int villagerXp;

        public DyedArmorForEmeralds(Item item, int n) {
            this(item, n, 12, 1);
        }

        public DyedArmorForEmeralds(Item item, int n, int n2, int n3) {
            this.item = item;
            this.value = n;
            this.maxUses = n2;
            this.villagerXp = n3;
        }

        @Override
        public MerchantOffer getOffer(Entity entity, Random random) {
            ItemStack itemStack = new ItemStack(Items.EMERALD, this.value);
            ItemStack itemStack2 = new ItemStack(this.item);
            if (this.item instanceof DyeableArmorItem) {
                ArrayList arrayList = Lists.newArrayList();
                arrayList.add(DyedArmorForEmeralds.getRandomDye(random));
                if (random.nextFloat() > 0.7f) {
                    arrayList.add(DyedArmorForEmeralds.getRandomDye(random));
                }
                if (random.nextFloat() > 0.8f) {
                    arrayList.add(DyedArmorForEmeralds.getRandomDye(random));
                }
                itemStack2 = DyeableLeatherItem.dyeArmor(itemStack2, arrayList);
            }
            return new MerchantOffer(itemStack, itemStack2, this.maxUses, this.villagerXp, 0.2f);
        }

        private static DyeItem getRandomDye(Random random) {
            return DyeItem.byColor(DyeColor.byId(random.nextInt(16)));
        }
    }

    static class TippedArrowForItemsAndEmeralds
    implements ItemListing {
        private final ItemStack toItem;
        private final int toCount;
        private final int emeraldCost;
        private final int maxUses;
        private final int villagerXp;
        private final Item fromItem;
        private final int fromCount;
        private final float priceMultiplier;

        public TippedArrowForItemsAndEmeralds(Item item, int n, Item item2, int n2, int n3, int n4, int n5) {
            this.toItem = new ItemStack(item2);
            this.emeraldCost = n3;
            this.maxUses = n4;
            this.villagerXp = n5;
            this.fromItem = item;
            this.fromCount = n;
            this.toCount = n2;
            this.priceMultiplier = 0.05f;
        }

        @Override
        public MerchantOffer getOffer(Entity entity, Random random) {
            ItemStack itemStack = new ItemStack(Items.EMERALD, this.emeraldCost);
            List list = Registry.POTION.stream().filter(potion -> !potion.getEffects().isEmpty() && PotionBrewing.isBrewablePotion(potion)).collect(Collectors.toList());
            Potion potion2 = (Potion)list.get(random.nextInt(list.size()));
            ItemStack itemStack2 = PotionUtils.setPotion(new ItemStack(this.toItem.getItem(), this.toCount), potion2);
            return new MerchantOffer(itemStack, new ItemStack(this.fromItem, this.fromCount), itemStack2, this.maxUses, this.villagerXp, this.priceMultiplier);
        }
    }

    static class EnchantedItemForEmeralds
    implements ItemListing {
        private final ItemStack itemStack;
        private final int baseEmeraldCost;
        private final int maxUses;
        private final int villagerXp;
        private final float priceMultiplier;

        public EnchantedItemForEmeralds(Item item, int n, int n2, int n3) {
            this(item, n, n2, n3, 0.05f);
        }

        public EnchantedItemForEmeralds(Item item, int n, int n2, int n3, float f) {
            this.itemStack = new ItemStack(item);
            this.baseEmeraldCost = n;
            this.maxUses = n2;
            this.villagerXp = n3;
            this.priceMultiplier = f;
        }

        @Override
        public MerchantOffer getOffer(Entity entity, Random random) {
            int n = 5 + random.nextInt(15);
            ItemStack itemStack = EnchantmentHelper.enchantItem(random, new ItemStack(this.itemStack.getItem()), n, false);
            int n2 = Math.min(this.baseEmeraldCost + n, 64);
            ItemStack itemStack2 = new ItemStack(Items.EMERALD, n2);
            return new MerchantOffer(itemStack2, itemStack, this.maxUses, this.villagerXp, this.priceMultiplier);
        }
    }

    static class SuspisciousStewForEmerald
    implements ItemListing {
        final MobEffect effect;
        final int duration;
        final int xp;
        private final float priceMultiplier;

        public SuspisciousStewForEmerald(MobEffect mobEffect, int n, int n2) {
            this.effect = mobEffect;
            this.duration = n;
            this.xp = n2;
            this.priceMultiplier = 0.05f;
        }

        @Nullable
        @Override
        public MerchantOffer getOffer(Entity entity, Random random) {
            ItemStack itemStack = new ItemStack(Items.SUSPICIOUS_STEW, 1);
            SuspiciousStewItem.saveMobEffect(itemStack, this.effect, this.duration);
            return new MerchantOffer(new ItemStack(Items.EMERALD, 1), itemStack, 12, this.xp, this.priceMultiplier);
        }
    }

    static class ItemsForEmeralds
    implements ItemListing {
        private final ItemStack itemStack;
        private final int emeraldCost;
        private final int numberOfItems;
        private final int maxUses;
        private final int villagerXp;
        private final float priceMultiplier;

        public ItemsForEmeralds(Block block, int n, int n2, int n3, int n4) {
            this(new ItemStack(block), n, n2, n3, n4);
        }

        public ItemsForEmeralds(Item item, int n, int n2, int n3) {
            this(new ItemStack(item), n, n2, 12, n3);
        }

        public ItemsForEmeralds(Item item, int n, int n2, int n3, int n4) {
            this(new ItemStack(item), n, n2, n3, n4);
        }

        public ItemsForEmeralds(ItemStack itemStack, int n, int n2, int n3, int n4) {
            this(itemStack, n, n2, n3, n4, 0.05f);
        }

        public ItemsForEmeralds(ItemStack itemStack, int n, int n2, int n3, int n4, float f) {
            this.itemStack = itemStack;
            this.emeraldCost = n;
            this.numberOfItems = n2;
            this.maxUses = n3;
            this.villagerXp = n4;
            this.priceMultiplier = f;
        }

        @Override
        public MerchantOffer getOffer(Entity entity, Random random) {
            return new MerchantOffer(new ItemStack(Items.EMERALD, this.emeraldCost), new ItemStack(this.itemStack.getItem(), this.numberOfItems), this.maxUses, this.villagerXp, this.priceMultiplier);
        }
    }

    static class EmeraldsForVillagerTypeItem
    implements ItemListing {
        private final Map<VillagerType, Item> trades;
        private final int cost;
        private final int maxUses;
        private final int villagerXp;

        public EmeraldsForVillagerTypeItem(int n, int n2, int n3, Map<VillagerType, Item> map) {
            Registry.VILLAGER_TYPE.stream().filter(villagerType -> !map.containsKey(villagerType)).findAny().ifPresent(villagerType -> {
                throw new IllegalStateException("Missing trade for villager type: " + Registry.VILLAGER_TYPE.getKey((VillagerType)villagerType));
            });
            this.trades = map;
            this.cost = n;
            this.maxUses = n2;
            this.villagerXp = n3;
        }

        @Nullable
        @Override
        public MerchantOffer getOffer(Entity entity, Random random) {
            if (entity instanceof VillagerDataHolder) {
                ItemStack itemStack = new ItemStack(this.trades.get(((VillagerDataHolder)((Object)entity)).getVillagerData().getType()), this.cost);
                return new MerchantOffer(itemStack, new ItemStack(Items.EMERALD), this.maxUses, this.villagerXp, 0.05f);
            }
            return null;
        }
    }

    static class EmeraldForItems
    implements ItemListing {
        private final Item item;
        private final int cost;
        private final int maxUses;
        private final int villagerXp;
        private final float priceMultiplier;

        public EmeraldForItems(ItemLike itemLike, int n, int n2, int n3) {
            this.item = itemLike.asItem();
            this.cost = n;
            this.maxUses = n2;
            this.villagerXp = n3;
            this.priceMultiplier = 0.05f;
        }

        @Override
        public MerchantOffer getOffer(Entity entity, Random random) {
            ItemStack itemStack = new ItemStack(this.item, this.cost);
            return new MerchantOffer(itemStack, new ItemStack(Items.EMERALD), this.maxUses, this.villagerXp, this.priceMultiplier);
        }
    }

    public static interface ItemListing {
        @Nullable
        public MerchantOffer getOffer(Entity var1, Random var2);
    }

}

