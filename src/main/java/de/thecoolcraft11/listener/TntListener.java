package de.thecoolcraft11.listener;

import de.thecoolcraft11.SurvivalUtilities;
import de.thecoolcraft11.util.Config;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;

public class TntListener implements Listener {
    Config config = new Config("config.yml", SurvivalUtilities.getProvidingPlugin(SurvivalUtilities.class).getDataFolder());

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        if (!config.getFileConfiguration().getBoolean("functions.tntProtection.enabled")) return;
        if (event.getEntity() instanceof TNTPrimed tnt) {

            if (tnt.getSource() instanceof Player player) {

                if (!player.hasPermission("survivalutilities.tnt.use")) {
                    event.setCancelled(true);

                }
            }
        }
    }

}
