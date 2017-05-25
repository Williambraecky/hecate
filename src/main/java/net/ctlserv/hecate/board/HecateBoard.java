package net.ctlserv.hecate.board;

import lombok.Getter;
import net.ctlserv.hecate.Hecate;
import net.ctlserv.hecate.util.HecateUtil;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.Collection;
import java.util.UUID;

/**
 * Created by Williambraecky on 25-05-17.
 */
@Getter
public class HecateBoard {

    private static HecateSidebarLine fakeLine = new HecateSidebarLine(666);
    private static HecateTabEntry fakeTabEntry = new HecateTabEntry(666, UUID.randomUUID());

    private Hecate hecate;
    private Player player;
    private boolean removed = false;
    private boolean visible = true;
    private String title = "";
    private Objective objective;
    private BukkitRunnable updater;

    private int protocol, boardTicks = 0; //Player version
    private HecateBoardProvider currentBoardProvider;
    private Scoreboard scoreboard;
    private HecateTabEntry[] tab;
    private HecateSidebarLine[] sidebar;


    public HecateBoard(Hecate hecate, Player player) {
        this.hecate = hecate;
        this.player = player;
        this.protocol = ((CraftPlayer)player).getHandle().playerConnection.networkManager.getVersion();

        if (player.getScoreboard() != null
                && player.getScoreboard() != hecate.getPlugin().getServer().getScoreboardManager().getMainScoreboard()) {
            this.scoreboard = player.getScoreboard();
        } else {
            this.scoreboard = hecate.getPlugin().getServer().getScoreboardManager().getNewScoreboard();
            player.setScoreboard(this.scoreboard);
        }
        if (scoreboard.getObjective("buffer") == null) {
            scoreboard.registerNewObjective("buffer", "dummy");
        }
        objective = scoreboard.getObjective("buffer");
                initSidebar();
        if (hecate.getHecateConfiguration().isUseTab()) {
            initTab();
        }

        if (hecate.getHecateConfiguration().getDefaultProvider() != null) {
            setBoardProvider(hecate.getHecateConfiguration().getDefaultProvider(), hecate.getHecateConfiguration().getDefaultRefreshRate());
        }
    }

    void updateBoard() {
        //We have to get it here before since we are in async it could technically change between tab and sidebar updates
        HecateBoardProvider provider = this.currentBoardProvider;
        if (provider == null) {
            setVisible(false);
        } else {
            if (isVisible()) {
                updateLines(provider);
            }
            if (hecate.getHecateConfiguration().isUseTab()) {
                //We can directly update the tab when the user does .setPrefix() etc.. so we don't need anything more like the sidebar
                provider.gatherTabUpdates(getPlayer(), this, getBoardTicks());
                updateTab();

            }
            boardTicks += 1;
        }
    }

    private void updateTab() {
        if (currentBoardProvider == null) {
            return;
        }
        for (HecateTabEntry entry : tab) {
            if (entry.isUpdated()) {
                Team t = getTeam(entry.getName());
                if (t == null) {
                    t = registerNewTeam(entry.getName());
                }
                if (entry.getPrefix() != null && !t.getPrefix().equalsIgnoreCase(entry.getPrefix())) {
                    t.setPrefix(entry.getPrefix());
                }
                if (entry.getSuffix() != null && !t.getSuffix().equalsIgnoreCase(entry.getSuffix())) {
                    t.setSuffix(entry.getSuffix());
                }
                if (!t.hasEntry(entry.getName())) {
                    t.addEntry(entry.getName());
                }
                entry.setUpdated(false);
            }
        }
    }

    private void updateLines(HecateBoardProvider provider) {
        int size = provider.gatherSidebarUpdates(getPlayer(), this, getBoardTicks());
        if (!title.equals(provider.getTitle(getPlayer()))){
            objective.setDisplayName(provider.getTitle(getPlayer()));
            title = provider.getTitle(getPlayer());
        }
        int start = (16 - (16 - (size + 1)));
        for (int i = 1; i != 16; i++){
            HecateSidebarLine entry = getLine(i);
            Team team = entry.getTeam();
            if (team == null){
                hecate.getPlugin().getLogger().info("Team for line: " + i + " wasn't found for player: " + player.getName());
                continue;
            }
            if (i > size){
                if (!entry.isBlank()){
                    entry.blank();
                }
                if (entry.isBlank() && entry.isUpdated()){
                    scoreboard.resetScores(entry.getOldName());
                    team.removeEntry(entry.getOldName());
                    entry.setOldName("");
                }
                continue;
            }
            int score = start - i;
            if (objective.getScore(entry.getName()).getScore() != score){
                objective.getScore(entry.getName()).setScore(score);
            }
            if (!entry.isUpdated()){
                continue;
            }
            String prefix = entry.getPrefix();
            if (!team.getPrefix().equals(prefix)){
                team.setPrefix(prefix);
            }
            String suffix = entry.getSuffix();
            if (!team.getSuffix().equals(suffix)){
                team.setSuffix(suffix);
            }
            if (entry.hasEntryNameChanged()){
                team.removeEntry(entry.getOldName());
                scoreboard.resetScores(entry.getOldName());

                if (!team.hasEntry(entry.getName())){
                    team.addEntry(entry.getName());
                }

                entry.setOldName(entry.getName());
            }
            entry.setUpdated(false);
        }
    }

