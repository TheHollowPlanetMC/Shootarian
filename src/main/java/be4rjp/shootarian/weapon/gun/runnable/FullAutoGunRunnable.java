package be4rjp.shootarian.weapon.gun.runnable;

import be4rjp.shootarian.entity.WorldSyncBulletEntity;
import be4rjp.shootarian.match.team.ShootarianTeam;
import be4rjp.shootarian.player.ShootarianPlayer;
import be4rjp.shootarian.weapon.gun.GunStatusData;
import be4rjp.shootarian.weapon.gun.FullAutoGun;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class FullAutoGunRunnable extends GunWeaponRunnable {
    
    private final FullAutoGun fullAutoGun;
    private final GunStatusData gunStatusData;
    
    public FullAutoGunRunnable(FullAutoGun fullAutoGun, GunStatusData gunStatusData, ShootarianPlayer shootarianPlayer){
        super(fullAutoGun, shootarianPlayer);
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
                Player player = shootarianPlayer.getBukkitPlayer();
                if (player == null) return;
                
                if(gunStatusData.consumeBullets(1)) {
                    
                    gunStatusData.updateDisplayName(shootarianPlayer);
                    
                    //射撃
                    Vector direction = player.getEyeLocation().getDirection();
                    Location origin = player.getEyeLocation();

                    WorldSyncBulletEntity worldSyncBulletEntity = new WorldSyncBulletEntity(shootarianPlayer.getShootarianTeam(), origin, fullAutoGun);
                    double range = fullAutoGun.getHipShootingRecoil().getShootRandomRange(clickTick);
                    Vector randomVector = new Vector(Math.random() * range - range / 2, Math.random() * range - range / 2, Math.random() * range - range / 2);
                    if(shootarianPlayer.isADS()) randomVector = new Vector(0.0, 0.0, 0.0);
                    worldSyncBulletEntity.shootInitialize(shootarianPlayer, direction.clone().add(randomVector).multiply(fullAutoGun.getShootSpeed()), fullAutoGun.getFallTick());
                    worldSyncBulletEntity.spawn();
                    
                    //反動
                    if(shootarianPlayer.isADS()){
                        gunStatusData.getAdsRecoil().sendRecoil(shootarianPlayer, shootIndex);
                    }else{
                        gunStatusData.getNormalRecoil().sendRecoil(shootarianPlayer, shootIndex);
                        
                        clickTick += fullAutoGun.getShootTick();
                    }
                    shootIndex++;
                    
                    if(gunStatusData.getBullets() == 0){
                        gunStatusData.reload();
                    }
                    
                    ShootarianTeam shootarianTeam = shootarianPlayer.getShootarianTeam();
                    if(shootarianTeam != null) {
                        for (ShootarianPlayer matchPlayer : shootarianTeam.getMatch().getPlayers()) {
                            if (matchPlayer == shootarianPlayer) {
                                matchPlayer.playSound(fullAutoGun.getShootSound());
                            } else {
                                matchPlayer.playSound(fullAutoGun.getShootSound(), shootarianPlayer.getEyeLocation());
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
