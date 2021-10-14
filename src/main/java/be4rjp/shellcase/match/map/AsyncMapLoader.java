package be4rjp.shellcase.match.map;

import be4rjp.parallel.util.ChunkPosition;
import be4rjp.shellcase.ShellCase;
import be4rjp.shellcase.util.TaskHandler;
import org.bukkit.Bukkit;
import org.bukkit.World;
import world.chiyogami.chiyogamilib.scheduler.WorldThreadRunnable;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;

public class AsyncMapLoader extends WorldThreadRunnable {

    private static int CPUS = Runtime.getRuntime().availableProcessors();


    private final MapRange mapRange;

    private final Queue<ChunkPosition> queue;

    private final int maxLoad;

    private final CompletableFuture<Void> completableFuture;

    private int load;

    private AsyncMapLoader(MapRange mapRange, CompletableFuture<Void> completableFuture){
        super(mapRange.getFirstLocation().getBukkitLocation().getWorld());
        this.mapRange = mapRange;
        this.completableFuture = completableFuture;
        this.queue = new ArrayDeque<>();
        this.queue.addAll(mapRange.getChunkPositions());
        this.maxLoad = mapRange.getChunkPositions().size();
    }

    @Override
    public void run() {
        World world = mapRange.getFirstLocation().getBukkitLocation().getWorld();
    
        if(queue.size() == 0){
            if(load == maxLoad){
                completableFuture.complete(null);
                cancel();
            }
            return;
        }
        
        for(int i = 0; i < CPUS; i++){
            ChunkPosition chunkPosition = queue.poll();
            if(chunkPosition == null){
                return;
            }
    
            world.getChunkAtAsync(chunkPosition.x, chunkPosition.z).thenAccept(chunk -> {
                chunk.setForceLoaded(true);
                AsyncMapLoader.this.load++;
            });
        }
    }

    public int getMaxLoad() {return maxLoad;}

    public int getLoadedTaskCount() {return load;}

    public CompletableFuture<Void> getCompletableFuture() {return completableFuture;}

    public static AsyncMapLoader startLoad(MapRange mapRange){
        CompletableFuture<Void> completableFuture = new CompletableFuture<>();
        AsyncMapLoader asyncMapLoader = new AsyncMapLoader(mapRange, completableFuture);

        TaskHandler.runAsync(() -> {
            try{
                mapRange.getFirstLocation().loadSlimeWorld();
            } catch (Exception e){e.printStackTrace();}

            TaskHandler.runSync(() -> {
                mapRange.getFirstLocation().createWorldAtMainThread();
                asyncMapLoader.setWorld(mapRange.getFirstLocation().getBukkitLocation().getWorld());
                asyncMapLoader.runTaskTimer(ShellCase.getPlugin(), 0, 10);
            });
        });

        return asyncMapLoader;
    }
}
