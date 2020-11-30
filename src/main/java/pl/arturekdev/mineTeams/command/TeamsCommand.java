package pl.arturekdev.mineTeams.command;

import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import pl.arturekdev.mineTeams.Teams;
import pl.arturekdev.mineTeams.messages.Messages;
import pl.arturekdev.mineTeams.objects.team.Team;
import pl.arturekdev.mineTeams.objects.team.TeamUtil;
import pl.arturekdev.mineUtiles.utils.MessageUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class TeamsCommand implements CommandExecutor, TabCompleter {


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        JsonObject config = Teams.getInstance().getConfiguration().getElement("configuration").getAsJsonObject();

        if (args.length == 0) {
            for (String usageMain : Messages.getList("usageMain", " &8>> &6/team create <TAG> &7- Tworzenie nowego zespołu ; &8>> &6/team delete &7- Usuwanie zespołu ; &8>> &6/team invite <nick> &7- Zapraszanie gracza do zespołu ; &8>> &6/team join <TAG> &7- Dołączanie do zespołu ; &8>> &6/team kick <nick> &7- Wyrzucanie gracza z zespołu ; &8>> &6/team info [TAG] &7- Informacje o zespole")) {
                MessageUtil.sendMessage(sender, usageMain);
            }
            return false;
        }

        Player player = (Player) sender;

        switch (args[0]) {
            case "create":
                new CreateCommand(player, args).run();
                return false;
            case "delete":
                new DeleteCommand(player, args).run();
                return false;
            case "invite":
                new InviteCommand(player, args).run();
                return false;
            case "join":
                new JoinCommand(player, args).run();
                return false;
            case "kick":
                new KickCommand(player, args).run();
                return false;
            case "leave":
                new LeaveCommand(player, args).run();
                return false;
            case "pvp":
                new PvpCommand(player, args).run();
                return false;
            case "bank":
                new BankCommand(player, args).run();
                return false;
            case "vault":
                new VaultCommand(player, args).run();
                return false;
            case "slots":
                new SlotsCommand(player, args).run();
                return false;
            case "info":
                new InfoCommand(player, args).run();
                return false;
            default:
                for (String usageMain : Messages.getList("usageMain", " &8>> &6/team create <TAG> &7- Tworzenie nowego zespołu ; &8>> &6/team delete &7- Usuwanie zespołu ; &8>> &6/team invite <nick> &7- Zapraszanie gracza do zespołu ; &8>> &6/team join <TAG> &7- Dołączanie do zespołu ; &8>> &6/team kick <nick> &7- Wyrzucanie gracza z zespołu ; &8>> &6/team info [TAG] &7- Informacje o zespole")) {
                    MessageUtil.sendMessage(sender, usageMain);
                }
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
