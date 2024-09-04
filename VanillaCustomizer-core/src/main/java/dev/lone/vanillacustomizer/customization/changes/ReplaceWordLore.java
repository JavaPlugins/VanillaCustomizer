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
import lonelibs.net.kyori.adventure.text.TextReplacementConfig;

import java.util.List;

public class ReplaceWordLore implements IChange
{
    TextReplacementConfig textReplacement;

    public ReplaceWordLore(String from, String to)
    {
        from = ConfigFile.convertColor(from);
        to = ConfigFile.convertColor(to);

        textReplacement = TextReplacementConfig.builder()
                .match(from)
                .replacement(to)
                .build();
    }

    @Override
    public void apply(ChangeSession session)
    {
        if(NMS.is_v1_1_20_5_or_greater)
        {
            NItem nbt = session.nbt();
            List<Object> loreNMS = nbt.getLoreCopy();
            if(loreNMS == null)
                return;

            for (int i = 0; i < loreNMS.size(); i++)
            {
                Object lineNMS = loreNMS.get(i);
                Component component = Comp.nmsToComponent(lineNMS);
                component = component.replaceText(textReplacement);
                loreNMS.set(i, Comp.componentToNms(component));
            }
            nbt.setLore(loreNMS);
            nbt.save();
            return;
        }

        NBTItem nbt = session.nbtLegacy();
        NBTCompound display = nbt.getOrCreateCompound("display");
        if(!display.hasTag("Lore"))
            return;

        NBTList<String> lore = display.getStringList("Lore");
        for (int i = 0; i < lore.size(); i++)
        {
            String lineJson = lore.get(i);
            Component component = Utils.jsonToComponent(lineJson);
            component = component.replaceText(textReplacement);
            lore.set(i, Comp.componentToJson(component));
        }

        session.saveNbt();
    }
}
