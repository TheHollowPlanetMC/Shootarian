package be4rjp.shellcase.match.result;

import be4rjp.shellcase.ShellCase;
import be4rjp.shellcase.match.Match;
import be4rjp.shellcase.match.team.ShellCaseTeam;
import be4rjp.shellcase.util.ProgressBar;
import be4rjp.shellcase.util.ShellCaseSound;
import org.bukkit.Sound;
import org.bukkit.scheduler.BukkitRunnable;

public class ConquestResultRunnable extends BukkitRunnable {
    
    private static final ShellCaseSound SHOW_RESULT_SOUND = new ShellCaseSound(Sound.ENTITY_ZOMBIE_INFECT, 13.0F, 1.5F);
    private static final ShellCaseSound WIN_SOUND = new ShellCaseSound(Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
    
    private final Match match;
    private final ShellCaseTeam winTeam;
    
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
            ProgressBar right = new ProgressBar(50).setProgressPercent(100 - g * 2).setEmptyColor(match.getShellCaseTeams().get(1).getShellCaseColor().getChatColor().toString()).setFrame(false);
            
            match.getPlayers().forEach(ShellCasePlayer -> ShellCasePlayer.sendTextTitle("none", new Object[]{}, "none-s", new Object[]{
                    g + "% [" + left.toString(match.getShellCaseTeams().get(0).getShellCaseColor().getChatColor().toString()) + right.toString("§7") + "§7]§r " + g + "%"
            }, 0, 40, 0));
            
            g += 2;
        }
        
        
        if(tick == 35){
            String team0Color = match.getShellCaseTeams().get(0).getShellCaseColor().getChatColor().toString();
            String team1Color = match.getShellCaseTeams().get(1).getShellCaseColor().getChatColor().toString();
            
            match.playSound(SHOW_RESULT_SOUND);
            
            if(winTeam == null){
                match.getPlayers().forEach(ShellCasePlayer -> ShellCasePlayer.sendTextTitle("none", new Object[]{}, "none-s", new Object[]{
                        "50% " + new ProgressBar(100).setProgress(50).setEmptyColor(team1Color).toString(team0Color) + " 50%"
                }, 0, 40, 0));
            }else{
                if(winTeam == match.getShellCaseTeams().get(0)){
                    int winTeamPaint = winTeam.getPoints();
                    int loseTeamPaint = match.getShellCaseTeams().get(1).getPoints();
                    
                    int maxPaint = winTeamPaint + loseTeamPaint;
                    double percent = (double) winTeamPaint / (double) maxPaint * 100.0;
    
                    match.getPlayers().stream().filter(ShellCasePlayer -> ShellCasePlayer.getShellCaseTeam() != null)
                            .forEach(ShellCasePlayer -> ShellCasePlayer.sendTextTitle(ShellCasePlayer.getShellCaseTeam() == winTeam ? "match-result-win" : "match-result-lose", new Object[]{}, "none-s", new Object[]{
                            (int)percent + "% " + new ProgressBar(100).setProgressPercent(percent).setEmptyColor(team1Color).toString(team0Color) + " " + (100 - (int)percent) + "%"
                    }, 0, 40, 0));
                }else{
                    int winTeamPaint = winTeam.getPoints();
                    int loseTeamPaint = match.getShellCaseTeams().get(0).getPoints();
    
                    int maxPaint = winTeamPaint + loseTeamPaint;
                    double percent = (double) winTeamPaint / (double) maxPaint * 100.0;
    
                    match.getPlayers().stream().filter(ShellCasePlayer -> ShellCasePlayer.getShellCaseTeam() != null)
                            .forEach(ShellCasePlayer -> ShellCasePlayer.sendTextTitle(ShellCasePlayer.getShellCaseTeam() == winTeam ? "match-result-win" : "match-result-lose", new Object[]{}, "none-s", new Object[]{
                            (100 - (int)percent) + "% " + new ProgressBar(100).setProgressPercent(100 - percent).setEmptyColor(team1Color).toString(team0Color) + " " + (int)percent + "%"
                    }, 0, 40, 0));
                }
            }
        }
    
        if(tick == 40){
            match.getPlayers().stream().filter(ShellCasePlayer -> ShellCasePlayer.getShellCaseTeam() != null)
                    .filter(ShellCasePlayer -> ShellCasePlayer.getShellCaseTeam() == winTeam)
                    .forEach(ShellCasePlayer -> ShellCasePlayer.playSound(WIN_SOUND));
            match.end();
            cancel();
        }
        
        
        tick++;
    }
    
    
    public void start(){
        this.runTaskTimerAsynchronously(ShellCase.getPlugin(), 0, 2);
    }
}
