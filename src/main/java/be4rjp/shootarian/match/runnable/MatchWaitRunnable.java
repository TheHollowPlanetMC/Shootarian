package be4rjp.shootarian.match.runnable;

import be4rjp.shootarian.Shootarian;
import be4rjp.shootarian.language.Lang;
import be4rjp.shootarian.match.Match;
import be4rjp.shootarian.match.MatchManager;
import be4rjp.shootarian.match.intro.IntroManager;
import be4rjp.shootarian.language.MessageManager;
import be4rjp.shootarian.match.map.AsyncMapLoader;
import be4rjp.shootarian.match.team.ShootarianTeam;
import be4rjp.shootarian.player.ShootarianPlayer;
import be4rjp.shootarian.util.ShootarianScoreboard;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

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
        for(ShootarianPlayer shootarianPlayer : matchManager.getJoinedPlayers()){
            if(shootarianPlayer.isOnline()) i++;
        }

        if(i < minPlayer){
            timeLeft = DEFAULT_WAIT_TIME;
        }

        //スコアボード
        ShootarianScoreboard scoreboard = matchManager.getScoreboard();
        for(ShootarianPlayer ShootarianPlayer : matchManager.getJoinedPlayers()) {
            Lang lang = ShootarianPlayer.getLang();
            List<String> lines = new ArrayList<>();
            lines.add("");
            lines.add("§a" + MessageManager.getText(lang, "match-map") + " » §r§l" + match.getShootarianMap().getDisplayName(lang));
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
            
            scoreboard.setSidebarLine(ShootarianPlayer, lines);
        }
        scoreboard.updateSidebar(matchManager.getJoinedPlayers());
        

        if(timeLeft <= 0 && asyncMapLoader.getCompletableFuture().isDone()){
    
            match.setMatchStatus(Match.MatchStatus.PLAYING_INTRO);
            
            if(matchManager.getType() == MatchManager.MatchManageType.CONQUEST) {
                ShootarianTeam team0 = match.getShootarianTeams().get(0);
                ShootarianTeam team1 = match.getShootarianTeams().get(1);
        
                int index = 0;
                for (ShootarianPlayer ShootarianPlayer : matchManager.getJoinedPlayers()) {
                    if(index % 2 == 0){
                        team0.join(ShootarianPlayer);
                    }else{
                        team1.join(ShootarianPlayer);
                    }
                    ShootarianPlayer.setScoreBoard(match.getScoreboard());
                    index++;
                }
            }
            match.getPlayers().forEach(shootarianPlayer -> shootarianPlayer.teleport(match.getShootarianMap().getWaitSCLocation().getBukkitLocation()));
            
            //開始処理
            new BukkitRunnable(){
                @Override
                public void run(){
                    IntroManager.playIntro(match);
                }
            }.runTaskLaterAsynchronously(Shootarian.getPlugin(), 40);
            
            this.cancel();
        }
        timeLeft--;
    }
    
    
    public void start(){
        this.runTaskTimerAsynchronously(Shootarian.getPlugin(), 0, 20);
        this.asyncMapLoader = match.loadGameMap();
    }
}
