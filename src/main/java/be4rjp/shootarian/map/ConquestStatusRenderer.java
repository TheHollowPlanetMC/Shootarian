package be4rjp.shootarian.map;

import be4rjp.shootarian.Shootarian;
import be4rjp.shootarian.map.component.MapComponent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class ConquestStatusRenderer extends BukkitRunnable {
    
    private final CanvasBuffer originalCanvasBuffer;
    
    private CanvasBuffer canvasBuffer;
    
    private final List<MapComponent> mapComponents = new ArrayList<>();
    
    public void addMapComponent(MapComponent mapComponent){this.mapComponents.add(mapComponent);}
    
    public List<MapComponent> getMapComponents() {return mapComponents;}
    
    public ConquestStatusRenderer(CanvasBuffer canvasBuffer){
        this.originalCanvasBuffer = canvasBuffer;
        this.canvasBuffer = canvasBuffer;
    }
    
    
    public synchronized CanvasBuffer copyCanvasBuffer(){
        return canvasBuffer.clone();
    }
    
    
    @Override
    public synchronized void run() {
        this.canvasBuffer = this.originalCanvasBuffer.clone();
        for(MapComponent mapComponent : mapComponents){
            mapComponent.setPixels(canvasBuffer);
        }
    }
    
    public void start(){
        this.runTaskTimerAsynchronously(Shootarian.getPlugin(), 0, 5);
    }
}
