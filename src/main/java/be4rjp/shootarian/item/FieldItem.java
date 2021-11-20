package be4rjp.shootarian.item;

import be4rjp.shootarian.Shootarian;
import be4rjp.shootarian.language.Lang;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Item;
import org.bukkit.util.Vector;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public abstract class FieldItem extends ShootarianItem {
    
    private static Map<String, FieldItem> fieldItemMap = new HashMap<>();
    
    public static void loadAllFieldItem(){
        Shootarian.getPlugin().getLogger().info("Loading weapons...");
        File dir = new File("plugins/Shootarian/weapon");
    
        dir.getParentFile().mkdir();
        dir.mkdir();
        File[] files = dir.listFiles();
        if(files.length == 0){
            Shootarian.getPlugin().saveResource("weapon/scar-h.yml", false);
            files = dir.listFiles();
        }
    
        if(files != null) {
            for (File file : files) {
                Shootarian.getPlugin().getLogger().info(file.getName());
                String id = file.getName().replace(".yml", "");
                try {
                    YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
                    ItemType itemType = ItemType.valueOf(yml.getString("type"));
                }catch (Exception e){e.printStackTrace();}
            }
        }
    }
    
    
    private static final Vector ZERO = new Vector(0.0, 0.0, 0.0);
    
    /**
     * アイテムをドロップする
     * @param location Location
     * @param velocity Velocity
     * @return ドロップしたアイテムエンティティ
     */
    public Item drop(Location location, Vector velocity){
        Item item = location.getWorld().dropItem(location, super.getItemStack(Lang.ja_JP));
        item.setVelocity(velocity);
        return item;
    }
    
    /**
     * アイテムをドロップする
     * @param location Location
     * @return ドロップしたアイテムエンティティ
     */
    public Item drop(Location location){
        return this.drop(location, ZERO);
    }
    
    public abstract ItemType getItemType();
    
    
    
    public enum ItemType{
        NORMAL,
        SCRIPT
    }
}
