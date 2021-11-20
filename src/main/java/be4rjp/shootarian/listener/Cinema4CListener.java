package be4rjp.shootarian.listener;

import be4rjp.cinema4c.event.AsyncMoviePlayFinishEvent;
import be4rjp.shootarian.Shootarian;
import be4rjp.shootarian.map.ConquestPlayerClickableGUIRenderer;
import be4rjp.shootarian.match.ConquestMatch;
import be4rjp.shootarian.match.Match;
import be4rjp.shootarian.match.intro.IntroManager;
import be4rjp.shootarian.player.ObservableOption;
import be4rjp.shootarian.player.ShootarianPlayer;
import be4rjp.shootarian.util.ShootarianSound;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

public class Cinema4CListener implements Listener {

    @EventHandler
    public void onFinishMovie(AsyncMoviePlayFinishEvent event){
        int playID = event.getPlayID();

        Match match = IntroManager.getAndRemoveMatchByMoviePlayID(playID);
        if(match != null){
            
            for(ShootarianPlayer shootarianPlayer : match.getPlayers()){
                shootarianPlayer.setGameMode(GameMode.ADVENTURE);
                shootarianPlayer.giveItems();
                match.teleportToTeamLocation(shootarianPlayer);
                shootarianPlayer.setObservableOption(ObservableOption.ONLY_MATCH_PLAYER);
                new BukkitRunnable(){
                    @Override
                    public void run() {
                        shootarianPlayer.sendTextTitle("none", "match-conquest-start-sub-title", 2, 20, 2);
                        shootarianPlayer.playSound(new ShootarianSound(Sound.ENTITY_ZOMBIE_INFECT, 1.0F, 2.0F));
                    }
                }.runTaskLaterAsynchronously(Shootarian.getPlugin(), 10);
                
                ConquestPlayerClickableGUIRenderer guiRenderer = new ConquestPlayerClickableGUIRenderer(shootarianPlayer, ((ConquestMatch) match).getConquestStatusRenderer(), ((ConquestMatch) match).getConquestMap().getCanvasData());
                guiRenderer.start();
                shootarianPlayer.setPlayerGUIRenderer(guiRenderer);
            }
            
            match.start();
            
            return;
        }
        /*
        ShootarianPlayer shootarianPlayer = IntroManager.getAndRemovePlayerByMoviePlayID(playID);
        if(shootarianPlayer != null){
            ShootarianTeam shootarianTeam = shootarianPlayer.getShootarianTeam();
            
            if(shootarianTeam != null) {
                shootarianPlayer.giveItems();
                shootarianTeam.getMatch().teleportToTeamLocation(shootarianPlayer);
                shootarianPlayer.setObservableOption(ObservableOption.ONLY_MATCH_PLAYER);
                shootarianPlayer.sendTextTitle("", "match-conquest-start-sub-title", 2, 20, 2);
                shootarianPlayer.playSound(new ShootarianSound(Sound.ENTITY_ZOMBIE_INFECT, 1.0F, 2.0F));
            }
        }*/
    }
}
