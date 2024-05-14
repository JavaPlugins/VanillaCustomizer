package dev.lone.vanillacustomizer.customization.changes;

import dev.lone.LoneLibs.nbt.nbtapi.NBTCompound;
import dev.lone.LoneLibs.nbt.nbtapi.NBTItem;
import dev.lone.LoneLibs.nbt.nbtapi.NBTList;
import dev.lone.vanillacustomizer.ChangeSession;
import dev.lone.vanillacustomizer.utils.Utilz;

import java.util.ArrayList;
import java.util.List;

public class LoreInsertJson implements IChange
{
    private final List<String> linesJson;
    private final int index;

    public LoreInsertJson(List<String> linesJson, int index)
    {
        this.linesJson = Utilz.fixJsonFormatting(linesJson);
        this.index = index;
    }

    @Override
    public void apply(ChangeSession session)
    {
        NBTItem nbt = session.nbt();
        NBTCompound display = nbt.getOrCreateCompound("display");

        List<String> newLinesJson = new ArrayList<>();
        for (String lineJson : linesJson)
            newLinesJson.add(IChange.replacePlaceholders(session, lineJson));

        if(!display.hasTag("Lore"))
        {
            NBTList<String> lore = display.getStringList("Lore");
            lore.addAll(newLinesJson);
        }
        else
        {
            NBTList<String> lore = display.getStringList("Lore");
            if(index < lore.size())
            {
                lore.addAll(index, newLinesJson);
            }
            else
            {
                lore.addAll(newLinesJson);
            }
        }

        session.saveNbt();
    }
}
