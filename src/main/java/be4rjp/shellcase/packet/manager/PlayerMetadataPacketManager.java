package be4rjp.shellcase.packet.manager;

import be4rjp.shellcase.match.team.ShellCaseTeam;
import be4rjp.shellcase.player.ShellCasePlayer;
import net.minecraft.server.v1_15_R1.DataWatcher;
import net.minecraft.server.v1_15_R1.PacketPlayOutEntityMetadata;

import java.lang.reflect.Field;
import java.util.List;

public class PlayerMetadataPacketManager {
    
    private static Field a;
    private static Field b;
    
    static {
        try{
            a = PacketPlayOutEntityMetadata.class.getDeclaredField("a");
            b = PacketPlayOutEntityMetadata.class.getDeclaredField("b");
            a.setAccessible(true);
            b.setAccessible(true);
        }catch (Exception e){e.printStackTrace();}
    }
    
    
    public static void write(PacketPlayOutEntityMetadata packet, ShellCasePlayer shellCasePlayer){
        try {
            
            ShellCaseTeam ShellCaseTeam = shellCasePlayer.getShellCaseTeam();
            if(ShellCaseTeam == null) return;
    
            int entityID = a.getInt(packet);
            ShellCasePlayer op = null;
            for (ShellCasePlayer matchPlayer : ShellCaseTeam.getMatch().getPlayers()){
                if(matchPlayer.getEntityID() == entityID){
                    op = matchPlayer;
                    break;
                }
            }
            if(op == null) return;
            
            List<DataWatcher.Item<?>> dataWatcherItems = (List<DataWatcher.Item<?>>) b.get(packet);
            for(DataWatcher.Item<?> item : dataWatcherItems){
                if(item.b() instanceof Byte) {
                    if (item.a().a() == 0) {
                        DataWatcher.Item<Byte> byteItem = (DataWatcher.Item<Byte>) item;
                        byte original = byteItem.b();
                        if (op.isInvisible()) original = (byte) (original | 0x20);
                        byteItem.a(original);
                    }
                }
    
                if(item.b() instanceof Float) {
                    if (item.a().a() == 8) {
                        DataWatcher.Item<Float> floatItem = (DataWatcher.Item<Float>) item;
                        floatItem.a(op.getHealth());
                    }
                }
            }
            
        }catch (Exception e){e.printStackTrace();}
    }
}
