package be4rjp.shellcase.util;

import be4rjp.shellcase.ShellCase;
import be4rjp.shellcase.world.AsyncWorld;
import com.grinderwolf.swm.api.SlimePlugin;
import com.grinderwolf.swm.api.loaders.SlimeLoader;
import com.grinderwolf.swm.api.world.SlimeWorld;
import com.grinderwolf.swm.api.world.properties.SlimePropertyMap;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

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

    public double getX() {return x;}

    public double getY() {return y;}

    public double getZ() {return z;}

    public String getWorldName() {return worldName;}

    public float getPitch() {return pitch;}

    public float getYaw() {return yaw;}

    public void setX(double x) {this.x = x;}

    public void setY(double y) {this.y = y;}

    public void setZ(double z) {this.z = z;}

    public void setYaw(float yaw) {this.yaw = yaw;}

    public void setPitch(float pitch) {this.pitch = pitch;}


    public Location getBukkitLocation(){return new Location(Bukkit.getWorld(worldName), x, y, z, yaw, pitch);}
    
    public void loadSlimeWorld() throws Exception{
        SlimePlugin slimePlugin = ShellCase.getSlimePlugin();
        SlimeLoader file = slimePlugin.getLoader("file");
        SlimePropertyMap properties = new SlimePropertyMap();
        
        this.slimeWorld = slimePlugin.loadWorld(file, worldName, true, properties);
    }
    
    public void createWorldAtMainThread(){
        World world = Bukkit.getWorld(worldName);
        if(world != null) {
            Bukkit.getServer().unloadWorld(world, false);
        }
        
        SlimePlugin slimePlugin = ShellCase.getSlimePlugin();
        slimePlugin.generateWorld(slimeWorld);
    }
}
