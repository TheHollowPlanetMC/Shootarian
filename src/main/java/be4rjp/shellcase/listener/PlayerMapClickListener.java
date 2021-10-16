package be4rjp.shellcase.listener;

import be4rjp.shellcase.map.ConquestPlayerClickableGUIRenderer;
import be4rjp.shellcase.map.PlayerGUIRenderer;
import be4rjp.shellcase.player.ShellCasePlayer;
import be4rjp.shellcase.util.TaskHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

public class PlayerMapClickListener implements Listener {
    
    @EventHandler
    public void onClickItem(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!event.hasItem()) return;
        if (event.getHand() != EquipmentSlot.HAND && event.getHand() != EquipmentSlot.OFF_HAND) return;
    
        TaskHandler.runAsync(() -> {
            ShellCasePlayer shellCasePlayer = ShellCasePlayer.getShellCasePlayer(player);
    
            PlayerGUIRenderer guiRenderer = shellCasePlayer.getPlayerGUIRenderer();
            if(guiRenderer == null) return;
            if(guiRenderer instanceof ConquestPlayerClickableGUIRenderer){
                ((ConquestPlayerClickableGUIRenderer) guiRenderer).onClick();
            }
        });
    }
}
