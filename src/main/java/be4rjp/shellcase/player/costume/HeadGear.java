package be4rjp.shellcase.player.costume;

import be4rjp.shellcase.ShellCase;
import be4rjp.shellcase.item.ShellCaseItem;
import be4rjp.shellcase.language.Lang;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class HeadGear extends ShellCaseItem {
    
    private static final Map<String, HeadGear> idMap = new HashMap<>();
    private static final Map<Integer, HeadGear> saveNumberMap = new HashMap<>();
    
    public static HeadGear getHeadGear(String id){return idMap.get(id);}
    
    public static HeadGear getHeadGearBySaveNumber(int saveNumber){return saveNumberMap.get(saveNumber);}
    
    
    public static void loadAllHeadGear() {
        ShellCase.getPlugin().getLogger().info("Loading head gears...");
        File dir = new File("plugins/ShellCase/gear");
    
        dir.getParentFile().mkdir();
        dir.mkdir();
        File[] files = dir.listFiles();
        if (files.length == 0) {
            ShellCase.getPlugin().saveResource("gear/helmet.yml", false);
            files = dir.listFiles();
        }
    
        if (files != null) {
            for (File file : files) {
                ShellCase.getPlugin().getLogger().info(file.getName());
                String id = file.getName().replace(".yml", "");
                YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
                HeadGear headGear = new HeadGear(id);
                headGear.loadData(yml);
            }
        }
    }
    
    
    //識別用ID
    private final String id;
    //設定ファイル
    private YamlConfiguration yml;
    //ヘッドギアのセーブ & ロード時の識別番号 (1 ~ 65535)
    private int saveNumber = 0;
    
    
    public HeadGear(String id){
        this.id = id;
        idMap.put(id, this);
    }
    
    
    /**
     * ymlファイルからロードする
     * @param yml
     */
    public void loadData(YamlConfiguration yml) {
        this.yml = yml;
    
        super.itemLoad(yml);
        
        if(yml.contains("save-number")){
            this.saveNumber = yml.getInt("save-number");
            saveNumberMap.put(saveNumber, this);
        }
    }
    
    public int getSaveNumber() {return saveNumber;}
    
    
    public ItemStack getItemStack(Lang lang){
        ItemStack itemStack = new ItemStack(material);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(this.getDisplayName(lang));
        itemMeta.setCustomModelData(modelID);
        itemMeta.setLore(super.getDescription(lang));
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }
}
