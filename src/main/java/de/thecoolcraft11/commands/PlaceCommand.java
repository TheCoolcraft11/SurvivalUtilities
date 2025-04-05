package de.thecoolcraft11.commands;

import de.thecoolcraft11.SurvivalUtilities;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentBuilder;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.block.data.BlockData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.*;

public class PlaceCommand implements CommandExecutor {


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        if (args.length != 1) {
            sender.sendMessage("Usage: /placeschematic <name>");
            return true;
        }

        String schematicName = args[0];
        File schematicFile = new File(SurvivalUtilities.getPlugin(SurvivalUtilities.class).getDataFolder(), "schematics/" + schematicName + ".yml");

        if (!schematicFile.exists()) {
            player.sendMessage("Schematic '" + schematicName + "' does not exist!");
            return true;
        }

        placeSchematic(player.getLocation(), schematicFile, player.getWorld(), player);
        player.sendMessage("Schematic '" + schematicName + "' has been placed!");

        return true;
    }

    private void placeSchematic(Location startLocation, File file, World world, Player player) {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        Set<String> keys = config.getConfigurationSection("blocks").getKeys(false);
        int startX = startLocation.getBlockX();
        int startY = startLocation.getBlockY();
        int startZ = startLocation.getBlockZ();


        BukkitRunnable runnable = new BukkitRunnable() {
            final Set<String> keys = config.getConfigurationSection("blocks").getKeys(false);
            boolean remove = true;
            int repeats = 0;

            int minX = Integer.MAX_VALUE;
            int maxX = Integer.MIN_VALUE;
            int minY = Integer.MAX_VALUE;
            int maxY = Integer.MIN_VALUE;
            int minZ = Integer.MAX_VALUE;
            int maxZ = Integer.MIN_VALUE;

            @Override
            public void run() {
                for (String key : keys) {
                    int x = config.getInt("blocks." + key + ".x");
                    int y = config.getInt("blocks." + key + ".y");
                    int z = config.getInt("blocks." + key + ".z");

                    minX = Math.min(minX, x);
                    maxX = Math.max(maxX, x);
                    minY = Math.min(minY, y);
                    maxY = Math.max(maxY, y);
                    minZ = Math.min(minZ, z);
                    maxZ = Math.max(maxZ, z);

                    Block block = world.getBlockAt(startX + x, startY + y, startZ + z);
                    String blockDataString = config.getString("blocks." + key + ".blockData");
                    BlockData blockData = Bukkit.createBlockData(blockDataString);

                    Location blockLocation = new Location(startLocation.getWorld(), startX + x, startY + y, startZ + z);


                    player.sendBlockChange(blockLocation, remove ? (repeats < 100 ? Material.BARRIER.createBlockData() : Material.AIR.createBlockData()) : blockData);

                    if (repeats % 2 == 0) {
                        if (repeats >= 100) {
                            player.spawnParticle(Particle.BLOCK, blockLocation, 20, blockData);
                        } else {
                            Particle.DustOptions dustOptions = new Particle.DustOptions(Color.RED, 1.0F);
                            player.spawnParticle(Particle.DUST, blockLocation.clone().add(0.5, 0.5, 0.5), 20, dustOptions);
                        }
                    }

                    if (player.getLocation().distance(blockLocation) <= 2) {
                        resetBlocks(keys, config, world, startX, startY, startZ, player, 2);
                    }
                }

                spawnOuterBoundingBoxParticles(minX, maxX + 1, minY, maxY + 1, minZ, maxZ + 1);

                remove = !remove;
                repeats++;
                if (repeats >= 101) {
                    cancel();
                }
            }


            private void spawnOuterBoundingBoxParticles(int minX, int maxX, int minY, int maxY, int minZ, int maxZ) {

                for (int x = minX; x <= maxX; x++) {
                    spawnParticleAt(x, minY, minZ);
                    spawnParticleAt(x, maxY, minZ);
                    spawnParticleAt(x, minY, maxZ);
                    spawnParticleAt(x, maxY, maxZ);
                    spawnParticleAt(x, minY, minZ);
                    spawnParticleAt(x, maxY, minZ);
                    spawnParticleAt(x, minY, maxZ);
                    spawnParticleAt(x, maxY, maxZ);
                }

                for (int z = minZ; z <= maxZ; z++) {
                    spawnParticleAt(minX, minY, z);
                    spawnParticleAt(minX, maxY, z);
                    spawnParticleAt(maxX, minY, z);
                    spawnParticleAt(maxX, maxY, z);
                }


                for (int y = minY; y <= maxY; y++) {
                    spawnParticleAt(minX, y, minZ);
                    spawnParticleAt(minX, y, maxZ);
                    spawnParticleAt(maxX, y, minZ);
                    spawnParticleAt(maxX, y, maxZ);
                }
            }

            private void spawnParticleAt(int x, int y, int z) {
                Location loc = new Location(startLocation.getWorld(), startX + x, startY + y, startZ + z);
                Particle.DustOptions dustOptions = new Particle.DustOptions(Color.AQUA, 1.0F);
                player.spawnParticle(Particle.DUST, loc, 1, dustOptions);
            }
        };

        BukkitRunnable runnable2 = new BukkitRunnable() {
            int repeats = 0;

            @Override
            public void run() {
                player.sendActionBar(Component.text("-- Sneak to Build --").color(TextColor.color(TextColor.color(61, 226, 255))));
                if (player.isSneaking()) {
                    Map<Material, Integer> missingItems = getMissingItemsWithAmount(keys, config, player);

                    if (isOnlyAir(keys, config, world, startX, startY, startZ)) {
                        if (missingItems.isEmpty() || player.getGameMode() == GameMode.CREATIVE) {
                            sendAirBlocks(keys, config, world, startX, startY, startZ, player);
                            placeBlocks(keys, config, world, startX, startY, startZ, player);
                            if (player.getGameMode() != GameMode.CREATIVE) {
                                player.sendMessage("Test");
                                removeItems(getRequiredItemsWithAmount(keys, config), player);
                            }
                            runnable.cancel();
                            cancel();
                        } else {
                            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1.0f, 1.0f);
                            player.sendActionBar(Component.text("-- You dont have all required materials --").color(TextColor.color(TextColor.color(255, 0, 0))));
                            for (Map.Entry<Material, Integer> entry : missingItems.entrySet()) {
                                Material material = entry.getKey();
                                int missingAmount = entry.getValue();
                                player.sendMessage(ChatColor.RED + material.name() + ": " + missingAmount);
                                openItemListBook(player, missingItems);
                            }
                        }
                    } else {
                        player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1.0f, 1.0f);
                        player.sendActionBar(Component.text("-- There are blocks in the way --").color(TextColor.color(TextColor.color(255, 0, 0))));
                        resetBlocks(keys, config, world, startX, startY, startZ, player, 0);
                        markNonAirBlocks(keys, config, world, startX, startY, startZ, player);
                        runnable.cancel();
                        cancel();
                    }
                }
                repeats++;
                if (repeats >= 101) {
                    cancel();
                }
            }


        };

        runnable.runTaskTimer(SurvivalUtilities.getPlugin(SurvivalUtilities.class), 0, 10);
        runnable2.runTaskTimer(SurvivalUtilities.getPlugin(SurvivalUtilities.class), 0, 10);
    }

    private void markNonAirBlocks(Set<String> keys, YamlConfiguration config, World world, int startX, int startY, int startZ, Player player) {

        BukkitRunnable runnable1 = new BukkitRunnable() {
            @Override
            public void run() {

                for (String key : keys) {
                    int x = config.getInt("blocks." + key + ".x");
                    int y = config.getInt("blocks." + key + ".y");
                    int z = config.getInt("blocks." + key + ".z");

                    Location blockLocation = new Location(world, startX + x, startY + y, startZ + z);

                    if (world.getBlockAt(blockLocation).getBlockData().getMaterial() != Material.AIR) {
                        player.sendBlockChange(blockLocation, Material.REDSTONE_BLOCK.createBlockData());
                    }
                    if (player.getLocation().distance(blockLocation) <= 2) {
                        resetBlocks(keys, config, world, startX, startY, startZ, player, 2);
                    }
                }

            }
        };

        BukkitRunnable runnable2 = new BukkitRunnable() {
            @Override
            public void run() {
                runnable1.cancel();
                resetBlocks(keys, config, world, startX, startY, startZ, player, 0);
            }
        };
        runnable1.runTaskTimer(SurvivalUtilities.getPlugin(SurvivalUtilities.class), 0, 10L);
        runnable2.runTaskLater(SurvivalUtilities.getPlugin(SurvivalUtilities.class), 200L);

    }

    private void openItemListBook(Player player, Map<Material, Integer> missingItems) {
        ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta bookMeta = (BookMeta) book.getItemMeta();
        bookMeta.setTitle("Missing Items");
        bookMeta.setAuthor("Server");

        List<Component> pages = new ArrayList<>();

        ComponentBuilder pageContent = Component.text()
                .append(Component.text("§cMissing Items:\n\n"));
        int lineCount = 0;

        for (Map.Entry<Material, Integer> entry : missingItems.entrySet()) {
            Material material = entry.getKey();
            int missingAmount = entry.getValue();

            Component translatedName = getTranslatedName(player, material);

            Component line = Component.text("§6- ")
                    .append(translatedName)
                    .append(Component.text(" §7(" + missingAmount + ")\n"));
            pageContent.append(line);

            lineCount++;

            if (lineCount >= 12) {
                pages.add(pageContent.build());
                pageContent = Component.text().append(Component.text("§c- Missing -\n\n"));
                lineCount = 0;
            }
        }

        if (lineCount > 0) {
            pages.add(pageContent.build());
        }

        bookMeta.pages(pages);
        book.setItemMeta(bookMeta);

        player.openBook(book);
    }

    private Component getTranslatedName(Player player, Material material) {
        String translationKey = material.name().toLowerCase().replace("_", " ");
        translationKey = Character.toUpperCase(translationKey.charAt(0)) + translationKey.substring(1);

        String[] words = translationKey.split(" ");
        for (int i = 1; i < words.length; i++) {
            words[i] = words[i].substring(0, 1).toUpperCase() + words[i].substring(1);
        }
        translationKey = String.join(" ", words);


        Component translatedComponent = Component.translatable(translationKey);

        return translatedComponent;
    }

    private Map<Material, Integer> getMissingItemsWithAmount(Set<String> keys, YamlConfiguration config, Player player) {
        Map<Material, Integer> requiredItems = new HashMap<>();

        for (String key : keys) {
            String materialName = config.getString("blocks." + key + ".type");
            String materialData = config.getString("blocks." + key + ".blockData");
            Material material = Material.matchMaterial(materialName);
            if (material != null && material.isItem() && !materialData.contains("part=foot") && !materialData.contains("half=lower")) {
                requiredItems.put(material, requiredItems.getOrDefault(material, 0) + 1);
            }
        }

        Map<Material, Integer> missingItems = new HashMap<>();
        for (Map.Entry<Material, Integer> entry : requiredItems.entrySet()) {
            Material material = entry.getKey();
            int requiredAmount = entry.getValue();

            int playerAmount = 0;
            for (ItemStack item : player.getInventory().getContents()) {
                if (item != null && item.getType() == material) {
                    playerAmount += item.getAmount();
                }
            }

            if (playerAmount < requiredAmount) {
                missingItems.put(material, requiredAmount - playerAmount);
            }
        }

        return missingItems;
    }

    private Map<Material, Integer> getRequiredItemsWithAmount(Set<String> keys, YamlConfiguration config) {
        Map<Material, Integer> requiredItems = new HashMap<>();

        for (String key : keys) {
            String materialName = config.getString("blocks." + key + ".type");
            String materialData = config.getString("blocks." + key + ".blockData");
            Material material = Material.matchMaterial(materialName);

            if (material != null && material.isItem() && !materialData.contains("part=foot") && !materialData.contains("half=lower")) {
                requiredItems.put(material, requiredItems.getOrDefault(material, 0) + 1);
            }
        }

        return requiredItems;
    }


    private void removeItems(Map<Material, Integer> items, Player player) {
        Inventory inventory = player.getInventory();
        player.sendMessage(String.valueOf(items));

        for (Map.Entry<Material, Integer> entry : items.entrySet()) {
            Material material = entry.getKey();
            int amountToRemove = entry.getValue();

            if (amountToRemove <= 0) {
                continue;
            }

            for (ItemStack itemStack : inventory.getContents()) {
                if (itemStack != null && itemStack.getType() == material) {
                    int stackAmount = itemStack.getAmount();

                    if (stackAmount >= amountToRemove) {
                        itemStack.setAmount(stackAmount - amountToRemove);
                        break;
                    } else {
                        inventory.remove(itemStack);
                        amountToRemove -= stackAmount;
                    }
                }
            }
        }
    }


    private void resetBlocks(Set<String> keys, YamlConfiguration config, World world, int startX, int startY, int startZ, Player player, int radius) {
        Location playerLocation = player.getLocation();
        int playerX = playerLocation.getBlockX();
        int playerY = playerLocation.getBlockY();
        int playerZ = playerLocation.getBlockZ();

        for (String key : keys) {
            int x = config.getInt("blocks." + key + ".x");
            int y = config.getInt("blocks." + key + ".y");
            int z = config.getInt("blocks." + key + ".z");

            if (radius > 0) {
                int blockX = startX + x;
                int blockY = startY + y;
                int blockZ = startZ + z;

                double distance = Math.sqrt(
                        Math.pow(blockX - playerX, 2) +
                                Math.pow(blockY - playerY, 2) +
                                Math.pow(blockZ - playerZ, 2)
                );

                if (distance <= radius) {
                    player.sendBlockChange(
                            new Location(world, blockX, blockY, blockZ),
                            world.getBlockData(new Location(world, blockX, blockY, blockZ))
                    );
                }
            } else {
                player.sendBlockChange(new Location(world, startX + x, startY + y, startZ + z), world.getBlockData(new Location(world, startX + x, startY + y, startZ + z)));
            }
        }
    }


    private boolean isOnlyAir(Set<String> keys, YamlConfiguration config, World world, int startX, int startY, int startZ) {
        for (String key : keys) {
            int x = config.getInt("blocks." + key + ".x");
            int y = config.getInt("blocks." + key + ".y");
            int z = config.getInt("blocks." + key + ".z");

            if (world.getBlockAt(new Location(world, startX + x, startY + y, startZ + z)).getBlockData().getMaterial() != Material.AIR) {
                return false;
            }
        }
        return true;
    }


    private void sendAirBlocks(Set<String> keys, YamlConfiguration config, World world, int startX, int startY, int startZ, Player player) {
        for (String key : keys) {
            int x = config.getInt("blocks." + key + ".x");
            int y = config.getInt("blocks." + key + ".y");
            int z = config.getInt("blocks." + key + ".z");


            player.sendBlockChange(new Location(world, startX + x, startY + y, startZ + z), Material.AIR.createBlockData());
        }
    }

    private static void placeBlocks(Set<String> keys, YamlConfiguration config, World world, int startX, int startY, int startZ, Player player) {
        final int delay = 1;

        final Iterator<String> keyIterator = keys.iterator();
        final int[] currentIndex = {0};
        Bukkit.getScheduler().runTaskTimer(SurvivalUtilities.getPlugin(SurvivalUtilities.class), new Runnable() {
            @Override
            public void run() {
                if (keyIterator.hasNext()) {
                    String key = keyIterator.next();
                    int x = config.getInt("blocks." + key + ".x");
                    int y = config.getInt("blocks." + key + ".y");
                    int z = config.getInt("blocks." + key + ".z");
                    String materialName = config.getString("blocks." + key + ".type");

                    Material material;
                    try {
                        material = Material.valueOf(materialName);
                    } catch (IllegalArgumentException e) {
                        System.out.println("Invalid material name: " + materialName);
                        return;
                    }

                    Block block = world.getBlockAt(startX + x, startY + y, startZ + z);
                    String blockDataString = config.getString("blocks." + key + ".blockData");

                    if (blockDataString != null && !blockDataString.isEmpty()) {
                        try {
                            BlockData blockData = Bukkit.createBlockData(blockDataString);
                            block.setType(material, false);
                            world.playSound(block.getLocation(), block.getBlockSoundGroup().getPlaceSound(), 1.0f, 1.0f);
                            if (material.asItemType() != null) {
                                ItemStack item = material.asItemType().createItemStack(1);
                                player.incrementStatistic(Statistic.USE_ITEM, item.getType());
                            }
                            block.setBlockData(blockData, false);
                        } catch (IllegalArgumentException e) {
                            System.out.println("Invalid block data string for material " + material + ": " + blockDataString);
                            e.printStackTrace();
                            return;
                        }
                    } else {
                        block.setType(material, false);
                    }

                    BlockState state = block.getState();
                    if (state instanceof InventoryHolder holder) {
                        if (config.contains("blocks." + key + ".inventory")) {
                            List<ItemStack> contents = new ArrayList<>();
                            for (Object item : config.getList("blocks." + key + ".inventory", new ArrayList<>())) {
                                if (item instanceof Map) {
                                    ItemStack itemStack = ItemStack.deserialize((Map<String, Object>) item);
                                    contents.add(itemStack);
                                }
                            }
                            holder.getInventory().setContents(contents.toArray(new ItemStack[0]));
                            state.update();
                        }
                    }

                    if (state instanceof Sign sign) {
                        String[] lines = new String[4];
                        if (config.contains("blocks." + key + ".text")) {
                            lines = config.getStringList("blocks." + key + ".text").toArray(new String[0]);
                        }
                        for (int i = 0; i < 4; i++) {
                            sign.setLine(i, lines[i]);
                        }
                        sign.update();
                    }

                    currentIndex[0]++;
                } else {
                    Bukkit.getScheduler().cancelTask(this.hashCode());
                }
            }
        }, 0L, delay);
    }


}


