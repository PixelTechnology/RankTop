package com.yuankong.ranktop.listener;

import com.yuankong.ranktop.RankTop;
import com.yuankong.ranktop.config.LoadConfig;
import com.yuankong.ranktop.data.Data;
import com.yuankong.ranktop.data.DataBase;
import com.yuankong.ranktop.data.DungeonData;
import com.yuankong.ranktop.init.Init;
import com.yuankong.ranktop.util.Channel;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.serverct.ersha.dungeon.DungeonPlus;
import org.serverct.ersha.dungeon.common.api.event.DungeonEvent;
import org.serverct.ersha.dungeon.common.team.Team;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.util.*;

public class EventHandler3 implements Listener {
    public static HashMap<Player,BigDecimal> enterTime = new HashMap<>();
    @org.bukkit.event.EventHandler
    public void onDungeonEvent(DungeonEvent event){
        Player player = Bukkit.getOfflinePlayer(event.getDungeon().getTeam().leader).getPlayer();
        if(player.getWorld().getName().contains("dungeon_")){
            return;
        }
        enterTime.put(player, BigDecimal.valueOf(System.currentTimeMillis()));
    }

    public static HashMap<String,BigDecimal> maxTime = new HashMap<>();
    public static void makeRank(CommandSender sender, String playerName){
        Player player = Bukkit.getPlayer(playerName);
        Team team = DungeonPlus.teamManager.getTeam(player);
        if(team == null || team.getTeamDungeon() == null){
            sender.sendMessage("§4该指令只能在副本使用");
            return;
        }
        Player leader = Bukkit.getOfflinePlayer(team.leader).getPlayer();
        if (Init.banList.contains(leader.getUniqueId())){
            return;
        }
        BigDecimal time = BigDecimal.valueOf(System.currentTimeMillis());
        if(EventHandler3.enterTime.containsKey(leader)){
            time = (time.subtract(EventHandler3.enterTime.get(leader))).divide(BigDecimal.valueOf(1000),0, RoundingMode.UP);
        }
        String dungeon = team.getTeamDungeon().getDungeonName();
        //如果通关速度大于排行最后一名
        if(maxTime.containsKey(dungeon) && time.compareTo(maxTime.get(dungeon)) > 0){
            return;
        }
        BigDecimal finalTime = time;
        long now = System.currentTimeMillis();
        DataBase.sqlManager.createQuery()
                .inTable(LoadConfig.getD_tableName())
                .selectColumns(DataBase.uuid,DataBase.player_name,DataBase.data,DataBase.date,DataBase.number)
                .addCondition(DataBase.dungeon, dungeon)
                .orderBy(DataBase.id,true)
                .build().executeAsync((success) -> {
                    List<DungeonData> dungeonDataList = new ArrayList<>();
                    ResultSet resultSet = success.getResultSet();
                    boolean flag = true;
                    while (resultSet.next()) {
                        UUID uuid = UUID.fromString(resultSet.getString(DataBase.uuid));
                        String name = resultSet.getString(DataBase.player_name);
                        BigDecimal data = new BigDecimal(resultSet.getString(DataBase.data));
                        long date = Long.parseLong(resultSet.getString(DataBase.date));
                        int number = resultSet.getInt(DataBase.number);
                        if(leader.getUniqueId().equals(uuid)){
                            if(finalTime.compareTo(data) >= 0){
                                return;
                            }else {
                                dungeonDataList.add(new DungeonData(leader,uuid,name,finalTime,now,team.getPlayers().size(),dungeon));
                            }
                            flag = false;
                        }else{
                            dungeonDataList.add(new DungeonData(null,uuid,name,data,date,number,dungeon));
                        }
                    }

                    if(flag){
                        dungeonDataList.add(new DungeonData(leader,leader.getUniqueId(),leader.getName(),finalTime,now,team.getPlayers().size(),dungeon));
                    }

                    dungeonDataList.sort(Comparator.comparing(DungeonData::getTime).thenComparing((o1, o2) -> BigDecimal.valueOf(o2.getDate()).compareTo(BigDecimal.valueOf(o1.getDate()))));

                    for(int i = dungeonDataList.size();i>0;i--){
                        if(i > LoadConfig.getD_count()){
                            dungeonDataList.remove(i-1);
                            continue;
                        }
                        dungeonDataList.get(i-1).updateData(i);
                    }
                    Data.dungeonRank.put(dungeon,dungeonDataList);
                    maxTime.put(dungeon,Data.dungeonRank.get(dungeon).get(Data.dungeonRank.get(dungeon).size()-1).getTime());
                    RankTop.instance.getServer().sendPluginMessage(RankTop.instance, Channel.DUNGEON.getChannel(), new byte[]{(byte)1});
                }, (exception, sqlAction) -> {
                    //操作失败回调
                    RankTop.instance.getLogger().warning(exception.getMessage());
                    RankTop.instance.getLogger().warning("操作失败,数据获取失败");
                });

    }
}
