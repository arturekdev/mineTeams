package pl.arturekdev.mineTeams;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import pl.arturekdev.mineTeams.command.TeamCommand;
import pl.arturekdev.mineTeams.configuration.Config;
import pl.arturekdev.mineTeams.listeners.AsyncPlayerChatListener;
import pl.arturekdev.mineTeams.listeners.EntityDamageByEntityListener;
import pl.arturekdev.mineTeams.listeners.PlayerDeathListener;
import pl.arturekdev.mineTeams.messages.Messages;
import pl.arturekdev.mineTeams.placeholder.PlaceholderExpansionTools;
import pl.arturekdev.mineTeams.runnable.TeamsGlowingUpdater;
import pl.arturekdev.mineTeams.runnable.TeamsImportanceRunnable;

import java.io.File;

public final class Teams extends JavaPlugin {

    @Getter
    private static Teams instance;
    @Getter
    private Config configuration;

    @Override
    public void onEnable() {

        instance = this;

        configuration = new Config(new File(this.getDataFolder(), "config.json"));
        configuration.parseConfiguration(this);

        Messages messages = new Messages(this);
        messages.loadMessages();

        getCommand("team").setExecutor(new TeamCommand());
        getCommand("team").setTabCompleter(new TeamCommand());

        Bukkit.getPluginManager().registerEvents(new EntityDamageByEntityListener(), this);
        Bukkit.getPluginManager().registerEvents(new AsyncPlayerChatListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerDeathListener(), this);

        Bukkit.getScheduler().runTaskTimer(this, new TeamsGlowingUpdater(), 20, 20);
        Bukkit.getScheduler().runTaskTimer(this, new TeamsImportanceRunnable(), 20, 20);

        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new PlaceholderExpansionTools().register();
        }

    }

}
