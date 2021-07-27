package be4rjp.shellcase.listener;

import be4rjp.shellcase.ShellCase;
import be4rjp.shellcase.ShellCaseConfig;
import be4rjp.shellcase.match.team.ShellCaseTeam;
import be4rjp.shellcase.player.ShellCasePlayer;
import be4rjp.shellcase.player.death.DeathType;
import be4rjp.shellcase.player.death.PlayerDeathManager;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class CancelListener implements Listener {
    
    @EventHandler
    public void onDamageByFall(EntityDamageEvent event) {
        if(event.getEntityType() != EntityType.PLAYER) return;
        
        event.setCancelled(true);
    
        if(event.getCause() == EntityDamageEvent.DamageCause.VOID){
            Player player = (Player) event.getEntity();
    
            new BukkitRunnable() {
                @Override
                public void run() {
                    ShellCasePlayer shellCasePlayer = ShellCasePlayer.getShellCasePlayer(player);
                    ShellCaseTeam ShellCaseTeam = shellCasePlayer.getShellCaseTeam();
                    
                    boolean lobbyPlayer = false;
                    if(ShellCaseTeam == null) lobbyPlayer = true;
                    if(ShellCaseTeam == ShellCase.getLobbyTeam()) lobbyPlayer = true;
                    
                    if(lobbyPlayer){
                        shellCasePlayer.teleport(ShellCaseConfig.getLobbyLocation());
                        return;
                    }
    
                    if(!shellCasePlayer.isDeath()) PlayerDeathManager.death(shellCasePlayer, shellCasePlayer, null, DeathType.FELL_OUT_OF_THE_WORLD);
                }
            }.runTaskAsynchronously(ShellCase.getPlugin());
        }
    }
    
    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event){
        event.setCancelled(true);
    }
}
