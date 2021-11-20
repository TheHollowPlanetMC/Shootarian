package be4rjp.shootarian.match;

import be4rjp.shootarian.Shootarian;
import be4rjp.shootarian.block.BlockUpdater;
import be4rjp.shootarian.data.settings.Settings;
import be4rjp.shootarian.entity.AsyncEntityTickRunnable;
import be4rjp.shootarian.entity.ShootarianEntity;
import be4rjp.shootarian.entity.ShootarianEntityTickRunnable;
import be4rjp.shootarian.language.Lang;
import be4rjp.shootarian.match.map.AsyncMapLoader;
import be4rjp.shootarian.match.map.ShootarianMap;
import be4rjp.shootarian.match.map.structure.MapStructure;
import be4rjp.shootarian.match.map.structure.MapStructureData;
import be4rjp.shootarian.match.runnable.MatchRunnable;
import be4rjp.shootarian.match.team.ShootarianTeam;
import be4rjp.shootarian.player.ObservableOption;
import be4rjp.shootarian.player.ShootarianPlayer;
import be4rjp.shootarian.util.LocationUtil;
import be4rjp.shootarian.util.particle.ShootarianParticle;
import be4rjp.shootarian.util.ShootarianScoreboard;
import be4rjp.shootarian.util.ShootarianSound;
import be4rjp.shootarian.world.AsyncWorld;
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
    protected final ShootarianMap shootarianMap;
    //状態
    protected MatchStatus matchStatus = MatchStatus.WAITING;
    //試合のスケジューラー
    protected MatchRunnable matchRunnable;
    //この試合のチーム
    protected List<ShootarianTeam> shootarianTeams = new ArrayList<>();
    //この試合のブロックアップデーター
    protected BlockUpdater blockUpdater = new BlockUpdater(this);
    //ShootarianTeamとスコアボードのチームのマップ
    protected Map<ShootarianTeam, Team> teamMap = new HashMap<>();
    //試合中に動作するスケジューラー
    protected Set<BukkitRunnable> runnableSet = new ConcurrentSet<>();
    //エンティティ
    protected Set<ShootarianEntity> shootarianEntities = new ConcurrentSet<>();
    //AsyncEntity
    protected Set<ShootarianEntity> asyncEntities = new ConcurrentSet<>();
    //エンティティの実行用tickRunnable
    protected ShootarianEntityTickRunnable entityTickRunnable = new ShootarianEntityTickRunnable(this);
    //AsyncEntity
    protected AsyncEntityTickRunnable asyncEntityTickRunnable = new AsyncEntityTickRunnable(this);
    //建造物のデータ
    protected Set<MapStructureData> mapStructureData = new HashSet<>();
    //AsyncWorld
    protected AsyncWorld asyncWorld = null;
    
    //試合のスコアボード
    protected final ShootarianScoreboard scoreboard;
    

    public Match(ShootarianMap ShootarianMap){
        this.shootarianMap = ShootarianMap;

        this.scoreboard = new ShootarianScoreboard("§6§lShootarian Engine§r " + Shootarian.VERSION, 20);
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
        this.entityTickRunnable.setWorld(this.shootarianMap.getMapRange().getFirstLocation().getBukkitLocation().getWorld());
        this.entityTickRunnable.start();
        this.asyncEntityTickRunnable.start();
    }
    
    /**
     * マップのロード
     */
    public AsyncMapLoader loadGameMap(){
        for(MapStructure mapStructure : getShootarianMap().getMapStructures()){
            MapStructureData mapStructureData = new MapStructureData(this, mapStructure);
            this.mapStructureData.add(mapStructureData);
        }
        
        return AsyncMapLoader.startLoad(this, getShootarianMap().getMapRange());
    }
    
    /**
     * 試合開始時のプレイヤーの準備処理
     * @param ShootarianPlayer
     */
    public abstract void initializePlayer(ShootarianPlayer ShootarianPlayer);
    
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
        
        this.getPlayers().forEach(ShootarianPlayer::clearGunWeaponTasks);
    }
    
    /**
     * 後片付け
     */
    public void end(){
        this.getPlayers().forEach(ShootarianPlayer::reset);
    
        try {
            this.entityTickRunnable.cancel();
        }catch (Exception e){/**/}
        try {
            this.asyncEntityTickRunnable.cancel();
        }catch (Exception e){/**/}
        this.shootarianEntities.clear();
        this.asyncEntities.clear();
    }
    
    /**
     * 試合の初期化およびセットアップ
     */
    public abstract void initialize();
    
    /**
     * この試合のゲームマップを取得する
     * @return ShootarianMap
     */
    public ShootarianMap getShootarianMap() {return shootarianMap;}
    
    /**
     * この試合のスコアボードを取得する
     * @return Scoreboard
     */
    public ShootarianScoreboard getScoreboard() {return scoreboard;}
    
    /**
     * 試合のステータスを取得する
     * @return
     */
    public MatchStatus getMatchStatus() {return matchStatus;}
    
    /**
     * 建造物のデータを取得する
     * @return
     */
    public Set<MapStructureData> getMapStructureData() {return mapStructureData;}
    
    /**
     * ランダムにチームを取得する
     * @return
     */
    public ShootarianTeam getRandomTeam() {return shootarianTeams.get(new Random().nextInt(shootarianTeams.size()));}
    
    /**
     * 試合のステータスを設定する
     * @param matchStatus
     */
    public void setMatchStatus(MatchStatus matchStatus) {this.matchStatus = matchStatus;}
    
    /**
     * この試合のスケジューラーをスタートさせる
     */
    public void startMatchRunnable(){
        this.matchRunnable.runTaskTimerAsynchronously(Shootarian.getPlugin(), 0, 20);
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
    public Set<ShootarianEntity> getShootarianEntities() {return shootarianEntities;}
    
    public Set<ShootarianEntity> getAsyncEntities() {return asyncEntities;}
    
    public AsyncWorld getAsyncWorld() {return asyncWorld;}
    
    public void setAsyncWorld(AsyncWorld asyncWorld) {this.asyncWorld = asyncWorld;}
    
    /**
     * 試合終了判定
     * @return boolean 試合が終了したかどうか
     */
    public abstract boolean checkWin();
    
    
    /**
     * 試合に勝利したチームを取得する。
     * 引き分けの場合はnull
     * @return ShootarianTeam
     */
    public abstract ShootarianTeam getWinner();
    
    
    /**
     * この試合に存在する全てのチームを取得する
     * @return List<ShootarianTeam>
     */
    public List<ShootarianTeam> getShootarianTeams() {return shootarianTeams;}
    
    
    /**
     * チームを追加します
     * @param ShootarianTeam 追加するチーム
     */
    public void addShootarianTeam(ShootarianTeam ShootarianTeam){this.shootarianTeams.add(ShootarianTeam);}
    
    
    /**
     * ブロックのアップデーターを取得する
     * @return
     */
    public BlockUpdater getBlockUpdater() {
        return blockUpdater;
    }
    
    /**
     * プレイヤーをチームのスポーン場所にテレポートさせます
     * @param ShootarianPlayer
     */
    public void teleportToTeamLocation(ShootarianPlayer ShootarianPlayer){
        ShootarianTeam ShootarianTeam = ShootarianPlayer.getShootarianTeam();
        if(ShootarianTeam == null) return;
    
        int index = this.getShootarianTeams().indexOf(ShootarianTeam);
        ShootarianPlayer.teleport(shootarianMap.getTeamLocation(index));
    }


    /**
     * この試合に参加しているプレイヤーを取得する
     * @return Set<ShootarianPlayer>
     */
    public Set<ShootarianPlayer> getPlayers(){
        Set<ShootarianPlayer> players = new HashSet<>();
        shootarianTeams.forEach(ShootarianTeam -> players.addAll(ShootarianTeam.getTeamMembers()));
        return players;
    }
    
    
    /**
     * この試合に参加しているプレイヤー全員に音を聞かせる
     * @param sound サウンド
     * @param location 再生する座標
     */
    public void playSound(ShootarianSound sound, Location location){this.getPlayers().forEach(ShootarianPlayer -> ShootarianPlayer.playSound(sound, location));}
    
    /**
     * この試合に参加しているプレイヤー全員に音を聞かせる
     * @param sound サウンド
     */
    public void playSound(ShootarianSound sound){this.getPlayers().forEach(ShootarianPlayer -> ShootarianPlayer.playSound(sound));}
    
    /**
     * この試合に参加しているプレイヤー全員にパーティクルを表示する
     * @param particle パーティクル
     * @param location パーティクルを表示する座標
     */
    public void spawnParticle(ShootarianParticle particle, Location location){this.getPlayers().forEach(ShootarianPlayer -> ShootarianPlayer.spawnParticle(particle, location));}

    /**
     * この試合に参加しているプレイヤー全員にパーティクルを表示する
     * @param particle パーティクル
     * @param location パーティクルを表示する座標
     * @param settings パーティクルを表示するかどうかの設置項目
     */
    public void spawnParticle(ShootarianParticle particle, Location location, Settings settings){this.getPlayers().forEach(ShootarianPlayer -> ShootarianPlayer.spawnParticle(particle, location, settings));}
    
    /**
     * この試合に参加しているプレイヤー全員にパケットを送信する
     * @param packet 送信するパケット
     */
    public void sendPacket(Packet<?> packet){this.getPlayers().forEach(ShootarianPlayer -> ShootarianPlayer.sendPacket(packet));}
    
    
    /**
     * 指定された範囲内にいるプレイヤーを取得します
     * @param center 中心
     * @param distance 距離
     */
    public Set<ShootarianPlayer> getPlayersInRange(Location center, double distance){
        Set<ShootarianPlayer> shootarianPlayers = new HashSet<>();
        for(ShootarianPlayer shootarianPlayer : this.getPlayers()){
            if(LocationUtil.distanceSquaredSafeDifferentWorld(shootarianPlayer.getLocation(), center) > distance * distance) continue;
            shootarianPlayers.add(shootarianPlayer);
        }
        
        return shootarianPlayers;
    }
    
    
    
    
    /**
     * 試合に参加しているプレイヤー全員に、表示するプレイヤーを設定する
     * @param option ObservableOption
     */
    public void setPlayerObservableOption(ObservableOption option){
        this.getPlayers().forEach(ShootarianPlayer -> ShootarianPlayer.setObservableOption(option));
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
        PLAYING_INTRO,
        IN_PROGRESS,
        FINISHED
    }
}
