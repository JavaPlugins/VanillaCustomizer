package dev.lone.vanillacustomizer.customization.rules;

import dev.lone.vanillacustomizer.customization.matchers.RegexMatcher;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class RuleMaterialRegex implements IRule
{
    RegexMatcher matcher;

    public RuleMaterialRegex(String regexStr)
    {
        matcher = new RegexMatcher(regexStr);
    }

    @Override
    public boolean matches(ItemStack item)
    {
        return matcher.matches(item.getType().toString());
    }
}
