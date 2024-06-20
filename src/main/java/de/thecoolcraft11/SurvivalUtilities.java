package de.thecoolcraft11;

import de.thecoolcraft11.commands.ChunkbaseCommand;
import de.thecoolcraft11.listener.CarryListener;
import de.thecoolcraft11.util.Config;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public final class SurvivalUtilities extends JavaPlugin {

    public Config config;

    @Override
    public void onEnable() {

        config = new Config("config.yml", SurvivalUtilities.getProvidingPlugin(SurvivalUtilities.class).getDataFolder());

        if (config.getFileConfiguration().getBoolean("commands.chunkbase.enabled")) {
            getCommand("chunkbase").setExecutor(new ChunkbaseCommand());
        }

        final PluginManager pluginManager = Bukkit.getPluginManager();

        pluginManager.registerEvents(new CarryListener(), this);

        config =  new Config("config.yml", getDataFolder());

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @NotNull
    public Config getPluginConfig() {
        return config;
    }
}
