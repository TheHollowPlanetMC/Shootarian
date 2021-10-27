package be4rjp.shellcase.weapon;

import be4rjp.shellcase.entity.AsyncFallingBlock;
import be4rjp.shellcase.item.ShellCaseItem;
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
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public abstract class ShellCaseWeapon extends ShellCaseItem {
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
    //一発分のダメージ
    protected float damage = 1.0F;
    //デフォルトの最大弾数
    protected int defaultBullets = 20;
    //メイン武器として使えるかどうか
    protected boolean isMain = false;
    
    public ShellCaseWeapon(String id){
        this.id = id;
        weaponMap.put(id, this);
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
        itemMeta.setLore(super.getDescription(lang));
        itemStack.setItemMeta(itemMeta);

        return WeaponManager.writeNBTTag(this, itemStack);
    }

    /**
     * 識別名を取得する
     * @return String
     */
    public String getID() {return id;}
    
    /**
     * 一発分のダメージを取得する
     * @return double
     */
    public float getDamage() {return damage;}

    /**
     * デフォルトの最大弾数を取得する
     * @return int
     */
    public int getDefaultBullets() {return defaultBullets;}
    
    /**
     * この武器を持って右クリックしたときの処理
     * @param shellCasePlayer player
     */
    public abstract void onRightClick(ShellCasePlayer shellCasePlayer);
    
    /**
     * この武器を持って左クリックしたときの処理
     * @param shellCasePlayer player
     */
    public abstract void onLeftClick(ShellCasePlayer shellCasePlayer);

    /**
     * メイン武器として使えるかどうか
     * @return boolean
     */
    public boolean isMain() {return isMain;}



    private static final ShellCaseSound EXPLOSION_SOUND = new ShellCaseSound(Sound.ENTITY_GENERIC_EXPLODE, 1.2F, 1.0F);
    private static final ShellCaseParticle EXPLOSION_PARTICLE1 = new NormalParticle(Particle.EXPLOSION_NORMAL, 1, 0, 0, 0, 0);
    private static final ShellCaseParticle EXPLOSION_PARTICLE2 = new NormalParticle(Particle.EXPLOSION_HUGE, 1, 0, 0, 0, 0);
    
    /**
     * 爆発を作成する
     * @param shellCasePlayer 爆発を起こしたプレイヤー
     * @param ShellCaseWeapon 爆発を起こした武器
     * @param center 爆発の中心
     * @param radius 爆発の半径
     */
    public static void createExplosion(ShellCasePlayer shellCasePlayer, ShellCaseWeapon ShellCaseWeapon, Location center, double radius, double velocityRate){
        ShellCaseTeam shellCaseTeam = shellCasePlayer.getShellCaseTeam();
        if(shellCaseTeam == null) return;
    
        //エフェクト
        shellCaseTeam.getMatch().spawnParticle(EXPLOSION_PARTICLE1, center);
        shellCaseTeam.getMatch().spawnParticle(EXPLOSION_PARTICLE2, center);
        
        //音
        shellCaseTeam.getMatch().playSound(EXPLOSION_SOUND, center);
        
        //ダメージ
        for(ShellCasePlayer matchPlayer : shellCaseTeam.getMatch().getPlayers()){
            if(matchPlayer.isDeath()) continue;
            
            double distance = Math.sqrt(LocationUtil.distanceSquaredSafeDifferentWorld(center, matchPlayer.getLocation()));
            if(distance >= radius) continue;
    
            Location loc = matchPlayer.getLocation();
            Vector velocity = new Vector(loc.getX() - center.getX(), loc.getY() - center.getY(), loc.getZ() - center.getZ());
            double length = velocity.length();
            double newLength = radius - length;
            newLength = Math.max(0.1, newLength);
            newLength = newLength * velocityRate;
            if(length > 0.0){
                velocity.normalize().multiply(newLength);
            }
            
            if(matchPlayer.getShellCaseTeam() == shellCaseTeam){
                
                matchPlayer.setVelocity(velocity);
                
                continue;
            }
            
            double damage = ((radius - distance) / radius) * ShellCaseWeapon.getDamage();
            matchPlayer.giveDamage((float) damage, shellCasePlayer, velocity, ShellCaseWeapon);
        }
        
        //破壊
        SphereBlocks sphereBlocks = new SphereBlocks(radius, center);
        for(Block block : sphereBlocks.getBlocks()){
            double distance = Math.sqrt(LocationUtil.distanceSquaredSafeDifferentWorld(center, block.getLocation()));
            if(distance * Math.random() > radius / 1.5) continue;
            
            for(MapStructureData mapStructureData : shellCaseTeam.getMatch().getMapStructureData()){
                
                if(!mapStructureData.getBoundingBox().isInBox(block.getLocation().toVector())) continue;
                if(block.getType().toString().endsWith("AIR")) continue;
                mapStructureData.giveDamage(1);
                
                if(mapStructureData.isDead()) continue;

                //FallingBlock
                if(new Random().nextInt(5) == 0) {
                    AsyncFallingBlock asyncFallingBlock = new AsyncFallingBlock(shellCaseTeam.getMatch(), block.getLocation(), block.getBlockData());
                    
                    Vector velocity = new Vector(block.getX() - center.getX(), block.getY() - center.getY(), block.getZ() - center.getZ());
                    double length = velocity.length();
                    double newLength = radius - length;
                    newLength = Math.max(0.1, newLength);
                    if(length > 0.0){
                        velocity.normalize().multiply(newLength);
                    }
                    
                    asyncFallingBlock.setVelocity(velocity);
                    asyncFallingBlock.spawn();
                }

                shellCaseTeam.getMatch().getBlockUpdater().remove(block);
            }
        }
    }
}
