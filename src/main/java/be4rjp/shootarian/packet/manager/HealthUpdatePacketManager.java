package be4rjp.shootarian.packet.manager;

import be4rjp.shootarian.player.ShootarianPlayer;
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
    
    public static void write(PacketPlayOutUpdateHealth packet, ShootarianPlayer shootarianPlayer){
        try {
            a.set(packet, shootarianPlayer.getHealth());
            b.set(packet, shootarianPlayer.getFoodLevel());
        }catch (Exception e){e.printStackTrace();}
    }
    
}
