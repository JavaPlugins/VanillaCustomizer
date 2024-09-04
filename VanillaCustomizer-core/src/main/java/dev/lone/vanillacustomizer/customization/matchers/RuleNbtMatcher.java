package dev.lone.vanillacustomizer.customization.matchers;

import dev.lone.LoneLibs.nbt.nbtapi.NBTCompound;
import dev.lone.LoneLibs.nbt.nbtapi.NBTItem;
import dev.lone.LoneLibs.nbt.nbtapi.NBTType;
import dev.lone.vanillacustomizer.customization.rules.IRule;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

public class RuleNbtMatcher implements IRule
{
    final String nbtPath;
    Object nbtValue;

    String[] nbtPathSplit;

    NBTType nbtValueType;

    public RuleNbtMatcher(String nbtPath, String nbtValueStr, String nbtValueTypeStr)
    {
        this.nbtPath = nbtPath;
        this.nbtValue = nbtValueStr;

        String nbtTypeFixed = "NBTTag" + StringUtils.capitalize(nbtValueTypeStr.toLowerCase());

        try
        {
            nbtValueType = NBTType.valueOf(nbtTypeFixed);
        }
        catch (IllegalArgumentException exc)
        {
            throw new IllegalArgumentException("Unknown nbt.type '" + nbtValueTypeStr + "' for nbt path '" + nbtPath + "'." +
                    ChatColor.GRAY + " Allowed: string, int, float, double, byte, short");
        }

        switch (nbtValueType)
        {
            case NBTTagString:
                break;
            case NBTTagInt:
                nbtValue = Integer.parseInt(nbtValueStr);
                break;
            case NBTTagFloat:
                nbtValue = Float.parseFloat(nbtValueStr);
                break;
            case NBTTagDouble:
                nbtValue = Double.parseDouble(nbtValueStr);
                break;
            case NBTTagByte:
                nbtValue = Byte.parseByte(nbtValueStr);
                break;
            case NBTTagShort:
                nbtValue = Short.parseShort(nbtValueStr);
                break;
        }

        this.nbtPathSplit = nbtPath.split("\\.");
    }

    public String getNbtPath()
    {
        return nbtPath;
    }

    public Object getNbtValue()
    {
        return nbtValue;
    }

    @Override
    public boolean matches(ItemStack item)
    {
        NBTItem nbt = new NBTItem(item);
        if (!nbt.hasTag(nbtPathSplit[0]))
            return false;

        NBTCompound currentCompound = nbt.getCompound(nbtPathSplit[0]);
        if(currentCompound == null)
            return false;
        for (int i = 1; i < nbtPathSplit.length - 1; i++)
        {
            assert currentCompound != null;
            if (!currentCompound.hasTag(nbtPathSplit[i]))
                return false;
            currentCompound = currentCompound.getCompound(nbtPathSplit[i]);
        }

        assert currentCompound != null;
        if (!currentCompound.hasTag(nbtPathSplit[nbtPathSplit.length - 1]))
            return false;

        NBTType nbtType = currentCompound.getType(nbtPathSplit[nbtPathSplit.length - 1]);
        if (nbtType != nbtValueType)
            return false;

        if (nbtType == NBTType.NBTTagString)
            return (currentCompound.getString(nbtPathSplit[nbtPathSplit.length - 1]).equals(nbtValue));
        else if (nbtType == NBTType.NBTTagInt)
            return (currentCompound.getInteger(nbtPathSplit[nbtPathSplit.length - 1]).equals(nbtValue));
        else if (nbtType == NBTType.NBTTagDouble)
            return (currentCompound.getDouble(nbtPathSplit[nbtPathSplit.length - 1]).equals(nbtValue));
        else if (nbtType == NBTType.NBTTagFloat)
            return (currentCompound.getFloat(nbtPathSplit[nbtPathSplit.length - 1]).equals(nbtValue));
        else if (nbtType == NBTType.NBTTagByte)
            return (currentCompound.getByte(nbtPathSplit[nbtPathSplit.length - 1]).equals(nbtValue));
        else if (nbtType == NBTType.NBTTagShort)
            return (currentCompound.getShort(nbtPathSplit[nbtPathSplit.length - 1]).equals(nbtValue));

        return false;
    }
}
