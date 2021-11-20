package be4rjp.shootarian.map;

import be4rjp.shootarian.Shootarian;
import be4rjp.shootarian.map.component.MapComponent;
import be4rjp.shootarian.player.ShootarianPlayer;
import net.minecraft.server.v1_15_R1.PacketPlayOutMap;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public abstract class PlayerGUIRenderer extends BukkitRunnable {
    
    protected PacketPlayOutMap packet;
    
    public PacketPlayOutMap getPacket() {return packet;}
    
    protected final ConquestStatusRenderer canvasBufferRenderer;
    protected final ShootarianPlayer shootarianPlayer;
    
    protected List<MapComponent> mapComponents = new ArrayList<>();
    
    public void addMapComponent(MapComponent mapComponent){
        this.mapComponents.add(mapComponent);
    }
    
    public PlayerGUIRenderer(ShootarianPlayer shootarianPlayer, ConquestStatusRenderer canvasBufferRenderer){
        this.shootarianPlayer = shootarianPlayer;
        this.canvasBufferRenderer = canvasBufferRenderer;
    }
    
    @Override
    public void run() {
        if(shootarianPlayer.getPlayerGUIRenderer() != this){
            return;
        }
        if(canvasBufferRenderer.isCancelled()){
            canvasBufferRenderer.cancel();
            return;
        }
        
        CanvasBuffer canvasBuffer = canvasBufferRenderer.copyCanvasBuffer();
    
        for(MapComponent mapComponent : this.mapComponents){
            mapComponent.setPixels(canvasBuffer);
        }
        
        render(canvasBuffer);
    }
    
    public abstract void render(CanvasBuffer canvasBuffer);
    
    public void start(){
        this.runTaskTimerAsynchronously(Shootarian.getPlugin(), 0, 1);
    }
}
