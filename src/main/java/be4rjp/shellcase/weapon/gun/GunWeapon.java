package be4rjp.shellcase.weapon.gun;

import be4rjp.shellcase.language.Lang;
import be4rjp.shellcase.player.ShellCasePlayer;
import be4rjp.shellcase.player.passive.Passive;
import be4rjp.shellcase.player.passive.PassiveInfluence;
import be4rjp.shellcase.util.ShellCaseSound;
import be4rjp.shellcase.weapon.ShellCaseWeapon;
import be4rjp.shellcase.weapon.WeaponStatusData;
import be4rjp.shellcase.weapon.attachment.Attachment;
import be4rjp.shellcase.weapon.attachment.Sight;
import be4rjp.shellcase.weapon.recoil.Recoil;
import be4rjp.shellcase.weapon.actions.Actions;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public abstract class GunWeapon extends ShellCaseWeapon {
    
    //識別IDとメインウエポンのマップ
    private static Map<String, GunWeapon> gunWeaponMap = new ConcurrentHashMap<>();
    
    public static void initialize(){
        gunWeaponMap.clear();
    }
    
    /**
     * 識別IDからメインウエポンを取得します
     * @param id 識別ID
     * @return MainWeapon
     */
    public static GunWeapon getGunWeapon(String id){return gunWeaponMap.get(id);}
    
    /**
     * メインウエポンのリストを取得します
     * @return Collection<MainWeapon>
     */
    public static Collection<GunWeapon> getGunWeaponList(){return gunWeaponMap.values();}
    
    /**
     * SaveNumberからGunWeaponを取得します
     * @param saveNumber int
     * @return GunWeapon
     */
    public static GunWeapon getGunWeaponBySaveNumber(int saveNumber){
        for(GunWeapon gunWeapon : gunWeaponMap.values()){
            if(gunWeapon.getSaveNumber() == saveNumber) return gunWeapon;
        }
        return null;
    }
    
    
    //設定ファイル
    protected YamlConfiguration yml;
    //SQLに保存するための識別番号
    protected int saveNumber = 0;
    //射撃時に鳴らすサウンド
    protected ShellCaseSound shootSound = new ShellCaseSound(Sound.ENTITY_PIG_STEP, 0.3F, 1F);
    //武器のパッシブ効果
    protected List<PassiveInfluence> passiveInfluenceList = new ArrayList<>();
    //弾の大きさ
    protected double bulletSize = 0.1;
    //ADS中の移動速度
    protected float adsWalkSpeed = 0.1F;
    //デフォルトサイト
    protected Sight defaultSight = (Sight) Attachment.getAttachment("rds");
    //リロードアクション
    protected Actions reloadActions = new Actions("null");
    //コンバットリロードアクション
    protected Actions combatReloadActions = new Actions("null");
    //撃ってから落ち始めるまでのtick
    protected int fallTick = 0;
    //射撃間隔
    protected int shootTick = 1;
    //射撃する弾の速度
    protected double shootSpeed = 0.1;
    //ADS時のリコイル
    protected Recoil adsRecoil = null;
    //腰撃ち時のリコイル
    protected Recoil normalRecoil = null;
    //リコイル
    protected final HipShootingRecoil hipShootingRecoil = new HipShootingRecoil();
    //装着可能なアタッチメント
    protected final Set<Attachment> attachments = new HashSet<>();
    
    public GunWeapon(String id){
        super(id);
        gunWeaponMap.put(id, this);
    }
    
    /**
     * 射撃時に鳴らすサウンドを取得する
     * @return ShellCaseSound
     */
    public ShellCaseSound getShootSound() {return shootSound;}

    
    @Override
    public abstract void onRightClick(ShellCasePlayer shellCasePlayer);
    
    
    @Override
    public void onLeftClick(ShellCasePlayer shellCasePlayer) {
        WeaponStatusData gunStatusData = shellCasePlayer.getWeaponStatusData(this);
        if(gunStatusData == null) return;
    
        shellCasePlayer.switchADS((GunStatusData) gunStatusData);
    }
    
    /**
     * 武器のタイプを取得する
     * @return MainWeaponType
     */
    public abstract GunWeaponType getType();
    
    /**
     * この武器についているパッシブ効果を取得する
     * @return List<PassiveInfluence>
     */
    public List<PassiveInfluence> getPassiveInfluenceList() {return passiveInfluenceList;}
    
    /**
     * 弾の大きさを取得する
     * @return double
     */
    public double getBulletSize() {return bulletSize;}
    
    public Sight getDefaultSight() {return defaultSight;}
    
    public Actions getReloadActions() {return reloadActions;}
    
    public Actions getCombatReloadActions() {return combatReloadActions;}
    
    public float getADSWalkSpeed() {return adsWalkSpeed;}
    
    public double getShootSpeed() {return shootSpeed;}
    
    public int getFallTick() {return fallTick;}
    
    public int getShootTick() {return shootTick;}
    
    public Recoil getADSRecoil(){return adsRecoil;}
    
    public Recoil getNormalRecoil() {return normalRecoil;}
    
    public HipShootingRecoil getHipShootingRecoil() {return hipShootingRecoil;}
    
    public int getSaveNumber() {return saveNumber;}
    
    public Set<Attachment> getAttachments() {return attachments;}
    
    /**
     * ymlファイルからロードする
     * @param yml
     */
    public void loadData(YamlConfiguration yml){
        this.yml = yml;
        
        super.itemLoad(yml);
        
        if(yml.contains("save-number")) this.saveNumber = yml.getInt("save-number");
        if(yml.contains("damage")) this.damage = (float)yml.getDouble("damage");
        if(yml.contains("sound")) this.shootSound = ShellCaseSound.getSoundByString(Objects.requireNonNull(yml.getString("sound")));
        if(yml.contains("passive")) yml.getStringList("passive").forEach(passiveString -> passiveInfluenceList.add(PassiveInfluence.fromString(passiveString)));
        if(yml.contains("bullet-size")) this.bulletSize = yml.getDouble("bullet-size");
        if(yml.contains("main-weapon")) super.isMain = yml.getBoolean("main-weapon");
        if(yml.contains("default-bullets")) this.defaultBullets = yml.getInt("default-bullets");
        if(yml.contains("default-sight")) this.defaultSight = (Sight) Attachment.getAttachment(yml.getString("default-sight"));
        if(yml.contains("reload")) this.reloadActions = Actions.getAction(yml.getString("reload"));
        if(yml.contains("reload-combat")) this.combatReloadActions = Actions.getAction(yml.getString("reload-combat"));
        if(yml.contains("ads-walk-speed")) this.adsWalkSpeed = (float) yml.getDouble("ads-walk-speed");
    
        if(yml.contains("fall-tick")) this.fallTick = yml.getInt("fall-tick");
        if(yml.contains("shoot-tick")) this.shootTick = yml.getInt("shoot-tick");
        if(yml.contains("shoot-speed")) this.shootSpeed = yml.getDouble("shoot-speed");
        if(yml.contains("ads-recoil")) this.adsRecoil = Recoil.getRecoil(yml.getString("ads-recoil"));
        if(yml.contains("normal-recoil")) this.normalRecoil = Recoil.getRecoil(yml.getString("normal-recoil"));
    
        if(yml.contains("hip-shooting-recoil")){
            if(yml.contains("hip-shooting-recoil.shoot-random")) hipShootingRecoil.setShootRandom(yml.getDouble("hip-shooting-recoil.shoot-random"));
            if(yml.contains("hip-shooting-recoil.shoot-max-random")) hipShootingRecoil.setShootMaxRandom(yml.getDouble("hip-shooting-recoil.shoot-max-random"));
            if(yml.contains("hip-shooting-recoil.increase-min-tick")) hipShootingRecoil.setMinTick(yml.getInt("hip-shooting-recoil.increase-min-tick"));
            if(yml.contains("hip-shooting-recoil.increase-max-tick")) hipShootingRecoil.setMaxTick(yml.getInt("hip-shooting-recoil.increase-max-tick"));
            if(yml.contains("hip-shooting-recoil.increase-reset-tick")) hipShootingRecoil.setResetTick(yml.getInt("hip-shooting-recoil.increase-reset-tick"));
        }
        
        if(yml.contains("attachment")){
            for(String line : yml.getStringList("attachment")){
                Attachment attachment = Attachment.getAttachment(line);
                if(attachment != null) this.attachments.add(attachment);
            }
        }
        
        loadDetailsData();
    }
    
    /**
     * ymlファイルから詳細なデータをロードする
     */
    public abstract void loadDetailsData();
    
    
    
    public enum GunWeaponType {
        FULL_AUTO_GUN(FullAutoGun.class),
        SEMI_AUTO_GUN(SemiAutoGun.class);
        
        private final Class<? extends GunWeapon> weaponClass;
        
        GunWeaponType(Class<? extends GunWeapon> weaponClass){
            this.weaponClass = weaponClass;
        }
        
        public GunWeapon createGunWeaponInstance(String id){
            try{
                return weaponClass.getConstructor(String.class).newInstance(id);
            }catch (Exception e){e.printStackTrace();}
            return null;
        }
    }
    
    
    public static class HipShootingRecoil {
        //撃った時の弾の散らばり
        private double shootRandom = 0.0;
        //撃った時の弾の散らばりの最大値
        private double shootMaxRandom = 1.0;
        //散らばりが増加し始めるtick
        private int minTick = 10;
        //散らばりが最大値に達するtick
        private int maxTick = 30;
        //撃つのをやめてから散らばりがリセットされるtick
        private int resetTick = 40;
        
        /**
         * 散らばりが最大値に達するtick
         * @param maxTick
         */
        public void setMaxTick(int maxTick) {this.maxTick = maxTick;}
        
        /**
         * 散らばりが増加し始めるtick
         * @param minTick
         */
        public void setMinTick(int minTick) {this.minTick = minTick;}
        
        /**
         * 撃つのをやめてから散らばりがリセットされるtick
         * @param resetTick
         */
        public void setResetTick(int resetTick) {this.resetTick = resetTick;}
        
        /**
         * 撃った時の弾の散らばりの最大値
         * @param shootMaxRandom
         */
        public void setShootMaxRandom(double shootMaxRandom) {this.shootMaxRandom = shootMaxRandom;}
        
        /**
         * 撃った時の弾の散らばり
         * @param shootRandom
         */
        public void setShootRandom(double shootRandom) {this.shootRandom = shootRandom;}
        
        /**
         * 撃つのをやめてから散らばりがリセットされるtick
         * @return int
         */
        public int getResetTick() {return resetTick;}
        
        public double getShootRandomRange(int clickTick){
            if(clickTick <= minTick){
                return shootRandom;
            }else{
                if(clickTick < maxTick){
                    double rate = (double)(clickTick - minTick) / (double)(maxTick - minTick);
                    double m = shootMaxRandom - shootRandom;
                    return shootRandom + (m * rate);
                }else{
                    return shootMaxRandom;
                }
            }
        }
    }
}
