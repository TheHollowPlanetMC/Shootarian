package be4rjp.shootarian.weapon;

import be4rjp.shootarian.language.Lang;
import be4rjp.shootarian.language.MessageManager;
import be4rjp.shootarian.player.ShootarianPlayer;
import be4rjp.shootarian.weapon.gadget.GadgetStatusData;
import be4rjp.shootarian.weapon.gadget.GadgetWeapon;
import be4rjp.shootarian.weapon.gun.GunStatusData;
import be4rjp.shootarian.weapon.gun.GunWeapon;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public abstract class WeaponStatusData {

    public static WeaponStatusData createWeaponStatusData(ShootarianWeapon shootarianWeapon, ShootarianPlayer shootarianPlayer){
        if(shootarianWeapon instanceof GunWeapon){
            return new GunStatusData((GunWeapon) shootarianWeapon, shootarianPlayer);
        }

        if(shootarianWeapon instanceof GadgetWeapon){
            return new GadgetStatusData((GadgetWeapon) shootarianWeapon, shootarianPlayer);
        }

        return null;
    }


    protected final ShootarianWeapon shootarianWeapon;
    protected final ShootarianPlayer shootarianPlayer;

    protected int bullets = 20;
    protected boolean isReloading = false;
    protected long coolTime = 0;
    
    protected int saveIndexNumber;

    //弾数系の動作の同期用インスタンス
    protected final Object BULLETS_LOCK = new Object();

    public WeaponStatusData(ShootarianWeapon shootarianWeapon, ShootarianPlayer shootarianPlayer){
        this.shootarianWeapon = shootarianWeapon;
        this.shootarianPlayer = shootarianPlayer;

        this.bullets = shootarianWeapon.getDefaultBullets();
    }

    public int getBullets() {synchronized (BULLETS_LOCK){return bullets;}}

    public boolean isReloading() {return isReloading;}

    public void setReloading(boolean reloading) {isReloading = reloading;}

    public void setBullets(int bullets) {synchronized (BULLETS_LOCK){this.bullets = bullets;}}

    public boolean consumeBullets(int bullets){
        synchronized (BULLETS_LOCK){
            if(this.getBullets() < bullets){
                this.setBullets(0);
                return false;
            }else{
                this.setBullets(this.getBullets() - bullets);
                return true;
            }
        }
    }

    public ItemStack getItemStack(Lang lang){
        ItemStack itemStack = this.getItemStackFlexible(lang);
        ItemMeta itemMeta = itemStack.getItemMeta();
        String displayName = itemMeta.getDisplayName();
        displayName = displayName + "§r < " + this.getBullets() + " >";
        itemMeta.setDisplayName(this.isReloading ? MessageManager.getText(lang, "gun-reload") : displayName);
        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

    public abstract void updateDisplayName(ShootarianPlayer shootarianPlayer);
    
    public abstract ItemStack getItemStackFlexible(Lang lang);
    
    public boolean isCoolTime(){return System.currentTimeMillis() < coolTime;}
    
    public void setCoolTime(long tick){coolTime = System.currentTimeMillis() + (tick * 50L);}
    
    public void reset(){
        bullets = shootarianWeapon.getDefaultBullets();
    }
}
