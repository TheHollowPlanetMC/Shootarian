package be4rjp.shellcase.match;

import be4rjp.shellcase.match.map.ShellCaseMap;
import be4rjp.shellcase.match.runnable.ConquestMatchRunnable;
import be4rjp.shellcase.match.team.ShellCaseTeam;
import be4rjp.shellcase.player.ShellCasePlayer;

import java.util.HashSet;
import java.util.Set;

public class ConquestMatch extends Match{
    
    public ConquestMatch(ShellCaseMap ShellCaseMap) {
        super(ShellCaseMap);
    }
    
    @Override
    public MatchType getType() {
        return MatchType.CONQUEST;
    }
    
    @Override
    public void initializePlayer(ShellCasePlayer ShellCasePlayer) {
    
    }
    
    @Override
    public void finish() {
        super.finish();
    }
    
    
    @Override
    public void initialize() {
        this.matchRunnable = new ConquestMatchRunnable(this, 180);
    }
    
    @Override
    public boolean checkWin() {
        return false;
    }
    
    @Override
    public ShellCaseTeam getWinner() {
        int paint = 0;
        Set<ShellCaseTeam> winTeam = new HashSet<>();
        for(ShellCaseTeam ShellCaseTeam : this.getShellCaseTeams()){
            int teamPaint = ShellCaseTeam.getPaints();
            if(paint <= teamPaint){
                if(paint != teamPaint) winTeam.clear();
                winTeam.add(ShellCaseTeam);
                paint = teamPaint;
            }
        }
        
        if(winTeam.size() == 1){
            for(ShellCaseTeam team : winTeam) return team;
        }else{
            return null;
        }
        return null;
    }
}
