package be4rjp.shootarian.match.intro;

import be4rjp.shootarian.Shootarian;
import be4rjp.shootarian.match.Match;
import be4rjp.shootarian.player.ShootarianPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Set;

/**
 * マップの名前とゲームモードの表示
 */
public class MapIntroRunnable extends BukkitRunnable {
    
    private final Set<ShootarianPlayer> players;
    
    private final Match match;
    
    private boolean is = true;
    
    public MapIntroRunnable(Set<ShootarianPlayer> players, Match match){
        this.players = players;
        this.match = match;
    }
    
    @Override
    public void run() {
        if(is) {
            players.forEach(shootarianPlayer ->
                    shootarianPlayer.sendTextTitle("match-" + match.getType(), "match-" + match.getType() + "-des", 20, 100, 20));
            players.forEach(shootarianPlayer -> shootarianPlayer.sendText("match-" + match.getType()));
            players.forEach(shootarianPlayer -> shootarianPlayer.sendText("match-" + match.getType() + "-des"));
            is = false;
            return;
        }
    
        for(ShootarianPlayer shootarianPlayer : match.getPlayers()){
            Player player = shootarianPlayer.getBukkitPlayer();
            if(player != null){
                player.sendTitle("", match.getShootarianMap().getDisplayName(shootarianPlayer.getLang()), 20, 80, 20);
            }
        }
        
        cancel();
    }
    
    
    public void start(){
        this.runTaskTimerAsynchronously(Shootarian.getPlugin(), 5, 140);
    }
}
