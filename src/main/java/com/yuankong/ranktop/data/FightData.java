package com.yuankong.ranktop.data;

import com.yuankong.ranktop.config.LoadConfig;
import org.bukkit.entity.Player;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.UUID;

public class FightData extends Data{
    BigDecimal fightValue;

    public FightData(Player player, UUID uuid, String playerName, BigDecimal fightValue) {
        this.player = player;
        this.uuid = uuid;
        this.playerName = playerName;
        this.fightValue = fightValue;
    }

    public BigDecimal getFightValue() {
        return fightValue;
    }

    public void setFightValue(BigDecimal fightValue) {
        this.fightValue = fightValue;
    }

    public void updateData(int id) throws SQLException {
        DataBase.sqlManager.createReplace(LoadConfig.getF_tableName())
                .setColumnNames(DataBase.id,DataBase.uuid,DataBase.player_name,DataBase.data)
                .setParams(id,this.uuid.toString(),this.playerName,this.fightValue.toString())
                .execute(/*(success) -> {
                    //操作成功回调
                }, (exception, sqlAction) -> {
                    RankTop.instance.getLogger().warning(exception.getMessage());
                    //操作失败回调
                    RankTop.instance.getLogger().warning("战斗力数据更新失败");
                }*/);
    }
}
