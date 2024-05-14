package dev.lone.vanillacustomizer.customization.rules;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class RuleMaterials implements IRule
{
    List<Material> materials;

    public RuleMaterials(List<Material> materials)
    {
        this.materials = materials;
    }

    @Override
    public boolean matches(ItemStack item)
    {
        Material type = item.getType();
        for (Material entry : materials)
        {
            if(type == entry)
                return true;
        }

        return false;
    }
}
