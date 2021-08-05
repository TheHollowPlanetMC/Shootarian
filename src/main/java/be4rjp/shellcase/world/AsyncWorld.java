package be4rjp.shellcase.world;

import be4rjp.parallel.util.ChunkPosition;
import net.minecraft.server.v1_15_R1.ChunkSection;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.v1_15_R1.CraftChunk;
import org.bukkit.craftbukkit.v1_15_R1.block.data.CraftBlockData;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AsyncWorld {
    
    private static Map<World, AsyncWorld> asyncWorldMap = new ConcurrentHashMap<>();
    
    public synchronized static AsyncWorld getAsyncWorld(World world){
        if(asyncWorldMap.containsKey(world)) return asyncWorldMap.get(world);
        return new AsyncWorld(world);
    }
    
    public synchronized static Collection<AsyncWorld> getWorlds(){return asyncWorldMap.values();}
    
    
    
    private final World world;
    
    private final Map<ChunkPosition, Map<Block, BlockData>> chunkPositionBlockMap = new ConcurrentHashMap<>();
    
    
    private AsyncWorld(World world){
        this.world = world;
    }
    
    public World getWorld() {return world;}
    
    public boolean setType(Block block, BlockData blockData){
        world.setAutoSave(false);
        Chunk chunk = world.getChunkAt(block.getX() >> 4, block.getZ() >> 4);
        
        if(chunk.isLoaded()) {
            ChunkSection[] chunkSections = ((CraftChunk) chunk).getHandle().getSections();
            ChunkSection chunkSection = chunkSections[block.getY() >> 4];
            
            if(chunkSection == null) chunkSection = new ChunkSection(block.getY() >> 4 << 4);
            chunkSection.setType(block.getX() & 0xF, block.getY() & 0xF, block.getZ() & 0xF, ((CraftBlockData) blockData).getState(), false);
            
            chunkSections[block.getY() >> 4] = chunkSection;
            return true;
        }else{
            ChunkPosition chunkPosition = new ChunkPosition(block.getX(), block.getZ());
            Map<Block, BlockData> blockDataMap = chunkPositionBlockMap.computeIfAbsent(chunkPosition, k -> new ConcurrentHashMap<>());
            blockDataMap.put(block, blockData);
            return false;
        }
    }
    
    public Map<ChunkPosition, Map<Block, BlockData>> getChunkPositionBlockMap() {return chunkPositionBlockMap;}
}
