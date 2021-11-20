package be4rjp.shootarian.weapon.actions.action;

import be4rjp.shootarian.match.team.ShootarianTeam;
import be4rjp.shootarian.player.ShootarianPlayer;
import be4rjp.shootarian.util.ShootarianSound;

public class SoundAction implements Action{
    
    private final ShootarianSound SOUND;
    
    public SoundAction(String string){
        this.SOUND = ShootarianSound.getSoundByString(string);
    }
    
    @Override
    public void play(ShootarianPlayer reloadPlayer, int tick) {
        ShootarianTeam shootarianTeam = reloadPlayer.getShootarianTeam();
        if(shootarianTeam == null) return;
        
        shootarianTeam.getMatch().playSound(SOUND, reloadPlayer.getLocation());
    }
}
