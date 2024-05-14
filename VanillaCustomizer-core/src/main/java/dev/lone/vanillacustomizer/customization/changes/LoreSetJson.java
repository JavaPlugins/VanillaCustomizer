package dev.lone.vanillacustomizer.customization.changes;

import dev.lone.LoneLibs.nbt.nbtapi.NBTCompound;
import dev.lone.LoneLibs.nbt.nbtapi.NBTItem;
import dev.lone.LoneLibs.nbt.nbtapi.NBTList;
import dev.lone.vanillacustomizer.ChangeSession;
import dev.lone.LoneLibs.chat.Comp;
import dev.lone.vanillacustomizer.utils.Utilz;

import java.util.ArrayList;
import java.util.List;

public class LoreSetJson implements IChange
{
    private final List<String> linesJson;

    public LoreSetJson(List<String> linesJson)
    {
        this.linesJson = Utilz.fixJsonFormatting(linesJson);
    }

    @Override
    public void apply(ChangeSession session)
    {
        NBTItem nbt = session.nbt();
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
