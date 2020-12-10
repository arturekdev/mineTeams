package pl.arturekdev.mineTeams.objects.team;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import pl.arturekdev.mineTeams.configuration.Config;
import pl.arturekdev.mineTeams.database.DatabaseConnector;
import pl.arturekdev.mineTeams.messages.Messages;
import pl.arturekdev.mineTeams.util.ItemSerializer;
import pl.arturekdev.mineUtiles.utils.TimeUtil;

import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.util.*;

@Getter
@Setter
public class Team {

    private String tag;
    private UUID owner;
    private long created;
    private long importance;
    private Set<UUID> members;
    private boolean pvp;
    private boolean glowing;
    private TeamStats stats;
    private int vaultSize;
    private int slots;
    private Inventory vault;
    private Set<Player> invites;
    private boolean needUpdate;

    @SneakyThrows
    public Team(ResultSet resultSet) {
        this.tag = resultSet.getString("tag");
        this.owner = UUID.fromString(resultSet.getString("owner"));
        this.created = resultSet.getLong("created");
        this.importance = resultSet.getLong("importance");
        Type type = new TypeToken<Set<UUID>>() {
        }.getType();
        this.members = new Gson().fromJson(resultSet.getString("members"), type);
        this.pvp = resultSet.getBoolean("pvp");
        this.glowing = resultSet.getBoolean("glowing");
        this.stats = new TeamStats(resultSet.getInt("kills"), resultSet.getInt("deaths"), resultSet.getInt("bank"));
        this.vaultSize = resultSet.getInt("vaultSize");
        this.slots = resultSet.getInt("slots");
        this.vault = Bukkit.createInventory(null, 9 * this.vaultSize, Messages.get("vaultTitleGUI", "&6Skarbiec twojego zespołu"));
        this.vault.setContents(ItemSerializer.itemStackArrayFromBase64(resultSet.getString("vault")));
        this.needUpdate = true;
    }

    public Team(String tag, UUID owner, Config config) {
        this.tag = tag;
        this.owner = owner;
        this.created = System.currentTimeMillis();
        JsonObject configuration = config.getElement("configuration").getAsJsonObject();
        this.importance = System.currentTimeMillis() + TimeUtil.timeFromString(configuration.get("importance").getAsJsonObject().get("start").getAsString());
        this.members = new HashSet<>();
        this.pvp = false;
        this.glowing = false;
        this.stats = new TeamStats();
        this.vaultSize = configuration.get("vaultStartSize").getAsInt();
        this.slots = configuration.get("slotsStart").getAsInt();
        this.vault = Bukkit.createInventory(null, 9 * this.vaultSize, Messages.get("vaultTitleGUI", "&6Skarbiec twojego zespołu"));
        this.needUpdate = true;
    }

    public boolean isNotOnTeam(Player player) {
        return !members.contains(player.getUniqueId()) && owner != player.getUniqueId();
    }

    public List<Player> onlinePlayers() {
        List<Player> players = new ArrayList<>();
        for (UUID uuid : members) {
            Player player = Bukkit.getOfflinePlayer(uuid).getPlayer();
            if (player == null) continue;
            players.add(player);
        }
        if (Bukkit.getOfflinePlayer(owner).getPlayer() != null) {
            players.add(Bukkit.getOfflinePlayer(owner).getPlayer());
        }
        return players;
    }

    public void update(DatabaseConnector databaseConnector) {

        if (!needUpdate) {
            return;
        }

        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("INSERT INTO mineTeamsTeams (tag, uuid, created, importance, members, pvp, glowing, kills, deaths, bank, vaultSize, slots, vault) VALUES (");
        stringBuilder.append("'").append(this.tag).append("',");
        stringBuilder.append("'").append(this.owner.toString()).append("',");
        stringBuilder.append("'").append(this.created).append("',");
        stringBuilder.append("'").append(this.importance).append("',");
        stringBuilder.append("'").append(new Gson().toJson(this.members)).append("',");
        stringBuilder.append("'").append(this.pvp).append("',");
        stringBuilder.append("'").append(this.glowing).append("',");
        stringBuilder.append("'").append(this.stats.getKills()).append("',");
        stringBuilder.append("'").append(this.stats.getDeaths()).append("',");
        stringBuilder.append("'").append(this.stats.getBank()).append("',");
        stringBuilder.append("'").append(this.vaultSize).append("',");
        stringBuilder.append("'").append(this.slots).append("',");
        stringBuilder.append("'").append(ItemSerializer.itemStackArrayToBase64(vault.getContents())).append("'");
        stringBuilder.append(") ON DUPLICATE KEY UPDATE ");
        stringBuilder.append("importance='").append(this.importance).append("',");
        stringBuilder.append("members='").append(new Gson().toJson(this.members)).append("',");
        stringBuilder.append("pvp='").append(this.pvp).append("',");
        stringBuilder.append("glowing='").append(this.glowing).append("',");
        stringBuilder.append("kills='").append(this.stats.getKills()).append("',");
        stringBuilder.append("deaths='").append(this.stats.getDeaths()).append("',");
        stringBuilder.append("bank='").append(this.stats.getBank()).append("',");
        stringBuilder.append("vaultSize='").append(this.vaultSize).append("',");
        stringBuilder.append("slots='").append(this.slots).append("',");
        stringBuilder.append("vault='").append(ItemSerializer.itemStackArrayToBase64(this.vault.getContents())).append("';");

        this.needUpdate = false;

        databaseConnector.executeUpdate(stringBuilder.toString());
    }


}
