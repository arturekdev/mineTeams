package pl.arturekdev.mineTeams.command;

import org.bukkit.entity.Player;
import pl.arturekdev.mineTeams.command.util.SubCommand;
import pl.arturekdev.mineTeams.messages.Messages;
import pl.arturekdev.mineTeams.objects.team.Team;
import pl.arturekdev.mineTeams.objects.team.TeamUtil;
import pl.arturekdev.mineUtiles.utils.MessageUtil;

import java.util.Arrays;

public class PvpCommand extends SubCommand {

    public PvpCommand() {
        super("pvp", Arrays.asList("ff", "friendlyfire"));
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

        if (team.isPvp()) {
            team.setPvp(false);
            MessageUtil.sendMessage(player, Messages.get("successDisablePvp", " &8>> &aAtakowanie się miedzy członkami zespołu zostało &4wyłączone"));
        } else {
            team.setPvp(true);
            MessageUtil.sendMessage(player, Messages.get("successEnablePvp", " &8>> &aAtakowanie się miedzy członkami zespołu zostało &2włączone"));
        }
    }
}
