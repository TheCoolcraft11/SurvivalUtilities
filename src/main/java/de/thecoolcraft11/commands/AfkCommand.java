package de.thecoolcraft11.commands;

import de.thecoolcraft11.SurvivalUtilities;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;

public class AfkCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (sender instanceof Player player) {
            if (!SurvivalUtilities.AFKList.containsKey(player.getUniqueId())) {
                SurvivalUtilities.AFKList.put(player.getUniqueId(), player.playerListName());

                Component prefix = null;
                Component suffix = null;
                Team team = Bukkit.getScoreboardManager().getMainScoreboard().getEntryTeam(player.getName());
                if (team != null) {
                    prefix = team.prefix();
                    suffix = team.suffix();
                }

                assert prefix != null;
                Component afkName = Component.text("[AFK] ")
                        .color(TextColor.color(73, 75, 77))
                        .decorate(TextDecoration.BOLD)
                        .append(prefix)
                        .append(Component.text(player.getName())
                                .color(TextColor.color(118, 120, 122))
                                .decorate(TextDecoration.STRIKETHROUGH, TextDecoration.ITALIC)
                                .decoration(TextDecoration.BOLD, false))
                        .append(suffix);
                player.playerListName(afkName);

            } else {
                SurvivalUtilities.AFKList.remove(player.getUniqueId());
                Component prefix = null;
                Component suffix = null;
                Team team = Bukkit.getScoreboardManager().getMainScoreboard().getEntryTeam(player.getName());
                if (team != null) {
                    prefix = team.prefix();
                    suffix = team.suffix();
                }
                assert prefix != null;
                Component restoredName = prefix
                        .append(Component.text(player.getName()))
                        .append(suffix);
                player.playerListName(restoredName);
            }
            return true;
        } else {
            sender.sendMessage(Component.text("Only players can use this command.").color(TextColor.color(255, 0, 0)));
            return false;
        }
    }
}
