package be4rjp.shootarian.block;

import be4rjp.parallel.util.BlockPosition3i;
import be4rjp.parallel.util.ChunkLocation;
import be4rjp.shootarian.Shootarian;
import be4rjp.shootarian.match.Match;
import be4rjp.shootarian.match.map.structure.MapStructureData;
import be4rjp.shootarian.player.ShootarianPlayer;
import net.minecraft.server.v1_15_R1.Chunk;
import net.minecraft.server.v1_15_R1.PacketPlayOutMultiBlockChange;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 決められた時間間隔でブロックを非同期でアップデート為のするクラス
 */
public class BlockUpdater extends BukkitRunnable {
    
    //このアップデーターを使用する試合のインスタンス
    private final Match match;
    //更新するブロックと適用するデータのマップ
    private final Map<Block, BlockData> blockMap = new ConcurrentHashMap<>();
    //削除するブロック
    private final Set<Block> removeBlocks = ConcurrentHashMap.newKeySet();
    //崩壊状態にする建造物のデータ
    private final Set<MapStructureData> mapStructureData = ConcurrentHashMap.newKeySet();
    
    public BlockUpdater(Match match){
        this.match = match;
    }
    
    
    /**
     * ブロックを設置する
     * @param block 設置するブロック
     * @param blockData 設置するデータ
     */
    public synchronized void setBlock(Block block, BlockData blockData, ShootarianPlayer shootarianPlayer){
        blockMap.put(block, blockData);
    }
    
    /**
     * ブロックを設置する
     * @param block 設置するブロック
     * @param material 設置するデータ
     */
    public synchronized void setBlock(Block block, Material material, ShootarianPlayer shootarianPlayer){
        this.setBlock(block, material.createBlockData(), shootarianPlayer);
    }
    
    /**
     * 削除するブロックを追加する
     * @param block 削除するブロック
     */
    public synchronized void remove(Block block, ShootarianPlayer shootarianPlayer){
        this.removeBlocks.add(block);
    }
    
    /**
     * 崩壊させる建造物のデータを追加する
     * @param mapStructureData
     */
    public synchronized void breakStructure(MapStructureData mapStructureData){
        this.mapStructureData.add(mapStructureData);
    }
    
    
    @Override
    public synchronized void run() {
        //AsyncWorldでブロックを設置する
        Map<ChunkLocation, Set<Block>> blockChunkMap = new HashMap<>();
        for (Map.Entry<Block, BlockData> entry : blockMap.entrySet()) {
            Block block = entry.getKey();
            BlockData blockData = entry.getValue();
    
            match.getAsyncWorld().setType(block, blockData);
            Set<Block> blocks = blockChunkMap.computeIfAbsent(new ChunkLocation(block.getWorld(), block.getX(), block.getZ()), k -> new HashSet<>());
            blocks.add(block);
        }

        for(Block block : removeBlocks){
            match.getAsyncWorld().setType(block, Material.AIR.createBlockData());
            Set<Block> blocks = blockChunkMap.computeIfAbsent(new ChunkLocation(block.getWorld(), block.getX(), block.getZ()), k -> new HashSet<>());
            blocks.add(block);
        }

        for(MapStructureData mapStructureData : this.mapStructureData){
            for(Map.Entry<BlockPosition3i, BlockData> entry : mapStructureData.getMapStructure().getDefaultData().getBlockDataMap().entrySet()){
                BlockPosition3i relative = entry.getKey();
        
                Block block = mapStructureData.getMapStructure().getBaseLocation().getBukkitLocation().clone().add(relative.getX(), relative.getY(), relative.getZ()).getBlock();
                match.getAsyncWorld().setType(block, Material.AIR.createBlockData());
                Set<Block> blocks = blockChunkMap.computeIfAbsent(new ChunkLocation(block.getWorld(), block.getX(), block.getZ()), k -> new HashSet<>());
                blocks.add(block);
            }
            for(Map.Entry<BlockPosition3i, BlockData> entry : mapStructureData.getMapStructure().getCollapseData().getBlockDataMap().entrySet()){
                BlockPosition3i relative = entry.getKey();
                BlockData blockData = entry.getValue();
                
                Block block = mapStructureData.getMapStructure().getBaseLocation().getBukkitLocation().clone().add(relative.getX(), relative.getY(), relative.getZ()).getBlock();
                match.getAsyncWorld().setType(block, blockData);
                Set<Block> blocks = blockChunkMap.computeIfAbsent(new ChunkLocation(block.getWorld(), block.getX(), block.getZ()), k -> new HashSet<>());
                blocks.add(block);
            }
        }
        
        update(blockChunkMap);
        
        blockMap.clear();
        removeBlocks.clear();
        mapStructureData.clear();
    }
    
    /**
     * 非同期更新タスクをスタートさせる
     */
    public void start(){
        this.runTaskTimerAsynchronously(Shootarian.getPlugin(), 0, 5);
    }
    
    
    public void update(Map<ChunkLocation, Set<Block>> updateMap){
        for(Map.Entry<ChunkLocation, Set<Block>> entry : updateMap.entrySet()) {
            ChunkLocation chunk = entry.getKey();
            Set<Block> blocks = entry.getValue();
        
            short[] locations = new short[blocks.size()];
            int index = 0;
            for(Block block : blocks){
                short loc = (short) ((block.getX() & 0xF) << 12 | (block.getZ() & 0xF) << 8 | block.getY());
                locations[index] = loc;
                index++;
            }
            
            for(ShootarianPlayer shootarianPlayer : match.getPlayers()){
                Player player = shootarianPlayer.getBukkitPlayer();
                if(player != null) {
                    Chunk nmsChunk = match.getAsyncWorld().getLoadedChunk(chunk.x, chunk.z);
                    if(nmsChunk != null){
                        PacketPlayOutMultiBlockChange multiBlockChange = new PacketPlayOutMultiBlockChange(index, locations, nmsChunk);
                        shootarianPlayer.sendPacket(multiBlockChange);
                    }
                }
            }
        }
    }
}
