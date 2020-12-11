package pl.arturekdev.mineTeams.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import pl.arturekdev.mineTeams.objects.team.Team;
import pl.arturekdev.mineTeams.objects.team.TeamUtil;

public class InventoryCloseListener implements Listener {

    @EventHandler
    public void event(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        Team team = TeamUtil.getTeam(player);
        Inventory inventory = event.getInventory();

        if (team == null) {
            return;
        }

        if (inventory != team.getVault()) {
            return;
        }

        team.setNeedUpdate(true);
    }
}
