package be4rjp.shootarian.entity;

import be4rjp.shootarian.Shootarian;
import be4rjp.shootarian.match.Match;
import org.bukkit.Bukkit;
import world.chiyogami.chiyogamilib.scheduler.WorldThreadRunnable;

public class ShootarianEntityTickRunnable extends WorldThreadRunnable {
    
    private final Match match;
    
    public ShootarianEntityTickRunnable(Match match){
        super(Bukkit.getWorlds().get(0));
        this.match = match;
    }
    
    @Override
    public void run() {
        match.getShootarianEntities().forEach(ShootarianEntity::tick);
    }
    
    
    public void start(){
        this.runTaskTimer(Shootarian.getPlugin(), 0, 1);
    }
}
