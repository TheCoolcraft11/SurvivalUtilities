package de.thecoolcraft11.commands;

import de.thecoolcraft11.SurvivalUtilities;
import de.thecoolcraft11.util.Config;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;


public class ChunkbaseCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        Player player = (Player) sender;
        Config config = new Config("config.yml", SurvivalUtilities.getProvidingPlugin(SurvivalUtilities.class).getDataFolder());
        if (config.getFileConfiguration().getBoolean("commands.chunkbase.enabled")) {
            if (player.hasPermission("survivalutilities.chunkbase")) {
                String seed = "seed=" + player.getWorld().getSeed();
                String xPos = "pinX=" + ((int) player.getX());
                String zPos = "pinZ=" + ((int) player.getZ());
                String x = "x=" + ((int) player.getX());
                String z = "z=" + ((int) player.getZ());
                String dimension = "dimension=overworld";
                String dim = "Overworld";
                if (player.getWorld().getName().equals("world_nether")) {
                    dimension = "dimension=" + "nether";
                    dim = "Nether";
                }
                if (player.getWorld().getName().equals("world_the_end")) {
                    dimension = "dimension=" + "end";
                    dim = "End";
                }
                String url = "https://www.chunkbase.com/apps/seed-map#" + seed + "&" + xPos + "&" + zPos + "&" + x + "&" + z + "&" + dimension;
                TextComponent hoverText = Component.text("Seed = " + player.getWorld().getSeed() + "\n" + " X = " + ((int) player.getX()) + " Z = " + ((int) player.getZ()) + "\n" + "Dimension = " + dim);
                TextComponent textComponent = Component.text("Click here to open the Chunkbase Seed Map").color(TextColor.color(0, 155, 255)).decorate(TextDecoration.UNDERLINED).clickEvent(ClickEvent.openUrl(url)).hoverEvent(HoverEvent.showText(hoverText));
                sender.sendMessage(textComponent);

                return true;
            }
        }
        return false;
    }
}
