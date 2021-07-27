package be4rjp.shellcase.weapon.main.runnable;

import be4rjp.shellcase.player.ShellCasePlayer;
import be4rjp.shellcase.weapon.main.GunWeapon;
import org.bukkit.scheduler.BukkitRunnable;

public abstract class GunWeaponRunnable extends BukkitRunnable {
    
    protected final ShellCasePlayer shellCasePlayer;
    protected final GunWeapon gunWeapon;
    protected int playerTick = 10;
    
    public GunWeaponRunnable(GunWeapon gunWeapon, ShellCasePlayer ShellCasePlayer){
        this.shellCasePlayer = ShellCasePlayer;
        this.gunWeapon = gunWeapon;
        
        ShellCasePlayer.getGunWeaponTaskMap().put(gunWeapon, this);
    }
    
    public void setPlayerTick(int tick){this.playerTick = tick;}
    
    @Override
    public abstract void run();
}
