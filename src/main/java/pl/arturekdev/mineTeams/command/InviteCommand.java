package pl.arturekdev.mineTeams.command;

import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import pl.arturekdev.mineTeams.Teams;
import pl.arturekdev.mineTeams.command.util.SubCommand;
import pl.arturekdev.mineTeams.messages.Messages;
import pl.arturekdev.mineTeams.objects.team.Team;
import pl.arturekdev.mineTeams.objects.team.TeamUtil;
import pl.arturekdev.mineUtiles.utils.MessageUtil;

public class InviteCommand extends SubCommand {

    public InviteCommand() {
        super("invite");
    }

    @Override
    public void handleCommand(Player player, String[] arguments) {
        JsonObject config = Teams.getInstance().getConfiguration().getElement("configuration").getAsJsonObject();

        if (arguments.length != 1) {
            MessageUtil.sendMessage(player, Messages.get("usageCreate", " &8>> &cPoprawne użycie: &e/team invite <nick>"));
            return;
        }

        Player victim = Bukkit.getPlayer(arguments[0]);

        if (victim == null) {
            MessageUtil.sendMessage(player, Messages.get("offlinePlayer", " &8>> &cPodany gracz jest offline!"));
            return;
        }

        if (victim == player) {
            MessageUtil.sendMessage(player, Messages.get("samePlayer", " &8>> &cNie możesz zaprosić siebie samego!"));
            return;
        }

        if (TeamUtil.getTeam(victim) != null) {
            MessageUtil.sendMessage(player, Messages.get("victimHasTeam", " &8>> &cGracz którego chcesz zaprosić ma już zespół!"));
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
        if (team.getMembers().size() >= team.getSlots()) {
            MessageUtil.sendMessage(player, Messages.get("maxTeamSize", " &8>> &cOsiągnąłeś maksymalną liczbę slotów w zespole!"));

            if (team.getSlots() != config.get("slotsStart").getAsInt()) {
                MessageUtil.sendMessage(player, Messages.get("slotsUpgradeInformation", " &8>> &aMożesz powiększyć ilośc slotów w zespole komendą &e/team slots upgrade"));
            }
            return;
        }

        if (team.getInvites().contains(victim)) {
            MessageUtil.sendMessage(player, Messages.get("successCancelInvite", " &8>> &aPomyślnie anulowano zaproszenie dla &e%player%").replace("%player%", victim.getName()));
            team.getInvites().remove(victim);
            return;
        }

        team.getInvites().add(victim);

        MessageUtil.sendMessage(player, Messages.get("successInvite", " &8>> &aPomyślnie zaprosiłeś &e%player% &ado swojeto zespołu!").replace("%player%", victim.getName()));
        MessageUtil.sendMessage(player, Messages.get("usageCancelInvite", " &8>> &aAby wycofać zaproszenie użyj komendy &e/team invite %player%").replace("%player%", victim.getName()));
        MessageUtil.sendMessage(victim, Messages.get("successInviteVictim", " &8>> &aOtrzymałeś zaproszenie do zespołu gracza &e%player% &aużyj komendy &e/team join %tag%").replace("%player%", player.getName()).replace("%tag%", team.getTag()));

    }
}
