package dev.lone.vanillacustomizer.utils;

import org.jetbrains.annotations.NotNull;
import dev.lone.vanillacustomizer.Main;
import dev.lone.vanillacustomizer.nms.NMS;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 2020-01-08 LoneDev
 */
@SuppressWarnings("unused")
public class ConfigFile
{
    private static final Pattern hexPattern = Pattern.compile("%#([A-Fa-f0-9]){6}%");

    final Plugin plugin;

    private FileConfiguration config;
    private final String filePath;
    private final File configFile;
    private boolean hasDefaultFile = true;
    private boolean autoAddMissingProperties = false;
    private String partialFilePath;
    private boolean announceCustomLanguage;

    /**
     * @param plugin                   Plugin
     * @param filePath                 Partial path of the file (root = plugins/XXX/)
     * @param hasDefaultFile           If there is a default file in .jar resources
     * @param autoAddMissingProperties Add properties that are missing from user config (useful on plugin updates if you create new properties in the config)
     */
    public ConfigFile(Plugin plugin, boolean pluginsFolderRoot, String filePath, boolean hasDefaultFile, boolean autoAddMissingProperties)
    {
        this(plugin, pluginsFolderRoot, filePath, hasDefaultFile, autoAddMissingProperties, true);
    }

    public ConfigFile(Plugin plugin, boolean pluginsFolderRoot, String filePath, boolean hasDefaultFile, boolean autoAddMissingProperties, boolean announceCustomLanguage)
    {
        this.plugin = plugin;
        this.announceCustomLanguage = announceCustomLanguage;

        if (pluginsFolderRoot)
        {
            this.partialFilePath = filePath;
            this.filePath = plugin.getDataFolder() + File.separator + filePath;
            //this.filePath = "plugins/" + plugin.getName() + "/" + filePath;
        }
        else
        {
            this.partialFilePath = filePath.replace(plugin.getDataFolder().getAbsolutePath(), "");
            this.filePath = filePath;
        }

        this.partialFilePath = this.partialFilePath.replace("\\", "/");

        if (!new File(this.filePath).exists())
            new File(this.filePath).getParentFile().mkdirs();

        this.hasDefaultFile = hasDefaultFile;
        this.autoAddMissingProperties = autoAddMissingProperties;
        this.configFile = new File(this.filePath);

        try
        {
            config = loadConfiguration(configFile);//seems to be the cause of lag on /iareload
            initFile();
        }
        catch (IOException | InvalidConfigurationException e)
        {
            e.printStackTrace();
        }
    }

    public ConfigFile(Plugin plugin, boolean pluginsFolderRoot, String filePath, boolean hasDefaultFile)
    {
        this(plugin, pluginsFolderRoot, filePath, hasDefaultFile, false);
    }

    @NotNull
    public static YamlConfiguration loadConfiguration(@NotNull File file) throws IOException, InvalidConfigurationException
    {
        Validate.notNull(file, "File cannot be null");
        YamlConfiguration config = new YamlConfiguration();

        try
        {
            config.load(file);
        }
        catch (FileNotFoundException ignored)
        {
            // Happens the first time since the file obviously doesn't exist.
        }
        catch (IOException | InvalidConfigurationException exc)
        {
            Bukkit.getLogger().log(Level.SEVERE, "Cannot load file: " + file, exc);
            throw exc;
        }
        return config;
    }

    private ConfigFile(Plugin plugin, String filePath)
    {
        this.plugin = plugin;
        this.filePath = filePath;
        this.configFile = new File(this.filePath);
        config = YamlConfiguration.loadConfiguration(configFile);
    }

    public ConfigFile clone()
    {
        ConfigFile clone = new ConfigFile(plugin, filePath);
        clone.partialFilePath = partialFilePath;
        clone.hasDefaultFile = hasDefaultFile;
        clone.autoAddMissingProperties = autoAddMissingProperties;
        return clone;
    }

    public File getFile()
    {
        return configFile;
    }

    /**
     * Reloads from file
     */
    public void reload()
    {
        config = YamlConfiguration.loadConfiguration(configFile);
    }

