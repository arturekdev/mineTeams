package pl.arturekdev.mineTeams.placeholder;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import pl.arturekdev.mineTeams.objects.user.User;
import pl.arturekdev.mineTeams.objects.user.UserUtil;

public class PlaceholderExpansionUsers extends PlaceholderExpansion {

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

        return null;
    }
}
