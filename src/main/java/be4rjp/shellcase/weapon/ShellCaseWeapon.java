package be4rjp.shellcase.weapon;

import be4rjp.shellcase.language.Lang;
import be4rjp.shellcase.match.map.structure.MapStructureData;
import be4rjp.shellcase.match.team.ShellCaseTeam;
import be4rjp.shellcase.player.ShellCasePlayer;
import be4rjp.shellcase.util.LocationUtil;
import be4rjp.shellcase.util.ShellCaseSound;
import be4rjp.shellcase.util.SphereBlocks;
import be4rjp.shellcase.util.math.Sphere;
import be4rjp.shellcase.util.particle.BlockParticle;
import be4rjp.shellcase.util.particle.NormalParticle;
import be4rjp.shellcase.util.particle.ShellCaseParticle;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
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
    //武器のマテリアル
    protected Material material = Material.BARRIER;
    //CustomModelDataのID
    protected int modelID = 0;
    //一発分のダメージ
    protected float damage = 1.0F;
    //デフォルトの最大弾数
    protected int defaultBullets = 20;
    
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


    /**
     * ItemStackを取得する
     * @return ItemStack
     */
    public ItemStack getItemStack(Lang lang){
        ItemStack itemStack = new ItemStack(material);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(this.getDisplayName(lang));
        itemMeta.setCustomModelData(modelID);
        itemStack.setItemMeta(itemMeta);

        return WeaponManager.writeNBTTag(this, itemStack);
    }

    /**
     * 識別名を取得する
     * @return String
     */
    public String getID() {return id;}

    /**
     * マテリアルを取得する
     * @return Material
     */
    public Material getMaterial() {return material;}

    /**
     * CustomModelDataのIDを取得する
     * @return int
     */
    public int getModelID() {return modelID;}
    
    /**
     * 一発分のダメージを取得する
     * @return double
     */
    public float getDamage() {return damage;}

    /**
     * デフォルトの最大弾数を取得する
     * @return
     */
    public int getDefaultBullets() {return defaultBullets;}
    
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
     * @param shellCasePlayer 爆発を起こしたプレイヤー
     * @param ShellCaseWeapon 爆発を起こした武器
     * @param center 爆発の中心
     * @param radius 爆発の半径
     */
    public static void createExplosion(ShellCasePlayer shellCasePlayer, ShellCaseWeapon ShellCaseWeapon, Location center, double radius){
        ShellCaseTeam shellCaseTeam = shellCasePlayer.getShellCaseTeam();
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
            otherTeamPlayer.giveDamage((float) damage, shellCasePlayer, velocity, ShellCaseWeapon);
        }
        
        //破壊
        SphereBlocks sphereBlocks = new SphereBlocks(radius, center);
        for(Block block : sphereBlocks.getBlocks()){
            double distance = Math.sqrt(LocationUtil.distanceSquaredSafeDifferentWorld(center, block.getLocation()));
            if(distance * Math.random() > radius / 1.5) continue;
            
            for(MapStructureData mapStructureData : shellCaseTeam.getMatch().getMapStructureData()){
                
                if(!mapStructureData.getBoundingBox().isInBox(block.getLocation().toVector())) continue;
                mapStructureData.giveDamage(1);
                
                if(mapStructureData.isDead()) continue;
                shellCaseTeam.getMatch().getBlockUpdater().remove(block);
            }
        }
    }
}
