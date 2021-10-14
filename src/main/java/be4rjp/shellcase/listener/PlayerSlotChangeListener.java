package be4rjp.shellcase.listener;

import be4rjp.shellcase.player.ShellCasePlayer;
import be4rjp.shellcase.player.passive.PlayerPassiveInfluence;
import be4rjp.shellcase.util.TaskHandler;
import be4rjp.shellcase.weapon.WeaponManager;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;

public class PlayerSlotChangeListener implements Listener {
    
    @EventHandler
    public void onSlotChange(PlayerItemHeldEvent event){
        TaskHandler.runAsync(() -> {
            ShellCasePlayer shellCasePlayer = ShellCasePlayer.getShellCasePlayer(event.getPlayer());
            
            //マップを見ているかどうか
            shellCasePlayer.setViewingMap(event.getPlayer().getInventory().getItemInMainHand().getType() == Material.FILLED_MAP);
            
            //パッシブ計算
            PlayerPassiveInfluence playerPassiveInfluence = shellCasePlayer.getPlayerPassiveInfluence();
            playerPassiveInfluence.onChangeSlot();
            
            shellCasePlayer.setADS(false);
            WeaponManager.switchADS(shellCasePlayer, null, false);
        });
    }
}
