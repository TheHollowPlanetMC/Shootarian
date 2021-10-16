package be4rjp.shellcase.match;

import be4rjp.shellcase.ShellCase;
import be4rjp.shellcase.map.*;
import be4rjp.shellcase.map.component.MapComponent;
import be4rjp.shellcase.map.component.MapObjectiveComponent;
import be4rjp.shellcase.map.component.MapTextComponent;
import be4rjp.shellcase.match.map.ConquestMap;
import be4rjp.shellcase.match.map.area.FlagArea;
import be4rjp.shellcase.match.map.area.FlagAreaData;
import be4rjp.shellcase.match.runnable.ConquestMatchRunnable;
import be4rjp.shellcase.match.team.ShellCaseTeam;
import be4rjp.shellcase.player.ShellCasePlayer;
import be4rjp.shellcase.util.Position2i;
import be4rjp.shellcase.util.math.Vec2f;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
        this.conquestStatusRenderer.cancel();
        super.finish();
    }
    
    
    @Override
    public void initialize() {
        this.matchRunnable = new ConquestMatchRunnable(this, 180);
        
        CanvasBuffer canvasBuffer = new CanvasBuffer(this.conquestMap.getCanvasData().getBytes());
        this.conquestStatusRenderer = new ConquestStatusRenderer(canvasBuffer);
        this.conquestStatusRenderer.start();
    
        CanvasData canvasData = this.conquestMap.getCanvasData();
        for(FlagArea flagArea : conquestMap.getFlagAreas()){
            FlagAreaData flagAreaData = new FlagAreaData(this, flagArea);
            
            Position2i pixelPosition = canvasData.locationToPixel(flagArea.getBoundingBox().getMin().getBlockX(), flagArea.getBoundingBox().getMin().getBlockZ());
            MapComponent component = new MapObjectiveComponent(flagAreaData, true, pixelPosition.x, pixelPosition.y, shellCasePlayer -> {
                double x = flagArea.getBoundingBox().getMin().getX() + ((flagArea.getBoundingBox().getMax().getX() - flagArea.getBoundingBox().getMin().getX()) * Math.random());
                double z = flagArea.getBoundingBox().getMin().getZ() + ((flagArea.getBoundingBox().getMax().getZ() - flagArea.getBoundingBox().getMin().getZ()) * Math.random());
                double y = flagArea.getBoundingBox().getMin().getY() + 1.5;
                
                shellCasePlayer.teleport(conquestMap.getWaitLocation().clone().set(x, y, z));
                
                String color = ChatColor.GRAY.toString();
                ShellCaseTeam shellCaseTeam = flagAreaData.getTeam();
                if(shellCaseTeam != null) color = shellCaseTeam.getShellCaseColor().getChatColor().toString();
                
                shellCasePlayer.sendText("match-conquest-teleport", color, flagArea.getDisplayName());
            });
            this.conquestStatusRenderer.addMapComponent(component);
            
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
    
    public ConquestMap getConquestMap() {return conquestMap;}
}
