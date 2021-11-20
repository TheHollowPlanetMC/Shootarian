package be4rjp.shootarian.match.result;

import be4rjp.shootarian.Shootarian;
import be4rjp.shootarian.ShootarianConfig;
import be4rjp.shootarian.match.Match;
import be4rjp.shootarian.match.team.ShootarianTeam;
import be4rjp.shootarian.player.ShootarianPlayer;
import be4rjp.shootarian.util.ShootarianSound;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class ResultRunnable extends BukkitRunnable {
    
    private static final ShootarianSound FINISH_SOUND = new ShootarianSound(Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 1.0F, 1.2F);
    private static final ShootarianSound WIN_SOUND = new ShootarianSound(Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
    
    
    private final Match match;
    
    private final ShootarianTeam winTeam;
    private ShootarianTeam loseTeam;
    
    private int i = 0;
    
    public ResultRunnable(Match match){
        this.match = match;
        
        this.winTeam = match.getWinner();
        if(winTeam != null) {
            for (ShootarianTeam shootarianTeam : winTeam.getOtherTeam()) {
                loseTeam = shootarianTeam;
                break;
            }
        }
    }
    
    @Override
    public void run() {
        
        if(i == 0){
            for(ShootarianPlayer shootarianPlayer : match.getPlayers()){
                shootarianPlayer.setGameMode(GameMode.SPECTATOR);
                shootarianPlayer.playSound(FINISH_SOUND);
                Player player = shootarianPlayer.getBukkitPlayer();
                if(player != null){
                    if(winTeam == null){
                        String title = "§7§lDRAW";
                        
                        ShootarianTeam other = null;
                        for(ShootarianTeam shootarianTeam : shootarianPlayer.getShootarianTeam().getOtherTeam()){
                            other = shootarianTeam;
                        }
                        
                        String subTitle = shootarianPlayer.getShootarianTeam().getShootarianColor().getChatColor().toString() + shootarianPlayer.getShootarianTeam().getPoints() + " §7vs " + other.getShootarianColor().getChatColor().toString() + other.getPoints();
                        player.sendTitle(title, subTitle, 10, 100, 20);
                        shootarianPlayer.sendText("none-s", title);
                        shootarianPlayer.sendText("none-s", subTitle);
                    } else {
                        String title;
                        String subTitle;
                        if (shootarianPlayer.getShootarianTeam() == winTeam) {
                            title = "§a§lYOU WON";
                            subTitle = winTeam.getShootarianColor().getChatColor().toString() + winTeam.getPoints() + " §7vs " + loseTeam.getShootarianColor().getChatColor().toString() + loseTeam.getPoints();
                        } else {
                            title = "§c§lYOU LOSE";
                            subTitle = loseTeam.getShootarianColor().getChatColor().toString() + loseTeam.getPoints() + " §7vs " + winTeam.getShootarianColor().getChatColor().toString() + winTeam.getPoints();
                        }
                        player.sendTitle(title, subTitle, 10, 100, 20);
                        shootarianPlayer.sendText("none-s", title);
                        shootarianPlayer.sendText("none-s", subTitle);
                    }
                }
            }
        }
        
        if(i == 1){
            match.getPlayers().forEach(shootarianPlayer -> shootarianPlayer.playSound(WIN_SOUND));
        }
        
        
        if(i == 8){
            for(ShootarianPlayer shootarianPlayer : match.getPlayers()){
                shootarianPlayer.setGameMode(GameMode.ADVENTURE, () -> {
                    shootarianPlayer.teleportSynced(ShootarianConfig.getLobbyLocation());
                });
                cancel();
            }
            match.end();
        }
        
        i++;
    }
    
    public void start(){this.runTaskTimerAsynchronously(Shootarian.getPlugin(), 0, 20);}
}
