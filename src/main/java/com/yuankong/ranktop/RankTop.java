package com.yuankong.ranktop;

import com.yuankong.ranktop.config.LoadConfig;
import com.yuankong.ranktop.data.Data;
import com.yuankong.ranktop.init.Init;
import com.yuankong.ranktop.init.PAPIUtil;
import com.yuankong.ranktop.listener.EventHandler;
import com.yuankong.ranktop.listener.EventHandler2;
import com.yuankong.ranktop.listener.EventHandler3;
import com.yuankong.ranktop.util.Channel;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public final class RankTop extends JavaPlugin {

    public static JavaPlugin instance;
    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        LoadConfig.load();
        Bukkit.getPluginManager().registerEvents(new EventHandler(),this);
        Bukkit.getPluginManager().registerEvents(new EventHandler2(),this);
        getServer().getMessenger().registerOutgoingPluginChannel(this, Channel.DAMAGE.getChannel());
        getServer().getMessenger().registerOutgoingPluginChannel(this, Channel.DUNGEON.getChannel());
        if(Bukkit.getPluginManager().getPlugin("DungeonPlus") != null){
            Bukkit.getPluginManager().registerEvents(new EventHandler3(),this);
        }
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "========RankTop已开启========");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!"rtop".equalsIgnoreCase(command.getName())) {
            return false;
        }

        if(!sender.isOp()){
            return true;
        }

        command.setUsage("§a/rtop reload -重载 \n§a/rtop makerank -计算副本通关时间，给副本boss绑定的指令,仅可在副本中使用");

        if (args.length == 1 && "reload".equalsIgnoreCase(args[0])) {
            int f_count = LoadConfig.getF_count();
            int h_count = LoadConfig.getH_count();
            int d_count = LoadConfig.getD_count();
            List<String> mobList = LoadConfig.getMob();
            int f_updateRate = LoadConfig.getF_updateRate();
            int m_updateRate = LoadConfig.getM_updateRate();

            LoadConfig.reload();

            Init.deleteMobData(mobList,h_count);
            if(f_count > LoadConfig.getF_count()){
                for(int i = 1;i<= f_count;i++){
                    if(i <= LoadConfig.getF_count()){
                        continue;
                    }
                    Init.deleteTable(LoadConfig.getF_tableName(),i);
                }
            }
            if(h_count > LoadConfig.getH_count()){
                for(String str:LoadConfig.getMob()){
                    for(int i = 1;i<= h_count;i++){
                        if(i <= LoadConfig.getH_count()){
                            continue;
                        }
                        Init.deleteDamageTable(str,i);
                    }
                }
            }
            if(d_count > LoadConfig.getD_count()){
                for(int i = 1;i<= d_count;i++){
                    if(i <= LoadConfig.getD_count()){
                        continue;
                    }
                    Init.deleteTable(LoadConfig.getD_tableName(),i);
                }
            }

            if(f_updateRate != LoadConfig.getF_updateRate()){
                EventHandler2.getFightTimer().timerSleep(LoadConfig.getF_updateRate());
            }
            if(m_updateRate != LoadConfig.getM_updateRate()){
                EventHandler2.getMoneyTimer().timerSleep(LoadConfig.getM_updateRate());
            }

            PAPIUtil.registerPAPI();
            sender.sendMessage("[RankTop]"+ChatColor.GREEN+"配置已重载完成");
            return true;
        }

        if (args.length == 2 && "makerank".equalsIgnoreCase(args[0])) {
            EventHandler3.makeRank(sender,args[1]);
            return true;
        }
        if (args.length == 1 && "test".equalsIgnoreCase(args[0])) {
            sender.sendMessage(Data.fightRank.toString());
            sender.sendMessage(Data.saneEconomyRank.toString());
            sender.sendMessage(Data.hamsterCurrencyRank.toString());
            sender.sendMessage(Data.damageRank.toString());
            sender.sendMessage(Data.dungeonRank.toString());
            if(Data.fightRank.size()>1){
                sender.sendMessage(Data.fightRank.get(0).getPlayerName()+":"+Data.fightRank.get(0).getFightValue().toString());
                sender.sendMessage(Data.fightRank.get(1).getPlayerName()+":"+Data.fightRank.get(1).getFightValue().toString());
            }
            return true;
        }
        return !sender.isOp();
    }

    @Override
    public void onDisable() {}
}
