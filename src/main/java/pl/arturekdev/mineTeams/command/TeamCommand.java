package pl.arturekdev.mineTeams.command;

import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.util.StringUtil;
import pl.arturekdev.mineEconomy.EconomyService;
import pl.arturekdev.mineTeams.messages.Messages;
import pl.arturekdev.mineTeams.mineTeams;
import pl.arturekdev.mineTeams.objects.Team;
import pl.arturekdev.mineTeams.objects.utils.TeamUtil;
import pl.arturekdev.mineUtiles.utils.MessageUtil;
import pl.arturekdev.mineUtiles.utils.TimeUtil;

import java.util.*;

public class TeamCommand implements CommandExecutor, TabCompleter {


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        JsonObject config = mineTeams.getInstance().getConfiguration().getElement("configuration").getAsJsonObject();

        if (args.length == 0) {
            for (String usageMain : Messages.getList("usageMain", " &8>> &6/team create <TAG> &7- Tworzenie nowego zespołu ; &8>> &6/team delete &7- Usuwanie zespołu ; &8>> &6/team invite <nick> &7- Zapraszanie gracza do zespołu ; &8>> &6/team join <TAG> &7- Dołączanie do zespołu ; &8>> &6/team kick <nick> &7- Wyrzucanie gracza z zespołu ; &8>> &6/team info [TAG] &7- Informacje o zespole")) {
                MessageUtil.sendMessage(sender, usageMain);
            }
            return false;
        }

        Player player = (Player) sender;

