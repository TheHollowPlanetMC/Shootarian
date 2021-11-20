package be4rjp.shootarian.scheduler;

import be4rjp.shootarian.Shootarian;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.*;

public abstract class MultiThreadRunnable implements Runnable{
    
    private static ExecutorService executorService;

    private static final Set<MultiThreadRunnable> multiThreadRunnableSet = ConcurrentHashMap.newKeySet();

    public static void initialize(int threads){executorService = Executors.newFixedThreadPool(threads);}

    public static void onDisable(){executorService.shutdown();}
    
    private static synchronized void registerTask(MultiThreadRunnable multiThreadRunnable){multiThreadRunnableSet.add(multiThreadRunnable);}
    
    
    static {
        new BukkitRunnable(){
            @Override
            public void run() {
                Set<Future<?>> futures = new HashSet<>();
                for(MultiThreadRunnable multiThreadRunnable : multiThreadRunnableSet){
                    futures.add(executorService.submit(multiThreadRunnable));
                }

                for(Future<?> future : futures){
                    try {
                        future.get();
                    } catch (Exception e){e.printStackTrace();}
                }
            }
        }.runTaskTimer(Shootarian.getPlugin(), 0, 1);
    }
    

    
    @Override
    public abstract void run();
    
    public void runTaskTimer(){registerTask(this);}
    
    public synchronized void cancel(){multiThreadRunnableSet.remove(this);}
}
