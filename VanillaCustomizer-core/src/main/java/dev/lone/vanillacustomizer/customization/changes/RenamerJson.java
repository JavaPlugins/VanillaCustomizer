package dev.lone.vanillacustomizer.customization.changes;

import dev.lone.LoneLibs.nbt.nbtapi.NBTCompound;
import dev.lone.LoneLibs.nbt.nbtapi.NBTItem;
import dev.lone.vanillacustomizer.ChangeSession;
import dev.lone.LoneLibs.chat.Comp;
import dev.lone.vanillacustomizer.utils.Utilz;

public class RenamerJson implements IChange
{
    private final String json;

    public RenamerJson(String json)
    {
        // This should validate if the json is valid and throw an exception if not.
        // NOTE: test if it is actually the case.
        Comp.jsonToComponent(json);

        this.json = Utilz.fixJsonFormatting(json);
    }

    @Override
    public void apply(ChangeSession session)
    {
        NBTItem nbt = session.nbt();
        NBTCompound display = nbt.getOrCreateCompound("display");
        display.setString("Name", IChange.replacePlaceholders(session, json));

        session.saveNbt();
    }
}
