package be4rjp.shellcase.match.map;

import be4rjp.shellcase.map.CanvasBuffer;
import be4rjp.shellcase.map.CanvasData;
import be4rjp.shellcase.match.map.area.FlagArea;
import be4rjp.shellcase.util.ConfigUtil;
import be4rjp.shellcase.util.math.Vec2f;
import org.bukkit.util.Vector;

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
    
    
    //最大チケット数
    private int maxTicket = 1000;
    //占拠するエリア
    private Set<FlagArea> flagAreas = new HashSet<>();
    //マップのキャンバスデータ
    private CanvasData canvasData;
    
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
        if(yml.contains("max-ticket")) this.maxTicket = yml.getInt("max-ticket");
        
        if(yml.contains("flag-area")){
            for(String name : yml.getConfigurationSection("flag-area").getKeys(false)){
                String displayName = yml.getString("flag-area." + name + ".display-name");
                Vector firstPosition = ConfigUtil.getVectorByString(Objects.requireNonNull(yml.getString("flag-area." + name + ".first-position")));
                Vector secondPosition = ConfigUtil.getVectorByString(Objects.requireNonNull(yml.getString("flag-area." + name + ".second-position")));
                
                flagAreas.add(new FlagArea(displayName, firstPosition, secondPosition));
            }
        }
        
        if(yml.contains("canvas-data")){
            this.canvasData = CanvasData.getCanvasData(yml.getString("canvas-data"));
        }
    }
    
    public int getMaxTicket() {return maxTicket;}
    
    public Set<FlagArea> getFlagAreas() {return flagAreas;}
    
    public CanvasData getCanvasData() {return canvasData;}
}
