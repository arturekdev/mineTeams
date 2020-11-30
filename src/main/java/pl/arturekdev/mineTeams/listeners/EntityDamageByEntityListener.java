package pl.arturekdev.mineTeams.listeners;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.projectiles.ProjectileSource;
import pl.arturekdev.mineTeams.messages.Messages;
import pl.arturekdev.mineTeams.objects.team.Team;
import pl.arturekdev.mineTeams.objects.team.TeamUtil;
import pl.arturekdev.mineUtiles.utils.MessageUtil;

public class EntityDamageByEntityListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void event(EntityDamageByEntityEvent e) {

        if (e.isCancelled()) {
            return;
        }

        if (!(e.getEntity() instanceof Player)) {
            return;
        }

        Player victim = (Player) e.getEntity();
        Team victimTeam = TeamUtil.getTeam(victim);

        if (victimTeam == null) {
            return;
        }

        Entity damagerEntity = e.getDamager();

        if (damagerEntity instanceof Projectile) {
            ProjectileSource projectileDamager = ((Projectile) e.getDamager()).getShooter();

            if (projectileDamager instanceof Player) {
                Player damager = (Player) projectileDamager;

                if (victim == damager) {
                    return;
                }

                if (!victimTeam.isOnTeam(damager)) {
                    return;
                }

                if (victimTeam.isPvp()) {
                    return;
                }

                MessageUtil.sendMessage(damager, Messages.get("damageTeammate", " &8>> &cAtakowanie się w zespole jest wyłączone!"));
                e.setCancelled(true);

            }

        } else if (damagerEntity instanceof Player) {

            Player damager = (Player) damagerEntity;

            if (!(e.getEntity() instanceof Player)) {
                return;
            }

            if (!victimTeam.isOnTeam(damager)) {
                return;
            }

            if (victimTeam.isPvp()) {
                return;
            }

            MessageUtil.sendMessage(damager, Messages.get("damageTeammate", " &8>> &cAtakowanie się w zespole jest wyłączone!"));
            e.setCancelled(true);

        }

    }

}
