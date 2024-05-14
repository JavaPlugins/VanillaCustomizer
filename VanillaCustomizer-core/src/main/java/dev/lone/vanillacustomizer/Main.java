package dev.lone.vanillacustomizer;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.Pair;
import dev.lone.LoneLibs.chat.Msg;
import dev.lone.vanillacustomizer.commands.Commands;
import dev.lone.vanillacustomizer.nms.items.ItemsNms;
import dev.lone.vanillacustomizer.utils.Packets;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public final class Main extends JavaPlugin implements Listener
{
    // Useful:
    // https://lingojam.com/MinecraftSmallFont
    // https://game8.co/games/Minecraft/archives/378457#hl_1

    //DO NOT SET AS "final" OR SPIGOT.MC won't replace it.
    public static String b = "%%__USER__%%";
    public static Msg msg;

    private static Main inst;

    public static Main inst()
    {
        return inst;
    }

    Customizations customizations;

    @Override
    public void onEnable()
    {
        inst = this;

        msg = new Msg("[VanillaCustomizer] ");

        Bukkit.getPluginManager().registerEvents(this, this);

        ItemsNms.inst().initialize();

        customizations = new Customizations();

        // https://nms.screamingsandals.org/1.19/net/minecraft/network/protocol/game/ClientboundContainerSetContentPacket.html
        Packets.out(this, PacketType.Play.Server.WINDOW_ITEMS, e -> {
            Player player = e.getPlayer();
            if(player.getGameMode() == GameMode.CREATIVE)
                return;

            PacketContainer packet = e.getPacket();
            List<ItemStack> items = packet.getItemListModifier().read(0);
            ItemStack carriedItem = packet.getItemModifier().read(0);

            for (ItemStack item : items)
            {
                customizations.handle(item, player);
            }

            customizations.handle(carriedItem, player);
        });

        // https://nms.screamingsandals.org/1.19/net/minecraft/network/protocol/game/ClientboundSetEquipmentPacket.html
        Packets.out(this, PacketType.Play.Server.ENTITY_EQUIPMENT, e -> {
            Player player = e.getPlayer();
            if(player.getGameMode() == GameMode.CREATIVE)
                return;

            PacketContainer packet = e.getPacket();
            List<Pair<EnumWrappers.ItemSlot, ItemStack>> slotsPairs = packet.getSlotStackPairLists().read(0);

            for (Pair<EnumWrappers.ItemSlot, ItemStack> pair : slotsPairs)
            {
                customizations.handle(pair.getSecond(), player);
            }
        });

        // https://nms.screamingsandals.org/1.19.2/net/minecraft/network/protocol/game/ClientboundContainerSetSlotPacket.html
        Packets.out(this, PacketType.Play.Server.SET_SLOT, e -> {
            Player player = e.getPlayer();
            if(player.getGameMode() == GameMode.CREATIVE)
                return;

            PacketContainer packet = e.getPacket();

            ItemStack itemStack = packet.getItemModifier().read(0);
            customizations.handle(itemStack, player);
        });

        // https://mappings.cephx.dev/1.20.1/net/minecraft/network/protocol/game/ClientboundMerchantOffersPacket.html
        Packets.out(this, PacketType.Play.Server.OPEN_WINDOW_MERCHANT, e -> {
            Player player = e.getPlayer();
            if(player.getGameMode() == GameMode.CREATIVE)
                return;

            PacketContainer packet = e.getPacket();
            List<MerchantRecipe> recipes = packet.getMerchantRecipeLists().read(0);
            for (int i = 0, recipesSize = recipes.size(); i < recipesSize; i++)
            {
                // NOTE: for some reason, I don't know why, if I don't clone the items they actually get edited in-game!
                // I'm sure since if I quit the server, remove the plugin and join I still see the edited recipes!
                MerchantRecipe recipe = recipes.get(i);
                List<ItemStack> ingredients = recipe.getIngredients();
                for (int j = 0, ingredientsSize = ingredients.size(); j < ingredientsSize; j++)
                {
                    ItemStack itemStack = ingredients.get(j).clone();
                    customizations.handle(itemStack, player);
                    ingredients.set(j, itemStack);
                }

                ItemStack result = recipe.getResult().clone();
                customizations.handle(result, player);

                MerchantRecipe newRecipe = new MerchantRecipe(result, recipe.getMaxUses());
                newRecipe.setUses(recipe.getUses());
                newRecipe.setDemand(recipe.getDemand());
                newRecipe.setPriceMultiplier(recipe.getPriceMultiplier());
                newRecipe.setExperienceReward(recipe.hasExperienceReward());
                newRecipe.setSpecialPrice(recipe.getSpecialPrice());
                newRecipe.setIngredients(ingredients);
                newRecipe.setVillagerExperience(recipe.getVillagerExperience());

                recipes.set(i, newRecipe);
            }

            packet.getMerchantRecipeLists().write(0, recipes);
        });

        Commands.register(this);

        reload();
    }

    public void reload()
    {
        customizations.load();
    }

    @EventHandler(ignoreCancelled = true)
    private void onGamemode(PlayerGameModeChangeEvent e)
    {
        // This is important to force the player inventory to update ( #updateInventory() triggers WINDOW_ITEMS packet)
        // so that the CREATIVE gamemode won't edit the items, as CREATIVE makes the client apply any visual change
        // to the items, the game works like that, for example custom clients can create any NBT item while in CREATIVE.
        Bukkit.getScheduler().runTaskLater(this, () -> {
            e.getPlayer().updateInventory();
        }, 5L);
    }
}
