package be4rjp.shellcase.map;

import be4rjp.shellcase.map.component.MapComponent;
import be4rjp.shellcase.map.component.MapComponentBoundingBox;
import be4rjp.shellcase.player.ShellCasePlayer;
import be4rjp.shellcase.util.math.Vec2f;
import net.minecraft.server.v1_15_R1.MapIcon;
import net.minecraft.server.v1_15_R1.PacketPlayOutMap;
import org.bukkit.craftbukkit.v1_15_R1.util.CraftChatMessage;

import java.util.*;

public class ConquestPlayerClickableGUIRenderer extends PlayerGUIRenderer{
    
    private float lastYaw;
    private float currentX = 0.0F;
    
    public ConquestPlayerClickableGUIRenderer(ShellCasePlayer shellCasePlayer, ConquestStatusRenderer canvasBufferRenderer){
        super(shellCasePlayer, canvasBufferRenderer);
        this.lastYaw = shellCasePlayer.getLocation().getYaw();
    }
    
    @Override
    public void render(CanvasBuffer canvasBuffer) {
        
        float y = shellCasePlayer.getLocation().getPitch();
        y -= 0.1F;
        y = Math.min(y, 90F);
        y = Math.max(y, 45F);
        y = ((y / 45.0F) - 1.5F) * 256.0F;
        
        float yaw = shellCasePlayer.getLocation().getYaw();
        float x = yaw - lastYaw;
        if(Math.abs(x) < 180){
            currentX += x * 3.0F;
            currentX = Math.min(currentX, 127.9F);
            currentX = Math.max(currentX, -128.0F);
        }
        this.lastYaw = yaw;
        
        
        int cursorPixelX = ((int) currentX + 128) >> 1;
        int cursorPixelY = ((int) y + 128) >> 1;
        List<MapComponent> allComponents = new ArrayList<>();
        allComponents.addAll(this.canvasBufferRenderer.getMapComponents());
        allComponents.addAll(this.mapComponents);
        for(MapComponent mapComponent : allComponents){
            MapComponentBoundingBox boundingBox = mapComponent.getBoundingBox();
            if(boundingBox == null) return;
            if(boundingBox.isInBox(cursorPixelX, cursorPixelY)) {
                drawBoundingBox(mapComponent.getBoundingBox(), canvasBuffer);
            }
        }
        
        //drawLine(canvasBuffer, new Vec2f(20, 30), new Vec2f(cursorPixelX, cursorPixelY), (byte) 116);
        
        Set<MapIcon> mapIcons = new HashSet<>();
        mapIcons.add(new MapIcon(MapIcon.Type.TARGET_X, (byte) currentX, (byte) y, (byte) 0, CraftChatMessage.fromStringOrNull(null)));
        PacketPlayOutMap map = new PacketPlayOutMap(0, (byte) 0, false, false, mapIcons, canvasBuffer.getBuffer(), 0, 0, 128, 128);
        this.packet = map;
        shellCasePlayer.sendPacket(map);
    }
    
    
    public static void drawBoundingBox(MapComponentBoundingBox boundingBox, CanvasBuffer canvasBuffer){
        drawLine(canvasBuffer, new Vec2f(boundingBox.minX, boundingBox.minY), new Vec2f(boundingBox.maxX, boundingBox.minY), (byte) 57);
        drawLine(canvasBuffer, new Vec2f(boundingBox.minX, boundingBox.minY), new Vec2f(boundingBox.minX, boundingBox.maxY), (byte) 57);
        drawLine(canvasBuffer, new Vec2f(boundingBox.maxX, boundingBox.minY), new Vec2f(boundingBox.maxX, boundingBox.maxY), (byte) 57);
        drawLine(canvasBuffer, new Vec2f(boundingBox.minX, boundingBox.maxY), new Vec2f(boundingBox.maxX, boundingBox.maxY), (byte) 57);
        drawLine(canvasBuffer, new Vec2f(boundingBox.minX - 1, boundingBox.minY - 1), new Vec2f(boundingBox.maxX + 1, boundingBox.minY - 1), (byte) 116);
        drawLine(canvasBuffer, new Vec2f(boundingBox.minX - 1, boundingBox.minY - 1), new Vec2f(boundingBox.minX - 1, boundingBox.maxY + 1), (byte) 116);
        drawLine(canvasBuffer, new Vec2f(boundingBox.maxX + 1, boundingBox.minY - 1), new Vec2f(boundingBox.maxX + 1, boundingBox.maxY + 1), (byte) 116);
        drawLine(canvasBuffer, new Vec2f(boundingBox.minX - 1, boundingBox.maxY + 1), new Vec2f(boundingBox.maxX + 1, boundingBox.maxY + 1), (byte) 116);
    }
    
    public static void drawLine(CanvasBuffer canvasBuffer, Vec2f start, Vec2f end, byte color){
        Vec2f direction = new Vec2f(end.x - start.x, end.y - start.y);
        
        float accuracy = 0.1F;
        int max = (int) (direction.length() / accuracy);
        
        Vec2f add = direction.clone().setLength(accuracy);
        Vec2f currentPosition = start.clone();
        for(int i = 0; i < max; i++){
            currentPosition.add(add);
            canvasBuffer.setPixel((int) currentPosition.x, (int) currentPosition.y, color);
        }
    }
}
