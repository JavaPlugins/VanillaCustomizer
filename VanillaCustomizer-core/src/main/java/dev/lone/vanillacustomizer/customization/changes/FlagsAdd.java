package dev.lone.vanillacustomizer.customization.changes;

import dev.lone.vanillacustomizer.ChangeSession;
import org.bukkit.inventory.ItemFlag;

import java.util.List;

public class FlagsAdd implements IChange
{
    final ItemFlag[] flags;

    public FlagsAdd(List<ItemFlag> flags)
    {
        this.flags = flags.toArray(ItemFlag[]::new);
    }

    @Override
    public void apply(ChangeSession session)
    {
        session.refreshMeta();
        session.meta.addItemFlags(flags);
        session.applyMeta();
    }
}
