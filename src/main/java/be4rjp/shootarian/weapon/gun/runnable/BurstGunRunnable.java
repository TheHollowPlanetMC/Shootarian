package be4rjp.shootarian.weapon.gun.runnable;

import be4rjp.shootarian.entity.WorldSyncBulletEntity;
import be4rjp.shootarian.match.team.ShootarianTeam;
import be4rjp.shootarian.player.ShootarianPlayer;
import be4rjp.shootarian.scheduler.MultiThreadRunnable;
import be4rjp.shootarian.weapon.gun.GunStatusData;
import be4rjp.shootarian.weapon.gun.GunWeapon;
import be4rjp.shootarian.weapon.gun.SemiAutoGun;
import org.bukkit.util.Vector;

public class BurstGunRunnable extends MultiThreadRunnable{
    
    private final ShootarianPlayer shootarianPlayer;
    private final GunStatusData gunStatusData;
    
    private int tick = 0;
    
    private int i = 0;
    
    private final int shootTick;
    
    public BurstGunRunnable(ShootarianPlayer shootarianPlayer, GunStatusData gunStatusData, int shootTick){
        this.shootarianPlayer = shootarianPlayer;
        this.gunStatusData = gunStatusData;
        this.shootTick = shootTick;
    }
    
    @Override
    public void run() {
        
        if(i % shootTick != 0){
            i++;
            return;
        }
        i++;
    
        ShootarianTeam shootarianTeam = shootarianPlayer.getShootarianTeam();
        if(shootarianTeam == null){
            cancel();
            return;
        }
    
        SemiAutoGun burstGun = (SemiAutoGun) gunStatusData.getGunWeapon();
        
        if(tick == burstGun.getBurstBullets()){
            cancel();
            return;
        }
        
        if(gunStatusData.consumeBullets(1)){
    
            Vector direction = shootarianPlayer.getEyeLocation().getDirection();
            
            if(shootarianPlayer.isADS()){
                gunStatusData.getAdsRecoil().sendRecoil(shootarianPlayer, gunStatusData.getMaxBullets() - gunStatusData.getBullets());
            } else {
                gunStatusData.getNormalRecoil().sendRecoil(shootarianPlayer, gunStatusData.getMaxBullets() - gunStatusData.getBullets());
                
                GunWeapon.HipShootingRecoil hipShootingRecoil = burstGun.getHipShootingRecoil();
                long noClickTick = (System.currentTimeMillis() - gunStatusData.getLastClickTime()) / 50L;
                gunStatusData.setLastClickTime(System.currentTimeMillis());
    
                if(noClickTick >= hipShootingRecoil.getResetTick()){
                    gunStatusData.setClickTick(0);
                }else{
                    gunStatusData.setClickTick(gunStatusData.getClickTick() + burstGun.getShootTick());
                }
                double range = hipShootingRecoil.getShootRandomRange((int) gunStatusData.getClickTick());
                Vector randomVector = new Vector(Math.random() * range - range / 2, Math.random() * range - range / 2, Math.random() * range - range / 2);
                
                direction.add(randomVector);
            }

            WorldSyncBulletEntity worldSyncBulletEntity = new WorldSyncBulletEntity(shootarianPlayer.getShootarianTeam(), shootarianPlayer.getEyeLocation(), burstGun);
            worldSyncBulletEntity.shootInitialize(shootarianPlayer, direction.multiply(burstGun.getShootSpeed()), burstGun.getFallTick());
            MultiThreadRunnable.addMainThreadTask(worldSyncBulletEntity::spawn);
            
            for(ShootarianPlayer matchPlayer : shootarianTeam.getMatch().getPlayers()) {
                if(matchPlayer == shootarianPlayer){
                    matchPlayer.playSound(burstGun.getShootSound());
                }else {
                    matchPlayer.playSound(burstGun.getShootSound(), shootarianPlayer.getEyeLocation());
                }
            }
            
            gunStatusData.updateDisplayName(shootarianPlayer);
            
        }else{
            gunStatusData.reload();
        }
        
        
        tick++;
    }
}
