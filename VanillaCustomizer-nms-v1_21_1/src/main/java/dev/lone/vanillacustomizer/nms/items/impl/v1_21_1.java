package dev.lone.vanillacustomizer.nms.items.impl;

import dev.lone.vanillacustomizer.nms.items.IItemsNms;
import dev.lone.vanillacustomizer.nms.items.Rarity;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.level.block.Block;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.craftbukkit.util.CraftMagicNumbers;
import org.bukkit.inventory.ItemStack;

@SuppressWarnings("unused")
public class v1_21_1 implements IItemsNms
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
        net.minecraft.world.item.ItemStack itemStack = CraftItemStack.asNMSCopy(bukkitItem);
        FoodProperties foodProperties = itemStack.get(DataComponents.FOOD);
        if(foodProperties != null)
            return foodProperties.nutrition();
        return 0;
    }

    @Override
    public float getSaturation(ItemStack bukkitItem)
    {
        net.minecraft.world.item.ItemStack itemStack = CraftItemStack.asNMSCopy(bukkitItem);
        FoodProperties foodProperties = itemStack.get(DataComponents.FOOD);
        if(foodProperties != null)
            return foodProperties.saturation();
        return 0;
    }
}
