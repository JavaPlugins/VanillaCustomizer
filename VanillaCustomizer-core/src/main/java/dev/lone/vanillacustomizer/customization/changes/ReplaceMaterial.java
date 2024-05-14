package dev.lone.vanillacustomizer.customization.changes;

import dev.lone.vanillacustomizer.ChangeSession;
import org.bukkit.Material;

public class ReplaceMaterial implements IChange
{
    private final Material material;

    public ReplaceMaterial(Material material)
    {
        this.material = material;
    }

    @Override
    public void apply(ChangeSession session)
    {
        session.item.setType(material);
        session.refreshAll(); // Idk if it's needed
    }
}
