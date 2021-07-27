package be4rjp.shellcase.util.particle;

import be4rjp.shellcase.util.LocationUtil;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

public class NormalParticle extends ShellCaseParticle{
    public NormalParticle(Particle particle, int count, double x_offset, double y_offset, double z_offset, double extra) {
        super(particle, count, x_offset, y_offset, z_offset, extra);
    }
    
    @Override
    public void spawn(Player player, Location location) {
        if(LocationUtil.distanceSquaredSafeDifferentWorld(player.getLocation(), location) > PARTICLE_DRAW_DISTANCE_SQUARE) return;
        player.spawnParticle(particle, location, count, x_offset, y_offset, z_offset, extra);
    }
}
