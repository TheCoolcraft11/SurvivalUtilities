package de.thecoolcraft11.listener;


import de.thecoolcraft11.SurvivalUtilities;
import de.thecoolcraft11.util.Config;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class CarryListener implements Listener {
    private final HashMap<UUID, Long> sneakStartTimes = new HashMap<>();
    private static final List<UUID> latestPickedEntity = new ArrayList<>();
    Config config = new Config("config.yml", SurvivalUtilities.getProvidingPlugin(SurvivalUtilities.class).getDataFolder());
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent event) {
        if (config.getFileConfiguration().getBoolean("functions.carryMobs.enabled")) {
            if (!config.getFileConfiguration().getStringList("functions.carryMobs.blacklistedEntities").contains(event.getRightClicked().getType().toString())) {
                if (!config.getFileConfiguration().getStringList("functions.carryMobs.blacklistedWorlds").contains(event.getPlayer().getWorld().getName())) {
                    if (config.getFileConfiguration().getBoolean("functions.carryMobs.enabled")) {
                        if (!config.getFileConfiguration().getBoolean("functions.carryMobs.requirePermission")) {
                            if(event.getPlayer().isSneaking() && event.getPlayer().getInventory().getItemInMainHand().isEmpty()) {
                                pickUpEntity(config, event);
                            }
                        }else {
                            if(event.getPlayer().isSneaking() && event.getPlayer().getInventory().getItemInMainHand().isEmpty() && event.getPlayer().hasPermission("survivalutilities.carry.pickup." + event.getRightClicked().getType())) {
                                pickUpEntity(config,event);
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
            long startTime = sneakStartTimes.get(playerUUID);

            BukkitRunnable runnable = new BukkitRunnable() {
                //int time = 4;
                @Override
                public void run() {
                    time++;
                    StringBuilder s = new StringBuilder();
                    for(int i = 1; i < getNumberPattern(time,11); i++) {
                        s.append("█");
                    }
                    StringBuilder s2 = new StringBuilder();
                    for(int i = 1; i < 11 - getNumberPattern(time,11); i++) {
                        s2.append(" ");
                    }

                            TextComponent component = Component.text(s2.toString()  + s + "  " + ChatColor.BOLD + getNumberPattern(time,11) + ChatColor.RESET + "  " + s + s2);
                    //event.getPlayer().sendMessage(component);
                    event.getPlayer().sendActionBar(component);
                    s.delete(0,s.length());
                    if(!event.getPlayer().isSneaking()) {
                        cancel();
                        event.getPlayer().sendActionBar("");
                        //throwStrength = time / 10;
                    }
                }
            };
            if (event.getPlayer().getPassenger() != null) {
                runnable.runTaskTimer(SurvivalUtilities.getPlugin(SurvivalUtilities.class),0,2);
            }


        } else
            if (sneakStartTimes.containsKey(playerUUID)) {
                long startTime = sneakStartTimes.remove(playerUUID);
                for (Entity passenger : event.getPlayer().getPassengers()) {
                    if(!latestPickedEntity.contains(playerUUID)) {
                        event.getPlayer().removePassenger(passenger);
                        if(passenger.getType() == EntityType.PLAYER) {
                            Player player = (Player) passenger;
                            player.setVelocity(event.getPlayer().getLocation().getDirection().multiply(Math.min(getNumberPattern(time, 11) / 5, 2)));
                        }
                        passenger.setVelocity(event.getPlayer().getLocation().getDirection().multiply(Math.min(getNumberPattern(time, 11) / 5, 2)));

                        BukkitRunnable runnable = new BukkitRunnable() {

                            @Override
                            public void run() {
                                World world = passenger.getWorld();

                                world.spawnParticle(Particle.HAPPY_VILLAGER,passenger.getLocation(),1);
                                if(passenger.getLocation().add(0,-1,0).getBlock().getType() != Material.AIR) {
                                    cancel();
                                }
                            }
                        };
                        runnable.runTaskTimer(SurvivalUtilities.getPlugin(SurvivalUtilities.class),0,1);
                    }
                }
            }
        latestPickedEntity.remove(playerUUID);
    }

    private static void pickUpEntity(Config config, PlayerInteractAtEntityEvent event) {
        if(config.getFileConfiguration().getBoolean("functions.carryMobs.allowMultiPicking") || event.getPlayer().getPassenger() == null) {
            if(config.getFileConfiguration().getBoolean("functions.carryMobs.allowRiders") || event.getRightClicked().getPassenger() == null) {
                event.getPlayer().addPassenger(event.getRightClicked());
                if(!latestPickedEntity.contains(event.getPlayer().getUniqueId())) {
                    latestPickedEntity.add(event.getPlayer().getUniqueId());
                }
            }
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
}
