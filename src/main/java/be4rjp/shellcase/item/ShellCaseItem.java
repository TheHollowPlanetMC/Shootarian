package be4rjp.shellcase.item;

import be4rjp.shellcase.ShellCase;
import be4rjp.shellcase.language.Lang;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.*;

public abstract class ShellCaseItem {
    
    //武器の表示名
    protected Map<Lang, String> displayName = new HashMap<>();
    //武器のマテリアル
    protected Material material = Material.BARRIER;
    //CustomModelDataのID
    protected int modelID = 0;
    //Lore
    protected Map<Lang, List<String>> langLoreMap = new HashMap<>();
    
    
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
    
    /**
     * プレイヤーに表示する説明文(Lore)を取得します
     * @param language 言語(ja_JP等)
     * @return List<String> 説明文(Lore)
     */
    public List<String> getDescription(Lang language){
        List<String> description = langLoreMap.get(language);
        if(description == null){
            return new ArrayList<>();
        }else{
            return description;
        }
    }
    
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
        if(yml.contains("description")) {
            for(String languageName : yml.getConfigurationSection("description").getKeys(false)){
                Lang language = Lang.valueOf(languageName);
                List<String> lines = new ArrayList<>();
                for(String line : yml.getStringList("description." + language)){
                    lines.add(ChatColor.translateAlternateColorCodes('&', line));
                }
                this.langLoreMap.put(language, lines);
            }
        }
    }
}
