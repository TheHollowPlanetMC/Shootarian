package be4rjp.shellcase.weapon;

import be4rjp.shellcase.language.Lang;
import be4rjp.shellcase.language.MessageManager;
import be4rjp.shellcase.player.ShellCasePlayer;
import be4rjp.shellcase.player.passive.PassiveInfluence;
import be4rjp.shellcase.weapon.attachment.Sight;
import be4rjp.shellcase.weapon.main.GunWeapon;
import be4rjp.shellcase.weapon.reload.ReloadRunnable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class GunStatusData {
    
    private final GunWeapon gunWeapon;
    private final ShellCasePlayer shellCasePlayer;
    
    private Sight sight;
    
    private PassiveInfluence passiveInfluence = new PassiveInfluence();
    
    private int maxBullets = 20;
    private int bullets = 20;
    private boolean isReloading = false;
    
    //弾数系の動作の同期用インスタンス
    private final Object BULLETS_LOCK = new Object();
    
    public GunStatusData(GunWeapon gunWeapon, ShellCasePlayer shellCasePlayer){
        this.gunWeapon = gunWeapon;
        this.shellCasePlayer = shellCasePlayer;
        
        sight = gunWeapon.getDefaultSight();
    }
    
    public void createPassiveInfluence(){this.passiveInfluence.createPassiveInfluence(this);}
    
    public Sight getSight() {return sight;}
    
    public void setSight(Sight sight) {this.sight = sight;}
    
    public GunWeapon getGunWeapon() {return gunWeapon;}
    
    public int getMaxBullets() {return maxBullets;}
    
    public int getBullets() {synchronized (BULLETS_LOCK){return bullets;}}
    
    public boolean isReloading() {return isReloading;}
    
    public void setReloading(boolean reloading) {isReloading = reloading;}
    
    public void setBullets(int bullets) {synchronized (BULLETS_LOCK){this.bullets = bullets;}}
    
    public void setMaxBullets(int maxBullets) {this.maxBullets = maxBullets;}

    public void reload(){
        if(this.isReloading) return;
        if(shellCasePlayer == null) return;
        this.isReloading = true;
        new ReloadRunnable(shellCasePlayer, this).start();
    }
    
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
        ItemStack itemStack = gunWeapon.getItemStack(lang);
        ItemMeta itemMeta = itemStack.getItemMeta();
        String displayName = itemMeta.getDisplayName();
        displayName = displayName + " < " + this.getBullets() + " >";
        itemMeta.setDisplayName(this.isReloading ? MessageManager.getText(lang, "gun-reload") : displayName);
        itemStack.setItemMeta(itemMeta);
        
        return itemStack;
    }
    
    public void updateGunDisplayName(ShellCasePlayer shellCasePlayer){
        Player player = shellCasePlayer.getBukkitPlayer();
        if(player == null) return;
        
        for(int index = 0; index < 9; index++){
            ItemStack itemStack = player.getInventory().getItem(index);
            if(itemStack == null) continue;
            
            GunWeapon gunWeapon = WeaponManager.getGunWeaponByItem(itemStack);
            if(gunWeapon == null) continue;
            if(gunWeapon != this.gunWeapon) continue;
            
            player.getInventory().setItem(index, this.getItemStack(shellCasePlayer.getLang()));
            break;
        }
    }
}
