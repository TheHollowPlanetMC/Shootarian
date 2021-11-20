package be4rjp.shootarian.weapon.gadget.runnable;

import be4rjp.shootarian.player.ShootarianPlayer;
import be4rjp.shootarian.util.RayTrace;
import be4rjp.shootarian.weapon.gadget.GadgetStatusData;
import net.minecraft.server.v1_15_R1.EntitySilverfish;
import net.minecraft.server.v1_15_R1.PacketPlayOutEntityDestroy;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Set;

public class GrappleRunnable extends BukkitRunnable {
    
    private final ShootarianPlayer shootarianPlayer;
    private final Player player;
    private final Location to;
    private final EntitySilverfish silverfish;
    private final GadgetStatusData gadgetStatusData;
    
    private final Set<ShootarianPlayer> shootarianPlayers;
    
    private int i = 1;
    
    public GrappleRunnable(ShootarianPlayer shootarianPlayer, Player player, Location to, EntitySilverfish silverfish, Set<ShootarianPlayer> shootarianPlayers, GadgetStatusData gadgetStatusData){
        this.shootarianPlayer = shootarianPlayer;
        this.player = player;
        this.to = to;
        this.silverfish = silverfish;
        this.shootarianPlayers = shootarianPlayers;
        this.gadgetStatusData = gadgetStatusData;
        
        player.setFallDistance(0);
        
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_BLAZE_HURT, 5F, 2F);
    }
    
    @Override
    public void run() {
        if(!player.isOnline()) stop();
        
        if(player.isSneaking()) stop();
        
        Location playerLoc = player.getEyeLocation();
        
        double distance = playerLoc.distance(to);
        
        if(distance < 3.0 || distance > 60.0) stop();
        
        Vector toVector = new Vector(to.getX() - playerLoc.getX(), to.getY() - playerLoc.getY(), to.getZ() - playerLoc.getZ()).normalize().multiply(0.3);
        
        RayTrace rayTrace = new RayTrace(playerLoc.toVector(), toVector);
        ArrayList<Vector> positions = rayTrace.traverse(playerLoc.distance(to), 1);
        for (Vector vector : positions) {
            Location position = vector.toLocation(player.getWorld());
            if (!position.getBlock().getType().toString().endsWith("AIR")) {
                stop();
                return;
            }
        }
        
        Vector direction = player.getEyeLocation().getDirection().multiply(1.3);
        
        if(toVector.angle(direction) > Math.toRadians(90)) stop();
        
        double speedRate = (0.4 * ((double)i / 20.0));
        Vector flyVector = player.getVelocity().multiply(1.3).add(toVector).add(direction).multiply(Math.min(speedRate, 1.2));
        
        if(flyVector.lengthSquared() > 30.0) stop();
        if(flyVector.lengthSquared() > 2.25){
            flyVector.normalize().multiply(1.5);
        }
        
        player.setVelocity(flyVector);
        player.getWorld().playSound(playerLoc, Sound.BLOCK_NOTE_BLOCK_HAT, 0.8F, 1F);
        
        i++;
    }
    
    
    public void stop(){
        this.cancel();
        player.setFallDistance(0);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_BLAZE_HURT, 5F, 2F);
        gadgetStatusData.setCoolTime(0);
    
        PacketPlayOutEntityDestroy destroy = new PacketPlayOutEntityDestroy(silverfish.getId());
        shootarianPlayers.forEach(shootarianPlayer1 -> shootarianPlayer1.sendPacket(destroy));
    }
}
