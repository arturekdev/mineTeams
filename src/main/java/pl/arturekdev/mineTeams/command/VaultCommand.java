package pl.arturekdev.mineTeams.command;

import com.google.gson.*;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.*;
import pl.arturekdev.mineTeams.*;
import pl.arturekdev.mineTeams.command.util.*;
import pl.arturekdev.mineTeams.messages.*;
import pl.arturekdev.mineTeams.objects.team.*;
import pl.arturekdev.mineUtiles.utils.*;

public class VaultCommand extends SubCommand {

    public VaultCommand() {
        super("vault");
    }

    @Override
    public void handleCommand(Player player, String[] arguments) {
        JsonObject config = Teams.getInstance().getConfiguration().getElement("configuration").getAsJsonObject();

        Team team = TeamUtil.getTeam(player);

        if (team == null) {
            MessageUtil.sendMessage(player, Messages.get("youDontHaveTeam", " &8>> &cNie posiadasz zespołu!"));
            return;
        }

        if (arguments.length != 1) {
            player.openInventory(team.getVault());
            if (team.getVaultSize() < 6) {
                MessageUtil.sendMessage(player, Messages.get("canUpgradeVaultInformation", " &8>> &aMożesz powiększyć swój skarbiec komendą &e/team vault upgrade"));
            }
            return;
        }

        if (!arguments[0].equalsIgnoreCase("upgrade")) {
            MessageUtil.sendMessage(player, Messages.get("usageVaultUpgrade", " &8>> &cPoprawne użycie: &e/team vault upgrade"));
        }

        if (team.getVaultSize() == 6) {
            MessageUtil.sendMessage(player, Messages.get("vaultMaxSize", " &8>> &cTwój skarbiec jest już maksymalnie powiększony!"));
            return;
        }

        int coast = config.get("vaultUpgradeCoast").getAsInt();
        if (team.getStats().getBank() < coast) {
            MessageUtil.sendMessage(player, Messages.get("bankDosntHasMoney", " &8>> &cBank twojego zespołu nie ma wystarczająco pieniędzy! Wymagana kwota to &e%coast% Iskier").replace("%coast%", String.valueOf(coast)));
            return;
        }

        team.setVaultSize(team.getVaultSize() + 1);
        team.getStats().setBank(team.getStats().getBank() - coast);

        Inventory inventory = Bukkit.createInventory(null, 9 * team.getVaultSize(), Messages.get("vaultTitleGUI", "&6Skarbiec twojego zespołu"));
        inventory.setContents(team.getVault().getContents());
        team.setVault(inventory);
        team.setNeedUpdate(true);

        MessageUtil.sendMessage(player, Messages.get("successUpgradeVault", " &8>> &aPomyślnie ulepszyłeś skarbiec swojego zespołu!"));

    }
}
