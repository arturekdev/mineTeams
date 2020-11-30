package pl.arturekdev.mineTeams.command;

import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import pl.arturekdev.mineTeams.Teams;
import pl.arturekdev.mineTeams.command.util.SubCommand;
import pl.arturekdev.mineTeams.messages.Messages;
import pl.arturekdev.mineTeams.objects.team.Team;
import pl.arturekdev.mineTeams.objects.team.TeamUtil;
import pl.arturekdev.mineUtiles.utils.MessageUtil;

public class VaultCommand extends SubCommand {

    public VaultCommand(Player player, String[] args) {
        super(player, args);
    }

    @Override
    public void run() {

        JsonObject config = Teams.getInstance().getConfiguration().getElement("configuration").getAsJsonObject();

        Team team = TeamUtil.getTeam(player);

        if (team == null) {
            MessageUtil.sendMessage(player, Messages.get("youDontHaveTeam", " &8>> &cNie posiadasz zespołu!"));
            return;
        }

        if (args.length != 2) {
            player.openInventory(team.getVault());
            if (team.getVaultSize() < 6) {
                MessageUtil.sendMessage(player, Messages.get("canUpgradeVaultInformation", " &8>> &aMożesz powiększyć swój skarbiec komendą &e/team vault upgrade"));
            }
            return;
        }

        if (!args[1].equalsIgnoreCase("upgrade")) {
            MessageUtil.sendMessage(player, Messages.get("usageVaultUpgrade", " &8>> &cPoprawne użycie: &e/team vault upgrade"));
        }

        if (team.getVaultSize() == 6) {
            MessageUtil.sendMessage(player, Messages.get("vaultMaxSize", " &8>> &cTwój skarbiec jest już maksymalnie powiększony!"));
            return;
        }

        if (team.getBank() < config.get("vaultUpgradeCoast").getAsInt()) {
            MessageUtil.sendMessage(player, Messages.get("bankDosntHasMoney", " &8>> &cBank twojego zespołu nie ma wystarczająco pieniędzy!"));
            return;
        }


        team.setVaultSize(team.getVaultSize() + 1);

        Inventory inventory = Bukkit.createInventory(null, 9 * team.getVaultSize(), Messages.get("vaultTitleGUI", "&6Skarbiec twojego zespołu"));
        inventory.setContents(team.getVault().getContents());
        team.setVault(inventory);

        MessageUtil.sendMessage(player, Messages.get("successUpgradeVault", " &8>> &aPomyślnie ulepszyłeś skarbiec swojego zespołu!"));

    }
}