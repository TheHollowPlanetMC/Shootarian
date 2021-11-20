package be4rjp.shootarian.ai;

import be4rjp.shootarian.player.ShootarianPlayer;
import net.citizensnpcs.api.ai.GoalSelector;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Location;

public class NavigationHelper {
    
    public int noNavigationTicks = 0;
    
    private double lastX = 0;
    private double lastZ = 0;
    
    public boolean navigationCanceled = true;
    
    public boolean tick(GoalSelector goalSelector, NPC npc, ShootarianPlayer ai, AILevel aiLevel){
        Location aiLocation = ai.getEyeLocation();
        if(aiLocation.getX() == lastX && aiLocation.getZ() == lastZ){
            noNavigationTicks++;
        }else{
            noNavigationTicks = 0;
        }
        lastX = aiLocation.getX();
        lastZ = aiLocation.getZ();
        
        double distance = 0;
        boolean targetDead = false;
        if(ai.getAiTarget() != null){
            targetDead = ai.getAiTarget().isDeath();
            
            if(ai.getAiTarget().getLocation().getWorld() == aiLocation.getWorld()){
                distance = ai.getAiTarget().getLocation().distance(aiLocation);
            }
        }
    
        if(noNavigationTicks > 40 || ai.isDeath() || targetDead || distance > aiLevel.getEnemyFindRange()){
            noNavigationTicks = 0;
            npc.getNavigator().cancelNavigation();
            goalSelector.finish();
            ai.setAiTarget(null);
            navigationCanceled = true;
            return true;
        }
        
        return false;
    }
    
    
}
