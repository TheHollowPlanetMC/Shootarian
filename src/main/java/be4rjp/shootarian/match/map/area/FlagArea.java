package be4rjp.shootarian.match.map.area;

import be4rjp.shootarian.util.BoundingBox;
import org.bukkit.util.Vector;

public class FlagArea {
    
    private final String displayName;
    
    private final BoundingBox boundingBox;
    
    public FlagArea(String displayName, Vector first, Vector second){
        this.displayName = displayName;
        this.boundingBox = new BoundingBox(first, second);
    }
    
    public String getDisplayName() {return displayName;}
    
    public BoundingBox getBoundingBox() {return boundingBox;}
}
