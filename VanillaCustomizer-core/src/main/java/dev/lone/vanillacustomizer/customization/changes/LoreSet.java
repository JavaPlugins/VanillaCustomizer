package dev.lone.vanillacustomizer.customization.changes;

import dev.lone.LoneLibs.nbt.nbtapi.NBTCompound;
import dev.lone.LoneLibs.nbt.nbtapi.NBTItem;
import dev.lone.LoneLibs.nbt.nbtapi.NBTList;
import dev.lone.vanillacustomizer.ChangeSession;
import dev.lone.LoneLibs.chat.Comp;
import dev.lone.vanillacustomizer.utils.ConfigFile;

import java.util.List;

public class LoreSet implements IChange
{
    private final List<String> lines;

    public LoreSet(List<String> lines)
    {
        this.lines = ConfigFile.getColored(lines);
    }

    @Override
    public void apply(ChangeSession session)
    {
        NBTItem nbt = session.nbt();
        NBTCompound display = nbt.getOrCreateCompound("display");

        display.removeKey("Lore");
        NBTList<String> lore = display.getStringList("Lore");
        for (String line : lines)
            lore.add(Comp.legacyToJson(IChange.replacePlaceholders(session, line)));

        session.saveNbt();
    }
}
