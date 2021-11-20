package be4rjp.shootarian;

import be4rjp.shootarian.gui.MainMenuItem;
import be4rjp.shootarian.language.MessageManager;
import be4rjp.shootarian.listener.*;
import be4rjp.shootarian.map.CanvasData;
import be4rjp.shootarian.match.MatchManager;
import be4rjp.shootarian.match.PlayerLobbyMatch;
import be4rjp.shootarian.match.map.ShootarianMap;
import be4rjp.shootarian.match.map.structure.MapStructure;
import be4rjp.shootarian.match.team.ShootarianColor;
import be4rjp.shootarian.match.team.ShootarianTeam;
import be4rjp.shootarian.player.costume.HeadGear;
import be4rjp.shootarian.scheduler.MultiThreadRunnable;
import be4rjp.shootarian.script.ScriptManager;
import be4rjp.shootarian.weapon.WeaponManager;
import be4rjp.shootarian.weapon.actions.Actions;
import com.grinderwolf.swm.api.SlimePlugin;
import com.samjakob.spigui.SpiGUI;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Timer;

public final class Shootarian extends JavaPlugin {
    
    public static final String VERSION = "v0.0.1 - α";
    
    private static Shootarian shootarian;
    private static SlimePlugin slimePlugin;
    
    private static PlayerLobbyMatch playerLobbyMatch;
    private static ShootarianTeam lobbyTeam;
    
    private static Timer asyncTimer;
    
    private static SpiGUI spiGUI;
    
    
    @Override
    public void onEnable() {
        // Plugin startup logic
        shootarian = this;
        
        spiGUI = new SpiGUI(this);
        
        asyncTimer = new Timer(true);
    
        SlimePlugin slimePluginInstance = (SlimePlugin) Bukkit.getPluginManager().getPlugin("SlimeWorldManager");
        if(slimePluginInstance == null){
            throw new NullPointerException("SlimeWorldManager is not found.");
        }
        slimePlugin = slimePluginInstance;
    
        ShootarianConfig.load();
        MultiThreadRunnable.initialize(ShootarianConfig.getRunnerThreads());
        MessageManager.loadAllMessage();
        CanvasData.loadAllCanvas();
        MapStructure.loadAllMapStructure();
        ShootarianMap.loadAllMap();
        
        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new CancelListener(), this);
        pluginManager.registerEvents(new Cinema4CListener(), this);
        pluginManager.registerEvents(new PlayerClickInventoryListener(), this);
        pluginManager.registerEvents(new PlayerItemClickListener(), this);
        pluginManager.registerEvents(new PlayerJoinQuitListener(), this);
        pluginManager.registerEvents(new PlayerSlotChangeListener(), this);
        pluginManager.registerEvents(new PlayerMapClickListener(), this);
        pluginManager.registerEvents(new NPCTeleportListener(), this);

        WeaponManager.loadAllAttachment();
        WeaponManager.loadAllRecoils();
        Actions.loadAllActions();
        WeaponManager.loadAllWeapon();
        HeadGear.loadAllHeadGear();
        MatchManager.load();
        ScriptManager.loadScript();
        
        new MainMenuItem();
    
    
        playerLobbyMatch = new PlayerLobbyMatch(ShootarianMap.getRandomMap());
        lobbyTeam = new ShootarianTeam(playerLobbyMatch, ShootarianColor.getRandomColor());
    }
    
    @Override
    public void onDisable() {
        // Plugin shutdown logic
        try{
            asyncTimer.cancel();
        }catch (Exception e){e.printStackTrace();}

        MultiThreadRunnable.onDisable();
    }
    
    public static Shootarian getPlugin() {return shootarian;}
    
    public static SlimePlugin getSlimePlugin() {return slimePlugin;}
    
    public static PlayerLobbyMatch getLobbyMatch() {return playerLobbyMatch;}
    
    public static ShootarianTeam getLobbyTeam() {return lobbyTeam;}
    
    public static Timer getAsyncTimer() {return asyncTimer;}
    
    public static SpiGUI getSpiGUI() {return spiGUI;}
}
