package pl.arturekdev.mineTeams.runnable;

import pl.arturekdev.mineTeams.database.DatabaseConnector;
import pl.arturekdev.mineTeams.objects.user.UserUtil;

public class UsersSaveRunnable implements Runnable {

    private final DatabaseConnector databaseConnector;

    public UsersSaveRunnable(DatabaseConnector databaseConnector) {
        this.databaseConnector = databaseConnector;
    }

    @Override
    public void run() {
        UserUtil.getUsers().forEach(user -> user.update(databaseConnector));
    }
}
