package be4rjp.shellcase.entity;

import be4rjp.shellcase.ShellCase;
import be4rjp.shellcase.match.Match;
import org.bukkit.scheduler.BukkitRunnable;

public class ShellCaseEntityTickRunnable extends BukkitRunnable {
    
    private final Match match;
    
    public ShellCaseEntityTickRunnable(Match match){
        this.match = match;
    }
    
    @Override
    public void run() {
        match.getShellCaseEntities().forEach(ShellCaseEntity::tick);
    }
    
    
    public void start(){
        this.runTaskTimerAsynchronously(ShellCase.getPlugin(), 0, 1);
    }
}
