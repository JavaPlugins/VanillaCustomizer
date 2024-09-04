package dev.lone.vanillacustomizer;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import dev.lone.vanillacustomizer.exception.InvalidCustomizationPropertyException;
import org.jetbrains.annotations.Nullable;
import dev.lone.vanillacustomizer.api.VanillaCustomizerApi;
import dev.lone.vanillacustomizer.commands.registered.MainCommand;
import dev.lone.vanillacustomizer.customization.changes.*;
import dev.lone.vanillacustomizer.customization.Customization;
import dev.lone.vanillacustomizer.customization.matchers.RuleNbtMatcher;
import dev.lone.vanillacustomizer.customization.rules.*;
import dev.lone.vanillacustomizer.nms.items.Rarity;
import dev.lone.vanillacustomizer.utils.*;
import org.apache.commons.io.FileUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.*;

public class Customizations
{
    public LinkedHashMap<String, Customization> customizations = new LinkedHashMap<>();

    public void load()
    {
        customizations.clear();

        for (File file : getAllYmlFiles())
        {
            ConfigFile config = new ConfigFile(Main.inst(), false, file.toPath().toString(), false, false);

            boolean ignoreInventoriesEnabled = config.getBoolean("settings.ignore_inventories.enabled");
            boolean ignoreAnyCustomInventory = ignoreInventoriesEnabled && config.getBoolean("settings.ignore_inventories.any_custom", false);
            List<String> ignoreInventoriesByTitle = ignoreInventoriesEnabled ? config.getStringList("settings.ignore_inventories.by_title"): null;

            ConfigurationSection customizationsSection = config.getConfigurationSection("customizations");
            if (customizationsSection == null) // File has no rules
                continue;

            for (String key : customizationsSection.getKeys(false))
            {
                Customization customization = new Customization(
                        ignoreInventoriesEnabled,
                        ignoreAnyCustomInventory,
                        ignoreInventoriesByTitle
                );
                ConfigurationSection section = config.getConfigurationSection("customizations." + key);
                assert section != null;

                if(!section.getBoolean("enabled", true))
                    continue;

                // TODO: rules_match: ANY | ALL
                ConfigurationSection rulesSection = section.getConfigurationSection("rules");
                if (rulesSection == null)
                {
                    Main.msg.error("Error: Customization '" + key + "' missing 'rules'. File: " + config.getPartialFilePath());
                    continue;
                }

                for (String matcherKey : rulesSection.getKeys(false))
                {
                    try
                    {
                        switch (matcherKey)
                        {
                            case "material_wildcards" ->
                            {
                                List<String> materialWildcards = rulesSection.getStringList(matcherKey);
                                RuleMaterialWildcards rule = new RuleMaterialWildcards();
                                for (String noMatch : rule.init(materialWildcards))
                                {
                                    Main.msg.warn("Warning: 'material_wildcards' -> '" + noMatch + "' of '" + key + "' has matched 0 items. File: " + config.getPartialFilePath());
                                }
                                customization.addRule(rule);
                            }
                            case "material" ->
                            {
                                String matStr = rulesSection.getString(matcherKey);
                                Material material = EnumUtil.safeGet(matStr, Material.class, null);
                                if (material == null)
                                {
                                    throwInvalidPropertyValue("material", matStr);
//                                    Main.msg.error("Error: Customization '" + key + "' has invalid 'material'. File: " + config.getPartialFilePath());
                                    break;
                                }

                                customization.addRule(new RuleMaterial(material));
                            }
                            case "materials" ->
                            {
                                List<String> matStrings = rulesSection.getStringList(matcherKey);
                                List<Material> materials = new ArrayList<>();
                                for (String matStr : matStrings)
                                {
                                    Material material = EnumUtil.safeGet(matStr, Material.class, null);
                                    if (material == null)
                                    {
                                        throwInvalidPropertyValue("material", matStr);
//                                        Main.msg.error("Error: Customization '" + key + "' has invalid 'material'. File: " + config.getPartialFilePath());
                                    }
                                    else
                                    {
                                        materials.add(material);
                                    }
                                }

                                customization.addRule(new RuleMaterials(materials));
                            }
                            case "material_regex" ->
                            {
                                String regexStr = rulesSection.getString(matcherKey);
                                RuleMaterialRegex ruleMaterialRegex = new RuleMaterialRegex(regexStr);
                                customization.addRule(ruleMaterialRegex);
                            }
                            case "nbt" ->
                            {
                                RuleNbtMatcher nbtMatcher = new RuleNbtMatcher(
                                        rulesSection.getString(matcherKey + ".path"),
                                        rulesSection.getString(matcherKey + ".value"),
                                        rulesSection.getString(matcherKey + ".type", "string")
                                );
                                customization.addRule(nbtMatcher);
                            }
                            // Matches only items which don't have custom lore and colored display name.
                            case "is_vanilla_item" ->
                            {
                                boolean isVanilla = rulesSection.getBoolean(matcherKey);
                                customization.addRule(new RuleVanillaItemMatcher(isVanilla));
                            }
                            case "rarity" ->
                            {
                                String rarityString = rulesSection.getString(matcherKey, "").toUpperCase();
                                Rarity rarity = EnumUtil.safeGet(rarityString,
                                        Rarity.class,
                                        null);

                                if(rarity == null)
                                {
                                    throwInvalidPropertyValue("rarity", rarityString);
                                    continue;
                                }
                                RuleRarity rule = new RuleRarity(rarity);
                                customization.addRule(rule);
                            }
                            case "placeable" ->
                            {
                                boolean is = rulesSection.getBoolean(matcherKey);
                                customization.addRule(new RulePlaceable(is));
                            }
                            case "hardness_greater_than" ->
                            {
                                double val = rulesSection.getDouble(matcherKey);
                                customization.addRule(new RuleHardnessGraterThan(val));
                            }
                            case "edible" ->
                            {
                                boolean val = rulesSection.getBoolean(matcherKey);
                                customization.addRule(new RuleEdible(val));
                            }
                            case "smelted" ->
                            {
                                boolean val = rulesSection.getBoolean(matcherKey);
                                customization.addRule(new RuleSmelted(val));
                            }
                        }
                    }
                    catch (Throwable ex)
                    {
                        if(ex instanceof InvalidCustomizationPropertyException)
                        {
                            Main.msg.error("Error loading customization '" + key + "'. File: " + config.getPartialFilePath());
                            Main.msg.error(ex.getMessage());
                        }
                        else
                            Main.msg.error("Error loading customization '" + key + "'. File: " + config.getPartialFilePath(), ex);
                    }
                }

                ConfigurationSection changesSection = section.getConfigurationSection("changes");
                if (changesSection == null)
                {
                    Main.msg.error("Error: Customization '" + key + "' missing 'changes'. File: " + config.getPartialFilePath());
                    continue;
                }

                for (String changeKey : changesSection.getKeys(false))
                {
                    try
                    {
                        switch (changeKey)
                        {
                            case "rename" ->
                            {
                                String name = changesSection.getString(changeKey);
                                assert name != null;
                                customization.addChange(new Renamer(name));
                            }
                            case "rename_json" ->
                            {
                                String json = changesSection.getString(changeKey);
                                customization.addChange(new RenamerJson(json));
                            }
                            case "replace_word_display_name" ->
                            {
                                ConfigurationSection thisSection = changesSection.getConfigurationSection(changeKey);
                                if (thisSection == null)
                                    throwInvalidConfig();
                                String from = thisSection.getString("from");
                                String to = thisSection.getString("to");
                                customization.addChange(new ReplaceWordDisplayName(from, to));
                            }
                            case "lore_set" ->
                            {
                                List<String> list = changesSection.getStringList(changeKey);
                                customization.addChange(new LoreSet(list));
                            }
                            case "lore_set_json" ->
                            {
                                List<String> list = changesSection.getStringList(changeKey);
                                customization.addChange(new LoreSetJson(list));
                            }
                            case "lore_insert" ->
                            {
                                ConfigurationSection thisSection = changesSection.getConfigurationSection(changeKey);
                                if (thisSection == null)
                                    throwInvalidConfig();
                                List<String> list = thisSection.getStringList("lines");
                                int index = thisSection.getInt("index", 0);
                                customization.addChange(new LoreInsert(list, index));
                            }
                            case "lore_insert_json" ->
                            {
                                ConfigurationSection thisSection = changesSection.getConfigurationSection(changeKey);
                                if (thisSection == null)
                                    throwInvalidConfig();
                                List<String> list = thisSection.getStringList("lines");
                                int index = thisSection.getInt("index", 0);
                                customization.addChange(new LoreInsertJson(list, index));
                            }
                            case "replace_word_lore" ->
                            {
                                ConfigurationSection thisSection = changesSection.getConfigurationSection(changeKey);
                                if (thisSection == null)
                                    throwInvalidConfig();
                                String from = thisSection.getString("from");
                                String to = thisSection.getString("to");
                                customization.addChange(new ReplaceWordLore(from, to));
                            }
                            case "protect_nbt_data" ->
                            {
                                customization.addChange(new ProtectNbtData());
                            }
                            case "durability" ->
                            {
                                short amount = (short) changesSection.getInt(changeKey);
                                customization.addChange(new ReplaceDurability(amount));
                            }
                            case "custom_model_data" ->
                            {
                                int id = changesSection.getInt(changeKey);
                                customization.addChange(new ReplaceCustomModelData(id));
                            }
                            case "add_enchants" ->
                            {
                                HashMap<Enchantment, Integer> enchants = new HashMap<>();
                                List<String> enchantsStrings = changesSection.getStringList(changeKey);
                                for (String enchantStringEntry : enchantsStrings)
                                {
                                    String[] tmp = enchantStringEntry.split(":");

                                    Enchantment enchant = null;
                                    int enchantLevel = 1;
                                    if (tmp.length == 1)
                                    {
                                        enchant = Enchantment.getByName(tmp[0]);

                                        if (enchant == null)
                                            enchant = Enchantment.getByKey(NamespacedKey.minecraft(tmp[0]));
                                    }
                                    else if (tmp.length == 2)
                                    {
                                        if (Utils.isNumeric(tmp[1]))
                                        {
                                            enchant = Enchantment.getByName(tmp[0]);
                                            enchantLevel = Utils.parseInt(tmp[1], 1);

                                            if (enchant == null)
                                                enchant = Enchantment.getByKey(NamespacedKey.minecraft(tmp[0]));
                                        }
                                        else
                                        {
                                            enchant = Enchantment.getByKey(new NamespacedKey(tmp[0], tmp[1]));
                                        }
                                    }
                                    else if (tmp.length == 3)
                                    {
                                        enchant = Enchantment.getByKey(new NamespacedKey(tmp[0], tmp[1]));
                                        enchantLevel = Utils.parseInt(tmp[2], 1);
                                    }

                                    enchants.put(enchant, enchantLevel);
                                }

                                customization.addChange(new EnchantsAdd(enchants));
                            }
                            case "remove_enchants" ->
                            {
                                List<Enchantment> enchants = new ArrayList<>();
                                List<String> enchantsStrings = changesSection.getStringList(changeKey);
                                for (String enchantStringEntry : enchantsStrings)
                                {
                                    String[] tmp = enchantStringEntry.split(":");

                                    Enchantment enchant = null;
                                    if (tmp.length == 1)
                                    {
                                        enchant = Enchantment.getByName(tmp[0]);

                                        if (enchant == null)
                                            enchant = Enchantment.getByKey(NamespacedKey.minecraft(tmp[0]));
                                    }
                                    else if (tmp.length == 2)
                                    {
                                        if (Utils.isNumeric(tmp[1]))
                                        {
                                            enchant = Enchantment.getByName(tmp[0]);
                                            if (enchant == null)
                                                enchant = Enchantment.getByKey(NamespacedKey.minecraft(tmp[0]));
                                        }
                                        else
                                        {
                                            enchant = Enchantment.getByKey(new NamespacedKey(tmp[0], tmp[1]));
                                        }
                                    }
                                    else if (tmp.length == 3)
                                    {
                                        enchant = Enchantment.getByKey(new NamespacedKey(tmp[0], tmp[1]));
                                    }

                                    enchants.add(enchant);
                                }

                                customization.addChange(new EnchantsRemove(enchants));
                            }
                            case "add_attributes" ->
                            {
                                Multimap<Attribute, AttributeModifier> modifiers = ArrayListMultimap.create();
                                ConfigurationSection thisSection = changesSection.getConfigurationSection(changeKey);
                                if (thisSection == null)
                                    throwInvalidConfig();

                                Set<String> attributeEntriesKeys = thisSection.getKeys(false);
                                for (String attributeEntryKey : attributeEntriesKeys)
                                {
                                    ConfigurationSection attributeSection = thisSection.getConfigurationSection(attributeEntryKey);
                                    if (attributeSection == null)
                                        throwInvalidConfig();

                                    @Nullable String attributeStr = getStringOrThrow(attributeSection, "attribute");
                                    Attribute attribute = Utils.strToAttributeType(attributeStr);
                                    if (attribute == null)
                                        throwInvalidPropertyValue("attribute");
                                    @Nullable String slotStr = getStringOrThrow(attributeSection, "slot");
                                    EquipmentSlot slot = Utils.strToVanillaEquipmentSlot(slotStr);
                                    if (slot == null)
                                        throwInvalidPropertyValue("slot");

                                    @Nullable String name = getStringOrThrow(attributeSection, "name");
                                    @Nullable String uuid = attributeSection.getString("uuid");
                                    @Nullable String operation = getStringOrThrow(attributeSection, "operation");
                                    @Nullable double amount = attributeSection.getDouble("amount", 0);

                                    AttributeModifier modifier = AttributesAdd.generateModifier(
                                            uuid,
                                            name,
                                            attribute,
                                            amount,
                                            operation,
                                            slot
                                    );

                                    modifiers.put(attribute, modifier);
                                }

                                customization.addChange(new AttributesAdd(modifiers));
                            }
                            case "flags" ->
                            {
                                List<String> flagsStrings = changesSection.getStringList(changeKey);
                                if (flagsStrings.size() == 0)
                                    throwInvalidPropertyValue("flags");

                                List<ItemFlag> flags = new ArrayList<>();

                                for (String flagsString : flagsStrings)
                                {
                                    ItemFlag flag = EnumUtil.safeGet(flagsString, ItemFlag.class, null);
                                    if(flag == null)
                                    {
                                        throwInvalidPropertyValue("flags", flagsString);
                                        continue;
                                    }

                                    flags.add(flag);
                                }

                                customization.addChange(new FlagsAdd(flags));
                            }
                            case "material" ->
                            {
                                Material material = EnumUtil.safeGet(changesSection.getString(changeKey, "").toUpperCase(),
                                        Material.class,
                                        null);
                                if (material == null)
                                    throwInvalidPropertyValue("material");
                                customization.addChange(new ReplaceMaterial(material));
                            }
                        }
                    }
                    catch (Throwable ex)
                    {
                        if(ex instanceof InvalidCustomizationPropertyException)
                        {
                            Main.msg.error("Error loading customization '" + key + "'. File: " + config.getPartialFilePath());
                            Main.msg.error(ex.getMessage());
                        }
                        else
                            Main.msg.error("Error loading customization '" + key + "'. File: " + config.getPartialFilePath(), ex);
                    }
                }


                if (!customization.isEmpty())
                {
                    customizations.put(key, customization);
                }
            }
        }
    }

