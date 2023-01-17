package com.yuankong.ranktop.init;

import org.appledash.saneeconomy.SaneEconomy;
import org.appledash.saneeconomy.economy.backend.EconomyStorageBackend;

import java.util.LinkedHashMap;
import java.util.UUID;

public class SaneEconomyUtil {
    public static LinkedHashMap<UUID,Double> getTopList(){
        EconomyStorageBackend economy = SaneEconomy.getInstance().getEconomyManager().getBackend();
        economy.reloadTopPlayerBalances();
        return economy.getTopPlayerBalances();
    }
}
