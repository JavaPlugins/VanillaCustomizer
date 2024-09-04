package dev.lone.vanillacustomizer.customization.changes;

import dev.lone.LoneLibs.nbt.nbtapi.NBTCompound;
import dev.lone.LoneLibs.nbt.nbtapi.NBTItem;
import dev.lone.vanillacustomizer.ChangeSession;
import dev.lone.LoneLibs.chat.Comp;
import dev.lone.vanillacustomizer.nms.NMS;
import dev.lone.vanillacustomizer.utils.ConfigFile;
import lonelibs.dev.lone.fastnbt.nms.nbt.NItem;

public class ReplaceWordDisplayName implements IChange
{
    private final String from;
    private final String to;

    public ReplaceWordDisplayName(String from, String to)
    {
        this.from = ConfigFile.convertColor(from);
        this.to = ConfigFile.convertColor(to);
    }

    @Override
    public void apply(ChangeSession session)
    {
        if(session.refreshMeta() == null)
            return;
        if(!session.refreshMeta().hasDisplayName())
            return;

        String name = session.refreshMeta().getDisplayName().replace(from, to);

        if(NMS.is_v1_1_20_5_or_greater)
        {
            NItem nbt = session.nbt();
            nbt.setCustomName(Comp.legacyToJson(name));
            nbt.save();
            return;
        }

        NBTItem nbt = session.nbtLegacy();
        NBTCompound display = nbt.getOrCreateCompound("display");
        display.setString("Name", Comp.legacyToJson(name));

        session.saveNbt();
    }
}
