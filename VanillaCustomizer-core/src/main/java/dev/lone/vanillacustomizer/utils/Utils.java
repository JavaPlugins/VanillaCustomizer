package dev.lone.vanillacustomizer.utils;

import com.google.gson.JsonSyntaxException;
import dev.lone.LoneLibs.chat.Comp;
import dev.lone.vanillacustomizer.nms.NMS;
import lonelibs.net.kyori.adventure.text.Component;
import lonelibs.net.kyori.adventure.text.format.TextColor;
import lonelibs.net.kyori.adventure.text.format.TextDecoration;
import lonelibs.org.jetbrains.annotations.NotNull;
import org.bukkit.attribute.Attribute;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.InventoryView;

import java.util.*;
import java.util.regex.Pattern;

public class Utils
{
    private static final Pattern PATTERN_IS_NUMERIC = Pattern.compile("-?\\d+(\\.\\d+)?");

    public static String backColor(String name)
    {
        return name.replace("\u00A7", "&");
    }

    /**
     * @param strNum check if it is numeric
     */
    public static boolean isNumeric(String strNum)
    {
        if (strNum == null)
            return false;
        return PATTERN_IS_NUMERIC.matcher(strNum).matches();
    }

    public static int parseInt(String number, int defaultValue)
    {
        try
        {
            return Integer.parseInt(number);
        }
        catch (Throwable ignored)
        {
        }
        return defaultValue;
    }

    // https://minecraft.wiki/w/Attribute
    public static Attribute strToAttributeType(String attributeModifier)
    {
        attributeModifier = attributeModifier.replace("minecraft:", "");
        return switch (attributeModifier)
        {
            case "generic.max_health" -> Attribute.GENERIC_MAX_HEALTH;
            case "generic.follow_range" -> Attribute.GENERIC_FOLLOW_RANGE;
            case "generic.knockback_resistance" -> Attribute.GENERIC_KNOCKBACK_RESISTANCE;
            case "generic.movement_speed" -> Attribute.GENERIC_MOVEMENT_SPEED;
            case "generic.flying_speed" -> Attribute.GENERIC_FLYING_SPEED;
            case "generic.attack_damage" -> Attribute.GENERIC_ATTACK_DAMAGE;
            case "generic.attack_knockback" -> // 1.16+
                    Attribute.GENERIC_ATTACK_KNOCKBACK;
            case "generic.attack_speed" -> Attribute.GENERIC_ATTACK_SPEED;
            case "generic.armor" -> Attribute.GENERIC_ARMOR;
            case "generic.armor_toughness" -> Attribute.GENERIC_ARMOR_TOUGHNESS;
            case "generic.luck" -> Attribute.GENERIC_LUCK;
            case "zombie.spawn_reinforcements" -> Attribute.ZOMBIE_SPAWN_REINFORCEMENTS;
            case "horse.jump_strength" ->
            {
                if (NMS.is_v1_1_20_5_or_greater)
                    yield Attribute.valueOf("HORSE_JUMP_STRENGTH");
                yield Attribute.GENERIC_JUMP_STRENGTH;
            }
            default -> null;
        };
    }

    public static EquipmentSlot strToVanillaEquipmentSlot(String str)
    {
        return switch (str.toLowerCase())
        {
            case "head" -> EquipmentSlot.HEAD;
            case "chest" -> EquipmentSlot.CHEST;
            case "legs" -> EquipmentSlot.LEGS;
            case "feet" -> EquipmentSlot.FEET;
            case "mainhand" -> EquipmentSlot.HAND;
            case "offhand" -> EquipmentSlot.OFF_HAND;
            default -> null;
        };
    }

    public static List<String> fixJsonFormatting(List<String> linesJson)
    {
        for (int i = 0, linesJsonSize = linesJson.size(); i < linesJsonSize; i++)
        {
            String jsonLine = linesJson.get(i);
            linesJson.set(i, fixJsonFormatting(jsonLine));
        }

        return linesJson;
    }

    public static String fixJsonFormatting(String jsonString)
    {
        Component component = Utils.jsonToComponent(jsonString);
        component = component.colorIfAbsent(TextColor.color(255, 255, 255));
        component = component.decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE);
        return Comp.componentToJson(component);
    }

    public static Object jsonToNMS(String json)
    {
        return Comp.componentToNms(Utils.jsonToComponent(json));
    }

    public static Object legacyToNMS(String text)
    {
        return Comp.componentToNms(jsonToComponent(Comp.legacyToJson(text)));
    }

    public static @NotNull Component jsonToComponent(String json) throws JsonSyntaxException
    {
        if ((json.startsWith("{") && json.endsWith("}") || (json.startsWith("[") && json.endsWith("]"))))
            return Comp.GSON_SERIALIZER.deserialize(json);
        return Component.text(json).style(Comp.WHITE_NORMAL_TEXT_STYLE);
    }

    public static boolean isCustomInventory(InventoryView openInventory)
    {
        return openInventory.getTopInventory().getClass().toString().endsWith("CraftInventoryCustom");
    }
}