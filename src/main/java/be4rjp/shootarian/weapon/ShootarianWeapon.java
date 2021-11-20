package be4rjp.shootarian.weapon;

import be4rjp.shootarian.entity.AsyncFallingBlock;
import be4rjp.shootarian.item.ShootarianItem;
import be4rjp.shootarian.language.Lang;
import be4rjp.shootarian.match.map.structure.MapStructureData;
import be4rjp.shootarian.match.team.ShootarianTeam;
import be4rjp.shootarian.player.ShootarianPlayer;
import be4rjp.shootarian.util.LocationUtil;
import be4rjp.shootarian.util.ShootarianSound;
import be4rjp.shootarian.util.SphereBlocks;
import be4rjp.shootarian.util.TaskHandler;
import be4rjp.shootarian.util.particle.NormalParticle;
import be4rjp.shootarian.util.particle.ShootarianParticle;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public abstract class ShootarianWeapon extends ShootarianItem {
    //識別IDと武器のマップ
    private static Map<String, ShootarianWeapon> weaponMap = new ConcurrentHashMap<>();
    
    public static void initialize(){
        weaponMap.clear();
    }
    
    /**
     * 識別IDから武器を取得する
     * @param id 識別ID
     * @return ShootarianWeapon
     */
    public static ShootarianWeapon getShootarianWeapon(String id){
        return weaponMap.get(id);
    }
    
    /**
     * 武器を登録する
     * @param id 識別ID
     * @param ShootarianWeapon 登録する武器のインスタンス
     */
    public static void registerShootarianWeapon(String id, ShootarianWeapon ShootarianWeapon){
        weaponMap.put(id, ShootarianWeapon);
    }
    
    
    public static Collection<ShootarianWeapon> getWeaponList(){return weaponMap.values();}
    
    
    
    //武器の識別名
    protected final String id;
    //一発分のダメージ
    protected float damage = 1.0F;
    //デフォルトの最大弾数
    protected int defaultBullets = 20;
    //メイン武器として使えるかどうか
    protected boolean isMain = false;
    
    public ShootarianWeapon(String id){
        this.id = id;
        weaponMap.put(id, this);
    }


    /**
     * ItemStackを取得する
     * @return ItemStack
     */
    @Override
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
     * @param shootarianPlayer player
     */
    public abstract void onRightClick(ShootarianPlayer shootarianPlayer);
    
    /**
     * この武器を持って左クリックしたときの処理
     * @param shootarianPlayer player
     */
    public abstract void onLeftClick(ShootarianPlayer shootarianPlayer);

    /**
     * メイン武器として使えるかどうか
     * @return boolean
     */
    public boolean isMain() {return isMain;}



    private static final ShootarianSound EXPLOSION_SOUND = new ShootarianSound(Sound.ENTITY_GENERIC_EXPLODE, 1.2F, 1.0F);
    private static final ShootarianParticle EXPLOSION_PARTICLE1 = new NormalParticle(Particle.EXPLOSION_NORMAL, 1, 0, 0, 0, 0);
    private static final ShootarianParticle EXPLOSION_PARTICLE2 = new NormalParticle(Particle.EXPLOSION_HUGE, 1, 0, 0, 0, 0);
    
    /**
     * 爆発を作成する
     * @param shootarianPlayer 爆発を起こしたプレイヤー
     * @param ShootarianWeapon 爆発を起こした武器
     * @param center 爆発の中心
     * @param radius 爆発の半径
     */
    public static void createExplosion(ShootarianPlayer shootarianPlayer, ShootarianWeapon ShootarianWeapon, Location center, double radius, double velocityRate){
        ShootarianTeam shootarianTeam = shootarianPlayer.getShootarianTeam();
        if(shootarianTeam == null) return;
    
        //エフェクト
        shootarianTeam.getMatch().spawnParticle(EXPLOSION_PARTICLE1, center);
        shootarianTeam.getMatch().spawnParticle(EXPLOSION_PARTICLE2, center);
        
        //音
        shootarianTeam.getMatch().playSound(EXPLOSION_SOUND, center);
        
        //ダメージ
        for(ShootarianPlayer matchPlayer : shootarianTeam.getMatch().getPlayers()){
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
            
            if(matchPlayer.getShootarianTeam() == shootarianTeam){
                
                matchPlayer.setVelocity(velocity);
                
                continue;
            }
            
            double damage = ((radius - distance) / radius) * ShootarianWeapon.getDamage();
            matchPlayer.giveDamage((float) damage, shootarianPlayer, velocity, ShootarianWeapon);
        }
        
        //破壊
        SphereBlocks sphereBlocks = new SphereBlocks(radius, center);
        Set<Block> blocks = sphereBlocks.getBlocks();
        
        TaskHandler.supplyWorldSync(center.getWorld(), () -> {
            //ワールドスレッドでブロックのMaterialを取得
            Map<Block, Material> materialMap = new HashMap<>();
            for(Block block : blocks){
                materialMap.put(block, block.getType());
            }
            return materialMap;
            
        }).thenAccept(blockMaterialMap -> {
            
            for(Block block : blockMaterialMap.keySet()){
                double distance = Math.sqrt(LocationUtil.distanceSquaredSafeDifferentWorld(center, block.getLocation()));
                if(distance * Math.random() > radius / 1.5) continue;
        
                boolean isInStructure = false;
                boolean isBreak = false;
                for(MapStructureData mapStructureData : shootarianTeam.getMatch().getMapStructureData()){
            
                    if(!mapStructureData.getBoundingBox().isInBox(block.getLocation().toVector())) continue;
                    if(blockMaterialMap.get(block).toString().endsWith("AIR")) continue;
                    mapStructureData.giveDamage(1);
            
                    if(mapStructureData.isDead()) continue;
            
                    shootarianTeam.getMatch().getBlockUpdater().remove(block, shootarianPlayer);
            
                    isInStructure = true;
                    isBreak = true;
                    break;
                }
        
        
                if(!isInStructure){
                    Set<Material> breakableMaterials = shootarianTeam.getMatch().getShootarianMap().getBreakableBlockTypes();
                    if(breakableMaterials.contains(blockMaterialMap.get(block))){
                        shootarianTeam.getMatch().getBlockUpdater().remove(block, shootarianPlayer);
                        isBreak = true;
                    }
                }
                
                
                if(isBreak){
                    //FallingBlock
                    if(new Random().nextInt(4) == 0) {
                        AsyncFallingBlock asyncFallingBlock = new AsyncFallingBlock(shootarianTeam.getMatch(), block.getLocation(), block.getBlockData());
        
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
                }
            }
            
        });
    }
}
