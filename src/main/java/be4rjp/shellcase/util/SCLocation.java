package be4rjp.shellcase.util;

import be4rjp.shellcase.ShellCase;
import com.grinderwolf.swm.api.SlimePlugin;
import com.grinderwolf.swm.api.loaders.SlimeLoader;
import com.grinderwolf.swm.api.world.SlimeWorld;
import com.grinderwolf.swm.api.world.properties.SlimePropertyMap;
import org.bukkit.Bukkit;
import org.bukkit.Location;

public class SCLocation {
    
    private final String worldName;
    
    private double x;
    private double y;
    private double z;
    
    private float yaw;
    private float pitch;
    
    private SlimeWorld slimeWorld = null;
    
    public SCLocation(String worldName, double x, double y, double z, float yaw, float pitch){
        this.worldName = worldName;
        this.x = x;
        this.y = y;
        this.z = z;
        
        this.yaw = yaw;
        this.pitch = pitch;
    }
    
    public SCLocation add(double x, double y, double z){
        this.x += x;
        this.y += y;
        this.z += z;
        
        return this;
    }
    
    public Location getBukkitLocation(){return new Location(Bukkit.getWorld(worldName), x, y, z, yaw, pitch);}
    
    public void loadSlimeWorld() throws Exception{
        SlimePlugin slimePlugin = ShellCase.getSlimePlugin();
        SlimeLoader file = slimePlugin.getLoader("file");
        SlimePropertyMap properties = new SlimePropertyMap();
        
        this.slimeWorld = slimePlugin.loadWorld(file, worldName, true, properties);
    }
    
    public void createWorldAtMainThread(){
        Bukkit.getServer().unloadWorld(worldName, false);
        
        SlimePlugin slimePlugin = ShellCase.getSlimePlugin();
        slimePlugin.generateWorld(slimeWorld);
    }
}
