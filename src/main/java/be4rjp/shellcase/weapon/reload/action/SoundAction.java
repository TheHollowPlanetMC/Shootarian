package be4rjp.shellcase.weapon.reload.action;

import be4rjp.shellcase.match.team.ShellCaseTeam;
import be4rjp.shellcase.player.ShellCasePlayer;
import be4rjp.shellcase.util.ShellCaseSound;

public class SoundAction implements Action{
    
    private final ShellCaseSound SOUND;
    
    public SoundAction(String string){
        this.SOUND = ShellCaseSound.getSoundByString(string);
    }
    
    @Override
    public void play(ShellCasePlayer reloadPlayer, int tick) {
        ShellCaseTeam shellCaseTeam = reloadPlayer.getShellCaseTeam();
        if(shellCaseTeam == null) return;
        
        shellCaseTeam.getMatch().playSound(SOUND, reloadPlayer.getLocation());
    }
}
