package be4rjp.shellcase.entity;

import be4rjp.shellcase.data.settings.Settings;
import be4rjp.shellcase.match.Match;
import be4rjp.shellcase.match.team.ShellCaseTeam;
import be4rjp.shellcase.player.ShellCasePlayer;
import be4rjp.shellcase.util.*;
import be4rjp.shellcase.util.RayTrace;
import be4rjp.shellcase.util.particle.BlockParticle;
import be4rjp.shellcase.util.particle.NormalParticle;
import be4rjp.shellcase.util.particle.ShellCaseParticle;
import be4rjp.shellcase.weapon.gun.GunWeapon;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Set;

public class AsyncBulletEntity implements ShellCaseEntity {
    
    private final Match match;
    private final ShellCaseTeam team;
    private Location location;
    private final GunWeapon gunWeapon;
    
    private ShellCasePlayer shooter;
    private Vector direction = new Vector(0, 0, 0);
    private Vector originalDirection = new Vector(0.0, 0.0, 0.0);
    private boolean hitSound = false;
    private boolean hitParticle = false;
    private double bulletSize = 0.2;
    private boolean particle = true;
    
    private boolean isSniperBullet = false;
    
    private int tick = 0;
    private int fallTick = 0;
    
    private boolean remove = false;
    private int removeTick = 0;
    private boolean isDead = false;
    
    private Set<ShellCasePlayer> showPlayer = new HashSet<>();
    
    public AsyncBulletEntity(ShellCaseTeam team, Location location, GunWeapon gunWeapon){
        this.team = team;
        this.match = team.getMatch();
        this.location = location.clone();
        this.gunWeapon = gunWeapon;
        
        //this.ORBIT_PARTICLE = new NormalParticle(Particle.)
    }
    
    
    public void shootInitialize(ShellCasePlayer shooter, Vector direction, int fallTick){
        this.direction = direction.clone();
        this.originalDirection = direction.clone();
        this.shooter = shooter;
        this.fallTick = fallTick;
    }
    
    
    public ShellCasePlayer getShooter(){return this.shooter;}
    
    public boolean isHitParticle() {return hitParticle;}
    
    public boolean isHitSound() {return hitSound;}
    
    public void setHitParticle(boolean hitParticle) {this.hitParticle = hitParticle;}
    
    public void setHitSound(boolean hitSound) {this.hitSound = hitSound;}
    
    public GunWeapon getMainWeapon(){return gunWeapon;}
    
    public double getBulletSize() {return bulletSize;}
    
    public void setBulletSize(double bulletSize) {this.bulletSize = bulletSize;}

    public void setParticle(boolean particle) {this.particle = particle;}
    
    public boolean isSniperBullet() {return isSniperBullet;}
    
    public void setSniperBullet(boolean sniperBullet) {isSniperBullet = sniperBullet;}
    
    @Override
    public void tick() {
    
        if(tick >= 2000 || location.getY() < 0 || (remove && removeTick == 1)){
            remove();
            return;
        }
        if(remove) removeTick++;
        
        Location oldLocation = location.clone();
        Vector oldDirection = direction.clone();
        
        Location hitLocation = null;
        if(!remove) {
            
            Location playerHitLocation = null;
            ShellCasePlayer target = null;
            
            RayTrace rayTrace = new RayTrace(oldLocation.toVector(), oldDirection);
            for (ShellCasePlayer shellCasePlayer : match.getPlayers()) {
                Player player = shellCasePlayer.getBukkitPlayer();
                if (player == null) continue;
                if (shellCasePlayer.getShellCaseTeam() == null) continue;
                if (shellCasePlayer.isDeath()) continue;
        
                BoundingBox boundingBox = new BoundingBox(player, bulletSize);
                Vector position = rayTrace.intersects(boundingBox, oldDirection.length(), 0.01);
                if (position == null) continue;
                if (this.team == shellCasePlayer.getShellCaseTeam()) continue;
        
                playerHitLocation = position.toLocation(location.getWorld());
                target = shellCasePlayer;
                
                //プレイヤーへのヒット
                //shellCasePlayer.giveDamage(gunWeapon.getDamage(), shooter, direction, gunWeapon);
                //match.spawnParticle(INK_HIT_PARTICLE, shellCasePlayer.getLocation().add(0.0, 1.0, 0.0));
        
                remove = true;
            }
            
            Location blockHitLocation = null;
            Block hitBlock = null;
            
            try {
                RayTraceResult rayTraceResult = oldLocation.getWorld().rayTraceBlocks(oldLocation, oldDirection, oldDirection.length());
                if (rayTraceResult != null) {
                    blockHitLocation = rayTraceResult.getHitPosition().toLocation(oldLocation.getWorld());
                    hitBlock = rayTraceResult.getHitBlock();
            
                    remove = true;
                }
            } catch (Exception e) {
                remove();
            }
            
            
            boolean playerHit = false;
            boolean blockHit = false;
            if(playerHitLocation != null && blockHitLocation == null) {
                playerHit = true;
            }
            if(playerHitLocation == null && blockHitLocation != null){
                blockHit = true;
            }
            if(playerHitLocation != null && blockHitLocation != null){
                if(LocationUtil.distanceSquaredSafeDifferentWorld(playerHitLocation, location) < LocationUtil.distanceSquaredSafeDifferentWorld(blockHitLocation, location)){
                    playerHit = true;
                }else{
                    blockHit = true;
                }
            }
            
            if(playerHit){
                target.giveDamage(gunWeapon.getDamage(), shooter, direction, gunWeapon);
                hitLocation = playerHitLocation;
            }
            if(blockHit){
                //ブロックへのヒット
                if(hitBlock != null) {
                    match.spawnParticle(new BlockParticle(Particle.BLOCK_CRACK, 3, 0, 0, 0, 1, hitBlock.getBlockData()), blockHitLocation);
                    match.playSound(new ShellCaseSound(hitBlock.getSoundGroup().getBreakSound(), 1.0F, 1.0F), blockHitLocation);
                }
                hitLocation = blockHitLocation;
            }
        }
        
        if(tick >= fallTick) {
            double t = ((double) tick / 20.0);
            direction = originalDirection.clone().add(new Vector(0.0, t * t * -4.9, 0.0));
        }
        location.add(direction);
        
        if(particle){
            ShellCaseParticle particle;
            if(hitLocation == null) {
                particle = new NormalParticle(Particle.CRIT, 0, direction.getX(), direction.getY(), direction.getZ(), 1);
            }else{
                particle = new NormalParticle(Particle.CRIT, 0, hitLocation.getX() - oldLocation.getX(), hitLocation.getY() - oldLocation.getY(), hitLocation.getZ() - oldLocation.getZ(), 1);
            }
            
            if (isSniperBullet) {
                for(ShellCasePlayer matchPlayer : match.getPlayers()){
                    if(matchPlayer == shooter){
                        matchPlayer.spawnParticleIgnoreRange(particle, oldLocation, Settings.SNIPER_BULLET_ORBIT_PARTICLE);
                    }else{
                        matchPlayer.spawnParticle(particle, oldLocation, Settings.BULLET_ORBIT_PARTICLE);
                    }
                }
            } else {
                match.spawnParticle(particle, oldLocation, Settings.BULLET_ORBIT_PARTICLE);
            }
        }
        
        tick++;
    }
    
    @Override
    public int getEntityID() {
        return 0;
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
        this.tick();
        match.getAsyncEntities().add(this);
    }
    
    @Override
    public void remove() {
        this.isDead = true;
        match.getAsyncEntities().remove(this);
    }
    
    @Override
    public boolean isDead() {
        return this.isDead;
    }
}
