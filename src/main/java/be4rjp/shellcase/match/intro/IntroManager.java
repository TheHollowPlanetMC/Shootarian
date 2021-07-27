package be4rjp.shellcase.match.intro;

import be4rjp.cinema4c.data.play.MovieData;
import be4rjp.shellcase.match.Match;
import be4rjp.shellcase.match.map.ShellCaseMap;
import be4rjp.shellcase.match.team.ShellCaseTeam;
import be4rjp.shellcase.player.ObservableOption;
import be4rjp.shellcase.player.ShellCasePlayer;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.minecraft.server.v1_15_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_15_R1.CraftServer;
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_15_R1.util.CraftChatMessage;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class IntroManager {

    private static Map<Integer, Match> matchMap = new ConcurrentHashMap<>();

    public static Match getMatchByMoviePlayID(int playID){return matchMap.get(playID);}

    private static void registerMatch(Match match, int playID){matchMap.put(playID, match);}


    public static void playIntro(Match match){
        ShellCaseMap ShellCaseMap = match.getShellCaseMap();
        MovieData movieData = ShellCaseMap.getIntroMovie();
        if(movieData == null) return;

        Set<Player> players = new HashSet<>();
        match.getPlayers().stream()
                .filter(ShellCasePlayer -> ShellCasePlayer.getBukkitPlayer() != null)
                .forEach(ShellCasePlayer -> players.add(ShellCasePlayer.getBukkitPlayer()));
    
        match.setPlayerObservableOption(ObservableOption.ALONE);
        
        int playID = movieData.play(players);
        registerMatch(match, playID);
        new MapIntroRunnable(match).start();
    }
}
