package pl.arturekdev.mineTeams.comparators;

import pl.arturekdev.mineTeams.objects.team.Team;

import java.util.Comparator;

public class TeamBankRanking implements Comparator<Team> {

    @Override
    public int compare(Team o1, Team o2) {
        return o2.getStats().getBank() - o1.getStats().getBank();
    }
}
