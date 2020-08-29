package me.ajan12.tabupdate;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;

class Handlers {

    static float[] currentLengths;

    /**
     * Handles all the online players' headers, footers and playernames.
     */
    static void handleAllPlayers() {

        //Iterating over all the online players.
        for (final Player player : Bukkit.getOnlinePlayers()) {
            //Handling the player.
            handlePlayer(player, true);
        }
    }

    static void handlePlayer(final Player player, boolean isParent) {
        //Getting the presets.
        String playername = TabUpdate.getInstance().getConfig().getString("playername-preset", " ");
        String header = TabUpdate.getInstance().getConfig().getString("header-preset", " ");
        String footer = TabUpdate.getInstance().getConfig().getString("footer-preset", " ");

        //Checking if playername is empty.
        if (playername.equalsIgnoreCase(" ")) return;

        //Checking if header is empty.
        if (!header.equalsIgnoreCase(" ")) {

            //Processing the header.
            header = ChatColor.translateAlternateColorCodes('&', PlaceHolderUtils.processPlaceHolders(header, player));
        }

        //Checking if header or footer is empty.
        if (!footer.equalsIgnoreCase(" ")) {

            //Processing the footer.
            footer = ChatColor.translateAlternateColorCodes('&', PlaceHolderUtils.processPlaceHolders(footer, player));
        }
        playername = ChatColor.translateAlternateColorCodes('&', PlaceHolderUtils.processPlaceHolders(playername, player));

        if (Handlers.currentLengths == null) {
            Handlers.currentLengths = new float[playername.split("%ALIGN%").length];
        }

        float[] currentLengths = Handlers.currentLengths.clone();
        String[] alignedComponents = playername.split("%ALIGN%");

        StringBuilder finalizedTab = new StringBuilder();

        boolean tmpBoolean = false;
        for (int i = 0; i < currentLengths.length; i++) {
            if (PlaceHolderUtils.getStringWidth(alignedComponents[i]) > currentLengths[i]) {
                tmpBoolean = true;
                currentLengths[i] = PlaceHolderUtils.getStringWidth(alignedComponents[i]);
            }
        }

        if (tmpBoolean && isParent) {
            Handlers.currentLengths = currentLengths;
            for (Player p : Bukkit.getOnlinePlayers() ) {
                handlePlayer(p, false);
            }
            return;
        }

        float alignmentError = 0.0F;
        for (int i = 0;i < alignedComponents.length; i++) {
            StringBuilder sb = new StringBuilder(alignedComponents[i]);
            float n = PlaceHolderUtils.getStringWidth(alignedComponents[i]);
            n += alignmentError;
            while (n < currentLengths[i]) {
                if (i == alignedComponents.length - 1) {
                    sb.insert(0, " ");
                } else {
                    sb.append(" ");
                }
                n += 8.0F;
            }
            alignmentError = n - currentLengths[i];
            if (i != alignedComponents.length - 1) {
                if (alignmentError <= 0.0F) {
                    sb.append("&8&l|||&r");
                } else if (0.0F < alignmentError && alignmentError <= 2.0F) {
                    sb.append("&8&l||&8|&r");
                    alignmentError -= 2.0F;
                } else if (2.0F < alignmentError && alignmentError <= 4.0F) {
                    sb.append("&8&l|&8||&r");
                    alignmentError -= 2.0F;
                } else if (4.0F < alignmentError && alignmentError <= 6.0F) {
                    sb.append("&8&l||&r");
                    alignmentError -= 2.0F;
                } else if (6.0F < alignmentError && alignmentError <= 8.0F) {
                    sb.append("&8&l|&8|&r");
                    alignmentError -= 2.0F;
                } else if (8.0F < alignmentError && alignmentError <= 10.0F) {
                    sb.append("&8&l||&r");
                    alignmentError -= 2.0F;
                } else if (10.0F < alignmentError && alignmentError <= 12.0F) {
                    sb.append("&8||&r");
                    alignmentError -= 2.0F;
                } else if (12.0F < alignmentError && alignmentError <= 14.0F) {
                    sb.append("&8|&r");
                    alignmentError -= 2.0F;
                } else {
                    sb.append("&c|&r");
                    alignmentError -= 4.0F;
                }
            }
            finalizedTab.append(sb.toString());
        }

        //Updating player's tabname.
        player.setPlayerListName(ChatColor.translateAlternateColorCodes('&', finalizedTab.toString()));

        //Creating the packet of header and footer.
        final PacketContainer headerFooter = TabUpdate.getProtocol().createPacket(PacketType.Play.Server.PLAYER_LIST_HEADER_FOOTER);
        headerFooter.getChatComponents()
                .write(0, WrappedChatComponent.fromText(header))
                .write(1, WrappedChatComponent.fromText(footer));
        //Sending the packet to the player.
        try {
            TabUpdate.getProtocol().sendServerPacket(player, headerFooter);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
