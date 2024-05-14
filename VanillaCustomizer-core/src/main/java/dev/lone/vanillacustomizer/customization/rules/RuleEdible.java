package dev.lone.vanillacustomizer.customization.rules;

import org.bukkit.inventory.ItemStack;

public class RuleEdible implements IRule
{
    private final boolean value;

    public RuleEdible(boolean value)
    {
        this.value = value;
    }

    @Override
    public boolean matches(ItemStack item)
    {
        if(value)
            return item.getType().isEdible();
        else
            return !item.getType().isEdible();
    }
}
