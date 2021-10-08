package be4rjp.shellcase.util.math;

public class Vec2f implements Cloneable{
    
    public float x;
    public float y;
    
    public Vec2f(float x, float y){
        this.x = x;
        this.y = y;
    }
    
    public Vec2f add(Vec2f add){
        this.x += add.x;
        this.y += add.y;
        return this;
    }
    
    public Vec2f setLength(float length){
        float currentLength = this.length();
        float rate = length / currentLength;
        x *= rate;
        y *= rate;
        
        return this;
    }
    
    public float length(){
        return (float) Math.sqrt((float) Math.pow(x, 2) + (float) Math.pow(y, 2));
    }
    
    @Override
    public Vec2f clone(){
        return new Vec2f(x, y);
    }
}
