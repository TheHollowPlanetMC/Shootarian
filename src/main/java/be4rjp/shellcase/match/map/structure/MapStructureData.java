package be4rjp.shellcase.match.map.structure;

import be4rjp.parallel.Config;
import be4rjp.parallel.Parallel;
import be4rjp.parallel.ParallelWorld;
import be4rjp.parallel.enums.UpdatePacketType;
import be4rjp.parallel.util.BlockPosition3i;
import be4rjp.shellcase.entity.AsyncFallingBlock;
import be4rjp.shellcase.match.Match;
import be4rjp.shellcase.util.BoundingBox;
import be4rjp.shellcase.util.particle.BlockParticle;
import org.bukkit.Particle;
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
    
        Vector basePosition = mapStructure.getBaseLocation().getBukkitLocation().toVector();
        this.boundingBox = new BoundingBox(basePosition, basePosition.clone().add(mapStructure.getBoundingBoxSize()));
        
        this.health = mapStructure.getMaxHealth();
    }
    
    
    public synchronized void giveDamage(int damage){
        if(health < 0) return;
        health -= damage;
        
        if(health < 0){
            List<Block> blocks = new ArrayList<>();
            for(BlockPosition3i relative : mapStructure.getCollapseData().getBlockDataMap().keySet()){
                Block block = mapStructure.getBaseLocation().getBukkitLocation().clone().add(relative.getX(), relative.getY(), relative.getZ()).getBlock();
                if(!block.getType().toString().endsWith("AIR")) blocks.add(block);
            }
            Collections.shuffle(blocks);
            
            int maxIndex = Math.min(blocks.size(), 25);
            for(int index = 0; index < maxIndex; index++){
                Block block = blocks.get(index);
                match.spawnParticle(new BlockParticle(Particle.BLOCK_CRACK, 3, 0, 0, 0, 1, block.getBlockData()), block.getLocation());
                AsyncFallingBlock asyncFallingBlock = new AsyncFallingBlock(match, block.getLocation(), block.getBlockData());
                asyncFallingBlock.spawn();
            }
    
            match.getBlockUpdater().breakStructure(this);
        }
    }
    
    public MapStructure getMapStructure() {return mapStructure;}
    
    public BoundingBox getBoundingBox() {return boundingBox;}
    
    public boolean isDead(){return this.health < 0;}
}
