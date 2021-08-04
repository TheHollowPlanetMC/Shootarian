package be4rjp.shellcase.entity;

import be4rjp.shellcase.match.Match;
import be4rjp.shellcase.player.ShellCasePlayer;
import be4rjp.shellcase.weapon.ShellCaseWeapon;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class FlagGrenadeEntity extends AsyncDropItem{

    private final ShellCasePlayer shellCasePlayer;
    private final ShellCaseWeapon shellCaseWeapon;

    private double explodeRadius = 5.0;

    public FlagGrenadeEntity(Match match, Location location, ShellCasePlayer shellCasePlayer, ShellCaseWeapon shellCaseWeapon) {
        super(match, location, new ItemStack(Material.TNT));

        this.shellCasePlayer = shellCasePlayer;
        this.shellCaseWeapon = shellCaseWeapon;
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

        ShellCaseWeapon.createExplosion(shellCasePlayer, shellCaseWeapon, this.getLocation(), explodeRadius);
    }

    /**
     * 爆発の半径を設定する。
     * @param explodeRadius double 半径
     */
    public void setExplodeRadius(double explodeRadius) {this.explodeRadius = explodeRadius;}
}
