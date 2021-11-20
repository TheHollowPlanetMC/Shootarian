package be4rjp.shootarian.player;

import be4rjp.cinema4c.util.SkinManager;
import be4rjp.kuroko.player.KurokoPlayer;
import be4rjp.parallel.ParallelWorld;
import be4rjp.shootarian.Shootarian;
import be4rjp.shootarian.data.*;
import be4rjp.shootarian.data.settings.PlayerSettings;
import be4rjp.shootarian.data.settings.Settings;
import be4rjp.shootarian.data.sql.SQLDriver;
import be4rjp.shootarian.gui.MainMenuItem;
import be4rjp.shootarian.language.Lang;
import be4rjp.shootarian.listener.NPCTeleportListener;
import be4rjp.shootarian.map.PlayerGUIRenderer;
import be4rjp.shootarian.match.MatchManager;
import be4rjp.shootarian.match.team.ShootarianTeam;
import be4rjp.shootarian.language.MessageManager;
import be4rjp.shootarian.player.costume.HeadGear;
import be4rjp.shootarian.player.death.DeathType;
import be4rjp.shootarian.player.death.PlayerDeathManager;
import be4rjp.shootarian.player.passive.Gear;
import be4rjp.shootarian.player.passive.Passive;
import be4rjp.shootarian.player.passive.PlayerPassiveInfluence;
import be4rjp.shootarian.script.ScriptManager;
import be4rjp.shootarian.util.TaskHandler;
import be4rjp.shootarian.util.particle.ShootarianParticle;
import be4rjp.shootarian.util.ShootarianScoreboard;
import be4rjp.shootarian.util.ShootarianSound;
import be4rjp.shootarian.weapon.WeaponStatusData;
import be4rjp.shootarian.weapon.gun.GunStatusData;
import be4rjp.shootarian.weapon.WeaponClass;
import be4rjp.shootarian.weapon.WeaponManager;
import be4rjp.shootarian.weapon.gun.GunWeapon;
import be4rjp.shootarian.weapon.ShootarianWeapon;
import be4rjp.shootarian.weapon.gun.runnable.GunWeaponRunnable;
import net.citizensnpcs.api.npc.NPC;
import net.minecraft.server.v1_15_R1.*;
import org.bukkit.*;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.util.Vector;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReentrantLock;

/**
 * プレイヤーへの処理の全般は基本的にこのクラスで行う
 */
public class ShootarianPlayer {
    
    private static final Map<String, ShootarianPlayer> playerMap = new ConcurrentHashMap<>();
    
    /**
     * 指定されたUUIDのShootarianPlayerを返します。
     * 存在しなければ新しく作成し登録した後、返します。
     * @param uuid プレイヤーのUUID
     * @return ShootarianPlayer
     */
    public synchronized static ShootarianPlayer getShootarianPlayer(String uuid){
        if(playerMap.containsKey(uuid)){
            return playerMap.get(uuid);
        }else{
            ShootarianPlayer ShootarianPlayer = new ShootarianPlayer(uuid);
            playerMap.put(uuid, ShootarianPlayer);
            return ShootarianPlayer;
        }
    }
    
    /**
     * 指定されたプレイヤーからShootarianPlayerを返します。
     * 存在しなければ新しく作成し登録した後、返します。
     * @param player プレイヤー
     * @return ShootarianPlayer
     */
    public synchronized static ShootarianPlayer getShootarianPlayer(Player player) {
        return getShootarianPlayer(player.getUniqueId().toString());
    }
    
    /**
     * 指定されたUUIDのShootarianPlayerが既に作成されているかどうかを取得します
     * @param uuid プレイヤーのUUID
     * @return 既に作成されている場合は true されていない場合は false
     */
    public synchronized static boolean isCreated(String uuid){
        return playerMap.containsKey(uuid);
    }


