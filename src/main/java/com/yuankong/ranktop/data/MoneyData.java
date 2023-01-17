package com.yuankong.ranktop.data;

import org.bukkit.entity.Player;

import java.math.BigDecimal;
import java.util.UUID;

public class MoneyData extends Data{
    BigDecimal money;

    public MoneyData(Player player, UUID uuid, String playerName, BigDecimal money) {
        this.player = player;
        this.uuid = uuid;
        this.playerName = playerName;
        this.money = money;
    }

    public BigDecimal getMoney() {
        return money;
    }

    public void setMoney(BigDecimal money) {
        this.money = money;
    }
}
