package net.ctlserv.hecate.listener;

import net.ctlserv.hecate.Hecate;
import net.ctlserv.hecate.board.HecateBoard;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Created by Williambraecky on 25-05-17.
 */
public class HecateListener implements Listener {

    private Hecate hecate;

    public HecateListener(Hecate hecate) {
        this.hecate = hecate;
    }

    //We need to make the board as quickly as possible so that plugins can get it aswell
    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent event) {
        hecate.setBoard(event.getPlayer().getUniqueId(), new HecateBoard(hecate, event.getPlayer()));
        hecate.getBoards().forEach(hecateBoard -> hecateBoard.onPlayerJoin(event.getPlayer()));
    }

    //This time we need to remove it as last, MONITOR would also work.
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onQuit(PlayerQuitEvent event) {
        hecate.removeBoard(event.getPlayer().getUniqueId()).remove();
        hecate.getBoards().forEach(hecateBoard -> hecateBoard.onPlayerQuit(event.getPlayer()));
    }
}
