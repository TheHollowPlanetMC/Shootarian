package be4rjp.shellcase.entity;

import be4rjp.shellcase.ShellCase;
import be4rjp.shellcase.match.Match;
import org.bukkit.scheduler.BukkitRunnable;

public class AsyncEntityTickRunnable extends BukkitRunnable {
    
    private final Match match;
    
    public AsyncEntityTickRunnable(Match match){
        this.match = match;
    }
    
    @Override
    public void run() {
        match.getAsyncEntities().forEach(ShellCaseEntity::tick);
    }
    
    public void start(){
        this.runTaskTimer(ShellCase.getPlugin(), 0, 1);
    }
}
