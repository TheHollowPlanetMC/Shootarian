package be4rjp.shellcase.entity;

import be4rjp.shellcase.data.settings.Settings;
import be4rjp.shellcase.match.Match;
import be4rjp.shellcase.match.team.ShellCaseTeam;
import be4rjp.shellcase.player.ShellCasePlayer;
import be4rjp.shellcase.util.*;
import be4rjp.shellcase.util.RayTrace;
import be4rjp.shellcase.util.particle.BlockParticle;
import be4rjp.shellcase.util.particle.ShellCaseParticle;
import be4rjp.shellcase.weapon.main.GunWeapon;
import net.minecraft.server.v1_15_R1.*;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Set;

public class BulletEntity implements ShellCaseEntity {
    
    private static ShellCaseSound INK_HIT_SOUND = new ShellCaseSound(Sound.ENTITY_SLIME_ATTACK, 0.25F, 2.0F);
    
    private final Match match;
    private final ShellCaseTeam team;
    private Location location;
    private final EntitySnowball snowball;
    private final GunWeapon gunWeapon;
    
    private ShellCasePlayer shooter;
    private Vector direction = new Vector(0, 0, 0);
    private boolean hitSound = false;
    private boolean hitParticle = false;
    private final ShellCaseParticle INK_PARTICLE;
    private final ShellCaseParticle INK_HIT_PARTICLE;
    private double bulletSize = 0.2;
    private double gravity = -0.12;
    private boolean particle = true;
    
    private int tick = 0;
    private int fallTick = 0;
    
    private boolean remove = false;
    private int removeTick = 0;
    private boolean isDead = false;
    
    private Set<ShellCasePlayer> showPlayer = new HashSet<>();
    
    public BulletEntity(ShellCaseTeam team, Location location, GunWeapon gunWeapon){
        this.team = team;
        this.match = team.getMatch();
        this.location = location.clone();
        this.gunWeapon = gunWeapon;
    
        WorldServer nmsWorld = ((CraftWorld)location.getWorld()).getHandle();
        this.snowball = new EntitySnowball(nmsWorld, location.getX(), location.getY(), location.getZ());
        this.bulletSize = gunWeapon.getBulletSize();
        
        this.INK_PARTICLE = new BlockParticle(Particle.BLOCK_DUST, 0, 0, -1, 0, 1, team.getShellCaseColor().getWool().createBlockData());
        this.INK_HIT_PARTICLE = new BlockParticle(Particle.BLOCK_DUST, 5, 0.5, 0.5, 0.5, 1, team.getShellCaseColor().getWool().createBlockData());
    }
    
    
    public void shootInitialize(ShellCasePlayer shooter, Vector direction, int fallTick){
        this.direction = direction.clone();
        this.shooter = shooter;
        this.fallTick = fallTick;
        
        if(shooter.getShellCaseTeam() != null){
            this.setItemStack(new ItemStack(shooter.getShellCaseTeam().getShellCaseColor().getWool()));
        }
    }
    
    
    public void setItemStack(ItemStack itemStack){this.snowball.setItem(CraftItemStack.asNMSCopy(itemStack));}
    
    public ShellCasePlayer getShooter(){return this.shooter;}
    
    public boolean isHitParticle() {return hitParticle;}
    
    public boolean isHitSound() {return hitSound;}
    
    public void setHitParticle(boolean hitParticle) {this.hitParticle = hitParticle;}
    
    public void setHitSound(boolean hitSound) {this.hitSound = hitSound;}
    
    public GunWeapon getMainWeapon(){return gunWeapon;}
    
    public double getBulletSize() {return bulletSize;}
    
    public void setBulletSize(double bulletSize) {this.bulletSize = bulletSize;}

    public void setGravity(double gravity) {this.gravity = gravity;}

    public void setParticle(boolean particle) {this.particle = particle;}

