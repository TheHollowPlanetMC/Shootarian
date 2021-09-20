package be4rjp.shellcase.player;

import be4rjp.cinema4c.util.SkinManager;
import be4rjp.parallel.ParallelWorld;
import be4rjp.shellcase.ShellCase;
import be4rjp.shellcase.data.AchievementData;
import be4rjp.shellcase.data.HeadGearPossessionData;
import be4rjp.shellcase.data.GunWeaponPossessionData;
import be4rjp.shellcase.data.settings.PlayerSettings;
import be4rjp.shellcase.data.settings.Settings;
import be4rjp.shellcase.data.sql.SQLDriver;
import be4rjp.shellcase.language.Lang;
import be4rjp.shellcase.match.MatchManager;
import be4rjp.shellcase.match.team.ShellCaseTeam;
import be4rjp.shellcase.language.MessageManager;
import be4rjp.shellcase.player.costume.HeadGear;
import be4rjp.shellcase.player.death.DeathType;
import be4rjp.shellcase.player.death.PlayerDeathManager;
import be4rjp.shellcase.player.passive.Gear;
import be4rjp.shellcase.player.passive.PassiveInfluence;
import be4rjp.shellcase.util.TaskHandler;
import be4rjp.shellcase.util.particle.ShellCaseParticle;
import be4rjp.shellcase.util.ShellCaseScoreboard;
import be4rjp.shellcase.util.ShellCaseSound;
import be4rjp.shellcase.weapon.WeaponStatusData;
import be4rjp.shellcase.weapon.gun.GunStatusData;
import be4rjp.shellcase.weapon.WeaponClass;
import be4rjp.shellcase.weapon.WeaponManager;
import be4rjp.shellcase.weapon.gun.GunWeapon;
import be4rjp.shellcase.weapon.ShellCaseWeapon;
import be4rjp.shellcase.weapon.gun.runnable.GunWeaponRunnable;
import net.minecraft.server.v1_15_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * プレイヤーへの処理の全般は基本的にこのクラスで行う
 */
public class ShellCasePlayer {
    
    private static final Map<String, ShellCasePlayer> playerMap = new ConcurrentHashMap<>();
    
    /**
     * 指定されたUUIDのShellCasePlayerを返します。
     * 存在しなければ新しく作成し登録した後、返します。
     * @param uuid プレイヤーのUUID
     * @return ShellCasePlayer
     */
    public synchronized static ShellCasePlayer getShellCasePlayer(String uuid){
        if(playerMap.containsKey(uuid)){
            return playerMap.get(uuid);
        }else{
            ShellCasePlayer ShellCasePlayer = new ShellCasePlayer(uuid);
            playerMap.put(uuid, ShellCasePlayer);
            return ShellCasePlayer;
        }
    }
    
    /**
     * 指定されたプレイヤーからShellCasePlayerを返します。
     * 存在しなければ新しく作成し登録した後、返します。
     * @param player プレイヤー
     * @return ShellCasePlayer
     */
    public synchronized static ShellCasePlayer getShellCasePlayer(Player player) {
        return getShellCasePlayer(player.getUniqueId().toString());
    }
    
    /**
     * 指定されたUUIDのShellCasePlayerが既に作成されているかどうかを取得します
     * @param uuid プレイヤーのUUID
     * @return 既に作成されている場合は true されていない場合は false
     */
    public synchronized static boolean isCreated(String uuid){
        return playerMap.containsKey(uuid);
    }


    //プレイヤーが攻撃をヒットさせた時に鳴らす通知音
    private static final ShellCaseSound HIT_SOUND_FOR_ATTACKER = new ShellCaseSound(Sound.ENTITY_PLAYER_HURT, 0.5F, 1F);
    //プレイヤーが攻撃を受けた時に鳴らす音
    private static final ShellCaseSound HIT_SOUND = new ShellCaseSound(Sound.ENTITY_PLAYER_HURT, 1F, 1F);
    
    
    //プレイヤーのUUID
    private final String uuid;
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
    private ShellCaseTeam shellCaseTeam = ShellCase.getLobbyTeam();
    //スコアボード
    private ShellCaseScoreboard scoreBoard = null;
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
    private final Map<ShellCaseWeapon, WeaponStatusData> weaponStatusDataMap = new ConcurrentHashMap<>();
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
    private final PassiveInfluence passiveInfluence = new PassiveInfluence();
    //装備しているヘッドギア
    private HeadGear headGear = null;
    //装備しているヘッドギアの番号
    private int headGearNumber = 0;
    //武器の所持データ
    private final GunWeaponPossessionData gunWeaponPossessionData = new GunWeaponPossessionData();
    //ヘッドギアの所持データ
    private final HeadGearPossessionData headGearPossessionData = new HeadGearPossessionData();
    //実績データ
    private final AchievementData achievementData = new AchievementData(this);
    //プレイヤーの設定
    private final PlayerSettings playerSettings = new PlayerSettings();

