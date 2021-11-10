package be4rjp.shellcase.ai;

import be4rjp.shellcase.match.Match;
import be4rjp.shellcase.player.ShellCasePlayer;
import be4rjp.shellcase.util.LocationUtil;
import be4rjp.shellcase.weapon.gun.GunStatusData;
import net.citizensnpcs.api.ai.GoalSelector;

import java.util.Random;


public class ShootGunGoal<T extends Match> extends AIGoal<T>{
    
    private int tick = new Random().nextInt(100);
    
    private int itemSlot = new Random().nextInt(2);
    
    private final NavigationHelper navigationHelper;
    
    public ShootGunGoal(ShellCasePlayer shellCasePlayer, T match, AILevel aiLevel) {
        super(shellCasePlayer, match, aiLevel);
        
        this.navigationHelper = new NavigationHelper();
    }
    
    @Override
    public void reset() {
    
    }
    
    @Override
    public void run(GoalSelector goalSelector) {
        tick++;
        
        if(navigationHelper.tick(goalSelector, npc, aiShellCasePlayer, aiLevel)) return;
    
        if(!npc.getEntity().isOnGround() || aiShellCasePlayer.getAiTarget() == null){
            npc.getNavigator().cancelNavigation();
            aiShellCasePlayer.setAiTarget(null);
            return;
        }
        
        if(match.getMatchStatus() != Match.MatchStatus.IN_PROGRESS) return;
        GunStatusData gunStatusData = null;
        if(itemSlot == 0){
            gunStatusData = aiShellCasePlayer.getWeaponClass().getMainWeapon();
        }else{
            gunStatusData = aiShellCasePlayer.getWeaponClass().getSubWeapon();
        }
        
        ShellCasePlayer target = aiShellCasePlayer.getAiTarget();
        
        if(!target.isOnline() || target.isDeath()
                /*|| LocationUtil.distanceSquaredSafeDifferentWorld(target.getLocation(), target.getLocation()) > aiLevel.getEnemyFindRange() * aiLevel.getEnemyFindRange()*/){
            npc.getNavigator().cancelNavigation();
            aiShellCasePlayer.setAiTarget(null);
            goalSelector.finish();
            return;
        }
        
        if(gunStatusData == null){
            return;
        }
        
        if(tick % 2 == 0){
            return;
        }
        
        boolean peek = AIManager.peek(aiShellCasePlayer, target);
        if(gunStatusData.isReloading() || !peek){
            if(navigationHelper.navigationCanceled){
                navigationHelper.noNavigationTicks = 0;
                npc.getNavigator().setTarget(aiShellCasePlayer.getAiTarget().getLocation());
            }
            navigationHelper.navigationCanceled = false;
        } else {
            navigationHelper.navigationCanceled = true;
            npc.getNavigator().cancelNavigation();
            try{
                npc.getEntity().getLocation().add(target.getLocation()).checkFinite();
                npc.faceLocation(target.getLocation());
                if(!aiShellCasePlayer.isDeath()) gunStatusData.getGunWeapon().onRightClick(aiShellCasePlayer);
            }catch (Exception e){/**/}
        }
    }
    
    @Override
    public boolean shouldExecute(GoalSelector goalSelector) {
        return aiShellCasePlayer.getAiTarget() != null;
    }
}
