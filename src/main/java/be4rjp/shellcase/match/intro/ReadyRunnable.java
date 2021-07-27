package be4rjp.shellcase.match.intro;

import be4rjp.shellcase.ShellCase;
import be4rjp.shellcase.match.Match;
import be4rjp.shellcase.match.map.ShellCaseMap;
import be4rjp.shellcase.match.team.ShellCaseTeam;
import be4rjp.shellcase.player.ObservableOption;
import be4rjp.shellcase.player.ShellCasePlayer;
import be4rjp.shellcase.util.ShellCaseSound;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;

public class ReadyRunnable extends BukkitRunnable {

    private final ShellCaseMap ShellCaseMap;
    private final Match match;

    private int count = 0;

    public ReadyRunnable(Match match){
        this.ShellCaseMap = match.getShellCaseMap();
        this.match = match;
    
        match.getPlayers().forEach(match::teleportToTeamLocation);
        match.getPlayers().forEach(ShellCasePlayer -> ShellCasePlayer.setGameMode(GameMode.ADVENTURE));
    }

    @Override
    public void run() {
        if(count == 0){
            //match.getPlayers().forEach(ShellCasePlayer::equipWeaponClass);
            match.getPlayers().forEach(ShellCasePlayer::equipHeadGear);
        }
        
        match.getPlayers().forEach(match::teleportToTeamLocation);
        
        
        
        if(count == 30){
            match.getPlayers().forEach(ShellCasePlayer -> ShellCasePlayer.sendTextTitle("match-ready-go", null, 2, 7, 2));
            match.start();
            this.cancel();
        }
        
        count++;
    }


    public void start(){
        this.runTaskTimerAsynchronously(ShellCase.getPlugin(), 0, 2);
    }
    
    
    @Override
    public synchronized void cancel() throws IllegalStateException {
        match.setPlayerObservableOption(ObservableOption.ONLY_MATCH_PLAYER);
        
        super.cancel();
    }
}
