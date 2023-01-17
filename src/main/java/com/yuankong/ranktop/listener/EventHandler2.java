package com.yuankong.ranktop.listener;

import com.yuankong.easylib.api.EasyLibApi;
import com.yuankong.easylib.event.AfterLoadConfigEvent;
import com.yuankong.easylib.event.ReloadConfigEvent;
import com.yuankong.easylib.event.SQLManagerFinishEvent;
import com.yuankong.ranktop.RankTop;
import com.yuankong.ranktop.data.DataBase;
import com.yuankong.ranktop.init.FightTimer;
import com.yuankong.ranktop.init.MoneyTimer;
import com.yuankong.ranktop.init.PAPIUtil;
import com.yuankong.ranktop.util.Channel;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

public class EventHandler2 implements Listener {
    static FightTimer fightTimer = null;
    static MoneyTimer moneyTimer = null;
    @org.bukkit.event.EventHandler
    public void onEasyLibEvent(SQLManagerFinishEvent event) {
        DataBase.sqlManager = event.getSqlManager();
        DataBase.createTable();
        fightTimer = new FightTimer();
        moneyTimer = new MoneyTimer();
        EasyLibApi.registerTimer(RankTop.instance,fightTimer);
        EasyLibApi.registerTimer(RankTop.instance,moneyTimer);
        PAPIUtil.registerPAPI();
        PAPIUtil.papi.build();
        DataBase.updateFightRank();
        DataBase.updateDamageRank();
        DataBase.updateDungeonRank();
        RankTop.instance.getLogger().info("初始化完成!");
    }

    @org.bukkit.event.EventHandler
    public void onEasyLibEvent2(ReloadConfigEvent event) {
        DataBase.sqlManager = event.getSqlManager();
    }

    @org.bukkit.event.EventHandler
    public void onEasyLibEvent3(AfterLoadConfigEvent event) {
        registerBCChannel();
        registerCallBack();
    }

    public static FightTimer getFightTimer(){
        return fightTimer;
    }

    public static MoneyTimer getMoneyTimer() {
        return moneyTimer;
    }

    public void registerBCChannel(){
        List<String> list = new ArrayList<>();
        list.add(Channel.DAMAGE.getChannel());
        list.add(Channel.DUNGEON.getChannel());
        EasyLibApi.registerBCChannel(RankTop.instance,list);
    }

    public void registerCallBack(){
        EasyLibApi.registerBCMessage(RankTop.instance, (s, bytes) -> {
            if(s.equals(Channel.DAMAGE.getChannel())){
                DataBase.updateDamageRank();
            }
            if(s.equals(Channel.DUNGEON.getChannel())){
                DataBase.updateDungeonRank();
            }
        });
    }
}