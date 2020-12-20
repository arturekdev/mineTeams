package pl.arturekdev.mineTeams.placeholder;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import pl.arturekdev.mineTeams.comparators.TeamBankRanking;
import pl.arturekdev.mineTeams.comparators.TeamKDRanking;
import pl.arturekdev.mineTeams.comparators.UserKillRanking;
import pl.arturekdev.mineTeams.messages.Messages;
import pl.arturekdev.mineTeams.objects.team.Team;
import pl.arturekdev.mineTeams.objects.team.TeamUtil;
import pl.arturekdev.mineTeams.objects.user.User;
import pl.arturekdev.mineTeams.objects.user.UserUtil;
import pl.arturekdev.mineUtiles.utils.MessageUtil;
import pl.arturekdev.mineUtiles.utils.TimeUtil;

import java.util.List;

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
            return team == null ? MessageUtil.fixColor(Messages.get("nullTeam", "&cBrak")) : String.valueOf(team.getStats().getKills());
        }

        if (identifier.equalsIgnoreCase("team_deaths")) {
            return team == null ? MessageUtil.fixColor(Messages.get("nullTeam", "&cBrak")) : String.valueOf(team.getStats().getDeaths());
        }

        if (identifier.equalsIgnoreCase("team_kd")) {
            return team == null ? MessageUtil.fixColor(Messages.get("nullTeam", "&cBrak")) : String.valueOf(team.getKD());
        }

        if (identifier.equalsIgnoreCase("team_members")) {
            return team == null ? MessageUtil.fixColor(Messages.get("nullTeam", "&cBrak")) : MessageUtil.fixColor("&f" + team.getMembers().size() + "&8/&e" + team.getSlots());
        }

        if (identifier.equalsIgnoreCase("team_importance")) {
            return team == null ? MessageUtil.fixColor(Messages.get("nullTeam", "&cBrak")) : TimeUtil.formatDate(team.getImportance());
        }

        if (identifier.equalsIgnoreCase("team_bank")) {
            return team == null ? MessageUtil.fixColor(Messages.get("nullTeam", "&cBrak")) : String.valueOf(team.getStats().getBank());
        }

        if (identifier.contains("team_top_")) {

            String[] strings = identifier.split("team_top_");
            int integer = Integer.parseInt(strings[1]) - 1;

            List<Team> teams = TeamUtil.getTeams();

            if (integer >= teams.size()) {
                return MessageUtil.fixColor("&cBrak");
            }

            teams.sort(new TeamKDRanking());

            Team team1 = teams.get(integer);

            return MessageUtil.fixColor("&e%tag% &8⇒ &f%kd% K/D").replace("%tag%", team1.getTag()).replace("%kd%", String.valueOf(team1.getKD()));
        }

        if (identifier.contains("team_topBank_")) {

            String[] strings = identifier.split("team_topBank_");
            int integer = Integer.parseInt(strings[1]) - 1;

            List<Team> teams = TeamUtil.getTeams();

            if (integer >= teams.size()) {
                return MessageUtil.fixColor("&cBrak");
            }

            teams.sort(new TeamBankRanking());

            Team team1 = teams.get(integer);

            return MessageUtil.fixColor("&e%tag% &8⇒ &f%bank% &eIskier").replace("%tag%", team1.getTag()).replace("%bank%", String.valueOf(team1.getStats().getBank()));
        }

        User user = UserUtil.getUser(player.getUniqueId());

        if (identifier.equalsIgnoreCase("user_kills")) {
            return String.valueOf(user.getKills());
        }

        if (identifier.equalsIgnoreCase("user_deaths")) {
            return String.valueOf(user.getDeaths());
        }

        if (identifier.equalsIgnoreCase("user_kd")) {
            return String.valueOf(user.getKills() / user.getDeaths());
        }

        if (identifier.contains("user_top_")) {

            String[] strings = identifier.split("user_top_");
            int integer = Integer.parseInt(strings[1]) - 1;

            List<User> users = UserUtil.getUsers();

            if (integer >= users.size()) {
                return MessageUtil.fixColor("&cBrak");
            }

            users.sort(new UserKillRanking());

            User user1 = users.get(integer);

            if (user1 == null) {
                return MessageUtil.fixColor("&cBrak");
            }

            return MessageUtil.fixColor("&e%name% &8⇒ &f%kills% &eZabójstw").replace("%name%", user1.getUsername()).replace("%kills%", String.valueOf(user1.getKills()));
        }

        return null;
    }
}
