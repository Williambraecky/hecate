package net.ctlserv.hecate.util;

import com.google.common.collect.ImmutableList;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.Collection;

/**
 * Created by Williambraecky on 25-05-17.
 */
public class HecateUtil {

    //We need this because we can't have two sidebarlines with the same name and for 1.8 tab
    public static String formatNumberToScoreboardString(int i) {
        String s = String.valueOf(i);
        StringBuilder sb = new StringBuilder();
        if (i < 10) {
            sb.append("§0");
        }
        for (int j = 0; j < s.length(); j++) {
            sb.append("§").append(s.substring(j, j + 1));
        }
        return sb.toString();
    }

    //We need this because bukkit has 2 methods for getting the players
    public static Collection<? extends Player> getOnlinePlayer() {
        try {
            Field f = Bukkit.getServer().getClass().getDeclaredField("playerView");
            f.setAccessible(true);
            Collection<? extends Player> coll = (Collection<? extends Player>) f.get(Bukkit.getServer());
            return coll;

        } catch (Exception ignored) {
        }
        return ImmutableList.of();
    }

}
