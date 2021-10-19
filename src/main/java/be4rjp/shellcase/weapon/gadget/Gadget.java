package be4rjp.shellcase.weapon.gadget;

public enum Gadget {
    
    FLAG_GRENADE(FlagGrenade.class, 0),
    GRAPPLE_GUN(GrappleGun.class, 1);
    
    private GadgetWeapon instance;
    
    private final int saveNumber;
    
    Gadget(Class<? extends GadgetWeapon> clazz, int saveNumber){
        try {
            this.instance = clazz.getConstructor(Gadget.class).newInstance(this);
        }catch (Exception e){
            e.printStackTrace();
            this.instance = null;
        }
        this.saveNumber = saveNumber;
    }
    
    public GadgetWeapon getInstance() {return instance;}
    
    public int getSaveNumber() {return saveNumber;}
    
    
    public static Gadget getGadgetBySaveNumber(int saveNumber){
        for(Gadget gadget : Gadget.values()){
            if(gadget.saveNumber == saveNumber){
                return gadget;
            }
        }
        
        return null;
    }
}
