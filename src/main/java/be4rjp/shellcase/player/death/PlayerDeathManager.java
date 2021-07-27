package be4rjp.shellcase.player.death;

import be4rjp.shellcase.match.Match;
import be4rjp.shellcase.match.team.ShellCaseTeam;
import be4rjp.shellcase.player.ShellCasePlayer;
import be4rjp.shellcase.weapon.ShellCaseWeapon;
import org.bukkit.GameMode;
import org.bukkit.Location;

public class PlayerDeathManager {

    /**
     * プレイヤーの死亡演出
     * @param target 死んだプレイヤー
     * @param killer キルしたプレイヤー、存在しない場合はnull若しくはShellCasePlayerと同じ物を指定
     * @param ShellCaseWeapon キルするのに使用した武器、なければnullを指定
     * @param deathType 死亡の種類
     */
    public static void death(ShellCasePlayer target, ShellCasePlayer killer, ShellCaseWeapon ShellCaseWeapon, DeathType deathType){
        if(killer == null) killer = target;

        ShellCaseTeam ShellCaseTeam = target.getShellCaseTeam();
        if(ShellCaseTeam == null) return;
        target.setDeath(true);
        target.setGameMode(GameMode.SPECTATOR);
        Match match = ShellCaseTeam.getMatch();

        //チャットテキスト系
        switch (deathType){
            case KILLED_BY_PLAYER:{
                for(ShellCasePlayer matchPlayer : match.getPlayers()){
                    if(matchPlayer == target || matchPlayer == killer){
                        matchPlayer.sendText("match-kill-message-bold", killer.getDisplayName(true), target.getDisplayName(true), ShellCaseWeapon.getDisplayName(matchPlayer.getLang()));
                    }else{
                        matchPlayer.sendText("match-kill-message", killer.getDisplayName(), target.getDisplayName(), ShellCaseWeapon.getDisplayName(matchPlayer.getLang()));
                    }
                }
                break;
            }
            
            case FELL_OUT_OF_THE_WORLD:{
                match.getPlayers().forEach(ShellCasePlayer -> ShellCasePlayer.sendText("match-fall-void-message", target.getDisplayName()));
                break;
            }
        }
    
        int index = match.getShellCaseTeams().indexOf(ShellCaseTeam);
        Location respawnLocation = match.getShellCaseMap().getTeamLocation(index);
        new PlayerDeathRunnable(respawnLocation, target, killer, ShellCaseWeapon, deathType).start();
    }
}
