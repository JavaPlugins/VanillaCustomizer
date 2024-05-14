package dev.lone.vanillacustomizer.commands;

import dev.lone.vanillacustomizer.commands.registered.MainCommand;
import org.bukkit.plugin.java.JavaPlugin;

public class Commands
{
    public static void register(JavaPlugin plugin)
    {
        new MainCommand(plugin, "vanillacustomizer");
    }
}
