package be4rjp.shootarian.ai;

import be4rjp.shootarian.match.Match;
import be4rjp.shootarian.player.ShootarianPlayer;
import be4rjp.shootarian.weapon.gun.GunStatusData;
import net.citizensnpcs.api.ai.GoalSelector;

import java.util.Random;


public class ShootGunGoal<T extends Match> extends AIGoal<T>{
    
    private int tick = new Random().nextInt(100);
    
    private int itemSlot = new Random().nextInt(2);
    
    private final NavigationHelper navigationHelper;
    
    public ShootGunGoal(ShootarianPlayer shootarianPlayer, T match, AILevel aiLevel) {
        super(shootarianPlayer, match, aiLevel);
        
        this.navigationHelper = new NavigationHelper();
    }
    
    @Override
    public void reset() {
    
    }
    
    @Override
    public void run(GoalSelector goalSelector) {
        tick++;
        
        if(navigationHelper.tick(goalSelector, npc, aiShootarianPlayer, aiLevel)) return;
    
        if(!npc.getEntity().isOnGround() || aiShootarianPlayer.getAiTarget() == null){
            npc.getNavigator().cancelNavigation();
            aiShootarianPlayer.setAiTarget(null);
            return;
        }
        
        if(match.getMatchStatus() != Match.MatchStatus.IN_PROGRESS) return;
        GunStatusData gunStatusData = null;
        if(itemSlot == 0){
            gunStatusData = aiShootarianPlayer.getWeaponClass().getMainWeapon();
        }else{
            gunStatusData = aiShootarianPlayer.getWeaponClass().getSubWeapon();
        }
        
        ShootarianPlayer target = aiShootarianPlayer.getAiTarget();
        
        if(!target.isOnline() || target.isDeath()
                /*|| LocationUtil.distanceSquaredSafeDifferentWorld(target.getLocation(), target.getLocation()) > aiLevel.getEnemyFindRange() * aiLevel.getEnemyFindRange()*/){
            npc.getNavigator().cancelNavigation();
            aiShootarianPlayer.setAiTarget(null);
            goalSelector.finish();
            return;
        }
        
        if(gunStatusData == null){
            return;
        }
        
        if(tick % 2 == 0){
            return;
        }
        
        boolean peek = AIManager.peek(aiShootarianPlayer, target);
        if(gunStatusData.isReloading() || !peek){
            if(navigationHelper.navigationCanceled){
                navigationHelper.noNavigationTicks = 0;
                npc.getNavigator().setTarget(aiShootarianPlayer.getAiTarget().getLocation());
            }
            navigationHelper.navigationCanceled = false;
        } else {
            navigationHelper.navigationCanceled = true;
            npc.getNavigator().cancelNavigation();
            try{
                npc.getEntity().getLocation().add(target.getLocation()).checkFinite();
                npc.faceLocation(target.getLocation());
                if(!aiShootarianPlayer.isDeath()) gunStatusData.getGunWeapon().onRightClick(aiShootarianPlayer);
            }catch (Exception e){/**/}
        }
    }
    
    @Override
    public boolean shouldExecute(GoalSelector goalSelector) {
        return aiShootarianPlayer.getAiTarget() != null;
    }
}
