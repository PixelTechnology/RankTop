package com.yuankong.ranktop.init;

import cn.hamster3.currency.data.PlayerData;
import com.google.gson.JsonParser;
import com.yuankong.ranktop.RankTop;
import com.yuankong.ranktop.config.LoadConfig;
import com.yuankong.ranktop.data.Data;
import com.yuankong.ranktop.data.DataBase;
import com.yuankong.ranktop.data.MoneyData;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class HamsterCurrencyUtil {
    public static List<MoneyData> moneyDataList = new ArrayList<>();
    static JsonParser parser = new JsonParser();
    public static void loadData(){
        moneyDataList.clear();
        DataBase.sqlManager.createQuery()
                .inTable("hamster_currency_player_data")
                .selectColumns(DataBase.data)
                .build().executeAsync((success) -> {
                    ResultSet resultSet = success.getResultSet();
                    while (resultSet.next()){
                        PlayerData playerData = new PlayerData(parser.parse(resultSet.getString(DataBase.data)).getAsJsonObject());
                        moneyDataList.add(new MoneyData(null,playerData.getUuid(),playerData.getPlayerName(), BigDecimal.valueOf(playerData.getPlayerCurrency("金币"))));

                        makeTop();
                    }
                }, (exception, sqlAction) -> {
                    //操作失败回调
                    RankTop.instance.getLogger().warning("hamster_currency_player_data表数据获取失败!");
                });

    }

    public static void makeTop(){
        moneyDataList.sort((o1, o2) -> o2.getMoney().compareTo(o1.getMoney()));
        Data.hamsterCurrencyRank.clear();
        for(MoneyData moneyData:moneyDataList){
            Data.hamsterCurrencyRank.add(moneyData);
            if(Data.hamsterCurrencyRank.size() >= LoadConfig.getM_count()){
                break;
            }
        }
    }
}
