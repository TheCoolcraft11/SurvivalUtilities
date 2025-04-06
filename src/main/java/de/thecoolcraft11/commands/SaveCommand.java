package de.thecoolcraft11.commands;

import de.thecoolcraft11.SurvivalUtilities;
import de.thecoolcraft11.util.Config;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

public class SaveCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        Config config = new Config("config.yml", SurvivalUtilities.getProvidingPlugin(SurvivalUtilities.class).getDataFolder());
        if (!config.getFileConfiguration().getBoolean("commands.saveSchematic.enabled")) {
            sender.sendMessage("This command is disabled in the config");
            return true;
        }
        if (!sender.hasPermission("survivalutilities.saveschematic")) {
            sender.sendMessage("You don't have permission to use this command");
            return true;
        }
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        if (args.length != 7) {
            sender.sendMessage("Usage: /saveschematic <name> <x1> <y1> <z1> <x2> <y2> <z2>");
            return true;
        }

        String schematicName = args[0];
        try {
            int x1 = Integer.parseInt(args[1]);
            int y1 = Integer.parseInt(args[2]);
            int z1 = Integer.parseInt(args[3]);
            int x2 = Integer.parseInt(args[4]);
            int y2 = Integer.parseInt(args[5]);
            int z2 = Integer.parseInt(args[6]);

            Location loc1 = new Location(player.getWorld(), x1, y1, z1);
            Location loc2 = new Location(player.getWorld(), x2, y2, z2);

            File schematicFile = new File(SurvivalUtilities.getPlugin(SurvivalUtilities.class).getDataFolder(), "schematics/" + schematicName + ".yml");
            saveSchematic(loc1, loc2, player.getWorld(), schematicFile);

            player.sendMessage("Schematic '" + schematicName + "' has been saved!");
        } catch (NumberFormatException e) {
            sender.sendMessage("Invalid coordinates. Please provide integers for the coordinates.");
        }
        return true;
    }

    private void saveSchematic(Location loc1, Location loc2, World world, File file) {
        YamlConfiguration config = new YamlConfiguration();

        int minX = Math.min(loc1.getBlockX(), loc2.getBlockX());
        int minY = Math.min(loc1.getBlockY(), loc2.getBlockY());
        int minZ = Math.min(loc1.getBlockZ(), loc2.getBlockZ());
        int maxX = Math.max(loc1.getBlockX(), loc2.getBlockX());
        int maxY = Math.max(loc1.getBlockY(), loc2.getBlockY());
        int maxZ = Math.max(loc1.getBlockZ(), loc2.getBlockZ());

        int index = 0;
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    Block block = world.getBlockAt(x, y, z);
                    if (block.getType() != Material.AIR) {
                        config.set("blocks." + index + ".x", x - minX);
                        config.set("blocks." + index + ".y", y - minY);
                        config.set("blocks." + index + ".z", z - minZ);
                        config.set("blocks." + index + ".type", block.getType().toString());
                        BlockData blockData = block.getBlockData();
                        config.set("blocks." + index + ".blockData", blockData.getAsString());

                        BlockState state = block.getState();
                        if (state instanceof InventoryHolder holder) {
                            config.set("blocks." + index + ".inventory", holder.getInventory().getContents());
                        }

                        index++;
                    }
                }
            }
        }

        try {
            config.save(file);
        } catch (IOException e) {
            SurvivalUtilities.getPlugin(SurvivalUtilities.class).getLogger().severe("Failed to save schematic file: " + file.getName() + " - " + e.getMessage());
        }
    }
}
