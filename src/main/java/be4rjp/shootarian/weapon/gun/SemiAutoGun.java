package be4rjp.shootarian.weapon.gun;

import be4rjp.shootarian.entity.WorldSyncBulletEntity;
import be4rjp.shootarian.match.team.ShootarianTeam;
import be4rjp.shootarian.player.ShootarianPlayer;
import be4rjp.shootarian.weapon.WeaponStatusData;
import be4rjp.shootarian.weapon.actions.ActionRunnable;
import be4rjp.shootarian.weapon.actions.Actions;
import be4rjp.shootarian.weapon.gun.runnable.BurstGunRunnable;
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
    public void onRightClick(ShootarianPlayer shootarianPlayer) {
        ShootarianTeam shootarianTeam = shootarianPlayer.getShootarianTeam();
        if(shootarianTeam == null) return;
        
        WeaponStatusData weaponStatusData = shootarianPlayer.getWeaponStatusData(this);
        if (weaponStatusData == null) return;
        
        GunStatusData gunStatusData = (GunStatusData) weaponStatusData;
        if(gunStatusData.isCoolTime()) return;
        
        if(isBurst){
            if(!gunStatusData.isReloading()) {
                BurstGunRunnable burstGunRunnable = new BurstGunRunnable(shootarianPlayer, gunStatusData, shootTick);
                burstGunRunnable.runTaskTimer();
            }
        }else {
            if (gunStatusData.consumeBullets(1)) {
                Vector direction = shootarianPlayer.getEyeLocation().getDirection().clone();
        
                if (pellet != 0) {
                    double range = hipShootingAccuracy;
            
                    for (int i = 0; i < pellet; i++) {
                        Vector randomVector = new Vector(Math.random() * range - range / 2, Math.random() * range - range / 2, Math.random() * range - range / 2);
                        WorldSyncBulletEntity worldSyncBulletEntity = new WorldSyncBulletEntity(shootarianTeam, shootarianPlayer.getEyeLocation(), this);
                        worldSyncBulletEntity.shootInitialize(shootarianPlayer, direction.clone().add(randomVector).multiply(shootSpeed), fallTick);
                        worldSyncBulletEntity.spawn();
                    }
            
                    if (shootarianPlayer.isADS()) {
                        gunStatusData.getAdsRecoil().sendRecoil(shootarianPlayer, gunStatusData.getMaxBullets() - gunStatusData.getBullets());
                    } else {
                        gunStatusData.getNormalRecoil().sendRecoil(shootarianPlayer, gunStatusData.getMaxBullets() - gunStatusData.getBullets());
                    }
                } else {
                    WorldSyncBulletEntity worldSyncBulletEntity = new WorldSyncBulletEntity(shootarianTeam, shootarianPlayer.getEyeLocation(), this);
                    if (shootarianPlayer.isADS()) {
                        gunStatusData.getAdsRecoil().sendRecoil(shootarianPlayer, gunStatusData.getMaxBullets() - gunStatusData.getBullets());
                    } else {
                        double range = hipShootingAccuracy;
                        Vector randomVector = new Vector(Math.random() * range - range / 2, Math.random() * range - range / 2, Math.random() * range - range / 2);
                        direction.add(randomVector);
                        gunStatusData.getNormalRecoil().sendRecoil(shootarianPlayer, gunStatusData.getMaxBullets() - gunStatusData.getBullets());
                    }
            
                    worldSyncBulletEntity.shootInitialize(shootarianPlayer, direction.multiply(shootSpeed), fallTick);
                    worldSyncBulletEntity.setSniperBullet(isSniper);
                    worldSyncBulletEntity.spawn();
                }
                
                for(ShootarianPlayer matchPlayer : shootarianTeam.getMatch().getPlayers()) {
                    if(matchPlayer == shootarianPlayer){
                        matchPlayer.playSound(shootSound);
                    }else {
                        matchPlayer.playSound(shootSound, shootarianPlayer.getEyeLocation());
                    }
                }
                gunStatusData.updateDisplayName(shootarianPlayer);
        
                if (gunStatusData.getBullets() == 0) {
                    gunStatusData.reload();
                }
        
            } else {
                gunStatusData.reload();
                gunStatusData.updateDisplayName(shootarianPlayer);
                return;
            }
        }
        
        gunStatusData.setCoolTime(isBurst ? burstTick : shootTick);
        new ActionRunnable(shootarianPlayer, boltAction).start();
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
