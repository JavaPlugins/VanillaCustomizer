package dev.lone.vanillacustomizer.customization.changes;

import dev.lone.LoneLibs.nbt.nbtapi.NBTCompound;
import dev.lone.LoneLibs.nbt.nbtapi.NBTItem;
import dev.lone.LoneLibs.nbt.nbtapi.NBTList;
import dev.lone.vanillacustomizer.ChangeSession;
import dev.lone.LoneLibs.chat.Comp;
import dev.lone.vanillacustomizer.nms.NMS;
import dev.lone.vanillacustomizer.utils.ConfigFile;
import dev.lone.vanillacustomizer.utils.Utils;
import lonelibs.dev.lone.fastnbt.nms.nbt.NItem;

import java.util.ArrayList;
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
        if(NMS.is_v1_1_20_5_or_greater)
        {
            NItem nbt = session.nbt();
            List<Object> loreNMS = nbt.getLoreCopy();
            if (loreNMS == null)
            {
                loreNMS = new ArrayList<>();
                for (String line : lines)
                    loreNMS.add(Utils.jsonToNMS(IChange.replacePlaceholders(session, line)));
            }
            else
            {
                // If the index is correctly inside the already existing lore range I can put the new lines there.
                if (index < loreNMS.size())
                {
                    int i = index;
                    for (String line : lines)
                    {
                        loreNMS.add(i, Utils.legacyToNMS(IChange.replacePlaceholders(session, line)));
                        i++;
                    }
                }
                else // If it's out of bounds I just append at the end.
                {
                    for (String line : lines)
                        loreNMS.add(Comp.legacyToJson(IChange.replacePlaceholders(session, line)));
                }
            }

            nbt.setLore(loreNMS);
            nbt.save();
            return;
        }

        NBTItem nbt = session.nbtLegacy();
        NBTCompound display = nbt.getOrCreateCompound("display");

        if (!display.hasTag("Lore"))
        {
            NBTList<String> lore = display.getStringList("Lore");
            for (String line : lines)
                lore.add(Comp.legacyToJson(IChange.replacePlaceholders(session, line)));
        }
        else
        {
            NBTList<String> lore = display.getStringList("Lore");
            // If the index is correctly inside the already existing lore range I can put the new lines there.
            if (index < lore.size())
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
        if(NMS.is_v1_1_20_5_or_greater)
        {
            NItem nbt = session.nbt();
            List<Object> loreNMS = nbt.getLoreCopy();
            if (loreNMS == null)
            {
                loreNMS = new ArrayList<>();
                loreNMS.add(Utils.legacyToNMS(line));
            }
            else
            {
                loreNMS.add(index, Utils.legacyToNMS(line));
            }

            nbt.setLore(loreNMS);
            nbt.save();
            return;
        }

        NBTItem nbt = session.nbtLegacy();
        NBTCompound display = nbt.getOrCreateCompound("display");

        NBTList<String> lore = display.getStringList("Lore");
        lore.add(index, Comp.legacyToJson(line));

        session.saveNbt();
    }
}
