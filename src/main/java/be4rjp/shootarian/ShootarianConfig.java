package be4rjp.shootarian;

import be4rjp.shootarian.util.ConfigUtil;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class ShootarianConfig {
    
    private static MySQLConfig mySQLConfig;
    
    private static Location joinLocation;
    
    private static Location lobbyLocation;
    
    private static int runnerThreads;
    
    private static boolean isRPGMode = false;
    
    public static MySQLConfig getMySQLConfig(){return mySQLConfig;}
    
    public static Location getJoinLocation() {return joinLocation;}
    
    public static Location getLobbyLocation() {return lobbyLocation;}
    
    public static int getRunnerThreads() {return runnerThreads;}
    
    public static boolean isRPGMode(){return isRPGMode;}
    
    public static void load(){
        File file = new File("plugins/Shootarian", "config.yml");
        file.getParentFile().mkdirs();
        
        if(!file.exists()){
            Shootarian.getPlugin().saveResource("config.yml", false);
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
        if(yml.contains("rpg-mode")) isRPGMode = yml.getBoolean("rpg-mode");
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
