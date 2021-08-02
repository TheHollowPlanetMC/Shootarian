package be4rjp.shellcase.match.map.structure;

import be4rjp.parallel.Config;
import be4rjp.parallel.enums.UpdatePacketType;
import be4rjp.shellcase.match.Match;
import be4rjp.shellcase.util.BoundingBox;
import org.bukkit.util.Vector;

public class MapStructureData {
    
    private final Match match;
    
    private final MapStructure mapStructure;
    
    private final BoundingBox boundingBox;
    
    private int health;
    
    public MapStructureData(Match match, MapStructure mapStructure){
        this.match = match;
        this.mapStructure = mapStructure;
    
        Vector basePosition = mapStructure.getStructure().getBaseLocation().toVector();
        this.boundingBox = new BoundingBox(basePosition, basePosition.clone().add(mapStructure.getBoundingBoxSize()));
        
        this.health = mapStructure.getMaxHealth();
    }
    
    public void initialize(){
        if(Config.getWorkType() == Config.WorkType.NORMAL) {
            match.getPlayers().forEach(shellCasePlayer -> mapStructure.getStructure().setStructureData(
                    shellCasePlayer.getUUID(), mapStructure.getDefaultData(), UpdatePacketType.NO_UPDATE));
        }else{
            mapStructure.getStructure().setStructureData("", mapStructure.getDefaultData(), UpdatePacketType.NO_UPDATE);
        }
    }
    
    
    public synchronized void giveDamage(int damage){
        if(health < 0) return;
        
        health -= damage;
        
        if(health < 0){
            if(Config.getWorkType() == Config.WorkType.NORMAL) {
                match.getPlayers().forEach(shellCasePlayer -> mapStructure.getStructure().setStructureData(
                        shellCasePlayer.getUUID(), mapStructure.getCollapseData(), UpdatePacketType.MULTI_BLOCK_CHANGE));
            }else{
                mapStructure.getStructure().setStructureData("", mapStructure.getCollapseData(), UpdatePacketType.MULTI_BLOCK_CHANGE);
            }
        }
    }
    
    
    public BoundingBox getBoundingBox() {return boundingBox;}
}
