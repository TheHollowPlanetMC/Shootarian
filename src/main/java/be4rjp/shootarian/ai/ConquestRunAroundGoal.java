package be4rjp.shootarian.ai;

import be4rjp.shootarian.match.ConquestMatch;
import be4rjp.shootarian.match.Match;
import be4rjp.shootarian.player.ShootarianPlayer;
import be4rjp.shootarian.util.SphereBlocks;
import net.citizensnpcs.api.ai.GoalSelector;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class ConquestRunAroundGoal extends AIGoal<ConquestMatch> {
    
    private final ShootGunGoal<ConquestMatch> shootGunGoal;
    
    private final NavigationHelper navigationHelper = new NavigationHelper();
    
    public ConquestRunAroundGoal(ShootarianPlayer shootarianPlayer, ConquestMatch conquestMatch, AILevel aiLevel) {
        super(shootarianPlayer, conquestMatch, aiLevel);
        
        this.shootGunGoal = new ShootGunGoal<>(shootarianPlayer, conquestMatch, aiLevel);
    }
    
    @Override
    public void reset() {
    
    }
    
    private int tick = new Random().nextInt(100);
    
    @Override
    public void run(GoalSelector goalSelector) {
        if(match.getMatchStatus() != Match.MatchStatus.IN_PROGRESS) return;
        
        tick++;
    
        if(navigationHelper.tick(goalSelector, npc, aiShootarianPlayer, aiLevel)) return;
        if(!npc.getEntity().isOnGround()){
            npc.getNavigator().cancelNavigation();
            aiShootarianPlayer.setAiTarget(null);
            return;
        }
        
        if(tick % 2 == 0) {
    
            Set<ShootarianPlayer> targetPlayers = match.getPlayersInRange(aiShootarianPlayer.getLocation(), aiLevel.getEnemyFindRange());
            targetPlayers.removeIf(target -> target.getShootarianTeam() == aiShootarianPlayer.getShootarianTeam());
    
            if (targetPlayers.size() != 0) {
                List<ShootarianPlayer> targetList = new ArrayList<>(targetPlayers);
                ShootarianPlayer target = null;
                for (ShootarianPlayer shootarianPlayer : targetList) {
                    if (AIManager.peek(aiShootarianPlayer, shootarianPlayer)) {
                        target = shootarianPlayer;
                        break;
                    }
                }
        
                if (target == null) {
                    aiShootarianPlayer.setAiTarget(null);
                    npc.getNavigator().getLocalParameters().speedModifier(1.0F);
                } else {
                    if (!target.isOnline() || target.isDeath()) {
                        aiShootarianPlayer.setAiTarget(null);
                        npc.getNavigator().getLocalParameters().speedModifier(1.0F);
                    } else {
                        aiShootarianPlayer.setAiTarget(target);
                        npc.getNavigator().getLocalParameters().speedModifier(1.4F);
                    }
                }
            }
    
            if (aiShootarianPlayer.getAiTarget() == null && tick % 100 == 0) {
                if (!npc.getNavigator().isNavigating()) {
                    SphereBlocks sphereBlocks = new SphereBlocks(aiLevel.getEnemyFindRange() / 1.5, aiShootarianPlayer.getLocation());
                    Set<Block> blocks = sphereBlocks.getBlocks();
                    blocks.removeIf(block -> block.getType() == Material.AIR || block.getRelative(0, 1, 0).getType() != Material.AIR || block.getRelative(0, 2, 0).getType() != Material.AIR);
                    if (blocks.size() == 0) return;
                    List<Block> blockList = new ArrayList<>(blocks);
                    Block target = blockList.get(new Random().nextInt(blockList.size())).getRelative(BlockFace.UP);
            
                    //npc.faceLocation(target.getLocation());
                    npc.getNavigator().setTarget(target.getLocation());
                }
            }
        }
    }
    
    @Override
    public boolean shouldExecute(GoalSelector goalSelector) {
        return aiShootarianPlayer.getAiTarget() == null;
    }
    
}
