package de.thecoolcraft11.commands;

import de.thecoolcraft11.SurvivalUtilities;
import de.thecoolcraft11.util.Config;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class SurvivalUtilitiesCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Config config = new Config("config.yml", SurvivalUtilities.getProvidingPlugin(SurvivalUtilities.class).getDataFolder());
        if (!config.getFileConfiguration().getBoolean("commands.survivalutilities.enabled")) {
            sender.sendMessage(Component.text("This command is disabled in the config").color(NamedTextColor.RED));
            return true;
        }
        if (!sender.hasPermission("survivalutilities.survivalutilities")) {
            sender.sendMessage(Component.text("You don't have permission to use this command").color(NamedTextColor.RED));
            return true;
        }
        if (args.length >= 1) {
            if (args[0].equals("reload")) {
                try {
                    config.reload();
                    sender.sendMessage(Component.text("Successfully reloaded config!").color(NamedTextColor.GREEN));
                } catch (Exception e) {
                    sender.sendMessage(Component.text("Failed to reload config: " + e).color(NamedTextColor.RED));
                    throw new RuntimeException(e);

                }
            } else if (args.length >= 4) {
                HashMap<UUID, PermissionAttachment> attachments = new HashMap<>();
                if (args[0].equals("permission")) {
                    Player player = Bukkit.getPlayer(args[1]);
                    if (player == null) {
                        return false;
                    }
                    attachments.put(player.getUniqueId(), player.addAttachment(SurvivalUtilities.getPlugin(SurvivalUtilities.class)));
                    switch (args[2]) {
                        case "set" -> {
                            attachments.get(player.getUniqueId()).setPermission(args[3], Boolean.parseBoolean(args[4]));
                            sender.sendMessage(Component.text("Set permission " + args[3] + " for " + player.getName()));
                        }
                        case "remove" -> {
                            attachments.get(player.getUniqueId()).unsetPermission(args[3]);
                            sender.sendMessage(Component.text("Removed permission " + args[3] + " from " + player.getName()));
                        }
                        case "get" ->
                                sender.sendMessage(Component.text(player.getName() + " has the Permission " + args[3] + " with value: " + player.permissionValue(args[3]).toBooleanOrElse(false)));
                    }
                }
            }
        }
        return true;

    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        List<String> list = new java.util.ArrayList<>(List.of());
        if (args.length == 2 && args[0].equals("permission")) {
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                list.add(onlinePlayer.getName());
            }
        } else if (args.length == 3 && args[0].equals("permission")) {
            list.add("set");
            list.add("remove");
            list.add("get");
        } else if (args.length == 4 && args[0].equals("permission")) {
            list.add("survivalutilities.carry.protected");
            list.add("survivalutilities.carry.pickup.");
        } else if (args.length == 6 && args[0].equals("permission") && args[2].equals("set")) {
            list.add("true");
            list.add("false");
        } else {
            list.add("reload");
            list.add("permission");
        }
        return list;
    }
}