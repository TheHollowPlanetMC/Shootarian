package be4rjp.shootarian.map;

import be4rjp.shootarian.Shootarian;
import be4rjp.shootarian.util.Position2i;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.libs.org.apache.commons.codec.DecoderException;
import org.bukkit.craftbukkit.libs.org.apache.commons.codec.binary.Hex;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CanvasData {
    
    private static final Map<String, CanvasData> canvasDataMap = new HashMap<>();
    
    public static CanvasData getCanvasData(String id){return canvasDataMap.get(id);}
    
    public static void loadAllCanvas() {
        Shootarian.getPlugin().getLogger().info("Loading canvas...");
        File dir = new File("plugins/Shootarian/canvas");
    
        dir.getParentFile().mkdir();
        dir.mkdir();
        File[] files = dir.listFiles();
    
        if (files != null) {
            for (File file : files) {
                Shootarian.getPlugin().getLogger().info(file.getName());
                String id = file.getName().replace(".yml", "");
                YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
                CanvasData canvasData = new CanvasData(id);
                canvasData.load(yml);
            }
        }
    }
    
    
    //iD
    private final String id;
    //Config
    private YamlConfiguration yml;
    //buffer
    private byte[] bytes;
    //scale
    private byte scale = (byte) 0;
    //centerX
    private int centerX = 0;
    //centerZ
    private int centerZ = 0;
    
    public CanvasData(String id){
        this.id = id;
        canvasDataMap.put(id, this);
    }
    
    public void load(YamlConfiguration yml){
        this.yml = yml;
        
        if(yml.contains("bytes")) {
            try {
                this.bytes = Hex.decodeHex(Objects.requireNonNull(yml.getString("bytes")).toCharArray());
            } catch (DecoderException e) {
                e.printStackTrace();
            }
        }
        if(yml.contains("scale")) this.scale = (byte) yml.getInt("scale");
        if(yml.contains("center")){
            String[] args = Objects.requireNonNull(yml.getString("center")).replace(" ", "").split(",");
            this.centerX = Integer.parseInt(args[0]);
            this.centerZ = Integer.parseInt(args[1]);
        }
    }
    
    public byte[] getBytes() {return bytes;}
    
    public byte getScale() {return scale;}
    
    public int getCenterX() {return centerX;}
    
    public int getCenterZ() {return centerZ;}
    
    public Position2i locationToPixel(int x, int z){
        int fromCenterX = x - centerX;
        int fromCenterZ = z - centerZ;
        
        int pixelX = fromCenterX >> scale;
        int pixelZ = fromCenterZ >> scale;
    
        pixelX += 64;
        pixelZ += 64;
        
        pixelX = Math.min(pixelX, 127);
        pixelX = Math.max(pixelX, 0);
        pixelZ = Math.min(pixelZ, 127);
        pixelZ = Math.max(pixelZ, 0);
        
        return new Position2i(pixelX, pixelZ);
    }
}
