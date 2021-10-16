package be4rjp.shellcase.gui;

import be4rjp.shellcase.ShellCase;
import be4rjp.shellcase.match.Match;
import be4rjp.shellcase.match.team.ShellCaseTeam;
import be4rjp.shellcase.player.ShellCasePlayer;
import be4rjp.shellcase.util.LocationUtil;
import org.bukkit.Location;

public class GUIManager {
    
    public static boolean isShowAllComponent(ShellCasePlayer shellCasePlayer){
        ShellCaseTeam shellCaseTeam = shellCasePlayer.getShellCaseTeam();
        if(shellCaseTeam == null) return true;
        if(shellCaseTeam == ShellCase.getLobbyTeam()) return true;
        
        Match match = shellCaseTeam.getMatch();
        Location location = match.getShellCaseMap().getTeamLocation(match.getShellCaseTeams().indexOf(shellCaseTeam));
        return LocationUtil.distanceSquaredSafeDifferentWorld(location, shellCasePlayer.getLocation()) <= 500.0;
    }
    
}
