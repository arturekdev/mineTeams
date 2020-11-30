package pl.arturekdev.mineTeams.runnable;

public class TeamsGlowingUpdater implements Runnable {

    @Override
    public void run() {
        /*
        for (Player player : Bukkit.getOnlinePlayers()) {

            Team team = TeamUtil.getTeam(player);


            if (team == null) {
                GlowAPI.setGlowing(player, false, Bukkit.getOnlinePlayers());
                continue;
            }

            GlowAPI.setGlowing(player, GlowAPI.Color.GREEN, team.onlinePlayers());

            EGlowAPI eGlowAPI = EGlow.getAPI();

            if (team == null) {
                eGlowAPI.disableGlow(player);
                continue;
            }

            eGlowAPI.enableGlow(player, EGlowColor.GREEN);
            eGlowAPI.setCustomGlowReceivers(player, team.onlinePlayers());

        }
        */
    }

}
