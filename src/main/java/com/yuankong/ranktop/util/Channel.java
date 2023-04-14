package com.yuankong.ranktop.util;

public enum Channel {
    DAMAGE("rank_top:damage"),
    DUNGEON("rank_top:dungeon"),
    UPDATE("rank_top:update"),
    BAN("rank_top:ban");
    private final String channel;
    Channel(String channel) {
        this.channel = channel;
    }

    public String getChannel() {
        return channel;
    }
}
