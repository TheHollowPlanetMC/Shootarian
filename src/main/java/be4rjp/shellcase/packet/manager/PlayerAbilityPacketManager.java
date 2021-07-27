package be4rjp.shellcase.packet.manager;

import be4rjp.shellcase.player.ShellCasePlayer;
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
    
    public static void write(PacketPlayOutAbilities packet, ShellCasePlayer shellCasePlayer){
        try {
            f.set(packet, shellCasePlayer.getFOV());
        }catch (Exception e){e.printStackTrace();}
    }
}
