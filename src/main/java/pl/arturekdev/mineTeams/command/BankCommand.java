package pl.arturekdev.mineTeams.command;

import org.bukkit.entity.Player;
import pl.arturekdev.mineEconomy.EconomyService;
import pl.arturekdev.mineTeams.command.util.SubCommand;
import pl.arturekdev.mineTeams.messages.Messages;
import pl.arturekdev.mineTeams.objects.team.Team;
import pl.arturekdev.mineTeams.objects.team.TeamUtil;
import pl.arturekdev.mineUtiles.utils.MessageUtil;

public class BankCommand extends SubCommand {

    public BankCommand() {
        super("bank");
    }

    @Override
    public void handleCommand(Player player, String[] arguments) {
        Team team = TeamUtil.getTeam(player);

        if (team == null) {
            MessageUtil.sendMessage(player, Messages.get("youDontHaveTeam", " &8>> &cNie posiadasz zespołu!"));
            return;
        }

        if (arguments.length != 3) {
            MessageUtil.sendMessage(player, Messages.get("teamBalance", " &8>> &aBank zespołu aktualnie posiada: &e%balance% Iskier").replace("%balance%", String.valueOf(team.getBank())));
            MessageUtil.sendMessage(player, Messages.get("usageBank", " &8>> &cPoprawne użycie: &e/team bank <withdraw/deposit> <ilość>"));
            return;
        }

        int value;

        try {
            value = Integer.parseInt(arguments[2]);
        } catch (Exception e) {
            MessageUtil.sendMessage(player, Messages.get("argumentIsNotNumber", " &8>> &cIlość musi być liczbą!"));
            return;
        }

        EconomyService economyService = new EconomyService();

        if (arguments[1].equalsIgnoreCase("withdraw")) {
            if (!team.getOwner().equals(player.getUniqueId())) {
                MessageUtil.sendMessage(player, Messages.get("youNeedBeOwner", " &8>> &cMusisz być właścicielem!"));
                return;
            }

            if (value < 0) {
                return;
            }

            if (team.getBank() < value) {
                MessageUtil.sendMessage(player, Messages.get("tooLittleBankMoney", " &8>> &cTwój zespół nie posiada takiej kwoty w banku!"));
                return;
            }

            team.setBank(team.getBank() - value);
            economyService.giveMoney(player, value);

            MessageUtil.sendMessage(player, Messages.get("successWithdrawBankMoney", " &8>> &aPomyślnie wypłaciłeś &e%value% Iskier &az banku swojego zespołu!").replace("%value%", String.valueOf(value)));
        } else if (arguments[1].equalsIgnoreCase("deposit")) {
            if (!economyService.has(player, value)) {
                MessageUtil.sendMessage(player, Messages.get("youDontHaveThatAmount", " &8>> &cNie posiadasz takiej ilości iskier"));
                return;
            }

            team.setBank(team.getBank() + value);
            economyService.takeMoney(player, value);

            MessageUtil.sendMessage(player, Messages.get("successDepositBankMoney", " &8>> &aPomyślnie wypłaciłeś &e%value% Iskier &ado banku swojego zespołu!").replace("%value%", String.valueOf(value)));
        }
    }
}
