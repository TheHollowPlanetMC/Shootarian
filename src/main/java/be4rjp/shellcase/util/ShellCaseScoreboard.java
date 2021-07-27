package be4rjp.shellcase.util;

import be4rjp.shellcase.ShellCase;
import be4rjp.shellcase.player.ShellCasePlayer;
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

public class ShellCaseScoreboard {
    
    //Bukkitのスコアボード
    private final Scoreboard scoreboard;
    //サイドバーのオブジェクト
    private final Objective objective;
    //プレイヤーと画面右スコアラインのマップ
    private final Map<ShellCasePlayer, List<String>> playerLineMap = new ConcurrentHashMap<>();
    //変更を加えたラインのマップ
    private final Map<ShellCasePlayer, Set<String>> playerRemoveLineMap = new ConcurrentHashMap<>();
    
    private final int sidebarSize;
    
    /**
     * スコアボードを作成
     * @param displayName 表示名
     * @param sidebarSize サイドバーの行数
     */
    public ShellCaseScoreboard(String displayName, int sidebarSize){
        ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
        this.sidebarSize = sidebarSize;
        this.scoreboard = scoreboardManager.getNewScoreboard();
        this.objective = scoreboard.registerNewObjective("ShellCaseSB", "ShellCaseSB", displayName);
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
    
        List<String> lines = new ArrayList<>();
        for(int index = 0; index < sidebarSize; index++){
            lines.add("ShellCaseSB" + index);
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
     * @param ShellCasePlayer 取得したいプレイヤー
     * @param index 取得したいインデックス
     * @return String
     */
    public synchronized String getSidebarLine(ShellCasePlayer ShellCasePlayer, int index){
        List<String> lines = playerLineMap.get(ShellCasePlayer);
        if(lines == null) return null;
        if(lines.size() <= index) return null;
        
        return lines.get(index);
    }
    
    
    /**
     * プレイヤーごとにサイドバーを設定します
     * @param ShellCasePlayer
     * @param lines
     */
    public synchronized void setSidebarLine(ShellCasePlayer ShellCasePlayer, List<String> lines){
        List<String> oldLines = playerLineMap.get(ShellCasePlayer);
        if(oldLines != null) {
            Set<String> removeLines = playerRemoveLineMap.get(ShellCasePlayer);
            if (removeLines == null) {
                removeLines = new ConcurrentSet<>();
                playerRemoveLineMap.put(ShellCasePlayer, removeLines);
            }
        
            for (String oldLine : oldLines){
                if(!lines.contains(oldLine)) removeLines.add(oldLine);
            }
        }
        
        playerLineMap.put(ShellCasePlayer, lines);
        ShellCasePlayer.setScoreBoard(this);
    }
    
    
    /**
     * サイドバーをアップデートする
     */
    public void updateSidebar(Set<ShellCasePlayer> players){
        for(ShellCasePlayer ShellCasePlayer : players){
            Set<String> removeLines = playerRemoveLineMap.get(ShellCasePlayer);
            
            if(removeLines != null) {
                for (String line : removeLines) {
                    PacketPlayOutScoreboardScore scorePacket = new PacketPlayOutScoreboardScore(ScoreboardServer.Action.REMOVE, "ShellCaseSB", line, 0);
                    ShellCasePlayer.sendPacket(scorePacket);
                }
                removeLines.clear();
            }
    
            for(int index = 0; index < sidebarSize; index++) {
                PacketPlayOutScoreboardScore scorePacket = new PacketPlayOutScoreboardScore(ScoreboardServer.Action.CHANGE, "ShellCaseSB", "ShellCaseSB" + index, sidebarSize - index - 1);
                ShellCasePlayer.sendPacket(scorePacket);
            }
        }
    }
}
