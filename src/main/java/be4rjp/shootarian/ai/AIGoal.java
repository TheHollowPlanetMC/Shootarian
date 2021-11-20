package be4rjp.shootarian.ai;

import be4rjp.shootarian.match.Match;
import be4rjp.shootarian.player.ShootarianPlayer;
import net.citizensnpcs.api.ai.Goal;
import net.citizensnpcs.api.npc.NPC;

public abstract class AIGoal<T extends Match> implements Goal {
    
    protected ShootarianPlayer aiShootarianPlayer;
    
    protected NPC npc;
    
    protected T match;
    
    protected AILevel aiLevel;
    
    public AIGoal(ShootarianPlayer shootarianPlayer, T match, AILevel aiLevel){
        this.aiShootarianPlayer = shootarianPlayer;
        this.npc = shootarianPlayer.getCitizensNPC();
        this.match = match;
        this.aiLevel = aiLevel;
    }
    
}
