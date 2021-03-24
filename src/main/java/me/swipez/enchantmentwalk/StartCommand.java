package me.swipez.enchantmentwalk;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StartCommand implements CommandExecutor {
    EnchantmentWalk plugin;

    public StartCommand(EnchantmentWalk plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player){
            if (sender.hasPermission("enchantchallenge.toggle")){
                Player p = (Player) sender;
                // Arguments
                if (args.length == 1){
                    if (args[0].equals("start")){
                        for (Player others : Bukkit.getOnlinePlayers()){
                            Location block = others.getWorld().getBlockAt(others.getLocation()).getLocation();
                            plugin.newloc.put(others.getUniqueId(), block);
                            plugin.numofench.put(others.getUniqueId(), 0);
                            plugin.hundcount.put(others.getUniqueId(), 0);
                        }
                        Bukkit.broadcastMessage(plugin.locale.string("challenge-started"));
                        plugin.gamestarted = true;
                    }
                    if (args[0].equals("stop")) {
                        for (Player others : Bukkit.getOnlinePlayers()) {
                            plugin.newloc.remove(others.getUniqueId());
                        }
                        Bukkit.broadcastMessage(plugin.locale.string("challenge-ended"));
                        plugin.gamestarted = false;
                    }
                    if (args[0].equals("reload")) {
                        plugin.reloadConfig();
                        plugin.locale.reloadConfig();
                        p.sendMessage(plugin.locale.string("config-reloaded"));
                    }
                }
                else {
                    p.sendMessage(ChatColor.RED+"/enchantchallenge <start/stop>");
                }
            }
            else {
                sender.sendMessage(plugin.locale.string("no-permission"));
            }
        }
        else {
            sender.sendMessage(plugin.locale.string("player-only-command"));
        }
        return true;
    }
}
