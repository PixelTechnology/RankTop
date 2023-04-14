package com.yuankong.ranktop.config;

import com.yuankong.ranktop.RankTop;
import org.bukkit.configuration.Configuration;

import java.util.List;

public class LoadConfig {
    private static boolean fightTopEnable;
    private static int f_updateRate;
    private static int f_count;
    private static String f_tableName;

    private static boolean moneyTopEnable;
    private static int m_updateRate;
    private static int m_count;
    private static String m_tableName;
    private static String usePlugin;

    private static boolean hurtTopEnable;
    private static int h_count;
    private static String h_tableName;
    private static List<String> mob;

    private static boolean dungeonTopEnable;
    private static int d_count;
    private static String d_tableName;
    private static List<String> dungeonList;

    private static String banTable;

    public static void load() {
        Configuration config = RankTop.instance.getConfig();
        fightTopEnable = config.getBoolean("战斗力排行榜.enable");
        f_updateRate = config.getInt("战斗力排行榜.updateRate");
        f_count = config.getInt("战斗力排行榜.count");

        moneyTopEnable = config.getBoolean("金币排行榜.enable");
        m_updateRate = config.getInt("金币排行榜.updateRate");
        m_count = config.getInt("金币排行榜.count");
        usePlugin = config.getString("金币排行榜.usePlugin","null");

        hurtTopEnable = config.getBoolean("伤害排行榜.enable");
        h_count = config.getInt("伤害排行榜.count");
        mob = config.getStringList("伤害排行榜.mob");

        dungeonTopEnable = config.getBoolean("副本记录排行榜.enable");
        d_count = config.getInt("副本记录排行榜.count");
        dungeonList = config.getStringList("副本记录排行榜.dungeon");

        f_tableName = config.getString("战斗力排行榜.tableName","rank_fight_top");
        m_tableName = config.getString("金币排行榜.tableName","rank_money_top");
        h_tableName = config.getString("伤害排行榜.tableName","rank_hurt_top");
        d_tableName = config.getString("副本记录排行榜.tableName","rank_dungeon_top");
        banTable = config.getString("banTable","rank_ban_top");
    }

    public static void reload() {
        RankTop.instance.reloadConfig();
        load();
    }

    public static boolean isFightTopEnable() {
        return fightTopEnable;
    }

    public static int getF_updateRate() {
        return f_updateRate;
    }

    public static int getF_count() {
        return f_count;
    }

    public static boolean isMoneyTopEnable() {
        return moneyTopEnable;
    }

    public static int getM_updateRate() {
        return m_updateRate;
    }

    public static int getM_count() {
        return m_count;
    }

    public static boolean isHurtTopEnable() {
        return hurtTopEnable;
    }

    public static int getH_count() {
        return h_count;
    }

    public static boolean isDungeonTopEnable() {
        return dungeonTopEnable;
    }

    public static int getD_count() {
        return d_count;
    }

    public static String getF_tableName() {
        return f_tableName;
    }

    public static String getM_tableName() {
        return m_tableName;
    }

    public static String getH_tableName() {
        return h_tableName;
    }

    public static String getD_tableName() {
        return d_tableName;
    }

    public static String getUsePlugin() {
        return usePlugin;
    }

    public static List<String> getMob() {
        return mob;
    }

    public static List<String> getDungeonList() {
        return dungeonList;
    }

    public static String getBanTable() {
        return banTable;
    }
}