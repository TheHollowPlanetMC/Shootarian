package be4rjp.shootarian.scheduler;

import be4rjp.shootarian.Shootarian;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

public abstract class MultiThreadRunnable implements Runnable{
    
    private static ExecutorService executorService;

    private static final Set<MultiThreadRunnable> multiThreadRunnableSet = ConcurrentHashMap.newKeySet();
    
    private static final Set<Runnable> mainThreadTasks = new HashSet<>();
    
    private static final ReentrantLock MAIN_THREAD_TASK_LOCK = new ReentrantLock(true);
    
    public static void addMainThreadTask(Runnable runnable){
        MAIN_THREAD_TASK_LOCK.lock();
        try{
            mainThreadTasks.add(runnable);
        }finally {
            MAIN_THREAD_TASK_LOCK.unlock();
        }
    }

    public static void initialize(int threads){executorService = Executors.newFixedThreadPool(threads);}

    public static void onDisable(){executorService.shutdown();}
    
    private static synchronized void registerTask(MultiThreadRunnable multiThreadRunnable){multiThreadRunnableSet.add(multiThreadRunnable);}
    
    
    static {
        Bukkit.getScheduler().runTaskTimer(Shootarian.getPlugin(), () -> {
            Set<Future<?>> futures = new HashSet<>();
            for(MultiThreadRunnable multiThreadRunnable : multiThreadRunnableSet){
                futures.add(executorService.submit(multiThreadRunnable));
            }
    
            for(Future<?> future : futures){
                try {
                    future.get();
                } catch (Exception e){e.printStackTrace();}
            }
            
            MAIN_THREAD_TASK_LOCK.lock();
            try {
                for (Runnable runnable : mainThreadTasks) {
                    try {
                        runnable.run();
                    }catch (Exception e){e.printStackTrace();}
                }
                mainThreadTasks.clear();
            }finally {
                MAIN_THREAD_TASK_LOCK.unlock();
            }
        }, 0, 1);
    }
    

    
    @Override
    public abstract void run();
    
    public void runTaskTimer(){registerTask(this);}
    
    public synchronized void cancel(){multiThreadRunnableSet.remove(this);}
}
