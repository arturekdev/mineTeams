package pl.arturekdev.mineTeams.runnable;

import com.google.gson.JsonObject;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import pl.arturekdev.mineTeams.Teams;
import pl.arturekdev.mineTeams.messages.Messages;
import pl.arturekdev.mineTeams.objects.team.Team;
import pl.arturekdev.mineTeams.objects.team.TeamUtil;
import pl.arturekdev.mineUtiles.utils.MessageUtil;
import pl.arturekdev.mineUtiles.utils.TimeUtil;

public class TeamsImportanceRunnable implements Runnable {

    @Override
    public void run() {
        for (Team team : TeamUtil.getTeams()) {

            JsonObject config = Teams.getInstance().getConfiguration().getElement("configuration").getAsJsonObject();

            if (team.getImportance() > System.currentTimeMillis()) {

                long difference = team.getImportance() - System.currentTimeMillis();

                if (difference <= TimeUtil.timeFromString(config.get("importance").getAsJsonObject().get("expiresInfo").getAsString()) && team.getStats().getBank() < config.get("importance").getAsJsonObject().get("coast").getAsInt()) {
                    team.onlinePlayers().forEach(player -> player.sendActionBar(new TextComponent(MessageUtil.fixColor(Messages.get("teamImportanceAlert", "&cTwój zespoł niedługo wygaśnie! Wpłać do banku zespołu wymaganą kwote!")))));
                }

                continue;
            }

            if (team.getStats().getBank() >= config.get("importance").getAsJsonObject().get("coast").getAsInt()) {
                team.getStats().setBank(team.getStats().getBank() - config.get("importance").getAsJsonObject().get("coast").getAsInt());
                team.setImportance(team.getImportance() + TimeUtil.timeFromString(config.get("importance").getAsJsonObject().get("renewal").getAsString()));
                continue;
            }

            Bukkit.broadcastMessage(MessageUtil.fixColor(Messages.get("teamDeleteImportance", " &6&lZespoły &8>> &cZespół &e%tag% &cwygasł z powodu nieopłacania go!").replace("%tag%", team.getTag())));
        }

        TeamUtil.getTeams().removeIf(team -> team.getImportance() < System.currentTimeMillis());
    }
}
