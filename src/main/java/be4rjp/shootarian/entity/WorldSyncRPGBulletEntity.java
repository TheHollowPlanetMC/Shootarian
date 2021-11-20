package be4rjp.shootarian.entity;

import be4rjp.shootarian.match.Match;
import be4rjp.shootarian.match.team.ShootarianTeam;
import be4rjp.shootarian.player.ShootarianPlayer;
import be4rjp.shootarian.player.death.PlayerDeathManager;
import be4rjp.shootarian.util.*;
import be4rjp.shootarian.util.particle.NormalParticle;
import be4rjp.shootarian.weapon.ShootarianWeapon;
import be4rjp.shootarian.weapon.gadget.GadgetWeapon;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Set;

public class WorldSyncRPGBulletEntity implements ShootarianEntity {
    
    private static final ShootarianSound SOUND1 = new ShootarianSound(Sound.ENTITY_PIG_STEP, 1.5F, 0.8F);
    private static final ShootarianSound SOUND2 = new ShootarianSound(Sound.ENTITY_PIG_STEP, 1.5F, 1.1F);
    private static final ShootarianSound SOUND3 = new ShootarianSound(Sound.ENTITY_FIREWORK_ROCKET_BLAST, 0.8F, 0.8F);
    private static final ShootarianSound SOUND4 = new ShootarianSound(Sound.BLOCK_FIRE_EXTINGUISH, 1.1F, 0.9F);
    private static final ShootarianSound SOUND5 = new ShootarianSound(Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 0.8F, 0.55F);
    

    private final Match match;
    private final ShootarianTeam team;
    private Location location;
    private final GadgetWeapon gadgetWeapon;

    private ShootarianPlayer shooter;
    private Vector direction = new Vector(0, 0, 0);
    private Vector originalDirection = new Vector(0.0, 0.0, 0.0);
    private boolean hitSound = false;
    private boolean hitParticle = false;
    private double bulletSize = 0.2;
    private boolean particle = true;

    private int tick = 0;
    private int fallTick = 0;

    private boolean remove = false;
    private int removeTick = 0;
    private boolean isDead = false;
    
    private double G = -2.5;

    private Set<ShootarianPlayer> showPlayer = new HashSet<>();

    public WorldSyncRPGBulletEntity(ShootarianTeam team, Location location, GadgetWeapon gadgetWeapon){
        this.team = team;
        this.match = team.getMatch();
        this.location = location.clone();
        this.gadgetWeapon = gadgetWeapon;
    }
    
    public void shootInitialize(ShootarianPlayer shooter, Vector direction, int fallTick){
        this.direction = direction.clone();
        this.originalDirection = direction.clone();
        this.shooter = shooter;
        this.fallTick = fallTick;
    }

    public ShootarianPlayer getShooter(){return this.shooter;}

    public boolean isHitParticle() {return hitParticle;}

    public boolean isHitSound() {return hitSound;}

    public void setHitParticle(boolean hitParticle) {this.hitParticle = hitParticle;}

    public void setHitSound(boolean hitSound) {this.hitSound = hitSound;}

    public GadgetWeapon getGadgetWeapon() {return gadgetWeapon;}

    public double getBulletSize() {return bulletSize;}

    public void setBulletSize(double bulletSize) {this.bulletSize = bulletSize;}

    public void setParticle(boolean particle) {this.particle = particle;}
    
    public void setG(double g) {G = g;}
    
    @Override
    public void tick() {

        if(tick >= 2000 || location.getY() < 0 || (remove && removeTick == 1)){
            remove();
            return;
        }
        if(remove) removeTick++;
        
        //Sound
        if(tick == 0){
            match.playSound(SOUND1, location);
            match.playSound(SOUND2, location);
            match.playSound(SOUND3, location);
    
            match.spawnParticle(new NormalParticle(Particle.SMOKE_LARGE, 5, 0.5, 0.5, 0.5, 1), location);
        }
        if(tick == 1){
            match.playSound(SOUND4, location);
            match.playSound(SOUND5, location);
        }

        
        Location oldLocation = location.clone();
        Vector oldDirection = direction.clone();

        Location hitLocation = null;
        RayTraceResult rayTraceResult = location.getWorld().rayTrace(oldLocation, oldDirection, oldDirection.length(),
                FluidCollisionMode.NEVER, true, bulletSize, entity -> {
                    if(!(entity instanceof LivingEntity)) return false;
                    if(entity == shooter.getBukkitPlayer()) return false;
                    return !PlayerDeathManager.deathPlayer.contains(entity);
                });
        if(rayTraceResult != null){
        
            if(rayTraceResult.getHitBlock() != null || rayTraceResult.getHitEntity() != null){
            
                hitLocation = rayTraceResult.getHitPosition().toLocation(location.getWorld());
            
                remove = true;
            }
        }

        if(tick >= fallTick) {
            double t = ((double) tick / 20.0);
            direction = originalDirection.clone().add(new Vector(0.0, t * t * G, 0.0));
        }
        location.add(direction);

        if(particle){
            match.spawnParticle(new NormalParticle(Particle.FLAME, 0, 0.0, 0.0, 0.0, 1), location);
            match.spawnParticle(new NormalParticle(Particle.CAMPFIRE_COSY_SMOKE, 0, 0.0, 0.0, 0.0, 1), location);
            match.spawnParticle(new NormalParticle(Particle.CAMPFIRE_SIGNAL_SMOKE, 0, 0.0, 0.0, 0.0, 1), location);
        }
        
        if(hitLocation != null){
    
            ShootarianWeapon.createExplosion(shooter, gadgetWeapon, hitLocation, 5.0, 0.5);
            this.remove();
            
        }

        tick++;
    }

    @Override
    public int getEntityID() {
        return 0;
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
        //this.tick();
        match.getShootarianEntities().add(this);
    }

    @Override
    public void remove() {
        this.isDead = true;
        match.getShootarianEntities().remove(this);
    }

    @Override
    public boolean isDead() {
        return this.isDead;
    }
}
