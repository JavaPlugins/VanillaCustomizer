package dev.lone.vanillacustomizer.customization.changes;

import dev.lone.LoneLibs.nbt.nbtapi.NBTCompound;
import dev.lone.LoneLibs.nbt.nbtapi.NBTItem;
import dev.lone.LoneLibs.nbt.nbtapi.NBTList;
import dev.lone.vanillacustomizer.ChangeSession;
import dev.lone.LoneLibs.chat.Comp;
import dev.lone.vanillacustomizer.utils.ConfigFile;

import java.util.List;

public class LoreInsert implements IChange
{
    private final List<String> lines;
    private final int index;

    public LoreInsert(List<String> lines, int index)
    {
        this.lines = ConfigFile.getColored(lines);
        this.index = index;
    }

    @Override
    public void apply(ChangeSession session)
    {
        NBTItem nbt = session.nbt();
        NBTCompound display = nbt.getOrCreateCompound("display");

        if(!display.hasTag("Lore"))
        {
            NBTList<String> lore = display.getStringList("Lore");
            for (String line : lines)
                lore.add(Comp.legacyToJson(IChange.replacePlaceholders(session, line)));
        }
        else
        {
            NBTList<String> lore = display.getStringList("Lore");
            // If the index is correctly inside the already existing lore range I can put the new lines there.
            if(index < lore.size())
            {
                int i = index;
                for (String line : lines)
                {
                    lore.add(i, Comp.legacyToJson(IChange.replacePlaceholders(session, line)));
                    i++;
                }
            }
            else // If it's out of bounds I just append at the end.
            {
                for (String line : lines)
                    lore.add(Comp.legacyToJson(IChange.replacePlaceholders(session, line)));
            }
        }

        session.saveNbt();
    }

    public static void putLine(ChangeSession session, int index, String line)
    {
        NBTItem nbt = session.nbt();
        NBTCompound display = nbt.getOrCreateCompound("display");

        NBTList<String> lore = display.getStringList("Lore");
        lore.add(index, Comp.legacyToJson(line));

        session.saveNbt();
    }
}
