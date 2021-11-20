package be4rjp.shootarian.listener;

import be4rjp.shootarian.Shootarian;
import be4rjp.shootarian.ShootarianConfig;
import be4rjp.shootarian.match.team.ShootarianTeam;
import be4rjp.shootarian.player.ShootarianPlayer;
import be4rjp.shootarian.player.death.DeathType;
import be4rjp.shootarian.player.death.PlayerDeathManager;
import be4rjp.shootarian.util.TaskHandler;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;

public class CancelListener implements Listener {
    
    @EventHandler
    public void onDamageByFall(EntityDamageEvent event) {
        if(event.getEntityType() != EntityType.PLAYER) return;
        
        event.setCancelled(true);
    
        if(event.getCause() == EntityDamageEvent.DamageCause.VOID){
            Player player = (Player) event.getEntity();
    
            TaskHandler.runAsync(() -> {
                ShootarianPlayer shootarianPlayer = ShootarianPlayer.getShootarianPlayer(player);
                ShootarianTeam ShootarianTeam = shootarianPlayer.getShootarianTeam();
    
                boolean lobbyPlayer = false;
                if(ShootarianTeam == null) lobbyPlayer = true;
                if(ShootarianTeam == Shootarian.getLobbyTeam()) lobbyPlayer = true;
    
                if(lobbyPlayer){
                    shootarianPlayer.teleport(ShootarianConfig.getLobbyLocation());
                    return;
                }
    
                if(!shootarianPlayer.isDeath()) PlayerDeathManager.death(shootarianPlayer, shootarianPlayer, null, DeathType.FELL_OUT_OF_THE_WORLD);
            });
        }
    }
    
    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event){
        event.setCancelled(true);
    }
}
