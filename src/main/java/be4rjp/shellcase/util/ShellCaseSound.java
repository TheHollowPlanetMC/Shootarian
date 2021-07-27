package be4rjp.shellcase.util;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class ShellCaseSound {
    
    public static ShellCaseSound getSoundByString(String string){
        String[] args = string.split("/");
        Sound sound = Sound.valueOf(args[0]);
        float volume = Float.parseFloat(args[1]);
        float pitch = Float.parseFloat(args[2]);
        return new ShellCaseSound(sound, volume, pitch);
    }
    
    
    private static final double PLAY_SOUND_DISTANCE_SQUARE = 800.0;
    
    private final Sound sound;
    private final float volume;
    private final float pitch;
    
    public ShellCaseSound(Sound sound, float volume, float pitch){
        this.sound = sound;
        this.volume = volume;
        this.pitch = pitch;
    }
    
    public void play(Player player, Location location){
        if(LocationUtil.distanceSquaredSafeDifferentWorld(player.getLocation(), location) > PLAY_SOUND_DISTANCE_SQUARE) return;
        player.playSound(location, sound, volume, pitch);
    }
    
    public Sound getSound() {return sound;}
    
    public float getPitch() {return pitch;}
    
    public float getVolume() {return volume;}
}
