package be4rjp.shootarian.weapon.attachment;

import be4rjp.shootarian.language.Lang;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Objects;

public class Sight extends Attachment{
    
    private String displayName = "NULL";
    
    private float fov = 0.1F;
    
    private Material sightMaterial = Material.BARRIER;
    
    private int sightModelID = 0;
    
    private boolean hasSightItem = false;
    
    
    public Sight(String id) {
        super(id);
    }
    
    @Override
    public void loadDetailsData() {
        if(yml.contains("field-of-view")) this.fov = (float) yml.getDouble("field-of-view");
        if(yml.contains("sight.material")){
            this.sightMaterial = Material.getMaterial(Objects.requireNonNull(yml.getString("sight.material")));
            this.hasSightItem = true;
        }
        if(yml.contains("sight.custom-model-data")) this.sightModelID = yml.getInt("sight.custom-model-data");
    }
    
    public float getFOV() {return fov;}
    
    
    public ItemStack getSightItemStack(Lang lang){
        ItemStack itemStack = new ItemStack(sightMaterial);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(this.getDisplayName(lang));
        itemMeta.setCustomModelData(sightModelID);
        itemStack.setItemMeta(itemMeta);
    
        return itemStack;
    }
    
    public boolean isHasSightItem() {return hasSightItem;}
}
