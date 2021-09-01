package be4rjp.shellcase.weapon.gun;

import be4rjp.shellcase.language.Lang;
import be4rjp.shellcase.player.ShellCasePlayer;
import be4rjp.shellcase.player.passive.Passive;
import be4rjp.shellcase.util.ShellCaseSound;
import be4rjp.shellcase.weapon.ShellCaseWeapon;
import be4rjp.shellcase.weapon.WeaponStatusData;
import be4rjp.shellcase.weapon.attachment.Attachment;
import be4rjp.shellcase.weapon.attachment.Sight;
import be4rjp.shellcase.weapon.reload.Actions;
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
    public static Collection<GunWeapon> getMainWeaponList(){return gunWeaponMap.values();}
    
    
    //設定ファイル
    protected YamlConfiguration yml;
    //射撃時に鳴らすサウンド
    protected ShellCaseSound shootSound = new ShellCaseSound(Sound.ENTITY_PIG_STEP, 0.3F, 1F);
    //武器のパッシブ効果
    protected List<Passive> passiveList = new ArrayList<>();
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
    public abstract MainWeaponType getType();
    
    /**
     * この武器についているパッシブ効果を取得する
     * @return List<Passive>
     */
    public List<Passive> getPassiveList() {return passiveList;}
    
    /**
     * 弾の大きさを取得する
     * @return double
     */
    public double getBulletSize() {return bulletSize;}
    
    public Sight getDefaultSight() {return defaultSight;}
    
    public Actions getReloadActions() {return reloadActions;}
    
    public Actions getCombatReloadActions() {return combatReloadActions;}
    
    public float getADSWalkSpeed() {return adsWalkSpeed;}
    
    /**
     * ymlファイルからロードする
     * @param yml
     */
    public void loadData(YamlConfiguration yml){
        this.yml = yml;
        
        if(yml.contains("display-name")){
            for(String languageName : yml.getConfigurationSection("display-name").getKeys(false)){
                Lang lang = Lang.valueOf(languageName);
                String name = yml.getString("display-name." + languageName);
                this.displayName.put(lang, ChatColor.translateAlternateColorCodes('&', name));
            }
        }
        if(yml.contains("material")) this.material = Material.getMaterial(Objects.requireNonNull(yml.getString("material")));
        if(yml.contains("custom-model-data")) this.modelID = yml.getInt("custom-model-data");
        if(yml.contains("damage")) this.damage = (float)yml.getDouble("damage");
        if(yml.contains("sound")) this.shootSound = ShellCaseSound.getSoundByString(Objects.requireNonNull(yml.getString("sound")));
        if(yml.contains("passive")) yml.getStringList("passive").forEach(passiveString -> passiveList.add(Passive.valueOf(passiveString)));
        if(yml.contains("bullet-size")) this.bulletSize = yml.getDouble("bullet-size");
        if(yml.contains("default-bullets")) this.defaultBullets = yml.getInt("default-bullets");
        if(yml.contains("default-sight")) this.defaultSight = (Sight) Attachment.getAttachment(yml.getString("default-sight"));
        if(yml.contains("reload")) this.reloadActions = Actions.getReloadAction(yml.getString("reload"));
        if(yml.contains("reload-combat")) this.combatReloadActions = Actions.getReloadAction(yml.getString("reload-combat"));
        if(yml.contains("ads-walk-speed")) this.adsWalkSpeed = (float) yml.getDouble("ads-walk-speed");
        
        loadDetailsData();
    }
    
    /**
     * ymlファイルから詳細なデータをロードする
     */
    public abstract void loadDetailsData();
    
    
    
    public enum MainWeaponType{
        FULL_AUTO_GUN(FullAutoGun.class);
        
        private final Class<? extends GunWeapon> weaponClass;
        
        MainWeaponType(Class<? extends GunWeapon> weaponClass){
            this.weaponClass = weaponClass;
        }
        
        public GunWeapon createGunWeaponInstance(String id){
            try{
                return weaponClass.getConstructor(String.class).newInstance(id);
            }catch (Exception e){e.printStackTrace();}
            return null;
        }
    }
}
