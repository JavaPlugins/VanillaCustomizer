package dev.lone.vanillacustomizer.customization.rules;

import org.bukkit.inventory.ItemStack;

public class RulePlaceable implements IRule
{
    final boolean placeable;

    public RulePlaceable(boolean placeable)
    {
        this.placeable = placeable;
    }

    @Override
    public boolean matches(ItemStack item)
    {
        if(placeable)
            return item.getType().isBlock();
        return !item.getType().isBlock();
    }
}
