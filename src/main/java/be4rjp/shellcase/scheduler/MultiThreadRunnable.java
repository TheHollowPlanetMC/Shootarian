package be4rjp.shellcase.scheduler;

import be4rjp.shellcase.ShellCase;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public abstract class MultiThreadRunnable implements Runnable{
    
    private static final int CPUS = Runtime.getRuntime().availableProcessors();
    
    private static final Map<Integer, Set<MultiThreadRunnable>> multiThreadRunnableMap = new ConcurrentHashMap<>();
    
    private static int cpuIndex = 0;
    
    private static synchronized void registerTask(MultiThreadRunnable multiThreadRunnable){
        int index = cpuIndex % CPUS;
        multiThreadRunnableMap.computeIfAbsent(index, k -> ConcurrentHashMap.newKeySet()).add(multiThreadRunnable);
        multiThreadRunnable.CPU_INDEX = index;
        cpuIndex++;
    }
    
    
    
    static {
        new BukkitRunnable(){
            @Override
            public void run() {
                Thread[] threads = new Thread[CPUS];
                for(int index = 0; index < CPUS; index++){
                    Set<MultiThreadRunnable> multiThreadRunnableSet = multiThreadRunnableMap.get(index);
                    if(multiThreadRunnableSet == null) return;
                    
                    Thread thread = new Thread(() -> multiThreadRunnableSet.forEach(MultiThreadRunnable::run));
                    thread.start();
                    threads[index] = thread;
                }
                
                for(Thread thread : threads){
                    try {
                        thread.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.runTaskTimer(ShellCase.getPlugin(), 0, 1);
    }
    
    
    
    private int CPU_INDEX = 0;
    
    @Override
    public abstract void run();
    
    public void runTaskTimer(){
        registerTask(this);
    }
    
    
    public synchronized void cancel(){
        Set<MultiThreadRunnable> multiThreadRunnableSet = multiThreadRunnableMap.get(CPU_INDEX);
        if(multiThreadRunnableSet == null) return;
        
        multiThreadRunnableSet.remove(this);
    }
}
