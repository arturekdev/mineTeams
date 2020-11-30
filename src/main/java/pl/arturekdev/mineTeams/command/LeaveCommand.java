package pl.arturekdev.mineTeams.command;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import pl.arturekdev.mineTeams.command.util.SubCommand;
import pl.arturekdev.mineTeams.messages.Messages;
import pl.arturekdev.mineTeams.objects.team.Team;
import pl.arturekdev.mineTeams.objects.team.TeamUtil;
import pl.arturekdev.mineUtiles.utils.MessageUtil;

public class LeaveCommand extends SubCommand {

    public LeaveCommand(Player player, String[] args) {
        super(player, args);
    }

    @Override
    public void run() {

        Team team = TeamUtil.getTeam(player);

        if (team == null) {
            MessageUtil.sendMessage(player, Messages.get("youDontHaveTeam", " &8>> &cNie posiadasz zespołu!"));
            return;
        }

        if (team.getOwner().equals(player.getUniqueId())) {
            MessageUtil.sendMessage(player, Messages.get("youAreOwner", " &8>> &cJesteś właścicielem!"));
            return;
        }

        team.getMembers().remove(player.getUniqueId());

        MessageUtil.sendMessage(player, Messages.get("successLeave", " &8>> &aPomyślnie opuściłeś zespoł &e%tag%").replace("%tag%", team.getTag()));
        Bukkit.broadcastMessage(MessageUtil.fixColor(Messages.get("successLeaveBroadcast", " &6&lZespoły &8>> &e%player% &copóścił zespół &e%tag%").replace("%player%", player.getName()).replace("%tag%", team.getTag())));

    }
}
