package be4rjp.shootarian.util;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

public class ShootarianSound {
    
    public static ShootarianSound getSoundByString(String string){

        String[] soundStrings = string.split("/n/");
        Set<SoundComponent> soundComponents = new HashSet<>();

        for(String soundString : soundStrings) {
            String[] args = soundString.split("/");
            Sound sound = Sound.valueOf(args[0]);
            float volume = Float.parseFloat(args[1]);
            float pitch = Float.parseFloat(args[2]);
            soundComponents.add(new SoundComponent(sound, volume, pitch));
        }

        return new ShootarianSound(soundComponents);
    }
    
    private static final double PLAY_SOUND_DISTANCE = 80.0;
    private static final double PLAY_SOUND_DISTANCE_SQUARE = Math.pow(PLAY_SOUND_DISTANCE, 2);

    private final Set<SoundComponent> sounds = new HashSet<>();

    public ShootarianSound(Sound sound, float volume, float pitch){
        this.sounds.add(new SoundComponent(sound, volume, pitch));
    }

    public ShootarianSound(Set<SoundComponent> sounds){
        this.sounds.addAll(sounds);
    }

    public void play(Player player, Location location){
        if(LocationUtil.distanceSquaredSafeDifferentWorld(player.getLocation(), location) > PLAY_SOUND_DISTANCE_SQUARE) return;
        sounds.forEach(soundComponent -> player.playSound(location, soundComponent.sound, soundComponent.volume, soundComponent.pitch));
    }


    public static class SoundComponent{
        private final Sound sound;
        private final float volume;
        private final float pitch;

        public SoundComponent(Sound sound, float volume, float pitch){
            this.sound = sound;
            this.volume = volume;
            this.pitch = pitch;
        }

        public Sound getSound() {return sound;}

        public float getPitch() {return pitch;}

        public float getVolume() {return volume;}
    }
}
