package be4rjp.shootarian.player.death;

import be4rjp.shootarian.match.Match;
import be4rjp.shootarian.match.team.ShootarianTeam;
import be4rjp.shootarian.player.ShootarianPlayer;
import be4rjp.shootarian.weapon.ShootarianWeapon;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerDeathManager {
    
    public static Set<Player> deathPlayer = ConcurrentHashMap.newKeySet();

    /**
     * プレイヤーの死亡演出
     * @param target 死んだプレイヤー
     * @param killer キルしたプレイヤー、存在しない場合はnull若しくはShootarianPlayerと同じ物を指定
     * @param ShootarianWeapon キルするのに使用した武器、なければnullを指定
     * @param deathType 死亡の種類
     */
    public static void death(ShootarianPlayer target, ShootarianPlayer killer, ShootarianWeapon ShootarianWeapon, DeathType deathType){
        if(killer == null) killer = target;
        
        deathPlayer.add(target.getBukkitPlayer());

        ShootarianTeam ShootarianTeam = target.getShootarianTeam();
        if(ShootarianTeam == null) return;
        target.setDeath(true);
        target.setGameMode(GameMode.SPECTATOR);
        Match match = ShootarianTeam.getMatch();

        //チャットテキスト系
        switch (deathType){
            case KILLED_BY_PLAYER:{
                for(ShootarianPlayer matchPlayer : match.getPlayers()){
                    if(matchPlayer == target || matchPlayer == killer){
                        matchPlayer.sendText("match-kill-message-bold", killer.getDisplayName(true), target.getDisplayName(true), ShootarianWeapon.getDisplayName(matchPlayer.getLang()));
                    }else{
                        matchPlayer.sendText("match-kill-message", killer.getDisplayName(), target.getDisplayName(), ShootarianWeapon.getDisplayName(matchPlayer.getLang()));
                    }
                }
                break;
            }
            
            case FELL_OUT_OF_THE_WORLD:{
                match.getPlayers().forEach(ShootarianPlayer -> ShootarianPlayer.sendText("match-fall-void-message", target.getDisplayName()));
                break;
            }
        }
    
        int index = match.getShootarianTeams().indexOf(ShootarianTeam);
        Location respawnLocation = match.getShootarianMap().getTeamLocation(index);
        new PlayerDeathRunnable(respawnLocation, target, killer, ShootarianWeapon, deathType).start();
    }
}
