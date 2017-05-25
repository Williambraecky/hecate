package net.ctlserv.hecate.board;

import org.bukkit.entity.Player;

/**
 * Created by Williambraecky on 25-05-17.
 */
public interface HecateBoardProvider {

    String getTitle(Player boardowner);

    String getPrefixFor(Player boardOwner, Player target);

    void gatherTabUpdates(Player boardOwner, HecateBoard board, int boardTicks);

    int gatherSidebarUpdates(Player boardOwner, HecateBoard board, int boardTicks);

}
