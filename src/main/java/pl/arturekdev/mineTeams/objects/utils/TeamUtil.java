package pl.arturekdev.mineTeams.objects.utils;

import lombok.Getter;
import org.bukkit.entity.Player;
import pl.arturekdev.mineTeams.objects.Team;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class TeamUtil {

    @Getter
    private static final Set<Team> teams = new HashSet<>();
    @Getter
    private static final HashMap<Team, Player> teamsInvites = new HashMap<>();
    @Getter
    private static final HashMap<Player, Long> deathsPlayers = new HashMap<>();


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
