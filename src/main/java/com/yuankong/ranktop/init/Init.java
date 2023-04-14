package com.yuankong.ranktop.init;

import com.google.common.base.Charsets;
import com.yuankong.ranktop.RankTop;
import com.yuankong.ranktop.config.LoadConfig;
import com.yuankong.ranktop.data.Data;
import com.yuankong.ranktop.data.DataBase;
import com.yuankong.ranktop.data.MoneyData;
import com.yuankong.ranktop.util.Channel;
import github.saukiya.sxattribute.SXAttribute;
import github.saukiya.sxattribute.data.attribute.SubAttribute;
import github.saukiya.sxattribute.util.MoneyUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;


public class Init {
    public static String getFightValues(Player player) {
        String lore = "%sx_value%";
        while (lore.contains("%") && lore.split("%").length > 1 && lore.split("%")[1].contains("sx_") && lore.split("%")[1].split("_").length > 1) {
            String[] loreSplit = lore.split("%");
            String string = loreSplit[1].split("_")[1];
            String str = null;
            if (string.equalsIgnoreCase("Money") && SXAttribute.isVault()) {
                str = SXAttribute.getDf().format(MoneyUtil.get(player));
            } else if (string.equalsIgnoreCase("value")) {
                str = SXAttribute.getDf().format(SXAttribute.getApi().getEntityAllData(player).getValue());
            } else {
                for (SubAttribute attribute : SXAttribute.getApi().getEntityAllData(player).getAttributeMap().values()) {
                    str = attribute.getPlaceholder(player, string);
                    if (str != null) {
                        break;
                    }
                }
            }
            if (str != null) {
                lore = lore.replaceFirst("%" + loreSplit[1] + "%", str);
            } else {
                lore = lore.replaceFirst("%" + loreSplit[1] + "%", "§cN/A");
            }
        }
        return lore;
    }

    public static void deleteTable(String table,int id){
        DataBase.sqlManager.createDelete(table)
                .addCondition(DataBase.id, id)
                .build().executeAsync();
    }

    public static void deleteDamageTable(String mob,int id){
        DataBase.sqlManager.createDelete(LoadConfig.getH_tableName())
                .addCondition(DataBase.mob, mob)
                .addCondition(DataBase.id, id)
                .build().executeAsync();
    }

    public static void deleteMobData(List<String> list,int count){
        for(String str:list){
            if(LoadConfig.getMob().contains(str)){
                continue;
            }
            for(int i = 1;i <= count;i++){
                deleteDamageTable(str,i);
            }
        }
    }

    //从数据存储处（数据库、本地数据）更新至内存，并更新排行榜
    public static void updateFromDataBase(CommandSender sender){
        FightTimer.updateFightRank();
        DataBase.updateDamageRank();
        DataBase.updateDungeonRank();
        MoneyTimer.updateMoneyRank();
        sender.sendMessage("§a刷新排行榜完成");
    }

    public static void banTop(CommandSender sender,String playerName){
        UUID uuid = getUUID(playerName);
        if(banList.contains(uuid)){
            sender.sendMessage("§4该玩家已禁止排行榜上榜");
            return;
        }
        DataBase.sqlManager.createInsert(LoadConfig.getBanTable())
                .setColumnNames(DataBase.uuid,DataBase.player_name)
                .setParams(uuid,playerName)
                .executeAsync((success1) -> {
                    sender.sendMessage("§a禁止该玩家排行榜上榜完成");
                    Init.updateBanList();
                    RankTop.instance.getServer().sendPluginMessage(RankTop.instance, Channel.BAN.getChannel(), new byte[]{(byte)1});
                },(exception, sqlAction) -> {
                    //操作失败回调
                    RankTop.instance.getLogger().warning("插入ban表数据时出错");
                    RankTop.instance.getLogger().warning(exception.getMessage());
                });
    }

