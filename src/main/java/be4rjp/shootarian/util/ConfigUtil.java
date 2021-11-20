package be4rjp.shootarian.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.util.Vector;

public class ConfigUtil {
    
    /**
     * 文字列を座標に変換する
     * ' world, x, y, z 'か' world, x, y, z, yaw, pitch 'の形式である必要があります
     * @param locString
     * @return
     */
    public static Location getLocationByString(String locString){
        locString = locString.replace(" ", "");
        String[] args = locString.split(",");
        
        if(args.length != 4 && args.length != 6){
            throw new IllegalArgumentException("Location must be in the format 'world, x, y, z' or 'world, x, y, z, yaw, pitch'.");
        }
        
        String worldName = args[0];
        World world = Bukkit.getWorld(worldName);
        if(world == null) Bukkit.createWorld(new WorldCreator(worldName));
        
        double x = Double.parseDouble(args[1]);
        double y = Double.parseDouble(args[2]);
        double z = Double.parseDouble(args[3]);
        
        float yaw = 0.0F;
        float pitch = 0.0F;
        if(args.length == 6){
            yaw = Float.parseFloat(args[4]);
            pitch = Float.parseFloat(args[5]);
        }
        
        return new Location(world, x, y, z, yaw, pitch);
    }
    
    /**
     * 文字列を座標に変換する
     * ' world, x, y, z 'か' world, x, y, z, yaw, pitch 'の形式である必要があります
     * @param locString
     * @return
     */
    public static SCLocation getSCLocationByString(String locString){
        locString = locString.replace(" ", "");
        String[] args = locString.split(",");
        
        if(args.length != 4 && args.length != 6){
            throw new IllegalArgumentException("Location must be in the format 'world, x, y, z' or 'world, x, y, z, yaw, pitch'.");
        }
        
        String worldName = args[0];
        
        double x = Double.parseDouble(args[1]);
        double y = Double.parseDouble(args[2]);
        double z = Double.parseDouble(args[3]);
        
        float yaw = 0.0F;
        float pitch = 0.0F;
        if(args.length == 6){
            yaw = Float.parseFloat(args[4]);
            pitch = Float.parseFloat(args[5]);
        }
        
        return new SCLocation(worldName, x, y, z, yaw, pitch);
    }
    
    /**
     * 文字列をVectorに変換する
     * 'x, y, z'の形式である必要があります
     * @param vecString
     * @return Vector
     */
    public static Vector getVectorByString(String vecString){
        String[] args = vecString.replace(" ", "").split(",");
        
        if(args.length != 3){
            throw new IllegalArgumentException("Vector must be in the format 'x, y, z'.");
        }
        
        double x = Double.parseDouble(args[0]);
        double y = Double.parseDouble(args[1]);
        double z = Double.parseDouble(args[2]);
        
        return new Vector(x, y, z);
    }
}
