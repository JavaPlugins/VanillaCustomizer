package dev.lone.vanillacustomizer.customization.rules;

import dev.lone.vanillacustomizer.ChangeSession;
import org.bukkit.inventory.ItemStack;

public interface IRule
{
    boolean matches(ItemStack item);

    default boolean matches(ChangeSession session)
    {
        return matches(session.item);
    }
}