    private String getStringOrThrow(ConfigurationSection section, String name)
    {
        @Nullable String value = section.getString(name);
        if(value == null)
            throwMissingProperty(name);
        return value;
    }

    private void throwInvalidConfig()
    {
        throw new InvalidCustomizationPropertyException("Invalid configuration. Please check the tutorials.");
    }

    private void throwMissingProperty(String name)
    {
        throw new InvalidCustomizationPropertyException("Missing property '" + name + "'.");
    }

    private void throwInvalidPropertyValue(String name)
    {
        throw new InvalidCustomizationPropertyException("Invalid value for property '" + name + "'.");
    }

    private void throwInvalidPropertyValue(String name, String value)
    {
        throw new InvalidCustomizationPropertyException("Invalid value for property '" + name + "' -> '" + value + "'.");
    }

    public void handle(ItemStack itemStack, Player player)
    {
        if (itemStack == null || itemStack.getType() == Material.AIR)
            return;

        boolean trackChanges = MainCommand.hasDebugTag(player);
        ChangeSession session = new ChangeSession(itemStack, player, trackChanges);
        for (Map.Entry<String, Customization> entry : customizations.entrySet())
        {
            Customization customization = entry.getValue();
            customization.handle(session);
        }

        // Warning! This doesn't edit the session data. Do not access use "customization" obj
        // after this line since it would result not updated with changes done by the API handlers.
        boolean apiExecuted = VanillaCustomizerApi.inst().executeHandlers(session.item, player);
        // In case the API executed any code.
        if(apiExecuted)
            session.refreshAll();

        if(trackChanges && session.hasChanged())
        {
            // TODO make it configurable
            LoreInsert.putLine(session, 0, ChatColor.GOLD + SmallCaps.apply("VANILLACUSTOMIZER"));
            //noinspection DataFlowIssue
            if(session.refreshMeta().getLore().size() > 1)
                LoreInsert.putLine(session, 1, " ");
        }
    }

    private static List<File> getAllYmlFiles()
    {
        File cosmeticsDir = new File(Main.inst().getDataFolder().getAbsolutePath() + "/contents/");
        if (!cosmeticsDir.exists())
            cosmeticsDir.mkdirs();
        return new ArrayList<>(FileUtils.listFiles(
                cosmeticsDir,
                new String[]{"yml"},
                true
        ));
    }
}
