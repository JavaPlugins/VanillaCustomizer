package dev.lone.vanillacustomizer;

import dev.lone.LoneLibs.nbt.nbtapi.NBTItem;
import dev.lone.LoneLibs.annotations.Nullable;
import dev.lone.vanillacustomizer.customization.rules.RuleVanillaItemMatcher;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

// TODO: find a better name for this
public class ChangeSession
{
    public final ItemStack item;
    public final ItemStack originalItem;

    public ItemMeta meta;

    public NBTItem nbt;

    public final boolean isVanilla;

    @Nullable
    private Boolean hasChanged_cached;

    public ChangeSession(ItemStack item, boolean trackChanges)
    {
        this.item = item;
        if(trackChanges)
            this.originalItem = item.clone();
        else
            this.originalItem = null;

        isVanilla = RuleVanillaItemMatcher.satisfy(item);
    }

    public ItemMeta refreshMeta()
    {
        // Meta always needs to be refreshed as Spigot caches it!
        meta = item.getItemMeta();
        return meta;
    }

    private void refreshNbt()
    {
        nbt = new NBTItem(item);
    }

    public NBTItem nbt()
    {
        if(nbt != null)
            return nbt;
        refreshNbt();
        return nbt;
    }

    /**
     * Shorthand to call saveMetaChanges and avoid deprecation warning 
     */
    @SuppressWarnings("deprecation")
    public void saveNbt()
    {
//        nbt.saveMetaChanges(); // TODO: idk if I need to somehow save the data or not, probably new NBT api lib doesn't require it anymore.
    }

    public void applyMeta()
    {
        this.item.setItemMeta(this.meta);
        refreshNbt();
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
        refreshNbt();
        refreshMeta();
    }
}
