package be4rjp.shootarian.weapon.gadget;

import be4rjp.shootarian.entity.FlagGrenadeEntity;
import be4rjp.shootarian.language.Lang;
import be4rjp.shootarian.language.MessageManager;
import be4rjp.shootarian.match.team.ShootarianTeam;
import be4rjp.shootarian.player.ShootarianPlayer;
import org.bukkit.Material;

public class FlagGrenade extends GadgetWeapon{
    public FlagGrenade(Gadget gadget) {
        super(gadget);

        this.material = Material.TNT;
        this.damage = 25.0F;
        for(Lang lang : Lang.values()){
            this.displayName.put(lang, MessageManager.getText(lang, "gadget-flag-grenade"));
        }
    }

    @Override
    public void onRightClick(ShootarianPlayer shootarianPlayer) {
        GadgetStatusData gadgetStatusData = (GadgetStatusData) shootarianPlayer.getWeaponStatusData(this);
        if(gadgetStatusData == null) return;
        if(gadgetStatusData.isCoolTime()) return;

        ShootarianTeam shootarianTeam = shootarianPlayer.getShootarianTeam();
        if(shootarianTeam == null) return;

        if(!gadgetStatusData.consumeBullets(1)){
            return;
        }
        gadgetStatusData.updateDisplayName(shootarianPlayer);

        FlagGrenadeEntity flagGrenadeEntity = new FlagGrenadeEntity(shootarianTeam.getMatch(), shootarianPlayer.getEyeLocation(), shootarianPlayer, this);
        flagGrenadeEntity.setVelocity(shootarianPlayer.getEyeLocation().getDirection());
        flagGrenadeEntity.spawn();
        
        gadgetStatusData.setCoolTime(20);
    }

    @Override
    public void onLeftClick(ShootarianPlayer shootarianPlayer) {
        this.onRightClick(shootarianPlayer);
    }
}
