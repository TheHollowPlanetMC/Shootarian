package be4rjp.shellcase.script;

import be4rjp.kuroko.script.NPCScript;
import be4rjp.kuroko.script.ScriptRunner;
import be4rjp.shellcase.ShellCase;

import java.io.File;

/**
 * Kurokoへの指示系
 */
public class ScriptManager {
    
    private static ScriptRunner playerJoinScriptRunner;
    
    public static void loadScript(){
    
        ShellCase.getPlugin().getLogger().info("Loading player join script...");
        File dir = new File("plugins/ShellCase/script");
    
        File playerJoinScriptFile = new File("plugins/ShellCase/script/player_join.js");
        dir.getParentFile().mkdir();
        dir.mkdir();
        if(!playerJoinScriptFile.exists()){
            ShellCase.getPlugin().saveResource("script/player_join.js", false);
        }
    
        NPCScript onPlayerJoinScript = new NPCScript("player_join", playerJoinScriptFile);
        onPlayerJoinScript.load();
        playerJoinScriptRunner = onPlayerJoinScript.createScriptRunner();
        
    }
    
    public static ScriptRunner getPlayerJoinScriptRunner() {return playerJoinScriptRunner;}
}