    //キルカウントの動作の同期用インスタンス
    private final Object KILL_COUNT_LOCK = new Object();
    //ポイントの動作の同期用インスタンス
    private final Object POINT_COUNT_LOCK = new Object();
    //フライ系の動作の同期用インスタンス
    private final Object FLY_LOCK = new Object();
    //死亡系の動作の同期用インスタンス
    private final Object DEATH_LOCK = new Object();
    //ランク系の動作の同期用インスタンス
    private final Object RANK_LOCK = new Object();
    //ADS系の動作の同期用インスタンス
    private final Object ADS_LOCK = new Object();
    
    
    /**
     * ShellCasePlayerを新しく作成
     * @param uuid プレイヤーのUUID
     */
    private ShellCasePlayer(String uuid){this.uuid = uuid;}
    
    
    public String getUUID() {return uuid;}
    
    public Lang getLang() {return lang;}
    
    public void setLang(Lang lang) {this.lang = lang;}
    
    public boolean isLoadedSaveData() {return loadedSaveData;}
    
    public void setLoadedSaveData(boolean loadedSaveData) {this.loadedSaveData = loadedSaveData;}
    
    public ShellCaseTeam getShellCaseTeam() {return shellCaseTeam;}
    
    public void setShellCaseTeam(ShellCaseTeam ShellCaseTeam) {this.shellCaseTeam = ShellCaseTeam;}
    
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

    public boolean isDeath() {synchronized (DEATH_LOCK){return isDeath;}}

    public void setDeath(boolean death) {synchronized (DEATH_LOCK){isDeath = death;}}

    public ShellCaseScoreboard getScoreBoard() {return scoreBoard;}
    
    public String[] getSkin() {return skin;}
    
    public ObservableOption getObservableOption() {return observableOption;}
    
    public List<Gear> getGearList() {return gearList;}
    
    public PassiveInfluence getPassiveInfluence() {return passiveInfluence;}
    
    public HeadGear getHeadGear() {return headGear;}
    
    public int getHeadGearNumber() {return headGearNumber;}
    
