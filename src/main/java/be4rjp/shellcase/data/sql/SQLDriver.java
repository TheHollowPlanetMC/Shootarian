package be4rjp.shellcase.data.sql;

import be4rjp.shellcase.ShellCaseConfig;
import be4rjp.shellcase.data.AchievementData;
import be4rjp.shellcase.data.GunWeaponPossessionData;
import be4rjp.shellcase.data.HeadGearPossessionData;
import be4rjp.shellcase.language.Lang;
import be4rjp.shellcase.player.costume.HeadGear;
import be4rjp.shellcase.weapon.gun.GunStatusData;
import be4rjp.shellcase.weapon.gun.GunWeapon;

public class SQLDriver {
    
    public static void createTable(SQLConnection sqlConnection) throws Exception{
        sqlConnection.execute("CREATE TABLE IF NOT EXISTS " + ShellCaseConfig.getMySQLConfig().table + " (uuid VARCHAR(36), lang TINYINT, kills INT, points INT, ranks INT, coin INT, weapon VARBINARY(38912), gear VARBINARY(256), gadget VARBINARY(64), equip BIGINT UNSIGNED, head SMALLINT, progress VARBINARY(512), quest VARBINARY(2048), settings INT);");
    }
    
    public static void loadAchievementData(AchievementData achievementData) throws Exception{
        ShellCaseConfig.MySQLConfig mySQLConfig = ShellCaseConfig.getMySQLConfig();
        String uuid = achievementData.getShellCasePlayer().getUUID();
        String notExistExecute = "INSERT INTO " + mySQLConfig.table + "(uuid, lang, kills, points, ranks, coin, weapon, gear, gadget, equip, head, progress, quest, settings) VALUES('" + uuid + "', 0, 0, 0, 0, 0, '" + new String(new byte[38912]) + "', '" + new String(new byte[256]) + "', '" + new String(new byte[64]) + "', 0, 0, '" + new String(new byte[512]) + "', '" + new String(new byte[2048]) + "', 2147483647);";
        
        SQLConnection sqlConnection = new SQLConnection(mySQLConfig.ip, mySQLConfig.port, mySQLConfig.database, mySQLConfig.username, mySQLConfig.password);
        createTable(sqlConnection);
        Lang lang = Lang.getLangByID(sqlConnection.getByte(mySQLConfig.table, "lang", "uuid", uuid, notExistExecute));
        int kill = sqlConnection.getInt(mySQLConfig.table, "kills", "uuid", uuid, notExistExecute);
        int point = sqlConnection.getInt(mySQLConfig.table, "points", "uuid", uuid, notExistExecute);
        int rank = sqlConnection.getInt(mySQLConfig.table, "ranks", "uuid", uuid, notExistExecute);
        int coin = sqlConnection.getInt(mySQLConfig.table, "coin", "uuid", uuid, notExistExecute);
        byte[] weapon = sqlConnection.getByteArray(mySQLConfig.table, "weapon", "uuid", uuid, notExistExecute);
        byte[] gear = sqlConnection.getByteArray(mySQLConfig.table, "gear", "uuid", uuid, notExistExecute);
        byte[] gadget = sqlConnection.getByteArray(mySQLConfig.table, "gadget", "uuid", uuid, notExistExecute);
        long equip = sqlConnection.getLong(mySQLConfig.table, "equip", "uuid", uuid, notExistExecute);
        int head = sqlConnection.getInt(mySQLConfig.table, "head", "uuid", uuid, notExistExecute);
        byte[] progress = sqlConnection.getByteArray(mySQLConfig.table, "progress", "uuid", uuid, notExistExecute);
        byte[] quest = sqlConnection.getByteArray(mySQLConfig.table, "quest", "uuid", uuid, notExistExecute);
        int settings = sqlConnection.getInt(mySQLConfig.table, "settings", "uuid", uuid, notExistExecute);
        achievementData.getShellCasePlayer().setLang(lang);
        achievementData.setKill(kill);
        achievementData.setPoint(point);
        achievementData.setRank(rank);
        achievementData.setCoin(coin);
        achievementData.getWeaponPossessionData().load_from_byte_array(weapon);
        achievementData.getHeadGearPossessionData().load_from_byte_array(gear);
        achievementData.getGadgetPossessionData().load_from_byte_array(gadget);
        achievementData.getProgressData().load_from_byte_array(progress);
        achievementData.getQuestProgress().load_from_byte_array(quest);
        achievementData.getShellCasePlayer().getPlayerSettings().setByCombinedID(settings);
    
        GunWeaponPossessionData weaponPossessionData = achievementData.getWeaponPossessionData();
        if(!weaponPossessionData.hasWeapon(0)){
            GunStatusData gunStatusData = new GunStatusData(GunWeapon.getGunWeaponBySaveNumber(0), achievementData.getShellCasePlayer());
            weaponPossessionData.setGunStatusData(gunStatusData);
        }
        
        HeadGearPossessionData headGearPossessionData = achievementData.getHeadGearPossessionData();
        if(!headGearPossessionData.hasHeadGear(HeadGear.getHeadGearBySaveNumber(0))){
            headGearPossessionData.setHeadGear(HeadGear.getHeadGearBySaveNumber(0));
        }
        HeadGear headGear = HeadGear.getHeadGearBySaveNumber(head);
        if(headGear != null) achievementData.getShellCasePlayer().setHeadGear(headGear);
        achievementData.getShellCasePlayer().equipHeadGear();
    
        
        achievementData.getShellCasePlayer().getWeaponClass().setByCombinedID(equip, achievementData);
        achievementData.getShellCasePlayer().giveItems();
        
        sqlConnection.close();
    }
    
    
    public static void saveAchievementData(AchievementData achievementData) throws Exception{
        ShellCaseConfig.MySQLConfig mySQLConfig = ShellCaseConfig.getMySQLConfig();
        String uuid = achievementData.getShellCasePlayer().getUUID();
    
        SQLConnection sqlConnection = new SQLConnection(mySQLConfig.ip, mySQLConfig.port, mySQLConfig.database, mySQLConfig.username, mySQLConfig.password);
        createTable(sqlConnection);
    
        sqlConnection.updateValue(mySQLConfig.table, "lang = " + achievementData.getShellCasePlayer().getLang().getSaveNumber(), "uuid = '" + uuid + "'");
        sqlConnection.updateValue(mySQLConfig.table, "kills = " + achievementData.getKill(), "uuid = '" + uuid + "'");
        sqlConnection.updateValue(mySQLConfig.table, "points = " + achievementData.getPoint(), "uuid = '" + uuid + "'");
        sqlConnection.updateValue(mySQLConfig.table, "ranks = " + achievementData.getRank(), "uuid = '" + uuid + "'");
        sqlConnection.updateValue(mySQLConfig.table, "coin = " + achievementData.getCoin(), "uuid = '" + uuid + "'");
        sqlConnection.updateByteValue(mySQLConfig.table, "weapon", achievementData.getWeaponPossessionData().write_to_byte_array(), "uuid = '" + uuid + "'");
        sqlConnection.updateByteValue(mySQLConfig.table, "gear", achievementData.getHeadGearPossessionData().write_to_byte_array(), "uuid = '" + uuid + "'");
        sqlConnection.updateByteValue(mySQLConfig.table, "gadget", achievementData.getGadgetPossessionData().write_to_byte_array(), "uuid = '" + uuid + "'");
        sqlConnection.updateValue(mySQLConfig.table, "equip = " + achievementData.getShellCasePlayer().getWeaponClass().getCombinedID(), "uuid = '" + uuid + "'");
        sqlConnection.updateValue(mySQLConfig.table, "head = " + achievementData.getShellCasePlayer().getHeadGearNumber(), "uuid = '" + uuid + "'");
        sqlConnection.updateByteValue(mySQLConfig.table, "progress", achievementData.getProgressData().write_to_byte_array(), "uuid = '" + uuid + "'");
        sqlConnection.updateByteValue(mySQLConfig.table, "quest", achievementData.getQuestProgress().write_to_byte_array(), "uuid = '" + uuid + "'");
        sqlConnection.updateValue(mySQLConfig.table, "settings = " + achievementData.getShellCasePlayer().getPlayerSettings().getCombinedID(), "uuid = '" + uuid + "'");
        
        sqlConnection.close();
    }
}
