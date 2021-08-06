package be4rjp.shellcase.match.map;

import be4rjp.shellcase.match.map.area.FlagArea;

import java.util.*;

public class ConquestMap extends ShellCaseMap{
    
    //全てのPVPマップのリスト
    private static List<ConquestMap> conquestMaps = new ArrayList<>();
    
    /**
     * ランダムにコンクエストマップを取得する
     * @return
     */
    public static ConquestMap getRandomConquestMap(){return conquestMaps.get(new Random().nextInt(conquestMaps.size()));}
    
    public static void initialize(){
        conquestMaps.clear();}
    
    
    
    //占拠するエリア
    private Set<FlagArea> flagAreas = new HashSet<>();
    
    public ConquestMap(String id) {
        super(id);
        conquestMaps.add(this);
    }
    
    @Override
    public MapType getType() {
        return MapType.NORMAL_PVP;
    }
    
    @Override
    public void loadDetailsData() {
        if(yml.contains("flag-area")){
            for(String name : yml.getConfigurationSection("flag-area").getKeys(false)){
            
            }
        }
    }
}
