package dev.lone.vanillacustomizer.customization.changes;

import dev.lone.LoneLibs.nbt.nbtapi.NBTCompound;
import dev.lone.LoneLibs.nbt.nbtapi.NBTItem;
import dev.lone.LoneLibs.nbt.nbtapi.NBTList;
import dev.lone.vanillacustomizer.ChangeSession;
import dev.lone.LoneLibs.chat.Comp;
import dev.lone.vanillacustomizer.utils.ConfigFile;
import lonelibs.net.kyori.adventure.text.Component;
import lonelibs.net.kyori.adventure.text.TextReplacementConfig;

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
        NBTItem nbt = session.nbt();
        NBTCompound display = nbt.getOrCreateCompound("display");
        if(!display.hasTag("Lore"))
            return;

        NBTList<String> lore = display.getStringList("Lore");
        for (int i = 0; i < lore.size(); i++)
        {
            String lineJson = lore.get(i);
            Component component = Comp.jsonToComponent(lineJson);
            component = component.replaceText(textReplacement);
            lore.set(i, Comp.componentToJson(component));
        }

        session.saveNbt();
    }
}
