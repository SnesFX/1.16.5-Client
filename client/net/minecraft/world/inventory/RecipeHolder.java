/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.world.inventory;

import java.util.Collection;
import java.util.Collections;
import javax.annotation.Nullable;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.ServerRecipeBook;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;

public interface RecipeHolder {
    public void setRecipeUsed(@Nullable Recipe<?> var1);

    @Nullable
    public Recipe<?> getRecipeUsed();

    default public void awardUsedRecipes(Player player) {
        Recipe<?> recipe = this.getRecipeUsed();
        if (recipe != null && !recipe.isSpecial()) {
            player.awardRecipes(Collections.singleton(recipe));
            this.setRecipeUsed(null);
        }
    }

    default public boolean setRecipeUsed(Level level, ServerPlayer serverPlayer, Recipe<?> recipe) {
        if (recipe.isSpecial() || !level.getGameRules().getBoolean(GameRules.RULE_LIMITED_CRAFTING) || serverPlayer.getRecipeBook().contains(recipe)) {
            this.setRecipeUsed(recipe);
            return true;
        }
        return false;
    }
}

