package net.ctlserv.hecate.board;

import lombok.Getter;
import lombok.Setter;
import net.ctlserv.hecate.util.HecateUtil;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Team;

/**
 * Created by Williambraecky on 25-05-17.
 */
@Getter
@Setter
public class HecateSidebarLine {

    private static final String STRAIGHT_LINE = "------------";
    private final int position;
    private String name = "";
    private int score = -1;
    private String oldName;
    private Team team;
    private Objective objective;

    HecateSidebarLine(int position) {
        this.position = position;
    }

    HecateSidebarLine setScore(int score) {
        if (this.score != score) {
            if (score == -1) {
                team.getScoreboard().resetScores(getName());
            } else {
                objective.getScore(getName()).setScore(score);
            }
        }
        this.score = score;
        return this;
    }

    public HecateSidebarLine setPrefix(String prefix) {
        if (!team.getPrefix().equals(prefix)) {
            if (prefix.length() > 16) {
                prefix = prefix.substring(0, 16);
            }
            team.setPrefix(prefix);
        }
        return this;
    }

    public HecateSidebarLine setSuffix(String suffix) {
        if (!team.getSuffix().equals(suffix)) {
            if (suffix.length() > 16) {
                suffix = suffix.substring(0, 16);
            }
            team.setSuffix(suffix);
        }
        return this;
    }

    public String getName() {
        if (name.equals("")) {
            setName(HecateUtil.formatNumberToScoreboardString(position) + ChatColor.RESET);
        }
        return name;
    }

    public HecateSidebarLine setName(String name) {
        if (!this.name.equals(name)) {
            if (name.length() > 16) {
                name = name.substring(0, 16);
            }
            if (team.hasEntry(this.name)) {
                team.removeEntry(this.name);
                team.getScoreboard().resetScores(this.name);
            }
            team.addEntry(name);
            objective.getScore(name).setScore(score);
            this.name = name;
        }
        return this;
    }

    public void blank() {
        setPrefix("");
        setName(HecateUtil.formatNumberToScoreboardString(position) + ChatColor.RESET);
        setSuffix("");
    }

    public void spacer() {
        setPrefix(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH.toString() + STRAIGHT_LINE);
        setName(HecateUtil.formatNumberToScoreboardString(position) + ChatColor.RESET);
        setSuffix(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH.toString() + STRAIGHT_LINE.substring(0, 7));
    }

    public void setTeamAndObjective(Team team, Objective objective) {
        this.team = team;
        this.objective = objective;
    }
}
