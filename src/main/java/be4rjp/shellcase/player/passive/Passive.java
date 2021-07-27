package be4rjp.shellcase.player.passive;


public enum Passive {
    
    //効果なし
    NONE(0.0F);

    
    private final float default_influence;
    
    Passive(float default_influence){
        this.default_influence = default_influence;
    }
    
    public float getDefaultInfluence() {return default_influence;}
}
