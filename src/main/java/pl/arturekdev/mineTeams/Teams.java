package pl.arturekdev.mineTeams;

import lombok.*;
import org.bukkit.*;
import org.bukkit.plugin.java.*;
import pl.arturekdev.mineTeams.command.*;
import pl.arturekdev.mineTeams.configuration.*;
import pl.arturekdev.mineTeams.database.*;
import pl.arturekdev.mineTeams.listeners.*;
import pl.arturekdev.mineTeams.messages.*;
import pl.arturekdev.mineTeams.objects.team.*;
import pl.arturekdev.mineTeams.objects.user.*;
import pl.arturekdev.mineTeams.placeholder.*;
import pl.arturekdev.mineTeams.runnable.*;

import java.io.*;

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

        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new PlaceholderExpansionTeams().register();
        }

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

    }

    @Override
    public void onDisable() {
        TeamUtil.getTeams().forEach(team -> team.update(databaseConnector));
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
        Bukkit.getScheduler().runTaskTimer(this, new TeamsImportanceRunnable(databaseConnector), 20, 20);
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, new TeamsSaveRunnable(databaseConnector), 60, 60);
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, new UsersSaveRunnable(databaseConnector), 60, 60);
    }

}
