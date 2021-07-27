package be4rjp.shellcase.weapon;

import be4rjp.shellcase.player.ShellCasePlayer;
import be4rjp.shellcase.weapon.main.GunWeapon;
import org.bukkit.entity.Player;

public class WeaponClass {

    private GunStatusData mainWeapon = null;
    
    
    public void setMainWeapon(GunStatusData mainWeapon) {this.mainWeapon = mainWeapon;}
    
    public GunStatusData getMainWeapon() {return mainWeapon;}
    
    public GunStatusData getGunStatusData(GunWeapon gunWeapon){
        if(mainWeapon == null) return null;
        if(mainWeapon.getGunWeapon() == gunWeapon) return mainWeapon;
        
        return null;
    }
    
    public void setItem(ShellCasePlayer shellCasePlayer){
        Player player = shellCasePlayer.getBukkitPlayer();
        if(player == null) return;
        
        if(mainWeapon != null){
            player.getInventory().setItem(0, mainWeapon.getItemStack(shellCasePlayer.getLang()));
        }
    }
}
