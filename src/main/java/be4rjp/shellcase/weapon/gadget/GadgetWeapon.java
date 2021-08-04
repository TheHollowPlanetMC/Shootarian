package be4rjp.shellcase.weapon.gadget;

import be4rjp.shellcase.player.ShellCasePlayer;
import be4rjp.shellcase.weapon.ShellCaseWeapon;

import java.util.HashMap;
import java.util.Map;

public abstract class GadgetWeapon extends ShellCaseWeapon {

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
    public abstract void onRightClick(ShellCasePlayer shellCasePlayer);

    @Override
    public abstract void onLeftClick(ShellCasePlayer shellCasePlayer);
}
