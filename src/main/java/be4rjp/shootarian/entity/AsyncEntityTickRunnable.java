package be4rjp.shootarian.entity;

import be4rjp.shootarian.Shootarian;
import be4rjp.shootarian.match.Match;
import org.bukkit.scheduler.BukkitRunnable;

public class AsyncEntityTickRunnable extends BukkitRunnable {
    
    private final Match match;
    
    public AsyncEntityTickRunnable(Match match){
        this.match = match;
    }
    
    @Override
    public void run() {
        match.getAsyncEntities().forEach(ShootarianEntity::tick);
    }
    
    public void start(){
        this.runTaskTimer(Shootarian.getPlugin(), 0, 1);
    }
}
