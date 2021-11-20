package be4rjp.shootarian.weapon.actions;

import be4rjp.shootarian.Shootarian;
import be4rjp.shootarian.player.ShootarianPlayer;
import be4rjp.shootarian.weapon.gun.GunStatusData;
import org.bukkit.scheduler.BukkitRunnable;

public class ReloadActionRunnable extends BukkitRunnable {
    
    private final ShootarianPlayer shootarianPlayer;
    private final GunStatusData gunStatusData;
    private final Actions reloadActions;
    
    public ReloadActionRunnable(ShootarianPlayer shootarianPlayer, GunStatusData gunStatusData){
        this.shootarianPlayer = shootarianPlayer;
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
            gunStatusData.updateDisplayName(shootarianPlayer);
        }
        
        reloadActions.play(shootarianPlayer, tick);
        
        if(tick == reloadActions.getFinalTick()){
            gunStatusData.setBullets(gunStatusData.getMaxBullets());
            gunStatusData.setReloading(false);
            gunStatusData.updateDisplayName(shootarianPlayer);
            this.cancel();
        }
        
        tick++;
    }
    
    public void start(){this.runTaskTimerAsynchronously(Shootarian.getPlugin(), 0, 1);}
}
