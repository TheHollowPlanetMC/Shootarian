package be4rjp.shellcase.weapon.gun.runnable;

import be4rjp.shellcase.entity.WorldSyncBulletEntity;
import be4rjp.shellcase.match.team.ShellCaseTeam;
import be4rjp.shellcase.player.ShellCasePlayer;
import be4rjp.shellcase.weapon.gun.GunStatusData;
import be4rjp.shellcase.weapon.gun.FullAutoGun;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class FullAutoGunRunnable extends GunWeaponRunnable {
    
    private final FullAutoGun fullAutoGun;
    private final GunStatusData gunStatusData;
    
    public FullAutoGunRunnable(FullAutoGun fullAutoGun, GunStatusData gunStatusData, ShellCasePlayer shellCasePlayer){
        super(fullAutoGun, shellCasePlayer);
        this.fullAutoGun = fullAutoGun;
        this.gunStatusData = gunStatusData;
    }
    
    private int taskTick = 0;
    private int clickTick = 0;
    private int noClickTick = 0;
    
    private int shootIndex = 0;
    
    private boolean beforeUsing = false;
    
    @Override
    public void run() {
        boolean using = playerTick <= 6;

        if(beforeUsing != using || gunStatusData.isReloading()){
            gunStatusData.resetRecoil();
            shootIndex = 0;
        }
        beforeUsing = using;
        
        if(taskTick % fullAutoGun.getShootTick() == 0){
            if(using && !gunStatusData.isReloading()) {
                Player player = shellCasePlayer.getBukkitPlayer();
                if (player == null) return;
                
                if(gunStatusData.consumeBullets(1)) {
                    
                    gunStatusData.updateDisplayName(shellCasePlayer);
                    
                    //射撃
                    Vector direction = player.getEyeLocation().getDirection();
                    Location origin = player.getEyeLocation();

                    WorldSyncBulletEntity worldSyncBulletEntity = new WorldSyncBulletEntity(shellCasePlayer.getShellCaseTeam(), origin, fullAutoGun);
                    double range = fullAutoGun.getHipShootingRecoil().getShootRandomRange(clickTick);
                    Vector randomVector = new Vector(Math.random() * range - range / 2, Math.random() * range - range / 2, Math.random() * range - range / 2);
                    if(shellCasePlayer.isADS()) randomVector = new Vector(0.0, 0.0, 0.0);
                    worldSyncBulletEntity.shootInitialize(shellCasePlayer, direction.clone().add(randomVector).multiply(fullAutoGun.getShootSpeed()), fullAutoGun.getFallTick());
                    worldSyncBulletEntity.spawn();
                    
                    //反動
                    if(shellCasePlayer.isADS()){
                        gunStatusData.getAdsRecoil().sendRecoil(shellCasePlayer, shootIndex);
                    }else{
                        gunStatusData.getNormalRecoil().sendRecoil(shellCasePlayer, shootIndex);
                        
                        clickTick += fullAutoGun.getShootTick();
                    }
                    shootIndex++;
                    
                    if(gunStatusData.getBullets() == 0){
                        gunStatusData.reload();
                    }
                    
                    ShellCaseTeam shellCaseTeam = shellCasePlayer.getShellCaseTeam();
                    if(shellCaseTeam != null) {
                        for (ShellCasePlayer matchPlayer : shellCaseTeam.getMatch().getPlayers()) {
                            if (matchPlayer == shellCasePlayer) {
                                matchPlayer.playSound(fullAutoGun.getShootSound());
                            } else {
                                matchPlayer.playSound(fullAutoGun.getShootSound(), shellCasePlayer.getEyeLocation());
                            }
                        }
                    }
                    
                }else{
                    gunStatusData.reload();
                }
            }else{
                noClickTick += fullAutoGun.getShootTick();
                if(noClickTick >= fullAutoGun.getHipShootingRecoil().getResetTick()) {
                    noClickTick = 0;
                    clickTick = 0;
                }
            }
        }
        
        
        playerTick++;
        taskTick++;
    }
}
