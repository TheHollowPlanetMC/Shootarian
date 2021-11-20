package be4rjp.shootarian.match.result;

import be4rjp.shootarian.Shootarian;
import be4rjp.shootarian.match.Match;
import be4rjp.shootarian.match.team.ShootarianTeam;
import be4rjp.shootarian.util.ProgressBar;
import be4rjp.shootarian.util.ShootarianSound;
import org.bukkit.Sound;
import org.bukkit.scheduler.BukkitRunnable;

public class ConquestResultRunnable extends BukkitRunnable {
    
    private static final ShootarianSound SHOW_RESULT_SOUND = new ShootarianSound(Sound.ENTITY_ZOMBIE_INFECT, 13.0F, 1.5F);
    private static final ShootarianSound WIN_SOUND = new ShootarianSound(Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
    
    private final Match match;
    private final ShootarianTeam winTeam;
    
    private int tick = 0;
    private int g = 0;
    
    public ConquestResultRunnable(Match match){
        this.match = match;
        
        this.winTeam = match.getWinner();
    }
    
    @Override
    public void run() {
    
        if(tick <= 15) {
            ProgressBar left = new ProgressBar(50).setProgressPercent(g * 2).setFrame(false);
            ProgressBar right = new ProgressBar(50).setProgressPercent(100 - g * 2).setEmptyColor(match.getShootarianTeams().get(1).getShootarianColor().getChatColor().toString()).setFrame(false);
            
            match.getPlayers().forEach(ShootarianPlayer -> ShootarianPlayer.sendTextTitle("none", new Object[]{}, "none-s", new Object[]{
                    g + "% [" + left.toString(match.getShootarianTeams().get(0).getShootarianColor().getChatColor().toString()) + right.toString("§7") + "§7]§r " + g + "%"
            }, 0, 40, 0));
            
            g += 2;
        }
        
        
        if(tick == 35){
            String team0Color = match.getShootarianTeams().get(0).getShootarianColor().getChatColor().toString();
            String team1Color = match.getShootarianTeams().get(1).getShootarianColor().getChatColor().toString();
            
            match.playSound(SHOW_RESULT_SOUND);
            
            if(winTeam == null){
                match.getPlayers().forEach(ShootarianPlayer -> ShootarianPlayer.sendTextTitle("none", new Object[]{}, "none-s", new Object[]{
                        "50% " + new ProgressBar(100).setProgress(50).setEmptyColor(team1Color).toString(team0Color) + " 50%"
                }, 0, 40, 0));
            }else{
                if(winTeam == match.getShootarianTeams().get(0)){
                    int winTeamPaint = winTeam.getPoints();
                    int loseTeamPaint = match.getShootarianTeams().get(1).getPoints();
                    
                    int maxPaint = winTeamPaint + loseTeamPaint;
                    double percent = (double) winTeamPaint / (double) maxPaint * 100.0;
    
                    match.getPlayers().stream().filter(ShootarianPlayer -> ShootarianPlayer.getShootarianTeam() != null)
                            .forEach(ShootarianPlayer -> ShootarianPlayer.sendTextTitle(ShootarianPlayer.getShootarianTeam() == winTeam ? "match-result-win" : "match-result-lose", new Object[]{}, "none-s", new Object[]{
                            (int)percent + "% " + new ProgressBar(100).setProgressPercent(percent).setEmptyColor(team1Color).toString(team0Color) + " " + (100 - (int)percent) + "%"
                    }, 0, 40, 0));
                }else{
                    int winTeamPaint = winTeam.getPoints();
                    int loseTeamPaint = match.getShootarianTeams().get(0).getPoints();
    
                    int maxPaint = winTeamPaint + loseTeamPaint;
                    double percent = (double) winTeamPaint / (double) maxPaint * 100.0;
    
                    match.getPlayers().stream().filter(ShootarianPlayer -> ShootarianPlayer.getShootarianTeam() != null)
                            .forEach(ShootarianPlayer -> ShootarianPlayer.sendTextTitle(ShootarianPlayer.getShootarianTeam() == winTeam ? "match-result-win" : "match-result-lose", new Object[]{}, "none-s", new Object[]{
                            (100 - (int)percent) + "% " + new ProgressBar(100).setProgressPercent(100 - percent).setEmptyColor(team1Color).toString(team0Color) + " " + (int)percent + "%"
                    }, 0, 40, 0));
                }
            }
        }
    
        if(tick == 40){
            match.getPlayers().stream().filter(ShootarianPlayer -> ShootarianPlayer.getShootarianTeam() != null)
                    .filter(ShootarianPlayer -> ShootarianPlayer.getShootarianTeam() == winTeam)
                    .forEach(ShootarianPlayer -> ShootarianPlayer.playSound(WIN_SOUND));
            match.end();
            cancel();
        }
        
        
        tick++;
    }
    
    
    public void start(){
        this.runTaskTimerAsynchronously(Shootarian.getPlugin(), 0, 2);
    }
}
