package be4rjp.shellcase.weapon.gadget;

public enum Gadget {
    
    FLAG_GRENADE(FlagGrenade.class);
    
    private GadgetWeapon instance;
    
    Gadget(Class<? extends GadgetWeapon> clazz){
        try {
            this.instance = clazz.getConstructor(Gadget.class).newInstance(this);
        }catch (Exception e){
            e.printStackTrace();
            this.instance = null;
        }
    }
    
    public GadgetWeapon getInstance() {return instance;}
}
