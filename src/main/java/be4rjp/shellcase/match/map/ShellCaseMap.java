package be4rjp.shellcase.match.map;

import be4rjp.cinema4c.data.play.MovieData;
import be4rjp.cinema4c.player.PlayManager;
import be4rjp.shellcase.ShellCase;
import be4rjp.shellcase.language.Lang;
import be4rjp.shellcase.match.map.structure.MapStructure;
import be4rjp.shellcase.util.ConfigUtil;
import be4rjp.shellcase.util.SCLocation;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.*;

public abstract class ShellCaseMap {
    
    //IDとShellCaseMapのハッシュマップ
    private static final Map<String, ShellCaseMap> maps = new HashMap<>();
    //マップのリスト
    private static final List<ShellCaseMap> mapList = new ArrayList<>();
    
    public static void initialize(){
        maps.clear();
        ConquestMap.initialize();
    }
    
    /**
     * ShellCaseMapを取得する
     * @param id
     * @return ShellCaseMap
     */
    public static ShellCaseMap getShellCaseMap(String id){return maps.get(id);}
    
    /**
     * ランダムにマップを取得する
     * @return ShellCaseMap
     */
    public static ShellCaseMap getRandomMap(){return mapList.get(new Random().nextInt(mapList.size()));}
    
    
    
    public static void loadAllMap(){
        initialize();
        
        ShellCase.getPlugin().getLogger().info("Loading maps...");
        File dir = new File("plugins/ShellCase/map");
    
        dir.getParentFile().mkdir();
        dir.mkdir();
        File[] files = dir.listFiles();
        if(files.length == 0){
            ShellCase.getPlugin().saveResource("map/map.yml", false);
            files = dir.listFiles();
        }
    
        if(files != null) {
            for (File file : files) {
                ShellCase.getPlugin().getLogger().info(file.getName());
                String id = file.getName().replace(".yml", "");
                YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
                MapType mapType = MapType.valueOf(yml.getString("type"));
                ShellCaseMap ShellCaseMap = mapType.createMapInstance(id);
                if(ShellCaseMap == null) continue;
                
                ShellCaseMap.loadData(yml);
            }
        }
    }
    
    
    //識別ID
    protected final String id;
    
    protected ShellCaseMap(String id){
        this.id = id;
        maps.put(id, this);
        mapList.add(this);
    }
    
    public String getID() {return this.id;}
    
    //設定ファイル
    protected YamlConfiguration yml;
    //表示名
    protected HashMap<Lang, String> displayName = new HashMap<>();
    //待機場所
    protected SCLocation waitLocation;
    //チームのスポーン場所
    protected final List<SCLocation> teamLocations = new ArrayList<>();
    //マップ紹介ムービー
    protected MovieData introMovie = null;
    //リザルト用ムービー
    protected MovieData resultMovie = null;
    //建造物
    protected Set<MapStructure> mapStructures = new HashSet<>();
    //マップの範囲
    protected MapRange mapRange = null;
    //破壊可能ブロック
    protected Set<Material> breakableBlockTypes = new HashSet<>();
    
    
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
     * 試合の待機場所を取得する
     * @return SCLocation
     */
    public SCLocation getWaitSCLocation(){return this.waitLocation;}
    
    /**
     * 試合の待機場所を取得する
     * @return Location
     */
    public Location getWaitLocation(){return this.waitLocation.getBukkitLocation();}
    
    /**
     * チームのスポーン場所を取得する
     * @param teamNumber チームの番号
     * @return Location
     */
    public Location getTeamLocation(int teamNumber){return this.teamLocations.get(teamNumber).getBukkitLocation();}
    
    /**
     * 設定されているチームのスポーン場所の数を取得します
     * @return
     */
    public int getNumberOfTeamLocations(){return this.teamLocations.size();}

    /**
     * マップ範囲を取得します
     * @return
     */
    public MapRange getMapRange() {
        return mapRange;
    }

    /**
     * このマップのタイプを取得する
     * @return
     */
    public abstract MapType getType();
    
    
    /**
     * 設定ファイルからロードする
     * @param yml
     */
    public void loadData(YamlConfiguration yml){
        this.yml = yml;
    
        if(yml.contains("display-name")){
            for(String languageName : yml.getConfigurationSection("display-name").getKeys(false)){
                Lang lang = Lang.valueOf(languageName);
                String name = yml.getString("display-name." + languageName);
                this.displayName.put(lang, ChatColor.translateAlternateColorCodes('&', name));
            }
        }
        
        if(yml.contains("wait-location")) this.waitLocation = ConfigUtil.getSCLocationByString(yml.getString("wait-location"));
        
        if(yml.contains("team-spawn-locations")){
            for(String locString : yml.getStringList("team-spawn-locations")){
                this.teamLocations.add(ConfigUtil.getSCLocationByString(locString).add(0.5, 0.0, 0.5));
            }
        }

        if(yml.contains("intro-movie")){
            String movieName = yml.getString("intro-movie");
            this.introMovie = PlayManager.getMovieData(movieName);
            if(introMovie == null) throw new IllegalArgumentException("No movie data with the name '" + movieName + "' was found.");
        }

        if(yml.contains("result-movie")){
            String movieName = yml.getString("result-movie");
            this.resultMovie = PlayManager.getMovieData(movieName);
            if(resultMovie == null) throw new IllegalArgumentException("No movie data with the name '" + movieName + "' was found.");
        }
        
        if(yml.contains("structures")){
            for(String line : yml.getStringList("structures")){
                mapStructures.add(MapStructure.getMapStructure(line));
            }
        }

        if(yml.contains("map-range")){
            String worldName = yml.getString("map-range.world");
            String firstPosition = yml.getString("map-range.first-position");
            String secondPosition = yml.getString("map-range.second-position");

            this.mapRange = new MapRange(worldName, firstPosition, secondPosition);
        }
        
        if(yml.contains("breakable-types")){
            this.breakableBlockTypes = BreakableTypeManager.toMaterialSet(yml.getStringList("breakable-types"));
        }
        
        loadDetailsData();
    }
    
    
    /**
     * 各マップの詳細データを取得する
     */
    public abstract void loadDetailsData();

    /**
     * マップ紹介ムービーを取得する
     * @return MovieData
     */
    public MovieData getIntroMovie() {return introMovie;}

    /**
     * リザルト用ムービーを取得する
     * @return MovieData
     */
    public MovieData getResultMovie() {return resultMovie;}
    
    public Set<MapStructure> getMapStructures() {return mapStructures;}
    
    public Set<Material> getBreakableBlockTypes() {return breakableBlockTypes;}
    
    public enum MapType{
        NORMAL_PVP(ConquestMap.class);
        
        private final Class<? extends ShellCaseMap> clazz;
        
        MapType(Class<? extends ShellCaseMap> clazz){
            this.clazz = clazz;
        }
        
        public ShellCaseMap createMapInstance(String id){
            try{
                return clazz.getConstructor(String.class).newInstance(id);
            }catch (Exception e){e.printStackTrace();}
            return null;
        }
    }
}
