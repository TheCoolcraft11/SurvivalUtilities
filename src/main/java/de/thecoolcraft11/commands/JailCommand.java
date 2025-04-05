package de.thecoolcraft11.commands;

import de.thecoolcraft11.SurvivalUtilities;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class JailCommand implements CommandExecutor {
    public static Map<UUID, BukkitRunnable> jails = new HashMap<>();
    Map<UUID, GameMode> gameModeHashBeforeJail = new HashMap<>();

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (commandSender.hasPermission("survivalutilites.jail")) {
            if (strings.length < 2) {
                commandSender.sendMessage("Usage <start/stop> <Player> <Block>");
                return false;
            }
            Player player = Bukkit.getPlayer(strings[1]);
            if (strings[0].equals("start")) {
                if (player == null) {
                    commandSender.sendMessage("Player " + strings[2] + " not found");
                    return false;
                }
                Material material = Material.getMaterial(strings[2].toUpperCase());
                if (material == null) {
                    commandSender.sendMessage("Material " + strings[2] + " not found. Proceeding with Glass");
                    material = Material.GLASS;
                }
                if (jails.containsKey(player.getUniqueId())) {
                    commandSender.sendMessage("Player " + player.getName() + " is already in jail");
                    return false;
                }
                startBoxPlacementTask(player.getUniqueId(), material);
            } else if (strings[0].equals("stop")) {
                boolean success = false;
                if (player != null) {
                    success = stopJail(player);
                }
                if (success) {
                    commandSender.sendMessage("Player " + player.getName() + " is no longer in jail");
                } else {
                    commandSender.sendMessage("Player " + (player != null ? player.getName() : null) + " isn't in Jail");
                }
            } else {
                commandSender.sendMessage("Usage <start/stop> <Player> <Block>");
            }
            return true;
        }
        return false;
    }

    private boolean stopJail(Player player) {
        UUID uuid = player.getUniqueId();
        if (jails.containsKey(uuid)) {
            jails.get(uuid).cancel();
            resetBlocksAroundPlayer(player);
            player.setGameMode(gameModeHashBeforeJail.get(player.getUniqueId()));
            return true;
        } else {
            return false;
        }
    }

    private void startBoxPlacementTask(UUID uuid, Material material) {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) {
            return;
        }
        Location blockLocation = player.getLocation().getBlock().getLocation();

        double x = blockLocation.getX() + 0.5;
        double y = blockLocation.getY();
        double z = blockLocation.getZ() + 0.5;

        Location centerLocation = new Location(blockLocation.getWorld(), x, y, z);

        player.teleport(centerLocation);
        BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                placeBoxAroundPlayer(uuid, material);
                player.setGameMode(GameMode.ADVENTURE);
            }
        };
        jails.put(player.getUniqueId(), runnable);
        gameModeHashBeforeJail.put(player.getUniqueId(), player.getGameMode());
        runnable.runTaskTimer(SurvivalUtilities.getProvidingPlugin(SurvivalUtilities.class), 0, 1);
    }

    private void placeBoxAroundPlayer(UUID uuid, Material material) {
        Player player = Bukkit.getPlayer(uuid);
        int boxSize = 3;

        for (int x = -boxSize; x <= boxSize; x++) {
            for (int y = -boxSize; y <= boxSize; y++) {
                for (int z = -boxSize; z <= boxSize; z++) {
                    if (Math.abs(x) <= 0 && Math.abs(z) <= 0 && (y == 0 || y == 1)) {
                        continue;
                    }

                    if (player != null) {
                        player.sendBlockChange(player.getLocation().add(x, y, z), material.createBlockData());
                    }
                }
            }
        }
    }

    private void resetBlocksAroundPlayer(Player player) {
        int boxSize = 3;

        for (int x = -boxSize; x <= boxSize; x++) {
            for (int y = -boxSize; y <= boxSize; y++) {
                for (int z = -boxSize; z <= boxSize; z++) {
                    if (Math.abs(x) <= 0 && Math.abs(z) <= 0 && (y == 0 || y == 1)) {
                        continue;
                    }

                    player.sendBlockChange(player.getLocation().add(x, y, z), player.getWorld().getBlockData(player.getLocation().add(x, y, z)));
                }
            }
        }
    }
}
