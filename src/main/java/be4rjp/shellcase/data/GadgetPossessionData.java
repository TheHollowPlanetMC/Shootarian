package be4rjp.shellcase.data;

import be4rjp.shellcase.weapon.gadget.Gadget;

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
