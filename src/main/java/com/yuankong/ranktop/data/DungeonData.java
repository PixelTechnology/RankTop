package com.yuankong.ranktop.data;

import com.yuankong.ranktop.config.LoadConfig;
import org.bukkit.entity.Player;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.UUID;

public class DungeonData extends Data{
    BigDecimal time;
    long date;
    int number;
    String dungeonName;

    public DungeonData(Player player, UUID uuid, String playerName, BigDecimal time, long date, int number,String dungeonName) {
        this.player = player;
        this.uuid = uuid;
        this.playerName = playerName;
        this.time = time;
        this.date = date;
        this.number = number;
        this.dungeonName = dungeonName;
    }

    public BigDecimal getTime() {
        return time;
    }

    public void setTime(BigDecimal time) {
        this.time = time;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getDungeonName() {
        return dungeonName;
    }

    public void setDungeonName(String dungeonName) {
        this.dungeonName = dungeonName;
    }

    public void updateData(int id) throws SQLException {
        DataBase.sqlManager.createReplace(LoadConfig.getD_tableName())
                .setColumnNames(DataBase.id,DataBase.uuid,DataBase.player_name,DataBase.data,DataBase.date,DataBase.number,DataBase.dungeon,DataBase.key)
                .setParams(id,this.uuid.toString(),this.playerName,this.time,this.date,this.number,this.dungeonName,this.dungeonName+id)
                .execute(/*(success) -> {
                    //操作成功回调
                }, (exception, sqlAction) -> {
                    //操作失败回调
                    RankTop.instance.getLogger().warning("伤害数据更新失败");
                }*/);
    }
}
