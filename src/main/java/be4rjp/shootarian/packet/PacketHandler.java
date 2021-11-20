package be4rjp.shootarian.packet;

import be4rjp.shootarian.Shootarian;
import be4rjp.shootarian.map.PlayerGUIRenderer;
import be4rjp.shootarian.packet.manager.*;
import be4rjp.shootarian.player.ShootarianPlayer;
import io.netty.channel.*;
import net.minecraft.server.v1_15_R1.*;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.nio.channels.ClosedChannelException;

public class PacketHandler extends ChannelDuplexHandler {
    
    private final Player player;
    private final ShootarianPlayer shootarianPlayer;
    private final EntityPlayer entityPlayer;
    
    public PacketHandler(Player player){
        this.player = player;
        this.shootarianPlayer = ShootarianPlayer.getShootarianPlayer(player);
        this.entityPlayer = ((CraftPlayer)player).getHandle();
    }
    
    @Override
    public void channelRead(ChannelHandlerContext channelHandlerContext, Object packet) throws Exception {
    
        super.channelRead(channelHandlerContext, packet);
    }
    
    
    @Override
    public void write(ChannelHandlerContext channelHandlerContext, Object packet, ChannelPromise channelPromise) throws Exception {
    
        if(packet instanceof PacketPlayOutScoreboardScore){
            boolean send = ScoreUpdatePacketManager.write((PacketPlayOutScoreboardScore) packet, shootarianPlayer);
            if(!send) return;
        }
    
        if(packet instanceof PacketPlayOutNamedEntitySpawn){
            boolean send = PlayerSpawnPacketManager.write((PacketPlayOutNamedEntitySpawn) packet, shootarianPlayer);
            if(!send) return;
        }
        
        if(packet instanceof PacketPlayOutAbilities){
            PlayerAbilityPacketManager.write((PacketPlayOutAbilities) packet, shootarianPlayer);
        }
        
        if(packet instanceof PacketPlayOutEntityMetadata){
            PlayerMetadataPacketManager.write((PacketPlayOutEntityMetadata) packet, shootarianPlayer);
        }
        
        if(packet instanceof PacketPlayOutUpdateHealth){
            HealthUpdatePacketManager.write((PacketPlayOutUpdateHealth) packet, shootarianPlayer);
        }
        
        if(packet instanceof PacketPlayOutMap){
            PlayerGUIRenderer playerGUIRenderer = shootarianPlayer.getPlayerGUIRenderer();
            if(playerGUIRenderer != null){
                if(playerGUIRenderer.getPacket() != null) {
                    super.write(channelHandlerContext, playerGUIRenderer.getPacket(), channelPromise);
                    return;
                }
            }
        }
        
        if(packet instanceof PacketPlayOutSetSlot){
            PacketPlayOutSetSlot slotPacket = shootarianPlayer.getSlotPacket();
            if(slotPacket != null){
                super.write(channelHandlerContext, slotPacket, channelPromise);
                return;
            }
        }
        
        
        super.write(channelHandlerContext, packet, channelPromise);
    }
    
    
    
    public void doRead(ChannelHandlerContext channelHandlerContext, Object packet) throws Exception{
        try {
            Channel channel = entityPlayer.playerConnection.networkManager.channel;
            
            ChannelHandler channelHandler = channel.pipeline().get(Shootarian.getPlugin().getName() + "PacketInjector:" + player.getName());
            if(channelHandler != null && player.isOnline()) {
                super.channelRead(channelHandlerContext, packet);
            }
        }catch (ClosedChannelException e){/**/}
    }
    
    public void doWrite(ChannelHandlerContext channelHandlerContext, Object packet, ChannelPromise channelPromise) throws Exception{
        try {
            Channel channel = entityPlayer.playerConnection.networkManager.channel;
            
            ChannelHandler channelHandler = channel.pipeline().get(Shootarian.getPlugin().getName() + "PacketInjector:" + player.getName());
            if(channelHandler != null && player.isOnline()) {
                super.write(channelHandlerContext, packet, channelPromise);
            }
        }catch (ClosedChannelException e){/**/}
    }
}
