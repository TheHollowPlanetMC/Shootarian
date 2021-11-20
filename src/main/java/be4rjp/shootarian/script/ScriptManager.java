package be4rjp.shootarian.script;

import be4rjp.kuroko.script.Script;
import be4rjp.kuroko.script.ScriptRunner;
import be4rjp.shootarian.Shootarian;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Kurokoへの指示系
 */
public class ScriptManager {
    
    private static Map<String, Script> scriptMap = new HashMap<>();
    
    private static ScriptRunner playerJoinScriptRunner;
    
    public static void loadScript(){
    
        Shootarian.getPlugin().getLogger().info("Loading player join script...");
        File dir = new File("plugins/Shootarian/script");
    
        File playerJoinScriptFile = new File("plugins/Shootarian/script/player_join.js");
        dir.getParentFile().mkdir();
        dir.mkdir();
        if(!playerJoinScriptFile.exists()){
            Shootarian.getPlugin().saveResource("script/player_join.js", false);
        }
    
        Script onPlayerJoinScript = new Script("player_join", playerJoinScriptFile);
        onPlayerJoinScript.load();
        playerJoinScriptRunner = onPlayerJoinScript.createScriptRunner();
    
    
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                String id = file.getName().replace(".js", "");
                Script script = new Script(id, file);
                script.load();
                scriptMap.put(id, script);
            }
        }
    }
    
    public static ScriptRunner getPlayerJoinScriptRunner() {return playerJoinScriptRunner;}
    
    public Script getScript(String id){return scriptMap.get(id);}
    
    public ScriptRunner createScriptRunner(String scriptID){return new ScriptRunner(scriptMap.get(scriptID));}
}
