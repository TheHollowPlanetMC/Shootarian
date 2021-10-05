package be4rjp.shellcase.weapon.gun.runnable;

import be4rjp.shellcase.entity.BulletEntity;
import be4rjp.shellcase.match.team.ShellCaseTeam;
import be4rjp.shellcase.player.ShellCasePlayer;
import be4rjp.shellcase.scheduler.MultiThreadRunnable;
import be4rjp.shellcase.weapon.gun.GunStatusData;
import be4rjp.shellcase.weapon.gun.GunWeapon;
import be4rjp.shellcase.weapon.gun.SemiAutoGun;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class BurstGunRunnable extends MultiThreadRunnable {
    
    private final ShellCasePlayer shellCasePlayer;
    private final GunStatusData gunStatusData;
    
    private int tick = 0;
    
    private int i = 0;
    
    private final int shootTick;
    
    public BurstGunRunnable(ShellCasePlayer shellCasePlayer, GunStatusData gunStatusData, int shootTick){
        this.shellCasePlayer = shellCasePlayer;
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
    
        ShellCaseTeam shellCaseTeam = shellCasePlayer.getShellCaseTeam();
        if(shellCaseTeam == null){
            cancel();
            return;
        }
    
        SemiAutoGun burstGun = (SemiAutoGun) gunStatusData.getGunWeapon();
        
        if(tick == burstGun.getBurstBullets()){
            cancel();
            return;
        }
        
        if(gunStatusData.consumeBullets(1)){
    
            Vector direction = shellCasePlayer.getEyeLocation().getDirection();
            
            if(shellCasePlayer.isADS()){
                gunStatusData.getAdsRecoil().sendRecoil(shellCasePlayer, gunStatusData.getMaxBullets() - gunStatusData.getBullets());
            } else {
                gunStatusData.getNormalRecoil().sendRecoil(shellCasePlayer, gunStatusData.getMaxBullets() - gunStatusData.getBullets());
                
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
    
            BulletEntity bulletEntity = new BulletEntity(shellCasePlayer.getShellCaseTeam(), shellCasePlayer.getEyeLocation(), burstGun);
            bulletEntity.shootInitialize(shellCasePlayer, direction.multiply(burstGun.getShootSpeed()), burstGun.getFallTick());
            bulletEntity.spawn();
            
            for(ShellCasePlayer matchPlayer : shellCaseTeam.getMatch().getPlayers()) {
                if(matchPlayer == shellCasePlayer){
                    matchPlayer.playSound(burstGun.getShootSound());
                }else {
                    matchPlayer.playSound(burstGun.getShootSound(), shellCasePlayer.getEyeLocation());
                }
            }
            
            gunStatusData.updateDisplayName(shellCasePlayer);
            
        }else{
            gunStatusData.reload();
        }
        
        
        tick++;
    }
}
