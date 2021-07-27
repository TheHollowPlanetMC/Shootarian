package be4rjp.shellcase.weapon.reload;

import be4rjp.shellcase.ShellCase;
import be4rjp.shellcase.player.ShellCasePlayer;
import be4rjp.shellcase.weapon.reload.action.Action;
import be4rjp.shellcase.weapon.reload.action.SoundAction;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ReloadActions {
    
    private static Map<String, ReloadActions> reloadActionsMap = new HashMap<>();
    
    public static ReloadActions getReloadAction(String id){return reloadActionsMap.get(id);}
    
    
    public static void loadAllReloadActions() {
        ShellCase.getPlugin().getLogger().info("Loading reload actions...");
        File dir = new File("plugins/ShellCase/reload");
    
        dir.getParentFile().mkdir();
        dir.mkdir();
        File[] files = dir.listFiles();
        if (files.length == 0) {
            ShellCase.getPlugin().saveResource("reload/scar-h-reload.yml", false);
            files = dir.listFiles();
        }
    
        if (files != null) {
            for (File file : files) {
                ShellCase.getPlugin().getLogger().info(file.getName());
                String id = file.getName().replace(".yml", "");
                try {
                    YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
                    ReloadActions reloadActions = new ReloadActions(id);
                    reloadActions.loadData(yml);
                }catch (Exception e){e.printStackTrace();}
            }
        }
    }
    
    
    //識別ID
    private final String id;
    //設定ファイル
    private YamlConfiguration yml;
    //再生するtickとアクションのマップ
    private final Map<Integer, Action> reloadActions = new HashMap<>();
    //アクションの最終tick
    private int finalTick = 0;
    
    public ReloadActions(String id){
        this.id = id;
        reloadActionsMap.put(id, this);
    }
    
    public void loadData(YamlConfiguration yml){
        this.yml = yml;
        reloadActions.clear();
        
        // - [SOUND] 0, Sound/volume/pitch
        if(yml.contains("actions")){
            for(String line : yml.getStringList("actions")){
                line = line.replace("[", "");
                line = line.replace(" ", "");
                
                String[] args = line.split("]");
                
                String type = args[0];
                String secondArg = args[1];
                
                if(type.equals("SOUND")){
                    args = secondArg.split(",");
                    
                    int tick = Integer.parseInt(args[0]);
                    String sound = args[1];
    
                    SoundAction soundAction = new SoundAction(sound);
                    reloadActions.put(tick, soundAction);
                    
                    if(finalTick < tick) finalTick = tick;
                }
            }
        }
    }
    
    public void play(ShellCasePlayer reloadPlayer, int tick){
        Action action = reloadActions.get(tick);
        if(action == null) return;
        
        action.play(reloadPlayer, tick);
    }
    
    public int getFinalTick() {return finalTick;}
}
