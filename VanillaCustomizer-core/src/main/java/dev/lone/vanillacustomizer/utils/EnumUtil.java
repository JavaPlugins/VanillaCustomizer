package dev.lone.vanillacustomizer.utils;

public class EnumUtil
{
    public static <T extends Enum<T>> T safeGet(String str, Class<T> enumType, T fallback)
    {
        try
        {
            return Enum.valueOf(enumType, str);
        }
        catch (Exception e)
        {
            return fallback;
        }
    }

}
