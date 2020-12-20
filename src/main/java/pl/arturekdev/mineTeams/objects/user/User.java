package pl.arturekdev.mineTeams.objects.user;

import lombok.*;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import pl.arturekdev.mineTeams.database.DatabaseConnector;

import java.sql.ResultSet;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User {

    private String username;
    private UUID uuid;
    private long lastDeath;
    private int kills;
    private int deaths;
    private boolean needUpdate;

    public User(UUID uuid) {
        this.uuid = uuid;
        this.username = Bukkit.getOfflinePlayer(uuid).getName();
        this.needUpdate = true;
    }

    @SneakyThrows
    public User(ResultSet resultSet) {
        this.uuid = UUID.fromString(resultSet.getString("uuid"));
        this.username = Bukkit.getOfflinePlayer(uuid).getName();
        this.kills = resultSet.getInt("kills");
        this.deaths = resultSet.getInt("deaths");
        this.needUpdate = false;
    }

    public Player getPlayer() {
        return Bukkit.getOfflinePlayer(uuid).getPlayer();
    }

    public OfflinePlayer getOfflinePlayer() {
        return Bukkit.getOfflinePlayer(uuid).getPlayer();
    }

    public void update(DatabaseConnector databaseConnector) {

        if (!this.needUpdate) {
            return;
        }

        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("INSERT INTO mineTeamsUsers (uuid, kills, deaths) VALUES (");
        stringBuilder.append("'").append(this.uuid).append("',");
        stringBuilder.append("'").append(this.kills).append("',");
        stringBuilder.append("'").append(this.deaths).append("'");
        stringBuilder.append(") ON DUPLICATE KEY UPDATE ");
        stringBuilder.append("kills='").append(this.kills).append("',");
        stringBuilder.append("deaths='").append(this.deaths).append("';");

        this.needUpdate = false;

        databaseConnector.executeUpdate(stringBuilder.toString());
    }

}
