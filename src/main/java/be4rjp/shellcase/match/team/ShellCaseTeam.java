package be4rjp.shellcase.match.team;

import be4rjp.shellcase.match.Match;
import be4rjp.shellcase.player.ShellCasePlayer;
import io.netty.util.internal.ConcurrentSet;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import java.util.HashSet;
import java.util.Set;

/**
 * チーム
 */
public class ShellCaseTeam {
    
    //試合のインスタンス
    private final Match match;
    //チームカラー
    private final ShellCaseColor ShellCaseColor;
    //チームの塗りポイント
    private int paints = 0;
    //チームのキルカウント
    private int kills = 0;
    //チームメンバー
    private Set<ShellCasePlayer> teamMembers = new ConcurrentSet<>();
    //スコアボードのチーム
    private final Team team;
    
    /**
     * チームのインスタンス作成
     * @param match
     * @param ShellCaseColor
     */
    public ShellCaseTeam(Match match, ShellCaseColor ShellCaseColor){
        this.match = match;
        this.ShellCaseColor = ShellCaseColor;
        
        this.team = match.getScoreboard().getBukkitScoreboard().registerNewTeam(ShellCaseColor.getDisplayName());
        team.setColor(ShellCaseColor.getChatColor());
        team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.FOR_OTHER_TEAMS);
        team.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
        team.setSuffix(ShellCaseColor.getChatColor().toString());
        team.setCanSeeFriendlyInvisibles(true);
        team.setDisplayName(ShellCaseColor.getDisplayName());
        
        this.match.addShellCaseTeam(this);
    }
    
    
    public int getKills() {return kills;}
    
    public int getPaints() {return paints;}
    
    public synchronized void addPaints(int paints) {this.paints += paints;}
    
    public synchronized void addKills(int kills) {this.kills += kills;}
    
    public Match getMatch() {return match;}
    
    public ShellCaseColor getShellCaseColor() {return ShellCaseColor;}
    
    public Team getScoreBoardTeam() {return team;}
    
    /**
     * プレイヤーをチームに参加させる
     * @param ShellCasePlayer 参加させるプレイヤー
     */
    public void join(ShellCasePlayer ShellCasePlayer){
        if(ShellCasePlayer.getBukkitPlayer() != null){
            Player player = ShellCasePlayer.getBukkitPlayer();
            //player.setScoreboard(match.getScoreboard().getBukkitScoreboard());
            team.addEntry(player.getName());
        }
        ShellCasePlayer.setShellCaseTeam(this);
        teamMembers.add(ShellCasePlayer);
    }

    /**
     * 同じ試合のほかのチームを取得する
     * @return Set<ShellCaseTeam>
     */
    public Set<ShellCaseTeam> getOtherTeam(){
        Set<ShellCaseTeam> ShellCaseTeams = new HashSet<>();
        for(ShellCaseTeam ShellCaseTeam : match.getShellCaseTeams()){
            if(ShellCaseTeam != this) ShellCaseTeams.add(ShellCaseTeam);
        }
        return ShellCaseTeams;
    }
    
    /**
     * 他のチームのプレイヤーを取得する
     * @return Set<ShellCasePlayer>
     */
    public Set<ShellCasePlayer> getOtherTeamPlayers(){
        Set<ShellCasePlayer> players = new HashSet<>();
        this.getOtherTeam().forEach(ShellCaseTeam -> players.addAll(ShellCaseTeam.getTeamMembers()));
        return players;
    }

    /**
     * チームメンバーを取得します
     * @return Set<ShellCasePlayer>
     */
    public Set<ShellCasePlayer> getTeamMembers(){
        return teamMembers;
    }
}
