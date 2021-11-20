package be4rjp.shootarian.match;

import be4rjp.shootarian.map.*;
import be4rjp.shootarian.map.component.MapComponent;
import be4rjp.shootarian.map.component.MapObjectiveComponent;
import be4rjp.shootarian.match.map.ConquestMap;
import be4rjp.shootarian.match.map.area.FlagArea;
import be4rjp.shootarian.match.map.area.FlagAreaData;
import be4rjp.shootarian.match.runnable.ConquestMatchRunnable;
import be4rjp.shootarian.match.team.ShootarianTeam;
import be4rjp.shootarian.player.ShootarianPlayer;
import be4rjp.shootarian.util.Position2i;
import org.bukkit.ChatColor;

import java.util.HashSet;
import java.util.Set;

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
    public void initializePlayer(ShootarianPlayer shootarianPlayer) {
        //None
    }
    
    @Override
    public void finish() {
        this.conquestStatusRenderer.cancel();
        super.finish();
    }
    
    
    @Override
    public void initialize() {
        this.matchRunnable = new ConquestMatchRunnable(this, 360);
        
        CanvasBuffer canvasBuffer = new CanvasBuffer(this.conquestMap.getCanvasData().getBytes());
        this.conquestStatusRenderer = new ConquestStatusRenderer(canvasBuffer);
        this.conquestStatusRenderer.start();
    
        CanvasData canvasData = this.conquestMap.getCanvasData();
        for(FlagArea flagArea : conquestMap.getFlagAreas()){
            FlagAreaData flagAreaData = new FlagAreaData(this, flagArea);
            
            Position2i pixelPosition = canvasData.locationToPixel(flagArea.getBoundingBox().getMin().getBlockX(), flagArea.getBoundingBox().getMin().getBlockZ());
            MapComponent component = new MapObjectiveComponent(flagAreaData, true, pixelPosition.x, pixelPosition.y, shootarianPlayer -> {
                double x = flagArea.getBoundingBox().getMin().getX() + ((flagArea.getBoundingBox().getMax().getX() - flagArea.getBoundingBox().getMin().getX()) * Math.random());
                double z = flagArea.getBoundingBox().getMin().getZ() + ((flagArea.getBoundingBox().getMax().getZ() - flagArea.getBoundingBox().getMin().getZ()) * Math.random());
                double y = flagArea.getBoundingBox().getMin().getY() + 1.5;
                
                shootarianPlayer.teleport(conquestMap.getWaitLocation().clone().set(x, y, z));
                
                String color = ChatColor.GRAY.toString();
                ShootarianTeam shootarianTeam = flagAreaData.getTeam();
                if(shootarianTeam != null) color = shootarianTeam.getShootarianColor().getChatColor().toString();
                
                shootarianPlayer.sendText("match-conquest-teleport", color, flagArea.getDisplayName());
            });
            this.conquestStatusRenderer.addMapComponent(component);
            
            this.flagAreaData.add(flagAreaData);
        }
    }
    
    @Override
    public boolean checkWin() {
        return this.getShootarianTeams().get(0).getPoints() >= this.conquestMap.getMaxTicket() || this.getShootarianTeams().get(1).getPoints() >= this.conquestMap.getMaxTicket();
    }
    
    @Override
    public ShootarianTeam getWinner() {
        int paint = 0;
        Set<ShootarianTeam> winTeam = new HashSet<>();
        for(ShootarianTeam shootarianTeam : this.getShootarianTeams()){
            int teamPoints = shootarianTeam.getPoints();
            if(paint <= teamPoints){
                if(paint != teamPoints) winTeam.clear();
                winTeam.add(shootarianTeam);
                paint = teamPoints;
            }
        }
        
        if(winTeam.size() == 1){
            for(ShootarianTeam team : winTeam) return team;
        }else{
            return null;
        }
        return null;
    }
    
    public Set<FlagAreaData> getFlagAreaData() {return flagAreaData;}
    
    public ConquestMap getConquestMap() {return conquestMap;}
}
