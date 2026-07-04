package net.daplow.daplowfletch.other.recipes;

import net.minecraft.item.Item;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class StemRecipeManager {

    public static final Map<Item, StemRecipe> RECIPES = new HashMap<>();

    public static void register(StemRecipe recipe) {
        RECIPES.put(recipe.getItem(), recipe);
    }

    @Nullable
    public static StemRecipe get(Item item) {
        return RECIPES.get(item);
    }


    public static Optional<StemRecipe> getOpt(Item item) {
        return RECIPES.values().stream()
                .filter(r -> r.getItem() == item)
                .findFirst();
    }

    public static Collection<StemRecipe> getRecipes() {
        return RECIPES.values();
    }
}
