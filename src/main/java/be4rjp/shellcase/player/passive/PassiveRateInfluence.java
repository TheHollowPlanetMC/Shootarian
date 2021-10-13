package be4rjp.shellcase.player.passive;


public class PassiveRateInfluence extends PassiveInfluence{
    
    private final double rate;
    
    public PassiveRateInfluence(Passive passive, double rate){
        super(passive);
        this.rate = rate;
    }
    
    public double getRate() {return rate;}
    
    
    @Override
    public double setInfluence(double raw) {return raw *= rate;}
}
