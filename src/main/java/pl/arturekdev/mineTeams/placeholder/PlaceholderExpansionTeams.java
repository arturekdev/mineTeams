package pl.arturekdev.mineTeams.placeholder;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import pl.arturekdev.mineTeams.messages.Messages;
import pl.arturekdev.mineTeams.objects.team.Team;
import pl.arturekdev.mineTeams.objects.team.TeamUtil;
import pl.arturekdev.mineUtiles.utils.MessageUtil;

public class PlaceholderExpansionTeams extends PlaceholderExpansion {

    @Override
    public String getIdentifier() {
        return "mineTeams";
    }

    @Override
    public String getAuthor() {
        return "arturekdev";
    }

    @Override
    public String getVersion() {
        return "1.0";
    }

    public String onPlaceholderRequest(Player player, String identifier) {

        if (player == null) {
            return "";
        }

        Team team = TeamUtil.getTeam(player);

        if (identifier.equalsIgnoreCase("team_tag")) {
            return team == null ? "" : MessageUtil.fixColor("&8(&e" + team.getTag() + "&8)");
        }

        if (identifier.equalsIgnoreCase("team_tag_sc")) {
            return team == null ? MessageUtil.fixColor(Messages.get("nullTeam", "&cBrak")) : MessageUtil.fixColor("&e" + team.getTag());
        }

        if (identifier.equalsIgnoreCase("team_kills")) {
            return team == null ? "0" : String.valueOf(team.getStats().getKills());
        }

        if (identifier.equalsIgnoreCase("team_deaths")) {
            return team == null ? "0" : String.valueOf(team.getStats().getDeaths());
        }

        if (identifier.equalsIgnoreCase("team_kd")) {
            return team == null ? "0" : String.valueOf(team.getStats().getKills() / team.getStats().getDeaths());
        }

        return null;
    }
}
