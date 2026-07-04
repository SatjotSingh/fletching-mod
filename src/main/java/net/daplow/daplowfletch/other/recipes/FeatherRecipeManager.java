package net.daplow.daplowfletch.other.recipes;

import net.minecraft.item.Item;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class FeatherRecipeManager {

    private static final Map<Item, FeatherRecipe> RECIPES = new HashMap<>();

    public static void register(FeatherRecipe recipe) {
        RECIPES.put(recipe.getItem(), recipe);
    }

    @Nullable
    public static FeatherRecipe get(Item item) {
        return RECIPES.get(item);
    }

    public static Collection<FeatherRecipe> getRecipes() {
        return RECIPES.values();
    }
}
