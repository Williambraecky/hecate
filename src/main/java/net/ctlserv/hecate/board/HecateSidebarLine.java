package net.ctlserv.hecate.board;

import lombok.Getter;
import lombok.Setter;
import net.ctlserv.hecate.util.HecateUtil;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.Team;

/**
 * Created by Williambraecky on 25-05-17.
 */
@Getter
@Setter
public class HecateSidebarLine {

    private String prefix = "", name = "", suffix = "";
    private boolean isUpdated = false;
    private final int position;
    private int score = -1;
    private final String teamName;
    private String oldName;
    private Team team;

    HecateSidebarLine(int position){
        this.position = position;
        teamName = "LINE_" + position;
    }

    public HecateSidebarLine(String prefix, String name, String suffix, int position){
        this.prefix = prefix;
        this.name = name;
        this.suffix = suffix;
        this.position = position;
        teamName = "LINE_" + position;
    }

    public HecateSidebarLine setPrefix(String prefix){
        if (!this.prefix.equals(prefix)){
            if (prefix.length() > 16){
                prefix = prefix.substring(0, 16);
            }
            this.prefix = prefix;
            isUpdated = true;
        }
        return this;
    }

    public HecateSidebarLine setName(String name){
        if (!this.name.equals(name)){
            if (name.length() > 16){
                name = name.substring(0, 16);
            }
            this.oldName = this.name;
            this.name = name;
            isUpdated = true;
        }
        return this;
    }

    public boolean hasEntryNameChanged(){
        return oldName != getName();
    }

    public String getName() {
        if (name.equals("")) {
            setName(HecateUtil.formatNumberToScoreboardString(position) + ChatColor.RESET);
        }
        return name;
    }

    public HecateSidebarLine setSuffix(String suffix){
        if (!this.suffix.equals(suffix)){
            if (suffix.length() > 16){
                suffix = suffix.substring(0, 16);
            }
            this.suffix = suffix;
            isUpdated = true;
        }
        return this;
    }

    public void blank(){
        setPrefix("");
        setName("");
        setSuffix("");
    }

    public boolean isBlank(){
        return prefix.equals("") && name.equals("") && suffix.equals("");
    }

    private static final String STRAIGHT_LINE = "------------";

    public void spacer(){
        setPrefix(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH.toString() + STRAIGHT_LINE);
        setName(HecateUtil.formatNumberToScoreboardString(position) + ChatColor.RESET);
        setSuffix(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH.toString() + STRAIGHT_LINE.substring(0, 7));
    }

}
