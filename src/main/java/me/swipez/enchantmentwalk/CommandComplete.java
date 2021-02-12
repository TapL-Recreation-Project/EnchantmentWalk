package me.swipez.enchantmentwalk;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class CommandComplete implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1){
            List<String> complete = new ArrayList<>();
            complete.add("start");
            complete.add("stop");
            if (args[0].startsWith("sto")){
                complete.remove("start");
            }
            if (args[0].startsWith("sta")){
                complete.remove("stop");
            }
            return complete;
        }
        else if (args.length >= 2){
            List<String> blank = new ArrayList<>();
            return blank;
        }
        return null;
    }
}
