package me.ajan12.tabupdate;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Command implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {

        //Checking if the argument amount exceeds 1.
        if (args.length != 1) return false;

        //Checking if the 1st argument equals to "all".
        if (args[0].equalsIgnoreCase("all")) {

            //Handling all the players.
            Handlers.handleAllPlayers();

            //Feedbacking the sender.
            sender.sendMessage(ChatColor.AQUA + "[" + ChatColor.GOLD + "TabUpdate" + ChatColor.AQUA + "]" + ChatColor.GREEN + " Handling all the online players' TAB.");
            return true;
        //Checking if the 1st argument equals to "reload".
        } else if (args[0].equalsIgnoreCase("reload")) {

            //Reloading the config.
            TabUpdate.getInstance().reloadConfig();

            //Deleting the currentLengths.
            Handlers.currentLengths = null;

            //Feedbacking the sender.
            sender.sendMessage(ChatColor.AQUA + "[" + ChatColor.GOLD + "TabUpdate" + ChatColor.AQUA + "]" + ChatColor.GREEN + " Reloading the config.");
            return true;
        } else {

            //Getting the player sender specified.
            final Player player = Bukkit.getPlayer(args[0]);
            //Checking if getting the player was successful.
            if (player == null) {

                //Feedbacking the sender.
                sender.sendMessage(ChatColor.AQUA + "[" + ChatColor.GOLD + "TabUpdate" + ChatColor.AQUA + "]" + ChatColor.DARK_RED + " Couldn't find the specified player.");
                return true;
            }

            //Handling the player.
            Handlers.handlePlayer(player, true);

            //Feedbacking the sender.
            sender.sendMessage(ChatColor.AQUA + "[" + ChatColor.GOLD + "TabUpdate" + ChatColor.AQUA + "]" + ChatColor.GREEN + " Handling " + ChatColor.YELLOW + args[0] + ChatColor.GREEN + "'s TAB.");
            return true;
        }
    }
}
