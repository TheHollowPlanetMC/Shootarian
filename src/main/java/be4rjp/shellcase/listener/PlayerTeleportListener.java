package be4rjp.shellcase.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerTeleportListener implements Listener {
    
    public static final Set<Player> scheduledTeleport = ConcurrentHashMap.newKeySet();
    
    @EventHandler
    public void onTeleport(PlayerTeleportEvent event){
        Player player = event.getPlayer();
        
        boolean contains = scheduledTeleport.remove(player);
        if(!contains) event.setCancelled(true);
    }
    
}
