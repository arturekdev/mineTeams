package pl.arturekdev.mineTeams.objects.user;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class UserUtil {

    private static final Set<User> users = new HashSet<>();

    public static Set<User> getUsers() {
        return users;
    }

    public static User getUser(UUID uuid) {
        for (User user : users) {
            if (user.getUuid().equals(uuid)) {
                return user;
            }
        }
        User user = new User();
        users.add(user);
        return user;
    }
}
