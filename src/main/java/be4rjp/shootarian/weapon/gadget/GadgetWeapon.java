package be4rjp.shootarian.weapon.gadget;

import be4rjp.shootarian.player.ShootarianPlayer;
import be4rjp.shootarian.weapon.ShootarianWeapon;

import java.util.HashMap;
import java.util.Map;

public abstract class GadgetWeapon extends ShootarianWeapon {

    private static final Map<String, GadgetWeapon> gadgetWeaponMap = new HashMap<>();

    public static GadgetWeapon getGadgetWeapon(String id){return gadgetWeaponMap.get(id);}


    protected final Gadget gadget;

    public GadgetWeapon(Gadget gadget) {
        super(gadget.toString());
        gadgetWeaponMap.put(id, this);
        this.gadget = gadget;
    }

    public Gadget getGadget() {return gadget;}

    @Override
    public abstract void onRightClick(ShootarianPlayer shootarianPlayer);

    @Override
    public abstract void onLeftClick(ShootarianPlayer shootarianPlayer);
}
