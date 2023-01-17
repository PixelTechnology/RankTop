package com.yuankong.ranktop.listener;

import com.yuankong.ranktop.RankTop;
import com.yuankong.ranktop.config.LoadConfig;
import com.yuankong.ranktop.data.Data;
import com.yuankong.ranktop.data.DataBase;
import com.yuankong.ranktop.data.HurtData;
import com.yuankong.ranktop.util.Channel;
import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.api.bukkit.BukkitAPIHelper;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.*;

public class EventHandler implements Listener {
    public static HashMap<String,BigDecimal> minDamage = new HashMap<>();

    @org.bukkit.event.EventHandler
    public void onDamageEvent(EntityDamageByEntityEvent event) {

        if(!LoadConfig.isHurtTopEnable()){
            return;
        }

        BukkitAPIHelper api = MythicMobs.inst().getAPIHelper();
        if(!api.isMythicMob(event.getEntity())){
            return;
        }
        String mobName = api.getMythicMobInstance(event.getEntity()).getMobType();

        if(!LoadConfig.getMob().contains(mobName)){
            return;
        }

        if(!(event.getDamager() instanceof Player)){
            return;
        }

        BigDecimal damage = BigDecimal.valueOf(event.getDamage());

        if(minDamage.containsKey(mobName)){
            if(minDamage.get(mobName).compareTo(damage) > 0){
                return;
            }
        }

        Player player = (Player) event.getDamager();
        long time = System.currentTimeMillis();

        DataBase.sqlManager.createQuery()
                .inTable(LoadConfig.getH_tableName())
                .selectColumns(DataBase.uuid,DataBase.player_name,DataBase.data,DataBase.date)
                .addCondition(DataBase.mob, mobName)
                .orderBy(DataBase.id,true)
                .build().executeAsync((success) -> {
                    List<HurtData> hurtDataList = new ArrayList<>();
                    ResultSet resultSet = success.getResultSet();
                    boolean flag = true;
                    while (resultSet.next()){
                        UUID uuid = UUID.fromString(resultSet.getString(DataBase.uuid));
                        String name = resultSet.getString(DataBase.player_name);
                        BigDecimal data = new BigDecimal(resultSet.getString(DataBase.data));
                        long date = Long.parseLong(resultSet.getString(DataBase.date));

                        if(player.getUniqueId().equals(uuid)){
                            if(data.compareTo(damage) >= 0){
                                return;
                            }else{
                                hurtDataList.add(new HurtData(player,uuid,name,damage,time,mobName));
                            }
                            flag = false;
                        }else{
                            hurtDataList.add(new HurtData(null,uuid,name,data,date,mobName));
                        }

                    }

                    if(flag){
                        hurtDataList.add(new HurtData(player,player.getUniqueId(), player.getName(), damage,time,mobName));
                    }

                    hurtDataList.sort(Comparator.comparing(HurtData::getDamage).thenComparing(o -> BigDecimal.valueOf(o.getDate())));
                    Collections.reverse(hurtDataList);

                    for(int i = hurtDataList.size();i>0;i--){
                        if(i > LoadConfig.getH_count()){
                            hurtDataList.remove(i-1);
                            continue;
                        }
                        hurtDataList.get(i-1).updateData(i);
                    }
                    Data.damageRank.put(mobName,hurtDataList);
                    minDamage.put(mobName,Data.damageRank.get(mobName).get(Data.damageRank.get(mobName).size()-1).getDamage());
                    RankTop.instance.getServer().sendPluginMessage(RankTop.instance, Channel.DAMAGE.getChannel(), new byte[]{(byte)1});
                }, (exception, sqlAction) -> {
                    //操作失败回调
                    RankTop.instance.getLogger().warning(exception.getMessage());
                    RankTop.instance.getLogger().warning("刷新伤害表时数据获取失败");
                });
    }
}
