package dev.lone.vanillacustomizer.customization.rules;

import dev.lone.vanillacustomizer.ChangeSession;
import dev.lone.vanillacustomizer.utils.Utilz;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class RuleVanillaItemMatcher implements IRule
{
    final boolean isVanilla;

    public RuleVanillaItemMatcher(boolean isVanilla)
    {
        this.isVanilla = isVanilla;
    }

    public static boolean satisfy(ItemStack itemStack)
    {
        if(itemStack == null)
            return false;

        if(!itemStack.hasItemMeta())
            return true;

        ItemMeta meta = itemStack.getItemMeta();
        if(meta == null)
            return true;

        // TODO: warning, items with no lore would still be considered vanilla while they might be custom.
        //  might need to check NBT
        if(meta.hasLore())
        {
            return false;
        }

        String displayName = meta.getDisplayName();
        displayName = Utilz.backColor(displayName);

        // Check if italic, means that might have been renamed using anvil.
        return !displayName.startsWith("&o");
    }

    @Override
    public boolean matches(ItemStack item)
    {
        return false;
    }

    @Override
    public boolean matches(ChangeSession session)
    {
        return session.isVanilla;
    }
}
