package pl.arturekdev.mineTeams.command;

import com.google.gson.JsonObject;
import org.bukkit.entity.Player;
import pl.arturekdev.mineTeams.Teams;
import pl.arturekdev.mineTeams.command.util.SubCommand;
import pl.arturekdev.mineTeams.messages.Messages;
import pl.arturekdev.mineTeams.objects.team.Team;
import pl.arturekdev.mineTeams.objects.team.TeamUtil;
import pl.arturekdev.mineUtiles.utils.MessageUtil;

public class SlotsCommand extends SubCommand {

    public SlotsCommand() {
        super("slots");
    }

    @Override
    public void handleCommand(Player player, String[] arguments) {
        JsonObject config = Teams.getInstance().getConfiguration().getElement("configuration").getAsJsonObject();

        Team team = TeamUtil.getTeam(player);

        if (team == null) {
            MessageUtil.sendMessage(player, Messages.get("youDontHaveTeam", " &8>> &cNie posiadasz zespołu!"));
            return;
        }

        if (arguments.length != 2) {
            MessageUtil.sendMessage(player, Messages.get("teamSlots", " &8>> &aTwój zespół aktualnie posiada &e%slots% &aslotów.").replace("%slots%", String.valueOf(team.getSlots())));
            if (team.getSlots() != config.get("slotsLimit").getAsInt()) {
                MessageUtil.sendMessage(player, Messages.get("slotsUpgradeInformation", " &8>> &aMożesz powiększyć ilośc slotów w zespole komendą &e/team slots upgrade"));
            }
            return;
        }

        if (!arguments[1].equalsIgnoreCase("upgrade")) {
            MessageUtil.sendMessage(player, Messages.get("usageSlotsUpgrade", " &8>> &cPoprawne użycie: &e/team slots upgrade"));
        }

        if (team.getSlots() == config.get("slotsLimit").getAsInt()) {
            MessageUtil.sendMessage(player, Messages.get("slotsMaxSize", " &8>> &cSloty w twoim zespole są juz maksymalnie powiększone!"));
            return;
        }

        if (team.getBank() < config.get("slotsUpgradeCoast").getAsInt()) {
            MessageUtil.sendMessage(player, Messages.get("bankDosntHasMoney", " &8>> &cBank twojego zespołu nie ma wystarczająco pieniędzy!"));
            return;
        }

        team.setSlots(team.getSlots() + 1);

        MessageUtil.sendMessage(player, Messages.get("successUpgradeSlots", " &8>> &aPomyślnie powiększyłeś ilość slotów!"));

    }
}
