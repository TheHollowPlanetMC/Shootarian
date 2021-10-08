package be4rjp.shellcase.map;

import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import java.util.concurrent.CompletableFuture;

public class CanvasCopyManager extends MapRenderer {
    
    public static CompletableFuture<CanvasBuffer> copy(MapView mapView){
        CompletableFuture<CanvasBuffer> completableFuture = new CompletableFuture<>();
        mapView.addRenderer(new CanvasCopyManager(completableFuture));
        
        return completableFuture;
    }
    
    
    private final CompletableFuture<CanvasBuffer> completableFuture;
    
    private CanvasCopyManager(CompletableFuture<CanvasBuffer> completableFuture){
        this.completableFuture = completableFuture;
    }
    
    @Override
    public void render(MapView map, MapCanvas canvas, Player player) {
        completableFuture.complete(CanvasBuffer.fromMapCanvas(canvas));
    }
}
