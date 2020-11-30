package pl.arturekdev.mineTeams.command;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import pl.arturekdev.mineTeams.command.util.SubCommand;
import pl.arturekdev.mineTeams.messages.Messages;
import pl.arturekdev.mineTeams.objects.team.Team;
import pl.arturekdev.mineTeams.objects.team.TeamUtil;
import pl.arturekdev.mineUtiles.utils.MessageUtil;
import pl.arturekdev.mineUtiles.utils.TimeUtil;

import java.util.List;
import java.util.UUID;

public class InfoCommand extends SubCommand {

    public InfoCommand(Player player, String[] args) {
        super(player, args);
    }

    @Override
    public void run() {

        if (args.length != 1) {

            Team team = TeamUtil.getTeam(player);

            if (team == null) {
                MessageUtil.sendMessage(player, Messages.get("youDontHaveTeam", " &8>> &cNie posiadasz zespołu!"));
                return;
            }

            OfflinePlayer owner = Bukkit.getOfflinePlayer(team.getOwner());

            StringBuilder members = new StringBuilder();
            for (UUID member : team.getMembers()) {
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(member);
                members.append(offlinePlayer.isOnline() ? "&a" + offlinePlayer.getName() : "&c" + offlinePlayer.getName()).append("&7, ");
            }

            List<String> strings = Messages.getList("teamInfo",
                    " ;" +
                            " &8>> &6Tag: &7%tag% ;" +
                            " &8>> &6Właściciel: %owner% ;" +
                            " &8>> &6Członkowie &8(&e%membersSize%&8/&e%slots%&8): &7%members% ;" +
                            " &8>> &6Zabójstw: &7%kills% ;" +
                            " &8>> &6Zgonów: &7%deaths% ;" +
                            " &8>> &6Ważny do: &7%importance% ;" +
                            " &8>> &6Stworzono: &7%created% ;" +
                            " ;" +
                            " &8>> &6Aby sprawdzić inny zespół użyj &e/team info <TAG>");

            for (String string : strings) {
                string = string.replace("%tag%", team.getTag());
                string = string.replace("%owner%", owner.isOnline() ? "&a" + owner.getName() : "&c" + owner.getName());
                string = string.replace("%members%", members.toString());
                string = string.replace("%created%", TimeUtil.formatDate(team.getCreated()));
                string = string.replace("%importance%", TimeUtil.formatDate(team.getImportance()));
                string = string.replace("%kills%", String.valueOf(team.getKills()));
                string = string.replace("%deaths%", String.valueOf(team.getDeaths()));
                string = string.replace("%slots%", String.valueOf(team.getSlots()));
                string = string.replace("%membersSize%", String.valueOf(team.getMembers().size()));
                MessageUtil.sendMessage(player, string);
            }

        } else {

            Team team = TeamUtil.getTeam(args[0]);

            if (team == null) {
                MessageUtil.sendMessage(player, Messages.get("teamIsNull", " &8>> &cNie ma takiego zespołu!"));
                return;
            }

            OfflinePlayer owner = Bukkit.getOfflinePlayer(team.getOwner());

            StringBuilder members = new StringBuilder();
            for (UUID member : team.getMembers()) {
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(member);
                members.append(offlinePlayer.isOnline() ? "&a" + offlinePlayer.getName() : "&c" + offlinePlayer.getName()).append("&7, ");
            }

            List<String> strings = Messages.getList("teamInfo",
                    " ;" +
                            " &8>> &6Tag: &7%tag% ;" +
                            " &8>> &6Właściciel: %owner% ;" +
                            " &8>> &6Członkowie &8(&e%membersSize%&8/&e%slots%&8): &7%members% ;" +
                            " &8>> &6Zabójstw: &7%kills% ;" +
                            " &8>> &6Zgonów: &7%deaths% ;" +
                            " &8>> &6Ważny do: &7%importance% ;" +
                            " &8>> &6Stworzono: &7%created% ;" +
                            " ;" +
                            " &8>> &6Aby sprawdzić inny zespół użyj &e/team info <TAG>");

            for (String string : strings) {
                string = string.replace("%tag%", team.getTag());
                string = string.replace("%owner%", owner.isOnline() ? "&a" + owner.getName() : "&c" + owner.getName());
                string = string.replace("%members%", members.toString());
                string = string.replace("%created%", TimeUtil.formatDate(team.getCreated()));
                string = string.replace("%importance%", TimeUtil.formatDate(team.getImportance()));
                string = string.replace("%kills%", String.valueOf(team.getKills()));
                string = string.replace("%deaths%", String.valueOf(team.getDeaths()));
                string = string.replace("%slots%", String.valueOf(team.getSlots()));
                string = string.replace("%membersSize%", String.valueOf(team.getMembers().size()));
                MessageUtil.sendMessage(player, string);
            }
        }
    }
}
