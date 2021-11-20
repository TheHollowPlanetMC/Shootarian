package be4rjp.shootarian.entity;

import be4rjp.shootarian.match.Match;
import be4rjp.shootarian.player.ShootarianPlayer;
import be4rjp.shootarian.util.LocationUtil;
import net.minecraft.server.v1_15_R1.*;
import org.bukkit.Location;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_15_R1.block.data.CraftBlockData;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Set;

public class AsyncFallingBlock implements ShootarianEntity {
    
    private final Match match;
    private final EntityFallingBlock entityFallingBlock;
    private final Set<ShootarianPlayer> showPlayer = new HashSet<>();
    
    public AsyncFallingBlock(Match match, Location location, BlockData blockData){
        this.match = match;
        this.location = location;
        this.entityFallingBlock = new EntityFallingBlock(((CraftWorld)location.getWorld()).getHandle(), location.getX() + 0.5, location.getY(), location.getZ() + 0.5, ((CraftBlockData)blockData).getState());
    }

    private Vector velocity = null;

    public void setVelocity(Vector velocity) {this.velocity = velocity;}

    private final Location location;
    private int tick = 0;
    private boolean isDead = false;
    
    @Override
    public void tick() {
        if(tick == 80){
            this.remove();
        }
        
        tick++;
    }
    
    @Override
    public int getEntityID() {
        return entityFallingBlock.getId();
    }
    
    @Override
    public void spawn() {
        for(ShootarianPlayer ShootarianPlayer : match.getPlayers()) {
            Player player = ShootarianPlayer.getBukkitPlayer();
            if (player == null) continue;
        
            Location playerLoc = ShootarianPlayer.getLocation();
            if (LocationUtil.distanceSquaredSafeDifferentWorld(playerLoc, location) > ENTITY_DRAW_DISTANCE_SQUARE) continue;
        
            showPlayer.add(ShootarianPlayer);
        }
        match.getAsyncEntities().add(this);
    
        double range = 0.1;
        if(velocity == null) entityFallingBlock.setMot(new Vec3D(Math.random() * range - range / 2.0, 0.0, Math.random() * range - range / 2.0));
        else entityFallingBlock.setMot(new Vec3D(velocity.getX(), velocity.getY(), velocity.getZ()));
        PacketPlayOutSpawnEntity spawn = new PacketPlayOutSpawnEntity(entityFallingBlock, Block.getCombinedId(entityFallingBlock.getBlock()));
        PacketPlayOutEntityVelocity velocity = new PacketPlayOutEntityVelocity(entityFallingBlock);
        showPlayer.forEach(shootarianPlayer -> shootarianPlayer.sendPacket(spawn));
        showPlayer.forEach(shootarianPlayer -> shootarianPlayer.sendPacket(velocity));
    }
    
    @Override
    public void remove() {
        isDead = true;
        
        PacketPlayOutEntityDestroy destroy = new PacketPlayOutEntityDestroy(entityFallingBlock.getId());
        showPlayer.forEach(shootarianPlayer -> shootarianPlayer.sendPacket(destroy));

        match.getAsyncEntities().remove(this);
    }
    
    @Override
    public boolean isDead() {
        return isDead;
    }
}
