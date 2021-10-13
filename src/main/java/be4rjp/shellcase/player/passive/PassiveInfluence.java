package be4rjp.shellcase.player.passive;

public abstract class PassiveInfluence {
    
    public static PassiveInfluence fromString(String line){
        //RUN_SPEED, +20%
        String[] args = line.replace(" ", "").split(",");
        
        if(args.length != 2) throwSyntaxError();
        
        Passive passive = Passive.valueOf(args[0]);
        String plusOrRate = args[1];
        
        switch (plusOrRate.toCharArray()[0]){
            case '+':{
                if(plusOrRate.contains("%")){
                    double rate = Double.parseDouble(plusOrRate.replace("+", "").replace("%", ""));
                    rate = rate / 100.0 + 1.0;
                    
                    return new PassiveRateInfluence(passive, rate);
                }else{
                    double plus = Double.parseDouble(plusOrRate.replace("+", ""));
                    
                    return new PassivePlusInfluence(passive, plus);
                }
            }
            case '-':{
                if(plusOrRate.contains("%")){
                    double rate = Double.parseDouble(plusOrRate.replace("-", "").replace("%", ""));
                    rate = 1.0 - (rate / 100.0);
                    
                    return new PassiveRateInfluence(passive, rate);
                }else{
                    double plus = -1.0 * Double.parseDouble(plusOrRate.replace("-", ""));
                    
                    return new PassivePlusInfluence(passive, plus);
                }
            }
            default:{
                throwSyntaxError();
                return null;
            }
        }
    }
    
    public static void throwSyntaxError(){
        throw new IllegalArgumentException("PassiveInfluence needs to be written in the form of 'RUN_SPEED, +20%'.");
    }
    
    
    protected final Passive passive;
    
    public PassiveInfluence(Passive passive){
        this.passive = passive;
    }
    
    public Passive getPassive() {return passive;}
    
    public abstract double setInfluence(double raw);
}
