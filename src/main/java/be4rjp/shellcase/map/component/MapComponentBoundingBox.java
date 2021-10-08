package be4rjp.shellcase.map.component;

public class MapComponentBoundingBox {
    
    public int minX;
    public int minY;
    
    public int maxX;
    public int maxY;
    
    public MapComponentBoundingBox(int minX, int minY, int maxX, int maxY){
        this.minX = minX;
        this.minY = minY;
        
        this.maxX = maxX;
        this.maxY = maxY;
    }
    
    public boolean isInBox(int x, int y){
        return minX <= x && x <= maxX && minY <= y && y <= maxY;
    }
    
}
