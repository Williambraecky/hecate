package net.ctlserv.hecate.board;

import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by Williambraecky on 25-05-17.
 */
public class HecateBoardRunnable extends BukkitRunnable {

    //I'm creating a separate class for the runnable as I had some issues before when an error would happen

    private HecateBoard hecateBoard;
    private HecateBoardProvider provider;

    HecateBoardRunnable(HecateBoard hecateBoard, HecateBoardProvider provider) {
        this.hecateBoard = hecateBoard;
        this.provider = provider;
    }

    @Override
    public void run() {
        if (hecateBoard.isRemoved() || hecateBoard == null || provider == null || provider != hecateBoard.getCurrentBoardProvider()) {
            cancel();
            return;
        }
        hecateBoard.updateBoard();
    }

    @Override
    public void cancel() {
        super.cancel();
        hecateBoard = null;
        provider = null;
    }
}
