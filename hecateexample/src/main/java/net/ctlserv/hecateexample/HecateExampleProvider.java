package net.ctlserv.hecateexample;

import net.ctlserv.hecate.board.HecateBoard;
import net.ctlserv.hecate.board.HecateBoardProvider;
import net.ctlserv.hecate.board.HecateSidebarLine;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Random;

/**
 * Created by Williambraecky on 25-05-17.
 */
public class HecateExampleProvider implements HecateBoardProvider {

    private HecateExample plugin;
    private int count = 0;

    public HecateExampleProvider(HecateExample plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getTitle(Player boardowner) {
        return "§6§lTitle§7: §e" + boardowner.getName();
    }

    @Override
    public String getPrefixFor(Player boardOwner, Player target) {
        return "§9§7[§a" + target.getName().substring(0, 3) + "§7] ";
    }

    @Override
    public void gatherTabUpdates(Player boardOwner, HecateBoard board) {
        if (board.getBoardTicks() == 0) {
            for (int row = 1; row <= (board.is1_8() ? 4 : 3); row++) {
                for (int column = 1; column <= 20; column++) {
                    board.getTabByPosition(row, column).setText("§6Row " + row + " Column " + column);
                }
            }
        }
    }

    @Override
    public void gatherSidebarUpdates(Player boardOwner, HecateBoard board) {
        HecateSidebarLine line;
        board.wrapScoreboardWithSpacersIfNotEmpty();
        line = board.getNextLine();
        line.setPrefix("§6Count§7: ").setSuffix("§e" + count++);
        line = board.getNextLine();
        line.setPrefix("§6TabTicks§7: ").setSuffix("§e" + board.getBoardTicks());
        board.getNextLine().blank();
        board.getNextLine().setPrefix("§6X: ").setSuffix("§e" + boardOwner.getLocation().getBlockX());
        board.getNextLine().setPrefix("§6Y: ").setSuffix("§e" + boardOwner.getLocation().getBlockY());
        board.getNextLine().setPrefix("§6Z: ").setSuffix("§e" + boardOwner.getLocation().getBlockZ());
    }
}
