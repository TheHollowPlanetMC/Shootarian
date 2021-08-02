package be4rjp.shellcase;

import be4rjp.shellcase.language.MessageManager;
import be4rjp.shellcase.listener.*;
import be4rjp.shellcase.match.MatchManager;
import be4rjp.shellcase.match.PlayerLobbyMatch;
import be4rjp.shellcase.match.map.ShellCaseMap;
import be4rjp.shellcase.match.team.ShellCaseColor;
import be4rjp.shellcase.match.team.ShellCaseTeam;
import be4rjp.shellcase.player.costume.HeadGear;
import be4rjp.shellcase.weapon.WeaponManager;
import be4rjp.shellcase.weapon.reload.ReloadActions;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class ShellCase extends JavaPlugin {
    
    public static final String VERSION = "v0.0.1 - α";
    
    private static ShellCase shellCase;
    
    private static PlayerLobbyMatch playerLobbyMatch;
    private static ShellCaseTeam lobbyTeam;
    
    
    @Override
    public void onEnable() {
        // Plugin startup logic
        shellCase = this;
    
        ShellCaseConfig.load();
        MessageManager.loadAllMessage();
        ShellCaseMap.loadAllMap();
        
        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new CancelListener(), this);
        pluginManager.registerEvents(new Cinema4CListener(), this);
        pluginManager.registerEvents(new PlayerClickInventoryListener(), this);
        pluginManager.registerEvents(new PlayerItemClickListener(), this);
        pluginManager.registerEvents(new PlayerJoinQuitListener(), this);

        WeaponManager.loadAllAttachment();
        WeaponManager.loadAllRecoils();
        ReloadActions.loadAllReloadActions();
        WeaponManager.loadAllWeapon();
        HeadGear.loadAllHeadGear();
        MatchManager.load();
    
    
        playerLobbyMatch = new PlayerLobbyMatch(ShellCaseMap.getRandomMap());
        lobbyTeam = new ShellCaseTeam(playerLobbyMatch, ShellCaseColor.getRandomColor());
    }
    
    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
    
    public static ShellCase getPlugin() {return shellCase;}
    
    public static PlayerLobbyMatch getLobbyMatch() {return playerLobbyMatch;}
    
    public static ShellCaseTeam getLobbyTeam() {return lobbyTeam;}
}
