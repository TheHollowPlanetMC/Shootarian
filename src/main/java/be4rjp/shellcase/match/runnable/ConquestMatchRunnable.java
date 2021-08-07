package be4rjp.shellcase.match.runnable;

import be4rjp.shellcase.language.Lang;
import be4rjp.shellcase.match.ConquestMatch;
import be4rjp.shellcase.language.MessageManager;
import be4rjp.shellcase.match.map.ConquestMap;
import be4rjp.shellcase.match.map.area.FlagAreaData;
import be4rjp.shellcase.match.result.ResultRunnable;
import be4rjp.shellcase.match.team.ShellCaseTeam;
import be4rjp.shellcase.player.ShellCasePlayer;
import be4rjp.shellcase.util.ProgressBar;
import be4rjp.shellcase.util.ShellCaseScoreboard;
import be4rjp.shellcase.util.ShellCaseSound;
import org.bukkit.GameMode;
import org.bukkit.Sound;

import java.util.ArrayList;
import java.util.List;

public class ConquestMatchRunnable extends MatchRunnable{
    
    private static final ShellCaseSound FINISH_SOUND = new ShellCaseSound(Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 3.0F, 1.3F);
    
    
    private final ConquestMatch conquestMatch;
    
    /**
     * 試合のスケジューラーを作成します。
     *
     * @param match     試合のインスタンス
     * @param timeLimit 試合の最大時間
     */
    public ConquestMatchRunnable(ConquestMatch match, int timeLimit) {
        super(match, timeLimit);
        this.conquestMatch = match;
    }
    
    @Override
    public void run() {
        
        for(FlagAreaData flagAreaData : conquestMatch.getFlagAreaData()){
            int territory = 0;
            
            int beforeTerritory = flagAreaData.getTerritory();
            
            for(ShellCasePlayer shellCasePlayer : match.getPlayers()){
                ShellCaseTeam shellCaseTeam = shellCasePlayer.getShellCaseTeam();
                if(shellCaseTeam == null) continue;
                if(!flagAreaData.getFlagArea().getBoundingBox().isInBox(shellCasePlayer.getLocation().toVector())) continue;
                
                if(match.getShellCaseTeams().get(0) == shellCaseTeam){
                    territory--;
                }else{
                    territory++;
                }
            }
            
            flagAreaData.addTerritory(territory * 2);
            
            ShellCaseTeam shellCaseTeam = flagAreaData.getTeam();
            if(shellCaseTeam != null && Math.abs(flagAreaData.getTerritory()) == 100){
                shellCaseTeam.addPoints(1);
                
                if(Math.abs(beforeTerritory) < 100){
                    shellCaseTeam.getTeamMembers().forEach(shellCasePlayer ->
                            shellCasePlayer.sendText("match-conquest-get-area", shellCaseTeam.getShellCaseColor().getDisplayName(), flagAreaData.getFlagArea().getDisplayName()));
                }else{
                    for(ShellCaseTeam otherTeam : shellCaseTeam.getOtherTeam()){
                        otherTeam.getTeamMembers().forEach(shellCasePlayer ->
                                shellCasePlayer.sendText("match-conquest-get-area-e", otherTeam.getShellCaseColor().getDisplayName(), flagAreaData.getFlagArea().getDisplayName()));
                    }
                }
            }
        }
    
    
        if(timeLeft == 0 || match.checkWin()){
            this.cancel();
            match.finish();
            new ResultRunnable(match).start();
            match.getPlayers().forEach(ShellCasePlayer -> ShellCasePlayer.setGameMode(GameMode.SPECTATOR));
            match.playSound(FINISH_SOUND);
        }
        
    
        String min = String.format("%02d", timeLeft % 60);
        //スコアボード
        ShellCaseScoreboard scoreboard = match.getScoreboard();
        for(ShellCasePlayer ShellCasePlayer : match.getPlayers()) {
            Lang lang = ShellCasePlayer.getLang();
            List<String> lines = new ArrayList<>();
            lines.add("");
            lines.add("§a" + MessageManager.getText(lang, "match-map") + " » §r§l" + match.getShellCaseMap().getDisplayName(lang));
            lines.add(" ");
            lines.add("§a" + MessageManager.getText(lang, "match-mode") + " » §6§l " + match.getType().getDisplayName(lang));
            lines.add("  ");
            lines.add("§6" + MessageManager.getText(lang, "match-flag") + " » ");
            
            for(FlagAreaData flagAreaData : conquestMatch.getFlagAreaData()){
                ProgressBar progressBar = new ProgressBar(10);
                progressBar.setBarCharacter("|");
                progressBar.setEmptyColor(match.getShellCaseTeams().get(0).getShellCaseColor().getChatColor().toString());
                progressBar.setProgressPercent((double)(flagAreaData.getTerritory() + 100) / 2.0);
                String bar = progressBar.toString(match.getShellCaseTeams().get(1).getShellCaseColor().getChatColor().toString());
                
                if(flagAreaData.getTeam() == null)
                    lines.add(" " + flagAreaData.getFlagArea().getDisplayName() + "§7 : " + progressBar.setEmptyColor("§7").toString("§7") + " 0%");
                else
                    lines.add(" " + flagAreaData.getFlagArea().getDisplayName() + "§7 : " + bar + " " + Math.abs(flagAreaData.getTerritory()) + "%");
            }
    
            lines.add("   ");
            
            ShellCaseTeam team0 = match.getShellCaseTeams().get(0);
            ShellCaseTeam team1 = match.getShellCaseTeams().get(1);
            
            String team0Line = team0.getShellCaseColor().getDisplayName() + "§7: §f" + (((ConquestMap)conquestMatch.getShellCaseMap()).getMaxTicket() - team1.getPoints());
            String team1Line = team1.getShellCaseColor().getDisplayName() + "§7: §f" + (((ConquestMap)conquestMatch.getShellCaseMap()).getMaxTicket() - team0.getPoints());
            lines.add("§6" + MessageManager.getText(lang, "match-ticket") + " » ");
            lines.add("  " + team0Line);
            lines.add("  " + team1Line);
            lines.add("    ");
            lines.add("§b" + MessageManager.getText(lang, "match-time") + " » §r§l" + timeLeft / 60 + ":" + min);
            
            scoreboard.setSidebarLine(ShellCasePlayer, lines);
        }
        scoreboard.updateSidebar(match.getPlayers());
        
        timeLeft--;
    }
    
    
    @Override
    public synchronized void cancel() throws IllegalStateException {
        super.cancel();
    }
}
