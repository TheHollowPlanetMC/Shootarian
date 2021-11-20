package be4rjp.shootarian.match;

import be4rjp.shootarian.Shootarian;
import be4rjp.shootarian.map.ConquestPlayerClickableGUIRenderer;
import be4rjp.shootarian.match.map.ConquestMap;
import be4rjp.shootarian.match.runnable.MatchWaitRunnable;
import be4rjp.shootarian.match.team.ShootarianColor;
import be4rjp.shootarian.match.team.ShootarianTeam;
import be4rjp.shootarian.player.ShootarianPlayer;
import be4rjp.shootarian.util.ShootarianScoreboard;
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
        File file = new File("plugins/Shootarian", "match-manager.yml");
        file.getParentFile().mkdirs();
    
        if (!file.exists()) {
            Shootarian.getPlugin().saveResource("match-manager.yml", false);
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
    private final Set<ShootarianPlayer> joinedPlayers = new ConcurrentSet<>();
    private final ShootarianScoreboard scoreboard = new ShootarianScoreboard("§6§lShootarian Engine§r " + Shootarian.VERSION, 10);
    
    private Match match = null;
    private MatchWaitRunnable waitRunnable = null;
    
    public MatchManager(String name, String displayName, MatchManageType type, int minPlayer){
        this.name = name;
        this.displayName = displayName;
        this.type = type;
        this.minPlayer = minPlayer;
        
        matchManagerMap.put(name, this);
    }
    
    
    public synchronized void join(ShootarianPlayer shootarianPlayer){
        if(shootarianPlayer.getMatchManager() != null) return;
        if(match == null) createMatch();
        if(match.getMatchStatus() == Match.MatchStatus.FINISHED) createMatch();
        
        if(joinedPlayers.size() == 8){
            shootarianPlayer.sendText("match-join-cannot-number");
            return;
        }
        
        shootarianPlayer.reset();
    
        if(match.getMatchStatus() == Match.MatchStatus.IN_PROGRESS) {
            switch (type) {
                case CONQUEST: {
                    ShootarianTeam team0 = match.getShootarianTeams().get(0);
                    ShootarianTeam team1 = match.getShootarianTeams().get(1);
            
                    int team0PlayerCount = team0.getTeamMembers().size();
                    int team1PlayerCount = team1.getTeamMembers().size();
            
                    if(team0PlayerCount == 32 && team1PlayerCount == 32){
                        shootarianPlayer.sendText("match-join-cannot-number");
                        return;
                    }
            
                    int teamNumber = 0;
                    if (team0PlayerCount >= team1PlayerCount) {
                        team1.join(shootarianPlayer);
                        teamNumber = 1;
                    }else{
                        team0.join(shootarianPlayer);
                    }
                    
                    shootarianPlayer.teleport(match.getShootarianMap().getTeamLocation(teamNumber));
    
                    ConquestPlayerClickableGUIRenderer guiRenderer = new ConquestPlayerClickableGUIRenderer(shootarianPlayer, ((ConquestMatch) match).getConquestStatusRenderer(), ((ConquestMatch) match).getConquestMap().getCanvasData());
                    guiRenderer.start();
                    shootarianPlayer.setPlayerGUIRenderer(guiRenderer);
                    
                    shootarianPlayer.setScoreBoard(match.getScoreboard());
                    joinedPlayers.add(shootarianPlayer);
                    shootarianPlayer.setMatchManager(this);
    
                    shootarianPlayer.sendText("match-join");
                    
                    return;
                }
            }
        }
    
        if(match.getMatchStatus() == Match.MatchStatus.PLAYING_INTRO){
            ShootarianTeam team0 = match.getShootarianTeams().get(0);
            ShootarianTeam team1 = match.getShootarianTeams().get(1);
            int team0PlayerCount = team0.getTeamMembers().size();
            int team1PlayerCount = team1.getTeamMembers().size();
            
            if (team0PlayerCount >= team1PlayerCount) {
                team1.join(shootarianPlayer);
            }else{
                team0.join(shootarianPlayer);
            }
            
            shootarianPlayer.teleport(match.getShootarianMap().getWaitLocation());
        }
        //shootarianPlayer.teleport(match.getShootarianMap().getWaitLocation());
        joinedPlayers.add(shootarianPlayer);
        shootarianPlayer.setMatchManager(this);
        
        /*
        for(int i = 0; i < 20; i++){
            AIManager.createAIPlayer(shootarianPlayer.getLocation(), match, AIType.CONQUEST, AILevel.EASY).thenAccept(aiPlayer -> {
                joinedPlayers.add(aiPlayer);
                aiPlayer.setMatchManager(MatchManager.this);
                //aiPlayer.getCitizensNPC().getNavigator().setTarget(shootarianPlayer.getBukkitPlayer(), true);
            });
        }*/
    
        shootarianPlayer.sendText("match-join");
    }
    
    private synchronized void createMatch(){
        switch (type){
            case CONQUEST:{
                match = new ConquestMatch(ConquestMap.getRandomConquestMap());
                ShootarianColor[] colors = ShootarianColor.getRandomColorPair();
                new ShootarianTeam(match, colors[0]);
                new ShootarianTeam(match, colors[1]);
                match.initialize();
                break;
            }
        }
        
        waitRunnable = new MatchWaitRunnable(this, match, minPlayer);
        waitRunnable.start();
    }
    
    public String getDisplayName() {return displayName;}
    
    public Set<ShootarianPlayer> getJoinedPlayers() {return joinedPlayers;}
    
    public ShootarianScoreboard getScoreboard() {return scoreboard;}
    
    public MatchManageType getType() {return type;}
    
    public Match getMatch() {return match;}
    
    public enum MatchManageType{
        CONQUEST
    }
}
