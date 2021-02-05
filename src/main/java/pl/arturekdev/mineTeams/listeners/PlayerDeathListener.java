package pl.arturekdev.mineTeams.listeners;

import com.google.gson.*;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.entity.*;
import pl.arturekdev.mineEconomy.*;
import pl.arturekdev.mineTeams.*;
import pl.arturekdev.mineTeams.messages.*;
import pl.arturekdev.mineTeams.objects.team.*;
import pl.arturekdev.mineTeams.objects.user.*;
import pl.arturekdev.mineUtiles.utils.*;

public class PlayerDeathListener implements Listener {

    @EventHandler
    public void event(PlayerDeathEvent event) {

        event.setDeathMessage("");

        Player victim = event.getEntity();
        Player killer = event.getEntity().getKiller();

        if (killer == null) {
            return;
        }

        User victimUser = UserUtil.getUser(victim.getUniqueId());
        User killerUser = UserUtil.getUser(killer.getUniqueId());

        JsonObject config = Teams.getInstance().getConfiguration().getElement("configuration").getAsJsonObject();

        long playTime = victim.getStatistic(Statistic.PLAY_ONE_MINUTE) * 50L;

        if (playTime < TimeUtil.timeFromString(config.get("minimumPlayTime").getAsString())) {
            MessageUtil.sendMessage(killer, Messages.get("minimHoursPlayed", " &8>> &cGracz, którego zabiłeś, nie rozegrał jeszcze minimalnej ilości godzin, więc nie otrzymasz za niego nagrody!"));
            return;
        }

        /*

        if (Objects.equals(Objects.requireNonNull(victim.getAddress()).getAddress(), Objects.requireNonNull(killer.getAddress()).getAddress())) {
            MessageUtil.sendMessage(killer, Messages.get("sameIPAdresses", " &8>> &cGracz, którego zabiłeś, posiada taki sam adres IP."));
            return;
        }

         */

        if (System.currentTimeMillis() - victimUser.getLastDeath() < TimeUtil.timeFromString(config.get("deathsDelay").getAsString())) {
            MessageUtil.sendMessage(killer, Messages.get("lastHourKilled", " &8>> &cGracz, którego zabiłeś, został już zabity w ciągu godziny, więc nie otrzymujesz za niego nagrody!"));
            return;
        }

        Team victimTeam = TeamUtil.getTeam(victim);
        Team killerTeam = TeamUtil.getTeam(killer);

        if (killerTeam == victimTeam && victimTeam != null) {
            MessageUtil.sendMessage(killer, Messages.get("sameTeamPlayer", " &8>> &cZabijąc swojego członka zespołu nie zmienią się statystyki!"));
            return;
        }

        if (victimTeam != null) {
            victimTeam.getStats().setDeaths(victimTeam.getStats().getDeaths() + 1);
            victimTeam.setNeedUpdate(true);
        }

        if (killerTeam != null) {
            killerTeam.getStats().setKills(killerTeam.getStats().getKills() + 1);
            killerTeam.setNeedUpdate(true);
        }

        EconomyService economyService = new EconomyService();
        int money = economyService.getMoney(victim) / 100 * config.get("killMoneyPercentage").getAsInt();

        killerUser.setKills(killerUser.getKills() + 1);
        victimUser.setDeaths(victimUser.getDeaths() + 1);
        killerUser.setNeedUpdate(true);
        victimUser.setNeedUpdate(true);
        victimUser.setLastDeath(System.currentTimeMillis());

        if (killerTeam != null) {
            economyService.takeMoney(victim, money);
            economyService.giveMoney(killer, money);
        } else {
            money = 0;
        }

        Bukkit.broadcastMessage(MessageUtil.fixColor(" &4&l✞ %victimTeam% &7%victim% &8(#d9293c-%money% Iskier&8) &czostał zabity przez %killerTeam% &7%killer% &8(&a+%money% Iskier&8)"
                .replace("%victimTeam%", victimTeam == null ? "" : "&8(&e" + victimTeam.getTag() + "&8)")
                .replace("%killerTeam%", killerTeam == null ? "" : "&8(&e" + killerTeam.getTag() + "&8)")
                .replace("%victim%", victim.getName())
                .replace("%killer%", killer.getName())
                .replace("%money%", String.valueOf(money))));

    }

}
