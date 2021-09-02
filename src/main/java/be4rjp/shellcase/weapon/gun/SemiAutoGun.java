package be4rjp.shellcase.weapon.gun;

import be4rjp.shellcase.entity.BulletEntity;
import be4rjp.shellcase.match.team.ShellCaseTeam;
import be4rjp.shellcase.player.ShellCasePlayer;
import be4rjp.shellcase.weapon.WeaponStatusData;
import be4rjp.shellcase.weapon.actions.ActionRunnable;
import be4rjp.shellcase.weapon.actions.Actions;
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
    
    
    public SemiAutoGun(String id) {
        super(id);
    }
    
    public Actions getBoltAction() {return boltAction;}
    
    @Override
    public void onRightClick(ShellCasePlayer shellCasePlayer) {
        ShellCaseTeam shellCaseTeam = shellCasePlayer.getShellCaseTeam();
        if(shellCaseTeam == null) return;
        
        WeaponStatusData weaponStatusData = shellCasePlayer.getWeaponStatusData(this);
        if (weaponStatusData == null) return;
        
        GunStatusData gunStatusData = (GunStatusData) weaponStatusData;
        if(gunStatusData.isCoolTime()) return;
        
        if(gunStatusData.consumeBullets(1)) {
            Vector direction = shellCasePlayer.getEyeLocation().getDirection().clone();
            
            if(pellet != 0){
                double range = hipShootingAccuracy;
                
                for(int i = 0; i < pellet; i++) {
                    Vector randomVector = new Vector(Math.random() * range - range / 2, Math.random() * range - range / 2, Math.random() * range - range / 2);
                    BulletEntity bulletEntity = new BulletEntity(shellCaseTeam, shellCasePlayer.getEyeLocation(), this);
                    bulletEntity.shootInitialize(shellCasePlayer, direction.clone().add(randomVector).multiply(shootSpeed), fallTick);
                    bulletEntity.spawn();
                }
    
                if (shellCasePlayer.isADS()) {
                    gunStatusData.getAdsRecoil().sendRecoil(shellCasePlayer, gunStatusData.getMaxBullets() - gunStatusData.getBullets());
                }else{
                    gunStatusData.getNormalRecoil().sendRecoil(shellCasePlayer, gunStatusData.getMaxBullets() - gunStatusData.getBullets());
                }
            }else {
                BulletEntity bulletEntity = new BulletEntity(shellCaseTeam, shellCasePlayer.getEyeLocation(), this);
                if (shellCasePlayer.isADS()) {
                    gunStatusData.getAdsRecoil().sendRecoil(shellCasePlayer, gunStatusData.getMaxBullets() - gunStatusData.getBullets());
                } else {
                    double range = hipShootingAccuracy;
                    Vector randomVector = new Vector(Math.random() * range - range / 2, Math.random() * range - range / 2, Math.random() * range - range / 2);
                    direction.add(randomVector);
                    gunStatusData.getNormalRecoil().sendRecoil(shellCasePlayer, gunStatusData.getMaxBullets() - gunStatusData.getBullets());
                }
    
                bulletEntity.shootInitialize(shellCasePlayer, direction.multiply(shootSpeed), fallTick);
                bulletEntity.setSniperBullet(isSniper);
                bulletEntity.spawn();
            }
            shellCaseTeam.getMatch().playSound(shootSound);
            gunStatusData.updateDisplayName(shellCasePlayer);
        }else{
            gunStatusData.reload();
            gunStatusData.updateDisplayName(shellCasePlayer);
            return;
        }
        
        gunStatusData.setCoolTime(shootTick);
        new ActionRunnable(shellCasePlayer, boltAction).start();
    }
    
    @Override
    public MainWeaponType getType() {
        return MainWeaponType.SEMI_AUTO_GUN;
    }
    
    @Override
    public void loadDetailsData() {
        if(yml.contains("bolt-action")) this.boltAction = Actions.getAction(yml.getString("bolt-action"));
        if(yml.contains("sniper")) this.isSniper = yml.getBoolean("sniper");
        if(yml.contains("hip-shooting-accuracy")) this.hipShootingAccuracy = yml.getDouble("hip-shooting-accuracy");
        if(yml.contains("pellet")) this.pellet = yml.getInt("pellet");
    }
}
