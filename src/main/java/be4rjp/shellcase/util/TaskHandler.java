package be4rjp.shellcase.util;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import world.chiyogami.chiyogamilib.scheduler.WorldThreadRunnable;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class TaskHandler<T> extends BukkitRunnable {
    
    
    public static <U> CompletableFuture<U> supplySync(Supplier<U> supplier, JavaPlugin plugin){
        CompletableFuture<U> completableFuture = new CompletableFuture<>();
        new TaskHandler<>(completableFuture, supplier, false, plugin).runTask(plugin);
        
        return completableFuture;
    }
    
    public static <U> CompletableFuture<U> supplyAsync(Supplier<U> supplier, JavaPlugin plugin){
        CompletableFuture<U> completableFuture = new CompletableFuture<>();
        new TaskHandler<>(completableFuture, supplier, true, plugin).runTaskAsynchronously(plugin);
        
        return completableFuture;
    }
    
    public static void runSync(Runnable runnable, JavaPlugin plugin){
        Bukkit.getScheduler().runTask(plugin, runnable);
    }
    
    public static void runAsync(Runnable runnable, JavaPlugin plugin){
        Bukkit.getScheduler().runTaskAsynchronously(plugin, runnable);
    }
    
    public static void runWorldSync(Runnable runnable, World world, JavaPlugin plugin){
        new WorldThreadRunnable(world){
            @Override
            public void run() {
                runnable.run();
            }
        }.runTask(plugin);
    }
    
    
    private final CompletableFuture<T> completableFuture;
    private final Supplier<T> supplier;
    private final boolean isAsync;
    private final JavaPlugin plugin;
    
    private TaskHandler(CompletableFuture<T> completableFuture, Supplier<T> supplier, boolean isAsync, JavaPlugin plugin){
        this.completableFuture = completableFuture;
        this.supplier = supplier;
        this.isAsync = isAsync;
        this.plugin = plugin;
    }
    
    @Override
    public void run() {
        T result = supplier.get();
        
        Runnable runnable = () -> completableFuture.complete(result);
        if(isAsync){
            TaskHandler.runSync(runnable, plugin);
        }else{
            TaskHandler.runAsync(runnable, plugin);
        }
    }
}
