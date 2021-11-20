package be4rjp.shootarian.weapon.gun.runnable;

import be4rjp.shootarian.player.ShootarianPlayer;
import be4rjp.shootarian.scheduler.MultiThreadRunnable;
import be4rjp.shootarian.weapon.gun.GunWeapon;

public abstract class GunWeaponRunnable extends MultiThreadRunnable {
    
    protected final ShootarianPlayer shootarianPlayer;
    protected final GunWeapon gunWeapon;
    protected int playerTick = 10;
    
    public GunWeaponRunnable(GunWeapon gunWeapon, ShootarianPlayer ShootarianPlayer){
        this.shootarianPlayer = ShootarianPlayer;
        this.gunWeapon = gunWeapon;
        
        ShootarianPlayer.getGunWeaponTaskMap().put(gunWeapon, this);
    }
    
    public void setPlayerTick(int tick){this.playerTick = tick;}
    
    @Override
    public abstract void run();
}
