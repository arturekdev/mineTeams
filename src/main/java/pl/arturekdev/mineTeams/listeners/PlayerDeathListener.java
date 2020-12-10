package pl.arturekdev.mineTeams.listeners;

import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import pl.arturekdev.mineEconomy.EconomyService;
import pl.arturekdev.mineTeams.Teams;
import pl.arturekdev.mineTeams.messages.Messages;
import pl.arturekdev.mineTeams.objects.team.Team;
import pl.arturekdev.mineTeams.objects.team.TeamUtil;
import pl.arturekdev.mineTeams.objects.user.User;
import pl.arturekdev.mineTeams.objects.user.UserUtil;
import pl.arturekdev.mineUtiles.utils.MessageUtil;
import pl.arturekdev.mineUtiles.utils.TimeUtil;

public class PlayerDeathListener implements Listener {

    @EventHandler
    public void event(PlayerDeathEvent e) {
        Player victim = e.getEntity();
        Player killer = e.getEntity().getKiller();
        User user = UserUtil.getUser(victim.getUniqueId());

        if (killer == null) {
            return;
        }

        JsonObject config = Teams.getInstance().getConfiguration().getElement("configuration").getAsJsonObject();

        long playTime = System.currentTimeMillis() - victim.getFirstPlayed();

        if (playTime < TimeUtil.timeFromString(config.get("minimumPlayTime").getAsString())) {
            MessageUtil.sendMessage(killer, Messages.get("minimHoursPlayed", " &8>> &cGracz, którego zabiłeś, nie rozegrał jeszcze minimalnej ilości godzin, więc nie otrzymasz za niego nagrody!"));
            return;
        }

        if (user.getLastDeath() < System.currentTimeMillis() + TimeUtil.timeFromString(config.get("deathsDelay").getAsString())) {
            MessageUtil.sendMessage(killer, Messages.get("lastHourKilled", " &8>> &cGracz, którego zabiłeś, został już zabity w ciągu godziny, więc nie otrzymujesz za niego nagrody!"));
            return;
        }

        Team victimTeam = TeamUtil.getTeam(victim);
        Team killerTeam = TeamUtil.getTeam(killer);

        if (victimTeam != null) {
            victimTeam.getStats().setDeaths(victimTeam.getStats().getDeaths() + 1);
        }

        if (killerTeam != null) {
            killerTeam.getStats().setKills(killerTeam.getStats().getKills() + 1);
        }

        EconomyService economyService = new EconomyService();
        int money = economyService.getMoney(victim) / 100 * config.get("killMoneyPercentage").getAsInt();

        economyService.takeMoney(victim, money);
        economyService.giveMoney(killer, money);

        Bukkit.broadcastMessage(MessageUtil.fixColor(" &4&l✞ %victimTeam% &7%victim% &8(#d9293c-%money% Iskier&8) &czostał zabiy przez %killerTeam% &7%killer% &8(&a+%money% Iskier&8)"
                .replace("%victimTeam%", victimTeam == null ? "" : "&8(&e" + victimTeam.getTag() + "&8)")
                .replace("%killerTeam%", killerTeam == null ? "" : "&8(&e" + killerTeam.getTag() + "&8)")
                .replace("%victim%", victim.getName())
                .replace("%killer%", killer.getName())
                .replace("%money%", String.valueOf(money))));

        if (victimTeam != null) {
            victimTeam.setNeedUpdate(true);
        }

        if (killerTeam != null) {
            killerTeam.setNeedUpdate(true);
        }
    }

}
