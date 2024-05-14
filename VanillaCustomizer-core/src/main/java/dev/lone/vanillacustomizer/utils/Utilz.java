package dev.lone.vanillacustomizer.utils;

import dev.lone.LoneLibs.chat.Comp;
import lonelibs.net.kyori.adventure.text.Component;
import lonelibs.net.kyori.adventure.text.format.TextColor;
import lonelibs.net.kyori.adventure.text.format.TextDecoration;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.reflect.FieldUtils;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.CharacterIterator;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.text.StringCharacterIterator;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Utilz
{
    public static Random random = new Random();

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("##.00");
    private static final Pattern PATTERN_HEX = Pattern.compile("%#([A-Fa-f0-9]){6}%");
    private static final Pattern PATTERN_IS_NUMERIC = Pattern.compile("-?\\d+(\\.\\d+)?");

    public static void removeEnchantments(ItemStack item)
    {
        item.getEnchantments().keySet().forEach(item::removeEnchantment);
    }

    public static String addSpace(String s)
    {
        return s.replace("-", " ");
    }

    public static float getRandom(String level)
    {
        if (level.contains("-"))
        {
            String[] spl = level.split("-");
            return round(randomNumber(Float.parseFloat(spl[0]), Float.parseFloat(spl[1])), 2);
        }
        else return Integer.parseInt(level);
    }

    public static int getRandomInt(String level)
    {
        if (level.contains("-"))
        {
            String[] spl = level.split("-");
            return getRandomInt(Integer.parseInt(spl[0]), Integer.parseInt(spl[1]));
        }
        else return Integer.parseInt(level);
    }

    public static float round(float d, int decimalPlace)
    {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();
    }

    public static String round(double value, int places, boolean showZeroes)
    {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);

        String result = bd.doubleValue() + "";
        if (!showZeroes)
            result = result
                    .replace(".00", "")
                    .replace(".0", "");
        return result;
    }

    public static float randomNumber(float f, float g)
    {
        return random.nextFloat() * (g - f) + f;
    }

    /**
     * Returns a 0 to 1 random double.
     *
     * @return A random double from 0 to 1.
     */
    public static double randomNumber()
    {
        return random.nextDouble();
    }

    /**
     * Supports negative min
     */
    public static int getRandomInt(int min, int max)
    {
        if (max == min)
            return max;
        return random.nextInt((max - min) + 1) + min;
    }

    /**
     * Supports negative min
     */
    public static int getRandomInt(Random random, int min, int max)
    {
        if (max == min)
            return max;
        return random.nextInt((max - min) + 1) + min;
    }

    public static boolean getSuccess(int percent)
    {
        int i = getRandomInt(1, 100);
        return i <= percent;
    }

    public static boolean hasPermmision(Player p, String perm)
    {
        if (p.hasPermission(perm)) return true;
        return p.isOp();
    }

    public static String backColor(String name)
    {
        return name.replace("\u00A7", "&");
    }

    public static boolean isColored(String str)
    {
        return str.contains("\u00A7([0-fk-or])") || str.contains("&([0-fk-or])");
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

    private static java.awt.Color hex2Rgb(String colorStr)
    {
        try
        {
            return new java.awt.Color(
                    Integer.valueOf(colorStr.substring(1, 3), 16),
                    Integer.valueOf(colorStr.substring(3, 5), 16),
                    Integer.valueOf(colorStr.substring(5, 7), 16)
            );
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public static Color hexToBukkitColor(String colorStr)
    {
        if (colorStr == null)
            return null;
        //fix java.lang.NumberFormatException: For input string: ".0"
        if (colorStr.endsWith(".0"))
            colorStr = colorStr.replace(".0", "");
        colorStr = colorStr.replace(".", "");
        //fix java.lang.StringIndexOutOfBoundsException: String index out of range: 7
        while (colorStr.length() < 7)
            colorStr = colorStr + "0";
        if (!colorStr.startsWith("#"))
            colorStr = "#" + colorStr;

        java.awt.Color javaColor = hex2Rgb(colorStr);
        return Color.fromRGB(javaColor.getRed(), javaColor.getGreen(), javaColor.getBlue());
    }

    public static String colorToHexString(int rgb)
    {
        java.awt.Color tmp = new java.awt.Color(rgb);
        return colorToHexString(tmp.getRed(), tmp.getGreen(), tmp.getBlue());
    }

    public static String colorToHexString(Color color, boolean hashtagPrefix)
    {
        java.awt.Color tmp = new java.awt.Color(color.asRGB());
        return colorToHexString(tmp.getRed(), tmp.getGreen(), tmp.getBlue(), hashtagPrefix);
    }

    public static String colorToHexString(int r, int g, int b)
    {
        return colorToHexString(r, g, b, true);
    }

    public static String colorToHexString(int r, int g, int b, boolean hashtagPrefix)
    {
        if (hashtagPrefix)
            return String.format("#%02x%02x%02x", r, g, b);
        return String.format("%02x%02x%02x", r, g, b);
    }

    public static Color bukkitColor(int rgb)
    {
        java.awt.Color tmp = new java.awt.Color(rgb);
        return Color.fromRGB(tmp.getRed(), tmp.getGreen(), tmp.getBlue());
    }

    public static int ceilDivision(float a, float b)
    {
        return (int) Math.ceil(a / b);

    }

    public static int parseInt(String number, int defaultValue)
    {
        try
        {
            return Integer.parseInt(number);
        }
        catch (Throwable ignored){}
        return defaultValue;
    }

    public static float parseFloat(String number, float defaultValue)
    {
        try
        {
            return Float.parseFloat(number);
        }
        catch (Throwable ignored){}
        return defaultValue;
    }

    public static Color bukkitColorFromString(String potionColorStr)
    {
        try
        {
            return (Color) FieldUtils.readStaticField(Color.class, potionColorStr);
        }
        catch (Exception e)
        {
            try
            {
                if (!Utilz.isNumeric(potionColorStr))
                {
                    if (potionColorStr == null)
                        return null;
                    //fix java.lang.NumberFormatException: For input string: ".0"
                    if (potionColorStr.endsWith(".0"))
                        potionColorStr = potionColorStr.replace(".0", "");
                    potionColorStr = potionColorStr.replace(".", "");
                    //fix java.lang.StringIndexOutOfBoundsException: String index out of range: 7
                    while (potionColorStr.length() < 7)
                        potionColorStr = potionColorStr + "0";
                    if (!potionColorStr.startsWith("#"))
                        potionColorStr = "#" + potionColorStr;

                    java.awt.Color javaColor = hex2Rgb(potionColorStr);
                    return Color.fromRGB(javaColor.getRed(), javaColor.getGreen(), javaColor.getBlue());
                }
                else
                {
                    Color.fromBGR(Integer.parseInt(potionColorStr));
                }
            }
            catch (Throwable ignored)
            {
            }
        }
        return null;
    }

    public static int bukkitColorFromString_integer(String potionColorStr)
    {
        try
        {
            return ((Color) FieldUtils.readStaticField(Color.class, potionColorStr)).asRGB();
        }
        catch (Exception e)
        {
            try
            {
                if (!Utilz.isNumeric(potionColorStr))
                {
                    if (potionColorStr == null)
                        return -1;
                    //fix java.lang.NumberFormatException: For input string: ".0"
                    if (potionColorStr.endsWith(".0"))
                        potionColorStr = potionColorStr.replace(".0", "");
                    potionColorStr = potionColorStr.replace(".", "");
                    //fix java.lang.StringIndexOutOfBoundsException: String index out of range: 7
                    while (potionColorStr.length() < 7)
                        potionColorStr = potionColorStr + "0";
                    if (!potionColorStr.startsWith("#"))
                        potionColorStr = "#" + potionColorStr;

                    java.awt.Color javaColor = hex2Rgb(potionColorStr);
                    return Color.fromRGB(javaColor.getRed(), javaColor.getGreen(), javaColor.getBlue()).asRGB();
                }
                else
                {
                    return Integer.parseInt(potionColorStr);
                }
            }
            catch (Throwable ignored){}
        }
        return -1;
    }

    public static boolean isBetween(int number, int min, int max, boolean equals)
    {
        if (equals)
            return number >= min && number <= max;
        return number > min && number < max;
    }

    public static boolean isBetween(int number, int min, int max)
    {
        return isBetween(number, min, max, true);
    }

    public static String getDateNowFormatted()
    {
        return getDateFormatted(new Date());
    }

    public static String getDateFormatted(Date date)
    {
        return new SimpleDateFormat("yyyy-MM-dd_HH-mm").format(date);
    }

    public static void printFunctionCaller()
    {
        try
        {
            StackTraceElement[] stackTrace = new Throwable().fillInStackTrace().getStackTrace();
            StringBuilder a = new StringBuilder();
            for (int i = 1; i < stackTrace.length; i++)
                a.append(stackTrace[i].getMethodName() + ":" + stackTrace[i].getLineNumber()).append("->");
            System.out.println(a.toString());
        }
        catch (Throwable ignored)
        {
        }
    }

    public static void showBook(Player player, String json)
    {
        ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
        Bukkit.getUnsafe().modifyItemStack(book, json);
        player.openBook(book);
    }

    public static int rgb(int r, int g, int b)
    {
        return new java.awt.Color(r, g, b, 255).getRGB();
    }

    public static boolean mkdirs(String path, boolean printErrors)
    {
        File file = new File(path);
        if (file.exists())
            return true;

        try
        {
            Files.createDirectories(file.toPath());
        }
        catch (Throwable e)
        {
            if (printErrors)
                e.printStackTrace();
            return false;
        }

        return true;
    }

    public static <T, E> Set<T> getKeysByValue(Map<T, E> map, E value)
    {
        return map.entrySet()
                .stream()
                .filter(entry -> Objects.equals(entry.getValue(), value))
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }

    /** See: {@link Utilz#partialFilePath(String)}. */
    public static String partialFilePath(File file)
    {
        return partialFilePath(file.getAbsolutePath());
    }

    /**
     * Input:
     * "C:/Progetti/Minecraft/TestServer/1.19/plugins/ItemsAdder/contents/_iainternal/resourcepack/assets/_iainternal/textures/icons/icon_next_orange.png"
     * "C:/Progetti/Minecraft/TestServer/blank_template/1.19.4/plugins/ModelEngine/resource pack/contents/prova/123/asd"
     * Output:
     * "contents/_iainternal/resourcepack/assets/_iainternal/textures/icons/icon_next_orange.png"
     * "resource pack/contents/prova/123/asd"
     */
    public static String partialFilePath(String fileAbsolutePath)
    {
        if (fileAbsolutePath == null)
            return null;

        String finalPath = fileAbsolutePath.replace("\\", "/");

        int indexOfPlugins = finalPath.indexOf("plugins/");
        if(indexOfPlugins != -1)
            finalPath = finalPath.substring(indexOfPlugins);

        // Remove the plugins/XXX/ text
        int indexOfSlash = finalPath.indexOf("/");
        if(indexOfSlash != -1)
            finalPath = finalPath.substring(indexOfSlash + 1);
        indexOfSlash = finalPath.indexOf("/");
        if(indexOfSlash != -1)
            finalPath = finalPath.substring(indexOfSlash + 1);

        return finalPath;
    }

    public static int getFilesCount(File dir)
    {
        String[] list = dir.list();
        if (list == null)
            return 0;
        return list.length;
    }

    public static String getPathRelativeToServerRoot(File file)
    {
        String serveRootPath = Bukkit.getServer().getWorldContainer().getAbsolutePath();
        if (serveRootPath.endsWith("."))
            serveRootPath = serveRootPath.substring(0, serveRootPath.length() - 2);
        File serverRootDir = new File(serveRootPath).getParentFile();
        return file.getAbsolutePath().replace(serverRootDir.getAbsolutePath(), "");
    }

    public static Path path(String path)
    {
        return new File(path).toPath();
    }

    /**
     * Very important, zip files must use / not \\ or Minecraft won't recognize files..
     */
    public static String sanitizePath(String str)
    {
        str = str.replace("\\", "/");
        if (str.startsWith("/"))
            str = str.substring(1);
        return str;
    }

    public static String sanitizedPath(File file)
    {
        return sanitizePath(file.getAbsolutePath());
    }

    public static String removeStartsWith(String str, String starsWith)
    {
        if (str.startsWith(starsWith))
            return str.substring(starsWith.length());
        return str;
    }

    public static String removeEndsWith(String str, String endsWith)
    {
        return str.replaceFirst(endsWith + "$", "");
    }

    public static String getLastNormalizedPathEntry(String str)
    {
        return str.substring(str.lastIndexOf('/') + 1);
    }

    public static String cloneString(String str)
    {
        return new String(str.getBytes(StandardCharsets.UTF_8));
    }

    public static <T> T[] copy(T[] matrix)
    {
        return Arrays.copyOf(matrix, matrix.length);
    }

    public static String fileExtension(File file)
    {
        return FilenameUtils.getExtension(file.getAbsolutePath());
    }

    public static String fileName(File file, boolean extension)
    {
        if(extension)
            return file.getName();
        return FilenameUtils.getBaseName(file.getAbsolutePath());
    }

    public static String humanReadableByteCountSI(long bytes)
    {
        if (-1000 < bytes && bytes < 1000)
        {
            return bytes + " B";
        }
        CharacterIterator ci = new StringCharacterIterator("kMGTPE");
        while (bytes <= -999_950 || bytes >= 999_950)
        {
            bytes /= 1000;
            ci.next();
        }
        return String.format("%.1f %cB", bytes / 1000.0, ci.current());
    }

    public static int minMax(int value, int min, int max)
    {
        if(value < min)
            return min;
        return Math.min(value, max);
    }

    public static String substringBefore(String string, String before)
    {
        return string.substring(string.indexOf(before));
    }

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
            case "horse.jump_strength" -> Attribute.HORSE_JUMP_STRENGTH;
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
        Component component = Comp.jsonToComponent(jsonString);
        component = component.colorIfAbsent(TextColor.color(255, 255, 255));
        component = component.decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE);
        return Comp.componentToJson(component);
    }
}