package be4rjp.shootarian.packet.manager;

import be4rjp.shootarian.match.team.ShootarianTeam;
import be4rjp.shootarian.player.ObservableOption;
import be4rjp.shootarian.player.ShootarianPlayer;
import net.minecraft.server.v1_15_R1.PacketPlayOutNamedEntitySpawn;

import java.lang.reflect.Field;
import java.util.UUID;

public class PlayerSpawnPacketManager {
    private static Field b;
    
    static {
        try{
            b = PacketPlayOutNamedEntitySpawn.class.getDeclaredField("b");
            b.setAccessible(true);
        }catch (Exception e){e.printStackTrace();}
    }
    
    
    public static boolean write(PacketPlayOutNamedEntitySpawn spawnPacket, ShootarianPlayer shootarianPlayer){
        try {

            if(shootarianPlayer.getObservableOption() == ObservableOption.ALL_PLAYER) return true;

            UUID uuid = (UUID) b.get(spawnPacket);
            if(uuid == null) return true;
            if(!ShootarianPlayer.isCreated(uuid)) return true;

            ShootarianTeam shootarianTeam = shootarianPlayer.getShootarianTeam();
            if(shootarianTeam == null){
                return shootarianPlayer.getObservableOption() != ObservableOption.ALONE;
            }

            ShootarianPlayer op = ShootarianPlayer.getShootarianPlayer(uuid);
            ShootarianTeam otherTeam = op.getShootarianTeam();
            if(otherTeam == null) return false;
            
            switch (shootarianPlayer.getObservableOption()){
                case ONLY_MATCH_PLAYER:{
                    return shootarianTeam.getMatch() == otherTeam.getMatch();
                }
                
                case ALONE:{
                    return false;
                }
            }
        }catch (Exception e){e.printStackTrace();}
        return true;
    }
}
