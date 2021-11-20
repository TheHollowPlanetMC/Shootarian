package be4rjp.shootarian.packet.manager;

import be4rjp.shootarian.player.ShootarianPlayer;
import net.minecraft.server.v1_15_R1.PacketPlayOutAbilities;

import java.lang.reflect.Field;

public class PlayerAbilityPacketManager {
    private static Field f;
    
    static {
        try{
            f = PacketPlayOutAbilities.class.getDeclaredField("f");
            f.setAccessible(true);
        }catch (Exception e){e.printStackTrace();}
    }
    
    public static void write(PacketPlayOutAbilities packet, ShootarianPlayer shootarianPlayer){
        try {
            f.set(packet, shootarianPlayer.getFOV());
        }catch (Exception e){e.printStackTrace();}
    }
}
