package be4rjp.shellcase.weapon.gun;

import be4rjp.shellcase.ShellCase;
import be4rjp.shellcase.player.ShellCasePlayer;
import be4rjp.shellcase.weapon.WeaponStatusData;
import be4rjp.shellcase.weapon.gun.runnable.FullAutoGunRunnable;

public class FullAutoGun extends GunWeapon {
    
    public FullAutoGun(String id) {
        super(id);
    }
    
    @Override
    public void onRightClick(ShellCasePlayer shellCasePlayer) {
        WeaponStatusData gunStatusData = shellCasePlayer.getWeaponStatusData(this);
        if (gunStatusData == null) return;
        
        FullAutoGunRunnable runnable = (FullAutoGunRunnable) shellCasePlayer.getGunWeaponTaskMap().get(this);
        if (runnable == null) {
            shellCasePlayer.clearGunWeaponTasks();
            runnable = new FullAutoGunRunnable(this, (GunStatusData) gunStatusData, shellCasePlayer);
            runnable.runTaskTimer();
            shellCasePlayer.getGunWeaponTaskMap().put(this, runnable);
        }
        
        runnable.setPlayerTick(0);
    }
    
    @Override
    public GunWeaponType getType() {
        return GunWeaponType.FULL_AUTO_GUN;
    }
    
    @Override
    public void loadDetailsData() {
        //None
    }
    
}
