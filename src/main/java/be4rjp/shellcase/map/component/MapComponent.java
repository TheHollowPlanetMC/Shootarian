package be4rjp.shellcase.map.component;

import be4rjp.shellcase.map.CanvasBuffer;

public abstract class MapComponent {
    
    protected int x;
    
    protected int z;
    
    protected Runnable clickRunnable;
    
    public MapComponent(int x, int z, Runnable clickRunnable){
        this.x = x;
        this.z = z;
        this.clickRunnable = clickRunnable;
    }
    
    public abstract void setPixels(CanvasBuffer canvasBuffer);
    
    public void onClick(){
        if(clickRunnable != null) clickRunnable.run();
    }
    
    public abstract MapComponentBoundingBox getBoundingBox();
}
