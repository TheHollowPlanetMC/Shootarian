package be4rjp.shellcase.ai;

import be4rjp.shellcase.match.Match;
import be4rjp.shellcase.player.ShellCasePlayer;
import net.citizensnpcs.api.ai.Goal;
import net.citizensnpcs.api.npc.NPC;

public abstract class AIGoal<T extends Match> implements Goal {
    
    protected ShellCasePlayer aiShellCasePlayer;
    
    protected NPC npc;
    
    protected T match;
    
    protected AILevel aiLevel;
    
    public AIGoal(ShellCasePlayer shellCasePlayer, T match, AILevel aiLevel){
        this.aiShellCasePlayer = shellCasePlayer;
        this.npc = shellCasePlayer.getCitizensNPC();
        this.match = match;
        this.aiLevel = aiLevel;
    }
    
}
