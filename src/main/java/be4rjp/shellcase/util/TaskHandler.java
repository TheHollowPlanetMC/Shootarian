package be4rjp.shellcase.util;

import be4rjp.shellcase.ShellCase;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;
import world.chiyogami.chiyogamilib.scheduler.WorldThreadRunnable;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class TaskHandler<T> extends BukkitRunnable {
    
    
    public static <U> CompletableFuture<U> supplySync(Supplier<U> supplier){
        CompletableFuture<U> completableFuture = new CompletableFuture<>();
        new TaskHandler<>(completableFuture, supplier, false).runTask(ShellCase.getPlugin());
        
        return completableFuture;
    }
    
    public static <U> CompletableFuture<U> supplyAsync(Supplier<U> supplier){
        CompletableFuture<U> completableFuture = new CompletableFuture<>();
        new TaskHandler<>(completableFuture, supplier, true).runTaskAsynchronously(ShellCase.getPlugin());
        
        return completableFuture;
    }
    
    public static void runSync(Runnable runnable){
        Bukkit.getScheduler().runTask(ShellCase.getPlugin(), runnable);
    }
    
    public static void runAsync(Runnable runnable){
        Bukkit.getScheduler().runTaskAsynchronously(ShellCase.getPlugin(), runnable);
    }
    
    public static void runAsyncImmediately(Runnable runnable){
        Thread thread = new Thread(runnable);
        thread.setDaemon(true);
        thread.start();
    }
    
    public static void runWorldSync(World world, Runnable runnable){
        new WorldThreadRunnable(world){
            @Override
            public void run() {
                runnable.run();
            }
        }.runTask(ShellCase.getPlugin());
    }
    
    
    private final CompletableFuture<T> completableFuture;
    private final Supplier<T> supplier;
    private final boolean isAsync;
    
    private TaskHandler(CompletableFuture<T> completableFuture, Supplier<T> supplier, boolean isAsync){
        this.completableFuture = completableFuture;
        this.supplier = supplier;
        this.isAsync = isAsync;
    }
    
    @Override
    public void run() {
        T result = supplier.get();
        
        Runnable runnable = () -> completableFuture.complete(result);
        if(isAsync){
            TaskHandler.runSync(runnable);
        }else{
            TaskHandler.runAsync(runnable);
        }
    }
}