    public void setBoardProvider(HecateBoardProvider provider) {
        setBoardProvider(provider, hecate.getHecateConfiguration().getDefaultRefreshRate());
    }

    public void setBoardProvider(HecateBoardProvider provider, long updateInterval) {
        if (provider != null && provider == this.currentBoardProvider) {
            return;
        }
        boolean wasNull = currentBoardProvider == null;
        this.currentBoardProvider = provider;
        if (this.updater != null) {
            this.updater.cancel();
        }
        if (wasNull) {
            //Update prefix of everyone for the first provider use reflection for spigot without getOnlinePlayers
            setVisible(true);
            HecateUtil.getOnlinePlayer().forEach(this::updatePrefix);
        }
        if (provider == null) {
            this.scoreboard.clearSlot(DisplaySlot.SIDEBAR);
            return;
        }
        boardTicks = 0;
        (this.updater = new HecateBoardRunnable(this, provider))
                .runTaskTimerAsynchronously(hecate.getPlugin(), 2L, updateInterval);
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
        this.objective.setDisplaySlot(this.visible ? DisplaySlot.SIDEBAR : null);
    }

    public Team registerNewTeam(String name) {
        try {
            Team team = scoreboard.registerNewTeam(name);
            team.setPrefix(name);
            return team;
        } catch (Exception exception) {
            //Team already existed
            Team team = scoreboard.getTeam(name);
            team.setPrefix(name);
            return team;
        }
    }

    public Team getTeam(String name) {
        Team team = scoreboard.getTeam(name);
        if (team == null) {
            team = registerNewTeam(name);
        }
        return team;
    }

    public void initSidebar() {
        sidebar = new HecateSidebarLine[16];
        for (int i = 1; i <= 16; i++){
            sidebar[i - 1] = new HecateSidebarLine(i);
            sidebar[i - 1].setTeam(registerNewTeam("LINE_" + i));
        }
    }

    public HecateSidebarLine getLine(int position){
        if (position > 16 || position < 1 || sidebar == null){
            //This is so that it doesn't throw an error for people
            return fakeLine;
        }
        return sidebar[position - 1];
    }

    public void initTab() {
        tab = new HecateTabEntry[80];
        HecateTabEntry entry;
        for (int i = 0; i < 80; i++){
            //Lazy making of uuid
            if (i < 10){
                entry = new HecateTabEntry(i, UUID.fromString("00000000-0000-0000-0000-00000000000" + i));
            }else {
                entry = new HecateTabEntry(i, UUID.fromString("00000000-0000-0000-0000-0000000000" + i));
            }
            tab[i] = entry;
            entry.send(player);
            if (is1_8()){
                entry.setPrefix(HecateUtil.formatNumberToScoreboardString(i));
            }
        }
    }

    public HecateTabEntry getTabByPosition(int column, int row){
        if (!hecate.getHecateConfiguration().isUseTab() || column <= 0 || column > 4 || row > 20 || row <= 0 || tab == null){
            return fakeTabEntry;
        }
        column -= 1;
        row -= 1;
        if (!is1_8()){
            int result = (row * 3) + column;
            return tab[result];
        }
        int result = column * 20 + row;
        return tab[result];
    }

    public boolean is1_8() {
        return protocol >= 47;
    }

    public void onPlayerJoin(Player player) {
        if (currentBoardProvider == null) {
            return;
        }
        String prefix = currentBoardProvider.getPrefixFor(this.player, player);
        if (prefix == null || prefix.equalsIgnoreCase("")) {
            if (hecate.getHecateConfiguration().isUseTab() && is1_8()) {
                prefix = HecateUtil.formatNumberToScoreboardString(100);
            } else {
                return;
            }
        }
        updatePrefix(player, prefix);
    }

    public void onPlayerQuit(Player player) {
        updatePrefix(player, "");
    }

    public void updatePrefix(Collection<Player> players) {
        players.forEach(this::updatePrefix);
    }

    public void updatePrefix(Player player) {
        if (currentBoardProvider == null) {
            return;
        }
        String prefix = currentBoardProvider.getPrefixFor(this.player, player);
        if (prefix == null || prefix.equalsIgnoreCase("")) {
            if (hecate.getHecateConfiguration().isUseTab() && is1_8()) {
                prefix = HecateUtil.formatNumberToScoreboardString(100);
            } else {
                return;
            }
        }
        updatePrefix(player, prefix);
    }

    public void updatePrefix(Player player, String prefix) {
        if (removed) {
            return;
        }
        if (prefix.equals("")) {
            scoreboard.getPlayerTeam(player).removePlayer(player);
            return;
        }
        Team team = getTeam(prefix);
        if (!team.hasPlayer(player)) {
            team.addPlayer(player);
        }
    }

    public void remove() {
        //TODO: clear board
        removed = true;
        player = null;
        if (updater != null) {
            updater.cancel();
        }
        updater = null;
        if (scoreboard != null) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    scoreboard.getTeams().forEach(Team::unregister);
                    scoreboard.getObjectives().forEach(Objective::unregister);
                }
            }.runTaskAsynchronously(hecate.getPlugin());
        }
    }
}
