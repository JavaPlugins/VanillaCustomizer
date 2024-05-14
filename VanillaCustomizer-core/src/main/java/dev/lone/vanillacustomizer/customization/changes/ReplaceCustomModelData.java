package dev.lone.vanillacustomizer.customization.changes;

import dev.lone.LoneLibs.nbt.nbtapi.NBTItem;
import dev.lone.vanillacustomizer.ChangeSession;

public class ReplaceCustomModelData implements IChange
{
    private final int id;

    public ReplaceCustomModelData(int id)
    {
        this.id = id;
    }

    @Override
    public void apply(ChangeSession session)
    {
        NBTItem nbt = session.nbt();
        nbt.setInteger("CustomModelData", id);

        session.saveNbt();
    }
}
