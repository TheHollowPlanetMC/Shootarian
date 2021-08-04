package be4rjp.shellcase.weapon.gun;

import be4rjp.shellcase.player.ShellCasePlayer;
import be4rjp.shellcase.player.passive.PassiveInfluence;
import be4rjp.shellcase.weapon.WeaponManager;
import be4rjp.shellcase.weapon.WeaponStatusData;
import be4rjp.shellcase.weapon.attachment.Sight;
import be4rjp.shellcase.weapon.reload.ReloadRunnable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class GunStatusData extends WeaponStatusData {
    
    private final GunWeapon gunWeapon;
    private final ShellCasePlayer shellCasePlayer;
    
    private Sight sight;
    
    private PassiveInfluence passiveInfluence = new PassiveInfluence();
    
    private int maxBullets = 20;
    
    //弾数系の動作の同期用インスタンス
    private final Object BULLETS_LOCK = new Object();
    
    public GunStatusData(GunWeapon gunWeapon, ShellCasePlayer shellCasePlayer){
        super(gunWeapon, shellCasePlayer);
        this.gunWeapon = gunWeapon;
        this.shellCasePlayer = shellCasePlayer;
        
        this.maxBullets = gunWeapon.getDefaultBullets();
        this.bullets = gunWeapon.getDefaultBullets();
        
        sight = gunWeapon.getDefaultSight();
    }

    public void createPassiveInfluence(){this.passiveInfluence.createPassiveInfluence(this);}
    
    public Sight getSight() {return sight;}
    
    public void setSight(Sight sight) {this.sight = sight;}
    
    public GunWeapon getGunWeapon() {return gunWeapon;}
    
    public int getMaxBullets() {return maxBullets;}
    
    public void setMaxBullets(int maxBullets) {this.maxBullets = maxBullets;}

    public void reload(){
        if(this.isReloading) return;
        if(shellCasePlayer == null) return;
        this.isReloading = true;
        new ReloadRunnable(shellCasePlayer, this).start();
    }

    @Override
    public void updateDisplayName(ShellCasePlayer shellCasePlayer){
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
