package be4rjp.shellcase.weapon;

import be4rjp.shellcase.data.AchievementData;
import be4rjp.shellcase.player.ShellCasePlayer;
import be4rjp.shellcase.weapon.gadget.Gadget;
import be4rjp.shellcase.weapon.gadget.GadgetStatusData;
import be4rjp.shellcase.weapon.gun.GunStatusData;
import be4rjp.shellcase.weapon.gun.GunWeapon;
import org.bukkit.entity.Player;

public class WeaponClass {

    private GunStatusData mainWeapon = null;
    
    private GunStatusData subWeapon = null;
    
    private GadgetStatusData mainGadget = null;
    
    private GadgetStatusData subGadget = null;
    
    
    public void setMainWeapon(GunStatusData mainWeapon) {this.mainWeapon = mainWeapon;}
    
    public void setSubWeapon(GunStatusData subWeapon) {this.subWeapon = subWeapon;}
    
    public void setMainGadget(GadgetStatusData mainGadget) {this.mainGadget = mainGadget;}
    
    public void setSubGadget(GadgetStatusData subGadget) {this.subGadget = subGadget;}
    
    public GadgetStatusData getMainGadget() {return mainGadget;}
    
    public GadgetStatusData getSubGadget() {return subGadget;}
    
    public GunStatusData getSubWeapon() {return subWeapon;}
    
    public GunStatusData getMainWeapon() {return mainWeapon;}
    
    private GunStatusData getGunStatusData(GunWeapon gunWeapon){
        if(mainWeapon == null) return null;
        if(mainWeapon.getGunWeapon() == gunWeapon) return mainWeapon;
        
        return null;
    }

    
    public WeaponStatusData getWeaponStatusData(ShellCaseWeapon shellCaseWeapon){
        if(mainWeapon != null){
            if(mainWeapon.getGunWeapon() == shellCaseWeapon) return mainWeapon;
        }
        
        if(subWeapon != null){
            if(subWeapon.getGunWeapon() == shellCaseWeapon) return subWeapon;
        }
        
        if(mainGadget != null){
            if(mainGadget.getGadgetWeapon() == shellCaseWeapon) return mainGadget;
        }
        
        if(subGadget != null){
            if(subGadget.getGadgetWeapon() == shellCaseWeapon) return subGadget;
        }

        return null;
    }
    
    
    public void loadGunStatusData(ShellCasePlayer shellCasePlayer){
        if(mainWeapon != null){
            mainWeapon = shellCasePlayer.getWeaponPossessionData().getGunStatusData(mainWeapon.getGunWeapon().getSaveNumber(), shellCasePlayer);
        }
    
        if(subWeapon != null){
            subWeapon = shellCasePlayer.getWeaponPossessionData().getGunStatusData(subWeapon.getGunWeapon().getSaveNumber(), shellCasePlayer);
        }
    }
    
    
    public void setItem(ShellCasePlayer shellCasePlayer){
        Player player = shellCasePlayer.getBukkitPlayer();
        if(player == null) return;
        
        if(mainWeapon != null){
            player.getInventory().setItem(0, mainWeapon.getItemStack(shellCasePlayer.getLang()));
        }
        
        if(subWeapon != null){
            player.getInventory().setItem(1, subWeapon.getItemStack(shellCasePlayer.getLang()));
        }
    
        if(mainGadget != null){
            player.getInventory().setItem(2, mainGadget.getItemStack(shellCasePlayer.getLang()));
        }
        
        if(subGadget != null){
            player.getInventory().setItem(3, subGadget.getItemStack(shellCasePlayer.getLang()));
        }
    }
    
    public void reset(){
        if(mainWeapon != null){
            mainWeapon.reset();
        }
    
        if(subWeapon != null){
            subWeapon.reset();
        }
    
        if(mainGadget != null){
            mainGadget.reset();
        }
    
        if(subGadget != null){
            subGadget.reset();
        }
    }
    
    
    public long getCombinedID(){
        long id = 0;
        
        id |= mainWeapon.getGunWeapon().getSaveNumber();
        id |= (long) subWeapon.getGunWeapon().getSaveNumber() << 12;
        id |= (long) mainGadget.getGadgetWeapon().getGadget().getSaveNumber() << 24;
        id |= (long) subGadget.getGadgetWeapon().getGadget().getSaveNumber() << 36;
        
        return id;
    }
    
    
    public void setByCombinedID(long id, AchievementData achievementData){
        mainWeapon = achievementData.getWeaponPossessionData().getGunStatusData((int) (id & 0xFFF), achievementData.getShellCasePlayer());
        subWeapon = achievementData.getWeaponPossessionData().getGunStatusData((int) (id >> 12 & 0xFFF), achievementData.getShellCasePlayer());
        mainGadget = new GadgetStatusData(Gadget.getGadgetBySaveNumber((int) (id >> 24 & 0xFFF)).getInstance(), achievementData.getShellCasePlayer());
        subGadget = new GadgetStatusData(Gadget.getGadgetBySaveNumber((int) (id >> 36 & 0xFFF)).getInstance(), achievementData.getShellCasePlayer());
    }
}
