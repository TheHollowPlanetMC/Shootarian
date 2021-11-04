package be4rjp.shellcase.world;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.server.v1_15_R1.Chunk;
import net.minecraft.server.v1_15_R1.ChunkSection;
import net.minecraft.server.v1_15_R1.MCUtil;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.v1_15_R1.block.data.CraftBlockData;

public class AsyncWorld {
    
    private final World world;
    
    private final Long2ObjectOpenHashMap<Chunk> loadedChunks = new Long2ObjectOpenHashMap<>(8192, 0.5f);
    
    public void addLoadedChunk(Chunk chunk){
        this.loadedChunks.put(MCUtil.getCoordinateKey(chunk.getPos().x, chunk.getPos().z), chunk);
    }
    
    public boolean isLoaded(int x, int z){return this.loadedChunks.containsKey(MCUtil.getCoordinateKey(x, z));}
    
    public AsyncWorld(World world){
        this.world = world;
    }
    
    public World getWorld() {return world;}
    
    public boolean setType(Block block, BlockData blockData){
        world.setAutoSave(false);
        Chunk chunk = loadedChunks.get(MCUtil.getCoordinateKey(block.getX() >> 4, block.getZ() >> 4));
        
        if(chunk == null) return false;
    
        
        ChunkSection[] chunkSections = chunk.getSections();
        ChunkSection chunkSection = chunkSections[block.getY() >> 4];
    
        if(chunkSection == null) chunkSection = new ChunkSection(block.getY() >> 4 << 4);
        chunkSection.setType(block.getX() & 0xF, block.getY() & 0xF, block.getZ() & 0xF, ((CraftBlockData) blockData).getState(), false);
    
        chunkSections[block.getY() >> 4] = chunkSection;
        return true;
    }
}
