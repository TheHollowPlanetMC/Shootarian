package be4rjp.shootarian.weapon.gadget;

import be4rjp.shootarian.entity.WorldSyncRPGBulletEntity;
import be4rjp.shootarian.language.Lang;
import be4rjp.shootarian.language.MessageManager;
import be4rjp.shootarian.match.team.ShootarianTeam;
import be4rjp.shootarian.player.ShootarianPlayer;
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
        
        shootarianPlayer.setVelocity(shootarianPlayer.getEyeLocation().getDirection().multiply(-0.5));
    
        WorldSyncRPGBulletEntity worldSyncRPGBulletEntity = new WorldSyncRPGBulletEntity(shootarianTeam, shootarianPlayer.getEyeLocation(), gadgetStatusData.getGadgetWeapon());
        worldSyncRPGBulletEntity.shootInitialize(shootarianPlayer, shootarianPlayer.getEyeLocation().getDirection().multiply(1.9), 3);
        worldSyncRPGBulletEntity.spawn();
        
        gadgetStatusData.setCoolTime(40);
    }
    
    @Override
    public void onLeftClick(ShootarianPlayer shootarianPlayer) {
        onRightClick(shootarianPlayer);
    }
}