    @Override
    public void tick() {
    
        if(tick >= 2000 || location.getY() < 0 || (remove && removeTick == 1)){
            remove();
            return;
        }
        if(remove) removeTick++;
        
        Location oldLocation = location.clone();
        Vector oldDirection = direction.clone();
    
        boolean sendTeleport = tick % 20 == 0 && tick != 0;
        PacketPlayOutEntityTeleport teleport = new PacketPlayOutEntityTeleport(snowball);
        PacketPlayOutEntityVelocity velocity = new PacketPlayOutEntityVelocity(snowball);
        PacketPlayOutSpawnEntity spawnEntity = new PacketPlayOutSpawnEntity(snowball);
        PacketPlayOutEntityMetadata metadata = new PacketPlayOutEntityMetadata(snowball.getId(), snowball.getDataWatcher(), true);
        
        if(tick == 1){
            shooter.sendPacket(spawnEntity);
            shooter.sendPacket(metadata);
        }
        
        for(ShellCasePlayer ShellCasePlayer : showPlayer){
            Player player = ShellCasePlayer.getBukkitPlayer();
            if(player == null) continue;
        
            if(tick == 0 && ShellCasePlayer != shooter){
                ShellCasePlayer.sendPacket(spawnEntity);
                ShellCasePlayer.sendPacket(metadata);
            }
        
            if(sendTeleport) ShellCasePlayer.sendPacket(teleport);
            ShellCasePlayer.sendPacket(velocity);
        }
    
        
        if(!remove) {
            try {
                RayTraceResult rayTraceResult = oldLocation.getWorld().rayTraceBlocks(oldLocation, oldDirection, oldDirection.length());
                if (rayTraceResult != null) {
                    Location hitLocation = rayTraceResult.getHitPosition().toLocation(oldLocation.getWorld());
                    
            
                    //ブロックへのヒット
                    match.playSound(INK_HIT_SOUND, hitLocation);
                    match.spawnParticle(INK_HIT_PARTICLE, hitLocation);
            
                    remove = true;
                }
            } catch (Exception e) {
                remove();
            }
    
            RayTrace rayTrace = new RayTrace(oldLocation.toVector(), oldDirection);
            for (ShellCasePlayer shellCasePlayer : match.getPlayers()) {
                Player player = shellCasePlayer.getBukkitPlayer();
                if (player == null) continue;
                if (shellCasePlayer.getShellCaseTeam() == null) continue;
                if (shellCasePlayer.isDeath()) continue;
        
                BoundingBox boundingBox = new BoundingBox(player, bulletSize);
                if (!rayTrace.intersects(boundingBox, oldDirection.length(), 0.1)) continue;
                if (this.team == shellCasePlayer.getShellCaseTeam()) continue;
                
        
                //プレイヤーへのヒット
                shellCasePlayer.giveDamage(gunWeapon.getDamage(), shooter, direction, gunWeapon);
                match.spawnParticle(INK_HIT_PARTICLE, shellCasePlayer.getLocation().add(0.0, 1.0, 0.0));
        
                remove = true;
            }
        }
        
        
        if(tick == fallTick){
            direction.multiply(direction.length() / 17);
        }
        
        if(tick >= fallTick && tick <= fallTick + 10) {
            //direction.normalize().multiply(0.9);
            //direction.add(new Vector(0, -0.21, 0));
            direction = direction.add(new Vector(0, gravity, 0));
        }
        location.add(direction);
    
        snowball.setPosition(location.getX(), location.getY(), location.getZ());
        snowball.setMot(direction.getX(), direction.getY(), direction.getZ());
        
        if(tick != 0 && !remove && particle) match.spawnParticle(INK_PARTICLE, oldLocation, Settings.INK_ORBIT_PARTICLE);
        
        tick++;
    }
    
    @Override
    public int getEntityID() {
        return snowball.getId();
    }
    
    @Override
    public void spawn() {
        for(ShellCasePlayer ShellCasePlayer : match.getPlayers()) {
            Player player = ShellCasePlayer.getBukkitPlayer();
            if (player == null) continue;
    
            Location playerLoc = ShellCasePlayer.getLocation();
            if (LocationUtil.distanceSquaredSafeDifferentWorld(playerLoc, location) > ENTITY_DRAW_DISTANCE_SQUARE) continue;
            
            showPlayer.add(ShellCasePlayer);
        }
        match.getShellCaseEntities().add(this);
    }
    
    @Override
    public void remove() {
        this.isDead = true;
        
        PacketPlayOutEntityDestroy destroy = new PacketPlayOutEntityDestroy(snowball.getId());
        for(ShellCasePlayer ShellCasePlayer : showPlayer) {
            Player player = ShellCasePlayer.getBukkitPlayer();
            if (player == null) continue;
    
            Location playerLoc = ShellCasePlayer.getLocation();
            if (LocationUtil.distanceSquaredSafeDifferentWorld(playerLoc, location) > ENTITY_DRAW_DISTANCE_SQUARE) continue;
            ShellCasePlayer.sendPacket(destroy);
        }
        
        match.getShellCaseEntities().remove(this);
    }
    
    @Override
    public boolean isDead() {
        return this.isDead;
    }
}