    /**
     * Inits file taking it from original file in .jar if not exists and adding missing attributes if autoAddMissingProperties
     */
    public void initFile()
    {
        if (hasDefaultFile)
        {
            String resourceFilePath = this.filePath
                    .replace("plugins/" + plugin.getName() + "/", "")
                    .replace("plugins\\" + plugin.getName() + "\\", "")
                    .replace("plugins\\" + plugin.getName() + "/", "");
            try
            {
                if (!configFile.exists())
                {
                    // Load the default file from JAR resources
                    FileUtils.copyInputStreamToFile(plugin.getResource(resourceFilePath), configFile);
                }
                else
                {
                    if (autoAddMissingProperties)
                    {
                        InputStream defaultFileStream = plugin.getResource(resourceFilePath);
                        if(defaultFileStream != null)
                        {
                            FileConfiguration c = YamlConfiguration
                                    .loadConfiguration((new InputStreamReader(defaultFileStream, StandardCharsets.UTF_8)));
                            for (String k : c.getKeys(true))
                            {
                                if (!config.contains(k))
                                    config.set(k, c.get(k));
                            }
                            config.save(configFile);
                        }
                    }
                }
                config.load(configFile);
            }
            catch (IOException | InvalidConfigurationException | NullPointerException e)
            {
                //e.printStackTrace();
                //Main.msg.sendConsoleError("ERROR LOADING file '" + filePath + "'");
                if (announceCustomLanguage)
                    Main.msg.log(ChatColor.YELLOW + "Using custom language file '" + filePath + "'");
                e.printStackTrace();
            }
        }
    }

    public FileConfiguration getConfigFile()
    {
        return config;
    }

    public String getFilePath()
    {
        return filePath;
    }

    public String getPartialFilePath()
    {
        return partialFilePath;
    }

    public static String convertColor(String string)
    {
        return convertColor0(string).replace("\\n", "\n");
    }

    private static String convertColor0(String message)
    {
        Matcher matcher = hexPattern.matcher(message);
        while (matcher.find())
        {
            final net.md_5.bungee.api.ChatColor hexColor = net.md_5.bungee.api.ChatColor.of(matcher.group().substring(1, matcher.group().length() - 1));
            final String before = message.substring(0, matcher.start());
            final String after = message.substring(matcher.end());
            message = before + hexColor + after;
            matcher = hexPattern.matcher(message);
        }
        return message.replace("&", "\u00A7");
    }

    protected void notifyMissingProperty(String path)
    {
        Main.msg.error("MISSING FILE PROPERTY! '" + path + "' in file '" + filePath + "'");
    }

    public void set(String path, Object value)
    {
        this.config.set(path, value);
    }

