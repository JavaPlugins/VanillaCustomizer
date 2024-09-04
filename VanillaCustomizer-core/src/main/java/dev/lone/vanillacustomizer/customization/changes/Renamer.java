package dev.lone.vanillacustomizer.customization.changes;

import dev.lone.LoneLibs.nbt.nbtapi.NBTCompound;
import dev.lone.LoneLibs.nbt.nbtapi.NBTItem;
import dev.lone.vanillacustomizer.ChangeSession;
import dev.lone.LoneLibs.chat.Comp;
import dev.lone.vanillacustomizer.nms.NMS;
import dev.lone.vanillacustomizer.utils.ConfigFile;
import lonelibs.dev.lone.fastnbt.nms.nbt.NItem;
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
        if(NMS.is_v1_1_20_5_or_greater)
        {
            NItem nbt = session.nbt();
            nbt.setCustomName(Comp.legacyToJson(IChange.replacePlaceholders(session, name)));
            nbt.save();
            return;
        }

        NBTItem nbt = session.nbtLegacy();
        NBTCompound display = nbt.getOrCreateCompound("display");
        display.setString("Name", Comp.legacyToJson(IChange.replacePlaceholders(session, name)));

        session.saveNbt();
    }
}
