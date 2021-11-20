package be4rjp.shootarian.player.passive;

import be4rjp.shootarian.player.ShootarianPlayer;
import be4rjp.shootarian.weapon.WeaponManager;
import be4rjp.shootarian.weapon.gun.GunStatusData;
import be4rjp.shootarian.weapon.gun.GunWeapon;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Set;

public class PlayerPassiveInfluence{
    
    private final ShootarianPlayer shootarianPlayer;
    
    private Set<PassiveInfluence> playerPlusPassiveInfluence = new HashSet<>();
    
    private Set<PassiveInfluence> playerRatePassiveInfluence = new HashSet<>();
    
    
    public PlayerPassiveInfluence(ShootarianPlayer shootarianPlayer){
        this.shootarianPlayer = shootarianPlayer;
    }
    
    
    public synchronized void onChangeSlot() {
        
        Set<PassiveInfluence> plusPassiveInfluences = new HashSet<>();
        Set<PassiveInfluence> ratePassiveInfluences = new HashSet<>();
    
        Player player = shootarianPlayer.getBukkitPlayer();
        if(player == null) return;
        
        ItemStack mainHandItem = player.getInventory().getItemInMainHand();
        GunWeapon gunWeapon = WeaponManager.getGunWeaponByItem(mainHandItem);
        if(gunWeapon != null){
            GunStatusData gunStatusData = (GunStatusData) shootarianPlayer.getWeaponStatusData(gunWeapon);
            
            for(PassiveInfluence passiveInfluence : gunStatusData.getAllPassiveInfluences()){
                if(passiveInfluence instanceof PassivePlusInfluence) {
                    plusPassiveInfluences.add(passiveInfluence);
                }else{
                    ratePassiveInfluences.add(passiveInfluence);
                }
            }
        }
    
        this.playerPlusPassiveInfluence = plusPassiveInfluences;
        this.playerRatePassiveInfluence = ratePassiveInfluences;
        
        this.shootarianPlayer.setWalkSpeed(0.2F);
    }
    
    
    /**
     * 装備から計算したパッシブ効果を取得し、適応させた値を返します
     * @param passive 取得するパッシブ効果の種類
     * @param raw 元の数値
     * @return double
     */
    public synchronized double setInfluence(Passive passive, double raw){
        //先に加算分を計算する
        for(PassiveInfluence passiveInfluence : this.playerPlusPassiveInfluence){
            if(passiveInfluence.getPassive() == passive){
                raw = passiveInfluence.setInfluence(raw);
            }
        }
        //次に乗算分を計算する
        for(PassiveInfluence passiveInfluence : this.playerRatePassiveInfluence){
            if(passiveInfluence.getPassive() == passive){
                raw = passiveInfluence.setInfluence(raw);
            }
        }
        
        return raw;
    }
}
