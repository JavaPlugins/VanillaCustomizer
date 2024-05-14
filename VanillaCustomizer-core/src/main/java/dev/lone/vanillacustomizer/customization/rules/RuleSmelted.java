package dev.lone.vanillacustomizer.customization.rules;

import dev.lone.vanillacustomizer.nms.items.ItemsNms;
import org.bukkit.inventory.ItemStack;

public class RuleSmelted implements IRule
{
    private final boolean value;

    public RuleSmelted(boolean value)
    {
        this.value = value;
    }

    @Override
    public boolean matches(ItemStack item)
    {
        if(value)
            return ItemsNms.inst().SMELTED_MATERIALS.contains(item.getType());
        else
            return !ItemsNms.inst().SMELTED_MATERIALS.contains(item.getType());
    }
}
