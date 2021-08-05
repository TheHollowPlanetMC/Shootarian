package be4rjp.shellcase.world;

import be4rjp.parallel.util.ChunkPosition;
import be4rjp.shellcase.ShellCase;
import net.minecraft.server.v1_15_R1.Chunk;
import net.minecraft.server.v1_15_R1.ChunkSection;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.v1_15_R1.CraftChunk;
import org.bukkit.craftbukkit.v1_15_R1.block.data.CraftBlockData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;

public class ChunkEventListener implements Listener {
    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event){
        
        new BukkitRunnable() {
            @Override
            public void run() {
                AsyncWorld asyncWorld = AsyncWorld.getAsyncWorld(event.getWorld());
                ChunkPosition chunkPosition = new ChunkPosition(event.getChunk().getX() << 4, event.getChunk().getZ() << 4);
                Map<Block, BlockData> blockDataMap = asyncWorld.getChunkPositionBlockMap().get(chunkPosition);
                
                if(blockDataMap == null) return;
                
                Chunk nmsChunk = ((CraftChunk) event.getChunk()).getHandle();
                ChunkSection[] chunkSections = nmsChunk.getSections();
                
                for(Map.Entry<Block, BlockData> entry : blockDataMap.entrySet()) {
                    Block block = entry.getKey();
                    BlockData blockData = entry.getValue();
                    
                    ChunkSection chunkSection = chunkSections[block.getY() >> 4];
                    
                    if (chunkSection == null) chunkSection = new ChunkSection(block.getY() >> 4 << 4);
                    chunkSection.setType(block.getX() & 0xF, block.getY() & 0xF, block.getZ() & 0xF, ((CraftBlockData) blockData).getState(), false);
                    
                    chunkSections[block.getY() >> 4] = chunkSection;
                }
    
                asyncWorld.getChunkPositionBlockMap().remove(chunkPosition);
            }
        }.runTaskAsynchronously(ShellCase.getPlugin());
    }
}
