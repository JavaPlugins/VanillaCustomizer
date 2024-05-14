package dev.lone.vanillacustomizer.customization.changes;

import dev.lone.vanillacustomizer.ChangeSession;
import org.bukkit.enchantments.Enchantment;

import java.util.List;

public class EnchantsRemove implements IChange
{
    final List<Enchantment> enchants;

    public EnchantsRemove(List<Enchantment> enchants)
    {
        this.enchants = enchants;
    }

    @Override
    public void apply(ChangeSession session)
    {
        if(session.refreshMeta() == null)
            return;

        for (Enchantment enchant : enchants)
        {
            session.meta.removeEnchant(enchant);
        }

        session.applyMeta();
    }
}