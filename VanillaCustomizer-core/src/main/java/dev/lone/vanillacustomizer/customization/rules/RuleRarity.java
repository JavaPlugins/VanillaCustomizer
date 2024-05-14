package dev.lone.vanillacustomizer.customization.rules;

import dev.lone.vanillacustomizer.nms.items.ItemsNms;
import dev.lone.vanillacustomizer.nms.items.Rarity;
import org.bukkit.inventory.ItemStack;

public class RuleRarity implements IRule
{
    private Rarity rarity;

    public RuleRarity(Rarity rarity)
    {
        this.rarity = rarity;
    }

    @Override
    public boolean matches(ItemStack item)
    {
        return rarity.equals(ItemsNms.nms().getRarity(item));
    }
}
