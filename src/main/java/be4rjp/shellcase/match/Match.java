package be4rjp.shellcase.match;

import be4rjp.shellcase.ShellCase;
import be4rjp.shellcase.block.BlockUpdater;
import be4rjp.shellcase.data.settings.Settings;
import be4rjp.shellcase.entity.ShellCaseEntity;
import be4rjp.shellcase.entity.ShellCaseEntityTickRunnable;
import be4rjp.shellcase.language.Lang;
import be4rjp.shellcase.match.map.ShellCaseMap;
import be4rjp.shellcase.match.runnable.MatchRunnable;
import be4rjp.shellcase.match.team.ShellCaseTeam;
import be4rjp.shellcase.player.ObservableOption;
import be4rjp.shellcase.player.ShellCasePlayer;
import be4rjp.shellcase.util.particle.ShellCaseParticle;
import be4rjp.shellcase.util.ShellCaseScoreboard;
import be4rjp.shellcase.util.ShellCaseSound;
import io.netty.util.internal.ConcurrentSet;
import net.minecraft.server.v1_15_R1.Packet;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.*;

import java.util.*;

/**
 * 各試合の親クラス
 */
public abstract class Match {
    
    //この試合のマップ
    protected final ShellCaseMap ShellCaseMap;
    //状態
    protected MatchStatus matchStatus = MatchStatus.WAITING;
    //試合のスケジューラー
    protected MatchRunnable matchRunnable;
    //この試合のチーム
    protected List<ShellCaseTeam> ShellCaseTeams = new ArrayList<>();
    //この試合のブロックアップデーター
    protected BlockUpdater blockUpdater = new BlockUpdater(this);
    //ShellCaseTeamとスコアボードのチームのマップ
    protected Map<ShellCaseTeam, Team> teamMap = new HashMap<>();
    //試合中に動作するスケジューラー
    protected Set<BukkitRunnable> runnableSet = new ConcurrentSet<>();
    //エンティティ
    protected Set<ShellCaseEntity> ShellCaseEntities = new ConcurrentSet<>();
    //エンティティの実行用tickRunnable
    protected ShellCaseEntityTickRunnable entityTickRunnable = new ShellCaseEntityTickRunnable(this);
    
    //試合のスコアボード
    protected final ShellCaseScoreboard scoreboard;
    

    public Match(ShellCaseMap ShellCaseMap){
        this.ShellCaseMap = ShellCaseMap;

        this.scoreboard = new ShellCaseScoreboard("§6§lshellcase§r " + ShellCase.VERSION, 10);
    }

    public abstract MatchType getType();
    
    /**
     * 試合を開始
     */
    public void start(){
        this.matchRunnable.start();
        this.matchStatus = MatchStatus.IN_PROGRESS;
        this.blockUpdater.start();
        
        this.getPlayers().forEach(this::initializePlayer);
        this.entityTickRunnable.start();
    }
    
    /**
     * 試合開始時のプレイヤーの準備処理
     * @param ShellCasePlayer
     */
    public abstract void initializePlayer(ShellCasePlayer ShellCasePlayer);
    
    /**
     * スケジューラーを登録する
     * @param bukkitRunnable
     */
    public void addBukkitRunnable(BukkitRunnable bukkitRunnable){runnableSet.add(bukkitRunnable);}
    
    /**
     * 終了処理
     */
    public void finish(){
        this.scoreboard.getBukkitScoreboard().clearSlot(DisplaySlot.SIDEBAR);
        this.matchStatus = MatchStatus.FINISHED;
        
        for(BukkitRunnable runnable : runnableSet){
            try {
                runnable.cancel();
            }catch (Exception e){/**/}
        }
        
        try {
            this.matchRunnable.cancel();
        }catch (Exception e){/**/}
        
        this.getPlayers().forEach(ShellCasePlayer::clearGunWeaponTasks);
    }
    
    /**
     * 後片付け
     */
    public void end(){
        this.getPlayers().forEach(ShellCasePlayer::reset);
    
        try {
            this.entityTickRunnable.cancel();
        }catch (Exception e){/**/}
        this.ShellCaseEntities.clear();
    }
    
    /**
     * 試合の初期化およびセットアップ
     */
    public abstract void initialize();
    
    /**
     * この試合のゲームマップを取得する
     * @return ShellCaseMap
     */
    public ShellCaseMap getShellCaseMap() {return ShellCaseMap;}
    
    /**
     * この試合のスコアボードを取得する
     * @return Scoreboard
     */
    public ShellCaseScoreboard getScoreboard() {return scoreboard;}
    
    /**
     * 試合のステータスを取得する
     * @return
     */
    public MatchStatus getMatchStatus() {return matchStatus;}
    
    
    /**
     * ランダムにチームを取得する
     * @return
     */
    public ShellCaseTeam getRandomTeam() {return ShellCaseTeams.get(new Random().nextInt(ShellCaseTeams.size()));}
    
    /**
     * 試合のステータスを設定する
     * @param matchStatus
     */
    public void setMatchStatus(MatchStatus matchStatus) {this.matchStatus = matchStatus;}
    
