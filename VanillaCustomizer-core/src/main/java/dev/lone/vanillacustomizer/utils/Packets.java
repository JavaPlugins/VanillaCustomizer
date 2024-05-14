package dev.lone.vanillacustomizer.utils;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.plugin.Plugin;

import java.util.function.Consumer;

public class Packets
{
    /**
     * Register Server->Client packet listener
     */
    public static void out(Plugin plugin, PacketType packetType, Consumer<PacketEvent> handle)
    {
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(
                PacketAdapter.params()
                        .plugin(plugin)
                        .types(packetType)
                        .listenerPriority(ListenerPriority.MONITOR)
        )
        {
            @Override
            public void onPacketSending(PacketEvent e)
            {
                if (e.isCancelled() || e.isPlayerTemporary())
                    return;

                handle.accept(e);
            }
        });
    }
}
