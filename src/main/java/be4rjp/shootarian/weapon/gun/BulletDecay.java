package be4rjp.shootarian.weapon.gun;

import it.unimi.dsi.fastutil.ints.Int2FloatArrayMap;

public class BulletDecay {
    
    private final int lastTick;
    private final Int2FloatArrayMap array = new Int2FloatArrayMap();

    public BulletDecay(String line){
        this.array.defaultReturnValue(1.0F);
        
        if(line.equals("")){
            this.lastTick = 0;
        }else{
            String[] args = line.replace(" ", "").split(">");
            
            int last = 0;
            for(String arg : args){
                String[] args2 = arg.split(",");
                
                int tick = Integer.parseInt(args2[0]);
                float rate = Float.parseFloat(args2[1]);
                
                if(last < tick){
                    last = tick;
                }
                
                this.array.put(tick, rate);
            }
            
            float temp = 1.0F;
            for(int index = 0; index < last; index++){
                float rate = array.get(index);
                if(rate == 1.0){
                    array.put(index, temp);
                }else{
                    temp = rate;
                }
            }
            
            this.lastTick = last;
        }
    }
    
    public float getRate(int tick){
        if(tick <= lastTick){
            return array.get(tick);
        }else{
            return array.get(lastTick);
        }
    }
}
