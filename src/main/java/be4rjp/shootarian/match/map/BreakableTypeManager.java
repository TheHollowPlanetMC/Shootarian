package be4rjp.shootarian.match.map;

import org.bukkit.Material;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BreakableTypeManager {
    
    public static Set<Material> toMaterialSet(List<String> lines){
        
        Set<Material> materials = new HashSet<>();
        
        for(String line : lines){
            for(Material material : Material.values()){
                if(material.toString().matches(line)){
                    materials.add(material);
                }
            }
        }
        
        return materials;
    }
    
}
