package be4rjp.shellcase.weapon.gun;

import be4rjp.shellcase.ShellCase;
import be4rjp.shellcase.player.ShellCasePlayer;
import be4rjp.shellcase.weapon.WeaponStatusData;
import be4rjp.shellcase.weapon.gun.runnable.FullAutoGunRunnable;

public class FullAutoGun extends GunWeapon {
    
    //リコイル
    private final HipShootingRecoil hipShootingRecoil = new HipShootingRecoil();
    
    public FullAutoGun(String id) {
        super(id);
    }
    
    public HipShootingRecoil getHipShootingRecoil() {return hipShootingRecoil;}
    
    @Override
    public void onRightClick(ShellCasePlayer shellCasePlayer) {
        WeaponStatusData gunStatusData = shellCasePlayer.getWeaponStatusData(this);
        if (gunStatusData == null) return;
        
        FullAutoGunRunnable runnable = (FullAutoGunRunnable) shellCasePlayer.getGunWeaponTaskMap().get(this);
        if (runnable == null) {
            shellCasePlayer.clearGunWeaponTasks();
            runnable = new FullAutoGunRunnable(this, (GunStatusData) gunStatusData, shellCasePlayer);
            runnable.runTaskTimerAsynchronously(ShellCase.getPlugin(), 0, 1);
            shellCasePlayer.getGunWeaponTaskMap().put(this, runnable);
        }
        
        runnable.setPlayerTick(0);
    }
    
    @Override
    public MainWeaponType getType() {
        return MainWeaponType.FULL_AUTO_GUN;
    }
    
    @Override
    public void loadDetailsData() {
        if(yml.contains("hip-shooting-recoil")){
            if(yml.contains("hip-shooting-recoil.shoot-random")) hipShootingRecoil.setShootRandom(yml.getDouble("hip-shooting-recoil.shoot-random"));
            if(yml.contains("hip-shooting-recoil.shoot-max-random")) hipShootingRecoil.setShootMaxRandom(yml.getDouble("hip-shooting-recoil.shoot-max-random"));
            if(yml.contains("hip-shooting-recoil.increase-min-tick")) hipShootingRecoil.setMinTick(yml.getInt("hip-shooting-recoil.increase-min-tick"));
            if(yml.contains("hip-shooting-recoil.increase-max-tick")) hipShootingRecoil.setMaxTick(yml.getInt("hip-shooting-recoil.increase-max-tick"));
            if(yml.contains("hip-shooting-recoil.increase-reset-tick")) hipShootingRecoil.setResetTick(yml.getInt("hip-shooting-recoil.increase-reset-tick"));
        }
    }
    
}
