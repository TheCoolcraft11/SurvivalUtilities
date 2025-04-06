package de.thecoolcraft11.listener;

import de.thecoolcraft11.SurvivalUtilities;
import de.thecoolcraft11.util.Config;
import de.thecoolcraft11.util.ItemBuilder;
import de.thecoolcraft11.util.PlayerConfig;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class DeathListener implements Listener {
    private final PlayerConfig playerConfig = new PlayerConfig(SurvivalUtilities.getProvidingPlugin(SurvivalUtilities.class).getDataFolder(), "keepInventory");

    @EventHandler(ignoreCancelled = true)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Config config = new Config("config.yml", SurvivalUtilities.getProvidingPlugin(SurvivalUtilities.class).getDataFolder());
        if (!config.getFileConfiguration().getBoolean("functions.keepInventory.enabled")) {
            return;
        }
        if (playerConfig.getPlayerConfig(event.getPlayer().getUniqueId()).getBoolean("keepInventory")) {
            event.setKeepInventory(true);
            event.setKeepLevel(true);
            event.getDrops().replaceAll(ignored -> new ItemBuilder(Material.AIR).build());
            event.setDroppedExp(0);
        } else {
            event.setDroppedExp(event.getPlayer().getTotalExperience());
            event.getPlayer().sendMessage(String.valueOf(event.getPlayer().getTotalExperience()));

        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Config config = new Config("config.yml", SurvivalUtilities.getProvidingPlugin(SurvivalUtilities.class).getDataFolder());
        if (!config.getFileConfiguration().getBoolean("functions.keepInventory.enabled")) {
            return;
        }
        FileConfiguration settings = playerConfig.getPlayerConfig(event.getPlayer().getUniqueId());
        settings.set("keepInventory", true);
        playerConfig.savePlayerConfig(event.getPlayer().getUniqueId(), settings);
    }

}