    public void setScoreBoard(ShellCaseScoreboard scoreBoard) {
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
    
    public WeaponClass getWeaponClass() {return weaponClass;}
    
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
        if(shellCaseTeam != null) shellCaseTeam.getTeamMembers().remove(this);
        this.shellCaseTeam = ShellCase.getLobbyTeam();
        this.setScoreBoard(ShellCase.getLobbyMatch().getScoreboard());
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
    public void loadAchievementFromSQL() throws Exception{
        SQLDriver.loadAchievementData(this.achievementData);
    }
    
    /**
     * プレイヤーの実績データをSQLに保存する
     */
    public void saveAchievementToSQL() throws Exception{
        SQLDriver.saveAchievementData(this.achievementData);
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
    public void updateBukkitPlayer(Player bukkitPlayer){
        if(bukkitPlayer != null) this.player = bukkitPlayer;
    }
    
    /**
     * Mojangのセッションサーバーへスキンデータのリクエストを送信して取得する
     */
    public void sendSkinRequest(){
        TaskHandler.runAsync(() -> {
            skin = SkinManager.getSkin(uuid);
        }, ShellCase.getPlugin());
    }
    
    /**
     * メインメニューを渡す
     */
    public void setMainMenu(){
        if(player == null) return;
        //player.getInventory().setItem(6, MainMenuItem.getItemStack(lang));
    }
    
    /**
     * ロビー上で装備させるアイテムを渡す
     */
    public void setLobbyItem(){
        
        this.equipHeadGear();
        this.setMainMenu();
    }
    
    /**
     * 表示するプレイヤーを設定する
     * @param option ObservableOption
     */
    public void setObservableOption(ObservableOption option){
        this.observableOption = option;
        
        switch (observableOption){
            case ALONE:{
                if(this.player == null) break;
                if(this.shellCaseTeam == null) break;
                
                TaskHandler.runSync(() ->{
                    for(Player op : Bukkit.getServer().getOnlinePlayers()) {
                        if(player != op) player.hidePlayer(ShellCase.getPlugin(), op);
                    }
                }, ShellCase.getPlugin());
                break;
            }
            
            case ONLY_MATCH_PLAYER:{
                if(this.player == null) break;
                if(this.shellCaseTeam == null) break;
                Set<Player> hidePlayers = new HashSet<>();
                Set<Player> showPlayers = new HashSet<>();
                
                for(Player op : Bukkit.getServer().getOnlinePlayers()){
                    ShellCasePlayer shellCasePlayer = ShellCasePlayer.getShellCasePlayer(op);
                    if(shellCasePlayer.getShellCaseTeam() == null) continue;
                    
                    if(shellCasePlayer.getShellCaseTeam().getMatch() == this.shellCaseTeam.getMatch()){
                        showPlayers.add(op);
                    }else{
                        hidePlayers.add(op);
                    }
                }
    
                TaskHandler.runSync(() -> {
                    for(Player op : hidePlayers) {
                        if(player != op) player.hidePlayer(ShellCase.getPlugin(), op);
                    }
                    for(Player op : showPlayers) {
                        if(player != op) player.showPlayer(ShellCase.getPlugin(), op);
                    }
                }, ShellCase.getPlugin());
                break;
            }
            
            case ALL_PLAYER:{
                if(this.player == null) break;
    
                TaskHandler.runSync(() -> {
                    for(Player op : Bukkit.getServer().getOnlinePlayers()) {
                        if(player != op) player.showPlayer(ShellCase.getPlugin(), op);
                    }
                }, ShellCase.getPlugin());
                break;
            }
        }
    }
    
    /**
     * ヘッドギアを装備させる
     * @param headGear
     */
    public void setHeadGear(HeadGear headGear, int headGearNumber){
        this.headGear = headGear;
        this.headGearNumber = headGearNumber;
        
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
     * 装備しているギアやメインウエポンからパッシブ効果を作成します
     */
    public void createPassiveInfluence(){
        //this.passiveInfluence.createPassiveInfluence(this);
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
     * @return Map<ShellCaseWeapon, WeaponStatusData>
     */
    public Map<ShellCaseWeapon, WeaponStatusData> getWeaponStatusDataMap() {return weaponStatusDataMap;}

    /**
     * 武器のステータスを取得する
     * @return
     */
    public WeaponStatusData getWeaponStatusData(ShellCaseWeapon shellCaseWeapon){
        WeaponStatusData weaponStatusData = this.weaponClass.getWeaponStatusData(shellCaseWeapon);
        if(weaponStatusData != null) return weaponStatusData;

        weaponStatusData = this.weaponStatusDataMap.get(shellCaseWeapon);
        if(weaponStatusData != null) return weaponStatusData;

        return WeaponStatusData.createWeaponStatusData(shellCaseWeapon, this);
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
        player.sendMessage("[§6ShellCase§r] " + message);
    }
    
    /**
     * 言語別のメッセージを送信します。
     * @param lang 言語
     * @param textName message.ymlに設定されているテキストの名前
     */
    public void sendText(Lang lang, String textName){
        if(player == null) return;
        player.sendMessage("[§6ShellCase§r] " + MessageManager.getText(lang, textName));
    }
    
    /**
     * 言語別のメッセージを送信します。
     * @param textName message.ymlに設定されているテキストの名前
     */
    public void sendText(String textName){
        if(player == null) return;
        player.sendMessage("[§6ShellCase§r] " + MessageManager.getText(lang, textName));
    }

    /**
     * 言語別のメッセージを送信します。
     * @param textName message.ymlに設定されているテキストの名前
     * @param args 置き換える値 (%d等)
     */
    public void sendText(String textName, Object... args){
        if(player == null) return;
        player.sendMessage("[§6ShellCase§r] " + String.format(MessageManager.getText(lang, textName), args));
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
        
        TaskHandler.runWorldSync(() -> {
            if (player == null) return;
            if (time != teleportTime) return;
            player.teleport(location);
        }, player.getWorld(), ShellCase.getPlugin());
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
        
        TaskHandler.runWorldSync(() -> {
            if (player == null) return;
            player.teleport(location);
            player.setGameMode(GameMode.ADVENTURE);
            setDeath(false);
        }, player.getWorld(), ShellCase.getPlugin());
    }
    
    /**
     * ゲームモードを変更します
     * @param gameMode 設定するゲームモード
     */
    public void setGameMode(GameMode gameMode){
        if(player == null) return;
        
        TaskHandler.runWorldSync(() -> {
            if (player == null) return;
            player.setGameMode(gameMode);
        }, player.getWorld(), ShellCase.getPlugin());
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
     * @param sound ShellCaseSound
     */
    public void playSound(ShellCaseSound sound){
        if(player == null) return;
        sound.play(player, player.getLocation());
    }
    
    /**
     * 音を再生する
     * @param sound ShellCaseSound
     * @param location 音を再生する座標
     */
    public void playSound(ShellCaseSound sound, Location location){
        if(player == null) return;
        sound.play(player, location);
    }
    
    /**
     * パーティクルを表示する
     * @param particle ShellCaseParticle
     * @param location パーティクルを表示する座標
     */
    public void spawnParticle(ShellCaseParticle particle, Location location){
        if(player == null) return;
        particle.spawn(player, location);
    }

    /**
     * パーティクルを表示する
     * @param particle ShellCaseParticle
     * @param location パーティクルを表示する座標
     * @param settings パーティクルを表示するかどうかの設置項目
     */
    public void spawnParticle(ShellCaseParticle particle, Location location, Settings settings){
        if(player == null) return;
        if(!this.playerSettings.getSettings(settings)) return;
        particle.spawn(player, location);
    }
    
    /**
     * 距離を無視してパーティクルを表示する
     * @param particle ShellCaseParticle
     * @param location パーティクルを表示する座標
     * @param settings パーティクルを表示するかどうかの設置項目
     */
    public void spawnParticleIgnoreRange(ShellCaseParticle particle, Location location, Settings settings){
        if(player == null) return;
        if(!this.playerSettings.getSettings(settings)) return;
        particle.spawnIgnoreRange(player, location);
    }
    
    /**
     * プレイヤーを回復させる
     * @param plus
     */
    public synchronized void heal(float plus){
        if(player == null) return;
        if(this.shellCaseTeam == null) return;
        
        if(this.health + plus < 20.0F){
            this.health += plus;
            PacketPlayOutUpdateHealth updateHealth = new PacketPlayOutUpdateHealth(this.health, this.foodLevel, 0.0F);
            this.sendPacket(updateHealth);
        }else if(this.health != 20.0F){
            this.health = 20.0F;
            PacketPlayOutUpdateHealth updateHealth = new PacketPlayOutUpdateHealth(this.health, this.foodLevel, 0.0F);
            this.sendPacket(updateHealth);
        }
    }
    
    /**
     * プレイヤーに毒ダメージを与えます
     * @param damage
     */
    public synchronized void givePoisonDamage(float damage){
        if(player == null) return;
        if(this.shellCaseTeam == null) return;
        
        if(this.health > damage){
            this.health -= damage;
            PacketPlayOutUpdateHealth updateHealth = new PacketPlayOutUpdateHealth(this.health, this.foodLevel, 0.0F);
            PacketPlayOutAnimation animation = new PacketPlayOutAnimation(((CraftPlayer)player).getHandle(), 1);
            this.sendPacket(updateHealth);
            this.shellCaseTeam.getMatch().sendPacket(animation);
            this.shellCaseTeam.getMatch().playSound(HIT_SOUND, player.getLocation());
        }
    }
    
    /**
     * プレイヤーにダメージを与える
     * @param damage 与えるダメージ
     * @param attacker 攻撃者
     * @param ShellCaseWeapon 攻撃に使用した武器
     */
    public synchronized void giveDamage(float damage, ShellCasePlayer attacker, Vector velocity, ShellCaseWeapon ShellCaseWeapon){
        if(player == null) return;
        if(attacker.getBukkitPlayer() == null) return;
        if(this.shellCaseTeam == null) return;
        
        if(this.getArmor() > 0.0 && velocity != null){
            Vector XZVec = new Vector(velocity.getX(), 0.0, velocity.getZ());
            if(XZVec.lengthSquared() > 0.0) XZVec.normalize();
            player.setVelocity(XZVec);
        }
        
        if(this.getHealth() + this.getArmor() > damage){
            if(this.getArmor() > damage){
                this.setArmor(this.getArmor() - damage);
            }else{
                //give damage
                float d = damage - this.getArmor();
                this.setHealth(this.getHealth() - d);
                this.setArmor(0.0F);
    
                PacketPlayOutUpdateHealth updateHealth = new PacketPlayOutUpdateHealth(this.health, this.foodLevel, 0.0F);
                PacketPlayOutAnimation animation = new PacketPlayOutAnimation(((CraftPlayer)player).getHandle(), 1);
                this.sendPacket(updateHealth);
                this.shellCaseTeam.getMatch().sendPacket(animation);
    
                attacker.playSound(HIT_SOUND_FOR_ATTACKER);
                this.shellCaseTeam.getMatch().playSound(HIT_SOUND, player.getLocation());
            }
        }else{
            //死亡処理
            this.setHealth(20.0F);
            PlayerDeathManager.death(this, attacker, ShellCaseWeapon, DeathType.KILLED_BY_PLAYER);
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
        if(shellCaseTeam == null) player.getName();
        
        return shellCaseTeam.getShellCaseColor().getChatColor() + (bold ? "§l" : "") + player.getName() + "§r";
    }
}
