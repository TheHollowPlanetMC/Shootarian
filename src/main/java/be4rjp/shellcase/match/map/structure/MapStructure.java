package be4rjp.shellcase.match.map.structure;

import be4rjp.parallel.structure.ParallelStructure;
import be4rjp.parallel.structure.StructureData;
import be4rjp.shellcase.ShellCase;
import be4rjp.shellcase.util.ConfigUtil;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.util.Vector;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class MapStructure {
    
    private static final Map<String, MapStructure> mapStructureMap = new HashMap<>();
    
    public static void initialize(){
        mapStructureMap.clear();
    }
    
    public static MapStructure getMapStructure(String id){return mapStructureMap.get(id);}
    
    
    public static void loadAllMapStructure() {
        initialize();
    
        ShellCase.getPlugin().getLogger().info("Loading maps...");
        File dir = new File("plugins/ShellCase/structure");
    
        dir.getParentFile().mkdir();
        dir.mkdir();
        File[] files = dir.listFiles();
        if (files.length == 0) {
            ShellCase.getPlugin().saveResource("structure/structure-1.yml", false);
            files = dir.listFiles();
        }
    
        if (files != null) {
            for (File file : files) {
                ShellCase.getPlugin().getLogger().info(file.getName());
                String id = file.getName().replace(".yml", "");
                YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
                MapStructure mapStructure = new MapStructure(id);
                mapStructure.loadData(yml);
            }
        }
    }
    
    
    
    
    private final String id;
    
    private YamlConfiguration yml;
    
    private ParallelStructure structure = null;
    
    private Vector boundingBoxSize = new Vector(0.0, 0.0, 0.0);
    
    private StructureData defaultData = null;
    
    private StructureData collapseData = null;
    
    private int maxHealth = 100;
    
    
    public MapStructure(String id){
        this.id = id;
        mapStructureMap.put(id, this);
    }
    
    
    public void loadData(YamlConfiguration yml){
        this.yml = yml;
        
        if(yml.contains("base-location")){
            Location baseLocation = ConfigUtil.getLocationByString(yml.getString("base-location"));
            this.structure = new ParallelStructure("SCStructure");
            structure.setBaseLocation(baseLocation);
        }
        
        if(yml.contains("bounding-box-size")){
            String line = yml.getString("bounding-box-size");
            String[] args = line.replace(" ", "").split(",");
            
            double x = Double.parseDouble(args[0]);
            double y = Double.parseDouble(args[1]);
            double z = Double.parseDouble(args[2]);
            this.boundingBoxSize = new Vector(x, y, z);
        }
        
        if(yml.contains("default-data")) this.defaultData = StructureData.getStructureData(yml.getString("default-data"));
        if(yml.contains("collapse-data")) this.collapseData = StructureData.getStructureData(yml.getString("collapse-data"));
        if(yml.contains("max-health")) this.maxHealth = yml.getInt("max-health");
    }
    
    public String getID() {return id;}
    
    public ParallelStructure getStructure() {return structure;}
    
    public Vector getBoundingBoxSize() {return boundingBoxSize;}
    
    public int getMaxHealth() {return maxHealth;}
    
    public StructureData getCollapseData() {return collapseData;}
    
    public StructureData getDefaultData() {return defaultData;}
}
