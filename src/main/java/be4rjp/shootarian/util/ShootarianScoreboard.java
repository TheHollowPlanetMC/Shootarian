package be4rjp.shootarian.util;

import be4rjp.shootarian.player.ShootarianPlayer;
import io.netty.util.internal.ConcurrentSet;
import net.minecraft.server.v1_15_R1.PacketPlayOutScoreboardScore;
import net.minecraft.server.v1_15_R1.ScoreboardServer;
import org.bukkit.Bukkit;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ShootarianScoreboard {
    
    //Bukkitのスコアボード
    private final Scoreboard scoreboard;
    //サイドバーのオブジェクト
    private final Objective objective;
    //プレイヤーと画面右スコアラインのマップ
    private final Map<ShootarianPlayer, List<String>> playerLineMap = new ConcurrentHashMap<>();
    //変更を加えたラインのマップ
    private final Map<ShootarianPlayer, Set<String>> playerRemoveLineMap = new ConcurrentHashMap<>();
    
    private final int sidebarSize;
    
    /**
     * スコアボードを作成
     * @param displayName 表示名
     * @param sidebarSize サイドバーの行数
     */
    public ShootarianScoreboard(String displayName, int sidebarSize){
        ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
        this.sidebarSize = sidebarSize;
        this.scoreboard = scoreboardManager.getNewScoreboard();
        this.objective = scoreboard.registerNewObjective("ShootarianSB", "ShootarianSB", displayName);
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
    
        List<String> lines = new ArrayList<>();
        for(int index = 0; index < sidebarSize; index++){
            lines.add("ShootarianSB" + index);
        }
        ObjectiveUtil.setLine(objective, lines);
    }
    
    
    /**
     * Bukkitのスコアボードを取得する
     * @return Scoreboard
     */
    public Scoreboard getBukkitScoreboard(){return this.scoreboard;}
    
    
    /**
     * プレイヤーごとにサイドバーのラインの文字列を取得します
     * @param ShootarianPlayer 取得したいプレイヤー
     * @param index 取得したいインデックス
     * @return String
     */
    public synchronized String getSidebarLine(ShootarianPlayer ShootarianPlayer, int index){
        List<String> lines = playerLineMap.get(ShootarianPlayer);
        if(lines == null) return null;
        if(lines.size() <= index) return null;
        
        return lines.get(index);
    }
    
    
    /**
     * プレイヤーごとにサイドバーを設定します
     * @param ShootarianPlayer
     * @param lines
     */
    public synchronized void setSidebarLine(ShootarianPlayer ShootarianPlayer, List<String> lines){
        List<String> oldLines = playerLineMap.get(ShootarianPlayer);
        if(oldLines != null) {
            Set<String> removeLines = playerRemoveLineMap.get(ShootarianPlayer);
            if (removeLines == null) {
                removeLines = new ConcurrentSet<>();
                playerRemoveLineMap.put(ShootarianPlayer, removeLines);
            }
        
            for (String oldLine : oldLines){
                if(!lines.contains(oldLine)) removeLines.add(oldLine);
            }
        }
        
        playerLineMap.put(ShootarianPlayer, lines);
        ShootarianPlayer.setScoreBoard(this);
    }
    
    
    /**
     * サイドバーをアップデートする
     */
    public void updateSidebar(Set<ShootarianPlayer> players){
        for(ShootarianPlayer ShootarianPlayer : players){
            Set<String> removeLines = playerRemoveLineMap.get(ShootarianPlayer);
            
            if(removeLines != null) {
                for (String line : removeLines) {
                    PacketPlayOutScoreboardScore scorePacket = new PacketPlayOutScoreboardScore(ScoreboardServer.Action.REMOVE, "ShootarianSB", line, 0);
                    ShootarianPlayer.sendPacket(scorePacket);
                }
                removeLines.clear();
            }
    
            for(int index = 0; index < sidebarSize; index++) {
                PacketPlayOutScoreboardScore scorePacket = new PacketPlayOutScoreboardScore(ScoreboardServer.Action.CHANGE, "ShootarianSB", "ShootarianSB" + index, sidebarSize - index - 1);
                ShootarianPlayer.sendPacket(scorePacket);
            }
        }
    }
}
