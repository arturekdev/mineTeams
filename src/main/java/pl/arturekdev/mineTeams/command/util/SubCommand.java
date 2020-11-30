package pl.arturekdev.mineTeams.command.util;

import org.bukkit.entity.Player;

import java.util.Arrays;

public abstract class SubCommand {

    public final String[] args;
    public final Player player;

    public SubCommand(Player player, String[] args) {
        this.player = player;
        this.args = Arrays.copyOfRange(args, 1, args.length);
    }

    public abstract void run();

}
