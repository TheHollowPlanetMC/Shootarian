package be4rjp.shootarian.match.intro;

import be4rjp.shootarian.Shootarian;
import be4rjp.shootarian.match.Match;
import be4rjp.shootarian.match.map.ShootarianMap;
import be4rjp.shootarian.player.ObservableOption;
import be4rjp.shootarian.player.ShootarianPlayer;
import org.bukkit.GameMode;
import org.bukkit.scheduler.BukkitRunnable;

public class ReadyRunnable extends BukkitRunnable {

    private final ShootarianMap ShootarianMap;
    private final Match match;

    private int count = 0;

    public ReadyRunnable(Match match){
        this.ShootarianMap = match.getShootarianMap();
        this.match = match;
    
        match.getPlayers().forEach(match::teleportToTeamLocation);
        match.getPlayers().forEach(ShootarianPlayer -> ShootarianPlayer.setGameMode(GameMode.ADVENTURE));
    }

    @Override
    public void run() {
        if(count == 0){
            //match.getPlayers().forEach(ShootarianPlayer::equipWeaponClass);
            match.getPlayers().forEach(ShootarianPlayer::equipHeadGear);
        }
        
        match.getPlayers().forEach(match::teleportToTeamLocation);
        
        
        
        if(count == 30){
            match.getPlayers().forEach(ShootarianPlayer -> ShootarianPlayer.sendTextTitle("match-ready-go", null, 2, 7, 2));
            match.start();
            this.cancel();
        }
        
        count++;
    }


    public void start(){
        this.runTaskTimerAsynchronously(Shootarian.getPlugin(), 0, 20);
    }
    
    
    @Override
    public synchronized void cancel() throws IllegalStateException {
        match.setPlayerObservableOption(ObservableOption.ONLY_MATCH_PLAYER);
        
        super.cancel();
    }
}
