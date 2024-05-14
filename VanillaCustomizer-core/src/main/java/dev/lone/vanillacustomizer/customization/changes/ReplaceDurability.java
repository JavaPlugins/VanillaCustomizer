package dev.lone.vanillacustomizer.customization.changes;

import dev.lone.vanillacustomizer.ChangeSession;
import org.bukkit.inventory.meta.Damageable;

public class ReplaceDurability implements IChange
{
    private final short amount;

    public ReplaceDurability(short amount)
    {
        this.amount = amount;
    }

    @Override
    public void apply(ChangeSession session)
    {
        Damageable damageable = (Damageable) session.refreshMeta();
        assert damageable != null;

        short max = session.item.getType().getMaxDurability();
        damageable.setDamage(amount > max ? 0 : (short) (max - amount));
    }
}
