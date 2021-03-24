package me.swipez.enchantmentwalk;

import me.swipez.enchantmentwalk.bstats.Metrics;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.UUID;

public final class EnchantmentWalk extends JavaPlugin {
    HashMap<UUID, Location> newloc = new HashMap<>();
    HashMap<UUID, Integer> numofench = new HashMap<>();
    HashMap<UUID, Integer> hundcount = new HashMap<>();
    boolean gamestarted = false;
    public Locale locale;

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new WalkListen(this), this);
        getCommand("enchantchallenge").setExecutor(new StartCommand(this));
        getCommand("enchantchallenge").setTabCompleter(new CommandComplete());
        getConfig().options().copyDefaults();
        saveDefaultConfig();
        Yml localeConfig = new Yml(this, "locales/locale.yml");
        if (!localeConfig.getFile().exists()) {
            localeConfig = Yml.fromResource(this, "locales/locale.yml", "locales/locale.yml");
        }
        locale = new Locale(this, localeConfig);
        new Metrics(this, 10430);

    }

    @Override
    public void onDisable() {

    }
}
