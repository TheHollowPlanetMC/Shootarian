package be4rjp.shootarian.entity;

import be4rjp.shootarian.match.Match;
import be4rjp.shootarian.player.ShootarianPlayer;
import be4rjp.shootarian.weapon.ShootarianWeapon;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.UUID;

public class FlagGrenadeEntity extends WorldSyncDropItem {

    private final ShootarianPlayer shootarianPlayer;
    private final ShootarianWeapon shootarianWeapon;

    private double explodeRadius = 5.0;

    public FlagGrenadeEntity(Match match, Location location, ShootarianPlayer shootarianPlayer, ShootarianWeapon shootarianWeapon) {
        super(match, location, new ItemStack(Material.TNT));

        this.shootarianPlayer = shootarianPlayer;
        this.shootarianWeapon = shootarianWeapon;
        
        ItemStack itemStack = new ItemStack(Material.TNT);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(UUID.randomUUID().toString());
        itemStack.setItemMeta(itemMeta);
        this.setItemStack(itemStack);
    }

    @Override
    public void tick(){
        if(tick == 100){
            this.remove();
        }

        super.tick();
    }

    @Override
    public void remove() {
        super.remove();

        ShootarianWeapon.createExplosion(shootarianPlayer, shootarianWeapon, this.getLocation(), explodeRadius, 0.4);
    }

    /**
     * 爆発の半径を設定する。
     * @param explodeRadius double 半径
     */
    public void setExplodeRadius(double explodeRadius) {this.explodeRadius = explodeRadius;}
}
