package be4rjp.shootarian.match.map;

import be4rjp.parallel.util.ChunkPosition;
import be4rjp.shootarian.util.ConfigUtil;
import be4rjp.shootarian.util.SCLocation;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Set;

public class MapRange {

    private final SCLocation firstLocation;

    private final SCLocation secondLocation;

    private final Set<ChunkPosition> chunkPositions;

    public MapRange(String worldName, String firstLocationString, String secondLocationString){
        Vector firstVector = ConfigUtil.getVectorByString(firstLocationString);
        Vector secondVector = ConfigUtil.getVectorByString(secondLocationString);

        Vector maximum = Vector.getMaximum(firstVector, secondVector);
        Vector minimum = Vector.getMinimum(firstVector, secondVector);

        this.firstLocation = new SCLocation(worldName, minimum.getX(), minimum.getY(), minimum.getZ(), 0F, 0F);
        this.secondLocation = new SCLocation(worldName, maximum.getX(), maximum.getY(), maximum.getZ(), 0F, 0F);

        chunkPositions = new HashSet<>();
        for(int x = (int) firstLocation.getX(); x < (int) secondLocation.getX(); x+=16){
            for(int z = (int) firstLocation.getZ(); z < (int) secondLocation.getZ(); z+=16) {
                chunkPositions.add(new ChunkPosition(x, z));
            }
        }
    }

    public SCLocation getFirstLocation() {return firstLocation;}

    public SCLocation getSecondLocation() {return secondLocation;}

    public Set<ChunkPosition> getChunkPositions(){
        return chunkPositions;
    }
}
