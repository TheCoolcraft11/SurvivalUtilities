package de.thecoolcraft11.listener;

import com.destroystokyo.paper.event.entity.SlimeTargetLivingEntityEvent;
import de.thecoolcraft11.SurvivalUtilities;
import de.thecoolcraft11.util.Config;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;

public class MonsterListener implements Listener {
    Config config = new Config("config.yml", SurvivalUtilities.getProvidingPlugin(SurvivalUtilities.class).getDataFolder());

    @EventHandler
    public void onPlayerAttack(EntityDamageByEntityEvent event) {
        if (!config.getFileConfiguration().getBoolean("functions.monsters.enabled")) return;
        Entity damager = event.getDamager();
        Entity entity = event.getEntity();

        if (damager instanceof Player) {
            if (entity instanceof Monster monster) {
                monster.setTarget((Player) damager);
                monster.getNearbyEntities(10, 10, 10).forEach(entity1 -> {
                    if (entity1.getType() == monster.getType()) {
                        ((Monster) entity1).setTarget(((Player) damager));
                    }
                });
            }
        } else if (damager instanceof Arrow arrow) {
            if (arrow.getShooter() instanceof Player) {
                Entity hitEntity = event.getEntity();
                if (hitEntity instanceof Monster monster) {
                    monster.setTarget((Player) arrow.getShooter());
                    monster.getNearbyEntities(10, 10, 10).forEach(entity1 -> {
                        if (entity1.getType() == monster.getType()) {
                            ((Monster) event).setTarget(((Player) arrow.getShooter()));
                        }
                    });
                }
            }
        }
    }

    @EventHandler
    public void onMonsterTarget(EntityTargetEvent event) {
        if (!config.getFileConfiguration().getBoolean("functions.monsters.enabled")) return;
        if (event.getEntity() instanceof Monster) {
            if (event.getTarget() instanceof Player) {
                if (!config.getFileConfiguration().getStringList("functions.monsters.allowed").contains(event.getEntity().getType().toString())) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityTargetLivingEntity(EntityTargetLivingEntityEvent event) {
        if (!config.getFileConfiguration().getBoolean("functions.monsters.enabled")) return;
        if (event.getEntity() instanceof Monster) {
            if (event.getTarget() instanceof Player) {
                if (!config.getFileConfiguration().getStringList("functions.monsters.allowed").contains(event.getEntity().getType().toString())) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onSlimeTargetLivingEntity(SlimeTargetLivingEntityEvent event) {
        if (!config.getFileConfiguration().getBoolean("functions.monsters.enabled")) return;
        if (event.getEntity() instanceof Monster) {
            if (event.getTarget() instanceof Player) {
                if (!config.getFileConfiguration().getStringList("functions.monsters.allowed").contains(event.getEntity().getType().toString())) {
                    event.setCancelled(true);
                }
            }
        }
    }


}
