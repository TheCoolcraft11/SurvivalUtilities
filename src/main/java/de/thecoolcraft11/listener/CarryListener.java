package de.thecoolcraft11.listener;

import de.thecoolcraft11.SurvivalUtilities;
import de.thecoolcraft11.util.Config;
import io.papermc.paper.event.player.ChatEvent;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

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


    @EventHandler
    public void onPlayerToggleSneak(PlayerToggleSneakEvent event) {
        UUID playerUUID = event.getPlayer().getUniqueId();

        if (event.isSneaking()) {
            sneakStartTimes.put(playerUUID, System.currentTimeMillis());
        } else
            if (sneakStartTimes.containsKey(playerUUID)) {
                long startTime = sneakStartTimes.remove(playerUUID);
                float sneakDuration = (float) (System.currentTimeMillis() - startTime) / 100;
                for (Entity passenger : event.getPlayer().getPassengers()) {
                    if(!latestPickedEntity.contains(playerUUID)) {
                        event.getPlayer().removePassenger(passenger);
                        if(passenger.getType() == EntityType.PLAYER) {
                            passenger.teleport(event.getPlayer());
                        }
                        passenger.setVelocity(event.getPlayer().getLocation().getDirection().multiply(Math.min(sneakDuration / 5, 2)));
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

}
