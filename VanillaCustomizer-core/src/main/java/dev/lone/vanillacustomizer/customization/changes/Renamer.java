package dev.lone.vanillacustomizer.customization.changes;

import dev.lone.LoneLibs.nbt.nbtapi.NBTCompound;
import dev.lone.LoneLibs.nbt.nbtapi.NBTItem;
import dev.lone.vanillacustomizer.ChangeSession;
import dev.lone.LoneLibs.chat.Comp;
import dev.lone.vanillacustomizer.utils.ConfigFile;
import org.bukkit.ChatColor;

public class Renamer implements IChange
{
    private final String name;

    public Renamer(String name)
    {
        name = ConfigFile.convertColor(name);
        this.name = !name.startsWith("&f") ? ChatColor.WHITE + name : name;
    }

    @Override
    public void apply(ChangeSession session)
    {
        NBTItem nbt = session.nbt();
        NBTCompound display = nbt.getOrCreateCompound("display");
        display.setString("Name", Comp.legacyToJson(IChange.replacePlaceholders(session, name)));

        session.saveNbt();
    }
}
