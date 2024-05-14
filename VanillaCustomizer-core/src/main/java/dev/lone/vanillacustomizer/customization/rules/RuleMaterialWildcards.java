package dev.lone.vanillacustomizer.customization.rules;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOCase;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class RuleMaterialWildcards implements IRule
{
    List<Material> matches = new ArrayList<>();

    public List<String> init(List<String> materialWildcards)
    {
        List<String> noMatches = new ArrayList<>();
        for (String entry : materialWildcards)
        {
            boolean any = false;
            for (Material material : Material.values())
            {
                if(FilenameUtils.wildcardMatch(material.toString(), entry, IOCase.INSENSITIVE))
                {
                    matches.add(material);
                    any = true;
                }
            }

            if(!any)
            {
                noMatches.add(entry);
            }
        }

        return noMatches;
    }

    @Override
    public boolean matches(ItemStack item)
    {
        return matches.contains(item.getType());
    }
}
