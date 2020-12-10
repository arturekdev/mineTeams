package pl.arturekdev.mineTeams.command;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import pl.arturekdev.mineTeams.command.util.SubCommand;
import pl.arturekdev.mineTeams.database.DatabaseConnector;
import pl.arturekdev.mineTeams.messages.Messages;
import pl.arturekdev.mineTeams.objects.team.Team;
import pl.arturekdev.mineTeams.objects.team.TeamUtil;
import pl.arturekdev.mineUtiles.utils.MessageUtil;

import java.util.Collections;

public class DeleteCommand extends SubCommand {

    private final DatabaseConnector databaseConnector;

    public DeleteCommand(DatabaseConnector databaseConnector) {
        super("delete", Collections.singletonList("remove"));
        this.databaseConnector = databaseConnector;
    }

    @Override
    public void handleCommand(Player player, String[] arguments) {
        Team team = TeamUtil.getTeam(player);

        if (team == null) {
            MessageUtil.sendMessage(player, Messages.get("youDontHaveTeam", " &8>> &cNie posiadasz zespołu!"));
            return;
        }

        if (!team.getOwner().equals(player.getUniqueId())) {
            MessageUtil.sendMessage(player, Messages.get("youNeedBeOwner", " &8>> &cMusisz być właścicielem!"));
            return;
        }

        databaseConnector.executeUpdate("DELETE FROM `mineTeamsTeams` WHERE `tag` = '" + team.getTag() + "'");
        TeamUtil.getTeams().remove(team);

        MessageUtil.sendMessage(player, Messages.get("successDelete", " &8>> &aPomyślnie usunąłeś swój zespół!"));
        Bukkit.broadcastMessage(MessageUtil.fixColor(Messages.get("successDeleteBroadcast", " &6&lZespoły &8>> &e%player% &crozwiązał zespoł o tagu &e%tag%&c!").replace("%player%", player.getName()).replace("%tag%", team.getTag())));

    }
}
