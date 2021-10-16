package be4rjp.shellcase.match.runnable;

import be4rjp.shellcase.ShellCase;
import be4rjp.shellcase.language.Lang;
import be4rjp.shellcase.match.Match;
import be4rjp.shellcase.match.MatchManager;
import be4rjp.shellcase.match.intro.IntroManager;
import be4rjp.shellcase.language.MessageManager;
import be4rjp.shellcase.match.map.AsyncMapLoader;
import be4rjp.shellcase.match.team.ShellCaseTeam;
import be4rjp.shellcase.player.ShellCasePlayer;
import be4rjp.shellcase.util.ShellCaseScoreboard;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class MatchWaitRunnable extends BukkitRunnable {

    //デフォルトのプレイヤー待機時間[sec]
    private static final int DEFAULT_WAIT_TIME = 20;
    
    //試合を開始するのに必要な最低人数
    private final int minPlayer;

    private final MatchManager matchManager;
    private final Match match;
    private int timeLeft = DEFAULT_WAIT_TIME;
    
    private AsyncMapLoader asyncMapLoader;
    
    private boolean isLoadComplete = false;
    
    public MatchWaitRunnable(MatchManager matchManager, Match match, int minPlayer){
        this.matchManager = matchManager;
        this.match = match;
        this.minPlayer = minPlayer;
    }


    @Override
    public void run() {
        
        int i = 0;
        for(ShellCasePlayer shellCasePlayer : matchManager.getJoinedPlayers()){
            if(shellCasePlayer.isOnline()) i++;
        }

        if(i < minPlayer){
            timeLeft = DEFAULT_WAIT_TIME;
        }

        //スコアボード
        ShellCaseScoreboard scoreboard = matchManager.getScoreboard();
        for(ShellCasePlayer ShellCasePlayer : matchManager.getJoinedPlayers()) {
            Lang lang = ShellCasePlayer.getLang();
            List<String> lines = new ArrayList<>();
            lines.add("");
            lines.add("§a" + MessageManager.getText(lang, "match-map") + " » §r§l" + match.getShellCaseMap().getDisplayName(lang));
            lines.add(" ");
            lines.add("§a" + MessageManager.getText(lang, "match-mode") + " » §6§l" + match.getType().getDisplayName(lang));
            lines.add("   ");
            if(matchManager.getJoinedPlayers().size() < minPlayer)
                lines.add("§b" + String.format(MessageManager.getText(lang, "match-wait-player"), minPlayer - matchManager.getJoinedPlayers().size()));
            else {
                if(timeLeft <= 0) lines.add("§b" + MessageManager.getText(lang, "match-wait-time") + " » §r§l" + timeLeft + MessageManager.getText(lang, "word-sec"));
                else lines.add(MessageManager.getText(lang, "match-wait-map-load"));
            }
            
            lines.add("    ");
            lines.add(String.format(MessageManager.getText(lang, "match-wait-map-load-progress"), asyncMapLoader.getLoadedTaskCount() + "/" + asyncMapLoader.getMaxLoad()));
            
            scoreboard.setSidebarLine(ShellCasePlayer, lines);
        }
        scoreboard.updateSidebar(matchManager.getJoinedPlayers());
        

        if(timeLeft <= 0 && asyncMapLoader.getCompletableFuture().isDone()){
    
            match.setMatchStatus(Match.MatchStatus.PLAYING_INTRO);
            
            if(matchManager.getType() == MatchManager.MatchManageType.CONQUEST) {
                ShellCaseTeam team0 = match.getShellCaseTeams().get(0);
                ShellCaseTeam team1 = match.getShellCaseTeams().get(1);
        
                int index = 0;
                for (ShellCasePlayer ShellCasePlayer : matchManager.getJoinedPlayers()) {
                    if(index % 2 == 0){
                        team0.join(ShellCasePlayer);
                    }else{
                        team1.join(ShellCasePlayer);
                    }
                    ShellCasePlayer.setScoreBoard(match.getScoreboard());
                    index++;
                }
            }
            match.getPlayers().forEach(shellCasePlayer -> shellCasePlayer.teleport(match.getShellCaseMap().getWaitSCLocation().getBukkitLocation()));
            
            //開始処理
            new BukkitRunnable(){
                @Override
                public void run(){
                    IntroManager.playIntro(match);
                }
            }.runTaskLaterAsynchronously(ShellCase.getPlugin(), 40);
            
            this.cancel();
        }
        timeLeft--;
    }
    
    
    public void start(){
        this.runTaskTimerAsynchronously(ShellCase.getPlugin(), 0, 20);
        this.asyncMapLoader = match.loadGameMap();
    }
}
