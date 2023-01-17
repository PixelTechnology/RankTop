package com.yuankong.ranktop.data;

import org.bukkit.entity.Player;

import java.util.*;

public abstract class Data {
    public static HashSet<FightData> fightSet = new HashSet<>();
    public static List<FightData> fightRank = new ArrayList<>();
    public static LinkedHashMap<UUID,Double> saneEconomyRank = new LinkedHashMap<>();
    public static List<MoneyData> hamsterCurrencyRank = new ArrayList<>();
    //public static List<HurtData> damageRank = new ArrayList<>();
    public static HashMap<String,List<HurtData>> damageRank = new HashMap<>();
    //public static List<DungeonData> dungeonRank = new ArrayList<>();
    public static HashMap<String,List<DungeonData>> dungeonRank = new HashMap<>();
    Player player;
    UUID uuid;
    String playerName;

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }
}