    public void save()
    {
        try
        {
            this.config.save(configFile);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void save(File newLocation)
    {
        try
        {
            this.config.save(newLocation);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public boolean hasKey(String path)
    {
        return config.contains(path);
    }

    public void remove(String path)
    {
        config.set(path, null);
    }

    public List<String> getSectionKeysList(String path)
    {
        if (!hasKey(path))
            return new ArrayList<>();
        return new ArrayList<>(config.getConfigurationSection(path).getKeys(false));
    }

    public static List<String> getSectionKeysListBySection(ConfigurationSection section)
    {
        return getSectionKeysListBySection(section, "");
    }

    public static List<String> getSectionKeysListBySection(ConfigurationSection section, String path)
    {
        if (!section.contains(path))
            return new ArrayList<>();
        return new ArrayList<>(section.getKeys(false));
    }

    public Map<String, Object> getSectionKeysAndValues(String path)
    {
        if (!hasKey(path))
            return null;
        return config.getConfigurationSection(path).getValues(false);
    }

    public Object get(String path, Object defaultValue)
    {
        if (hasKey(path))
            return config.get(path);
        return defaultValue;
    }

    public String getString(String path)
    {
        return config.getString(path);
    }

    public String getString(String path, String defaultValue)
    {
        if (!hasKey(path))
            return defaultValue;
        return config.getString(path);
    }

    public int getInt(String path)
    {
        return config.getInt(path);
    }

    /**
     * Riturna interno se trovato, se no ritorna defaultValue
     *
     * @param path
     * @param defaultValue
     * @return
     */
    public int getInt(String path, int defaultValue)
    {
        if (hasKey(path))
            return config.getInt(path);
        return defaultValue;
    }

    /**
     * Ritorna double se trovato, se no ritorna defaultValue
     *
     * @param path
     * @param defaultValue
     * @return
     */
    public double getDouble(String path, double defaultValue)
    {
        if (hasKey(path))
            return config.getDouble(path);
        return defaultValue;
    }

    public float getFloat(String path, float defaultValue)
    {
        if (hasKey(path))
            return (float) config.getDouble(path);
        return defaultValue;
    }

    public float getFloat(String path)
    {
        return getFloat(path, 0f);
    }

    public boolean getBoolean(String path)
    {
        return config.getBoolean(path);
    }

    public boolean getBoolean(String path, boolean defaultValue)
    {
        if (hasKey(path))
            return config.getBoolean(path);
        return defaultValue;
    }

    public Material getMaterial(String path, Material defaultValue)
    {
        if (hasKey(path))
            return EnumUtil.safeGet(config.getString(path).toUpperCase(), Material.class, defaultValue);
        else
            return defaultValue;
    }

    public static Material getMaterialByString(String value, Material defaultValue)
    {
        if(value == null)
            return null;
        return EnumUtil.safeGet(value.toUpperCase(), Material.class, defaultValue);
    }

    public Material getMaterial(String path)
    {
        return getMaterial(path, null);
    }

    public PotionEffectType getPotionEffectType(String path)
    {
        if (hasKey(path))
            return PotionEffectType.getByName(config.getString(path));
        else
            return null;
    }

    @Nullable
    public static PotionEffectType getPotionEffectTypeByString(String value)
    {
        return PotionEffectType.getByName(value);
    }

    public String getColored(String path)
    {
        return getColored(path, null);
    }

    public String getColored(String path, String defaultValue)
    {
        if (hasKey(path))
        {
            return convertColor(config.getString(path));
        }
        else
        {
            //notifyMissingProperty(path);
            //return ChatColor.RED + "Error in file " + filePath;
        }
        return defaultValue;
    }

    public String getStripped(String name)
    {
        return ChatColor.stripColor(getColored(name));
    }


    public String getStrippedColors(String name)
    {
        return ChatColor.stripColor(getColored(name));
    }

    /**
     * Returns a list of strings with colors already converted to bukkit format
     */
    public static List<String> getColored(List<String> list)
    {
        ArrayList<String> colored = new ArrayList<>();
        for (String entry : list)
            colored.add(convertColor(entry));
        return colored;
    }

    /**
     * Returns a list of strings with colors already converted to bukkit format
     *
     * @param path
     * @param colored
     * @return
     */
    public List<String> getStringList(String path, boolean colored)
    {
        if (colored)
        {
            ArrayList<String> coloredList = new ArrayList<>();
            try
            {
                ArrayList<String> list = (ArrayList<String>) config.getStringList(path);
                for (String entry : list)
                    coloredList.add(convertColor(entry));
            }
            catch (NullPointerException exc)
            {
                exc.printStackTrace();
                notifyMissingProperty(path);
            }

            return coloredList;
        }
        return config.getStringList(path);
    }

    /**
     * Returns a list of strings (without converting colors)
     *
     * @param path
     * @return
     */
    public List<String> getStringList(String path)
    {
        return getStringList(path, false);
    }

    public List<Material> getMaterialList(String path)
    {
        ArrayList<Material> coloredList = new ArrayList<>();

        ArrayList<String> list = (ArrayList<String>) config.getStringList(path);
        for (String material : list)
        {
            if (Material.valueOf(material.toUpperCase()) != null)//does this even work?
                coloredList.add(Material.valueOf(material.toUpperCase()));
            else
                Main.msg.error("No material found with name " + material + ". Please check config '" + filePath + "'");
        }

        return coloredList;
    }

    @Nullable
    public ConfigurationSection getConfigurationSection(@NotNull String path)
    {
        return config.getConfigurationSection(path);
    }

    @Nullable
    public EntityType getEntityType(String key, EntityType armorStand, Consumer<String> failAction)
    {
        if (hasKey(key))
        {
            String val = getString(key).toUpperCase();
            try
            {
                return EntityType.valueOf(val);
            }
            catch (Exception exc)
            {
                failAction.accept(val);
                return null;
            }
        }
        return armorStand;
    }
}