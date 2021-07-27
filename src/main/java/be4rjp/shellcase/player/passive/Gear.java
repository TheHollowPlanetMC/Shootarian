package be4rjp.shellcase.player.passive;

import be4rjp.shellcase.language.Lang;
import be4rjp.shellcase.language.MessageManager;

/**
 * ギア (最大数: 256)
 */
public enum Gear {

    NO_GEAR(Passive.NONE, 0);

    private final Passive passive;
    private final int saveNumber;

    Gear(Passive passive, int saveNumber){
        this.passive = passive;
        this.saveNumber = saveNumber;
    }
    
    public Passive getPassive() {return passive;}
    
    public int getSaveNumber() {return saveNumber;}
    
    public String getDisplayName(Lang lang){
        return MessageManager.getText(lang, "gear-" + this);
    }
    
    public static Gear getGearByID(int id){
        for(Gear gear : Gear.values()){
            if(gear.getSaveNumber() == id) return gear;
        }
        return NO_GEAR;
    }
}
