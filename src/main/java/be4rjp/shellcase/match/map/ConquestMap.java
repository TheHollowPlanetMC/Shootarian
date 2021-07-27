package be4rjp.shellcase.match.map;

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

    }
}
