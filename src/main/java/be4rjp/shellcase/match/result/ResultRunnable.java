package be4rjp.shellcase.match.result;

import be4rjp.cinema4c.data.play.MovieData;
import be4rjp.shellcase.ShellCase;
import be4rjp.shellcase.match.Match;
import be4rjp.shellcase.match.map.ShellCaseMap;
import be4rjp.shellcase.player.ObservableOption;
import be4rjp.shellcase.player.ShellCasePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ResultRunnable extends BukkitRunnable {
    
    private final Match match;
    
    public ResultRunnable(Match match){
        this.match = match;
    }
    
    @Override
    public void run() {
        //ムービー再生
        ShellCaseMap ShellCaseMap = match.getShellCaseMap();
        MovieData movieData = ShellCaseMap.getResultMovie();
        if(movieData != null) {
            List<Player> players = new ArrayList<>();
            match.getPlayers().stream()
                    .filter(ShellCasePlayer -> ShellCasePlayer.getBukkitPlayer() != null)
                    .forEach(ShellCasePlayer -> players.add(ShellCasePlayer.getBukkitPlayer()));
    
            match.setPlayerObservableOption(ObservableOption.ALONE);
            movieData.play(players);
        }
        
        new ConquestResultRunnable(match).start();
        match.getPlayers().forEach(ShellCasePlayer::setLobbyItem);
        this.cancel();
    }
    
    public void start(){this.runTaskTimerAsynchronously(ShellCase.getPlugin(), 60, 10);}
}
