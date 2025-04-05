package de.thecoolcraft11.listener;


import de.thecoolcraft11.SurvivalUtilities;
import de.thecoolcraft11.util.Config;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.BlockState;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDismountEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class CarryListener implements Listener {
    private static final NamespacedKey namespacedKey = new NamespacedKey(SurvivalUtilities.getPlugin(SurvivalUtilities.class), "isCarry");
    private final HashMap<UUID, Long> sneakStartTimes = new HashMap<>();
    private static final List<UUID> latestPickedEntity = new ArrayList<>();
    private final HashMap<UUID, BlockState> fallingBlockInv = new HashMap<>();
    Config config = new Config("config.yml", SurvivalUtilities.getProvidingPlugin(SurvivalUtilities.class).getDataFolder());

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent event) {
        if (config.getFileConfiguration().getBoolean("functions.carryMobs.enabled")) {
            if (!config.getFileConfiguration().getStringList("functions.carryMobs.blacklistedEntities").contains(event.getRightClicked().getType().toString())) {
                if (!config.getFileConfiguration().getStringList("functions.carryMobs.blacklistedWorlds").contains(event.getPlayer().getWorld().getName())) {
                    if (config.getFileConfiguration().getBoolean("functions.carryMobs.enabled")) {
                        if (!config.getFileConfiguration().getBoolean("functions.carryMobs.requirePermission")) {
                            if (event.getPlayer().isSneaking() && event.getPlayer().getInventory().getItemInMainHand().isEmpty()) {
                                if (config.getFileConfiguration().getBoolean("functions.carryMobs.protectOP") && event.getRightClicked() instanceof Player && event.getRightClicked().hasPermission("survivalutilities.carry.protected") && !event.getPlayer().hasPermission("survivalUtilities.carry.protected")) {
                                    return;
                                }
                                if (event.getRightClicked() instanceof Tameable tameable && config.getFileConfiguration().getBoolean("functions.carryMobs.protectTamed")) {
                                    if (tameable.isTamed() && tameable.getOwner() != event.getPlayer()) {
                                        return;
                                    }
                                }
                                pickUpEntity(config, event);
                                addSubTitle(event.getPlayer(), config);
                                event.setCancelled(true);
                            }
                        } else {
                            if (event.getPlayer().isSneaking() && event.getPlayer().getInventory().getItemInMainHand().isEmpty() && event.getPlayer().hasPermission("survivalutilities.carry.pickup." + event.getRightClicked().getType())) {
                                if (config.getFileConfiguration().getBoolean("functions.carryMobs.protectOP") && event.getRightClicked() instanceof Player && event.getRightClicked().hasPermission("survivalutilities.carry.protected") && !event.getPlayer().hasPermission("survivalUtilities.carry.protected")) {
                                    return;
                                }
                                if (event.getRightClicked() instanceof Tameable tameable && config.getFileConfiguration().getBoolean("functions.carryMobs.protectTamed")) {
                                    if (tameable.isTamed() && tameable.getOwner() != event.getPlayer()) {
                                        return;
                                    }
                                }
                                pickUpEntity(config, event);
                                addSubTitle(event.getPlayer(), config);
                                event.setCancelled(true);
                            }
                        }
                    }
                }
            }
        }
    }

    int time = 0;

    @EventHandler
    public void onPlayerToggleSneak(PlayerToggleSneakEvent event) {
        UUID playerUUID = event.getPlayer().getUniqueId();


        if (event.isSneaking()) {
            sneakStartTimes.put(playerUUID, System.currentTimeMillis());

            BukkitRunnable runnable = new BukkitRunnable() {
                //int time = 4;
                @Override
                public void run() {
                    time++;
                    StringBuilder s = new StringBuilder();
                    for (int i = 1; i < getNumberPattern(time, 11); i++) {
                        s.append("█");
                    }
                    StringBuilder s2 = new StringBuilder();
                    for (int i = 1; i < 11 - getNumberPattern(time, 11); i++) {
                        s2.append(" ");
                    }

                    TextComponent component = Component.text(s2 + getRainbowString(s.toString(), true, false) + "  " + ChatColor.BOLD + getNumberPattern(time, 11) + ChatColor.RESET + "  " + getRainbowString(s.toString(), false, true) + s2);
                    event.getPlayer().sendActionBar(component);
                    s.delete(0, s.length());
                    if (!event.getPlayer().isSneaking()) {
                        time = 0;
                        cancel();
                        event.getPlayer().sendActionBar("");
                        //throwStrength = time / 10;
                    }
                }
            };
            if (event.getPlayer().getPassenger() != null) {
                runnable.runTaskTimer(SurvivalUtilities.getPlugin(SurvivalUtilities.class), 0, 2);
            }


        } else if (sneakStartTimes.containsKey(playerUUID)) {
            for (Entity passenger : event.getPlayer().getPassengers()) {
                if (!latestPickedEntity.contains(playerUUID)) {
                    event.getPlayer().removePassenger(passenger);
                    passenger.setVelocity(event.getPlayer().getLocation().getDirection().multiply(Math.min(getNumberPattern(time, 11) / 5, 2)));
                    BukkitRunnable runnable = new BukkitRunnable() {

                        int i = 0;

                        @Override
                        public void run() {
                            if (passenger == null || (passenger instanceof LivingEntity && passenger.isDead()) || !passenger.isValid()) {
                                cancel();
                            } else {
                                World world = passenger.getWorld();
                                i++;
                                if (i >= 50) {
                                    cancel();
                                }

                                world.spawnParticle(Particle.HAPPY_VILLAGER, passenger.getLocation(), 1);
                                if (passenger.getLocation().add(0, -1, 0).getBlock().getType() != Material.AIR) {
                                    cancel();
                                }
                            }
                        }
                    };
                    runnable.runTaskTimer(SurvivalUtilities.getPlugin(SurvivalUtilities.class), 0, 1);
                }
            }
        }
        latestPickedEntity.remove(playerUUID);
    }

    private String getRainbowString(String input, boolean insideOut, boolean appendReset) {
        ChatColor[] colors = {
                ChatColor.RED,
                ChatColor.GOLD,
                ChatColor.YELLOW,
                ChatColor.GREEN,
                ChatColor.AQUA,
                ChatColor.BLUE,
                ChatColor.DARK_PURPLE
        };

        int length = input.length();
        StringBuilder rainbowString = new StringBuilder();

        for (int i = 0; i < length; i++) {
            int colorIndex = insideOut
                    ? (i * colors.length / length) % colors.length
                    : ((length - i - 1) * colors.length / length) % colors.length;

            rainbowString.append(colors[colorIndex]);
            rainbowString.append(input.charAt(i));
        }

        if (appendReset) {
            rainbowString.append(ChatColor.RESET);
        }

        return rainbowString.toString();
    }


    private static void pickUpEntity(Config config, PlayerInteractAtEntityEvent event) {
        if (config.getFileConfiguration().getBoolean("functions.carryMobs.allowMultiPicking") || event.getPlayer().getPassenger() == null) {
            if (config.getFileConfiguration().getBoolean("functions.carryMobs.allowRiders") || event.getRightClicked().getPassenger() == null) {
                event.getPlayer().addPassenger(event.getRightClicked());
                if (event.getRightClicked() instanceof Player target) {
                    Player player = event.getPlayer();
                    ArmorStand armorStand = (ArmorStand) player.getWorld().spawnEntity(player.getLocation(), EntityType.ARMOR_STAND);
                    armorStand.getPersistentDataContainer().set(namespacedKey, PersistentDataType.BOOLEAN, true);
                    armorStand.setInvisible(true);
                    armorStand.setInvulnerable(true);
                    armorStand.setBasePlate(false);
                    armorStand.setSmall(true);
                    armorStand.getAttribute(Attribute.GENERIC_SCALE).setBaseValue(0.1);
                    armorStand.addPassenger(target);
                    player.addPassenger(armorStand);
                    BukkitRunnable runnable = new BukkitRunnable() {
                        @Override
                        public void run() {
                            if (armorStand.getVehicle() == null) {
                                if (armorStand.isValid() && armorStand != null) {
                                    if (armorStand.getPassenger() != null && armorStand.getPassenger().isValid()) {
                                        if (!armorStand.getWorld().getBlockAt(armorStand.getLocation().add(0, -0.1, 0)).isPassable()) {
                                            armorStand.removePassenger(armorStand.getPassenger());
                                            armorStand.remove();
                                            cancel();
                                        }
                                    } else {
                                        armorStand.remove();
                                    }
                                } else {
                                    cancel();
                                }
                            }
                        }
                    };
                    runnable.runTaskTimer(SurvivalUtilities.getPlugin(SurvivalUtilities.class), 0, 2);
                }
                if (event.getRightClicked() instanceof FallingBlock) {
                    if (Boolean.TRUE.equals(event.getRightClicked().getPersistentDataContainer().get(new NamespacedKey(SurvivalUtilities.getPlugin(SurvivalUtilities.class), "isPickedUpBlock"), PersistentDataType.BOOLEAN))) {
                        return;
                    }
                }
                if (!latestPickedEntity.contains(event.getPlayer().getUniqueId())) {
                    latestPickedEntity.add(event.getPlayer().getUniqueId());
                }
            }
        }
    }

    private static void pickUpBlockEntity(Config config, Player player, ArmorStand armorStand) {
        player.addPassenger(armorStand);
        armorStand.setNoPhysics(false);
        ((FallingBlock) armorStand.getPassenger()).setHurtEntities(true);
        armorStand.getPersistentDataContainer().set(new NamespacedKey(SurvivalUtilities.getPlugin(SurvivalUtilities.class), "isPickedUpBlock"), PersistentDataType.BOOLEAN, true);
        armorStand.getPassenger().getPersistentDataContainer().set(new NamespacedKey(SurvivalUtilities.getPlugin(SurvivalUtilities.class), "isPickedUpBlock"), PersistentDataType.BOOLEAN, true);
        if (!latestPickedEntity.contains(player.getUniqueId())) {
            latestPickedEntity.add(player.getUniqueId());
            BukkitRunnable runnable = new BukkitRunnable() {
                @Override
                public void run() {
                    if (armorStand != null) {
                        if (armorStand.getVehicle() == null) {
                            if (!armorStand.getWorld().getBlockAt(armorStand.getLocation()).getType().isAir()) {
                                armorStand.getLocation().getBlock().breakNaturally();
                                armorStand.teleport(new Location(armorStand.getWorld(), Math.round(armorStand.getX()) + 0.5, Math.round(armorStand.getY()), Math.round(armorStand.getZ()) + 0.5));
                            }
                            if (!armorStand.getWorld().getBlockAt(armorStand.getLocation().add(0, -1, 0)).getType().isAir()) {
                                if (armorStand.getY() % 1 == 0) {
                                    armorStand.remove();
                                    cancel();
                                }
                            }
                        }
                    } else {
                        cancel();
                    }
                }
            };
            runnable.runTaskTimer(SurvivalUtilities.getPlugin(SurvivalUtilities.class), 0, 2);
        }
    }

    private static int getNumberPattern(int input, int limit) {
        int cycleLength = 2 * limit - 2;
        int position = input % cycleLength;

        if (position == 0) {
            return 2; // Special case for multiples of cycleLength
        } else if (position <= limit) {
            return position;
        } else {
            return 2 * limit - position;
        }
    }

    private static void addSubTitle(Player player, Config config) {
        BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                if (player != null && player.isValid()) {
                    if (player.getPassenger() != null && player.getPassenger().isValid()) {
                        if (!player.isSneaking()) {
                            if (player.getPassenger().getType() == EntityType.ARMOR_STAND && player.getPassenger().getPassenger().getType() == EntityType.FALLING_BLOCK) {
                                player.sendActionBar(Component.text("-- ").color(TextColor.color(61, 226, 255)).append(Component.translatable(((FallingBlock) player.getPassenger().getPassenger()).getBlockData().getMaterial().getItemTranslationKey())).color(TextColor.color(61, 226, 255)).append(Component.text(" --").color(TextColor.color(61, 119, 255))));
                            } else if (player.getPassenger().getType() == EntityType.ARMOR_STAND && player.getPassenger().getPassenger().getType() == EntityType.PLAYER) {
                                player.sendActionBar(Component.text("-- ").color(TextColor.color(61, 226, 255)).append(Component.translatable((player.getPassenger().getPassenger()).getName()).color(TextColor.color(61, 226, 255)).append(Component.text(" --").color(TextColor.color(61, 119, 255)))));
                            } else {
                                player.sendActionBar(Component.text("-- ").color(TextColor.color(61, 226, 255)).append(Component.translatable(player.getPassengers().get(0).getName()).color(TextColor.color(61, 226, 255))).append(Component.text(" --").color(TextColor.color(61, 119, 255))));
                            }
                        }
                    } else {
                        cancel();
                    }
                } else {
                    cancel();
                }
            }
        };
        runnable.runTaskTimer(SurvivalUtilities.getPlugin(SurvivalUtilities.class), 0, 2);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getPlayer().getPassenger() == null) {
            if (!latestPickedEntity.contains(event.getPlayer().getUniqueId())) {
                if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getPlayer().isSneaking() && event.getPlayer().getInventory().getItemInMainHand().isEmpty()) {
                    if (config.getFileConfiguration().getBoolean("functions.carryMobs.enabled") && config.getFileConfiguration().getBoolean("functions.carryMobs.allowBlockCarry")) {
                        if (!config.getFileConfiguration().getBoolean("functions.carryMobs.blacklistBlocks")) {
                            if (config.getFileConfiguration().getStringList("functions.carryMobs.whitelistBlocks").contains(event.getClickedBlock().getType().name())) {
                                ArmorStand armorStand = (ArmorStand) event.getPlayer().getWorld().spawnEntity(event.getInteractionPoint(), EntityType.ARMOR_STAND);
                                fallingBlockInv.put(armorStand.getUniqueId(), event.getClickedBlock().getState());
                                armorStand.setNoPhysics(true);
                                armorStand.getPersistentDataContainer().set(namespacedKey, PersistentDataType.BOOLEAN, true);
                                armorStand.setInvisible(true);
                                armorStand.setInvulnerable(true);
                                armorStand.setBasePlate(false);
                                armorStand.setSmall(true);
                                armorStand.getAttribute(Attribute.GENERIC_SCALE).setBaseValue(0.1);
                                FallingBlock fallingBlock = event.getPlayer().getWorld().spawnFallingBlock(event.getInteractionPoint().add(0, 400, 0), event.getClickedBlock().getBlockData());
                                fallingBlock.setBlockState(event.getClickedBlock().getState());
                                fallingBlock.setInvulnerable(true);
                                fallingBlock.setDropItem(false);
                                armorStand.addPassenger(fallingBlock);
                                pickUpBlockEntity(config, event.getPlayer(), armorStand);
                                addSubTitle(event.getPlayer(), config);
                                event.getClickedBlock().setType(Material.AIR);
                                event.setCancelled(true);
                            }
                        } else {
                            if (!config.getFileConfiguration().getStringList("functions.carryMobs.whitelistBlocks").contains(event.getClickedBlock().getType().name())) {
                                ArmorStand armorStand = (ArmorStand) event.getPlayer().getWorld().spawnEntity(event.getInteractionPoint(), EntityType.ARMOR_STAND);
                                fallingBlockInv.put(armorStand.getUniqueId(), event.getClickedBlock().getState());
                                armorStand.setNoPhysics(true);
                                armorStand.getPersistentDataContainer().set(namespacedKey, PersistentDataType.BOOLEAN, true);
                                armorStand.setInvisible(true);
                                armorStand.setInvulnerable(true);
                                armorStand.setBasePlate(false);
                                armorStand.setSmall(true);
                                armorStand.getAttribute(Attribute.GENERIC_SCALE).setBaseValue(0.1);
                                FallingBlock fallingBlock = event.getPlayer().getWorld().spawnFallingBlock(event.getInteractionPoint().add(0, 400, 0), event.getClickedBlock().getBlockData());
                                fallingBlock.setBlockState(event.getClickedBlock().getState());
                                fallingBlock.setInvulnerable(true);
                                fallingBlock.setDropItem(false);
                                armorStand.addPassenger(fallingBlock);
                                pickUpBlockEntity(config, event.getPlayer(), armorStand);
                                addSubTitle(event.getPlayer(), config);
                                event.getClickedBlock().setType(Material.AIR);
                                event.setCancelled(true);
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (config.getFileConfiguration().getBoolean("functions.carryMobs.disableAttacking")) {
            if (event.getEntity() instanceof Player) {
                if (event.getDamager().getVehicle() == event.getEntity()) {
                    event.setCancelled(true);
                }
            }
            if (event.getDamager() instanceof Player) {
                if (event.getEntity().getVehicle() == event.getDamager()) {
                    event.setCancelled(true);
                }
            }
            if (event.getEntity() instanceof Player) {
                if (event.getDamager().getVehicle() != null) {
                    if (event.getDamager().getVehicle().getVehicle() != null) {
                        if (event.getDamager().getVehicle().getVehicle() == event.getEntity()) {
                            event.setCancelled(true);
                        }
                    }
                }

            }
            if (event.getDamager() instanceof Player) {
                if (event.getEntity().getVehicle() != null) {
                    if (event.getEntity().getVehicle().getVehicle() != null) {
                        if (event.getEntity().getVehicle().getVehicle() == event.getDamager()) {
                            event.setCancelled(true);
                        }
                    }
                }

            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityDismount(EntityDismountEvent event) {
        if (event.getDismounted() instanceof ArmorStand) {
            if (event.getDismounted().getVehicle() instanceof Player) {
                if (event.getDismounted().getVehicle().hasPermission("survivalutilities.carry.forcePassengerStay")) {
                    if (!event.getDismounted().getVehicle().isSneaking()) {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (event.getPlayer().getVehicle() instanceof ArmorStand) {
            if (event.getPlayer().getVehicle().getVehicle() instanceof Player) {
                event.getPlayer().getVehicle().removePassenger(event.getPlayer());
                event.getPlayer().getVehicle().remove();
            }
        }
    }

}
