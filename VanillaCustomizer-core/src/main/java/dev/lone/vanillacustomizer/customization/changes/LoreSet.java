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
import lonelibs.net.kyori.adventure.text.Component;

import java.util.ArrayList;
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
        if(NMS.is_v1_1_20_5_or_greater)
        {
            NItem nbt = session.nbt();
            List<Object> loreNMS = new ArrayList<>();
            for (String line : lines)
            {
                String json = Comp.legacyToJson(IChange.replacePlaceholders(session, line));
                Component component = Utils.jsonToComponent(json);
                Object nms = Comp.componentToNms(component);
                loreNMS.add(nms);
            }
            nbt.setLore(loreNMS);
            nbt.save();
            return;
        }

        NBTItem nbt = session.nbtLegacy();
        NBTCompound display = nbt.getOrCreateCompound("display");

        display.removeKey("Lore");
        NBTList<String> lore = display.getStringList("Lore");
        for (String line : lines)
            lore.add(Comp.legacyToJson(IChange.replacePlaceholders(session, line)));

        session.saveNbt();
    }
}
