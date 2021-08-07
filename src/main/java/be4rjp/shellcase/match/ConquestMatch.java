package be4rjp.shellcase.match;

import be4rjp.shellcase.match.map.ConquestMap;
import be4rjp.shellcase.match.map.area.FlagArea;
import be4rjp.shellcase.match.map.area.FlagAreaData;
import be4rjp.shellcase.match.runnable.ConquestMatchRunnable;
import be4rjp.shellcase.match.team.ShellCaseTeam;
import be4rjp.shellcase.player.ShellCasePlayer;

import java.util.HashSet;
import java.util.Set;

public class ConquestMatch extends Match{
    
    private final ConquestMap conquestMap;
    
    private final Set<FlagAreaData> flagAreaData = new HashSet<>();
    
    
    public ConquestMatch(ConquestMap conquestMap) {
        super(conquestMap);
        this.conquestMap = conquestMap;
    }
    
    @Override
    public MatchType getType() {
        return MatchType.CONQUEST;
    }
    
    @Override
    public void initializePlayer(ShellCasePlayer shellCasePlayer) {
        //None
    }
    
    @Override
    public void finish() {
        super.finish();
    }
    
    
    @Override
    public void initialize() {
        this.matchRunnable = new ConquestMatchRunnable(this, 180);
        
        for(FlagArea flagArea : conquestMap.getFlagAreas()){
            flagAreaData.add(new FlagAreaData(this, flagArea));
        }
    }
    
    @Override
    public boolean checkWin() {
        return this.getShellCaseTeams().get(0).getPoints() >= this.conquestMap.getMaxTicket() || this.getShellCaseTeams().get(1).getPoints() >= this.conquestMap.getMaxTicket();
    }
    
    @Override
    public ShellCaseTeam getWinner() {
        int paint = 0;
        Set<ShellCaseTeam> winTeam = new HashSet<>();
        for(ShellCaseTeam shellCaseTeam : this.getShellCaseTeams()){
            int teamPoints = shellCaseTeam.getPoints();
            if(paint <= teamPoints){
                if(paint != teamPoints) winTeam.clear();
                winTeam.add(shellCaseTeam);
                paint = teamPoints;
            }
        }
        
        if(winTeam.size() == 1){
            for(ShellCaseTeam team : winTeam) return team;
        }else{
            return null;
        }
        return null;
    }
    
    public Set<FlagAreaData> getFlagAreaData() {return flagAreaData;}
}
