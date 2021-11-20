package be4rjp.shootarian.weapon;

import be4rjp.shootarian.data.AchievementData;
import be4rjp.shootarian.player.ShootarianPlayer;
import be4rjp.shootarian.weapon.gadget.Gadget;
import be4rjp.shootarian.weapon.gadget.GadgetStatusData;
import be4rjp.shootarian.weapon.gun.GunStatusData;
import be4rjp.shootarian.weapon.gun.GunWeapon;
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

    
    public WeaponStatusData getWeaponStatusData(ShootarianWeapon shootarianWeapon){
        if(mainWeapon != null){
            if(mainWeapon.getGunWeapon() == shootarianWeapon) return mainWeapon;
        }
        
        if(subWeapon != null){
            if(subWeapon.getGunWeapon() == shootarianWeapon) return subWeapon;
        }
        
        if(mainGadget != null){
            if(mainGadget.getGadgetWeapon() == shootarianWeapon) return mainGadget;
        }
        
        if(subGadget != null){
            if(subGadget.getGadgetWeapon() == shootarianWeapon) return subGadget;
        }

        return null;
    }
    
    
    public void loadGunStatusData(ShootarianPlayer shootarianPlayer){
        if(mainWeapon != null){
            mainWeapon = shootarianPlayer.getWeaponPossessionData().getGunStatusData(mainWeapon.getGunWeapon().getSaveNumber(), shootarianPlayer);
        }
    
        if(subWeapon != null){
            subWeapon = shootarianPlayer.getWeaponPossessionData().getGunStatusData(subWeapon.getGunWeapon().getSaveNumber(), shootarianPlayer);
        }
    }
    
    
    public void setItem(ShootarianPlayer shootarianPlayer){
        Player player = shootarianPlayer.getBukkitPlayer();
        if(player == null) return;
        
        if(mainWeapon != null){
            player.getInventory().setItem(0, mainWeapon.getItemStack(shootarianPlayer.getLang()));
        }
        
        if(subWeapon != null){
            player.getInventory().setItem(1, subWeapon.getItemStack(shootarianPlayer.getLang()));
        }
    
        if(mainGadget != null){
            player.getInventory().setItem(2, mainGadget.getItemStack(shootarianPlayer.getLang()));
        }
        
        if(subGadget != null){
            player.getInventory().setItem(3, subGadget.getItemStack(shootarianPlayer.getLang()));
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
        mainWeapon = achievementData.getWeaponPossessionData().getGunStatusData((int) (id & 0xFFF), achievementData.getShootarianPlayer());
        subWeapon = achievementData.getWeaponPossessionData().getGunStatusData((int) (id >> 12 & 0xFFF), achievementData.getShootarianPlayer());
        mainGadget = new GadgetStatusData(Gadget.getGadgetBySaveNumber((int) (id >> 24 & 0xFFF)).getInstance(), achievementData.getShootarianPlayer());
        subGadget = new GadgetStatusData(Gadget.getGadgetBySaveNumber((int) (id >> 36 & 0xFFF)).getInstance(), achievementData.getShootarianPlayer());
    }
}
