package be4rjp.shellcase.block;

import be4rjp.parallel.Config;
import be4rjp.parallel.Parallel;
import be4rjp.parallel.ParallelWorld;
import be4rjp.parallel.enums.UpdatePacketType;
import be4rjp.shellcase.ShellCase;
import be4rjp.shellcase.match.Match;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 決められた時間間隔でブロックを非同期でアップデート為のするクラス
 */
public class BlockUpdater extends BukkitRunnable {
    
    //このアップデーターを使用する試合のインスタンス
    private final Match match;
    //更新するブロックと適用するデータのマップ
    private final Map<Block, BlockData> blockMap = new ConcurrentHashMap<>();
    
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
    
    
    @Override
    public void run() {
        //Parallelを使ってプレイヤーごとにブロックを設置
        if(Config.getWorkType() == Config.WorkType.NORMAL) {
            match.getPlayers().forEach(sclatPlayer -> sclatPlayer.getParallelWorld().setBlocks(blockMap, UpdatePacketType.MULTI_BLOCK_CHANGE));
        }else{
            ParallelWorld.getParallelWorld("").setBlocks(blockMap, UpdatePacketType.MULTI_BLOCK_CHANGE);
        }
        
        blockMap.clear();
    }
    
    
    /**
     * 非同期更新タスクをスタートさせる
     */
    public void start(){
        this.runTaskTimerAsynchronously(ShellCase.getPlugin(), 0, 15);
    }
}
