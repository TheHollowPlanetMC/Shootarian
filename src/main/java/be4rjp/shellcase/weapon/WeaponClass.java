package be4rjp.shellcase.weapon;

import be4rjp.shellcase.player.ShellCasePlayer;
import be4rjp.shellcase.weapon.gun.GunStatusData;
import be4rjp.shellcase.weapon.gun.GunWeapon;
import org.bukkit.entity.Player;

public class WeaponClass {

    private GunStatusData mainWeapon = null;
    
    
    public void setMainWeapon(GunStatusData mainWeapon) {this.mainWeapon = mainWeapon;}
    
    public GunStatusData getMainWeapon() {return mainWeapon;}
    
    private GunStatusData getGunStatusData(GunWeapon gunWeapon){
        if(mainWeapon == null) return null;
        if(mainWeapon.getGunWeapon() == gunWeapon) return mainWeapon;
        
        return null;
    }

    public WeaponStatusData getWeaponStatusData(ShellCaseWeapon shellCaseWeapon){
        if(mainWeapon == null) return null;
        if(mainWeapon.getGunWeapon() == shellCaseWeapon) return mainWeapon;

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
