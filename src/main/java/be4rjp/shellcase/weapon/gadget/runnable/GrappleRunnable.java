package be4rjp.shellcase.weapon.gadget.runnable;

import be4rjp.shellcase.player.ShellCasePlayer;
import be4rjp.shellcase.util.RayTrace;
import be4rjp.shellcase.weapon.gadget.GadgetStatusData;
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
    
    private final ShellCasePlayer shellCasePlayer;
    private final Player player;
    private final Location to;
    private final EntitySilverfish silverfish;
    private final GadgetStatusData gadgetStatusData;
    
    private final Set<ShellCasePlayer> shellCasePlayers;
    
    private int i = 1;
    
    public GrappleRunnable(ShellCasePlayer shellCasePlayer, Player player, Location to, EntitySilverfish silverfish, Set<ShellCasePlayer> shellCasePlayers, GadgetStatusData gadgetStatusData){
        this.shellCasePlayer = shellCasePlayer;
        this.player = player;
        this.to = to;
        this.silverfish = silverfish;
        this.shellCasePlayers = shellCasePlayers;
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
        shellCasePlayers.forEach(shellCasePlayer1 -> shellCasePlayer1.sendPacket(destroy));
    }
}
