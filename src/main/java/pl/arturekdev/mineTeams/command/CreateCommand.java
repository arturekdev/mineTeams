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
import pl.arturekdev.mineUtiles.utils.TimeUtil;

import java.util.HashSet;

public class CreateCommand extends SubCommand {

    public CreateCommand(Player player, String[] args) {
        super(player, args);
    }

    @Override
    public void run() {

        JsonObject config = Teams.getInstance().getConfiguration().getElement("configuration").getAsJsonObject();

        if (args.length != 1) {
            MessageUtil.sendMessage(player, Messages.get("usageCreate", " &8>> &cPoprawne użycie: &e/team create <TAG>"));
            return;
        }

        if (TeamUtil.hasTeam(player)) {
            MessageUtil.sendMessage(player, Messages.get("youHasTeam", " &8>> &cPosiadasz już zespół!"));
            return;
        }

        String tag = args[0];

        if (tag.length() > config.get("maxCharsTag").getAsInt()) {
            MessageUtil.sendMessage(player, Messages.get("maxChars", " &8>> &cTag może mieć maksymalnie 5 znaków!"));
            return;
        }

        Team team = TeamUtil.getTeam(tag);

        if (team != null) {
            MessageUtil.sendMessage(player, Messages.get("tagBusy", " &8>> &cPodany TAG jest już zajęty!"));
            return;
        }

        team = Team.builder()
                .tag(tag)
                .owner(player.getUniqueId())
                .pvp(false)
                .glowing(false)
                .importance(System.currentTimeMillis() + TimeUtil.timeFromString(config.get("importance").getAsJsonObject().get("start").getAsString()))
                .created(System.currentTimeMillis())
                .bank(0)
                .kills(0)
                .deaths(0)
                .vaultSize(config.get("vaultStartSize").getAsInt())
                .slots(config.get("slotsStart").getAsInt())
                .vault(Bukkit.createInventory(null, 9 * config.get("vaultStartSize").getAsInt(), Messages.get("vaultTitleGUI", "&6Skarbiec twojego zespołu")))
                .members(new HashSet<>())
                .build();

        TeamUtil.getTeams().add(team);

        MessageUtil.sendMessage(player, Messages.get("successCreate", " &8>> &aPomyślnie stworzyłeś zespół o tagu &e%tag%").replace("%tag%", tag));
        Bukkit.broadcastMessage(MessageUtil.fixColor(Messages.get("successCreateBroadcast", " &6&lZespoły &8>> &e%player% &azałożył zespoł o tagu &e%tag%&a! Gratulacje!").replace("%player%", player.getName()).replace("%tag%", team.getTag())));
    }
}
