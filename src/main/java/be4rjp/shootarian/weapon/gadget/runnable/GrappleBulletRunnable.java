package be4rjp.shootarian.weapon.gadget.runnable;

import be4rjp.shootarian.Shootarian;
import be4rjp.shootarian.entity.ShootarianEntity;
import be4rjp.shootarian.player.ShootarianPlayer;
import be4rjp.shootarian.util.RayTrace;
import be4rjp.shootarian.util.ShootarianSound;
import be4rjp.shootarian.weapon.gadget.GadgetStatusData;
import net.minecraft.server.v1_15_R1.*;
import org.bukkit.*;
import org.bukkit.Particle;
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Set;

public class GrappleBulletRunnable extends BukkitRunnable {
    
    private final ShootarianPlayer shootarianPlayer;
    private final GadgetStatusData gadgetStatusData;
    private final Player player;
    private final Vector direction;
    
    private int tick = 0;
    
    private RayTrace rayTrace;
    private ArrayList<Vector> positions;
    
    public GrappleBulletRunnable(ShootarianPlayer shootarianPlayer, Player player, GadgetStatusData gadgetStatusData){
        this.shootarianPlayer = shootarianPlayer;
        this.player = player;
        this.gadgetStatusData = gadgetStatusData;
        this.direction = player.getEyeLocation().getDirection();
        
        this.rayTrace = new RayTrace(player.getEyeLocation().toVector(), player.getEyeLocation().getDirection());
        this.positions = rayTrace.traverse(40.0, 3);
        
        player.getWorld().playSound(player.getLocation(), Sound.ITEM_CROSSBOW_SHOOT, 1F, 1F);
    }
    
    @Override
    public void run(){
        
        if(!player.isOnline()){
            stop();
            return;
        }
        
        if(tick < positions.size()){
            Location location = positions.get(tick).toLocation(player.getWorld());
            location.getWorld().spawnParticle(Particle.CRIT, location, 1, 0, 0, 0, 0);
            
            
            RayTrace rayTrace2 = new RayTrace(location.toVector(), direction);
            ArrayList<Vector> positions2 = rayTrace2.traverse(3.0, 0.5);
            
            for(int i = 0; i < positions2.size(); i++) {
                
                Location position = positions2.get(i).toLocation(player.getWorld());
                
                if(!position.getBlock().getType().toString().endsWith("AIR")){
                    position.getWorld().playSound(position, Sound.ITEM_CROSSBOW_HIT, 1F, 1F);
                    
                    try {
                        EntitySilverfish silverfish = new EntitySilverfish(EntityTypes.SILVERFISH, ((CraftWorld) location.getWorld()).getHandle());
                        silverfish.setPositionRotation(position.getX(), position.getY() - 1.0, position.getZ(), 0, 0);
                        silverfish.setInvisible(true);
                        PacketPlayOutSpawnEntityLiving spawnEntityLiving = new PacketPlayOutSpawnEntityLiving(silverfish);
                        PacketPlayOutEntityMetadata metadata = new PacketPlayOutEntityMetadata(silverfish.getId(), silverfish.getDataWatcher(), true);
                        PacketPlayOutAttachEntity attachEntity = new PacketPlayOutAttachEntity(silverfish, ((CraftPlayer) player).getHandle());
    
                        Set<ShootarianPlayer> matchPlayers = shootarianPlayer.getNearPlayer(ShootarianEntity.ENTITY_DRAW_DISTANCE);
                        for(ShootarianPlayer matchPlayer : matchPlayers) {
                            matchPlayer.playSound(new ShootarianSound(Sound.ITEM_CROSSBOW_HIT, 1F, 1F), position);
                            matchPlayer.sendPacket(spawnEntityLiving);
                            matchPlayer.sendPacket(metadata);
                            matchPlayer.sendPacket(attachEntity);
                        }
                        
                        GrappleRunnable runnable = new GrappleRunnable(shootarianPlayer, player, position, silverfish, matchPlayers, gadgetStatusData);
                        runnable.runTaskTimerAsynchronously(Shootarian.getPlugin(), 0, 1);
                    }catch (Exception e){e.printStackTrace();}
                    
                    stop();
                    return;
                }
            }
        }else{
            stop();
        }
        
        tick++;
    }
    
    public void stop(){
        this.cancel();
        gadgetStatusData.setCoolTime(5);
    }
}

