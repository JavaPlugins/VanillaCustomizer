package dev.lone.vanillacustomizer.customization.changes;

import dev.lone.LoneLibs.nbt.nbtapi.NBTCompound;
import dev.lone.LoneLibs.nbt.nbtapi.NBTItem;
import dev.lone.vanillacustomizer.ChangeSession;
import dev.lone.LoneLibs.chat.Comp;
import dev.lone.vanillacustomizer.utils.ConfigFile;

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

        NBTItem nbt = session.nbt();
        NBTCompound display = nbt.getOrCreateCompound("display");
        display.setString("Name", Comp.legacyToJson(name));

        session.saveNbt();
    }
}
