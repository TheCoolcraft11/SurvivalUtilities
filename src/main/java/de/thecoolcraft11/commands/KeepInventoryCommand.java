package de.thecoolcraft11.commands;

import de.thecoolcraft11.SurvivalUtilities;
import de.thecoolcraft11.util.Config;
import de.thecoolcraft11.util.PlayerConfig;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class KeepInventoryCommand implements CommandExecutor {
    private final PlayerConfig playerConfig = new PlayerConfig(SurvivalUtilities.getProvidingPlugin(SurvivalUtilities.class).getDataFolder(), "keepInventory");

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        Config config = new Config("config.yml", SurvivalUtilities.getProvidingPlugin(SurvivalUtilities.class).getDataFolder());
        if (config.getFileConfiguration().getBoolean("commands.keepInventory.enabled")) {
            if (!commandSender.hasPermission("survivalutilities.keepinventory")) {
                commandSender.sendMessage("You don't have permission to use this command");
                return true;
            }
        } else {
            commandSender.sendMessage("This command is disabled in the config");
            return true;
        }
        if (commandSender instanceof Player) {

            FileConfiguration settings = playerConfig.getPlayerConfig(((Player) commandSender).getUniqueId());
            settings.set("keepInventory", !settings.getBoolean("keepInventory"));
            playerConfig.savePlayerConfig(((Player) commandSender).getUniqueId(), settings);
            if (settings.getBoolean("keepInventory")) {
                commandSender.sendMessage("KeepInventory activated");
            } else {
                commandSender.sendMessage("KeepInventory disabled");
            }
        } else {
            commandSender.sendMessage("Only players can use this command");
        }
        return true;
    }
}
