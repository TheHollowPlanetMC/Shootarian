package be4rjp.shellcase.weapon.recoil;

import org.bukkit.configuration.file.YamlConfiguration;

import java.util.*;

public class Recoil {

    private static final Map<String, Recoil> recoilPatternMap = new HashMap<>();

    public static Recoil getRecoil(String id){return recoilPatternMap.get(id);}


    //リコイルの識別名
    private final String id;
    //設定ファイル
    private YamlConfiguration yml;
    //リコイルパターンのリスト
    private List<RecoilPattern> recoilPatterns = new ArrayList<>();

    public Recoil(String id){
        this.id = id;
    }

    /**
     * ymlファイルからロードする
     * @param yml
     */
    public void loadData(YamlConfiguration yml) {
        this.yml = yml;

        if(yml.contains("patterns")){
            for(String name : yml.getConfigurationSection("patterns").getKeys(false)){
                List<String> lines = yml.getStringList("patterns." + name);
                recoilPatterns.add(new RecoilPattern(lines));
            }
        }
    }

    public RecoilPattern getRandomPattern(){return recoilPatterns.get(new Random().nextInt(recoilPatterns.size()));}
}
