package pl.arturekdev.mineTeams.comparators;

import pl.arturekdev.mineTeams.objects.user.User;

import java.util.Comparator;

public class UserKillRanking implements Comparator<User> {

    @Override
    public int compare(User o1, User o2) {
        return o2.getKills() - o1.getKills();
    }
}
