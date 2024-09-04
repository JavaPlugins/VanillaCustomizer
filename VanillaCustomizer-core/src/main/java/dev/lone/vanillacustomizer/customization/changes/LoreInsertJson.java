package dev.lone.vanillacustomizer.customization.changes;

import dev.lone.LoneLibs.nbt.nbtapi.NBTCompound;
import dev.lone.LoneLibs.nbt.nbtapi.NBTItem;
import dev.lone.LoneLibs.nbt.nbtapi.NBTList;
import dev.lone.fastnbt_benchmark.libs.org.jetbrains.annotations.Nullable;
import dev.lone.vanillacustomizer.ChangeSession;
import dev.lone.vanillacustomizer.nms.NMS;
import dev.lone.vanillacustomizer.utils.Utils;
import lonelibs.dev.lone.fastnbt.nms.nbt.NItem;

import java.util.ArrayList;
import java.util.List;

public class LoreInsertJson implements IChange
{
    private final List<String> linesJson;
    private final int index;

    public LoreInsertJson(List<String> linesJson, int index)
    {
        this.linesJson = Utils.fixJsonFormatting(linesJson);
        this.index = index;
    }

    @Override
    public void apply(ChangeSession session)
    {
        if(NMS.is_v1_1_20_5_or_greater)
        {
            NItem nbt = session.nbt();

            List<Object> newLinesNMS = new ArrayList<>();
            for (String lineJson : linesJson)
                newLinesNMS.add(Utils.jsonToNMS(IChange.replacePlaceholders(session, lineJson)));

            @Nullable List<Object> loreNMS = nbt.getLoreCopy();
            if(loreNMS == null)
            {
                loreNMS = new ArrayList<>(newLinesNMS);
            }
            else
            {
                if (index < loreNMS.size())
                    loreNMS.addAll(index, newLinesNMS);
                else
                    loreNMS.addAll(newLinesNMS);
            }

            nbt.setLore(loreNMS);
            nbt.save();
            return;
        }

        NBTItem nbt = session.nbtLegacy();
        NBTCompound display = nbt.getOrCreateCompound("display");

        List<String> newLinesJson = new ArrayList<>();
        for (String lineJson : linesJson)
            newLinesJson.add(IChange.replacePlaceholders(session, lineJson));

        if (!display.hasTag("Lore"))
        {
            NBTList<String> lore = display.getStringList("Lore");
            lore.addAll(newLinesJson);
        }
        else
        {
            NBTList<String> lore = display.getStringList("Lore");
            if (index < lore.size())
                lore.addAll(index, newLinesJson);
            else
                lore.addAll(newLinesJson);
        }

        session.saveNbt();
    }
}
