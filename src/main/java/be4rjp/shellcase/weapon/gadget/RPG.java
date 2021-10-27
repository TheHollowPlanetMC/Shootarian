package be4rjp.shellcase.weapon.gadget;

import be4rjp.shellcase.entity.AsyncRPGBulletEntity;
import be4rjp.shellcase.language.Lang;
import be4rjp.shellcase.language.MessageManager;
import be4rjp.shellcase.match.team.ShellCaseTeam;
import be4rjp.shellcase.player.ShellCasePlayer;
import org.bukkit.Material;

public class RPG extends GadgetWeapon{
    
    public RPG(Gadget gadget) {
        super(gadget);
    
        super.material = Material.STICK;
        super.damage = 25.0F;
        for(Lang lang : Lang.values()){
            this.displayName.put(lang, MessageManager.getText(lang, "gadget-rpg"));
        }
    }
    
    @Override
    public void onRightClick(ShellCasePlayer shellCasePlayer) {
        GadgetStatusData gadgetStatusData = (GadgetStatusData) shellCasePlayer.getWeaponStatusData(this);
        if(gadgetStatusData == null) return;
    
        ShellCaseTeam shellCaseTeam = shellCasePlayer.getShellCaseTeam();
        if(shellCaseTeam == null) return;
    
        if(!gadgetStatusData.consumeBullets(1)){
            return;
        }
        gadgetStatusData.updateDisplayName(shellCasePlayer);
        
        shellCasePlayer.setVelocity(shellCasePlayer.getEyeLocation().getDirection().multiply(-0.5));
    
        AsyncRPGBulletEntity asyncRPGBulletEntity = new AsyncRPGBulletEntity(shellCaseTeam, shellCasePlayer.getEyeLocation(), gadgetStatusData.getGadgetWeapon());
        asyncRPGBulletEntity.shootInitialize(shellCasePlayer, shellCasePlayer.getEyeLocation().getDirection().multiply(1.9), 3);
        asyncRPGBulletEntity.spawn();
    }
    
    @Override
    public void onLeftClick(ShellCasePlayer shellCasePlayer) {
        onRightClick(shellCasePlayer);
    }
}
