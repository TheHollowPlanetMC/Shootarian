package be4rjp.shellcase.weapon;

import be4rjp.shellcase.language.Lang;
import be4rjp.shellcase.match.team.ShellCaseTeam;
import be4rjp.shellcase.player.ShellCasePlayer;
import be4rjp.shellcase.util.LocationUtil;
import be4rjp.shellcase.util.ShellCaseSound;
import be4rjp.shellcase.util.math.Sphere;
import be4rjp.shellcase.util.particle.BlockParticle;
import be4rjp.shellcase.util.particle.NormalParticle;
import be4rjp.shellcase.util.particle.ShellCaseParticle;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.util.Vector;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public abstract class ShellCaseWeapon {
    //識別IDと武器のマップ
    private static Map<String, ShellCaseWeapon> weaponMap = new ConcurrentHashMap<>();
    
    public static void initialize(){
        weaponMap.clear();
    }
    
    /**
     * 識別IDから武器を取得する
     * @param id 識別ID
     * @return ShellCaseWeapon
     */
    public static ShellCaseWeapon getShellCaseWeapon(String id){
        return weaponMap.get(id);
    }
    
    /**
     * 武器を登録する
     * @param id 識別ID
     * @param ShellCaseWeapon 登録する武器のインスタンス
     */
    public static void registerShellCaseWeapon(String id, ShellCaseWeapon ShellCaseWeapon){
        weaponMap.put(id, ShellCaseWeapon);
    }
    
    
    public static Collection<ShellCaseWeapon> getWeaponList(){return weaponMap.values();}
    
    
    
    //武器の識別名
    protected final String id;
    //武器の表示名
    protected Map<Lang, String> displayName = new HashMap<>();
    //一発分のダメージ
    protected float damage = 0.0F;
    
    public ShellCaseWeapon(String id){
        this.id = id;
        weaponMap.put(id, this);
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

    
    public String getId(){return id;}
    
    /**
     * 一発分のダメージを取得する
     * @return double
     */
    public float getDamage() {return damage;}
    
    /**
     * この武器を持って右クリックしたときの処理
     * @param shellCasePlayer
     */
    public abstract void onRightClick(ShellCasePlayer shellCasePlayer);
    
    /**
     * この武器を持って左クリックしたときの処理
     * @param shellCasePlayer
     */
    public abstract void onLeftClick(ShellCasePlayer shellCasePlayer);
    
    
    
    private static ShellCaseSound EXPLOSION_SOUND = new ShellCaseSound(Sound.ENTITY_GENERIC_EXPLODE, 1.0F, 1.0F);
    private static ShellCaseParticle EXPLOSION_PARTICLE1 = new NormalParticle(Particle.EXPLOSION_NORMAL, 5, 0, 0, 0, 0);
    
    /**
     * 爆発を作成する
     * @param ShellCasePlayer 爆発を起こしたプレイヤー
     * @param ShellCaseWeapon 爆発を起こした武器
     * @param center 爆発の中心
     * @param radius 爆発の半径
     */
    public static void createExplosion(ShellCasePlayer ShellCasePlayer, ShellCaseWeapon ShellCaseWeapon, Location center, double radius){
        ShellCaseTeam shellCaseTeam = ShellCasePlayer.getShellCaseTeam();
        if(shellCaseTeam == null) return;
    
        //エフェクト
        shellCaseTeam.getMatch().spawnParticle(EXPLOSION_PARTICLE1, center);
        
        //音
        shellCaseTeam.getMatch().playSound(EXPLOSION_SOUND, center);
        
        //ダメージ
        for(ShellCasePlayer otherTeamPlayer : shellCaseTeam.getOtherTeamPlayers()){
            if(otherTeamPlayer.isDeath()) continue;
            
            double distance = Math.sqrt(LocationUtil.distanceSquaredSafeDifferentWorld(center, otherTeamPlayer.getLocation()));
            if(distance >= radius) continue;
    
            Location loc = otherTeamPlayer.getLocation();
            Vector velocity = new Vector(loc.getX() - center.getX(), loc.getY() - center.getY(), loc.getZ() - center.getZ());
            double damage = ((radius - distance) / radius) * ShellCaseWeapon.getDamage();
            otherTeamPlayer.giveDamage((float) damage, ShellCasePlayer, velocity, ShellCaseWeapon);
        }
    }
}
