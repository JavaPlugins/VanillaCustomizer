package dev.lone.vanillacustomizer.customization.changes;

import dev.lone.LoneLibs.nbt.nbtapi.NBTCompound;
import dev.lone.LoneLibs.nbt.nbtapi.NBTItem;
import dev.lone.LoneLibs.nbt.nbtapi.NBTList;
import dev.lone.vanillacustomizer.ChangeSession;
import dev.lone.vanillacustomizer.nms.NMS;
import dev.lone.vanillacustomizer.utils.Utils;
import lonelibs.dev.lone.fastnbt.nms.nbt.NItem;

import java.util.ArrayList;
import java.util.List;

public class LoreSetJson implements IChange
{
    private final List<String> linesJson;

    public LoreSetJson(List<String> linesJson)
    {
        this.linesJson = Utils.fixJsonFormatting(linesJson);
    }

    @Override
    public void apply(ChangeSession session)
    {
        if(NMS.is_v1_1_20_5_or_greater)
        {
            NItem nbt = session.nbt();
            List<Object> loreNMS = new ArrayList<>();
            for (String line : linesJson)
                loreNMS.add(Utils.jsonToNMS(IChange.replacePlaceholders(session, line)));
            nbt.setLore(loreNMS);
            nbt.save();
            return;
        }

        NBTItem nbt = session.nbtLegacy();
        NBTCompound display = nbt.getOrCreateCompound("display");

        display.removeKey("Lore");
        NBTList<String> lore = display.getStringList("Lore");

        List<String> newLinesJson = new ArrayList<>();
        for (String lineJson : linesJson)
            newLinesJson.add(IChange.replacePlaceholders(session, lineJson));

        lore.addAll(newLinesJson);

        session.saveNbt();
    }
}
