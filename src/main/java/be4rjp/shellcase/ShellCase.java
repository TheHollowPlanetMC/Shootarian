package be4rjp.shellcase;

import be4rjp.shellcase.gui.MainMenuItem;
import be4rjp.shellcase.language.MessageManager;
import be4rjp.shellcase.listener.*;
import be4rjp.shellcase.map.CanvasData;
import be4rjp.shellcase.match.MatchManager;
import be4rjp.shellcase.match.PlayerLobbyMatch;
import be4rjp.shellcase.match.map.ShellCaseMap;
import be4rjp.shellcase.match.map.structure.MapStructure;
import be4rjp.shellcase.match.team.ShellCaseColor;
import be4rjp.shellcase.match.team.ShellCaseTeam;
import be4rjp.shellcase.player.costume.HeadGear;
import be4rjp.shellcase.weapon.WeaponManager;
import be4rjp.shellcase.weapon.actions.Actions;
import com.grinderwolf.swm.api.SlimePlugin;
import com.samjakob.spigui.SpiGUI;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Timer;

public final class ShellCase extends JavaPlugin {
    
    public static final String VERSION = "v0.0.1 - α";
    
    private static ShellCase shellCase;
    private static SlimePlugin slimePlugin;
    
    private static PlayerLobbyMatch playerLobbyMatch;
    private static ShellCaseTeam lobbyTeam;
    
    private static Timer asyncTimer;
    
    private static SpiGUI spiGUI;
    
    
    @Override
    public void onEnable() {
        // Plugin startup logic
        shellCase = this;
        
        spiGUI = new SpiGUI(this);
        
        asyncTimer = new Timer(true);
    
        SlimePlugin slimePluginInstance = (SlimePlugin) Bukkit.getPluginManager().getPlugin("SlimeWorldManager");
        if(slimePluginInstance == null){
            throw new NullPointerException("SlimeWorldManager is not found.");
        }
        slimePlugin = slimePluginInstance;
    
        ShellCaseConfig.load();
        MessageManager.loadAllMessage();
        CanvasData.loadAllCanvas();
        MapStructure.loadAllMapStructure();
        ShellCaseMap.loadAllMap();
        
        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new CancelListener(), this);
        pluginManager.registerEvents(new Cinema4CListener(), this);
        pluginManager.registerEvents(new PlayerClickInventoryListener(), this);
        pluginManager.registerEvents(new PlayerItemClickListener(), this);
        pluginManager.registerEvents(new PlayerJoinQuitListener(), this);
        pluginManager.registerEvents(new PlayerSlotChangeListener(), this);

        WeaponManager.loadAllAttachment();
        WeaponManager.loadAllRecoils();
        Actions.loadAllActions();
        WeaponManager.loadAllWeapon();
        HeadGear.loadAllHeadGear();
        MatchManager.load();
        
        new MainMenuItem();
    
    
        playerLobbyMatch = new PlayerLobbyMatch(ShellCaseMap.getRandomMap());
        lobbyTeam = new ShellCaseTeam(playerLobbyMatch, ShellCaseColor.getRandomColor());
    }
    
    @Override
    public void onDisable() {
        // Plugin shutdown logic
        try{
            asyncTimer.cancel();
        }catch (Exception e){e.printStackTrace();}
    }
    
    public static ShellCase getPlugin() {return shellCase;}
    
    public static SlimePlugin getSlimePlugin() {return slimePlugin;}
    
    public static PlayerLobbyMatch getLobbyMatch() {return playerLobbyMatch;}
    
    public static ShellCaseTeam getLobbyTeam() {return lobbyTeam;}
    
    public static Timer getAsyncTimer() {return asyncTimer;}
    
    public static SpiGUI getSpiGUI() {return spiGUI;}
}
