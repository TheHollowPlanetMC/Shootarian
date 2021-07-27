package be4rjp.shellcase.match.runnable;

import be4rjp.shellcase.language.Lang;
import be4rjp.shellcase.match.ConquestMatch;
import be4rjp.shellcase.language.MessageManager;
import be4rjp.shellcase.match.result.ResultRunnable;
import be4rjp.shellcase.player.ShellCasePlayer;
import be4rjp.shellcase.util.ProgressBar;
import be4rjp.shellcase.util.ShellCaseScoreboard;
import be4rjp.shellcase.util.ShellCaseSound;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Sound;

import java.util.ArrayList;
import java.util.List;

public class ConquestMatchRunnable extends MatchRunnable{
    
    private static final ShellCaseSound FINISH_SOUND = new ShellCaseSound(Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 3.0F, 1.3F);
    
    /**
     * 試合のスケジューラーを作成します。
     *
     * @param match     試合のインスタンス
     * @param timeLimit 試合の最大時間
     */
    public ConquestMatchRunnable(ConquestMatch match, int timeLimit) {
        super(match, timeLimit);
    }
    
    @Override
    public void run() {
    
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
            lines.add("    ");
            lines.add("§b" + MessageManager.getText(lang, "match-time") + " » §r§l" + timeLeft / 60 + ":" + min);
            scoreboard.setSidebarLine(ShellCasePlayer, lines);
        }
        scoreboard.updateSidebar(match.getPlayers());
        
        if(timeLeft == 0){
            this.cancel();
            match.finish();
            new ResultRunnable(match).start();
            match.getPlayers().forEach(ShellCasePlayer -> ShellCasePlayer.setGameMode(GameMode.SPECTATOR));
            match.playSound(FINISH_SOUND);
        }
        timeLeft--;
    }
    
    
    @Override
    public synchronized void cancel() throws IllegalStateException {
        super.cancel();
    }
}
