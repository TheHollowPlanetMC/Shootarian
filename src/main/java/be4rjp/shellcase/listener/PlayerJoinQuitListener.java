package be4rjp.shellcase.listener;

import be4rjp.shellcase.ShellCase;
import be4rjp.shellcase.ShellCaseConfig;
import be4rjp.shellcase.match.ConquestMatch;
import be4rjp.shellcase.match.map.ShellCaseMap;
import be4rjp.shellcase.match.team.ShellCaseColor;
import be4rjp.shellcase.match.team.ShellCaseTeam;
import be4rjp.shellcase.packet.PacketHandler;
import be4rjp.shellcase.player.ShellCasePlayer;
import be4rjp.shellcase.weapon.gadget.Gadget;
import be4rjp.shellcase.weapon.gadget.GadgetStatusData;
import be4rjp.shellcase.weapon.gun.GunStatusData;
import be4rjp.shellcase.weapon.gun.GunWeapon;
import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import io.papermc.lib.PaperLib;
import org.bukkit.Instrument;
import org.bukkit.Note;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PlayerJoinQuitListener implements Listener {
    
    private static final ConquestMatch match;
    private static final ShellCaseTeam team0;
    private static final ShellCaseTeam team1;
    
    private static int index = 0;
    
    static {
        match = new ConquestMatch(ShellCaseMap.getRandomMap());
        team0 = new ShellCaseTeam(match, ShellCaseColor.BLUE);
        team1 = new ShellCaseTeam(match, ShellCaseColor.ORANGE);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        PaperLib.teleportAsync(player, ShellCaseConfig.getJoinLocation());
    
    
        new BukkitRunnable() {
            @Override
            public void run() {
                player.setWalkSpeed(0.2F);
                player.getInventory().clear();
    
                ShellCasePlayer shellCasePlayer = ShellCasePlayer.getShellCasePlayer(player);
                shellCasePlayer.updateBukkitPlayer(player);
                shellCasePlayer.sendSkinRequest();
    
                try {
                    //shellCasePlayer.loadAchievementFromSQL();
                }catch (Exception e){
                    player.playNote(player.getLocation(), Instrument.BASS_GUITAR, Note.flat(0, Note.Tone.G));
                    Date dateObj = new Date();
                    SimpleDateFormat format = new SimpleDateFormat( "yyyy/MM/dd HH:mm:ss" );
                    player.sendMessage("§c§n以下の理由により正常にセーブデータを読み込むことができませんでした。");
                    player.sendMessage("§c§n再度接続し直しても同じエラーが出る場合は運営に報告してください。");
                    player.sendMessage("§c§nThe save data could not be loaded properly for the following reasons.");
                    player.sendMessage("§c§nIf you still get the same error after trying to connect again, please report it to the administrators.");
                    player.sendMessage("");
                    player.sendMessage("§eError (" + format.format(dateObj) + ") : ");
                    player.sendMessage(e.getMessage());
                    e.printStackTrace();
                    
                    return;
                }
                
                //shellCasePlayer.setLoadedSaveData(true);
    
                
                //ShellCaseTeam ShellCaseTeam = shellCasePlayer.getShellCaseTeam();
                
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if(index == 0){
                            match.initialize();
                            match.initializeStructure();
                            match.start();
                        }
                        
                        if(index % 2 == 0){
                            team0.join(shellCasePlayer);
                        }else{
                            team1.join(shellCasePlayer);
                        }
    
                        GunStatusData gunStatusData = new GunStatusData(GunWeapon.getGunWeapon("scar-h"), shellCasePlayer);
                        GadgetStatusData gadgetStatusData = new GadgetStatusData(Gadget.FLAG_GRENADE.getInstance(), shellCasePlayer);
                        shellCasePlayer.getWeaponClass().setMainWeapon(gunStatusData);
                        shellCasePlayer.getWeaponClass().setMainGadget(gadgetStatusData);
                        shellCasePlayer.getWeaponClass().setItem(shellCasePlayer);
                        
                        index++;
                    }
                }.runTask(ShellCase.getPlugin());
                
                /*
                if(ShellCaseTeam != null) {
                    if(ShellCaseTeam == ShellCase.getLobbyTeam()){
                        MatchManager matchManager = shellCasePlayer.getMatchManager();
                        if(matchManager != null){
                            Match match = matchManager.getMatch();
                            if(match != null){
                                shellCasePlayer.teleport(match.getShellCaseMap().getWaitLocation());
                                shellCasePlayer.setLobbyItem();
                                return;
                            }
                        }
                        
                        shellCasePlayer.setLobbyItem();
                    }
                    
                    if(ShellCaseTeam != ShellCase.getLobbyTeam()) {
                        Match match = ShellCaseTeam.getMatch();
    
                        switch (match.getMatchStatus()) {
                            case WAITING: {
                                shellCasePlayer.teleport(match.getShellCaseMap().getWaitLocation());
                                shellCasePlayer.setLobbyItem();
                                break;
                            }
        
                            case IN_PROGRESS: {
                                match.teleportToTeamLocation(shellCasePlayer);
                                break;
                            }
        
                            default: {
                                shellCasePlayer.reset();
                                shellCasePlayer.teleport(ShellCaseConfig.getLobbyLocation());
                                shellCasePlayer.setLobbyItem();
                                break;
                            }
                        }
                    }
                }*/
            }
        }.runTaskAsynchronously(ShellCase.getPlugin());
    }
    
    
    @EventHandler
    public void onjoin(PlayerJoinEvent event){
        //Inject packet handler
        Player player = event.getPlayer();
        
        PacketHandler packetHandler = new PacketHandler(player);
        
        try {
            ChannelPipeline pipeline = ((CraftPlayer)player).getHandle().playerConnection.networkManager.channel.pipeline();
            pipeline.addBefore("packet_handler", ShellCase.getPlugin().getName() + "PacketInjector:" + player.getName(), packetHandler);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
    @EventHandler
    public void onleave(PlayerQuitEvent event){
        Player player = event.getPlayer();
    
        new BukkitRunnable() {
            @Override
            public void run() {
                ShellCasePlayer shellCasePlayer = ShellCasePlayer.getShellCasePlayer(player);
                try {
                    if(shellCasePlayer.isLoadedSaveData()) shellCasePlayer.saveAchievementToSQL();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }.runTaskAsynchronously(ShellCase.getPlugin());
        
        try {
            Channel channel = ((CraftPlayer)player).getHandle().playerConnection.networkManager.channel;
            
            channel.eventLoop().submit(() -> {
                channel.pipeline().remove(ShellCase.getPlugin().getName() + "PacketInjector:" + player.getName());
                return null;
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    
        PaperLib.teleportAsync(player, ShellCaseConfig.getJoinLocation());
    }
}
