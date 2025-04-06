package de.thecoolcraft11;

import de.thecoolcraft11.commands.*;
import de.thecoolcraft11.listener.*;
import de.thecoolcraft11.util.Config;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public final class SurvivalUtilities extends JavaPlugin {

    public Config config;
    public static HashMap<UUID, Component> AFKList = new HashMap<>();

    @Override
    public void onEnable() {

        config = new Config("config.yml", SurvivalUtilities.getProvidingPlugin(SurvivalUtilities.class).getDataFolder());

        Objects.requireNonNull(getCommand("chunkbase")).setExecutor(new ChunkbaseCommand());
        Objects.requireNonNull(getCommand("survivalutilities")).setExecutor(new SurvivalUtilitiesCommand());
        Objects.requireNonNull(getCommand("sudo")).setExecutor(new SudoCommand());
        //  getCommand("afk").setExecutor(new AfkCommand());
        Objects.requireNonNull(getCommand("keepinventory")).setExecutor(new KeepInventoryCommand());
        Objects.requireNonNull(getCommand("save")).setExecutor(new SaveCommand());
        Objects.requireNonNull(getCommand("place")).setExecutor(new PlaceCommand());
        Objects.requireNonNull(getCommand("jail")).setExecutor(new JailCommand());

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
