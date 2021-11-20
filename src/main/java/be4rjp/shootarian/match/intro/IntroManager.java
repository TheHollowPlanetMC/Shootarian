package be4rjp.shootarian.match.intro;

import be4rjp.cinema4c.data.play.MovieData;
import be4rjp.shootarian.match.Match;
import be4rjp.shootarian.match.map.ShootarianMap;
import be4rjp.shootarian.player.ObservableOption;
import be4rjp.shootarian.player.ShootarianPlayer;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class IntroManager {

    private static Map<Integer, Match> matchMap = new ConcurrentHashMap<>();
    private static Map<Integer, ShootarianPlayer> playerMap = new ConcurrentHashMap<>();

    public static Match getAndRemoveMatchByMoviePlayID(int playID){
        Match match = matchMap.get(playID);
        matchMap.remove(playID);
        return match;
    }
    
    public static ShootarianPlayer getAndRemovePlayerByMoviePlayID(int playID){
        ShootarianPlayer shootarianPlayer = playerMap.get(playID);
        playerMap.remove(playID);
        return shootarianPlayer;
    }

    private static void registerMatch(Match match, int playID){matchMap.put(playID, match);}
    private static void registerPlayer(ShootarianPlayer shootarianPlayer, int playID){playerMap.put(playID, shootarianPlayer);}


    public static void playIntro(ShootarianPlayer shootarianPlayer, Match match){
        ShootarianMap ShootarianMap = match.getShootarianMap();
        MovieData movieData = ShootarianMap.getIntroMovie();
        if(movieData == null) return;
        
        Player player = shootarianPlayer.getBukkitPlayer();
        if(player == null) return;

        List<Player> players = new ArrayList<>();
        players.add(player);
    
        shootarianPlayer.setObservableOption(ObservableOption.ALONE);
        
        Set<ShootarianPlayer> shootarianPlayers = new HashSet<>();
        shootarianPlayers.add(shootarianPlayer);
        int playID = movieData.play(players);
        registerPlayer(shootarianPlayer, playID);
        new MapIntroRunnable(shootarianPlayers, match).start();
    }
    
    
    public static void playIntro(Match match){
        ShootarianMap ShootarianMap = match.getShootarianMap();
        MovieData movieData = ShootarianMap.getIntroMovie();
        if(movieData == null) return;
        
        List<Player> players = new ArrayList<>();
        match.getPlayers().stream()
                .filter(ShootarianPlayer -> ShootarianPlayer.getBukkitPlayer() != null)
                .forEach(ShootarianPlayer -> players.add(ShootarianPlayer.getBukkitPlayer()));
        
        match.setPlayerObservableOption(ObservableOption.ALONE);
        
        int playID = movieData.play(players);
        registerMatch(match, playID);
        new MapIntroRunnable(match.getPlayers(), match).start();
    }
}
