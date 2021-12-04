package be4rjp.shootarian.listener;

import be4rjp.shootarian.match.Match;
import be4rjp.shootarian.match.team.ShootarianTeam;
import be4rjp.shootarian.player.ShootarianPlayer;
import be4rjp.shootarian.util.TaskHandler;
import be4rjp.shootarian.weapon.gun.GunStatusData;
import be4rjp.shootarian.weapon.ShootarianWeapon;
import be4rjp.shootarian.weapon.WeaponManager;
import be4rjp.shootarian.weapon.gun.GunWeapon;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

public class PlayerItemClickListener implements Listener {
    @EventHandler
    public void onClickItem(PlayerInteractEvent event){
        Player player = event.getPlayer();
        if(!event.hasItem()) return;
        if(event.getHand() != EquipmentSlot.HAND && event.getHand() != EquipmentSlot.OFF_HAND) return;
    
        TaskHandler.runAsyncImmediately(() -> {
            ShootarianWeapon shootarianWeapon = WeaponManager.getShootarianWeaponByItem(event.getItem());
            ShootarianPlayer shootarianPlayer = ShootarianPlayer.getShootarianPlayer(player);
            if(shootarianWeapon == null) return;
    
            ShootarianTeam shootarianTeam = shootarianPlayer.getShootarianTeam();
            if(shootarianTeam == null) return;
            Match.MatchStatus matchStatus = shootarianTeam.getMatch().getMatchStatus();
            //if((matchStatus == Match.MatchStatus.FINISHED || matchStatus == Match.MatchStatus.WAITING) && !shootarianWeapon.getId().endsWith("nw")) return;
    
            Action action = event.getAction();
            if(action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK)
                shootarianWeapon.onLeftClick(shootarianPlayer);
            if(action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK)
                shootarianWeapon.onRightClick(shootarianPlayer);
        });
    }
    
    
    @EventHandler
    public void onThrowItem(PlayerDropItemEvent event){
        event.setCancelled(true);
        Player player = event.getPlayer();
    
        TaskHandler.runAsync(() -> {
            GunWeapon gunWeapon = WeaponManager.getGunWeaponByItem(event.getItemDrop().getItemStack());
            if(gunWeapon == null) return;
    
            ShootarianPlayer shootarianPlayer = ShootarianPlayer.getShootarianPlayer(player);
            ShootarianTeam ShootarianTeam = shootarianPlayer.getShootarianTeam();
            if(ShootarianTeam == null) return;
            Match.MatchStatus matchStatus = ShootarianTeam.getMatch().getMatchStatus();
            //if((matchStatus == Match.MatchStatus.FINISHED || matchStatus == Match.MatchStatus.WAITING) && !shootarianWeapon.getId().endsWith("nw")) return;
    
            GunStatusData gunStatusData = (GunStatusData) shootarianPlayer.getWeaponStatusData(gunWeapon);
            if(gunStatusData == null) return;
    
            gunStatusData.reload();
        });
    }
}
