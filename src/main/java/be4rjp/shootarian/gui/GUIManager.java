package be4rjp.shootarian.gui;

import be4rjp.shootarian.Shootarian;
import be4rjp.shootarian.match.Match;
import be4rjp.shootarian.match.team.ShootarianTeam;
import be4rjp.shootarian.player.ShootarianPlayer;
import be4rjp.shootarian.util.LocationUtil;
import org.bukkit.Location;

public class GUIManager {
    
    public static boolean isShowAllComponent(ShootarianPlayer shootarianPlayer){
        ShootarianTeam shootarianTeam = shootarianPlayer.getShootarianTeam();
        if(shootarianTeam == null) return true;
        if(shootarianTeam == Shootarian.getLobbyTeam()) return true;
        
        Match match = shootarianTeam.getMatch();
        Location location = match.getShootarianMap().getTeamLocation(match.getShootarianTeams().indexOf(shootarianTeam));
        return LocationUtil.distanceSquaredSafeDifferentWorld(location, shootarianPlayer.getLocation()) <= 500.0;
    }
    
}
