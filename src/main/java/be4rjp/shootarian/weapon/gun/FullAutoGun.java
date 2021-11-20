package be4rjp.shootarian.weapon.gun;

import be4rjp.shootarian.player.ShootarianPlayer;
import be4rjp.shootarian.weapon.WeaponStatusData;
import be4rjp.shootarian.weapon.gun.runnable.FullAutoGunRunnable;

public class FullAutoGun extends GunWeapon {
    
    public FullAutoGun(String id) {
        super(id);
    }
    
    @Override
    public void onRightClick(ShootarianPlayer shootarianPlayer) {
        WeaponStatusData gunStatusData = shootarianPlayer.getWeaponStatusData(this);
        if (gunStatusData == null) return;
        
        FullAutoGunRunnable runnable = (FullAutoGunRunnable) shootarianPlayer.getGunWeaponTaskMap().get(this);
        if (runnable == null) {
            shootarianPlayer.clearGunWeaponTasks();
            runnable = new FullAutoGunRunnable(this, (GunStatusData) gunStatusData, shootarianPlayer);
            runnable.runTaskTimer();
            shootarianPlayer.getGunWeaponTaskMap().put(this, runnable);
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
