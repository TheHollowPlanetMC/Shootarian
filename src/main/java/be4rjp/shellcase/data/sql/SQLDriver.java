package be4rjp.shellcase.data.sql;

import be4rjp.shellcase.ShellCaseConfig;
import be4rjp.shellcase.data.AchievementData;
import be4rjp.shellcase.data.HeadGearPossessionData;
import be4rjp.shellcase.data.WeaponPossessionData;
import be4rjp.shellcase.language.Lang;
import be4rjp.shellcase.player.costume.HeadGear;
import be4rjp.shellcase.player.costume.HeadGearData;
import be4rjp.shellcase.player.passive.Gear;

public class SQLDriver {
    
    public static void createTable(SQLConnection sqlConnection) throws Exception{
        sqlConnection.execute("CREATE TABLE IF NOT EXISTS " + ShellCaseConfig.getMySQLConfig().table + " (uuid VARCHAR(36), lang TINYINT, kills INT, paints INT, ranks INT, coin INT, weapon VARBINARY(32), gear VARBINARY(2565), equip TINYINT UNSIGNED, head SMALLINT, progress INT, settings INT);");
    }
    
    public static void loadAchievementData(AchievementData achievementData) throws Exception{
        ShellCaseConfig.MySQLConfig mySQLConfig = ShellCaseConfig.getMySQLConfig();
        String uuid = achievementData.getShellCasePlayer().getUUID();
        String notExistExecute = "INSERT INTO " + mySQLConfig.table + "(uuid, lang, kills, paints, ranks, coin, weapon, gear, equip, head, progress, settings) VALUES('" + uuid + "', 0, 0, 0, 0, 0, '" + new String(new byte[32]) + "', '" + new String(new byte[2564]) + "', 0, 0, 0, 2147483647);";
        
        SQLConnection sqlConnection = new SQLConnection(mySQLConfig.ip, mySQLConfig.port, mySQLConfig.database, mySQLConfig.username, mySQLConfig.password);
        createTable(sqlConnection);
        Lang lang = Lang.getLangByID(sqlConnection.getByte(mySQLConfig.table, "lang", "uuid", uuid, notExistExecute));
        int kill = sqlConnection.getInt(mySQLConfig.table, "kills", "uuid", uuid, notExistExecute);
        int paint = sqlConnection.getInt(mySQLConfig.table, "paints", "uuid", uuid, notExistExecute);
        int rank = sqlConnection.getInt(mySQLConfig.table, "ranks", "uuid", uuid, notExistExecute);
        int coin = sqlConnection.getInt(mySQLConfig.table, "coin", "uuid", uuid, notExistExecute);
        byte[] weapon = sqlConnection.getByteArray(mySQLConfig.table, "weapon", "uuid", uuid, notExistExecute);
        byte[] gear = sqlConnection.getByteArray(mySQLConfig.table, "gear", "uuid", uuid, notExistExecute);
        int equip = sqlConnection.getInt(mySQLConfig.table, "equip", "uuid", uuid, notExistExecute);
        int head = sqlConnection.getInt(mySQLConfig.table, "head", "uuid", uuid, notExistExecute);
        int progress = sqlConnection.getInt(mySQLConfig.table, "progress", "uuid", uuid, notExistExecute);
        int settings = sqlConnection.getInt(mySQLConfig.table, "settings", "uuid", uuid, notExistExecute);
        achievementData.getShellCasePlayer().setLang(lang);
        achievementData.setKill(kill);
        achievementData.setPoint(paint);
        achievementData.setRank(rank);
        achievementData.setCoin(coin);
        achievementData.getWeaponPossessionData().load_from_byte_array(weapon);
        achievementData.getHeadGearPossessionData().load_from_byte_array(gear);
        achievementData.getProgressData().setByCombinedID(progress);
        achievementData.getShellCasePlayer().getPlayerSettings().setByCombinedID(settings);
    
        HeadGearPossessionData headGearPossessionData = achievementData.getHeadGearPossessionData();
        if(headGearPossessionData.getHeadGearData(0) == null){
            headGearPossessionData.addHeadGearData(new HeadGearData(HeadGear.getHeadGearBySaveNumber(1), Gear.NO_GEAR, Gear.NO_GEAR, Gear.NO_GEAR));
        }
        HeadGearData headGearData = achievementData.getHeadGearPossessionData().getHeadGearData(head);
        if(headGearData != null) achievementData.getShellCasePlayer().setHeadGearData(headGearData, head);
        achievementData.getShellCasePlayer().equipHeadGear();
    
        
        //achievementData.getShellCasePlayer().equipWeaponClass();
        
        achievementData.getShellCasePlayer().createPassiveInfluence();
        
        sqlConnection.close();
    }
    
    
    public static void saveAchievementData(AchievementData achievementData) throws Exception{
        ShellCaseConfig.MySQLConfig mySQLConfig = ShellCaseConfig.getMySQLConfig();
        String uuid = achievementData.getShellCasePlayer().getUUID();
    
        SQLConnection sqlConnection = new SQLConnection(mySQLConfig.ip, mySQLConfig.port, mySQLConfig.database, mySQLConfig.username, mySQLConfig.password);
        createTable(sqlConnection);
    
        sqlConnection.updateValue(mySQLConfig.table, "lang = " + achievementData.getShellCasePlayer().getLang().getSaveNumber(), "uuid = '" + uuid + "'");
        sqlConnection.updateValue(mySQLConfig.table, "kills = " + achievementData.getKill(), "uuid = '" + uuid + "'");
        sqlConnection.updateValue(mySQLConfig.table, "paints = " + achievementData.getPoint(), "uuid = '" + uuid + "'");
        sqlConnection.updateValue(mySQLConfig.table, "ranks = " + achievementData.getRank(), "uuid = '" + uuid + "'");
        sqlConnection.updateValue(mySQLConfig.table, "coin = " + achievementData.getCoin(), "uuid = '" + uuid + "'");
        sqlConnection.updateValue(mySQLConfig.table, "weapon = " + "'" + new String(achievementData.getWeaponPossessionData().write_to_byte_array()) + "'", "uuid = '" + uuid + "'");
        sqlConnection.updateValue(mySQLConfig.table, "gear = " + "'" + new String(achievementData.getHeadGearPossessionData().write_to_byte_array()) + "'", "uuid = '" + uuid + "'");
        sqlConnection.updateValue(mySQLConfig.table, "head = " + achievementData.getShellCasePlayer().getHeadGearNumber(), "uuid = '" + uuid + "'");
        sqlConnection.updateValue(mySQLConfig.table, "progress = " + achievementData.getProgressData().getCombinedID(), "uuid = '" + uuid + "'");
        sqlConnection.updateValue(mySQLConfig.table, "settings = " + achievementData.getShellCasePlayer().getPlayerSettings().getCombinedID(), "uuid = '" + uuid + "'");
        
        sqlConnection.close();
    }
}
