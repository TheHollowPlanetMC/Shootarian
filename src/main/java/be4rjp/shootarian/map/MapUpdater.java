package be4rjp.shootarian.map;

import java.lang.reflect.Field;
import java.util.UUID;

import org.bukkit.World;
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_15_R1.map.CraftMapView;
import org.bukkit.map.MapView;

import com.mojang.authlib.GameProfile;

import net.minecraft.server.v1_15_R1.EntityHuman;
import net.minecraft.server.v1_15_R1.ItemWorldMap;
import net.minecraft.server.v1_15_R1.Items;
import net.minecraft.server.v1_15_R1.WorldMap;

public final class MapUpdater extends EntityHuman {
    public static final UUID ID = UUID.randomUUID();
    public static final String NAME = "_______MAP_UPDATER_______";
    
    public MapUpdater(World world) {
        super(((CraftWorld) world).getHandle(), new GameProfile(ID, NAME));
    }
    
    @Override
    public boolean isCreative() {
        return false;
    }
    
    @Override
    public boolean isSpectator() {
        return false;
    }
    
    public void update(MapView mapView) {
        if (mapView == null) {
            throw new IllegalArgumentException("mapView cannot be null");
        }
        
        if (((CraftWorld) mapView.getWorld()).getHandle() != world) {
            throw new IllegalArgumentException("world of mapView cannot be different");
        }
        
        try {
            Field field = CraftMapView.class.getDeclaredField("worldMap");
            field.setAccessible(true);
            WorldMap worldMap = (WorldMap) field.get(mapView);
            int size = 128 << worldMap.scale;
            
            for (int x = worldMap.centerX - size / 2; x <= worldMap.centerX + size / 2; x += 16) {
                for (int z = worldMap.centerZ - size / 2; z <= worldMap.centerZ + size / 2; z += 16) {
                    setPosition(x, 0.0, z);
                    ((ItemWorldMap) Items.FILLED_MAP).a(world, this, worldMap);
                }
            }
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
