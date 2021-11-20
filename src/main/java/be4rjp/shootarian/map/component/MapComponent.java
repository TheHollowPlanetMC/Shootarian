package be4rjp.shootarian.map.component;

import be4rjp.shootarian.map.CanvasBuffer;
import be4rjp.shootarian.player.ShootarianPlayer;

public abstract class MapComponent {
    
    protected int x;
    
    protected int z;
    
    protected MapClickRunnable clickRunnable;
    
    public MapComponent(int x, int z, MapClickRunnable clickRunnable){
        this.x = x;
        this.z = z;
        this.clickRunnable = clickRunnable;
    }
    
    public abstract void setPixels(CanvasBuffer canvasBuffer);
    
    public void onClick(ShootarianPlayer shootarianPlayer){
        if(clickRunnable != null) clickRunnable.run(shootarianPlayer);
    }
    
    public abstract MapComponentBoundingBox getBoundingBox();
    
    
    public static interface MapClickRunnable{
        void run(ShootarianPlayer shootarianPlayer);
    }
}
