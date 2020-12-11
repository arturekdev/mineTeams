package pl.arturekdev.mineTeams.runnable;

import pl.arturekdev.mineTeams.database.DatabaseConnector;
import pl.arturekdev.mineTeams.objects.team.TeamUtil;

public class TeamsSaveRunnable implements Runnable{

    private final DatabaseConnector databaseConnector;

    public TeamsSaveRunnable(DatabaseConnector databaseConnector) {
        this.databaseConnector = databaseConnector;
    }

    @Override
    public void run() {
        TeamUtil.getTeams().forEach(team -> team.update(databaseConnector));
    }

}
