package be4rjp.shellcase.match;

import be4rjp.shellcase.ShellCase;
import be4rjp.shellcase.map.ConquestPlayerClickableGUIRenderer;
import be4rjp.shellcase.match.map.ConquestMap;
import be4rjp.shellcase.match.map.ShellCaseMap;
import be4rjp.shellcase.match.runnable.MatchWaitRunnable;
import be4rjp.shellcase.match.team.ShellCaseColor;
import be4rjp.shellcase.match.team.ShellCaseTeam;
import be4rjp.shellcase.player.ShellCasePlayer;
import be4rjp.shellcase.util.ShellCaseScoreboard;
import io.netty.util.internal.ConcurrentSet;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.*;

public class MatchManager {
    
    //名前とMatchManagerのマップ
    private static Map<String, MatchManager> matchManagerMap = new HashMap<>();
    
    /**
     * 名前からMatchManagerを取得する
     * @param name MatchManagerの名前
     * @return MatchManager
     */
    public static MatchManager getMatchManager(String name){return matchManagerMap.get(name);}
    
    public static Collection<MatchManager> getMatchManagers(){return matchManagerMap.values();}
    
    
    public static void load() {
        File file = new File("plugins/ShellCase", "match-manager.yml");
        file.getParentFile().mkdirs();
    
        if (!file.exists()) {
            ShellCase.getPlugin().saveResource("match-manager.yml", false);
        }
    
        //ロードと値の保持
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
        for(String name : Objects.requireNonNull(yml.getConfigurationSection("match-manager")).getKeys(false)){
            String displayName = yml.getString("match-manager." + name + ".display-name");
            MatchManageType type = MatchManageType.valueOf(yml.getString("match-manager." + name + ".type"));
            int minPlayer = yml.getInt("match-manager." + name + ".min-player");
            
            new MatchManager(name, displayName, type, minPlayer);
        }
    }
    
    
    
    private final String name;
    private final String displayName;
    private final MatchManageType type;
    private final int minPlayer;
    private final Set<ShellCasePlayer> joinedPlayers = new ConcurrentSet<>();
    private final ShellCaseScoreboard scoreboard = new ShellCaseScoreboard("§6§lshellcase§r " + ShellCase.VERSION, 10);
    
    private Match match = null;
    private MatchWaitRunnable waitRunnable = null;
    
    public MatchManager(String name, String displayName, MatchManageType type, int minPlayer){
        this.name = name;
        this.displayName = displayName;
        this.type = type;
        this.minPlayer = minPlayer;
        
        matchManagerMap.put(name, this);
    }
    
    
    public synchronized void join(ShellCasePlayer shellCasePlayer){
        if(shellCasePlayer.getMatchManager() != null) return;
        if(match == null) createMatch();
        if(match.getMatchStatus() == Match.MatchStatus.FINISHED) createMatch();
        
        if(joinedPlayers.size() == 8){
            shellCasePlayer.sendText("match-join-cannot-number");
            return;
        }
        
        shellCasePlayer.reset();
    
        if(match.getMatchStatus() == Match.MatchStatus.IN_PROGRESS) {
            switch (type) {
                case CONQUEST: {
                    ShellCaseTeam team0 = match.getShellCaseTeams().get(0);
                    ShellCaseTeam team1 = match.getShellCaseTeams().get(1);
            
                    int team0PlayerCount = team0.getTeamMembers().size();
                    int team1PlayerCount = team1.getTeamMembers().size();
            
                    if(team0PlayerCount == 32 && team1PlayerCount == 32){
                        shellCasePlayer.sendText("match-join-cannot-number");
                        return;
                    }
            
                    int teamNumber = 0;
                    if (team0PlayerCount >= team1PlayerCount) {
                        team1.join(shellCasePlayer);
                        teamNumber = 1;
                    }else{
                        team0.join(shellCasePlayer);
                    }
                    
                    shellCasePlayer.teleport(match.getShellCaseMap().getTeamLocation(teamNumber));
    
                    ConquestPlayerClickableGUIRenderer guiRenderer = new ConquestPlayerClickableGUIRenderer(shellCasePlayer, ((ConquestMatch) match).getConquestStatusRenderer(), ((ConquestMatch) match).getConquestMap().getCanvasData());
                    guiRenderer.start();
                    shellCasePlayer.setPlayerGUIRenderer(guiRenderer);
                    
                    shellCasePlayer.setScoreBoard(match.getScoreboard());
                    joinedPlayers.add(shellCasePlayer);
                    shellCasePlayer.setMatchManager(this);
    
                    shellCasePlayer.sendText("match-join");
                    
                    return;
                }
            }
        }
    
        if(match.getMatchStatus() == Match.MatchStatus.PLAYING_INTRO){
            ShellCaseTeam team0 = match.getShellCaseTeams().get(0);
            ShellCaseTeam team1 = match.getShellCaseTeams().get(1);
            int team0PlayerCount = team0.getTeamMembers().size();
            int team1PlayerCount = team1.getTeamMembers().size();
            
            if (team0PlayerCount >= team1PlayerCount) {
                team1.join(shellCasePlayer);
            }else{
                team0.join(shellCasePlayer);
            }
            
            shellCasePlayer.teleport(match.getShellCaseMap().getWaitLocation());
        }
        //shellCasePlayer.teleport(match.getShellCaseMap().getWaitLocation());
        joinedPlayers.add(shellCasePlayer);
        shellCasePlayer.setMatchManager(this);
    
        shellCasePlayer.sendText("match-join");
    }
    
    private synchronized void createMatch(){
        switch (type){
            case CONQUEST:{
                match = new ConquestMatch(ConquestMap.getRandomConquestMap());
                ShellCaseColor[] colors = ShellCaseColor.getRandomColorPair();
                new ShellCaseTeam(match, colors[0]);
                new ShellCaseTeam(match, colors[1]);
                match.initialize();
                break;
            }
        }
        
        waitRunnable = new MatchWaitRunnable(this, match, minPlayer);
        waitRunnable.start();
    }
    
    public String getDisplayName() {return displayName;}
    
    public Set<ShellCasePlayer> getJoinedPlayers() {return joinedPlayers;}
    
    public ShellCaseScoreboard getScoreboard() {return scoreboard;}
    
    public MatchManageType getType() {return type;}
    
    public Match getMatch() {return match;}
    
    public enum MatchManageType{
        CONQUEST
    }
}
