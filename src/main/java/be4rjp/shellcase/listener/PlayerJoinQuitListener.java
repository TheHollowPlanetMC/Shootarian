package be4rjp.shellcase.listener;

import be4rjp.shellcase.ShellCase;
import be4rjp.shellcase.ShellCaseConfig;
import be4rjp.shellcase.gui.MainMenuGUI;
import be4rjp.shellcase.map.ConquestPlayerClickableGUIRenderer;
import be4rjp.shellcase.map.ConquestPlayerMapRenderer;
import be4rjp.shellcase.map.PlayerGUIRenderer;
import be4rjp.shellcase.match.ConquestMatch;
import be4rjp.shellcase.match.map.ConquestMap;
import be4rjp.shellcase.match.map.ShellCaseMap;
import be4rjp.shellcase.match.team.ShellCaseColor;
import be4rjp.shellcase.match.team.ShellCaseTeam;
import be4rjp.shellcase.packet.PacketHandler;
import be4rjp.shellcase.player.ShellCasePlayer;
import be4rjp.shellcase.util.TaskHandler;
import be4rjp.shellcase.weapon.attachment.Attachment;
import be4rjp.shellcase.weapon.gadget.Gadget;
import be4rjp.shellcase.weapon.gadget.GadgetStatusData;
import be4rjp.shellcase.weapon.gun.GunStatusData;
import be4rjp.shellcase.weapon.gun.GunWeapon;
import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import io.papermc.lib.PaperLib;
import org.bukkit.Instrument;
import org.bukkit.Material;
import org.bukkit.Note;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.scheduler.BukkitRunnable;
import world.chiyogami.chiyogamilib.scheduler.WorldThreadRunnable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CompletableFuture;

public class PlayerJoinQuitListener implements Listener {
    
    private static final ConquestMatch match;
    private static final ShellCaseTeam team0;
    private static final ShellCaseTeam team1;
    
    private static int index = 0;
    
    static {
        match = new ConquestMatch(ConquestMap.getRandomConquestMap());
        team0 = new ShellCaseTeam(match, ShellCaseColor.BLUE);
        team1 = new ShellCaseTeam(match, ShellCaseColor.ORANGE);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        player.teleport(ShellCaseConfig.getJoinLocation());
    
    
        new BukkitRunnable() {
            @Override
            public void run() {
                player.setWalkSpeed(0.2F);
                player.getInventory().clear();
    
                ShellCasePlayer shellCasePlayer = ShellCasePlayer.getShellCasePlayer(player);
                shellCasePlayer.updateBukkitPlayer(player);
                shellCasePlayer.sendSkinRequest();
    
                shellCasePlayer.loadAchievementFromSQL();
    
                //shellCasePlayer.getAchievementData().getWeaponPossessionData().setGunStatusData(new GunStatusData(GunWeapon.getGunWeaponBySaveNumber(0), shellCasePlayer));
                //shellCasePlayer.getAchievementData().getWeaponPossessionData().setGunStatusData(new GunStatusData(GunWeapon.getGunWeaponBySaveNumber(2), shellCasePlayer));
                
                
                GunStatusData gunStatusData1 = new GunStatusData(GunWeapon.getGunWeaponBySaveNumber(0), shellCasePlayer);
                //gunStatusData1.addAttachment(Attachment.getAttachmentBySaveNumber(0));
                gunStatusData1.addAttachment(Attachment.getAttachmentBySaveNumber(1));
                gunStatusData1.addAttachment(Attachment.getAttachmentBySaveNumber(2));
                gunStatusData1.addAttachment(Attachment.getAttachmentBySaveNumber(3));
                gunStatusData1.addAttachment(Attachment.getAttachmentBySaveNumber(4));
                shellCasePlayer.getWeaponPossessionData().setGunStatusData(gunStatusData1);
    
                GunStatusData gunStatusData2 = new GunStatusData(GunWeapon.getGunWeaponBySaveNumber(2), shellCasePlayer);
                //gunStatusData2.addAttachment(Attachment.getAttachmentBySaveNumber(0));
                gunStatusData2.addAttachment(Attachment.getAttachmentBySaveNumber(1));
                gunStatusData2.addAttachment(Attachment.getAttachmentBySaveNumber(2));
                
                shellCasePlayer.getWeaponPossessionData().setGunStatusData(gunStatusData1);
                shellCasePlayer.getWeaponPossessionData().setGunStatusData(gunStatusData2);
                
                //MainMenuGUI.openMainMenuGUI(shellCasePlayer);
                shellCasePlayer.giveItems();
                
                
                //if(!shellCasePlayer.getWeaponPossessionData().hasWeapon(0)){
                    //shellCasePlayer.getAchievementData().getWeaponPossessionData().setGunStatusData(new GunStatusData(GunWeapon.getGunWeaponBySaveNumber(0), shellCasePlayer));
                    //shellCasePlayer.getAchievementData().getWeaponPossessionData().setGunStatusData(new GunStatusData(GunWeapon.getGunWeaponBySaveNumber(2), shellCasePlayer));
                //}
    
                //MainMenuGUI.openMainMenuGUI(shellCasePlayer);
                
                
                //shellCasePlayer.setLoadedSaveData(true);
    
                
                //ShellCaseTeam ShellCaseTeam = shellCasePlayer.getShellCaseTeam();
    
                /*----------------------------------
                TaskHandler.runSync(() -> {
    
                    if(index == 0){
                        match.initialize();
                        CompletableFuture<Void> completableFuture = match.loadGameMap();
                        completableFuture.thenAccept(v -> {
                            System.out.println("Map loaded!");
                            match.start();
                            shellCasePlayer.teleport(match.getShellCaseMap().getWaitLocation());
    
                            PlayerGUIRenderer playerGUIRenderer = new ConquestPlayerMapRenderer(shellCasePlayer, match.getConquestStatusRenderer(), match.getConquestMap().getCanvasData());
                            playerGUIRenderer.start();
                            shellCasePlayer.setPlayerGUIRenderer(playerGUIRenderer);
                        });
                    }
    
                    if(index % 2 == 0){
                        team0.join(shellCasePlayer);
                    }else{
                        team1.join(shellCasePlayer);
                    }
    
                    if(!shellCasePlayer.getWeaponPossessionData().hasWeapon(GunWeapon.getGunWeapon("scar-h").getSaveNumber())) {
                        GunStatusData scar = new GunStatusData(GunWeapon.getGunWeapon("scar-h"), shellCasePlayer);
                        GunStatusData mk14 = new GunStatusData(GunWeapon.getGunWeapon("m16-a3"), shellCasePlayer);
                        //GadgetStatusData gadgetStatusData = new GadgetStatusData(Gadget.FLAG_GRENADE.getInstance(), shellCasePlayer);
                        shellCasePlayer.getWeaponClass().setMainWeapon(scar);
                        shellCasePlayer.getWeaponClass().setSubWeapon(mk14);
                        //shellCasePlayer.getWeaponClass().setMainGadget(gadgetStatusData);
                    }
    
                    shellCasePlayer.getWeaponClass().setItem(shellCasePlayer);
    
                    ItemStack itemStack = new ItemStack(Material.FILLED_MAP);
                    MapMeta meta = (MapMeta) itemStack.getItemMeta();
                    meta.setMapId(0);
                    meta.setDisplayName("Copy map");
                    itemStack.setItemMeta(meta);
                    player.getInventory().addItem(itemStack);
    
                    index++;
                    
                });--------------------*/
                
                
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
    
        player.teleport(ShellCaseConfig.getJoinLocation());
    }
}