    //プレイヤーが攻撃をヒットさせた時に鳴らす通知音
    private static final ShootarianSound HIT_SOUND_FOR_ATTACKER = new ShootarianSound(Sound.ENTITY_PLAYER_HURT, 0.5F, 1F);
    //プレイヤーが攻撃を受けた時に鳴らす音
    private static final ShootarianSound HIT_SOUND = new ShootarianSound(Sound.ENTITY_PLAYER_HURT, 1F, 1F);
    
    
    //プレイヤーのUUID
    private final String uuid;
    //AIかどうか
    private boolean isAI = false;
    //NPC
    private NPC npc;
    //AI
    private ShootarianPlayer aiTarget = null;
    //プレイヤーの言語設定
    private Lang lang = Lang.ja_JP;
    //プレイヤー
    private Player player = null;
    //セーブデータを正しくロードできたかどうか
    private boolean loadedSaveData = false;
    //Parallel
    private ParallelWorld parallelWorld;
    //参加しているMatchManager
    private MatchManager matchManager = null;
    //所属しているチーム
    private ShootarianTeam shootarianTeam = Shootarian.getLobbyTeam();
    //スコアボード
    private ShootarianScoreboard scoreBoard = null;
    //最後にテレポートを実行した時間
    private long teleportTime = 0;
    //塗りポイント
    private int points = 0;
    //キルカウント
    private int kills = 0;
    //ランク
    private int rank = 0;
    //銃のスケジューラーのマップ
    private final Map<GunWeapon, GunWeaponRunnable> mainWeaponTaskMap = new ConcurrentHashMap<>();
    //武器とそのステータスのマップ
    private final Map<ShootarianWeapon, WeaponStatusData> weaponStatusDataMap = new ConcurrentHashMap<>();
    //装備しているクラス
    private final WeaponClass weaponClass = new WeaponClass();
    //プレイヤーの体力
    private float health = 20.0F;
    //プレイヤーのアーマー値
    private float armor = 0.0F;
    //クライアントの視野角
    private float field_of_view = 0.1F;
    //プレイヤーのスキンデータ
    private String[] skin = null;
    //どのプレイヤーを表示するかのオプション
    private ObservableOption observableOption = ObservableOption.ALL_PLAYER;
    //フライ状態であるかどうか
    private boolean isFly = false;
    //移動速度
    private float walkSpeed = 0.2F;
    //フードレベル
    private int foodLevel = 20;
    //死んでいるかどうか
    private boolean isDeath = false;
    //透明かどうか
    private boolean isInvisible = false;
    //ADS中かどうか
    private boolean isADS = false;
    //ギアのリスト
    private final List<Gear> gearList = new CopyOnWriteArrayList<>();
    //パッシブ効果
    private final PlayerPassiveInfluence playerPassiveInfluence = new PlayerPassiveInfluence(this);
    //装備しているヘッドギア
    private HeadGear headGear = null;
    //装備しているヘッドギアの番号
    private int headGearNumber = 0;
    //武器の所持データ
    private final GunWeaponPossessionData gunWeaponPossessionData = new GunWeaponPossessionData();
    //ヘッドギアの所持データ
    private final HeadGearPossessionData headGearPossessionData = new HeadGearPossessionData();
    //ガジェットの所持データ
    private final GadgetPossessionData gadgetPossessionData = new GadgetPossessionData();
    //プレイヤーの設定
    private final PlayerSettings playerSettings = new PlayerSettings();
    //クエストの進捗
    private final QuestProgress questProgress = new QuestProgress();
    //実績データ
    private final AchievementData achievementData = new AchievementData(this);
    //マップのレンダラー
    private PlayerGUIRenderer playerGUIRenderer = null;
    //SetSlotPacket
    private PacketPlayOutSetSlot slotPacket = null;
    //GUIのスタック
    private final Deque<Map.Entry<Class<?>, Object>> guiStack = new ConcurrentLinkedDeque<>();
    //プレイヤーがマップを見ているかどうか
    private boolean isViewingMap = false;

    //キルカウントの動作の同期用インスタンス
    private final Object KILL_COUNT_LOCK = new Object();
    //ポイントの動作の同期用インスタンス
    private final Object POINT_COUNT_LOCK = new Object();
    //フライ系の動作の同期用インスタンス
    private final Object FLY_LOCK = new Object();
    //死亡系の動作の同期用インスタンス
    private final Object HEALTH_LOCK = new Object();
    //ランク系の動作の同期用インスタンス
    private final Object RANK_LOCK = new Object();
    //ADS系の動作の同期用インスタンス
    private final Object ADS_LOCK = new Object();
    //マップレンダラー系の動作の同期用インスタンス
    private final ReentrantLock MAP_RENDERER_LOCK = new ReentrantLock(true);
    
    
    /**
     * ShootarianPlayerを新しく作成
     * @param uuid プレイヤーのUUID
     */
    protected ShootarianPlayer(String uuid){this.uuid = uuid;}
    
    
    public String getUUID() {return uuid;}
    
    public Lang getLang() {return lang;}
    
    public void setLang(Lang lang) {this.lang = lang;}
    
    public boolean isLoadedSaveData() {return loadedSaveData;}
    
    public void setLoadedSaveData(boolean loadedSaveData) {this.loadedSaveData = loadedSaveData;}
    
    public ShootarianTeam getShootarianTeam() {return shootarianTeam;}
    
    public void setShootarianTeam(ShootarianTeam ShootarianTeam) {this.shootarianTeam = ShootarianTeam;}
    
    public MatchManager getMatchManager() {return matchManager;}
    
    public void setMatchManager(MatchManager matchManager) {this.matchManager = matchManager;}
    
    public int getPaints() {synchronized (POINT_COUNT_LOCK){return points;}}
    
    public int getKills() {synchronized (KILL_COUNT_LOCK){return kills;}}
    
    public boolean isInvisible() {return isInvisible;}
    
    public void setInvisible(boolean invisible) {isInvisible = invisible;}
    
    public synchronized float getArmor() {return armor;}
    
