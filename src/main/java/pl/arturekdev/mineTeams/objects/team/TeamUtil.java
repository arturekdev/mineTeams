package pl.arturekdev.mineTeams.objects.team;

import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

public class TeamUtil {

    private static final Set<Team> teams = new HashSet<>();

    public static Set<Team> getTeams() {
        return teams;
    }

    public static Team getTeam(String tag) {
        for (Team team : teams) {
            if (team.getTag().equals(tag)) {
                return team;
            }
        }
        return null;
    }

    public static Team getTeam(Player player) {
        for (Team team : teams) {
            if (team.getOwner().equals(player.getUniqueId()) || team.getMembers().contains(player.getUniqueId())) {
                return team;
            }
        }
        return null;
    }

    public static boolean hasTeam(Player player) {
        for (Team team : teams) {
            if (team.getOwner().equals(player.getUniqueId())) {
                return true;
            }
        }
        return false;
    }


}
