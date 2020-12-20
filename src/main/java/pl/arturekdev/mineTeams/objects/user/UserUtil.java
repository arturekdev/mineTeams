package pl.arturekdev.mineTeams.objects.user;

import org.bukkit.Bukkit;
import pl.arturekdev.mineTeams.database.DatabaseConnector;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UserUtil {

    private static final List<User> users = new ArrayList<>();

    public static List<User> getUsers() {
        return users;
    }

    public static User getUser(UUID uuid) {
        for (User user : users) {
            if (user.getUuid().equals(uuid)) {
                return user;
            }
        }
        User user = new User(uuid);
        users.add(user);
        return user;
    }

    public void loadUsers(DatabaseConnector databaseConnector) {
        try {
            try (ResultSet rs = databaseConnector.executeQuery("SELECT * FROM mineTeamsUsers")) {
                while (rs.next()) {
                    User user = new User(rs);
                    users.add(user);
                }
                Bukkit.getLogger().info(" Pomyślnie załadowano " + users.size() + " użytkowników.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
