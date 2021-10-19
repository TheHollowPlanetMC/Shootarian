package be4rjp.shellcase.weapon;

import be4rjp.shellcase.language.Lang;
import be4rjp.shellcase.language.MessageManager;
import be4rjp.shellcase.player.ShellCasePlayer;
import be4rjp.shellcase.weapon.gadget.GadgetStatusData;
import be4rjp.shellcase.weapon.gadget.GadgetWeapon;
import be4rjp.shellcase.weapon.gun.GunStatusData;
import be4rjp.shellcase.weapon.gun.GunWeapon;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public abstract class WeaponStatusData {

    public static WeaponStatusData createWeaponStatusData(ShellCaseWeapon shellCaseWeapon, ShellCasePlayer shellCasePlayer){
        if(shellCaseWeapon instanceof GunWeapon){
            return new GunStatusData((GunWeapon) shellCaseWeapon, shellCasePlayer);
        }

        if(shellCaseWeapon instanceof GadgetWeapon){
            return new GadgetStatusData((GadgetWeapon) shellCaseWeapon, shellCasePlayer);
        }

        return null;
    }


    protected final ShellCaseWeapon shellCaseWeapon;
    protected final ShellCasePlayer shellCasePlayer;

    protected int bullets = 20;
    protected boolean isReloading = false;
    protected long coolTime = 0;
    
    protected int saveIndexNumber;

    //弾数系の動作の同期用インスタンス
    protected final Object BULLETS_LOCK = new Object();

    public WeaponStatusData(ShellCaseWeapon shellCaseWeapon, ShellCasePlayer shellCasePlayer){
        this.shellCaseWeapon = shellCaseWeapon;
        this.shellCasePlayer = shellCasePlayer;

        this.bullets = shellCaseWeapon.getDefaultBullets();
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

    public abstract void updateDisplayName(ShellCasePlayer shellCasePlayer);
    
    public abstract ItemStack getItemStackFlexible(Lang lang);
    
    public boolean isCoolTime(){return System.currentTimeMillis() < coolTime;}
    
    public void setCoolTime(long tick){coolTime = System.currentTimeMillis() + (tick * 50L);}
    
    public void reset(){
        bullets = shellCaseWeapon.getDefaultBullets();
    }
}