        if (args[0].equalsIgnoreCase("create")) {

            if (args.length != 2) {
                MessageUtil.sendMessage(sender, Messages.get("usageCreate", " &8>> &cPoprawne użycie: &e/team create <TAG>"));
                return false;
            }

            if (TeamUtil.hasTeam(player)) {
                MessageUtil.sendMessage(sender, Messages.get("youHasTeam", " &8>> &cPosiadasz już zespół!"));
                return false;
            }

            String tag = args[1];

            if (tag.length() > config.get("maxCharsTag").getAsInt()) {
                MessageUtil.sendMessage(sender, Messages.get("maxChars", " &8>> &cTag może mieć maksymalnie 5 znaków!"));
                return false;
            }

            Team team = TeamUtil.getTeam(tag);

            if (team != null) {
                MessageUtil.sendMessage(sender, Messages.get("tagBusy", " &8>> &cPodany TAG jest już zajęty!"));
                return false;
            }

            team = Team.builder()
                    .tag(tag)
                    .owner(player.getUniqueId())
                    .pvp(false)
                    .glowing(false)
                    .importance(System.currentTimeMillis() + TimeUtil.timeFromString(config.get("importance").getAsJsonObject().get("start").getAsString()))
                    .created(System.currentTimeMillis())
                    .bank(0)
                    .kills(0)
                    .deaths(0)
                    .vaultSize(config.get("vaultStartSize").getAsInt())
                    .slots(config.get("slotsStart").getAsInt())
                    .vault(Bukkit.createInventory(null, 9 * config.get("vaultStartSize").getAsInt(), Messages.get("vaultTitleGUI", "&6Skarbiec twojego zespołu")))
                    .members(new HashSet<>())
                    .build();

            TeamUtil.getTeams().add(team);

            MessageUtil.sendMessage(sender, Messages.get("successCreate", " &8>> &aPomyślnie stworzyłeś zespół o tagu &e%tag%").replace("%tag%", tag));
            Bukkit.broadcastMessage(MessageUtil.fixColor(Messages.get("successCreateBroadcast", " &6&lZespoły &8>> &e%player% &azałożył zespoł o tagu &e%tag%&a! Gratulacje!").replace("%player%", player.getName()).replace("%tag%", team.getTag())));

            return false;

        } else if (args[0].equalsIgnoreCase("delete")) {

            Team team = TeamUtil.getTeam(player);

            if (team == null) {
                MessageUtil.sendMessage(sender, Messages.get("youDontHaveTeam", " &8>> &cNie posiadasz zespołu!"));
                return false;
            }

            if (!team.getOwner().equals(player.getUniqueId())) {
                MessageUtil.sendMessage(sender, Messages.get("youNeedBeOwner", " &8>> &cMusisz być właścicielem!"));
                return false;
            }

            TeamUtil.getTeams().remove(team);
            MessageUtil.sendMessage(sender, Messages.get("successDelete", " &8>> &aPomyślnie usunąłeś swój zespół!"));
            Bukkit.broadcastMessage(MessageUtil.fixColor(Messages.get("successDeleteBroadcast", " &6&lZespoły &8>> &e%player% &crozwiązał zespoł o tagu &e%tag%&c!").replace("%player%", player.getName()).replace("%tag%", team.getTag())));

            return false;

        } else if (args[0].equalsIgnoreCase("invite")) {

            if (args.length != 2) {
                MessageUtil.sendMessage(sender, Messages.get("usageCreate", " &8>> &cPoprawne użycie: &e/team invite <nick>"));
                return false;
            }

            Player victim = Bukkit.getPlayer(args[1]);

            if (victim == null) {
                MessageUtil.sendMessage(sender, Messages.get("offlinePlayer", " &8>> &cPodany gracz jest offline!"));
                return false;
            }

            if (victim == player) {
                MessageUtil.sendMessage(sender, Messages.get("samePlayer", " &8>> &cNie możesz zaprosić siebie samego!"));
                return false;
            }

            if (TeamUtil.getTeam(victim) != null) {
                MessageUtil.sendMessage(sender, Messages.get("victimHasTeam", " &8>> &cGracz którego chcesz zaprosić ma już zespół!"));
                return false;
            }

            Team team = TeamUtil.getTeam(player);

            if (team == null) {
                MessageUtil.sendMessage(sender, Messages.get("youDontHaveTeam", " &8>> &cNie posiadasz zespołu!"));
                return false;
            }

            if (!team.getOwner().equals(player.getUniqueId())) {
                MessageUtil.sendMessage(sender, Messages.get("youNeedBeOwner", " &8>> &cMusisz być właścicielem!"));
                return false;
            }
            if (team.getMembers().size() >= team.getSlots()) {
                MessageUtil.sendMessage(sender, Messages.get("maxTeamSize", " &8>> &cOsiągnąłeś maksymalną liczbę slotów w zespole!"));

                if (team.getSlots() != config.get("slotsStart").getAsInt()) {
                    MessageUtil.sendMessage(sender, Messages.get("slotsUpgradeInformation", " &8>> &aMożesz powiększyć ilośc slotów w zespole komendą &e/team slots upgrade"));
                }
                return false;
            }

            if (TeamUtil.getTeamsInvites().containsKey(team)) {
                MessageUtil.sendMessage(sender, Messages.get("successCancelInvite", " &8>> &aPomyślnie anulowano zaproszenie dla &e%player%").replace("%player%", victim.getName()));
                TeamUtil.getTeamsInvites().remove(team, victim);
                return false;
            }

            TeamUtil.getTeamsInvites().put(team, victim);

            MessageUtil.sendMessage(sender, Messages.get("successInvite", " &8>> &aPomyślnie zaprosiłeś &e%player% &ado swojeto zespołu!").replace("%player%", victim.getName()));
            MessageUtil.sendMessage(sender, Messages.get("usageCancelInvite", " &8>> &aAby wycofać zaproszenie użyj komendy &e/team invite %player%").replace("%player%", victim.getName()));
            MessageUtil.sendMessage(victim, Messages.get("successInviteVictim", " &8>> &aOtrzymałeś zaproszenie do zespołu gracza &e%player% &aużyj komendy &e/team join %tag%").replace("%player%", player.getName()).replace("%tag%", team.getTag()));
            return false;

        } else if (args[0].equalsIgnoreCase("join")) {

            if (args.length != 2) {
                MessageUtil.sendMessage(sender, Messages.get("usageJoin", " &8>> &cPoprawne użycie: &e/team join <tag>"));
                return false;
            }

            if (!TeamUtil.getTeamsInvites().containsValue(player)) {
                MessageUtil.sendMessage(sender, Messages.get("youDontHaveInvite", " &8>> &cNie posiadasz zaproszenie do zespołu!"));
                return false;
            }

            Team team = TeamUtil.getTeam(args[1]);

            if (team == null) {
                MessageUtil.sendMessage(sender, Messages.get("teamIsNull", " &8>> &cNie ma takiego zespołu!"));
                return false;
            }

            if (TeamUtil.getTeamsInvites().get(team) != player) {
                MessageUtil.sendMessage(sender, Messages.get("youDontHaveInvite", " &8>> &cNie posiadasz zaproszenie do tego zespołu!"));
                return false;
            }

            team.getMembers().add(player.getUniqueId());
            TeamUtil.getTeamsInvites().remove(team, player);

            MessageUtil.sendMessage(sender, Messages.get("successJoin", " &8>> &aPomyślnie dołączyłeś do zespołu &e%tag%").replace("%tag%", team.getTag()));
            Bukkit.broadcastMessage(MessageUtil.fixColor(Messages.get("successJoinBroadcast", " &6&lZespoły &8>> &e%player% &adołączył do zespołu &e%tag%").replace("%player%", player.getName()).replace("%tag%", team.getTag())));
            return false;

        } else if (args[0].equalsIgnoreCase("kick")) {

            if (args.length != 2) {
                MessageUtil.sendMessage(sender, Messages.get("usageKick", " &8>> &cPoprawne użycie: &e/team kick <nick>"));
                return false;
            }

            UUID victimUUID = Bukkit.getPlayerUniqueId(args[1]);

            if (victimUUID == null) {
                MessageUtil.sendMessage(sender, Messages.get("uuidNull", " &8>> &cBrak takiego gracza w bazie danych!"));
                return false;
            }

            Team team = TeamUtil.getTeam(player);

            if (team == null) {
                MessageUtil.sendMessage(sender, Messages.get("youDontHaveTeam", " &8>> &cNie posiadasz zespołu!"));
                return false;
            }

            if (!team.getOwner().equals(player.getUniqueId())) {
                MessageUtil.sendMessage(sender, Messages.get("youNeedBeOwner", " &8>> &cMusisz być właścicielem!"));
                return false;
            }

            if (!team.getMembers().contains(victimUUID)) {
                MessageUtil.sendMessage(sender, Messages.get("victimIsNotMember", " &8>> &cPodany gracz nie jest członkiem twojego zespołu!"));
                return false;
            }

            team.getMembers().remove(victimUUID);
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(victimUUID);

            MessageUtil.sendMessage(sender, Messages.get("successKick", " &8>> &aPomyślnie wyrzuciłeś &e%player% &aze swojego zespołu!").replace("%player%", offlinePlayer.getName()));
            Bukkit.broadcastMessage(MessageUtil.fixColor(Messages.get("successJoinBroadcast", " &6&lZespoły &8>> &e%player% &czostał wyrzucony z zespołu &e%tag%").replace("%player%", offlinePlayer.getName()).replace("%tag%", team.getTag())));
            return false;

        } else if (args[0].equalsIgnoreCase("leave")) {

            Team team = TeamUtil.getTeam(player);

            if (team == null) {
                MessageUtil.sendMessage(sender, Messages.get("youDontHaveTeam", " &8>> &cNie posiadasz zespołu!"));
                return false;
            }

            if (team.getOwner().equals(player.getUniqueId())) {
                MessageUtil.sendMessage(sender, Messages.get("youAreOwner", " &8>> &cJesteś właścicielem!"));
                return false;
            }

            team.getMembers().remove(player.getUniqueId());

            MessageUtil.sendMessage(sender, Messages.get("successLeave", " &8>> &aPomyślnie opuściłeś zespoł &e%tag%").replace("%tag%", team.getTag()));
            Bukkit.broadcastMessage(MessageUtil.fixColor(Messages.get("successLeaveBroadcast", " &6&lZespoły &8>> &e%player% &copóścił zespół &e%tag%").replace("%player%", player.getName()).replace("%tag%", team.getTag())));
            return false;

        } else if (args[0].equalsIgnoreCase("pvp")) {

            Team team = TeamUtil.getTeam(player);

            if (team == null) {
                MessageUtil.sendMessage(sender, Messages.get("youDontHaveTeam", " &8>> &cNie posiadasz zespołu!"));
                return false;
            }

            if (!team.getOwner().equals(player.getUniqueId())) {
                MessageUtil.sendMessage(sender, Messages.get("youNeedBeOwner", " &8>> &cMusisz być właścicielem!"));
                return false;
            }

            if (team.isPvp()) {
                team.setPvp(false);
                MessageUtil.sendMessage(sender, Messages.get("successDisablePvp", " &8>> &aAtakowanie się miedzy członkami zespołu zostało &4wyłączone"));
            } else {
                team.setPvp(true);
                MessageUtil.sendMessage(sender, Messages.get("successEnablePvp", " &8>> &aAtakowanie się miedzy członkami zespołu zostało &2włączone"));
            }

            return false;

        } else if (args[0].equalsIgnoreCase("bank")) {

            Team team = TeamUtil.getTeam(player);

            if (team == null) {
                MessageUtil.sendMessage(sender, Messages.get("youDontHaveTeam", " &8>> &cNie posiadasz zespołu!"));
                return false;
            }

            if (args.length != 3) {
                MessageUtil.sendMessage(sender, Messages.get("teamBalance", " &8>> &aBank zespołu aktualnie posiada: &e%balance% Iskier").replace("%balance%", String.valueOf(team.getBank())));
                MessageUtil.sendMessage(sender, Messages.get("usageBank", " &8>> &cPoprawne użycie: &e/team bank <withdraw/deposit> <ilość>"));
                return false;
            }

            int value;

            try {
                value = Integer.parseInt(args[2]);
            } catch (Exception e) {
                MessageUtil.sendMessage(sender, Messages.get("argumentIsNotNumber", " &8>> &cIlość musi być liczbą!"));
                return false;
            }

            EconomyService economyService = new EconomyService();

            if (args[1].equalsIgnoreCase("withdraw")) {

                if (!team.getOwner().equals(player.getUniqueId())) {
                    MessageUtil.sendMessage(sender, Messages.get("youNeedBeOwner", " &8>> &cMusisz być właścicielem!"));
                    return false;
                }

                if (value < 0) {
                    return false;
                }

                if (!(team.getBank() >= value)) {
                    MessageUtil.sendMessage(sender, Messages.get("tooLittleBankMoney", " &8>> &cTwój zespół nie posiada takiej kwoty w banku!"));
                    return false;
                }

                team.setBank(team.getBank() - value);
                economyService.giveMoney(player, value);

                MessageUtil.sendMessage(sender, Messages.get("successWithdrawBankMoney", " &8>> &aPomyślnie wypłaciłeś &e%value% Iskier &az banku swojego zespołu!").replace("%value%", String.valueOf(value)));
                return false;

            } else if (args[1].equalsIgnoreCase("deposit")) {

                if (!economyService.has(player, value)) {
                    MessageUtil.sendMessage(sender, Messages.get("youDontHaveThatAmount", " &8>> &cNie posiadasz takiej ilości iskier"));
                    return false;
                }

                team.setBank(team.getBank() + value);
                economyService.takeMoney(player, value);

                MessageUtil.sendMessage(sender, Messages.get("successDepositBankMoney", " &8>> &aPomyślnie wypłaciłeś &e%value% Iskier &ado banku swojego zespołu!").replace("%value%", String.valueOf(value)));
                return false;
            }

            return false;

        } else if (args[0].equalsIgnoreCase("vault")) {

            Team team = TeamUtil.getTeam(player);

            if (team == null) {
                MessageUtil.sendMessage(sender, Messages.get("youDontHaveTeam", " &8>> &cNie posiadasz zespołu!"));
                return false;
            }

            if (args.length != 2) {
                player.openInventory(team.getVault());
                if (team.getVaultSize() < 6) {
                    MessageUtil.sendMessage(sender, Messages.get("canUpgradeVaultInformation", " &8>> &aMożesz powiększyć swój skarbiec komendą &e/team vault upgrade"));
                }
                return false;
            }

            if (!args[1].equalsIgnoreCase("upgrade")) {
                MessageUtil.sendMessage(sender, Messages.get("usageVaultUpgrade", " &8>> &cPoprawne użycie: &e/team vault upgrade"));
            }

            if (team.getVaultSize() == 6) {
                MessageUtil.sendMessage(sender, Messages.get("vaultMaxSize", " &8>> &cTwój skarbiec jest już maksymalnie powiększony!"));
                return false;
            }

            if (team.getBank() < config.get("vaultUpgradeCoast").getAsInt()) {
                MessageUtil.sendMessage(sender, Messages.get("bankDosntHasMoney", " &8>> &cBank twojego zespołu nie ma wystarczająco pieniędzy!"));
                return false;
            }


            team.setVaultSize(team.getVaultSize() + 1);

            Inventory inventory = Bukkit.createInventory(null, 9 * team.getVaultSize(), Messages.get("vaultTitleGUI", "&6Skarbiec twojego zespołu"));
            inventory.setContents(team.getVault().getContents());
            team.setVault(inventory);

            MessageUtil.sendMessage(sender, Messages.get("successUpgradeVault", " &8>> &aPomyślnie ulepszyłeś skarbiec swojego zespołu!"));

            return false;

        } else if (args[0].equalsIgnoreCase("slots")) {

            Team team = TeamUtil.getTeam(player);

            if (team == null) {
                MessageUtil.sendMessage(sender, Messages.get("youDontHaveTeam", " &8>> &cNie posiadasz zespołu!"));
                return false;
            }

            if (args.length != 2) {
                MessageUtil.sendMessage(sender, Messages.get("teamSlots", " &8>> &aTwój zespół aktualnie posiada &e%slots% &aslotów.").replace("%slots%", String.valueOf(team.getSlots())));
                if (team.getSlots() != config.get("slotsLimit").getAsInt()) {
                    MessageUtil.sendMessage(sender, Messages.get("slotsUpgradeInformation", " &8>> &aMożesz powiększyć ilośc slotów w zespole komendą &e/team slots upgrade"));
                }
                return false;
            }

            if (!args[1].equalsIgnoreCase("upgrade")) {
                MessageUtil.sendMessage(sender, Messages.get("usageSlotsUpgrade", " &8>> &cPoprawne użycie: &e/team slots upgrade"));
            }

            if (team.getSlots() == config.get("slotsLimit").getAsInt()) {
                MessageUtil.sendMessage(sender, Messages.get("slotsMaxSize", " &8>> &cSloty w twoim zespole są juz maksymalnie powiększone!"));
                return false;
            }

            if (team.getBank() < config.get("slotsUpgradeCoast").getAsInt()) {
                MessageUtil.sendMessage(sender, Messages.get("bankDosntHasMoney", " &8>> &cBank twojego zespołu nie ma wystarczająco pieniędzy!"));
                return false;
            }

            team.setSlots(team.getSlots() + 1);

            MessageUtil.sendMessage(sender, Messages.get("successUpgradeSlots", " &8>> &aPomyślnie powiększyłeś ilość slotów!"));

            return false;

        } else if (args[0].equalsIgnoreCase("info")) {

            if (args.length == 1) {

                Team team = TeamUtil.getTeam(player);

                if (team == null) {
                    MessageUtil.sendMessage(sender, Messages.get("youDontHaveTeam", " &8>> &cNie posiadasz zespołu!"));
                    return false;
                }

                OfflinePlayer owner = Bukkit.getOfflinePlayer(team.getOwner());

                StringBuilder members = new StringBuilder();
                for (UUID member : team.getMembers()) {
                    OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(member);
                    members.append(offlinePlayer.isOnline() ? "&a" + offlinePlayer.getName() : "&c" + offlinePlayer.getName()).append("&7, ");
                }

                List<String> strings = Messages.getList("teamInfo",
                        " ;" +
                                " &8>> &6Tag: &7%tag% ;" +
                                " &8>> &6Właściciel: %owner% ;" +
                                " &8>> &6Członkowie &8(&e%membersSize%&8/&e%slots%&8): &7%members% ;" +
                                " &8>> &6Zabójstw: &7%kills% ;" +
                                " &8>> &6Zgonów: &7%deaths% ;" +
                                " &8>> &6Ważny do: &7%importance% ;" +
                                " &8>> &6Stworzono: &7%created% ;" +
                                " ;" +
                                " &8>> &6Aby sprawdzić inny zespół użyj &e/team info <TAG>");

                for (String string : strings) {
                    string = string.replace("%tag%", team.getTag());
                    string = string.replace("%owner%", owner.isOnline() ? "&a" + owner.getName() : "&c" + owner.getName());
                    string = string.replace("%members%", members.toString());
                    string = string.replace("%created%", TimeUtil.formatDate(team.getCreated()));
                    string = string.replace("%importance%", TimeUtil.formatDate(team.getImportance()));
                    string = string.replace("%kills%", String.valueOf(team.getKills()));
                    string = string.replace("%deaths%", String.valueOf(team.getDeaths()));
                    string = string.replace("%slots%", String.valueOf(team.getSlots()));
                    string = string.replace("%membersSize%", String.valueOf(team.getMembers().size()));
                    MessageUtil.sendMessage(player, string);
                }

                return false;

            } else if (args.length == 2) {

                Team team = TeamUtil.getTeam(args[1]);

                if (team == null) {
                    MessageUtil.sendMessage(sender, Messages.get("teamIsNull", " &8>> &cNie ma takiego zespołu!"));
                    return false;
                }

                OfflinePlayer owner = Bukkit.getOfflinePlayer(team.getOwner());

                StringBuilder members = new StringBuilder();
                for (UUID member : team.getMembers()) {
                    OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(member);
                    members.append(offlinePlayer.isOnline() ? "&a" + offlinePlayer.getName() : "&c" + offlinePlayer.getName()).append("&7, ");
                }

                List<String> strings = Messages.getList("teamInfo",
                        " ;" +
                                " &8>> &6Tag: &7%tag% ;" +
                                " &8>> &6Właściciel: %owner% ;" +
                                " &8>> &6Członkowie &8(&e%membersSize%&8/&e%slots%&8): &7%members% ;" +
                                " &8>> &6Zabójstw: &7%kills% ;" +
                                " &8>> &6Zgonów: &7%deaths% ;" +
                                " &8>> &6Ważny do: &7%importance% ;" +
                                " &8>> &6Stworzono: &7%created% ;" +
                                " ;" +
                                " &8>> &6Aby sprawdzić inny zespół użyj &e/team info <TAG>");

                for (String string : strings) {
                    string = string.replace("%tag%", team.getTag());
                    string = string.replace("%owner%", owner.isOnline() ? "&a" + owner.getName() : "&c" + owner.getName());
                    string = string.replace("%members%", members.toString());
                    string = string.replace("%created%", TimeUtil.formatDate(team.getCreated()));
                    string = string.replace("%importance%", TimeUtil.formatDate(team.getImportance()));
                    string = string.replace("%kills%", String.valueOf(team.getKills()));
                    string = string.replace("%deaths%", String.valueOf(team.getDeaths()));
                    string = string.replace("%slots%", String.valueOf(team.getSlots()));
                    string = string.replace("%membersSize%", String.valueOf(team.getMembers().size()));
                    MessageUtil.sendMessage(player, string);
                }
                return false;
            }

            return false;
        }

