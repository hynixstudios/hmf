package me.infinityz.minigame.crafting.recipes;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;

import me.infinityz.minigame.crafting.CustomRecipe;

public class TotemRecipe extends CustomRecipe {

    public TotemRecipe(NamespacedKey namespacedKey, Recipe craft, String name) {
        super(namespacedKey, craft, name);

        final ItemStack item = new ItemStack(Material.TOTEM_OF_UNDYING);

        final ShapedRecipe recipe = new ShapedRecipe(namespacedKey, item);
        recipe.shape("EAE", "AAA", "BAB");
        recipe.setIngredient('E', Material.EMERALD);
        recipe.setIngredient('A', Material.GOLD_BLOCK);
        recipe.setIngredient('B', Material.AIR);

        setRecipe(recipe);
    }

}