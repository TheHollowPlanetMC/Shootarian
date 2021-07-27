package be4rjp.shellcase.packet.manager;

import be4rjp.shellcase.match.team.ShellCaseTeam;
import be4rjp.shellcase.player.ObservableOption;
import be4rjp.shellcase.player.ShellCasePlayer;
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
    
    
    public static boolean write(PacketPlayOutNamedEntitySpawn spawnPacket, ShellCasePlayer shellCasePlayer){
        try {

            if(shellCasePlayer.getObservableOption() == ObservableOption.ALL_PLAYER) return true;

            UUID uuid = (UUID) b.get(spawnPacket);
            if(uuid == null) return true;
            if(!ShellCasePlayer.isCreated(uuid.toString())) return true;

            ShellCaseTeam shellCaseTeam = shellCasePlayer.getShellCaseTeam();
            if(shellCaseTeam == null){
                return shellCasePlayer.getObservableOption() != ObservableOption.ALONE;
            }

            ShellCasePlayer op = ShellCasePlayer.getShellCasePlayer(uuid.toString());
            ShellCaseTeam otherTeam = op.getShellCaseTeam();
            if(otherTeam == null) return false;
            
            switch (shellCasePlayer.getObservableOption()){
                case ONLY_MATCH_PLAYER:{
                    return shellCaseTeam.getMatch() == otherTeam.getMatch();
                }
                
                case ALONE:{
                    return false;
                }
            }
        }catch (Exception e){e.printStackTrace();}
        return true;
    }
}
