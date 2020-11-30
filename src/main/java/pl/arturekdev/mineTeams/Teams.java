package pl.arturekdev.mineTeams;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import pl.arturekdev.mineTeams.command.TeamsCommand;
import pl.arturekdev.mineTeams.configuration.Config;
import pl.arturekdev.mineTeams.listeners.AsyncPlayerChatListener;
import pl.arturekdev.mineTeams.listeners.EntityDamageByEntityListener;
import pl.arturekdev.mineTeams.listeners.PlayerDeathListener;
import pl.arturekdev.mineTeams.messages.Messages;
import pl.arturekdev.mineTeams.placeholder.PlaceholderExpansionTeams;
import pl.arturekdev.mineTeams.placeholder.PlaceholderExpansionUsers;
import pl.arturekdev.mineTeams.runnable.TeamsGlowingUpdater;
import pl.arturekdev.mineTeams.runnable.TeamsImportanceRunnable;

import java.io.File;

@Getter
public final class Teams extends JavaPlugin {

    private static Teams instance;
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

        getCommand("team").setExecutor(new TeamsCommand());
        getCommand("team").setTabCompleter(new TeamsCommand());

        Bukkit.getPluginManager().registerEvents(new EntityDamageByEntityListener(), this);
        Bukkit.getPluginManager().registerEvents(new AsyncPlayerChatListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerDeathListener(), this);

        Bukkit.getScheduler().runTaskTimer(this, new TeamsGlowingUpdater(), 20, 20);
        Bukkit.getScheduler().runTaskTimer(this, new TeamsImportanceRunnable(), 20, 20);

        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new PlaceholderExpansionTeams().register();
            new PlaceholderExpansionUsers().register();
        }

    }

}
