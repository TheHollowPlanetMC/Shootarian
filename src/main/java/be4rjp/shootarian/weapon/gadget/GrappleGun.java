package be4rjp.shootarian.weapon.gadget;

import be4rjp.shootarian.Shootarian;
import be4rjp.shootarian.language.Lang;
import be4rjp.shootarian.language.MessageManager;
import be4rjp.shootarian.match.team.ShootarianTeam;
import be4rjp.shootarian.player.ShootarianPlayer;
import be4rjp.shootarian.weapon.gadget.runnable.GrappleBulletRunnable;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class GrappleGun extends GadgetWeapon{
    
    
    public GrappleGun(Gadget gadget) {
        super(gadget);
    
        super.material = Material.GOLDEN_HOE;
        for(Lang lang : Lang.values()){
            this.displayName.put(lang, MessageManager.getText(lang, "gadget-grapple-gun"));
        }
    }
    
    @Override
    public synchronized void onRightClick(ShootarianPlayer shootarianPlayer) {
        GadgetStatusData gadgetStatusData = (GadgetStatusData) shootarianPlayer.getWeaponStatusData(this);
        if(gadgetStatusData == null) return;
        if(gadgetStatusData.isCoolTime()) return;
    
        ShootarianTeam shootarianTeam = shootarianPlayer.getShootarianTeam();
        if(shootarianTeam == null) return;
    
        Player player = shootarianPlayer.getBukkitPlayer();
        if(player == null) return;
        
        if(!gadgetStatusData.consumeBullets(1)){
            return;
        }
        gadgetStatusData.updateDisplayName(shootarianPlayer);
        
        gadgetStatusData.setCoolTime(200);
    
        new GrappleBulletRunnable(shootarianPlayer, player, gadgetStatusData).runTaskTimerAsynchronously(Shootarian.getPlugin(), 0, 1);
    }
    
    @Override
    public synchronized void onLeftClick(ShootarianPlayer shootarianPlayer) {
    
    }
}
