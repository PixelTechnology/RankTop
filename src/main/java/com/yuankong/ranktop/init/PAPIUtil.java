package com.yuankong.ranktop.init;

import com.yuankong.easylib.util.papi.PAPI;
import com.yuankong.ranktop.config.LoadConfig;
import com.yuankong.ranktop.data.Data;
import org.bukkit.Bukkit;

import java.text.SimpleDateFormat;
import java.util.*;

public class PAPIUtil {
    static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH:mm");
    public static PAPI papi = new PAPI("ranktop");
    public static void registerPAPI(){

        for(int i = 1; i <= LoadConfig.getF_count(); i++){
            //ranktop_fight_player_n
            int x = i;
            papi.setPAPI("fight_player_" + i, offlinePlayer -> {
                if(Data.fightRank.isEmpty() || Data.fightRank.size() < x){

                    return " ";
                }
                return Data.fightRank.get(x-1).getPlayerName();
            });

            //ranktop_fight_value_n
            papi.setPAPI("fight_value_" + i, offlinePlayer -> {
                if(Data.fightRank.isEmpty() || Data.fightRank.size() < x){
                    return " ";
                }
                return Data.fightRank.get(x-1).getFightValue().toString();
            });
        }

        for(int i = 1; i <= LoadConfig.getM_count(); i++){
            int x = i;
            //ranktop_money_player_n
            papi.setPAPI("money_player_" + i, offlinePlayer -> {
                if(LoadConfig.getUsePlugin().equals("SaneEconomy")){
                    if(Data.saneEconomyRank.isEmpty() || Data.saneEconomyRank.size() < x){
                        return " ";
                    }
                    List<UUID> uuidList = new ArrayList<>(Data.saneEconomyRank.keySet());
                    return Bukkit.getOfflinePlayer(uuidList.get(x-1)).getName();
                }else if(LoadConfig.getUsePlugin().equals("HamsterCurrency")){
                    if(Data.hamsterCurrencyRank.isEmpty() || Data.hamsterCurrencyRank.size() < x){
                        return " ";
                    }
                    return Data.hamsterCurrencyRank.get(x-1).getPlayerName();
                }else{
                    return " ";
                }
            });

            //ranktop_money_value_n
            papi.setPAPI("money_value_" + i, offlinePlayer -> {
                if(LoadConfig.getUsePlugin().equals("SaneEconomy")){
                    if(Data.saneEconomyRank.isEmpty() || Data.saneEconomyRank.size() < x){
                        return " ";
                    }
                    List<UUID> uuidList = new ArrayList<>(Data.saneEconomyRank.keySet());
                    return String.valueOf(Data.saneEconomyRank.get(uuidList.get(x-1)));
                }else if(LoadConfig.getUsePlugin().equals("HamsterCurrency")){
                    if(Data.hamsterCurrencyRank.isEmpty() || Data.hamsterCurrencyRank.size() < x){
                        return " ";
                    }
                    return Data.hamsterCurrencyRank.get(x-1).getMoney().toString();
                }else{
                    return " ";
                }
            });
        }

        for(int i = 1; i <= LoadConfig.getH_count(); i++){
            int x = i;
            for(String str:LoadConfig.getMob()){
                //ranktop_damage_xxx_player_n
                papi.setPAPI("damage_" + str + "_player_"+i,offlinePlayer -> {
                    if(!Data.damageRank.containsKey(str) || Data.damageRank.get(str).isEmpty() || Data.damageRank.get(str).size() < x){
                        return " ";
                    }
                    return Data.damageRank.get(str).get(x-1).getPlayerName();
                });

                //ranktop_damage_xxx_value_n
                papi.setPAPI("damage_" + str + "_value_"+i,offlinePlayer -> {
                    if(!Data.damageRank.containsKey(str) || Data.damageRank.get(str).isEmpty() || Data.damageRank.get(str).size() < x){
                        return " ";
                    }
                    return Data.damageRank.get(str).get(x-1).getDamage().toString();
                });

                //ranktop_damage_xxx_date_n
                papi.setPAPI("damage_" + str + "_date_"+i,offlinePlayer -> {
                    if(!Data.damageRank.containsKey(str) || Data.damageRank.get(str).isEmpty() || Data.damageRank.get(str).size() < x){
                        return " ";
                    }
                    return getDateString(Data.damageRank.get(str).get(x-1).getDate());
                });
            }
        }

        for(int i = 1; i <= LoadConfig.getD_count(); i++){
            int x = i;
            for(String dungeon:LoadConfig.getDungeonList()){
                //ranktop_dungeon_xxx_player_n
                papi.setPAPI("dungeon_"+ dungeon +"_player_" + i,offlinePlayer -> {
                    if(!Data.dungeonRank.containsKey(dungeon) || Data.dungeonRank.get(dungeon).isEmpty() || Data.dungeonRank.get(dungeon).size() < x){
                        return " ";
                    }
                    return Data.dungeonRank.get(dungeon).get(x-1).getPlayerName()+"的队伍";
                });

                //ranktop_dungeon_xxx_value_n
                papi.setPAPI("dungeon_"+ dungeon +"_value_" + i,offlinePlayer -> {
                    if(!Data.dungeonRank.containsKey(dungeon) || Data.dungeonRank.get(dungeon).isEmpty() || Data.dungeonRank.get(dungeon).size() < x){
                        return " ";
                    }
                    return Data.dungeonRank.get(dungeon).get(x-1).getTime().toString();
                });

                //ranktop_dungeon_xxx_date_n
                papi.setPAPI("dungeon_"+ dungeon +"_date_" + i,offlinePlayer -> {
                    if(!Data.dungeonRank.containsKey(dungeon) || Data.dungeonRank.get(dungeon).isEmpty() || Data.dungeonRank.get(dungeon).size() < x){
                        return " ";
                    }
                    return getDateString(Data.dungeonRank.get(dungeon).get(x-1).getDate());
                });

                //ranktop_dungeon_xxx_number_n
                papi.setPAPI("dungeon_"+ dungeon +"_number_" + i,offlinePlayer -> {
                    if(!Data.dungeonRank.containsKey(dungeon) || Data.dungeonRank.get(dungeon).isEmpty() || Data.dungeonRank.get(dungeon).size() < x){
                        return " ";
                    }
                    return Data.dungeonRank.get(dungeon).get(x - 1).getNumber() +"人";
                });
            }
        }
    }

    public static String getDateString(long time){
        Date date = new Date(time);
        return format.format(date);
    }

}
