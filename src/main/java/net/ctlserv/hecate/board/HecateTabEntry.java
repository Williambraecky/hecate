package net.ctlserv.hecate.board;

import com.google.common.base.Preconditions;
import lombok.Getter;
import lombok.Setter;
import net.ctlserv.hecate.util.HecateUtil;
import net.minecraft.server.v1_7_R4.PacketPlayOutPlayerInfo;
import net.minecraft.util.com.mojang.authlib.GameProfile;
import net.minecraft.util.com.mojang.authlib.properties.Property;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import java.lang.reflect.Field;
import java.util.UUID;

/**
 * Created by Williambraecky on 25-05-17.
 */
@Getter
@Setter
public class HecateTabEntry {

    //Blank skin for 1.8 not sure if this will work
    private static Property p = new Property("textures",
            "eyJ0aW1lc3RhbXAiOjE0OTEzMDQ5NTE0NDIsInByb2ZpbGVJZCI6IjljMmZhMGMzOWFlYjRiZjI5MzQxOGZlMzM2MTlhZmRmIiwicHJvZmlsZU5hbWUiOiJXaWxsaWFtYnJhZWNreSIsInRleHR1cmVzIjp7IlNLSU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS80YWNjZjYzNjQ1OWM5ZGE0OGQ1M2JlYzE1ZWI2NzJhMTcxYTU4ZTFkZjk3ZDI2NTU2MjVkNjM4NDQyMmE4In19fQ==",
            "bjcROhHGcCh5c1uXz6NDgfTcHDerEc/StvX4fhNZJsOmbyIGWtVOYIcXBwo6KHyyVP29okUKb3k70afbUwmQlYuFzIORV6eG92RQSdNVRdVLeBIzIELlMNmWdHeDkXVwc2VoqzzShUtG7A4RfvAXQyigVD+hrCnr0862AO047m7iNdTCuhTNCTUyt4llA58HCn88wOEAMkB0Piguo/zbJEPKTH3f3BeyeNEPP8ylfIdHPQ0FjH/bJz2cz/VEppMVe13iEI7EJNf+Fxz49NQT2n7T9tegELf1AT8tmpgK/8iXVclkMO3tZ0phNGYz6SGTFYhQUOxXWbWm1Fgq1gpJdzBuXOABaspojGnn87I4RIA5juKHltCnSmOV5FszxIAmt8Zc/8VmsggaURJrFo2yhOWQBpAuNLU2ewYFkXEmV0aBIxV1kVjRjs7DatrMUHOR+//uTNEddfRdCjEGsHWjeGhGU0ANOQ6JUUG9eYbZpdWme7/KaZW2uqma7kCgO6Pe+5bLjTVq0fUGBUj7bMtr/Yl1NU6MOMJiCGn/rG2QSlOGmoQEAAwCWIYBYsMo8psfzbxuaXTZ+Pdar34gRn2lCCoX7DXtgGWXBMlQEplBXbzPTOzFwAy5vfo6EcRy+F7d0Xby0F82FmC2X/BYw7x7kkU3fd5MoPCBtBxlnic4TDA=");

    private String name;

    private UUID uuid; //Only used once

    private Team team;

    private HecateTabEntry(String name, UUID uuid) {
        this.setName(name);
        this.setUuid(uuid);
    }

    HecateTabEntry(int position, UUID uuid) {
        this(HecateUtil.formatNumberToScoreboardString(position), uuid);
    }

    public HecateTabEntry setPrefix(String s) {
        if (!s.startsWith(getName())) {
            s = getName() + s;
        }
        s = s.substring(0, Math.min(s.length(), 16));
        team.setPrefix(s);
        return this;
    }

    public HecateTabEntry setSuffix(String s) {
        s = s.substring(0, Math.min(s.length(), 16));
        team.setSuffix(s);
        return this;
    }

    public void setText(String text) {
        Preconditions.checkArgument(text.length() <= 26, "Text for tab entry to long.");
        if (text.length() > 12) {
            String prefix = text.substring(0, 12);
            setPrefix(prefix);
            String suffix = text.substring(12, text.length());
            if (!suffix.startsWith("§")) {
                suffix = ChatColor.getLastColors(prefix) + suffix;
            }
            setSuffix(suffix);
        } else {
            setPrefix(text);
        }
    }

    void setTeam(Team team) {
        if (this.team != null && this.team.hasEntry(getName())) {
            this.team.removeEntry(getName());
        }
        team.addEntry(getName());
        this.team = team;
    }

    private GameProfile createProfile() {
        GameProfile profile = new GameProfile(uuid, name);
        profile.getProperties().put("textures", p);
        this.uuid = null;
        return profile;
    }

    public void send(Player p) {
        //We will need reflection because default spigot values are private
        PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo();
        try {
            Field action = packet.getClass().getDeclaredField("action");
            action.setAccessible(true);
            action.set(packet, 0);

            Field username = packet.getClass().getDeclaredField("username");
            username.setAccessible(true);
            username.set(packet, name);

            Field gamemode = packet.getClass().getDeclaredField("gamemode");
            gamemode.setAccessible(true);
            gamemode.set(packet, GameMode.SURVIVAL.getValue());

            Field ping = packet.getClass().getDeclaredField("ping");
            ping.setAccessible(true);
            ping.set(packet, 0);

            Field player = packet.getClass().getDeclaredField("player");
            player.setAccessible(true);
            player.set(packet, createProfile());
            ((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
    }
}
