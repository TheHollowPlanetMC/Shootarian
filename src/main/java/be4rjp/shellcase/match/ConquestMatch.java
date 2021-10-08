package be4rjp.shellcase.match;

import be4rjp.shellcase.ShellCase;
import be4rjp.shellcase.map.*;
import be4rjp.shellcase.map.component.MapObjectiveComponent;
import be4rjp.shellcase.map.component.MapTextComponent;
import be4rjp.shellcase.match.map.ConquestMap;
import be4rjp.shellcase.match.map.area.FlagArea;
import be4rjp.shellcase.match.map.area.FlagAreaData;
import be4rjp.shellcase.match.runnable.ConquestMatchRunnable;
import be4rjp.shellcase.match.team.ShellCaseTeam;
import be4rjp.shellcase.player.ShellCasePlayer;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_15_R1.map.CraftMapView;
import org.bukkit.map.MapView;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class ConquestMatch extends Match{
    
    private final ConquestMap conquestMap;
    
    private final Set<FlagAreaData> flagAreaData = new HashSet<>();
    
    private ConquestStatusRenderer conquestStatusRenderer;
    
    public ConquestStatusRenderer getConquestStatusRenderer() {return conquestStatusRenderer;}
    
    
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
        
        CanvasBuffer canvasBuffer = new CanvasBuffer(new byte[128 * 128]);
        this.conquestStatusRenderer = new ConquestStatusRenderer(canvasBuffer);
        this.conquestStatusRenderer.addMapComponent(new MapObjectiveComponent(" A ", true, 10, 10, () -> {}));
        this.conquestStatusRenderer.start();
        
        this.getPlayers().forEach(shellCasePlayer -> {
            PlayerGUIRenderer playerGUIRenderer = new ConquestPlayerClickableGUIRenderer(shellCasePlayer, conquestStatusRenderer);
            playerGUIRenderer.addMapComponent(new MapTextComponent("Test", true, 50, 50, () -> {}));
            playerGUIRenderer.start();
            shellCasePlayer.setPlayerGUIRenderer(playerGUIRenderer);
        });
        
        for(FlagArea flagArea : conquestMap.getFlagAreas()){
            FlagAreaData flagAreaData = new FlagAreaData(this, flagArea);
            
            this.flagAreaData.add(flagAreaData);
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
