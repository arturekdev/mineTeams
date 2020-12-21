package pl.arturekdev.mineTeams.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import pl.arturekdev.mineTeams.command.util.SubCommand;
import pl.arturekdev.mineTeams.configuration.Config;
import pl.arturekdev.mineTeams.database.DatabaseConnector;
import pl.arturekdev.mineTeams.messages.Messages;
import pl.arturekdev.mineTeams.objects.team.Team;
import pl.arturekdev.mineTeams.objects.team.TeamUtil;
import pl.arturekdev.mineUtiles.utils.MessageUtil;

import java.util.*;
import java.util.stream.Collectors;

public class TeamsCommand implements CommandExecutor, TabCompleter {

    private final List<SubCommand> subCommands;

    public TeamsCommand(DatabaseConnector databaseConnector, Config configuration) {
        this.subCommands = new ArrayList<>();
        this.subCommands.add(new BankCommand());
        this.subCommands.add(new CreateCommand(configuration));
        this.subCommands.add(new DeleteCommand(databaseConnector));
        this.subCommands.add(new InfoCommand());
        this.subCommands.add(new InviteCommand());
        this.subCommands.add(new JoinCommand());
        this.subCommands.add(new KickCommand());
        this.subCommands.add(new LeaveCommand());
        this.subCommands.add(new PvpCommand());
        this.subCommands.add(new SlotsCommand());
        this.subCommands.add(new VaultCommand());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] arguments) {
        if (arguments.length == 0) {
            for (String usageMain : Messages.getList("usageMain", " &8>> &6/team create <TAG> &7- Tworzenie nowego zespołu ; &8>> &6/team delete &7- Usuwanie zespołu ; &8>> &6/team invite <nick> &7- Zapraszanie gracza do zespołu ; &8>> &6/team join <TAG> &7- Dołączanie do zespołu ; &8>> &6/team kick <nick> &7- Wyrzucanie gracza z zespołu ; &8>> &6/team info [TAG] &7- Informacje o zespole")) {
                MessageUtil.sendMessage(sender, usageMain);
            }
            return false;
        }

        Player player = (Player) sender;

        Optional<SubCommand> optionalSubCommand = getSubCommand(arguments[0]);
        if (optionalSubCommand.isPresent()) {
            SubCommand subCommand = optionalSubCommand.get();
            subCommand.handleCommand(player, Arrays.copyOfRange(arguments, 1, arguments.length));
        } else {
            for (String usageMain : Messages.getList("usageMain", " &8>> &6/team create <TAG> &7- Tworzenie nowego zespołu ; &8>> &6/team delete &7- Usuwanie zespołu ; &8>> &6/team invite <nick> &7- Zapraszanie gracza do zespołu ; &8>> &6/team join <TAG> &7- Dołączanie do zespołu ; &8>> &6/team kick <nick> &7- Wyrzucanie gracza z zespołu ; &8>> &6/team info [TAG] &7- Informacje o zespole")) {
                MessageUtil.sendMessage(sender, usageMain);
            }
        }

        return false;
    }

    public Optional<SubCommand> getSubCommand(String name) {
        return subCommands.stream()
                .filter(subCommand -> subCommand.getName().equalsIgnoreCase(name) || subCommand.getAliases().contains(name))
                .findAny();
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

        if (args[0].equalsIgnoreCase("invite")) {

            if (args.length == 2) {
                List<String> old = Bukkit.getOnlinePlayers().stream().map(HumanEntity::getName).collect(Collectors.toList());

                List<String> tab = new ArrayList<>();
                StringUtil.copyPartialMatches(args[1], old, tab);
                Collections.sort(tab);
                return tab;
            }

            return Collections.emptyList();
        }

        if (args[0].equalsIgnoreCase("bank")) {

            if (args.length == 2) {
                List<String> old = new ArrayList<>();

                old.add("withdraw");
                old.add("deposit");

                List<String> tab = new ArrayList<>();
                StringUtil.copyPartialMatches(args[1], old, tab);
                Collections.sort(tab);
                return tab;
            }

            return Collections.emptyList();
        }

        if (args[0].equalsIgnoreCase("vault")) {

            if (args.length == 2) {
                List<String> old = new ArrayList<>();

                old.add("upgrade");

                List<String> tab = new ArrayList<>();
                StringUtil.copyPartialMatches(args[1], old, tab);
                Collections.sort(tab);
                return tab;
            }

            return Collections.emptyList();

        }

        if (args[0].equalsIgnoreCase("slots")) {

            if (args.length == 2) {
                List<String> old = new ArrayList<>();

                old.add("upgrade");

                List<String> tab = new ArrayList<>();
                StringUtil.copyPartialMatches(args[1], old, tab);
                Collections.sort(tab);
                return tab;
            }

            return Collections.emptyList();
        }

        if (args[0].equalsIgnoreCase("kick")) {

            if (args.length == 2) {
                List<String> old = new ArrayList<>();

                Player player = (Player) sender;

                Team team = TeamUtil.getTeam(player);

                if (team == null) {
                    return Collections.emptyList();
                }

                if (!team.getOwner().equals(player.getUniqueId())) {
                    return Collections.emptyList();
                }

                for (UUID member : team.getMembers()) {
                    old.add(Bukkit.getOfflinePlayer(member).getName());
                }

                List<String> tab = new ArrayList<>();
                StringUtil.copyPartialMatches(args[1], old, tab);
                Collections.sort(tab);
                return tab;

            }

            return Collections.emptyList();
        }


        return Collections.emptyList();
    }
}
