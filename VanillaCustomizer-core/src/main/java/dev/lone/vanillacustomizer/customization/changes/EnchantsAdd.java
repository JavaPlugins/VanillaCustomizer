package dev.lone.vanillacustomizer.customization.changes;

import dev.lone.vanillacustomizer.ChangeSession;
import org.bukkit.enchantments.Enchantment;

import java.util.HashMap;
import java.util.Map;

public class EnchantsAdd implements IChange
{
    final HashMap<Enchantment, Integer> enchants;

    public EnchantsAdd(HashMap<Enchantment, Integer> enchants)
    {
        this.enchants = enchants;
    }

    @Override
    public void apply(ChangeSession session)
    {
        if(session.refreshMeta() == null)
            return;

        for (Map.Entry<Enchantment, Integer> e : enchants.entrySet())
        {
            Enchantment enchantment = e.getKey();
            Integer level = e.getValue();

            if(level <= 0)
                session.meta.removeEnchant(enchantment);
            else
                session.meta.addEnchant(enchantment, level, true);
        }

        session.applyMeta();
    }
}