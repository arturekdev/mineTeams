package pl.arturekdev.mineTeams.command;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import pl.arturekdev.mineTeams.command.util.SubCommand;
import pl.arturekdev.mineTeams.messages.Messages;
import pl.arturekdev.mineTeams.objects.team.Team;
import pl.arturekdev.mineTeams.objects.team.TeamUtil;
import pl.arturekdev.mineUtiles.utils.MessageUtil;

public class DeleteCommand extends SubCommand {

    public DeleteCommand(Player player, String[] args) {
        super(player, args);
    }

    @Override
    public void run() {

        Team team = TeamUtil.getTeam(player);

        if (team == null) {
            MessageUtil.sendMessage(player, Messages.get("youDontHaveTeam", " &8>> &cNie posiadasz zespołu!"));
            return;
        }

        if (!team.getOwner().equals(player.getUniqueId())) {
            MessageUtil.sendMessage(player, Messages.get("youNeedBeOwner", " &8>> &cMusisz być właścicielem!"));
            return;
        }

        TeamUtil.getTeams().remove(team);
        MessageUtil.sendMessage(player, Messages.get("successDelete", " &8>> &aPomyślnie usunąłeś swój zespół!"));
        Bukkit.broadcastMessage(MessageUtil.fixColor(Messages.get("successDeleteBroadcast", " &6&lZespoły &8>> &e%player% &crozwiązał zespoł o tagu &e%tag%&c!").replace("%player%", player.getName()).replace("%tag%", team.getTag())));

    }
}
