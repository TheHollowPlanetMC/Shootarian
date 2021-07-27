package be4rjp.shellcase.packet.manager;

import be4rjp.shellcase.player.ShellCasePlayer;
import net.minecraft.server.v1_15_R1.PacketPlayOutUpdateHealth;

import java.lang.reflect.Field;

public class HealthUpdatePacketManager {
    
    private static Field a;
    private static Field b;
    
    static {
        try{
            a = PacketPlayOutUpdateHealth.class.getDeclaredField("a");
            b = PacketPlayOutUpdateHealth.class.getDeclaredField("b");
            a.setAccessible(true);
            b.setAccessible(true);
        }catch (Exception e){e.printStackTrace();}
    }
    
    public static void write(PacketPlayOutUpdateHealth packet, ShellCasePlayer shellCasePlayer){
        try {
            a.set(packet, shellCasePlayer.getHealth());
            b.set(packet, shellCasePlayer.getFoodLevel());
        }catch (Exception e){e.printStackTrace();}
    }
    
}
