package be4rjp.shellcase.weapon.actions;

import be4rjp.shellcase.ShellCase;
import be4rjp.shellcase.player.ShellCasePlayer;
import be4rjp.shellcase.weapon.gun.GunStatusData;
import org.bukkit.scheduler.BukkitRunnable;

public class ReloadActionRunnable extends BukkitRunnable {
    
    private final ShellCasePlayer shellCasePlayer;
    private final GunStatusData gunStatusData;
    private final Actions reloadActions;
    
    public ReloadActionRunnable(ShellCasePlayer shellCasePlayer, GunStatusData gunStatusData){
        this.shellCasePlayer = shellCasePlayer;
        this.gunStatusData = gunStatusData;
        
        if(gunStatusData.getBullets() == 0){
            this.reloadActions = gunStatusData.getGunWeapon().getCombatReloadActions();
        }else{
            this.reloadActions = gunStatusData.getGunWeapon().getReloadActions();
        }
    }
    
    private int tick = 0;
    
    @Override
    public void run() {
        if(tick == 0){
            gunStatusData.setReloading(true);
            gunStatusData.setBullets(0);
            gunStatusData.updateDisplayName(shellCasePlayer);
        }
        
        reloadActions.play(shellCasePlayer, tick);
        
        if(tick == reloadActions.getFinalTick()){
            gunStatusData.setBullets(gunStatusData.getMaxBullets());
            gunStatusData.setReloading(false);
            gunStatusData.updateDisplayName(shellCasePlayer);
            this.cancel();
        }
        
        tick++;
    }
    
    public void start(){this.runTaskTimerAsynchronously(ShellCase.getPlugin(), 0, 1);}
}
