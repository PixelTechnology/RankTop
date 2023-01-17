package com.yuankong.ranktop.init;

import com.yuankong.easylib.util.timer.TimerHandler;
import com.yuankong.ranktop.RankTop;
import com.yuankong.ranktop.config.LoadConfig;
import com.yuankong.ranktop.data.Data;
import com.yuankong.ranktop.data.DataBase;
import com.yuankong.ranktop.data.FightData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FightTimer extends TimerHandler {
    @Override
    public void times(long times) {
        if(LoadConfig.isFightTopEnable()){
            updateFightRank();
            this.timerSleep(LoadConfig.getF_updateRate() * 1000L);
        }
    }

    private void updateFightRank(){
        Data.fightSet.clear();
        for(Player player:Bukkit.getServer().getOnlinePlayers()){
            if(!player.isOnline()){
                continue;
            }
            BigDecimal bigDecimal = new BigDecimal(Init.getFightValues(player));
            Data.fightSet.add(new FightData(player,player.getUniqueId(),player.getName(), bigDecimal));
        }
        DataBase.sqlManager.createQuery()
                .inTable(LoadConfig.getF_tableName())
                .selectColumns(DataBase.uuid,DataBase.player_name,DataBase.data)
                .orderBy(DataBase.id,true)
                .build().executeAsync((success) -> {
                    ResultSet resultSet = success.getResultSet();
                    while (resultSet.next()){
                        boolean flag = true;
                        UUID uuid = UUID.fromString(resultSet.getString(DataBase.uuid));
                        String name = resultSet.getString(DataBase.player_name);
                        BigDecimal data = new BigDecimal(resultSet.getString(DataBase.data));
                        for(FightData fightData:Data.fightSet){
                            if(uuid.equals(fightData.getUuid())){
                                if(fightData.getFightValue().compareTo(data) < 0){
                                    fightData.setFightValue(data);
                                }
                                flag = false;
                            }
                        }
                        if(flag){
                            Data.fightSet.add(new FightData(null,uuid,name,data));
                        }
                    }
                    List<FightData> list = new ArrayList<>(Data.fightSet);
                    list.sort((o1, o2) -> o2.getFightValue().compareTo(o1.getFightValue()));
                    for(int i = list.size();i>0;i--){
                        if(i > LoadConfig.getF_count()){
                            list.remove(i-1);
                            continue;
                        }
                        list.get(i-1).updateData(i);
                    }
                    Data.fightRank = list;

                }, (exception, sqlAction) -> {
                    RankTop.instance.getLogger().warning(exception.getMessage());
                    //操作失败回调
                    RankTop.instance.getLogger().warning("更新战力数据时数据获取失败");
                });
    }

}
