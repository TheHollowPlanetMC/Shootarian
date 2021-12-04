package be4rjp.shootarian.listener;

import be4rjp.kuroko.player.KurokoPlayer;
import be4rjp.shootarian.Shootarian;
import be4rjp.shootarian.ShootarianConfig;
import be4rjp.shootarian.match.Match;
import be4rjp.shootarian.match.MatchManager;
import be4rjp.shootarian.match.team.ShootarianTeam;
import be4rjp.shootarian.packet.PacketHandler;
import be4rjp.shootarian.player.ShootarianPlayer;
import be4rjp.shootarian.script.ScriptManager;
import be4rjp.shootarian.util.TaskHandler;
import be4rjp.shootarian.weapon.attachment.Attachment;
import be4rjp.shootarian.weapon.gadget.Gadget;
import be4rjp.shootarian.weapon.gun.GunStatusData;
import be4rjp.shootarian.weapon.gun.GunWeapon;
import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerJoinQuitListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        player.teleport(ShootarianConfig.getJoinLocation());
        
    
        TaskHandler.runAsync(() -> {
            player.setWalkSpeed(0.2F);
            player.getInventory().clear();

            ShootarianPlayer shootarianPlayer = ShootarianPlayer.getShootarianPlayer(player);
            shootarianPlayer.updateBukkitPlayer(player, false);
            shootarianPlayer.sendSkinRequest();

            //セーブデータのロード
            shootarianPlayer.loadAchievementFromSQL();
    
            //プレイヤー参加時のスクリプト実行
            TaskHandler.supplySync(() -> KurokoPlayer.getKurokoPlayer(player)).thenAccept(kurokoPlayer -> {
                if(kurokoPlayer == null) return;
                ScriptManager.getPlayerJoinScriptRunner().runFunction("onPlayerJoin", shootarianPlayer, player, kurokoPlayer);
            });
            
            //デバッグ用武器配布
            for(int i = 0; i < 4; i++) {
                GunStatusData gunStatusData = new GunStatusData(GunWeapon.getGunWeaponBySaveNumber(i), shootarianPlayer);
                gunStatusData.addAttachment(Attachment.getAttachmentBySaveNumber(1));
                gunStatusData.addAttachment(Attachment.getAttachmentBySaveNumber(2));
                gunStatusData.addAttachment(Attachment.getAttachmentBySaveNumber(3));
                gunStatusData.addAttachment(Attachment.getAttachmentBySaveNumber(4));
                shootarianPlayer.getWeaponPossessionData().setGunStatusData(gunStatusData);
            }
            shootarianPlayer.getGadgetPossessionData().setGadget(Gadget.FLAG_GRENADE);
            shootarianPlayer.getGadgetPossessionData().setGadget(Gadget.GRAPPLE_GUN);
            shootarianPlayer.getGadgetPossessionData().setGadget(Gadget.RPG_7);
            
            //アイテムセット
            shootarianPlayer.giveItems();
            
            
            /*
            ShootarianTeam ShootarianTeam = shootarianPlayer.getShootarianTeam();
            
            if(ShootarianTeam != null) {
                if(ShootarianTeam == Shootarian.getLobbyTeam()){
                    MatchManager matchManager = shootarianPlayer.getMatchManager();
                    if(matchManager != null){
                        Match match = matchManager.getMatch();
                        if(match != null){
                            shootarianPlayer.teleport(ShootarianConfig.getLobbyLocation());
                            return;
                        }
                    }
                }
                
                if(ShootarianTeam != Shootarian.getLobbyTeam()) {
                    Match match = ShootarianTeam.getMatch();

                    switch (match.getMatchStatus()) {
                        case WAITING: {
                            shootarianPlayer.teleport(ShootarianConfig.getLobbyLocation());
                            break;
                        }
    
                        case IN_PROGRESS: {
                            match.teleportToTeamLocation(shootarianPlayer);
                            break;
                        }
    
                        default: {
                            shootarianPlayer.reset();
                            shootarianPlayer.teleport(ShootarianConfig.getLobbyLocation());
                            break;
                        }
                    }
                }
            }*/
        });
    }
    
    
    @EventHandler
    public void onjoin(PlayerJoinEvent event){
        //Inject packet handler
        Player player = event.getPlayer();
        
        PacketHandler packetHandler = new PacketHandler(player);
        
        try {
            ChannelPipeline pipeline = ((CraftPlayer)player).getHandle().playerConnection.networkManager.channel.pipeline();
            pipeline.addBefore("packet_handler", Shootarian.getPlugin().getName() + "PacketInjector:" + player.getName(), packetHandler);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
    @EventHandler
    public void onleave(PlayerQuitEvent event){
        Player player = event.getPlayer();
        NPCTeleportListener.scheduledTeleport.remove(player);
    
        TaskHandler.runAsync(() -> {
            ShootarianPlayer shootarianPlayer = ShootarianPlayer.getShootarianPlayer(player);
            shootarianPlayer.reset();
            try {
                if(shootarianPlayer.isLoadedSaveData()) shootarianPlayer.saveAchievementToSQL();
            }catch (Exception e){
                e.printStackTrace();
            }
        });
        
        try {
            Channel channel = ((CraftPlayer)player).getHandle().playerConnection.networkManager.channel;
            
            channel.eventLoop().submit(() -> {
                channel.pipeline().remove(Shootarian.getPlugin().getName() + "PacketInjector:" + player.getName());
                return null;
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    
        player.teleport(ShootarianConfig.getJoinLocation());
    }
}
