package pl.arturekdev.mineTeams.objects.team;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TeamStats {

    private int kills;
    private int deaths;
    private int bank;

}
