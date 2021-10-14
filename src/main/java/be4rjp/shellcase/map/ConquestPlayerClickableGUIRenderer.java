package be4rjp.shellcase.map;

import be4rjp.shellcase.map.component.MapComponent;
import be4rjp.shellcase.map.component.MapComponentBoundingBox;
import be4rjp.shellcase.match.Match;
import be4rjp.shellcase.match.team.ShellCaseTeam;
import be4rjp.shellcase.player.ShellCasePlayer;
import be4rjp.shellcase.util.LocationUtil;
import be4rjp.shellcase.util.math.Vec2f;
import net.minecraft.server.v1_15_R1.MapIcon;
import net.minecraft.server.v1_15_R1.PacketPlayOutMap;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_15_R1.util.CraftChatMessage;

import java.util.*;

public class ConquestPlayerClickableGUIRenderer extends PlayerGUIRenderer{
    
    private float lastYaw;
    private float currentX = 0.0F;
    
    private final CanvasData canvasData;
    
    public ConquestPlayerClickableGUIRenderer(ShellCasePlayer shellCasePlayer, ConquestStatusRenderer canvasBufferRenderer, CanvasData canvasData){
        super(shellCasePlayer, canvasBufferRenderer);
        this.lastYaw = shellCasePlayer.getLocation().getYaw();
        this.canvasData = canvasData;
    }
    
    @Override
    public void render(CanvasBuffer canvasBuffer) {
        
        //カーソル
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
        Set<MapIcon> mapIcons = new HashSet<>();
        if(isDrawCursor(shellCasePlayer)) mapIcons.add(new MapIcon(MapIcon.Type.TARGET_X, (byte) currentX, (byte) y, (byte) 0, CraftChatMessage.fromStringOrNull(null)));
        
        
        
        //プレイヤーの位置
        Location location = shellCasePlayer.getLocation();
        int cursorX = (location.getBlockX() - canvasData.getCenterX()) << 1 >> canvasData.getScale();
        int cursorZ = (location.getBlockZ() - canvasData.getCenterZ()) << 1 >> canvasData.getScale();
    
        cursorX = Math.min(cursorX, 127);
        cursorX = Math.max(cursorX, -127);
        cursorZ = Math.min(cursorZ, 127);
        cursorZ = Math.max(cursorZ, -127);
    
        Location temp = shellCasePlayer.getLocation();
        temp.setDirection(temp.getDirection());
        int direction = (int) (temp.getYaw() / 22.5F);
        direction = Math.min(direction, 15);
        direction = Math.max(direction, 0);
        mapIcons.add(new MapIcon(MapIcon.Type.PLAYER, (byte) cursorX, (byte) cursorZ, (byte) direction, CraftChatMessage.fromStringOrNull(null)));
        
        
        
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
    
    
    public static boolean isDrawCursor(ShellCasePlayer shellCasePlayer){
        ShellCaseTeam shellCaseTeam = shellCasePlayer.getShellCaseTeam();
        if(shellCaseTeam == null) return false;
    
        Match match = shellCaseTeam.getMatch();
        Location location = match.getShellCaseMap().getTeamLocation(match.getShellCaseTeams().indexOf(shellCaseTeam));
        return LocationUtil.distanceSquaredSafeDifferentWorld(location, shellCasePlayer.getLocation()) <= 100.0;
    }
}
