package dev.lone.vanillacustomizer.commands.registered;

import dev.lone.vanillacustomizer.Main;
import dev.lone.vanillacustomizer.commands.CommandRun;
import dev.lone.vanillacustomizer.utils.SmallCaps;
import fr.mrmicky.fastinv.FastInv;
import org.jetbrains.annotations.NotNull;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.List;
import java.util.stream.IntStream;

public class MainCommand extends CommandRun
{
    HashMap<Player, Integer> playerPage = new HashMap<>();

    public MainCommand(JavaPlugin plugin, String name)
    {
        super(plugin, name);
    }

    public static boolean hasDebugTag(Player player)
    {
        return player.hasMetadata("showdebugtag");
    }

    @Override
    public void run(Player player, Command command, String label, String[] args)
    {
        if(args.length >= 1)
        {
            switch (args[0])
            {
                case "showdebugtag":
                    boolean showdebugtag = hasDebugTag(player);
                    if (showdebugtag)
                        Main.msg.send(player, "Disabled debug tag");

                    else
                        Main.msg.send(player, "Enabled debug tag");

                    if (!showdebugtag)
                        player.setMetadata("showdebugtag", new FixedMetadataValue(Main.inst(), 0));
                    else
                        player.removeMetadata("showdebugtag", Main.inst());

                    // To trigger refresh
                    player.updateInventory();
                    break;

                case "small":
                    if(args.length < 2)
                    {
                        Main.msg.send(player, "Usage: /vanillacustomizer small <text>");
                        return;
                    }
                    String text = args[1];
                    Main.msg.send(player, SmallCaps.apply(text));
                    break;
                case "debugmenu":

                    FastInv inv = new FastInv(54);

                    int page = 0;
                    playerPage.put(player, page);
                    loadPage(inv, page);

                    inv.setItem(52, new ItemStack(Material.WHITE_CONCRETE), e -> {
                        // todo check >= 0
                        playerPage.put(player, playerPage.get(player) - 1);
                        loadPage(inv, playerPage.get(player));
                    });

                    inv.setItem(53, new ItemStack(Material.RED_CONCRETE), e -> {
                        playerPage.put(player, playerPage.get(player) + 1);
                        loadPage(inv, playerPage.get(player));
                    });

                    inv.open(player);

                    break;
                case "reload":
                    Main.inst().reload();
                    Bukkit.getOnlinePlayers().forEach(Player::updateInventory);
                    Main.msg.send(player, "Reloaded configs.");
                    break;
            }
        }
    }

    @Override
    public void run(ConsoleCommandSender sender, Command command, String label, String[] args)
    {
        if(args.length >= 1)
        {
            switch (args[0])
            {
                case "reload":
                    Main.inst().reload();
                    Bukkit.getOnlinePlayers().forEach(Player::updateInventory);
                    Main.msg.send(sender, "Reloaded configs.");
                    return;
            }
        }

        sender.sendMessage("This command can be run only by players!");
    }

    private void loadPage(FastInv inv, int page)
    {
        inv.removeItems(IntStream.rangeClosed(0, 44).toArray());
        Material[] values = Material.values();
        int j = 0;
        for (int i = page * 45; j < 45 && i < values.length; i++)
        {
            Material mat = values[i];
            if (mat.isItem())
            {
                inv.addItem(new ItemStack(mat));
                j++;
            }
        }
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, Command command, @NotNull String alias, @NotNull String[] args)
    {
        if(args.length == 1)
            return List.of("reload", "showdebugtag", "small", "debugmenu");
        return List.of();
    }
}
