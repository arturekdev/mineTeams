package pl.arturekdev.mineTeams.comparators;

import pl.arturekdev.mineTeams.objects.team.Team;

import java.util.Comparator;

public class TeamKDRanking implements Comparator<Team> {

    @Override
    public int compare(Team o1, Team o2) {
        return (int) (o2.getKD() - o1.getKD());
    }
}
