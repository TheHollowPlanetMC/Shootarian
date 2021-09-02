package be4rjp.shellcase.weapon.actions;

import be4rjp.shellcase.ShellCase;
import be4rjp.shellcase.player.ShellCasePlayer;
import org.bukkit.scheduler.BukkitRunnable;

public class ActionRunnable extends BukkitRunnable {
    
    private final ShellCasePlayer shellCasePlayer;
    private final Actions actions;
    
    public ActionRunnable(ShellCasePlayer shellCasePlayer, Actions actions){
        this.shellCasePlayer = shellCasePlayer;
        this.actions = actions;
    }
    
    private int tick = 0;
    
    @Override
    public void run() {
        actions.play(shellCasePlayer, tick);
        
        if(tick == actions.getFinalTick()){
            this.cancel();
        }
        
        tick++;
    }
    
    public void start(){this.runTaskTimerAsynchronously(ShellCase.getPlugin(), 0, 1);}
}
