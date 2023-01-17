package com.yuankong.ranktop.init;

import com.yuankong.easylib.util.timer.TimerHandler;
import com.yuankong.ranktop.RankTop;
import com.yuankong.ranktop.config.LoadConfig;
import com.yuankong.ranktop.data.Data;
import org.bukkit.Bukkit;

import java.util.LinkedHashMap;
import java.util.UUID;

public class MoneyTimer extends TimerHandler {
    @Override
    public void times(long times) {
        if(LoadConfig.isMoneyTopEnable()){
            updateMoneyRank();
            this.timerSleep(LoadConfig.getM_updateRate() * 1000L);
        }
    }

    private void updateMoneyRank(){
        if(Bukkit.getPluginManager().getPlugin(LoadConfig.getUsePlugin()) == null || !Bukkit.getPluginManager().getPlugin(LoadConfig.getUsePlugin()).isEnabled()) {
            RankTop.instance.getLogger().warning("未找到"+LoadConfig.getUsePlugin()+"插件!请检查!");
            return;
        }

        if(LoadConfig.getUsePlugin().equals("SaneEconomy")){
            LinkedHashMap<UUID,Double> map = SaneEconomyUtil.getTopList();
            if(map != null && !map.isEmpty()){
                Data.saneEconomyRank.clear();
                map.forEach(((uuid, money) -> {
                    if(Data.saneEconomyRank.size() < LoadConfig.getM_count()){
                        Data.saneEconomyRank.put(uuid,money);
                    }
                }));
            }
        }else if(LoadConfig.getUsePlugin().equals("HamsterCurrency")){
            HamsterCurrencyUtil.loadData();
        }else{
            RankTop.instance.getLogger().warning("不支持的插件：" + LoadConfig.getUsePlugin());
        }

    }
}
