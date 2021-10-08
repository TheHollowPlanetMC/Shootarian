package be4rjp.shellcase.packet;

import be4rjp.shellcase.ShellCase;
import be4rjp.shellcase.map.PlayerGUIRenderer;
import be4rjp.shellcase.packet.manager.*;
import be4rjp.shellcase.player.ShellCasePlayer;
import io.netty.channel.*;
import net.minecraft.server.v1_15_R1.*;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.nio.channels.ClosedChannelException;

public class PacketHandler extends ChannelDuplexHandler {
    
    private final Player player;
    private final ShellCasePlayer shellCasePlayer;
    private final EntityPlayer entityPlayer;
    
    public PacketHandler(Player player){
        this.player = player;
        this.shellCasePlayer = ShellCasePlayer.getShellCasePlayer(player);
        this.entityPlayer = ((CraftPlayer)player).getHandle();
    }
    
    @Override
    public void channelRead(ChannelHandlerContext channelHandlerContext, Object packet) throws Exception {
    
        super.channelRead(channelHandlerContext, packet);
    }
    
    
    @Override
    public void write(ChannelHandlerContext channelHandlerContext, Object packet, ChannelPromise channelPromise) throws Exception {
    
        if(packet instanceof PacketPlayOutScoreboardScore){
            boolean send = ScoreUpdatePacketManager.write((PacketPlayOutScoreboardScore) packet, shellCasePlayer);
            if(!send) return;
        }
    
        if(packet instanceof PacketPlayOutNamedEntitySpawn){
            boolean send = PlayerSpawnPacketManager.write((PacketPlayOutNamedEntitySpawn) packet, shellCasePlayer);
            if(!send) return;
        }
        
        if(packet instanceof PacketPlayOutAbilities){
            PlayerAbilityPacketManager.write((PacketPlayOutAbilities) packet, shellCasePlayer);
        }
        
        if(packet instanceof PacketPlayOutEntityMetadata){
            PlayerMetadataPacketManager.write((PacketPlayOutEntityMetadata) packet, shellCasePlayer);
        }
        
        if(packet instanceof PacketPlayOutUpdateHealth){
            HealthUpdatePacketManager.write((PacketPlayOutUpdateHealth) packet, shellCasePlayer);
        }
        
        if(packet instanceof PacketPlayOutMap){
            PlayerGUIRenderer playerGUIRenderer = shellCasePlayer.getPlayerGUIRenderer();
            if(playerGUIRenderer != null){
                if(playerGUIRenderer.getPacket() != null) {
                    super.write(channelHandlerContext, playerGUIRenderer.getPacket(), channelPromise);
                    return;
                }
            }
        }
        
        
        super.write(channelHandlerContext, packet, channelPromise);
    }
    
    
    
    public void doRead(ChannelHandlerContext channelHandlerContext, Object packet) throws Exception{
        try {
            Channel channel = entityPlayer.playerConnection.networkManager.channel;
            
            ChannelHandler channelHandler = channel.pipeline().get(ShellCase.getPlugin().getName() + "PacketInjector:" + player.getName());
            if(channelHandler != null && player.isOnline()) {
                super.channelRead(channelHandlerContext, packet);
            }
        }catch (ClosedChannelException e){/**/}
    }
    
    public void doWrite(ChannelHandlerContext channelHandlerContext, Object packet, ChannelPromise channelPromise) throws Exception{
        try {
            Channel channel = entityPlayer.playerConnection.networkManager.channel;
            
            ChannelHandler channelHandler = channel.pipeline().get(ShellCase.getPlugin().getName() + "PacketInjector:" + player.getName());
            if(channelHandler != null && player.isOnline()) {
                super.write(channelHandlerContext, packet, channelPromise);
            }
        }catch (ClosedChannelException e){/**/}
    }
}