        for (String usageMain : Messages.getList("usageMain", " &8>> &6/team create <TAG> &7- Tworzenie nowego zespołu ; &8>> &6/team delete &7- Usuwanie zespołu ; &8>> &6/team invite <nick> &7- Zapraszanie gracza do zespołu ; &8>> &6/team join <TAG> &7- Dołączanie do zespołu ; &8>> &6/team kick <nick> &7- Wyrzucanie gracza z zespołu ; &8>> &6/team info [TAG] &7- Informacje o zespole")) {
            MessageUtil.sendMessage(sender, usageMain);
        }

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

        if (args.length <= 1) {
            List<String> old = new ArrayList<>();

            old.add("create");
            old.add("delete");
            old.add("invite");
            old.add("join");
            old.add("leave");
            old.add("kick");
            old.add("pvp");
            old.add("info");
            old.add("bank");
            old.add("vault");
            old.add("slots");

            List<String> tab = new ArrayList<>();
            StringUtil.copyPartialMatches(args[0], old, tab);
            Collections.sort(tab);
            return tab;
        }

        if (args[0].equalsIgnoreCase("info") || args[0].equalsIgnoreCase("join")) {
            List<String> old = new ArrayList<>();

            for (Team team : TeamUtil.getTeams()) {
                old.add(team.getTag());
            }

            List<String> tab = new ArrayList<>();
            StringUtil.copyPartialMatches(args[1], old, tab);
            Collections.sort(tab);
            return tab;
        }