    public synchronized void setArmor(float armor) {this.armor = armor;}
    
    public synchronized float getHealth() {return health;}
    
    public synchronized void setHealth(float health) {
        this.health = health;
        PacketPlayOutUpdateHealth updateHealth = new PacketPlayOutUpdateHealth(this.health, this.foodLevel, 0.0F);
        this.sendPacket(updateHealth);
    }
    
    public ParallelWorld getParallelWorld() {
        if(parallelWorld == null) parallelWorld = ParallelWorld.getParallelWorld(uuid);
        return parallelWorld;
    }
    
    public void addPoints(int points) {synchronized (POINT_COUNT_LOCK){
        this.points += points;
    }}
    
    public void addKills(int kills) {synchronized (KILL_COUNT_LOCK){this.kills += kills;}}

    public boolean isDeath() {synchronized (HEALTH_LOCK){return isDeath;}}

    public void setDeath(boolean death) {synchronized (HEALTH_LOCK){isDeath = death;}}

    public ShootarianScoreboard getScoreBoard() {return scoreBoard;}
    
    public String[] getSkin() {return skin;}
    
    public ObservableOption getObservableOption() {return observableOption;}
    
    public List<Gear> getGearList() {return gearList;}
    
    public PlayerPassiveInfluence getPassiveInfluence() {return playerPassiveInfluence;}
    
    public HeadGear getHeadGear() {return headGear;}
    
    public int getHeadGearNumber() {return headGearNumber;}
    
    public void setScoreBoard(ShootarianScoreboard scoreBoard) {
        this.scoreBoard = scoreBoard;
        if(player != null) player.setScoreboard(scoreBoard.getBukkitScoreboard());
    }
    
    public int getRank() {synchronized (RANK_LOCK){return rank;}}
    
    public void addRank(int rank){synchronized (RANK_LOCK){this.rank += rank;}}
    
    public boolean isADS() {synchronized (ADS_LOCK){return isADS;}}
    
    public void setADS(boolean ADS) {synchronized (ADS_LOCK){isADS = ADS;}}
    
    public void switchADS(GunStatusData gunStatusData){synchronized (ADS_LOCK){
        setADS(!isADS);
        WeaponManager.switchADS(this, gunStatusData, this.isADS);
    }}
    
    public AchievementData getAchievementData() {return achievementData;}

    public PlayerSettings getPlayerSettings() {return playerSettings;}
    
    public HeadGearPossessionData getHeadGearPossessionData() {return headGearPossessionData;}
    
    public GunWeaponPossessionData getWeaponPossessionData() {return gunWeaponPossessionData;}
    
    public GadgetPossessionData getGadgetPossessionData() {return gadgetPossessionData;}
    
    public QuestProgress getQuestProgress() {return questProgress;}
    
    public WeaponClass getWeaponClass() {return weaponClass;}
    
    public PlayerGUIRenderer getPlayerGUIRenderer() {return playerGUIRenderer;}
    
    public void setPlayerGUIRenderer(PlayerGUIRenderer playerGUIRenderer) {
        MAP_RENDERER_LOCK.lock();
        try {
            if(this.playerGUIRenderer != null){
                try{
                    this.playerGUIRenderer.cancel();
                }catch (Exception e){/**/}
            }
            this.playerGUIRenderer = playerGUIRenderer;
        } finally {
            MAP_RENDERER_LOCK.unlock();
        }
    }
    
    public PacketPlayOutSetSlot getSlotPacket() {return slotPacket;}
    
    public void setSlotPacket(PacketPlayOutSetSlot slotPacket) {this.slotPacket = slotPacket;}
    
    public Deque<Map.Entry<Class<?>, Object>> getGUIStack() {return guiStack;}
    
    public PlayerPassiveInfluence getPlayerPassiveInfluence() {return playerPassiveInfluence;}
    
    public boolean isViewingMap() {return isViewingMap;}
    
    public void setViewingMap(boolean viewingMap) {isViewingMap = viewingMap;}
    
    public NPC getCitizensNPC() {return npc;}
    
    public void setCitizensNPC(NPC npc) {this.npc = npc;}
    
    public ShootarianPlayer getAiTarget() {return aiTarget;}
    
    public void setAiTarget(ShootarianPlayer aiTarget) {this.aiTarget = aiTarget;}
    
