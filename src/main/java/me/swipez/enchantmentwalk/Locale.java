package me.swipez.enchantmentwalk;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.stream.Stream;

public class Locale {
    private Yml currentLocale;
    private final Yml localeConfig;
    private String localeCode;
    private final JavaPlugin plugin;
    private HashMap<String, String> messageCache = new HashMap<String, String>();

    public Locale(JavaPlugin plugin, Yml localeConfig) {
        this.localeConfig = localeConfig;
        this.plugin = plugin;
        Yml defaultEnLocale = new Yml(plugin, "locales/locale_en.yml");
        if (!defaultEnLocale.getFile().exists()) {
            defaultEnLocale = Yml.fromResource(plugin,"locales/locale_en.yml", "locales/locale_en.yml");
        }
        YamlConfiguration localeConfigYamlData = localeConfig.yamlConfig();
        if (localeConfigYamlData != null) {
            this.localeCode = localeConfigYamlData.getString("locale");
        }
        exportAllResourceLocales();
        if (this.localeCode == null) {
            plugin.getLogger().log(Level.SEVERE, "Could not find valid locale specified in locale.yml (is the file corrupted or missing?)");
            this.localeCode = "en";
            currentLocale = defaultEnLocale;
        } else {
            currentLocale = new Yml(plugin, "locales/locale_" + localeCode + ".yml");
            if (!currentLocale.getFile().exists()) {
                plugin.getLogger().log(Level.WARNING, "Could not find file " + currentLocale.getFile().getName() + " for specified locale, falling back to default");
                currentLocale = defaultEnLocale;
            }
        }
        buildMessageCache();
    }

    public void reloadConfig() {
        localeConfig.loadYamlFromFile();
        this.localeCode = localeConfig.yamlConfig().getString("locale");
        currentLocale = currentLocale = new Yml(plugin, "locales/locale_" + localeCode + ".yml");
        buildMessageCache();
    }

    public String string(String id) {
        if (messageCache.containsKey(id)) {
            return ChatColor.translateAlternateColorCodes('&', messageCache.get(id));
        } else {
            return id;
        }
    }

    private void buildMessageCache() {
        messageCache.clear();
        YamlConfiguration currentLocaleYamlData = this.currentLocale.yamlConfig();
        if (currentLocaleYamlData == null || !currentLocale.getFile().exists()) return;
        for (String key : currentLocaleYamlData.getKeys(true)) {
            messageCache.put(key, currentLocaleYamlData.getString(key));
        }
    }

    private void exportAllResourceLocales() {
        Iterator<Path> pathIterator = getAllPathsWithinLocales();
        if (pathIterator == null) {
            return;
        }
        while (pathIterator.hasNext()) {
            Path localePath = pathIterator.next();
            InputStream resourceStream = plugin.getResource(localePath.toString().replace("/locales", "locales"));
            if (resourceStream == null) {
                plugin.getLogger().log(Level.SEVERE, "Could not load resource " + localePath.toString());
                continue;
            }
            try {
                File outFile = new File(plugin.getDataFolder() + "/locales/" + localePath.getFileName());
                if (outFile.exists()) continue;
                Path outPath = outFile.toPath();
                Files.copy(resourceStream, outPath, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException ex) {
                plugin.getLogger().log(Level.SEVERE, "Could not copy to " + localePath.toString());
                ex.printStackTrace();
            }
        }
    }

    private Iterator<Path> getAllPathsWithinLocales() {
        URI uri;
        try {
            uri = this.plugin.getClass().getResource("/locales").toURI();
        } catch (URISyntaxException ex) {
            plugin.getLogger().log(Level.SEVERE, "Got invalid URI Syntax from /locales folder");
            return null;
        }
        Path localesPath;
        if (uri.getScheme().equals("jar")) {
            FileSystem fileSystem;
            try {
                fileSystem = FileSystems.getFileSystem(uri);
            } catch (FileSystemNotFoundException ignored) {
                try {
                    fileSystem = FileSystems.newFileSystem(uri, new HashMap<String, Object>());
                } catch (IOException ex) {
                    plugin.getLogger().log(Level.SEVERE, "Error enumerating resources");
                    ex.printStackTrace();
                    return null;
                }
            }
            localesPath = fileSystem.getPath("/locales");
        } else {
            localesPath = Paths.get(uri);
        }
        Stream<Path> walker;
        try {
            walker = Files.walk(localesPath, 1);
        } catch (IOException ex) {
            plugin.getLogger().log(Level.SEVERE, "Error enumerating resources");
            ex.printStackTrace();
            return null;
        }
        return walker.iterator();
    }
}