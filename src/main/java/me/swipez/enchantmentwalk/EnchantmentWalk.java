package me.swipez.enchantmentwalk;

import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.UUID;

public final class EnchantmentWalk extends JavaPlugin {
    HashMap<UUID, Location> newloc = new HashMap<>();
    HashMap<UUID, Integer> numofench = new HashMap<>();
    HashMap<UUID, Integer> hundcount = new HashMap<>();
    boolean gamestarted = false;

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new WalkListen(this), this);
        getCommand("enchantchallenge").setExecutor(new StartCommand(this));
        getCommand("enchantchallenge").setTabCompleter(new CommandComplete());
        getConfig().options().copyDefaults();
        saveDefaultConfig();

    }

    @Override
    public void onDisable() {
    }
}
