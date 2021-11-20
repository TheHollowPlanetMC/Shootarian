package be4rjp.shootarian.util.particle;

import be4rjp.shootarian.util.LocationUtil;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

public class NormalParticle extends ShootarianParticle {
    public NormalParticle(Particle particle, int count, double x_offset, double y_offset, double z_offset, double extra) {
        super(particle, count, x_offset, y_offset, z_offset, extra);
    }
    
    @Override
    public void spawn(Player player, Location location) {
        if(LocationUtil.distanceSquaredSafeDifferentWorld(player.getLocation(), location) > PARTICLE_DRAW_DISTANCE_SQUARE) return;
        player.spawnParticle(particle, location, count, x_offset, y_offset, z_offset, extra);
    }
    
    @Override
    public void spawnIgnoreRange(Player player, Location location) {
        player.spawnParticle(particle, location, count, x_offset, y_offset, z_offset, extra);
    }
}
