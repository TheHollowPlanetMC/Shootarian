package be4rjp.shellcase.listener;

import be4rjp.shellcase.ShellCase;
import be4rjp.shellcase.match.Match;
import be4rjp.shellcase.match.team.ShellCaseTeam;
import be4rjp.shellcase.player.ShellCasePlayer;
import be4rjp.shellcase.weapon.GunStatusData;
import be4rjp.shellcase.weapon.ShellCaseWeapon;
import be4rjp.shellcase.weapon.WeaponManager;
import be4rjp.shellcase.weapon.main.GunWeapon;
import be4rjp.shellcase.weapon.reload.ReloadRunnable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerItemClickListener implements Listener {
    @EventHandler
    public void onClickItem(PlayerInteractEvent event){
        Player player = event.getPlayer();
        if(!event.hasItem()) return;
        if(event.getHand() != EquipmentSlot.HAND && event.getHand() != EquipmentSlot.OFF_HAND) return;
    
        new BukkitRunnable() {
            @Override
            public void run() {
                ShellCaseWeapon shellCaseWeapon = WeaponManager.getShellCaseWeaponByItem(event.getItem());
                ShellCasePlayer shellCasePlayer = ShellCasePlayer.getShellCasePlayer(player);
                if(shellCaseWeapon == null) return;
    
                ShellCaseTeam ShellCaseTeam = shellCasePlayer.getShellCaseTeam();
                if(ShellCaseTeam == null) return;
                Match.MatchStatus matchStatus = ShellCaseTeam.getMatch().getMatchStatus();
                //if((matchStatus == Match.MatchStatus.FINISHED || matchStatus == Match.MatchStatus.WAITING) && !shellCaseWeapon.getId().endsWith("nw")) return;
    
                Action action = event.getAction();
                if(action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK)
                    shellCaseWeapon.onLeftClick(shellCasePlayer);
                if(action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK)
                    shellCaseWeapon.onRightClick(shellCasePlayer);
            }
        }.runTaskAsynchronously(ShellCase.getPlugin());
    }
    
    
    @EventHandler
    public void onThrowItem(PlayerDropItemEvent event){
        event.setCancelled(true);
        Player player = event.getPlayer();
    
        new BukkitRunnable() {
            @Override
            public void run() {
                GunWeapon gunWeapon = WeaponManager.getGunWeaponByItem(event.getItemDrop().getItemStack());
                if(gunWeapon == null) return;

                ShellCasePlayer shellCasePlayer = ShellCasePlayer.getShellCasePlayer(player);
                ShellCaseTeam ShellCaseTeam = shellCasePlayer.getShellCaseTeam();
                if(ShellCaseTeam == null) return;
                Match.MatchStatus matchStatus = ShellCaseTeam.getMatch().getMatchStatus();
                //if((matchStatus == Match.MatchStatus.FINISHED || matchStatus == Match.MatchStatus.WAITING) && !shellCaseWeapon.getId().endsWith("nw")) return;

                GunStatusData gunStatusData = shellCasePlayer.getWeaponClass().getGunStatusData(gunWeapon);
                if(gunStatusData == null) return;

                gunStatusData.reload();
            }
        }.runTaskAsynchronously(ShellCase.getPlugin());
    }
}
