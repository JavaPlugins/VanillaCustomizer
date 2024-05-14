package dev.lone.vanillacustomizer.nms.items.impl;

import dev.lone.vanillacustomizer.nms.items.IItemsNms;
import dev.lone.vanillacustomizer.nms.items.Rarity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_20_R3.util.CraftMagicNumbers;
import org.bukkit.inventory.ItemStack;

public class v1_20_R3 implements IItemsNms
{
    @Override
    public Rarity getRarity(ItemStack bukkitItem)
    {
        net.minecraft.world.item.ItemStack item = CraftItemStack.asNMSCopy(bukkitItem);
        return Rarity.values()[item.getRarity().ordinal()];
    }

    @Override
    public float getDestroySpeed(ItemStack bukkitItem)
    {
        Block block = CraftMagicNumbers.getBlock(bukkitItem.getType());
        return block.defaultBlockState().destroySpeed;
    }

    @Override
    public int getNutrition(ItemStack bukkitItem)
    {
        Item item = CraftItemStack.asNMSCopy(bukkitItem).getItem();
        FoodProperties foodProperties = item.getFoodProperties();
        if(foodProperties == null)
            return 0;
        return foodProperties.getNutrition();
    }

    @Override
    public float getSaturation(ItemStack bukkitItem)
    {
        Item item = CraftItemStack.asNMSCopy(bukkitItem).getItem();
        FoodProperties foodProperties = item.getFoodProperties();
        if(foodProperties == null)
            return 0;
        return foodProperties.getSaturationModifier();
    }
}
