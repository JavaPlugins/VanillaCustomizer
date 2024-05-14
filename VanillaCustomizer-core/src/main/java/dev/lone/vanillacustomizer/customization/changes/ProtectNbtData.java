package dev.lone.vanillacustomizer.customization.changes;

import dev.lone.LoneLibs.nbt.nbtapi.NBTItem;
import dev.lone.vanillacustomizer.ChangeSession;

public class ProtectNbtData implements IChange
{
    @Override
    public void apply(ChangeSession session)
    {
        NBTItem nbt = session.nbt();
        nbt.removeKey("PublicBukkitValues");
        nbt.removeKey("itemsadder");

        session.saveNbt();
    }
}
