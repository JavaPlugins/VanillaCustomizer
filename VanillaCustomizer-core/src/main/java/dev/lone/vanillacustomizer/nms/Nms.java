package dev.lone.vanillacustomizer.nms;

import dev.lone.LoneLibs.annotations.Nullable;
import dev.lone.LoneLibs.nbt.nbtapi.utils.MinecraftVersion;
import dev.lone.vanillacustomizer.Main;
import org.bukkit.Bukkit;

import java.lang.reflect.InvocationTargetException;

/**
 * Utility to initialize NMS wrappers and avoid Maven circular dependency problems.
 */
public class Nms
{
    public static boolean is_v18_2_or_greater;
    public static boolean is_v19_2_or_greater;
    public static boolean is_v17_or_greater;
    public static boolean is_v1_16_or_greater;

    static
    {
        is_v19_2_or_greater = MinecraftVersion.isAtLeastVersion(MinecraftVersion.MC1_19_R1);
        is_v18_2_or_greater = MinecraftVersion.isAtLeastVersion(MinecraftVersion.MC1_18_R1);
        is_v17_or_greater = MinecraftVersion.isAtLeastVersion(MinecraftVersion.MC1_17_R1);
        is_v1_16_or_greater = MinecraftVersion.isAtLeastVersion(MinecraftVersion.MC1_16_R1);
    }

    /**
     * Gets a suitable implementaion for the current Minecraft server version.
     *
     * @param implClazz   the interface class which every implementation implements.
     * @param nmsHolder   the wrapper which holds NMS methods as Singleton utility.
     * @param ignoreError If loading errors should be ignored, for example if no implementation exists. This is hacky but can
     *                    be useful in some cases.
     * @return the correct implementation.
     */
    @Nullable
    @SuppressWarnings("unchecked")
    public static <T> T findImplementation(Class<T> implClazz, Object nmsHolder, boolean ignoreError)
    {
        String nmsVersion = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];

        try
        {
            Class<?> implClass = Class.forName(nmsHolder.getClass().getPackage().getName() + ".impl." + nmsVersion);
            try
            {
                //To fix a bug: For example implementations of IBlockInfo class have BlockInfo has first param of the constructor.
                return (T) implClass.getDeclaredConstructor(nmsHolder.getClass()).newInstance(nmsHolder);
            }
            catch (NoSuchMethodException e)
            {
                return (T) implClass.getDeclaredConstructor().newInstance();
            }
        }
        catch (ClassNotFoundException | InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e)
        {
            if (ignoreError)
                return null;

            Main.msg.error("Error getting implementation for " + nmsHolder.getClass() + " - NMS " + nmsVersion);
            e.printStackTrace();

            Bukkit.getPluginManager().disablePlugin(Main.inst());
            Bukkit.shutdown();
        }
        return null;
    }
}
