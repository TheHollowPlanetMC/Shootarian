package be4rjp.shootarian.listener;

import be4rjp.shootarian.player.ShootarianPlayer;
import be4rjp.shootarian.player.passive.PlayerPassiveInfluence;
import be4rjp.shootarian.util.TaskHandler;
import be4rjp.shootarian.weapon.WeaponManager;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;

public class PlayerSlotChangeListener implements Listener {
    
    @EventHandler
    public void onSlotChange(PlayerItemHeldEvent event){
        TaskHandler.runAsync(() -> {
            ShootarianPlayer shootarianPlayer = ShootarianPlayer.getShootarianPlayer(event.getPlayer());
            
            //マップを見ているかどうか
            shootarianPlayer.setViewingMap(event.getPlayer().getInventory().getItemInMainHand().getType() == Material.FILLED_MAP);
            
            //パッシブ計算
            PlayerPassiveInfluence playerPassiveInfluence = shootarianPlayer.getPlayerPassiveInfluence();
            playerPassiveInfluence.onChangeSlot();
            
            shootarianPlayer.setADS(false);
            WeaponManager.switchADS(shootarianPlayer, null, false);
        });
    }
}
