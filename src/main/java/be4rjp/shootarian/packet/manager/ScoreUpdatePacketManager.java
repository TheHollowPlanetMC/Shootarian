package be4rjp.shootarian.packet.manager;

import be4rjp.shootarian.player.ShootarianPlayer;
import be4rjp.shootarian.util.ShootarianScoreboard;
import net.minecraft.server.v1_15_R1.PacketPlayOutScoreboardScore;
import net.minecraft.server.v1_15_R1.ScoreboardServer;

import java.lang.reflect.Field;

public class ScoreUpdatePacketManager {
    
    private static Field a;
    private static Field d;
    
    static {
        try{
            a = PacketPlayOutScoreboardScore.class.getDeclaredField("a");
            d = PacketPlayOutScoreboardScore.class.getDeclaredField("d");
            a.setAccessible(true);
            d.setAccessible(true);
        }catch (Exception e){e.printStackTrace();}
    }
    
    
    public static boolean write(PacketPlayOutScoreboardScore scorePacket, ShootarianPlayer shootarianPlayer){
        try {
            if(d.get(scorePacket) == ScoreboardServer.Action.REMOVE){
                return true;
            }
            
            String line = (String) a.get(scorePacket);
            
            if(shootarianPlayer.getScoreBoard() == null) return true;
            if(!line.contains("ShootarianSB")) return true;
            
            ShootarianScoreboard scoreboard = shootarianPlayer.getScoreBoard();
            int index = Integer.parseInt(line.replace("ShootarianSB", ""));
            
            String newLine = scoreboard.getSidebarLine(shootarianPlayer, index);
            if(newLine == null) return false;
            
            a.set(scorePacket, newLine);
            return true;
        }catch (Exception e){e.printStackTrace();}
        return true;
    }
    
}
