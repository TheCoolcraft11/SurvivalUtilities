package de.thecoolcraft11.util;

import de.thecoolcraft11.SurvivalUtilities;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class PlayerConfig {
    private final File playerDataFolder;

    public PlayerConfig(File dataFolder, String name) {
        this.playerDataFolder = new File(dataFolder, name);
        if (!this.playerDataFolder.exists()) {
            boolean wasCreated = this.playerDataFolder.mkdirs();
            if (wasCreated) {
                SurvivalUtilities.getPlugin(SurvivalUtilities.class).getLogger().info("Created player data folder: " + this.playerDataFolder);
            } else {
                SurvivalUtilities.getPlugin(SurvivalUtilities.class).getLogger().warning("Player data folder already exists: " + this.playerDataFolder);
            }
        }
    }

    public FileConfiguration getPlayerConfig(UUID playerUUID) {
        File playerFile = new File(playerDataFolder, playerUUID.toString() + ".yml");
        if (!playerFile.exists()) {
            try {
                boolean wasCreated = playerFile.createNewFile();
                if (wasCreated) {
                    SurvivalUtilities.getPlugin(SurvivalUtilities.class).getLogger().info("Created player config file for " + playerUUID);
                } else {
                    SurvivalUtilities.getPlugin(SurvivalUtilities.class).getLogger().warning("Player config file already exists for " + playerUUID);
                }
            } catch (IOException e) {
                SurvivalUtilities.getPlugin(SurvivalUtilities.class).getLogger().severe("Failed to create player config file for " + playerUUID);
            }
        }
        return YamlConfiguration.loadConfiguration(playerFile);
    }

    public void savePlayerConfig(UUID playerUUID, FileConfiguration config) {
        File playerFile = new File(playerDataFolder, playerUUID.toString() + ".yml");
        try {
            config.save(playerFile);
        } catch (IOException e) {
            SurvivalUtilities.getPlugin(SurvivalUtilities.class).getLogger().severe("Failed to save player config file for " + playerUUID);
        }
    }
}
