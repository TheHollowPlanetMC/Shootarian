package be4rjp.shellcase.map;

import be4rjp.shellcase.ShellCase;
import be4rjp.shellcase.map.component.MapComponent;
import be4rjp.shellcase.map.component.MapTextComponent;
import be4rjp.shellcase.player.ShellCasePlayer;
import net.minecraft.server.v1_15_R1.PacketPlayOutMap;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public abstract class PlayerGUIRenderer extends BukkitRunnable {
    
    protected PacketPlayOutMap packet;
    
    public PacketPlayOutMap getPacket() {return packet;}
    
    protected final ConquestStatusRenderer canvasBufferRenderer;
    protected final ShellCasePlayer shellCasePlayer;
    
    protected List<MapComponent> mapComponents = new ArrayList<>();
    
    public void addMapComponent(MapComponent mapComponent){
        this.mapComponents.add(mapComponent);
    }
    
    public PlayerGUIRenderer(ShellCasePlayer shellCasePlayer, ConquestStatusRenderer canvasBufferRenderer){
        this.shellCasePlayer = shellCasePlayer;
        this.canvasBufferRenderer = canvasBufferRenderer;
    }
    
    @Override
    public void run() {
        if(shellCasePlayer.getPlayerGUIRenderer() != this){
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
        this.runTaskTimerAsynchronously(ShellCase.getPlugin(), 0, 1);
    }
}
