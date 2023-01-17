package com.yuankong.ranktop.data;

import cc.carm.lib.easysql.api.SQLManager;
import com.yuankong.ranktop.RankTop;
import com.yuankong.ranktop.config.LoadConfig;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class DataBase {
    public static SQLManager sqlManager;

    public static final String id = "id";
    public static final String uuid = "uuid";
    public static final String player_name = "player_name";
    public static final String data = "data";
    public static final String date = "date";
    public static final String number = "number";
    public static final String dungeon = "dungeon";
    public static final String mob = "mob";
    public static final String key = "key";

    public static void createTable() {
        if (sqlManager == null) {
            RankTop.instance.getLogger().warning("createTable - 未连接数据库！");
            return;
        }
        sqlManager.createTable(LoadConfig.getF_tableName())
                .addColumn(id, "BIGINT NOT NULL UNIQUE KEY")
                .addColumn(uuid, "VARCHAR(40) NOT NULL UNIQUE KEY")
                .addColumn(player_name, "VARCHAR(20) NOT NULL")
                .addColumn(data, "VARCHAR(25) NOT NULL")
                .build().executeAsync((success) -> {
                    //操作成功回调
                    RankTop.instance.getLogger().info("战力表加载成功!");
                }, (exception, sqlAction) -> {
                    //操作失败回调
                    RankTop.instance.getLogger().warning(exception.getMessage());
                    RankTop.instance.getLogger().warning("战力表加载失败，可能数据库连接失败!");
                });

        /*sqlManager.createTable(LoadConfig.getM_tableName())
                .addColumn(id, "TINYINT NOT NULL UNIQUE KEY")
                .addColumn(uuid, "VARCHAR(40) NOT NULL")
                .addColumn(player_name, "VARCHAR(20) NOT NULL")
                .addColumn(data, "VARCHAR(25) NOT NULL")
                .build().executeAsync((success) -> {
                    //操作成功回调
                    RankTop.instance.getLogger().info("金币表加载成功!");
                }, (exception, sqlAction) -> {
                    //操作失败回调
                    RankTop.instance.getLogger().warning("金币表加载失败，可能数据库连接失败!");
                });*/

        sqlManager.createTable(LoadConfig.getH_tableName())
                .addColumn(uuid, "VARCHAR(40) NOT NULL")
                .addColumn(id, "BIGINT NOT NULL")
                .addColumn(player_name, "VARCHAR(20) NOT NULL")
                .addColumn(data, "VARCHAR(25) NOT NULL")
                .addColumn(date, "VARCHAR(20) NOT NULL")
                .addColumn(mob, "VARCHAR(20) NOT NULL")
                .addColumn(key, "VARCHAR(23) NOT NULL UNIQUE KEY")
                .build().executeAsync((success) -> {
                    //操作成功回调
                    RankTop.instance.getLogger().info("伤害表加载成功!");
                }, (exception, sqlAction) -> {
                    //操作失败回调
                    RankTop.instance.getLogger().warning(exception.getMessage());
                    RankTop.instance.getLogger().warning("伤害表加载失败，可能数据库连接失败!");
                });
        sqlManager.createTable(LoadConfig.getD_tableName())
                .addColumn(id, "BIGINT NOT NULL")
                .addColumn(uuid, "VARCHAR(40) NOT NULL")
                .addColumn(player_name, "VARCHAR(20) NOT NULL")
                .addColumn(data, "VARCHAR(25) NOT NULL")
                .addColumn(date, "VARCHAR(20) NOT NULL")
                .addColumn(number, "BIGINT NOT NULL")
                .addColumn(dungeon, "VARCHAR(20) NOT NULL")
                .addColumn(key, "VARCHAR(23) NOT NULL UNIQUE KEY")
                .build().executeAsync((success) -> {
                    //操作成功回调.
                    RankTop.instance.getLogger().info("副本表加载成功!");
                }, (exception, sqlAction) -> {
                    //操作失败回调
                    RankTop.instance.getLogger().warning(exception.getMessage());
                    RankTop.instance.getLogger().warning("副本表加载失败，可能数据库连接失败!");
                });
    }


    public static void updateFightRank(){
        sqlManager.createQuery()
                .inTable(LoadConfig.getF_tableName())
                .selectColumns(uuid,player_name,data)
                .orderBy(id,true)
                .build().executeAsync((success) -> {
                    List<FightData> fightDataList = new ArrayList<>();
                    ResultSet resultSet = success.getResultSet();
                    while (resultSet.next()){
                        UUID uuid = UUID.fromString(resultSet.getString(DataBase.uuid));
                        String name = resultSet.getString(player_name);
                        BigDecimal data = new BigDecimal(resultSet.getString(DataBase.data));
                        fightDataList.add(new FightData(null,uuid,name,data));
                    }
                    Data.fightRank = fightDataList;
                }, (exception, sqlAction) -> {
                    //操作失败回调
                    RankTop.instance.getLogger().warning(exception.getMessage());
                    RankTop.instance.getLogger().warning("更新战力数据时数据获取失败");
                });
    }

    public static void updateDamageRank(){
        sqlManager.createQuery()
                .inTable(LoadConfig.getH_tableName())
                .selectColumns(id,uuid, player_name, mob, data, date)
                .orderBy(id, true)
                .build().executeAsync((success) -> {
                    ResultSet resultSet = success.getResultSet();
                    HashMap<String,List<HurtData>> damageRank = new HashMap<>();
                    for(String str:LoadConfig.getMob()){
                        damageRank.put(str,new ArrayList<>());
                    }
                    while (resultSet.next()) {
                        int id = resultSet.getInt(DataBase.id);
                        UUID uuid = UUID.fromString(resultSet.getString(DataBase.uuid));
                        String name = resultSet.getString(player_name);
                        BigDecimal data = new BigDecimal(resultSet.getString(DataBase.data));
                        String mob = resultSet.getString(DataBase.mob);
                        long date = Long.parseLong(resultSet.getString(DataBase.date));
                        damageRank.get(mob).add(id-1,new HurtData(null, uuid, name, data, date, mob));
                    }
                    Data.damageRank = damageRank;
                }, (exception, sqlAction) -> {
                    //操作失败回调
                    RankTop.instance.getLogger().warning(exception.getMessage());
                    RankTop.instance.getLogger().warning("刷新伤害数据时数据获取失败");
                });

    }

    public static void updateDungeonRank(){
        sqlManager.createQuery()
                .inTable(LoadConfig.getD_tableName())
                .selectColumns(uuid,player_name,data,date,number,dungeon)
                .orderBy(id, true)
                .build().executeAsync((success) -> {
                    HashMap<String,List<DungeonData>> dungeonRank = new HashMap<>();
                    for(String str:LoadConfig.getDungeonList()){
                        dungeonRank.put(str,new ArrayList<>());
                    }
                    ResultSet resultSet = success.getResultSet();
                    while (resultSet.next()) {
                        UUID uuid = UUID.fromString(resultSet.getString(DataBase.uuid));
                        String name = resultSet.getString(DataBase.player_name);
                        BigDecimal data = new BigDecimal(resultSet.getString(DataBase.data));
                        long date = Long.parseLong(resultSet.getString(DataBase.date));
                        int number = resultSet.getInt(DataBase.number);
                        String dungeon = resultSet.getString(DataBase.dungeon);
                        dungeonRank.get(dungeon).add(new DungeonData(null,uuid,name,data,date,number,dungeon));
                    }
                    Data.dungeonRank = dungeonRank;
                }, (exception, sqlAction) -> {
                    //操作失败回调
                    RankTop.instance.getLogger().warning(exception.getMessage());
                    RankTop.instance.getLogger().warning("刷新副本数据时数据获取失败");
                });
    }


}