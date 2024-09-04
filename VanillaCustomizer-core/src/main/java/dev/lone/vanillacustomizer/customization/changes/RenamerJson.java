package dev.lone.vanillacustomizer.customization.changes;

import dev.lone.LoneLibs.nbt.nbtapi.NBTCompound;
import dev.lone.LoneLibs.nbt.nbtapi.NBTItem;
import dev.lone.vanillacustomizer.ChangeSession;
import dev.lone.LoneLibs.chat.Comp;
import dev.lone.vanillacustomizer.nms.NMS;
import dev.lone.vanillacustomizer.utils.Utils;
import lonelibs.dev.lone.fastnbt.nms.nbt.NItem;

public class RenamerJson implements IChange
{
    private final String json;

    public RenamerJson(String json)
    {
        // This should validate if the json is valid and throw an exception if not.
        // NOTE: test if it is actually the case.
        Utils.jsonToComponent(json);

        this.json = Utils.fixJsonFormatting(json);
    }

    @Override
    public void apply(ChangeSession session)
    {
        if(NMS.is_v1_1_20_5_or_greater)
        {
            NItem nbt = session.nbt();
            String json = IChange.replacePlaceholders(session, this.json);
            nbt.setCustomName(json);
            nbt.save();
            return;
        }

        NBTItem nbt = session.nbtLegacy();
        NBTCompound display = nbt.getOrCreateCompound("display");
        display.setString("Name", IChange.replacePlaceholders(session, json));

        session.saveNbt();
    }
}
