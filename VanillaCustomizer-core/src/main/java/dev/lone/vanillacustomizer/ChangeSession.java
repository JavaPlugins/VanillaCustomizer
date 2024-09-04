package dev.lone.vanillacustomizer;

import dev.lone.LoneLibs.nbt.nbtapi.NBTItem;
import org.jetbrains.annotations.Nullable;
import dev.lone.vanillacustomizer.customization.rules.RuleVanillaItemMatcher;
import dev.lone.vanillacustomizer.nms.NMS;
import lonelibs.dev.lone.fastnbt.nms.nbt.NItem;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

// TODO: find a better name for this
public class ChangeSession
{
    public final ItemStack item;
    private final Player player;
    public final ItemStack originalItem;

    public ItemMeta meta;

    public NBTItem nbtLegacy;
    public NItem nbt;

    public final boolean isVanilla;

    @Nullable
    private Boolean hasChanged_cached;

    public ChangeSession(ItemStack item, Player player, boolean trackChanges)
    {
        this.item = item;
        this.player = player;
        if(trackChanges)
            this.originalItem = item.clone();
        else
            this.originalItem = null;

        isVanilla = RuleVanillaItemMatcher.satisfy(item);
    }

    public Player getPlayer()
    {
        return player;
    }

    public ItemMeta refreshMeta()
    {
        // Meta always needs to be refreshed as Spigot caches it!
        meta = item.getItemMeta();
        return meta;
    }

    private void refreshNBT()
    {
        nbtLegacy = new NBTItem(item);
        if(NMS.is_v1_1_20_5_or_greater)
            nbt = new NItem(item);
    }

    public NBTItem nbtLegacy()
    {
        if(nbtLegacy != null)
            return nbtLegacy;
        refreshNBT();
        return nbtLegacy;
    }

    public NItem nbt()
    {
        if(nbt != null)
            return nbt;
        refreshNBT();
        return nbt;
    }

    /**
     * Shorthand to call saveMetaChanges and avoid deprecation warning 
     */
    @SuppressWarnings("deprecation")
    public void saveNbt()
    {
//        nbt.saveMetaChanges(); // TODO: idk if I need to somehow save the data or not, probably new NBT api lib doesn't require it anymore.
        if(nbt != null)
            nbt.save();
    }

    public void applyMeta()
    {
        this.item.setItemMeta(this.meta);
        refreshNBT();
    }

    public boolean hasChanged()
    {
        if(hasChanged_cached != null)
            return hasChanged_cached;
        if(originalItem == null)
            return false;

        hasChanged_cached = !originalItem.equals(item);
        return hasChanged_cached;
    }

    public void refreshAll()
    {
        refreshNBT();
        refreshMeta();
    }
}
