package pl.arturekdev.mineTeams.database;

import lombok.SneakyThrows;
import pl.arturekdev.mineDatabase.DatabaseService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class DatabaseConnector {


    private final DatabaseService databaseService = new DatabaseService();

    public Connection getConnection() {
        return databaseService.getConnection();
    }

    @SneakyThrows
    public ResultSet executeQuery(String queryString) {
        PreparedStatement preparedStatement = getConnection().prepareStatement(queryString);
        return preparedStatement.executeQuery();
    }

    @SneakyThrows
    public void executeUpdate(String queryString) {
        PreparedStatement preparedStatement = getConnection().prepareStatement(queryString);
        preparedStatement.executeUpdate();
        preparedStatement.close();
    }

    @SneakyThrows
    public void prepareCollectionTeams() {
        Statement statement = databaseService.getConnection().createStatement();
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("create table if not exists `mineTeamsTeams`(");
        stringBuilder.append("`tag` varchar(32) not null,");
        stringBuilder.append("`owner` varchar(64) not null,");
        stringBuilder.append("`created` long not null,");
        stringBuilder.append("`importance` long not null,");
        stringBuilder.append("`members` json not null,");
        stringBuilder.append("`pvp` boolean not null,");
        stringBuilder.append("`glowing` boolean not null,");
        stringBuilder.append("`kills` int not null,");
        stringBuilder.append("`deaths` int not null,");
        stringBuilder.append("`bank` int not null,");
        stringBuilder.append("`vaultSize` int not null,");
        stringBuilder.append("`slots` int not null,");
        stringBuilder.append("`vault` text not null,");
        stringBuilder.append("primary key (nick));");

        statement.executeUpdate(stringBuilder.toString());
    }

    @SneakyThrows
    public void prepareCollectionUsers() {
        Statement statement = databaseService.getConnection().createStatement();
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("create table if not exists `mineTeamsUsers`(");
        stringBuilder.append("`uuid` varchar(32) not null,");
        stringBuilder.append("`kills` json not null,");
        stringBuilder.append("`deaths` json not null,");
        stringBuilder.append("primary key (nick));");

        statement.executeUpdate(stringBuilder.toString());
    }
}
