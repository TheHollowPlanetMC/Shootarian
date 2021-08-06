package be4rjp.shellcase.block;

import be4rjp.parallel.Config;
import be4rjp.parallel.Parallel;
import be4rjp.parallel.ParallelWorld;
import be4rjp.parallel.enums.UpdatePacketType;
import be4rjp.parallel.util.BlockPosition3i;
import be4rjp.shellcase.ShellCase;
import be4rjp.shellcase.match.Match;
import be4rjp.shellcase.match.map.structure.MapStructureData;
import be4rjp.shellcase.player.ShellCasePlayer;
import be4rjp.shellcase.world.AsyncWorld;
import io.netty.util.internal.ConcurrentSet;
import org.bukkit.Chunk;
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
    private final Set<Block> removeBlocks = new ConcurrentSet<>();
    //崩壊状態にする建造物のデータ
    private final Set<MapStructureData> mapStructureData = new ConcurrentSet<>();
    
    public BlockUpdater(Match match){
        this.match = match;
    }
    
    
    /**
     * ブロックを設置する
     * @param block 設置するブロック
     * @param blockData 設置するデータ
     */
    public void setBlock(Block block, BlockData blockData){
        blockMap.put(block, blockData);
    }
    
    /**
     * ブロックを設置する
     * @param block 設置するブロック
     * @param material 設置するデータ
     */
    public void setBlock(Block block, Material material){
        this.setBlock(block, material.createBlockData());
    }
    
    /**
     * 削除するブロックを追加する
     * @param block 削除するブロック
     */
    public void remove(Block block){this.removeBlocks.add(block);}
    
    /**
     * 崩壊させる建造物のデータを追加する
     * @param mapStructureData
     */
    public void breakStructure(MapStructureData mapStructureData){
        this.mapStructureData.add(mapStructureData);
    }
    
    
    @Override
    public void run() {
        //AsyncWorldでブロックを設置する
        Map<Chunk, Set<Block>> blockChunkMap = new HashMap<>();
        for (Map.Entry<Block, BlockData> entry : blockMap.entrySet()) {
            Block block = entry.getKey();
            BlockData blockData = entry.getValue();
    
            AsyncWorld.getAsyncWorld(block.getWorld()).setType(block, blockData);
            Set<Block> blocks = blockChunkMap.computeIfAbsent(block.getChunk(), k -> new HashSet<>());
            blocks.add(block);
        }

        for(Block block : removeBlocks){
            AsyncWorld.getAsyncWorld(block.getWorld()).setType(block, Material.AIR.createBlockData());
            Set<Block> blocks = blockChunkMap.computeIfAbsent(block.getChunk(), k -> new HashSet<>());
            blocks.add(block);
            ParallelWorld.getParallelWorld("").removeBlock(block);
        }

        for(MapStructureData mapStructureData : this.mapStructureData){
            for(Map.Entry<BlockPosition3i, BlockData> entry : mapStructureData.getMapStructure().getDefaultData().getBlockDataMap().entrySet()){
                BlockPosition3i relative = entry.getKey();
        
                Block block = mapStructureData.getMapStructure().getBaseLocation().getBukkitLocation().clone().add(relative.getX(), relative.getY(), relative.getZ()).getBlock();
                AsyncWorld.getAsyncWorld(block.getWorld()).setType(block, Material.AIR.createBlockData());
                Set<Block> blocks = blockChunkMap.computeIfAbsent(block.getChunk(), k -> new HashSet<>());
                blocks.add(block);
            }
            for(Map.Entry<BlockPosition3i, BlockData> entry : mapStructureData.getMapStructure().getCollapseData().getBlockDataMap().entrySet()){
                BlockPosition3i relative = entry.getKey();
                BlockData blockData = entry.getValue();
                
                Block block = mapStructureData.getMapStructure().getBaseLocation().getBukkitLocation().clone().add(relative.getX(), relative.getY(), relative.getZ()).getBlock();
                AsyncWorld.getAsyncWorld(block.getWorld()).setType(block, blockData);
                Set<Block> blocks = blockChunkMap.computeIfAbsent(block.getChunk(), k -> new HashSet<>());
                blocks.add(block);
            }
        }
        
        for(ShellCasePlayer shellCasePlayer : match.getPlayers()){
            Player player = shellCasePlayer.getBukkitPlayer();
            if(player != null) {
                shellCasePlayer.getParallelWorld().sendUpdatePacket(player, UpdatePacketType.MULTI_BLOCK_CHANGE, blockChunkMap);
            }
        }
        
        blockMap.clear();
        removeBlocks.clear();
        mapStructureData.clear();
    }
    
    
    /**
     * 非同期更新タスクをスタートさせる
     */
    public void start(){
        this.runTaskTimerAsynchronously(ShellCase.getPlugin(), 0, 5);
    }
}
