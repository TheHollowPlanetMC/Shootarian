package be4rjp.shellcase.item;

import be4rjp.shellcase.language.Lang;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public abstract class ShellCaseItem {
    
    //武器の表示名
    protected Map<Lang, String> displayName = new HashMap<>();
    //武器のマテリアル
    protected Material material = Material.BARRIER;
    //CustomModelDataのID
    protected int modelID = 0;
    
    
    /**
     * 表示名を取得する
     * @return String
     */
    public String getDisplayName(Lang lang) {
        String name = displayName.get(lang);
        if(name == null){
            return "No name.";
        }else{
            return name;
        }
    }
    
    /**
     * マテリアルを取得する
     * @return Material
     */
    public Material getMaterial() {return material;}
    
    /**
     * CustomModelDataのIDを取得する
     * @return int
     */
    public int getModelID() {return modelID;}
    
    public void itemLoad(YamlConfiguration yml){
        if(yml.contains("display-name")){
            for(String languageName : yml.getConfigurationSection("display-name").getKeys(false)){
                Lang lang = Lang.valueOf(languageName);
                String name = yml.getString("display-name." + languageName);
                this.displayName.put(lang, ChatColor.translateAlternateColorCodes('&', name));
            }
        }
    
        if(yml.contains("material")) this.material = Material.getMaterial(Objects.requireNonNull(yml.getString("material")));
        if(yml.contains("custom-model-data")) this.modelID = yml.getInt("custom-model-data");
    }
}