        if (args[0].equalsIgnoreCase("bank")) {
            List<String> old = new ArrayList<>();

            old.add("withdraw");
            old.add("deposit");

            List<String> tab = new ArrayList<>();
            StringUtil.copyPartialMatches(args[1], old, tab);
            Collections.sort(tab);
            return tab;
        }

        if (args[0].equalsIgnoreCase("vault")) {
            List<String> old = new ArrayList<>();

            old.add("upgrade");

            List<String> tab = new ArrayList<>();
            StringUtil.copyPartialMatches(args[1], old, tab);
            Collections.sort(tab);
            return tab;
        }

        if (args[0].equalsIgnoreCase("slots")) {
            List<String> old = new ArrayList<>();

            old.add("upgrade");

            List<String> tab = new ArrayList<>();
            StringUtil.copyPartialMatches(args[1], old, tab);
            Collections.sort(tab);
            return tab;
        }

        if (args[0].equalsIgnoreCase("kick")) {
            List<String> old = new ArrayList<>();

            Player player = (Player) sender;

            Team team = TeamUtil.getTeam(player);

            if (team == null) {
                return null;
            }

            if (!team.getOwner().equals(player.getUniqueId())) {
                return null;
            }

            for (UUID member : team.getMembers()) {
                old.add(Bukkit.getOfflinePlayer(member).getName());
            }

            List<String> tab = new ArrayList<>();
            StringUtil.copyPartialMatches(args[1], old, tab);
            Collections.sort(tab);
            return tab;
        }


        return null;
    }
}
