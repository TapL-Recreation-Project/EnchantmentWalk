package me.swipez.enchantmentwalk;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.logging.Level;

public class Yml {
    private final File configFile;
    private final JavaPlugin pluginInstance;
    private YamlConfiguration yamlConfig;

    /**
     * Creates a Yml object that holds a reference to a .yml file.
     * The file is not created by default
     */
    public Yml(JavaPlugin instance, String relativePath) {
        this.pluginInstance = instance;
        this.configFile = new File(
                this.pluginInstance.getDataFolder().getAbsolutePath() + File.separator + relativePath);
    }
    /**
     * Gets the YamlConfiguration from this Yml object, loading it from the file
     * if necessary.
     */
    public YamlConfiguration yamlConfig() {
        if (this.yamlConfig == null) {
            loadYamlFromFile();
        }
        return this.yamlConfig;
    }

    /**
     * Creates an empty file
     */
    public void createFile() {
        try {
            if (this.configFile.getParentFile() != null && !this.configFile.getParentFile().exists()) {
                this.configFile.getParentFile().mkdirs();
            }
            this.configFile.createNewFile();
        } catch (IOException ex) {
            pluginInstance.getLogger().log(Level.SEVERE, "Could not create file " + configFile.getName());
            ex.printStackTrace();
        }
    }

    /**
     * Saves changes to Yaml file
     */
    public void saveChanges() {
        try {
            this.yamlConfig.save(this.configFile);
        } catch (IOException ex) {
            pluginInstance.getLogger().log(Level.SEVERE, "I/O error saving to " + this.configFile.getName());
            ex.printStackTrace();
        }
    }

    /**
     * Loads Yaml configuration from the file this Yml object points to
     */
    public void loadYamlFromFile() {
        this.yamlConfig = new YamlConfiguration();
        try {
            this.yamlConfig.load(this.configFile);
        } catch (IOException ex) {
            pluginInstance.getLogger().log(Level.SEVERE, "I/O error while loading " + this.configFile.getName());
            ex.printStackTrace();
        } catch (InvalidConfigurationException ex) {
            pluginInstance.getLogger().log(Level.SEVERE, "Invalid configuration in " + this.configFile.getName());
            ex.printStackTrace();
        }

    }

    /**
     * Gets the File this Yml object points to
     */
    public File getFile() {
        return this.configFile;
    }

    /**
     * Creates a new Yml object. Its file will be copied from an existing resource
     */
    public static Yml fromResource(JavaPlugin plugin,  String relativePath, String resourceUri) {
        Yml ret = new Yml(plugin, relativePath);
        ret.createFile();
        InputStream resourceStream = plugin.getResource(resourceUri);
        try {
            Files.copy(resourceStream, ret.getFile().toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            plugin.getLogger().log(Level.SEVERE, "ERROR: could not create " + ret.getFile().toPath());
            ex.printStackTrace();
        }
        return ret;
    }
}
