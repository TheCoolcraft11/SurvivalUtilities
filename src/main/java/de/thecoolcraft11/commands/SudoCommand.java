package de.thecoolcraft11.commands;

import de.thecoolcraft11.SurvivalUtilities;
import de.thecoolcraft11.util.Config;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SudoCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Config config = new Config("config.yml", SurvivalUtilities.getProvidingPlugin(SurvivalUtilities.class).getDataFolder());
        if (!config.getFileConfiguration().getBoolean("commands.sudo.enabled")) {
            sender.sendMessage("This command is disabled in the config");
            return true;
        }
        if (!sender.hasPermission("survivalutilities.sudo")) {
            sender.sendMessage("You don't have permission to use this command");
            return true;
        }
        if (args.length < 2) {
            sender.sendMessage("Usage: /sudo <player> <command>");
            return false;
        }

        Player targetPlayer = Bukkit.getPlayer(args[0]);
        if (targetPlayer == null) {
            sender.sendMessage("Player not found.");
            return false;
        }


        StringBuilder commandBuilder = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            commandBuilder.append(args[i]).append(" ");
        }
        String commandToExecute = commandBuilder.toString().trim();


        targetPlayer.performCommand(commandToExecute);
        sender.sendMessage("Executed command as " + targetPlayer.getName() + ": " + commandToExecute);
        return true;
    }
}

