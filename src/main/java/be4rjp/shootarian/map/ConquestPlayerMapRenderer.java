package be4rjp.shootarian.map;

import be4rjp.shootarian.player.ShootarianPlayer;
import net.minecraft.server.v1_15_R1.MapIcon;
import net.minecraft.server.v1_15_R1.PacketPlayOutMap;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_15_R1.util.CraftChatMessage;

import java.util.HashSet;
import java.util.Set;

public class ConquestPlayerMapRenderer extends PlayerGUIRenderer{
    
    private final CanvasData canvasData;
    
    public ConquestPlayerMapRenderer(ShootarianPlayer shootarianPlayer, ConquestStatusRenderer canvasBufferRenderer, CanvasData canvasData) {
        super(shootarianPlayer, canvasBufferRenderer);
        this.canvasData = canvasData;
    }
    
    @Override
    public void render(CanvasBuffer canvasBuffer) {
    
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
        
        Set<MapIcon> mapIcons = new HashSet<>();
        mapIcons.add(new MapIcon(MapIcon.Type.PLAYER, (byte) cursorX, (byte) cursorZ, (byte) direction, CraftChatMessage.fromStringOrNull(null)));
        PacketPlayOutMap map = new PacketPlayOutMap(0, (byte) 0, false, false, mapIcons, canvasBuffer.getBuffer(), 0, 0, 128, 128);
        this.packet = map;
        shootarianPlayer.sendPacket(map);
    }
}