    /**
     * 情報をリセットする
     */
    public void reset(){
        this.achievementData.addKill(kills);
        this.achievementData.addPoint(points);
        this.achievementData.addRank(rank);
        
        if(parallelWorld != null) parallelWorld.removeAll();
        
        if(matchManager != null) matchManager.getJoinedPlayers().remove(this);
        this.matchManager = null;
        if(shootarianTeam != null) shootarianTeam.getTeamMembers().remove(this);
        this.shootarianTeam = Shootarian.getLobbyTeam();
        this.setScoreBoard(Shootarian.getLobbyMatch().getScoreboard());
        this.points = 0;
        this.kills = 0;
        this.health = 20.0F;
        this.armor = 0.0F;
        this.observableOption = ObservableOption.ALL_PLAYER;
        this.isDeath = false;
        this.isADS = false;
        this.headGear = null;
        this.gearList.clear();
        this.setFOV(0.1F);
        this.setFly(false);
        this.setWalkSpeed(0.2F);
        this.setFoodLevel(20);
        this.clearGunWeaponTasks();
        this.weaponStatusDataMap.clear();
        if(this.playerGUIRenderer != null){
            try {
                this.playerGUIRenderer.cancel();
            }catch (Exception e){/**/}
            this.playerGUIRenderer = null;
        }
    }
    
    /**
     * BukkitのPlayerを取得します。
     * @return Player
     */
    public Player getBukkitPlayer(){
        return player;
    }
    
