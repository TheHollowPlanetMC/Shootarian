package be4rjp.shootarian.listener;

import net.citizensnpcs.api.event.NPCDespawnEvent;
import net.citizensnpcs.api.event.NPCTeleportEvent;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class NPCTeleportListener implements Listener {
    
    public static final Set<NPC> scheduledTeleport = ConcurrentHashMap.newKeySet();
    
    @EventHandler
    public void onTeleport(NPCTeleportEvent event){
        NPC npc = event.getNPC();
        
        boolean contains = scheduledTeleport.remove(npc);
        if(!contains) event.setCancelled(true);
    }
    
    @EventHandler
    public void onRemove(NPCDespawnEvent event){
        NPC npc = event.getNPC();
        scheduledTeleport.remove(npc);
    }
    
}
