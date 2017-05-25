package hecateexample;

import net.ctlserv.hecate.board.HecateBoard;
import net.ctlserv.hecate.board.HecateBoardProvider;
import net.ctlserv.hecate.board.HecateSidebarLine;
import org.bukkit.entity.Player;

import java.util.Random;

/**
 * Created by Williambraecky on 25-05-17.
 */
public class HecateExampleProvider implements HecateBoardProvider {

    private HecateExample plugin;
    private int count = 0;
    private Random random = new Random();

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
    public void gatherTabUpdates(Player boardOwner, HecateBoard board, int boardTicks) {
        if (boardTicks == 0) {
            for (int row = 1; row <= (board.is1_8() ? 4 : 3); row++) {
                for (int column = 1; column <= 20; column++) {
                    board.getTabByPosition(row, column).setPrefix("§6Row "+ row).setSuffix(" §6Column " + column);
                }
            }
        }
    }

    @Override
    public int gatherSidebarUpdates(Player boardOwner, HecateBoard board, int boardTicks) {
        HecateSidebarLine line;
        int index = 1;
        board.getLine(index++).spacer();
        line = board.getLine(index++);
        line.setPrefix("§6Count§7: ").setSuffix("§e" + count++);
        line = board.getLine(index++);
        line.setPrefix("§6TabTicks§7: ").setSuffix("§e" + boardTicks);
        for (int i = 0; i < random.nextInt(10); i ++) {
            board.getLine(index++).setPrefix("§6Line§7: ").setSuffix("§e" + i);
        }
        board.getLine(index).spacer();
        return index;
    }
}
