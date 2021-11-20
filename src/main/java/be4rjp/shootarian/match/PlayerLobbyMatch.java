package be4rjp.shootarian.match;

import be4rjp.shootarian.match.map.ShootarianMap;
import be4rjp.shootarian.match.team.ShootarianTeam;
import be4rjp.shootarian.player.ShootarianPlayer;

public class PlayerLobbyMatch extends Match{
    public PlayerLobbyMatch(ShootarianMap ShootarianMap) {
        super(ShootarianMap);
    }
    
    @Override
    public MatchType getType() {
        return MatchType.LOBBY;
    }
    
    @Override
    public void initializePlayer(ShootarianPlayer ShootarianPlayer) {
    
    }
    
    @Override
    public void finish() {
    
    }
    
    @Override
    public void initialize() {
    
    }
    
    @Override
    public boolean checkWin() {
        return false;
    }

    @Override
    public ShootarianTeam getWinner() {
        return null;
    }
}
