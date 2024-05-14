package dev.lone.vanillacustomizer.api;

import dev.lone.vanillacustomizer.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public class VanillaCustomizerApi implements Listener
{
    //<editor-fold desc="Exposed API">
    public static VanillaCustomizerApi inst()
    {
        if(inst == null)
            inst = new VanillaCustomizerApi(Main.inst());
        return inst;
    }

    public void registerHandler(Plugin plugin, CustomizerHandler handler)
    {
        handlers.computeIfAbsent(plugin, plugin1 -> new ArrayList<>()).add(handler);
    }

    @FunctionalInterface
    public interface CustomizerHandler
    {
        void call(Player player, ItemStack itemStack);
    }
    //</editor-fold>

    //<editor-fold desc="Internal methods">
    private static final HashMap<Plugin, List<CustomizerHandler>> handlers = new HashMap<>();

    private static VanillaCustomizerApi inst;

    VanillaCustomizerApi(Plugin plugin)
    {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public boolean executeHandlers(ItemStack itemStack, Player player)
    {
        if(handlers.size() == 0)
            return false;

        boolean executedAnything = false;
        for (Map.Entry<Plugin, List<CustomizerHandler>> entry : handlers.entrySet())
        {
            List<CustomizerHandler> handlers = entry.getValue();
            for (CustomizerHandler handler : handlers)
            {
                handler.call(player, itemStack);
                executedAnything = true;
            }
        }

        return executedAnything;
    }

    @EventHandler
    private void pluginUnload(PluginDisableEvent e)
    {
        handlers.remove(e.getPlugin());
    }
    //</editor-fold>
}
