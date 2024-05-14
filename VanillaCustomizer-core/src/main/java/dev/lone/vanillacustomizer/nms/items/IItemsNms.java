package dev.lone.vanillacustomizer.nms.items;

import org.bukkit.inventory.ItemStack;

public interface IItemsNms
{
    Rarity getRarity(ItemStack bukkitItem);

    float getDestroySpeed(ItemStack bukkitItem);

    int getNutrition(ItemStack bukkitItem);

    float getSaturation(ItemStack bukkitItem);
}