    /**
     * プレイヤーの実績データをSQLからロードする
     */
    public void loadAchievementFromSQL(){
        if(isAI) return;
        try {
            SQLDriver.loadAchievementData(this.achievementData);
            this.setLoadedSaveData(true);
            
            TaskHandler.supplySync(() -> KurokoPlayer.getKurokoPlayer(player)).thenAccept(kurokoPlayer -> {
                if(kurokoPlayer == null) return;
                ScriptManager.getPlayerJoinScriptRunner().runFunction("onPlayerJoin", this, player, kurokoPlayer);
            });
            
        }catch (Exception e){
            player.playNote(player.getLocation(), Instrument.BASS_GUITAR, Note.flat(0, Note.Tone.G));
            Date dateObj = new Date();
            SimpleDateFormat format = new SimpleDateFormat( "yyyy/MM/dd HH:mm:ss" );
            player.sendMessage("§c§n以下の理由により正常にセーブデータを読み込むことができませんでした。");
            player.sendMessage("§c§n再度接続し直しても同じエラーが出る場合は運営に報告してください。");
            player.sendMessage("§c§nThe save data could not be loaded properly for the following reasons.");
            player.sendMessage("§c§nIf you still get the same error after trying to connect again, please report it to the administrators.");
            player.sendMessage("");
            player.sendMessage("§eError (" + format.format(dateObj) + ") : §n" + e.getClass().getSimpleName());
            player.sendMessage(e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * プレイヤーの実績データをSQLに保存する
     */
    public void saveAchievementToSQL(){
        if(isAI) return;
        try {
            SQLDriver.saveAchievementData(this.achievementData);
        }catch (Exception e){e.printStackTrace();}
    }
    
    /**
     * プレイヤーのエンティティIDを取得します
     * @return
     */
    public int getEntityID(){
        if(player == null) return 0;
        return player.getEntityId();
    }
    
    /**
     * プレイヤーがオンラインであるかどうかを取得する
     * @return
     */
    public boolean isOnline(){
        if(player == null) return false;
        return player.isOnline();
    }
    
    /**
     * BukkitのPlayerをアップデートする（参加時用）
     */
    public void updateBukkitPlayer(Player bukkitPlayer, boolean isAI){
        if(bukkitPlayer != null) this.player = bukkitPlayer;
        this.isAI = isAI;
    }
    
    /**
     * AIであるかどうか
     * @return
     */
    public boolean isAI() {return isAI;}
    
    /**
     * Mojangのセッションサーバーへスキンデータのリクエストを送信して取得する
     */
    public void sendSkinRequest(){TaskHandler.runAsync(() -> skin = SkinManager.getSkin(uuid));}
    
    /**
     * メインメニューを渡す
     */
    public void setMainMenu(){
        if(player == null) return;
        player.getInventory().setItem(7, MainMenuItem.mainMenuItem.getItemStack(lang));
    }
    
    /**
     * ロビー上で装備させるアイテムを渡す
     */
    public void setLobbyItem(){
        this.equipHeadGear();
        this.setMainMenu();
    }
    
    /**
     * 全てを装備させえる
     */
    public void giveItems(){
        if(player != null) player.getInventory().clear();
        this.equipHeadGear();
        this.weaponClass.setItem(this);
        this.setMainMenu();
        this.setMap();
    }
    
    /**
     * 同じ試合位に参加しているプレイヤーの中から指定した範囲内にいるプレイヤーを取得します
     * @param distance 距離
     * @return Set<ShootarianPlayer>
     */
    public Set<ShootarianPlayer> getNearPlayer(double distance){
        ShootarianTeam shootarianTeam = this.getShootarianTeam();
        if(shootarianTeam == null) return new HashSet<>();
        
        return shootarianTeam.getMatch().getPlayersInRange(this.getLocation(), distance);
    }
    
    /**
     * プレイヤーにマップを渡す
     */
    public void setMap(){
        if(player != null){
            ItemStack itemStack = new ItemStack(Material.FILLED_MAP);
            MapMeta mapMeta = (MapMeta) itemStack.getItemMeta();
            mapMeta.setMapId(0);
            mapMeta.setDisplayName("§f§nField map");
            itemStack.setItemMeta(mapMeta);
            player.getInventory().setItem(6, itemStack);
        }
    }
    
    /**
     * GUIをクリックしたときの音をプレイヤーに聞かせる
     */
    public void playGUIClickSound(){
        Player player = this.getBukkitPlayer();
        if(player == null) return;
        
        player.playNote(player.getLocation(), Instrument.STICKS, Note.flat(1, Note.Tone.C));
    }
    
    /**
     * 表示するプレイヤーを設定する
     * @param option ObservableOption
     */
    public void setObservableOption(ObservableOption option){
        this.observableOption = option;
        
        /*
        switch (observableOption){
            case ALONE:{
                if(this.player == null) break;
                if(this.shootarianTeam == null) break;
                
                TaskHandler.runSync(() ->{
                    for(Player op : Bukkit.getServer().getOnlinePlayers()) {
                        if(player != op) player.hidePlayer(Shootarian.getPlugin(), op);
                    }
                });
                break;
            }
            
            case ONLY_MATCH_PLAYER:{
                if(this.player == null) break;
                if(this.shootarianTeam == null) break;
                Set<Player> hidePlayers = new HashSet<>();
                Set<Player> showPlayers = new HashSet<>();
                
                for(Player op : Bukkit.getServer().getOnlinePlayers()){
                    ShootarianPlayer shootarianPlayer = ShootarianPlayer.getShootarianPlayer(op);
                    if(shootarianPlayer.getShootarianTeam() == null) continue;
                    
                    if(shootarianPlayer.getShootarianTeam().getMatch() == this.shootarianTeam.getMatch()){
                        showPlayers.add(op);
                    }else{
                        hidePlayers.add(op);
                    }
                }
    
                TaskHandler.runSync(() -> {
                    for(Player op : hidePlayers) {
                        if(player != op) player.hidePlayer(Shootarian.getPlugin(), op);
                    }
                    for(Player op : showPlayers) {
                        if(player != op) player.showPlayer(Shootarian.getPlugin(), op);
                    }
                });
                break;
            }
            
            case ALL_PLAYER:{
                if(this.player == null) break;
    
                TaskHandler.runSync(() -> {
                    for(Player op : Bukkit.getServer().getOnlinePlayers()) {
                        if(player != op) player.showPlayer(Shootarian.getPlugin(), op);
                    }
                });
                break;
            }
        }*/
    }
    
    /**
     * ヘッドギアを装備させる
     * @param headGear
     */
    public void setHeadGear(HeadGear headGear){
        this.headGear = headGear;
        this.headGearNumber = headGear.getSaveNumber();
        
        if(player == null) return;
        player.getInventory().setHelmet(headGear.getItemStack(lang));
    }
    
    /**
     * ヘッドギアを装備させる
     */
    public void equipHeadGear(){
        if(player == null || headGear == null) return;
        player.getInventory().setHelmet(headGear.getItemStack(lang));
    }
    
    /**
     * プレイヤーの帽子を取る
     */
    public void removeHelmet(){
        if(player == null) return;
        player.getInventory().setHelmet(new org.bukkit.inventory.ItemStack(org.bukkit.Material.AIR));
    }
    
    /**
     * クライアントの視野角を取得します
     * @return float
     */
    public float getFOV() {return field_of_view;}
    
    /**
     * クライアントの視野角を変更します
     * @param field_of_view
     */
    public void setFOV(float field_of_view) {
        this.field_of_view = field_of_view;
        
        PlayerAbilities abilities = new PlayerAbilities();
        abilities.walkSpeed = field_of_view;
        PacketPlayOutAbilities abilitiesPacket = new PacketPlayOutAbilities(abilities);
        this.sendPacket(abilitiesPacket);
    }

    /**
     * 武器とそのステータスのマップを取得します。
     * @return Map<ShootarianWeapon, WeaponStatusData>
     */
    public Map<ShootarianWeapon, WeaponStatusData> getWeaponStatusDataMap() {return weaponStatusDataMap;}

    /**
     * 武器のステータスを取得する
     * @return
     */
    public WeaponStatusData getWeaponStatusData(ShootarianWeapon shootarianWeapon){
        WeaponStatusData weaponStatusData = this.weaponClass.getWeaponStatusData(shootarianWeapon);
        if(weaponStatusData != null) return weaponStatusData;

        weaponStatusData = this.weaponStatusDataMap.get(shootarianWeapon);
        if(weaponStatusData != null) return weaponStatusData;

        return WeaponStatusData.createWeaponStatusData(shootarianWeapon, this);
    }

    /**
     * プレイヤーの移動速度を取得する
     * @return float
     */
    public float getWalkSpeed() {return walkSpeed;}
    
    /**
     * プレイヤーの移動速度を設定する
     * @param speed
     */
    public void setWalkSpeed(float speed){
        if(player == null) return;
        
        speed = (float) this.playerPassiveInfluence.setInfluence(Passive.RUN_SPEED, speed);
        this.walkSpeed = speed;
        player.setWalkSpeed(speed);
    }
    
    /**
     * プレイヤーの飛行状態を設定します
     * @param fly
     */
    public void setFly(boolean fly){
        synchronized (FLY_LOCK) {
            if (player == null) return;
            isFly = fly;
            player.setAllowFlight(fly);
            player.setFlying(fly);
        }
    }
    
    /**
     * プレイヤーが飛行状態であるかどうかを取得します
     * @return
     */
    public boolean isFly() {
        synchronized (FLY_LOCK) {
            return isFly;
        }
    }
    
    /**
     * メッセージを送信します。
     * 基本的にメッセージを送信するときはsendText()を使用してください。
     * @param message メッセージ
     */
    @Deprecated
    public void sendMessage(String message){
        if(player == null) return;
        player.sendMessage("[§6Shootarian§r] " + message);
    }
    
    /**
     * 言語別のメッセージを送信します。
     * @param lang 言語
     * @param textName message.ymlに設定されているテキストの名前
     */
    public void sendText(Lang lang, String textName){
        if(player == null) return;
        player.sendMessage("[§6Shootarian§r] " + MessageManager.getText(lang, textName));
    }
    
    /**
     * 言語別のメッセージを送信します。
     * @param textName message.ymlに設定されているテキストの名前
     */
    public void sendText(String textName){
        if(player == null) return;
        player.sendMessage("[§6Shootarian§r] " + MessageManager.getText(lang, textName));
    }

    /**
     * 言語別のメッセージを送信します。
     * @param textName message.ymlに設定されているテキストの名前
     * @param args %s等と置き換える値
     */
    public void sendText(String textName, Object... args){
        if(player == null) return;
        player.sendMessage("[§6Shootarian§r] " + String.format(MessageManager.getText(lang, textName), args));
    }
    
    /**
     * 言語別のタイトルメッセージを送信します
     * @param titleTextName message.ymlに設定されているタイトルテキストの名前
     * @param subTitleTextName message.ymlに設定されているサブタイトルテキストの名前
     * @param fadeIn 文字のフェードイン[tick]
     * @param stay 文字の表示時間[tick]
     * @param fadeOut 文字のフェードアウト[tick]
     */
    public void sendTextTitle(String titleTextName, String subTitleTextName, int fadeIn, int stay, int fadeOut){
        if(player == null) return;
        player.sendTitle(MessageManager.getText(lang, titleTextName), MessageManager.getText(lang, subTitleTextName), fadeIn, stay, fadeOut);
    }

    /**
     * 言語別のタイトルメッセージを送信します
     * @param titleTextName message.ymlに設定されているタイトルテキストの名前
     * @param titleArgs 置き換える値 (%d等)
     * @param subTitleTextName message.ymlに設定されているサブタイトルテキストの名前
     * @param subTitleArgs 置き換える値 (%d等)
     * @param fadeIn 文字のフェードイン[tick]
     * @param stay 文字の表示時間[tick]
     * @param fadeOut 文字のフェードアウト[tick]
     */
    public void sendTextTitle(String titleTextName, Object[] titleArgs, String subTitleTextName, Object[] subTitleArgs, int fadeIn, int stay, int fadeOut){
        if(player == null) return;
        player.sendTitle(String.format(MessageManager.getText(lang, titleTextName), titleArgs), String.format(MessageManager.getText(lang, subTitleTextName), subTitleArgs), fadeIn, stay, fadeOut);
    }
    
    /**
     * タイトルテキストをリセットします
     */
    public void resetTitle(){
        sendTextTitle("none", "none", 0, 0, 0);
    }
    
    /**
     * アイコンのタイトルを送信します
     * @param titleIcon タイトルに表示するアイコン
     * @param subtitleIcon サブタイトルに表示するアイコン
     * @param fadeIn 文字のフェードイン[tick]
     * @param stay 文字の表示時間[tick]
     * @param fadeOut 文字のフェードアウト[tick]
     */
    public void sendIconTitle(String titleIcon, String subtitleIcon, int fadeIn, int stay, int fadeOut){
        if(player == null) return;
        player.sendTitle(titleIcon, subtitleIcon, fadeIn, stay, fadeOut);
    }
    
    /**
     * タイトルメッセージを送信します
     * @param titleText タイトルテキスト
     * @param subTitleText サブタイトルテキスト
     * @param fadeIn 文字のフェードイン[tick]
     * @param stay 文字の表示時間[tick]
     * @param fadeOut 文字のフェードアウト[tick]
     */
    @Deprecated
    public void sendTitle(String titleText, String subTitleText, int fadeIn, int stay, int fadeOut){
        if(player == null) return;
        player.sendTitle(titleText, subTitleText, fadeIn, stay, fadeOut);
    }
    
    /**
     * 言語別のアクションバーメッセージを送信します。
     * @param textName message.ymlに設定されているテキストの名前
     */
    public void sendTextActionbar(String textName){
        if(player == null) return;
        player.sendActionBar(MessageManager.getText(lang, textName));
    }
    
    /**
     * パケットを送信します
     * @param packet 送信するパケット
     */
    public void sendPacket(Packet<?> packet){
        if(player == null) return;
        EntityPlayer entityPlayer = ((CraftPlayer)player).getHandle();
        entityPlayer.playerConnection.sendPacket(packet);
    }
    
    /**
     * プレイヤーの座標を返します
     * @return Location
     */
    public Location getLocation(){
        if(player == null){
            return new Location(Bukkit.getWorld("world"), 0, 0, 0);
        }else{
            return player.getLocation();
        }
    }
    
    /**
     * プレイヤーの目線の座標を返します
     * @return Location
     */
    public Location getEyeLocation(){
        if(player == null){
            return new Location(Bukkit.getWorld("world"), 0, 0, 0);
        }else{
            return player.getEyeLocation();
        }
    }
    
    /**
     * テレポートさせます。
     * @param location テレポート先
     */
    public void teleport(Location location){
        long time = System.currentTimeMillis();
        this.teleportTime = time;
        if(player == null) return;
        
        TaskHandler.runSync(() -> {
            if (player == null) return;
            if (time != teleportTime) return;
            if(isAI){
                NPCTeleportListener.scheduledTeleport.add(npc);
            }
            player.teleport(location);
        });
    }
    
    /**
     * テレポートさせます。
     * @param location テレポート先
     */
    public void teleportSynced(Location location){
        long time = System.currentTimeMillis();
        this.teleportTime = time;
        if(player == null) return;
        if(isAI){
            NPCTeleportListener.scheduledTeleport.add(npc);
        }
        player.teleport(location);
    }
    
    /**
     * リスポーンさせます
     * @param location テレポート先
     */
    public void respawn(Location location){
        long time = System.currentTimeMillis();
        this.teleportTime = time;
        this.setHealth(20.0F);
        if(player == null) return;
        
        TaskHandler.runSync(() -> {
            if (player == null) return;
            if(isAI){
                NPCTeleportListener.scheduledTeleport.add(npc);
            }
            player.teleport(location);
            player.setGameMode(GameMode.ADVENTURE);
            setDeath(false);
        });
    }
    
    /**
     * ゲームモードを変更します
     * @param gameMode 設定するゲームモード
     */
    public void setGameMode(GameMode gameMode){
        if(player == null) return;
        
        TaskHandler.runSync(() -> {
            if (player == null) return;
            player.setGameMode(gameMode);
        });
    }
    
    /**
     * ゲームモードを変更します
     * @param gameMode 設定するゲームモード
     */
    public void setGameMode(GameMode gameMode, Runnable runnable){
        if(player == null) return;
        
        TaskHandler.runSync(() -> {
            if (player == null) return;
            player.setGameMode(gameMode);
            runnable.run();
        });
    }
    
    /**
     * Velocityを設定します
     * @param velocity Vector
     */
    public void setVelocity(Vector velocity){
        if(player == null) return;
        player.setVelocity(velocity);
    }
    
    /**
     * メインウエポンのスケジューラーのマップを取得する
     * @return Map<MainWeapon, MainWeaponRunnable>
     */
    public Map<GunWeapon, GunWeaponRunnable> getGunWeaponTaskMap(){return this.mainWeaponTaskMap;}
    
    /**
     * メインウエポンのスケジューラーを全て停止して削除する
     */
    public void clearGunWeaponTasks(){
        for(GunWeaponRunnable runnable : mainWeaponTaskMap.values()){
            try{
                runnable.cancel();
            }catch (Exception e){/**/}
        }
        
        mainWeaponTaskMap.clear();
    }
    
    /**
     * 音を再生する
     * @param sound ShootarianSound
     */
    public void playSound(ShootarianSound sound){
        if(player == null) return;
        sound.play(player, player.getLocation());
    }
    
    /**
     * 音を再生する
     * @param sound ShootarianSound
     * @param location 音を再生する座標
     */
    public void playSound(ShootarianSound sound, Location location){
        if(player == null) return;
        sound.play(player, location);
    }
    
    /**
     * パーティクルを表示する
     * @param particle ShootarianParticle
     * @param location パーティクルを表示する座標
     */
    public void spawnParticle(ShootarianParticle particle, Location location){
        if(player == null) return;
        particle.spawn(player, location);
    }

    /**
     * パーティクルを表示する
     * @param particle ShootarianParticle
     * @param location パーティクルを表示する座標
     * @param settings パーティクルを表示するかどうかの設置項目
     */
    public void spawnParticle(ShootarianParticle particle, Location location, Settings settings){
        if(player == null) return;
        if(!this.playerSettings.getSettings(settings)) return;
        particle.spawn(player, location);
    }
    
    /**
     * 距離を無視してパーティクルを表示する
     * @param particle ShootarianParticle
     * @param location パーティクルを表示する座標
     * @param settings パーティクルを表示するかどうかの設置項目
     */
    public void spawnParticleIgnoreRange(ShootarianParticle particle, Location location, Settings settings){
        if(player == null) return;
        if(!this.playerSettings.getSettings(settings)) return;
        particle.spawnIgnoreRange(player, location);
    }
    
    /**
     * プレイヤーを回復させる
     * @param plus
     */
    public void heal(float plus){
        synchronized (HEALTH_LOCK) {
            if (player == null) return;
            if (this.shootarianTeam == null) return;
    
            if (this.health + plus < 20.0F) {
                this.health += plus;
                PacketPlayOutUpdateHealth updateHealth = new PacketPlayOutUpdateHealth(this.health, this.foodLevel, 0.0F);
                this.sendPacket(updateHealth);
            } else if (this.health != 20.0F) {
                this.health = 20.0F;
                PacketPlayOutUpdateHealth updateHealth = new PacketPlayOutUpdateHealth(this.health, this.foodLevel, 0.0F);
                this.sendPacket(updateHealth);
            }
        }
    }
    
    /**
     * プレイヤーに毒ダメージを与えます
     * @param damage
     */
    public void givePoisonDamage(float damage){
        synchronized (HEALTH_LOCK) {
            if (player == null) return;
            if (this.shootarianTeam == null) return;
    
            if (this.health > damage) {
                this.health -= damage;
                PacketPlayOutUpdateHealth updateHealth = new PacketPlayOutUpdateHealth(this.health, this.foodLevel, 0.0F);
                PacketPlayOutAnimation animation = new PacketPlayOutAnimation(((CraftPlayer) player).getHandle(), 1);
                this.sendPacket(updateHealth);
                this.shootarianTeam.getMatch().sendPacket(animation);
                this.shootarianTeam.getMatch().playSound(HIT_SOUND, player.getLocation());
            }
        }
    }
    
    /**
     * プレイヤーにダメージを与える
     * @param damage 与えるダメージ
     * @param attacker 攻撃者
     * @param ShootarianWeapon 攻撃に使用した武器
     */
    public void giveDamage(float damage, ShootarianPlayer attacker, Vector velocity, ShootarianWeapon ShootarianWeapon){
        synchronized (HEALTH_LOCK) {
            if (player == null) return;
            if (attacker.getBukkitPlayer() == null) return;
            if (this.shootarianTeam == null) return;
            if (attacker.getShootarianTeam() == null) return;
            if (attacker.getShootarianTeam() == this.shootarianTeam) return;
    
            if (this.getArmor() > 0.0 && velocity != null) {
                player.setVelocity(velocity);
            }
    
            if (this.getHealth() + this.getArmor() > damage) {
                if (this.getArmor() > damage) {
                    this.setArmor(this.getArmor() - damage);
                } else {
                    //give damage
                    float d = damage - this.getArmor();
                    this.setHealth(this.getHealth() - d);
                    this.setArmor(0.0F);
            
                    PacketPlayOutUpdateHealth updateHealth = new PacketPlayOutUpdateHealth(this.health, this.foodLevel, 0.0F);
                    PacketPlayOutAnimation animation = new PacketPlayOutAnimation(((CraftPlayer) player).getHandle(), 1);
                    this.sendPacket(updateHealth);
                    this.shootarianTeam.getMatch().sendPacket(animation);
            
                    attacker.playSound(HIT_SOUND_FOR_ATTACKER);
                    this.shootarianTeam.getMatch().playSound(HIT_SOUND, player.getLocation());
                }
            } else {
                //死亡処理
                this.setHealth(20.0F);
                PlayerDeathManager.death(this, attacker, ShootarianWeapon, DeathType.KILLED_BY_PLAYER);
            }
        }
    }
    
    /**
     * フードレベルを取得します
     * @return
     */
    public synchronized int getFoodLevel() {return foodLevel;}
    
    /**
     * フードレベルを設定します
     * @param foodLevel
     */
    public synchronized void setFoodLevel(int foodLevel) {
        this.foodLevel = foodLevel;
        if(player == null) return;
        PacketPlayOutUpdateHealth updateHealth = new PacketPlayOutUpdateHealth(this.health, this.foodLevel, 0.0F);
        this.sendPacket(updateHealth);
    }
    
    /**
     * プレイヤーの表示名を取得する
     * @return String
     */
    public String getDisplayName(){
        return getDisplayName(false);
    }
    
    /**
     * プレイヤーの表示名を取得する
     * @return String
     */
    public String getDisplayName(boolean bold){
        if(player == null) return "";
        if(shootarianTeam == null) player.getName();
        
        return shootarianTeam.getShootarianColor().getChatColor() + (bold ? "§l" : "") + player.getName() + "§r";
    }
}
