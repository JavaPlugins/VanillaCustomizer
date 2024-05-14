package dev.lone.vanillacustomizer.utils;

import dev.lone.LoneLibs.annotations.NotNull;

public class SmallCaps
{
    private static final char[] CHARS = "ᴀʙᴄᴅᴇꜰɢʜɪᴊᴋʟᴍɴᴏᴘǫʀsᴛᴜᴠᴡxʏᴢ".toCharArray();

    public static String apply(@NotNull String string)
    {
        final StringBuilder sb = new StringBuilder();
        final char[] chars = string.toLowerCase().toCharArray();
        for (char c : chars)
        {
            final int index = c - 'a';
            if (index >= 0 && index < CHARS.length)
                sb.append(CHARS[index]);
            else
                sb.append(c);
        }

        return sb.toString();
    }
}