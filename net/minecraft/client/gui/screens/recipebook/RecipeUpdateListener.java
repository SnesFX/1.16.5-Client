/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.gui.screens.recipebook;

import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;

public interface RecipeUpdateListener {
    public void recipesUpdated();

    public RecipeBookComponent getRecipeBookComponent();
}

