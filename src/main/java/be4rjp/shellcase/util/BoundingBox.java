package be4rjp.shellcase.util;

import net.minecraft.server.v1_15_R1.AxisAlignedBB;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftEntity;

public class BoundingBox {
    
    Vector max;
    Vector min;
    
    public BoundingBox(Vector firstPoint, Vector secondPoint) {
        max = Vector.getMaximum(firstPoint, secondPoint);
        min = Vector.getMinimum(firstPoint, secondPoint);
    }
    
    
    public BoundingBox(Entity entity, double plus){
        AxisAlignedBB bb = ((CraftEntity) entity).getHandle().getBoundingBox();
        min = new Vector(bb.minX - plus, bb.minY, bb.minZ - plus);
        max = new Vector(bb.maxX + plus, bb.maxY, bb.maxZ + plus);
    }
    
    public BoundingBox (AxisAlignedBB bb){
        min = new Vector(bb.minX, bb.minY, bb.minZ);
        max = new Vector(bb.maxX, bb.maxY, bb.maxZ);
    }
    
    public Vector midPoint(){
        return max.clone().add(min).multiply(0.5);
    }
    
    public boolean isInBox(Vector position){
        if(min.getX() > position.getX()) return false;
        if(min.getY() > position.getY()) return false;
        if(min.getZ() > position.getZ()) return false;
        if(position.getX() > max.getX()) return false;
        if(position.getY() > max.getY()) return false;
        if(position.getZ() > max.getZ()) return false;
        
        return true;
    }
    
    public Vector getMax() {return max;}
    
    public Vector getMin() {return min;}
}
