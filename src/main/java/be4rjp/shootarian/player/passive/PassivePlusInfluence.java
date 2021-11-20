package be4rjp.shootarian.player.passive;

public class PassivePlusInfluence extends PassiveInfluence{
    
    private final double plus;
    
    public PassivePlusInfluence(Passive passive, double plus){
        super(passive);
        this.plus = plus;
    }
    
    public double getPlus() {return plus;}
    
    
    @Override
    public double setInfluence(double raw) {return raw += plus;}
}
