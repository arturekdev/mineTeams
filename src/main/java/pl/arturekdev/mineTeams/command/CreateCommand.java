package pl.arturekdev.mineTeams.command;

import com.google.gson.*;
import org.bukkit.*;
import org.bukkit.entity.*;
import pl.arturekdev.mineEconomy.*;
import pl.arturekdev.mineTeams.*;
import pl.arturekdev.mineTeams.command.util.*;
import pl.arturekdev.mineTeams.configuration.*;
import pl.arturekdev.mineTeams.messages.*;
import pl.arturekdev.mineTeams.objects.team.*;
import pl.arturekdev.mineUtiles.utils.*;

public class CreateCommand extends SubCommand {

    private final Config configuration;
    private final EconomyService economyService = new EconomyService();

    public CreateCommand(Config configuration) {
        super("create");
        this.configuration = configuration;
    }

    @Override
    public void handleCommand(Player player, String[] arguments) {
        JsonObject config = Teams.getInstance().getConfiguration().getElement("configuration").getAsJsonObject();

        if (arguments.length != 1) {
            MessageUtil.sendMessage(player, Messages.get("usageCreate", " &8>> &cPoprawne użycie: &e/team create <TAG>"));
            return;
        }

        if (TeamUtil.hasTeam(player)) {
            MessageUtil.sendMessage(player, Messages.get("youHasTeam", " &8>> &cPosiadasz już zespół!"));
            return;
        }

        int coast = config.get("createCoast").getAsInt();

        if (!economyService.has(player, coast)) {
            MessageUtil.sendMessage(player, Messages.get("playerDosntHasMoney", " &8>> &cNie posiadasz &e%coast% Iskier &caby móc założyć zespół!").replace("%coast%", String.valueOf(coast)));
            return;
        }

        String tag = arguments[0];

        if (tag.length() > config.get("maxCharsTag").getAsInt()) {
            MessageUtil.sendMessage(player, Messages.get("maxChars", " &8>> &cTag może mieć maksymalnie 5 znaków!"));
            return;
        }

        if (TeamUtil.getTeam(tag) != null) {
            MessageUtil.sendMessage(player, Messages.get("tagBusy", " &8>> &cPodany TAG jest już zajęty!"));
            return;
        }

        Team team = new Team(tag, player.getUniqueId(), configuration);

        TeamUtil.getTeams().add(team);
        economyService.takeMoney(player, coast);

        MessageUtil.sendMessage(player, Messages.get("successCreate", " &8>> &aPomyślnie stworzyłeś zespół o tagu &e%tag%").replace("%tag%", tag));
        Bukkit.broadcastMessage(MessageUtil.fixColor(Messages.get("successCreateBroadcast", " &6&lZespoły &8>> &e%player% &azałożył zespoł o tagu &e%tag%&a! Gratulacje!").replace("%player%", player.getName()).replace("%tag%", team.getTag())));
    }
}
