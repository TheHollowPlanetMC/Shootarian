package be4rjp.shellcase.match.intro;

import be4rjp.shellcase.ShellCase;
import be4rjp.shellcase.match.Match;
import be4rjp.shellcase.player.ShellCasePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Set;

/**
 * マップの名前とゲームモードの表示
 */
public class MapIntroRunnable extends BukkitRunnable {
    
    private final Set<ShellCasePlayer> players;
    
    private final Match match;
    
    private boolean is = true;
    
    public MapIntroRunnable(Set<ShellCasePlayer> players, Match match){
        this.players = players;
        this.match = match;
    }
    
    @Override
    public void run() {
        if(is) {
            players.forEach(shellCasePlayer ->
                    shellCasePlayer.sendTextTitle("match-" + match.getType(), "match-" + match.getType() + "-des", 20, 100, 20));
            players.forEach(shellCasePlayer -> shellCasePlayer.sendText("match-" + match.getType()));
            players.forEach(shellCasePlayer -> shellCasePlayer.sendText("match-" + match.getType() + "-des"));
            is = false;
            return;
        }
    
        for(ShellCasePlayer shellCasePlayer : match.getPlayers()){
            Player player = shellCasePlayer.getBukkitPlayer();
            if(player != null){
                player.sendTitle("", match.getShellCaseMap().getDisplayName(shellCasePlayer.getLang()), 20, 80, 20);
            }
        }
        
        cancel();
    }
    
    
    public void start(){
        this.runTaskTimerAsynchronously(ShellCase.getPlugin(), 5, 140);
    }
}
