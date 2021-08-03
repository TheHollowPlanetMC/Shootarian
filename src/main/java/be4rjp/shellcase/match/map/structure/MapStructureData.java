package be4rjp.shellcase.match.map.structure;

import be4rjp.parallel.Config;
import be4rjp.parallel.Parallel;
import be4rjp.parallel.ParallelWorld;
import be4rjp.parallel.enums.UpdatePacketType;
import be4rjp.parallel.util.BlockPosition3i;
import be4rjp.shellcase.entity.AsyncFallingBlock;
import be4rjp.shellcase.match.Match;
import be4rjp.shellcase.util.BoundingBox;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.util.Vector;

import java.util.*;

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
            List<Block> blocks = new ArrayList<>();
            for(BlockPosition3i relative : mapStructure.getCollapseData().getBlockDataMap().keySet()){
                Block block = mapStructure.getStructure().getBaseLocation().clone().add(relative.getX(), relative.getY(), relative.getZ()).getBlock();
                if(Config.getWorkType() == Config.WorkType.NORMAL) {
                
                } else {
                    if(ParallelWorld.getParallelWorld("").getBlockData(block) != null) blocks.add(block);
                }
            }
            Collections.shuffle(blocks);
            
            int maxIndex = Math.min(blocks.size(), 20);
            for(int index = 0; index < maxIndex; index++){
                Block block = blocks.get(index);
                if(Config.getWorkType() == Config.WorkType.NORMAL) {
                
                } else {
                    BlockData blockData = ParallelWorld.getParallelWorld("").getBlockData(block);
                    AsyncFallingBlock asyncFallingBlock = new AsyncFallingBlock(match, block.getLocation(), blockData);
                    asyncFallingBlock.spawn();
                }
            }
    
            match.getBlockUpdater().breakStructure(this);
        }
    }
    
    public void setCollapseData(){
        if(health < 0){
            if(Config.getWorkType() == Config.WorkType.NORMAL) {
                match.getPlayers().forEach(shellCasePlayer -> mapStructure.getStructure().setStructureData(
                        shellCasePlayer.getUUID(), mapStructure.getCollapseData(), UpdatePacketType.NO_UPDATE));
            }else{
                mapStructure.getStructure().setStructureData("", mapStructure.getCollapseData(), UpdatePacketType.NO_UPDATE);
            }
        }
    }
    
    public MapStructure getMapStructure() {return mapStructure;}
    
    public BoundingBox getBoundingBox() {return boundingBox;}
    
    public boolean isDead(){return this.health < 0;}
}
