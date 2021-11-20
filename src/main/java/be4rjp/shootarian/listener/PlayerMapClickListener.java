package be4rjp.shootarian.listener;

import be4rjp.shootarian.map.ConquestPlayerClickableGUIRenderer;
import be4rjp.shootarian.map.PlayerGUIRenderer;
import be4rjp.shootarian.player.ShootarianPlayer;
import be4rjp.shootarian.util.TaskHandler;
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
            ShootarianPlayer shootarianPlayer = ShootarianPlayer.getShootarianPlayer(player);
    
            PlayerGUIRenderer guiRenderer = shootarianPlayer.getPlayerGUIRenderer();
            if(guiRenderer == null) return;
            if(guiRenderer instanceof ConquestPlayerClickableGUIRenderer){
                ((ConquestPlayerClickableGUIRenderer) guiRenderer).onClick();
            }
        });
    }
}
