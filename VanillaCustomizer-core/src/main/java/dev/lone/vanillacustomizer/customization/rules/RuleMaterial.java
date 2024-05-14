package dev.lone.vanillacustomizer.customization.rules;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class RuleMaterial implements IRule
{
    Material material;

    public RuleMaterial(Material material)
    {
        this.material = material;
    }

    @Override
    public boolean matches(ItemStack item)
    {
        return item.getType() == material;
    }
}
