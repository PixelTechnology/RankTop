package com.yuankong.ranktop.data;

import com.yuankong.ranktop.config.LoadConfig;
import org.bukkit.entity.Player;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.util.UUID;

public class HurtData extends Data{

    BigDecimal damage;
    long date;
    String mob;

    public HurtData(Player player, UUID uuid, String playerName, BigDecimal damage, long date,String mob) {
        this.player = player;
        this.uuid = uuid;
        this.playerName = playerName;
        this.damage = damage.setScale(2, RoundingMode.DOWN);
        this.date = date;
        this.mob = mob;
    }

    public BigDecimal getDamage() {
        return damage;
    }

    public void setDamage(BigDecimal damage) {
        this.damage = damage.setScale(2, RoundingMode.DOWN);
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getMob() {
        return mob;
    }

    public void setMob(String mob) {
        this.mob = mob;
    }

    /*public void updateData(int id) throws SQLException {
        DataBase.sqlManager.createDelete(LoadConfig.getH_tableName())
                .addCondition(DataBase.mob, this.mob)
                .addCondition(DataBase.id, id)
                .build().execute(*//*(success) -> updateData2(id), (exception, sqlAction) -> {
                    //操作失败回调
                    updateData2(id);
                }*//*);
        updateData2(id);

    }*/

    public void updateData(int id) throws SQLException {
        DataBase.sqlManager.createReplace(LoadConfig.getH_tableName())
                .setColumnNames(DataBase.id, DataBase.uuid, DataBase.player_name, DataBase.data, DataBase.date,DataBase.mob,DataBase.key)
                .setParams(id, this.uuid.toString(), this.playerName, this.damage.toString(), this.date, this.mob,this.mob+id)
                .execute(/*(s) -> {
                    //操作成功回调
                }, (e, s) -> {
                    //操作失败回调
                    RankTop.instance.getLogger().warning(e.getMessage());
                    RankTop.instance.getLogger().warning("副本数据更新失败");
                }*/);
    }
}
