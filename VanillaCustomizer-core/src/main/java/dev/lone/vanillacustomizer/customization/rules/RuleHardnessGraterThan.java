package dev.lone.vanillacustomizer.customization.rules;

import dev.lone.vanillacustomizer.nms.items.ItemsNms;
import org.bukkit.inventory.ItemStack;

public class RuleHardnessGraterThan implements IRule
{
    private final double value;

    public RuleHardnessGraterThan(double value)
    {
        this.value = value;
    }

    @Override
    public boolean matches(ItemStack item)
    {
        return ItemsNms.nms().getDestroySpeed(item) > value;
    }
}
