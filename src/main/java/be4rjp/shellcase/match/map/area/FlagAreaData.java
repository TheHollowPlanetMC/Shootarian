package be4rjp.shellcase.match.map.area;

import be4rjp.shellcase.match.ConquestMatch;
import be4rjp.shellcase.match.team.ShellCaseTeam;

public class FlagAreaData {
    
    private final ConquestMatch match;
    
    private final FlagArea flagArea;
    
    private ShellCaseTeam team;
    
    private int territory = 0;
    
    public FlagAreaData(ConquestMatch match, FlagArea flagArea){
        this.match = match;
        this.flagArea = flagArea;
    }
    
    public synchronized void addTerritory(int add){
        if(territory + add > 100){
            territory = 100;
        }else if(territory + add < -100){
            territory = -100;
        }else{
            territory += add;
        }
        
        if(territory > 0){
            team = match.getShellCaseTeams().get(1);
        }else if(territory < 0){
            team = match.getShellCaseTeams().get(0);
        }else{
            team = null;
        }
    }
    
    public FlagArea getFlagArea() {return flagArea;}
    
    public int getTerritory() {return territory;}
    
    public ShellCaseTeam getTeam() {return team;}
}
