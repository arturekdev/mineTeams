package pl.arturekdev.mineTeams.command;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import pl.arturekdev.mineTeams.command.util.SubCommand;
import pl.arturekdev.mineTeams.messages.Messages;
import pl.arturekdev.mineTeams.objects.team.Team;
import pl.arturekdev.mineTeams.objects.team.TeamUtil;
import pl.arturekdev.mineUtiles.utils.MessageUtil;

public class JoinCommand extends SubCommand {

    public JoinCommand() {
        super("join");
    }

    @Override
    public void handleCommand(Player player, String[] arguments) {
        if (arguments.length != 1) {
            MessageUtil.sendMessage(player, Messages.get("usageJoin", " &8>> &cPoprawne użycie: &e/team join <tag>"));
            return;
        }

        Team team = TeamUtil.getTeam(arguments[0]);

        if (team == null) {
            MessageUtil.sendMessage(player, Messages.get("teamIsNull", " &8>> &cNie ma takiego zespołu!"));
            return;
        }

        if (!team.getInvites().contains(player)) {
            MessageUtil.sendMessage(player, Messages.get("youDontHaveInvite", " &8>> &cNie posiadasz zaproszenie do tego zespołu!"));
            return;
        }

        team.getMembers().add(player.getUniqueId());
        team.getInvites().remove(player);
        team.setNeedUpdate(true);

        MessageUtil.sendMessage(player, Messages.get("successJoin", " &8>> &aPomyślnie dołączyłeś do zespołu &e%tag%").replace("%tag%", team.getTag()));
        Bukkit.broadcastMessage(MessageUtil.fixColor(Messages.get("successJoinBroadcast", " &6&lZespoły &8>> &e%player% &adołączył do zespołu &e%tag%").replace("%player%", player.getName()).replace("%tag%", team.getTag())));
    }
}
