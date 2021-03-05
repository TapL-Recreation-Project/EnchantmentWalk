package me.swipez.enchantmentwalk;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;


public class WalkListen implements Listener {
    EnchantmentWalk plugin;

    public WalkListen(EnchantmentWalk plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerWalk(PlayerMoveEvent e){
        // Check if game has started
        if (plugin.gamestarted){
            Player p = e.getPlayer();
            Location block = p.getWorld().getBlockAt(p.getLocation()).getLocation();
            // Check if stored location is different from current
            if (plugin.newloc.get(p.getUniqueId()).getX() != block.getX() || plugin.newloc.get(p.getUniqueId()).getY() != block.getY() || plugin.newloc.get(p.getUniqueId()).getZ() != block.getZ()){
                // Inventory enchant system
                ItemStack[] inventory = p.getInventory().getContents();
                List<Integer> slotswithitems = getSlotsWithItems(p.getInventory());
                Random rand = new Random();
                int listsize = slotswithitems.size();
                if (listsize > 0){
                    int randomslot = rand.nextInt(listsize);
                    enchantInventory(inventory, plugin.getConfig().getBoolean("enchantfullinventory"), p, block, slotswithitems, randomslot);
                }
            }
        }
    }
    public static List<Integer> getSlotsWithItems(Inventory inventory){
        List<Integer> itemslots = new ArrayList<>();
        for (int i=0;i<inventory.getSize();i++){
            if (inventory.getItem(i) != null){
                itemslots.add(i);
            }
        }
        return itemslots;
    }
    public void enchantInventory(ItemStack[] inventory, Boolean configoption, Player p, Location block, List<Integer> slotswithitems, Integer randomslot){
        addOneToCounter(p, plugin);
        if (configoption){
            for (int i=0;i<40;i++){
                if (inventory[i] != null){
                    ItemStack item = inventory[i];
                    ItemMeta meta = item.getItemMeta();
                    Enchantment randEnchant = Enchantment.values()[(int) (Math.random()*Enchantment.values().length)];
                    boolean nosilktouch = plugin.getConfig().getBoolean("nosilktouch");
                    if (randEnchant.equals(Enchantment.SILK_TOUCH)){
                        if (nosilktouch){
                            randEnchant = Enchantment.CHANNELING;
                        }
                    }
                    Map<Enchantment, Integer> enchants = meta.getEnchants();
                    // Check if Enchantment already applied
                    if (enchants.containsKey(randEnchant)){
                        // Increases level if true
                        int enchvalue = enchants.get(randEnchant) + 1;
                        if (plugin.getConfig().get("enchantlimit").equals(true)){
                            meta.addEnchant(randEnchant, enchvalue, false);
                        }
                        else if (plugin.getConfig().get("enchantlimit").equals(false)){
                            try {
                                if (enchvalue >= ((int) plugin.getConfig().get("maxenchant"))){
                                    enchvalue = ((int) plugin.getConfig().get("maxenchant"));
                                }

                            }
                            catch (NullPointerException ex){
                                ex.printStackTrace();
                            }
                            meta.addEnchant(randEnchant, enchvalue, true);
                        }
                    }
                    else {
                        // Adds new Enchant if false
                        int enchvalue = 1;
                        Enchantment secondtry = Enchantment.values()[(int) (Math.random()*Enchantment.values().length)];
                        if (secondtry.equals(Enchantment.SILK_TOUCH)){
                            if (nosilktouch){
                                secondtry = Enchantment.CHANNELING;
                            }
                        }
                        meta.addEnchant(secondtry, enchvalue, false);
                    }
                    // Set Enchants
                    item.setItemMeta(meta);
                    // Store Position
                    plugin.newloc.put(p.getUniqueId(), block);
                }
            }
        }
        else {
            ItemStack item = inventory[slotswithitems.get(randomslot)];
            ItemMeta meta = item.getItemMeta();
            Enchantment randEnchant = Enchantment.values()[(int) (Math.random()*Enchantment.values().length)];
            boolean nosilktouch = plugin.getConfig().getBoolean("nosilktouch");
            if (randEnchant.equals(Enchantment.SILK_TOUCH)){
                if (nosilktouch){
                    randEnchant = Enchantment.CHANNELING;
                }
            }
            Map<Enchantment, Integer> enchants = meta.getEnchants();
            // Check if Enchantment already applied
            if (enchants.containsKey(randEnchant)){
                // Increases level if true
                int enchvalue = enchants.get(randEnchant) + 1;
                if (plugin.getConfig().get("enchantlimit").equals(true)){
                    meta.addEnchant(randEnchant, enchvalue, false);
                }
                else if (plugin.getConfig().get("enchantlimit").equals(false)){
                    try {
                        if (enchvalue >= ((int) plugin.getConfig().get("maxenchant"))){
                            enchvalue = ((int) plugin.getConfig().get("maxenchant"));
                        }

                    }
                    catch (NullPointerException ex){
                        ex.printStackTrace();
                    }
                    meta.addEnchant(randEnchant, enchvalue, true);
                }
            }
            else {
                // Adds new Enchant if false
                int enchvalue = 1;
                Enchantment secondtry = Enchantment.values()[(int) (Math.random()*Enchantment.values().length)];
                if (secondtry.equals(Enchantment.SILK_TOUCH)){
                    if (nosilktouch){
                        secondtry = Enchantment.CHANNELING;
                    }
                }
                meta.addEnchant(secondtry, enchvalue, false);
            }
            item.setItemMeta(meta);
            // Store Position
            plugin.newloc.put(p.getUniqueId(), block);
        }
    }
    public static void addOneToCounter(Player p, EnchantmentWalk plugin){
        int numofench = plugin.numofench.get(p.getUniqueId()) + 1;
        plugin.numofench.put(p.getUniqueId(), numofench);
        // Dummy Counter
        int hundcount = plugin.hundcount.get(p.getUniqueId()) + 1;
        plugin.hundcount.put(p.getUniqueId(), hundcount);
        // Checks every 100, then resets back to 0, triggering the if statement
        if (plugin.hundcount.get(p.getUniqueId()) >= 100) {
            plugin.hundcount.put(p.getUniqueId(), 0);
            p.sendMessage("[" + ChatColor.LIGHT_PURPLE + "!" + ChatColor.WHITE + "]" + " You have been enchanted " + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + plugin.numofench.get(p.getUniqueId()) + " times!");
        }
    }
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e){
        if (plugin.gamestarted){
            Player player = e.getPlayer();
            Location block = player.getWorld().getBlockAt(player.getLocation()).getLocation();
            plugin.newloc.put(player.getUniqueId(), block);
        }
    }
}
