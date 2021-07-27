package be4rjp.shellcase.match;

import be4rjp.shellcase.match.map.ShellCaseMap;
import be4rjp.shellcase.match.team.ShellCaseTeam;
import be4rjp.shellcase.player.ShellCasePlayer;

public class PlayerLobbyMatch extends Match{
    public PlayerLobbyMatch(ShellCaseMap ShellCaseMap) {
        super(ShellCaseMap);
    }
    
    @Override
    public MatchType getType() {
        return MatchType.LOBBY;
    }
    
    @Override
    public void initializePlayer(ShellCasePlayer ShellCasePlayer) {
    
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
    public ShellCaseTeam getWinner() {
        return null;
    }
}
