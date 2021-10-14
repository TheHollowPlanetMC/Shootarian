package be4rjp.shellcase.listener;

import be4rjp.cinema4c.event.AsyncMoviePlayFinishEvent;
import be4rjp.shellcase.ShellCase;
import be4rjp.shellcase.map.ConquestPlayerClickableGUIRenderer;
import be4rjp.shellcase.match.ConquestMatch;
import be4rjp.shellcase.match.Match;
import be4rjp.shellcase.match.intro.IntroManager;
import be4rjp.shellcase.match.intro.ReadyRunnable;
import be4rjp.shellcase.match.team.ShellCaseTeam;
import be4rjp.shellcase.player.ObservableOption;
import be4rjp.shellcase.player.ShellCasePlayer;
import be4rjp.shellcase.util.ShellCaseSound;
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
            
            for(ShellCasePlayer shellCasePlayer : match.getPlayers()){
                shellCasePlayer.setGameMode(GameMode.ADVENTURE);
                shellCasePlayer.giveItems();
                match.teleportToTeamLocation(shellCasePlayer);
                shellCasePlayer.setObservableOption(ObservableOption.ONLY_MATCH_PLAYER);
                new BukkitRunnable(){
                    @Override
                    public void run() {
                        shellCasePlayer.sendTextTitle("none", "match-conquest-start-sub-title", 2, 20, 2);
                        shellCasePlayer.playSound(new ShellCaseSound(Sound.ENTITY_ZOMBIE_INFECT, 1.0F, 2.0F));
                    }
                }.runTaskLaterAsynchronously(ShellCase.getPlugin(), 10);
                
                ConquestPlayerClickableGUIRenderer guiRenderer = new ConquestPlayerClickableGUIRenderer(shellCasePlayer, ((ConquestMatch) match).getConquestStatusRenderer(), ((ConquestMatch) match).getConquestMap().getCanvasData());
                guiRenderer.start();
                shellCasePlayer.setPlayerGUIRenderer(guiRenderer);
            }
            
            match.start();
            
            return;
        }
        /*
        ShellCasePlayer shellCasePlayer = IntroManager.getAndRemovePlayerByMoviePlayID(playID);
        if(shellCasePlayer != null){
            ShellCaseTeam shellCaseTeam = shellCasePlayer.getShellCaseTeam();
            
            if(shellCaseTeam != null) {
                shellCasePlayer.giveItems();
                shellCaseTeam.getMatch().teleportToTeamLocation(shellCasePlayer);
                shellCasePlayer.setObservableOption(ObservableOption.ONLY_MATCH_PLAYER);
                shellCasePlayer.sendTextTitle("", "match-conquest-start-sub-title", 2, 20, 2);
                shellCasePlayer.playSound(new ShellCaseSound(Sound.ENTITY_ZOMBIE_INFECT, 1.0F, 2.0F));
            }
        }*/
    }
}
