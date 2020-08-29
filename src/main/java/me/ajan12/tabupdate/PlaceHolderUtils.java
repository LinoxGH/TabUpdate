package me.ajan12.tabupdate;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;

class PlaceHolderUtils {

    /**
     * Replaces the placeholders in the String str with values gathered from configs and the player.
     *
     * @param str   : The str to take the placeholders.
     * @param player: The player to take the values.
     * @return      : The String with replaced placeholders.
     */
    static String processPlaceHolders(String str, final Player player) {

        //Replacing player names.
        str = str
                .replaceAll("%PLAYER_NAME%", player.getName())
                .replaceAll("%PLAYER_DISPLAY_NAME%", player.getDisplayName())
                .replaceAll("%PLAYER_UUID%", player.getUniqueId().toString());

        //Getting the primary group of the player.
        final String group = TabUpdate.getPerms().getPrimaryGroup(player);
        final String prefix = TabUpdate.getChat().getPlayerPrefix(player);
        final String groupPrefix = TabUpdate.getChat().getGroupPrefix((String) null, group);

        //Replacing the prefixes.
        str = str
                .replaceAll("%PLAYER_PREFIX%", prefix.replaceAll("(&[\\dmnlkoa-f])", ""))
                .replaceAll("%PLAYER_PREFIX_COLORED%", ChatColor.translateAlternateColorCodes('&', prefix))
                .replaceAll("%PLAYER_RANK_PREFIX%", groupPrefix.replaceAll("(&[\\dmnlkoa-f])", ""))
                .replaceAll("%PLAYER_RANK_PREFIX_COLORED%", ChatColor.translateAlternateColorCodes('&', groupPrefix));

        //Getting the rank symbol of the player.
        final String symbol = TabUpdate.getInstance().getConfig().getString("rank-symbols." + group, Character.toString(group.charAt(0)));
        //Asserting symbol isn't null as we can predict it to be P or the value in the config.
        assert symbol != null;

        //Replacing the symbols.
        str = str
                .replaceAll("%PLAYER_RANK_SYMBOL%", symbol.replaceAll("(&[\\dmnlkoa-f])", ""))
                .replaceAll("%PLAYER_RANK_SYMBOL_COLORED%", ChatColor.translateAlternateColorCodes('&', symbol));

        //Getting the world values.
        final String world = player.getWorld().getName();
        final String customWorld = TabUpdate.getInstance().getConfig().getString("worlds." + world, world);
        //Asserting customWorld isn't null as we can predict it to be world's name or the value in the config.
        assert customWorld != null;

        //Replacing the worlds.
        str = str
                .replaceAll("%WORLD%", world)
                .replaceAll("%WORLD_CUSTOM%", ChatColor.translateAlternateColorCodes('&', customWorld));

        //Getting the ping of the player.
        int ping = 0;
        try {
            Object entityPlayer = player.getClass().getMethod("getHandle").invoke(player);
            ping = (int) entityPlayer.getClass().getField("ping").get(entityPlayer);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException | NoSuchFieldException e) {
            e.printStackTrace();
        }
        //Adding the colors according to the ping amount.
        final String pingStr;
        if (ping <= 0) {
            pingStr = TabUpdate.getInstance().getConfig().getString("pings.0-bar", "&4&l%PING%").replaceAll("%PING%", String.valueOf(ping));
        } else if (ping < 150) {
            pingStr = TabUpdate.getInstance().getConfig().getString("pings.5-bar", "&2%PING%").replaceAll("%PING%", String.valueOf(ping));
        } else if (ping < 300) {
            pingStr = TabUpdate.getInstance().getConfig().getString("pings.4-bar", "&a%PING%").replaceAll("%PING%", String.valueOf(ping));
        } else if (ping < 600) {
            pingStr = TabUpdate.getInstance().getConfig().getString("pings.3-bar", "&e%PING%").replaceAll("%PING%", String.valueOf(ping));
        } else if (ping < 1000) {
            pingStr = TabUpdate.getInstance().getConfig().getString("pings.2-bar", "&c%PING%").replaceAll("%PING%", String.valueOf(ping));
        } else {
            pingStr = TabUpdate.getInstance().getConfig().getString("pings.1-bar", "&4%PING%").replaceAll("%PING%", String.valueOf(ping));
        }

        //Replacing the pings.
        str = str
                .replaceAll("%PING%", ChatColor.translateAlternateColorCodes('&', pingStr))
                .replaceAll("%PING_COLORED%", ChatColor.translateAlternateColorCodes('&', pingStr));

        //Returning the string with placeholders gone.
        return str;
    }

    static float getStringWidth(String str) {

        final String[] parts = str.replaceAll("(&[\\da-fmnlko])", "$1§§").split("§§");

        //The width of this str in pixels.
        float pixel = 0;
        //Iterating over the parts of the str.
        for (String part : parts) {

            int modifier = 0;
            if (part.startsWith("&l")) modifier = 2;

            part = ChatColor.stripColor(part);
            //Iterating over the chars of word i - 1.
            for (final char c : part.toCharArray()) {
                if ('i' == c || '!' == c || '.' == c || ',' == c || ':' == c || ';' == c || '|' == c) {
                    pixel += 4.0F + modifier;
                } else if ('l' == c || '\'' == c || '`' == c) {
                    pixel += 6.0F + modifier;
                } else if (' ' == c || 'I' == c || 't' == c) {
                    pixel += 8.0F + modifier;
                } else if ('f' == c || 'k' == c || '"' == c || '(' == c || ')' == c || '{' == c || '}' == c || '[' == c || ']' == c || '*' == c || '<' == c || '>' == c) {
                    pixel += 10.0F + modifier;
                } else if ('~' == c || '@' == c) {
                    pixel += 14.0F + modifier;
                } else if ('\\' == c || '/' == c) {
                    pixel += 12.25F + modifier;
                } else {
                    pixel += 12.0F + modifier;
                }
            }
        }

        return pixel;
    }
}
