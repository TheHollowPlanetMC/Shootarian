package be4rjp.shellcase.entity;

import be4rjp.shellcase.ShellCase;
import be4rjp.shellcase.match.Match;
import org.bukkit.Bukkit;
import world.chiyogami.chiyogamilib.scheduler.WorldThreadRunnable;

public class ShellCaseEntityTickRunnable extends WorldThreadRunnable {
    
    private final Match match;
    
    public ShellCaseEntityTickRunnable(Match match){
        super(Bukkit.getWorlds().get(0));
        this.match = match;
    }
    
    @Override
    public void run() {
        match.getShellCaseEntities().forEach(ShellCaseEntity::tick);
    }
    
    
    public void start(){
        this.runTaskTimer(ShellCase.getPlugin(), 0, 1);
    }
}
