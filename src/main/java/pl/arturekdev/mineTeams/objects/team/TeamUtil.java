package pl.arturekdev.mineTeams.objects.team;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import pl.arturekdev.mineTeams.database.DatabaseConnector;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TeamUtil {

    private static final List<Team> teams = new ArrayList<>();

    public static List<Team> getTeams() {
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

    public void loadTeams(DatabaseConnector databaseConnector) {
        try {
            try (ResultSet rs = databaseConnector.executeQuery("SELECT * FROM mineTeamsTeams")) {
                while (rs.next()) {
                    Team team = new Team(rs);
                    teams.add(team);
                }
                Bukkit.getLogger().info(" Pomyślnie załadowano " + teams.size() + " zespołów.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
