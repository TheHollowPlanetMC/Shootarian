package be4rjp.shootarian.match.runnable;

import be4rjp.shootarian.language.Lang;
import be4rjp.shootarian.match.ConquestMatch;
import be4rjp.shootarian.language.MessageManager;
import be4rjp.shootarian.match.map.ConquestMap;
import be4rjp.shootarian.match.map.area.FlagAreaData;
import be4rjp.shootarian.match.result.ResultRunnable;
import be4rjp.shootarian.match.team.ShootarianTeam;
import be4rjp.shootarian.player.ShootarianPlayer;
import be4rjp.shootarian.util.ShootarianScoreboard;
import be4rjp.shootarian.util.ShootarianSound;
import org.bukkit.GameMode;
import org.bukkit.Sound;

import java.util.ArrayList;
import java.util.List;

public class ConquestMatchRunnable extends MatchRunnable{
    
    private static final ShootarianSound FINISH_SOUND = new ShootarianSound(Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 3.0F, 1.3F);
    
    
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
            
            for(ShootarianPlayer shootarianPlayer : match.getPlayers()){
                ShootarianTeam shootarianTeam = shootarianPlayer.getShootarianTeam();
                if(shootarianTeam == null) continue;
                if(!flagAreaData.getFlagArea().getBoundingBox().isInBox(shootarianPlayer.getLocation().toVector())) continue;
                
                if(match.getShootarianTeams().get(0) == shootarianTeam){
                    territory--;
                }else{
                    territory++;
                }
            }
            
            flagAreaData.addTerritory(territory * 2);
            
            ShootarianTeam shootarianTeam = flagAreaData.getTeam();
            if(shootarianTeam != null && Math.abs(flagAreaData.getTerritory()) == 100){
                shootarianTeam.addPoints(1);
                
                if(Math.abs(beforeTerritory) < 100){
                    shootarianTeam.getTeamMembers().forEach(shootarianPlayer ->
                            shootarianPlayer.sendText("match-conquest-get-area", shootarianTeam.getShootarianColor().getDisplayName(), flagAreaData.getFlagArea().getDisplayName()));
    
                    for(ShootarianTeam otherTeam : shootarianTeam.getOtherTeam()){
                        otherTeam.getTeamMembers().forEach(shootarianPlayer ->
                                shootarianPlayer.sendText("match-conquest-get-area-e", shootarianTeam.getShootarianColor().getDisplayName(), flagAreaData.getFlagArea().getDisplayName()));
                    }
                }
            }
        }
    
    
        if(timeLeft == 0 || match.checkWin()){
            this.cancel();
            match.finish();
            new ResultRunnable(match).start();
            match.getPlayers().forEach(ShootarianPlayer -> ShootarianPlayer.setGameMode(GameMode.SPECTATOR));
            match.playSound(FINISH_SOUND);
        }
        
    
        String min = String.format("%02d", timeLeft % 60);
        //スコアボード
        ShootarianScoreboard scoreboard = match.getScoreboard();
        for(ShootarianPlayer ShootarianPlayer : match.getPlayers()) {
            Lang lang = ShootarianPlayer.getLang();
            List<String> lines = new ArrayList<>();
            //lines.add("");
            lines.add("§a" + MessageManager.getText(lang, "match-map") + " » §r§l" + match.getShootarianMap().getDisplayName(lang));
            lines.add(" ");
            lines.add("§a" + MessageManager.getText(lang, "match-mode") + " » §6§l " + match.getType().getDisplayName(lang));
            lines.add("  ");
            lines.add("§6" + MessageManager.getText(lang, "match-flag") + " » ");
            
            for(FlagAreaData flagAreaData : conquestMatch.getFlagAreaData()){
                ShootarianTeam team = flagAreaData.getTeam();
                if(team == null)
                    lines.add(" §7" + flagAreaData.getFlagArea().getDisplayName() + "§7 : " + " 0%");
                else
                    lines.add(" " + team.getShootarianColor() + flagAreaData.getFlagArea().getDisplayName() + "§7 : " + Math.abs(flagAreaData.getTerritory()) + "%");
            }
    
            lines.add("   ");
            
            ShootarianTeam team0 = match.getShootarianTeams().get(0);
            ShootarianTeam team1 = match.getShootarianTeams().get(1);
            
            String team0Line = team0.getShootarianColor().getDisplayName() + "§7: §f" + (((ConquestMap)conquestMatch.getShootarianMap()).getMaxTicket() - team1.getPoints());
            String team1Line = team1.getShootarianColor().getDisplayName() + "§7: §f" + (((ConquestMap)conquestMatch.getShootarianMap()).getMaxTicket() - team0.getPoints());
            lines.add("§6" + MessageManager.getText(lang, "match-ticket") + " » ");
            lines.add("  " + team0Line);
            lines.add("  " + team1Line);
            lines.add("    ");
            lines.add("§b" + MessageManager.getText(lang, "match-time") + " » §r§l" + timeLeft / 60 + ":" + min);
            
            scoreboard.setSidebarLine(ShootarianPlayer, lines);
        }
        scoreboard.updateSidebar(match.getPlayers());
        
        timeLeft--;
    }
    
    
    @Override
    public synchronized void cancel() throws IllegalStateException {
        super.cancel();
    }
}
