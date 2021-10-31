package be4rjp.shellcase.ai;

import be4rjp.shellcase.match.Match;
import be4rjp.shellcase.player.ShellCasePlayer;
import be4rjp.shellcase.weapon.gun.GunStatusData;
import net.citizensnpcs.api.ai.GoalSelector;


public class ShootGunGoal<T extends Match> extends AIGoal<T>{
    
    private int tick = 0;
    
    private int itemSlot = 0;
    
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
        
        if(match.getMatchStatus() != Match.MatchStatus.IN_PROGRESS) return;
        GunStatusData gunStatusData = null;
        if(itemSlot == 0){
            gunStatusData = aiShellCasePlayer.getWeaponClass().getMainWeapon();
        }
        
        ShellCasePlayer target = aiShellCasePlayer.getAiTarget();
        
        if(!target.isOnline() || target.isDeath()){
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
        
        
        if(gunStatusData.isReloading() || !AIManager.peek(aiShellCasePlayer, target)){
            if(navigationHelper.navigationCanceled){
                navigationHelper.noNavigationTicks = 0;
                npc.getNavigator().setTarget(aiShellCasePlayer.getAiTarget().getBukkitPlayer(), false);
            }
            navigationHelper.navigationCanceled = false;
        } else {
            navigationHelper.navigationCanceled = true;
            npc.getNavigator().cancelNavigation();
            npc.faceLocation(target.getLocation());
            if(!aiShellCasePlayer.isDeath()) gunStatusData.getGunWeapon().onRightClick(aiShellCasePlayer);
        }
    }
    
    @Override
    public boolean shouldExecute(GoalSelector goalSelector) {
        return aiShellCasePlayer.getAiTarget() != null;
    }
}
