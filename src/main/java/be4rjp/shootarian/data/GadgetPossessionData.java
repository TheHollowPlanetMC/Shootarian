package be4rjp.shootarian.data;

import be4rjp.shootarian.weapon.gadget.Gadget;

/**
 * 最大512個(64byte)
 */
public class GadgetPossessionData extends SavableBitData{

    public boolean hasGadget(Gadget gadget){
        return super.getBit(gadget.getSaveNumber());
    }
    
    public void setGadget(Gadget gadget){
        super.setBit(gadget.getSaveNumber(), true);
    }
}
