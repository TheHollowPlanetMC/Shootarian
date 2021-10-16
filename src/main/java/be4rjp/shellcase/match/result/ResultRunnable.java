package be4rjp.shellcase.match.result;

import be4rjp.cinema4c.data.play.MovieData;
import be4rjp.shellcase.ShellCase;
import be4rjp.shellcase.ShellCaseConfig;
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ResultRunnable extends BukkitRunnable {
    
    private static final ShellCaseSound FINISH_SOUND = new ShellCaseSound(Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 1.0F, 1.2F);
    private static final ShellCaseSound WIN_SOUND = new ShellCaseSound(Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
    
    
    private final Match match;
    
    private final ShellCaseTeam winTeam;
    private ShellCaseTeam loseTeam;
    
    private int i = 0;
    
    public ResultRunnable(Match match){
        this.match = match;
        
        this.winTeam = match.getWinner();
        if(winTeam != null) {
            for (ShellCaseTeam shellCaseTeam : winTeam.getOtherTeam()) {
                loseTeam = shellCaseTeam;
                break;
            }
        }
    }
    
    @Override
    public void run() {
        
        if(i == 0){
            for(ShellCasePlayer shellCasePlayer : match.getPlayers()){
                shellCasePlayer.setGameMode(GameMode.SPECTATOR);
                shellCasePlayer.playSound(FINISH_SOUND);
                Player player = shellCasePlayer.getBukkitPlayer();
                if(player != null){
                    if(winTeam == null){
                        String title = "§7§lDRAW";
                        
                        ShellCaseTeam other = null;
                        for(ShellCaseTeam shellCaseTeam : shellCasePlayer.getShellCaseTeam().getOtherTeam()){
                            other = shellCaseTeam;
                        }
                        
                        String subTitle = shellCasePlayer.getShellCaseTeam().getShellCaseColor().getChatColor().toString() + shellCasePlayer.getShellCaseTeam().getPoints() + " §7vs " + other.getShellCaseColor().getChatColor().toString() + other.getPoints();
                        player.sendTitle(title, subTitle, 10, 100, 20);
                        shellCasePlayer.sendText("none-s", title);
                        shellCasePlayer.sendText("none-s", subTitle);
                    } else {
                        String title;
                        String subTitle;
                        if (shellCasePlayer.getShellCaseTeam() == winTeam) {
                            title = "§a§lYOU WON";
                            subTitle = winTeam.getShellCaseColor().getChatColor().toString() + winTeam.getPoints() + " §7vs " + loseTeam.getShellCaseColor().getChatColor().toString() + loseTeam.getPoints();
                        } else {
                            title = "§c§lYOU LOSE";
                            subTitle = loseTeam.getShellCaseColor().getChatColor().toString() + loseTeam.getPoints() + " §7vs " + winTeam.getShellCaseColor().getChatColor().toString() + winTeam.getPoints();
                        }
                        player.sendTitle(title, subTitle, 10, 100, 20);
                        shellCasePlayer.sendText("none-s", title);
                        shellCasePlayer.sendText("none-s", subTitle);
                    }
                }
            }
        }
        
        if(i == 1){
            match.getPlayers().forEach(shellCasePlayer -> shellCasePlayer.playSound(WIN_SOUND));
        }
        
        
        if(i == 8){
            for(ShellCasePlayer shellCasePlayer : match.getPlayers()){
                shellCasePlayer.setGameMode(GameMode.ADVENTURE, () -> {
                    shellCasePlayer.teleportSynced(ShellCaseConfig.getLobbyLocation());
                });
                cancel();
            }
            match.end();
        }
        
        i++;
    }
    
    public void start(){this.runTaskTimerAsynchronously(ShellCase.getPlugin(), 0, 20);}
}
