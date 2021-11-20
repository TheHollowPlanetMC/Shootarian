package be4rjp.shootarian.match.team;

import be4rjp.shootarian.match.Match;
import be4rjp.shootarian.player.ShootarianPlayer;
import io.netty.util.internal.ConcurrentSet;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import java.util.HashSet;
import java.util.Set;

/**
 * チーム
 */
public class ShootarianTeam {
    
    //試合のインスタンス
    private final Match match;
    //チームカラー
    private final ShootarianColor shootarianColor;
    //チームの塗りポイント
    private int paints = 0;
    //チームのキルカウント
    private int kills = 0;
    //チームメンバー
    private Set<ShootarianPlayer> teamMembers = new ConcurrentSet<>();
    //スコアボードのチーム
    private final Team team;
    
    /**
     * チームのインスタンス作成
     * @param match
     * @param shootarianColor
     */
    public ShootarianTeam(Match match, ShootarianColor shootarianColor){
        this.match = match;
        this.shootarianColor = shootarianColor;
        
        this.team = match.getScoreboard().getBukkitScoreboard().registerNewTeam(shootarianColor.getDisplayName());
        team.setColor(shootarianColor.getChatColor());
        team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.FOR_OTHER_TEAMS);
        team.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
        team.setSuffix(shootarianColor.getChatColor().toString());
        team.setCanSeeFriendlyInvisibles(true);
        team.setDisplayName(shootarianColor.getDisplayName());
        
        this.match.addShootarianTeam(this);
    }
    
    
    public int getKills() {return kills;}
    
    public int getPoints() {return paints;}
    
    public synchronized void addPoints(int paints) {this.paints += paints;}
    
    public synchronized void addKills(int kills) {this.kills += kills;}
    
    public Match getMatch() {return match;}
    
    public ShootarianColor getShootarianColor() {return shootarianColor;}
    
    public Team getScoreBoardTeam() {return team;}
    
    /**
     * プレイヤーをチームに参加させる
     * @param ShootarianPlayer 参加させるプレイヤー
     */
    public void join(ShootarianPlayer ShootarianPlayer){
        if(ShootarianPlayer.getBukkitPlayer() != null){
            Player player = ShootarianPlayer.getBukkitPlayer();
            //player.setScoreboard(match.getScoreboard().getBukkitScoreboard());
            team.addEntry(player.getName());
        }
        ShootarianPlayer.setShootarianTeam(this);
        teamMembers.add(ShootarianPlayer);
    }

    /**
     * 同じ試合のほかのチームを取得する
     * @return Set<ShootarianTeam>
     */
    public Set<ShootarianTeam> getOtherTeam(){
        Set<ShootarianTeam> shootarianTeams = new HashSet<>();
        for(ShootarianTeam ShootarianTeam : match.getShootarianTeams()){
            if(ShootarianTeam != this) shootarianTeams.add(ShootarianTeam);
        }
        return shootarianTeams;
    }
    
    /**
     * 他のチームのプレイヤーを取得する
     * @return Set<ShootarianPlayer>
     */
    public Set<ShootarianPlayer> getOtherTeamPlayers(){
        Set<ShootarianPlayer> players = new HashSet<>();
        this.getOtherTeam().forEach(ShootarianTeam -> players.addAll(ShootarianTeam.getTeamMembers()));
        return players;
    }

    /**
     * チームメンバーを取得します
     * @return Set<ShootarianPlayer>
     */
    public Set<ShootarianPlayer> getTeamMembers(){
        return teamMembers;
    }
}
