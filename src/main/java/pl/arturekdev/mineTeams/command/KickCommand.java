package pl.arturekdev.mineTeams.command;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import pl.arturekdev.mineTeams.command.util.SubCommand;
import pl.arturekdev.mineTeams.messages.Messages;
import pl.arturekdev.mineTeams.objects.team.Team;
import pl.arturekdev.mineTeams.objects.team.TeamUtil;
import pl.arturekdev.mineUtiles.utils.MessageUtil;

import java.util.UUID;

public class KickCommand extends SubCommand {

    public KickCommand(Player player, String[] args) {
        super(player, args);
    }

    @Override
    public void run() {

        if (args.length != 1) {
            MessageUtil.sendMessage(player, Messages.get("usageKick", " &8>> &cPoprawne użycie: &e/team kick <nick>"));
            return;
        }

        UUID victimUUID = Bukkit.getPlayerUniqueId(args[0]);

        if (victimUUID == null) {
            MessageUtil.sendMessage(player, Messages.get("uuidNull", " &8>> &cBrak takiego gracza w bazie danych!"));
            return;
        }

        Team team = TeamUtil.getTeam(player);

        if (team == null) {
            MessageUtil.sendMessage(player, Messages.get("youDontHaveTeam", " &8>> &cNie posiadasz zespołu!"));
            return;
        }

        if (!team.getOwner().equals(player.getUniqueId())) {
            MessageUtil.sendMessage(player, Messages.get("youNeedBeOwner", " &8>> &cMusisz być właścicielem!"));
            return;
        }

        if (!team.getMembers().contains(victimUUID)) {
            MessageUtil.sendMessage(player, Messages.get("victimIsNotMember", " &8>> &cPodany gracz nie jest członkiem twojego zespołu!"));
            return;
        }

        team.getMembers().remove(victimUUID);
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(victimUUID);

        MessageUtil.sendMessage(player, Messages.get("successKick", " &8>> &aPomyślnie wyrzuciłeś &e%player% &aze swojego zespołu!").replace("%player%", offlinePlayer.getName()));
        Bukkit.broadcastMessage(MessageUtil.fixColor(Messages.get("successJoinBroadcast", " &6&lZespoły &8>> &e%player% &czostał wyrzucony z zespołu &e%tag%").replace("%player%", offlinePlayer.getName()).replace("%tag%", team.getTag())));

    }
}
