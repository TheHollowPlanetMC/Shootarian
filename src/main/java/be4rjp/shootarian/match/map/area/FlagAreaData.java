package be4rjp.shootarian.match.map.area;

import be4rjp.shootarian.match.ConquestMatch;
import be4rjp.shootarian.match.team.ShootarianTeam;

public class FlagAreaData {
    
    private final ConquestMatch match;
    
    private final FlagArea flagArea;
    
    private ShootarianTeam team;
    
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
            team = match.getShootarianTeams().get(1);
        }else if(territory < 0){
            team = match.getShootarianTeams().get(0);
        }else{
            team = null;
        }
    }
    
    public FlagArea getFlagArea() {return flagArea;}
    
    public int getTerritory() {return territory;}
    
    public ShootarianTeam getTeam() {return team;}
}
