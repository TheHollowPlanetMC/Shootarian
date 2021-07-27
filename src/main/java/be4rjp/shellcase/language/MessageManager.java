package be4rjp.shellcase.language;

import be4rjp.shellcase.ShellCase;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class MessageManager {
    
    //言語別テキストのマップ
    private static final Map<Lang, Map<String, String>> languageMap = new HashMap<>();
    
    
    /**
     * plugins/shellcase/message.ymlに記述されている言語別テキストをロードします。
     */
    public static void loadAllMessage(){
        languageMap.clear();
        ShellCase.getPlugin().saveResource("message.yml", false);
        File file = new File("plugins/shellcase/message.yml");
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
        
        for(String languageName : yml.getKeys(false)){
            Lang lang = Lang.valueOf(languageName);
            languageMap.put(lang, new HashMap<>());
            for(String name : yml.getConfigurationSection(languageName).getKeys(false)){
                String message = yml.getString(lang + "." + name);
                languageMap.get(lang).put(name, message);
            }
        }
    }
    
    
    /**
     * 言語別テキストを取得します
     * @param lang 言語
     * @param textName テキストの名前
     * @return String テキスト
     */
    public static String getText(Lang lang, String textName){
        if(textName == null) return "";
        
        Map<String, String> textMap = languageMap.get(lang);
        if(textMap == null){
            ShellCase.getPlugin().getLogger().warning("The specified language was not found.");
            ShellCase.getPlugin().getLogger().warning("Language: " + lang);
            return "";
        }
        
        String text = textMap.get(textName);
        if(text == null){
            ShellCase.getPlugin().getLogger().warning("The specified text was not found.");
            ShellCase.getPlugin().getLogger().warning("TextName: " + textName);
            return "";
        }else{
            text = ChatColor.translateAlternateColorCodes('&', text);
            return text;
        }
    }
}
