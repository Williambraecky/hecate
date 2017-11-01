package net.ctlserv.hecate;

import lombok.Getter;
import net.ctlserv.hecate.board.HecateBoard;
import net.ctlserv.hecate.config.HecateConfiguration;
import net.ctlserv.hecate.listener.HecateListener;
import net.ctlserv.hecate.util.HecateUtil;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.*;

/**
 * Created by Williambraecky on 25-05-17.
 */
@Getter
public class Hecate {

    private static Hecate instance;

    private Plugin plugin;
    private Map<UUID, HecateBoard> hecateBoardMap = new HashMap<>();
    private HecateConfiguration hecateConfiguration;

    public Hecate(Plugin plugin, HecateConfiguration configuration) {
        if (instance != null) {
            throw new UnsupportedOperationException("A running hecate has been found, there is no support for multiple instances.");
        }
        instance = this;
        this.plugin = plugin;
        this.hecateConfiguration = configuration;
        plugin.getServer().getPluginManager().registerEvents(new HecateListener(this), plugin);
        Collection<Player> collection = (Collection<Player>) HecateUtil.getOnlinePlayer();
        for (Player player : collection) {
            setBoard(player.getUniqueId(), new HecateBoard(this, player));
            collection.forEach(player1 -> getBoard(player.getUniqueId()).onPlayerJoin(player1));
        }
    }

    public HecateBoard getBoard(Player player) {
        return getBoard(player.getUniqueId());
    }

    public HecateBoard getBoard(UUID uuid) {
        return hecateBoardMap.get(uuid);
    }

    public HecateBoard setBoard(Player player, HecateBoard board) {
        return setBoard(player.getUniqueId(), board);
    }

    public HecateBoard setBoard(UUID uuid, HecateBoard board) {
        return hecateBoardMap.put(uuid, board);
    }

    public HecateBoard removeBoard(Player player) {
        return removeBoard(player.getUniqueId());
    }

    public HecateBoard removeBoard(UUID uuid) {
        return hecateBoardMap.remove(uuid);
    }

    public Collection<HecateBoard> getBoards() {
        return hecateBoardMap.values();
    }
}