    /**
     * この試合のスケジューラーをスタートさせる
     */
    public void startMatchRunnable(){
        this.matchRunnable.runTaskTimerAsynchronously(ShellCase.getPlugin(), 0, 20);
    }

    /**
     * この試合のスケジューラーを停止させる
     */
    public void stopMatchRunnable(){
        this.matchRunnable.cancel();
    }
    
    /**
     * この試合で動作しているエンティティを取得する
     * @return
     */
    public Set<ShellCaseEntity> getShellCaseEntities() {return ShellCaseEntities;}

    /**
     * 試合終了判定
     * @return boolean 試合が終了したかどうか
     */
    public abstract boolean checkWin();
    
    
    /**
     * 試合に勝利したチームを取得する。
     * 引き分けの場合はnull
     * @return ShellCaseTeam
     */
    public abstract ShellCaseTeam getWinner();
    
    
    /**
     * この試合に存在する全てのチームを取得する
     * @return List<ShellCaseTeam>
     */
    public List<ShellCaseTeam> getShellCaseTeams() {return ShellCaseTeams;}
    
    
    /**
     * チームを追加します
     * @param ShellCaseTeam 追加するチーム
     */
    public void addShellCaseTeam(ShellCaseTeam ShellCaseTeam){this.ShellCaseTeams.add(ShellCaseTeam);}
    
    
    /**
     * プレイヤーをチームのスポーン場所にテレポートさせます
     * @param ShellCasePlayer
     */
    public void teleportToTeamLocation(ShellCasePlayer ShellCasePlayer){
        ShellCaseTeam ShellCaseTeam = ShellCasePlayer.getShellCaseTeam();
        if(ShellCaseTeam == null) return;
    
        int index = this.getShellCaseTeams().indexOf(ShellCaseTeam);
        ShellCasePlayer.teleport(ShellCaseMap.getTeamLocation(index));
    }


    /**
     * この試合に参加しているプレイヤーを取得する
     * @return Set<ShellCasePlayer>
     */
    public Set<ShellCasePlayer> getPlayers(){
        Set<ShellCasePlayer> players = new HashSet<>();
        ShellCaseTeams.forEach(ShellCaseTeam -> players.addAll(ShellCaseTeam.getTeamMembers()));
        return players;
    }
    
    
    /**
     * この試合に参加しているプレイヤー全員に音を聞かせる
     * @param sound サウンド
     * @param location 再生する座標
     */
    public void playSound(ShellCaseSound sound, Location location){this.getPlayers().forEach(ShellCasePlayer -> ShellCasePlayer.playSound(sound, location));}
    
    /**
     * この試合に参加しているプレイヤー全員に音を聞かせる
     * @param sound サウンド
     */
    public void playSound(ShellCaseSound sound){this.getPlayers().forEach(ShellCasePlayer -> ShellCasePlayer.playSound(sound));}
    
    /**
     * この試合に参加しているプレイヤー全員にパーティクルを表示する
     * @param particle パーティクル
     * @param location パーティクルを表示する座標
     */
    public void spawnParticle(ShellCaseParticle particle, Location location){this.getPlayers().forEach(ShellCasePlayer -> ShellCasePlayer.spawnParticle(particle, location));}

    /**
     * この試合に参加しているプレイヤー全員にパーティクルを表示する
     * @param particle パーティクル
     * @param location パーティクルを表示する座標
     * @param settings パーティクルを表示するかどうかの設置項目
     */
    public void spawnParticle(ShellCaseParticle particle, Location location, Settings settings){this.getPlayers().forEach(ShellCasePlayer -> ShellCasePlayer.spawnParticle(particle, location, settings));}
    
    /**
     * この試合に参加しているプレイヤー全員にパケットを送信する
     * @param packet 送信するパケット
     */
    public void sendPacket(Packet<?> packet){this.getPlayers().forEach(ShellCasePlayer -> ShellCasePlayer.sendPacket(packet));}
    
    
    
    
    /**
     * 試合に参加しているプレイヤー全員に、表示するプレイヤーを設定する
     * @param option ObservableOption
     */
    public void setPlayerObservableOption(ObservableOption option){
        this.getPlayers().forEach(ShellCasePlayer -> ShellCasePlayer.setObservableOption(option));
    }
    
    
    public enum MatchType{
        LOBBY("§6§lロビー", "§6§lLobby"),
        CONQUEST("§6§lコンクエスト", "§6§lConquest");
        
        private final HashMap<Lang, String> displayName;
    
        MatchType(String ja_JP, String en_US){
            displayName = new HashMap<>();
            displayName.put(Lang.ja_JP, ja_JP);
            displayName.put(Lang.en_US, en_US);
        }
        
        /**
         * 表示名を取得する
         * @return String
         */
        public String getDisplayName(Lang lang) {
            String name = displayName.get(lang);
            if(name == null){
                return "No name.";
            }else{
                return name;
            }
        }
    }
    
    
    public enum MatchStatus{
        WAITING,
        IN_PROGRESS,
        FINISHED
    }
}
