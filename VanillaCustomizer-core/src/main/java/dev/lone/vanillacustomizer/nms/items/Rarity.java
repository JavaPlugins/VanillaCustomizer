package dev.lone.vanillacustomizer.nms.items;

import org.bukkit.Color;

@SuppressWarnings("unused")
public enum Rarity
{
    COMMON(Color.WHITE),
    UNCOMMON(Color.YELLOW),
    RARE(Color.AQUA),
    EPIC(Color.PURPLE);

    public final Color color;

    Rarity(Color var2)
    {
        this.color = var2;
    }
}
