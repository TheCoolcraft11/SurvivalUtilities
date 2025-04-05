package de.thecoolcraft11;

import de.thecoolcraft11.commands.*;
import de.thecoolcraft11.listener.*;
import de.thecoolcraft11.util.Config;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.UUID;

public final class SurvivalUtilities extends JavaPlugin {

    public Config config;
    public static HashMap<UUID, Component> AFKList = new HashMap<>();

    @Override
    public void onEnable() {

        config = new Config("config.yml", SurvivalUtilities.getProvidingPlugin(SurvivalUtilities.class).getDataFolder());

        getCommand("chunkbase").setExecutor(new ChunkbaseCommand());
        getCommand("survivalutilities").setExecutor(new SurvivalUtilitiesCommand());
        getCommand("sudo").setExecutor(new SudoCommand());
        //  getCommand("afk").setExecutor(new AfkCommand());
        getCommand("keepinventory").setExecutor(new KeepInventoryCommand());
        getCommand("save").setExecutor(new SaveCommand());
        getCommand("place").setExecutor(new PlaceCommand());
        getCommand("jail").setExecutor(new JailCommand());

        final PluginManager pluginManager = Bukkit.getPluginManager();

        pluginManager.registerEvents(new CarryListener(), this);
        pluginManager.registerEvents(new DeathListener(), this);
        pluginManager.registerEvents(new MonsterListener(), this);
        pluginManager.registerEvents(new ChatListener(), this);
        pluginManager.registerEvents(new TntListener(), this);

        config = new Config("config.yml", getDataFolder());

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

}
