package pl.arturekdev.mineTeams.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import pl.arturekdev.mineTeams.messages.Messages;
import pl.arturekdev.mineTeams.objects.team.Team;
import pl.arturekdev.mineTeams.objects.team.TeamUtil;
import pl.arturekdev.mineUtiles.utils.MessageUtil;

import java.util.UUID;

public class AsyncPlayerChatListener implements Listener {

    @EventHandler
    public void event(AsyncPlayerChatEvent e) {

        Player player = e.getPlayer();
        Team team = TeamUtil.getTeam(player);

        if (team == null) {
            return;
        }

        if (!e.getMessage().startsWith("!")) {
            return;
        }

        e.setCancelled(true);

        for (UUID member : team.getMembers()) {

            Player memberPlayer = Bukkit.getOfflinePlayer(member).getPlayer();

            if (memberPlayer == null) continue;

            MessageUtil.sendMessage(memberPlayer, Messages.get("teamFormatMessage", " &6&lZespół &e%player% &8>> &7%message%").replace("%player%", player.getName()).replace("%message%", e.getMessage().replaceFirst("!", "")));

        }

        MessageUtil.sendMessage(player, Messages.get("teamFormatMessage", " &6&lZespół &e%player% &8>> &7%message%").replace("%player%", player.getName()).replace("%message%", e.getMessage().replaceFirst("!", "")));

    }

}
