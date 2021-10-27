package be4rjp.shellcase.weapon.gun;

import be4rjp.shellcase.entity.AsyncBulletEntity;
import be4rjp.shellcase.match.team.ShellCaseTeam;
import be4rjp.shellcase.player.ShellCasePlayer;
import be4rjp.shellcase.weapon.WeaponStatusData;
import be4rjp.shellcase.weapon.actions.ActionRunnable;
import be4rjp.shellcase.weapon.actions.Actions;
import be4rjp.shellcase.weapon.gun.runnable.BurstGunRunnable;
import org.bukkit.util.Vector;

public class SemiAutoGun extends GunWeapon{
    
    //ボルトアクション
    private Actions boltAction = new Actions("null");
    //腰撃ち精度
    private double hipShootingAccuracy = 0.5;
    //スナイパー武器かどうか
    private boolean isSniper = false;
    //一度に打つ弾の数(ショットガン用)
    private int pellet = 0;
    //バースト撃ちかどうか
    private boolean isBurst = false;
    //バースト撃ちのときの発射弾数
    private int burstBullets = 3;
    //バースト撃ちの間隔
    private int burstTick = 10;
    
    
    public SemiAutoGun(String id) {
        super(id);
    }
    
    public Actions getBoltAction() {return boltAction;}
    
    public double getHipShootingAccuracy() {return hipShootingAccuracy;}
    
    public int getBurstBullets() {return burstBullets;}
    
    @Override
    public void onRightClick(ShellCasePlayer shellCasePlayer) {
        ShellCaseTeam shellCaseTeam = shellCasePlayer.getShellCaseTeam();
        if(shellCaseTeam == null) return;
        
        WeaponStatusData weaponStatusData = shellCasePlayer.getWeaponStatusData(this);
        if (weaponStatusData == null) return;
        
        GunStatusData gunStatusData = (GunStatusData) weaponStatusData;
        if(gunStatusData.isCoolTime()) return;
        
        if(isBurst){
            if(!gunStatusData.isReloading()) {
                BurstGunRunnable burstGunRunnable = new BurstGunRunnable(shellCasePlayer, gunStatusData, shootTick);
                burstGunRunnable.runTaskTimer();
            }
        }else {
            if (gunStatusData.consumeBullets(1)) {
                Vector direction = shellCasePlayer.getEyeLocation().getDirection().clone();
        
                if (pellet != 0) {
                    double range = hipShootingAccuracy;
            
                    for (int i = 0; i < pellet; i++) {
                        Vector randomVector = new Vector(Math.random() * range - range / 2, Math.random() * range - range / 2, Math.random() * range - range / 2);
                        AsyncBulletEntity asyncBulletEntity = new AsyncBulletEntity(shellCaseTeam, shellCasePlayer.getEyeLocation(), this);
                        asyncBulletEntity.shootInitialize(shellCasePlayer, direction.clone().add(randomVector).multiply(shootSpeed), fallTick);
                        asyncBulletEntity.spawn();
                    }
            
                    if (shellCasePlayer.isADS()) {
                        gunStatusData.getAdsRecoil().sendRecoil(shellCasePlayer, gunStatusData.getMaxBullets() - gunStatusData.getBullets());
                    } else {
                        gunStatusData.getNormalRecoil().sendRecoil(shellCasePlayer, gunStatusData.getMaxBullets() - gunStatusData.getBullets());
                    }
                } else {
                    AsyncBulletEntity asyncBulletEntity = new AsyncBulletEntity(shellCaseTeam, shellCasePlayer.getEyeLocation(), this);
                    if (shellCasePlayer.isADS()) {
                        gunStatusData.getAdsRecoil().sendRecoil(shellCasePlayer, gunStatusData.getMaxBullets() - gunStatusData.getBullets());
                    } else {
                        double range = hipShootingAccuracy;
                        Vector randomVector = new Vector(Math.random() * range - range / 2, Math.random() * range - range / 2, Math.random() * range - range / 2);
                        direction.add(randomVector);
                        gunStatusData.getNormalRecoil().sendRecoil(shellCasePlayer, gunStatusData.getMaxBullets() - gunStatusData.getBullets());
                    }
            
                    asyncBulletEntity.shootInitialize(shellCasePlayer, direction.multiply(shootSpeed), fallTick);
                    asyncBulletEntity.setSniperBullet(isSniper);
                    asyncBulletEntity.spawn();
                }
                
                for(ShellCasePlayer matchPlayer : shellCaseTeam.getMatch().getPlayers()) {
                    if(matchPlayer == shellCasePlayer){
                        matchPlayer.playSound(shootSound);
                    }else {
                        matchPlayer.playSound(shootSound, shellCasePlayer.getEyeLocation());
                    }
                }
                gunStatusData.updateDisplayName(shellCasePlayer);
        
                if (gunStatusData.getBullets() == 0) {
                    gunStatusData.reload();
                }
        
            } else {
                gunStatusData.reload();
                gunStatusData.updateDisplayName(shellCasePlayer);
                return;
            }
        }
        
        gunStatusData.setCoolTime(isBurst ? burstTick : shootTick);
        new ActionRunnable(shellCasePlayer, boltAction).start();
    }
    
    @Override
    public GunWeaponType getType() {
        return GunWeaponType.SEMI_AUTO_GUN;
    }
    
    @Override
    public void loadDetailsData() {
        if(yml.contains("bolt-action")) this.boltAction = Actions.getAction(yml.getString("bolt-action"));
        if(yml.contains("sniper")) this.isSniper = yml.getBoolean("sniper");
        if(yml.contains("hip-shooting-accuracy")) this.hipShootingAccuracy = yml.getDouble("hip-shooting-accuracy");
        if(yml.contains("pellet")) this.pellet = yml.getInt("pellet");
    
        if(yml.contains("burst")){
            this.isBurst = true;
            this.burstBullets = yml.getInt("burst.bullets");
            this.burstTick = yml.getInt("burst.shoot-tick");
        }
    }
}
