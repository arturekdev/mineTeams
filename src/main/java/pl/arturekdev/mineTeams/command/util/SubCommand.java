package pl.arturekdev.mineTeams.command.util;

import lombok.Data;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

@Data
public abstract class SubCommand {

    private final String name;
    private final List<String> aliases;

    public SubCommand(String name, List<String> aliases) {
        this.name = name;
        this.aliases = aliases;
    }

    public SubCommand(String name) {
        this(name, Collections.emptyList());
    }

    public abstract void handleCommand(Player player, String[] arguments);
}
