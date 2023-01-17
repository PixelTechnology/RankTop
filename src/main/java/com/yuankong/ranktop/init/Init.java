package com.yuankong.ranktop.init;

import com.yuankong.ranktop.config.LoadConfig;
import com.yuankong.ranktop.data.DataBase;
import github.saukiya.sxattribute.SXAttribute;
import github.saukiya.sxattribute.data.attribute.SubAttribute;
import github.saukiya.sxattribute.util.MoneyUtil;
import org.bukkit.entity.Player;
import java.util.*;


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
}
