package be4rjp.shellcase.weapon.gadget;

import be4rjp.shellcase.ShellCase;
import be4rjp.shellcase.language.Lang;
import be4rjp.shellcase.language.MessageManager;
import be4rjp.shellcase.match.team.ShellCaseTeam;
import be4rjp.shellcase.player.ShellCasePlayer;
import be4rjp.shellcase.weapon.gadget.runnable.GrappleBulletRunnable;
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
    public synchronized void onRightClick(ShellCasePlayer shellCasePlayer) {
        GadgetStatusData gadgetStatusData = (GadgetStatusData) shellCasePlayer.getWeaponStatusData(this);
        if(gadgetStatusData == null) return;
        if(gadgetStatusData.isCoolTime()) return;
    
        ShellCaseTeam shellCaseTeam = shellCasePlayer.getShellCaseTeam();
        if(shellCaseTeam == null) return;
    
        Player player = shellCasePlayer.getBukkitPlayer();
        if(player == null) return;
        
        if(!gadgetStatusData.consumeBullets(1)){
            return;
        }
        gadgetStatusData.updateDisplayName(shellCasePlayer);
        
        gadgetStatusData.setCoolTime(200);
    
        new GrappleBulletRunnable(shellCasePlayer, player, gadgetStatusData).runTaskTimerAsynchronously(ShellCase.getPlugin(), 0, 1);
    }
    
    @Override
    public synchronized void onLeftClick(ShellCasePlayer shellCasePlayer) {
    
    }
}
