package be4rjp.shellcase.weapon.gadget;

import be4rjp.shellcase.entity.FlagGrenadeEntity;
import be4rjp.shellcase.language.Lang;
import be4rjp.shellcase.language.MessageManager;
import be4rjp.shellcase.match.team.ShellCaseTeam;
import be4rjp.shellcase.player.ShellCasePlayer;
import org.bukkit.Material;

public class FlagGrenade extends GadgetWeapon{
    public FlagGrenade() {
        super(Gadget.FLAG_GRENADE);

        this.material = Material.TNT;
        this.damage = 25.0F;
        for(Lang lang : Lang.values()){
            this.displayName.put(lang, MessageManager.getText(lang, "gadget-flag-grenade"));
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

        FlagGrenadeEntity flagGrenadeEntity = new FlagGrenadeEntity(shellCaseTeam.getMatch(), shellCasePlayer.getEyeLocation(), shellCasePlayer, this);
        flagGrenadeEntity.setVelocity(shellCasePlayer.getEyeLocation().getDirection());
        flagGrenadeEntity.spawn();
    }

    @Override
    public void onLeftClick(ShellCasePlayer shellCasePlayer) {
        this.onRightClick(shellCasePlayer);
    }
}
