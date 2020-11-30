package pl.arturekdev.mineTeams.objects.team;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Builder
public class Team {

    private String tag;
    private UUID owner;
    private long created;
    private long importance;
    private Set<UUID> members;
    private boolean pvp;
    private boolean glowing;
    private Inventory vault;
    private int kills;
    private int deaths;
    private int bank;
    private int vaultSize;
    private int slots;
    private Set<Player> invites;

    public boolean isOnTeam(Player player) {
        return members.contains(player.getUniqueId()) || owner == player.getUniqueId();
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

}
