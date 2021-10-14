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

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class IntroManager {

    private static Map<Integer, Match> matchMap = new ConcurrentHashMap<>();
    private static Map<Integer, ShellCasePlayer> playerMap = new ConcurrentHashMap<>();

    public static Match getAndRemoveMatchByMoviePlayID(int playID){
        Match match = matchMap.get(playID);
        matchMap.remove(playID);
        return match;
    }
    
    public static ShellCasePlayer getAndRemovePlayerByMoviePlayID(int playID){
        ShellCasePlayer shellCasePlayer = playerMap.get(playID);
        playerMap.remove(playID);
        return shellCasePlayer;
    }

    private static void registerMatch(Match match, int playID){matchMap.put(playID, match);}
    private static void registerPlayer(ShellCasePlayer shellCasePlayer, int playID){playerMap.put(playID, shellCasePlayer);}


    public static void playIntro(ShellCasePlayer shellCasePlayer, Match match){
        ShellCaseMap ShellCaseMap = match.getShellCaseMap();
        MovieData movieData = ShellCaseMap.getIntroMovie();
        if(movieData == null) return;
        
        Player player = shellCasePlayer.getBukkitPlayer();
        if(player == null) return;

        List<Player> players = new ArrayList<>();
        players.add(player);
    
        shellCasePlayer.setObservableOption(ObservableOption.ALONE);
        
        Set<ShellCasePlayer> shellCasePlayers = new HashSet<>();
        shellCasePlayers.add(shellCasePlayer);
        int playID = movieData.play(players);
        registerPlayer(shellCasePlayer, playID);
        new MapIntroRunnable(shellCasePlayers, match).start();
    }
    
    
    public static void playIntro(Match match){
        ShellCaseMap ShellCaseMap = match.getShellCaseMap();
        MovieData movieData = ShellCaseMap.getIntroMovie();
        if(movieData == null) return;
        
        List<Player> players = new ArrayList<>();
        match.getPlayers().stream()
                .filter(ShellCasePlayer -> ShellCasePlayer.getBukkitPlayer() != null)
                .forEach(ShellCasePlayer -> players.add(ShellCasePlayer.getBukkitPlayer()));
        
        match.setPlayerObservableOption(ObservableOption.ALONE);
        
        int playID = movieData.play(players);
        registerMatch(match, playID);
        new MapIntroRunnable(match.getPlayers(), match).start();
    }
}
