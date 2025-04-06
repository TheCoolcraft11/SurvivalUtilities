package de.thecoolcraft11.listener;

import de.thecoolcraft11.SurvivalUtilities;
import de.thecoolcraft11.util.Config;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ChatListener implements Listener {


    @EventHandler(ignoreCancelled = true)
    public void onAsyncChat(AsyncChatEvent event) {
        Config config = new Config("config.yml", SurvivalUtilities.getProvidingPlugin(SurvivalUtilities.class).getDataFolder());
        if (!config.getFileConfiguration().getBoolean("functions.chat.enabled")) {
            return;
        }
        Component component = event.message();

        String message = component.toString();

        if (message.contains("@")) {
            int start = message.indexOf("@") + 1;
            int end = message.indexOf(" ", start);
            if (end == -1) end = message.length();

            String playerName = message.substring(start, end);

            Player targetPlayer = Bukkit.getPlayer(playerName);
            if (targetPlayer != null && targetPlayer.isOnline()) {
                targetPlayer.playSound(targetPlayer.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.0f);

                event.message(Component.text(message.replace("@" + playerName, "")).append(Component.text("@" + playerName).color(net.kyori.adventure.text.format.NamedTextColor.AQUA)));
            }
        }
    }

}
