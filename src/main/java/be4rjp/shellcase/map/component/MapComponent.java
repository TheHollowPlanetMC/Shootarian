package be4rjp.shellcase.map.component;

import be4rjp.shellcase.map.CanvasBuffer;
import be4rjp.shellcase.player.ShellCasePlayer;

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
    
    public void onClick(ShellCasePlayer shellCasePlayer){
        if(clickRunnable != null) clickRunnable.run(shellCasePlayer);
    }
    
    public abstract MapComponentBoundingBox getBoundingBox();
    
    
    public static interface MapClickRunnable{
        void run(ShellCasePlayer shellCasePlayer);
    }
}
