package be4rjp.shootarian.map;

import be4rjp.shootarian.gui.GUIManager;
import be4rjp.shootarian.map.component.MapComponent;
import be4rjp.shootarian.map.component.MapComponentBoundingBox;
import be4rjp.shootarian.map.component.MapObjectiveComponent;
import be4rjp.shootarian.match.team.ShootarianTeam;
import be4rjp.shootarian.player.ShootarianPlayer;
import be4rjp.shootarian.util.math.Vec2f;
import net.minecraft.server.v1_15_R1.MapIcon;
import net.minecraft.server.v1_15_R1.PacketPlayOutMap;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_15_R1.util.CraftChatMessage;

import java.util.*;

public class ConquestPlayerClickableGUIRenderer extends PlayerGUIRenderer{
    
    private float lastYaw;
    private float currentX = 0.0F;
    
    private final CanvasData canvasData;
    
    private int cursorPixelX = 0;
    private int cursorPixelY = 0;
    
    public ConquestPlayerClickableGUIRenderer(ShootarianPlayer shootarianPlayer, ConquestStatusRenderer canvasBufferRenderer, CanvasData canvasData){
        super(shootarianPlayer, canvasBufferRenderer);
        this.lastYaw = shootarianPlayer.getLocation().getYaw();
        this.canvasData = canvasData;
    }
    
    @Override
    public void render(CanvasBuffer canvasBuffer) {
        boolean isShowAllComponent = GUIManager.isShowAllComponent(shootarianPlayer);
    
        ShootarianTeam shootarianTeam = shootarianPlayer.getShootarianTeam();
        if(shootarianTeam == null) return;
        
        //カーソル
        float y = shootarianPlayer.getLocation().getPitch();
        y -= 0.1F;
        y = Math.min(y, 90F);
        y = Math.max(y, 45F);
        y = ((y / 45.0F) - 1.5F) * 256.0F;
        
        float yaw = shootarianPlayer.getLocation().getYaw();
        float x = yaw - lastYaw;
        if(Math.abs(x) < 180){
            currentX += x * 3.0F;
            currentX = Math.min(currentX, 127.9F);
            currentX = Math.max(currentX, -128.0F);
        }
        this.lastYaw = yaw;
        
        
        this.cursorPixelX = ((int) currentX + 128) >> 1;
        this.cursorPixelY = ((int) y + 128) >> 1;
        List<MapComponent> allComponents = new ArrayList<>();
        allComponents.addAll(this.canvasBufferRenderer.getMapComponents());
        allComponents.addAll(this.mapComponents);
        if(isShowAllComponent) {
            for (MapComponent mapComponent : allComponents) {
                if(mapComponent instanceof MapObjectiveComponent){
                    if(((MapObjectiveComponent) mapComponent).getFlagAreaData().getTeam() != shootarianTeam){
                        continue;
                    }
                }
                
                MapComponentBoundingBox boundingBox = mapComponent.getBoundingBox();
                if (boundingBox == null) return;
                if (boundingBox.isInBox(cursorPixelX, cursorPixelY)) {
                    drawBoundingBox(mapComponent.getBoundingBox(), canvasBuffer);
                }
            }
        }
        Set<MapIcon> mapIcons = new HashSet<>();
        
        if(isShowAllComponent) mapIcons.add(new MapIcon(MapIcon.Type.TARGET_X, (byte) currentX, (byte) y, (byte) 0, CraftChatMessage.fromStringOrNull(null)));
        
        
        //プレイヤーの位置
        Location location = shootarianPlayer.getLocation();
        int cursorX = (location.getBlockX() - canvasData.getCenterX()) << 1 >> canvasData.getScale();
        int cursorZ = (location.getBlockZ() - canvasData.getCenterZ()) << 1 >> canvasData.getScale();
    
        cursorX = Math.min(cursorX, 127);
        cursorX = Math.max(cursorX, -127);
        cursorZ = Math.min(cursorZ, 127);
        cursorZ = Math.max(cursorZ, -127);
    
        Location temp = shootarianPlayer.getLocation();
        temp.setDirection(temp.getDirection());
        int direction = (int) (temp.getYaw() / 22.5F);
        direction = Math.min(direction, 15);
        direction = Math.max(direction, 0);
        if(!isShowAllComponent) mapIcons.add(new MapIcon(MapIcon.Type.PLAYER, (byte) cursorX, (byte) cursorZ, (byte) direction, CraftChatMessage.fromStringOrNull(null)));
        
        
        PacketPlayOutMap map = new PacketPlayOutMap(0, (byte) 0, false, false, mapIcons, canvasBuffer.getBuffer(), 0, 0, 128, 128);
        this.packet = map;
        shootarianPlayer.sendPacket(map);
    }
    
    public void onClick(){
        ShootarianTeam shootarianTeam = shootarianPlayer.getShootarianTeam();
        if(shootarianTeam == null) return;
        
        if(GUIManager.isShowAllComponent(shootarianPlayer)) {
            List<MapComponent> allComponents = new ArrayList<>();
            allComponents.addAll(this.canvasBufferRenderer.getMapComponents());
            allComponents.addAll(this.mapComponents);
    
            for (MapComponent mapComponent : allComponents) {
                if(mapComponent instanceof MapObjectiveComponent){
                    if(((MapObjectiveComponent) mapComponent).getFlagAreaData().getTeam() != shootarianTeam){
                        continue;
                    }
                }
                
                MapComponentBoundingBox boundingBox = mapComponent.getBoundingBox();
                if (boundingBox == null) return;
                if (boundingBox.isInBox(cursorPixelX, cursorPixelY)) {
                    mapComponent.onClick(shootarianPlayer);
                }
            }
        }
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
    
    /*
    public static boolean isDrawCursor(ShootarianPlayer shootarianPlayer){
        ShootarianTeam shootarianTeam = shootarianPlayer.getShootarianTeam();
        if(shootarianTeam == null) return false;
    
        Match match = shootarianTeam.getMatch();
        Location location = match.getShootarianMap().getTeamLocation(match.getShootarianTeams().indexOf(shootarianTeam));
        return LocationUtil.distanceSquaredSafeDifferentWorld(location, shootarianPlayer.getLocation()) <= 100.0;
    }*/
}
