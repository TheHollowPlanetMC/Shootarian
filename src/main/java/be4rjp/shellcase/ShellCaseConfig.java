package be4rjp.shellcase;

import be4rjp.shellcase.util.ConfigUtil;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class ShellCaseConfig {
    
    private static MySQLConfig mySQLConfig;
    
    private static Location joinLocation;
    
    private static Location lobbyLocation;
    
    private static int runnerThreads;
    
    public static MySQLConfig getMySQLConfig(){return mySQLConfig;}
    
    public static Location getJoinLocation() {return joinLocation;}
    
    public static Location getLobbyLocation() {return lobbyLocation;}
    
    public static int getRunnerThreads() {return runnerThreads;}
    
    public static void load(){
        File file = new File("plugins/ShellCase", "config.yml");
        file.getParentFile().mkdirs();
        
        if(!file.exists()){
            ShellCase.getPlugin().saveResource("config.yml", false);
        }
        
        //ロードと値の保持
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
        
        String host = yml.getString("my-sql.host");
        String port = yml.getString("my-sql.port");
        String database = yml.getString("my-sql.data-base");
        String table = yml.getString("my-sql.table");
        String user = yml.getString("my-sql.user");
        String password = yml.getString("my-sql.password");
        mySQLConfig = new MySQLConfig(host, port, database, table, user, password);
        
        joinLocation = ConfigUtil.getLocationByString(yml.getString("join-location"));
        lobbyLocation = ConfigUtil.getLocationByString(yml.getString("lobby-location"));
        runnerThreads = yml.getInt("runner-threads");
    }
    
    
    public static class MySQLConfig{
        public final String ip, port, database, table, username, password;
    
        public MySQLConfig(String ip, String port, String database, String table, String username, String password) {
            this.ip = ip;
            this.port = port;
            this.database = database;
            this.table = table;
            this.username = username;
            this.password = password;
        }
    }
}
