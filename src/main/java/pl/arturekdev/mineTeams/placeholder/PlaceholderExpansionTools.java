package pl.arturekdev.mineTeams.placeholder;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import pl.arturekdev.mineTeams.objects.Team;
import pl.arturekdev.mineTeams.objects.utils.TeamUtil;
import pl.arturekdev.mineUtiles.utils.MessageUtil;

public class PlaceholderExpansionTools extends PlaceholderExpansion {

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

        if (identifier.equalsIgnoreCase("team")) {

            Team team = TeamUtil.getTeam(player);

            if (team == null) {
                return "";
            }

            return MessageUtil.fixColor("&8(&e" + team.getTag() + "&8)");


        }
        if (identifier.equalsIgnoreCase("teamScoreboard")) {

            Team team = TeamUtil.getTeam(player);

            if (team == null) {
                return MessageUtil.fixColor("&cBrak");
            }

            return MessageUtil.fixColor("&e" + team.getTag());


        }

        if (player == null) {
            return "";
        }


        return null;
    }
}