    public static void unbanTop(CommandSender sender,String playerName){
        UUID uuid = getUUID(playerName);
        if(!banList.contains(uuid)){
            sender.sendMessage("§4该玩家未禁止排行榜上榜");
            return;
        }
        DataBase.sqlManager.createDelete(LoadConfig.getBanTable())
                .addCondition(DataBase.uuid,uuid)
                .build().executeAsync((success1) -> {
                    sender.sendMessage("§a解除禁止该玩家排行榜刷新完成");
                    Init.updateBanList();
                    RankTop.instance.getServer().sendPluginMessage(RankTop.instance, Channel.BAN.getChannel(), new byte[]{(byte)1});
                },(exception, sqlAction) -> {
                    //操作失败回调
                    RankTop.instance.getLogger().warning("删除ban表数据时出错");
                    RankTop.instance.getLogger().warning(exception.getMessage());
                });
    }

    public static void clearTopData(CommandSender sender,String playerName){
        UUID uuid = getUUID(playerName);
        DataBase.sqlManager.createDelete(LoadConfig.getF_tableName())
                .addCondition(DataBase.uuid,uuid)
                .build().executeAsync((success) -> {
                    DataBase.sqlManager.createDelete(LoadConfig.getH_tableName())
                            .addCondition(DataBase.uuid,uuid)
                            .build().executeAsync((success1) -> {
                                DataBase.sqlManager.createDelete(LoadConfig.getD_tableName())
                                        .addCondition(DataBase.uuid,uuid)
                                        .build().executeAsync((success2) -> {
                                            if(!Data.saneEconomyRank.isEmpty()){
                                                Data.saneEconomyRank.remove(uuid);
                                            }else{
                                                AtomicReference<MoneyData> data = new AtomicReference<>();
                                                Data.hamsterCurrencyRank.forEach((moneyData -> {
                                                    if (moneyData.getUuid().equals(uuid)){
                                                        data.set(moneyData);
                                                    }
                                                }));
                                                if(data.get() != null){
                                                    Data.hamsterCurrencyRank.remove(data.get());
                                                }
                                            }
                                            DataBase.updateDamageRank();
                                            DataBase.updateDungeonRank();
                                            DataBase.updateFightRank();
                                            sender.sendMessage("§a清除玩家排行榜数据完成");
                                        },(exception, sqlAction) -> {
                                            //操作失败回调
                                            RankTop.instance.getLogger().warning("删除副本表数据时出错");
                                            RankTop.instance.getLogger().warning(exception.getMessage());
                                        });
                            },(exception, sqlAction) -> {
                                //操作失败回调
                                RankTop.instance.getLogger().warning("删除伤害表数据时出错");
                                RankTop.instance.getLogger().warning(exception.getMessage());
                            });
                },(exception, sqlAction) -> {
                    //操作失败回调
                    RankTop.instance.getLogger().warning("删除战力表数据时出错");
                    RankTop.instance.getLogger().warning(exception.getMessage());
                });
    }

    //这个方法会卡，弃用
    public static OfflinePlayer getPlayer(CommandSender sender,String playerName){
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerName);
        if(offlinePlayer == null){
            sender.sendMessage("§4玩家不存在");
            return null;
        }
        if(Objects.isNull(offlinePlayer.getUniqueId())){
            sender.sendMessage("§4玩家不存在");
            return null;
        }
        return offlinePlayer;
    }

    public static UUID getUUID(String playerName){
        return UUID.nameUUIDFromBytes(("OfflinePlayer:" + playerName).getBytes(Charsets.UTF_8));
    }

    public static List<UUID> banList = new ArrayList<>();
    public static void updateBanList() {
        /*ResultSet resultSet = DataBase.sqlManager.createQuery().inTable(LoadConfig.getBanTable())
                .selectColumns(DataBase.uuid)
                .build().execute().getResultSet();
        banList.clear();
        while (resultSet.next()){
            banList.add(UUID.fromString(resultSet.getString(DataBase.uuid)));
        }*/
        DataBase.sqlManager.createQuery().inTable(LoadConfig.getBanTable())
                .selectColumns(DataBase.uuid)
                .build().executeAsync((success) -> {
                    ResultSet resultSet = success.getResultSet();
                    banList.clear();
                    while (resultSet.next()){
                        banList.add(UUID.fromString(resultSet.getString(DataBase.uuid)));
                    }
                },(exception, sqlAction) -> {
                    //操作失败回调
                    RankTop.instance.getLogger().warning("刷新ban表数据时出错");
                    RankTop.instance.getLogger().warning(exception.getMessage());
                });
    }
}
