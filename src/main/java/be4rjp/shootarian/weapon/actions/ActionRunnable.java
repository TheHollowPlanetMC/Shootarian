package be4rjp.shootarian.weapon.actions;

import be4rjp.shootarian.Shootarian;
import be4rjp.shootarian.player.ShootarianPlayer;
import org.bukkit.scheduler.BukkitRunnable;

public class ActionRunnable extends BukkitRunnable {
    
    private final ShootarianPlayer shootarianPlayer;
    private final Actions actions;
    
    public ActionRunnable(ShootarianPlayer shootarianPlayer, Actions actions){
        this.shootarianPlayer = shootarianPlayer;
        this.actions = actions;
    }
    
    private int tick = 0;
    
    @Override
    public void run() {
        actions.play(shootarianPlayer, tick);
        
        if(tick == actions.getFinalTick()){
            this.cancel();
        }
        
        tick++;
    }
    
    public void start(){this.runTaskTimerAsynchronously(Shootarian.getPlugin(), 0, 1);}
}
