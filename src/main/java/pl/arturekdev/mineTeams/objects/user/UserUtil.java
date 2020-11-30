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
        return users.stream().filter(user -> user.getUuid().equals(uuid)).findFirst().orElse(new User(uuid));
    }
}
