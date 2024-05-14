package dev.lone.vanillacustomizer.commands;

import dev.lone.LoneLibs.annotations.NotNull;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public abstract class CommandRun implements CommandExecutor, TabCompleter
{
    protected CommandRun(JavaPlugin plugin, String name)
    {
        plugin.getCommand(name).setExecutor(this);
        plugin.getCommand(name).setTabCompleter(this);
    }

    protected abstract void run(Player sender, Command command, String label, String[] args);

    protected abstract void run(ConsoleCommandSender sender, Command command, String label, String[] args);

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args)
    {
        if(sender instanceof Player)
            run((Player) sender, command, label, args);
        else if(sender instanceof ConsoleCommandSender)
            run((ConsoleCommandSender) sender, command, label, args);

        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, Command command, @NotNull String alias, @NotNull String[] args)
    {
        return List.of();
    }
}
