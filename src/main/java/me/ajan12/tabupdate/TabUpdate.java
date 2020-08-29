package me.ajan12.tabupdate;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class TabUpdate extends JavaPlugin {

    //The field to get the inner methods of this class from other classes.
    private static TabUpdate instance;
    private static Permission perms;
    private static ProtocolManager protocol;
    private static Chat chat;
    private static int taskId;
    private int iteration;

    @Override
    public void onEnable() {
        //Checking if the server has Vault
        if (getServer().getPluginManager().getPlugin("Vault") == null || !getServer().getPluginManager().getPlugin("Vault").isEnabled()) {

            //Sending a message to console indicating that this plugin needs Vault.
            Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "[" + ChatColor.GOLD + "TabUpdate" + ChatColor.AQUA + "]" + ChatColor.DARK_RED + " The plugin " + ChatColor.YELLOW + "Vault" + ChatColor.DARK_RED + " is needed for this plugin!");
            //Sending a message to console indication that this plugin isn't being enabled.
            Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "[" + ChatColor.GOLD + "TabUpdate" + ChatColor.AQUA + "]" + ChatColor.DARK_RED + " Disabling this plugin.");

            //Aborting the plugin initialization.
            return;
        }
        //Checking if the server has ProtocolLib
        if (getServer().getPluginManager().getPlugin("ProtocolLib") == null || !getServer().getPluginManager().getPlugin("ProtocolLib").isEnabled()) {

            //Sending a message to console indicating that this plugin needs ProtocolLib.
            Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "[" + ChatColor.RED + "AC" + ChatColor.AQUA + "]" + ChatColor.DARK_RED + " The plugin " + ChatColor.YELLOW + "ProtocolLib" + ChatColor.DARK_RED + " is needed for this plugin!");
            //Sending a message to console indication that this plugin isn't being enabled.
            Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "[" + ChatColor.RED + "AC" + ChatColor.AQUA + "]" + ChatColor.DARK_RED + " Disabling this plugin.");

            //Aborting the plugin initialization.
            return;
        }


        //Initializing the instance.
        instance = this;
        //Saving the default config options.
        saveDefaultConfig();

        //Initializing the protocol.
        protocol = ProtocolLibrary.getProtocolManager();
        //Initializing the perms.
        RegisteredServiceProvider<Permission> rspPerms = getServer().getServicesManager().getRegistration(Permission.class);
        perms = rspPerms.getProvider();
        //Initializing the chat
        RegisteredServiceProvider<Chat> rspChat = getServer().getServicesManager().getRegistration(Chat.class);
        chat = rspChat.getProvider();

        //Registering the command.
        getCommand("tabupdate").setExecutor(new Command());

        //Scheduling the tab update.
        taskId = Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> {

            Handlers.handleAllPlayers();
            iteration++;
            if (iteration * getConfig().getInt("tabupdate-cycle", 5) >= 30) {
                iteration = 0;
                Handlers.currentLengths = new float[TabUpdate.getInstance().getConfig().getString("playername-preset", " ").split("%ALIGN%").length];
            }

        }, getConfig().getInt("tabupdate-cycle", 5) * 20, getConfig().getInt("tabupdate-cycle", 5) * 20).getTaskId();
    }

    @Override
    public void onDisable() {

        //Cancelling the tab update schedule.
        Bukkit.getScheduler().cancelTask(taskId);

        //Deleting all the api connections.
        instance = null;
        perms = null;
        protocol = null;
        chat = null;
    }

    static Permission getPerms() { return perms; }
    static Chat getChat() { return chat; }
    static ProtocolManager getProtocol() { return protocol; }
    static TabUpdate getInstance() { return instance; }
}
