package pl.arturekdev.mineTeams;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import pl.arturekdev.mineTeams.command.TeamsCommand;
import pl.arturekdev.mineTeams.configuration.Config;
import pl.arturekdev.mineTeams.database.DatabaseConnector;
import pl.arturekdev.mineTeams.listeners.AsyncPlayerChatListener;
import pl.arturekdev.mineTeams.listeners.EntityDamageByEntityListener;
import pl.arturekdev.mineTeams.listeners.InventoryCloseListener;
import pl.arturekdev.mineTeams.listeners.PlayerDeathListener;
import pl.arturekdev.mineTeams.messages.Messages;
import pl.arturekdev.mineTeams.objects.team.TeamUtil;
import pl.arturekdev.mineTeams.objects.user.UserUtil;
import pl.arturekdev.mineTeams.placeholder.PlaceholderExpansionTeams;
import pl.arturekdev.mineTeams.placeholder.PlaceholderExpansionUsers;
import pl.arturekdev.mineTeams.runnable.TeamsGlowingUpdater;
import pl.arturekdev.mineTeams.runnable.TeamsImportanceRunnable;
import pl.arturekdev.mineTeams.runnable.TeamsSaveRunnable;
import pl.arturekdev.mineTeams.runnable.UsersSaveRunnable;

import java.io.File;

@Getter
public final class Teams extends JavaPlugin {

    private static Teams instance;
    private static DatabaseConnector databaseConnector;
    private Config configuration;

    public static Teams getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {

        instance = this;

        configuration = new Config(new File(this.getDataFolder(), "config.json"));
        configuration.parseConfiguration(this);

        Messages messages = new Messages(this);
        messages.loadMessages();

        databaseConnector = new DatabaseConnector();
        databaseConnector.prepareCollectionTeams();
        databaseConnector.prepareCollectionUsers();

        TeamUtil teamUtil = new TeamUtil();
        teamUtil.loadTeams(databaseConnector);

        UserUtil userUtil = new UserUtil();
        userUtil.loadUsers(databaseConnector);

        initListeners();
        initSchedulers();

        getCommand("team").setExecutor(new TeamsCommand(databaseConnector, configuration));
        getCommand("team").setTabCompleter(new TeamsCommand(databaseConnector, configuration));

        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new PlaceholderExpansionTeams().register();
            new PlaceholderExpansionUsers().register();
        }
    }

    private void initListeners() {
        Bukkit.getPluginManager().registerEvents(new EntityDamageByEntityListener(), this);
        Bukkit.getPluginManager().registerEvents(new AsyncPlayerChatListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerDeathListener(), this);
        Bukkit.getPluginManager().registerEvents(new InventoryCloseListener(), this);
        this.getLogger().fine("Success registered listeners!");
    }

    private void initSchedulers() {
        Bukkit.getScheduler().runTaskTimer(this, new TeamsGlowingUpdater(), 20, 20);
        Bukkit.getScheduler().runTaskTimer(this, new TeamsImportanceRunnable(), 20, 20);
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, new TeamsSaveRunnable(databaseConnector), 60, 60);
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, new UsersSaveRunnable(databaseConnector), 60, 60);
    }

}
